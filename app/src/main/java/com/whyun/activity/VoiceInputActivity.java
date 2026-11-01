package com.whyun.activity;

import java.util.ArrayList;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.whyun.activity.component.ActivityUtil;
import com.whyun.activity.component.SegmentedControl;
import com.whyun.bluetooth.R;
import com.whyun.util.AppPrefs;

/**
 * 语音输入：把说的话（或打的字）作为文本发到电脑当前光标处。
 * <ul>
 * <li>系统输入法（默认）：自动弹出软键盘，用输入法自带的麦克风说话，不需要录音权限；</li>
 * <li>内置识别：用 SpeechRecognizer 识别，部分结果实时写入文本框，需要录音权限。</li>
 * </ul>
 * 内置识别的麦克风有两种用法：点一下开始、停顿后自动结束；或按住说话、松手结束、上滑取消。
 * 按住说话可在设置里选择松手后直接发送。
 * 识别结果都可以先修改再发送，发送走 TEXT 消息，电脑端需要支持 0x50。
 */
public class VoiceInputActivity extends BaseControlActivity {

	private static final int REQ_RECORD_AUDIO = 1;
	private static final int WAVE_BARS = 7;
	/** 按住说话时手指上滑超过这个距离（dp）松手即取消 */
	private static final int CANCEL_SLIDE_DP = 72;
	/** 按住说话时放宽静音判定，避免说话间隙被提前截断（部分识别服务会忽略） */
	private static final long HOLD_SILENCE_MS = 10_000;

	private EditText etText;
	private TextView tvMicStatus;
	private TextView btnLanguage;
	private View btnMic;
	private View haloOuter;
	private View haloInner;
	private LinearLayout wave;
	private SegmentedControl sendMode;

	private boolean builtin;
	private SpeechRecognizer recognizer;
	private boolean listening;
	/** 本次识别开始前文本框里已有的内容，识别结果追加在它后面 */
	private String prefix = "";

	private final Handler handler = new Handler(Looper.getMainLooper());
	/** 手指正按住麦克风说话 */
	private boolean holding;
	/** 按住时手指已上滑到取消区域 */
	private boolean cancelZone;
	/** 已松手，等最终结果；若设置了直接发送，结果到达后发送 */
	private boolean awaitingHoldResult;
	/** 按住期间识别已自行结束，结果已在文本框里 */
	private boolean holdResultReady;
	private float downY;
	/** 按住前文本框里的内容，上滑取消时恢复 */
	private String beforeHold = "";
	private final Runnable beginHold = new Runnable() {
		@Override
		public void run() {
			startHold();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_voice_input);
		bindBack(R.id.btn_close);

		etText = (EditText) findViewById(R.id.et_text);
		tvMicStatus = (TextView) findViewById(R.id.tv_mic_status);
		btnLanguage = (TextView) findViewById(R.id.btn_language);
		btnMic = findViewById(R.id.btn_mic);
		haloOuter = findViewById(R.id.mic_halo_outer);
		haloInner = findViewById(R.id.mic_halo_inner);
		wave = (LinearLayout) findViewById(R.id.wave);

		builtin = SpeechRecognizer.isRecognitionAvailable(this)
				&& IMyPreference.VOICE_ENGINE_BUILTIN.equals(
						AppPrefs.get(this, IMyPreference.VOICE_ENGINE, IMyPreference.VOICE_ENGINE_BUILTIN));

		sendMode = new SegmentedControl((LinearLayout) findViewById(R.id.seg_send_mode));
		sendMode.select(AppPrefs.get(this, IMyPreference.VOICE_SEND_ENTER, true) ? 1 : 0);

		findViewById(R.id.btn_clear).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				etText.setText("");
			}
		});
		findViewById(R.id.btn_retry).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				etText.setText("");
				if (builtin) {
					startListening(false);
				} else {
					showKeyboard();
				}
			}
		});
		findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				send();
			}
		});
		btnLanguage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				nextLanguage();
			}
		});
		renderLanguage();

		if (builtin) {
			setupBuiltin();
		} else {
			setupIme();
		}
	}

	// ---------------------------------------------------------------- 系统输入法

	private void setupIme() {
		findViewById(R.id.block_mic).setVisibility(View.GONE);
		findViewById(R.id.tv_ime_hint).setVisibility(View.VISIBLE);
		btnLanguage.setVisibility(View.INVISIBLE);// 语言由输入法决定
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
				| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		etText.requestFocus();
	}

	private void showKeyboard() {
		etText.requestFocus();
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		if (imm != null) {
			imm.showSoftInput(etText, InputMethodManager.SHOW_IMPLICIT);
		}
	}

	// ---------------------------------------------------------------- 内置识别

	private void setupBuiltin() {
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
				| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		int barWidth = dp(4);
		for (int i = 0; i < WAVE_BARS; i++) {
			View bar = new View(this);
			bar.setBackgroundResource(R.drawable.bh_wave_bar);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(barWidth, dp(6));
			lp.leftMargin = lp.rightMargin = dp(3);
			wave.addView(bar, lp);
		}
		btnMic.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				toggleListening();// 短按，或读屏软件的双击
			}
		});
		btnMic.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				return onMicTouch(e);
			}
		});
		renderListening(false, IDLE_HINT);
		if (!AppPrefs.get(this, IMyPreference.VOICE_HOLD_SEND, true)) {
			((TextView) findViewById(R.id.tv_mic_hint)).setText("松手后先显示结果 · 上滑取消");
		}
		if (!hasRecordPermission()) {
			ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.RECORD_AUDIO }, REQ_RECORD_AUDIO);
		}
	}

	private static final String IDLE_HINT = "按住说话，松手发送";

	private void toggleListening() {
		if (listening) {
			recognizer.stopListening();
		} else {
			startListening(false);
		}
	}

	/** 短按交给点击处理；按住超过长按时间进入按住说话 */
	private boolean onMicTouch(MotionEvent e) {
		switch (e.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
			btnMic.getParent().requestDisallowInterceptTouchEvent(true);
			downY = e.getRawY();
			cancelZone = false;
			btnMic.setPressed(true);
			handler.postDelayed(beginHold, ViewConfiguration.getLongPressTimeout());
			return true;
		case MotionEvent.ACTION_MOVE:
			if (holding) {
				boolean inZone = downY - e.getRawY() > dp(CANCEL_SLIDE_DP);
				if (inZone != cancelZone) {
					cancelZone = inZone;
					tvMicStatus.setText(inZone ? "松开手指，取消这次输入" : "正在聆听，松手结束 · 上滑取消");
					btnMic.setAlpha(inZone ? 0.5f : 1f);
				}
			}
			return true;
		case MotionEvent.ACTION_UP:
			btnMic.setPressed(false);
			handler.removeCallbacks(beginHold);
			if (holding) {
				endHold(cancelZone);
			} else {
				btnMic.performClick();
			}
			return true;
		case MotionEvent.ACTION_CANCEL:
			btnMic.setPressed(false);
			handler.removeCallbacks(beginHold);
			if (holding) {
				endHold(true);
			}
			return true;
		default:
			return false;
		}
	}

	private void startHold() {
		if (!hasRecordPermission()) {
			startListening(false);// 申请权限
			return;
		}
		holding = true;
		beforeHold = etText.getText().toString();
		awaitingHoldResult = false;
		holdResultReady = false;
		btnMic.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
		btnMic.animate().scaleX(1.12f).scaleY(1.12f).setDuration(120).start();
		if (!listening) {
			startListening(true);
		}
		tvMicStatus.setText("正在聆听，松手结束 · 上滑取消");
	}

	private void endHold(boolean cancel) {
		holding = false;
		btnMic.animate().scaleX(1f).scaleY(1f).setDuration(120).start();
		btnMic.setAlpha(1f);
		if (cancel) {
			if (recognizer != null) {
				recognizer.cancel();
			}
			etText.setText(beforeHold);
			etText.setSelection(etText.length());
			renderListening(false, "已取消");
			return;
		}
		if (listening) {
			awaitingHoldResult = true;
			recognizer.stopListening();
			tvMicStatus.setText("正在识别…");
		} else if (holdResultReady) {
			finishHold();
		}
	}

	/** 按住期间重新开始识别，新结果追加在已识别文字后面 */
	private void continueHold() {
		startListening(true);
		tvMicStatus.setText(cancelZone ? "松开手指，取消这次输入" : "正在聆听，松手结束 · 上滑取消");
	}

	/** 按住说话得到最终结果后：按设置直接发送，或留给用户确认 */
	private void finishHold() {
		awaitingHoldResult = false;
		holdResultReady = false;
		if (AppPrefs.get(this, IMyPreference.VOICE_HOLD_SEND, true) && !etText.getText().toString().trim().isEmpty()) {
			send();
		} else {
			renderListening(false, "识别完成，可修改后发送");
		}
	}

	private boolean hasRecordPermission() {
		return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
				== PackageManager.PERMISSION_GRANTED;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode != REQ_RECORD_AUDIO) {
			return;
		}
		if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
			renderListening(false, IDLE_HINT);
		} else {
			renderListening(false, "没有录音权限，可以直接打字，或在设置里改用系统输入法");
		}
	}

	/** hold 为 true 时放宽静音判定，让说话的人自己决定何时结束 */
	private void startListening(boolean hold) {
		if (!hasRecordPermission()) {
			ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.RECORD_AUDIO }, REQ_RECORD_AUDIO);
			return;
		}
		if (recognizer == null) {
			recognizer = SpeechRecognizer.createSpeechRecognizer(this);
			recognizer.setRecognitionListener(new Listener());
		} else {
			recognizer.cancel();
		}
		String text = etText.getText().toString();
		prefix = text.isEmpty() || text.endsWith(" ") ? text : text + " ";
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, currentLanguage());
		intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
		intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
		if (hold) {
			intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, HOLD_SILENCE_MS);
			intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, HOLD_SILENCE_MS);
		}
		recognizer.startListening(intent);
		renderListening(true, "正在聆听…");
	}

	private void stopListening() {
		if (recognizer != null && listening) {
			recognizer.cancel();
		}
		renderListening(false, IDLE_HINT);
	}

	private void renderListening(boolean on, String status) {
		listening = on;
		tvMicStatus.setText(status);
		btnMic.setContentDescription(on ? "停止说话" : "开始说话，长按可按住说话");
		haloOuter.setVisibility(on ? View.VISIBLE : View.INVISIBLE);
		haloInner.setVisibility(on ? View.VISIBLE : View.INVISIBLE);
		wave.setVisibility(on ? View.VISIBLE : View.INVISIBLE);
		if (!on) {
			renderLevel(0);
		}
	}

	/** level 0..1，驱动光环缩放和波形高度 */
	private void renderLevel(float level) {
		float s = 0.9f + level * 0.15f;
		haloOuter.setScaleX(s);
		haloOuter.setScaleY(s);
		haloInner.setScaleX(0.95f + level * 0.08f);
		haloInner.setScaleY(0.95f + level * 0.08f);
		int max = dp(24);
		int min = dp(6);
		for (int i = 0; i < wave.getChildCount(); i++) {
			// 中间高两边低，看起来像声波
			float weight = 1f - Math.abs(i - (WAVE_BARS - 1) / 2f) / WAVE_BARS;
			View bar = wave.getChildAt(i);
			ViewGroup.LayoutParams lp = bar.getLayoutParams();
			lp.height = (int) (min + (max - min) * level * weight);
			bar.setLayoutParams(lp);
		}
	}

	private void showRecognized(ArrayList<String> results) {
		if (results != null && !results.isEmpty()) {
			etText.setText(prefix + results.get(0));
			etText.setSelection(etText.length());
		}
	}

	private class Listener implements RecognitionListener {
		@Override
		public void onReadyForSpeech(Bundle params) {
		}

		@Override
		public void onBeginningOfSpeech() {
		}

		@Override
		public void onRmsChanged(float rmsdB) {
			if (listening) {
				// rmsdB 大约在 -2..10 之间
				renderLevel(Math.max(0f, Math.min(1f, (rmsdB + 2f) / 12f)));
			}
		}

		@Override
		public void onBufferReceived(byte[] buffer) {
		}

		@Override
		public void onEndOfSpeech() {
			tvMicStatus.setText("正在识别…");
		}

		@Override
		public void onError(int error) {
			boolean silence = error == SpeechRecognizer.ERROR_NO_MATCH || error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT;
			if (holding) {
				listening = false;
				if (silence) {
					continueHold();// 只是停顿太久，手指还按着就接着听
				} else {
					holdResultReady = false;
					tvMicStatus.setText(errorText(error));
				}
				return;
			}
			if (awaitingHoldResult && holdResultReady && silence) {
				finishHold();// 松手前最后一段没说话，前面已识别的句子照常处理
				return;
			}
			awaitingHoldResult = false;
			holdResultReady = false;
			renderListening(false, errorText(error));
		}

		@Override
		public void onResults(Bundle results) {
			showRecognized(results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION));
			if (holding) {
				// 识别服务按停顿自行结束了一句，手指还按着：保留这句，接着听下一句
				listening = false;
				holdResultReady = true;
				continueHold();
				return;
			}
			if (awaitingHoldResult) {
				finishHold();
				return;
			}
			renderListening(false, "识别完成，可修改后发送");
		}

		@Override
		public void onPartialResults(Bundle partialResults) {
			showRecognized(partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION));
		}

		@Override
		public void onEvent(int eventType, Bundle params) {
		}
	}

	private static String errorText(int error) {
		switch (error) {
		case SpeechRecognizer.ERROR_NO_MATCH:
		case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
			return "没有听清，点击麦克风再说一次";
		case SpeechRecognizer.ERROR_NETWORK:
		case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
			return "网络不可用，识别失败";
		case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
			return "没有录音权限";
		case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
			return "识别服务忙，请稍后再试";
		case SpeechRecognizer.ERROR_AUDIO:
			return "录音失败，麦克风可能被占用";
		default:
			return "识别出错（" + error + "），点击麦克风重试";
		}
	}

	// ---------------------------------------------------------------- 语言

	private String currentLanguage() {
		return AppPrefs.get(this, IMyPreference.VOICE_LANGUAGE, SettingsActivity.LANG_CODES[0]);
	}

	private void nextLanguage() {
		String[] codes = SettingsActivity.LANG_CODES;
		String cur = currentLanguage();
		int next = 0;
		for (int i = 0; i < codes.length; i++) {
			if (codes[i].equals(cur)) {
				next = (i + 1) % codes.length;
			}
		}
		AppPrefs.put(this, IMyPreference.VOICE_LANGUAGE, codes[next]);
		renderLanguage();
		if (listening) {
			startListening(holding);// 用新语言重新开始
		}
	}

	private void renderLanguage() {
		String name = SettingsActivity.langName(currentLanguage());
		btnLanguage.setText(name);
		btnLanguage.setContentDescription("识别语言：" + name + "，点击切换");
	}

	// ---------------------------------------------------------------- 发送

	private void send() {
		String text = etText.getText().toString();
		boolean enter = sendMode.getSelected() == 1;
		if (enter) {
			// 末尾的换行会被电脑端当成回车，再加上“输入并回车”就成了两次回车
			text = text.replaceAll("[\r\n]+$", "");
		}
		if (text.trim().isEmpty() && !enter) {
			ActivityUtil.toastShow(this, "还没有要发送的文字");
			return;
		}
		stopListening();
		connection.sendText(text, enter);
		etText.setText("");
		ActivityUtil.toastShow(this, "已发送到电脑");
	}

	// ---------------------------------------------------------------- 生命周期

	@Override
	protected void onPause() {
		if (builtin) {
			stopListening();
		}
		super.onPause();
	}

	@Override
	protected void onDisconnected() {
		if (builtin) {
			stopListening();
		}
	}

	@Override
	protected void onDestroy() {
		if (recognizer != null) {
			recognizer.destroy();
			recognizer = null;
		}
		super.onDestroy();
	}

	private int dp(int v) {
		return (int) (v * getResources().getDisplayMetrics().density + 0.5f);
	}
}

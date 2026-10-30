package com.whyun.activity;

import java.util.ArrayList;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
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
 * 识别结果都可以先修改再发送，发送走 TEXT 消息，电脑端需要支持 0x50。
 */
public class VoiceInputActivity extends BaseControlActivity {

	private static final int REQ_RECORD_AUDIO = 1;
	private static final int WAVE_BARS = 7;

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

		builtin = IMyPreference.VOICE_ENGINE_BUILTIN.equals(
				AppPrefs.get(this, IMyPreference.VOICE_ENGINE, IMyPreference.VOICE_ENGINE_IME))
				&& SpeechRecognizer.isRecognitionAvailable(this);

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
					startListening();
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
				if (listening) {
					recognizer.stopListening();
				} else {
					startListening();
				}
			}
		});
		renderListening(false, "点击麦克风开始说话");
		if (hasRecordPermission()) {
			startListening();
		} else {
			ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.RECORD_AUDIO }, REQ_RECORD_AUDIO);
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
			startListening();
		} else {
			renderListening(false, "没有录音权限，可以直接打字，或在设置里改用系统输入法");
		}
	}

	private void startListening() {
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
		recognizer.startListening(intent);
		renderListening(true, "正在聆听…");
	}

	private void stopListening() {
		if (recognizer != null && listening) {
			recognizer.cancel();
		}
		renderListening(false, "点击麦克风开始说话");
	}

	private void renderListening(boolean on, String status) {
		listening = on;
		tvMicStatus.setText(status);
		btnMic.setContentDescription(on ? "停止说话" : "开始说话");
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
		int max = dp(28);
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
			renderListening(false, errorText(error));
		}

		@Override
		public void onResults(Bundle results) {
			showRecognized(results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION));
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
			startListening();// 用新语言重新开始
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

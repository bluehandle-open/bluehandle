package com.whyun.activity;

import java.util.Locale;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.whyun.IBlueToothConst;
import com.whyun.bluetooth.R;
import com.whyun.event.KeyButtonBinder;
import com.whyun.message.key.Vk;
import com.whyun.util.AppPrefs;

/**
 * PPT 演示：竖屏单手盲操作。大号“下一页”、可选音量键翻页、翻页震动和演讲计时。
 * 翻页发送 PgUp/PgDn，PowerPoint、WPS 放映和大多数 PDF 阅读器都支持。
 * 音量键只在本界面处于前台且亮屏时有效。
 */
public class PptActivity extends BaseControlActivity {

	private static final String STATE_ACCUMULATED = "accumulated";
	private static final String STATE_RUNNING = "running";
	private static final String STATE_REMINDED = "reminded";
	private static final long REMIND_BEFORE_MS = 2 * 60 * 1000L;

	private final Handler handler = new Handler(Looper.getMainLooper());
	private Vibrator vibrator;
	private boolean vibrateOnFlip;
	private boolean volumeKeys;

	/** 计时：暂停前累计的时长 + 本次开始的时刻 */
	private long accumulatedMs = 0;
	private long startedAt = 0;
	private boolean running = true;
	private boolean reminded = false;

	private TextView tvElapsed;
	private TextView tvRemaining;
	private ImageButton btnTimerToggle;

	private final Runnable tick = new Runnable() {
		@Override
		public void run() {
			renderTimer();
			handler.postDelayed(this, 1000);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ppt);
		bindBack(R.id.btn_back);
		vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);

		findViewById(R.id.btn_settings).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(PptActivity.this, SettingsActivity.class));
			}
		});

		KeyButtonBinder keys = new KeyButtonBinder(this);
		keys.tap(setupKey(R.id.key_from_current, R.drawable.bh_ic_ppt, "从当前页", "Shift+F5"), Vk.SHIFT, Vk.F5);
		keys.tap(setupKey(R.id.key_black, R.drawable.bh_ic_black_screen, "黑屏", "B"), Vk.KEY_B);
		keys.tap(setupKey(R.id.key_exit, R.drawable.bh_ic_close, "退出放映", "Esc"), Vk.ESC);

		findViewById(R.id.btn_prev).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				flip(false);
			}
		});
		findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				flip(true);
			}
		});

		tvElapsed = (TextView) findViewById(R.id.tv_elapsed);
		tvRemaining = (TextView) findViewById(R.id.tv_remaining);
		btnTimerToggle = (ImageButton) findViewById(R.id.btn_timer_toggle);
		btnTimerToggle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setRunning(!running);
			}
		});
		findViewById(R.id.btn_timer_reset).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				accumulatedMs = 0;
				startedAt = SystemClock.elapsedRealtime();
				reminded = false;
				renderTimer();
			}
		});

		if (savedInstanceState != null) {
			accumulatedMs = savedInstanceState.getLong(STATE_ACCUMULATED);
			running = savedInstanceState.getBoolean(STATE_RUNNING, true);
			reminded = savedInstanceState.getBoolean(STATE_REMINDED);
		}
		startedAt = SystemClock.elapsedRealtime();
		renderTimerButton();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 设置可能在演示设置页里改过
		vibrateOnFlip = AppPrefs.get(this, IMyPreference.USE_SHAKE, false);
		volumeKeys = AppPrefs.get(this, IMyPreference.PPT_VOLUME_KEYS, true);
		findViewById(R.id.tv_volume_hint).setVisibility(volumeKeys ? View.VISIBLE : View.GONE);
		((TextView) findViewById(R.id.tv_feedback_status)).setText(
				"翻页震动 " + (vibrateOnFlip ? "已开启" : "已关闭") + "    屏幕常亮 "
						+ (AppPrefs.get(this, IMyPreference.KEEP_SCREEN_ON, true) ? "已开启" : "已关闭"));
		handler.post(tick);
	}

	@Override
	protected void onPause() {
		handler.removeCallbacks(tick);
		super.onPause();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(STATE_ACCUMULATED, elapsedMs());
		outState.putBoolean(STATE_RUNNING, running);
		outState.putBoolean(STATE_REMINDED, reminded);
	}

	// ---------------------------------------------------------------- 翻页

	private void flip(boolean next) {
		connection.sendKeys(new byte[] { next ? Vk.PAGE_DOWN : Vk.PAGE_UP }, IBlueToothConst.toPreassRelease);
		if (vibrateOnFlip && vibrator != null) {
			vibrator.vibrate(next ? 30 : 60);// 上下页震感不同，不看屏幕也能分辨
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		int code = event.getKeyCode();
		if (volumeKeys && (code == KeyEvent.KEYCODE_VOLUME_UP || code == KeyEvent.KEYCODE_VOLUME_DOWN)) {
			if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
				flip(code == KeyEvent.KEYCODE_VOLUME_DOWN);
			}
			return true;// 吞掉按下和抬起，不改变手机音量
		}
		return super.dispatchKeyEvent(event);
	}

	// ---------------------------------------------------------------- 计时

	private long elapsedMs() {
		return accumulatedMs + (running ? SystemClock.elapsedRealtime() - startedAt : 0);
	}

	private void setRunning(boolean run) {
		if (run == running) {
			return;
		}
		if (run) {
			startedAt = SystemClock.elapsedRealtime();
		} else {
			accumulatedMs = elapsedMs();
		}
		running = run;
		renderTimerButton();
		renderTimer();
	}

	private void renderTimerButton() {
		btnTimerToggle.setImageResource(running ? R.drawable.bh_ic_pause : R.drawable.bh_ic_play);
		btnTimerToggle.setContentDescription(running ? "暂停计时" : "继续计时");
	}

	private void renderTimer() {
		long elapsed = elapsedMs();
		tvElapsed.setText(format(elapsed));
		int targetMin = AppPrefs.get(this, IMyPreference.PPT_TARGET_MINUTES, IMyPreference.DEFAULT_PPT_MINUTES);
		long remaining = targetMin * 60_000L - elapsed;
		if (remaining >= 0) {
			tvRemaining.setText("距 " + targetMin + " 分钟还剩 " + format(remaining));
			tvRemaining.setTextColor(ContextCompat.getColor(this,
					remaining <= REMIND_BEFORE_MS ? R.color.bh_action : R.color.bh_text_secondary));
		} else {
			tvRemaining.setText("已超时 " + format(-remaining));
			tvRemaining.setTextColor(ContextCompat.getColor(this, R.color.bh_action));
		}
		if (running && !reminded && remaining <= REMIND_BEFORE_MS && remaining > 0
				&& AppPrefs.get(this, IMyPreference.PPT_REMIND, true)) {
			reminded = true;
			if (vibrator != null) {
				vibrator.vibrate(800);// 长震一次提醒，不发出声音
			}
		}
	}

	private static String format(long ms) {
		long s = ms / 1000;
		long h = s / 3600;
		if (h > 0) {
			return String.format(Locale.US, "%d:%02d:%02d", h, (s / 60) % 60, s % 60);
		}
		return String.format(Locale.US, "%02d:%02d", s / 60, s % 60);
	}

}

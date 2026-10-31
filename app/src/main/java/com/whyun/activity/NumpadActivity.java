package com.whyun.activity;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;

import com.whyun.bluetooth.R;
import com.whyun.event.KeyButtonBinder;
import com.whyun.message.key.Vk;
import com.whyun.util.UnitTransformTool;

/**
 * 遥控器 · 数字键：每按一次立即发送到电脑，上方回显最近输入的内容。
 */
public class NumpadActivity extends BaseControlActivity {

	private static final int ECHO_MAX = 16;
	private static final String[] KEYS = { "1", "2", "3", "4", "5", "6", "7", "8", "9", ".", "0", "⌫" };

	private TextView tvEcho;
	private final StringBuilder echo = new StringBuilder();
	private KeyButtonBinder binder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_numpad);
		((TextView) findViewById(R.id.header_title)).setText("数字键");
		bindBack(R.id.header_back);

		tvEcho = (TextView) findViewById(R.id.tv_echo);
		binder = new KeyButtonBinder(this);
		buildGrid((LinearLayout) findViewById(R.id.numpad_grid));

		binder.action(findViewById(R.id.key_enter), new KeyButtonBinder.Action() {
			@Override
			public void run() {
				send(Vk.ENTER);
				echo.setLength(0);// 回车后开始新的一段
				tvEcho.setText("↵");
			}
		});
	}

	private void buildGrid(final LinearLayout grid) {
		final int gap = UnitTransformTool.dip2px(this, 14);
		final int height = UnitTransformTool.dip2px(this, 72);
		// 行高默认 72dp；可用高度放不下四行时按比例压缩
		grid.post(new Runnable() {
			@Override
			public void run() {
				int rows = grid.getChildCount();
				int fit = (grid.getHeight() - gap * (rows - 1)) / rows;
				if (fit > 0 && fit < height) {
					for (int i = 0; i < rows; i++) {
						grid.getChildAt(i).getLayoutParams().height = fit;
					}
					grid.requestLayout();
				}
			}
		});
		LinearLayout row = null;
		for (int i = 0; i < KEYS.length; i++) {
			if (i % 3 == 0) {
				row = new LinearLayout(this);
				row.setOrientation(LinearLayout.HORIZONTAL);
				LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
				if (i > 0) {
					rp.topMargin = gap;
				}
				grid.addView(row, rp);
			}
			final String label = KEYS[i];
			View key = label.equals("⌫") ? backspaceKey() : digitKey(label);
			LinearLayout.LayoutParams kp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
			if (i % 3 > 0) {
				kp.setMarginStart(gap);
			}
			row.addView(key, kp);

			if (label.equals("⌫")) {
				binder.action(key, new KeyButtonBinder.Action() {
					@Override
					public void run() {
						send(Vk.BACKSPACE);
						if (echo.length() > 0) {
							echo.setLength(echo.length() - 1);
						}
						tvEcho.setText(echo);
					}
				});
			} else {
				final byte vk = label.equals(".") ? Vk.PERIOD : Vk.digit(label.charAt(0) - '0');
				binder.action(key, new KeyButtonBinder.Action() {
					@Override
					public void run() {
						send(vk);
						echo.append(label);
						if (echo.length() > ECHO_MAX) {
							echo.delete(0, echo.length() - ECHO_MAX);
						}
						tvEcho.setText(echo);
					}
				});
			}
		}
	}

	private View digitKey(String label) {
		TextView key = new TextView(this);
		key.setText(label);
		key.setTextSize(26);
		key.setGravity(Gravity.CENTER);
		key.setTextColor(ContextCompat.getColor(this, R.color.bh_text));
		key.setBackgroundResource(R.drawable.bh_pad_key);
		key.setContentDescription(label.equals(".") ? "小数点" : label);
		return key;
	}

	private View backspaceKey() {
		FrameLayout key = new FrameLayout(this);
		key.setBackgroundResource(R.drawable.bh_pad_key);
		key.setContentDescription("退格");
		ImageView icon = new ImageView(this);
		icon.setImageResource(R.drawable.bh_ic_backspace);
		ImageViewCompat.setImageTintList(icon, ColorStateList.valueOf(ContextCompat.getColor(this, R.color.bh_text_secondary)));
		int size = UnitTransformTool.dip2px(this, 24);
		key.addView(icon, new FrameLayout.LayoutParams(size, size, Gravity.CENTER));
		return key;
	}

	private void send(byte vk) {
		connection.sendKeys(new byte[] { vk }, com.whyun.IBlueToothConst.toPreassRelease);
	}
}

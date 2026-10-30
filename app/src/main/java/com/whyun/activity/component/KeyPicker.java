package com.whyun.activity.component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.whyun.bluetooth.R;
import com.whyun.message.key.GamepadButtons;
import com.whyun.util.UnitTransformTool;

/**
 * 分类键盘：上方分类标签，下方按行排布的键。键名与 {@link com.whyun.message.key.KeyMap#KEY_MAP} 一致。
 */
public class KeyPicker {

	public interface OnKeyPicked {
		void onPicked(String keyName);
	}

	private static final String[] CATEGORY_NAMES = { "字母数字", "功能键", "方向与小键盘", "媒体与符号" };

	private static final List<List<String>> CATEGORIES = Arrays.asList(
			Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P",
					"Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
					"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"),
			Arrays.asList(" Ctrl ", " Alt ", "shift", "Win", "Tab", "空格", "Enter", "escape",
					"Backspace", "Delete", "Insert", "Home", " End ", "PgUp", "PgDn", "CapsLock",
					"F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9", "F10", "F11", "F12"),
			Arrays.asList("向上", "向下", "向左", "向右",
					"numpad0", "numpad1", "numpad2", "numpad3", "numpad4",
					"numpad5", "numpad6", "numpad7", "numpad8", "numpad9",
					"add", "-", "multiply", "divide", "decimal"),
			Arrays.asList("静音", "音量-", "音量+", "上一曲", "播放/暂停", "下一曲", "停止播放", "菜单键",
					";", "=", "<", ">", "~", "|", "？"));

	private static final int COLUMNS = 6;

	private final Context context;
	private final LinearLayout categoryBar;
	private final LinearLayout board;
	private final OnKeyPicked listener;
	private int category = 0;
	private Set<String> highlighted;

	public KeyPicker(LinearLayout categoryBar, LinearLayout board, OnKeyPicked listener) {
		this.context = board.getContext();
		this.categoryBar = categoryBar;
		this.board = board;
		this.listener = listener;
		buildCategories();
		buildBoard();
	}

	/** 高亮当前组合里已有的键 */
	public void setHighlighted(Set<String> keys) {
		this.highlighted = keys;
		for (int r = 0; r < board.getChildCount(); r++) {
			LinearLayout row = (LinearLayout) board.getChildAt(r);
			for (int c = 0; c < row.getChildCount(); c++) {
				View key = row.getChildAt(c);
				Object name = key.getTag();
				key.setSelected(name != null && keys != null && keys.contains(name));
			}
		}
	}

	private void buildCategories() {
		int gap = dp(6);
		for (int i = 0; i < CATEGORY_NAMES.length; i++) {
			final int index = i;
			TextView chip = new TextView(context);
			chip.setText(CATEGORY_NAMES[i]);
			chip.setTextSize(12);
			chip.setGravity(Gravity.CENTER);
			chip.setPadding(dp(12), 0, dp(12), 0);
			chip.setBackgroundResource(R.drawable.bh_bg_chip);
			chip.setTextColor(ContextCompat.getColorStateList(context, R.color.bh_chip_text));
			chip.setSelected(i == category);
			chip.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					category = index;
					for (int k = 0; k < categoryBar.getChildCount(); k++) {
						categoryBar.getChildAt(k).setSelected(k == index);
					}
					buildBoard();
				}
			});
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
			if (i > 0) {
				lp.setMarginStart(gap);
			}
			categoryBar.addView(chip, lp);
		}
	}

	private void buildBoard() {
		board.removeAllViews();
		List<String> keys = CATEGORIES.get(category);
		int gap = dp(6);
		LinearLayout row = null;
		for (int i = 0; i < keys.size(); i++) {
			if (i % COLUMNS == 0) {
				row = new LinearLayout(context);
				row.setOrientation(LinearLayout.HORIZONTAL);
				LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT, dp(42));
				if (i > 0) {
					rp.topMargin = gap;
				}
				board.addView(row, rp);
			}
			final String name = keys.get(i);
			TextView key = new TextView(context);
			String label = GamepadButtons.display(name);
			key.setText(label);
			key.setTag(name);
			key.setGravity(Gravity.CENTER);
			key.setTextSize(label.length() > 4 ? 11 : 14);
			key.setSingleLine(true);
			key.setBackgroundResource(R.drawable.bh_bg_key);
			key.setTextColor(ContextCompat.getColorStateList(context, R.color.bh_key_text));
			key.setContentDescription(label);
			key.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					listener.onPicked(name);
				}
			});
			LinearLayout.LayoutParams kp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
			if (i % COLUMNS > 0) {
				kp.setMarginStart(gap);
			}
			row.addView(key, kp);
		}
		// 最后一行补空位，保持键宽一致
		if (row != null) {
			for (int k = row.getChildCount(); k < COLUMNS; k++) {
				LinearLayout.LayoutParams kp = new LinearLayout.LayoutParams(0, 1, 1f);
				kp.setMarginStart(gap);
				row.addView(new View(context), kp);
			}
		}
		setHighlighted(highlighted);
	}

	private int dp(float v) {
		return UnitTransformTool.dip2px(context, v);
	}
}

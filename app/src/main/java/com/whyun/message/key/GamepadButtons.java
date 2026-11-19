package com.whyun.message.key;

import android.content.Context;
import com.whyun.bluetooth.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.whyun.IBlueToothConst;
import com.whyun.message.bean.KeyInfo;
import com.whyun.message.data.KeyTableOperator;

/**
 * 手柄十个按钮的元数据：协议按钮名、数据库字段前缀、界面显示名，以及键位文字描述。
 */
public final class GamepadButtons implements IBlueToothConst {

	public static final class Button {
		/** 协议里的按钮名，见 {@link IBlueToothConst} */
		public final String name;
		/** 数据库字段前缀，见 {@link KeyTableOperator} */
		public final String field;
		/** 界面显示名 */
		private final String label;

		public String label(Context context) {
			return display(context, label);
		}
		/** 默认键位的显示文字 */
		public final String defaultKeys;

		Button(String name, String field, String label, String defaultKeys) {
			this.name = name;
			this.field = field;
			this.label = label;
			this.defaultKeys = defaultKeys;
		}
	}

	/** 按设计稿映射宫格的顺序排列 */
	public static final List<Button> ALL = new ArrayList<Button>();

	static {
		ALL.add(new Button(upBtn, KeyTableOperator.KEY_TABLE_UP_FIELD, "上", "W"));
		ALL.add(new Button(downBtn, KeyTableOperator.KEY_TABLE_DOWN_FIELD, "下", "S"));
		ALL.add(new Button(leftBtn, KeyTableOperator.KEY_TABLE_LEFT_FIELD, "左", "A"));
		ALL.add(new Button(rightBtn, KeyTableOperator.KEY_TABLE_RIGHT_FIELD, "右", "D"));
		ALL.add(new Button(selectGunBtn, KeyTableOperator.KEY_TABLE_X_FIELD, "X", "U"));
		ALL.add(new Button(dropGunBtn, KeyTableOperator.KEY_TABLE_Y_FIELD, "Y", "I"));
		ALL.add(new Button(fireBtn, KeyTableOperator.KEY_TABLE_A_FIELD, "A", "J"));
		ALL.add(new Button(jumpBtn, KeyTableOperator.KEY_TABLE_B_FIELD, "B", "K"));
		ALL.add(new Button(selectBtn, KeyTableOperator.KEY_TABLE_SELECT_FIELD, "Select", "1"));
		ALL.add(new Button(startBtn, KeyTableOperator.KEY_TABLE_START_FIELD, "Start", "5"));
	}

	/** 键名（KeyMap 的键）到更短显示文字的映射，未列出的直接去掉首尾空格 */
	private static final Map<String, String> DISPLAY = new LinkedHashMap<String, String>();

	static {
		DISPLAY.put("向上", "↑");
		DISPLAY.put("向下", "↓");
		DISPLAY.put("向左", "←");
		DISPLAY.put("向右", "→");
		DISPLAY.put("escape", "Esc");
		DISPLAY.put("shift", "Shift");
		DISPLAY.put("Backspace", "退格");
		DISPLAY.put("CapsLock", "Caps");
		DISPLAY.put("Enter", "回车");
		DISPLAY.put("空格", "空格");
	}

	private GamepadButtons() {
	}

	public static String display(Context context, String keyName) {
		if (keyName == null) {
			return "";
		}
		String d = DISPLAY.get(keyName);
		String text = d != null ? d : keyName.trim();
		if ("上".equals(text)) return context.getString(R.string.ui_up);
		if ("下".equals(text)) return context.getString(R.string.ui_down);
		if ("左".equals(text)) return context.getString(R.string.ui_left);
		if ("右".equals(text)) return context.getString(R.string.ui_right);
		if ("退格".equals(text)) return context.getString(R.string.ui_backspace);
		if ("回车".equals(text)) return context.getString(R.string.ui_enter);
		if ("空格".equals(text)) return context.getString(R.string.ui_space);
		if ("静音".equals(text)) return context.getString(R.string.ui_mute);
		if ("音量-".equals(text)) return context.getString(R.string.ui_volume_down_key);
		if ("音量+".equals(text)) return context.getString(R.string.ui_volume_up_key);
		if ("上一曲".equals(text)) return context.getString(R.string.ui_previous_track);
		if ("播放/暂停".equals(text)) return context.getString(R.string.ui_play_pause);
		if ("下一曲".equals(text)) return context.getString(R.string.ui_next_track);
		if ("停止播放".equals(text)) return context.getString(R.string.ui_stop_playback);
		if ("菜单键".equals(text)) return context.getString(R.string.ui_menu_key);
		return text;
	}

	/** 把最多三个键名拼成 "Ctrl+C"，全部为空时返回空串 */
	public static String combo(Context context, String k1, String k2, String k3) {
		StringBuilder sb = new StringBuilder();
		for (String k : new String[] { k1, k2, k3 }) {
			if (k != null && k.length() > 0) {
				if (sb.length() > 0) {
					sb.append('+');
				}
				sb.append(display(context, k));
			}
		}
		return sb.toString();
	}

	public static String combo(Context context, KeyInfo info, Button b) {
		return combo(context, info.getKey(b.field, 1), info.getKey(b.field, 2), info.getKey(b.field, 3));
	}

	/** 方案的一行摘要，用于列表 */
	public static String summary(Context context, KeyInfo info) {
		StringBuilder sb = new StringBuilder();
		for (Button b : ALL) {
			String c = combo(context, info, b);
			if (c.length() == 0) {
				continue;
			}
			if (sb.length() > 0) {
				sb.append(" · ");
			}
			sb.append(b.label(context)).append(' ').append(c);
		}
		return sb.length() == 0 ? context.getString(R.string.ui_no_keys_assigned) : sb.toString();
	}

	public static String defaultSummary(Context context) {
		StringBuilder sb = new StringBuilder();
		for (Button b : ALL) {
			if (sb.length() > 0) {
				sb.append(" · ");
			}
			sb.append(b.label(context)).append(' ').append(b.defaultKeys);
		}
		return sb.toString();
	}
}

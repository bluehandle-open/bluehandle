package com.whyun.message.key;

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
		public final String label;
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

	public static String display(String keyName) {
		if (keyName == null) {
			return "";
		}
		String d = DISPLAY.get(keyName);
		return d != null ? d : keyName.trim();
	}

	/** 把最多三个键名拼成 "Ctrl+C"，全部为空时返回空串 */
	public static String combo(String k1, String k2, String k3) {
		StringBuilder sb = new StringBuilder();
		for (String k : new String[] { k1, k2, k3 }) {
			if (k != null && k.length() > 0) {
				if (sb.length() > 0) {
					sb.append('+');
				}
				sb.append(display(k));
			}
		}
		return sb.toString();
	}

	public static String combo(KeyInfo info, Button b) {
		return combo(info.getKey(b.field, 1), info.getKey(b.field, 2), info.getKey(b.field, 3));
	}

	/** 方案的一行摘要，用于列表 */
	public static String summary(KeyInfo info) {
		StringBuilder sb = new StringBuilder();
		for (Button b : ALL) {
			String c = combo(info, b);
			if (c.length() == 0) {
				continue;
			}
			if (sb.length() > 0) {
				sb.append(" · ");
			}
			sb.append(b.label).append(' ').append(c);
		}
		return sb.length() == 0 ? "尚未设置按键" : sb.toString();
	}

	public static String defaultSummary() {
		StringBuilder sb = new StringBuilder();
		for (Button b : ALL) {
			if (sb.length() > 0) {
				sb.append(" · ");
			}
			sb.append(b.label).append(' ').append(b.defaultKeys);
		}
		return sb.toString();
	}
}

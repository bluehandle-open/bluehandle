package com.whyun.message.key;

/**
 * 遥控器、PPT、数字键直接发送的 Windows 虚拟键码。
 * 组合键按数组顺序按下，电脑端逆序松开。
 */
public final class Vk {
	public static final byte BACKSPACE = 0x08;
	public static final byte TAB = 0x09;
	public static final byte ENTER = 0x0D;
	public static final byte SHIFT = 0x10;
	public static final byte ALT = 0x12;
	public static final byte ESC = 0x1B;
	public static final byte PAGE_UP = 0x21;
	public static final byte PAGE_DOWN = 0x22;
	public static final byte LEFT = 0x25;
	public static final byte UP = 0x26;
	public static final byte RIGHT = 0x27;
	public static final byte DOWN = 0x28;
	public static final byte KEY_B = 0x42;
	public static final byte KEY_D = 0x44;
	public static final byte WIN = KeyMap.KeyLWin;
	public static final byte APPS = KeyMap.KeyApps;
	public static final byte F5 = 0x74;
	public static final byte VOLUME_MUTE = KeyMap.KeyVolumeMute;
	public static final byte VOLUME_DOWN = KeyMap.KeyVolumeDown;
	public static final byte VOLUME_UP = KeyMap.KeyVolumeUp;
	public static final byte MEDIA_NEXT = KeyMap.KeyMediaNext;
	public static final byte MEDIA_PREV = KeyMap.KeyMediaPrev;
	public static final byte MEDIA_PLAY_PAUSE = KeyMap.KeyMediaPlayPause;
	/** 主键盘区的句点 VK_OEM_PERIOD */
	public static final byte PERIOD = (byte) 0xBE;

	private Vk() {
	}

	/** 主键盘区数字 0-9 */
	public static byte digit(int d) {
		return (byte) ('0' + d);
	}

	public static byte[] of(byte... keys) {
		return keys;
	}
}

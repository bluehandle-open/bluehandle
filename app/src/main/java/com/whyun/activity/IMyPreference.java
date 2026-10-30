package com.whyun.activity;

/**
 * SharedPreferences 键名。前几项沿用旧版本，类型不变以兼容已有数据。
 */
public interface IMyPreference {
	/** boolean，按键震动 */
	public static final String USE_SHAKE = "use_shake";
	/** String "0"/"1"，默认连接方式（旧版本存为字符串） */
	public static final String CONNECT_TYPE = "connect_type";
	/** int，手柄键位方案：负数为内置默认键位，非负为自定义方案 id */
	public static final String KEY_TYPE = "keyType";
	public static final String REMOVE_AD = "remove_ad";
	public static final String FIRST_OPEN = "first_open";
	/** boolean，重力感应 */
	public static final String ENABLE_GRAVITY = "enable_gravity";
	/** int 0-100，重力感应灵敏度 */
	public static final String SENSITIVITY_VOLUME = "sensitivity_volume";

	/** boolean，连接期间屏幕常亮，默认开 */
	public static final String KEEP_SCREEN_ON = "keep_screen_on";
	/** int，Wi-Fi 监听端口，默认 8088 */
	public static final String WIFI_PORT = "wifi_port";
	/** boolean，PPT 模式用音量键翻页，默认开 */
	public static final String PPT_VOLUME_KEYS = "ppt_volume_keys";
	/** int，PPT 目标演讲时长（分钟），默认 20 */
	public static final String PPT_TARGET_MINUTES = "ppt_target_minutes";
	/** boolean，PPT 剩余 2 分钟时震动提醒，默认开 */
	public static final String PPT_REMIND = "ppt_remind";
	/** String，语音识别语言，默认 zh-CN */
	public static final String VOICE_LANGUAGE = "voice_language";
	/** String，语音识别引擎：ime 系统输入法 / builtin 内置识别，默认 ime */
	public static final String VOICE_ENGINE = "voice_engine";
	/** boolean，语音发送后默认按回车，默认开 */
	public static final String VOICE_SEND_ENTER = "voice_send_enter";

	public static final int DEFAULT_SENSITIVITY = 50;
	public static final int DEFAULT_PPT_MINUTES = 20;
	public static final String VOICE_ENGINE_IME = "ime";
	public static final String VOICE_ENGINE_BUILTIN = "builtin";
}

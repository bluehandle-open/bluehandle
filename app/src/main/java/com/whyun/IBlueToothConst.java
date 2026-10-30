package com.whyun;

import com.whyun.util.MyLog;

public interface IBlueToothConst {
	String uuidString  = "00001101-0000-1000-8000-00805F9B34FB";
	boolean isDebug = false;
	int CONNECTED = 1;
	int FAILED = 0;
	int CLIENT_CONN_ERR = 2;
	
	public static final String serverSign = "MyBluetoothApp";
	public static final String clientConnectResponse = "hello server\r\n";
	
	/**消息类型*/
	public static final byte sendServerSign = 0x01;
	public static final byte sendKey = 0x02;
	public static final byte handleKeySet = 0x10;
	public static final byte pptKeySet = 0x20;
	public static final byte playerKeySet = 0x30;
	public static final byte FINISH_SOCKET = 0x40;
	public static final byte HEART_BEAT_REQ = 0x41;
	public static final byte HEART_BEAT_RESP = 0x42;
	/** 文本输入：正文为 [flags][UTF-8 字节]，flags bit0 = 输入完后按回车。详见 docs/protocol.md */
	public static final byte SEND_TEXT = 0x50;
	/** 锁定电脑屏幕，无正文。电脑端应调用 LockWorkStation，Win+L 模拟按键会被系统拦截 */
	public static final byte LOCK_SCREEN = 0x51;
	//////////消息类型结束

	/** 文本消息 flags：输入完后按回车 */
	public static final byte TEXT_FLAG_ENTER = 0x01;
	/** 单个文本报文正文最多携带的 UTF-8 字节数。长度字段是有符号 byte，留出余量 */
	public static final int TEXT_CHUNK_MAX_BYTES = 120;
	
	public static final int SET_KEY_HANDLE = -1;
	//public static final byte handleKeyGet = 0x11;
	public static final int SET_KEY_PPT = -2;
	//public static final byte pptKeyGet = 0x21;
	public static final int SET_KEY_PLAYER = -3;
		
	public static final String upBtn = "top";//0
	public static final String downBtn = "bottom";//1
	public static final String leftBtn = "left";//2
	public static final String rightBtn = "right";//3
	public static final String selectGunBtn = "xBtn";//4
	public static final String dropGunBtn = "yBtn";//5
	public static final String other1Btn = "zBtn";//6
	public static final String fireBtn = "aBtn";//7
	public static final String jumpBtn = "bBtn";//8
	public static final String other2Btn = "cBtn";//9
	public static final String selectBtn = "select";//10
	public static final String startBtn = "start";	//11
	
	public static final byte toPress = 1;
	public static final byte toRelease = 2;
	public static final byte toPreassRelease = 3;
	
	public static final byte[] DEFAULT_HANDLE_SET = new byte[] {87/*w0*/,83/*s1*/,65/*a2*/,68/*d3*/,
		49/*1 4*/,53/*5 5*/,85/*U6*/,
		73/*i7*/,79/*o8*/,74/*j9*/,75/*k10*/,76/*l 11*/};
	
	public static final int PORT = 8088;
	public static final int WIFI_TYPE = 0;
	public static final int BLUETOOTH_TYPE = 1;
	
	public static final int TOP_LEVEL_LOG = MyLog.DEBUG;
	
	public static final String URL_DOWNLOAD = "https://whyun.com/sub/0/download.php";
	public static final String SAVE_FILE_NAME = "blue_handle_pc.7z";
	public static final String SAVE_URL_FILE = "download.txt";
}

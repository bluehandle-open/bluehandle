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
	//////////消息类型结束
	
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
	
	public static final String URL_DOWNLOAD = "http://whyun.com/sub/0/download.php";
	public static final String SAVE_FILE_NAME = "blue_handle_pc.7z";
	public static final String SAVE_URL_FILE = "download.txt";
}

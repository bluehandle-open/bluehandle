package com.whyun.communication;

import com.whyun.IBlueToothConst;
import com.whyun.communication.bluetooth.ServerBlueTooth;
import com.whyun.communication.bluetooth.ServerSocketThread;
import com.whyun.communication.wifi.ServerWifi;
import com.whyun.communication.wifi.WifiSocketThread;
import com.whyun.util.MyLog;

import android.os.Handler;

public final class ServerFactory {
	private static MyLog logger = MyLog.getLogger(ServerFactory.class);
	private ServerFactory() {
		
	}
	
	public static IServer getServer(Handler pHandler,int connectType) {
		logger.debug("begin->connectType:" + connectType);
		IServer server = null;
		switch(connectType) {
		case IBlueToothConst.WIFI_TYPE:
			server = new ServerWifi(pHandler);
			break;
		case IBlueToothConst.BLUETOOTH_TYPE:
			server = new ServerBlueTooth(pHandler);
			break;
		}
		return server;
	}
	
	public static ISocketThread getSocketThread(int connectType) {
		ISocketThread thread = null;
		switch(connectType) {
		case IBlueToothConst.WIFI_TYPE:
			thread = WifiSocketThread.getInstance();
			break;
		case IBlueToothConst.BLUETOOTH_TYPE:
			thread = ServerSocketThread.getInstance();
			break;
		}
		return thread;
	}
}

package com.whyun.communication;

import com.whyun.IBlueToothConst;

public class ConnectSetting {
	public static volatile ConnectSetting instance = null;
	private volatile int connectType;
	private IServer server;
	
	private ConnectSetting() {
		this.connectType = IBlueToothConst.WIFI_TYPE;
	}
	
	public static ConnectSetting getInstance() {
		if (instance == null) {
			synchronized (ConnectSetting.class) {
				if (instance == null) {
					instance = new ConnectSetting();
				}
			}
		}
		return instance;
	}
	
	public void setConnectType(int type) {
		this.connectType = type;
	}
	
	public int getConnectType() {
		return this.connectType;
	}

	public IServer getServer() {
		return server;
	}

	public void setServer(IServer server) {
		this.server = server;
	}	
}

package com.whyun.util;

import com.whyun.MyApplication;

import android.content.Context;
import android.os.PowerManager;

public class PMManager {
	private PowerManager.WakeLock mWakeLock;
	private MyLog logger = MyLog.getLogger(PMManager.class);

	public PMManager() {

	}

	public void start() {
		MyApplication app = MyApplication.getInstance();
		PowerManager pm = (PowerManager) app
				.getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
				"XYTEST");
		mWakeLock.acquire();
		logger.info("start");
	}
	
	public void destory() {
		
	}

}

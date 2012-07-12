package com.whyun;

import android.app.Application;

public class MyApplication  extends Application {
	private static MyApplication instance = null;
	
	private MyApplication() {
		
	}

	public static MyApplication getInstance() {
		if (instance == null) {
			synchronized (MyApplication.class) {
				if (instance == null) {
					instance = new MyApplication();
				}
			}
		}
		
		return instance;
	}
	
	
}

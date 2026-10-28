package com.whyun;

import android.app.Application;
import com.whyun.util.CrashHandler;
import com.whyun.util.MyLog;

public class MyApplication  extends Application {
	private static MyApplication instance = null;

    @Override
    public void onCreate() {
        super.onCreate();
        MyLog.init(getApplicationContext());// 错误日志写入应用专属目录，不再依赖存储权限
        CrashHandler handler = CrashHandler.getInstance();
        handler.init(getApplicationContext()); //在Appliction里面设置我们的异常处理器为UncaughtExceptionHandler处理器
    }

//	public static MyApplication getInstance() {
//		if (instance == null) {
//			synchronized (MyApplication.class) {
//				if (instance == null) {
//					instance = new MyApplication();
//				}
//			}
//		}
//
//		return instance;
//	}
	
	
}

package com.whyun.communication.util;

import android.os.Handler;
import android.os.Message;

/**
 * android消息通知处理的辅助类.
 */
public class HandleProcessUtil {

	public static void send2Activity(Handler pHandler,int flag,Object obj) {
		if(pHandler == null){
			return;
		}
		Message msg = pHandler.obtainMessage();
	    msg.what = flag;
	    if (obj != null) {
	    	msg.obj=obj;
	    }
	    pHandler.sendMessage(msg);
	}
	
	public static void send2Activity(Handler pHandler,int flag) {
		send2Activity(pHandler,flag,null);
	}
}

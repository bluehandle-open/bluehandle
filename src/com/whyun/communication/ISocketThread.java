package com.whyun.communication;

import android.os.Handler;

public interface ISocketThread extends Runnable {
	/**
	 * Send msg.
	 * 
	 * @param keyName 按键名称
	 * @param pressType 按键方式
	 */
	public void sendMsg(String keyName,byte pressType);
	
	/**
	 * 关闭socket连接
	 * */
	public void close();
	
	public void close(boolean isShowMsg);
	
	public void setpHandler(Handler pHandler);
}

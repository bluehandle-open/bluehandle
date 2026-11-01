package com.whyun.communication;

import android.os.Handler;

import com.whyun.message.AbstractMessage;

public interface ISocketThread extends Runnable {
	/**
	 * 同步发送一条报文，写失败时关闭连接并通知 pHandler。
	 * 只应在 {@link ConnectionManager} 的发送线程中调用。
	 */
	public void send(AbstractMessage message);

	/**
	 * 关闭socket连接
	 * */
	public void close();

	public void close(boolean isShowMsg);

	public void setpHandler(Handler pHandler);
}

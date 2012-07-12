package com.whyun.communication;

public interface IServer  extends Runnable {
	
	/**
	 * 关闭服务，并且将服务标志位设为不可用
	 */
	public void stopServer();
	
	/**
	 * 设置服务的标志位,如果设置为true，则允许服务启动，否则不允许服务启动。
	 *
	 * @param stop the new stop
	 */
	public void setCanStart(boolean flag);
}

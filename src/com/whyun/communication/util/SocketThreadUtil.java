package com.whyun.communication.util;

import android.os.Handler;

import com.whyun.communication.ConnectSetting;
import com.whyun.communication.IServer;
import com.whyun.communication.ISocketThread;
import com.whyun.communication.ServerFactory;
import com.whyun.util.MyLog;

/**
 * 于客户端通信的线程类的服务处理类
 */
public class SocketThreadUtil {
	private static MyLog logger = MyLog.getLogger(SocketThreadUtil.class);
	
	/**
	 * 关闭当前socket连接.
	 */
	public static void close() {
		ISocketThread socketThread = ServerFactory
		.getSocketThread(ConnectSetting.getInstance().getConnectType());
		if (socketThread != null) {
			socketThread.close();
		} else {
			logger.warn("the ISocketThread is null.");
		}
		
		IServer server = ConnectSetting.getInstance().getServer();
		server.stopServer();
		logger.debug("shut down the connection");
	}
	
	public static void setHandler(Handler pHandler) {
		ISocketThread socketThread = ServerFactory
				.getSocketThread(ConnectSetting.getInstance().getConnectType());
		socketThread.setpHandler(pHandler);
	}
}

package com.whyun.communication.wifi;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import android.os.Handler;

import com.whyun.IBlueToothConst;
import com.whyun.communication.IServer;
import com.whyun.communication.util.HandleProcessUtil;
import com.whyun.util.MyLog;

/**
 * Wi-Fi 监听线程：在指定端口等待一个 PC 接入，交给 {@link WifiSocketThread} 后结束。
 */
public class ServerWifi implements IServer {
	private static final MyLog logger = MyLog.getLogger(ServerWifi.class);
	private volatile ServerSocket server = null;
	private volatile boolean stop = false;
	private final Handler pHandler;
	private final int port;

	public ServerWifi(Handler pHandler, int port) {
		this.pHandler = pHandler;
		this.port = port;
	}

	@Override
	public void run() {
		logger.info("初始化wifi开始，端口 " + port);
		try {
			server = new ServerSocket(port);
			if (stop) {// 监听建立前已被取消
				closeServer();
				return;
			}
			Socket client = server.accept();
			closeServer();// 只接受一个连接，同时释放端口

			WifiSocketThread clientThread = WifiSocketThread.getInstance();
			clientThread.init(client);
			clientThread.start();
			logger.debug("client wifi connected success");
			HandleProcessUtil.send2Activity(pHandler, IBlueToothConst.CONNECTED);
		} catch (IOException e) {
			// stopServer() 关闭 server 也会走到这里，由 ConnectionManager 判断是否为主动取消
			logger.warn("wifi server stopped: " + e.getMessage());
			HandleProcessUtil.send2Activity(pHandler, IBlueToothConst.FAILED);
		}
	}

	private void closeServer() {
		ServerSocket s = server;
		if (s != null) {
			try {
				s.close();
			} catch (IOException ignored) {
			}
		}
	}

	@Override
	public void stopServer() {
		stop = true;
		closeServer();// 让阻塞中的 accept() 立即返回
	}

	@Override
	public void setCanStart(boolean flag) {
		this.stop = !flag;
	}
}

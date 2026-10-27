package com.whyun.communication.wifi;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import android.os.Handler;

import com.whyun.IBlueToothConst;
import com.whyun.communication.ISocketThread;
import com.whyun.communication.util.HandleProcessUtil;
import com.whyun.message.FinishMessage;
import com.whyun.message.HeartBeatRequestMessage;
import com.whyun.message.KeyCommunication;
import com.whyun.util.MyLog;

/**
 * wifi 会话线程（单例）。
 * 单例本身不再继承 Thread，而是在每次新连接时创建一个新的工作线程发送心跳，
 * 这样同一个单例可以被反复 start/close。
 */
public class WifiSocketThread implements ISocketThread {

	private Socket client = null;
	private OutputStream os;
	private InputStream is;
	private volatile static WifiSocketThread instance = null;
	private volatile boolean isInit = false;
	private Handler pHandler = null;
	/** 真正发送心跳的线程，每次新连接都重新创建 */
	private Thread worker;
	private static final MyLog logger = MyLog.getLogger(WifiSocketThread.class);

	private WifiSocketThread() {

	}

	public static WifiSocketThread getInstance() {
		if (instance == null) {
			synchronized (WifiSocketThread.class) {
				if (instance == null) {
					instance = new WifiSocketThread();
				}
			}
		}
		return instance;
	}

	public synchronized void init(Socket client) throws IOException {
		if (client == null) {
			return;
		}
		if (isInit) {
			close(false);// 上一条连接没有收尾，先关掉
		}
		this.client = client;
		os = client.getOutputStream();
		is = client.getInputStream();
		isInit = true;
	}

	@Override
	public void setpHandler(Handler pHandler) {
		this.pHandler = pHandler;
	}

	/**
	 * 启动心跳工作线程。保持方法名不变，调用方无需修改。
	 */
	public synchronized void start() {
		if (worker != null && worker.isAlive()) {
			logger.debug("worker thread is still alive, skip start.");
			return;
		}
		worker = new Thread(this, "wifi-heartbeat");
		worker.start();
	}

	@Override
	public void run() {
		while (isInit && !Thread.currentThread().isInterrupted()) {
			try {
				Thread.sleep(1000);
				logger.debug("send heart beat");
				KeyCommunication.sendMsg(os, new HeartBeatRequestMessage());//发送心跳包
			} catch (InterruptedException e) {
				break;// close() 主动中断，正常退出
			} catch (IOException e) {
				e.printStackTrace();
				close(true);// 对端断开，通知界面
				break;
			} catch (Exception e) {
				e.printStackTrace();
				close(true);
				break;
			}
		}
		logger.debug("heartbeat thread exit.");
	}

	@Override
	public void close() {
		close(false);
	}

	@Override
	public synchronized void close(boolean isShowMsg) {
		if (!isInit) {
			return;
		}
		isInit = false;
		Thread t = worker;
		if (t != null && t != Thread.currentThread()) {
			t.interrupt();// 中断的是工作线程，而不是调用者
		}
		try {
			KeyCommunication.sendMsg(os, new FinishMessage());
			os.flush();
		} catch (IOException ignored) {
		}
		closeQuietly(os);
		closeQuietly(is);
		closeQuietly(client);
		logger.debug("close thread.");
		if (isShowMsg && pHandler != null) {
			logger.debug("show disconnect");
			HandleProcessUtil.send2Activity(pHandler, IBlueToothConst.CLIENT_CONN_ERR);
		}
	}

	private static void closeQuietly(Closeable c) {
		if (c == null) {
			return;
		}
		try {
			c.close();
		} catch (IOException ignored) {
		}
	}

	@Override
	public void sendMsg(String keyName, byte pressType) {
		try {
			KeyCommunication.sendMsg(os, keyName, pressType);
		} catch (IOException e) {
			e.printStackTrace();
			close(true);
		}
	}
}

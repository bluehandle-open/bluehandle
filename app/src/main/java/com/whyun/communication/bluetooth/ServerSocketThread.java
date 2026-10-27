package com.whyun.communication.bluetooth;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import com.whyun.IBlueToothConst;
import com.whyun.communication.ISocketThread;
import com.whyun.communication.util.HandleProcessUtil;
import com.whyun.message.FinishMessage;
import com.whyun.message.HeartBeatRequestMessage;
import com.whyun.message.KeyCommunication;

/**
 * 蓝牙会话线程（单例）。
 * 单例本身不再继承 Thread，而是在每次新连接时创建一个新的工作线程发送心跳，
 * 这样同一个单例可以被反复 start/close。
 */
public class ServerSocketThread implements IBlueToothConst, ISocketThread {
	private volatile boolean socketInitialized = false;
	private volatile static ServerSocketThread instance = null;
	private BluetoothSocket socket;
	private OutputStream os;
	private InputStream is;
	private Handler pHandler = null;
	/** 真正发送心跳的线程，每次新连接都重新创建 */
	private Thread worker;

	private ServerSocketThread() {

	}

	public static ServerSocketThread getInstance() {
		if (instance == null) {
			synchronized (ServerSocketThread.class) {
				if (instance == null) {
					instance = new ServerSocketThread();
				}
			}
		}
		return instance;
	}

	public synchronized void initSocket(BluetoothSocket socket) throws IOException {
		if (socketInitialized) {
			close(false);// 上一条连接没有收尾，先关掉
		}
		this.socket = socket;
		os = socket.getOutputStream();
		is = socket.getInputStream();

		os.write((serverSign + "\r\n").getBytes());
		socketInitialized = true;
	}

	/**
	 * 启动心跳工作线程。保持方法名不变，调用方无需修改。
	 */
	public synchronized void start() {
		if (worker != null && worker.isAlive()) {
			Log.i(serverSign, "worker thread is still alive, skip start.");
			return;
		}
		worker = new Thread(this, "bt-heartbeat");
		worker.start();
	}

	@Override
	public void run() {
		Log.i(serverSign, "thread is " + Thread.currentThread().getId());
		while (socketInitialized && !Thread.currentThread().isInterrupted()) {
			try {
				Thread.sleep(1000);
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
		Log.i(serverSign, "heartbeat thread exit.");
	}

	/**
	 * Send msg.
	 *
	 * @param keyName 按键名称
	 * @param pressType 按键方式
	 */
	@Override
	public void sendMsg(String keyName, byte pressType) {
		try {
			KeyCommunication.sendMsg(os, keyName, pressType);
		} catch (IOException e) {
			close(true);
		}
	}

	@Override
	public void close() {
		close(false);
	}

	@Override
	public synchronized void close(boolean isShowMsg) {
		if (!socketInitialized) {
			return;
		}
		socketInitialized = false;
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
		closeQuietly(socket);
		Log.i(serverSign, "close thread.");
		if (isShowMsg && pHandler != null) {
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
	public void setpHandler(Handler pHandler) {
		this.pHandler = pHandler;
	}

}

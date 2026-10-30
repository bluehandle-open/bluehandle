package com.whyun.communication.bluetooth;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import com.whyun.IBlueToothConst;
import com.whyun.communication.IServer;
import com.whyun.communication.util.HandleProcessUtil;
import com.whyun.util.MyLog;

/**
 * 蓝牙监听线程：等待一个 PC 接入，交给 {@link ServerSocketThread} 后结束。
 */
public class ServerBlueTooth implements IBlueToothConst, IServer {
	private static final MyLog logger = MyLog.getLogger(ServerBlueTooth.class);
	private volatile boolean done = false;
	private volatile BluetoothServerSocket serverSocket = null;
	private final Handler pHandler;

	public ServerBlueTooth(Handler pHandler) {
		this.pHandler = pHandler;
	}

	@Override
	public void run() {
		UUID uuid = UUID.fromString(uuidString);
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		try {
			serverSocket = adapter.listenUsingRfcommWithServiceRecord(serverSign, uuid);
			logger.info("create service on " + uuidString);
			if (done) {// 监听建立前已被取消
				closeServerSocket();
				return;
			}
			BluetoothSocket socket = serverSocket.accept();
			logger.info("an connection is login");
			closeServerSocket();// 只接受一个连接

			ServerSocketThread clientThread = ServerSocketThread.getInstance();
			clientThread.initSocket(socket);
			clientThread.start();
			HandleProcessUtil.send2Activity(pHandler, CONNECTED);
		} catch (IOException e) {
			// stopServer() 关闭 serverSocket 也会走到这里，由 ConnectionManager 判断是否为主动取消
			logger.warn("bluetooth server stopped: " + e.getMessage());
			HandleProcessUtil.send2Activity(pHandler, FAILED);
		} catch (SecurityException e) {
			// Android 12+ 缺少 BLUETOOTH_CONNECT 时 listenUsingRfcomm 会抛此异常
			logger.warn("bluetooth permission denied", e);
			HandleProcessUtil.send2Activity(pHandler, FAILED);
		}
	}

	private void closeServerSocket() {
		BluetoothServerSocket s = serverSocket;
		if (s != null) {
			try {
				s.close();
			} catch (IOException ignored) {
			}
		}
	}

	@Override
	public void stopServer() {
		done = true;
		closeServerSocket();// 让阻塞中的 accept() 立即返回
	}

	@Override
	public void setCanStart(boolean flag) {
		this.done = !flag;
	}
}

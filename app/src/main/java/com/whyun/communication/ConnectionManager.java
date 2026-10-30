package com.whyun.communication;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.whyun.IBlueToothConst;
import com.whyun.message.AbstractMessage;
import com.whyun.message.KeyCommunication;
import com.whyun.message.LockScreenMessage;
import com.whyun.message.TextMessage;
import com.whyun.util.MyLog;

/**
 * 与电脑端连接的唯一管理者。
 * <p>
 * 连接的建立与断开只由用户操作（{@link #start}/{@link #stop}）或断线触发，
 * 与任何界面的生命周期无关，因此在各个模式界面间切换时连接保持。
 * 界面在前台时通过 {@link #addListener} 监听状态，离开时移除。
 * <p>
 * {@link #start}、{@link #stop}、监听回调都在主线程；所有发送在单独的发送线程中按顺序执行。
 */
public final class ConnectionManager implements IBlueToothConst {

	public enum State { IDLE, WAITING, CONNECTED }

	/** 状态变化原因 */
	public static final int REASON_NONE = 0;
	/** 用户主动断开或取消等待 */
	public static final int REASON_USER = 1;
	/** 监听失败，例如端口被占用、缺少蓝牙权限 */
	public static final int REASON_FAILED = 2;
	/** 已连接后与电脑端的通信中断 */
	public static final int REASON_LOST = 3;

	public interface Listener {
		void onConnectionChanged(State state, int connectType, int reason);
	}

	private static final MyLog logger = MyLog.getLogger(ConnectionManager.class);
	private static final Charset UTF_8 = Charset.forName("UTF-8");
	private static volatile ConnectionManager instance;

	private final List<Listener> listeners = new CopyOnWriteArrayList<Listener>();
	private final ExecutorService sender = Executors.newSingleThreadExecutor(new ThreadFactory() {
		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(r, "bluehandle-sender");
			t.setDaemon(true);
			return t;
		}
	});

	private volatile State state = State.IDLE;
	private volatile int connectType = WIFI_TYPE;
	private volatile IServer server;
	private volatile ISocketThread session;
	/** 当前这次连接专用的回调 Handler，旧连接迟到的消息据此被忽略 */
	private SessionHandler currentHandler;

	private ConnectionManager() {
	}

	public static ConnectionManager get() {
		if (instance == null) {
			synchronized (ConnectionManager.class) {
				if (instance == null) {
					instance = new ConnectionManager();
				}
			}
		}
		return instance;
	}

	public State getState() {
		return state;
	}

	public boolean isConnected() {
		return state == State.CONNECTED;
	}

	/** 当前或最近一次连接使用的方式：{@link #WIFI_TYPE} 或 {@link #BLUETOOTH_TYPE} */
	public int getConnectType() {
		return connectType;
	}

	public void addListener(Listener l) {
		if (!listeners.contains(l)) {
			listeners.add(l);
		}
	}

	public void removeListener(Listener l) {
		listeners.remove(l);
	}

	/**
	 * 开始等待电脑端接入。已在等待或已连接时忽略。主线程调用。
	 *
	 * @param type     {@link #WIFI_TYPE} 或 {@link #BLUETOOTH_TYPE}
	 * @param wifiPort Wi-Fi 方式的监听端口，蓝牙方式忽略
	 */
	public void start(int type, int wifiPort) {
		if (state != State.IDLE) {
			logger.debug("start ignored, state is " + state);
			return;
		}
		connectType = type;
		SessionHandler h = new SessionHandler();
		currentHandler = h;
		server = ServerFactory.getServer(h, type, wifiPort);
		session = ServerFactory.getSocketThread(type);
		session.setpHandler(h);
		setState(State.WAITING, REASON_NONE);
		new Thread(server, "bluehandle-listen").start();
	}

	/** 断开连接或取消等待。主线程调用。 */
	public void stop() {
		if (state == State.IDLE) {
			return;
		}
		teardown();
		setState(State.IDLE, REASON_USER);
	}

	private void teardown() {
		currentHandler = null;
		final IServer s = server;
		final ISocketThread t = session;
		server = null;
		session = null;
		// 关闭会写出结束报文，属于网络操作，放到发送线程执行
		sender.execute(new Runnable() {
			@Override
			public void run() {
				if (t != null) {
					t.close();
				}
				if (s != null) {
					s.stopServer();
				}
			}
		});
	}

	private void setState(State newState, int reason) {
		state = newState;
		logger.info("connection state -> " + newState + ", reason " + reason);
		for (Listener l : listeners) {
			l.onConnectionChanged(newState, connectType, reason);
		}
	}

	// ---------------------------------------------------------------- 发送

	/** 按手柄按钮名发送，键码取自当前键位方案 */
	public void sendButton(String buttonName, byte pressType) {
		if (isConnected()) {
			post(KeyCommunication.forButton(buttonName, pressType));
		}
	}

	/** 直接发送虚拟键码，多个即组合键 */
	public void sendKeys(byte[] keyCodes, byte pressType) {
		if (isConnected()) {
			post(KeyCommunication.forKeys(keyCodes, pressType));
		}
	}

	/** 发送一段文本，超长时按 UTF-8 字符边界分包，只有最后一包携带回车标志 */
	public void sendText(String text, boolean pressEnter) {
		if (!isConnected()) {
			return;
		}
		List<byte[]> chunks = splitUtf8(text, TEXT_CHUNK_MAX_BYTES);
		for (int i = 0; i < chunks.size(); i++) {
			boolean last = i == chunks.size() - 1;
			post(new TextMessage(chunks.get(i), last && pressEnter));
		}
	}

	public void lockScreen() {
		if (isConnected()) {
			post(new LockScreenMessage());
		}
	}

	private void post(final AbstractMessage message) {
		final ISocketThread t = session;
		if (message == null || t == null) {
			return;
		}
		sender.execute(new Runnable() {
			@Override
			public void run() {
				t.send(message);
			}
		});
	}

	static List<byte[]> splitUtf8(String text, int maxBytes) {
		List<byte[]> out = new ArrayList<byte[]>();
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		int i = 0;
		while (i < text.length()) {
			int cp = text.codePointAt(i);
			byte[] b = new String(Character.toChars(cp)).getBytes(UTF_8);
			if (buf.size() + b.length > maxBytes) {
				out.add(buf.toByteArray());
				buf.reset();
			}
			buf.write(b, 0, b.length);
			i += Character.charCount(cp);
		}
		if (buf.size() > 0 || out.isEmpty()) {
			out.add(buf.toByteArray());// 空文本也发一包，用于单独发送回车
		}
		return out;
	}

	// ---------------------------------------------------------------- 回调

	private class SessionHandler extends Handler {
		SessionHandler() {
			super(Looper.getMainLooper());
		}

		@Override
		public void handleMessage(Message msg) {
			if (this != currentHandler) {
				return;// 已被 stop() 或新的 start() 取代
			}
			switch (msg.what) {
			case CONNECTED:
				if (state == State.WAITING) {
					setState(State.CONNECTED, REASON_NONE);
				}
				break;
			case FAILED:
				if (state == State.WAITING) {
					teardown();
					setState(State.IDLE, REASON_FAILED);
				}
				break;
			case CLIENT_CONN_ERR:
				if (state == State.CONNECTED) {
					teardown();
					setState(State.IDLE, REASON_LOST);
				}
				break;
			}
		}
	}
}

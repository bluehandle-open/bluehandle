package com.whyun.communication.wifi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import android.os.Handler;
import android.os.Looper;

import com.whyun.IBlueToothConst;
import com.whyun.communication.ISocketThread;
import com.whyun.communication.util.HandleProcessUtil;
import com.whyun.message.FinishMessage;
import com.whyun.message.HeartBeatRequestMessage;
import com.whyun.message.KeyCommunication;
import com.whyun.util.MyLog;

public class WifiSocketThread extends Thread implements ISocketThread {

	private Socket client = null;
	private OutputStream os;
	private InputStream is;
	private volatile static WifiSocketThread instance = null;
	private volatile boolean isInit = false;
	private Handler pHandler = null;
	private static final MyLog logger = MyLog.getLogger(WifiSocketThread.class);
	
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
		this.client = client;
		if (client != null) {
			os = client.getOutputStream();
			is = client.getInputStream();
			isInit = true;
		}
	}
	
	public void setpHandler(Handler pHandler) {
		this.pHandler = pHandler;
	}

	public void run() {
		Looper.prepare();
		if (is != null) {
			while(isInit) {
				try {
					Thread.currentThread();
					Thread.sleep(1000);
                    logger.debug("send heart beat");
                    KeyCommunication.sendMsg(os, new HeartBeatRequestMessage());//发送心跳包
					//KeyCommunication.reciveMessage(is);

				} catch (InterruptedException e) {
					e.printStackTrace();
//					if (client != null) {
						close(true);
//					}
				} catch (IOException e) {
					e.printStackTrace();
//					if (client != null) {
						close(true);
//					}
				} catch (Exception e) {
                    e.printStackTrace();

                    close(true);

                }
			}
		}
		Looper.loop();
	}
	
	public void close() {
		close(false);
	}
	
	public void close(boolean isShowMsg) {
		if (isInit) {
			Thread.currentThread().interrupt();
			try {
				KeyCommunication.sendMsg(os, new FinishMessage());
				Thread.currentThread();
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {					
				}
				if (os != null) {
					os.close();
				}
				if (is != null) {
					is.close();
				}
				if (client != null) {
					client.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			logger.debug("close thread.");
			logger.debug("this state is "+Thread.currentThread().getState());
//			instance = null;
			isInit = false;
			if (isShowMsg && pHandler != null) {
				logger.debug("show disconnect");
				HandleProcessUtil.send2Activity(pHandler,IBlueToothConst.CLIENT_CONN_ERR);
			}
			
		}
	}

	@Override
	public void sendMsg(String keyName, byte pressType) {
		try {
			KeyCommunication.sendMsg(os, keyName, pressType);
		} catch (IOException e) {			
			e.printStackTrace();
//			if (client != null) {
				close(true);
//			}
		}
	}
}

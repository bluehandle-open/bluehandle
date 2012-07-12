package com.whyun.communication.wifi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.Socket;

import android.util.Log;

import com.whyun.IBlueToothConst;
import com.whyun.communication.ISocketThread;
import com.whyun.message.KeyCommunication;

public class WifiSocketThread implements ISocketThread {

	private Socket client = null;
	private OutputStream os;
	private InputStream is;
	private volatile static WifiSocketThread instance = null;
	private volatile boolean isInit = false;
	
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
	
	public void run() {
		if (is != null) {
			while(isInit) {
				try {
					Thread.currentThread();
					Thread.sleep(10);
					KeyCommunication.reciveMessage(is);
				} catch (InterruptedException e) {
					e.printStackTrace();
					if (client != null) {
						close();
					}
				} catch (IOException e) {
					e.printStackTrace();
					if (client != null) {
						close();
					}
				}
			}
		}
	}
	
	public void close() {
		if (isInit) {
			Thread.currentThread().interrupt();
			try {
				os.close();
				is.close();
				client.close();
				Log.i(IBlueToothConst.serverSign,"close thread.");
				System.out.println("this state is "+Thread.currentThread().getState());
				instance = null;
				isInit = false;				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}

	@Override
	public void sendMsg(String keyName, byte pressType) {
		KeyCommunication.sendMsg(os, keyName, pressType);
	}

}

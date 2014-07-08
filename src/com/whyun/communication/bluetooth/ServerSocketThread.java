package com.whyun.communication.bluetooth;

//import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
//import java.io.InputStreamReader;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.whyun.IBlueToothConst;
import com.whyun.communication.ISocketThread;
import com.whyun.communication.util.HandleProcessUtil;
import com.whyun.message.FinishMessage;
import com.whyun.message.HeartBeatRequestMessage;
import com.whyun.message.KeyCommunication;
//import com.whyun.message.RecieveKeySetting;
//import com.whyun.message.key.HandleKeys;
//import com.whyun.message.util.MessageTool;

//import android.bluetooth.BluetoothAdapter;


public class ServerSocketThread extends Thread implements IBlueToothConst,ISocketThread {
	private volatile boolean socketInitialized = false;
	//private volatile boolean keySetted = false;
	private volatile static ServerSocketThread instance = null;
	private BluetoothSocket socket;
	private OutputStream os;
	private InputStream is;
	private Handler pHandler = null;
	
	private ServerSocketThread() {
		
	}
	
	public static ServerSocketThread getInstance() {
		if (instance == null) {
			synchronized(ServerSocketThread.class) {
				if (instance == null) {
					instance = new ServerSocketThread();
				}
			}
		}
		return instance;
	}

	public synchronized void initSocket(BluetoothSocket socket) throws IOException {
		
		if (!socketInitialized) {
			this.socket = socket;
			os = socket.getOutputStream();
			is = socket.getInputStream();
			
			os.write((serverSign + "\r\n").getBytes());
			if (Thread.currentThread().getState() == Thread.State.NEW) {
				Thread.currentThread().start();
			} else {
				Log.i(serverSign,"resart the thread.");
				
				try {
					Thread.currentThread().interrupt();
					Thread.sleep(1000);
					Thread.currentThread().start();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			socketInitialized = true;
		}
	}
	
	//
	
	public void run() {
		Looper.prepare();
		Log.i(serverSign, "thread is " + Thread.currentThread().getId());
		// synchronized(setKeyLock) {
		while (socketInitialized) {
			try {
				Thread.currentThread();
				Thread.sleep(1000);
				//KeyCommunication.reciveMessage(is);
				KeyCommunication.sendMsg(os, new HeartBeatRequestMessage());//发送心跳包
				System.out.println("process...");
			} catch (InterruptedException e) {
				e.printStackTrace();
//				if (socket != null) {
					close(true);
//				}
//				return;
			} catch (IOException e) {
				e.printStackTrace();
//				if (socket != null) {
					close(true);
//				}
			} catch (Exception e) {
                e.printStackTrace();
                close(true);
            }
		}
		Looper.loop();
	}
	
	/**
	 * Send msg.
	 * 
	 * @param keyName 按键名称
	 * @param pressType 按键方式
	 */
	public void sendMsg(String keyName,byte pressType) {
		try {
			KeyCommunication.sendMsg(os, keyName, pressType);
		} catch (IOException e) {
//			if (socket != null) {
				close(true);
//			}
		}
	}
	
			
	public void close() {
		close(false);
	}
			
	public void close(boolean isShowMsg) {
		if(socketInitialized) {
			try {
				Thread.currentThread().interrupt();
				socketInitialized = false;
				
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
				if (socket != null) {
					socket.close();
				}
				
				
			} catch (IOException e) {			
				e.printStackTrace();
			}
			Log.i(serverSign,"close thread.");
			System.out.println("this state is "+Thread.currentThread().getState());
			socketInitialized = false;
			if (isShowMsg && pHandler != null) {
				HandleProcessUtil.send2Activity(pHandler,IBlueToothConst.CLIENT_CONN_ERR);
			}
		}
	}

	@Override
	public void setpHandler(Handler pHandler) {
		this.pHandler = pHandler;
	}
	
}

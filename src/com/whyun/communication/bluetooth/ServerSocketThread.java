package com.whyun.communication.bluetooth;

//import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
//import java.io.InputStreamReader;
import java.io.OutputStream;

import com.whyun.IBlueToothConst;
import com.whyun.communication.ISocketThread;
import com.whyun.message.FinishMessage;
//import com.whyun.message.AbstractMessage;
import com.whyun.message.KeyCommunication;
//import com.whyun.message.RecieveKeySetting;
//import com.whyun.message.key.HandleKeys;
//import com.whyun.message.util.MessageTool;

//import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class ServerSocketThread implements IBlueToothConst,ISocketThread {
	private volatile boolean socketInitialized = false;
	//private volatile boolean keySetted = false;
	private volatile static ServerSocketThread instance = null;
	private BluetoothSocket socket;
	private OutputStream os;
	private InputStream is;
	//private HandleKeys keySet = HandleKeys.getInstance();
	
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
		Log.i(serverSign, "thread is " + Thread.currentThread().getId());
		// synchronized(setKeyLock) {
		while (socketInitialized) {
			try {
				Thread.currentThread();
				Thread.sleep(10);
				KeyCommunication.reciveMessage(is);
				
			} catch (InterruptedException e) {
				e.printStackTrace();
				if (socket != null) {
					close();
				}
				return;
			} catch (IOException e) {
				e.printStackTrace();
				if (socket != null) {
					close();
				}
			}
			System.out.println("process...");
		}
		// }
	}
	
	/**
	 * Send msg.
	 * 
	 * @param keyName 按键名称
	 * @param pressType 按键方式
	 */
	public void sendMsg(String keyName,byte pressType) {
		KeyCommunication.sendMsg(os, keyName, pressType);
	}
	
			
	public void close() {
		if(socketInitialized) {
			try {
				Thread.currentThread().interrupt();
				socketInitialized = false;
				
				KeyCommunication.sendMsg(os, new FinishMessage());
				os.close();
				is.close();
				socket.close();
				Log.i(serverSign,"close thread.");
				System.out.println("this state is "+Thread.currentThread().getState());
				//BluetoothAdapter.getDefaultAdapter().disable();
				instance = null;
			} catch (IOException e) {			
				e.printStackTrace();
			}			
		}
	}
	
}

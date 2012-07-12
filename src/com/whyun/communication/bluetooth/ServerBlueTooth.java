package com.whyun.communication.bluetooth;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;

import com.whyun.IBlueToothConst;
import com.whyun.communication.IServer;
import com.whyun.communication.util.HandleProcessUtil;

public class ServerBlueTooth implements IBlueToothConst,IServer {
	private volatile boolean done = false;
	private BluetoothServerSocket serverSocket = null;
	private BluetoothSocket socket;
	private Handler pHandler;
	
	public ServerBlueTooth(Handler pHandler) {
		this.pHandler = pHandler;
	}
	
	public void run() {
		Looper.prepare();
		System.out.println("begin init.");
		UUID uuid = UUID.fromString(uuidString); 
		
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		try {
			//adapter.enable();
			serverSocket = adapter.listenUsingRfcommWithServiceRecord(serverSign, uuid);
			//adapter.
			System.out.println("create service on " + uuidString);
			while(!done) {
				try {
					Thread.currentThread();
					Thread.sleep(10);
					socket = serverSocket.accept();					
					
					System.out.println("an connection is login .....................");
					
					ServerSocketThread serverSide = ServerSocketThread.getInstance();
					serverSide.initSocket(socket);//����socket�߳�
					
					HandleProcessUtil.send2Activity(pHandler,CONNECTED);
					done = true;//�����߳�
					//Thread.currentThread().interrupt();
				} catch (InterruptedException e) {					
					e.printStackTrace();
					HandleProcessUtil.send2Activity(pHandler,FAILED);//�����ر�����
					return;
				}
			}
		} catch (IOException e1) {			
			e1.printStackTrace();
			HandleProcessUtil.send2Activity(pHandler,FAILED);//����ʧ��
		}
		
		Looper.loop();
	}

	public void stopServer() {
		done = true;//������û�н����ɹ�����Ҫ����־
		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException e) {			
				e.printStackTrace();
			}
		}
		
		Thread.currentThread().interrupt();
	}
	
	public void setCanStart(boolean stop) {
		this.done = !stop;
	}	

}

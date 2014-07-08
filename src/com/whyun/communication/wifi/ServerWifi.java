package com.whyun.communication.wifi;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.whyun.IBlueToothConst;
import com.whyun.communication.IServer;
import com.whyun.communication.util.HandleProcessUtil;
import com.whyun.util.MyLog;

public class ServerWifi implements IServer {
	private ServerSocket server = null;
	private volatile boolean stop = false;
	private Handler pHandler;
    private WifiSocketThread clientThread;
	private MyLog logger = MyLog.getLogger(ServerWifi.class);
	
	public ServerWifi(Handler pHandler) {
		this.pHandler = pHandler;
	}
	
	public void run() {
		Looper.prepare();
		logger.info("初始化wifi开始");
		try {
			server = new ServerSocket(IBlueToothConst.PORT);
			Log.i(IBlueToothConst.serverSign,"start wifi server...");
			Socket client = null;
			while(!stop) {
				try {
					Thread.currentThread();
					Thread.sleep(10);
					client = server.accept();
					//client.getChannel().configureBlocking(false);

                    clientThread = WifiSocketThread.getInstance();
					clientThread.init(client);
                    clientThread.start();
                    logger.debug("client wifi connected success");
					
					HandleProcessUtil.send2Activity(pHandler,IBlueToothConst.CONNECTED);
					return;//完成一个连接就关闭服务线程
				} catch (InterruptedException e) {
					e.printStackTrace();
					HandleProcessUtil.send2Activity(pHandler,IBlueToothConst.FAILED);
				}				
			}
		} catch (IOException e) {
			e.printStackTrace();
			HandleProcessUtil.send2Activity(pHandler,IBlueToothConst.FAILED);
		}
		Looper.loop();
	}
	
	public void stopServer() {
		if (server != null) {
			try {
				server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
            if (clientThread != null) {
                clientThread.close();
            }
			stop = true;
		}
		Thread.currentThread().interrupt();
	}

	public void setCanStart(boolean stop) {
		this.stop = !stop;
	}	
}

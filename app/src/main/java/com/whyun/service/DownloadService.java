package com.whyun.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.whyun.activity.MainActivity;
import com.whyun.bluetooth.R;
import com.whyun.util.FileUtil;
import com.whyun.util.MyLog;

public  class DownloadService extends Service {  
	private MyLog logger  = MyLog.getLogger(DownloadService.class);
	  
    private static final int NOTIFICATION_ID = 0x12;
    private static final String CHANNEL_ID = "download";
    private NotificationCompat.Builder notificationBuilder = null;
    private NotificationManager manager = null;
    private int _progress = 0;
    private boolean isRunning = false;

    private Binder serviceBinder = new DownLoadServiceBinder();
    private static final int NET_IO_ERROR = -1;
    private static final int DOWNLOAD_ERROR = -2;
    private static final int SDCARD_NOT_FOUND = -3;


    @Override
    public void onCreate() {
        super.onCreate();
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Android 8（API 26）起没有 channel 的通知不会显示
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "下载进度", NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("下载蓝色手柄电脑端时显示进度");
            manager.createNotificationChannel(channel);
        }

        // targetSdk 31 起必须显式指定 FLAG_IMMUTABLE 或 FLAG_MUTABLE，否则创建时抛异常
        int piFlags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            piFlags |= PendingIntent.FLAG_IMMUTABLE;
        }
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), piFlags);

        notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("下载蓝色手柄电脑端")
                .setContentText("进度" + _progress + "%")
                .setProgress(100, 0, false)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setContentIntent(contentIntent);
    }

    /** 更新通知文案与进度后重新发布；progress 小于 0 表示隐藏进度条 */
    private void showNotification(String text, int progress) {
        notificationBuilder.setContentText(text);
        if (progress < 0) {
            notificationBuilder.setProgress(0, 0, false).setOngoing(false);
        } else {
            notificationBuilder.setProgress(100, progress, false);
        }
        manager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }
      
    @Override  
    public IBinder onBind(Intent intent) {  
        return serviceBinder;  
    }  

    @Override  
    public void onDestroy() {  
        isRunning = false;  
        manager.cancel(NOTIFICATION_ID);  
          
        super.onDestroy();  
    }  
      
    public void startDownLoad(String url) {  
    	logger.debug("[startDownLoad]");
        isRunning = true;
        showNotification("进度0%", 0);
        new ProgressThread(url,handler).start();
    }  

    public class DownLoadServiceBinder extends Binder {  
        public DownloadService getService() {  
            return DownloadService.this;  
        }  
    }  
      
    private class ProgressThread extends Thread { 
    	private String url;
    	private Handler handlerNow;
    	public ProgressThread(String url,Handler handler) {
    		this.url = url;
    		this.handlerNow = handler;
    	}

        @Override  
		public void run() {

			if (isRunning) {
				try {
					// 把文件存到path
					if (Environment.getExternalStorageState().equals(
							Environment.MEDIA_MOUNTED)) {
						URL myURL = new URL(url);
						HttpURLConnection conn = (HttpURLConnection) myURL
								.openConnection();

						conn.setConnectTimeout(10000);
						conn.setReadTimeout(20000);
						InputStream is = conn.getInputStream();

						if (conn.getResponseCode() == 404 || is == null) {
							logger.warn("获取数据失败");
							Message msg = handlerNow.obtainMessage();
							msg.arg1 = NET_IO_ERROR;
							msg.sendToTarget();
							return;
						}

						int filesize = conn.getContentLength();
						logger.debug("filesize:" + filesize);

						url = conn.getURL().toString();
						String filename = url
								.substring(url.lastIndexOf("/") + 1);

						String filenameStr = conn
								.getHeaderField("Content-Disposition");
						if (filenameStr != null && !"".equals(filenameStr)) {
							String splitSubStr = "filename=";
							filename = filenameStr.substring(
									filenameStr.indexOf(splitSubStr)
											+ splitSubStr.length(),
									filenameStr.length());
						}

						// 写入应用专属下载目录，Android 10+ 不再允许直接写 sdcard 根目录
						java.io.File target = new java.io.File(FileUtil.getAppDir(
								DownloadService.this, Environment.DIRECTORY_DOWNLOADS), filename);
						String path = target.getAbsolutePath();
						logger.debug("path:" + path);
						OutputStream os = new FileOutputStream(target);
						byte buf[] = new byte[1024 * 256];
						int hasRead = 0;
						int showPercentage = 0;
						boolean isFirst = true;
						while (isRunning) {
							sleep(1000);
							int numread = is.read(buf);
							logger.debug("numread:" + numread);
							if (numread == -1) {
								Message msg = handlerNow.obtainMessage();
								logger.warn("读取数据过程发生异常");
								msg.arg1 = DOWNLOAD_ERROR;
								msg.sendToTarget();
								break;
							}

							hasRead += numread;
							// logger.debug("hasRead:" + hasRead);
							os.write(buf, 0, numread);
							_progress = hasRead * 100 / filesize;
							logger.debug("prgress is " + _progress);

							Message msg = handlerNow.obtainMessage();

							if ((showPercentage == 0 && isFirst)
									|| _progress > showPercentage) {
								showPercentage = _progress;
								msg.arg1 = _progress;
								msg.sendToTarget();
								isFirst = false;
								if (_progress == 100) {
									logger.debug("[run]下载完成");
									break;// 下载完成
								}
							}

						}

						is.close();
						os.close();
						buf = null;
					} else {
						Message msg = handlerNow.obtainMessage();
						msg.arg1 = SDCARD_NOT_FOUND;
						msg.sendToTarget();
						logger.warn("sdcard没有找到");
					}
				} catch (IOException e) {
					Message msg = handlerNow.obtainMessage();
					logger.warn("下载时出现IO异常", e);
					msg.arg1 = NET_IO_ERROR;
					msg.sendToTarget();
				} catch (InterruptedException e) {
					logger.warn("sleep 时发生异常", e);
				}

			} else {
				logger.warn("当前不允许启动下载（starting download is unable now）");
			}

		}
		
    }  
      
    public Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
        	String errorText = null;
        	if (msg.arg1 == NET_IO_ERROR) {
        		errorText = "网络异常，请重试";
        	} else if (msg.arg1 == DOWNLOAD_ERROR) {
        		errorText = "下载失败，请重试";
        	} else if (msg.arg1 == SDCARD_NOT_FOUND) {
        		errorText = "存储不可用";
        	}
        	if (errorText != null) {
        		isRunning = false;
        		showNotification(errorText, -1);// 保留通知让用户看到失败原因
        		Toast.makeText(DownloadService.this, errorText, Toast.LENGTH_LONG).show();
        		return;
        	}
            showNotification("进度" + msg.arg1 + "%", msg.arg1);

            if (msg.arg1 == 100) {
                _progress = 0;
                manager.cancel(NOTIFICATION_ID);
                isRunning = false;
                Toast.makeText(DownloadService.this, "下载完毕", Toast.LENGTH_LONG).show();
                logger.debug("[handleMessage]现在完成");
            }
        }
    };
}  

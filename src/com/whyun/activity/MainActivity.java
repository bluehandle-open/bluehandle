package com.whyun.activity;

import java.io.IOException;
import java.lang.ref.WeakReference;

import net.youmi.android.AdManager;
import net.youmi.android.AdView;
import net.youmi.android.appoffers.YoumiOffersManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.whyun.IBlueToothConst;
import com.whyun.activity.component.ActivityUtil;
import com.whyun.activity.component.top.impl.TopCustom;
import com.whyun.bluetooth.R;
import com.whyun.communication.ConnectSetting;
import com.whyun.communication.IServer;
import com.whyun.communication.ISocketThread;
import com.whyun.communication.ServerFactory;
import com.whyun.event.BottomClickEvent;
import com.whyun.service.DownloadService;
import com.whyun.util.FileUtil;
import com.whyun.util.MyLog;

public class MainActivity extends Activity implements IBlueToothConst,IBottom {
	private BluetoothAdapter btAdapt;
	private ImageView img;
	private ImageButton exit;
	private ISocketThread threadSocket = null;
	private IServer server;
	private ProgressDialog prgd;

	private ConnectSetting connectSetting = ConnectSetting.getInstance();
	private int index = connectSetting.getConnectType();
	
	private MyLog logger = MyLog.getLogger(MainActivity.class);
	
	private Activity activityNow = this;
	private SharedPreferences settings;
	
	static class MyHandler extends Handler {
		WeakReference<MainActivity> mActivity;  
		  
        MyHandler(MainActivity activity) {  
                mActivity = new WeakReference<MainActivity>(activity);  
        }
        @Override
		public void handleMessage(Message msg) {			
			super.handleMessage(msg);
			MainActivity theActivity = mActivity.get();
			if (theActivity != null) {
				switch (msg.what) {
				case CONNECTED://连接成功
					theActivity.dismissDlg();
					//显示手柄界面的acitivty////////////
					Intent intent = new Intent();
					//intent.setClass(MainActivity.this, HandleWebActivity.class);
					intent.setClass(theActivity, HandleNativeActivity.class);
					theActivity.startActivity(intent);
					
					break;
				case FAILED:
					ActivityUtil.toastShow(theActivity,"开启服务失败或者您主动关闭了服务");
					theActivity.dismissDlg();
					break;
				}
			}
			
		}
	}
	
	
	private DownloadService downLoadService;  
	private boolean flag = false;
	private ImageButton pcdownload;
	
	private ServiceConnection serviceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			downLoadService = ((DownloadService.DownLoadServiceBinder) service)
					.getService();
			
			flag = true;
			downLoadService.startDownLoad(URL_DOWNLOAD);
			logger.info("[onServiceConnected]服务开启");
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			logger.info("服务结束");
		}
	};
	private WifiManager wifiManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.my_main);		
		
		YoumiOffersManager.init(MainActivity.this, "0874deea1c82005e", "8eef5acdfa4fdb3a");
		// 应用Id 应用密码 广告请求间隔(s) 测试模式
		AdManager.init(MainActivity.this,"0874deea1c82005e", "8eef5acdfa4fdb3a", 30, false);
		
		LinearLayout top = (LinearLayout)findViewById(R.id.top);
		top.addView(new TopCustom(this,"蓝色手柄", new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, KeyListActivity.class);
				startActivity(intent);
			}}).getView());

		img = (ImageView)findViewById(R.id.imgBtn);		
		img.setOnClickListener(new ClickEvent());//注册开始按钮监听事件��ʼl��		
		
		ImageButton connectType = (ImageButton)findViewById(DONATE_ID);
		connectType.setOnClickListener(new BottomClickEvent(this));
		
		ImageButton share = (ImageButton)findViewById(SHARE_ID);
		share.setOnClickListener(new BottomClickEvent(this));
		
		pcdownload = (ImageButton)findViewById(PC_DOWNLOAD_ID);
		pcdownload.setOnClickListener(new ClickEvent());
		
		ImageButton config = (ImageButton)findViewById(CONFIG_ID);
		config.setOnClickListener(new BottomClickEvent(this));
		
		exit = (ImageButton)findViewById(EXIT_ID);
		exit.setOnClickListener(new ClickEvent());
		settings = PreferenceManager.getDefaultSharedPreferences(this);
		
		wifiManager = (WifiManager)MainActivity.this.getSystemService(Context.WIFI_SERVICE);
		showGuide();
	}
	
	public void dismissDlg() {
		if (prgd != null) {
			prgd.dismiss();
		}
	}
	
	private void showGuide() {
		if (settings.getBoolean(IMyPreference.FIRST_OPEN, true)) {
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, GuideActivity.class);
			Editor editor = settings.edit();
			editor.putBoolean(IMyPreference.FIRST_OPEN, false);
			editor.commit();
			startActivity(intent);
		}
	}
	
	private void showAd() {
		
		LinearLayout head = (LinearLayout)findViewById(R.id.head);
		if (settings.getBoolean(IMyPreference.REMOVE_AD, false)) {
			head.removeAllViews();
			logger.debug("remove ad already");
		} else {
	        LinearLayout layout=new LinearLayout(this);   
	        layout.setOrientation(LinearLayout.VERTICAL);   
	        //layout.setBackgroundResource(R.drawable.bg);   
	        //初始化广告视图，可以使用其他的构造函数设置广告视图的背景色、透明度及字体颜色  
	        AdView adView = new AdView(this);   
	        LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);       
	        layout.addView(adView, params);
			
			head.addView(layout);
		}
	}
	
	private void initConnectType() {
		
		String typeNow = settings.getString(IMyPreference.CONNECT_TYPE,"0");
		logger.info("typeNow:" + typeNow);
		index = Integer.parseInt(typeNow);
		ConnectSetting.getInstance().setConnectType(index);
	}
	
	private void acceptConnection() {//启动服务器端线程
		
		prgd =  ProgressDialog.show(MainActivity.this,
				"提示", "等待PC端接入……", true);	
		prgd.setCancelable(true);
		prgd.setOnCancelListener(new OnCancelListener() {//点击取消，取消服务

            public void onCancel(DialogInterface arg0) {                
            	logger.info("...cancel button is pressed");
                if (threadSocket != null) {
                	threadSocket.close();
                }
                if (server != null) {
                	server.stopServer();
                	server.setCanStart(true);
                }
                prgd.dismiss();
            }
        });
		server = ServerFactory.getServer(new MyHandler(this), index);
		connectSetting.setServer(server);
		threadSocket = ServerFactory.getSocketThread(index);
		new Thread(server).start();
	}
	
	private String intToIp(int i)	  
    {   
		return ( i & 0xFF)+ "." + ((i >> 8 ) & 0xFF) + "." + ((i >> 16 ) & 0xFF) +"."+((i >> 24 ) & 0xFF);
	}  
	
	/**
	 * 允许pc端连接android蓝牙设置或者wifi的事件监听类.
	 */
	class ClickEvent implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			if (v == img) {
				if (index == BLUETOOTH_TYPE) {//蓝牙方式
					logger.info("当前为蓝牙方式");
					btAdapt = BluetoothAdapter.getDefaultAdapter();// ��ʼ������6�9���
					btAdapt.enable();
					Intent cwjIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE); 
					cwjIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300); 
					startActivityForResult(cwjIntent,1);
				} else {
					logger.info("当前为wifi方式");
					if (wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLED) {
						ActivityUtil.toastShow(MainActivity.this, "当前wifi不能用，请启用wifi完毕后重试");
					} else {
						WifiInfo  wifiinfo= wifiManager.getConnectionInfo();
						String ip=intToIp(wifiinfo.getIpAddress());  
						ActivityUtil.toastShow(MainActivity.this, "当前wifi的ip地址：" + ip);  
						acceptConnection();
					}					
				}
			} else if (v == pcdownload) {
				
				new AlertDialog.Builder(activityNow)
				.setMessage("亲，下载的文件有10MB左右奥，您确定要现在下载吗？点击确定，将直接下载；点击取消，将在sd卡中生成一个名为"
						+ SAVE_URL_FILE + "的文件，里面包含下载地址，你可以复制到电脑上下载。")
				.setTitle("下载电脑端")
				.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								try {
									FileUtil.saveFile(SAVE_URL_FILE, URL_DOWNLOAD);
									ActivityUtil.toastShow(activityNow, "保存下载地址成功！");
								} catch (IOException e) {
									logger.warn("保存下载地址失败",e);
									ActivityUtil.toastShow(activityNow, "保存下载地址失败");
								}
							}
						})
				.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								bindService();
								logger.info("[ClickEvent]开启服务");
							}
						}).show();   
				
			} else if (v == exit) {
				ActivityUtil.exit(activityNow);
			}
		}
		
	}
	
	/**请求蓝牙在5分钟内允许PC连接的activity结果的监听函数*/
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		logger.info("the result code is "+resultCode);
		switch (resultCode) {
		case 300://打开蓝牙监听成功
			logger.info("enable for search.");
			acceptConnection();//启动andorid端服务器线程
			break;
		}
	} 
	
	@Override
	protected void onResume() {
		super.onResume();
		initConnectType();
		showAd();
	}
	
	private void bindService() {
		logger.debug("[bindService]");
		Intent intent = new Intent(MainActivity.this, DownloadService.class);
		this.getApplicationContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
		//startService(intent);
	}

	private void unBind() {
		if (flag == true) {
			unbindService(serviceConnection);
			flag = false;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();		
	}	

	@Override
	protected void onRestart() {
		super.onRestart();
		activityNow = this;
	}

	@Override
	protected void onDestroy() {		
		super.onDestroy();
		dismissDlg();
		logger.info("destroy occured.");
		if (threadSocket != null) {
			threadSocket.close();
		}
		if (server != null) {
			server.stopServer();
		}
		unBind();
		//btAdapt.
	}	

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {   
			//按下的如果是BACK，同时没有重复
			ActivityUtil.exit(this);
            return true;
		}  
		return super.onKeyDown(keyCode, event);
	}   
	    
}  
	  
	


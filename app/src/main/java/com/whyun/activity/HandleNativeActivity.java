package com.whyun.activity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

import com.whyun.IBlueToothConst;
import com.whyun.activity.component.ActivityUtil;
import com.whyun.activity.component.MenuAdapter;
import com.whyun.activity.component.top.AbstractHeadView;
import com.whyun.activity.component.top.impl.TopQieHuan;
import com.whyun.bluetooth.R;
import com.whyun.communication.ConnectionManager;
import com.whyun.event.ButtonTouchListener;
import com.whyun.event.MySensorEventListener;
import com.whyun.message.bean.KeyInfo;
import com.whyun.message.data.KeyTableOperator;
import com.whyun.message.key.HandleKeys;
import com.whyun.util.MyLog;

public class HandleNativeActivity extends Activity implements IBlueToothConst,IMyPreference {

	private boolean useShake;
	private static final int HANDLE_ID = -4;
	private static final int EXIT_ID = -1;
	private HandleKeys keySet = HandleKeys.getInstance();
	private SharedPreferences settings = null;
	private KeyTableOperator keyTableOperator;
	
	private boolean useGravity;
	private int gravitySenstivity;
	
	/** SensorManager管理器 **/
	private SensorManager mSensorMgr = null;
	private Sensor mSensor = null;
	private MySensorEventListener sensorEventListener;
	private LinearLayout top;
	private AbstractHeadView headView;
	
	private AlertDialog menuDialog;// menu菜单Dialog
	private GridView menuGrid;
	private View menuView;
	
	/**电源管理**/
	private PowerManager pManager ;
	private WakeLock mWakeLock ;
	private static final String PM_TAG = "myPowerManager";
	
	private static final MyLog logger = MyLog.getLogger(HandleNativeActivity.class);
	
	private final ConnectionManager.Listener connectionListener = new ConnectionManager.Listener() {
		@Override
		public void onConnectionChanged(ConnectionManager.State state, int connectType, int reason) {
			if (state == ConnectionManager.State.IDLE) {
				showConnectionLost();
			}
		}
	};

	private void showConnectionLost() {
		if (isFinishing()) {
			return;
		}
		new AlertDialog.Builder(this)
		.setMessage("和电脑端的通信出现异常，电脑端可能已经退出，点击确定结束本次通信")
		.setTitle("结束通信")
		.setCancelable(false)
		.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						finish();
					}
				}).show();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, 
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.handle2);		
				
		init();
		RelativeLayout contanier = (RelativeLayout)findViewById(R.id.handleContanier);
		contanier.setOnTouchListener(new ButtonTouchListener(this,useShake));
	}
	
	private void init() {
		settings = PreferenceManager.getDefaultSharedPreferences(this);
		useShake = settings.getBoolean(USE_SHAKE, false);
		
		keyTableOperator = new KeyTableOperator(this);
		int keyType = settings.getInt(KEY_TYPE,SET_KEY_HANDLE);
		if (keyType < 0) {
			keyType = SET_KEY_HANDLE;// 旧版本的 PPT/千千静听 键位已移除
		}
		String title = null;
		if (keyType >= 0) {
			keyTableOperator.reGetReadDb();
			KeyInfo info = keyTableOperator.getKeySettingInfo(keyType);
			keyTableOperator.readFinish();
			if (info != null) {
				title = info.getKeyname();
			}
			keySet.setKeys(info, null);
		} else {
			keySet.setKeys(null, keyType, null);
		}
		
		headView = new TopQieHuan(this,getTitle(keyType, title),new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showMenu();
			}
			
		});
		top = (LinearLayout)findViewById(R.id.top);
		top.addView(headView.getView());
		
		useGravity = settings.getBoolean(ENABLE_GRAVITY, false);
		if (useGravity) {
			gravitySenstivity = settings.getInt(SENSITIVITY_VOLUME, 0);
			logger.debug(SENSITIVITY_VOLUME+gravitySenstivity);
			/** 得到SensorManager对象 **/
			mSensorMgr = (SensorManager) getSystemService(Activity.SENSOR_SERVICE);
			mSensor = mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			int timeInFrame = (100 - settings.getInt(SENSITIVITY_VOLUME, 50)) * 10;
			sensorEventListener = new MySensorEventListener(this,timeInFrame);
			// 注册listener，第三个参数是检测的精确度
			// SENSOR_DELAY_FASTEST 最灵敏 因为太快了没必要使用
			// SENSOR_DELAY_GAME 游戏开发中使用
			// SENSOR_DELAY_NORMAL 正常速度
			// SENSOR_DELAY_UI 最慢的速度
			mSensorMgr.registerListener(sensorEventListener, mSensor,
					SensorManager.SENSOR_DELAY_GAME);		
		}
		
		menuView = View.inflate(this, R.layout.gridview_menu, null);
		// 创建AlertDialog
		menuDialog = new AlertDialog.Builder(this).create();
		menuDialog.setView(menuView);
		menuDialog.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_MENU)// 监听按键
					dialog.dismiss();
				return false;
			}
		});

		menuGrid = (GridView) menuView.findViewById(R.id.gridview);
		menuGrid.setAdapter(getMenuAdapter());
		
		menuGrid.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position,
					long id) {
				Editor editor = settings.edit();//获取编辑器
				
				switch ((int)id) {
				case HANDLE_ID:
					setTitle("设置为手柄按键");
					keySet.setKeys(DEFAULT_HANDLE_SET, SET_KEY_HANDLE,editor);			
					break;
				case EXIT_ID:
					confirmDisconnect();
					break;
				default:
					logger.debug("the selected id now is " + id);
					keyTableOperator.reGetReadDb();
					KeyInfo info = keyTableOperator.getKeySettingInfo((int)id);
					keyTableOperator.readFinish();
					if (info != null) {
						keySet.setKeys(info, editor);
						setTitle("设置为" + info.getKeyname() + "按键");
					} else {
						logger.warn("the selected menu may has dirty data");
					}
					
					break;
				}
				menuDialog.dismiss();
			}
			
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/** 断开与电脑的连接并回到首页 */
	private void confirmDisconnect() {
		new AlertDialog.Builder(this)
		.setMessage("断开与电脑的连接？")
		.setNegativeButton("取消", null)
		.setPositiveButton("断开", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				finish();// 先结束界面，避免断开回调再弹“通信异常”
				ConnectionManager.get().stop();
			}
		}).show();
	}
	
	private SimpleAdapter getMenuAdapter() {
		ArrayList<HashMap<String, Object>> datas = new ArrayList<HashMap<String, Object>>();
		
		HashMap<String, Object> elementHandle = new HashMap<String, Object>();
		elementHandle.put(MenuAdapter.MAP_ITEM_IMAGE, R.drawable.menu_shoubing);
		elementHandle.put(MenuAdapter.MAP_ITEM_TEXT, "手柄");
		elementHandle.put(MenuAdapter.MAP_ITEM_ID, HANDLE_ID);
		datas.add(elementHandle);
		
		keyTableOperator.reGetReadDb();
		ArrayList<KeyInfo> list = keyTableOperator.getAllList();
		keyTableOperator.readFinish();
		if (list != null && list.size() > 0) {
			
			for (KeyInfo info:list) {
				HashMap<String, Object> element = new HashMap<String, Object>();
				element.put(MenuAdapter.MAP_ITEM_IMAGE, R.drawable.menu_custom);
				element.put(MenuAdapter.MAP_ITEM_TEXT, info.getKeyname());
				element.put(MenuAdapter.MAP_ITEM_ID, info.getKeyId());
				datas.add(element);
			}
		}
		
		HashMap<String, Object> elementEixt = new HashMap<String, Object>();
		elementEixt.put(MenuAdapter.MAP_ITEM_IMAGE, R.drawable.menu_tuichu);
		elementEixt.put(MenuAdapter.MAP_ITEM_TEXT, "退出");
		elementEixt.put(MenuAdapter.MAP_ITEM_ID, EXIT_ID);
		datas.add(elementEixt);
		
		SimpleAdapter simperAdapter = new MenuAdapter(this, datas);
		return simperAdapter;
		
	}
	
	@Override
	public void setTitle(CharSequence title) {
		if (headView != null) {
			headView.setTitle(title);
		}
	}

	/**设置菜单*/
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("menu");// 必须创建一项
		return super.onCreateOptionsMenu(menu);
	}
	
	private String getTitle(int keyType, String title) {
		String tileNow = null;
		if (title != null) {
			tileNow = (title);
		} else {
			switch (keyType) {
			case SET_KEY_HANDLE:
				tileNow = ("设置为手柄按键");			
				break;
			case SET_KEY_PPT:
				tileNow = ("设置为PPT按键");			
				break;
			case SET_KEY_PLAYER:
				tileNow = ("设置为千千静听按键");			
				break;
			}
		}
		return tileNow;
	}
	
	private void showMenu() {
		if (menuDialog == null) {
			menuDialog = new AlertDialog.Builder(this).setView(menuView).show();
		} else {
			menuDialog.show();
		}
	}
	
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		showMenu();
		return false;// 返回为true 则显示系统menu
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {   
			// 返回首页，连接保持；断开请用菜单里的“退出”或首页的断开按钮
			finish();
            return true;
		}  
		return super.onKeyDown(keyCode, event);
	}   
	@Override
	public void onResume() {
		if (useGravity) {
			mSensorMgr.registerListener(sensorEventListener, mSensor,
				SensorManager.SENSOR_DELAY_GAME);
		}

		super.onResume();
		ConnectionManager.get().addListener(connectionListener);
		if (!ConnectionManager.get().isConnected()) {
			showConnectionLost();// 在后台期间断线
		}
		pManager = ((PowerManager) getSystemService(POWER_SERVICE));//禁止休眠
        mWakeLock = pManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK |
						PowerManager.ACQUIRE_CAUSES_WAKEUP,
				this.getClass().getCanonicalName());
        mWakeLock.acquire();
	}

	@Override
	public void onPause() {
		if (useGravity) {
			mSensorMgr.unregisterListener(sensorEventListener);
		}
		ConnectionManager.get().removeListener(connectionListener);

		super.onPause();
		if(null != mWakeLock){
            mWakeLock.release();
        }
	}
}

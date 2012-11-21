package com.whyun.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.whyun.IBlueToothConst;
import com.whyun.activity.component.ActivityUtil;
import com.whyun.bluetooth.R;
import com.whyun.communication.util.SocketThreadUtil;
import com.whyun.event.ButtonTouchListener;
import com.whyun.event.MySensorEventListener;
import com.whyun.message.bean.KeyInfo;
import com.whyun.message.data.KeyTableOperator;
import com.whyun.message.key.HandleKeys;
import com.whyun.util.MyLog;

public class HandleNativeActivity extends Activity implements IBlueToothConst,IMyPreference {
	private ImageView btnUp;
	private ImageView btnDown;
	private ImageView btnLeft;
	private ImageView btnRight;
	private ImageView btnX;
	private ImageView btnY;
	private ImageView btnA;
	private ImageView btnB;
	private ImageView btnStart;
	private ImageView btnSelect;
	private boolean useShake;
	private static final int HANDLE_ID = -4;
	private static final int PPT_ID = -3;
	private static final int PLAYER_ID = -2;
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
	
	private static final MyLog logger = MyLog.getLogger(HandleNativeActivity.class);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.handle);
		btnUp = (ImageView)findViewById(R.id.btnUp);
		btnDown = (ImageView)findViewById(R.id.btnDwon);
		btnLeft = (ImageView)findViewById(R.id.btnLeft);
		btnRight = (ImageView)findViewById(R.id.btnRight);
		btnX = (ImageView)findViewById(R.id.btnX);
		btnY = (ImageView)findViewById(R.id.btnY);
		btnA = (ImageView)findViewById(R.id.btnA);
		btnB = (ImageView)findViewById(R.id.btnB);
		btnStart = (ImageView)findViewById(R.id.btnStart);
		btnSelect = (ImageView)findViewById(R.id.btnSelect);
				
		init();
		
		btnUp.setOnTouchListener(new ButtonTouchListener(HandleNativeActivity.this,useShake,upBtn));
		btnDown.setOnTouchListener(new ButtonTouchListener(HandleNativeActivity.this,useShake,downBtn));
		btnLeft.setOnTouchListener(new ButtonTouchListener(HandleNativeActivity.this,useShake,leftBtn));
		btnRight.setOnTouchListener(new ButtonTouchListener(HandleNativeActivity.this,useShake,rightBtn));
		btnX.setOnTouchListener(new ButtonTouchListener(HandleNativeActivity.this,useShake,selectGunBtn));
		btnY.setOnTouchListener(new ButtonTouchListener(HandleNativeActivity.this,useShake,dropGunBtn));
		btnA.setOnTouchListener(new ButtonTouchListener(HandleNativeActivity.this,useShake,fireBtn));
		btnB.setOnTouchListener(new ButtonTouchListener(HandleNativeActivity.this,useShake,jumpBtn));
		btnStart.setOnTouchListener(new ButtonTouchListener(HandleNativeActivity.this,useShake,startBtn));
		btnSelect.setOnTouchListener(new ButtonTouchListener(HandleNativeActivity.this,useShake,selectBtn));
	}
	
	private void init() {
		settings = PreferenceManager.getDefaultSharedPreferences(this);
		useShake = settings.getBoolean(USE_SHAKE, false);
		
		keyTableOperator = new KeyTableOperator(this);
		int keyType = settings.getInt(KEY_TYPE,SET_KEY_HANDLE);
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
		setTitleNow(keyType,title);
		
		useGravity = settings.getBoolean(ENABLE_GRAVITY, false);
		if (useGravity) {
			gravitySenstivity = settings.getInt(SENSITIVITY_VOLUME, 0);
			logger.debug(SENSITIVITY_VOLUME+gravitySenstivity);
			/** 得到SensorManager对象 **/
			mSensorMgr = (SensorManager) getSystemService(Activity.SENSOR_SERVICE);
			mSensor = mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			int timeInFrame = 100 - settings.getInt(SENSITIVITY_VOLUME, 50);
			sensorEventListener = new MySensorEventListener(this,timeInFrame);
			// 注册listener，第三个参数是检测的精确度
			// SENSOR_DELAY_FASTEST 最灵敏 因为太快了没必要使用
			// SENSOR_DELAY_GAME 游戏开发中使用
			// SENSOR_DELAY_NORMAL 正常速度
			// SENSOR_DELAY_UI 最慢的速度
			mSensorMgr.registerListener(sensorEventListener, mSensor,
					SensorManager.SENSOR_DELAY_GAME);		
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		SocketThreadUtil.close();
	}
	
	/**设置菜单*/
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		int index = 0;
		menu.add(0,HANDLE_ID,index++,R.string.menu_handle);
		menu.add(0, PPT_ID, index++, R.string.menu_ppt);
		menu.add(0, PLAYER_ID, index++, R.string.menu_player);
		keyTableOperator.reGetReadDb();
		ArrayList<KeyInfo> list = keyTableOperator.getAllList();
		keyTableOperator.readFinish();
		if (list != null && list.size() > 0) {
			
			for (KeyInfo info:list) {
				menu.add(0,info.getKeyId(),index++,info.getKeyname());					
			}
		}
		menu.add(0, EXIT_ID, index++, R.string.menu_exit);
		
		return true;
	}
	
	private void setTitleNow(int keyType, String title) {
		if (title != null) {
			setTitle(title);
		} else {
			switch (keyType) {
			case SET_KEY_HANDLE:
				setTitle("设置为手柄按键");			
				break;
			case SET_KEY_PPT:
				setTitle("设置为PPT按键");			
				break;
			case SET_KEY_PLAYER:
				setTitle("设置为千千静听按键");			
				break;
			}
		}		
	}
	
	/**菜单监听函数*/
	public boolean onOptionsItemSelected(MenuItem item) {
		Editor editor = settings.edit();//获取编辑器
		int id = item.getItemId();
		switch (id) {
		
		case HANDLE_ID:
			setTitle("设置为手柄按键");
			keySet.setKeys(DEFAULT_HANDLE_SET, SET_KEY_HANDLE,editor);			
			break;
		case PPT_ID:
			setTitle("设置为PPT按键");
			keySet.setKeys(null,SET_KEY_PPT,editor);
			break;
		case PLAYER_ID:
			setTitle("设置为千千静听按键");
			keySet.setKeys(null,SET_KEY_PLAYER,editor);
			break;
		case EXIT_ID:
			ActivityUtil.exit(this,"点击确定后，您本次和电脑间的连接将会结束!");
			break;
		default:
			logger.debug("the selected id now is " + id);
			keyTableOperator.reGetReadDb();
			KeyInfo info = keyTableOperator.getKeySettingInfo(id);
			keyTableOperator.readFinish();
			if (info != null) {
				keySet.setKeys(info, editor);
				setTitle("设置为" + info.getKeyname() + "按键");
			} else {
				logger.warn("the selected menu may has dirty data");
			}
			
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {   
			//按下的如果是BACK，同时没有重复
			ActivityUtil.exit(this,"点击确定后，您本次和电脑间的连接将会结束!");
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
	}

	@Override
	public void onPause() {
		if (useGravity) {
			mSensorMgr.unregisterListener(sensorEventListener);
		}

		super.onPause();
	}
}

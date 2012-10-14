package com.whyun.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.whyun.IBlueToothConst;
import com.whyun.activity.component.ActivityUtil;
import com.whyun.bluetooth.R;
import com.whyun.communication.util.SocketThreadUtil;
import com.whyun.event.ButtonTouchListener;
import com.whyun.message.bean.KeyInfo;
import com.whyun.message.data.KeyTableOperator;
import com.whyun.message.key.HandleKeys;

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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		int keyType = settings.getInt(KEY_TYPE,handleKeySet);
		String title = null;
		if (keyType >= 0) {
			KeyInfo info = keyTableOperator.getKeySettingInfo(keyType);
			if (info != null) {
				title = info.getKeyname();
			}
		} else {
			
		}
		setTitleNow(keyType,title);
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
		ArrayList<KeyInfo> list = keyTableOperator.getAllList();
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
			case handleKeySet:
				setTitle("设置为手柄按键");			
				break;
			case pptKeySet:
				setTitle("设置为PPT按键");			
				break;
			case playerKeySet:
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
			keySet.setKeys(DEFAULT_HANDLE_SET, handleKeySet,editor);			
			break;
		case PPT_ID:
			setTitle("设置为PPT按键");
			keySet.setKeys(null,pptKeySet,editor);
			break;
		case PLAYER_ID:
			setTitle("设置为千千静听按键");
			keySet.setKeys(null,playerKeySet,editor);
			break;
		case EXIT_ID:
			ActivityUtil.exit(this,"点击确定后，您本次和电脑间的连接将会结束!");
			break;
		default:
			KeyInfo info = keyTableOperator.getKeySettingInfo(id);
			keySet.setKeys(info, editor);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
}

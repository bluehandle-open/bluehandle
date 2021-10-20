package com.whyun.activity;

//import net.youmi.android.offers.PointsManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.whyun.IBlueToothConst;
import com.whyun.activity.component.KeyboardView;
import com.whyun.activity.component.top.impl.TopQueDing;
import com.whyun.bluetooth.R;
import com.whyun.message.bean.KeyInfo;
import com.whyun.message.data.KeyTableOperator;
import com.whyun.util.MyLog;

public class KeyAddActivity extends Activity {
	public static final String KEY_RECORD = "keyRecord";
	private static final MyLog logger = MyLog.getLogger(KeyAddActivity.class);
	private boolean isModify; 
	private KeyboardView keyboardViewUp;
	private KeyboardView keyboardViewDown;
	private KeyboardView keyboardViewLeft;
	private KeyboardView keyboardViewRight;
	private KeyboardView keyboardViewX;
	private KeyboardView keyboardViewY;
	private KeyboardView keyboardViewA;
	private KeyboardView keyboardViewB;
	private KeyboardView keyboardViewStart;
	private KeyboardView keyboardViewSelect;
	private KeyTableOperator operator;
	private EditText keynameInput;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}
	
	private void showResult(boolean operResult) {
		if (operResult) {
			new AlertDialog.Builder(KeyAddActivity.this)
			.setMessage("操作成功，点击确定后返回列表页")
			.setTitle("操作成功")					
			.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							KeyAddActivity.this.finish();
						}
					}).show();    
		} else {
			new AlertDialog.Builder(KeyAddActivity.this)
			.setMessage("操作失败，请稍后重试")
			.setTitle("操作失败")
			.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {									
						}
					}).show(); 
		}
	}
	
	private void init() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.listadd);
		operator = new KeyTableOperator(this);
		

		final KeyInfo info = (KeyInfo)getIntent().getSerializableExtra(KEY_RECORD);
		String title = "添加自定义按键";
		keynameInput = (EditText)findViewById(R.id.keynameInput);
		
		if (info != null) {
			logger.debug("要修改的info：" + info.toString());
			isModify = true;
			title = "按键[" + info.getKeyname() + "]修改";
			keynameInput.setText(info.getKeyname());
		} else {
			
		}		
		
		ScrollView keyboardUp = (ScrollView)findViewById(R.id.keyboardUp);
		keyboardViewUp = new KeyboardView(this,KeyTableOperator.KEY_TABLE_UP_FIELD,info);
		keyboardUp.addView(keyboardViewUp.getView());
		
		ScrollView keyboardDown = (ScrollView)findViewById(R.id.keyboardDown);
		keyboardViewDown = new KeyboardView(this,KeyTableOperator.KEY_TABLE_DOWN_FIELD,info);
		keyboardDown.addView(keyboardViewDown.getView());
		
		ScrollView keyboardLeft = (ScrollView)findViewById(R.id.keyboardLeft);
		keyboardViewLeft = new KeyboardView(this,KeyTableOperator.KEY_TABLE_LEFT_FIELD,info);
		keyboardLeft.addView(keyboardViewLeft.getView());
		
		ScrollView keyboardRight = (ScrollView)findViewById(R.id.keyboardRight);
		keyboardViewRight = new KeyboardView(this,KeyTableOperator.KEY_TABLE_RIGHT_FIELD,info);
		keyboardRight.addView(keyboardViewRight.getView());
		
		ScrollView keyboardX = (ScrollView)findViewById(R.id.keyboardX);
		keyboardViewX = new KeyboardView(this,KeyTableOperator.KEY_TABLE_X_FIELD,info);
		keyboardX.addView(keyboardViewX.getView());
		
		ScrollView keyboardY = (ScrollView)findViewById(R.id.keyboardY);
		keyboardViewY = new KeyboardView(this,KeyTableOperator.KEY_TABLE_Y_FIELD,info);
		keyboardY.addView(keyboardViewY.getView());
		
		ScrollView keyboardA = (ScrollView)findViewById(R.id.keyboardA);
		keyboardViewA = new KeyboardView(this,KeyTableOperator.KEY_TABLE_A_FIELD,info);
		keyboardA.addView(keyboardViewA.getView());
		
		ScrollView keyboardB = (ScrollView)findViewById(R.id.keyboardB);
		keyboardViewB = new KeyboardView(this,KeyTableOperator.KEY_TABLE_B_FIELD,info);
		keyboardB.addView(keyboardViewB.getView());
		
		ScrollView keyboardStart = (ScrollView)findViewById(R.id.keyboardStart);
		keyboardViewStart = new KeyboardView(this,KeyTableOperator.KEY_TABLE_START_FIELD,info);
		keyboardStart.addView(keyboardViewStart.getView());
		
		ScrollView keyboardSelect = (ScrollView)findViewById(R.id.keyboardSelect);
		keyboardViewSelect = new KeyboardView(this,KeyTableOperator.KEY_TABLE_SELECT_FIELD,info);
		keyboardSelect.addView(keyboardViewSelect.getView());
		
		LinearLayout head = (LinearLayout) findViewById(R.id.head);		
		head.addView(new TopQueDing(this,title,new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				ContentValues values = new ContentValues();
				values.put(KeyTableOperator.KEY_TABLE_FIELD_NAME,
						keynameInput.getText().toString());
				values = keyboardViewUp.getContentValues(values);
				values = keyboardViewDown.getContentValues(values);
				values = keyboardViewLeft.getContentValues(values);
				values = keyboardViewRight.getContentValues(values);
				values = keyboardViewX.getContentValues(values);
				values = keyboardViewY.getContentValues(values);
				values = keyboardViewA.getContentValues(values);
				values = keyboardViewB.getContentValues(values);
				values = keyboardViewStart.getContentValues(values);
				values = keyboardViewSelect.getContentValues(values);
				boolean operResult = false;
				final ContentValues valuesFinal =  values;

				if (isModify) {
					operResult = operator.updateKey(info.getKeyId(), values);
					showResult(operResult);
				} else {
					new AlertDialog.Builder(KeyAddActivity.this)
					.setMessage("点击确定添加")
					.setTitle("添加自定义按键")
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
								}
							})
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									boolean operResult
										= operator.addKey(keynameInput.getText().toString(), valuesFinal);
									showResult(operResult);
								}
							}).show();
					
				}
				
			}}).getView());		
	}
}

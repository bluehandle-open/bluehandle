package com.whyun.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;

import com.whyun.IBlueToothConst;
import com.whyun.activity.component.ActivityUtil;
import com.whyun.bluetooth.R;
import com.whyun.event.GamepadTouchHandler;
import com.whyun.event.GravitySteering;
import com.whyun.util.AppPrefs;

/**
 * 游戏手柄横屏界面。返回首页时连接保持，断开只通过右上角的断开按钮。
 */
public class GamepadActivity extends BaseControlActivity implements IBlueToothConst {

	private GamepadTouchHandler touchHandler;
	private SensorManager sensorManager;
	private Sensor accelerometer;
	private GravitySteering steering;
	private ImageButton btnGravity;
	private ImageButton btnVibrate;
	private TextView tvScheme;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gamepad);

		ViewGroup area = (ViewGroup) findViewById(R.id.gamepad_area);
		touchHandler = new GamepadTouchHandler(area);
		touchHandler.addButton(findViewById(R.id.pad_up), upBtn);
		touchHandler.addButton(findViewById(R.id.pad_down), downBtn);
		touchHandler.addButton(findViewById(R.id.pad_left), leftBtn);
		touchHandler.addButton(findViewById(R.id.pad_right), rightBtn);
		touchHandler.addButton(findViewById(R.id.pad_x), selectGunBtn);
		touchHandler.addButton(findViewById(R.id.pad_y), dropGunBtn);
		touchHandler.addButton(findViewById(R.id.pad_a), fireBtn);
		touchHandler.addButton(findViewById(R.id.pad_b), jumpBtn);
		touchHandler.addButton(findViewById(R.id.pad_select), selectBtn);
		touchHandler.addButton(findViewById(R.id.pad_start), startBtn);
		area.setOnTouchListener(touchHandler);

		bindBack(R.id.btn_back);
		tvScheme = (TextView) findViewById(R.id.tv_scheme);
		View btnScheme = findViewById(R.id.btn_scheme);
		btnScheme.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(GamepadActivity.this, GamepadSchemeActivity.class));
			}
		});

		btnGravity = (ImageButton) findViewById(R.id.btn_gravity);
		btnGravity.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean on = !AppPrefs.get(GamepadActivity.this, IMyPreference.ENABLE_GRAVITY, false);
				AppPrefs.put(GamepadActivity.this, IMyPreference.ENABLE_GRAVITY, on);
				ActivityUtil.toastShow(GamepadActivity.this, on ? "重力感应已开启，左右倾斜手机转向" : "重力感应已关闭");
				applyToggles();
			}
		});
		btnVibrate = (ImageButton) findViewById(R.id.btn_vibrate);
		btnVibrate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean on = !AppPrefs.get(GamepadActivity.this, IMyPreference.USE_SHAKE, false);
				AppPrefs.put(GamepadActivity.this, IMyPreference.USE_SHAKE, on);
				applyToggles();
			}
		});
		findViewById(R.id.btn_disconnect).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				confirmDisconnect();
			}
		});

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		accelerometer = sensorManager == null ? null : sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		if (accelerometer == null) {
			btnGravity.setVisibility(View.GONE);
		}
		applyScheme();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		applyScheme();// 从键位方案页换了方案回来
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (connection.isConnected()) {
			applyToggles();
		}
	}

	@Override
	protected void onPause() {
		touchHandler.releaseAll();// 防止离开界面时电脑端按键卡住
		stopSteering();
		super.onPause();
	}

	@Override
	protected void onDisconnected() {
		touchHandler.releaseAll();
		stopSteering();
	}

	private void applyScheme() {
		tvScheme.setText("键位：" + GamepadSchemeActivity.applySelectedScheme(this));
		findViewById(R.id.btn_scheme).setContentDescription("当前键位 " + tvScheme.getText() + "，点击更换或编辑键位方案");
	}

	/** 按偏好刷新重力感应与震动开关的显示和行为 */
	private void applyToggles() {
		boolean vibrate = AppPrefs.get(this, IMyPreference.USE_SHAKE, false);
		touchHandler.setVibrate(vibrate);
		setToggle(btnVibrate, vibrate);

		boolean gravity = accelerometer != null && AppPrefs.get(this, IMyPreference.ENABLE_GRAVITY, false);
		setToggle(btnGravity, gravity);
		stopSteering();
		if (gravity) {
			steering = new GravitySteering(AppPrefs.get(this, IMyPreference.SENSITIVITY_VOLUME,
					IMyPreference.DEFAULT_SENSITIVITY));
			sensorManager.registerListener(steering, accelerometer, SensorManager.SENSOR_DELAY_GAME);
		}
	}

	private void setToggle(ImageButton button, boolean on) {
		button.setSelected(on);
		ImageViewCompat.setImageTintList(button, ColorStateList.valueOf(ContextCompat.getColor(this, on ? R.color.bh_primary_text : R.color.bh_text_secondary)));
	}

	private void stopSteering() {
		if (steering != null) {
			sensorManager.unregisterListener(steering);
			steering = null;
		}
	}
}

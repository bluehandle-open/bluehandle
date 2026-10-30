package com.whyun.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.whyun.IBlueToothConst;
import com.whyun.activity.component.ActivityUtil;
import com.whyun.bluetooth.R;
import com.whyun.communication.ConnectionManager;
import com.whyun.event.GamepadTouchHandler;
import com.whyun.event.GravitySteering;
import com.whyun.util.AppPrefs;

/**
 * 游戏手柄横屏界面。返回首页时连接保持，断开只通过右上角的断开按钮。
 */
public class GamepadActivity extends AppCompatActivity implements IBlueToothConst, ConnectionManager.Listener {

	private final ConnectionManager connection = ConnectionManager.get();
	private GamepadTouchHandler touchHandler;
	private SensorManager sensorManager;
	private Sensor accelerometer;
	private GravitySteering steering;
	private ImageButton btnGravity;
	private ImageButton btnVibrate;
	private TextView tvScheme;
	private AlertDialog lostDialog;

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

		findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
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
		if (AppPrefs.get(this, IMyPreference.KEEP_SCREEN_ON, true)) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
		connection.addListener(this);
		if (!connection.isConnected()) {
			showConnectionLost();// 在后台期间断线
			return;
		}
		((TextView) findViewById(R.id.status_text)).setText("已连接 · "
				+ (connection.getConnectType() == BLUETOOTH_TYPE ? "蓝牙" : "Wi‑Fi"));
		View dot = findViewById(R.id.status_dot);
		DrawableCompat.setTint(DrawableCompat.wrap(dot.getBackground().mutate()),
				ContextCompat.getColor(this, R.color.bh_status_connected));
		applyToggles();
	}

	@Override
	protected void onPause() {
		connection.removeListener(this);
		touchHandler.releaseAll();// 防止离开界面时电脑端按键卡住
		stopSteering();
		super.onPause();
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
		button.setColorFilter(ContextCompat.getColor(this, on ? R.color.bh_primary_text : R.color.bh_text_secondary));
	}

	private void stopSteering() {
		if (steering != null) {
			sensorManager.unregisterListener(steering);
			steering = null;
		}
	}

	private void confirmDisconnect() {
		new AlertDialog.Builder(this)
		.setMessage("断开与电脑的连接？")
		.setNegativeButton("取消", null)
		.setPositiveButton("断开", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				finish();// 先结束界面，避免断开回调再弹“连接中断”
				connection.stop();
			}
		}).show();
	}

	@Override
	public void onConnectionChanged(ConnectionManager.State state, int connectType, int reason) {
		if (state == ConnectionManager.State.IDLE) {
			touchHandler.releaseAll();
			showConnectionLost();
		}
	}

	private void showConnectionLost() {
		if (isFinishing() || (lostDialog != null && lostDialog.isShowing())) {
			return;
		}
		lostDialog = new AlertDialog.Builder(this)
		.setTitle("连接已断开")
		.setMessage("与电脑的连接已中断，电脑端可能已经退出。请回到首页重新连接。")
		.setCancelable(false)
		.setPositiveButton("回到首页", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		}).show();
	}
}

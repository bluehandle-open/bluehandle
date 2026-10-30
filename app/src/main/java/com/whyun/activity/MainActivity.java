package com.whyun.activity;

import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.whyun.IBlueToothConst;
import com.whyun.activity.component.ActivityUtil;
import com.whyun.activity.component.PcDownloadHelper;
import com.whyun.activity.component.SegmentedControl;
import com.whyun.bluetooth.R;
import com.whyun.communication.ConnectionManager;
import com.whyun.util.AppPrefs;
import com.whyun.util.MyLog;

/**
 * 首页：选择连接方式、等待电脑端接入、选择遥控模式。
 * 连接成功后停留在首页，由用户选择模式；连接由 {@link ConnectionManager} 保持，
 * 只有点“断开”或退出应用才会结束。
 */
public class MainActivity extends AppCompatActivity implements IBlueToothConst, ConnectionManager.Listener {
	/** 请求蓝牙可见性的 requestCode */
	private static final int REQ_DISCOVERABLE = 1;
	/** 请求 Android 12+ 蓝牙运行时权限的 requestCode */
	private static final int REQ_BT_PERMISSION = 2;

	private static final MyLog logger = MyLog.getLogger(MainActivity.class);

	private final ConnectionManager connection = ConnectionManager.get();
	private SegmentedControl connectTypeSeg;
	private LinearLayout btnConnect;
	private ImageView btnConnectIcon;
	private TextView btnConnectText;
	private View statusDot;
	private TextView statusText;
	private TextView statusDetail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		findViewById(R.id.btn_settings).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, SettingsActivity.class));
			}
		});

		connectTypeSeg = new SegmentedControl((LinearLayout) findViewById(R.id.seg_connect_type));
		connectTypeSeg.setOnSelectedListener(new SegmentedControl.OnSelectedListener() {
			@Override
			public void onSelected(int index) {
				AppPrefs.setConnectType(MainActivity.this, index == 0 ? WIFI_TYPE : BLUETOOTH_TYPE);
				render();
			}
		});

		btnConnect = (LinearLayout) findViewById(R.id.btn_connect);
		btnConnectIcon = (ImageView) findViewById(R.id.btn_connect_icon);
		btnConnectText = (TextView) findViewById(R.id.btn_connect_text);
		statusDot = findViewById(R.id.status_dot);
		statusText = (TextView) findViewById(R.id.status_text);
		statusDetail = (TextView) findViewById(R.id.status_detail);
		btnConnect.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onConnectClicked();
			}
		});

		setupMode(R.id.mode_gamepad, R.drawable.bh_ic_gamepad, "游戏手柄",
				"横屏双手 · 可选默认或自定义键位", false, new Runnable() {
					@Override
					public void run() {
						openGamepad();
					}
				});
		setupMode(R.id.mode_remote, R.drawable.bh_ic_remote, "遥控器",
				"竖屏单手 · 影音控制与语音输入", true, new Runnable() {
					@Override
					public void run() {
						openMode(RemoteActivity.class);
					}
				});
		setupMode(R.id.mode_ppt, R.drawable.bh_ic_ppt, "PPT 演示",
				"竖屏单手 · 大按钮盲操作翻页", true, new Runnable() {
					@Override
					public void run() {
						openMode(PptActivity.class);
					}
				});

		findViewById(R.id.btn_download).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				PcDownloadHelper.show(MainActivity.this);
			}
		});
		findViewById(R.id.btn_help).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, GuideActivity.class));
			}
		});

		showGuideOnFirstOpen();
	}

	@Override
	protected void onResume() {
		super.onResume();
		connection.addListener(this);
		render();
	}

	@Override
	protected void onPause() {
		connection.removeListener(this);
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (isFinishing()) {
			connection.stop();// 退出应用时断开；配置变化导致的重建不断开
		}
	}

	// ---------------------------------------------------------------- 模式

	private void setupMode(int includeId, int iconRes, String title, String desc,
			boolean isNew, final Runnable action) {
		View card = findViewById(includeId);
		((ImageView) card.findViewById(R.id.mode_icon)).setImageResource(iconRes);
		((TextView) card.findViewById(R.id.mode_title)).setText(title);
		((TextView) card.findViewById(R.id.mode_desc)).setText(desc);
		card.findViewById(R.id.mode_badge).setVisibility(isNew ? View.VISIBLE : View.GONE);
		card.setContentDescription(title + "，" + desc);
		card.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				action.run();
			}
		});
	}

	/** 遥控器和 PPT 需要先连接 */
	private void openMode(Class<?> activity) {
		if (!connection.isConnected()) {
			ActivityUtil.toastShow(this, "请先连接电脑");
			return;
		}
		startActivity(new Intent(this, activity));
	}

	private void openGamepad() {
		// 选择和编辑键位方案不需要连接，打开手柄时再检查
		startActivity(new Intent(this, GamepadSchemeActivity.class));
	}

	// ---------------------------------------------------------------- 连接

	private void onConnectClicked() {
		switch (connection.getState()) {
		case IDLE:
			if (AppPrefs.connectType(this) == BLUETOOTH_TYPE) {
				startBluetoothMode();
			} else {
				startWifiMode();
			}
			break;
		case WAITING:
			connection.stop();
			break;
		case CONNECTED:
			new AlertDialog.Builder(this)
			.setMessage("断开与电脑的连接？")
			.setNegativeButton("取消", null)
			.setPositiveButton("断开", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					connection.stop();
				}
			}).show();
			break;
		}
	}

	private void startWifiMode() {
		if (localWifiIp() == null) {
			ActivityUtil.toastShow(this, "当前 Wi‑Fi 不可用，请连接 Wi‑Fi 后重试");
			return;
		}
		connection.start(WIFI_TYPE, AppPrefs.wifiPort(this));
	}

	/** 返回本机 Wi-Fi 地址，Wi-Fi 未连接时返回 null */
	private String localWifiIp() {
		WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		if (wifi == null || wifi.getWifiState() != WifiManager.WIFI_STATE_ENABLED) {
			return null;
		}
		WifiInfo info = wifi.getConnectionInfo();
		int i = info == null ? 0 : info.getIpAddress();
		if (i == 0) {
			return null;
		}
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 24) & 0xFF);
	}

	/**
	 * 返回蓝牙方式下尚未授予的运行时权限。
	 * Android 12（API 31）起监听 RFCOMM 需要 BLUETOOTH_CONNECT，
	 * 请求可见性需要 BLUETOOTH_ADVERTISE；更低版本无需运行时权限。
	 */
	private String[] missingBluetoothPermissions() {
		List<String> missing = new ArrayList<String>();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
			String[] required = new String[] {
					Manifest.permission.BLUETOOTH_CONNECT,
					Manifest.permission.BLUETOOTH_ADVERTISE };
			for (String p : required) {
				if (ContextCompat.checkSelfPermission(this, p)
						!= PackageManager.PERMISSION_GRANTED) {
					missing.add(p);
				}
			}
		}
		return missing.toArray(new String[0]);
	}

	/**
	 * 蓝牙方式入口：先确认设备支持蓝牙和权限已授予，再请求可见性。
	 * 可见性请求弹窗会在蓝牙未开启时顺带引导用户开启。
	 */
	private void startBluetoothMode() {
		if (BluetoothAdapter.getDefaultAdapter() == null) {
			ActivityUtil.toastShow(this, "当前设备不支持蓝牙");
			return;
		}
		String[] missing = missingBluetoothPermissions();
		if (missing.length > 0) {
			logger.info("request bluetooth permissions");
			ActivityCompat.requestPermissions(this, missing, REQ_BT_PERMISSION);
			return;
		}
		Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
		startActivityForResult(intent, REQ_DISCOVERABLE);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode != REQ_BT_PERMISSION) {
			return;
		}
		for (int result : grantResults) {
			if (result != PackageManager.PERMISSION_GRANTED) {
				ActivityUtil.toastShow(this, "未授予蓝牙权限，无法使用蓝牙方式，可改用 Wi‑Fi 方式");
				return;
			}
		}
		startBluetoothMode();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode != REQ_DISCOVERABLE) {
			return;
		}
		if (resultCode == RESULT_CANCELED) {
			ActivityUtil.toastShow(this, "您拒绝了蓝牙可见性请求，无法等待电脑端接入");
			return;
		}
		// 允许可见时，resultCode 为用户同意的可见时长（秒）
		connection.start(BLUETOOTH_TYPE, AppPrefs.wifiPort(this));
	}

	@Override
	public void onConnectionChanged(ConnectionManager.State state, int connectType, int reason) {
		if (reason == ConnectionManager.REASON_FAILED) {
			ActivityUtil.toastShow(this, "开启服务失败，请检查网络或蓝牙权限");
		} else if (reason == ConnectionManager.REASON_LOST) {
			ActivityUtil.toastShow(this, "与电脑的连接已断开");
		}
		render();
	}

	/** 按当前连接状态刷新连接卡片 */
	private void render() {
		ConnectionManager.State state = connection.getState();
		int type = state == ConnectionManager.State.IDLE
				? AppPrefs.connectType(this) : connection.getConnectType();
		connectTypeSeg.select(type == BLUETOOTH_TYPE ? 1 : 0);
		connectTypeSeg.setEnabled(state == ConnectionManager.State.IDLE);
		String typeName = type == BLUETOOTH_TYPE ? "蓝牙" : "Wi‑Fi";

		int dotColor;
		int fg;
		switch (state) {
		case WAITING:
			btnConnect.setBackgroundResource(R.drawable.bh_bg_connect_waiting);
			btnConnectIcon.setImageResource(R.drawable.bh_ic_close);
			btnConnectText.setText("取消等待");
			fg = R.color.bh_text;
			dotColor = R.color.bh_action;
			statusText.setText("等待电脑端接入…");
			break;
		case CONNECTED:
			btnConnect.setBackgroundResource(R.drawable.bh_bg_connect_connected);
			btnConnectIcon.setImageResource(R.drawable.bh_ic_power);
			btnConnectText.setText("断开连接");
			fg = R.color.bh_action;
			dotColor = R.color.bh_status_connected;
			statusText.setText("已连接 · " + typeName);
			break;
		default:
			btnConnect.setBackgroundResource(R.drawable.bh_bg_connect_idle);
			btnConnectIcon.setImageResource(R.drawable.bh_ic_power);
			btnConnectText.setText("开始连接");
			fg = R.color.bh_on_primary;
			dotColor = R.color.bh_text_secondary;
			statusText.setText("未连接");
			break;
		}
		btnConnectText.setTextColor(ContextCompat.getColor(this, fg));
		btnConnectIcon.setColorFilter(ContextCompat.getColor(this, fg));
		btnConnect.setContentDescription(btnConnectText.getText());
		DrawableCompat.setTint(DrawableCompat.wrap(statusDot.getBackground().mutate()),
				ContextCompat.getColor(this, dotColor));

		if (type == BLUETOOTH_TYPE) {
			statusDetail.setText("在电脑端通过蓝牙搜索本机");
		} else {
			String ip = localWifiIp();
			statusDetail.setText(ip == null ? "请先连接 Wi‑Fi"
					: "本机地址 " + ip + ":" + AppPrefs.wifiPort(this));
		}
	}

	private void showGuideOnFirstOpen() {
		if (AppPrefs.get(this, IMyPreference.FIRST_OPEN, true)) {
			AppPrefs.put(this, IMyPreference.FIRST_OPEN, false);
			startActivity(new Intent(this, GuideActivity.class));
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			ActivityUtil.exit(this, connection.getState() == ConnectionManager.State.IDLE
					? null : "退出后与电脑的连接将断开");
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}

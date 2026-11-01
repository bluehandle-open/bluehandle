package com.whyun.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.graphics.drawable.GradientDrawable;

import com.whyun.IBlueToothConst;
import com.whyun.bluetooth.R;
import com.whyun.communication.ConnectionManager;
import com.whyun.util.AppPrefs;

/**
 * 各遥控模式界面的公共基类：
 * <ul>
 * <li>按设置保持屏幕常亮；</li>
 * <li>在前台时监听连接，断开后提示并回到首页；</li>
 * <li>刷新布局里可选的 status_dot / status_text 连接状态；</li>
 * <li>返回只关闭界面，连接保持。</li>
 * </ul>
 */
public abstract class BaseControlActivity extends AppCompatActivity implements ConnectionManager.Listener {

	protected final ConnectionManager connection = ConnectionManager.get();
	private AlertDialog lostDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (AppPrefs.get(this, IMyPreference.KEEP_SCREEN_ON, true)) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
	}

	/** 子类在 setContentView 之后调用，绑定布局里的返回按钮 */
	protected void bindBack(int viewId) {
		View back = findViewById(viewId);
		if (back != null) {
			back.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		connection.addListener(this);
		renderStatus();
		if (!connection.isConnected()) {
			onDisconnected();
			showConnectionLost();// 在后台期间断线
		}
	}

	@Override
	protected void onPause() {
		connection.removeListener(this);
		super.onPause();
	}

	@Override
	public void onConnectionChanged(ConnectionManager.State state, int connectType, int reason) {
		if (state == ConnectionManager.State.CONNECTED) {
			// 蓝牙断线后自动重新等待，电脑端重连上来就直接继续用
			if (lostDialog != null && lostDialog.isShowing()) {
				lostDialog.dismiss();
			}
			renderStatus();
			onReconnected();
		} else {
			renderStatus();
			onDisconnected();
			showConnectionLost();
		}
	}

	/** 断线时调用，子类可在这里松开按住的键、停止传感器等 */
	protected void onDisconnected() {
	}

	/** 蓝牙断线后电脑端又自动连上时调用，子类可恢复 {@link #onDisconnected} 停掉的东西 */
	protected void onReconnected() {
	}

	private void renderStatus() {
		ConnectionManager.State state = connection.getState();
		TextView text = (TextView) findViewById(R.id.status_text);
		if (text != null) {
			String type = connection.getConnectType() == IBlueToothConst.BLUETOOTH_TYPE ? "蓝牙" : "Wi‑Fi";
			text.setText(state == ConnectionManager.State.CONNECTED ? "已连接 · " + type
					: state == ConnectionManager.State.WAITING ? "等待重新连接…" : "未连接");
		}
		View dot = findViewById(R.id.status_dot);
		if (dot != null) {
			// 直接改形状颜色：5.x 上 DrawableCompat.wrap 返回新对象，着色不会作用到控件上
			((GradientDrawable) dot.getBackground().mutate()).setColor(
					ContextCompat.getColor(this, state == ConnectionManager.State.CONNECTED ? R.color.bh_status_connected
							: state == ConnectionManager.State.WAITING ? R.color.bh_action : R.color.bh_text_secondary));
		}
	}

	/** 填充一个 bh_remote_key：图标、名称、对应快捷键 */
	protected View setupKey(int id, int icon, String label, String hint) {
		View key = findViewById(id);
		((android.widget.ImageView) key.findViewById(R.id.key_icon)).setImageResource(icon);
		((TextView) key.findViewById(R.id.key_label)).setText(label);
		((TextView) key.findViewById(R.id.key_hint)).setText(hint);
		key.setContentDescription(label + "，" + hint);
		return key;
	}

	/** 主动断开：先结束界面，避免断开回调再弹“连接已断开” */
	protected void confirmDisconnect() {
		new AlertDialog.Builder(this)
		.setMessage("断开与电脑的连接？")
		.setNegativeButton("取消", null)
		.setPositiveButton("断开", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				finish();
				connection.stop();
			}
		}).show();
	}

	private void showConnectionLost() {
		if (isFinishing()) {
			return;
		}
		String message = connection.getState() == ConnectionManager.State.WAITING
				? "与电脑的连接已中断，正在等待电脑端重新连接。连上后这个提示会自动消失，也可以回到首页。"
				: "与电脑的连接已中断，电脑端可能已经退出。请回到首页重新连接。";
		if (lostDialog != null && lostDialog.isShowing()) {
			lostDialog.setMessage(message);// 例如重新等待失败，转为未连接
			return;
		}
		lostDialog = new AlertDialog.Builder(this)
		.setTitle("连接已断开")
		.setMessage(message)
		.setCancelable(false)
		.setPositiveButton("回到首页", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		}).show();
	}
}

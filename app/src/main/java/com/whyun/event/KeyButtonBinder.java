package com.whyun.event;

import android.app.Service;
import android.content.Context;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;

import com.whyun.IBlueToothConst;
import com.whyun.activity.IMyPreference;
import com.whyun.communication.ConnectionManager;
import com.whyun.util.AppPrefs;

/**
 * 把一个按钮视图绑定到电脑按键。
 * <ul>
 * <li>点按：点击时发送"按下并松开"，适合锁屏、切换窗口、媒体键等。</li>
 * <li>按住：手指按下发"按下"，抬起或取消发"松开"，由电脑端系统负责长按连发，适合方向键、音量、翻页。</li>
 * </ul>
 * 两种方式都保留无障碍点击：读屏软件激活时发送一次"按下并松开"。
 */
public class KeyButtonBinder {

	public interface Action {
		void run();
	}

	private final ConnectionManager connection = ConnectionManager.get();
	private final Vibrator vibrator;
	private final boolean vibrate;

	public KeyButtonBinder(Context context) {
		this.vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
		this.vibrate = AppPrefs.get(context, IMyPreference.USE_SHAKE, false);
	}

	public void tap(View view, final byte... keys) {
		action(view, new Action() {
			@Override
			public void run() {
				connection.sendKeys(keys, IBlueToothConst.toPreassRelease);
			}
		});
	}

	/** 点击时执行任意动作，并给出震动反馈 */
	public void action(View view, final Action action) {
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				feedback();
				action.run();
			}
		});
	}

	public void hold(View view, final byte... keys) {
		view.setOnTouchListener(new View.OnTouchListener() {
			private boolean down;

			@Override
			public boolean onTouch(View v, MotionEvent e) {
				switch (e.getActionMasked()) {
				case MotionEvent.ACTION_DOWN:
					down = true;
					v.setPressed(true);
					if (v.getParent() != null) {
						v.getParent().requestDisallowInterceptTouchEvent(true);// 按住时不让外层滚动抢走事件
					}
					feedback();
					connection.sendKeys(keys, IBlueToothConst.toPress);
					return true;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL:
					if (down) {
						down = false;
						v.setPressed(false);
						connection.sendKeys(keys, IBlueToothConst.toRelease);
					}
					return true;
				default:
					return true;
				}
			}
		});
		view.setOnClickListener(new View.OnClickListener() {// 仅无障碍激活会走到这里
			@Override
			public void onClick(View v) {
				connection.sendKeys(keys, IBlueToothConst.toPreassRelease);
			}
		});
	}

	private void feedback() {
		if (vibrate && vibrator != null) {
			vibrator.vibrate(40);
		}
	}
}

package com.whyun.event;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.graphics.Rect;
import android.os.Vibrator;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.whyun.IBlueToothConst;
import com.whyun.communication.ConnectionManager;

/**
 * 手柄区域的多点触控处理。
 * <p>
 * 记录每根手指当前压在哪个按钮上：手指按下或滑入按钮时发送"按下"，抬起、滑出或被系统取消时发送"松开"。
 * 同一按钮被多根手指压住时只发送一次按下，最后一根手指离开时才松开，保证每次按下都有对应的松开。
 * 按钮视图本身不可点击，触摸由容器统一处理，按下时设置 pressed 状态以显示高亮。
 */
public class GamepadTouchHandler implements View.OnTouchListener {

	private static final class Btn {
		final View view;
		final String name;
		int holders;

		Btn(View view, String name) {
			this.view = view;
			this.name = name;
		}
	}

	private final ViewGroup area;
	private final List<Btn> buttons = new ArrayList<Btn>();
	private final SparseArray<Btn> pointerToBtn = new SparseArray<Btn>();
	private final Rect tmp = new Rect();
	private final ConnectionManager connection = ConnectionManager.get();
	private final Vibrator vibrator;
	private boolean vibrate;

	public GamepadTouchHandler(ViewGroup area) {
		this.area = area;
		this.vibrator = (Vibrator) area.getContext().getSystemService(Service.VIBRATOR_SERVICE);
	}

	/** 注册一个按钮视图，name 为协议按钮名，见 {@link IBlueToothConst} */
	public void addButton(View view, String name) {
		buttons.add(new Btn(view, name));
	}

	public void setVibrate(boolean vibrate) {
		this.vibrate = vibrate;
	}

	@Override
	public boolean onTouch(View v, MotionEvent e) {
		switch (e.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_POINTER_DOWN: {
			int idx = e.getActionIndex();
			moveTo(e.getPointerId(idx), hit(e.getX(idx), e.getY(idx)));
			break;
		}
		case MotionEvent.ACTION_MOVE:
			for (int i = 0; i < e.getPointerCount(); i++) {
				moveTo(e.getPointerId(i), hit(e.getX(i), e.getY(i)));
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP: {
			int id = e.getPointerId(e.getActionIndex());
			moveTo(id, null);
			pointerToBtn.remove(id);
			break;
		}
		case MotionEvent.ACTION_CANCEL:
			releaseAll();
			break;
		}
		return true;
	}

	/** 松开所有按钮，界面离开前台时调用，避免电脑端按键卡住 */
	public void releaseAll() {
		for (int i = 0; i < pointerToBtn.size(); i++) {
			Btn b = pointerToBtn.valueAt(i);
			if (b != null) {
				release(b);
			}
		}
		pointerToBtn.clear();
	}

	private void moveTo(int pointerId, Btn target) {
		Btn current = pointerToBtn.get(pointerId);
		if (current == target) {
			return;
		}
		if (current != null) {
			release(current);
		}
		pointerToBtn.put(pointerId, target);
		if (target != null) {
			press(target);
		}
	}

	private void press(Btn b) {
		if (b.holders++ == 0) {
			connection.sendButton(b.name, IBlueToothConst.toPress);
			b.view.setPressed(true);
			if (vibrate && vibrator != null) {
				vibrator.vibrate(50);
			}
		}
	}

	private void release(Btn b) {
		if (b.holders > 0 && --b.holders == 0) {
			connection.sendButton(b.name, IBlueToothConst.toRelease);
			b.view.setPressed(false);
		}
	}

	/** 返回坐标（相对容器）所在的按钮 */
	private Btn hit(float x, float y) {
		for (Btn b : buttons) {
			if (b.view.getVisibility() != View.VISIBLE) {
				continue;
			}
			tmp.set(0, 0, b.view.getWidth(), b.view.getHeight());
			area.offsetDescendantRectToMyCoords(b.view, tmp);
			if (tmp.contains((int) x, (int) y)) {
				return b;
			}
		}
		return null;
	}
}

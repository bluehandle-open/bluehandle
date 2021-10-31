package com.whyun.event;

import java.util.HashMap;

import android.app.Activity;
import android.app.Service;
import android.content.res.Resources;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.whyun.IBlueToothConst;
import com.whyun.bluetooth.R;
import com.whyun.communication.ConnectSetting;
import com.whyun.communication.ISocketThread;
import com.whyun.communication.ServerFactory;
import com.whyun.util.MyLog;
//import com.whyun.message.key.HandleKeys;
//import android.widget.Toast;

public class ButtonTouchListener implements View.OnTouchListener {
	// private String buttonId;
	private static final String LOG_TAG = ButtonTouchListener.class.getName();
	private ISocketThread serverThread = ServerFactory
			.getSocketThread(ConnectSetting.getInstance().getConnectType());
	private boolean useShake;
	private Activity activity;
	private MyLog logger = MyLog.getLogger(LOG_TAG);
	// private HandleKeys handleKeys = HandleKeys.getInstance();
	Vibrator mVibrator = null;

	private ImageView btnUp;
	private ImageView btnDown;
	private ImageView btnLeft;
	private ImageView btnRight;
	private TextView btnX;
	private TextView btnY;
	private TextView btnA;
	private TextView btnB;
	private TextView btnStart;
	private TextView btnSelect;

	private boolean locationInit;
	private int[] upLocation = new int[2];
	private int[] downLocation = new int[2];
	private int[] leftLocation = new int[2];
	private int[] rightLocation = new int[2];
	private int[] xLocation = new int[2];
	private int[] yLocation = new int[2];
	private int[] aLocation = new int[2];
	private int[] bLocation = new int[2];
	private int[] startLocation = new int[2];
	private int[] selectLocation = new int[2];

	private static final int BTN_UP = R.id.btnUp;
	private static final int BTN_DOWN = R.id.btnDwon;
	private static final int BTN_LEFT = R.id.btnLeft;
	private static final int BTN_RIGHT = R.id.btnRight;
	private static final int BTN_X = R.id.btnX;
	private static final int BTN_Y = R.id.btnY;
	private static final int BTN_A = R.id.btnA;
	private static final int BTN_B = R.id.btnB;
	private static final int BTN_START = R.id.btnStart;
	private static final int BTN_SELECT = R.id.btnSelect;

	private static final HashMap<Integer, Integer> BTN_SELECTED_BG_MAP
		= new HashMap<Integer, Integer>() {
		{
			put(BTN_UP,R.drawable.up_btn_selected);
			put(BTN_DOWN,R.drawable.down_btn_selected);
			put(BTN_LEFT,R.drawable.left_btn_selected);
			put(BTN_RIGHT,R.drawable.right_btn_selected);
			put(BTN_X,R.drawable.circle_shape_button_selected);
			put(BTN_Y,R.drawable.circle_shape_button_selected);
			put(BTN_A,R.drawable.circle_shape_button_selected);
			put(BTN_B,R.drawable.circle_shape_button_selected);
			put(BTN_START,R.drawable.shape_selected);
			put(BTN_SELECT,R.drawable.shape_selected);
		}
	};
	
	private static final HashMap<Integer, Integer> BTN_SELECTED_MAP
		= new HashMap<Integer, Integer>() {
	{
		put(BTN_UP,R.drawable.up_btn);
		put(BTN_DOWN,R.drawable.down_btn);
		put(BTN_LEFT,R.drawable.left_btn);
		put(BTN_RIGHT,R.drawable.right_btn);
		put(BTN_X,R.drawable.circle_shape_button);
		put(BTN_Y,R.drawable.circle_shape_button);
		put(BTN_A,R.drawable.circle_shape_button);
		put(BTN_B,R.drawable.circle_shape_button);
		put(BTN_START,R.drawable.shape);
		put(BTN_SELECT,R.drawable.shape);
	}
};

	public ButtonTouchListener(Activity activity, boolean useShake) {
		// this.buttonId = id;
		this.useShake = useShake;
		this.activity = activity;

		btnUp = (ImageView) activity.findViewById(BTN_UP);
		btnDown = (ImageView) activity.findViewById(BTN_DOWN);
		btnLeft = (ImageView) activity.findViewById(BTN_LEFT);
		btnRight = (ImageView) activity.findViewById(BTN_RIGHT);
		btnX = (TextView) activity.findViewById(BTN_X);
		btnY = (TextView) activity.findViewById(BTN_Y);
		btnA = (TextView) activity.findViewById(BTN_A);
		btnB = (TextView) activity.findViewById(BTN_B);
		btnStart = (TextView) activity.findViewById(BTN_START);
		btnSelect = (TextView) activity.findViewById(BTN_SELECT);
		locationInit = false;
	}	

	private void fontColorShow(View view, boolean isDown) {
		if (view instanceof TextView) {
			TextView textView = (TextView)view;
			Resources rs = activity.getResources(); 
			if (isDown) {
				textView.setTextColor(rs.getColor(R.color.buttonNormal));
			} else {
				textView.setTextColor(rs.getColor(R.color.cate_white));
			}
		}
	}

	private void buttonBgShow(int id, View view, boolean isDown) {
		Integer drawableId = null;
		if (isDown) {
			drawableId = BTN_SELECTED_BG_MAP.get(id);
		} else {
			drawableId = BTN_SELECTED_MAP.get(id);
		}
		if (drawableId != null) {
			view.setBackgroundResource(drawableId);
		}
	}

	private void initLocation() {
		if (!locationInit) {
			btnUp.getLocationOnScreen(upLocation);
			btnDown.getLocationOnScreen(downLocation);
			btnLeft.getLocationOnScreen(leftLocation);
			btnRight.getLocationOnScreen(rightLocation);
			btnX.getLocationOnScreen(xLocation);
			btnY.getLocationOnScreen(yLocation);
			btnA.getLocationOnScreen(aLocation);
			btnB.getLocationOnScreen(bLocation);
			btnStart.getLocationOnScreen(startLocation);
			btnSelect.getLocationOnScreen(selectLocation);
			locationInit = true;
			logger.debug("right location:" + rightLocation + ",down location:"
					+ downLocation + ",rightHeight:" + btnRight.getHeight()
					+ "rightWidth:" + btnRight.getWidth());
		}
	}

	private void sendMessage(String buttonId, boolean isDown) {
		// int keyType = handleKeys.getTypeNow();
		// if (keyType == IBlueToothConst.SET_KEY_HANDLE) {
		if (isDown) {
			serverThread.sendMsg(buttonId, IBlueToothConst.toPress);
			if (useShake) {
				mVibrator = (Vibrator) activity
						.getSystemService(Service.VIBRATOR_SERVICE);
				// final long[] pattern = new long[]{100,20};
				// mVibrator.vibrate(pattern , -1);
				mVibrator.vibrate(100);
			}
		} else {
			serverThread.sendMsg(buttonId, IBlueToothConst.toRelease);
		}
		// } else {
		// if (isDown) {
		// serverThread.sendMsg(buttonId, IBlueToothConst.toPreassRelease);
		// if (useShake) {
		// mVibrator = (Vibrator)
		// activity.getSystemService(Service.VIBRATOR_SERVICE);
		// mVibrator.vibrate(100);
		// }
		// }
		// }
	}

	private void buttonProcess(float x, float y, boolean isDown) {
		logger.debug("event x:" + x + ",y:" + y);
		initLocation();

		if (upLocation[0] <= x && x <= upLocation[0] + btnUp.getWidth()
				&& upLocation[1] <= y && y <= upLocation[1] + btnUp.getHeight()) {
			sendMessage(IBlueToothConst.upBtn, isDown);
			buttonBgShow(BTN_UP,btnUp,isDown);
//			
		}
		if (downLocation[0] <= x && x <= downLocation[0] + btnDown.getWidth()
				&& downLocation[1] <= y
				&& y <= downLocation[1] + btnDown.getHeight()) {
			sendMessage(IBlueToothConst.downBtn, isDown);
			buttonBgShow(BTN_DOWN,btnDown,isDown);
		}
		if (leftLocation[0] <= x && x <= leftLocation[0] + btnLeft.getWidth()
				&& leftLocation[1] <= y
				&& y <= leftLocation[1] + btnLeft.getHeight()) {
			sendMessage(IBlueToothConst.leftBtn, isDown);
			buttonBgShow(BTN_LEFT,btnLeft,isDown);
		}
		if (rightLocation[0] <= x
				&& x <= rightLocation[0] + btnRight.getWidth()
				&& rightLocation[1] <= y
				&& y <= rightLocation[1] + btnRight.getHeight()) {
			sendMessage(IBlueToothConst.rightBtn, isDown);
			buttonBgShow(BTN_RIGHT,btnRight,isDown);
		}
		if (xLocation[0] <= x && x <= xLocation[0] + btnX.getWidth()
				&& xLocation[1] <= y && y <= xLocation[1] + btnX.getHeight()) {
			sendMessage(IBlueToothConst.selectGunBtn, isDown);
			buttonBgShow(BTN_X,btnX,isDown);
			fontColorShow(btnX,isDown);
		}
		if (yLocation[0] <= x && x <= yLocation[0] + btnY.getWidth()
				&& yLocation[1] <= y && y <= yLocation[1] + btnY.getHeight()) {
			sendMessage(IBlueToothConst.dropGunBtn, isDown);
			buttonBgShow(BTN_Y,btnY,isDown);
			fontColorShow(btnY,isDown);
		}
		if (aLocation[0] <= x && x <= aLocation[0] + btnA.getWidth()
				&& aLocation[1] <= y && y <= aLocation[1] + btnA.getHeight()) {
			sendMessage(IBlueToothConst.fireBtn, isDown);
			buttonBgShow(BTN_A,btnA,isDown);
			fontColorShow(btnA,isDown);
		}
		if (bLocation[0] <= x && x <= bLocation[0] + btnB.getWidth()
				&& bLocation[1] <= y && y <= bLocation[1] + btnB.getHeight()) {
			sendMessage(IBlueToothConst.jumpBtn, isDown);
			buttonBgShow(BTN_B,btnB,isDown);
			fontColorShow(btnB,isDown);
		}
		if (startLocation[0] <= x
				&& x <= startLocation[0] + btnStart.getWidth()
				&& startLocation[1] <= y
				&& y <= startLocation[1] + btnStart.getHeight()) {
			sendMessage(IBlueToothConst.startBtn, isDown);
			buttonBgShow(BTN_START,btnStart,isDown);
			fontColorShow(btnStart,isDown);
		}
		if (selectLocation[0] <= x
				&& x <= selectLocation[0] + btnSelect.getWidth()
				&& selectLocation[1] <= y
				&& y <= selectLocation[1] + btnSelect.getHeight()) {
			sendMessage(IBlueToothConst.selectBtn, isDown);
			buttonBgShow(BTN_SELECT,btnSelect,isDown);
			fontColorShow(btnSelect,isDown);
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int action = event.getAction();
		if (action != 2) {
			logger.info("[motion type]" + action);
		}

		float x = event.getX();
		float y = event.getY();
//		int pointerCount = event.getPointerCount();
		int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >>
				MotionEvent.ACTION_POINTER_INDEX_SHIFT;
		final int pointerId = event.getPointerId(pointerIndex);

		switch (action) {
		case MotionEvent.ACTION_DOWN: {
			buttonProcess(x, y, true);
			break;
		}
		case MotionEvent.ACTION_UP: {
			buttonProcess(x, y, false);
			break;
		}
		case MotionEvent.ACTION_POINTER_DOWN: {
			buttonProcess(event.getX(pointerId), event.getY(pointerId),true);
			break;
		}
		case MotionEvent.ACTION_POINTER_UP:
				buttonProcess(event.getX(pointerId), event.getY(pointerId), false);
			break;
		}
		return true;
	}

}

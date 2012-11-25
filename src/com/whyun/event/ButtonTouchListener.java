package com.whyun.event;

import android.app.Activity;
import android.app.Service;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.whyun.IBlueToothConst;
import com.whyun.bluetooth.R;
import com.whyun.communication.ConnectSetting;
import com.whyun.communication.ISocketThread;
import com.whyun.communication.ServerFactory;
import com.whyun.message.key.HandleKeys;
import com.whyun.util.MyLog;
//import android.widget.Toast;

public class ButtonTouchListener implements View.OnTouchListener {
//	private String buttonId;
	private static final String LOG_TAG = ButtonTouchListener.class.getName();
	private ISocketThread serverThread = ServerFactory
			.getSocketThread(ConnectSetting.getInstance().getConnectType());
	private boolean useShake;
	private Activity activity;
	private MyLog logger = MyLog.getLogger(LOG_TAG);
	private HandleKeys handleKeys = HandleKeys.getInstance();
	Vibrator mVibrator = null;
	
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
	
	public ButtonTouchListener(Activity activity, boolean useShake,String id) {
//		this.buttonId = id;
		this.useShake = useShake;
		this.activity= activity;
		
		btnUp = (ImageView)activity.findViewById(R.id.btnUp);
		btnDown = (ImageView)activity.findViewById(R.id.btnDwon);
		btnLeft = (ImageView)activity.findViewById(R.id.btnLeft);
		btnRight = (ImageView)activity.findViewById(R.id.btnRight);
		btnX = (ImageView)activity.findViewById(R.id.btnX);
		btnY = (ImageView)activity.findViewById(R.id.btnY);
		btnA = (ImageView)activity.findViewById(R.id.btnA);
		btnB = (ImageView)activity.findViewById(R.id.btnB);
		btnStart = (ImageView)activity.findViewById(R.id.btnStart);
		btnSelect = (ImageView)activity.findViewById(R.id.btnSelect);
		locationInit = false;
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
		}
	}
	
	private void sendMessage(String buttonId,boolean isDown) {
		int keyType = handleKeys.getTypeNow();
		if (keyType == IBlueToothConst.SET_KEY_HANDLE) {
			if (isDown) {
				serverThread.sendMsg(buttonId, IBlueToothConst.toPress);
				if (useShake) {
					mVibrator = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
					//final long[] pattern = new long[]{100,20};
					//mVibrator.vibrate(pattern , -1);
					mVibrator.vibrate(100);
				}
			} else {
				serverThread.sendMsg(buttonId,IBlueToothConst.toRelease);
			}
		} else {
			if (isDown) {
				serverThread.sendMsg(buttonId, IBlueToothConst.toPreassRelease);
				if (useShake) {
					mVibrator = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
					mVibrator.vibrate(100);
				}
			}
		}
	}
	
	private void buttonProcess(float x, float y, boolean isDown) {
		initLocation();
		
		if (upLocation[0] <= x && x <= upLocation[0] + btnUp.getWidth()
				&& upLocation[1] <= y && y <= upLocation[1] + btnUp.getHeight()) {
			sendMessage(IBlueToothConst.upBtn,isDown);
		}
		if (downLocation[0] <= x && x <= downLocation[0] + btnDown.getWidth()
				&& downLocation[1] <= y && y <= downLocation[1] + btnDown.getHeight()) {
			sendMessage(IBlueToothConst.downBtn,isDown);
		}
		if (leftLocation[0] <= x && x <= leftLocation[0] + btnLeft.getWidth()
				&& leftLocation[1] <= y && y <= leftLocation[1] + btnLeft.getHeight()) {
			sendMessage(IBlueToothConst.leftBtn,isDown);
		}
		if (rightLocation[0] <= x && x <= rightLocation[0] + btnRight.getWidth()
				&& rightLocation[1] <= y && y <= rightLocation[1] + btnRight.getHeight()) {
			sendMessage(IBlueToothConst.rightBtn,isDown);
		}
		if (xLocation[0] <= x && x <= xLocation[0] + btnX.getWidth()
				&& xLocation[1] <= y && y <= xLocation[1] + btnX.getHeight()) {
			sendMessage(IBlueToothConst.selectGunBtn,isDown);
		}
		if (yLocation[0] <= x && x <= yLocation[0] + btnY.getWidth()
				&& yLocation[1] <= y && y <= yLocation[1] + btnY.getHeight()) {
			sendMessage(IBlueToothConst.dropGunBtn,isDown);
		}
		if (aLocation[0] <= x && x <= aLocation[0] + btnA.getWidth()
				&& aLocation[1] <= y && y <= aLocation[1] + btnA.getHeight()) {
			sendMessage(IBlueToothConst.fireBtn,isDown);
		}
		if (bLocation[0] <= x && x <= bLocation[0] + btnB.getWidth()
				&& bLocation[1] <= y && y <= bLocation[1] + btnB.getHeight()) {
			sendMessage(IBlueToothConst.jumpBtn,isDown);
		}
		if (startLocation[0] <= x && x <= startLocation[0] + btnStart.getWidth()
				&& startLocation[1] <= y && y <= startLocation[1] + btnStart.getHeight()) {
			sendMessage(IBlueToothConst.selectBtn,isDown);
		}
		if (selectLocation[0] <= x && x <= selectLocation[0] + btnSelect.getWidth()
				&& selectLocation[1] <= y && y <= selectLocation[1] + btnSelect.getHeight()) {
			sendMessage(IBlueToothConst.startBtn,isDown);
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int action = event.getAction();
		logger.info("[motion type]" + action);
		
		float x = event.getX();
		float y = event.getY();
		int pointerCount = event.getPointerCount();
		int pointerId = 0;
		if (pointerCount > 1) {
			pointerId = (action & MotionEvent.ACTION_POINTER_ID_MASK) >>> MotionEvent.ACTION_POINTER_ID_SHIFT;
		}
		switch (action) {
		case MotionEvent.ACTION_DOWN: {
			buttonProcess(x,y,true);
			break;
		}
		case MotionEvent.ACTION_UP: {
			buttonProcess(x,y,false);
			break;
		}
		case MotionEvent.ACTION_POINTER_1_DOWN: {
			buttonProcess(event.getX(pointerId),event.getY(pointerId),true);
			break;
		}
		case MotionEvent.ACTION_POINTER_1_UP:
			buttonProcess(event.getX(pointerId),event.getY(pointerId),false);
			break;
		case MotionEvent.ACTION_POINTER_2_DOWN:
			buttonProcess(event.getX(pointerId),event.getY(pointerId),true);
			break;
		case MotionEvent.ACTION_POINTER_2_UP:
			buttonProcess(event.getX(pointerId),event.getY(pointerId),false);
			break;		
		}
		return true;
	}

}

package com.whyun.event;

import android.app.Activity;
import android.app.Service;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;

import com.whyun.IBlueToothConst;
import com.whyun.communication.ConnectSetting;
import com.whyun.communication.ISocketThread;
import com.whyun.communication.ServerFactory;
import com.whyun.message.key.HandleKeys;
import com.whyun.util.MyLog;
//import android.widget.Toast;

public class ButtonTouchListener implements View.OnTouchListener {
	private String buttonId;
	private static final String LOG_TAG = ButtonTouchListener.class.getName();
	private ISocketThread serverThread = ServerFactory
			.getSocketThread(ConnectSetting.getInstance().getConnectType());
	private boolean useShake;
	private Activity activity;
	private MyLog logger = MyLog.getLogger(LOG_TAG);
	private HandleKeys handleKeys = HandleKeys.getInstance();
	Vibrator mVibrator = null;
	
	public ButtonTouchListener(Activity activity, boolean useShake,String id) {
		this.buttonId = id;
		this.useShake = useShake;
		this.activity= activity;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int id = event.getAction();
		logger.info("[motion type]" + id);
		int keyType = handleKeys.getTypeNow();
		switch (id) {
		case MotionEvent.ACTION_DOWN: {
			logger.info("press " + buttonId);
			if (keyType == IBlueToothConst.SET_KEY_HANDLE) {
				serverThread.sendMsg(buttonId, IBlueToothConst.toPress);
			} else {
				serverThread.sendMsg(buttonId, IBlueToothConst.toPreassRelease);
			}
			if (useShake) {
				mVibrator = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
				//final long[] pattern = new long[]{100,20};
				//mVibrator.vibrate(pattern , -1);
				mVibrator.vibrate(100);
			}
			break;
		}
		case MotionEvent.ACTION_UP: {
			if (keyType == IBlueToothConst.SET_KEY_HANDLE) {
				logger.info("release " + buttonId);
				serverThread.sendMsg(buttonId,IBlueToothConst.toRelease);
			} else {
				
			}
			
			break;
		}
		case MotionEvent.ACTION_POINTER_1_DOWN: {
			break;
		}
		}
		return true;
	}

}

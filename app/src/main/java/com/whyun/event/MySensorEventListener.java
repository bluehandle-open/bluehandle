package com.whyun.event;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.whyun.IBlueToothConst;
import com.whyun.bluetooth.R;
import com.whyun.communication.ConnectSetting;
import com.whyun.communication.ISocketThread;
import com.whyun.communication.ServerFactory;
import com.whyun.util.MyLog;

public class MySensorEventListener implements SensorEventListener {
	private static final MyLog logger = MyLog.getLogger(MySensorEventListener.class);
	
	private ISocketThread serverThread = ServerFactory
			.getSocketThread(ConnectSetting.getInstance().getConnectType());
	
	/** 每50帧刷新一次屏幕 **/
	private static final int RE_DRAW_INTERVAL = 50;
	private  int timeInFrame = 50;

	/** 手机屏幕宽高 **/
	int mScreenWidth = 0;
	int mScreenHeight = 0;
	
	/** 小球的宽高*/
	private int mBallWidth;
	private int mBallHeight;

	/** 小球资源文件越界区域 **/
	private int mScreenBallWidth = 0;
	private int mScreenBallHeight = 0;

	/** 小球的坐标位置 **/
	private float mPosX = 200;
	private float mPosY = 0;

	/** 重力感应X轴 Y轴 Z轴的重力值 **/
	private float mGX = 0;
	private float mGY = 0;
	
	private int mLastX = 0;


	private ImageView img;
	private LinearLayout ballLay;

	private long lastDrawTime = 0;
	private long lastSendTime = 0;
	public MySensorEventListener(Activity activity,int timeInFrame) {
		//this.activity = activity;
		img = (ImageView) activity.findViewById(R.id.imgBall);
		ballLay = (LinearLayout) activity.findViewById(R.id.ballLay);
		ballLay.setVisibility(View.VISIBLE);

		WindowManager manage = activity.getWindowManager();
		Display display = manage.getDefaultDisplay();
		/** 得到当前屏幕宽高 **/
		mScreenWidth = display.getWidth();
		mScreenHeight = display.getHeight();
		
		int w=View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
		int h=View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
		img.measure(w, h);
		mBallWidth=img.getMeasuredWidth();
		mBallHeight=img.getMeasuredHeight();
		logger.debug("mBallWidth:"+mBallWidth+",mBallHeight:"+mBallHeight);
		
		this.timeInFrame = timeInFrame;
	}
	public MySensorEventListener(Activity activity) {
		this(activity,50);
	}
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		
	}
	
	private void sendMessage(int left) {
		
		if (left - mLastX > 0) {
			serverThread.sendMsg(IBlueToothConst.leftBtn,
					IBlueToothConst.toPreassRelease);
		} else if (left - mLastX < 0) {
			serverThread.sendMsg(IBlueToothConst.rightBtn,
					IBlueToothConst.toPreassRelease);
		} else {
			//logger.debug("not moved!");
		}

	}
	
	private void sendMessage2(float y) {
		if (y >= 1) {
			serverThread.sendMsg(IBlueToothConst.rightBtn,
					IBlueToothConst.toPreassRelease);
		} else if (y <= -1) {
			serverThread.sendMsg(IBlueToothConst.leftBtn,
					IBlueToothConst.toPreassRelease);
		}
	}
	
	private void draw(int left, int top, int right, int bottom) {
		
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ballLay
				.getLayoutParams();
		
		if (left - mLastX != 0) {
			params.setMargins(left, top, right, bottom);
//			logger.debug("left:"+left+",top:"+top+",right:"+right+",bottom:"+bottom
//			 +";mScreenBallWidth:"+mScreenBallWidth+",mScreenBallHeight:"+mScreenBallHeight);
			ballLay.setLayoutParams(params);			
		}
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		mGX = event.values[SensorManager.DATA_X];
		mGY = event.values[SensorManager.DATA_Y];
		float z = event.values[SensorManager.DATA_Z];
		
		// 这里乘以2是为了让小球移动的更快
		mPosX += mGY * 2;
		mPosY += mGX * 2;
		
		/** 得到小球越界区域 **/
		if (mScreenBallWidth == 0) {
			mScreenBallWidth = mScreenWidth - mBallWidth;
		}
		if (mScreenBallHeight == 0) {
			mScreenBallHeight = mScreenHeight - mBallHeight;
		}
		// 检测小球是否超出边界
		if (mPosX < 0) {
			mPosX = 0;
		} else if (mPosX > mScreenBallWidth) {
			mPosX = mScreenBallWidth;
		}
		if (mPosY < 0) {
			mPosY = 0;
		} else if (mPosY > mScreenBallHeight) {
			mPosY = mScreenBallHeight;
		}
		long timeNow = System.currentTimeMillis();
		if (timeNow - lastDrawTime > RE_DRAW_INTERVAL) {
			int left = (int) mPosX;
			int top = (int) mPosY;
			int right = (int) (mScreenWidth - mPosX - mBallWidth);
			int bottom = (int) (mScreenHeight - mPosY - mBallHeight);
			
			draw(left,top,right,bottom);
			if (timeNow - lastSendTime > timeInFrame) {
				logger.debug("x:"+mGX+",y:"+mGY+",z:"+z);
//				sendMessage(left);
				sendMessage2(mGY);
				lastSendTime = timeNow;
			} else {
				//logger.debug("has not reached next send time!");
			}
			lastDrawTime = timeNow;	
			mLastX = left;
		}
	}	
}

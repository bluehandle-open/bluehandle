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

import com.whyun.bluetooth.R;

public class MySensorEventListener implements SensorEventListener {
	//private Activity activity;
	/** 每50帧刷新一次屏幕 **/
	private  int timeInFrame = 50;

	/** 手机屏幕宽高 **/
	int mScreenWidth = 0;
	int mScreenHeight = 0;

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

	private long lastDrawTime;
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
		
		this.timeInFrame = timeInFrame;
	}
	public MySensorEventListener(Activity activity) {
		this(activity,50);
	}
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		
	}
	
	private void draw() {
		
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ballLay
				.getLayoutParams();
		int left = (int) mPosX;
		int top = (int) mPosY;
		int right = (int) (mScreenWidth - mPosX - img.getWidth());
		int bottom = (int) (mScreenHeight - mPosY - img.getHeight());
		if (left - mLastX != 0) {
			params.setMargins(left, top, right, bottom);
//			 System.out.println("left:"+left+",top:"+top+",right:"+right+",bottom:"+bottom
//			 +";mScreenBallWidth:"+mScreenBallWidth+",mScreenBallHeight:"+mScreenBallHeight);
			ballLay.setLayoutParams(params);
			if (left - mLastX > 0) {
				System.out.println("move right");
			} else {
				System.out.println("move left");
			}
			mLastX = left;
		}
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		mGX = event.values[SensorManager.DATA_X];
		mGY = event.values[SensorManager.DATA_Y];

		// 这里乘以2是为了让小球移动的更快
		mPosX += mGY * 2;
		mPosY += mGX * 2;
		
		/** 得到小球越界区域 **/
		if (mScreenBallWidth == 0) {
			mScreenBallWidth = mScreenWidth - img.getWidth();
		}
		if (mScreenBallHeight == 0) {
			mScreenBallHeight = mScreenHeight - img.getHeight();
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
		if (timeNow - lastDrawTime > timeInFrame) {
			lastDrawTime = timeNow;
			draw();
		}
	}	
}

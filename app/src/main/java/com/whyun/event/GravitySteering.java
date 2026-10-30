package com.whyun.event;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.SystemClock;

import com.whyun.IBlueToothConst;
import com.whyun.communication.ConnectionManager;

/**
 * 重力感应转向：横屏左右倾斜手机时，按灵敏度对应的间隔发送左/右方向键。
 * 判定逻辑沿用旧版 MySensorEventListener：Y 轴加速度超过 ±1 视为倾斜。
 */
public class GravitySteering implements SensorEventListener {

	private static final float TILT_THRESHOLD = 1f;
	private static final long MIN_INTERVAL_MS = 50;

	private final ConnectionManager connection = ConnectionManager.get();
	private final long intervalMs;
	private long lastSend;

	/** @param sensitivity 0-100，越大发送越频繁 */
	public GravitySteering(int sensitivity) {
		int s = Math.max(0, Math.min(100, sensitivity));
		this.intervalMs = Math.max(MIN_INTERVAL_MS, (100 - s) * 10L);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		long now = SystemClock.uptimeMillis();
		if (now - lastSend < intervalMs) {
			return;
		}
		float y = event.values[1];
		if (y >= TILT_THRESHOLD) {
			connection.sendButton(IBlueToothConst.rightBtn, IBlueToothConst.toPreassRelease);
			lastSend = now;
		} else if (y <= -TILT_THRESHOLD) {
			connection.sendButton(IBlueToothConst.leftBtn, IBlueToothConst.toPreassRelease);
			lastSend = now;
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}
}

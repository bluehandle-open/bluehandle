package net.youmi.android;

import com.whyun.bluetooth.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.FrameLayout;

public class AdYMActivity extends Activity {
	// 注意 请在程序入口点使用static代码块初始化AdManager.init

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ad);
		this.setTitle("请点击广告支持蓝牙手柄");
		// 应用Id 应用密码 广告请求间隔(s) 测试模式
		AdManager.init(AdYMActivity.this,"0874deea1c82005e", "8eef5acdfa4fdb3a", 30, false);
		// 初始化广告视图
		AdView adView = new AdView(this);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.FILL_PARENT,
				FrameLayout.LayoutParams.WRAP_CONTENT);

		// 设置广告出现的位置(悬浮于屏幕右下角)
		params.gravity = Gravity.BOTTOM | Gravity.RIGHT;
		// 将广告视图加入Activity 中
		addContentView(adView, params);
	}
}

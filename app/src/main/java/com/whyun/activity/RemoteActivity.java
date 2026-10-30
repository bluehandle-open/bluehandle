package com.whyun.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.whyun.activity.component.ActivityUtil;
import com.whyun.bluetooth.R;
import com.whyun.event.KeyButtonBinder;
import com.whyun.message.key.Vk;

/**
 * 电脑遥控器：外观模仿电视遥控器，每个按键映射为电脑快捷键，按键上标注对应快捷键。
 */
public class RemoteActivity extends BaseControlActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_remote);
		bindBack(R.id.btn_back);

		KeyButtonBinder keys = new KeyButtonBinder(this);

		View lock = setupKey(R.id.key_lock, R.drawable.bh_ic_lock, "锁屏", "Win+L");
		lock.setBackgroundResource(R.drawable.bh_bg_action_soft);
		tintKey(lock, R.color.bh_action);
		keys.action(lock, new KeyButtonBinder.Action() {
			@Override
			public void run() {
				connection.lockScreen();// Win+L 模拟按键会被系统拦截，由电脑端调用锁屏接口
			}
		});
		keys.tap(setupKey(R.id.key_switch, R.drawable.bh_ic_switch_window, "切换窗口", "Alt+Tab"), Vk.ALT, Vk.TAB);
		keys.tap(setupKey(R.id.key_mute, R.drawable.bh_ic_mute, "静音", "媒体键"), Vk.VOLUME_MUTE);

		keys.hold(findViewById(R.id.key_up), Vk.UP);
		keys.hold(findViewById(R.id.key_down), Vk.DOWN);
		keys.hold(findViewById(R.id.key_left), Vk.LEFT);
		keys.hold(findViewById(R.id.key_right), Vk.RIGHT);
		keys.hold(findViewById(R.id.key_ok), Vk.ENTER);

		keys.tap(setupKey(R.id.key_esc, R.drawable.bh_ic_back, "返回", "Esc"), Vk.ESC);
		keys.tap(setupKey(R.id.key_desktop, R.drawable.bh_ic_desktop, "桌面", "Win+D"), Vk.WIN, Vk.KEY_D);
		keys.tap(setupKey(R.id.key_menu, R.drawable.bh_ic_menu, "菜单", "右键菜单"), Vk.APPS);

		keys.hold(findViewById(R.id.key_vol_up), Vk.VOLUME_UP);
		keys.hold(findViewById(R.id.key_vol_down), Vk.VOLUME_DOWN);
		keys.hold(findViewById(R.id.key_page_up), Vk.PAGE_UP);
		keys.hold(findViewById(R.id.key_page_down), Vk.PAGE_DOWN);

		keys.tap(findViewById(R.id.key_prev), Vk.MEDIA_PREV);
		keys.tap(findViewById(R.id.key_play), Vk.MEDIA_PLAY_PAUSE);
		keys.tap(findViewById(R.id.key_next), Vk.MEDIA_NEXT);

		findViewById(R.id.key_voice).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ActivityUtil.toastShow(RemoteActivity.this, "语音输入即将上线");
			}
		});
		findViewById(R.id.key_numpad).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(RemoteActivity.this, NumpadActivity.class));
			}
		});
	}

	private void tintKey(View key, int colorRes) {
		int c = ContextCompat.getColor(this, colorRes);
		((ImageView) key.findViewById(R.id.key_icon)).setColorFilter(c);
		((TextView) key.findViewById(R.id.key_label)).setTextColor(c);
		((TextView) key.findViewById(R.id.key_hint)).setTextColor(ContextCompat.getColor(this, R.color.bh_action_muted));
	}
}

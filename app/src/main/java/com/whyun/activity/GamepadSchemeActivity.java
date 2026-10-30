package com.whyun.activity;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.whyun.IBlueToothConst;
import com.whyun.activity.component.ActivityUtil;
import com.whyun.bluetooth.R;
import com.whyun.communication.ConnectionManager;
import com.whyun.message.bean.KeyInfo;
import com.whyun.message.data.KeyTableOperator;
import com.whyun.message.key.GamepadButtons;
import com.whyun.message.key.HandleKeys;
import com.whyun.util.AppPrefs;

/**
 * 游戏手柄 · 选择键位方案。内置默认键位 + 用户的自定义方案，选中后打开手柄。
 * 选中项保存在偏好 {@link IMyPreference#KEY_TYPE}：负数为默认键位，否则为方案 id。
 */
public class GamepadSchemeActivity extends AppCompatActivity implements IBlueToothConst {

	private static final String DEFAULT_NAME = "默认键位";

	private final ArrayList<KeyInfo> schemes = new ArrayList<KeyInfo>();
	private int selectedId = SET_KEY_HANDLE;
	private LinearLayout listView;
	private View defaultRow;
	private TextView btnOpen;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gamepad_scheme);

		((TextView) findViewById(R.id.header_title)).setText("选择键位方案");
		findViewById(R.id.header_back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		listView = (LinearLayout) findViewById(R.id.scheme_list);
		defaultRow = findViewById(R.id.scheme_default);
		((TextView) defaultRow.findViewById(R.id.scheme_name)).setText(DEFAULT_NAME);
		((TextView) defaultRow.findViewById(R.id.scheme_summary)).setText(GamepadButtons.defaultSummary());
		defaultRow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				select(SET_KEY_HANDLE);
			}
		});

		findViewById(R.id.btn_new_scheme).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(GamepadSchemeActivity.this, KeyEditActivity.class));
			}
		});

		btnOpen = (TextView) findViewById(R.id.btn_open_gamepad);
		btnOpen.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openGamepad();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		loadSchemes();// 从编辑页返回后刷新
	}

	private void loadSchemes() {
		KeyTableOperator op = new KeyTableOperator(this);
		ArrayList<KeyInfo> list = op.getAllList();
		op.readFinish();
		schemes.clear();
		if (list != null) {
			schemes.addAll(list);
		}

		int saved = AppPrefs.get(this, IMyPreference.KEY_TYPE, SET_KEY_HANDLE);
		selectedId = find(saved) != null ? saved : SET_KEY_HANDLE;// 方案已删除时回落默认

		listView.removeAllViews();
		LayoutInflater inflater = LayoutInflater.from(this);
		for (final KeyInfo info : schemes) {
			View row = inflater.inflate(R.layout.bh_item_scheme, listView, false);
			((TextView) row.findViewById(R.id.scheme_name)).setText(info.getKeyname());
			((TextView) row.findViewById(R.id.scheme_summary)).setText(GamepadButtons.summary(info));
			View edit = row.findViewById(R.id.scheme_edit);
			edit.setVisibility(View.VISIBLE);
			edit.setContentDescription("编辑方案 " + info.getKeyname());
			edit.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(GamepadSchemeActivity.this, KeyEditActivity.class);
					intent.putExtra(KeyEditActivity.EXTRA_SCHEME, info);
					startActivity(intent);
				}
			});
			row.setTag(info.getKeyId());
			row.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					select(info.getKeyId());
				}
			});
			row.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					confirmDelete(info);
					return true;
				}
			});
			listView.addView(row);
		}
		((TextView) findViewById(R.id.scheme_count)).setText(schemes.isEmpty() ? "还没有" : schemes.size() + " 套");
		render();
	}

	private KeyInfo find(int id) {
		if (id < 0) {
			return new KeyInfo();// 默认键位总是存在，返回非空占位
		}
		for (KeyInfo info : schemes) {
			if (info.getKeyId() == id) {
				return info;
			}
		}
		return null;
	}

	private void select(int id) {
		selectedId = id;
		AppPrefs.put(this, IMyPreference.KEY_TYPE, id);
		render();
	}

	private void render() {
		boolean defaultOn = selectedId < 0;
		defaultRow.setSelected(defaultOn);
		defaultRow.findViewById(R.id.scheme_current).setVisibility(defaultOn ? View.VISIBLE : View.GONE);
		for (int i = 0; i < listView.getChildCount(); i++) {
			View row = listView.getChildAt(i);
			boolean on = ((Integer) row.getTag()) == selectedId;
			row.setSelected(on);
			row.findViewById(R.id.scheme_current).setVisibility(on ? View.VISIBLE : View.GONE);
		}
		btnOpen.setText("用「" + selectedName() + "」打开手柄");
	}

	private String selectedName() {
		if (selectedId < 0) {
			return DEFAULT_NAME;
		}
		KeyInfo info = find(selectedId);
		return info == null ? DEFAULT_NAME : info.getKeyname();
	}

	private void confirmDelete(final KeyInfo info) {
		new AlertDialog.Builder(this)
		.setTitle("删除方案")
		.setMessage("确定删除「" + info.getKeyname() + "」？删除后无法恢复。")
		.setNegativeButton("取消", null)
		.setPositiveButton("删除", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				KeyTableOperator op = new KeyTableOperator(GamepadSchemeActivity.this);
				op.deleteKey(info.getKeyId());
				op.readFinish();
				loadSchemes();
			}
		}).show();
	}

	private void openGamepad() {
		if (!ConnectionManager.get().isConnected()) {
			ActivityUtil.toastShow(this, "请先在首页连接电脑");
			return;
		}
		applySelectedScheme(this);
		Intent intent = new Intent(this, GamepadActivity.class);
		// 从手柄界面进入本页时，回到原有的手柄界面而不是再叠一个
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
		finish();
	}

	/**
	 * 把偏好里选中的方案写入 {@link HandleKeys}，返回方案名。方案已删除时回落默认键位。
	 */
	static String applySelectedScheme(android.content.Context context) {
		int id = AppPrefs.get(context, IMyPreference.KEY_TYPE, SET_KEY_HANDLE);
		if (id >= 0) {
			KeyTableOperator op = new KeyTableOperator(context);
			KeyInfo info = op.getKeySettingInfo(id);
			op.readFinish();
			if (info != null) {
				HandleKeys.getInstance().setKeys(info, null);
				return info.getKeyname();
			}
			AppPrefs.put(context, IMyPreference.KEY_TYPE, SET_KEY_HANDLE);
		}
		HandleKeys.getInstance().setKeys(null, SET_KEY_HANDLE, null);
		return DEFAULT_NAME;
	}
}

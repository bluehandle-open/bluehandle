package com.whyun.activity;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.SpeechRecognizer;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.slider.Slider;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.whyun.IBlueToothConst;
import com.whyun.activity.component.ActivityUtil;
import com.whyun.activity.component.PcDownloadHelper;
import com.whyun.activity.component.SegmentedControl;
import com.whyun.bluetooth.R;
import com.whyun.communication.ConnectionManager;
import com.whyun.message.bean.KeyInfo;
import com.whyun.message.data.KeyTableOperator;
import com.whyun.util.AppPrefs;
import com.whyun.util.UnitTransformTool;

/**
 * 设置页。对应设计稿 Settings 画板。所有项直接读写 {@link AppPrefs}，改动立即生效。
 */
public class SettingsActivity extends AppCompatActivity implements IMyPreference {

	static String[] languageNames(android.content.Context context) {
		return new String[] { context.getString(R.string.ui_mandarin), context.getString(R.string.ui_cantonese), "English" };
	}
	static final String[] LANG_CODES = { "zh-CN", "yue-CN", "en-US" };
	private static final int MINUTES_STEP = 5;
	private static final int MINUTES_MIN = 5;
	private static final int MINUTES_MAX = 120;

	private TextView tvMinutes;
	private View rowSchemes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		((TextView) findViewById(R.id.header_title)).setText(getString(R.string.ui_settings));
		findViewById(R.id.header_back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		setupAppearance();
		setupConnection();
		setupFeedback();
		setupGamepad();
		setupPpt();
		setupVoice();
		setupAbout();
	}

	@Override
	protected void onResume() {
		super.onResume();
		refreshSchemeCount();// 从键位管理返回后数量可能变化
	}

	// ---------------------------------------------------------------- 外观

	private static final String[] THEMES = { THEME_SYSTEM, THEME_LIGHT, THEME_DARK };

	private void setupAppearance() {
		// 10 以上系统有深色模式，“跟随系统”名副其实，不用解释
		findViewById(R.id.tv_theme_hint).setVisibility(
				Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ? View.GONE : View.VISIBLE);
		SegmentedControl seg = new SegmentedControl((LinearLayout) findViewById(R.id.seg_theme));
		seg.select(indexOf(THEMES, AppPrefs.get(this, THEME_MODE, THEME_SYSTEM)));
		seg.setOnSelectedListener(new SegmentedControl.OnSelectedListener() {
			@Override
			public void onSelected(int index) {
				AppPrefs.put(SettingsActivity.this, THEME_MODE, THEMES[index]);
				AppPrefs.applyTheme(SettingsActivity.this);// 打开的页面会按新主题重建
			}
		});
	}

	// ---------------------------------------------------------------- 连接

	private void setupConnection() {
		SegmentedControl seg = new SegmentedControl((LinearLayout) findViewById(R.id.seg_default_type));
		seg.select(AppPrefs.connectType(this) == IBlueToothConst.BLUETOOTH_TYPE ? 1 : 0);
		seg.setOnSelectedListener(new SegmentedControl.OnSelectedListener() {
			@Override
			public void onSelected(int index) {
				int type = index == 0 ? IBlueToothConst.WIFI_TYPE : IBlueToothConst.BLUETOOTH_TYPE;
				AppPrefs.setConnectType(SettingsActivity.this, type);
				ConnectionManager cm = ConnectionManager.get();
				if (cm.getState() != ConnectionManager.State.IDLE && cm.getConnectType() != type) {
					ActivityUtil.toastShow(SettingsActivity.this, getString(R.string.ui_applies_to_the_next_connection));
				}
			}
		});

		final View rowPort = findViewById(R.id.row_port);
		valueRow(rowPort, getString(R.string.ui_wi_fi_port), null, String.valueOf(AppPrefs.wifiPort(this)), true);
		rowPort.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				editPort(rowPort);
			}
		});

		View rowNetwork = findViewById(R.id.row_network);
		valueRow(rowNetwork, getString(R.string.ui_system_network_settings), getString(R.string.ui_open_wi_fi_and_bluetooth_settings), null, true);
		rowNetwork.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
			}
		});
	}

	private void editPort(final View row) {
		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		input.setText(String.valueOf(AppPrefs.wifiPort(this)));
		input.setSelectAllOnFocus(true);
		FrameLayout box = new FrameLayout(this);
		int pad = UnitTransformTool.dip2px(this, 20);
		box.setPadding(pad, pad / 2, pad, 0);
		box.addView(input, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));
		new AlertDialog.Builder(this)
		.setTitle(getString(R.string.ui_wi_fi_port))
		.setMessage(getString(R.string.ui_use_a_port_from_1024_65535_and_enter_the_same_port_on_your_comput))
		.setView(box)
		.setNegativeButton(getString(R.string.ui_cancel), null)
		.setPositiveButton(getString(R.string.ui_ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				int port;
				try {
					port = Integer.parseInt(input.getText().toString().trim());
				} catch (NumberFormatException e) {
					port = -1;
				}
				if (port < 1024 || port > 65535) {
					ActivityUtil.toastShow(SettingsActivity.this, getString(R.string.ui_port_must_be_between_1024_and_65535));
					return;
				}
				AppPrefs.put(SettingsActivity.this, WIFI_PORT, port);
				((TextView) row.findViewById(R.id.row_value)).setText(String.valueOf(port));
			}
		}).show();
	}

	// ---------------------------------------------------------------- 反馈与手柄

	private void setupFeedback() {
		switchRow(R.id.row_shake, getString(R.string.ui_key_vibration), getString(R.string.ui_vibrate_briefly_when_a_key_is_pressed), USE_SHAKE, false, null);
		switchRow(R.id.row_keep_screen, getString(R.string.ui_keep_screen_on), getString(R.string.ui_keep_the_screen_awake_while_connected), KEEP_SCREEN_ON, true, null);
	}

	private void setupGamepad() {
		final View block = findViewById(R.id.block_sensitivity);
		final TextView value = (TextView) findViewById(R.id.sensitivity_value);
		final Slider slider = (Slider) findViewById(R.id.slider_sensitivity);
		int current = Math.max(0, Math.min(100, AppPrefs.get(this, SENSITIVITY_VOLUME, DEFAULT_SENSITIVITY)));
		slider.setValue(current);
		value.setText(String.valueOf(current));
		slider.addOnChangeListener(new Slider.OnChangeListener() {
			@Override
			public void onValueChange(@NonNull Slider s, float v, boolean fromUser) {
				value.setText(String.valueOf((int) v));
				if (fromUser) {
					AppPrefs.put(SettingsActivity.this, SENSITIVITY_VOLUME, (int) v);
				}
			}
		});

		switchRow(R.id.row_gravity, getString(R.string.ui_tilt_steering), getString(R.string.ui_tilt_your_phone_left_or_right_to_send_arrow_keys), ENABLE_GRAVITY, false,
				new OnSwitch() {
					@Override
					public void onChanged(boolean on) {
						// 灵敏度只在开启重力感应时有意义，关闭时置灰
						slider.setEnabled(on);
						block.setAlpha(on ? 1f : 0.45f);
					}
				});
	}

	// ---------------------------------------------------------------- PPT

	private void setupPpt() {
		switchRow(R.id.row_ppt_volume, getString(R.string.ui_volume_key_navigation), getString(R.string.ui_volume_up_previous_slide_volume_down_next_slide), PPT_VOLUME_KEYS, true, null);
		switchRow(R.id.row_ppt_remind, getString(R.string.ui_remind_with_2_minutes_left), getString(R.string.ui_one_long_vibration_without_sound), PPT_REMIND, true, null);

		tvMinutes = (TextView) findViewById(R.id.tv_minutes);
		renderMinutes();
		findViewById(R.id.btn_minutes_minus).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				changeMinutes(-MINUTES_STEP);
			}
		});
		findViewById(R.id.btn_minutes_plus).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				changeMinutes(MINUTES_STEP);
			}
		});
	}

	private void changeMinutes(int delta) {
		int m = AppPrefs.get(this, PPT_TARGET_MINUTES, DEFAULT_PPT_MINUTES) + delta;
		m = Math.max(MINUTES_MIN, Math.min(MINUTES_MAX, m));
		AppPrefs.put(this, PPT_TARGET_MINUTES, m);
		renderMinutes();
	}

	private void renderMinutes() {
		tvMinutes.setText(getString(R.string.duration_minutes, AppPrefs.get(this, PPT_TARGET_MINUTES, DEFAULT_PPT_MINUTES)));
	}

	// ---------------------------------------------------------------- 语音

	private void setupVoice() {
		final View rowLang = findViewById(R.id.row_voice_lang);
		valueRow(rowLang, getString(R.string.ui_recognition_language), null, langName(this, AppPrefs.get(this, VOICE_LANGUAGE, LANG_CODES[0])), true);
		rowLang.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int checked = indexOf(LANG_CODES, AppPrefs.get(SettingsActivity.this, VOICE_LANGUAGE, LANG_CODES[0]));
				new AlertDialog.Builder(SettingsActivity.this)
				.setTitle(getString(R.string.ui_recognition_language))
				.setSingleChoiceItems(languageNames(SettingsActivity.this), checked, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						AppPrefs.put(SettingsActivity.this, VOICE_LANGUAGE, LANG_CODES[which]);
						((TextView) rowLang.findViewById(R.id.row_value)).setText(languageNames(SettingsActivity.this)[which]);
						dialog.dismiss();
					}
				}).show();
			}
		});

		final boolean builtinAvailable = SpeechRecognizer.isRecognitionAvailable(this);
		final View rowEngine = findViewById(R.id.row_voice_engine);
		valueRow(rowEngine, getString(R.string.ui_recognition_engine), builtinAvailable ? null : getString(R.string.ui_no_built_in_recognition_service_is_available),
				engineName(currentEngine(builtinAvailable)), true);
		rowEngine.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!builtinAvailable) {
					ActivityUtil.toastShow(SettingsActivity.this, getString(R.string.ui_no_built_in_recognition_service_use_the_system_keyboard_instead));
					return;
				}
				final String[] codes = { VOICE_ENGINE_IME, VOICE_ENGINE_BUILTIN };
				final String[] names = { engineName(codes[0]), engineName(codes[1]) };
				new AlertDialog.Builder(SettingsActivity.this)
				.setTitle(getString(R.string.ui_recognition_engine))
				.setSingleChoiceItems(names, indexOf(codes, currentEngine(true)),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								AppPrefs.put(SettingsActivity.this, VOICE_ENGINE, codes[which]);
								((TextView) rowEngine.findViewById(R.id.row_value)).setText(names[which]);
								dialog.dismiss();
							}
						}).show();
			}
		});

		switchRow(R.id.row_voice_hold_send, getString(R.string.ui_hold_to_send_setting), getString(R.string.ui_for_built_in_recognition_turn_off_to_review_the_result_before_sen),
				VOICE_HOLD_SEND, true, null);

		SegmentedControl seg = new SegmentedControl((LinearLayout) findViewById(R.id.seg_voice_send));
		seg.select(AppPrefs.get(this, VOICE_SEND_ENTER, true) ? 1 : 0);
		seg.setOnSelectedListener(new SegmentedControl.OnSelectedListener() {
			@Override
			public void onSelected(int index) {
				AppPrefs.put(SettingsActivity.this, VOICE_SEND_ENTER, index == 1);
			}
		});
	}

	private String currentEngine(boolean builtinAvailable) {
		String e = AppPrefs.get(this, VOICE_ENGINE, VOICE_ENGINE_BUILTIN);
		return builtinAvailable ? e : VOICE_ENGINE_IME;
	}

	private String engineName(String code) {
		return VOICE_ENGINE_BUILTIN.equals(code) ? getString(R.string.ui_built_in_recognition) : getString(R.string.ui_system_keyboard);
	}

	static String langName(android.content.Context context, String code) {
		int i = indexOf(LANG_CODES, code);
		return languageNames(context)[i];
	}

	private static int indexOf(String[] arr, String v) {
		for (int i = 0; i < arr.length; i++) {
			if (arr[i].equals(v)) {
				return i;
			}
		}
		return 0;
	}

	// ---------------------------------------------------------------- 键位与关于

	private void setupAbout() {
		rowSchemes = findViewById(R.id.row_schemes);
		valueRow(rowSchemes, getString(R.string.ui_manage_key_schemes), null, "", true);
		rowSchemes.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(SettingsActivity.this, GamepadSchemeActivity.class));
			}
		});

		View rowHelp = findViewById(R.id.row_help);
		valueRow(rowHelp, getString(R.string.ui_help), null, null, true);
		rowHelp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(SettingsActivity.this, GuideActivity.class));
			}
		});

		View rowDownload = findViewById(R.id.row_download);
		valueRow(rowDownload, getString(R.string.ui_get_desktop_app), null, null, true);
		rowDownload.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				PcDownloadHelper.show(SettingsActivity.this);
			}
		});

		final String version = versionName();
		View rowAbout = findViewById(R.id.row_about);
		valueRow(rowAbout, getString(R.string.ui_about_blue_handle), null, version, true);
		rowAbout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(SettingsActivity.this)
				.setTitle(getString(R.string.ui_about_blue_handle))
				.setMessage(getString(R.string.about_details, version))
				.setPositiveButton(getString(R.string.ui_ok), null)
				.show();
			}
		});
	}

	private void refreshSchemeCount() {
		KeyTableOperator op = new KeyTableOperator(this);
		ArrayList<KeyInfo> list = op.getAllList();
		op.readFinish();
		int n = list == null ? 0 : list.size();
		((TextView) rowSchemes.findViewById(R.id.row_value)).setText(n == 0 ? getString(R.string.ui_default_keys_only) : getString(R.string.custom_scheme_count, n));
	}

	private String versionName() {
		try {
			return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (PackageManager.NameNotFoundException e) {
			return "";
		}
	}

	// ---------------------------------------------------------------- 行组件

	private interface OnSwitch {
		void onChanged(boolean on);
	}

	/** 初始化一个开关行：整行可点，状态写入偏好 key */
	private void switchRow(int rowId, String title, String subtitle, final String key, boolean def,
			final OnSwitch extra) {
		View row = findViewById(rowId);
		((TextView) row.findViewById(R.id.row_title)).setText(title);
		TextView sub = (TextView) row.findViewById(R.id.row_subtitle);
		if (subtitle != null) {
			sub.setText(subtitle);
			sub.setVisibility(View.VISIBLE);
		}
		final SwitchMaterial sw = (SwitchMaterial) row.findViewById(R.id.row_switch);
		sw.setChecked(AppPrefs.get(this, key, def));
		row.setContentDescription(title);
		row.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean on = !sw.isChecked();
				sw.setChecked(on);
				AppPrefs.put(SettingsActivity.this, key, on);
				if (extra != null) {
					extra.onChanged(on);
				}
			}
		});
		if (extra != null) {
			extra.onChanged(sw.isChecked());
		}
	}

	private static void valueRow(View row, String title, String subtitle, String value, boolean chevron) {
		((TextView) row.findViewById(R.id.row_title)).setText(title);
		TextView sub = (TextView) row.findViewById(R.id.row_subtitle);
		if (subtitle != null) {
			sub.setText(subtitle);
			sub.setVisibility(View.VISIBLE);
		}
		TextView v = (TextView) row.findViewById(R.id.row_value);
		v.setText(value == null ? "" : value);
		row.findViewById(R.id.row_chevron).setVisibility(chevron ? View.VISIBLE : View.GONE);
	}
}

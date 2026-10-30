package com.whyun.activity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.whyun.activity.component.ActivityUtil;
import com.whyun.activity.component.KeyPicker;
import com.whyun.bluetooth.R;
import com.whyun.message.bean.KeyInfo;
import com.whyun.message.data.KeyTableOperator;
import com.whyun.message.key.GamepadButtons;
import com.whyun.message.key.KeyMap;
import com.whyun.util.UnitTransformTool;

/**
 * 新建或编辑一套手柄键位方案。每个按钮最多三个键的组合，数据库结构与旧版相同。
 */
public class KeyEditActivity extends AppCompatActivity {
	public static final String EXTRA_SCHEME = "scheme";
	private static final int SLOTS = 3;

	/** 字段前缀 -> 三个键名（KeyMap 的键），空位为 null */
	private final Map<String, String[]> combos = new HashMap<String, String[]>();
	private KeyInfo editing;
	private int selectedButton = 0;
	private int selectedSlot = 0;
	private boolean dirty = false;

	private EditText etName;
	private TextView tvEditTitle;
	private final TextView[] slotViews = new TextView[SLOTS];
	private KeyPicker picker;
	private LinearLayout mappingGrid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_key_edit);

		editing = (KeyInfo) getIntent().getSerializableExtra(EXTRA_SCHEME);
		for (GamepadButtons.Button b : GamepadButtons.ALL) {
			String[] keys = new String[SLOTS];
			for (int i = 0; i < SLOTS; i++) {
				// 新建时以默认键位为起点，用户只需改动需要的按钮
				String k = editing != null ? editing.getKey(b.field, i + 1) : (i == 0 ? b.defaultKeys : null);
				keys[i] = (k == null || k.length() == 0) ? null : k;
			}
			combos.put(b.field, keys);
		}

		((TextView) findViewById(R.id.header_title)).setText(editing == null ? "新建方案" : "编辑方案");
		findViewById(R.id.header_back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		addSaveButton();

		etName = (EditText) findViewById(R.id.et_scheme_name);
		if (editing != null) {
			etName.setText(editing.getKeyname());
		}
		etName.addTextChangedListener(new TextWatcher() {
			public void beforeTextChanged(CharSequence s, int a, int b, int c) {
			}

			public void onTextChanged(CharSequence s, int a, int b, int c) {
				dirty = true;
			}

			public void afterTextChanged(Editable s) {
			}
		});

		tvEditTitle = (TextView) findViewById(R.id.tv_edit_title);
		int[] slotIds = { R.id.slot_1, R.id.slot_2, R.id.slot_3 };
		for (int i = 0; i < SLOTS; i++) {
			final int slot = i;
			slotViews[i] = (TextView) findViewById(slotIds[i]);
			slotViews[i].setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					selectedSlot = slot;
					renderEditor();
				}
			});
		}
		findViewById(R.id.btn_clear).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String[] keys = currentCombo();
				for (int i = 0; i < SLOTS; i++) {
					keys[i] = null;
				}
				selectedSlot = 0;
				dirty = true;
				renderAll();
			}
		});

		picker = new KeyPicker((LinearLayout) findViewById(R.id.key_categories),
				(LinearLayout) findViewById(R.id.key_board), new KeyPicker.OnKeyPicked() {
					@Override
					public void onPicked(String keyName) {
						pickKey(keyName);
					}
				});

		mappingGrid = (LinearLayout) findViewById(R.id.mapping_grid);
		buildMappingGrid();
		renderAll();
	}

	private void addSaveButton() {
		FrameLayout right = (FrameLayout) findViewById(R.id.header_right);
		ViewGroup.LayoutParams lp = right.getLayoutParams();
		lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
		right.setLayoutParams(lp);
		TextView save = new TextView(this);
		save.setText("保存");
		save.setTextColor(ContextCompat.getColor(this, R.color.bh_on_primary));
		save.setTextSize(15);
		save.setGravity(Gravity.CENTER);
		save.setBackgroundResource(R.drawable.bh_bg_primary);
		int pad = UnitTransformTool.dip2px(this, 16);
		save.setPadding(pad, 0, pad, 0);
		save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				save();
			}
		});
		right.addView(save, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				UnitTransformTool.dip2px(this, 40), Gravity.CENTER_VERTICAL));
	}

	private String[] currentCombo() {
		return combos.get(GamepadButtons.ALL.get(selectedButton).field);
	}

	private void pickKey(String keyName) {
		String[] keys = currentCombo();
		for (int i = 0; i < SLOTS; i++) {
			if (i != selectedSlot && keyName.equals(keys[i])) {
				ActivityUtil.toastShow(this, "组合里已有这个键");
				return;
			}
		}
		keys[selectedSlot] = keyName;
		if (selectedSlot < SLOTS - 1) {
			selectedSlot++;// 自动跳到下一个空位，方便连续输入组合键
		}
		dirty = true;
		renderAll();
	}

	private void renderAll() {
		renderEditor();
		renderMappingGrid();
	}

	private void renderEditor() {
		GamepadButtons.Button b = GamepadButtons.ALL.get(selectedButton);
		tvEditTitle.setText("“" + b.label + "” 键 · 最多 3 键组合");
		String[] keys = currentCombo();
		Set<String> used = new HashSet<String>();
		for (int i = 0; i < SLOTS; i++) {
			boolean empty = keys[i] == null;
			slotViews[i].setText(empty ? "空" : GamepadButtons.display(keys[i]));
			slotViews[i].setTextColor(ContextCompat.getColor(this, empty ? R.color.bh_text_secondary : R.color.bh_text));
			slotViews[i].setSelected(i == selectedSlot);
			slotViews[i].setContentDescription("第" + (i + 1) + "个键 " + slotViews[i].getText());
			if (!empty) {
				used.add(keys[i]);
			}
		}
		picker.setHighlighted(used);
	}

	private void buildMappingGrid() {
		int gap = UnitTransformTool.dip2px(this, 8);
		LinearLayout row = null;
		for (int i = 0; i < GamepadButtons.ALL.size(); i++) {
			if (i % 2 == 0) {
				row = new LinearLayout(this);
				row.setOrientation(LinearLayout.HORIZONTAL);
				LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT, UnitTransformTool.dip2px(this, 54));
				rp.topMargin = gap;
				mappingGrid.addView(row, rp);
			}
			final int index = i;
			LinearLayout cell = new LinearLayout(this);
			cell.setOrientation(LinearLayout.HORIZONTAL);
			cell.setGravity(Gravity.CENTER_VERTICAL);
			int pad = UnitTransformTool.dip2px(this, 14);
			cell.setPadding(pad, 0, pad, 0);
			cell.setBackgroundResource(R.drawable.bh_bg_card_selectable);
			cell.setClickable(true);

			TextView label = new TextView(this);
			label.setText(GamepadButtons.ALL.get(i).label);
			label.setTextSize(14);
			label.setTextColor(ContextCompat.getColor(this, R.color.bh_text_secondary));
			cell.addView(label, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

			TextView value = new TextView(this);
			value.setTextSize(15);
			value.setSingleLine(true);
			value.setTypeface(value.getTypeface(), android.graphics.Typeface.BOLD);
			value.setTextColor(ContextCompat.getColor(this, R.color.bh_text));
			cell.addView(value);

			cell.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					selectedButton = index;
					String[] keys = currentCombo();
					selectedSlot = 0;
					while (selectedSlot < SLOTS - 1 && keys[selectedSlot] != null) {
						selectedSlot++;// 定位到第一个空位
					}
					renderAll();
				}
			});
			LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
			if (i % 2 == 1) {
				cp.setMarginStart(gap);
			}
			row.addView(cell, cp);
		}
	}

	private void renderMappingGrid() {
		for (int i = 0; i < GamepadButtons.ALL.size(); i++) {
			LinearLayout row = (LinearLayout) mappingGrid.getChildAt(i / 2);
			LinearLayout cell = (LinearLayout) row.getChildAt(i % 2);
			GamepadButtons.Button b = GamepadButtons.ALL.get(i);
			String[] k = combos.get(b.field);
			String c = GamepadButtons.combo(k[0], k[1], k[2]);
			((TextView) cell.getChildAt(1)).setText(c.length() == 0 ? "未设置" : c);
			cell.setSelected(i == selectedButton);
			cell.setContentDescription(b.label + " 映射为 " + (c.length() == 0 ? "未设置" : c));
		}
	}

	private void save() {
		String name = etName.getText().toString().trim();
		if (name.length() == 0) {
			ActivityUtil.toastShow(this, "请填写方案名称");
			etName.requestFocus();
			return;
		}
		ContentValues values = new ContentValues();
		values.put(KeyTableOperator.KEY_TABLE_FIELD_NAME, name);
		for (GamepadButtons.Button b : GamepadButtons.ALL) {
			String[] keys = combos.get(b.field);
			int slot = 1;
			for (String k : keys) {// 压缩空位，组合键按顺序靠前存放
				if (k != null) {
					values.put(b.field + "key" + slot++, KeyMap.KEY_MAP.get(k));
				}
			}
			while (slot <= SLOTS) {
				values.putNull(b.field + "key" + slot++);
			}
		}
		KeyTableOperator op = new KeyTableOperator(this);
		boolean ok = editing == null ? op.addKey(name, values) : op.updateKey(editing.getKeyId(), values);
		op.readFinish();
		if (!ok) {
			ActivityUtil.toastShow(this, "保存失败，请稍后重试");
			return;
		}
		ActivityUtil.toastShow(this, "已保存");
		dirty = false;
		finish();
	}

	@Override
	public void onBackPressed() {
		if (!dirty) {
			super.onBackPressed();
			return;
		}
		new AlertDialog.Builder(this)
		.setMessage("修改尚未保存，确定放弃？")
		.setNegativeButton("继续编辑", null)
		.setPositiveButton("放弃", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dirty = false;
				finish();
			}
		}).show();
	}
}

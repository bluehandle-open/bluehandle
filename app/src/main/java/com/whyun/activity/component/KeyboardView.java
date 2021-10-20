package com.whyun.activity.component;

import android.app.Activity;
import android.content.ContentValues;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.whyun.bluetooth.R;
import com.whyun.message.bean.KeyInfo;
import com.whyun.message.key.KeyMap;
import com.whyun.util.MyLog;

public class KeyboardView {
	private TextView firstInput;
	private TextView secondInput;
	private TextView thirdInput;

	private LinearLayout keyboard;
	private static final MyLog logger = MyLog.getLogger(KeyboardView.class);
	private TextView selectedView;
	private LinearLayout view;
	private Activity activity;
	private String fieldNamePrefix;
	private Button closePanel;
	private Button clearInput;
	private KeyInfo info;
	
	public KeyboardView(Activity activity, String fieldNamePrefix, KeyInfo info) {
		this.activity = activity;
		this.fieldNamePrefix = fieldNamePrefix;
		this.info = info;
		init();
	}
	
	public KeyboardView(Activity activity, String fieldNamePrefix) {
		this(activity, fieldNamePrefix, null);
	}
	
	public ContentValues getContentValues(ContentValues values) {
		ContentValues newValues;
		if (values != null) {
			newValues = values;
		} else {
			newValues = new ContentValues();
		}

		newValues.put(fieldNamePrefix + "key1",
				KeyMap.KEY_MAP.get(firstInput.getText().toString()));
		newValues.put(fieldNamePrefix + "key2",
				KeyMap.KEY_MAP.get(secondInput.getText().toString()));
		newValues.put(fieldNamePrefix + "key3",
				KeyMap.KEY_MAP.get(thirdInput.getText().toString()));
		
		return newValues;
	}
	
	public View getView() {
		return view;
	}

    private void init() {
    	view = (LinearLayout)LayoutInflater.from(activity).inflate(R.layout.keyboard_area,null);
        firstInput = (TextView)view.findViewById(R.id.firstKey);
        secondInput = (TextView)view.findViewById(R.id.secondKey);
        thirdInput = (TextView)view.findViewById(R.id.thirdKey);
        firstInput.setOnClickListener(new ClickEvent());
        secondInput.setOnClickListener(new ClickEvent());
        thirdInput.setOnClickListener(new ClickEvent());
        
        if (info != null) {
        	firstInput.setText(info.getKey(fieldNamePrefix,1));
        	secondInput.setText(info.getKey(fieldNamePrefix,2));
        	thirdInput.setText(info.getKey(fieldNamePrefix,3));
        }
        
        keyboard = (LinearLayout)view.findViewById(R.id.keyboard);
        keyboard.setVisibility(View.GONE);
        
        closePanel = (Button)view.findViewById(R.id.closePanel);
        closePanel.setOnClickListener(new ClickEvent());
        
        clearInput = (Button)view.findViewById(R.id.clearInput);
        clearInput.setOnClickListener(new ClickEvent());

        for (int i = 0,boardLen=keyboard.getChildCount(); i < boardLen; i++) {
            View child = keyboard.getChildAt(i);
            if (child instanceof TableLayout) {
            	TableLayout table = (TableLayout)child;
            	for (int j=0,len=table.getChildCount();j<len;j++) {
            		View row = table.getChildAt(j);
            		if (row instanceof TableRow) {
            			TableRow tableRow = (TableRow)row;
            			for (int k=0,tdLen=tableRow.getChildCount();k<tdLen;k++) {
            				View button = tableRow.getChildAt(k);
            				if (button instanceof MyButton) {
            					MyButton myButton = (MyButton)button;
            					myButton.setOnClickListener(new ClickEvent());            					
            				}
            			}
            		}
            	}
            }
        }
    }

    private void showSelected(int i) {
    	Resources resources = activity.getResources();
    	Drawable selectedBg = resources.getDrawable(R.drawable.circle_bg);
    	Drawable noneSelectedBg = resources.getDrawable(R.drawable.circle_bg2);
    	switch (i) {
    	case 1:
    		firstInput.setBackground(selectedBg);
    		secondInput.setBackground(noneSelectedBg);
    		thirdInput.setBackground(noneSelectedBg);
    		selectedView = firstInput;
    		break;
    	case 2:
    		firstInput.setBackground(noneSelectedBg);
    		secondInput.setBackground(selectedBg);
    		thirdInput.setBackground(noneSelectedBg);
    		selectedView = secondInput;
    		break;
    	case 3:
    		firstInput.setBackground(noneSelectedBg);
    		secondInput.setBackground(noneSelectedBg);
    		thirdInput.setBackground(selectedBg);
    		selectedView = thirdInput;
    		break;
    	}
    }
    
    class ClickEvent implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			keyboard.setVisibility(View.VISIBLE);
			if (v == firstInput) {				
				showSelected(1);
			} else if (v == secondInput) {
				showSelected(2);
			} else if (v == thirdInput) {
				showSelected(3);
			} else if (v == closePanel) {
				keyboard.setVisibility(View.GONE);
			} else if (v == clearInput) { 
				if (selectedView != null) {
					selectedView.setText("");
				} else {
					logger.warn("没有选中的textview");
				}
			} else if (v instanceof MyButton) {
				MyButton button = (MyButton)v;
				if (selectedView != null) {
					selectedView.setText(button.getText());
				} else {
					logger.warn("没有选中的textview");
				}
			} else {
				Toast.makeText(activity, "没有点击", Toast.LENGTH_SHORT).show();
			}
			
		}
    	
    }
}

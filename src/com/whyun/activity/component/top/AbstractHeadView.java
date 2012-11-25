package com.whyun.activity.component.top;

import android.app.Activity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.whyun.bluetooth.R;

public abstract class AbstractHeadView {
	private View view;
	private Activity activity;
	private String titleString;
	private int buttonId;
	private OnClickListener clickListener;
	private boolean hideReturn;
	
	protected static final int BUTTON_QUE_DING = 1;
	protected static final int BUTTON_TIAN_JIA = 2;
	protected static final int BUTTON_CUSTOM_JIA = 3;
	protected static final int BUTTON_QIE_HUAN = 4;
	
	protected AbstractHeadView(Activity activity, String title,
			int buttonId, OnClickListener clickListener, boolean hideReturn) {
		this.activity = activity;
		this.titleString = title;
		this.buttonId = buttonId;
		this.clickListener = clickListener;
		this.hideReturn = hideReturn;
		
		init();
	}
	
	protected AbstractHeadView(Activity activity, String title,
			int buttonId, OnClickListener clickListener) {
		this(activity, title, buttonId, clickListener, false);
	}
	
	private void init() {
		view = LayoutInflater.from(activity).inflate(R.layout.headview,/**读取headview.xml来构建布局*/
				null);
		WindowManager manage = activity.getWindowManager();
		Display display = manage.getDefaultDisplay();
		int screenWidth = display.getWidth();
		LayoutParams para = new LayoutParams(screenWidth,LayoutParams.WRAP_CONTENT);
		para.width = screenWidth;
		view.setLayoutParams(para);
		
		ImageButton backButton = (ImageButton) view.findViewById(R.id.backbutton);
		if (!hideReturn) {
			backButton.setVisibility(View.VISIBLE);
			backButton.setOnClickListener(new OnClickListener() {
	
				@Override
				public void onClick(View v) {
					switch (v.getId()) {
					case R.id.backbutton:
						activity.finish();
						break;
					}
				}
			});/** 返回按钮监听事件 */	
		} else {
			backButton.setVisibility(View.GONE);
		}
		
		TextView title = (TextView) view.findViewById(R.id.title);
		title.setText(titleString);
		ImageButton button = null;
		
		switch (buttonId) {
		case BUTTON_QUE_DING:
			button = (ImageButton)view.findViewById(R.id.quedingbutton);
			break;
		case BUTTON_TIAN_JIA:
			button = (ImageButton)view.findViewById(R.id.tianjiabutton);
			break;
		case BUTTON_CUSTOM_JIA:
			button = (ImageButton)view.findViewById(R.id.custombutton);
			break;
		case BUTTON_QIE_HUAN:
			button = (ImageButton)view.findViewById(R.id.qiehuanbutton);
			break;
		}
		if (button != null) {
			button.setVisibility(View.VISIBLE);
			button.setOnClickListener(clickListener);
		} else {
			//TODO
		}
	}
	
	public View getView() {
		return view;
	}
	
	public void setTitle(CharSequence text) {
		if (view != null) {
			TextView textView = (TextView)view.findViewById(R.id.title);
			textView.setText(text);
		}
	}
}

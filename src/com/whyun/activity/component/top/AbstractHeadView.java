package com.whyun.activity.component.top;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.whyun.bluetooth.R;

public abstract class AbstractHeadView {
	private View view;
	private Activity activity;
	private String titleString;
	private int buttonId;
	private OnClickListener clickListener;
	
	protected static final int BUTTON_QUE_DING = 1;
	protected static final int BUTTON_TIAN_JIA = 2;
	
	protected AbstractHeadView(Activity activity, String title,
			int buttonId, OnClickListener clickListener) {
		this.activity = activity;
		this.titleString = title;
		this.buttonId = buttonId;
		this.clickListener = clickListener;
		
		init();
	}
	
	private void init() {
		view = LayoutInflater.from(activity).inflate(R.layout.headview,/**读取headview.xml来构建布局*/
				null);
		ImageButton backButton = (ImageButton) view.findViewById(R.id.backbutton);
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
}

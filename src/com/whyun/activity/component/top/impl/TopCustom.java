package com.whyun.activity.component.top.impl;

import android.app.Activity;
import android.view.View.OnClickListener;

import com.whyun.activity.component.top.AbstractHeadView;

public class TopCustom   extends AbstractHeadView {

	public TopCustom(Activity activity, String title,
			OnClickListener clickListener) {
		super(activity, title, BUTTON_CUSTOM_JIA, clickListener, true);
	}	
}

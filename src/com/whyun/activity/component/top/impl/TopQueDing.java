package com.whyun.activity.component.top.impl;

import android.app.Activity;
import android.view.View.OnClickListener;

import com.whyun.activity.component.top.AbstractHeadView;

public class TopQueDing  extends AbstractHeadView {

	public TopQueDing(Activity activity, String title,
			OnClickListener clickListener) {
		super(activity, title, BUTTON_QUE_DING, clickListener,true);
	}

}

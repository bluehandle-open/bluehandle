package com.whyun.activity.component.top.impl;

import android.app.Activity;
import android.view.View.OnClickListener;

import com.whyun.activity.component.top.AbstractHeadView;

public class TopTianJia extends AbstractHeadView {
	private static final String TITLE = "按键管理";

	public TopTianJia(Activity activity,
			OnClickListener clickListener) {
		super(activity, TITLE, BUTTON_TIAN_JIA, clickListener);
	}

}

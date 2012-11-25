package com.whyun.activity.component.top.impl;

import android.app.Activity;
import android.view.View.OnClickListener;

import com.whyun.activity.component.top.AbstractHeadView;

public class TopQieHuan  extends AbstractHeadView {

	public TopQieHuan(Activity activity, String title,
			OnClickListener clickListener) {
		super(activity, title, BUTTON_QIE_HUAN, clickListener);
	}
}

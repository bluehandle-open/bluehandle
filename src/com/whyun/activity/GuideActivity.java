package com.whyun.activity;

import com.whyun.activity.component.scroll.MyScrollLayout;
import com.whyun.activity.component.scroll.OnViewChangeListener;
import com.whyun.bluetooth.R;
import com.whyun.util.MyLog;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class GuideActivity extends Activity implements OnViewChangeListener, OnClickListener{
    /** Called when the activity is first created. */
	private MyLog logger = MyLog.getLogger(GuideActivity.class);

	private MyScrollLayout mScrollLayout;
	
	private ImageView[] mImageViews;
	
	private int mViewCount;
	
	private int mCurSel;
	private ImageView btnBeginUse;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course);
        btnBeginUse = (ImageView)findViewById(R.id.btnBeginUse);
        init();
    }
    
    

    private void init()
    {
    	mScrollLayout = (MyScrollLayout) findViewById(R.id.ScrollLayout);
    	mScrollLayout.SetOnViewChangeListener(this);
    	logger.debug("this:"+this);
    	
    	LinearLayout linearLayout = (LinearLayout) findViewById(R.id.llayout);
    	
    	mViewCount = mScrollLayout.getChildCount();
    	mImageViews = new ImageView[mViewCount];
    	
    	for(int i = 0; i < mViewCount; i++)
    	{
    		mImageViews[i] = (ImageView) linearLayout.getChildAt(i);
    		mImageViews[i].setEnabled(true);
    		mImageViews[i].setOnClickListener(this);
    		mImageViews[i].setTag(i);
    	}
    	
    	mCurSel = 0;
    	mImageViews[mCurSel].setEnabled(false);
    	
    	
    	btnBeginUse.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				GuideActivity.this.finish();
			}
			
    	});
    }


    private void setCurPoint(int index)
    {
    	logger.debug("index now:"+index);
    	if (index == mViewCount - 1) {
    		btnBeginUse.setVisibility(View.VISIBLE);
    	} else {
    		btnBeginUse.setVisibility(View.GONE);
    	}
    	if (index < 0 || index > mViewCount - 1 || mCurSel == index)
    	{
    		return ;
    	}
    	
    	mImageViews[mCurSel].setEnabled(true);
    	mImageViews[index].setEnabled(false);
    	
    	mCurSel = index;
    }

    @Override
	public void OnViewChange(int pos) {
    	logger.debug("index pos now:"+pos);
    	setCurPoint(pos);
	}


	@Override
	public void onClick(View v) {
		int pos = (Integer)(v.getTag());
		setCurPoint(pos);
		mScrollLayout.snapToScreen(pos);
	}
}
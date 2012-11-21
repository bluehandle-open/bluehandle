package com.whyun.activity.component;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.whyun.activity.IMyPreference;
import com.whyun.bluetooth.R;
import com.whyun.util.MyLog;

public class SeekBarPreference extends DialogPreference implements
		OnSeekBarChangeListener {
	private SeekBar seekBar;
	private TextView textView;
	private SharedPreferences settings;
	private MyLog logger = MyLog.getLogger(SeekBarPreference.class);

	public SeekBarPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		settings = PreferenceManager.getDefaultSharedPreferences(context);
	}

	@Override
	protected void onBindDialogView(View view) {
		super.onBindDialogView(view);
		seekBar = (SeekBar) view.findViewById(R.id.seekBar1);
		textView = (TextView) view.findViewById(R.id.textView1);
		int progress = settings.getInt(IMyPreference.SENSITIVITY_VOLUME, 50);
		textView.setText(progress + "/100");
		seekBar.setProgress(progress);
		logger.debug("old progress:"+progress);
		
		seekBar.setOnSeekBarChangeListener(this);
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		if (positiveResult) {
			Editor editor = settings.edit();//获取编辑器
			editor.putInt(IMyPreference.SENSITIVITY_VOLUME, seekBar.getProgress());
			editor.commit();
		} else {
			//logger.debug( "You click negative button");
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		textView.setText(progress + "/100");
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}

}

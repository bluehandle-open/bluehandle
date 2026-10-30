package com.whyun.activity.component;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.whyun.bluetooth.R;

/**
 * 设计稿里的分段切换：容器使用 bh_bg_segment 背景，子项为 TextView。
 * 选中项提亮背景与文字，其余透明。
 */
public class SegmentedControl {

	public interface OnSelectedListener {
		void onSelected(int index);
	}

	private final ViewGroup container;
	private int selected = -1;
	private OnSelectedListener listener;

	public SegmentedControl(ViewGroup container) {
		this.container = container;
		for (int i = 0; i < container.getChildCount(); i++) {
			final int index = i;
			View child = container.getChildAt(i);
			child.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (index != selected) {
						select(index);
						if (listener != null) {
							listener.onSelected(index);
						}
					}
				}
			});
		}
	}

	public void setOnSelectedListener(OnSelectedListener listener) {
		this.listener = listener;
	}

	/** 只改变显示，不触发监听 */
	public void select(int index) {
		selected = index;
		for (int i = 0; i < container.getChildCount(); i++) {
			View child = container.getChildAt(i);
			boolean on = i == index;
			child.setBackgroundResource(on ? R.drawable.bh_bg_segment_selected : android.R.color.transparent);
			child.setSelected(on);
			if (child instanceof TextView) {
				((TextView) child).setTextColor(ContextCompat.getColor(child.getContext(),
						on ? R.color.bh_text : R.color.bh_text_secondary));
			}
		}
	}

	public int getSelected() {
		return selected;
	}

	public void setEnabled(boolean enabled) {
		container.setAlpha(enabled ? 1f : 0.5f);
		for (int i = 0; i < container.getChildCount(); i++) {
			container.getChildAt(i).setEnabled(enabled);
		}
	}
}

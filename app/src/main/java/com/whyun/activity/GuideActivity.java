package com.whyun.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.whyun.IBlueToothConst;
import com.whyun.bluetooth.R;

/**
 * 新用户引导，四页：欢迎、装电脑端、连接、选模式。对应 design/guide-preview.html。
 * 首次打开由首页自动显示，之后可从首页“使用帮助”和设置页进入。
 */
public class GuideActivity extends AppCompatActivity {
	private static final int[] PAGES = {
			R.layout.guide_page_welcome,
			R.layout.guide_page_pc,
			R.layout.guide_page_connect,
			R.layout.guide_page_modes,
	};
	private static final int PAGE_PC = 1;
	private static final int PAGE_CONNECT = 2;

	private ViewPager pager;
	private TextView tvStep;
	private TextView btnSkip;
	private TextView btnShare;
	private TextView btnNext;
	private LinearLayout dots;
	private TextView tvIp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);
		pager = (ViewPager) findViewById(R.id.pager);
		tvStep = (TextView) findViewById(R.id.tv_step);
		btnSkip = (TextView) findViewById(R.id.btn_skip);
		btnShare = (TextView) findViewById(R.id.btn_share_url);
		btnNext = (TextView) findViewById(R.id.btn_next);
		dots = (LinearLayout) findViewById(R.id.dots);

		pager.setOffscreenPageLimit(PAGES.length);
		pager.setAdapter(new PagerAdapter() {
			@Override
			public int getCount() {
				return PAGES.length;
			}

			@Override
			public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
				return view == object;
			}

			@NonNull
			@Override
			public Object instantiateItem(@NonNull ViewGroup container, int position) {
				View page = LayoutInflater.from(GuideActivity.this).inflate(PAGES[position], container, false);
				if (position == PAGE_CONNECT) {
					tvIp = (TextView) page.findViewById(R.id.tv_guide_ip);
					renderIp();
				}
				container.addView(page);
				return page;
			}

			@Override
			public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
				container.removeView((View) object);
			}
		});
		pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				render(position);
			}
		});

		for (int i = 0; i < PAGES.length; i++) {
			dots.addView(new View(this));
		}
		btnSkip.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		btnNext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int next = pager.getCurrentItem() + 1;
				if (next < PAGES.length) {
					pager.setCurrentItem(next);
				} else {
					finish();
				}
			}
		});
		btnShare.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				shareDownloadUrl();
			}
		});
		render(0);
	}

	@Override
	protected void onResume() {
		super.onResume();
		renderIp();// 用户可能离开去连了 Wi‑Fi
	}

	@Override
	public void onBackPressed() {
		int cur = pager.getCurrentItem();
		if (cur > 0) {
			pager.setCurrentItem(cur - 1);
		} else {
			super.onBackPressed();
		}
	}

	private void render(int position) {
		boolean last = position == PAGES.length - 1;
		boolean step = position == PAGE_PC || position == PAGE_CONNECT;
		tvStep.setText(step ? "第 " + position + " 步 / 共 2 步" : "");
		btnSkip.setVisibility(last ? View.INVISIBLE : View.VISIBLE);
		btnShare.setVisibility(position == PAGE_PC ? View.VISIBLE : View.GONE);
		btnNext.setText(last ? "开始使用" : position == PAGE_PC ? "已经装好，下一步" : "下一步");
		btnNext.setBackgroundResource(last ? R.drawable.bh_bg_action : R.drawable.bh_bg_primary);
		btnNext.setTextColor(getResources().getColor(last ? R.color.bh_on_action : R.color.bh_on_primary));

		float d = getResources().getDisplayMetrics().density;
		for (int i = 0; i < dots.getChildCount(); i++) {
			View dot = dots.getChildAt(i);
			boolean on = i == position;
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					Math.round((on ? 20 : 6) * d), LinearLayout.LayoutParams.MATCH_PARENT);
			lp.leftMargin = lp.rightMargin = Math.round(3 * d);
			dot.setLayoutParams(lp);
			dot.setBackgroundResource(on ? R.drawable.bh_guide_dot_on : R.drawable.bh_guide_dot);
		}
	}

	private void renderIp() {
		if (tvIp == null) {
			return;
		}
		String ip = MainActivity.localWifiIp(this);
		tvIp.setText(ip == null ? "请先连接 Wi‑Fi" : ip);
	}

	/** 手机上装不了电脑端：把下载地址分享出去，在电脑上打开 */
	private void shareDownloadUrl() {
		Intent send = new Intent(Intent.ACTION_SEND);
		send.setType("text/plain");
		send.putExtra(Intent.EXTRA_TEXT, "蓝色手柄电脑端下载地址：" + IBlueToothConst.URL_DOWNLOAD);
		startActivity(Intent.createChooser(send, "把下载地址发到电脑"));
	}
}

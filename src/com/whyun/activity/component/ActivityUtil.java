package com.whyun.activity.component;

import net.youmi.android.appoffers.YoumiOffersManager;
import net.youmi.android.appoffers.YoumiPointsManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.whyun.bluetooth.R;

public final class ActivityUtil {
	private ActivityUtil() {
		
	}
	
	public static void exit(Activity activity) {
		exit(activity,null);
	}
	
	public static void exit(Activity activity,String title) {
		final Activity activityNow = activity;
		new AlertDialog.Builder(activity)
		.setMessage(title == null ? "" : title)
		.setTitle("确定退出程序？")
		.setNegativeButton("取消",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
					}
				})
		.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						activityNow.finish();
					}
				}).show();    
	}
	
	public static void toastShow(Activity activity,String txt) {
		Toast.makeText(activity, txt, 1000).show();
	}
	
	public static void customToastShow(Activity activity,String txt) {
		View toastRoot = activity.getLayoutInflater().inflate(R.layout.my_toast, null);
		Toast toast=new Toast(activity.getApplicationContext());
		toast.setView(toastRoot);
		toast.setGravity(Gravity.BOTTOM, 0, 10);
		toast.setDuration(Toast.LENGTH_SHORT);
		TextView tv=(TextView)toastRoot.findViewById(R.id.TextViewInfo);
		tv.setText(txt);
		toast.show();
	}
	
	public static void notEnoughCoins(final Activity activity, int coinsNow) {
		
		new AlertDialog.Builder(activity)
		.setMessage("你当前的积分不足(当前积分："+coinsNow+")，点击确定将打开积分墙.")
		.setTitle("积分不足")
		.setNegativeButton("取消",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
					}
				})
		.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						YoumiOffersManager.showOffers(activity,
								YoumiOffersManager.TYPE_REWARD_OFFERS);
					}
				}).show();
	}
	
	public static void confirmSpend(final Activity activity,final int neededCoins) {
		new AlertDialog.Builder(activity)
		.setMessage("当前操作要消费" + neededCoins + "个积分，"
				+ "点击确定将会将其扣除.")
		.setTitle("去除广告")
		.setNegativeButton("取消",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
					}
				})
		.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						YoumiPointsManager.spendPoints(activity,
								neededCoins);
						
					}
				}).show();
	}
}

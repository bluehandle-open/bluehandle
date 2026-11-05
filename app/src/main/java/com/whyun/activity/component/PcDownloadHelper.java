package com.whyun.activity.component;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.whyun.IBlueToothConst;

/**
 * "获取电脑端"对话框。电脑端是 Windows 程序，手机上下载了也用不了，
 * 所以只把下载网址交给用户：复制，或分享到能在电脑上打开的地方。首页、设置页和引导页共用。
 */
public final class PcDownloadHelper {

	private PcDownloadHelper() {
	}

	public static void show(final Activity activity) {
		new AlertDialog.Builder(activity)
		.setTitle("获取电脑端")
		.setMessage("在电脑浏览器中打开下面的网址，下载并运行 bluehandle.exe（Windows 10 / 11）：\n\n"
				+ IBlueToothConst.URL_PC_SITE_DISPLAY)
		.setNegativeButton("复制网址", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				copyUrl(activity);
			}
		})
		.setPositiveButton("分享到电脑", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				share(activity);
			}
		})
		.show();
	}

	public static void copyUrl(Activity context) {
		ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		if (cm == null) {
			return;
		}
		cm.setPrimaryClip(ClipData.newPlainText("蓝色手柄电脑端", IBlueToothConst.URL_PC_SITE));
		ActivityUtil.toastShow(context, "已复制：" + IBlueToothConst.URL_PC_SITE_DISPLAY);
	}

	/** 通过系统分享发到微信、邮件等，在电脑上打开 */
	public static void share(Activity context) {
		Intent send = new Intent(Intent.ACTION_SEND);
		send.setType("text/plain");
		send.putExtra(Intent.EXTRA_TEXT, "蓝色手柄电脑端下载地址：" + IBlueToothConst.URL_PC_SITE);
		context.startActivity(Intent.createChooser(send, "把下载地址发到电脑"));
	}
}

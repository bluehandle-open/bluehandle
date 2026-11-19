package com.whyun.activity.component;

import com.whyun.bluetooth.R;
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
		.setTitle(activity.getString(R.string.ui_get_desktop_app_title))
		.setMessage(activity.getString(R.string.desktop_download_instructions, IBlueToothConst.URL_PC_SITE_DISPLAY))
		.setNegativeButton(activity.getString(R.string.ui_copy_link), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				copyUrl(activity);
			}
		})
		.setPositiveButton(activity.getString(R.string.ui_share_to_computer), new DialogInterface.OnClickListener() {
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
		cm.setPrimaryClip(ClipData.newPlainText(context.getString(R.string.ui_blue_handle_desktop_app), IBlueToothConst.URL_PC_SITE));
		ActivityUtil.toastShow(context, context.getString(R.string.copied_url, IBlueToothConst.URL_PC_SITE_DISPLAY));
	}

	/** 通过系统分享发到微信、邮件等，在电脑上打开 */
	public static void share(Activity context) {
		Intent send = new Intent(Intent.ACTION_SEND);
		send.setType("text/plain");
		send.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.desktop_download_share, IBlueToothConst.URL_PC_SITE));
		context.startActivity(Intent.createChooser(send, context.getString(R.string.ui_send_the_download_link_to_your_computer)));
	}
}

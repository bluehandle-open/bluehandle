package com.whyun.activity.component;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.whyun.IBlueToothConst;
import com.whyun.service.DownloadService;
import com.whyun.util.FileUtil;
import com.whyun.util.MyLog;

/**
 * "下载电脑端"对话框：直接下载，或只保存下载地址。首页和设置页共用。
 * 绑定在 ApplicationContext 上，界面关闭后下载继续。
 */
public final class PcDownloadHelper {
	private static final MyLog logger = MyLog.getLogger(PcDownloadHelper.class);

	private static ServiceConnection connection;

	private PcDownloadHelper() {
	}

	public static void show(final Activity activity) {
		new AlertDialog.Builder(activity)
		.setTitle("下载电脑端")
		.setMessage("安装包约 10MB，确定现在下载吗？\n点击“只保存地址”会在本应用的外部存储目录生成 "
				+ IBlueToothConst.SAVE_URL_FILE + "，可以复制到电脑上下载。")
		.setNegativeButton("只保存地址", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				try {
					File saved = FileUtil.saveFile(activity, IBlueToothConst.SAVE_URL_FILE,
							IBlueToothConst.URL_DOWNLOAD);
					ActivityUtil.toastShow(activity, "已保存下载地址：" + saved.getAbsolutePath());
				} catch (IOException e) {
					logger.warn("保存下载地址失败", e);
					ActivityUtil.toastShow(activity, "保存下载地址失败");
				}
			}
		})
		.setPositiveButton("下载", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				startDownload(activity.getApplicationContext());
			}
		})
		.show();
	}

	private static synchronized void startDownload(Context appContext) {
		if (connection != null) {
			appContext.unbindService(connection);// 上一次的绑定，重新开始
		}
		connection = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				((DownloadService.DownLoadServiceBinder) service).getService()
						.startDownLoad(IBlueToothConst.URL_DOWNLOAD);
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
			}
		};
		appContext.bindService(new Intent(appContext, DownloadService.class), connection,
				Context.BIND_AUTO_CREATE);
	}
}

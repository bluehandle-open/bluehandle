package com.whyun.event;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.whyun.IConst;
import com.whyun.activity.ConfigActivity;
import com.whyun.activity.IBottom;
import com.whyun.activity.component.ActivityUtil;
import com.whyun.util.MyLog;

public class BottomClickEvent implements View.OnClickListener,IBottom {
	private Activity activity;
	private MyLog logger = MyLog.getLogger(BottomClickEvent.class);
	
	public BottomClickEvent(Activity activity) {
		this.activity = activity;
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		logger.debug("id:"+id);
		switch(id) {
		
		case DONATE_ID:
			ActivityUtil.toastShow(activity, "请多多使用积分购买自定义按键，您的支持是软件得以进步的最大支持");
			break;
		case SHARE_ID:
			Intent intent=new Intent(Intent.ACTION_SEND);
	        intent.setType("text/plain");
	        intent.putExtra(Intent.EXTRA_SUBJECT, IConst.SHARE_TITLE);
	        intent.putExtra(Intent.EXTRA_TEXT, IConst.SHARE_CONTENT);
	        activity.startActivity(Intent.createChooser(intent, "分享到"));
			break;
		case PC_DOWNLOAD_ID:
//			if (downLoadService != null) {
//				downLoadService.startDownLoad(IBlueToothConst.URL_DOWNLOAD);
//			} else {
//				logger.error("downLoadService is null.");
//			}
			break;
		case CONFIG_ID:
			Intent intent1=new Intent();
			intent1.setClass(activity,ConfigActivity.class);
			activity.startActivity(intent1);
			break;
		case EXIT_ID:
			ActivityUtil.exit(activity);
			break;
		}
	}

}

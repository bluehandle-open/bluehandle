package com.whyun.activity;

import net.youmi.android.appoffers.YoumiPointsManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.whyun.IBlueToothConst;
import com.whyun.activity.component.ActivityUtil;
import com.whyun.activity.component.top.impl.TopTianJia;
import com.whyun.bluetooth.R;
import com.whyun.message.bean.KeyInfo;
import com.whyun.message.data.KeyTableOperator;
import com.whyun.util.MyLog;

public class KeyListActivity extends Activity {
	
	private ListView keyList;
	private static final MyLog logger = MyLog.getLogger(KeyListActivity.class);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.keylist);
		
	}	
	
	@Override
	protected void onResume() {
		initUI();
		super.onResume();
	}

	private void initUI() {		
		LinearLayout head = (LinearLayout) findViewById(R.id.head);
		head.addView(new TopTianJia(this,new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				int coinsNow = YoumiPointsManager.queryPoints(KeyListActivity.this);
				if (coinsNow < IBlueToothConst.ADD_USER_KEY_COINS && !IBlueToothConst.COIN_DEBUG) {
					ActivityUtil.notEnoughCoins(KeyListActivity.this, coinsNow);
				} else {
					Intent intent = new Intent();
					intent.setClass(KeyListActivity.this, KeyAddActivity.class);
					startActivity(intent);
				}				
			}
			
		}).getView());
		keyList = (ListView)findViewById(R.id.keysList);
		keyList.setAdapter(new KeyListAdapter(this));		
	}
	
	private static class KeyViewHolder {
		TextView keysName;
		TextView keys;
		Button modify;
		Button delete;
	}
	
	private  class KeyListAdapter extends BaseAdapter {
		
		private LayoutInflater inflater = null;
		private Activity activity;
		private KeyTableOperator tableOperator;
		
		public KeyListAdapter(Activity activity) {
			this.activity = activity;
			this.tableOperator = new KeyTableOperator(activity);
		}

		@Override
		public int getCount() {
			Cursor cursor = tableOperator.getAll();
			int count = cursor.getCount();
			cursor.close();
			return count;
		}

		@Override
		public Object getItem(int index) {					
			return null;
		}

		@Override
		public long getItemId(int position) {
			Cursor cursor = tableOperator.getAll();
			cursor.moveToPosition(position);
			long id = (long)cursor.getInt(cursor.getColumnIndex("id"));
			cursor.close();
			return id;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			KeyViewHolder viewHolder;
			if (convertView == null) {
				if (inflater == null) {
					inflater = (LayoutInflater)activity.getSystemService(LAYOUT_INFLATER_SERVICE);
				}
				convertView = inflater.inflate(R.layout.item_key, null);
				viewHolder = new KeyViewHolder();
				viewHolder.keysName = (TextView)convertView.findViewById(R.id.keysName);
				viewHolder.keys = (TextView)convertView.findViewById(R.id.keys);
				viewHolder.modify = (Button)convertView.findViewById(R.id.modify);
				viewHolder.delete = (Button)convertView.findViewById(R.id.delete);
				
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (KeyViewHolder)convertView.getTag();
			}
			Cursor cursor = tableOperator.getAll();
			cursor.moveToPosition(position);

			final KeyInfo info = KeyTableOperator.cursor2Info(cursor);
			cursor.close();
			
			String keys = "";
			keys += "up:" + info.getUp1() + "+" + info.getUp2() + "+" + info.getUp3();
			keys += "...";
			logger.debug("keys:" + keys);
			viewHolder.keys.setText(keys);
			viewHolder.keysName.setText(info.getKeyname());
			viewHolder.modify.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent();
					intent.putExtra(KeyAddActivity.KEY_RECORD, info);
					intent.setClass(KeyListActivity.this, KeyAddActivity.class);
					
					activity.startActivity(intent);
				}
			});
			final KeyListAdapter adpater = this;
			final String keyNameNow = info.getKeyname();
			viewHolder.delete.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					new AlertDialog.Builder(KeyListActivity.this)
					.setMessage("确定要删除自定义按键" + keyNameNow + ",删除后无法恢复，不过你会找回"
							+ IBlueToothConst.ADD_USER_KEY_COINS + "个积分.点击确定后删除。")
					.setTitle("删除")
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
//									YoumiPointsManager.spendPoints(ConfigActivity.this,
//											IBlueToothConst.REMOVE_AD_COINS);
									YoumiPointsManager.awardPoints(KeyListActivity.this,
											IBlueToothConst.ADD_USER_KEY_COINS);
									tableOperator.deleteKey(info.getKeyId());
									adpater.notifyDataSetChanged();
								}
							}).show();
					
				}
				
			});
				
			return convertView;
		}		
	}

}

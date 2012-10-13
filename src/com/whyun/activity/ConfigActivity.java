package com.whyun.activity;

import net.youmi.android.appoffers.YoumiOffersManager;
import net.youmi.android.appoffers.YoumiPointsManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import com.whyun.IBlueToothConst;
import com.whyun.bluetooth.R;
import com.whyun.communication.ConnectSetting;
import com.whyun.util.MyLog;

public class ConfigActivity extends PreferenceActivity implements  
		OnPreferenceChangeListener,IMyPreference {
	
	 private CheckBoxPreference mCheckBoxPre;  
	 private ListPreference connectType;
	 private ConnectSetting connectSetting = ConnectSetting.getInstance();
	 private SharedPreferences settings;
	 private MyLog logger = MyLog.getLogger(ConfigActivity.class);
	
	@Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        addPreferencesFromResource(R.xml.config);
  
        mCheckBoxPre = (CheckBoxPreference) findPreference(USE_SHAKE);  
        mCheckBoxPre.setOnPreferenceChangeListener(this);
        
        connectType = (ListPreference)findPreference(CONNECT_TYPE);
        connectType.setOnPreferenceChangeListener(this);
        
        settings = PreferenceManager.getDefaultSharedPreferences(this);
    }  

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		String key = preference.getKey();
		String valueNow = newValue.toString();
		logger.info("valueNow:" + valueNow);
		if (key.equals(CONNECT_TYPE)) {
			int typeNow;// = (Integer)newValue;
			try {
				typeNow = Integer.parseInt(valueNow);				
			} catch (Exception e) {
				logger.warn("transform to integer error:",e);
				typeNow = 0;
			}
			connectSetting.setConnectType(typeNow);
			logger.info("typeNow:" + typeNow);
		}
		return true;
	}
	
	private void removeAdSet() {
		Editor editor = settings.edit();
		editor.putBoolean(REMOVE_AD, true);
		editor.commit();
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		logger.info("preference key is:"+preference.getKey());
		if (preference.getKey().equals("wifi_setting")) {
			startActivity(new
            Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
		} else if (REMOVE_AD.equals(preference.getKey())) {
			int coinsNow = YoumiPointsManager.queryPoints(ConfigActivity.this);
			if (!settings.getBoolean(REMOVE_AD, false)) {
				
				if (coinsNow >= IBlueToothConst.REMOVE_AD_COINS) {
					new AlertDialog.Builder(ConfigActivity.this)
					.setMessage("当前操作要消费" + IBlueToothConst.REMOVE_AD_COINS + "个积分，"
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
									YoumiPointsManager.spendPoints(ConfigActivity.this,
											IBlueToothConst.REMOVE_AD_COINS);
									removeAdSet();
								}
							}).show();
					
					
				} else {
					new AlertDialog.Builder(ConfigActivity.this)
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
									YoumiOffersManager.showOffers(ConfigActivity.this,
											YoumiOffersManager.TYPE_REWARD_OFFERS);
								}
							}).show();
				}
			}
			
		} else if ("manage_user_key".equals(preference.getKey())) {
			Intent intent = new Intent();
			intent.setClass(ConfigActivity.this, KeyListActivity.class);
			startActivity(intent);
		}
		return true;
	}
}

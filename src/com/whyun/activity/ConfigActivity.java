package com.whyun.activity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

import com.whyun.bluetooth.R;
import com.whyun.communication.ConnectSetting;
import com.whyun.util.MyLog;

public class ConfigActivity extends PreferenceActivity implements  
		OnPreferenceChangeListener,IMyPreference {
	
	 private CheckBoxPreference mCheckBoxPre;  
	 private ListPreference connectType;
	 private ConnectSetting connectSetting = ConnectSetting.getInstance();
	 private MyLog logger = MyLog.getLogger(ConfigActivity.class);
	
	@Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        addPreferencesFromResource(R.xml.config);
  
        mCheckBoxPre = (CheckBoxPreference) findPreference(USE_SHAKE);  
        mCheckBoxPre.setOnPreferenceChangeListener(this);
        
        connectType = (ListPreference)findPreference(CONNECT_TYPE);
        connectType.setOnPreferenceChangeListener(this);
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

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		logger.info("preference key is:"+preference.getKey());
		if (preference.getKey().equals("wifi_setting")) {
			startActivity(new
            Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
		}
		return true;
	}
}

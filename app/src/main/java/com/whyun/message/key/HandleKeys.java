package com.whyun.message.key;

import java.util.HashMap;
import java.util.Map;

import android.content.SharedPreferences.Editor;

import com.whyun.IBlueToothConst;
import com.whyun.activity.IMyPreference;
import com.whyun.message.RecieveKeySetting;
import com.whyun.message.bean.KeyInfo;
import com.whyun.message.data.KeyTableOperator;

//import com.sunny.message.RecieveKeySetting;

/**
 * The Class HandleKeys.
 */
public class HandleKeys  implements IBlueToothConst {
	
	/** The instance. */
	private volatile static HandleKeys instance = null;
	
	/** 每个按钮对应一个byte数组. */
	private volatile Map<String,byte[]> keys = new HashMap<String,byte[]>();
	
	/** 当前的按键设置，默认为手柄. */
	private int typeNow = SET_KEY_HANDLE;
	
	/**
	 * 获取现在的按键类型.
	 * 
	 * @return the type now
	 */
	public int getTypeNow() {
		return typeNow;
	}

	/**
	 * 构造函数，默认采用手柄按键.
	 */
	private HandleKeys() {
		applyDefault();
	}

	/** 写入默认手柄键位，会先清空现有映射 */
	private void applyDefault() {
		byte[] message = DEFAULT_HANDLE_SET;
		Map<String, byte[]> m = new HashMap<String, byte[]>();
		m.put(upBtn,new byte[]{ message[0]});//w
		m.put(downBtn,new byte[]{ message[1]});//s
		m.put(leftBtn,new byte[]{ message[2]});//a
		m.put(rightBtn,new byte[]{ message[3]});//d
		m.put(selectGunBtn,new byte[]{ message[6]});//u (X)
		m.put(dropGunBtn,new byte[]{ message[7]});//i (Y)
		m.put(other1Btn,new byte[]{ message[8]});//o
		m.put(fireBtn,new byte[]{ message[9]});//j (A)
		m.put(jumpBtn,new byte[]{ message[10]});//k (B)
		m.put(other2Btn,new byte[]{ message[11]});//l
		m.put(selectBtn,new byte[]{ message[4]});//1
		m.put(startBtn,new byte[]{ message[5]});//5
		keys = m;
		typeNow = SET_KEY_HANDLE;
	}
	
	/**
	 * Gets the single instance of HandleKeys.
	 * 
	 * @return single instance of HandleKeys
	 */
	public static HandleKeys getInstance() {
		if (instance == null) {
			synchronized(HandleKeys.class) {
				if (instance == null) {
					instance = new HandleKeys();
				}
			}
		}
		return instance;
	}
		
	/**
	 * 将当前的按键设置清空
	 */
	public  void clearSetting() {
		keys = new HashMap<String,byte[]>();
	}

	/**
	 * 获取当前所有的按键设置
	 * 
	 * @return the keys
	 */
	public  Map<String, byte[]> getKeys() {
		return keys;
	}
	
	/**
	 * 获取指定的按钮的按键设置
	 * 
	 * @param index 按钮名
	 * 
	 * @return 返回的当前的按钮对应的快捷键byte数组
	 */
	public byte[] getKeyBody(String index) {
		return keys.get(index);
	}

	/**
	 * 批量设置所有按键
	 * 
	 * @param _keys the _keys
	 */
	public  void setKeys(Map<String, byte[]> _keys) {
		keys = _keys;
	}
	
	/**
	 * 设置内置按键。内置方案只剩默认手柄键位；
	 * 旧版本存下的 PPT(-2)/千千静听(-3) 已改为独立界面，这里一律回落到默认键位。
	 *
	 * @param message 忽略，保留参数以兼容旧调用
	 * @param type    忽略，保留参数以兼容旧调用
	 */
	public void setKeys(byte[] message, int type,Editor editor) {
		applyDefault();
		if (editor != null) {
			editor.putInt(IMyPreference.KEY_TYPE, typeNow);
			editor.commit();
		}
	}

	/**
	 * 根据电脑端接收到的消息来设置按键，现在这个逻辑不用了。
	 * 
	 * @param settingMessage the new keys
	 */
	public void setKeys(RecieveKeySetting settingMessage) {
		clearSetting();
		byte type = settingMessage.getType();
		byte[] message = settingMessage.getBody();
			
		setKeys(message,type,null);
	}
	
	public void setKeys(KeyInfo info,Editor editor) {
		if (info == null) {// 方案已被删除
			setKeys(null, SET_KEY_HANDLE, editor);
			return;
		}
		// 先整体替换为新映射，避免未设置的按钮残留上一套方案的键
		Map<String, byte[]> keys = new HashMap<String, byte[]>();
		typeNow = info.getKeyId();
		keys.put(upBtn,info.getBytes(KeyTableOperator.KEY_TABLE_UP_FIELD));
		keys.put(downBtn,info.getBytes(KeyTableOperator.KEY_TABLE_DOWN_FIELD));
		keys.put(leftBtn,info.getBytes(KeyTableOperator.KEY_TABLE_LEFT_FIELD));
		keys.put(rightBtn,info.getBytes(KeyTableOperator.KEY_TABLE_RIGHT_FIELD));
		
		keys.put(selectGunBtn,info.getBytes(KeyTableOperator.KEY_TABLE_X_FIELD));
		keys.put(dropGunBtn,info.getBytes(KeyTableOperator.KEY_TABLE_Y_FIELD));

		keys.put(fireBtn,info.getBytes(KeyTableOperator.KEY_TABLE_A_FIELD));
		keys.put(jumpBtn,info.getBytes(KeyTableOperator.KEY_TABLE_B_FIELD));

		keys.put(selectBtn,info.getBytes(KeyTableOperator.KEY_TABLE_SELECT_FIELD));
		keys.put(startBtn,info.getBytes(KeyTableOperator.KEY_TABLE_START_FIELD));
		this.keys = keys;
		
		if (editor != null) {
			editor.putInt(IMyPreference.KEY_TYPE,info.getKeyId());
			editor.commit();
		}
	}
}

package com.whyun.message.key;

import java.util.HashMap;
import java.util.Map;

import android.content.SharedPreferences.Editor;

import com.whyun.IBlueToothConst;
import com.whyun.activity.IMyPreference;
import com.whyun.message.RecieveKeySetting;

//import com.sunny.message.RecieveKeySetting;

/**
 * The Class HandleKeys.
 */
public class HandleKeys  implements IBlueToothConst {
	
	/** The instance. */
	private volatile static HandleKeys instance = null;
	
	/** The keys. */
	private  Map<String,byte[]> keys = new HashMap<String,byte[]>();
	
	/** 当前的按键设置，默认为手柄. */
	private byte typeNow = handleKeySet;
	
	/**
	 * 获取现在的按键类型.
	 * 
	 * @return the type now
	 */
	public byte getTypeNow() {
		return typeNow;
	}

	/**
	 * 构造函数，默认采用手柄按键.
	 */
	private HandleKeys() {
		byte[] message = DEFAULT_HANDLE_SET;
		keys.put(upBtn,new byte[]{ message[0]});//w
		keys.put(downBtn,new byte[]{ message[1]});//s
		keys.put(leftBtn,new byte[]{ message[2]});//a
		keys.put(rightBtn,new byte[]{ message[3]});//d
		keys.put(selectGunBtn,new byte[]{ message[6]});//u (X)
		keys.put(dropGunBtn,new byte[]{ message[7]});//i (Y)
		keys.put(other1Btn,new byte[]{ message[8]});//o
		keys.put(fireBtn,new byte[]{ message[9]});//j (A)
		keys.put(jumpBtn,new byte[]{ message[10]});//k (B)
		keys.put(other2Btn,new byte[]{ message[11]});//l
		keys.put(selectBtn,new byte[]{ message[4]});//1
		keys.put(startBtn,new byte[]{ message[5]});//5
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
	 * Clear setting.
	 */
	public  void clearSetting() {
		keys = new HashMap<String,byte[]>();
	}

	/**
	 * Gets the keys.
	 * 
	 * @return the keys
	 */
	public  Map<String, byte[]> getKeys() {
		return keys;
	}
	
	/**
	 * Gets the key body.
	 * 
	 * @param index the index
	 * 
	 * @return the key body
	 */
	public byte[] getKeyBody(String index) {
		return keys.get(index);
	}

	/**
	 * Sets the keys.
	 * 
	 * @param _keys the _keys
	 */
	public  void setKeys(Map<String, byte[]> _keys) {
		keys = _keys;
	}
	
	/**
	 * Sets the keys.
	 * 
	 * @param message the message
	 * @param type the type
	 */
	public void setKeys(byte[] message, byte type,Editor editor) {
		if (type == handleKeySet) {
			typeNow = handleKeySet;
			keys.put(upBtn,new byte[]{ message[0]});
			keys.put(downBtn,new byte[]{ message[1]});
			keys.put(leftBtn,new byte[]{ message[2]});
			keys.put(rightBtn,new byte[]{ message[3]});
			
			keys.put(selectGunBtn,new byte[]{ message[6]});
			keys.put(dropGunBtn,new byte[]{ message[7]});
			keys.put(other1Btn,new byte[]{ message[8]});
			keys.put(fireBtn,new byte[]{ message[9]});
			keys.put(jumpBtn,new byte[]{ message[10]});
			keys.put(other2Btn,new byte[]{ message[11]});
			keys.put(selectBtn,new byte[]{ message[4]});
			keys.put(startBtn,new byte[]{ message[5]});
			if (editor != null) {
				editor.putInt(IMyPreference.KEY_TYPE, typeNow);
			}
		} else if (type == pptKeySet) {
			typeNow = pptKeySet;
			keys.put(upBtn,new byte[]{ 38});//up arrow
			keys.put(downBtn,new byte[]{ 40});//down arrow
			keys.put(leftBtn,new byte[]{ 37});//left arrow
			keys.put(rightBtn,new byte[]{ 39});//right arrow
			keys.put(selectGunBtn,new byte[]{ 27});// esc(stop fullscrenn) (X)
			keys.put(dropGunBtn,new byte[]{ 16, 116});// shift+F5(begin fullscreen) (Y)
			if (editor != null) {
				editor.putInt(IMyPreference.KEY_TYPE, typeNow);
			}
		} else if (type == playerKeySet) {
			typeNow = playerKeySet;
			keys.put(upBtn,new byte[]{ 17,18,38});//crtl + alt + up(turn up)
			keys.put(downBtn,new byte[]{ 17,18,40});//crtl + alt + down(turn down)
			keys.put(leftBtn,new byte[]{ 17,18,37});//crtl + alt + left(previous)
			keys.put(rightBtn,new byte[]{17,18, 39});//crtl + alt + right(next)
			keys.put(selectGunBtn,new byte[]{ 17,18,116});//crtl + alt + f5(play) (X)
			keys.put(dropGunBtn,new byte[]{ 17,18,117});//crtl + alt + f6(stop) (Y)
			if (editor != null) {
				editor.putInt(IMyPreference.KEY_TYPE, typeNow);
			}
		}

	}
	
	/**
	 * Sets the keys.
	 * 
	 * @param settingMessage the new keys
	 */
	public void setKeys(RecieveKeySetting settingMessage) {
		clearSetting();
		byte type = settingMessage.getType();
		byte[] message = settingMessage.getBody();
			
		setKeys(message,type,null);
	}
}

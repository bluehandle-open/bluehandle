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
	private  Map<String,byte[]> keys = new HashMap<String,byte[]>();
	
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
	 * 设置内置按键
	 * 
	 * @param message the message
	 * @param type 当前的按键类型，可选类型handleKeySet、SET_KEY_PPT、SET_KEY_PLAYER
	 */
	public void setKeys(byte[] message, int type,Editor editor) {
		if (type == SET_KEY_HANDLE) {
			typeNow = SET_KEY_HANDLE;
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
				editor.commit();
			}
		} else if (type == SET_KEY_PPT) {
			typeNow = SET_KEY_PPT;
			keys.put(upBtn,new byte[]{ 38});//up arrow
			keys.put(downBtn,new byte[]{ 40});//down arrow
			keys.put(leftBtn,new byte[]{ 37});//left arrow
			keys.put(rightBtn,new byte[]{ 39});//right arrow
			keys.put(selectGunBtn,new byte[]{ 27});// esc(stop fullscrenn) (X)
			keys.put(dropGunBtn,new byte[]{ 16, 116});// shift+F5(begin fullscreen) (Y)
			if (editor != null) {
				editor.putInt(IMyPreference.KEY_TYPE, typeNow);
				editor.commit();
			}
		} else if (type == SET_KEY_PLAYER) {
			typeNow = SET_KEY_PLAYER;
			keys.put(upBtn,new byte[]{ 17,18,38});//crtl + alt + up(turn up)
			keys.put(downBtn,new byte[]{ 17,18,40});//crtl + alt + down(turn down)
			keys.put(leftBtn,new byte[]{ 17,18,37});//crtl + alt + left(previous)
			keys.put(rightBtn,new byte[]{17,18, 39});//crtl + alt + right(next)
			keys.put(selectGunBtn,new byte[]{ 17,18,116});//crtl + alt + f5(play) (X)
			keys.put(dropGunBtn,new byte[]{ 17,18,117});//crtl + alt + f6(stop) (Y)
			if (editor != null) {
				editor.putInt(IMyPreference.KEY_TYPE, typeNow);
				editor.commit();
			}
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
		
		if (editor != null) {
			editor.putInt(IMyPreference.KEY_TYPE,info.getKeyId());
			editor.commit();
		}
	}
}

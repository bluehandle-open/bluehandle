package com.whyun.message;

import java.io.IOException;
import java.io.OutputStream;

import com.whyun.IBlueToothConst;
import com.whyun.message.key.HandleKeys;
import com.whyun.util.MyLog;

/**
 * 按键报文。
 * |head|body|
 * 其中 head 为 |messageType|totalLen|，totalLen 为整个数据包长度（含 head）；
 * body 为 |pressType|keyCodes...|，pressType 可选 {@link IBlueToothConst#toPress}/
 * {@link IBlueToothConst#toRelease}/{@link IBlueToothConst#toPreassRelease}，
 * keyCodes 为 Windows 虚拟键码，多个即组合键。
 */
public class KeyCommunication extends AbstractMessage implements IBlueToothConst {

	private static final MyLog logger = MyLog.getLogger(KeyCommunication.class);

	public KeyCommunication(byte totalLen, byte type, byte[] body) {
		super(totalLen, type, body);
	}

	/**
	 * 按手柄按钮名构造报文，键码取自当前生效的键位方案。
	 *
	 * @return 该按钮未设置按键时返回 null
	 */
	public static KeyCommunication forButton(String buttonName, byte pressType) {
		byte[] keyBody = HandleKeys.getInstance().getKeyBody(buttonName);
		if (keyBody == null) {
			logger.debug("the key " + buttonName + " is not setted, can't be sended.");
			return null;
		}
		return forKeys(keyBody, pressType);
	}

	/** 直接按虚拟键码构造报文，供遥控器、PPT、数字键等固定布局使用。 */
	public static KeyCommunication forKeys(byte[] keyCodes, byte pressType) {
		int len = keyCodes.length + 1;// 快捷键数组长度 + 按键类型长度
		byte[] body = new byte[len];
		body[0] = pressType;
		System.arraycopy(keyCodes, 0, body, 1, keyCodes.length);
		return new KeyCommunication((byte) (len + 2), sendKey, body);
	}

	/** 同步写出一条报文。调用方负责在非主线程调用。 */
	public static void sendMsg(OutputStream os, AbstractMessage message)
			throws IOException {
		if (os == null) {
			throw new IOException("output stream is not ready");
		}
		os.write(message.getData());
	}
}

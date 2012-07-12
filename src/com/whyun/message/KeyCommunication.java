package com.whyun.message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import android.util.Log;

import com.whyun.IBlueToothConst;
import com.whyun.message.key.HandleKeys;
import com.whyun.message.util.MessageTool;

public class KeyCommunication extends AbstractMessage implements IBlueToothConst {
	private static final String TAG = "AbstractMessage";
	private static HandleKeys keySet = HandleKeys.getInstance();
	
	public KeyCommunication(byte totalLen, byte type, byte[] body) {
		super(totalLen, type, body);		
	}
	
	private void printSelf(byte[] message) {
		if (isDebug && message != null && message.length > 2) {
			Log.i(TAG,"the length of the message is " + message[0]);
			Log.i(TAG,"the type of the message is " + message[1]);
			
			String body = "the message body is: ";
			for(int i=2,len=message.length;i<len-2;i++) {
				body += (message[i] + " ");
			}
			Log.i(TAG,body);
		} else {
			Log.i(TAG,"wrong message.");
		}
	}

	public void sendSelf(OutputStream os) {
		int len = body.length + 4;
		byte[] totalMessage = new byte[len];
		totalMessage[0] = totalLen;
		totalMessage[1] = type;
		totalMessage[len-1] = 10;
		totalMessage[len-2] = 13;
		System.arraycopy(body, 0, totalMessage, 2, len-4);
		try {
			os.write(totalMessage);
			printSelf(totalMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Send msg.
	 * 
	 * @param os 输出流对象
	 * @param keyName 按键名称
	 * @param pressType 按键方式
	 * @param useDefaultSet 是否使用默认设置，默认设置为手柄
	 */
	public static void sendMsg(OutputStream os, String keyName,byte pressType) {

		byte[] keyBody = keySet.getKeyBody(keyName);
		if (keyBody != null) {
			int len = keyBody.length + 1;
			byte[] body = new byte[len];
			
			byte handleType = keySet.getTypeNow();
			if (handleType == handleKeySet) {
				body[0] = pressType;
			} else {
				body[0] = toPreassRelease;
			}
			
			System.arraycopy(keyBody, 0, body, 1, len-1);
			KeyCommunication message =
				new KeyCommunication((byte)(len+2),sendKey,body);
			message.sendSelf(os);
		} else {
			Log.i(serverSign,"the key is not setted,can't be sended.");
		}

	}
	
	/**
	 * 接收并解析PC端发送过来的报文，如果报文是设置按键，则更新手机端按键设置。
	 *
	 * @param is 输入流
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void reciveMessage(InputStream is) throws IOException {
		BufferedReader brd = new BufferedReader(new InputStreamReader(is));

		String messageStr = brd.readLine();
		Log.i(serverSign, "get a message.");
		AbstractMessage message = MessageTool.getMessage(messageStr);
		if (message instanceof RecieveKeySetting) {//设置按键信息

			// TODO check the valid of message
			keySet.setKeys((RecieveKeySetting) message);
			//keySetted = true;
			Log.i(serverSign, "set the key setting.");
		}
	}

}

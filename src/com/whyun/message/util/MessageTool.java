package com.whyun.message.util;

import android.util.Log;

import com.whyun.IBlueToothConst;
import com.whyun.message.AbstractMessage;
import com.whyun.message.KeyCommunication;
import com.whyun.message.RecieveKeySetting;

public class MessageTool implements IBlueToothConst {
	private MessageTool() {
		
	}
	private static  void printInfo(byte[] body) {
		if (body != null && body.length > 0) {
			byte lenMsg = body[0];
			byte typeMsg = body[1];
			Log.i(serverSign,"the length of message is " + lenMsg);
			Log.i(serverSign,"the type of message is " + typeMsg);
			String bodyStr = "";
			for(int i=2;i<body.length;i++) {
				bodyStr += body[i] + " ";
			}
			Log.i(serverSign,"body is " + bodyStr);
		}
	}
	public static AbstractMessage getMessage(String messageStr) {
		byte[] messageArray = messageStr.getBytes();
		//int len = messageArray.length;
		byte totalLen = messageArray[0];
		byte type = messageArray[1];
		
		int bodyLen = totalLen-2;
		
		byte[] body = null;
		if (bodyLen > 0) {
			body= new byte[bodyLen];
			//System.out.println(messageArray.length);
			//System.out.println(body.length);
			try {
				System.arraycopy(messageArray,2,body,0,bodyLen);
			} catch(Exception e) {
				Log.i(serverSign,"you have given a wrong message.");
				return null;
			}
			Log.i(serverSign,"the message get from pc client is " + new String(body));
		} else {
			body = new byte[0];
		}
		if (isDebug) {
			printInfo(messageArray);
		}
		if ((type & 1) == 0) {//��������
			return new RecieveKeySetting(totalLen,type,body);
		} else {//��Ϣͨ��
			return new KeyCommunication(totalLen,type,body);
		}
	}
}

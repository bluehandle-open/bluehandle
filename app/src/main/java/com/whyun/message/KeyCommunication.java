package com.whyun.message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import com.whyun.IBlueToothConst;
import com.whyun.message.key.HandleKeys;
import com.whyun.message.util.MessageTool;
import com.whyun.util.MyLog;

public class KeyCommunication extends AbstractMessage implements IBlueToothConst {

	private static HandleKeys keySet = HandleKeys.getInstance();
	private static final MyLog logger = MyLog.getLogger(KeyCommunication.class);
	/**
	 * |head|body|
	 * 其中head部分包括
	 * |totalLen|messageType|
	 * 其中totalLen为整个数据包长度，包括head和body的总长，messageType为当前消息类型
	 * 其中body中包括
	 * |pressType|keybody|
	 * 其中pressType为敲击类型，可选值为{@link IBlueToothConst.toPress}/
	 * {@link IBlueToothConst.toRelease}/{@link IBlueToothConst.toPreassRelease}
	 * keybody为按键的assic码组成的数组
	 * @param totalLen
	 * @param type
	 * @param body
	 */
	public KeyCommunication(byte totalLen, byte type, byte[] body) {
		super(totalLen, type, body);		
	}
	
	private void printSelf(byte[] message) {
		if (isDebug && message != null && message.length > 2) {
			logger.debug("the length of the message is " + message[0]);
			logger.debug("the type of the message is " + message[1]);
			
			String body = "the message body is: ";
			for(int i=2,len=message.length;i<len-2;i++) {//最后两字节是\r\n
				body += (message[i] + " ");
			}
			logger.debug(body);
		} else {
			logger.debug("not in debug mode or message wrong.");
		}
	}

	public void sendSelf(OutputStream os) throws IOException {
		int len = body.length + 2;
		byte[] totalMessage = new byte[len];
		totalMessage[1] = totalLen;//总长度
		totalMessage[0] = type;//消息类型
		//totalMessage[len-1] = 10;//以\r\n结尾要发送的数据，因为电脑端读取数据的时候是按行读取的
		//totalMessage[len-2] = 13;
		System.arraycopy(body, 0, totalMessage, 2, len-2);
		
		os.write(totalMessage);
		printSelf(totalMessage);
		
	}
	
	/**
	 * 根据按键名称获取要所有按钮的按键配置的byte数组，然后组织成数据包，发送到电脑端。
	 * 数据包正文部分第一个字节是敲击键类型，可选值为{@link IBlueToothConst.toPress}/
	 * {@link IBlueToothConst.toRelease}/{@link IBlueToothConst.toPreassRelease},
	 * 剩余部分是当前的按钮对应的快捷键的byte数组。
	 * 
	 * @param os 输出流对象
	 * @param keyName 按键名称
	 * @param pressType 按键方式。可选值为{@link IBlueToothConst.toPress}/
	 * {@link IBlueToothConst.toRelease}/{@link IBlueToothConst.toPreassRelease}
	 *
	 */
	public static void sendMsg(OutputStream os, String keyName,byte pressType) throws IOException {

		byte[] keyBody = keySet.getKeyBody(keyName);
		if (keyBody != null) {
			int len = keyBody.length + 1;//快捷键数组长度+按键类型长度
			byte[] body = new byte[len];//消息正文数组
			
			body[0] = pressType;
			
			System.arraycopy(keyBody, 0, body, 1, len-1);
			KeyCommunication message =
				new KeyCommunication((byte)(len+2),sendKey/*消息类型为发送按键*/,body);
			message.sendSelf(os);
		} else {
			logger.debug("the key is not setted,can't be sended.");
		}

	}
	
	public static void sendMsg(OutputStream os,AbstractMessage message)
			throws IOException {
		os.write(message.getData());
	}
	
	/**
	 * 接收并解析PC端发送过来的报文。
	 * 如果报文是设置按键，则更新手机端按键设置。 现在这个逻辑已经不再使用了。
	 *
	 * @param is 输入流
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void reciveMessage(InputStream is) throws IOException {
		BufferedReader brd = new BufferedReader(new InputStreamReader(is));

		String messageStr = brd.readLine();
		logger.debug( "get a message.");
		AbstractMessage message = MessageTool.getMessage(messageStr);
		if (message instanceof RecieveKeySetting) {//设置按键信息

			// TODO check the valid of message
			keySet.setKeys((RecieveKeySetting) message);
			//keySetted = true;
			logger.debug( "set the key setting.");
		}
	}

}

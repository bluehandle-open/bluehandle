package com.whyun.message;
/**
 * 抽象消息类<br />
 * 一条消息由消息头+消息正文构成<br />
 * 消息头包括：当前消息的总长度+当前消息类型
 * */
public abstract class AbstractMessage {
	/** 当前消息的总长度. */
	protected byte totalLen;
	
	/** 当前消息类型. */
	protected byte type;
	
	/** 消息正文. */
	protected byte[] body;	
	
	public AbstractMessage(byte totalLen, byte type, byte[] body) {		
		this.totalLen = totalLen;
		this.type = type;
		this.body = body;
	}

	//abstract public byte[] getMessage();

	public byte getTotalLen() {
		return totalLen;
	}

	public byte getType() {
		return type;
	}

	public byte[] getBody() {
		return body;
	}
	
	public byte[] getData() {
		byte[] data = new byte[totalLen];
		data[1] = totalLen;
		data[0] = type;
		if (body != null) {
			System.arraycopy(body, 0, data, 2, body.length);
		}
		
		return data;
	}
}

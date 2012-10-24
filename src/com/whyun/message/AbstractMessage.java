package com.whyun.message;

public abstract class AbstractMessage {
	/** The total message length. */
	protected byte totalLen;
	
	/** The message type. */
	protected byte type;
	
	/** The message content body. */
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
		data[0] = totalLen;
		data[1] = type;
		if (body != null) {
			System.arraycopy(body, 0, data, 2, body.length);
		}
		
		return data;
	}
}

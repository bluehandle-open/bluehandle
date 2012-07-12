package com.whyun.message;

public class RecieveKeySetting extends AbstractMessage {
	
	public RecieveKeySetting(byte totalLen, byte type, byte[] body) {
		super(totalLen, type, body);		
	}
	
}

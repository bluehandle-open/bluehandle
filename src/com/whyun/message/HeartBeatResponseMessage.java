package com.whyun.message;

import com.whyun.IBlueToothConst;

public class HeartBeatResponseMessage extends AbstractMessage {

	public HeartBeatResponseMessage() {
		super((byte)2, IBlueToothConst.HEART_BEAT_RESP, null);
	}
	
}

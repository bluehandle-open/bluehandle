package com.whyun.message;

import com.whyun.IBlueToothConst;

public class HeartBeatRequestMessage extends AbstractMessage {

	public HeartBeatRequestMessage() {
		super((byte)2, IBlueToothConst.HEART_BEAT_REQ, null);
	}
}

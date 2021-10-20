package com.whyun.message;

import com.whyun.IBlueToothConst;

public class FinishMessage extends AbstractMessage {

	public FinishMessage() {
		super((byte)2, IBlueToothConst.FINISH_SOCKET, null);		
	}

}

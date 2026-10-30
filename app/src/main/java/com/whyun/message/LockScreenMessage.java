package com.whyun.message;

import com.whyun.IBlueToothConst;

/** 请求电脑锁屏，无正文。 */
public class LockScreenMessage extends AbstractMessage {

	public LockScreenMessage() {
		super((byte) 2, IBlueToothConst.LOCK_SCREEN, null);
	}
}

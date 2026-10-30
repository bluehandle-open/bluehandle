package com.whyun.message;

import com.whyun.IBlueToothConst;

/**
 * 文本输入报文：|type|totalLen|flags|UTF-8 字节...|
 * 单包正文不超过 {@link IBlueToothConst#TEXT_CHUNK_MAX_BYTES} 字节，长文本由调用方分包。
 */
public class TextMessage extends AbstractMessage {

	public TextMessage(byte[] utf8Chunk, boolean pressEnter) {
		super((byte) (utf8Chunk.length + 3), IBlueToothConst.SEND_TEXT,
				buildBody(utf8Chunk, pressEnter));
	}

	private static byte[] buildBody(byte[] utf8Chunk, boolean pressEnter) {
		if (utf8Chunk.length > IBlueToothConst.TEXT_CHUNK_MAX_BYTES) {
			throw new IllegalArgumentException("text chunk too long: " + utf8Chunk.length);
		}
		byte[] body = new byte[utf8Chunk.length + 1];
		body[0] = pressEnter ? IBlueToothConst.TEXT_FLAG_ENTER : 0;
		System.arraycopy(utf8Chunk, 0, body, 1, utf8Chunk.length);
		return body;
	}
}

package com.whyun.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.os.Environment;

public final class HTTPUtil {
	private static MyLog logger = MyLog.getLogger(HTTPUtil.class);
	public static void down_file(String url) throws IOException {
		// 下载函数
		URL myURL = new URL(url);
		URLConnection conn = myURL.openConnection();
		conn.connect();
		InputStream is = conn.getInputStream();
		if (is == null)
			throw new RuntimeException("stream is null");
		// 把文件存到path
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			String filename=url.substring(url.lastIndexOf("/") + 1);
			String SDCard=Environment.getExternalStorageDirectory()+"";  
			String path = SDCard + "/" + filename;
			OutputStream os = new FileOutputStream(path);
			byte buf[] = new byte[1024];
			do {
				int numread = is.read(buf);
				if (is.read(buf) == -1) {
					break;
				}
				os.write(buf, 0, numread);
			} while (true);

			is.close();
			os.close();
		} else {
			logger.warn("sdcard没有找到");
		}
		
	}
}

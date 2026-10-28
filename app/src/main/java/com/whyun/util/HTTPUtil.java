package com.whyun.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.os.Environment;

public final class HTTPUtil {
	private static MyLog logger = MyLog.getLogger(HTTPUtil.class);

	private HTTPUtil() {

	}

	/**
	 * 下载文件到本应用的下载目录（无需存储权限）。
	 *
	 * @return 保存的文件
	 */
	public static File down_file(Context context, String url) throws IOException {
		URL myURL = new URL(url);
		URLConnection conn = myURL.openConnection();
		conn.connect();
		InputStream is = conn.getInputStream();
		if (is == null)
			throw new IOException("stream is null");
		String filename = url.substring(url.lastIndexOf("/") + 1);
		File target = new File(FileUtil.getAppDir(context, Environment.DIRECTORY_DOWNLOADS), filename);
		OutputStream os = new FileOutputStream(target);
		try {
			byte buf[] = new byte[1024];
			int numread;
			while ((numread = is.read(buf)) != -1) {
				os.write(buf, 0, numread);
			}
		} finally {
			is.close();
			os.close();
		}
		logger.debug("downloaded to " + target.getAbsolutePath());
		return target;
	}
}

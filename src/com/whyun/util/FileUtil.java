package com.whyun.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.os.Environment;

public final class FileUtil {
	private static MyLog  logger = MyLog.getLogger(FileUtil.class);
	public static void saveFile(String filename,String content) throws IOException {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			String SDCard=Environment.getExternalStorageDirectory()+"";  
			String path = SDCard + "/" + filename;
			File f = new File(path);
			if (f.exists()) {

			} else {
				if (f.createNewFile()) {
					logger.debug("文件创建成功！");
					 BufferedWriter output = new BufferedWriter(new FileWriter(f));
					 output.write(content);
				     output.close();
				} else {
					//logger.error("文件创建失败！");
					throw new IOException("create file failed.");
				}
			}
		} else {
			throw new IOException("sdcard not found.");
		}
	}
}

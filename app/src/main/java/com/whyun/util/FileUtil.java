package com.whyun.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;

public final class FileUtil {
	private static MyLog  logger = MyLog.getLogger(FileUtil.class);

	private FileUtil() {

	}

	/**
	 * 返回本应用可直接写入、无需运行时存储权限的目录。
	 * 优先使用外部存储中的应用专属目录（Android/data/包名/files/子目录），
	 * 用户可以通过 USB 文件传输看到；外部存储不可用时回退到内部存储。
	 *
	 * @param context 上下文
	 * @param subDir  子目录名，可传 null 或 {@link Environment#DIRECTORY_DOWNLOADS} 等
	 */
	public static File getAppDir(Context context, String subDir) {
		File dir = null;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			dir = context.getExternalFilesDir(subDir);
		}
		if (dir == null) {
			dir = subDir == null ? context.getFilesDir() : new File(context.getFilesDir(), subDir);
		}
		if (!dir.exists() && !dir.mkdirs()) {
			logger.warn("create dir failed: " + dir);
		}
		return dir;
	}

	/**
	 * 把文本内容写入应用专属目录下的文件。文件已存在时直接覆盖。
	 *
	 * @return 实际写入的文件
	 */
	public static File saveFile(Context context, String filename, String content) throws IOException {
		File f = new File(getAppDir(context, null), filename);
		BufferedWriter output = new BufferedWriter(new FileWriter(f, false));
		try {
			output.write(content);
		} finally {
			output.close();
		}
		logger.debug("file saved: " + f.getAbsolutePath());
		return f;
	}
}

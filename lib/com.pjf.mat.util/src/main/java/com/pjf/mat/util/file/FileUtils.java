package com.pjf.mat.util.file;


import java.io.FileReader;
import java.io.IOException;

public class FileUtils {

	/**
	 * Read a file to a string, truncating to max size
	 * @param fpath path to the file
	 * @param maxSize max size to accept
	 * @return string
	 * @throws IOException
	 */
	public static String readFileToString(String fpath, int maxSize) throws IOException {
		FileReader fr = new FileReader(fpath);
		char buf[] = new char[maxSize];
		fr.read(buf);
		fr.close();
		String content = new String(buf);
		// trim empty portion
		int end = content.indexOf('\0');
		if (end > 0) {
			content = content.substring(0, end);
		}
		return content;
	}
	

}

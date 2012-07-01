package com.pjf.mat.impl.util;

public class Conversion {

	public static String toHexIntString(int value) {
		StringBuffer buf = new StringBuffer();
		buf.append(toHexByteString(value >> 24));
		buf.append(toHexByteString(value >> 16));
		buf.append(toHexByteString(value >> 8));
		buf.append(toHexByteString(value));
		return buf.toString();
		}


	
	public static String toHexString(byte[] rep) {
		StringBuffer buf = new StringBuffer();
		for (byte b : rep) {
			buf.append(toHexByteString(b));
			buf.append(' ');
		}
		return buf.toString();
	}
	
	public static String toHexByteString(int data) {
		StringBuffer buf = new StringBuffer();
		char[] map = new char[] {'0', '1', '2', '3', '4', '5', '6', '7',
								 '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
		int d = data & 0xff;
		buf.append(map[(d >> 4) & 0xf]);
		buf.append(map[d & 0xf]);
		return buf.toString();
	}

	/**
	 * Convert 4 bytes to an int, big endian
	 * 
	 * @param data		byte array
	 * @param offset	index to start at
	 * @param len		number of bytes to convert
	 * @return			output
	 */
	public static int getIntFromBytes(byte[] data, int offset, int len) {
		  int ret = 0;
		  for (int i=offset; i<offset+len && i<data.length; i++) {
		    ret <<= 8;
		    ret |= (int)data[i] & 0xFF;
		  }
		return ret;
	}

}
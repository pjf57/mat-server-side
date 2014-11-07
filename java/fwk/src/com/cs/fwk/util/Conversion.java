package com.cs.fwk.util;

public class Conversion {

	/**
	 * @param value integer value
	 * @return 8 character hex representation
	 */
	public static String toHexIntString(int value) {
		StringBuffer buf = new StringBuffer();
		buf.append(toHexByteString(value >> 24));
		buf.append(toHexByteString(value >> 16));
		buf.append(toHexByteString(value >> 8));
		buf.append(toHexByteString(value));
		return buf.toString();
		}

	/**
	 * @param value long value
	 * @return 16 character hex representation
	 */
	public static String toHexLongString(long value) {
		StringBuffer buf = new StringBuffer();
		buf.append(toHexByteString((int) (value >> 56)));
		buf.append(toHexByteString((int) (value >> 48)));
		buf.append(toHexByteString((int) (value >> 40)));
		buf.append(toHexByteString((int) (value >> 32)));
		buf.append(toHexByteString((int) (value >> 24)));
		buf.append(toHexByteString((int) (value >> 16)));
		buf.append(toHexByteString((int) (value >> 8)));
		buf.append(toHexByteString((int) (value)));
		return buf.toString();
	}

	/**
	 * @param rep byte array
	 * @return 2*N hex character representation
	 */
	public static String toHexString(byte[] rep) {
		StringBuffer buf = new StringBuffer();
		for (byte b : rep) {
			buf.append(toHexByteString(b));
			buf.append(' ');
		}
		return buf.toString();
	}

	/**
	 * @param data - byte
	 * @return 2 char string hex representation
	 */
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
	 * Convert up to 4 bytes to an int, big endian
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

	/**
	 * Convert up to 8 bytes to a long, big endian
	 * 
	 * @param data		byte array
	 * @param offset	index to start at
	 * @param len		number of bytes to convert
	 * @return			output
	 */
	public static long getLongFromBytes(byte[] data, int offset, int len) {
		  long ret = 0;
		  for (int i=offset; i<offset+len && i<data.length; i++) {
		    ret <<= 8;
		    ret |= (int)data[i] & 0xFF;
		  }
		return ret;
	}

	/**
	 * 
	 * @param val
	 * @return hex representation of float
	 */
	public static String toHexFloatString(float val) {
		return toHexIntString(Float.floatToIntBits(val));
	}

	/**
	 * Convert float value into a whole integer comprising all the precision.
	 * (ie: 5.21 becomes 521)
	 * 
	 * @param val - float value
	 * @return whole int
	 */
	public static int floatToIntWhole(float val) {
		// Note: initial solution is fixed to work with 2dp		
		return (int) (val * 100);
	}

	/**
	 * Return the number of decimal places in a float
	 * eg: 5.21 becomes 2
	 * 
	 * @param val - float value
	 * @return number of dps
	 */
	public static int floatToNdp(float val) {
		// Note: initial solution is fixed to work with 2dp
		return 2;
	}

	/**
	 * Convert an 8 char string to a long
	 * 
	 * @param str - the string
	 * @return long
	 * @throws Exception
	 */
	public static long stringToRaw(String str) throws Exception {
		long ret = 0;
		if (str.length() != 8) {
			throw new Exception("stringToRaw() - illegal strlen: [" + str + "]");
		}
		for (int i=0; i<8; i++) {
			ret <<= 8;
			ret |= (byte) str.charAt(i);
		}
		return ret;
	}

	/**
	 * Convert a float to a 4 byte representation in a buffer
	 * 
	 * @param val - value to convert
	 * @param buf - buffer to store into
	 * @param idx - index in buffer to start at
	 */
	public static void floatToBytes(float val, byte[] buf, int idx) {
		int intval = Float.floatToIntBits(val);
		buf[idx++] = (byte) ((intval >> 24) & 0xff);
		buf[idx++] = (byte) ((intval >> 16) & 0xff);
		buf[idx++] = (byte) ((intval >> 8) & 0xff);
		buf[idx++] = (byte) (intval & 0xff);
	}

	/**
	 * Convert an array to a string
	 *
	 * @param arr
	 * @return String of form [o1 o2 o3]
	 */
	public static String arrayToString(Object[] arr) {
		StringBuffer buf = new StringBuffer("[");
		int num = 0;
		for (Object obj:arr) {
			if (obj == null) {
				buf.append("null");
			} else {
				buf.append(obj.toString());
			}
			num++;
			if (num < arr.length) {
				buf.append(' ');
			}
		}
		buf.append(']');
		return buf.toString();
	}

	/**
	 * Convert a string of hex chars to an array of byte.
	 * 
	 * @param string
	 * @return byte array
	 * @throws Exception if non hex char found
	 */
	public static byte[] hextTobyte(String s) throws Exception {
		int len = s.length() / 2;
		byte[] data = new byte[len];
		int si = 0;
		for (int bi=0; bi<len; bi++) {
			int d = hex(s.charAt(si)) * 16 + hex(s.charAt(si+1));
			data[bi] = (byte) (d & 0xff);
			si += 2;
		}
		return data;
	}

	/**
	 * @param ch
	 * @return hex value of ch
	 * @throws Exception 
	 */
	private static int hex(char ch) throws Exception {
		int d = 0;
		switch(ch) {
		case '0' :	d = 0; break;
		case '1' :	d = 1; break;
		case '2' :	d = 2; break;
		case '3' :	d = 3; break;
		case '4' :	d = 4; break;
		case '5' :	d = 5; break;
		case '6' :	d = 6; break;
		case '7' :	d = 7; break;
		case '8' :	d = 8; break;
		case '9' :	d = 9; break;
		case 'a' :	d = 10; break;
		case 'A' :	d = 10; break;
		case 'b' :	d = 11; break;
		case 'B' :	d = 11; break;
		case 'c' :	d = 12; break;
		case 'C' :	d = 12; break;
		case 'd' :	d = 13; break;
		case 'D' :	d = 13; break;
		case 'e' :	d = 14; break;
		case 'E' :	d = 14; break;
		case 'f' :	d = 15; break;
		case 'F' :	d = 15; break;
		default: throw new Exception("non-hex char found");
		}
		return d;
	}


}
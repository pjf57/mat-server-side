package com.cs.fwk.test.util;


import com.cs.fwk.util.Conversion;

import junit.framework.TestCase;


public class ConversionTest extends TestCase {

	public void testFetIntFromBytes() {
		byte[] data = new byte[4];
		
		data[0] = (byte) 0x00;
		data[1] = (byte) 0x80;
		data[2] = (byte) 0x45;
		data[3] = (byte) 0x31;
		int bitmap = Conversion.getIntFromBytes(data,0,4);
		assertEquals(bitmap,0x00804531);
		convertBitmapToElementSet(bitmap);

		data[0] = (byte) 0x80;
		data[1] = (byte) 0x00;
		data[2] = (byte) 0x00;
		data[3] = (byte) 0x02;
		bitmap = Conversion.getIntFromBytes(data,0,4);
		assertEquals(bitmap,0x80000002);
		convertBitmapToElementSet(bitmap);

	}

	private void convertBitmapToElementSet(int takers) {
		int t = takers;
		int id = 0;
		System.out.print("Converting " + Conversion.toHexIntString(takers));
		while (id < 32) {
			if ( (t & 1) != 0) {
				System.out.print(" " + id);
			}
			id++;
			t = t >>> 1;
		}
		System.out.println("");
	}


}

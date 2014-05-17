package com.cs.fwk.sim.test.bricks;

import com.cs.fwk.sim.bricks.NlFinder;
import com.cs.fwk.sim.bricks.NlFinder.Result;

import junit.framework.TestCase;

public class NLFinderTest extends TestCase {
	
	public void testFromMidPt() {
		NlFinder f = new NlFinder();
		int[] pts = new int[] {200,500,600,700,1000,1100,1200,1500,1700,2000,2100,2200,3000,3100,3200};
		Result r1 = f.find(2150, 1100, 5, pts);
		assertEquals(10,r1.idxL);
		assertEquals(11,r1.idxH);
		Result r2 = f.find(1500, 1100, 5, pts);
		assertEquals(7,r2.idxL);
		assertEquals(7,r2.idxH);
		Result r3 = f.find(3150, 1100, 5, pts);
		assertEquals(13,r3.idxL);
		assertEquals(14,r3.idxH);
		Result r4 = f.find(650, 1100, 5, pts);
		assertEquals(2,r4.idxL);
		assertEquals(3,r4.idxH);
		Result r5 = f.find(200, 1100, 5, pts);
		assertEquals(0,r5.idxL);
		assertEquals(1,r5.idxH);
		Result r6 = f.find(201, 1100, 5, pts);
		assertEquals(0,r6.idxL);
		assertEquals(1,r6.idxH);
		Result r7 = f.find(1100, 1100, 5, pts);
		assertEquals(4,r7.idxL);
		assertEquals(5,r7.idxH);
	}

	public void testFromMidPtMid() {
		NlFinder f = new NlFinder();
		int[] pts = new int[] {200,500,600,700,1000,1100,1200,1500,1700,2000,2100,2200,3000,3100,3200};
		Result r1 = f.find(2150, 1150, 5, pts);
		assertEquals(10,r1.idxL);
		assertEquals(11,r1.idxH);
		Result r2 = f.find(1500, 1150, 5, pts);
		assertEquals(7,r2.idxL);
		assertEquals(7,r2.idxH);
		Result r3 = f.find(3150, 1150, 5, pts);
		assertEquals(13,r3.idxL);
		assertEquals(14,r3.idxH);
		Result r4 = f.find(650, 1150, 5, pts);
		assertEquals(2,r4.idxL);
		assertEquals(3,r4.idxH);
		Result r5 = f.find(200, 1150, 5, pts);
		assertEquals(0,r5.idxL);
		assertEquals(1,r5.idxH);
		Result r6 = f.find(201, 1150, 5, pts);
		assertEquals(0,r6.idxL);
		assertEquals(1,r6.idxH);
		Result r7 = f.find(1100, 1150, 5, pts);
		assertEquals(4,r7.idxL);
		assertEquals(5,r7.idxH);
	}

	
	public void testFromLow() {
		NlFinder f = new NlFinder();
		int[] pts = new int[] {200,500,600,700,1000,1100,1200,1500,1700,2000,2100,2200,3000,3100,3200};
		Result r1 = f.find(2150, 200, 0, pts);
		assertEquals(10,r1.idxL);
		assertEquals(11,r1.idxH);
		Result r2 = f.find(1500, 200, 0, pts);
		assertEquals(7,r2.idxL);
		assertEquals(7,r2.idxH);
		Result r3 = f.find(3150, 200, 0, pts);
		assertEquals(13,r3.idxL);
		assertEquals(14,r3.idxH);
		Result r4 = f.find(650, 200, 0, pts);
		assertEquals(2,r4.idxL);
		assertEquals(3,r4.idxH);
		Result r5 = f.find(200, 200, 0, pts);
		assertEquals(0,r5.idxL);
		assertEquals(0,r5.idxH);
		Result r6 = f.find(201, 200, 0, pts);
		assertEquals(0,r6.idxL);
		assertEquals(1,r6.idxH);
		Result r7 = f.find(1100, 200, 0, pts);
		assertEquals(5,r7.idxL);
		assertEquals(5,r7.idxH);
	}
	
	public void testFromHigh() {
		NlFinder f = new NlFinder();
		int[] pts = new int[] {200,500,600,700,1000,1100,1200,1500,1700,2000,2100,2200,3000,3100,3200};
		Result r1 = f.find(2150, 3200, 14, pts);
		assertEquals(10,r1.idxL);
		assertEquals(11,r1.idxH);
		Result r2 = f.find(1500, 3200, 14, pts);
		assertEquals(7,r2.idxL);
		assertEquals(7,r2.idxH);
		Result r3 = f.find(3150, 3200, 14, pts);
		assertEquals(13,r3.idxL);
		assertEquals(14,r3.idxH);
		Result r4 = f.find(650, 3200, 14, pts);
		assertEquals(2,r4.idxL);
		assertEquals(3,r4.idxH);
		Result r5 = f.find(200, 3200, 14, pts);
		assertEquals(0,r5.idxL);
		assertEquals(1,r5.idxH);
		Result r6 = f.find(201, 3200, 14, pts);
		assertEquals(0,r6.idxL);
		assertEquals(1,r6.idxH);
		Result r7 = f.find(1100, 3200, 14, pts);
		assertEquals(5,r7.idxL);
		assertEquals(5,r7.idxH);
	}

	public void testLong() {
		NlFinder f = new NlFinder();
		int[] pts = new int[256];
		int v = 200;
		for (int x=0; x<256; x++) {
			v += x;
			if ((x % 10) == 0) {
				v += 100;
			}
			pts[x] = v;
		}
		System.out.println("max=" + v);
		int xx = 35;
		v = pts[xx];
		
		Result r1 = f.find(2150, v, xx, pts);
		System.out.println("val = " + pts[r1.idxL] + "," + pts[r1.idxH]);
		assertEquals(51,r1.idxL);
		assertEquals(52,r1.idxH);

		Result r2 = f.find(34002, v, xx, pts);
		System.out.println("val = " + pts[r2.idxL] + "," + pts[r2.idxH]);	
		assertEquals(249,r2.idxL);
		assertEquals(250,r2.idxH);

		Result r3 = f.find(210, v, xx, pts);
		System.out.println("val = " + pts[r3.idxL] + "," + pts[r3.idxH]);	
		assertEquals(0,r3.idxL);
		assertEquals(1,r3.idxH);

		Result r4 = f.find(50, v, xx, pts);
		System.out.println("val = " + pts[r4.idxL] + "," + pts[r4.idxH]);	
		assertEquals(0,r4.idxL);
		assertEquals(1,r4.idxH);
}
	

}

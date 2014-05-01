package com.pjf.mat.sim.bricks;

import com.pjf.mat.sim.bricks.NlFinder.Result;

import junit.framework.TestCase;

public class NLFinderTest extends TestCase {
	
	public void test1() {
		NlFinder f = new NlFinder();
		int[] pts = new int[] {200,500,600,700,1000,1100,1200,1500,1700,2000,2100,2200,3000,3100,3200};
		Result r = f.find(2150, 1100, 5, pts);
		assertEquals(10,r.idxL);
		assertEquals(11,r.idxH);
	}

}

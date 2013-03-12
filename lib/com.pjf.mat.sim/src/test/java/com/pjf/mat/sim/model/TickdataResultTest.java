package com.pjf.mat.sim.model;

import junit.framework.TestCase;

public class TickdataResultTest extends TestCase {
	
	public void testInvalid() {
		TickdataResult trd = new TickdataResult(5);
		assertFalse (trd.isValid());
	}

	public void testValid() {
		TickdataResult trd = new TickdataResult(0x123456789abcdef0L,4);
		assertTrue(trd.isValid());
		assertEquals(trd.getMicrotickDelay(),4);
		assertEquals(trd.getRawData(),0x123456789abcdef0L);
	}

}

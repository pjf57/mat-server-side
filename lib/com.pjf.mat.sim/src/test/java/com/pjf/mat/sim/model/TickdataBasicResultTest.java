package com.pjf.mat.sim.model;

import com.pjf.mat.sim.types.TickRefData;
import com.pjf.mat.util.data.TickData;

import junit.framework.TestCase;

public class TickdataBasicResultTest extends TestCase {
	
	public void testInvalid() {
		TickDataBasicResult trd = new TickDataBasicResult(5);
		assertFalse (trd.isValid());
	}

	public void testValid() throws Exception {
		TickData d = new TickData(3,"IBM     ",5.21f,300f);
		TickRefData td = new TickRefData(0x15,0x07,0x12,d);
		TickDataBasicResult trd = new TickDataBasicResult(td,4);
		assertTrue(trd.isValid());
		assertEquals(trd.getMicrotickDelay(),4);
		assertEquals(trd.getRawData(),0x030712150000012cL);
	}

}

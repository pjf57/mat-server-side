package com.cs.fwk.sim.test.model;


import com.cs.fwk.sim.model.TickDataBasicResult;
import com.cs.fwk.sim.types.TickRefData;
import com.cs.fwk.util.data.TickData;

import junit.framework.TestCase;

public class TickdataBasicResultTest extends TestCase {
	
	public void testInvalid() {
		TickDataBasicResult trd = new TickDataBasicResult(5);
		assertFalse (trd.isValid());
	}

	public void testValid() throws Exception {
		int evt = 3;
		String sym = "IBM     ";
		float price = 5.21f;
		float vol = 300f;
		int tickref = 0x15;
		int mktId = 7;
		int instrID = 0x12;
		TickData d = new TickData(evt,sym,price,vol);
		TickRefData td = new TickRefData(tickref,mktId,instrID,d);
		TickDataBasicResult trd = new TickDataBasicResult(td,4);
		assertTrue(trd.isValid());
		assertEquals(trd.getMicrotickDelay(),4);
		assertEquals(trd.getRawData(),0x030712150000012cL);
	}

	public void testValues() throws Exception {
		int evt = 3;
		String sym = "IBM     ";
		float price = 5.21f;
		float vol = 300f;
		int tickref = 0x15;
		int mktId = 7;
		int instrID = 0x12;
		TickData d = new TickData(evt,sym,price,vol);
		TickRefData td = new TickRefData(tickref,mktId,instrID,d);
		TickDataBasicResult trd = new TickDataBasicResult(td,4);
		assertTrue(trd.isValid());
		assertEquals(trd.getInstrumentId(),0x12);
		assertEquals(trd.getMktId(),7);
		assertEquals(trd.getVolumeInt(),300);
		assertEquals(trd.getEvt().getIntCode(),3);
		assertEquals(trd.getInstrumentId(),0x12);
	}

}

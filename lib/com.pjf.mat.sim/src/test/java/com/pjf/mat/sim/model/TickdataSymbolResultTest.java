package com.pjf.mat.sim.model;

import com.pjf.mat.sim.types.TickRefData;
import com.pjf.mat.util.data.TickData;

import junit.framework.TestCase;

public class TickdataSymbolResultTest extends TestCase {
	
	public void testInvalid() {
		TickDataSymbolResult trd = new TickDataSymbolResult(5);
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
		TickDataSymbolResult trd = new TickDataSymbolResult(td,4);
		assertTrue(trd.isValid());
		assertEquals(trd.getMicrotickDelay(),4);
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
		TickDataSymbolResult trd = new TickDataSymbolResult(td,4);
		assertTrue(trd.isValid());
		assertEquals("IBM     ",trd.getSymbol());
	}

}

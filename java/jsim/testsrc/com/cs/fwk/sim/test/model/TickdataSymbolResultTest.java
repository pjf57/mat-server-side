package com.cs.fwk.sim.test.model;



import com.cs.fwk.sim.model.TickDataSymbolResult;
import com.cs.fwk.sim.types.TickRefData;
import com.cs.fwk.util.data.TickData;

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

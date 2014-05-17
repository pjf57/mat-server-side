package com.cs.fwk.sim.model;

import com.cs.fwk.sim.types.TickRefData;
import com.cs.fwk.util.Conversion;

public class TickDataSymbolResult extends TickdataResult {

	/**
	 * Create an invalid result
	 */
	public TickDataSymbolResult(int microtickDelay) {
		super(microtickDelay);
	}

	/**
	 * Create valid result from TickRefData
	 * 
	 * @param data - tickref data
	 * @param microtickDelay
	 * @throws Exception 
	 */
	public TickDataSymbolResult(TickRefData data, int microtickDelay) throws Exception {
		super(Conversion.stringToRaw(data.getSymbol()),	microtickDelay);
	}

	public String getSymbol() {
		StringBuffer buf = new StringBuffer();
		long d = getRawData();
		buf.append((char) ((d >> 56) & 0xff));
		buf.append((char) ((d >> 48) & 0xff));
		buf.append((char) ((d >> 40) & 0xff));
		buf.append((char) ((d >> 32) & 0xff));
		buf.append((char) ((d >> 24) & 0xff));
		buf.append((char) ((d >> 16) & 0xff));
		buf.append((char) ((d >> 8) & 0xff));
		buf.append((char) (d & 0xff));
		return buf.toString();
	}
		
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer(super.toString());
		buf.append(" symbol=[" + getSymbol() + "]");
		return buf.toString();
	}

}

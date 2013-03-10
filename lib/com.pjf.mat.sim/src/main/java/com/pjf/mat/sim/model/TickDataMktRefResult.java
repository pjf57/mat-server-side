package com.pjf.mat.sim.model;

import com.pjf.mat.sim.types.TickRefData;
import com.pjf.mat.util.Conversion;

public class TickDataMktRefResult extends TickdataResult {

	/**
	 * Create an invalid result
	 */
	public TickDataMktRefResult(int microtickDelay) {
		super(microtickDelay);
	}

	/**
	 * Create valid result from TickRefData
	 * 
	 * @param data - tickref data
	 * @param microtickDelay
	 * @throws Exception 
	 */
	public TickDataMktRefResult(TickRefData data, int microtickDelay) throws Exception {
		super(Conversion.stringToRaw(data.getMktRef()),	microtickDelay);
	}

	public String getMktRef() {
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
	
	

}

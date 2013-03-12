package com.pjf.mat.sim.model;

import com.pjf.mat.util.Conversion;

public class TickdataResult {
	private final boolean valid;
	private final long rawData;
	private final int microtickDelay;	// number of microticks delay to be modelled

	/**
	 * Create an invalid result
	 */
	public TickdataResult(int microtickDelay) {
		this.rawData = 0;
		this.valid = false;
		this.microtickDelay = microtickDelay;
	}

	/**
	 * Create valid result from a long
	 * 
	 * @param data - raw long data
	 * @param microtickDelay
	 */
	public TickdataResult(long data, int microtickDelay) {
		this.rawData = data;
		this.valid = true;
		this.microtickDelay = microtickDelay;
	}
		
	
	public long getRawData() {
		return rawData;
	}
	
	
	public boolean isValid() {
		return valid;
	}
	

	public int getMicrotickDelay() {
		return microtickDelay;
	}
	
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("validity=" + valid + " ");
		if (valid) {
			buf.append(Conversion.toHexIntString((int)((rawData/4294967296L)&0xffffffff)));
			buf.append(Conversion.toHexIntString((int)(rawData&0xffffffff)));
		}
		return buf.toString();
	}

}

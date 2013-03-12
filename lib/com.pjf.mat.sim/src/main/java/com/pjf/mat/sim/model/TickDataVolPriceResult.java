package com.pjf.mat.sim.model;

import com.pjf.mat.sim.types.TickRefData;

public class TickDataVolPriceResult extends TickdataResult {

	/**
	 * Create an invalid result
	 */
	public TickDataVolPriceResult(int microtickDelay) {
		super(microtickDelay);
	}

	/**
	 * Create valid result from TickRefData
	 * 
	 * vol/price 	- vol-sp32/price.sp32
	 * 
	 * @param data - tickref data
	 * @param microtickDelay
	 */
	public TickDataVolPriceResult(TickRefData data, int microtickDelay) {
		super(	(((long)((Float.floatToRawIntBits(data.getVolume())))<<32) & 0xffffffff00000000L) |
				(((long)Float.floatToRawIntBits(data.getPrice()))),
				microtickDelay);
	}

	public float getPrice() {
		return Float.intBitsToFloat((int) (getRawData() & 0xffffffff)) ;
	}

	public float getVolume() {
		return Float.intBitsToFloat((int) ((getRawData()>>32) & 0xffffffff)) ;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer(super.toString());
		buf.append(" price=" + getPrice());
		buf.append(" volume=" + getVolume());
		return buf.toString();
	}

}

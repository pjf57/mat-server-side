package com.pjf.mat.sim.model;

import com.pjf.mat.sim.types.TickRefData;
import com.pjf.mat.util.data.MarketEventType;

public class TickDataBasicResult extends TickdataResult {

	/**
	 * Create an invalid result
	 */
	public TickDataBasicResult(int microtickDelay) {
		super(microtickDelay);
	}

	/**
	 * Create valid result from TickRefData
	 * 
	 * type.8/mktref.8/instr.8/tickref.8/volume.u32
	 * 
	 * @param data - tickref data
	 * @param microtickDelay
	 */
	public TickDataBasicResult(TickRefData data, int microtickDelay) {
		super(	(((long)(data.getEvt().getIntCode()))<<56) |
				(((long)(data.getMktId())<<48) & 0x00ff000000000000L) |
				(((long)(data.getInstrumentId())<<40) & 0x0000ff0000000000L) |
				(((long)(data.getTickref())<<32) & 0x000000ff00000000L) |
				(((long)data.getVolume())),
				microtickDelay);
	}

	public MarketEventType getEvt() {
		return new MarketEventType((byte)(getRawData()>>56) & 0xff);
	}
	
	public int getMktId() {
		return (int) (getRawData()>>40) & 0xff;
	}

	public int getInstrumentId() {
		return (int) (getRawData()>>40) & 0xff;
	}

	public int getVolumeInt() {
		return (int) getRawData() & 0xffffffff;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer(super.toString());
		buf.append(" Evt=" + getEvt());
		buf.append(" MktId=" + getMktId());
		buf.append(" instr=" + getInstrumentId());
		buf.append(" vol=" + getVolumeInt());
		return buf.toString();
	}
}

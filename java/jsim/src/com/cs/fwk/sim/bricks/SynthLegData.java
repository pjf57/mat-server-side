package com.cs.fwk.sim.bricks;

import com.cs.fwk.util.Conversion;

/**
 * Class to hold data for one leg of a synthetic instrument
 * 
 * @author pjf
 *
 */
public class SynthLegData {
	private boolean valid;
	private int instrId;
	private int tickref;
	private int rawData;
	
	public SynthLegData(int instrId, int tickref, int rawData) {
		this.instrId = instrId;
		this.tickref = tickref;
		this.rawData = rawData;
		this.valid = true;
	}
	
	public void setValue(int tickref, int rawData) {
		this.tickref = tickref;
		this.rawData = rawData;
		this.valid = true;				
	}

	public boolean isValid() {
		return valid;
	}

	public int getInstrId() {
		return instrId;
	}

	public int getTickref() {
		return tickref;
	}

	public int getRawData() {
		return rawData;
	}

	@Override
	public String toString() {
		return "[valid=" + valid + ", instrId=" + instrId
				+ ", tickref=" + tickref + ", rawData=" + Conversion.toHexIntString(rawData) + "]";
	}

	
}

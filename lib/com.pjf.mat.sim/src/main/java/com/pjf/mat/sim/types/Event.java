package com.pjf.mat.sim.types;

import com.pjf.mat.util.Conversion;

/**
 * Event data passed between elements
 * 
 * @author pjf
 *
 */
public class Event {
	private final int rawData;
	private final int src;
	private final int instrument_id;
	
	public Event(int src, int instrument_id, int rawData) {
		this.rawData = rawData;
		this.src = src;
		this.instrument_id = instrument_id;
	}

	public Event(int src, int instrument_id, float data) {
		this.rawData = Float.floatToIntBits(data);
		this.src = src;
		this.instrument_id = instrument_id;
	}

	public int getRawData() {
		return rawData;
	}

	public float getFloatData() {
		return Float.intBitsToFloat(rawData);
	}

	public int getSrc() {
		return src;
	}

	public int getInstrument_id() {
		return instrument_id;
	}
	
	@Override
	public String toString() {
		return "[src" + src + 
		",InstrId=" + instrument_id + 
		",data=" + Conversion.toHexIntString(rawData) + "]";
	}
	
	
}

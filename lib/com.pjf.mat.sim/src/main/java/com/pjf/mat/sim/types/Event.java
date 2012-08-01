package com.pjf.mat.sim.types;

import com.pjf.mat.api.Timestamp;
import com.pjf.mat.util.Conversion;

/**
 * Event data passed between elements
 * 
 * @author pjf
 *
 */
public class Event implements Comparable<Event> {
	private final int rawData;
	private final int src;
	private final int instrument_id;
	private final boolean isFloat;
	private Timestamp timestamp;
	private int tag;
	
	public Event(Timestamp timestamp, int src, int instrument_id, int rawData) {
		this.rawData = rawData;
		this.src = src;
		this.instrument_id = instrument_id;
		this.isFloat = false;
		this.timestamp = timestamp;
		tag = -1;
	}

	public Event(Timestamp timestamp, int src, int instrument_id, float data) {
		this.rawData = Float.floatToIntBits(data);
		this.src = src;
		this.instrument_id = instrument_id;
		this.isFloat = true;
		this.timestamp = timestamp;
		tag = -1;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}
	
	public Timestamp getTimestamp() {
		return timestamp;
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
		return "[tag=" + tag +
		" ts=" + timestamp + 
		" src" + src + 
		",InstrId=" + instrument_id + 
		",data=" + ((isFloat) ? getFloatData() : Conversion.toHexIntString(rawData)) + "]";
	}

	@Override
	public int compareTo(Event o) {
		return timestamp.compareTo(o.timestamp);
	}

	public void setTimestamp(Timestamp newTime) {
		this.timestamp = newTime;		
	}
	
	
}

package com.pjf.mat.sim.model;

import com.pjf.mat.sim.types.FloatValue;
import com.pjf.mat.sim.types.IntValue;
import com.pjf.mat.util.Conversion;

public class LookupResult {
	private final int responderId;		// id of element that responded
	private final int rawData;
	private final LookupValidity validity;
	private final int microtickDelay;	// number of microticks delay to be modelled
	
	public LookupResult(int id, IntValue data, int microtickDelay) {
		this.responderId = id;
		this.rawData = data.getRawData();
		this.validity = (data.isValid()) ? LookupValidity.OK : LookupValidity.NODATA;
		this.microtickDelay = microtickDelay;
	}
	
	public LookupResult(int id, FloatValue data, int microtickDelay) {
		this.responderId = id;
		this.rawData = data.getRawData();
		this.validity = (data.isValid()) ? LookupValidity.OK : LookupValidity.NODATA;
		this.microtickDelay = microtickDelay;
	}
	
	public LookupResult(int id, LookupValidity validity, int microtickDelay) {
		this.responderId = id;
		this.rawData = 0;
		this.validity = validity;
		this.microtickDelay = microtickDelay;
	}
	
	public int getIntData() {
		return rawData;
	}
	
	public float getFloatData() {
		return Float.intBitsToFloat(rawData);
	}
	
	public boolean isValid() {
		return validity.equals(LookupValidity.OK);
	}
	
	public LookupValidity getValidity() {
		return validity;
	}

	public int getMicrotickDelay() {
		return microtickDelay;
	}
	
	public FloatValue getFloatValue() {
		FloatValue val = new FloatValue();
		if (validity.equals(LookupValidity.OK)) {
			val.set(getFloatData());
		}
		return val;
	}
	
	public int getResponder() {
		return responderId;
	}
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("id=" + responderId + " ");
		buf.append("validity=" + validity + " ");
		if (validity.equals(LookupValidity.OK)) {
			buf.append(Conversion.toHexIntString(rawData));
			buf.append( " / ");
			buf.append(Float.intBitsToFloat(rawData));
		}
		return buf.toString();
	}
}

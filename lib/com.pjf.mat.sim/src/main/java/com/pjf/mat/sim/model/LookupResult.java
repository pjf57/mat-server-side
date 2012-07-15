package com.pjf.mat.sim.model;

import com.pjf.mat.sim.types.FloatValue;
import com.pjf.mat.sim.types.IntValue;

public class LookupResult {
	private final int rawData;
	private final LookupValidity validity;
	
	public LookupResult(IntValue data) {
		this.rawData = data.getRawData();
		this.validity = (data.isValid()) ? LookupValidity.OK : LookupValidity.NODATA;
	}
	
	public LookupResult(FloatValue data) {
		this.rawData = data.getRawData();
		this.validity = (data.isValid()) ? LookupValidity.OK : LookupValidity.NODATA;
	}
	
	public LookupResult(LookupValidity validity) {
		this.rawData = 0;
		this.validity = validity;
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
	

}

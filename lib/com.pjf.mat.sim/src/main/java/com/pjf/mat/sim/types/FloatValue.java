package com.pjf.mat.sim.types;

import com.pjf.mat.sim.model.Value;

public class FloatValue extends BaseValue implements Value {
	private float data;
	
	public FloatValue() {
		super();
		data = 0.0f;
	}

	public FloatValue(float val) {
		super();
		valid = true;
		data = val;
	}
	
	public void set(float val) {
		valid = true;
		data = val;
	}
	
	public float getValue() {
		return data;
	}

	@Override
	public int getRawData() {
		return Float.floatToIntBits(data);
	}

	@Override
	public String toString() {
		if (valid) {
			return Float.toString(data);
		}
		return "invalid";
	}

}

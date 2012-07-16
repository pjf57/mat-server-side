package com.pjf.mat.sim.types;

import com.pjf.mat.sim.model.Value;

public class IntValue extends BaseValue implements Value {
	private int data;
	
	public IntValue() {
		super();
		data = 0;
	}

	public IntValue(int val) {
		super();
		valid = true;
		data = val;
	}

	public void set(int val) {
		valid = true;
		data = val;
	}

	public int getValue() {
		return data;
	}

	@Override
	public int getRawData() {
		return data;
	}

	@Override
	public String toString() {
		if (valid) {
			return Integer.toString(data);
		}
		return "invalid";
	}

}

package com.pjf.mat.sim.types;

import com.pjf.mat.sim.model.Value;

public class BooleanValue extends BaseValue implements Value {
	private boolean data;
	
	public BooleanValue() {
		super();
		data = false;
	}

	public BooleanValue(boolean val) {
		super();
		data = val;
	}

	public void set(boolean val) {
		valid = true;
		data = val;
	}

	public boolean getValue() {
		return data;
	}

	@Override
	public int getRawData() {
		return (data) ? 1 : 0;
	}


}

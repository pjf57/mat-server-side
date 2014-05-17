package com.cs.fwk.sim.types;

import com.cs.fwk.sim.model.Value;

public abstract class BaseValue implements Value {
	protected boolean valid;
	
	public BaseValue() {
		valid = false;
	}

	@Override
	public boolean isValid() {
		return valid;
	}

}

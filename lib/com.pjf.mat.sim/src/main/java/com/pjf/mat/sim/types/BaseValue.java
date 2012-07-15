package com.pjf.mat.sim.types;

import com.pjf.mat.sim.model.Value;

public abstract class BaseValue implements Value {
	protected boolean valid;
	
	public BaseValue() {
		valid = false;
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return valid;
	}

}

package com.cs.fwk.sim.types;

import com.cs.fwk.sim.model.Value;
import com.cs.fwk.util.Conversion;

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
			return Float.toString(data) + " [" + 
				Conversion.toHexIntString(Float.floatToIntBits(data))+ "]" ;
		}
		return "invalid";
	}

	public FloatValue subtract(FloatValue s) {
		FloatValue fv = new FloatValue();
		if (isValid()  &&  s.isValid()) {
			fv.set(getValue() - s.getValue());
		}
		return fv;
	}

}

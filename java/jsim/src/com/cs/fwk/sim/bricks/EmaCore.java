package com.cs.fwk.sim.bricks;

import com.cs.fwk.sim.types.FloatValue;
import com.cs.fwk.api.MatElementDefs;

/**
 * Implements basic EMA logic, supporting parallel calculation of sequences
 * for different instruments
 * 
 * @author pjf
 *
 */
public class EmaCore {
	private float alpha;		// exp coefficient
	private int len;			// length of sequence
	private float[] last;
	private int[] length;

	public EmaCore() {
		last = new float[MatElementDefs.MAX_INSTRUMENTS];
		length = new int[MatElementDefs.MAX_INSTRUMENTS];
		for (int instr=0; instr<MatElementDefs.MAX_INSTRUMENTS; instr++) {
			last[instr] = 0.0f;
			length[instr] = 0;
		}
	}
	
	public void setParameters(float alpha, int len) {
		this.alpha = alpha;
		this.len = len;
	}
	
	/**
	 * Process an event into the EMA, returning either a valid or invalid result
	 * depending on whether he have had sufficient events on that instrument
	 * 
	 * @param instrumentId
	 * @param data
	 * @return output point - valid or invalid
	 */
	public FloatValue processEvent(int instrumentId, float data) {
		FloatValue output = new FloatValue();
		float nextVal = (alpha * (data - last[instrumentId])) + last[instrumentId];
		last[instrumentId] = nextVal;
		if (length[instrumentId] >= len-1) {
			// only put output if had sufficient values
			output.set(nextVal);
		} else {
			length[instrumentId]++;
		}		
		return output;
	}

}

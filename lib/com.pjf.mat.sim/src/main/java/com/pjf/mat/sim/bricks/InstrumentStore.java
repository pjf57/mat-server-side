package com.pjf.mat.sim.bricks;

import org.apache.log4j.Logger;

import com.pjf.mat.api.MatElementDefs;
import com.pjf.mat.sim.types.FloatValue;

/**
 * Stores a single value with validity flags for each instrument
 * 
 * @author pjf
 *
 */
public class InstrumentStore {
	private final static Logger logger = Logger.getLogger(InstrumentStore.class);
	private final String name;
	private FloatValue[] values;
	
	public InstrumentStore(String name) {
		this.name = name;
		this.values = new FloatValue[MatElementDefs.MAX_INSTRUMENTS];
		for (int instr=0; instr<MatElementDefs.MAX_INSTRUMENTS; instr++) {
			values[instr] = new FloatValue();
		}
	}
	
	public void put(float value, int instrumentId) throws Exception {
		checkInstrumentId(instrumentId);
		FloatValue val = new FloatValue(value);
		values[instrumentId] = val;
	}

	public FloatValue get(int instrumentId) throws Exception {
		checkInstrumentId(instrumentId);
		return values[instrumentId];
	}

	private void checkInstrumentId(int instrumentId) throws Exception {
		if ((instrumentId < 0)  ||  (instrumentId >= MatElementDefs.MAX_INSTRUMENTS)) {
			String msg = "InstrumentId [" + instrumentId + "] out of range in " +
			name + " instrument store.";
			logger.error(msg);
			throw new Exception(msg);
		}
	}
	
	@Override
	public String toString() {
		return name + " instrument store";
	}

	public void logState() {
		StringBuffer buf = new StringBuffer();
		buf.append(name + ":) ");
		for (int instr=0; instr<MatElementDefs.MAX_INSTRUMENTS; instr++) {
			if (values[instr].isValid()) {
				buf.append(instr);
				buf.append("/");
				buf.append(values[instr]);
				buf.append(" ");
			}
		}
		logger.debug(buf);
	}

}

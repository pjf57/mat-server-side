package com.pjf.mat.sim.element;

import org.apache.log4j.Logger;

import com.pjf.mat.api.MatElementDefs;
import com.pjf.mat.sim.model.BaseElement;
import com.pjf.mat.sim.model.SimElement;
import com.pjf.mat.sim.model.SimHost;
import com.pjf.mat.sim.types.ConfigItem;
import com.pjf.mat.sim.types.Event;

/**
 * Implements Exponential Moving Average
 * 
 * @author pjf
 *
 */
public class EMA extends BaseElement implements SimElement {
	private final static Logger logger = Logger.getLogger(EMA.class);
	private float c_alpha;		// exp coefficient
	private int c_len;			// length of sequence
	private float[] last;
	private int[] length;
	
	public EMA(int id, SimHost host) {
		super(id, MatElementDefs.EL_TYP_EMA,host);
		last = new float[MatElementDefs.MAX_INSTRUMENTS];
		length = new int[MatElementDefs.MAX_INSTRUMENTS];
		for (int instr=0; instr<MatElementDefs.MAX_INSTRUMENTS; instr++) {
			last[instr] = 0.0f;
			length[instr] = 0;
		}
	}


	@Override
	protected void processConfig(ConfigItem cfg) {
		switch (cfg.getItemId()) {
		case MatElementDefs.EL_EMA_C_ALPHA: c_alpha = cfg.getFloatData();	break;
		case MatElementDefs.EL_EMA_C_LEN: c_len = cfg.getRawData();			break;
		default: logger.warn(getIdStr() + "Unexpected configuration: " + cfg); break;
		}
	}

	@Override
	protected void processEvent(int input, Event evt) {
		int instr = evt.getInstrument_id();
		float nextVal = (c_alpha * (evt.getFloatData() - last[instr])) + last[instr];
		last[instr] = nextVal;
		if (length[instr] >= c_len) {
			// only put event out if had sufficient values
			Event evtOut = new Event(elementId,instr,nextVal);
			host.publishEvent(evtOut);
		} else {
			length[instr]++;
		}
	}

	@Override
	protected String getTypeName() {
		return "EMA";
	}

}

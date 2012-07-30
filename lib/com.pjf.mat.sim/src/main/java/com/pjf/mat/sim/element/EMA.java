package com.pjf.mat.sim.element;

import org.apache.log4j.Logger;

import com.pjf.mat.api.MatElementDefs;
import com.pjf.mat.api.Timestamp;
import com.pjf.mat.sim.bricks.BaseElement;
import com.pjf.mat.sim.bricks.EmaLogic;
import com.pjf.mat.sim.model.SimElement;
import com.pjf.mat.sim.model.SimHost;
import com.pjf.mat.sim.types.ConfigItem;
import com.pjf.mat.sim.types.Event;
import com.pjf.mat.sim.types.FloatValue;

/**
 * Implements Exponential Moving Average
 * 
 * @author pjf
 *
 */
public class EMA extends BaseElement implements SimElement {
	private final static Logger logger = Logger.getLogger(EMA.class);
	private static final int LATENCY = 14;	// input to output latency (microticks)
	private EmaLogic ema;
	private float c_alpha;		// exp coefficient
	private int c_len;			// length of sequence
	
	public EMA(int id, SimHost host) {
		super(id, MatElementDefs.EL_TYP_EMA,host);
		ema = new EmaLogic();
	}


	@Override
	protected void processConfig(ConfigItem cfg) {
		switch (cfg.getItemId()) {
		case MatElementDefs.EL_EMA_C_ALPHA: c_alpha = cfg.getFloatData();	break;
		case MatElementDefs.EL_EMA_C_LEN: 	c_len   = cfg.getRawData();		break;
		default: logger.warn(getIdStr() + "Unexpected configuration: " + cfg); break;
		}
	}
	
	@Override
	protected void processConfigDone() {
		ema.setParameters(c_alpha, c_len);
	}

	@Override
	protected void processEvent(Timestamp ts, int input, Event evt) {
		FloatValue output = ema.processEvent(evt.getInstrument_id(),evt.getFloatData());
		if (output.isValid()) {
			Event evtOut = new Event(elementId,evt.getInstrument_id(),output.getValue());
			host.publishEvent(evtOut,LATENCY);
		}
	}

	@Override
	protected String getTypeName() {
		return "EMA";
	}

}

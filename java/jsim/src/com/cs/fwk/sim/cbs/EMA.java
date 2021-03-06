package com.cs.fwk.sim.cbs;

import org.apache.log4j.Logger;

import com.cs.fwk.sim.bricks.BaseElement;
import com.cs.fwk.sim.bricks.EmaCore;
import com.cs.fwk.sim.model.SimElement;
import com.cs.fwk.sim.model.SimHost;
import com.cs.fwk.sim.types.Event;
import com.cs.fwk.sim.types.FloatValue;
import com.cs.fwk.api.MatElementDefs;
import com.cs.fwk.api.util.ConfigItem;

/**
 * Implements Exponential Moving Average
 * 
 * @author pjf
 *
 */
public class EMA extends BaseElement implements SimElement {
	private final static Logger logger = Logger.getLogger(EMA.class);
	private static final int LATENCY = 14;	// input to output latency (microticks)
	private EmaCore ema;
	private float c_alpha;		// exp coefficient
	private int c_len;			// length of sequence
	
	public EMA(int id, SimHost host) {
		super(id, MatElementDefs.EL_TYP_EMA,host);
		ema = new EmaCore();
	}


	@Override
	protected void processConfig(ConfigItem cfg) {
		switch (cfg.getItemId()) {
		case MatElementDefs.EL_EMA_C_ALPHA: c_alpha = cfg.getFloatData();	break;
		case MatElementDefs.EL_EMA_C_LEN: 	c_len   = cfg.getRawData();		break;
		default: 
			logger.warn(getIdStr() + "Unexpected configuration: " + cfg); 
			setErrorCode(MatElementDefs.CB_EC_GEN_CFG_ERR);
			break;
		}
	}
	
	@Override
	protected void processConfigDone() {
		ema.setParameters(c_alpha, c_len);
	}

	@Override
	protected void processEvent(int input, Event evt) {
		FloatValue output = ema.processEvent(evt.getInstrument_id(),evt.getFloatData());
		if (output.isValid()) {
			Event evtOut = new Event(host.getCurrentSimTime(),elementId,evt.getInstrument_id(),
					evt.getTickref(), output.getValue());
			host.publishEvent(evtOut,LATENCY);
		}
	}

	@Override
	protected String getTypeName() {
		return "EMA";
	}

}

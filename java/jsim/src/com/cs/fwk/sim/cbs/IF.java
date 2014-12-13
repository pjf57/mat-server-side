package com.cs.fwk.sim.cbs;

import org.apache.log4j.Logger;

import com.cs.fwk.api.MatElementDefs;
import com.cs.fwk.api.util.ConfigItem;
import com.cs.fwk.sim.bricks.BaseElement;
import com.cs.fwk.sim.model.SimElement;
import com.cs.fwk.sim.model.SimHost;
import com.cs.fwk.sim.types.Event;

/**
 * Implements IF block
 * 
 * @author pjf
 *
 */
public class IF extends BaseElement implements SimElement {
	private final static Logger logger = Logger.getLogger(IF.class);
	private static final int LATENCY = 2;	// input to output latency (microticks)
	private boolean c_global;				// GLOBAL switch rather than exp coefficient
	private int[] sel_state;				// array of switch posn by instrument id
	
	public IF(int id, SimHost host) {
		super(id, MatElementDefs.EL_TYP_IF,host);
		sel_state = new int[256];
	}


	@Override
	protected void processConfig(ConfigItem cfg) {
		switch (cfg.getItemId()) {
		case MatElementDefs.EL_IF_C_CTRL: c_global = ((cfg.getRawData() & 0x0001) == 1);	break;
		default: 
			logger.warn(getIdStr() + "Unexpected configuration: " + cfg); 
			setErrorCode(MatElementDefs.CB_EC_GEN_CFG_ERR);
			break;
		}
	}

	@Override
	protected void processConfigDone() {
		for (int i=0; i<256; i++) {
			sel_state[i] = 0;
		}
	}
	

	@Override
	protected void processEvent(int input, Event evt) {
		int instr = evt.getInstrument_id();
		int data = evt.getRawData();
		int posn = 0;
		if (c_global) {
			posn = sel_state[0];
		} else {
			posn = sel_state[instr];
		}
		logger.info("evt: ip=" + input + " instr=" + instr + " posn=" + posn);

		switch(input) {

		case 1:
			// set new switch posn
			int setPosn = 1;
			if (data != 0) {
				setPosn = 2;
			}
			if (c_global) {
				sel_state[0] = setPosn;
			} else {
				sel_state[instr] = setPosn;
			}
			break;
			
		case 2:
			// process ip A
			if (posn == 1) {
				Event evtOut = new Event(host.getCurrentSimTime(),elementId,evt.getInstrument_id(),
						evt.getTickref(), evt.getFloatData());
				host.publishEvent(evtOut,LATENCY);
			}
			break;
			
		case 3:
			// process ip B
			if (posn == 2) {
				Event evtOut = new Event(host.getCurrentSimTime(),elementId,evt.getInstrument_id(),
						evt.getTickref(), evt.getFloatData());
				host.publishEvent(evtOut,LATENCY);
			}

		}
	}

	@Override
	protected String getTypeName() {
		return "IF";
	}

}

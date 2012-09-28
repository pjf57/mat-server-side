package com.pjf.mat.sim.element;

import org.apache.log4j.Logger;

import com.pjf.mat.api.MatElementDefs;
import com.pjf.mat.sim.bricks.BaseElement;
import com.pjf.mat.sim.bricks.EmaCore;
import com.pjf.mat.sim.model.SimElement;
import com.pjf.mat.sim.model.SimHost;
import com.pjf.mat.sim.types.ConfigItem;
import com.pjf.mat.sim.types.Event;
import com.pjf.mat.sim.types.FloatValue;

/**
 * Implements MACD - Moving Average Convergence Divergence with selectable output
 * 
 * 		MACD   = Fast_EMA(Price) - Slow_EMA(price) 
 *		Signal = EMA(MACD)
 *		Histogram: = MACD Line - Signal
 * 
 * @author pjf
 *
 */
public class MACD_selop extends BaseElement implements SimElement {
	private final static Logger logger = Logger.getLogger(MACD_selop.class);
	//FIXME - get correct latency
	private static final int LATENCY = 14;	// input to output latency (microticks)
	private EmaCore fastEma;
	private EmaCore slowEma;
	private EmaCore signalEma;
	private float c_fast_alpha;			// fs exp coefficient
	private int c_fast_len;				// fs length of sequence
	private float c_slow_alpha;			// sl exp coefficient
	private int c_slow_len;				// sl length of sequence
	private float c_signal_alpha;		// sg exp coefficient
	private int c_signal_len;			// sg length of sequence
	private int c_op_sel;				// which output to select
	
	public MACD_selop(int id, SimHost host) {
		super(id, MatElementDefs.EL_TYP_MACD,host);
		fastEma = new EmaCore();
		slowEma = new EmaCore();
		signalEma = new EmaCore();
	}


	@Override
	protected void processConfig(ConfigItem cfg) {
		switch (cfg.getItemId()) {
		case MatElementDefs.EL_MACD_C_FS_ALPHA: c_fast_alpha = cfg.getFloatData();	break;
		case MatElementDefs.EL_MACD_C_FS_LEN: 	c_fast_len   = cfg.getRawData();	break;
		case MatElementDefs.EL_MACD_C_SL_ALPHA: c_slow_alpha = cfg.getFloatData();	break;
		case MatElementDefs.EL_MACD_C_SL_LEN: 	c_slow_len   = cfg.getRawData();	break;
		case MatElementDefs.EL_MACD_C_SG_ALPHA: c_signal_alpha = cfg.getFloatData();break;
		case MatElementDefs.EL_MACD_C_SG_LEN: 	c_signal_len   = cfg.getRawData();	break;
		case MatElementDefs.EL_MACD_C_SET_OP: 	c_op_sel	   = cfg.getRawData();	break;
		default: logger.warn(getIdStr() + "Unexpected configuration: " + cfg); break;
		}
	}
	

	@Override
	protected void processConfigDone() {
		fastEma.setParameters(c_fast_alpha, c_fast_len);
		slowEma.setParameters(c_slow_alpha, c_slow_len);
		signalEma.setParameters(c_signal_alpha, c_signal_len);
	}

	@Override
	protected void processEvent(int input, Event evt) {
		FloatValue signal = new FloatValue();
		FloatValue hist = new FloatValue();
		int instr = evt.getInstrument_id();
		float ipData = evt.getFloatData();
		FloatValue macd = fastEma.processEvent(instr,ipData).subtract(slowEma.processEvent(instr,ipData));
		if (macd.isValid()) {
			logger.debug("macd is: " + macd);
			signal = signalEma.processEvent(instr, macd.getValue());
			if (signal.isValid()) {
				logger.debug("signal is: " + signal);
				hist = macd.subtract(signal);
			}
		}
		// select output
		FloatValue output = new FloatValue();
		switch (c_op_sel) {
		case 0: output = macd;		break;
		case 1: output = signal;	break;
		case 2: output = hist;		break;
		default: output = macd;		break;
		}
		if (output.isValid()) {
			Event evtOut = new Event(host.getCurrentSimTime(),elementId,evt.getInstrument_id(),output.getValue());
			host.publishEvent(evtOut,LATENCY);
		}
	}

	@Override
	protected String getTypeName() {
		return "MACD";
	}

}

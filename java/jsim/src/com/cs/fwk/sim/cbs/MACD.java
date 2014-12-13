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
 * Implements MACD - Moving Average Convergence Divergence
 * 
 * 		port 0: MACD   		= Fast_EMA(Price) - Slow_EMA(price) 
 *		port 1: Signal 		= EMA(MACD)
 *		port 2: Histogram: 	= MACD Line - Signal
 *
 *		alpha = 2 / (L + 1)
 * 
 * @author pjf
 *
 */
public class MACD extends BaseElement implements SimElement {
	private final static Logger logger = Logger.getLogger(MACD.class);
	private static final int LATENCY_MACD = 24;	// input to output latency (microticks)
	private static final int LATENCY_SIGNAL = 41;	// input to output latency (microticks)
	private static final int LATENCY_HIST = 45;	// input to output latency (microticks)
	// define output ports
	public static final int MACD_PORT = 0;
	public static final int SIGNAL_PORT = 1;
	public static final int HIST_PORT = 2;

	private EmaCore fastEma;
	private EmaCore slowEma;
	private EmaCore signalEma;
	private float c_fast_alpha;			// fs exp coefficient
	private int c_fast_len;				// fs length of sequence
	private float c_slow_alpha;			// sl exp coefficient
	private int c_slow_len;				// sl length of sequence
	private float c_signal_alpha;		// sg exp coefficient
	private int c_signal_len;			// sg length of sequence
	
	public MACD(int id, SimHost host) {
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
		default: 
			logger.warn(getIdStr() + "Unexpected configuration: " + cfg); 
			setErrorCode(MatElementDefs.CB_EC_GEN_CFG_ERR);
			break;
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
		// make outputs
		if (macd.isValid()) {
			Event evtOut = new Event(host.getCurrentSimTime(),elementId,MACD_PORT,evt.getInstrument_id(),evt.getTickref(), macd.getValue());
			publishEvent(evtOut,LATENCY_MACD);	
		}
		if (signal.isValid()) {
			Event evtOut = new Event(host.getCurrentSimTime(),elementId,SIGNAL_PORT,evt.getInstrument_id(),evt.getTickref(), signal.getValue());
			publishEvent(evtOut,LATENCY_SIGNAL);	
		}
		if (hist.isValid()) {
			Event evtOut = new Event(host.getCurrentSimTime(),elementId,HIST_PORT,evt.getInstrument_id(),evt.getTickref(), hist.getValue());
			publishEvent(evtOut,LATENCY_HIST);	
		}
	}

	@Override
	protected String getTypeName() {
		return "MACD";
	}
	
	public static float alpha(int len) {
		return 2.0f / (len + 1);
	}

}

package com.pjf.mat.sim.element;


import org.apache.log4j.Logger;
import com.pjf.mat.api.MatElementDefs;
import com.pjf.mat.sim.bricks.BaseElement;
import com.pjf.mat.sim.bricks.EmaLogic;
import com.pjf.mat.sim.bricks.InstrumentStore;
import com.pjf.mat.sim.model.LookupResult;
import com.pjf.mat.sim.model.LookupValidity;
import com.pjf.mat.sim.model.SimElement;
import com.pjf.mat.sim.model.SimHost;
import com.pjf.mat.sim.types.ConfigItem;
import com.pjf.mat.sim.types.Event;
import com.pjf.mat.sim.types.FloatValue;

/**
 *--		Implements ATR indicator
 *--		accepts events with last period high data (or no data according to config)
 *--		looks up relevant other HLOC data about the instrument and updates the
 *--		instrument's ATR value in an instrument store.
 *--		this store is accessible by lookup.
 *--		Writes an output of the ATR value for each new calculation for each instrument.
 *
 * @author pjf
 *
 */
public class ATR extends BaseElement implements SimElement {
	private final static Logger logger = Logger.getLogger(ATR.class);
	private float c_alpha;			// exp coefficient
	private int c_len;				// length of sequence
	private boolean c_ipHasCloseN1;	// indicates if input has close(N-1) value
	private EmaLogic ema;
	private InstrumentStore atrStore;	// store of ATR values
		
	public ATR(int id, SimHost host) {
		super(id, MatElementDefs.EL_TYP_ATR,host);
		atrStore = new InstrumentStore("atr");
		ema = new EmaLogic();
	}

	@Override
	protected void processConfig(ConfigItem cfg) {
		switch (cfg.getItemId()) {
		case MatElementDefs.EL_ATR_C_ALPHA: c_alpha = cfg.getFloatData();	break;
		case MatElementDefs.EL_ATR_C_LEN:   c_len   = cfg.getRawData();		break;
		case MatElementDefs.EL_ATR_C_IP_CN1: 
			c_ipHasCloseN1 = (cfg.getRawData() & 1) == 1;
			break;
		default: logger.warn(getIdStr() + "Unexpected configuration: " + cfg); break;
		}
	}

	@Override
	protected void processConfigDone() {
		ema.setParameters(c_alpha, c_len);
	}

	@Override
	protected void processEvent(int input, Event evt) throws Exception {
		int instr = evt.getInstrument_id();
		// get close(n-1), high(n), low(n)
		FloatValue closeN1;
		if (c_ipHasCloseN1) {
			closeN1 = new FloatValue(evt.getFloatData());;
		} else {
			closeN1 = lookup(instr, MatElementDefs.EL_HLOC_L_PRVM1_C).getFloatValue();
		}
		FloatValue highN = lookup(instr, MatElementDefs.EL_HLOC_L_PREV_H).getFloatValue();
		FloatValue lowN = lookup(instr, MatElementDefs.EL_HLOC_L_PREV_L).getFloatValue();
		
		logger.debug(getIdStr() + "High(N)=" + highN + " Low(N)=" + lowN + " Close(N-1)=" + closeN1);

		if (closeN1.isValid() &&  highN.isValid() && lowN.isValid()) {
			float val = Math.max(highN.getValue(), closeN1.getValue()) -
						Math.min(lowN.getValue(), closeN1.getValue());
			
			FloatValue output = ema.processEvent(instr, val);
			if (output.isValid()) {
				atrStore.put(output.getValue(), instr);
				Event evtOut = new Event(elementId,instr,output.getValue());
				host.publishEvent(evtOut);
			}
		}
	}
	

	@Override
	public LookupResult handleLookup(int instrumentId, int lookupKey) throws Exception {
		LookupResult result = new LookupResult(elementId,LookupValidity.TIMEOUT);
		switch (lookupKey) {
		case MatElementDefs.EL_ATR_L_ATR:
			result = new LookupResult(elementId,atrStore.get(instrumentId)); break;
		}
		return result;
	}

	@Override
	protected String getTypeName() {
		return "ATR";
	}

}
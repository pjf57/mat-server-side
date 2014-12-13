package com.cs.fwk.sim.cbs;


import org.apache.log4j.Logger;

import com.cs.fwk.sim.bricks.BaseElement;
import com.cs.fwk.sim.bricks.EmaCore;
import com.cs.fwk.sim.bricks.InstrumentStore;
import com.cs.fwk.sim.model.LookupResult;
import com.cs.fwk.sim.model.LookupValidity;
import com.cs.fwk.sim.model.SimElement;
import com.cs.fwk.sim.model.SimHost;
import com.cs.fwk.sim.types.Event;
import com.cs.fwk.sim.types.FloatValue;
import com.cs.fwk.api.MatElementDefs;
import com.cs.fwk.api.util.ConfigItem;


/**
 *--		Implements ADX indicator
 *--		accepts events with no data
 *--		looks up relevant other HLOC and ATR data about the instrument and updates the
 *--		instrument's ADX value in an instrument store.
 *--		this store is accessible by lookup.
 *--		Writes an output of the ADX value for each new calculation for each instrument.
 *
 * @author pjf
 *
 */
public class ADX extends BaseElement implements SimElement {
	private final static Logger logger = Logger.getLogger(ADX.class);
	private static final int LOOKUP_DLY = 2;	// lookup delay, microticks
	private static final int LATENCY = 87;	// input to output latency (microticks)
	private float c_pdn_alpha;			// PDN EMA exp coefficient
	private int c_pdn_len;				// PDN EMA length of sequence
	private float c_ndn_alpha;			// NDN EMA exp coefficient
	private int c_ndn_len;				// NDN EMA length of sequence
	private float c_adx_alpha;			// ADX EMA exp coefficient
	private int c_adx_len;				// ADX EMA length of sequence
	private EmaCore pdnEma;
	private EmaCore ndnEma;
	private EmaCore adxEma;
	private InstrumentStore adxStore;	// store of ADX values
		
	public ADX(int id, SimHost host) {
		super(id, MatElementDefs.EL_TYP_ADX,host);
		adxStore = new InstrumentStore("adx");
		pdnEma = new EmaCore();
		ndnEma = new EmaCore();
		adxEma = new EmaCore();
	}

	@Override
	protected void processConfig(ConfigItem cfg) {
		switch (cfg.getItemId()) {
		case MatElementDefs.EL_ADX_C_PDN_ALPHA: c_pdn_alpha = cfg.getFloatData();	break;
		case MatElementDefs.EL_ADX_C_PDN_LEN:   c_pdn_len   = cfg.getRawData();		break;
		case MatElementDefs.EL_ADX_C_NDN_ALPHA: c_ndn_alpha = cfg.getFloatData();	break;
		case MatElementDefs.EL_ADX_C_NDN_LEN:   c_ndn_len   = cfg.getRawData();		break;
		case MatElementDefs.EL_ADX_C_ADX_ALPHA: c_adx_alpha = cfg.getFloatData();	break;
		case MatElementDefs.EL_ADX_C_ADX_LEN:   c_adx_len   = cfg.getRawData();		break;
		default: 
			logger.warn(getIdStr() + "Unexpected configuration: " + cfg); 
			setErrorCode(MatElementDefs.CB_EC_GEN_CFG_ERR);
			break;
		}
	}

	@Override
	protected void processConfigDone() {
		pdnEma.setParameters(c_pdn_alpha, c_pdn_len);
		ndnEma.setParameters(c_ndn_alpha, c_ndn_len);
		adxEma.setParameters(c_adx_alpha, c_adx_len);
	}

	@Override
	protected void processEvent(int input, Event evt) throws Exception {
		int instr = evt.getInstrument_id();
		int tr = evt.getTickref();
		// get high(n), high(n-1), low(n), low(n-1)
		FloatValue highN  = HlocLookup(instr, tr, MatElementDefs.EL_HLOC_L_PREV_H).getFloatValue();
		FloatValue highN1 = HlocLookup(instr, tr, MatElementDefs.EL_HLOC_L_PRVM1_H).getFloatValue();
		FloatValue lowN   = HlocLookup(instr, tr, MatElementDefs.EL_HLOC_L_PREV_L).getFloatValue();
		FloatValue lowN1  = HlocLookup(instr, tr, MatElementDefs.EL_HLOC_L_PRVM1_L).getFloatValue();
		FloatValue atr    = AtrLookup(instr, tr, MatElementDefs.EL_ATR_L_ATR).getFloatValue();
		logger.debug("processEvent(" + show(evt.getFloatData()) + "): highH=" + highN + ", highN1=" + highN1 +
				", lowN=" + lowN + ", lowN1=" + lowN1 +
				", atr=" + atr);
		if (highN.isValid() && highN1.isValid() && 
				lowN.isValid() && lowN1.isValid() &&
				atr.isValid()) {
			float upN = highN.getValue() - highN1.getValue();
			float dnN = lowN1.getValue() - lowN.getValue();
			float pdmN = 0.0f;
			if ((upN > dnN) && (upN > 0)) {
				pdmN = upN;
			}
			float ndmN = 0.0f;
			if ((dnN > upN) && (dnN > 0)) {
				ndmN = dnN;
			}
			FloatValue pEma = pdnEma.processEvent(instr,pdmN);
			FloatValue nEma = ndnEma.processEvent(instr,ndmN);
			logger.debug("ADX: upn=" + show(upN) + ", dnn=" + show(dnN) +
					", pdnN=" + show(pdmN) + ", ndmN=" + show(ndmN) +
					", pEma=" + pEma + ", nEma=" + nEma);
			if (pEma.isValid() && nEma.isValid()) {
				float pdi = pEma.getValue() / atr.getValue();
				float ndi = nEma.getValue() / atr.getValue();
				float strength = Math.abs((pdi - ndi) / (pdi + ndi));
				FloatValue aEma = adxEma.processEvent(instr, strength);
				logger.debug("processEvent()---- pdi=" + show(pdi) + ", ndi=" + show(ndi) +
						", sub=" + show(pdi-ndi) + ", add=" + show(pdi+ndi) +
						", strength=" + show(strength) + ", aEma=" + aEma);
				if (aEma.isValid()) {
					float adx = 100 * aEma.getValue();
					adxStore.put(adx, instr);
					Event evtOut = new Event(host.getCurrentSimTime(),elementId,instr,0,adx);
					host.publishEvent(evtOut,LATENCY);
				}
			}
		}
	}
	

	private LookupResult HlocLookup(int instr, int tickref, int key) throws Exception {
		LookupResult rslt = lookup(instr, tickref, key, getLookupTarget(0));
		if (!rslt.isValid()) {
			setErrorCode(MatElementDefs.CB_EC_GEN_FETCH_ERR);
		}
		return rslt;
	}

	private LookupResult AtrLookup(int instr, int tickref, int key) throws Exception {
		LookupResult rslt = lookup(instr, tickref, key, getLookupTarget(1));
		if (!rslt.isValid()) {
			setErrorCode(MatElementDefs.CB_EC_GEN_FETCH_ERR);
		}
		return rslt;
	}


	@Override
	public LookupResult lookupBehaviour(int instrumentId, int arg, int tickref, int lookupKey) throws Exception {
		LookupResult result = new LookupResult(elementId,LookupValidity.TIMEOUT,LOOKUP_TIMEOUT_DLY);
		switch (lookupKey) {
		case MatElementDefs.EL_ADX_L_ADX:
			result = new LookupResult(elementId,adxStore.get(instrumentId),LOOKUP_DLY); break;
		}
		return result;
	}

	@Override
	protected String getTypeName() {
		return "ADX";
	}

}

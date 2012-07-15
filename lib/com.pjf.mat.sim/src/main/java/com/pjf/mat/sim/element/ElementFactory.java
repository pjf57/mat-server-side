package com.pjf.mat.sim.element;

import com.pjf.mat.api.MatElementDefs;
import com.pjf.mat.sim.model.SimElement;
import com.pjf.mat.sim.model.SimHost;

public class ElementFactory {

	public static SimElement create(int id, int hwType, SimHost host) {
		SimElement el = null;
		switch (hwType) {
		case MatElementDefs.EL_TYP_ROUTER: break;	// dont need to instantiate the router
		case MatElementDefs.EL_TYP_TG1: 		el = new TG1(id,host);		break;
		case MatElementDefs.EL_TYP_LOG: 		el = new Logger(id,host);	break;
		case MatElementDefs.EL_TYP_EMA:			el = new EMA(id,host);		break;
		case MatElementDefs.EL_TYP_LOGIC_4IP:
		case MatElementDefs.EL_TYP_ARITH_4IP:
		case MatElementDefs.EL_TYP_UDP_RAW_MKT:
		case MatElementDefs.EL_TYP_HLOC:
		case MatElementDefs.EL_TYP_ATR:
		case MatElementDefs.EL_TYP_ADX:
		default:
		}
		return el;
	}
}

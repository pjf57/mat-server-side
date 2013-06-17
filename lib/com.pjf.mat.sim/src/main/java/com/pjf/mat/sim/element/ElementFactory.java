package com.pjf.mat.sim.element;

import com.pjf.mat.api.MatElementDefs;
import com.pjf.mat.sim.model.SimElement;
import com.pjf.mat.sim.model.SimHost;

public class ElementFactory {

	/**
	 * Factory to create an element
	 * 
	 * @param id		id of element to create
	 * @param hwType	hwType code indicates which type of element to create
	 * @param host		reference to the host for callback
	 * @return			element or null
	 * @throws Exception 
	 */
	public static SimElement create(int id, int hwType, SimHost host) throws Exception {
		SimElement el = null;
		switch (hwType) {
		case MatElementDefs.EL_TYP_ROUTER: break;	// dont need to instantiate the router
		case MatElementDefs.EL_TYP_TG1: 		el = new TG1(id,host);			break;
		case MatElementDefs.EL_TYP_LOG: 		el = new Logger(id,host);		break;
		case MatElementDefs.EL_TYP_EMA:			el = new EMA(id,host);			break;
		case MatElementDefs.EL_TYP_MACD:		el = new MACD(id,host);			break;
		case MatElementDefs.EL_TYP_LOGIC_4IP:	el = new L4IP(id,host);			break;
		case MatElementDefs.EL_TYP_ARITH_4IP:	el = new L4IP(id,host);			break;
		case MatElementDefs.EL_TYP_HLOC:		el = new HLOC(id,host);			break;
		case MatElementDefs.EL_TYP_ATR:			el = new ATR(id,host);			break;
		case MatElementDefs.EL_TYP_ADX:			el = new ADX(id,host);			break;
		case MatElementDefs.EL_TYP_UDP_MFD_SYM:	el = new IpMfdSym(id,host);		break;
		case MatElementDefs.EL_TYP_RMO:			el = new RMO(id,host);			break;
		case MatElementDefs.EL_TYP_UDP_RAW_MKT:	// not supported in sim
		default:
		}
		return el;
	}
}

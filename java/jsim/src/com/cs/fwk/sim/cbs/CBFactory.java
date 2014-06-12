package com.cs.fwk.sim.cbs;

import com.cs.fwk.sim.model.SimElement;
import com.cs.fwk.sim.model.SimHost;
import com.cs.fwk.api.MatElementDefs;

public class CBFactory {

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
		case MatElementDefs.EL_TYP_ARITH_4IP:	el = new A4IP(id,host);			break;
		case MatElementDefs.EL_TYP_HLOC:		el = new HLOC(id,host);			break;
		case MatElementDefs.EL_TYP_ATR:			el = new ATR(id,host);			break;
		case MatElementDefs.EL_TYP_ADX:			el = new ADX(id,host);			break;
		case MatElementDefs.EL_TYP_MFD_SYM:		el = new IpMfdSym(id,host);		break;
		case MatElementDefs.EL_TYP_RMO:			el = new RMO(id,host);			break;
		case MatElementDefs.EL_TYP_SIB:			el = new SIB(id,host);			break;
		case MatElementDefs.EL_TYP_UDP_RAW_MKT:	// not supported in sim
		default:
		}
		return el;
	}
}

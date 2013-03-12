package com.pjf.mat.sim.element;

import com.pjf.mat.api.MatElementDefs;
import com.pjf.mat.sim.bricks.BaseElement;
import com.pjf.mat.sim.model.SimElement;
import com.pjf.mat.sim.model.SimHost;
import com.pjf.mat.sim.model.TickDataBasicResult;
import com.pjf.mat.sim.model.TickDataSymbolResult;
import com.pjf.mat.sim.model.TickDataVolPriceResult;
import com.pjf.mat.sim.types.Event;
import com.pjf.mat.api.util.ConfigItem;

/**
 * Raw Market Order placer
 * 
 * Simple order placer
 *
 * 		buy requests come in on input 0
 * 		sell requests com in on input 1
 * 
 * 		order volume is constrained between min & max (configurable)
 * 
 * 		Orders are output on the order output port.
 * 
 * @author pjf
 *
 */
public class RMO extends BaseElement implements SimElement {
	private final static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(RMO.class);
	private int c_minVol;
	private int c_maxVol;
	private int c_port;
	private int c_udpip;
	
	public RMO(int id, SimHost host) {
		super(id, MatElementDefs.EL_TYP_LOG,host);
	}

	@Override
	protected void processConfig(ConfigItem cfg) {
		switch (cfg.getItemId()) {
		case MatElementDefs.EL_RMO_C_MIN_VOL: 	c_minVol = cfg.getRawData();	break;
		case MatElementDefs.EL_RMO_C_MAX_VOL: 	c_maxVol = cfg.getRawData();	break;
		case MatElementDefs.EL_RMO_C_UDPPORT: 	c_port = cfg.getRawData();		break;
		case MatElementDefs.EL_RMO_C_UDPIP: 	c_udpip = cfg.getRawData();		break;
		default: logger.warn(getIdStr() + "Unexpected configuration: " + cfg); break;
		}
	}


	@Override
	protected void processEvent(int input, Event evt) {
		logger.debug("processEvent() - " + evt);
		boolean buy = false;
		boolean sell = false;
		if ((evt.getRawData() & 0x00000001) == 0x00000001) {
			if (input == 0) {
				buy = true;
			}
			if (input == 1) {
				sell = true;
			}
			if (buy || sell) {
				// get tick data
				try {
					TickDataBasicResult basicRslt = (TickDataBasicResult) host.tickdata(elementId, evt.getTickref(), MatElementDefs.TDS_BASIC);
					TickDataSymbolResult symRslt = (TickDataSymbolResult) host.tickdata(elementId, evt.getTickref(), MatElementDefs.TDS_SYMBOL);
					TickDataVolPriceResult pvRslt = (TickDataVolPriceResult) host.tickdata(elementId, evt.getTickref(), MatElementDefs.TDS_VOL_PRICE_SP);
					if (basicRslt.isValid() &&  symRslt.isValid() && pvRslt.isValid()) {
						// determine volume
						if (basicRslt.getVolumeInt() >= c_minVol) {
							int orderVol = Math.min(basicRslt.getVolumeInt(), c_maxVol);
							String symbol = symRslt.getSymbol();
							float price = pvRslt.getPrice();
							String dir = "Buy";
							if (sell) {
								dir = "Sell";
							}
							logger.info(getLogId() + "--- Place ORDER: " + dir + " " + orderVol +
									" " + symbol + " at " + price);
							Event evtOut = new Event(host.getCurrentSimTime(),elementId,0,evt.getInstrument_id(),evt.getTickref(), price);
							publishEvent(evtOut,1);	
						}
					}
				} catch (Exception e) {
					logger.error("Error getting tickdata - " + e.getMessage());
				}
			}
		}
	}

	@Override
	protected String getTypeName() {
		return "RMO";
	}


}

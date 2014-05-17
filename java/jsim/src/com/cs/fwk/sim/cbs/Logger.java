package com.cs.fwk.sim.cbs;

import com.cs.fwk.sim.bricks.BaseElement;
import com.cs.fwk.sim.model.SimElement;
import com.cs.fwk.sim.model.SimHost;
import com.cs.fwk.sim.types.Event;
import com.cs.fwk.api.MatElementDefs;
import com.cs.fwk.api.util.ConfigItem;

/**
 * The logger element takes events on any of its four inputs and
 * sends the events to the host system
 * 
 * @author pjf
 *
 */
public class Logger extends BaseElement implements SimElement {
	private final static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Logger.class);

	public Logger(int id, SimHost host) {
		super(id, MatElementDefs.EL_TYP_LOG,host);
	}

	@Override
	protected void processConfig(ConfigItem cfg) {
		logger.warn(getIdStr() + "Unexpected configuration: " + cfg);
	}

	@Override
	protected void processEvent(int input, Event evt) {
		logger.debug("processEvent() - " + evt);
		host.publishEventLog(evt.getTimestamp(), evt.getSrc(), evt.getSrcPort(), 
				evt.getInstrument_id(), evt.getTickref(), evt.getRawData());
	}

	@Override
	protected String getTypeName() {
		return "Logger";
	}


}

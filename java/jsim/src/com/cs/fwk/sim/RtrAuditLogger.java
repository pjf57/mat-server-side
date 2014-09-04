package com.cs.fwk.sim;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.cs.fwk.api.Element;
import com.cs.fwk.api.MatElementDefs;
import com.cs.fwk.api.OutputPort;
import com.cs.fwk.api.Timestamp;
import com.cs.fwk.api.logging.RtrAuditLog;
import com.cs.fwk.api.util.ConfigItem;

/**
 * Manages the repository of LKU audit logs
 * 
 * @author pjf
 *
 */
public class RtrAuditLogger {
	private final static Logger logger = Logger.getLogger(RtrAuditLogger.class);
	private final List<RtrAuditLog> queue;
	private int autoThreshold;
	
	private final int MAX_LOGS = 80;
	
	public RtrAuditLogger() {
		queue = new ArrayList<RtrAuditLog>();
		autoThreshold = 0;
	}
	
	public synchronized void addLog(RtrAuditLog log) {
		queue.add(log);
	}
	
	public List<RtrAuditLog> getLogs(int max) {
		List<RtrAuditLog> result = new ArrayList<RtrAuditLog>();
		int num = max;
		if (num > queue.size()) {
			num = queue.size();
		}
		for(int n=0; n<num; n++) {
			result.add(queue.get(n));
		}
		synchronized (this) {
			queue.removeAll(result);
		}
		return result;
	}

	/**
	 * Add new LKU Audit Log from parameters
	 * 
	 * @param startTime
	 * @param requester
	 * @param instrumentId
	 * @param tickref
	 * @param lookupKey
	 * @param responder
	 * @param resultCode
	 * @param data
	 * @param lookupTime
	 */
	public void addLog(Timestamp timestamp, Element source, int srcPort, Set<Element> takers, 
			int instrument_id, int tickref, int qTime, int deltime, int rawData) {
		OutputPort op = null;
		if (source != null) {
			op = source.getOutputs().get(srcPort);
		}		
		RtrAuditLog log = new RtrAuditLog(timestamp, source, op, takers, 
				instrument_id, tickref, qTime, deltime, rawData);
		if (source == null) {
			logger.error("addLog() - source element is null [" + log + "]");
		}
		addLog(log);		
	}

	public void putConfig(ConfigItem cfg) {
		if (cfg.getElementId() == 0) {
			switch (cfg.getItemId()) {
			case MatElementDefs.EL_C_RTR_AUDIT_THRESH: setAuditThreshold(cfg.getRawData());	break;
			}
		}
		
	}

	private void setAuditThreshold(int data) {
		logger.info("setAuditThreshold(" + data + ")");
		autoThreshold = data;		
	}
	
	/**
	 * Check if should autosend
	 * 
	 * @return collection of audit logs, or null
	 */
	public List<RtrAuditLog> checkAutoSend() {
		List<RtrAuditLog> ret = null;
		if (autoThreshold > 0 && queue.size() >= autoThreshold) {
			logger.debug("checkAutoSend() - queue size = " + queue.size() + ", autoThreshold = " + autoThreshold);
			ret = getLogs(MAX_LOGS);
		}
		return ret;
	}
	

}

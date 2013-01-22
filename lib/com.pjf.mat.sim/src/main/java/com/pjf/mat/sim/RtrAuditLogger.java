package com.pjf.mat.sim;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.pjf.mat.api.Element;
import com.pjf.mat.api.MatElementDefs;
import com.pjf.mat.api.RtrAuditLog;
import com.pjf.mat.api.Timestamp;
import com.pjf.mat.sim.types.ConfigItem;

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
	
	public Collection<RtrAuditLog> getLogs(int max) {
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
			int instrument_id, int tickref, int qTime, int deltime, float data) {
		
		RtrAuditLog log = new RtrAuditLog(timestamp, source, srcPort, takers, 
				instrument_id, tickref, qTime, deltime, data);
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
	public Collection<RtrAuditLog> checkAutoSend() {
		Collection<RtrAuditLog> ret = null;
		if (autoThreshold > 0 && queue.size() >= autoThreshold) {
			logger.debug("checkAutoSend() - queue size = " + queue.size() + ", autoThreshold = " + autoThreshold);
			ret = getLogs(MAX_LOGS);
		}
		return ret;
	}
	

}

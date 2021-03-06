package com.cs.fwk.sim;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.cs.fwk.api.Element;
import com.cs.fwk.api.LkuResult;
import com.cs.fwk.api.MatElementDefs;
import com.cs.fwk.api.Timestamp;
import com.cs.fwk.api.logging.LkuAuditLog;
import com.cs.fwk.api.util.ConfigItem;

/**
 * Manages the repository of LKU audit logs
 * 
 * @author pjf
 *
 */
public class LkuAuditLogger {
	private final static Logger logger = Logger.getLogger(LkuAuditLogger.class);
	private final List<LkuAuditLog> queue;
	private int autoThreshold;
	
	private final int MAX_LOGS = 80;
	
	public LkuAuditLogger() {
		queue = new ArrayList<LkuAuditLog>();
		autoThreshold = 0;
	}
	
	public synchronized void addLog(LkuAuditLog log) {
		queue.add(log);
	}
	
	public List<LkuAuditLog> getLogs(int max) {
		List<LkuAuditLog> result = new ArrayList<LkuAuditLog>();
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
	public void addLog(Timestamp startTime, Element requester, int instrumentId,
			int tickref, int lookupKey, int arg, Element responder, LkuResult resultCode,
			float data, int lookupTime) {
		
		LkuAuditLog log = new LkuAuditLog(startTime, requester, instrumentId, tickref,
				lookupKey, arg, responder, lookupTime, resultCode, data);
		addLog(log);		
	}

	public void putConfig(ConfigItem cfg) {
		if (cfg.getElementId() == 0) {
			switch (cfg.getItemId()) {
			case MatElementDefs.EL_C_LKU_AUDIT_THRESH: setAuditThreshold(cfg.getRawData());	break;
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
	public List<LkuAuditLog> checkAutoSend() {
		List<LkuAuditLog> ret = null;
		if (autoThreshold > 0 && queue.size() >= autoThreshold) {
			logger.debug("checkAutoSend() - queue size = " + queue.size() + ", autoThreshold = " + autoThreshold);
			ret = getLogs(MAX_LOGS);
		}
		return ret;
	}
	

}

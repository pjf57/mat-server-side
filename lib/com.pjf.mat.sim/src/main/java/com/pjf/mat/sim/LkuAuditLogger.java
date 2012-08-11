package com.pjf.mat.sim;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.pjf.mat.api.Element;
import com.pjf.mat.api.LkuAuditLog;
import com.pjf.mat.api.LkuResult;
import com.pjf.mat.api.Timestamp;

/**
 * Manages the repository of LKU audit logs
 * 
 * @author pjf
 *
 */
public class LkuAuditLogger {
	private final List<LkuAuditLog> queue;
	
	public LkuAuditLogger() {
		queue = new ArrayList<LkuAuditLog>();
	}
	
	public synchronized void addLog(LkuAuditLog log) {
		queue.add(log);
	}
	
	public Collection<LkuAuditLog> getLogs(int max) {
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
	 * @param lookupKey
	 * @param responder
	 * @param resultCode
	 * @param data
	 * @param lookupTime
	 */
	public void addLog(Timestamp startTime, Element requester, int instrumentId,
			int lookupKey, Element responder, LkuResult resultCode,
			float data, int lookupTime) {
		
		LkuAuditLog log = new LkuAuditLog(startTime, requester, instrumentId, lookupKey, 
				responder, lookupTime, resultCode, data);
		queue.add(log);		
	}
	

}

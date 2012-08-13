package com.pjf.mat.sys;

import java.util.concurrent.PriorityBlockingQueue;

import org.apache.log4j.Logger;

import com.pjf.mat.api.TimeOrdered;

/**
 * Maintains a store of all received logs in time order
 * 
 * @author pjf
 *
 */
public class UnifiedEventLogger {
	private final static Logger logger = Logger.getLogger(UnifiedEventLogger.class);
	private final PriorityBlockingQueue<TimeOrdered> store;
	
	public UnifiedEventLogger() {
		store = new PriorityBlockingQueue<TimeOrdered>();
	}
	
	public void addLog(TimeOrdered event) {
		store.put(event);
	}
	
	public void logAndClear(){
		while (!store.isEmpty()) {
			try {
				TimeOrdered evt = store.take();
				logger.info("log: " + evt);
			} catch (InterruptedException e) {
				logger.warn("logAndClear() - interrupted - " + e.getMessage());
			}
		}
	}
	

}

package com.pjf.marketsim;


/**
 * Callback interface through which an event feeder communicates its status
 */
public interface EventFeedCallbackInt {
	
	/**
	 * Notify indicative state
	 * 
	 * @param state 		short descriptive string
	 * @param totalsent		total number of events sent so far
	 */
	public void notifyEventFeedState(String state, long totalSent);
	
}

package com.cs.fwk.api;

/**
 * Unified interface for various event classes
 * Allows time ordering of the events
 * 
 * @author pjf
 *
 */
public interface TimeOrdered extends Comparable<TimeOrdered> {
	public Timestamp getTimestamp();
}

package com.pjf.mat.sim.router;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.pjf.mat.api.Timestamp;
import com.pjf.mat.sim.types.Event;

/**
 * Implements an ordered list of events.
 * The ordering is by timestamp of execution
 * For each timestamp value, there can be one or more events to be executed
 * these are also kept in order of adding
 * 
 * This implementation is not threadsafe
 * 
 * @author pjf
 *
 */
public class SortedEventQueue {
	private final static Logger logger = Logger.getLogger(SortedEventQueue.class);
	private final SortedMap<Timestamp,Bucket> list;

	/**
	 * Bucket of events all with the same timestamp
	 * @author pjf
	 */
	class Bucket implements Comparable<Bucket> {
		private final Timestamp timestamp;		// time at which events should be delivered
		private final List<Event> events;		// events to be delivered
		
		public Bucket(Timestamp timestamp) {
			this.timestamp = timestamp;
			this.events = new ArrayList<Event>();
		}
		
		public void add(Event evt) {
			events.add(evt);
		}
		
		public Timestamp getTimestamp() {
			return timestamp;
		}
		
		public Collection<Event> getEvents() {
			return events;
		}
		
		@Override
		public int compareTo(Bucket o) {
			return timestamp.compareTo(o.timestamp);
		}
		
		@Override
		public String toString() {
			StringBuffer buf = new StringBuffer();
			buf.append(timestamp);
			buf.append(":");
			buf.append(events.size());
			return buf.toString();
		}
		
		@Override
		public int hashCode() {
			return timestamp.hashCode();
		}
		
		@Override
		public boolean equals(Object o) {
			if (! (o instanceof Bucket)) {
				return false;
			}
			Bucket ob = (Bucket) o;
			return this.timestamp.equals(ob.timestamp);
		}
	}
	
	public SortedEventQueue() {
		this.list = new TreeMap<Timestamp,Bucket>();
	}
	
	/**
	 * Add an event to be scheduled for delivery at the specified time
	 * 
	 * @param evt
	 * @param evtTime
	 */
	public synchronized void add(Event evt, Timestamp evtTime) {
		Bucket b = list.get(evtTime);
		if (b == null) {
			// This timestamp doesnt already exist in the list, so add it
			b = new Bucket(evtTime);
			list.put(evtTime,b);
		}
		b.add(evt);		
	}

	/**
	 * Get a collection of events that are to be executed at this simTime
	 * or should have already been executed by this sim time (log warning)
	 * Remove them from the list
	 * 
	 * @param simTime
	 * @return collection of events
	 */
	public synchronized Collection<Event> takeEvents(Timestamp time) {
		List<Event> events = new ArrayList<Event>();
		List<Timestamp> removeList = new ArrayList<Timestamp>();
		// see if there are any that are earlier than the required time
		SortedMap<Timestamp,Bucket> earlier = list.headMap(time);
		for (Bucket b : earlier.values()) {
			logger.debug("takeEvents() event is before current time: " + time +
					" " + b);
			events.addAll(b.getEvents());
			removeList.add(b.getTimestamp());
		}
		
		// remove the buckets that we already processed
		for (Timestamp t : removeList) {
			list.remove(t);
		}

		// normal case: see if there is a bucket AT the required time
		Bucket b = list.get(time);
		if (b != null) {
			logger.debug("takeEvents(): have " + b);
			events.addAll(b.getEvents());
			list.remove(b.getTimestamp());
		}
		return events;
	}
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("[");
		for (Bucket b : list.values()) {
			buf.append(b);
			buf.append(" ");
		}
		buf.append("]");
		return buf.toString();
	}

}

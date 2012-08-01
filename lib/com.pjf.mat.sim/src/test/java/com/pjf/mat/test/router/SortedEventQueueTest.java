package com.pjf.mat.test.router;

import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.BasicConfigurator;

import com.pjf.mat.api.Timestamp;
import com.pjf.mat.sim.router.SortedEventQueue;
import com.pjf.mat.sim.types.Event;

import junit.framework.TestCase;

public class SortedEventQueueTest extends TestCase {
	
	@Override
	protected void setUp() {
		BasicConfigurator.configure();
	}
	
	public void testInitialisation() {
		SortedEventQueue q = new SortedEventQueue();
		Collection<Event> events;
		Timestamp t = new Timestamp();
		assertEquals(t.getMicroticks(),0L);
		events = q.takeEvents(t);
		assertEquals(events.size(),0);
	}

	public void testOneCurrent() {
		SortedEventQueue q = new SortedEventQueue();
		Collection<Event> events;
		Timestamp t = new Timestamp();
		t.add(100);
		q.add(new Event(t,2,3,50.0f), t);
		events = q.takeEvents(t);
		assertEquals(events.size(),1);
		Event e = events.iterator().next();
		assertEquals(e.getFloatData(),50.0f);
		events = q.takeEvents(t);
		assertEquals(events.size(),0);
	}

	public void testOneAhead() {
		SortedEventQueue q = new SortedEventQueue();
		Collection<Event> events;
		Timestamp t1 = new Timestamp();
		t1.add(100);
		Timestamp t2 = new Timestamp(t1);
		t2.add(5);
		q.add(new Event(t2,2,3,50.0f), t2);
		events = q.takeEvents(t1);
		assertEquals(events.size(),0);
		events = q.takeEvents(t2);
		assertEquals(events.size(),1);
		Event e = events.iterator().next();
		assertEquals(e.getFloatData(),50.0f);
		events = q.takeEvents(t1);
		assertEquals(events.size(),0);
		events = q.takeEvents(t2);
		assertEquals(events.size(),0);
	}

	public void testOneBehind() {
		SortedEventQueue q = new SortedEventQueue();
		Collection<Event> events;
		Timestamp t1 = new Timestamp();
		t1.add(100);
		Timestamp t2 = new Timestamp(t1);
		t2.add(5);
		q.add(new Event(t1,2,3,50.0f), t1);
		events = q.takeEvents(t2);
		assertEquals(events.size(),1);
		Event e = events.iterator().next();
		assertEquals(e.getFloatData(),50.0f);
		events = q.takeEvents(t1);
		assertEquals(events.size(),0);
		events = q.takeEvents(t2);
		assertEquals(events.size(),0);
	}

	public void testTwoCurrentSameT() {
		SortedEventQueue q = new SortedEventQueue();
		Collection<Event> events;
		Timestamp t = new Timestamp();
		t.add(100);
		q.add(new Event(t,2,3,50.0f), t);
		q.add(new Event(t,2,3,51.0f), t);
		events = q.takeEvents(t);
		assertEquals(events.size(),2);
		Iterator<Event> iter = events.iterator();
		Event e = iter.next();
		assertEquals(e.getFloatData(),50.0f);
		e = iter.next();
		assertEquals(e.getFloatData(),51.0f);
		events = q.takeEvents(t);
		assertEquals(events.size(),0);
	}

	public void testTwoCurrentDiffTimeSameOrderCollectSeq() {
		SortedEventQueue q = new SortedEventQueue();
		Collection<Event> events;
		Timestamp t1 = new Timestamp();
		t1.add(100);
		Timestamp t2 = new Timestamp(t1);
		t2.add(5);
		q.add(new Event(t1,2,3,50.0f), t1);
		q.add(new Event(t2,2,3,51.0f), t2);
		Event e;
		events = q.takeEvents(t1);
		assertEquals(events.size(),1);
		e = events.iterator().next();
		assertEquals(e.getFloatData(),50.0f);		
		events = q.takeEvents(t2);
		assertEquals(events.size(),1);
		e = events.iterator().next();
		assertEquals(e.getFloatData(),51.0f);
		events = q.takeEvents(t2);
		assertEquals(events.size(),0);
	}
	
	
	public void testTwoCurrentDiffTimeDiffOrderCollectSeq() {
		SortedEventQueue q = new SortedEventQueue();
		Collection<Event> events;
		Timestamp t1 = new Timestamp();
		t1.add(100);
		Timestamp t2 = new Timestamp(t1);
		t2.add(5);
		q.add(new Event(t2,2,3,51.0f), t2);
		q.add(new Event(t1,2,3,50.0f), t1);
		Event e;
		events = q.takeEvents(t1);
		assertEquals(events.size(),1);
		e = events.iterator().next();
		assertEquals(e.getFloatData(),50.0f);		
		events = q.takeEvents(t2);
		assertEquals(events.size(),1);
		e = events.iterator().next();
		assertEquals(e.getFloatData(),51.0f);
		events = q.takeEvents(t2);
		assertEquals(events.size(),0);
	}
	
	public void testTwoCurrentDiffTimeSameOrderCollectTogether() {
		SortedEventQueue q = new SortedEventQueue();
		Collection<Event> events;
		Timestamp t1 = new Timestamp();
		t1.add(100);
		Timestamp t2 = new Timestamp(t1);
		t2.add(5);
		q.add(new Event(t1,2,3,50.0f), t1);
		q.add(new Event(t2,2,3,51.0f), t2);
		Event e;
		events = q.takeEvents(t2);
		assertEquals(events.size(),2);
		Iterator<Event> iter = events.iterator();
		e = iter.next();
		assertEquals(e.getFloatData(),50.0f);
		e = iter.next();
		assertEquals(e.getFloatData(),51.0f);
		events = q.takeEvents(t2);
		assertEquals(events.size(),0);
	}
	
	public void testTwoCurrentDiffTimeSameOrderCollectTogetherBehind() {
		SortedEventQueue q = new SortedEventQueue();
		Collection<Event> events;
		Timestamp t1 = new Timestamp();
		t1.add(100);
		Timestamp t2 = new Timestamp(t1);
		t2.add(5);
		Timestamp t3 = new Timestamp(t2);
		t3.add(20);
		q.add(new Event(t1,2,3,50.0f), t1);
		q.add(new Event(t2,2,3,51.0f), t2);
		Event e;
		events = q.takeEvents(t3);
		assertEquals(events.size(),2);
		Iterator<Event> iter = events.iterator();
		e = iter.next();
		assertEquals(e.getFloatData(),50.0f);
		e = iter.next();
		assertEquals(e.getFloatData(),51.0f);
		events = q.takeEvents(t3);
		assertEquals(events.size(),0);
	}
	
	
}

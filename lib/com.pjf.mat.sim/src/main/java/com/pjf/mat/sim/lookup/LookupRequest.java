package com.pjf.mat.sim.lookup;

import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;

import com.pjf.mat.sim.model.LookupResult;

/**
 * This class holds one lookup request and can block on a semaphore
 * 
 * @author pjf
 */
public class LookupRequest implements Comparable<LookupRequest>{
	private final static Logger logger = Logger.getLogger(LookupRequest.class);
	private final int source;
	private final int instrumentId;
	private final int lookupKey;
	private final Semaphore sem;
	private LookupResult result;
	private final boolean valid;
	
	
	/**
	 * Construct an invalid (empty) request.
	 */
	public static LookupRequest createNullRequest() {
		return new LookupRequest();
	}

	private LookupRequest() {
		this.valid = false;
		this.source = 0;
		this.instrumentId = 0;
		this.lookupKey = 0;
		this.sem = new Semaphore(0);
		this.result = null;
	}
	
	/**
	 * Construct a normal request
	 * 
	 * @param source
	 * @param instrumentId
	 * @param lookupKey
	 */
	public LookupRequest(int source, int instrumentId, int lookupKey) {
		this.source = source;
		this.instrumentId = instrumentId;
		this.lookupKey = lookupKey;
		this.sem = new Semaphore(0);
		this.result = null;
		this.valid = true;
	}
	
	/**
	 * Suspend thread until advised ready
	 */
	public LookupResult waitForResult() {
		try {
			sem.acquire();
		} catch (InterruptedException e) {
			logger.warn("waitForResult() interrupted exception - " + toString());
		}
		return result;
	}
	
	public void provideResult(LookupResult result) {
		this.result = result;
		sem.release();
	}
	
	public int getSource() {
		return source;
	}
	public int getInstrumentId() {
		return instrumentId;
	}
	public int getLookupKey() {
		return lookupKey;
	}
	
	public boolean isValid() {
		return valid;
	}
	
	@Override
	public String toString() {
		return "src=" + source + ",instr=" + instrumentId +
			",key=" + lookupKey + ",sem=" + sem.availablePermits();
	}

	@Override
	public int compareTo(LookupRequest o) {
		return source - o.source;
	}

	


}

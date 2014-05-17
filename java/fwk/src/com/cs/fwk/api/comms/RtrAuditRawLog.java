package com.cs.fwk.api.comms;

import com.cs.fwk.api.Timestamp;

/**
 * Container for one Router Audit Log entry
 * 
 * @author pjf
 *
 */
public class RtrAuditRawLog {
	private final Timestamp timestamp;
	private final int sourceId;
	private final int sourcePort;
	private final int instrumentId;
	private final int tickref;
	private final float data;
	private final int takerSet;		// set of CB Ids that took the event
	private final int qTimeMicroticks;		// queueing time
	private final int delTimeMicroticks;	// delivery time
	
	private static final int CLK_TIME_NS = 10;	// microtick time

	public RtrAuditRawLog(Timestamp timestamp, int sourceId, int sourcePort, int takerSet, 
			int instrumentId, int tickref, int qTime, int deltime, float data) {
		this.timestamp = timestamp;
		this.instrumentId = instrumentId;
		this.tickref = tickref;
		this.sourceId = sourceId;
		this.sourcePort = sourcePort;
		this.qTimeMicroticks = qTime;
		this.delTimeMicroticks = deltime;
		this.data = data;
		this.takerSet = takerSet;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public int getSourceId() {
		return sourceId;
	}

	public int getSourcePort() {
		return sourcePort;
	}

	public int getInstrumentId() {
		return instrumentId;
	}

	public int getTickref() {
		return tickref;
	}

	public float getData() {
		return data;
	}

	public int getTakerSet() {
		return takerSet;
	}

	public int getqTimeMicroticks() {
		return qTimeMicroticks;
	}

	public int getDelTimeMicroticks() {
		return delTimeMicroticks;
	}

	public static int getClkTimeNs() {
		return CLK_TIME_NS;
	}

	@Override
	public String toString() {
		return "RtrAuditRawLog [timestamp=" + timestamp + ", sourceId="
				+ sourceId + ", sourcePort=" + sourcePort + ", instrumentId="
				+ instrumentId + ", tickref=" + tickref + ", data=" + data
				+ ", takerSet=" + takerSet + ", qTimeMicroticks="
				+ qTimeMicroticks + ", delTimeMicroticks=" + delTimeMicroticks
				+ "]";
	}

}

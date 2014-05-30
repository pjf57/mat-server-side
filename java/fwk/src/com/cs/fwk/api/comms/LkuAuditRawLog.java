package com.cs.fwk.api.comms;


import com.cs.fwk.api.LkuResult;
import com.cs.fwk.api.Timestamp;

/**
 * Container for one RAW Lookup Audit Log entry
 * 
 * @author pjf
 *
 */
public class LkuAuditRawLog {
	private final Timestamp timestamp;
	private final int instrumentId;
	private final int tickref;
	private final int operation;
	private final int arg;
	private final int requesterId;
	private final int responderId;
	private final int rspTimeMicroticks;
	private final LkuResult result;
	private final float data;
	
 	private static final int CLK_TIME_NS = 10;	// FIXME microtick time

	public LkuAuditRawLog(Timestamp timestamp, int requesterId,
			int instrumentId, int tickref, int operation, int arg, int responderId, 
			int rspTimeMicroticks, LkuResult result, float data) {
		this.timestamp = timestamp;
		this.instrumentId = instrumentId;
		this.tickref = tickref;
		this.requesterId = requesterId;
		this.operation = operation;
		this.arg = arg;
		this.responderId = responderId;
		this.rspTimeMicroticks = rspTimeMicroticks;
		this.result = result;
		this.data = data;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public int getInstrumentId() {
		return instrumentId;
	}

	public int getTickref() {
		return tickref;
	}

	public int getOperation() {
		return operation;
	}

	public int getArg() {
		return arg;
	}

	public int getRequesterId() {
		return requesterId;
	}

	public int getResponderId() {
		return responderId;
	}

	public int getRspTimeMicroticks() {
		return rspTimeMicroticks;
	}

	public LkuResult getResult() {
		return result;
	}

	public float getData() {
		return data;
	}

	public static int getClkTimeNs() {
		return CLK_TIME_NS;
	}

	@Override
	public String toString() {
		return "LkuAuditRawLog [timestamp=" + timestamp + ", instrumentId="	+ instrumentId +
				", tickref=" + tickref + ", operation="	+ operation + ", arg="	+ arg +
				", requesterId=" + requesterId + ", responderId=" + responderId +
				", rspTimeMicroticks=" + rspTimeMicroticks
				+ ", result=" + result + ", data=" + data + "]";
	}

}

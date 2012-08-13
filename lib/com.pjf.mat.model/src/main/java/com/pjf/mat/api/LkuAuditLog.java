package com.pjf.mat.api;

/**
 * Container for one Lookup Audit Log entry
 * 
 * @author pjf
 *
 */
public class LkuAuditLog implements TimeOrdered {
	private final Timestamp timestamp;
	private final Element requester;		// element that requested 
	private final int instrument_id;
	private final int operation;
	private final Element responder;		// element that responded, or null
	private final int rspTimeMicroticks;
	private LkuResult result;
	private final float data;
	
	private static final int CLK_TIME_NS = 8;	// microtick time

	public LkuAuditLog(Timestamp timestamp, Element requester,
			int instrument_id, int operation, Element responder, int rspTimeMicroticks,
			LkuResult result, float data) {
		this.timestamp = timestamp;
		this.requester = requester;
		this.instrument_id = instrument_id;
		this.operation = operation;
		this.responder = responder;
		this.rspTimeMicroticks = rspTimeMicroticks;
		this.result = result;
		this.data = data;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public Element getRequester() {
		return requester;
	}

	public int getInstrument_id() {
		return instrument_id;
	}

	public int getOperation() {
		return operation;
	}
	
	public Element getResponder() {
		return responder;
	}

	public int getRspTimeMicroticks() {
		return rspTimeMicroticks;
	}

	public LkuResult getResult() {
		return result;
	}

	public float getData() {
		return data;
	};
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("Lookup:");
		buf.append(timestamp);
		buf.append(": req:"); buf.append(getShortName(requester));
		buf.append(",instr="); buf.append(instrument_id);
		buf.append(",op="); buf.append(MatElementDefs.LkuOpToString(operation));
		if (result.equals(LkuResult.TIMEOUT)) {
			buf.append(" TIMEOUT after "); buf.append(rspTimeMicroticks*CLK_TIME_NS); buf.append("ns"); 
			buf.append(" Data="); buf.append(data);
		} else if (result.equals(LkuResult.ERROR)) {
			buf.append(" ERROR result after "); buf.append(rspTimeMicroticks*CLK_TIME_NS); buf.append("ns");
			buf.append(" Data="); buf.append(data);
		} else if (result.equals(LkuResult.UNKNOWN)) {
			buf.append(" UNKNOWN result after "); buf.append(rspTimeMicroticks*CLK_TIME_NS); buf.append("ns");
			buf.append(" Data="); buf.append(data);
		} else {
			buf.append(" rsp:"); buf.append(getShortName(responder));
			buf.append(" "); buf.append(result);
			buf.append(" Data="); buf.append(data);
			buf.append(" in "); buf.append(rspTimeMicroticks*CLK_TIME_NS); buf.append("ns");
		}
		return buf.toString();
	}
	
	private String getShortName(Element el) {
		if (el == null) {
			return "null";
		}
		return el.getShortName();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + instrument_id;
		result = prime * result + operation;
		result = prime * result
				+ ((requester == null) ? 0 : requester.hashCode());
		result = prime * result
				+ ((responder == null) ? 0 : responder.hashCode());
		result = prime * result
				+ ((timestamp == null) ? 0 : timestamp.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LkuAuditLog other = (LkuAuditLog) obj;
		if (instrument_id != other.instrument_id)
			return false;
		if (operation != other.operation)
			return false;
		if (requester == null) {
			if (other.requester != null)
				return false;
		} else if (!requester.equals(other.requester))
			return false;
		if (responder == null) {
			if (other.responder != null)
				return false;
		} else if (!responder.equals(other.responder))
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		return true;
	}

	@Override
	public int compareTo(TimeOrdered o) {
		return timestamp.compareTo(o.getTimestamp());
	}
	

}

package com.pjf.mat.api;

public class EventLog implements TimeOrdered {
	private final Timestamp timestamp;
	private final Element src;
	private final OutputPort output;
	private final int intrumentId;
	private final int rawValue;
	private final String dispValue;
	
	public EventLog(Timestamp timestamp, Element src, OutputPort op, int intrumentId,
			int rawValue, String dispValue) {
		this.timestamp = timestamp;
		this.src = src;
		this.output = op;
		this.intrumentId = intrumentId;
		this.rawValue = rawValue;
		this.dispValue = dispValue;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public Element getSrc() {
		return src;
	}
	
	public OutputPort getSourcePort() {
		return output;
	}

	public int getIntrumentId() {
		return intrumentId;
	}

	public int getRawValue() {
		return rawValue;
	}

	public String getDispValue() {
		return dispValue;
	}
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("EvtLog:");
		buf.append(timestamp); 
		buf.append(": ");
		buf.append(src.getShortName()); 
		if (output != null) {
			buf.append(":");
			buf.append(output.getName());
		}
		buf.append(" InstrId=");
		buf.append(intrumentId);
		buf.append(" val=");
		buf.append(dispValue);
		return buf.toString();
	}

	@Override
	public int compareTo(TimeOrdered o) {
		return timestamp.compareTo(o.getTimestamp());
	}
}

package com.pjf.mat.api;

public class EventLog extends BaseLog {
	private final OutputPort output;
	private final int rawValue;
	private final String dispValue;
	
	public EventLog(Timestamp timestamp, Element src, OutputPort op, int instrumentId,
			int rawValue, String dispValue) {
		super(timestamp,instrumentId,src);
		this.output = op;
		this.rawValue = rawValue;
		this.dispValue = dispValue;
	}


	
	public OutputPort getSourcePort() {
		return output;
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
		buf.append(getTimestamp()); 
		buf.append(": ");
		buf.append(getSrcElement().getShortName()); 
		if (output != null) {
			buf.append(":");
			buf.append(output.getName());
		}
		buf.append(" InstrId=");
		buf.append(getInstrumentId());
		buf.append(" val=");
		buf.append(dispValue);
		return buf.toString();
	}

}

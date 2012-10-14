package com.pjf.mat.api;

import java.util.Set;

/**
 * Container for one Router Audit Log entry
 * 
 * @author pjf
 *
 */
public class RtrAuditLog extends BaseLog {
	private final int sourcePort;			// port on the element that produced the event 
	private final float data;
	private final Set<Element> takers;		// element that took the event
	private final int qTimeMicroticks;		// queueing time
	private final int delTimeMicroticks;	// delivery time
	
	private static final int CLK_TIME_NS = 8;	// microtick time

	/**
	 * Construct a Router Audit Log entry
	 * 
	 * @param timestamp
	 * @param source
	 * @param sourcePort 
	 * @param takers
	 * @param instrumentId
	 * @param qTime
	 * @param deltime
	 * @param data
	 */
	public RtrAuditLog(Timestamp timestamp, Element source, int sourcePort, Set<Element> takers, 
			int instrumentId, int qTime, int deltime, float data) {
		super(timestamp,instrumentId,source);
		this.sourcePort = sourcePort;
		this.qTimeMicroticks = qTime;
		this.delTimeMicroticks = deltime;
		this.data = data;
		this.takers = takers;
	}
		

	public OutputPort getSourcePort() {
		return getSrcElement().getOutputs().get(sourcePort);
	}

	public float getData() {
		return data;
	}

	public Set<Element> getTakers() {
		return takers;
	}

	public int getqTimeMicroticks() {
		return qTimeMicroticks;
	}

	public int getDelTimeMicroticks() {
		return delTimeMicroticks;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("Router:");
		buf.append(getTimestamp());
		buf.append(": src:"); buf.append(getShortName(getSrcElement())); buf.append(":"); 
		buf.append(getSrcElement().getOutputs().get(sourcePort).getName());
		buf.append(",instr="); buf.append(getInstrumentId());
		buf.append(" Data="); buf.append(data);
		buf.append(" Takers:[ ");
		for (Element el : takers) {
			buf.append(getShortName(el));
			buf.append(" ");
		}
		buf.append("] qtime=");
		buf.append(qTimeMicroticks*CLK_TIME_NS); buf.append("ns");
		buf.append(" delTime=");
		buf.append(delTimeMicroticks*CLK_TIME_NS); buf.append("ns"); 
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
		int result = super.hashCode();
		result = prime * result + Float.floatToIntBits(data);
		result = prime * result + sourcePort;
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
		RtrAuditLog other = (RtrAuditLog) obj;
		if (Float.floatToIntBits(data) != Float.floatToIntBits(other.data))
			return false;
		if (!super.equals(obj)) {
			return false;
		}
		if (sourcePort != other.sourcePort) {
			return false;
		}
		return true;
	}

	

}

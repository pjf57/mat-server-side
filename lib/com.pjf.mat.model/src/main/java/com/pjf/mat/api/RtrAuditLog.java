package com.pjf.mat.api;

import java.util.Set;

/**
 * Container for one Router Audit Log entry
 * 
 * @author pjf
 *
 */
public class RtrAuditLog implements TimeOrdered {
	private final Timestamp timestamp;
	private final Element source;			// element that produced the event 
	private final int sourcePort;			// port on the element that produced the event 
	private final int instrument_id;
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
	 * @param instrument_id
	 * @param qTime
	 * @param deltime
	 * @param data
	 */
	public RtrAuditLog(Timestamp timestamp, Element source, int sourcePort, Set<Element> takers, 
			int instrument_id, int qTime, int deltime, float data) {
		this.timestamp = timestamp;
		this.source = source;
		this.sourcePort = sourcePort;
		this.instrument_id = instrument_id;
		this.qTimeMicroticks = qTime;
		this.delTimeMicroticks = deltime;
		this.data = data;
		this.takers = takers;
	}
	
	public Timestamp getTimestamp() {
		return timestamp;
	}
	
	public Element getSource() {
		return source;
	}

	public OutputPort getSourcePort() {
		return source.getOutputs().get(sourcePort);
	}

	public int getInstrument_id() {
		return instrument_id;
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
		buf.append(timestamp);
		buf.append(": src:"); buf.append(getShortName(source)); buf.append(":"); buf.append(source.getOutputs().get(sourcePort).getName());
		buf.append(",instr="); buf.append(instrument_id);
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
		int result = 1;
		result = prime * result + Float.floatToIntBits(data);
		result = prime * result + instrument_id;
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + sourcePort;
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
		RtrAuditLog other = (RtrAuditLog) obj;
		if (Float.floatToIntBits(data) != Float.floatToIntBits(other.data))
			return false;
		if (instrument_id != other.instrument_id)
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (sourcePort != other.sourcePort) {
			return false;
		}
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

package com.pjf.mat.api;

/**
 * Base class for logs
 * 
 * Allows unified view of some attributes of different log types
 * 
 * @author pjf
 *
 */
public class BaseLog implements TimeOrdered {
	private final Timestamp timestamp;
	private final int instrumentId;
	private final int tickref;
	private final Element srcElement;
	
	public BaseLog(Timestamp timestamp, int instrumentId, int tickref, Element srcElement) {
		this.timestamp = timestamp;
		this.instrumentId = instrumentId;
		this.tickref = tickref;
		this.srcElement = srcElement;
	}

	
	public Timestamp getTimestamp() {
		return timestamp;
	}
	
	public int getInstrumentId() {
		return instrumentId;
	}
	
	public Element getSrcElement() {
		return srcElement;
	}
	
	public int getTickref() {
		return tickref;
	}
	
	@Override
	public int compareTo(TimeOrdered o) {
		return timestamp.compareTo(o.getTimestamp());
	}

	@Override
	public String toString() {
		return getTimestamp() + ":" + srcElement.getShortName() + ":" + getInstrumentId() +
			"/" + getTickref();
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + instrumentId;
		result = prime * result + tickref;
		result = prime * result
				+ ((srcElement == null) ? 0 : srcElement.hashCode());
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
		BaseLog other = (BaseLog) obj;
		if (instrumentId != other.instrumentId)
			return false;
		if (tickref != other.tickref)
			return false;
		if (srcElement == null) {
			if (other.srcElement != null)
				return false;
		} else if (!srcElement.equals(other.srcElement))
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		return true;
	}
	
	

}

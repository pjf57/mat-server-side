package com.pjf.mat.api.logging;

import com.pjf.mat.api.Element;
import com.pjf.mat.api.OutputPort;
import com.pjf.mat.api.TimeOrdered;
import com.pjf.mat.api.Timestamp;

/**
 * Base class for logs
 * 
 * Allows unified view of some attributes of different log types
 * 
 * @author pjf
 *
 */
public class BaseLog implements TimeOrdered {
	protected Timestamp timestamp;
	private final int instrumentId;
	private final int tickref;
	private final Element srcElement;		// null if missing
	private final OutputPort sourcePort;	// null if missing
	private final String srcName;			// alternative to srcElement
	private final String srcPortName;		// alternative to sourcePort
	
	public BaseLog(Timestamp timestamp, int instrumentId, int tickref, Element srcElement) {
		this.timestamp = timestamp;
		this.instrumentId = instrumentId;
		this.tickref = tickref;
		this.srcElement = srcElement;
		this.sourcePort = null;
		this.srcName = null;
		this.srcPortName = null;
	}

	public BaseLog(Timestamp timestamp, int instrumentId, int tickref, 
			Element srcElement, OutputPort sourcePort) {
		this.timestamp = timestamp;
		this.instrumentId = instrumentId;
		this.tickref = tickref;
		this.srcElement = srcElement;
		this.sourcePort = sourcePort;
		this.srcName = null;
		this.srcPortName = null;
	}

	public BaseLog(Timestamp timestamp, int instrumentId, int tickref, 
			String srcElement, String sourcePort) {
		this.timestamp = timestamp;
		this.instrumentId = instrumentId;
		this.tickref = tickref;
		this.srcElement = null;
		this.sourcePort = null;
		this.srcName = srcElement;
		this.srcPortName = sourcePort;
	}

	/**
	 * Construct a baselog with only a timestamp
	 * 
	 * @param timestamp
	 */
	public BaseLog(Timestamp timestamp) {
		this.timestamp = timestamp;
		this.instrumentId = 0;
		this.tickref = 0;
		this.srcElement = null;
		this.sourcePort = null;
		this.srcName = null;
		this.srcPortName = null;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public OutputPort getSourcePort() {
		return sourcePort;
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
	
	/**
	 * @return type of log
	 */
	public String getType() {
		return "UNK";
	}
	
	@Override
	public int compareTo(TimeOrdered o) {
		return timestamp.compareTo(o.getTimestamp());
	}

	@Override
	public String toString() {
		String ses = getSourceElementName();
		return getTimestamp() + ":" + ses + ":" + getInstrumentId() +
			"/" + getTickref();
	}

	/**
	 * @return source element name or ""
	 */
	public String getSourceElementName() {
		String s = "";
		if (srcElement != null) {
			s = srcElement.getShortName();
		} else if (srcName != null) {
			s = srcName;
		}
		return s;
	}

	/**
	 * @return source port name or ""
	 */
	public String getSourcePortName() {
		String s = "";
		if (sourcePort != null) {
			s = sourcePort.getName();
		} else if (srcPortName != null) {
			s = srcPortName;
		}
		return s;
	}

	/**
	 * Template method
	 * 
	 * @return display value
	 */
	public String getDispValue() {
		return "";
	}
	


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + instrumentId;
		result = prime * result
				+ ((sourcePort == null) ? 0 : sourcePort.hashCode());
		result = prime * result
				+ ((srcElement == null) ? 0 : srcElement.hashCode());
		result = prime * result + tickref;
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
		if (sourcePort == null) {
			if (other.sourcePort != null)
				return false;
		} else if (!sourcePort.equals(other.sourcePort))
			return false;
		if (srcElement == null) {
			if (other.srcElement != null)
				return false;
		} else if (!srcElement.equals(other.srcElement))
			return false;
		if (tickref != other.tickref)
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		return true;
	}


}

package com.pjf.mat.api.comms;

/**
 * Container class for the raw status of a CB
 * 
 * @author pjf
 *
 */
public class CBRawStatus {
	private final int id;
	private final String typeStr;
	private final String basisStateStr;
	private final int intState;
	private final int evtCount;
	
	public CBRawStatus(int id, String typeStr, String basisStateStr, int intState,
			int evtCount) {
		this.id = id;
		this.typeStr = typeStr;
		this.basisStateStr = basisStateStr;
		this.intState = intState;
		this.evtCount = evtCount;
	}

	public int getId() {
		return id;
	}

	public String getTypeStr() {
		return typeStr;
	}

	public String getBasisStateStr() {
		return basisStateStr;
	}

	public int getIntState() {
		return intState;
	}

	public int getEvtCount() {
		return evtCount;
	}

	@Override
	public String toString() {
		return "CBStatus [id=" + id + ", typeStr=" + typeStr
				+ ", basisStateStr=" + basisStateStr + ", intState=" + intState
				+ ", evtCount=" + evtCount + "]";
	}

}

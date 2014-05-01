package com.pjf.mat.api.comms;

import com.pjf.mat.api.Timestamp;

/**
 * Container for event logs received from the Cheetah Framework
 * 
 * @author pjf
 *
 */
public class EvtLogRaw {
	private final Timestamp ts;
	private final int src;
	private final int port;
	private final int instrId;
	private final int tickref;
	private final int data;
	
	public EvtLogRaw(Timestamp ts, int src, int port, int instrId, int tickref,
			int data) {
		this.ts = ts;
		this.src = src;
		this.port = port;
		this.instrId = instrId;
		this.tickref = tickref;
		this.data = data;
	}

	public Timestamp getTs() {
		return ts;
	}

	public int getSrc() {
		return src;
	}

	public int getPort() {
		return port;
	}

	public int getInstrId() {
		return instrId;
	}

	public int getTickref() {
		return tickref;
	}

	public int getData() {
		return data;
	}

	@Override
	public String toString() {
		return "EvtLog [ts=" + ts + ", src=" + src + ", port=" + port
				+ ", instrId=" + instrId + ", tickref=" + tickref + ", data="
				+ data + "]";
	}


}

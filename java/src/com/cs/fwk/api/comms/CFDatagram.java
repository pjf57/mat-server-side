package com.cs.fwk.api.comms;

/**
 * Container for holding a Cheetah Framework Datagram
 * This is a unit of transmission between Host and the Cheetah Framework in the FPGA
 * 
 * @author pjf
 */
public class CFDatagram {
	private int dstPort;
	private int srcPort;
	private byte[] data;
	
	public CFDatagram(int dstPort, byte[] data) {
		this.dstPort = dstPort;
		this.srcPort = 0;
		this.data = data;
	}

	public int getDstPort() {
		return dstPort;
	}

	public byte[] getData() {
		return data;
	}

	public void setSrcPort(int srcPort) {
		this.srcPort = srcPort;
	}

	public int getSrcPort() {
		return srcPort;
	}
	
}

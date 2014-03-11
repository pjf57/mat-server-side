package com.pjf.mat.api.comms;

public class RxPkt {
	private int port;
	private byte[] data;
	
	public RxPkt(int port, byte[] data) {
		this.port = port;
		this.data = data;
	}

	public int getPort() {
		return port;
	}

	public byte[] getData() {
		return data;
	}

}

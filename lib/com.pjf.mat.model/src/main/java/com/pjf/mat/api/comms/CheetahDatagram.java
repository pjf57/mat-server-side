package com.pjf.mat.api.comms;

public class CheetahDatagram {
	private int port;
	private byte[] data;
	
	public CheetahDatagram(int port, byte[] data) {
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

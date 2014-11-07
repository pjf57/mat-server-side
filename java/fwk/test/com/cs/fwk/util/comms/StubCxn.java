package com.cs.fwk.util.comms;

import java.io.IOException;

import com.cs.fwk.api.comms.CFDatagram;
import com.cs.fwk.api.comms.CxnInt;
import com.cs.fwk.api.comms.LoopbackInt;

public class StubCxn implements CxnInt {

	@Override
	public void setLoopbackCallback(LoopbackInt cb) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void send(CFDatagram datagram) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public CFDatagram rcv() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMtuSize() {
		// TODO Auto-generated method stub
		return 0;
	}

}

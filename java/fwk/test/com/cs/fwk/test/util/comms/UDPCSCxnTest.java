package com.cs.fwk.test.util.comms;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;


import com.cs.fwk.api.comms.CFDatagram;
import com.cs.fwk.util.comms.UDPCxn;

public class UDPCSCxnTest {
	private UDPCxn cxn;
	
	public UDPCSCxnTest() throws SocketException, UnknownHostException {
		cxn = new UDPCxn("192.168.2.9");		
	}

	public void go() throws IOException {
		byte[] d = new byte[]{1,0,-61,0,0,0,0,0};
		CFDatagram dgs = new CFDatagram(2000,d);
		cxn.send(dgs);
		CFDatagram dg = cxn.rcv();
		byte[] rep = dg.getData();
		String reply = new String(rep);
		System.out.println("Got [" + reply + "]");
	}
	

	public static void main(String[] args) {
		UDPCSCxnTest ut;
		try {
			ut = new UDPCSCxnTest();
			ut.go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}

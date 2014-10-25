package com.cs.fwk.test.util.comms;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;


import com.cs.fwk.api.comms.CFDatagram;
import com.cs.fwk.util.comms.UDPCxn;

public class UDPTest {
	private UDPCxn cxn;
	
	public UDPTest() throws SocketException, UnknownHostException {
		cxn = new UDPCxn("192.168.2.25");		
		//		cxn = new UDPCxn("255.255.255.255");		
	}

	public void go() throws IOException {
		String fix1 = "P=45~34=201~18=23~";
		byte[] data = fixSend(fix1);
		CFDatagram pkt = new CFDatagram(2001,data);
		cxn.send(pkt);
		CFDatagram dg = cxn.rcv();
		byte[] rep = dg.getData();
		String reply = new String(rep);
		System.out.println("Got [" + reply + "]");
	}
	
	public byte[] fixSend(String str) throws IOException {
		String s1 = str.replace('~','\001');
		byte[] data = s1.getBytes();
		return data;
	}


	public static void main(String[] args) {
		UDPTest ut;
		try {
			ut = new UDPTest();
			ut.go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}

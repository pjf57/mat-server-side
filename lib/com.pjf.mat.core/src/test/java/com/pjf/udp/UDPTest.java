package com.pjf.udp;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.pjf.mat.api.comms.CheetahDatagram;
import com.pjf.mat.api.comms.CxnInt;
import com.pjf.mat.util.comms.UDPCxn;


public class UDPTest {
	private CxnInt cxn;
	
	public UDPTest() throws SocketException, UnknownHostException {
		cxn = new UDPCxn("192.168.0.25");
	}

	public void go() throws IOException {
		CheetahDatagram pkt = cxn.rcv();
		byte[] rep = pkt.getData();
		String reply = new String(rep);
		System.out.println("Got [" + reply + "]");
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

package com.pjf.udp;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.pjf.mat.api.comms.RxPkt;
import com.pjf.mat.util.comms.UDPCxn;


public class UDPTest {
	private UDPCxn cxn;
	
	public UDPTest() throws SocketException, UnknownHostException {
		cxn = new UDPCxn("192.168.0.25");		
		//		cxn = new UDPCxn("255.255.255.255");		
	}

	public void go() throws IOException {
		String fix1 = "P=45~34=201~18=23~";
		cxn.fixSend(fix1, 2000, true);
		RxPkt pkt = cxn.rcv();
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

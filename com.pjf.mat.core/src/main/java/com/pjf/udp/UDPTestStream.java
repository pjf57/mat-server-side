package com.pjf.udp;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;


public class UDPTestStream {
	private UDPCxn cxn;
	private Listener listener;
	private int sent;
	private int received;
	
	class Listener extends Thread {
		
		@Override
		public void run() {
			byte[] rep;
			while (true) {
				try {
					rep = cxn.rcv();
					String reply = new String(rep);
					System.out.println("Got [" + reply + "]");
					received++;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public UDPTestStream() throws SocketException, UnknownHostException {
		cxn = new UDPCxn("192.168.0.25");
		sent = 0;
		received = 0;
		listener = new Listener();
		listener.start();
	}

	public void go() throws IOException, InterruptedException {
		String fix1 = "1=45~34=";
		String fix2 = "~18=23~";
		for (int price=225; price>=150; price--) {
			StringBuffer fixmsg = new StringBuffer(fix1);
			fixmsg.append(Integer.toString(price));
			fixmsg.append(fix2);
			System.out.println("Sending price tick " + price);
			sent++;
			cxn.fixSend(fixmsg.toString(), 2000, false);
		}
		System.out.println("--- Sent = " + sent);
		Thread.sleep(2000);
		System.out.println("--- Received = " + received);
	}


	public static void main(String[] args) {
		UDPTestStream ut;
		try {
			ut = new UDPTestStream();
			ut.go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}

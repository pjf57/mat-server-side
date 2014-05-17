package com.cs.fwk.test.util;


import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;

import com.cs.fwk.api.Cmd;
import com.cs.fwk.api.Element;
import com.cs.fwk.api.comms.CFDatagram;
import com.cs.fwk.api.comms.CxnInt;
import com.cs.fwk.api.comms.LoopbackInt;
import com.cs.fwk.api.util.HwStatus;
import com.cs.fwk.core.sys.MATComms;
import com.cs.fwk.util.comms.UDPCxn;


public class UDPSktCommsTest extends TestCase implements LoopbackInt {
	private MATComms skt;
	
	public class Skt extends MATComms {

		public Skt(CxnInt cxn) throws SocketException, UnknownHostException {
			super(3000);
			setCxn(cxn);
		}

		@Override
		public void sendConfig(List<Element> collection) throws Exception {
		}

		@Override
		public void requestStatus() throws Exception {
		}

		@Override
		public void requestStatus(Element element) throws Exception {
		}

		@Override
		public void requestLkuAuditLogs() throws Exception {
		}

		@Override
		public void requestRtrAuditLogs() throws Exception {
		}

		@Override
		public void sendCmd(Cmd cmd) throws Exception {
		}

		@Override
		public long getHWSignature() throws Exception {
			return 0;
		}

		@Override
		public void synchroniseClock(long syncOrigin) throws Exception {
		}

		@Override
		public void resetCounters() throws IOException {
		}

		@Override
		public HwStatus getHWStatus() {
			return null;
		}

		@Override
		public void resetConfig(int elId) throws IOException {
		}
		
	}
	
	@Override
	public void setUp() throws SocketException, UnknownHostException {
		BasicConfigurator.configure();
		CxnInt cxn = new UDPCxn("192.168.0.7");		
		skt = new Skt(cxn);
		skt.subscribeIncomingMsgs(5000,this);
	}
	
	public void testCxnSendReceive() throws IOException {
		CxnInt cxn = skt.getCxn();
		CFDatagram data = new CFDatagram(5000,"Test Data".getBytes());		
		cxn.send(data);
	}

	@Override
	public void injectLoopbackMsg(int port, byte[] msg) {
		System.out.println("Received msg on port " + port + ", " + msg.length + " bytes.");
	}

}

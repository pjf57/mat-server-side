package com.pjf.mat.util.comms;


import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.log4j.BasicConfigurator;

import com.pjf.mat.api.Cmd;
import com.pjf.mat.api.Element;
import com.pjf.mat.api.Status;
import com.pjf.mat.api.comms.CFDatagram;
import com.pjf.mat.api.comms.CxnInt;
import com.pjf.mat.api.comms.LoopbackInt;
import com.pjf.mat.api.util.HwStatus;

import junit.framework.TestCase;


public class UDPSktCommsTest extends TestCase implements LoopbackInt {
	private RComms skt;
	
	public class Skt extends RComms {

		public Skt(CxnInt cxn) throws SocketException, UnknownHostException {
			super(cxn);
		}

		@Override
		public void sendConfig(List<Element> collection) throws Exception {
		}

		@Override
		public Status requestStatus() throws Exception {
			return null;
		}

		@Override
		public Status requestStatus(Element element) throws Exception {
			return null;
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

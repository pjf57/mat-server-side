package com.pjf.mat.util.comms;


import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collection;

import org.apache.log4j.BasicConfigurator;

import com.pjf.mat.api.Cmd;
import com.pjf.mat.api.Element;
import com.pjf.mat.api.Status;
import com.pjf.mat.api.comms.CxnInt;
import com.pjf.mat.api.comms.InMsgCallbackInt;
import com.pjf.mat.api.util.HwStatus;

import junit.framework.TestCase;


public class UDPSktCommsTest extends TestCase implements InMsgCallbackInt {
	private UDPSktComms skt;
	
	public class Skt extends UDPSktComms {

		public Skt(String ip) throws SocketException, UnknownHostException {
			super(ip);
		}

		@Override
		public void sendConfig(Collection<Element> collection) throws Exception {
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
		skt = new Skt("192.168.0.7");
		skt.subscribeIncomingMsgs(5000,this);
	}
	
	public void testCxnSendReceive() throws IOException {
		CxnInt cxn = skt.getCxn();
		byte[] data = "Test Data".getBytes();
		cxn.send(data, 5000);
	}

	@Override
	public void processIncomingMsg(int port, byte[] msg) {
		System.out.println("Received msg on port " + port + ", " + msg.length + " bytes.");
	}

}

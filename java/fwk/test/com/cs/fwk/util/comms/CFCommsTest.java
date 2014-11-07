package com.cs.fwk.util.comms;

import java.util.List;

import com.cs.fwk.api.MatElementDefs;
import com.cs.fwk.api.comms.CBRawStatus;
import com.cs.fwk.api.comms.CFCallback;
import com.cs.fwk.api.comms.EvtLogRaw;
import com.cs.fwk.api.comms.LkuAuditRawLog;
import com.cs.fwk.api.comms.RtrAuditRawLog;
import com.cs.fwk.api.util.HwStatus;
import com.cs.fwk.util.Conversion;

import junit.framework.TestCase;

public class CFCommsTest extends TestCase implements CFCallback {
	private HwStatus rx_status;
	
	public void testProcessCFStatusMsgV1() throws Exception {
		byte[] msg = Conversion.hextTobyte("04534286717613248301922301");
		rx_status = null;
		CFComms comms = new CFComms(new StubCxn(),1000);
		comms.setCallback(this);
		comms.handleIncomingMsg(MatElementDefs.CS_PORT_STATUS, msg);
		assertNotNull(rx_status);
		assertFalse(rx_status.hasConfigStatus());
		assertEquals("23.01",rx_status.getCf_version());
		assertEquals(5999505475481314435L,rx_status.getHwSig());
		assertEquals(402,rx_status.getMicrotickPeriod());		
	}

	public void testProcessCFStatusMsgV2() throws Exception {
		byte[] msg = Conversion.hextTobyte("045342867176132483019223010512345678");
		rx_status = null;
		CFComms comms = new CFComms(new StubCxn(),1000);
		comms.setCallback(this);
		comms.handleIncomingMsg(MatElementDefs.CS_PORT_STATUS, msg);
		assertNotNull(rx_status);
		assertTrue(rx_status.hasConfigStatus());
		assertEquals("23.01",rx_status.getCf_version());
		assertEquals(5999505475481314435L,rx_status.getHwSig());
		assertEquals(402,rx_status.getMicrotickPeriod());		
		assertEquals(305419896,rx_status.getCfgEventCount());
		assertTrue(rx_status.isInitialised());
		assertFalse(rx_status.isConfigReady());
		assertTrue(rx_status.hadConfigError());
	}

	@Override
	public void processCFStatus(HwStatus st) {
		System.out.println("Received status: " + st);
		rx_status = st;
	}

	@Override
	public void processCBStatus(List<CBRawStatus> statusList) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processEvtLogs(List<EvtLogRaw> logs) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processLkuLogs(List<LkuAuditRawLog> logs) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processRtrLogs(List<RtrAuditRawLog> logs) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processUnknownMsg(int destPort, byte[] msg) {
		// TODO Auto-generated method stub
		
	}

}

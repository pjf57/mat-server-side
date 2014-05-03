package com.pjf.mat.examples;

import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.pjf.mat.api.comms.CBRawStatus;
import com.pjf.mat.api.comms.CFCallback;
import com.pjf.mat.api.comms.CFCommsInt;
import com.pjf.mat.api.comms.CxnInt;
import com.pjf.mat.api.comms.EvtLogRaw;
import com.pjf.mat.api.comms.LkuAuditRawLog;
import com.pjf.mat.api.comms.RtrAuditRawLog;
import com.pjf.mat.api.util.HwStatus;
import com.pjf.mat.util.Conversion;
import com.pjf.mat.util.comms.CFComms;
import com.pjf.mat.util.comms.UDPCxn;

/**
 * First steps - request the HW Sig from the FPGA and print it out, request CB status, and print.
 * 
 * @author pjf
 *
 */
public class CheetahFirstSteps implements CFCallback {
	private final static Logger logger = Logger.getLogger(CheetahFirstSteps.class);
	private CFCommsInt comms = null;
	
	private void run() throws Exception {
		logger.info("Starting ...");
		CxnInt cxn = new UDPCxn("192.168.2.9");
		comms = new CFComms(cxn,2000);
		comms.setCallback(this);
		logger.info("Request Cheetah Framework Status");
		comms.requestCFStatus();
		Thread.sleep(1000);
		logger.info("Request CB Status");
		comms.requestStatus();
		Thread.sleep(1000);
		logger.info("Waiting ...");
		Thread.sleep(5000);
		comms.shutdown();
		logger.info("Exiting");
	}


	@Override
	public void processCFStatus(HwStatus st) {
		logger.info("processCFStatus(): " + st);
	}


	@Override
	public void processCBStatus(List<CBRawStatus> statusList) {
		logger.info("processCBStatus(): " + statusList.size() + " status reports received:");
		for (CBRawStatus stat : statusList) {
			logger.info("  " + stat);
		}	
	}


	@Override
	public void processEvtLogs(List<EvtLogRaw> logs) {
		logger.info("processEvtLogs(): " + logs.size() + " logs received:");
		for (EvtLogRaw log : logs) {
			logger.info("  " + log);
		}	
	}


	@Override
	public void processLkuLogs(List<LkuAuditRawLog> logs) {
		logger.info("processEvtLogs(): " + logs.size() + " logs received:");
		for (LkuAuditRawLog log : logs) {
			logger.info("  " + log);
		}	
	}


	@Override
	public void processRtrLogs(List<RtrAuditRawLog> logs) {
		logger.info("processEvtLogs(): " + logs.size() + " logs received:");
		for (RtrAuditRawLog log : logs) {
			logger.info("  " + log);
		}	
	}


	@Override
	public void processUnknownMsg(int destPort, byte[] msg) {
		logger.warn("processUnknownMsg(): port= " + destPort + " msg=" + Conversion.toHexString(msg));
	}

	public static void main(String[] args) {
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.INFO);
		CheetahFirstSteps sys = new CheetahFirstSteps();
		try {
			sys.run();
		} catch (Exception e) {
			logger.error("Outer error catcher: " + e.getMessage());
			e.printStackTrace();
		}		
	}

}

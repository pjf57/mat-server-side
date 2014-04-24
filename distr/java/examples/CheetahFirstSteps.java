package com.pjf.mat.examples;

import java.util.Collection;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.pjf.mat.api.Element;
import com.pjf.mat.api.NotificationCallback;
import com.pjf.mat.api.TimeOrdered;
import com.pjf.mat.api.comms.Comms;
import com.pjf.mat.api.logging.EventLog;
import com.pjf.mat.api.logging.LkuAuditLog;
import com.pjf.mat.api.logging.OrderLog;
import com.pjf.mat.api.logging.RtrAuditLog;
import com.pjf.mat.sys.UDPComms;

/**
 * First steps - request the HW Sig from the FPGA and print it out
 * 
 * @author pjf
 *
 */
public class CheetahFirstSteps implements NotificationCallback {
	private final static Logger logger = Logger.getLogger(CheetahFirstSteps.class);
	private Comms comms = null;
	
	private void run() throws Exception {
		logger.info("Starting ...");
		comms = new UDPComms("192.168.2.9",2000);
		comms.addNotificationSubscriber(this);
		logger.info("Request Signature");
		comms.getHWSignature();
		logger.info("Request Status");
		comms.requestStatus();
		logger.info("Waiting ...");
		Thread.sleep(5000);
		logger.info("Exiting");
	}


	@Override
	public void notifyEventLog(EventLog evt) {
		logger.info("notifyEventLog(): " + evt);
	}

	@Override
	public void notifyElementStatusUpdate(Collection<Element> cbs) {
		logger.info("Status update received for " + cbs.size() + " CBs:");
		for (Element cb : cbs) {
			logger.info("Status Update: cb=" + cb.getId() +
					" type=" + cb.getType() +
					" state=" + cb.getElementStatus());	
		}
	}

	@Override
	public void notifyLkuAuditLogReceipt(Collection<LkuAuditLog> logs) {
		for (LkuAuditLog log : logs) {
			logger.info("notifyLkuAuditLogReceipt(): " + log);
		}
	}

	@Override
	public void notifyRtrAuditLogReceipt(Collection<RtrAuditLog> logs) {
		for (RtrAuditLog log : logs) {
			logger.info("notifyRtrAuditLogReceipt(): " + log);
		}
	}

	@Override
	public void notifyOrderReceipt(OrderLog order) {
		logger.warn("notifyOrderReceipt(): " + order);
	}

	@Override
	public void notifyUnifiedEventLog(List<TimeOrdered> logs) {
		logger.error("notifyUnifiedEventLog(): - not supported");
	}


	public static void main(String[] args) {
		BasicConfigurator.configure();
		logger.info("startup");
		CheetahFirstSteps sys = new CheetahFirstSteps();
		try {
			sys.run();
		} catch (Exception e) {
			logger.error("Outer error catcher: " + e.getMessage());
			e.printStackTrace();
		}		
	}


}

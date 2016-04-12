package com.cs.fwk.core.examples;

import java.io.FileInputStream;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.cs.fwk.api.Element;
import com.cs.fwk.api.MatApi;
import com.cs.fwk.api.NotificationCallback;
import com.cs.fwk.api.TimeOrdered;
import com.cs.fwk.api.comms.CxnInt;
import com.cs.fwk.api.comms.MATCommsApi;
import com.cs.fwk.api.logging.EventLog;
import com.cs.fwk.api.logging.LkuAuditLog;
import com.cs.fwk.api.logging.OrderLog;
import com.cs.fwk.api.logging.RtrAuditLog;
import com.cs.fwk.core.impl.MatInterface;
import com.cs.fwk.core.impl.MatInterfaceModel;
import com.cs.fwk.core.sys.MATComms;
import com.cs.fwk.util.comms.UDPCxn;

public class CheetahBaseExample implements NotificationCallback {
	protected final static Logger logger = Logger.getLogger(CheetahExample2mfd.class);
	protected MatApi mat = null;
	protected MATCommsApi comms = null;
	protected boolean running = true;

	/**
	 * Method to call to initialise the system.
	 * 
	 * @param propsResource	resource file for HW properties
	 * @param hwIPAddr - IP address of hardware
	 * @param hwPortNum - port number of hardware
	 * @throws Exception
	 */
	protected void init(String propsResource, String hwIPAddr, int hwPortNum) throws Exception {
		Properties props = new Properties();
		props.load(new FileInputStream(propsResource));
		CxnInt cxn = new UDPCxn(hwIPAddr);
		comms = new MATComms(cxn,hwPortNum);
		comms.addNotificationSubscriber(this);
		MatInterfaceModel model = new MatInterfaceModel(props);
		mat = new MatInterface(comms,model);
		comms.setMat(mat);
		mat.checkHWSignature();
		mat.resetErrorState();
		logger.info("init(): configure the HW");
		mat.putIntoConfigMode();
		configure(mat);
		mat.syncClock(0);
		logger.info("-----");	
		mat.requestHWStatus();		
	}

	protected void configure(MatApi mat2) throws Exception {
		logger.warn("configure(): NEED TO OVERRIDE THIS METHOD");
	}

	protected void shutdown() {
		logger.info("Shutting down ...");
		if (mat != null) {
			mat.shutdown();
		}
	}

	protected void sleep(int s) {
		try {
			Thread.sleep(s);
		} catch (InterruptedException e) {
			// ignore
		}
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


}

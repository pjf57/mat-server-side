package com.cs.fwk.core.examples;

import java.io.FileInputStream;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.cs.fwk.api.Cmd;
import com.cs.fwk.api.Element;
import com.cs.fwk.api.MatApi;
import com.cs.fwk.api.MatElementDefs;
import com.cs.fwk.api.NotificationCallback;
import com.cs.fwk.api.TimeOrdered;
import com.cs.fwk.api.comms.MATCommsApi;
import com.cs.fwk.api.comms.CxnInt;
import com.cs.fwk.api.gridattr.GridAttribute;
import com.cs.fwk.api.logging.EventLog;
import com.cs.fwk.api.logging.LkuAuditLog;
import com.cs.fwk.api.logging.OrderLog;
import com.cs.fwk.api.logging.RtrAuditLog;
import com.cs.fwk.core.impl.MatInterface;
import com.cs.fwk.core.impl.MatInterfaceModel;
import com.cs.fwk.core.impl.element.BasicCmd;
import com.cs.fwk.core.sys.MATComms;
import com.cs.fwk.util.comms.UDPCxn;

public class CheetahExampleSIB implements NotificationCallback {
	private final static Logger logger = Logger.getLogger(CheetahExampleSIB.class);
	private MatApi mat = null;
	private MATCommsApi comms = null;
	private boolean running = true;
	
	private void run() throws Exception {
		int runSeconds = 0;
		// initialise model with specified palette
		init("resources/mat.32sib01.csp","192.168.2.9",2000);
		logger.info("Example runtime processing");
		sleep(1000);
		// start the TG1
		Cmd start = new BasicCmd(mat.getModel().getElement(2), "Start", MatElementDefs.EL_TG1_C_START);
		comms.sendCmd(start);
		while (running) {
			sleep(1000);
			mat.requestHWStatus();
			runSeconds++;
			if (runSeconds >= 10) {
				logger.info("Time up - ending the run");
				running = false;
			}
		}
		// Move the HW to config mode so that it's not running anymore
		mat.putIntoConfigMode();
		shutdown();
	}

	public void shutdown() {
		logger.info("Shutting down ...");
		if (mat != null) {
			mat.shutdown();
		}
	}

	/**
	 * Method to call to initialise the system.
	 * 
	 * @param propsResource	resource file for HW properties
	 * @param hwIPAddr - IP address of hardware
	 * @param hwPortNum - port number of hardware
	 * @throws Exception
	 */
	private void init(String propsResource, String hwIPAddr, int hwPortNum) throws Exception {
		Properties props = new Properties();
		props.load(new FileInputStream(propsResource));
		CxnInt cxn = new UDPCxn(hwIPAddr);
		comms = new MATComms(cxn,hwPortNum);
		comms.addNotificationSubscriber(this);
		MatInterfaceModel model = new MatInterfaceModel(props);
		mat = new MatInterface(comms,model);
		comms.setMat(mat);
		mat.checkHWSignature();
		logger.info("init(): configure the HW");
		mat.putIntoConfigMode();
		configure(mat);
		logger.info("mat is: " + mat);
		mat.configureHW();
		mat.syncClock(0);
		logger.info("-----");	
		mat.requestHWStatus();		
	}
	

	/**
	 * Configure the algo - set parameters and wire up the cheetah blocks
	 * 
	 * @param mat - MAT API
	 * @throws Exception
	 */
	private void configure(MatApi mat) throws Exception {
		Element sys = mat.getModel().getElement(0);
		Element lgr = mat.getModel().getElement(1);
		Element tg1 = mat.getModel().getElement(2);
		Element mfd = mat.getModel().getElement(30);
		Element sib = mat.getModel().getElement(26);

		// configure system attributes
		sys.getAttribute("lookup_audit_autosend").setValue("4");
		sys.getAttribute("router_audit_autosend").setValue("4");
		// configure TG1
		tg1.getAttribute("len").setValue("10");
		tg1.getAttribute("gap").setValue("5");
		tg1.getAttribute("initial_value").setValue("50");
		tg1.getAttribute("p1").setValue("1");
		
		// configure MFD
		mfd.getAttribute("trade").setValue("0");
		mfd.getAttribute("bid").setValue("1");
		mfd.getAttribute("ask").setValue("2");
		GridAttribute symGrid = (GridAttribute) mfd.getAttribute("symbols");
		symGrid.add(new String[]{"IBM","3"});
		symGrid.add(new String[]{"APPL","4"});		

		// configure SIB
		sib.getAttribute("OPCFG").setValue("0123");
		GridAttribute sibGrid = sib.getGridAttribute("SynthInstr");
		sibGrid.add(new String[]{"20","3","4","2",""});
		sibGrid.add(new String[]{"21","5","6","3",""});
		// connect mfd.2 to sib.0 and tg1.0 to sib.1
		sib.getInputs().get(0).connectTo(mfd.getOutputs().get(2));
		sib.getInputs().get(1).connectTo(tg1.getOutputs().get(0));

		// logger connections
		lgr.getInputs().get(0).connectTo(sib.getOutputs().get(0));
	}

	private void sleep(int s) {
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


	public static void main(String[] args) {
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.INFO);
		logger.info("startup");
		CheetahExampleSIB sys = new CheetahExampleSIB();
		try {
			sys.run();
		} catch (Exception e) {
			logger.error("Outer error catcher: " + e.getMessage());
			e.printStackTrace();
			sys.shutdown();
		}		
	}


}

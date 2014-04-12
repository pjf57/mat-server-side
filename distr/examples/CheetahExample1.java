package com.pjf.mat.examples;

import java.io.FileInputStream;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.pjf.mat.api.Element;
import com.pjf.mat.api.MatApi;
import com.pjf.mat.api.NotificationCallback;
import com.pjf.mat.api.TimeOrdered;
import com.pjf.mat.api.comms.Comms;
import com.pjf.mat.api.logging.EventLog;
import com.pjf.mat.api.logging.LkuAuditLog;
import com.pjf.mat.api.logging.OrderLog;
import com.pjf.mat.api.logging.RtrAuditLog;
import com.pjf.mat.impl.MatInterface;
import com.pjf.mat.impl.MatInterfaceModel;
import com.pjf.mat.sys.UDPComms;

public class CheetahExample1 implements NotificationCallback {
	private final static Logger logger = Logger.getLogger(CheetahExample1.class);
	private MatApi mat = null;
	private Comms comms = null;
	private boolean running = true;
	
	private void run() throws Exception {
		int runSeconds = 0;
		// initialise model with specified palette
		init("resources/mat.32v83.csp","192.168.2.9",2000);
		logger.info("Example runtime processing");
		while (running) {
			sleep(1000);
			mat.requestHWStatus();
			runSeconds++;
			if (runSeconds >= 10) {
				logger.info("Time up - ending the run");
				running = false;
			}
		}
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
		comms = new UDPComms(hwIPAddr,hwPortNum);
		comms.addNotificationSubscriber(this);
		MatInterfaceModel model = new MatInterfaceModel(props);
		mat = new MatInterface(comms,model);
		comms.setMat(mat);
		mat.checkHWSignature();
		if (System.getProperty("active") != null) {
			logger.info("init(): active mode - so will configure the HW");
			mat.putIntoConfigMode();
			configure(mat);
			logger.info("mat is: " + mat);
			mat.configureHW();
			mat.syncClock(0);
		} else {
			logger.info("init(): passive mode - so will NOT configure the HW");
		}
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
		Element mfd = mat.getModel().getElement(30);
		Element macd = mat.getModel().getElement(10);
		Element lgr = mat.getModel().getElement(1);
		Element rmo = mat.getModel().getElement(31);
		Element logicBuy = mat.getModel().getElement(15);
		Element logicSell = mat.getModel().getElement(16);

		// configure system attributes
		sys.getAttribute("lookup_audit_autosend").setValue("4");
		sys.getAttribute("router_audit_autosend").setValue("4");
		// configure MFD 
		mfd.getAttribute("udp_listen_port").setValue("15000");
		mfd.getAttribute("market_ID").setValue("1");
		mfd.getAttribute("trade").setValue("0");	// trade to this op
		mfd.getAttribute("bid").setValue("1");		// bid to this op
		mfd.getAttribute("ask").setValue("2");		// ask to this op
		mfd.getAttribute("symbols").setValue("IBM:5,APPL:7");		
		// Configure MACD
		macd.getAttribute("FAST_EMA_len").setValue("3");	// 
		macd.getAttribute("SLOW_EMA_len").setValue("7");	// 
		macd.getAttribute("SIGNAL_EMA_len").setValue("3");	// 
		macd.getAttribute("OP_ENABLE_MASK").setValue("4");	// enable hist op
		macd.getInputs().get(0).connectTo(mfd.getOutputs().get(2));
		// Configure logic 1
		logicBuy.getAttribute("Z").setValue("P");
		logicBuy.getAttribute("P").setValue("A>K1");
		logicBuy.getAttribute("k1").setValue("0.05");
		logicBuy.getInputs().get(0).connectTo(macd.getOutputs().get(2));
		logicSell.getAttribute("Z").setValue("P");
		logicSell.getAttribute("P").setValue("A<K1");
		logicSell.getAttribute("k1").setValue("0");	 	
		logicSell.getInputs().get(0).connectTo(macd.getOutputs().get(2));
		// Configure RMO
		rmo.getAttribute("udp_ip").setValue("0C0A80205");	// 
		rmo.getAttribute("udp_port").setValue("3500");	// 
		rmo.getAttribute("min_vol").setValue("100");	// 
		rmo.getAttribute("max_vol").setValue("500");	// 
		rmo.getInputs().get(0).connectTo(logicBuy.getOutputs().get(0));
		rmo.getInputs().get(1).connectTo(logicSell.getOutputs().get(0));

		// logger connections
		lgr.getInputs().get(0).connectTo(rmo.getOutputs().get(0));
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
		logger.info("startup");
		CheetahExample1 sys = new CheetahExample1();
		try {
			sys.run();
		} catch (Exception e) {
			logger.error("Outer error catcher: " + e.getMessage());
			e.printStackTrace();
			sys.shutdown();
		}		
	}


}

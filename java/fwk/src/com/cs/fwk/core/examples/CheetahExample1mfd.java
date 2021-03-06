package com.cs.fwk.core.examples;

import java.io.FileInputStream;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.cs.fwk.api.Element;
import com.cs.fwk.api.MatApi;
import com.cs.fwk.api.NotificationCallback;
import com.cs.fwk.api.TimeOrdered;
import com.cs.fwk.api.comms.MATCommsApi;
import com.cs.fwk.api.comms.CxnInt;
import com.cs.fwk.api.logging.EventLog;
import com.cs.fwk.api.logging.LkuAuditLog;
import com.cs.fwk.api.logging.OrderLog;
import com.cs.fwk.api.logging.RtrAuditLog;
import com.cs.fwk.core.impl.MatInterface;
import com.cs.fwk.core.impl.MatInterfaceModel;
import com.cs.fwk.core.sys.MATComms;
import com.cs.fwk.util.comms.UDPCxn;

public class CheetahExample1mfd implements NotificationCallback {
	private final static Logger logger = Logger.getLogger(CheetahExample1mfd.class);
	private MatApi mat = null;
	private MATCommsApi comms = null;
	private boolean running = true;
	
	private void run() throws Exception {
		int runSeconds = 0;
		// initialise model with specified palette
		init("resources/mat.32v02.csp","192.168.2.9",2000);
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
		macd.getInput("input").connectTo(mfd.getOutput("MFD.tick.2"));
		// Configure logic 1
		logicBuy.getAttribute("Z").setValue("P");
		logicBuy.getAttribute("P").setValue("A>K1");
		logicBuy.getAttribute("k1").setValue("0.15");
		logicBuy.getInput("A").connectTo(macd.getOutput("macd"));
		logicSell.getAttribute("Z").setValue("P");
		logicSell.getAttribute("P").setValue("A<K1");
		logicSell.getAttribute("k1").setValue("-0.06");	 	
		logicSell.getInput("A").connectTo(macd.getOutput("macd"));
		// Configure RMO
		rmo.getAttribute("udp_ip").setValue("0C0A80205");	// 
		rmo.getAttribute("udp_port").setValue("3500");	// 
		rmo.getAttribute("min_vol").setValue("100");	// 
		rmo.getAttribute("max_vol").setValue("500");	// 
		rmo.getInput("BUY").connectTo(logicBuy.getOutput("Z"));
		rmo.getInput("SELL").connectTo(logicSell.getOutput("Z"));

		// logger connections
		lgr.getInputs().get(0).connectTo(rmo.getOutput("ORDER"));
		
		// Push config to FPGA
		logger.info("mat is: " + mat);
		mat.configureHW();
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
		CheetahExample1mfd sys = new CheetahExample1mfd();
		try {
			sys.run();
		} catch (Exception e) {
			logger.error("Outer error catcher: " + e.getMessage());
			e.printStackTrace();
			sys.shutdown();
		}		
	}


}

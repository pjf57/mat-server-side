package com.cs.fwk.test;



import java.io.FileInputStream;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Semaphore;

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
import com.cs.fwk.core.marketsim.EventFeedCallbackInt;
import com.cs.fwk.core.marketsim.SymbolEventFeed;
import com.cs.fwk.core.sys.MATComms;
import com.cs.fwk.util.comms.UDPCxn;

public class SoakTest implements NotificationCallback, EventFeedCallbackInt {
	private final static Logger logger = Logger.getLogger(SoakTest.class);
	private MatInterface mat = null;
	private MATCommsApi comms = null;
	long totalTicksSent = 0;
	Semaphore sem;
	boolean running;

	private void run() throws Exception {
		// initialise model with specified palette
		init("testresources/mat_32_soak.csp","192.168.2.9",2000);
		// semaphore interworks with mkt evt push.
		sem = new Semaphore(0);
		running = true;
		sleep(1000);
		logger.info("Soaktest runtime processing");
		// kick off the first batch of mkt evts
		pushMktEvts();
		while (running) {
			sem.acquire();
//			sleep(1);
			try {
				pushMktEvts();
			} catch (Exception e) {
				logger.error("Error pushing mkt evts: " + e.getMessage());
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
		if (System.getProperty("active") != null) {
			logger.info("init(): active mode - so will configure the HW");
			mat.putIntoConfigMode();
			configure(mat);
			mat.syncClock(0);
		} else {
			logger.info("init(): passive mode - so will NOT configure the HW");
		}
		logger.info("-----");	
		mat.requestHWStatus();		
	}

	
	private void configure(MatApi mat) throws Exception {
		Element sys = mat.getModel().getElement(0);
		Element mfd = mat.getModel().getElement(30);
		Element macd = mat.getModel().getElement(10);
		Element rmo = mat.getModel().getElement(31);
		Element logicBuy = mat.getModel().getElement(15);
		Element logicSell = mat.getModel().getElement(17);

		// configure system attributes
		sys.getAttribute("lookup_audit_autosend").setValue("16");
		sys.getAttribute("router_audit_autosend").setValue("16");
		// configure MFD 
		mfd.getAttribute("udp_listen_port").setValue("6000");
		mfd.getAttribute("market_ID").setValue("1");
		mfd.getAttribute("trade").setValue("0");	// trade to this op
		mfd.getAttribute("bid").setValue("1");		// bid to this op
		mfd.getAttribute("ask").setValue("2");		// ask to this op
		mfd.getAttribute("symbols").setValue("IBM:5,APPL:6");
		// Configure MACD
		macd.getAttribute("FAST_EMA_len").setValue("3");	// 
		macd.getAttribute("SLOW_EMA_len").setValue("5");	// 
		macd.getAttribute("SIGNAL_EMA_len").setValue("3");	// 
		macd.getAttribute("OP_ENABLE_MASK").setValue("1");	// enable macd op
		macd.getInputs().get(0).connectTo(mfd.getOutputs().get(2));
		// Configure logic 1
		logicBuy.getAttribute("Z").setValue("P");
		logicBuy.getAttribute("P").setValue("A>K1");
		logicBuy.getAttribute("k1").setValue("0.15");
		logicBuy.getInputs().get(0).connectTo(macd.getOutputs().get(0));
		logicSell.getAttribute("Z").setValue("P");
		logicSell.getAttribute("P").setValue("A<K1");
		logicSell.getAttribute("k1").setValue("-0.06");	 	
		logicSell.getInputs().get(0).connectTo(macd.getOutputs().get(0));
		// Configure RMO
		rmo.getAttribute("udp_ip").setValue("C0A80205");	//
		rmo.getAttribute("udp_port").setValue("3500");	//
		rmo.getAttribute("min_vol").setValue("100");	//
		rmo.getAttribute("max_vol").setValue("500");	//
		rmo.getAttribute("max_posn").setValue("1500");	//
		rmo.getInputs().get(0).connectTo(logicBuy.getOutputs().get(0));
		rmo.getInputs().get(1).connectTo(logicSell.getOutputs().get(0));

		// logger connections
//		lgr.getInputs().get(0).connectTo(rmo.getOutputs().get(0));
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

	/**
	 * Open file and push mkt events to required ip addr
	 * 
	 * @throws Exception
	 */
	public void pushMktEvts() throws Exception {
		String ip="direct";
		CxnInt cxn = getCxnOrLoopback(ip);
		int port = 6000;
		String file = "md1_soak.csv";
		int bursts = 20;
		int ticksPerPkt = 50;
		int gapMs = 1;
		SymbolEventFeed feed = new SymbolEventFeed(cxn,port);
		feed.setCb(this);
		String fpath = "src/test/resources/" + file;
		feed.sendTradeBurst(fpath,bursts,ticksPerPkt,gapMs);
		if (!ip.equals("direct")) {
			// only close if we opened this cxn especially
			cxn.close();
		}
	}
	

	public void notifyEventFeedState(String state, long totalSent) {
		logger.info("notifyEventFeedState: state=" + state + ", total sent = " + totalSent);
		if (state.equals("stopped")) {
			totalTicksSent += totalSent;
			logger.info("TotalTicksSend = " + totalTicksSent);
			mat.requestHWStatus();
			sem.release();	// signal main thread to start another batch
		}
	}	
	
	private CxnInt getCxnOrLoopback(String ip) throws SocketException, UnknownHostException {
		CxnInt cxn = null;
		if (ip.equals("direct")) {
			cxn = comms.getCxn();
		} else {
			cxn = new UDPCxn(ip);				
		}
		return cxn;
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
		logger.info("startup");
		SoakTest sys = new SoakTest();
		try {
			sys.run();
		} catch (Exception e) {
			logger.error("Outer error catcher: " + e.getMessage());
			e.printStackTrace();
			sys.shutdown();
		}		
	}

}

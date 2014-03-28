package com.pjf.mat.sys;

import java.io.FileInputStream;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.pjf.marketsim.BasicEventFeed;
import com.pjf.marketsim.EventFeedInt;
import com.pjf.mat.api.Cmd;
import com.pjf.mat.api.Element;
import com.pjf.mat.api.MatApi;
import com.pjf.mat.api.MatSimInt;
import com.pjf.mat.api.NotificationCallback;
import com.pjf.mat.api.TimeOrdered;
import com.pjf.mat.api.comms.Comms;
import com.pjf.mat.api.comms.CxnInt;
import com.pjf.mat.api.logging.EventLog;
import com.pjf.mat.api.logging.LkuAuditLog;
import com.pjf.mat.api.logging.MatLogger;
import com.pjf.mat.api.logging.OrderLog;
import com.pjf.mat.api.logging.RtrAuditLog;
import com.pjf.mat.impl.MatInterface;
import com.pjf.mat.impl.MatInterfaceModel;
import com.pjf.mat.sim.MatSim;
import com.pjf.mat.util.SystemServicesInt;
import com.pjf.mat.util.comms.UDPCxn;


public abstract class MatSystem implements SystemServicesInt {
	protected final static Logger logger = Logger.getLogger(MatSystem.class);
	private MatInterface mat = null;
	private Comms comms = null;

	private MatSimInt sim = null;
	private EventFeedInt feed;
	private final UnifiedEventLogger ueLogger;
	private final NotificationHandler notificationHandler;

	class NotificationHandler implements NotificationCallback {

		@Override
		public void notifyEventLog(EventLog evt) {
			ueLogger.addLog(evt);
			logger.debug("Event: ts=" + evt.getTimestamp() + " src=" + evt.getSrcElement().getId() +
						" type=" + evt.getSrcElement().getType() +
						" instrument=" + evt.getInstrumentId() +
						" value=" + evt.getDispValue());			
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
				logger.debug("Received LKU Audit Log: [" + log + "]");
				ueLogger.addLog(log);
			}
		}

		@Override
		public void notifyRtrAuditLogReceipt(Collection<RtrAuditLog> logs) {
			for (RtrAuditLog log : logs) {
				logger.debug("Received Router Audit Log: [" + log + "]");
				ueLogger.addLog(log);
			}
		}

		@Override
		public void notifyUnifiedEventLog(List<TimeOrdered> logs) {
			for (TimeOrdered log : logs) {
				logger.info("Unified Event Log: " + log);
			}
		}

		@Override
		public void notifyOrderReceipt(OrderLog order) {
			logger.error("notifyOrderReceipt(): " + order);			
		}
		
	}

	public MatSystem(){
		notificationHandler = new NotificationHandler();
		ueLogger = new UnifiedEventLogger(notificationHandler,false);
	}
	
	
	/**
	 * Template method called during boot process
	 * It can perform any initialisation and then call init() to init the cxn to HW
	 */
	protected abstract void start() throws Exception;

	/**
	 * Template method to configure the system
	 * 
	 * @param mat
	 * @throws Exception
	 */
	protected abstract void configure(MatApi mat) throws Exception;

	/** 
	 * Template method to send data to the HW
	 * 
	 * @param mat
	 * @param feed
	 * @throws Exception
	 */
	protected void sendTradeBurst(MatApi mat, EventFeedInt feed) throws Exception {
		if (feed != null) {
			feed.sendTradeBurst("resources/GLP_27667_1.csv",20,10,1);
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
	protected void init(String propsResource, String hwIPAddr, int hwPortNum) throws Exception {
		Properties props = loadProperties(propsResource);
		if (System.getProperty("sim") != null) {
			sim = new MatSim(new MatLogger() {
				@Override
				public void info(String message) {
					logger.info(message);
				}
				@Override
				public void error(Exception ex) {
					logger.error(ex);
				}
				@Override
				public void debug(String message) {
					logger.debug(message);
				}
			}, this);
			comms = sim;
		} else if (System.getProperty("dummy") != null) {
			comms = new DummyComms();
		} else {
			comms = new UDPComms(hwIPAddr,hwPortNum);
		}
		comms.addNotificationSubscriber(notificationHandler);
		MatInterfaceModel model = new MatInterfaceModel(props);
		mat = new MatInterface(comms,model);
		comms.setMat(mat);
		if (sim == null) {
			feed = createEventFeeder(comms.getCxn());
		} else {
			// transfer config data into simulator
			sim.init(mat.getModel().getElements());
		}
		mat.checkHWSignature();
	}

	/**
	 * Template method to create an event feeder
	 * 
	 * @param cxn UDP cxn opened to HW
	 * @return event feeder
	 * @throws UnknownHostException 
	 * @throws SocketException 
	 */
	protected EventFeedInt createEventFeeder(CxnInt cxn) throws Exception {
		EventFeedInt fd = new BasicEventFeed(cxn,15000);
		return fd;
	}

	protected void run() throws Exception {
		logger.info("-----");	
		reqStatus(); Thread.sleep(500);
		mat.putIntoConfigMode();
		configure(mat);
		mat.syncClock(0);
		
		logger.info("-----");	
		reqStatus();
		Thread.sleep(500);
		sendTradeBurst(mat,feed);
		
		try {
			doRunProcessing(mat);
		} catch (Exception e) {
			logger.error("Exception in doRunProcessing(): " + e.getMessage());
		}
		
		logger.info("-----");
		getFinalStatus();
		Thread.sleep(500);
		Thread.sleep(1000);
		
		logger.info("----------------------------------------");
		ueLogger.flush();
		logger.info("----------------------------------------");
	}

	/**
	 * Template method for runtime processing - execution continues until this method completes
	 * 
	 * @param mat
	 * @throws Exception 
	 */
	protected void doRunProcessing(MatInterface mat2) throws Exception {
		logger.info("Default runtime processing");
	}


	/**
	 * Template method
	 * 
	 * can be overwritten to change behaviour of collection of final status from the HW
	 * 
	 * @throws Exception
	 */
	protected void getFinalStatus() throws Exception {
		reqAuditLogs(); 
		reqStatus(); 
	}


	protected void boot() {
		try {
			start();
			if (sim != null) {
				sim.start();
			}
			run();
			shutdown();
		} catch (Exception e) {
			logger.error("Outer error catcher: " + e.getMessage());
			e.printStackTrace();
			shutdown();
		}		
	}


	public void shutdown() {
		logger.info("Shutting down ...");
		mat.shutdown();
		ueLogger.shutdown();
	}


	protected void sendCmd(int elId, String cmdName) {
		Element el = mat.getModel().getElement(elId);
		if (el != null) {
			Cmd cmd = el.getCmds().get(0);
			mat.sendCmd(cmd);
		}
	}
	
	protected void reqStatus() {
		mat.requestHWStatus();		
	}
		
	protected void reqAuditLogs() throws Exception {
		mat.reqLkuAuditLogs();		
		mat.reqRtrAuditLogs();		
	}

	protected static Properties loadProperties(String resource) throws Exception {
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(resource));
		} catch (Exception e) {
			throw new Exception("Cant load properties file",e);
		}
		return props;
	}

	@Override
	public CxnInt getCxnOrLoopback(String ip) throws SocketException, UnknownHostException {
		CxnInt cxn = null;
		if (ip.equals("direct")) {
			cxn = comms.getCxn();
			if (sim != null) {
				// Set to loopback outgoing UDP pkts so that we can receive anything
				// that is sent on this cxn
				cxn.setLoopbackCallback(sim);
			}
		} else {
			cxn = new UDPCxn(ip);				
		}
		return cxn;
	}

}

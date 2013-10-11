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
import com.pjf.mat.api.EventLog;
import com.pjf.mat.api.LkuAuditLog;
import com.pjf.mat.api.MatApi;
import com.pjf.mat.api.MatElementDefs;
import com.pjf.mat.api.MatLogger;
import com.pjf.mat.api.NotificationCallback;
import com.pjf.mat.api.OrderLog;
import com.pjf.mat.api.RtrAuditLog;
import com.pjf.mat.api.Status;
import com.pjf.mat.api.TimeOrdered;
import com.pjf.mat.impl.MatInterface;
import com.pjf.mat.impl.MatInterfaceModel;
import com.pjf.mat.impl.element.BasicCmd;
import com.pjf.mat.sim.MatSim;
import com.pjf.mat.util.SystemServicesInt;
import com.pjf.mat.util.comms.BaseComms;
import com.pjf.mat.util.comms.UDPCxn;


public abstract class MatSystem implements SystemServicesInt {
	protected final static Logger logger = Logger.getLogger(MatSystem.class);
	private MatInterface mat = null;
	private MatSim sim = null;
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
		public void notifyElementStatusUpdate(Element element) {
			logger.info("Status Update: element=" + element.getId() +
						" type=" + element.getType() +
						" state=" + element.getElementStatus());			
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
			logger.error("notifyOrderReceipt() - not supported");			
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
	 * @throws Exception
	 */
	protected void init(String propsResource) throws Exception {
		Properties props = loadProperties(propsResource);
		BaseComms comms;
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
			comms = new UDPComms("192.168.0.9",2000);
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
	protected EventFeedInt createEventFeeder(UDPCxn cxn) throws Exception {
		EventFeedInt fd = new BasicEventFeed(cxn,15000);
		return fd;
	}

	protected void run() throws Exception {
		logger.info("-----");	
		reqStatus(); Thread.sleep(500);
		putIntoConfigMode();
		configure(mat);
		mat.syncClock(0);
		
		logger.info("-----");	
		reqStatus();
		Thread.sleep(500);
		sendTradeBurst(mat,feed);
		
		logger.info("-----");
		getFinalStatus();
		Thread.sleep(500);
		Thread.sleep(1000);
		
		logger.info("----------------------------------------");
		ueLogger.flush();
		logger.info("----------------------------------------");
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
	
	/**
	 * Send HW into configuration state if not already there
	 * @throws Exception if unable to get all elements into config state
	 * 
	 */
	private void putIntoConfigMode() throws Exception {
		logger.info("Ensuring system is in config mode.");
		boolean didReset = false;
		for (Element el : mat.getModel().getElements()) {
			if (el.getId() != 0) {	// ignore router element
				Status s = el.getElementStatus();
				if (!s.isInConfigState()) {
					logger.info("Resetting " + el.getShortName() + " to force it to config state..");
					mat.sendCmd(new BasicCmd(el, "reset", MatElementDefs.EL_C_RESET, 0));
					didReset = true;
				}
			}
		}
		if (didReset) {
			// check that all elements are ready for configuration
			reqStatus();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				logger.info("Sleep interrupted - " + e);
			}
			// check all in config mode now
			for (Element el : mat.getModel().getElements()) {
				if (el.getId() != 0) {	// ignore router element
					Status s = el.getElementStatus();
					if (!s.isInConfigState()) {
						logger.error("Unable to force element " + el.getShortName() + " into config state.");
						throw new Exception("System not ready for configuration - el:" + el.getShortName());
					}
				}
			}
		}		
		logger.info("Ready to config ...");
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
	public UDPCxn getCxnOrLoopback(String ip) throws SocketException, UnknownHostException {
		throw new UnknownHostException("not supported");
	}

}

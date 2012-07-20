package com.pjf.mat.sys;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.pjf.marketsim.EventFeed;
import com.pjf.mat.api.Cmd;
import com.pjf.mat.api.Element;
import com.pjf.mat.api.MatApi;
import com.pjf.mat.api.NotificationCallback;
import com.pjf.mat.impl.MatInterface;
import com.pjf.mat.impl.MatInterfaceModel;
import com.pjf.mat.sim.MatSim;
import com.pjf.mat.util.comms.BaseComms;


public abstract class MatSystem {
	protected final static Logger logger = Logger.getLogger(MatSystem.class);
	private MatInterface mat = null;
	private MatSim sim = null;
	private EventFeed feed;

	class NotificationHandler implements NotificationCallback {

		@Override
		public void notifyEventLog(Element src, int intrument_id, int rawValue,
				String dispValue) {
			logger.info("Event: src=" + src.getId() +
						" type=" + src.getType() +
						" instrument=" + intrument_id +
						" value=" + dispValue);			
		}

		@Override
		public void notifyElementStatusUpdate(Element element) {
			logger.info("Status Update: element=" + element.getId() +
						" type=" + element.getType() +
						" state=" + element.getElementStatus());			
		}
		
	}

	public MatSystem(){
	}
	
	
	/**
	 * Template method called during boot process
	 * It can perform any initialisation and then call init() to init the cxn to HW
	 */
	protected abstract void start() throws Exception;

	/**
	 * Template method to send data
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
	protected void sendTradeBurst(MatApi mat, EventFeed feed) throws Exception {
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
			sim = new MatSim();
			comms = sim;
		} else if (System.getProperty("dummy") != null) {
			comms = new DummyComms();
		} else {
			comms = new UDPComms("192.168.0.9",2000);
		}
		comms.addNotificationSubscriber(new NotificationHandler());
		MatInterfaceModel model = new MatInterfaceModel(props);
		mat = new MatInterface(comms,model);
		comms.setMat(mat);
		if (sim != null) {
			// transfer config data into simulator
			sim.init(mat.getModel().getElements());
		}
		mat.checkHWSignature();
		if (feed != null) {
			feed = new EventFeed(comms.getCxn(),15000);
		}
	}

	protected void run() throws Exception {
		logger.info("-----");	reqStatus(); Thread.sleep(500);
		configure(mat);
		logger.info("-----");	reqStatus(); Thread.sleep(500);
		sendTradeBurst(mat,feed);
		logger.info("-----");	reqStatus(); Thread.sleep(500);
		Thread.sleep(1000);
	}

	protected void boot() {
		try {
			start();
			run();
			shutdown();
		} catch (Exception e) {
			logger.error("Outer error catcher: " + e.getMessage());
			e.printStackTrace();
		}		
	}


	public void shutdown() {
		logger.info("Shutting down ...");
		mat.shutdown();
	}


	protected void sendCmd(int elId, String cmdName) {
		Element el = mat.getModel().getElement(elId);
		if (el != null) {
			Cmd cmd = el.getCmds().get(0);
			mat.sendCmd(cmd);
		}
	}
	
	private void reqStatus() {
		mat.getHWStatus();		
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

}

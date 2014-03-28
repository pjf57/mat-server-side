package com.pjf.mat.test;


import java.util.concurrent.Semaphore;

import com.pjf.marketsim.EventFeedCallbackInt;
import com.pjf.marketsim.EventFeedInt;
import com.pjf.marketsim.SymbolEventFeed;
import com.pjf.mat.api.Element;
import com.pjf.mat.api.MatApi;
import com.pjf.mat.api.comms.CxnInt;
import com.pjf.mat.impl.MatInterface;
import com.pjf.mat.sys.MatSystem;

public class SoakTest extends MatSystem implements EventFeedCallbackInt{
	long totalTicksSent = 0;
	Semaphore sem;
	boolean running;

	@Override
	protected void start() throws Exception {
		// initialise model with specified palette
		init("src/test/resources/mat_32_soak.csp","192.168.2.9",2000);
		sem = new Semaphore(0);
		running = true;
	}
	
	@Override
	protected void configure(MatApi mat) throws Exception {
		Element sys = mat.getModel().getElement(0);
		Element mfd = mat.getModel().getElement(30);
		Element macd = mat.getModel().getElement(10);
		Element lgr = mat.getModel().getElement(1);
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
		lgr.getInputs().get(0).connectTo(rmo.getOutputs().get(0));
		logger.info("mat is: " + mat);

		mat.configureHW();
	}

	// Ignore default mkt feed mechanism
	protected EventFeedInt createEventFeeder(CxnInt cxn) throws Exception {
		return null;
	}
	

	/** 
	 * send mkt data to the HW
	 * 
	 * @param feed
	 * @throws Exception
	 */
	@Override
	protected void sendTradeBurst(MatApi mat, EventFeedInt feed) throws Exception {
		logger.warn("sendTradeBurst() - not supported.");
	}

	@Override
	protected void doRunProcessing(MatInterface mat2) throws Exception {
		logger.info("Soaktest runtime processing");
		// kick off the first batch of mkt evts
		pushMktEvts();
		while (running) {
			sem.acquire();
			sleep(50);
			try {
				pushMktEvts();
			} catch (Exception e) {
				logger.error("Error pushing mkt evts: " + e.getMessage());
			}
		}
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
	

	@Override
	protected void getFinalStatus() throws Exception {
		reqAuditLogs(); 
		reqStatus(); 
	}


	@Override
	public void notifyEventFeedState(String state, long totalSent) {
		logger.info("notifyEventFeedState: state=" + state + ", total sent = " + totalSent);
		if (state.equals("stopped")) {
			totalTicksSent += totalSent;
			logger.info("TotalTicksSend = " + totalTicksSent);
			sem.release();	// signal main thread to start another batch
		}
	}	

	public static void main(String[] args) {
		logger.info("startup");
		SoakTest sys = new SoakTest();
		sys.boot();
	}


}

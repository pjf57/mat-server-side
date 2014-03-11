package com.pjf.mat.examples;

import org.apache.log4j.BasicConfigurator;

import com.pjf.marketsim.EventFeedInt;
import com.pjf.mat.api.Element;
import com.pjf.mat.api.MatApi;
import com.pjf.mat.sys.MatSystem;

public class CheetahExample1 extends MatSystem{

	@Override
	protected void start() throws Exception {
		// initialise model with specified palette
		init("resources/mat.32v83.csp","192.168.1.9",2000);
	}
	
	@Override
	protected void configure(MatApi mat) throws Exception {
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
		rmo.getAttribute("udp_ip").setValue("0C0A80105");	// 
		rmo.getAttribute("udp_port").setValue("3500");	// 
		rmo.getAttribute("min_vol").setValue("100");	// 
		rmo.getAttribute("max_vol").setValue("500");	// 
		rmo.getInputs().get(0).connectTo(logicBuy.getOutputs().get(0));
		rmo.getInputs().get(1).connectTo(logicSell.getOutputs().get(0));

		// logger connections
		lgr.getInputs().get(0).connectTo(rmo.getOutputs().get(0));
		logger.info("mat is: " + mat);

		mat.configureHW();
	}

	/** 
	 * send mkt data to the HW
	 * 
	 * @param feed
	 * @throws Exception
	 */
	@Override
	protected void sendTradeBurst(MatApi mat, EventFeedInt feed) throws Exception {
//		sendCmd(2,"start");
		if (feed != null) {
			feed.sendTradeBurst("resources/GLP_27667_1.csv",20,5,1);
		}
		Thread.sleep(5000);
	}

	@Override
	protected void getFinalStatus() throws Exception {
		reqAuditLogs(); 
		reqStatus(); 
	}


	public static void main(String[] args) {
		BasicConfigurator.configure();
		logger.info("startup");
		CheetahExample1 sys = new CheetahExample1();
		sys.boot();
	}


}

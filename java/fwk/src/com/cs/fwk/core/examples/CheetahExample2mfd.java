package com.cs.fwk.core.examples;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.cs.fwk.api.Element;
import com.cs.fwk.api.MatApi;

public class CheetahExample2mfd extends CheetahBaseExample {
	
	private void run() throws Exception {
		int runSeconds = 0;
		// initialise model with specified palette and UDP address
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


	/**
	 * Configure the algo - set parameters and wire up the cheetah blocks
	 * 
	 * @param mat - MAT API
	 * @throws Exception
	 */
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


	public static void main(String[] args) {
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.DEBUG);
		logger.info("startup");
		CheetahExample2mfd sys = new CheetahExample2mfd();
		try {
			sys.run();
		} catch (Exception e) {
			logger.error("Outer error catcher: " + e.getMessage());
			e.printStackTrace();
			sys.shutdown();
		}		
	}


}

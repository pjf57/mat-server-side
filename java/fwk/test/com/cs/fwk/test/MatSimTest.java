package com.cs.fwk.test;


import com.cs.fwk.api.Element;
import com.cs.fwk.api.MatApi;
import com.cs.fwk.core.marketsim.EventFeedInt;
import com.cs.fwk.core.sys.MatSystem;

public class MatSimTest extends MatSystem {

	@Override
	protected void start() throws Exception {
		init("resources/mat.properties.32.rmo","192.168.0.9",2000);
	}
	
	@Override
	protected void configure(MatApi mat) throws Exception {

//		Properties p = loadProperties("resources/matSim.matdef");
//		MatSystemLoader loader = new MatSystemLoader(p);
//		loader.initialize(mat.getModel());
		
		Element sys = mat.getModel().getElement(0);
		Element mfd = mat.getModel().getElement(30);
		Element macdBuy = mat.getModel().getElement(10);
		Element macdSell = mat.getModel().getElement(11);
		Element logicBuy = mat.getModel().getElement(15);
		Element logicSell = mat.getModel().getElement(16);
		Element lgr = mat.getModel().getElement(1);
		Element rmo = mat.getModel().getElement(31);

		// configure system attributes
		sys.getAttribute("lookup_audit_autosend").setValue("4");
		sys.getAttribute("router_audit_autosend").setValue("4");

		// configure element attributes
		mfd.getAttribute("udp_listen_port").setValue("15000");
		mfd.getAttribute("trade").setValue("0f");
		mfd.getAttribute("bid").setValue("00");		// bid events, price to op 0	
		mfd.getAttribute("ask").setValue("01");		// ask events, price to op 1	
		mfd.getAttribute("symbols").setValue("IBM:5,APPL:18");
		mfd.getAttribute("market_ID").setValue("23");
		
		
		// Configure BUY MACD
		macdBuy.getAttribute("FAST_EMA_alpha").setValue("0.153846");	// 
		macdBuy.getAttribute("FAST_EMA_len").setValue("12");	// 
		macdBuy.getAttribute("SLOW_EMA_alpha").setValue("0.07407407");	// 
		macdBuy.getAttribute("SLOW_EMA_len").setValue("26");	// 
		macdBuy.getAttribute("SIGNAL_EMA_alpha").setValue("0.153846");	// 
		macdBuy.getAttribute("SIGNAL_EMA_len").setValue("12");	// 
		macdBuy.getAttribute("OP_ENABLE_MASK").setValue("4");	// enable only HIST OP
		macdBuy.getInputs().get(0).connectTo(mfd.getOutputs().get(1));	// ask events generate buys
		// configure Logic BUY
		logicBuy.getAttribute("oper").setValue("03044");	// 	Z = A > k1 
		logicBuy.getAttribute("k1").setValue("0.01");	// 	
		logicBuy.getInputs().get(0).connectTo(macdBuy.getOutputs().get(2));

		// Configure SELL MACD
		macdSell.getAttribute("FAST_EMA_alpha").setValue("0.153846");	// 
		macdSell.getAttribute("FAST_EMA_len").setValue("12");	// 
		macdSell.getAttribute("SLOW_EMA_alpha").setValue("0.07407407");	// 
		macdSell.getAttribute("SLOW_EMA_len").setValue("26");	// 
		macdSell.getAttribute("SIGNAL_EMA_alpha").setValue("0.153846");	// 
		macdSell.getAttribute("SIGNAL_EMA_len").setValue("12");	// 
		macdSell.getAttribute("OP_ENABLE_MASK").setValue("4");	// enable only HIST OP
		macdSell.getInputs().get(0).connectTo(mfd.getOutputs().get(0));	// bid events generate sells
		// configure Logic Sell
		logicSell.getAttribute("oper").setValue("01044");	// 	Z = A < k1 
		logicSell.getAttribute("k1").setValue("-0.01");	// 	
		logicSell.getInputs().get(0).connectTo(macdSell.getOutputs().get(2));
		
		// Configure RMO
		rmo.getAttribute("udp_ip").setValue("0C0A80006");	// 
		rmo.getAttribute("udp_port").setValue("3500");	// 
		rmo.getAttribute("min_vol").setValue("100");	// 
		rmo.getAttribute("max_vol").setValue("500");	// 
		rmo.getAttribute("max_posn").setValue("1000");	// 
		rmo.getInputs().get(0).connectTo(logicBuy.getOutputs().get(0));
		rmo.getInputs().get(1).connectTo(logicSell.getOutputs().get(0));

		// logger connections
		lgr.getInputs().get(0).connectTo(rmo.getOutputs().get(0));
		lgr.getInputs().get(1).connectTo(macdBuy.getOutputs().get(2));
		lgr.getInputs().get(2).connectTo(macdSell.getOutputs().get(2));

		logger.info("mat is: " + mat);

		mat.configureHW();
	}


	@Override
	protected void sendTradeBurst(MatApi mat, EventFeedInt feed) throws Exception {
		sendCmd(30,"start");
		Thread.sleep(5000);
	}

	public static void main(String[] args) {
		MatSimTest sys = new MatSimTest();
		sys.boot();
	}


}

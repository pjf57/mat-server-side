package com.pjf.mat.test;

import com.pjf.marketsim.EventFeedInt;
import com.pjf.mat.api.Element;
import com.pjf.mat.api.MatApi;
import com.pjf.mat.sys.MatSystem;

public class MatAdx32ema3_27 extends MatSystem {

	@Override
	protected void start() throws Exception {
		init("resources/mat.properties.32ema3-27","192.168.0.9",2000);
	}
	
	@Override
	protected void configure(MatApi mat) throws Exception {
//		MatProperties p = loadProperties("resources/matAdx16.matdef");
//		MatSystemLoader loader = new MatSystemLoader(p);
//		loader.initialize(mat);
		
		Element sys = mat.getModel().getElement(0);
		Element lgr = mat.getModel().getElement(1);
		Element tg1 = mat.getModel().getElement(2);
//		Element ema1 = mat.getModel().getElement(3);
//		Element ema2 = mat.getModel().getElement(4);
		Element hloc = mat.getModel().getElement(28);
		Element atr = mat.getModel().getElement(29);
		Element adx = mat.getModel().getElement(30);
		Element mfd = mat.getModel().getElement(31);

		// configure system attributes
		sys.getAttribute("lookup_audit_autosend").setValue("4");
		sys.getAttribute("router_audit_autosend").setValue("4");
		// configure element attributes
		mfd.getAttribute("udp_listen_port").setValue("15000");
		mfd.getAttribute("price_op").setValue("0");
		mfd.getAttribute("volume_op").setValue("f");
		mfd.getAttribute("mdtype").setValue("1");
//		ema.getAttribute("len").setValue("7");
//		ema.getAttribute("alpha").setValue("0.25");
		tg1.getAttribute("len").setValue("1000");
		tg1.getAttribute("gap").setValue("100");
		tg1.getAttribute("initial_value").setValue("50");
		tg1.getAttribute("p1").setValue("0.25");
		hloc.getAttribute("period").setValue("1000");	// 
		hloc.getAttribute("metric").setValue("11");			// 
		hloc.getAttribute("throttle").setValue("5");
		atr.getAttribute("len").setValue("3");	// 
		atr.getAttribute("alpha").setValue("0.5");	// 
		atr.getAttribute("IP_Has_Close(N-1)").setValue("0");	// 

		adx.getAttribute("PDN_EMA_len").setValue("3");	// 
		adx.getAttribute("PDN_EMA_alpha").setValue("0.5");	// 
		adx.getAttribute("NDN_EMA_len").setValue("3");	// 
		adx.getAttribute("NDN_EMA_alpha").setValue("0.5");	// 
		adx.getAttribute("ADX_EMA_len").setValue("3");	// 
		adx.getAttribute("ADX_EMA_alpha").setValue("0.5");	// 
//		adx.getAttribute("LKU_TARGET_ATR").setValue("5");	// 
		
		// Configure MACD
//		macd.getAttribute("FAST_EMA_alpha").setValue("0.5");	// 
//		macd.getAttribute("FAST_EMA_len").setValue("3");	// 
//		macd.getAttribute("SLOW_EMA_alpha").setValue("0.25");	// 
//		macd.getAttribute("SLOW_EMA_len").setValue("7");	// 
//		macd.getAttribute("SIGNAL_EMA_alpha").setValue("0.5");	// 
//		macd.getAttribute("SIGNAL_EMA_len").setValue("3");	// 
//		macd.getAttribute("OP_ENABLE_MASK").setValue("2");	// enable only SIGNAL OP
		
		// configure element connections
//		ema.getInputs().get(0).connectTo(mfd.getOutputs().get(0));
		hloc.getInputs().get(0).connectTo(tg1.getOutputs().get(0));
		atr.getInputs().get(0).connectTo(hloc.getOutputs().get(0));
		adx.getInputs().get(0).connectTo(hloc.getOutputs().get(0));
//		macd.getInputs().get(0).connectTo(tg1.getOutputs().get(0));
		
		// logger connections
		//		lgr.getInputs().get(0).connectTo(tg1.getOutputs().get(0));
//		lgr.getInputs().get(1).connectTo(hloc.getOutputs().get(0));
//		lgr.getInputs().get(0).connectTo(atr.getOutputs().get(0));
		//		lgr.getInputs().get(1).connectTo(macd.getOutputs().get(0));
		//		lgr.getInputs().get(2).connectTo(macd.getOutputs().get(2));
		lgr.getInputs().get(3).connectTo(adx.getOutputs().get(0));
//		lgr.getInputs().get(2).connectTo(macd.getOutput("hist"));
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
		sendCmd(2,"start");
//		if (feed != null) {
//			feed.sendTradeBurst("resources/GLP_27667_1.csv",3,5,1);
//		}
		Thread.sleep(5000);
	}

	public static void main(String[] args) {
		MatAdx32ema3_27 sys = new MatAdx32ema3_27();
		sys.boot();
	}


}

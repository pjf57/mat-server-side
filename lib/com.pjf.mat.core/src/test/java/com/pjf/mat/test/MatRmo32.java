package com.pjf.mat.test;

import com.pjf.marketsim.EventFeed;
import com.pjf.mat.api.Element;
import com.pjf.mat.api.MatApi;
import com.pjf.mat.sys.MatSystem;

public class MatRmo32 extends MatSystem {

	@Override
	protected void start() throws Exception {
		init("resources/mat.properties.32.rmo");
	}
	
	@Override
	protected void configure(MatApi mat) throws Exception {
		Element sys = mat.getModel().getElement(0);
		Element mfd = mat.getModel().getElement(30);
		Element tg1 = mat.getModel().getElement(2);
		Element macd = mat.getModel().getElement(10);
		Element lgr = mat.getModel().getElement(1);
		Element hloc = mat.getModel().getElement(27);
		Element atr = mat.getModel().getElement(28);
		Element adx = mat.getModel().getElement(29);
		Element rmo = mat.getModel().getElement(31);
		Element logic1 = mat.getModel().getElement(15);

		// configure system attributes
//		sys.getAttribute("lookup_audit_autosend").setValue("4");
		sys.getAttribute("router_audit_autosend").setValue("4");
		// configure element attributes
		mfd.getAttribute("udp_listen_port").setValue("15000");
		mfd.getAttribute("price_op").setValue("f");
		mfd.getAttribute("volume_op").setValue("0");
		mfd.getAttribute("mdtype").setValue("1");
//		ema.getAttribute("len").setValue("7");
//		ema.getAttribute("alpha").setValue("0.25");
		tg1.getAttribute("len").setValue("10000");
		tg1.getAttribute("gap").setValue("20");
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
		adx.getAttribute("LKU_TARGET_ATR").setValue("28");	// 
		
		// Configure MACD
		macd.getAttribute("FAST_EMA_alpha").setValue("0.5");	// 
		macd.getAttribute("FAST_EMA_len").setValue("3");	// 
		macd.getAttribute("SLOW_EMA_alpha").setValue("0.25");	// 
		macd.getAttribute("SLOW_EMA_len").setValue("7");	// 
		macd.getAttribute("SIGNAL_EMA_alpha").setValue("0.5");	// 
		macd.getAttribute("SIGNAL_EMA_len").setValue("3");	// 
		macd.getAttribute("OP_ENABLE_MASK").setValue("4");	// enable hist op

		// Configure logic 1
		logic1.getAttribute("oper").setValue("3044");	// 	Z = A > k1
		logic1.getAttribute("k1").setValue("0");	// 
		
		// Configure RMO
		rmo.getAttribute("udp_ip").setValue("0C0A80006");	// 
		rmo.getAttribute("udp_port").setValue("8100");	// 
		rmo.getAttribute("min_vol").setValue("100");	// 
		rmo.getAttribute("max_vol").setValue("500");	// 

		// configure element connections
//		ema.getInputs().get(0).connectTo(mfd.getOutputs().get(0));
//		hloc.getInputs().get(0).connectTo(tg1.getOutputs().get(0));
//		atr.getInputs().get(0).connectTo(hloc.getOutputs().get(0));
//		adx.getInputs().get(0).connectTo(hloc.getOutputs().get(0));
		macd.getInputs().get(0).connectTo(mfd.getOutputs().get(0));
		logic1.getInputs().get(0).connectTo(macd.getOutputs().get(2));
		rmo.getInputs().get(0).connectTo(logic1.getOutputs().get(0));
		
		// logger connections
		lgr.getInputs().get(0).connectTo(rmo.getOutputs().get(0));
//		lgr.getInputs().get(1).connectTo(hloc.getOutputs().get(0));
//		lgr.getInputs().get(0).connectTo(atr.getOutputs().get(0));
		//		lgr.getInputs().get(1).connectTo(macd.getOutputs().get(0));
		//		lgr.getInputs().get(2).connectTo(macd.getOutputs().get(2));
//		lgr.getInputs().get(3).connectTo(adx.getOutputs().get(0));
//		lgr.getInputs().get(2).connectTo(macd.getOutput("macd"));
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
	protected void sendTradeBurst(MatApi mat, EventFeed feed) throws Exception {
//		sendCmd(2,"start");
		if (feed != null) {
			feed.sendTradeBurst("resources/GLP_27667_1.csv",3,5,1);
		}
		Thread.sleep(5000);
	}

	@Override
	protected void getFinalStatus() throws Exception {
		reqAuditLogs(); 
		reqStatus(); 
	}


	public static void main(String[] args) {
		MatRmo32 sys = new MatRmo32();
		sys.boot();
	}


}

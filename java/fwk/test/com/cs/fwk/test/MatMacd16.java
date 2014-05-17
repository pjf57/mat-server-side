package com.cs.fwk.test;


import com.cs.fwk.api.Element;
import com.cs.fwk.api.MatApi;
import com.cs.fwk.core.marketsim.EventFeedInt;
import com.cs.fwk.core.sys.MatSystem;

public class MatMacd16 extends MatSystem {

	@Override
	protected void start() throws Exception {
		init("resources/mat_macd.properties.16","192.168.0.9",2000);
	}
	
	@Override
	protected void configure(MatApi mat) throws Exception {
		Element sys = mat.getModel().getElement(0);
		Element lgr = mat.getModel().getElement(1);
		Element tg1 = mat.getModel().getElement(2);
		Element ema_p = mat.getModel().getElement(3);
		Element ema_q = mat.getModel().getElement(4);
		Element macd = mat.getModel().getElement(5);
		Element a4ip_macd = mat.getModel().getElement(9);

		// configure system attributes
		sys.getAttribute("lookup_audit_autosend").setValue("4");
		sys.getAttribute("router_audit_autosend").setValue("4");
		// configure element attributes
		tg1.getAttribute("len").setValue("1000");
		tg1.getAttribute("gap").setValue("1000");
		tg1.getAttribute("initial_value").setValue("50");
		tg1.getAttribute("p1").setValue("0.25");
		ema_p.getAttribute("len").setValue("7");
		ema_p.getAttribute("alpha").setValue("0.25");
		ema_q.getAttribute("len").setValue("3");
		ema_q.getAttribute("alpha").setValue("0.5");
		a4ip_macd.getAttribute("oper").setValue("3000");	// Z = A - B
		macd.getAttribute("FAST_EMA_alpha").setValue("0.5");	// 
		macd.getAttribute("FAST_EMA_len").setValue("3");	// 
		macd.getAttribute("SLOW_EMA_alpha").setValue("0.25");	// 
		macd.getAttribute("SLOW_EMA_len").setValue("7");	// 
		macd.getAttribute("SIGNAL_EMA_alpha").setValue("0.5");	// 
		macd.getAttribute("SIGNAL_EMA_len").setValue("3");	// 
		macd.getAttribute("OP_ENABLE_MASK").setValue("2");	// enable only SIGNAL OP

		// configure element connections
		ema_p.getInputs().get(0).connectTo(tg1.getOutputs().get(0));
		ema_q.getInputs().get(0).connectTo(tg1.getOutputs().get(0));
		a4ip_macd.getInputs().get(0).connectTo(ema_p.getOutputs().get(0));
		a4ip_macd.getInputs().get(1).connectTo(ema_q.getOutputs().get(0));
		lgr.getInputs().get(0).connectTo(tg1.getOutputs().get(0));
		lgr.getInputs().get(1).connectTo(a4ip_macd.getOutputs().get(0));
		lgr.getInputs().get(2).connectTo(macd.getOutputs().get(1));
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
		MatMacd16 sys = new MatMacd16();
		sys.boot();
	}


}

package com.pjf.mat.test;

import com.pjf.marketsim.EventFeed;
import com.pjf.mat.api.Element;
import com.pjf.mat.api.MatApi;
import com.pjf.mat.sys.MatSystem;

public class MatAdx16 extends MatSystem {

	@Override
	protected void start() throws Exception {
		init("resources/mat_adx.properties.16");
	}
	
	@Override
	protected void configure(MatApi mat) throws Exception {
//		MatProperties p = loadProperties("resources/matAdx16.matdef");
//		MatSystemLoader loader = new MatSystemLoader(p);
//		loader.initialize(mat);
		
		Element mfd = mat.getModel().getElement(15);
		Element tg1 = mat.getModel().getElement(2);
		Element ema_p = mat.getModel().getElement(3);
		Element ema_q = mat.getModel().getElement(4);
		Element ema_s = mat.getModel().getElement(5);
		Element a4ip_macd = mat.getModel().getElement(9);
		Element a4ip_hist = mat.getModel().getElement(10);
		Element lgr = mat.getModel().getElement(1);
		Element hloc = mat.getModel().getElement(12);
		Element atr = mat.getModel().getElement(13);
		Element adx = mat.getModel().getElement(14);

		// configure element attributes
		mfd.getAttribute("udp_listen_port").setValue("15000");
		mfd.getAttribute("price_op").setValue("0");
		mfd.getAttribute("volume_op").setValue("f");
		mfd.getAttribute("mdtype").setValue("1");
		ema_p.getAttribute("len").setValue("7");
		ema_p.getAttribute("alpha").setValue("0.25");
//		ema_q.getAttribute("len").setValue("3");
//		ema_q.getAttribute("alpha").setValue("0.5");
//		ema_s.getAttribute("len").setValue("5");
//		ema_s.getAttribute("alpha").setValue("0.333333");
//		a4ip_macd.getAttribute("oper").setValue("3000");	// Z = A - B
//		a4ip_hist.getAttribute("oper").setValue("3000");	// Z = A - B
		tg1.getAttribute("len").setValue("100");
		tg1.getAttribute("gap").setValue("2");
		tg1.getAttribute("initial_value").setValue("50");
		tg1.getAttribute("p1").setValue("0.25");
		hloc.getAttribute("period").setValue("10");	// 
		hloc.getAttribute("metric").setValue("11");			// 
		hloc.getAttribute("throttle").setValue("0");
		atr.getAttribute("len").setValue("3");	// 
		atr.getAttribute("alpha").setValue("0.5");	// 
		atr.getAttribute("IP_Has_Close(N-1)").setValue("1");	// 

		adx.getAttribute("PDN_EMA_len").setValue("3");	// 
		adx.getAttribute("PDN_EMA_alpha").setValue("0.5");	// 
		adx.getAttribute("NDN_EMA_len").setValue("3");	// 
		adx.getAttribute("NDN_EMA_alpha").setValue("0.5");	// 
		adx.getAttribute("ADX_EMA_len").setValue("3");	// 
		adx.getAttribute("ADX_EMA_alpha").setValue("0.5");	// 


		// configure element connections
		ema_p.getInputs().get(0).connectTo(mfd.getOutputs().get(0));
//		ema_q.getInputs().get(0).connectTo(mfd.getOutputs().get(0));
		hloc.getInputs().get(0).connectTo(mfd.getOutputs().get(0));
		atr.getInputs().get(0).connectTo(hloc.getOutputs().get(0));
		adx.getInputs().get(0).connectTo(hloc.getOutputs().get(0));
//		a4ip_macd.getInputs().get(0).connectTo(ema_p.getOutputs().get(0));
//		a4ip_macd.getInputs().get(1).connectTo(ema_q.getOutputs().get(0));
//		ema_s.getInputs().get(0).connectTo(a4ip_macd.getOutputs().get(0));
//		a4ip_hist.getInputs().get(0).connectTo(a4ip_macd.getOutputs().get(0));
//		a4ip_hist.getInputs().get(1).connectTo(ema_s.getOutputs().get(0));
		lgr.getInputs().get(0).connectTo(mfd.getOutputs().get(0));
//		lgr.getInputs().get(1).connectTo(tg1.getOutputs().get(0));
		lgr.getInputs().get(2).connectTo(atr.getOutputs().get(0));
		lgr.getInputs().get(3).connectTo(ema_p.getOutputs().get(0));
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

	public static void main(String[] args) {
		MatAdx16 sys = new MatAdx16();
		sys.boot();
	}


}

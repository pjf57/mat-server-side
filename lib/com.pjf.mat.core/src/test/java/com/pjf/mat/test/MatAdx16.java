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
		Element ema_p = mat.getModel().getElement(3);
		Element ema_q = mat.getModel().getElement(4);
		Element ema_s = mat.getModel().getElement(5);
		Element a4ip_macd = mat.getModel().getElement(9);
		Element a4ip_hist = mat.getModel().getElement(10);
		Element lgr = mat.getModel().getElement(1);
		Element hloc = mat.getModel().getElement(12);

		// configure element attributes
		mfd.getAttribute("udp listen port").setValue("15000");
		mfd.getAttribute("price_op").setValue("0");
		mfd.getAttribute("volume_op").setValue("f");
		mfd.getAttribute("mdtype").setValue("1");
		ema_p.getAttribute("len").setValue("7");
		ema_p.getAttribute("alpha").setValue("0.25");
		ema_q.getAttribute("len").setValue("3");
		ema_q.getAttribute("alpha").setValue("0.5");
		ema_s.getAttribute("len").setValue("5");
		ema_s.getAttribute("alpha").setValue("0.333333");
		a4ip_macd.getAttribute("oper").setValue("3000");	// Z = A - B
		a4ip_hist.getAttribute("oper").setValue("3000");	// Z = A - B
		hloc.getAttribute("period").setValue("10000");	// 100us
		hloc.getAttribute("metric").setValue("4");			// High(N)
		hloc.getAttribute("throttle").setValue("1");

		// configure element connections
		ema_p.getInputs().get(0).connectTo(mfd.getOutputs().get(0));
		ema_q.getInputs().get(0).connectTo(mfd.getOutputs().get(0));
		hloc.getInputs().get(0).connectTo(mfd.getOutputs().get(0));
		a4ip_macd.getInputs().get(0).connectTo(ema_p.getOutputs().get(0));
		a4ip_macd.getInputs().get(1).connectTo(ema_q.getOutputs().get(0));
		ema_s.getInputs().get(0).connectTo(a4ip_macd.getOutputs().get(0));
		a4ip_hist.getInputs().get(0).connectTo(a4ip_macd.getOutputs().get(0));
		a4ip_hist.getInputs().get(1).connectTo(ema_s.getOutputs().get(0));
		lgr.getInputs().get(0).connectTo(mfd.getOutputs().get(0));
		lgr.getInputs().get(1).connectTo(a4ip_macd.getOutputs().get(0));
		lgr.getInputs().get(2).connectTo(a4ip_hist.getOutputs().get(0));
		lgr.getInputs().get(3).connectTo(hloc.getOutputs().get(0));
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
		if (feed != null) {
			feed.sendTradeBurst("resources/GLP_27667_1.csv",3,5,1);
		}
	}

	public static void main(String[] args) {
		MatAdx16 sys = new MatAdx16();
		sys.boot();
	}


}

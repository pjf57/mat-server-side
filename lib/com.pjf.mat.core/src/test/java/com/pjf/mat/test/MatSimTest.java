package com.pjf.mat.test;

import com.pjf.marketsim.EventFeed;
import com.pjf.mat.api.Element;
import com.pjf.mat.api.MatApi;
import com.pjf.mat.sys.MatSystem;

public class MatSimTest extends MatSystem {

	@Override
	protected void start() throws Exception {
		init("resources/mat.properties.sim");
	}
	
	@Override
	protected void configure(MatApi mat) throws Exception {
		Element tg1 = mat.getElement(2);
		Element lgr = mat.getElement(1);
		Element ema1 = mat.getElement(3);

		// configure element attributes
		tg1.getAttribute("len").setValue("20");
		tg1.getAttribute("gap").setValue("5");
		tg1.getAttribute("initial value").setValue("50");
		tg1.getAttribute("p1").setValue("0.25");
		ema1.getAttribute("len").setValue("3");
		ema1.getAttribute("alpha").setValue("0.5");

		// configure element connections
		ema1.getInputs().get(0).connectTo(tg1.getOutputs().get(0));
		lgr.getInputs().get(0).connectTo(tg1.getOutputs().get(0));
		lgr.getInputs().get(1).connectTo(ema1.getOutputs().get(0));
		logger.info("mat is: " + mat);

		mat.configureHW();
	}

	@Override
	protected void sendTradeBurst(MatApi mat, EventFeed feed) throws Exception {
		sendCmd(2,"start");
	}

	public static void main(String[] args) {
		MatSimTest sys = new MatSimTest();
		sys.boot();
	}


}

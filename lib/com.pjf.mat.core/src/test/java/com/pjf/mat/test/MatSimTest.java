package com.pjf.mat.test;

import com.pjf.marketsim.EventFeed;
import com.pjf.mat.api.Element;
import com.pjf.mat.api.MatApi;
import com.pjf.mat.api.MatElementDefs;
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
		Element hloc = mat.getElement(4);
		Element atr = mat.getElement(5);
		Element adx = mat.getElement(6);

		// configure element attributes
		tg1.getAttribute("len").setValue("100");
		tg1.getAttribute("gap").setValue("1");
		tg1.getAttribute("initial value").setValue("50");
		tg1.getAttribute("p1").setValue("0.25");
		ema1.getAttribute("len").setValue("3");
		ema1.getAttribute("alpha").setValue("0.5");
		hloc.getAttribute("period").setValue("10");
		hloc.getAttribute("metric").setValue("" + MatElementDefs.EL_HLOC_L_PRVM1_C);
		hloc.getAttribute("throttle").setValue("0");
		atr.getAttribute("len").setValue("3");
		atr.getAttribute("alpha").setValue("0.5");
		atr.getAttribute("IP_Has_Close(N-1)").setValue("1");
		adx.getAttribute("PDN EMA len").setValue("3");
		adx.getAttribute("PDN EMA alpha").setValue("0.5");
		adx.getAttribute("NDN EMA len").setValue("3");
		adx.getAttribute("NDN EMA alpha").setValue("0.5");
		adx.getAttribute("ADX EMA len").setValue("3");
		adx.getAttribute("ADX EMA alpha").setValue("0.5");
		
		// configure element connections
		hloc.getInputs().get(0).connectTo(tg1.getOutputs().get(0));
		atr.getInputs().get(0).connectTo(hloc.getOutputs().get(0));
		adx.getInputs().get(0).connectTo(hloc.getOutputs().get(0));
//		lgr.getInputs().get(0).connectTo(tg1.getOutputs().get(0));
		lgr.getInputs().get(1).connectTo(adx.getOutputs().get(0));
		logger.info("mat is: " + mat);

		mat.configureHW();
	}

	@Override
	protected void sendTradeBurst(MatApi mat, EventFeed feed) throws Exception {
		sendCmd(2,"start");
		Thread.sleep(5000);
	}

	public static void main(String[] args) {
		MatSimTest sys = new MatSimTest();
		sys.boot();
	}


}

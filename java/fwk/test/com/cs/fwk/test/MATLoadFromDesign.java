package com.cs.fwk.test;

import com.cs.fwk.api.Element;
import com.cs.fwk.api.MatApi;
import com.cs.fwk.core.marketsim.EventFeedInt;
import com.cs.fwk.core.marketsim.SymbolEventFeed;
import com.cs.fwk.core.sys.MatSystem;
import com.cs.fwk.core.util.DesignUtils;

import com.cs.fwk.api.comms.CxnInt;

import com.cs.fwk.util.file.FileUtils;

public class MATLoadFromDesign extends MatSystem {

	@Override
	protected void start() throws Exception {
//		init("resources/mat.properties.32.rmo");
		init("C:/Users/pjf/Documents/GitHub/mat-hw/PALETTES/mat.32v84.csp","192.168.0.9",2000);
	}
	
	@Override
	protected void configure(MatApi mat) throws Exception {
		String designText = FileUtils.readFileToString("C:/Users/pjf/Documents/GitHub/mat-host/MAT_designer/DESIGNS/V1.cdf", DesignUtils.MAX_DESIGN_SIZE);
		mat.loadDesign(designText);

		// configure system attributes
		Element sys = mat.getModel().getElement(0);
		sys.getAttribute("lookup_audit_autosend").setValue("4");
		sys.getAttribute("router_audit_autosend").setValue("4");

		logger.info("mat is: " + mat);
		mat.configureHW();
	}

	@Override
	protected EventFeedInt createEventFeeder(CxnInt cxn) throws Exception {
		EventFeedInt fd = new SymbolEventFeed(cxn,15000);
		return fd;
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
			feed.sendTradeBurst("resources/GLP_27667_1a.csv",100,5,3);
		}
		Thread.sleep(5000);
	}

	public static void main(String[] args) {
		MATLoadFromDesign sys = new MATLoadFromDesign();
		sys.boot();
	}


}

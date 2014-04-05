package com.pjf.mat.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.pjf.mat.api.Attribute;
import com.pjf.mat.api.Cmd;
import com.pjf.mat.api.Element;
import com.pjf.mat.api.MatApi;
import com.pjf.mat.api.MatElementDefs;
import com.pjf.mat.api.MatModel;
import com.pjf.mat.api.Status;
import com.pjf.mat.api.Timestamp;
import com.pjf.mat.api.comms.Comms;
import com.pjf.mat.api.util.AttributeCalcInt;
import com.pjf.mat.api.util.SignatureResult;
import com.pjf.mat.impl.element.BasicCmd;
import com.pjf.mat.util.Conversion;
import com.pjf.mat.util.DesignUtils;

public class MatInterface implements MatApi {
	private final static Logger logger = Logger.getLogger(MatInterface.class);
	private static final String CALCULATOR_PKG = "com.pjf.mat.config.calculators";

	private final Comms comms;
	private final MatModel model;
	private long clockOriginMs;
	
	public MatInterface(Comms comms, MatModel model) throws Exception {
		this.comms = comms;
		this.model = model;
	}


	@Override
	public MatModel getModel() {
		return this.model;
	}	
	
	@Override
	public void requestHWStatus() {
		try {
			comms.requestStatus();
		} catch (Exception e) {
			logger.error("Error requesting status: " + e);
		}		
	}

	@Override
	public void configureHW() throws Exception {
		recalcCalculatedAttrs();
		logger.info("About to encode the following configuration:");
		for (Element el : model.getElements()){
			logger.info("Element: " + el);
		}
		try {
			comms.sendConfig(model.getElements());
		} catch (IOException e) {
			logger.error("Error sending conifg: " + e);
		}		
	}
	
	@Override
	public void recalcCalculatedAttrs() throws Exception {
		logger.info("Recalculating calculated attributes...");
		for (Element el : model.getElements()){
			recalcElAttrs(el);
		}
	}


	@Override
	public List<Attribute> recalcElAttrs(Element el) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, Exception {
		List<Attribute> attrList = new ArrayList<Attribute>();
		for (Attribute attr : el.getAttributes()) {
			if (attr.isCalculated()) {
				String spec = attr.getCalcSpecs();
				String tokens[] = spec.split(":");
				String calcClassName = tokens[0];
				String arg = "";
				if (tokens.length > 1) {
					arg = tokens[1];
				}
				// load the class and execute the calculation
				String cn = CALCULATOR_PKG + "." + calcClassName;
				AttributeCalcInt calc = (AttributeCalcInt) ClassLoader.getSystemClassLoader().loadClass(cn).newInstance();
				calc.calculate(attr.getName(), el, arg);
				attrList.add(attr);
				logger.info("Recalculated attr: " + attr.toString() + " on " + el.getShortName());
			}
		}
		return attrList;
	}


	@Override
	public void sendCmd(Cmd cmd) {
		try {
			comms.sendCmd(cmd);
		} catch (Exception e) {
			logger.error("Error sending cmd[" + cmd + "]: " + e);
		}
	}

	@Override
	public String toString() {
		for (Element el : model.getElements()) {
			logger.info(el);
		}
		return "[mat: " + model.getElements().size() + " elements]";
	}


	public void shutdown() {
		comms.shutdown();		
	}


	@Override
	public void putIntoConfigMode() throws Exception {
		logger.info("Ensuring system is in config mode.");
		boolean didReset = false;
		for (Element el : getModel().getElements()) {
			if (el.getId() != 0) {	// ignore router element
				Status s = el.getElementStatus();
				if (!s.isInConfigState()) {
					logger.info("Resetting " + el.getShortName() + " to force it to config state..");
					sendCmd(new BasicCmd(el, "reset", MatElementDefs.EL_C_RESET, 0));
					didReset = true;
				}
			}
		}
		if (didReset) {
			// check that all elements are ready for configuration
			requestHWStatus();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				logger.info("Sleep interrupted - " + e);
			}
			// check all in config mode now
			for (Element el : getModel().getElements()) {
				if (el.getId() != 0) {	// ignore router element
					Status s = el.getElementStatus();
					if (!s.isInConfigState()) {
						logger.error("Unable to force element " + el.getShortName() + " into config state.");
						throw new Exception("System not ready for configuration - el:" + el.getShortName());
					}
				}
			}
		}		
		logger.info("Ready to config ...");
	}
	

	/**
	 * Check that the HW signature matches with the configuration we have loaded.
	 * 
	 * @throws Exception if the configuration doesn't match
	 */
	@Override
	public SignatureResult checkHWSignature() throws Exception {
		long paletteSignature = getSWSignature();
		logger.info("Palette Signature is " + Conversion.toHexLongString(paletteSignature));
		long signatureHW = comms.getHWSignature();
		logger.info("Signatures HW=" + Conversion.toHexLongString(signatureHW) + 
										" Palette=" + Conversion.toHexLongString(paletteSignature));
		if (signatureHW != paletteSignature) {
			String msg = "Configuration signatatures dont match: Signatures HW=" + 
			Conversion.toHexLongString(signatureHW) + " Palette=" + Conversion.toHexLongString(paletteSignature);
			logger.error(msg);
// FIXME - enable this throw once HW sig is returned ok
//			throw new Exception(msg);
		}
		return new SignatureResult(signatureHW,paletteSignature);
	}


	/**
	 * Calculate SW configuration signature using the same alg as is used in the HW
	 * 
	 * @return 64 bit signature
	 */
//	@Override
	public long getSWSignature() {
		return model.getSWSignature();
	}


	@Override
	public void syncClock(long ltime) throws Exception {
		this.clockOriginMs = ltime;
		comms.synchroniseClock(ltime);	
	}

	@Override
	public Timestamp getCurrentTime() {
		long ctime = System.currentTimeMillis();
		Timestamp ts = new Timestamp(clockOriginMs,ctime,comms.getHWStatus().getMicrotickPeriod());
		return ts;
	}

	@Override
	public void reqLkuAuditLogs() throws Exception {
		comms.requestLkuAuditLogs();
	}

	@Override
	public void reqRtrAuditLogs() throws Exception {
		comms.requestRtrAuditLogs();
	}

	@Override
	public void resetCounters() {
		try {
			comms.resetCounters();
		} catch (Exception e) {
			logger.error("Error requesting status: " + e);
		}		
	}


	@Override
	public void loadDesign(String designText) throws Exception {
		JSONObject design = JSONObject.fromObject(designText);
		loadDesign(design);
	}


	@Override
	public void loadDesign(JSONObject design) throws Exception {
		DesignUtils.parseDesign(design,model);
	}





}

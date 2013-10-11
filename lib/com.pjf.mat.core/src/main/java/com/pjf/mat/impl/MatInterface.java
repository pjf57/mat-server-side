package com.pjf.mat.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.pjf.mat.api.Attribute;
import com.pjf.mat.api.Cmd;
import com.pjf.mat.api.Comms;
import com.pjf.mat.api.Element;
import com.pjf.mat.api.MatApi;
import com.pjf.mat.api.MatModel;
import com.pjf.mat.api.Timestamp;
import com.pjf.mat.api.util.AttributeCalcInt;
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
	
	/**
	 * Recalculate all the calculated attributes in the model
	 * @throws Exception 
	 */
	public void recalcCalculatedAttrs() throws Exception {
		logger.info("Recalculating calculated attributes...");
		for (Element el : model.getElements()){
			recalcElAttrs(el);
		}
	}


	/**
	 * Recalculate all the attributes for an element
	 * 
	 * @param el	the element
	 * @return list of attributes that were recalculated
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws Exception
	 */
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

	/**
	 * Check that the HW signature matches with the configuration we have loaded.
	 * 
	 * @throws Exception if the configuration doesn't match
	 */
	@Override
	public void checkHWSignature() throws Exception {
		long signatureSW = getSWSignature();
		logger.info("SW Signature is " + Conversion.toHexLongString(signatureSW));
		long signatureHW = comms.getHWSignature();
		logger.info("Signatures HW=" + Conversion.toHexLongString(signatureHW) + 
										" SW=" + Conversion.toHexLongString(signatureSW));
		if (signatureHW != signatureSW) {
			String msg = "Configuration signatatures dont match: Signatures HW=" + 
			Conversion.toHexLongString(signatureHW) + " SW=" + Conversion.toHexLongString(signatureSW);
			logger.error(msg);
// FIXME - enable this throw once HW sig is returned ok
//			throw new Exception(msg);
		}
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
		Timestamp ts = new Timestamp(clockOriginMs,ctime);
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

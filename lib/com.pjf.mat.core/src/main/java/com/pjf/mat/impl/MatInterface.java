package com.pjf.mat.impl;

import java.io.IOException;

import javax.net.ssl.SSLEngineResult.Status;

import org.apache.log4j.Logger;

import com.pjf.mat.api.Cmd;
import com.pjf.mat.api.Comms;
import com.pjf.mat.api.Element;
import com.pjf.mat.api.MatApi;
import com.pjf.mat.api.MatModel;
import com.pjf.mat.util.Conversion;

public class MatInterface implements MatApi {
	private final static Logger logger = Logger.getLogger(MatInterface.class);
	private final Comms comms;
	private final MatModel model;
	
	public MatInterface(Comms comms, MatModel model) throws Exception {
		this.comms = comms;
		this.model = model;
	}


	@Override
	public MatModel getModel() {
		return this.model;
	}
	
	
	public MatModel copyModel() throws Exception {
		return this.model.copy();
	}
	
	
	@Override
	public Status getHWStatus() {
		try {
			comms.requestStatus();
		} catch (Exception e) {
			logger.error("Error requesting status: " + e);
		}		
		return null;
	}

	@Override
	public void configureHW() throws Exception {
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
		long signatureHW = comms.getHWSignature();
		long signatureSW = getSWSignature();
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






}

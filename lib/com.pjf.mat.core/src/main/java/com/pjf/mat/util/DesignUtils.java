package com.pjf.mat.util;

import org.apache.log4j.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.pjf.mat.api.Element;
import com.pjf.mat.api.MatElementDefs;
import com.pjf.mat.api.MatModel;

public class DesignUtils {
	private final static Logger logger = Logger.getLogger(DesignUtils.class);

	public static final int MAX_DESIGN_SIZE = 10000;	// max encoded design size

	/**
	 * Decode one CB, setting attr values into the model
	 * 
	 * @param obj	JSON encoding of the CB (contains gr obj and cb obj)
	 * @param model	the model
	 * @return	element representing the CB
	 * @throws Exception 
	 */
	public static Element decodeCB(JSONObject obj, MatModel model) throws Exception {
		JSONObject cbj = obj.getJSONObject("cb");
		int id = cbj.getInt("elId");
		Element el = model.getElement(id);
		// set attributes
		JSONArray attrList = cbj.getJSONArray("attrs");
		for (int a=0; a<attrList.size(); a++) {
			JSONObject attr = attrList.getJSONObject(a);
			String name = attr.getString("name");
			String val = attr.getString("val");
			el.getAttribute(name).setValue(val);
		}
		return el;
	}

	/**
	 * Format JSON Design string so that it is human readable, but still parsable
	 * @param JSON encoded design text
	 * @return formatted design text
	 */
	public static String formatDesign(String text) {
		int level = 0;
		int i = 0;
		StringBuffer buf = new StringBuffer();
		
		while (i < text.length()) {
			char ch = text.charAt(i);
			if (ch == '{') {
				if (level > 0) {
					// indent
					buf.append('\n');
					for (int j=0; j<level; j++) {
						buf.append("  ");
					}
				}
				level++;
				buf.append(ch);
			} else if (ch == '}') {
				if (level > 0) {
					// indent
					buf.append('\n');
					for (int j=0; j<level; j++) {
						buf.append("  ");
					}
				}
				level--;
				buf.append(ch);
				buf.append('\n');
			} else {
				buf.append(ch);
			}
			i++;
		}
				
		return buf.toString();
	}

	/**
	 * Parse a design and load it into the model
	 * @param design - JSON encoded design
	 * @param model - model to load into
	 * @throws Exception on parse error
	 */
	public static void parseDesign(JSONObject design, MatModel model) throws Exception {
		JSONObject desObj = design.getJSONObject("design");
		// parse CBs
		JSONArray cbList = desObj.getJSONArray("cbs");
		for (int i=0; i<cbList.size(); i++) {
			JSONObject cbEntry = cbList.getJSONObject(i);
			DesignUtils.decodeCB(cbEntry,model);
		}
		// parse CXNS
		for (Element el : model.getElements()) {
			el.removeAllConnections();
		}
		JSONArray cxnList = desObj.getJSONArray("cxns");
		for (int i=0; i<cxnList.size(); i++) {
			JSONObject cxnEntry = cxnList.getJSONObject(i);
			int cbFrom = cxnEntry.getInt("cbFrom");
			int opId = cxnEntry.getInt("opId");
			int cbTo = cxnEntry.getInt("cbTo");
			int ipId = cxnEntry.getInt("ipId");
			Element from = model.getElement(cbFrom);
			Element to = model.getElement(cbTo);
			to.getInputs().get(ipId).connectTo(from.getOutputs().get(opId));		
		}
		// parse output logging requests
		int numLogCxns = 0;
		JSONArray oplList = desObj.getJSONArray("logging");
		if (oplList.size() > 0) {
			// make cxns to logger
			// get Logger element from model
			Element logger = null;
			for (Element el : model.getElements()) {
				if (el.getHWType() == MatElementDefs.EL_TYP_LOG) {
					logger = el;
					break;
				}
			}
			if (logger == null) {
				throw new Exception("decodeDesign() cant find logger in model");
			}
			int maxLoggerCxns = logger.getInputs().size();
			int ipId = 0;
			for (int i=0; i<oplList.size(); i++) {
				if (numLogCxns >= maxLoggerCxns) {
					throw new Exception("Too many logger cxns, max is " + maxLoggerCxns);
				}
				JSONObject oplEntry = oplList.getJSONObject(i);
				int cbFrom = oplEntry.getInt("cbId");
				int opId = oplEntry.getInt("opId");
				Element from = model.getElement(cbFrom);
				logger.getInputs().get(ipId).connectTo(from.getOutputs().get(opId));
				ipId++;
				numLogCxns++;
			}
		}
		logger.info("Made " + numLogCxns + " logger connections.");
	}
	

}

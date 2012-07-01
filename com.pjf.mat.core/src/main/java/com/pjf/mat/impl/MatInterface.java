package com.pjf.mat.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.net.ssl.SSLEngineResult.Status;

import org.apache.log4j.Logger;

import com.pjf.mat.api.Attribute;
import com.pjf.mat.api.Cmd;
import com.pjf.mat.api.Comms;
import com.pjf.mat.api.Element;
import com.pjf.mat.api.InputPort;
import com.pjf.mat.api.MatApi;
import com.pjf.mat.api.OutputPort;
import com.pjf.mat.impl.element.BasicCmd;
import com.pjf.mat.impl.element.BasicElement;
import com.pjf.mat.impl.element.BasicInputPort;
import com.pjf.mat.impl.element.BasicOutputPort;
import com.pjf.mat.impl.element.FloatAttribute;
import com.pjf.mat.impl.element.FloatOutputPort;
import com.pjf.mat.impl.element.HexAttribute;
import com.pjf.mat.impl.element.IntegerAttribute;
import com.pjf.mat.impl.element.IntegerOutputPort;
import com.pjf.mat.impl.element.StringAttribute;


public class MatInterface implements MatApi{
	private final static Logger logger = Logger.getLogger(MatInterface.class);
	private final Properties props;
	private final Comms comms;
	private Map<Integer,Element> elements;	// holds the actual configured elements
	private Map<String,Element> types;		// holds the different element types (key=type)
	
	public MatInterface(Properties props, Comms comms) {
		this.props = props;
		this.comms = comms;
		elements = new HashMap<Integer,Element>();
		types = new HashMap<String,Element>();	
		initialise();
	}


	@Override
	public Collection<Element> getElements() {
		return elements.values();
	}


	@Override
	public Element getElement(int id) {
		return elements.get(new Integer(id));
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
		for (Element el : getElements()){
			logger.info("Element: " + el);
		}
		try {
			comms.sendConfig(elements.values());
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

	
	private void initialise() {
		// read info about the basic types of element
		
		int id = 1;
		Element type = null;
		while((type=readType(id)) != null) {
			logger.info("Found type: " + type);
			types.put(type.getType(), type);
			id++;
		}

		// read element info
		id = 0;
		Element element = null;
		while((element=readElement(id)) != null) {
			logger.info("Found Element: " + element);
			elements.put(new Integer(id),element);
			id++;
		}
		
		
	}

	// read one element from properties - return null if non existent
	private Element readElement(int id) {
		BasicElement el = null;
		String e = "element" + id;
		String elType = props.getProperty(e + ".type");
		if (elType != null) {
			Element type = types.get(elType);
			if (type == null) {
				logger.error("No such type: " + elType + " for element " + id);
			}
			el = new BasicElement(id,elType,type);
		}
		return el;
	}


	// read one type from properties - return null if non existent
	private Element readType(int id) {
		BasicElement type = null;
		String t = "type" + id;
		String ids = props.getProperty(t + ".id");
		if (ids != null) {
			String typeName = props.getProperty(t + ".name");
			type = new BasicElement(Integer.parseInt(ids),typeName);
		}
		// read attributes
		int an = 1;
		boolean keepReading = true;
		while (keepReading) {
			String a = t + ".attr" + an;
			String aIds = props.getProperty(a + ".id");
			if (aIds == null) {
				keepReading = false;
			} else {
				Attribute attr = null;
				String attrName = props.getProperty(a + ".name");
				String attrType = props.getProperty(a + ".type");
				String attrConfigS = props.getProperty(a + ".config");
				int configId = Integer.parseInt(attrConfigS);
				if (attrType.equals("int")) {
					attr = new IntegerAttribute(attrName,configId);
					type.addAttribute(attr);
				} else if (attrType.equals("hex")) {
					attr = new HexAttribute(attrName,configId);
					type.addAttribute(attr);
				} else if (attrType.equals("string")) {
					attr = new StringAttribute(attrName,configId);
					type.addAttribute(attr);
				} else if (attrType.equals("float")) {
					attr = new FloatAttribute(attrName,configId);
					type.addAttribute(attr);
				} else {
					logger.error("Unrecognized attribute type: " + attrType);
				}
				an++;
			}			
		}
		
		// read inputs
		int in = 1;
		keepReading = true;
		while (keepReading) {
			String i = t + ".input" + in;
			String iIds = props.getProperty(i + ".id");
			if (iIds == null) {
				keepReading = false;
			} else {
				InputPort ip = null;
				String ipName = props.getProperty(i + ".name");
				String ipType = props.getProperty(i + ".type");
				ip = new BasicInputPort(in,ipName,ipType);
				type.addInputPort(ip);
				in++;
			}
		}

		// read outputs
		int on = 1;
		keepReading = true;
		while (keepReading) {
			String o = t + ".output" + on;
			String oIds = props.getProperty(o + ".id");
			if (oIds == null) {
				keepReading = false;
			} else {
				OutputPort op = null;
				String opName = props.getProperty(o + ".name");
				String opType = props.getProperty(o + ".type");				
				if (opType.equals("int")) {
					op = new IntegerOutputPort(type,on,opName,opType);
				} else if (opType.equals("float")) {
					op = new FloatOutputPort(type,on,opName,opType);
				} else {
					op = new BasicOutputPort(type,on,opName,opType);
				}
				type.addOutputPort(op);
				on++;
			}
		}

		// read cmds
		int cn = 1;
		keepReading = true;
		while (keepReading) {
			String c = t + ".cmd" + cn;
			String cIds = props.getProperty(c + ".id");
			if (cIds == null) {
				keepReading = false;
			} else {
				Cmd cmd = null;
				String cName = props.getProperty(c + ".name");
				String cConfigS = props.getProperty(c + ".config");
				int configId = Integer.parseInt(cConfigS);
				cmd = new BasicCmd(type,cName,configId);
				type.addCmd(cmd);
				cn++;
			}
		}

		return type;
	}

	@Override
	public String toString() {
		for (Element el : elements.values()) {
			logger.info(el);
		}
		return "[mat: " + elements.size() + " elements]";
	}


	public void shutdown() {
		comms.shutdown();		
	}





}

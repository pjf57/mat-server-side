package com.cs.fwk.core.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import com.cs.fwk.api.AttrSysType;
import com.cs.fwk.api.Attribute;
import com.cs.fwk.api.Cmd;
import com.cs.fwk.api.Element;
import com.cs.fwk.api.EnumValue;
import com.cs.fwk.api.InputPort;
import com.cs.fwk.api.MatModel;
import com.cs.fwk.api.OutputPort;
import com.cs.fwk.api.gridattr.GridAttribute;
import com.cs.fwk.api.gridattr.GridColumnSpec;
import com.cs.fwk.core.impl.element.BasicCmd;
import com.cs.fwk.core.impl.element.BasicElement;
import com.cs.fwk.core.impl.element.BasicInputPort;
import com.cs.fwk.core.impl.element.BasicOutputPort;
import com.cs.fwk.core.impl.element.FloatOutputPort;
import com.cs.fwk.core.impl.element.IntegerOutputPort;
import com.cs.fwk.util.attr.EnumAttribute;
import com.cs.fwk.util.attr.FloatAttribute;
import com.cs.fwk.util.attr.HexAttribute;
import com.cs.fwk.util.attr.IntegerAttribute;
import com.cs.fwk.util.attr.StringAttribute;
import com.cs.fwk.util.attr.UserDefAttribute;


public class MatInterfaceModel implements MatModel {
	private final static Logger logger = Logger.getLogger(MatInterfaceModel.class);
	private static final String MODEL_VER = "01.00";
	protected final Properties props;
	protected Map<Integer,Element> elements;	// holds the actual configured elements
	protected Map<String,Element> types;		// holds the different element types (key=type)
	
	public MatInterfaceModel(Properties props) throws Exception {
		this.props = props;
		elements = new HashMap<Integer,Element>();
		types = new HashMap<String,Element>();	
		initialise();
	}

	@Override
	public List<Element> getElements() {
		List<Element> list = new ArrayList<Element>(elements.values());
		Collections.sort(list);
		return list;
	}

	@Override
	public Set<String> getTypes() {
		return types.keySet();
	}

	@Override
	public Element getType(String typeName) {
		if (types.containsKey(typeName)) {
			return types.get(typeName);
		}
		return null;
	}

	@Override
	public Element getElement(int id) {
		return elements.get(new Integer(id));
	}
	
	private void initialise() throws Exception {
		// read info about the basic types of element
		
		logger.info("Reading types from properties ...");
		int id = 1;
		Element type = null;
		while((type=readType(id)) != null) {
			logger.info("Found type: " + type);
			types.put(type.getType(), type);
			id++;
		}
		logger.info("Read " + (id-1) + " types.");

		// read element info
		logger.info("Reading elements from properties ...");
		id = 0;
		Element element = null;
		while((element=readElement(id)) != null) {
			logger.info("Found Element: " + element);
			elements.put(new Integer(id),element);
			id++;
		}
		logger.info("Read " + (id-1) + " elements.");
	}

	// read one element from properties - return null if non existent
	private Element readElement(int id) throws Exception {
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
	private Element readType(int id) throws Exception {
		BasicElement type = null;
		String t = "type" + id;
		String ids = props.getProperty(t + ".id");
		if (ids != null) {
			String tName = props.getProperty(t + ".name");
			String[] components = tName.split(":");
			if (components.length != 2) {
				String msg = "Bad type format in type index=" + id +
					", should be [typename:hwtype] is [" + tName + "]";
				logger.error("readType(): " + msg);
				throw new Exception(msg);
			}
			String typeName = components[0];
			int hwType = Integer.parseInt(components[1]);
			type = new BasicElement(Integer.parseInt(ids),typeName,hwType);
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
				String sysTypeS = props.getProperty(a + ".systype");
				String defaultS = props.getProperty(a + ".default");
				String orderS = props.getProperty(a + ".order");
				String calcS = props.getProperty(a + ".calc");
				int order = 99;
				if (orderS != null) {
					order = Integer.parseInt(orderS);
				}
				AttrSysType sysType = AttrSysType.NORMAL;
				if (sysTypeS != null) {
					sysType = AttrSysType.valueOf(sysTypeS);
					if (sysType == null) {
						logger.error("Unrecognized systype: " + sysTypeS +
								" on " + a + ".name = " + attrName);
					}
				}
				int configId = Integer.parseInt(attrConfigS);
				attr = createAttribute(type,a,attrType,attrName,configId,sysType,defaultS,order,calcS);
				type.addAttribute(attr);				
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


	/**
	 * Create an attribute according to its type
	 * 
	 * @param type 		- parent type which will contain the attribute
	 * @param prefix	- property prefix of the form: type1.attr2
	 * @param attrType	- type of the attribute
	 * @param attrName
	 * @param configId
	 * @param sysType
	 * @param defaultS
	 * @param order
	 * @param calcS
	 * @return newly created attribute
	 * @throws Exception 
	 */
	private Attribute createAttribute(BasicElement type, String prefix, String attrType, String attrName,
			int configId, AttrSysType sysType, String defaultS, int order,
			String calcS) throws Exception {
		Attribute attr = null;
		if (attrType.equals("int")) {
			attr = new IntegerAttribute(type,attrName,configId,sysType,defaultS,order,calcS);
		} else if (attrType.equals("hex")) {
			attr = new HexAttribute(type,attrName,configId,sysType,defaultS,order,calcS);
		} else if (attrType.equals("string")) {
			attr = new StringAttribute(type,attrName,configId,sysType,defaultS,order,calcS);
		} else if (attrType.equals("float")) {
			attr = new FloatAttribute(type,attrName,configId,sysType,defaultS,order,calcS);
		} else if (attrType.equals("enum")) {
			attr = loadEnumAttribute(type,prefix,attrName,configId,sysType,defaultS,order,calcS);			
			type.addAttribute(attr);
		} else if (attrType.startsWith("string:")) {
			String converter = attrType.split(":")[1];
			attr = new UserDefAttribute(type,attrName,configId,converter,sysType,defaultS,order,calcS);
		} else if (attrType.startsWith("grid/")) {
			int restIdx = attrType.indexOf('/');
			String gridSpec = attrType.substring(restIdx+1);
			attr = createGridAttribute(type,gridSpec, attrName,configId,sysType,defaultS,order,calcS);
		} else {
			logger.error("Unrecognized attribute type: " + attrType);
			throw new Exception("Unrecognized attribute type: " + attrType);
		}
		return attr;
	}

	/**
	 * Parse a type string and create a grid attribute
	 * 
	 * type13.attr4.type=grid/Symbol:string,Instr_id:int/SymbolGridEncode
	 * 
	 * @param type		- parent type which will contain the attribute
	 * @param gridSpec	- specification for grid (the type string following the "grid/" part
	 * @param attrName
	 * @param configId
	 * @param sysType
	 * @param defaultS
	 * @param order
	 * @param calcS
	 * @return grid attribute
	 * @throws Exception if error parsing the gridspec or creating the attr
	 */
	private Attribute createGridAttribute(BasicElement type, String gridSpec, String attrName,
			int configId, AttrSysType sysType, String defaultS, int order,
			String calcS) throws Exception {
		// Split gridspec into columdef part and encoder part
		String[] parts = gridSpec.split("/");
		if (parts.length != 2) {
			throw new Exception("createGridAttribute() - bad gridspec for attr " + attrName + " [" + gridSpec + "]");
		}
		String colSpec = parts[0];
		String converter = parts[1];
		// parse the colspec
		List<GridColumnSpec> gcs = new ArrayList<GridColumnSpec>();
		String[] cols = colSpec.split(",");
		if (cols.length == 0) {
			throw new Exception("createGridAttribute() - zero cols for gridspec " + attrName + " [" + gridSpec + "]");		
		}
		int cn = 0;
		for (String cs : cols) {
			String[] csa = cs.split(":");	// split name:type
			if (csa.length != 2) {
				throw new Exception("createGridAttribute() - bad format for colspec on " + attrName + " [" + cs + "]");		
			}
			GridColumnSpec s = new GridColumnSpec(csa[0],csa[1],cn);			
			gcs.add(s);
			cn++;
		}
		// now create the attr
		Attribute attr = new GridAttribute(type, attrName, configId, converter, sysType, order, null, gcs);
		logger.debug("Created: " + attr.toString());
		return attr;
	}

	/**
	 * Create an enum attribute and load its values list from the properties file
	 * 
	 * @param prefix - property prefix of the form: type1.attr2
	 * @param attrName
	 * @param configId
	 * @param sysType
	 * @param defaultStr 
	 * @param order 
	 * @param calcS 
	 * @return the completed attribute
	 * @throws Exception 
	 * 
	 * The values list is defined in the properties in the form:
	 * 	type1.attr2.enum1=name:value:description
	 */
	private Attribute loadEnumAttribute(Element type, String prefix, String attrName, 
			int configId, AttrSysType sysType, String defaultStr, int order, String calcS) throws Exception {
		EnumAttribute attr = new EnumAttribute(type,attrName,configId,sysType,order,calcS);
		int en = 1;
		boolean keepReading = true;
		while (keepReading) {
			String p = prefix + ".enum" + en;
			String enumSpec = props.getProperty(p);
			if (enumSpec == null) {
				keepReading = false;
			} else {
				// decode the enum spec
				String[] tokens = enumSpec.split(":");
				if (tokens.length < 2  ||  tokens.length > 3) {
					throw new Exception("bad format for enum specification at " + p +
							" [" + enumSpec + "]");
				}
				String description = "";
				if (tokens.length > 2) {
					description = tokens[2];
				}
				String sval = tokens[1];
				EnumValue ev = null;
				if (sval.charAt(0) == 'x') {
					// intepret as hex
					String hv = sval.substring(1);
					ev = new EnumValue(tokens[0],Integer.parseInt(hv,16),en,description);
				} else {
					// interpret as decimal
					ev = new EnumValue(tokens[0],Integer.parseInt(sval),en,description);
				}
				attr.addEnumValue(ev);
				en++;
			}
		}
		if (defaultStr != null) {
			attr.setValue(defaultStr);
		}
		return attr;
	}

	@Override
	public String toString() {
		for (Element el : elements.values()) {
			logger.info(el);
		}
		StringBuilder sb = new StringBuilder();
		sb.append("[mat: ").append(elements.size());
		sb.append(" elements / 0x").append(Long.toHexString(getSWSignature()).toUpperCase());
		sb.append(" ]");
		return sb.toString();
	}

	/**
	 * Calculate SW configuration signature using the same alg as is used in the HW
	 * 
	 * @return 64 bit signature
	 */
//	@Override
	public long getSWSignature() {
		int id = 0;
		Element el;
		long crc = 1;
		while ((el = getElement(id)) != null) {
			crc += el.getHWType() * (id+1) * 1237;
			id++;
		}
		crc &= 0xffffffffffL;
		return crc;
	}


	@Override
	public Properties getProperties() {
		return this.props;
	}

	@Override
	public String getApiVersion() {
		return MODEL_VER;
	}

}

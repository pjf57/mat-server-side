package com.cs.fwk.core.impl.element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.cs.fwk.api.AttrSysType;
import com.cs.fwk.api.Attribute;
import com.cs.fwk.api.Cmd;
import com.cs.fwk.api.Element;
import com.cs.fwk.api.InputPort;
import com.cs.fwk.api.OutputPort;
import com.cs.fwk.api.Status;
import com.cs.fwk.util.ElementStatus;

public class BasicElement extends BasicItem implements Element {
	private final static Logger logger = Logger.getLogger(BasicElement.class);

	private final String type;
	private final int hwType;					// the HW ID for this type
	private List<InputPort> inputs;
	private List<OutputPort> outputs;
	private Map<String,Attribute> attributes;	// key = name
	private List<Cmd> cmds;
	private ElementStatus status;
	private boolean statusHasChanged;
	
	/**
	 * Basic constructor to make a type
	 * 
	 * @param id
	 * @param type
	 */
	public BasicElement(int id, String type, int hwType) {
		super(id);
		this.type = type;
		this.hwType = hwType;
		this.inputs = new ArrayList<InputPort>();
		this.outputs = new ArrayList<OutputPort>();
		this.attributes = new HashMap<String,Attribute>();
		this.cmds = new ArrayList<Cmd>();
		this.status = new ElementStatus();
		this.statusHasChanged = true;
	}

	/**
	 * Constructor to make an element from the specified type
	 * 
	 * @param id
	 * @param elType
	 * @param type
	 * @throws Exception 
	 */
	public BasicElement(int id, String elType, Element type) throws Exception {
		super(id);
		this.type = elType;
		this.hwType = type.getHWType();
		this.inputs = cloneInputsFromType(type);
		this.outputs = cloneOutputsFromType(type);
		this.attributes = cloneAttributesFromType(this,type);
		this.cmds = cloneCmdsFromType(type);
		this.status = new ElementStatus();
		this.statusHasChanged = true;
	}

	private Map<String,Attribute> cloneAttributesFromType(Element newParent, Element type) throws Exception {
		Map<String,Attribute> attrs = new HashMap<String,Attribute>();
		for (Attribute att : type.getAttributes()) {
			Attribute a = att.clone(newParent);
			if (a.getSysType() == AttrSysType.LKU_TARGET) {
				// default value of LKU Targets is "ALL"
				a.setValue("63");
			}
			attrs.put(a.getName(),a);
		}
		return attrs;
	}

	private List<OutputPort> cloneOutputsFromType(Element type) {
		List <OutputPort> list = new ArrayList<OutputPort>();
		for (OutputPort op : type.getOutputs()) {
			OutputPort opp = op.clone(this);
			list.add(opp);
		}
		return list;
	}

	private List<InputPort> cloneInputsFromType(Element type) {
		List <InputPort> list = new ArrayList<InputPort>();
		for (InputPort ip : type.getInputs()) {
			InputPort ipp = new BasicInputPort(ip);
			list.add(ipp);
		}
		return list;
	}

	private List<Cmd> cloneCmdsFromType(Element type) {
		List <Cmd> list = new ArrayList<Cmd>();
		for (Cmd c : type.getCmds()) {
			Cmd bc = new BasicCmd(this,c);
			list.add(bc);
		}
		return list;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public int getHWType() {
		return hwType;
	}

	@Override
	public Collection<Attribute> getAttributes() {
		return attributes.values();
	}

	@Override
	public Attribute getAttribute(String name) throws Exception {
		Attribute attr = attributes.get(name);
		if (attr == null) {
			throw new Exception("Attribute [" + name + "] doesnt exist on CB " + getShortName());
		}
		return attr;
	}

	@Override
	public Collection<Attribute> getStatusAttrs() throws Exception {
		return status.getAttributes();
	}
	
	@Override
	public List<InputPort> getInputs() {
		return inputs;
	}

	@Override
	public List<OutputPort> getOutputs() {
		return outputs;
	}
	
	@Override
	public List<Cmd> getCmds() {
		return cmds;
	}
	
	public void addAttribute(Attribute attr) {
		attributes.put(attr.getName(),attr);
	}

	public void addInputPort(InputPort ip) {
		inputs.add(ip);
	}

	public void addOutputPort(OutputPort op) {
		outputs.add(op);
	}
	
	public void addCmd(Cmd cmd) {
		cmds.add(cmd);
	}


	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer("[");
		buf.append("id="); buf.append(getId());
		buf.append(" type="); buf.append(type);
		try {
			buf.append(" Inputs:");
			for (InputPort x : inputs) {
				buf.append(x); buf.append(' ');
			}
			buf.append(" Outputs:"); 
			for (OutputPort x : outputs) {
				buf.append(x); buf.append(' ');
			}
			buf.append(" Attr:"); 	
			for (Attribute x : attributes.values()) {
				buf.append(x); buf.append(' ');
			}
			buf.append(" Cmds:"); 	
			for (Cmd x : cmds) {
				buf.append(x); buf.append(' ');
			}
			buf.append(" Status:"); 	
			for (Attribute x : status.getAttributes()) {
				buf.append(x); buf.append(' ');
			}
		} catch (Exception e) {
			logger.error("Error getting attributes for element " + getShortName() + ": " + e.getMessage());
			buf.append("*error*");
		}
		buf.append(']');
		return buf.toString();
	}

	@Override
	public Status getElementStatus() {
		return status;
	}

	@Override
	public void setStatus(Status newStatus) {
		if (!this.status.equals(newStatus)) {
			statusHasChanged = true;
		}
		this.status = new ElementStatus(newStatus.getBaseState(),
							newStatus.getRawRunState(),
							newStatus.getEventInCount());
	}

	@Override
	public int hashCode() {
		return ( 31 * getId() + (31 * type.hashCode()) * 17 ) * 17;
	}
	
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Element)) return false;
		Element other = (Element) object;
		return getId() == other.getId() 
				&& ((type != null && type.equals(other.getType()))
					|| other.getType() == null);		
	}

	@Override
	public String getShortName() {
		return Integer.toString(getId()) + "/" + getType();
	}

	@Override
	public OutputPort getOutput(String name) throws Exception {
		OutputPort out = null;
		for (OutputPort p : outputs) {
			if (p.getName().equals(name)) {
				out = p;
				break;
			}
		}
		if (out == null) {
			throw new Exception("No such output named [" + name + "] on element " + getShortName());
		}
		return out;
	}

	@Override
	public void removeAllConnections() {
		for (InputPort ip : inputs) {
			ip.removeCxn();
		}
		
	}

	@Override
	public boolean hasStatusChanged(boolean reset) {
		boolean ret = statusHasChanged;
		if (reset) {
			statusHasChanged = false;
		}
		return ret;
	}


}

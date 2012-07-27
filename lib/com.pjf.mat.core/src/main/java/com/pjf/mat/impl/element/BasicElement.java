package com.pjf.mat.impl.element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pjf.mat.api.Attribute;
import com.pjf.mat.api.Cmd;
import com.pjf.mat.api.Element;
import com.pjf.mat.api.InputPort;
import com.pjf.mat.api.OutputPort;
import com.pjf.mat.api.Status;
import com.pjf.mat.util.ElementStatus;

public class BasicElement implements Element {

	private final int id;
	private final String type;
	private final int hwType;					// the HW ID for this type
	private List<InputPort> inputs;
	private List<OutputPort> outputs;
	private Map<String,Attribute> attributes;	// key = name
	private List<Cmd> cmds;
	private ElementStatus status;
	
	/**
	 * Basic constructor to make a type
	 * 
	 * @param id
	 * @param type
	 */
	public BasicElement(int id, String type, int hwType) {
		this.id = id;
		this.type = type;
		this.hwType = hwType;
		this.inputs = new ArrayList<InputPort>();
		this.outputs = new ArrayList<OutputPort>();
		this.attributes = new HashMap<String,Attribute>();
		this.cmds = new ArrayList<Cmd>();
		this.status = new ElementStatus();
	}

	/**
	 * Constructor to make an element from the specified type
	 * 
	 * @param id
	 * @param elType
	 * @param type
	 */
	public BasicElement(int id, String elType, Element type) {
		this.id = id;
		this.type = elType;
		this.hwType = type.getHWType();
		this.inputs = cloneInputsFromType(type);
		this.outputs = cloneOutputsFromType(type);
		this.attributes = cloneAttributesFromType(type);
		this.cmds = cloneCmdsFromType(type);
		this.status = new ElementStatus();
	}

	private Map<String,Attribute> cloneAttributesFromType(Element type) {
		Map<String,Attribute> attrs = new HashMap<String,Attribute>();
		for (Attribute att : type.getAttributes()) {
			Attribute a = att.clone();
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
	public int getId() {
		return id;
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
	public Attribute getAttribute(String name) {
		return attributes.get(name);
	}

	@Override
	public Collection<Attribute> getStatusAttrs() {
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
		buf.append("id="); buf.append(id);
		buf.append(" type="); buf.append(type);
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
		buf.append(']');
		return buf.toString();
	}

	@Override
	public Status getElementStatus() {
		return status;
	}

	@Override
	public void setStatus(Status newStatus) {
		this.status = new ElementStatus(newStatus.getBaseState(),
							newStatus.getRawRunState(),
							newStatus.getEventInCount());
	}

	@Override
	public int hashCode() {
		return ( 31 * id + (31 * type.hashCode()) * 17 ) * 17;
	}
	
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Element)) return false;
		Element other = (Element) object;
		return id == other.getId() 
				&& ((type != null && type.equals(other.getType()))
					|| other.getType() == null);		
	}
}
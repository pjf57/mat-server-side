package com.pjf.mat.util;

import java.util.ArrayList;
import java.util.Collection;

import com.pjf.mat.api.Attribute;
import com.pjf.mat.api.Status;
import com.pjf.mat.util.attr.IntegerAttribute;
import com.pjf.mat.util.attr.StringAttribute;

public class ElementStatus implements Status{
	private String basis_state;					// state of basis of element
	private int el_state;						// state of element
	private long evt_cnt;						// #evts processed by element
	
	public ElementStatus() {
		this.basis_state = "unknown";
		this.el_state = 0;
		this.evt_cnt = -1;
	}

	public ElementStatus(String basisState, int elState, long ip_evt_cnt) {
		this.basis_state = basisState;
		this.el_state = elState;
		this.evt_cnt = ip_evt_cnt;
	}

	public void setElementStatus(String basis_state, int el_state, long evt_cnt) {
		this.basis_state = basis_state;
		this.el_state = el_state;
		this.evt_cnt = evt_cnt;
	}

	@Override
	public String getBaseState() {
		return basis_state;
	}

	@Override
	public String getRunState() {
		return Integer.toBinaryString(el_state);
	}

	@Override
	public long getEventInCount() {
		return evt_cnt;
	}

	@Override
	public Collection<Attribute> getAttributes() {
		Collection<Attribute> attrs = new ArrayList<Attribute>();
		Attribute attr;
		attr = new StringAttribute("basis state",0);
		attr.setValue(basis_state);	
		attrs.add(attr);
		attr = new IntegerAttribute("element state",0);
		attr.setValue(Integer.toBinaryString(el_state));	
		attrs.add(attr);
		attr = new IntegerAttribute("ip_evt_cnt",0);
		attr.setValue(Long.toString(evt_cnt));	
		attrs.add(attr);		
		return attrs;
	}

	@Override
	public int getRawRunState() {
		return el_state;
	}
	
	@Override
	public String toString() {
		return "[" + basis_state + "," + el_state + "," + evt_cnt + "]";
	}

}

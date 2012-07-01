package com.pjf.mat.impl.element;

import java.util.ArrayList;
import java.util.Collection;

import com.pjf.mat.api.Attribute;

public class ElementStatus {
	private String basis_state;					// state of basis of element
	private int el_state;						// state of element
	private int evt_cnt;						// #evts processed by element
	
	public ElementStatus() {
		this.basis_state = "unknown";
		this.el_state = 0;
		this.evt_cnt = -1;
	}

	public void setElementStatus(String basis_state, int el_state, int evt_cnt) {
		this.basis_state = basis_state;
		this.el_state = el_state;
		this.evt_cnt = evt_cnt;
	}

	public Collection<Attribute> getAsAttributes() {
		Collection<Attribute> attrs = new ArrayList<Attribute>();
		Attribute attr;
		attr = new StringAttribute("basis state",0);
		attr.setValue(basis_state);	
		attrs.add(attr);
		attr = new IntegerAttribute("element state",0);
		attr.setValue(Integer.toBinaryString(el_state));	
		attrs.add(attr);
		attr = new IntegerAttribute("evt_cnt",0);
		attr.setValue(Integer.toString(evt_cnt));	
		attrs.add(attr);		
		return attrs;
	}

}

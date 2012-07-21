package com.pjf.mat.sim;

import com.pjf.mat.sim.model.SimElement;

public class ElementException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final SimElement element;

	public ElementException(SimElement element) {
		this.element = element;
	}

	public ElementException(SimElement element,String msg) {
		super(msg);
		this.element = element;
	}

	public ElementException(SimElement element,Throwable t) {
		super(t);
		this.element = element;
	}

	public ElementException(SimElement element, String msg, Throwable t) {
		super(msg, t);
		this.element = element;
	}
	
	public SimElement getElement() {
		return element;
	}

}

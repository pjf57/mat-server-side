package com.pjf.mat.api;

import java.util.Collection;

import javax.net.ssl.SSLEngineResult.Status;

/** @model */
public interface MatApi {	
	/** 
	 * @model type="Element" containment="true" 
	 */
	public Collection<Element> getElements();	// return collection of mutable elements
	/**
	 * @model
	 */
	public Element getElement(int id);			// return element for this id or null
	/**
	 * @model
	 */
	public void configureHW() throws Exception;	// send config state of elements to HW
	/**
	 * @model
	 */
	public void sendCmd(Cmd cmd);				// send a cmd to the HW
	/**
	 * @model 
	 */
	public Status getHWStatus();				// get status from HW
	/**
	 * @model 
	 */
	public long getSWSignature();
	/**
	 * @model 
	 */
	public void checkHWSignature() throws Exception;
}

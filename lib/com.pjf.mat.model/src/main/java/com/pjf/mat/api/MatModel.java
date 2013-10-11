package com.pjf.mat.api;

import java.util.Collection;
import java.util.Properties;
import java.util.Set;

/** @model */
public interface MatModel {

	/**
	 * @return collection of mutable elements
	 */
	public Collection<Element> getElements(); 

	/**
	 * @return mutable element for this id or null
	 */
	public Element getElement(int id); 

	/**
	 * @return signature of the model
	 */
	public long getSWSignature();

	/**
	 * @param typeName - name of the type to retrieve
	 * @return type descriptor
	 */
	public Element getType(String typeName);	

	/**
	 * @return set of properties used to originally construct the model
	 */
	public Properties getProperties();

	/**
	 * @return set of names of all types used in the model
	 */
	public Set<String> getTypes();

}

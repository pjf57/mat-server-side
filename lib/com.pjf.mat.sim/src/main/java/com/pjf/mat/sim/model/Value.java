package com.pjf.mat.sim.model;

/**
 * Generic interface for modelling data with validity
 * 
 * @author pjf
 *
 */
public interface Value {
	public int getRawData();
	public boolean isValid();
}

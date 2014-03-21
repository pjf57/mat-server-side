package com.pjf.mat.api;

import java.util.Collection;

import com.pjf.mat.api.comms.Comms;

/**
 * Generic interface to a MAT simulator
 * 
 * @author pjf
 *
 */
public interface MatSimInt extends Comms  {

	/**
	 * instantiate all the simulation elements
	 * 
	 * @param prjElements
	 * @throws Exception
	 */
	public void init(Collection<Element> prjElements) throws Exception;

	/**
	 * Start the simulator
	 */
	public void start();

	/**
	 * Shutdown the simulator
	 */
	public void shutdown();


}
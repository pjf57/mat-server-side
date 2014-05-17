package com.cs.fwk.api;

import java.util.Collection;

import com.cs.fwk.api.comms.MATCommsApi;
import com.cs.fwk.api.logging.MatLogger;
import com.cs.fwk.util.SystemServicesInt;

/**
 * Generic interface to a MAT simulator
 * 
 * @author pjf
 *
 */
public interface MatSimInt extends MATCommsApi  {

	/**
	 * Set arguments if created without
	 * 
	 * @param logger - logger to use
	 * @param services - system services to use
	 * @throws Exception 
	 */
	public void setArgs(MatLogger logger, SystemServicesInt services) throws Exception;

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
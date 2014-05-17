package com.cs.fwk.core.util;

import com.cs.fwk.api.MatSimInt;
import com.cs.fwk.api.logging.MatLogger;
import com.cs.fwk.util.SystemServicesInt;

public class SimLoader {
	/**
	 * Load the simulator by classname
	 * 
	 * @param name - name of simulator class
	 * @param logger - logger to use
	 * @param services - system services to use
	 * @return the simulator
	 * @throws Exception if error loading
	 */
	public static MatSimInt loadMatSim(String name, MatLogger logger, SystemServicesInt services) throws Exception {
		MatSimInt sim = (MatSimInt) MatSimInt.class
			                   .getClassLoader()
			                   .loadClass(name)
			                   .newInstance();
		sim.setArgs(logger,services);
		return sim;
	}

}

package com.cs.fwk.util;

import java.net.SocketException;
import java.net.UnknownHostException;

import com.cs.fwk.api.comms.CxnInt;

/**
 * Interface to provide generalised MAT system services
 * 
 * @author pjf
 *
 */

public interface SystemServicesInt {
	
	/**
	 * get a UDP connection, either a loopback or normal
	 * 
	 * @param ip ("direct" for loopback)
	 * @return cxn
	 * @throws SocketException
	 * @throws UnknownHostException
	 */
	public CxnInt getCxnOrLoopback(String ip) throws SocketException, UnknownHostException;

}

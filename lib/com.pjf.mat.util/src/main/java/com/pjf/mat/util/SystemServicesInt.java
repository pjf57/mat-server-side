package com.pjf.mat.util;

import java.net.SocketException;
import java.net.UnknownHostException;

import com.pjf.mat.util.comms.UDPCxn;

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
	public UDPCxn getCxnOrLoopback(String ip) throws SocketException, UnknownHostException;

}
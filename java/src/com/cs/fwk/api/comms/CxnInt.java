package com.cs.fwk.api.comms;

import java.io.IOException;


public interface CxnInt {

	/**
	 * Set the object that should receive outgoing messages
	 * 
	 * @param cb	callback object
	 */
	public void setLoopbackCallback(LoopbackInt cb);

	/**
	 * Send data to a specific port
	 * 
	 * @param datagram datagram to send (data, destination port)
	 * @throws IOException
	 */
	public void send(CFDatagram datagram) throws IOException;

	/**
	 * Receive one datagram. Abort if cxn is shutdown
	 * Blocks until a datagram is received
	 * 
	 * @return datagram with data and the source port
	 * @throws IOException
	 */
	public CFDatagram rcv() throws IOException;

	/**
	 * Close the connection
	 */
	public void close();

	/**
	 * @return a string that identifies the transport address of the cxn
	 */
	public String getAddress();

	/**
	 * @return max transmission unit size (bytes) - this is the MTU at the cxn data layer
	 */
	public int getMtuSize();

}
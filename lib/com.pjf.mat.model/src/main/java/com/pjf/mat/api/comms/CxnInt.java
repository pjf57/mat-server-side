package com.pjf.mat.api.comms;

import java.io.IOException;


public interface CxnInt {

	/**
	 * Set the object that should receive outgoing messages
	 * 
	 * @param cb	callback object
	 */
	public void setLoopbackCallback(InMsgCallbackInt cb);

	/**
	 * Send data to a specific port
	 * 
	 * @param datagram datagram to send (data, destination port)
	 * @throws IOException
	 */
	public void send(CheetahDatagram datagram) throws IOException;

	/**
	 * Receive one datagram. Abort if cxn is shutdown
	 * Blocks until a datagram is received
	 * 
	 * @return datagram with data and the source port
	 * @throws IOException
	 */
	public CheetahDatagram rcv() throws IOException;

	/**
	 * Close the connection
	 */
	public void close();

	/**
	 * @return a string that identifies the transport address of the cxn
	 */
	public String getAddress();

}
package com.pjf.mat.api.comms;

import java.io.IOException;


public interface CxnInt {

	/**
	 * Set the object that should receive outgoing messages
	 * 
	 * @param cb	callback object
	 */
	public void setLoopbackCallback(InMsgCallbackInt cb);

	public void send(byte[] data, int port) throws IOException;

	/**
	 * Receive one datagram. Abort if cxn is shutdown
	 * 
	 * @return
	 * @throws IOException
	 */
	public RxPkt rcv() throws IOException;

	public void close();

	public String getIp();

}
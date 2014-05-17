package com.cs.fwk.api.comms;

/**
 * Callback for incoming messages
 * 
 */
public interface LoopbackInt {
	
	/**
	 * Callback method to inject an incoming comms message (coming from loopback)
	 * 
	 * @param port	port the msg was received on
	 * @param msg	the msg itself
	 */
	public void injectLoopbackMsg(int port, byte[] msg);

}

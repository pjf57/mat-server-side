package com.pjf.mat.api;

/**
 * Callback for incoming messages
 * 
 */
public interface InMsgCallbackInt {
	
	/**
	 * Callback method to process an incoming comms message
	 * 
	 * @param port	port the msg was received on
	 * @param msg	the msg itself
	 */
	public void processIncomingMsg(int port, byte[] msg);

}

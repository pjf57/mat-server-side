package com.pjf.mat.util.comms;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;



/**
 * Extends base comms by providing a UDP socket with reader
 * 
 * @author pjf
 *
 */
public abstract class UDPSktComms extends BaseComms {
	private final static Logger logger = Logger.getLogger(UDPSktComms.class);
	protected UDPCxn cxn;
	private final Reader reader;
	private final String ip;
	private int rspCnt;
	
	class Reader extends Thread {
		private boolean keepGoing = true;
		
		public Reader() {
			super();
			this.setName("reader");
		}
		
		@Override
		public void run() {
			logger.info("Receiver starting");
			try {
				while (keepGoing) {
					RxPkt pkt = cxn.rcv();
					logger.info("Got pkt");
					if (keepGoing) {
						rspCnt++;
						processIncomingMsg(pkt.getPort(),pkt.getData());
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			logger.info("Receiver stopped.");
		}
		

		public void shutdown() {
			logger.info("Receiver shutting down");
			keepGoing = false;
			cxn.close();
		}
	}
	
	public UDPSktComms(String ip) throws SocketException, UnknownHostException {
		cxn = new UDPCxn(ip);
		this.ip = ip;
		rspCnt = 0;
		this.reader = new Reader();
		reader.start();
	}
	
	
	/**
	 * Shutdown the reader thread
	 */
	public void shutdown() {
		reader.shutdown();
	}
	
	/**
	 * @return ip addr
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @return number of incoming pkts
	 */
	public int getRspCnt() {
		return rspCnt;
	}
	
	@Override
	public UDPCxn getCxn() {
		return cxn;
	}

}

package com.pjf.mat.util.comms;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import com.pjf.mat.api.comms.CxnInt;
import com.pjf.mat.api.comms.CheetahDatagram;



/**
 * Extends base comms by providing a reader
 * 
 * @author pjf
 *
 */
public abstract class ReaderComms extends BaseComms {
	private final static Logger logger = Logger.getLogger(ReaderComms.class);
	protected CxnInt cxn;
	private final Reader reader;
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
					CheetahDatagram pkt = cxn.rcv();
					if (keepGoing) {
						logger.info("Got pkt");
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
	
	public ReaderComms(CxnInt cxn) throws SocketException, UnknownHostException {
		this.cxn = cxn;
		rspCnt = 0;
		this.reader = new Reader();
		reader.start();
	}
	
	public ReaderComms() throws SocketException, UnknownHostException {
		this.cxn = null;
		rspCnt = 0;
		this.reader = new Reader();
	}
	
	public void setCxn(CxnInt cxn) {
		this.cxn = cxn;
		reader.start();
	}

	
	/**
	 * Shutdown the reader thread
	 */
	public void shutdown() {
		reader.shutdown();
	}
	

	/**
	 * @return number of incoming pkts
	 */
	public int getRspCnt() {
		return rspCnt;
	}
	
	@Override
	public CxnInt getCxn() {
		return cxn;
	}

}

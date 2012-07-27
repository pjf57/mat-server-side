package com.pjf.mat.sys;


import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collection;

import org.apache.log4j.Logger;

import com.pjf.mat.api.Cmd;
import com.pjf.mat.api.Comms;
import com.pjf.mat.api.Element;
import com.pjf.mat.api.Status;
import com.pjf.mat.util.comms.BaseComms;
import com.pjf.mat.util.comms.UDPCxn;

public class UDPComms extends BaseComms implements Comms {
	private final static Logger logger = Logger.getLogger(UDPComms.class);
	private UDPCxn cxn;
	private final String ip;
	private int port;
	private final Reader reader;
	
	
	
	class Reader extends Thread {
		private boolean keepGoing = true;
		private int rspCnt;
		
		public Reader() {
			super();
			this.setName("reader");
		}
		
		@Override
		public void run() {
			logger.info("Receiver starting");
			try {
				while (keepGoing) {
					byte[] rep = cxn.rcv();
					if (keepGoing) {
						rspCnt++;
						processIncomingMsg(rep);
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
	
	
	public UDPComms(String ip, int port) throws SocketException, UnknownHostException {
		cxn = new UDPCxn(ip);
		this.ip = ip;
		this.port = port;
		this.mat = null;
		this.reader = new Reader();
		reader.start();
	}

	
	public void shutdown() {
		reader.shutdown();
	}
	
	@Override
	public void sendConfig(Collection<Element> elements) throws Exception {
		logger.info("Preparing to send config to ip " + ip + " port " + port);
		EncodedConfigItemList cfg = new EncodedConfigItemList();
		for (Element el : elements) {
			hwEncodeConfig(el,cfg);
		}
		logger.info("sendConfig(): encoded " + cfg.getItemCount() + " items into " + cfg.getLength() + " bytes");
		cxn.send(cfg.getData(),port);
	}
	
	@Override
	public void sendCmd(Cmd cmd) throws IOException {
		EncodedConfigItemList cfg = new EncodedConfigItemList();
		cfg.put(cmd.getParent().getId(),cmd.getConfigId() | 0x80,0);
		logger.info("sendCmd(" + cmd.getFullName() + "): encoded " + cfg.getLength() + " bytes");
		cxn.send(cfg.getData(),port);
	}


	@Override
	public Status requestStatus() throws Exception {
			byte[] req = new byte[7];
			req[0] = 1;
			req[1] = 0;
			req[2] = (byte) 0x83;
			req[3] = 0;
			req[4] = 0;
			req[5] = 0;
			req[6] = (byte) 0xff;
			cxn.send(req,port);
		return null;
	}

	@Override
	public long getHWSignature() throws Exception{
		// send request
		byte[] req = new byte[7];
		req[0] = 1;
		req[1] = 0;
		req[2] = (byte) 0x84;
		req[3] = 0;
		req[4] = 0;
		req[5] = 0;
		req[6] = 0;
		cxn.send(req,port);
		return 0;
	}

	@Override
	public Status requestStatus(Element element) throws Exception {
		byte[] req = new byte[7];
		req[0] = 1;
		req[1] = 0;
		req[2] = (byte) 0x83;
		req[3] = 0;
		req[4] = 0;
		req[5] = 0;
		req[6] = (byte) element.getId();
		cxn.send(req,port);
		return null;
	}


	/**
	 * @return underlying cxn used to do the comms
	 */
	@Override
	public UDPCxn getCxn() {
		return cxn;
	}




	





}
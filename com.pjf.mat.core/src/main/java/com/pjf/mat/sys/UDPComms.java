package com.pjf.mat.sys;


import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import com.pjf.mat.api.Attribute;
import com.pjf.mat.api.Cmd;
import com.pjf.mat.api.Comms;
import com.pjf.mat.api.Element;
import com.pjf.mat.api.InputPort;
import com.pjf.mat.api.MatApi;
import com.pjf.mat.api.NotificationCallback;
import com.pjf.mat.api.OutputPort;
import com.pjf.mat.api.Status;
import com.pjf.mat.impl.MatInterface;
import com.pjf.mat.impl.element.ElementStatus;
import com.pjf.mat.impl.util.Conversion;
import com.pjf.udp.UDPCxn;

public class UDPComms extends BaseComms implements Comms {
	private final static Logger logger = Logger.getLogger(UDPComms.class);
	private UDPCxn cxn;
	private final String ip;
	private int port;
	private MatApi mat;
	private final Reader reader;
	
	// configuration values
//	private final byte EL_C_RESET = 0;
	private final byte EL_C_SRC_ROUTE = 1;	// xxxx xxxx xxxI xxSS (for source SS on input I)
	private final byte EL_C_CFG_DONE = 2;	// config is done

	// incoming message types
	private final byte ST_TX_STATUS = 1;	// status report
//	private final byte ST_TX_CONFIG = 2;	// config report
	private final byte ST_TX_EVTLOG = 3;	// request to log an event

	// element type IDSs
	private final byte EL_TYP_EMA 			= 0x10;
	private final byte EL_TYP_TG1 			= 0x20;
	private final byte EL_TYP_LOG 			= 0x30;
	private final byte EL_TYP_LOGIC_4IP 	= 0x40;
	private final byte EL_TYP_ARITH_4IP 	= 0x41;
	private final byte EL_TYP_UDP_RAW_MKT 	= 0x50;

	// basis states
	private final byte BS_INIT	= 1;
	private final byte BS_CFG	= 2;
	private final byte BS_RST	= 3;
	private final byte BS_RUN	= 4;


	
	private class EncodedConfigItemList {
		private int itemCount;
		private int upto;
		private byte[] data;
		
		public EncodedConfigItemList() {
			itemCount = 0;
			upto = 1;
			data = new byte[1500];
		}

		public void put(int id, int configId, int value) {
			data[upto++] = (byte) id;
			data[upto++] = (byte) configId;
			data[upto++] = (byte) ((value >> 24) & 0xff);
			data[upto++] = (byte) ((value >> 16) & 0xff);
			data[upto++] = (byte) ((value >> 8) & 0xff);
			data[upto++] = (byte) (value & 0xff);
			itemCount++;
			logger.debug("cfg.put(el=" + id + " cfgId=" + configId + " data=0x" + Integer.toHexString(value) +
					") msg = [" + toHexString(data,upto-6,upto-1) + "]");			
		}
		

		public byte[] getData() {
			data[0] = (byte) itemCount;
			byte[] buf = new byte[upto];
			System.arraycopy(data, 0, buf, 0, upto);
			return buf;
		}
		
		public int getItemCount() {
			return itemCount;
		}
		
		public int getLength() {
			return upto;
		}
	}
	
	class Reader extends Thread {
		private boolean keepGoing = true;
		private int rspCnt;
		
		public Reader() {
			super();
			this.setName("reader");
		}
		
		@Override
		public void run() {
			System.out.println("Receiver starting");
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
			System.out.println("Receiver stopped.");
		}
		

		public void shutdown() {
			System.out.println("Receiver shutting down");
			keepGoing = false;
			cxn.close();
		}
	}
	
	
	public UDPComms(String ip, int port) throws SocketException, UnknownHostException {
		cxn = new UDPCxn(ip);
		this.ip = ip;
		this.port = port;
		this.mat = null;
		notificationSubscribers = new ArrayList<NotificationCallback>();
		this.reader = new Reader();
		reader.start();
	}

	public void setMat(MatInterface mat) {
		this.mat = mat;
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
	 * 		Encodes the configuration for an item into a byte array comprising
	 * 		a number of config items.
	 * 
	 *		each item is 6 bytes:
	 *		------------------------------------------------------------------------------
	 *		| element ID (8 bit) | cmd (1bit) config item (7 bits) | data H..L (32 bits) |
	 *		------------------------------------------------------------------------------
	 * @param cfg 
	 * @throws Exception 
	 */
	private void hwEncodeConfig(Element el, EncodedConfigItemList cfg) throws Exception {	
		// encode attribute values
		for (Attribute attr : el.getAttributes()) {
			cfg.put(el.getId(),attr.getConfigId(),attr.getEncodedData());
		}		
		// encode connections
		for (InputPort ip : el.getInputs()) {
			if (ip.getConnectedSrc() != null) {
				int val = ((ip.getId()-1) << 8) | ip.getConnectedSrc().getParent().getId();
				cfg.put(el.getId(), EL_C_SRC_ROUTE, val);
			}
		}		
		// put config done for this element
		cfg.put(el.getId(), EL_C_CFG_DONE, 0);
	}

	/**
	 * Process a message that has been received from the MAT
	 * 
	 * @param msg
	 */
	private void processIncomingMsg(byte[] msg) {
		byte cmd = msg[0];
		switch (cmd) {
		case ST_TX_STATUS: processStatusMsg(msg);	break;
		case ST_TX_EVTLOG: processEventLogMsg(msg); break;
		default: logger.error("Unkown status message received: [" + Conversion.toHexString(msg)); break;
		}
	}


	/**
	 * Process a status message. May contain one or more status elements
	 * 
	 * @param msg	- message, starting from cmd byte
	 */
	private void processStatusMsg(byte[] msg) {
		byte items = msg[1];
		int upto = 2;
		while (items-- > 0) {
			// process first/next status item
			int id = msg[upto++];
			int type = msg[upto++];
			int basisState = msg[upto++];
			int intState = msg[upto++];
			int evtCount = Conversion.getIntFromBytes(msg,upto,4);
			upto += 4;
			String typeStr = "";
			switch(type) {
			case EL_TYP_TG1			: typeStr = "TG1";							break;
			case EL_TYP_EMA			: typeStr = "EMA";							break;
			case EL_TYP_LOG			: typeStr = "LOGGER";						break;
			case EL_TYP_LOGIC_4IP	: typeStr = "Logic_4IP";					break;
			case EL_TYP_ARITH_4IP	: typeStr = "Arith_4IP";					break;
			case EL_TYP_UDP_RAW_MKT	: typeStr = "UDPrawMKT";					break;
			default					: typeStr = "unknown(" + type + ")";		break;
			}
			String basisStateStr = "";
			switch(basisState) {
			case BS_INIT:	basisStateStr = "INIT";							break;
			case BS_CFG:	basisStateStr = "CFG";							break;
			case BS_RST:	basisStateStr = "RST";							break;
			case BS_RUN:	basisStateStr = "RUN";							break;
			default:		basisStateStr = "unknown(" + basisState + ")";	break;
			}
			processNewStatusUpdate(id,typeStr,basisStateStr,intState,evtCount);
		}		
	}



	private void processNewStatusUpdate(int id, String type,
			String basisState, int intState, int evtCount) {
		
		Element element = mat.getElement(id);
		String srcName = "unknown";
		if (element != null) {			
			srcName = element.getType();
			// Update status in the element
			Status newStatus = new ElementStatus(basisState,intState,evtCount);
			element.setStatus(newStatus);
		}

		logger.info("Status Update: id=" + id + 
				" name=" + srcName + 
				" type=" + type +
				" basis-state=" + basisState + " int-state=" + intState +
				" event-count=" + evtCount);
		for (NotificationCallback subscriber : notificationSubscribers) {
			subscriber.notifyElementStatusUpdate(element);
		}

	}

	private void processEventLogMsg(byte[] msg) {
		byte items = msg[1];
		int upto = 2;
		while (items-- > 0) {
			// process first/next log item
			int src = msg[upto++];
			int instrId = msg[upto++];
			upto+=2;
			OutputPort op = null;
			if (src > 0  &&  mat != null) {
				Element el = mat.getElement(src);
				if (el != null) {
					op = el.getOutputs().get(0);
				}
			}
			int data = Conversion.getIntFromBytes(msg,upto,4);
			upto +=4;
			String value = "undefined";
			if (op != null) {
				value = op.dataToString(data);
			} else {
				float fval = Float.intBitsToFloat(data);
				value = Float.toString(fval);
			}
			Element srcElement = mat.getElement(src);
			String srcName = "unknown";
			if (srcElement != null) {
				srcName = srcElement.getType();
			}
			logger.debug("Event from element=" + src + ":" + srcName + " InstrId=" + instrId + " val=" + value);
			for (NotificationCallback subscriber : notificationSubscribers) {
				subscriber.notifyEventLog(srcElement, instrId, data, value);
			}
		}		
	}

	
	private String toHexString(byte[] data, int start, int end) {
		byte[] buf = new byte[end-start+1];
		System.arraycopy(data, start, buf, 0, end-start+1);
		return Conversion.toHexString(buf);
	}

	/**
	 * @return underlying cxn used to do the comms
	 */
	public UDPCxn getCxn() {
		return cxn;
	}


	





}

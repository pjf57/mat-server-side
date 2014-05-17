package com.cs.fwk.util.comms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.cs.fwk.api.Attribute;
import com.cs.fwk.api.comms.CFDatagram;
import com.cs.fwk.api.comms.MATCommsApi;
import com.cs.fwk.api.comms.CxnInt;
import com.cs.fwk.api.comms.LoopbackInt;
import com.cs.fwk.api.logging.EventLog;
import com.cs.fwk.api.logging.LkuAuditLog;
import com.cs.fwk.api.logging.OrderLog;
import com.cs.fwk.api.logging.RtrAuditLog;
import com.cs.fwk.api.util.ConfigItem;
import com.cs.fwk.api.util.HwStatus;
import com.cs.fwk.api.Element;
import com.cs.fwk.api.InputPort;
import com.cs.fwk.api.LkuResult;
import com.cs.fwk.api.MatApi;
import com.cs.fwk.api.MatElementDefs;
import com.cs.fwk.api.NotificationCallback;
import com.cs.fwk.api.OutputPort;
import com.cs.fwk.api.Status;
import com.cs.fwk.api.Timestamp;
import com.cs.fwk.util.Conversion;
import com.cs.fwk.util.ElementStatus;

public abstract class RComms implements MATCommsApi {
	private final static Logger logger = Logger.getLogger(RComms.class);
	protected MatApi mat;
	protected Collection<NotificationCallback> notificationSubscribers;
	protected Map<Integer,LoopbackInt> inMsgSubscribers;
	protected HwStatus hwStatus;
	protected CxnInt cxn;
	private final Reader reader;
	private int rspCnt;

	// basis states
	protected final byte BS_INIT	= 1;
	protected final byte BS_CFG		= 2;
	protected final byte BS_RST		= 3;
	protected final byte BS_RUN		= 4;

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
					CFDatagram pkt = cxn.rcv();
					if (keepGoing) {
						logger.info("Got pkt");
						rspCnt++;
						injectLoopbackMsg(pkt.getDstPort(),pkt.getData());
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

	/**
	 * Standard constructor - specifiying cxn to FPGA
	 * 
	 * @param cxn
	 */
	public RComms(CxnInt cxn) {
		notificationSubscribers = new ArrayList<NotificationCallback>();
		inMsgSubscribers = new HashMap<Integer,LoopbackInt>();
		hwStatus = new HwStatus();
		this.cxn = cxn;
		rspCnt = 0;
		this.reader = new Reader();
	}

	/**
	 * Alternate constructor - specify cxn later with setCxn()
	 */
	public RComms() {
		notificationSubscribers = new ArrayList<NotificationCallback>();
		inMsgSubscribers = new HashMap<Integer,LoopbackInt>();
		hwStatus = new HwStatus();
		this.cxn = null;
		rspCnt = 0;
		this.reader = new Reader();
	}


	/**
	 * Set connection for the Comms to FPGA
	 * 
	 * @param cxn
	 */
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
	public void setMat(MatApi mat) {
		this.mat = mat;
	}

	public void subscribeIncomingMsgs(int port, LoopbackInt cb) {
		inMsgSubscribers.put(new Integer(port), cb);
		logger.info("subscribeIncomingMsgs() " + cb + " subscribed to port " + port); 
	}

	@Override
	public CxnInt getCxn() {
		return cxn;
	}

	protected class EncodedConfigItemList {
		private int itemCount;
		private int upto;
		private byte[] data;
		
		public EncodedConfigItemList() {
			itemCount = 0;
			upto = 1;
			data = new byte[1500];
		}

		private void putRaw(int id, int configId, int arg, int value) {
			data[upto++] = (byte) id;
			data[upto++] = (byte) configId;
			data[upto++] = (byte) arg;
			data[upto++] = (byte) ((value >> 24) & 0xff);
			data[upto++] = (byte) ((value >> 16) & 0xff);
			data[upto++] = (byte) ((value >> 8) & 0xff);
			data[upto++] = (byte) (value & 0xff);
			itemCount++;
			logger.debug("cfg.put(el=" + id + " cfgId=" + configId + " data=0x" + Integer.toHexString(value) +
					") msg = [" + toHexString(data,upto-6,upto-1) + "]");			
		}
		
		public void putConfigItem(int id, int configId, int arg, int value) {
			putRaw(id,configId | 0x80,arg,value);
		}
		
		public void putSystemItem(int id, int configId, int arg, int value) {
			putRaw(id,configId | 0x40,arg,value);
		}

		public void putCmdItem(int id, int configId, int arg, int value) {
			putRaw(id,configId | 0xc0,arg,value);
		}

		public void putConfigList(List<ConfigItem> configs) {
			for (ConfigItem cfg : configs) {
				int mask;
				switch (cfg.getSysType()) {
				case NORMAL :	mask = 0x80;	break;
				case SYSTEM :	mask = 0x40;	break;
				default 	:	mask = 0x00;	break;
				}
				putRaw(cfg.getElementId(),cfg.getItemId() | mask,cfg.getArg(),cfg.getRawData());
			}
			
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
		
		@Override
		public String toString() {
			StringBuffer buf = new StringBuffer();
			buf.append(itemCount); buf.append("/"); buf.append(upto);
			buf.append("[");
			buf.append(Conversion.toHexString(getData()));
			buf.append("]");
			return buf.toString();
		}

	}


	@Override
	public void addNotificationSubscriber(NotificationCallback subscriber) {
		notificationSubscribers.add(subscriber);		
	}

	/**
	 * Process a message that has been received from the MAT
	 * @param port - UDP port on which data was received
	 * @param msg - the raw message
	 */
	public void injectLoopbackMsg(int port, byte[] msg) {
		logger.debug("--> RX MSG (port=" + port + ") " + toHexString(msg,0,msg.length-1));
		LoopbackInt cb = inMsgSubscribers.get(new Integer(port));
		if (cb != null) {
			// send the message to the subscriber
			cb.injectLoopbackMsg(port,msg);
		} else {
			// process the message here
			if (port == MatElementDefs.CS_RMO_ORDER_PORT) {
				processIncomingOrder(msg);
			} else if (port == MatElementDefs.CS_PORT_LOGGER) {
				byte cmd = msg[0];
				switch (cmd) {
				case MatElementDefs.ST_TX_EVTLOG: processEventLogMsg(msg); break;
				default: logger.error("Unkown log message received on logger port: [" + Conversion.toHexString(msg)); break;
				}
			} else if (port == MatElementDefs.CS_PORT_STATUS) {
				byte cmd = msg[0];
				switch (cmd) {
				case MatElementDefs.ST_TX_HWSIG: processHWSigMsg(msg); break;
				case MatElementDefs.ST_TX_STATUS: processStatusMsg(msg);	break;
				case MatElementDefs.ST_TX_LKUAUDIT: processLkuAuditLogMsg(msg); break;
				case MatElementDefs.ST_TX_RTRAUDIT: processRtrAuditLogMsg(msg); break;
				default: logger.error("Unkown status message received on status port: [" + Conversion.toHexString(msg)); break;
				}
			} else {
				logger.error("Unkown message received on port " + port +
						" [" + Conversion.toHexString(msg));
			}
		}
	}


	/**
	 * Process HW signature received - store it and release sem
	 * @param msg
	 */
	private void processHWSigMsg(byte[] msg) {
		long hwSig = Conversion.getLongFromBytes(msg,1,8);
		int microtickPeriod = 0;
		if (msg.length >= 11) {
			microtickPeriod = Conversion.getIntFromBytes(msg,9,2);
		}
		String cf_version = "unknown";
		if (msg.length >= 13) {
			byte maj = msg[11];
			byte min = msg[12];
			cf_version = "" + Conversion.toHexByteString(maj) + "." + Conversion.toHexByteString(min);
		}
		HwStatus st = new HwStatus(hwSig,microtickPeriod,cf_version);
		setHwStatus(st);
		processRxHwSig(st);
	}

	@Override
	public void setHwStatus(HwStatus st) {
		logger.info("setHwStatus() - new HW Status is: " + st);
		this.hwStatus = st;
	}

	/**
	 * Template method for handling receipt of HW signature
	 * 
	 * @param sig
	 */
	protected void processRxHwSig(HwStatus hws) {
		logger.warn("processRxHwSig() - signature received - default action is do nothing");
	}

	/**
	 * Process a status message. May contain one or more status elements
	 * 
	 * @param msg	- message, starting from cmd byte
	 */
	protected void processStatusMsg(byte[] msg) {
		byte items = msg[1];
		int upto = 2;
		int nCBs = 0;
		Set<Element> cbsUpdated = new HashSet<Element>();
		while (items-- > 0) {
			// process first/next status item
			int id = msg[upto++];
			int type = msg[upto++];
			int basisState = msg[upto++];
			int intState = msg[upto++];
			int evtCount = Conversion.getIntFromBytes(msg,upto,4);
			upto += 4;
			String typeStr = MatElementDefs.ElementTypeToString(type);
			String basisStateStr = "";
			switch(basisState) {
			case BS_INIT:	basisStateStr = Status.INIT;								break;
			case BS_CFG:	basisStateStr = Status.CFG;									break;
			case BS_RST:	basisStateStr = Status.RST;									break;
			case BS_RUN:	basisStateStr = Status.RUN;									break;
			default:		basisStateStr = Status.UNKNOWN + "(" + basisState + ")";	break;
			}
			nCBs++;
			Element cb = processNewStatusUpdate(id,typeStr,basisStateStr,intState,evtCount);
			if (cb != null) {
				if (cb.hasStatusChanged(true)) {
					cbsUpdated.add(cb);
				}
			}
		}
		logger.info("processStatusMsg(): received status for " + nCBs + " CBs, with " +
					cbsUpdated.size() + " changed.");
		if (cbsUpdated.size() > 0) {
			for (NotificationCallback subscriber : notificationSubscribers) {
				subscriber.notifyElementStatusUpdate(cbsUpdated);
			}
		}		
	}



	/**
	 * Handle status update for a CB
	 * 
	 * NOTE: this does not send any notifications
	 * 
	 * @param id - id of the CB (used to select from model)
	 * @param type - type of the CB - to update
	 * @param basisState - basis state to update
	 * @param intState - internal state to update
	 * @param evtCount - event count
	 * @return the updated CB (or null if not found)
	 */
	protected Element processNewStatusUpdate(int id, String type,
			String basisState, int intState, int evtCount) {		
		Element element = mat.getModel().getElement(id);
		String srcName = "unknown";
		if (element != null) {			
			srcName = element.getType();
			// Update status in the element
			Status newStatus = new ElementStatus(basisState,intState,evtCount);
			element.setStatus(newStatus);
			logger.debug("processNewStatusUpdate(): id=" + id + 
					" name=" + srcName + 
					" type=" + type +
					" basis-state=" + basisState + " int-state=" + intState +
					" event-count=" + evtCount);
		} else {
			logger.error("processNewStatusUpdate(): Error getting element, id=" + id + 
					" type=" + type +
					" basis-state=" + basisState + " int-state=" + intState +
					" event-count=" + evtCount);
		}
		return element;
	}

	protected void processEventLogMsg(byte[] msg) {
		byte items = msg[1];
		int upto = 2;
		int mtp = hwStatus.getMicrotickPeriod();
		while (items-- > 0) {
			// process first/next log item
			int src = msg[upto++];
			int port = msg[upto++];
			int instrId = msg[upto++];
			int tickref = msg[upto++] & 0xFF;
			long timestamp = Conversion.getLongFromBytes(msg,upto,6);
			upto+=6;
			int data = Conversion.getIntFromBytes(msg,upto,4);
			upto +=4;
			notifyEvent(new Timestamp(timestamp,mtp),src, port, instrId, tickref, data);
		}		
	}

	/**
	 * @param msg
	 * |#items|ts(48bit)|RQ_EL_ID|instr|lku_op|RSP_EL_ID|rsp time|rslt|data(32bit)| .. next ..|
	 */
	private void processLkuAuditLogMsg(byte[] msg) {
		byte items = msg[1];
		int upto = 2;
		int mtp = hwStatus.getMicrotickPeriod();
		List<LkuAuditLog> logs = new ArrayList<LkuAuditLog>();
		while (items-- > 0) {
			// process first/next log item
			long timestamp = Conversion.getLongFromBytes(msg,upto,6);
			upto+=6;
			int requesterId = msg[upto++];
			int instrId = msg[upto++];
			int tickref = msg[upto++] & 0xFF;
			int op = msg[upto++];
			int responderId = msg[upto++];
			int rspTime = msg[upto++];
			int resultCode = msg[upto++];
			int data = Conversion.getIntFromBytes(msg,upto,4);
			float fdata = Float.intBitsToFloat(data);
			upto +=4;
			// resolve format of incoming binary data
			Element requester = mat.getModel().getElement(requesterId);
			Element responder = mat.getModel().getElement(responderId);
			LkuResult rslt = LkuResult.UNKNOWN;
			switch(resultCode) {
			case 0:	rslt = LkuResult.NO_DATA;	break;
			case 1:	rslt = LkuResult.OK;		break;
			case 2:	rslt = LkuResult.ERROR;		break;
			case 3:	rslt = LkuResult.TIMEOUT;	break;
			}
			LkuAuditLog log = new LkuAuditLog(new Timestamp(timestamp,mtp),requester,instrId,
					tickref,op,responder,rspTime,rslt,fdata);
			logs.add(log);
		}
		notifyLkuAuditLogsReceipt(logs);
	}
	
	/**
	 * @param msg
	 * |#items|ts(48bit)|src|takers(32 bit map)|instr|data(32bit)|qtime|delivery time| .. next ..|
	 */
	private void processRtrAuditLogMsg(byte[] msg) {
		logger.debug("Received RTR audit message with " + msg.length + " bytes.");
		byte items = msg[1];
		int upto = 2;
		int mtp = hwStatus.getMicrotickPeriod();
		List<RtrAuditLog> logs = new ArrayList<RtrAuditLog>();
		while (items-- > 0) {
			// process first/next log item
			long timestamp = Conversion.getLongFromBytes(msg,upto,6);
			upto+=6;
			int sourceId = msg[upto++];
			int sourcePort = msg[upto++];
			int takerBitmap = Conversion.getIntFromBytes(msg,upto,4);
			upto += 4;
			int instrId = msg[upto++];
			int tickref = msg[upto++] & 0xFF;
			int data = Conversion.getIntFromBytes(msg,upto,4);
			float fdata = Float.intBitsToFloat(data);
			upto +=4;
			int qTime = msg[upto++];
			int delTime = msg[upto++];
			// resolve format of incoming binary data
			Element source = mat.getModel().getElement(sourceId);
			Set<Element> takers = ConvertBitmapToElementSet(takerBitmap);
			OutputPort op = null;
			if (source != null) {
				op = source.getOutputs().get(sourcePort);
			}
			RtrAuditLog log = new RtrAuditLog(new Timestamp(timestamp,mtp),source,op,takers,
					instrId,tickref,qTime,delTime,fdata);
			if (source == null) {
				logger.error("processRtrAuditLogMsg() - source element is null [" + log + "], sourceId=" + sourceId);
			}
			logs.add(log);
		}
		notifyRtrAuditLogsReceipt(logs);
	}
	

	/**
	 * Convert a bitmap of elements into a set of elements
	 * 
	 * @param bitmap - bitmap of elements, bit 0=ID0, bit 31=ID31
	 * @return set of elements or empty set if bitmap==0
	 */
	private Set<Element> ConvertBitmapToElementSet(int takers) {
		int t = takers;
		Set<Element> set = new HashSet<Element>();
		int id = 0;
		while (id < 32) {
			if ( (t & 1) != 0) {
				Element el = mat.getModel().getElement(id);
				if (el == null) {
					logger.error("ConvertBitmapToElementSet has unknown element id=" + id);
				} else {
					set.add(el);
				}
			}
			id++;
			t = t >>> 1;
		}
		return set;
	}

	/**
	 * Notify subscribers of a collection of LKU audit logs
	 * @param logs
	 */
	protected void notifyLkuAuditLogsReceipt(Collection<LkuAuditLog> logs) {
		for (NotificationCallback subscriber : notificationSubscribers) {
			subscriber.notifyLkuAuditLogReceipt(logs);
		}
	}

	/**
	 * Notify subscribers of a collection of LKU audit logs
	 * @param logs
	 */
	protected void notifyRtrAuditLogsReceipt(Collection<RtrAuditLog> logs) {
		for (NotificationCallback subscriber : notificationSubscribers) {
			subscriber.notifyRtrAuditLogReceipt(logs);
		}
	}

	/**
	 * Notify subscribers of new event
	 * 
	 * @param ts 		- timestamp of the event
	 * @param src		- id of element that generated the event
	 * @param port		- id of the port on the element that generated the event
	 * @param instrId	- instrument id
	 * @param tickref	- tick reference
	 * @param data		- data of the event 
	 */
	protected void notifyEvent(Timestamp ts, int src, int port, int instrId, int tickref, int data) {
		OutputPort op = null;
		if (src > 0  &&  mat != null) {
			Element el = mat.getModel().getElement(src);
			if (el != null) {
				if (el.getOutputs().size() > 0) {
					op = el.getOutputs().get(port);
				}
			}
		}
		String value = "undefined";
		if (op != null) {
			value = op.dataToString(data);
		} else {
			float fval = Float.intBitsToFloat(data);
			value = Float.toString(fval);
		}
		Element srcElement = mat.getModel().getElement(src);
		EventLog evt = new EventLog(ts,srcElement,op,instrId, tickref, data, value);
		logger.debug("Event from element=" + evt);
		for (NotificationCallback subscriber : notificationSubscribers) {
			subscriber.notifyEventLog(evt);
		}
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
	protected void hwEncodeConfig(Element el, EncodedConfigItemList cfg) throws Exception {	
		// encode attribute values
		for (Attribute attr : el.getAttributes()) {
			if (attr.getConfigId() >= 0) {
				// Only configure attrs that are not pseudo attrs
				switch (attr.getSysType()) {
				
				case NORMAL:
					List<ConfigItem> configs = attr.getConfigList();
					cfg.putConfigList(configs);
					break;
					
				case SYSTEM:
					cfg.putSystemItem(el.getId(),attr.getConfigId(),0,attr.getEncodedData());
					break;
					
				case LKU_TARGET:
					cfg.putSystemItem(el.getId(),MatElementDefs.EL_C_CFG_LKU_TRG,0,
							(attr.getConfigId() << 8) | (attr.getEncodedData() & 0xff));
					break;
					
				default:
					throw new Exception("Dont know how to encode: " + attr);
				}
			}
		}		
		// encode connections
		for (InputPort ip : el.getInputs()) {
			OutputPort src = ip.getConnectedSrc();
			if (src != null) {
				int val = ((ip.getId()-1) << 16) | (src.getId()-1) << 8 | src.getParent().getId();
				cfg.putSystemItem(el.getId(), MatElementDefs.EL_C_SRC_ROUTE, 0, val);
			}
		}		
		// put config done for this element
		cfg.putSystemItem(el.getId(), MatElementDefs.EL_C_CFG_DONE, 0, 0);
	}

	/**
	 * Decode incoming order
	 * 
	 * 	|Size|B/S|symbol(8)|price fp(4)|volume uint32|
	 *
	 * @param msg
	 */
	private void processIncomingOrder(byte[] msg) {
		int upto= 0;
		StringBuffer symbolBuf = new StringBuffer();
		String symbol;
		int len = msg[upto++];
		if (len == 17) {
			char side = (char) msg[upto++];
			for (int i=0; i<8; i++) {
				symbolBuf.append(cvtChar(msg[upto++]));
			}
			symbol = symbolBuf.toString();
			int priceData = Conversion.getIntFromBytes(msg,upto,4);
			upto += 4;
			float price = Float.intBitsToFloat(priceData);
			int volume = Conversion.getIntFromBytes(msg,upto,4);	
			logger.info("---- Order: side=" + side + ", symbol=[" + symbol + 
					"], price=" + price + ", vol=" + volume);
			// create Order log
			OrderLog order = new OrderLog(symbol,side,price,volume);
			for (NotificationCallback subscriber : notificationSubscribers) {
				subscriber.notifyOrderReceipt(order);
			}

		} else {
			logger.error("Order with incorrect len received. Len=" + len);
		}
	}

	@Override
	public HwStatus getHWStatus() {
		return hwStatus;
	}
		
	/**
	 * Convert byte to char, substituting _ for unprintables
	 * @param b - byte in
	 * @return converted char
	 */
	private char cvtChar(byte b) {
		char c = (char) b;
		if (b < 0x20) {
			c = '_';
		};
		return c;
	}

	protected String toHexString(byte[] data, int start, int end) {
		byte[] buf = new byte[end-start+1];
		System.arraycopy(data, start, buf, 0, end-start+1);
		return Conversion.toHexString(buf);
	}

}

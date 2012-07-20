package com.pjf.mat.util.comms;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import com.pjf.mat.api.Attribute;
import com.pjf.mat.api.Comms;
import com.pjf.mat.api.Element;
import com.pjf.mat.api.InputPort;
import com.pjf.mat.api.MatApi;
import com.pjf.mat.api.MatElementDefs;
import com.pjf.mat.api.NotificationCallback;
import com.pjf.mat.api.OutputPort;
import com.pjf.mat.api.Status;
import com.pjf.mat.util.Conversion;
import com.pjf.mat.util.ElementStatus;

public abstract class BaseComms implements Comms {
	private final static Logger logger = Logger.getLogger(BaseComms.class);
	
	protected MatApi mat;
	protected Collection<NotificationCallback> notificationSubscribers;

	// basis states
	protected final byte BS_INIT	= 1;
	protected final byte BS_CFG		= 2;
	protected final byte BS_RST		= 3;
	protected final byte BS_RUN		= 4;

	public BaseComms() {
		notificationSubscribers = new ArrayList<NotificationCallback>();
	}
	
	public void setMat(MatApi mat) {
		this.mat = mat;
	}
	
	public UDPCxn getCxn() {
		return null;
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


	@Override
	public void addNotificationSubscriber(NotificationCallback subscriber) {
		notificationSubscribers.add(subscriber);		
	}

	/**
	 * Process a message that has been received from the MAT
	 * 
	 * @param msg
	 */
	protected void processIncomingMsg(byte[] msg) {
		byte cmd = msg[0];
		switch (cmd) {
		case MatElementDefs.ST_TX_STATUS: processStatusMsg(msg);	break;
		case MatElementDefs.ST_TX_EVTLOG: processEventLogMsg(msg); break;
		default: logger.error("Unkown status message received: [" + Conversion.toHexString(msg)); break;
		}
	}


	/**
	 * Process a status message. May contain one or more status elements
	 * 
	 * @param msg	- message, starting from cmd byte
	 */
	protected void processStatusMsg(byte[] msg) {
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
			case MatElementDefs.EL_TYP_TG1			: typeStr = "TG1";			break;
			case MatElementDefs.EL_TYP_EMA			: typeStr = "EMA";			break;
			case MatElementDefs.EL_TYP_LOG			: typeStr = "LOGGER";		break;
			case MatElementDefs.EL_TYP_LOGIC_4IP	: typeStr = "Logic_4IP";	break;
			case MatElementDefs.EL_TYP_ARITH_4IP	: typeStr = "Arith_4IP";	break;
			case MatElementDefs.EL_TYP_UDP_RAW_MKT	: typeStr = "UDPrawMKT";	break;
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



	protected void processNewStatusUpdate(int id, String type,
			String basisState, int intState, int evtCount) {
		
		Element element = mat.getModel().getElement(id);
		String srcName = "unknown";
		if (element != null) {			
			srcName = element.getType();
			// Update status in the element
			Status newStatus = new ElementStatus(basisState,intState,evtCount);
			element.setStatus(newStatus);
		}

		logger.debug("processNewStatusUpdate(): id=" + id + 
				" name=" + srcName + 
				" type=" + type +
				" basis-state=" + basisState + " int-state=" + intState +
				" event-count=" + evtCount);
		for (NotificationCallback subscriber : notificationSubscribers) {
			subscriber.notifyElementStatusUpdate(element);
		}

	}

	protected void processEventLogMsg(byte[] msg) {
		byte items = msg[1];
		int upto = 2;
		while (items-- > 0) {
			// process first/next log item
			int src = msg[upto++];
			int instrId = msg[upto++];
			upto+=2;
			int data = Conversion.getIntFromBytes(msg,upto,4);
			upto +=4;
			notifyEvent(src, instrId, data);
		}		
	}

	/**
	 * Notify subscribers of new event
	 * 
	 * @param src
	 * @param instrId
	 * @param data
	 */
	protected void notifyEvent(int src, int instrId, int data) {
		OutputPort op = null;
		if (src > 0  &&  mat != null) {
			Element el = mat.getModel().getElement(src);
			if (el != null) {
				if (el.getOutputs().size() > 0) {
					op = el.getOutputs().get(0);
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
		String srcName = "unknown";
		if (srcElement != null) {
			srcName = srcElement.getType();
		}
		logger.debug("Event from element=" + src + ":" + srcName + " InstrId=" + instrId + " val=" + value);
		for (NotificationCallback subscriber : notificationSubscribers) {
			subscriber.notifyEventLog(srcElement, instrId, data, value);
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
			cfg.put(el.getId(),attr.getConfigId(),attr.getEncodedData());
		}		
		// encode connections
		for (InputPort ip : el.getInputs()) {
			if (ip.getConnectedSrc() != null) {
				int val = ((ip.getId()-1) << 8) | ip.getConnectedSrc().getParent().getId();
				cfg.put(el.getId(), MatElementDefs.EL_C_SRC_ROUTE, val);
			}
		}		
		// put config done for this element
		cfg.put(el.getId(), MatElementDefs.EL_C_CFG_DONE, 0);
	}


	
	protected String toHexString(byte[] data, int start, int end) {
		byte[] buf = new byte[end-start+1];
		System.arraycopy(data, start, buf, 0, end-start+1);
		return Conversion.toHexString(buf);
	}

}

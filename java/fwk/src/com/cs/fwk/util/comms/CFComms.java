package com.cs.fwk.util.comms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;

import com.cs.fwk.api.ErrorState;
import com.cs.fwk.api.LkuResult;
import com.cs.fwk.api.MatElementDefs;
import com.cs.fwk.api.Status;
import com.cs.fwk.api.Timestamp;
import com.cs.fwk.api.comms.CBRawStatus;
import com.cs.fwk.api.comms.CFCallback;
import com.cs.fwk.api.comms.CFDatagram;
import com.cs.fwk.api.comms.CFCommsInt;
import com.cs.fwk.api.comms.CxnInt;
import com.cs.fwk.api.comms.EvtLogRaw;
import com.cs.fwk.api.comms.LoopbackInt;
import com.cs.fwk.api.comms.LkuAuditRawLog;
import com.cs.fwk.api.comms.RtrAuditRawLog;
import com.cs.fwk.api.util.HwStatus;
import com.cs.fwk.util.Conversion;

public class CFComms implements CFCommsInt, LoopbackInt {
	private final static Logger logger = Logger.getLogger(CFComms.class);
	private int CFPort;				// comms port of the Cheetah Framework in HW
	private CFCallback callback;
	private HwStatus hwStatus;
	private CxnInt cxn;
	private final Reader reader;
	private EncodedConfigItemList configBuf;
	private Semaphore rxSem;

	// basis states
	private final byte BS_INIT	= 1;
	private final byte BS_CFG	= 2;
	private final byte BS_RST	= 3;
	private final byte BS_RUN	= 4;

	class Reader extends Thread {
		private boolean keepGoing = true;
		
		public Reader() {
			super();
			this.setName("reader");
		}
		
		@Override
		public void run() {
			logger.debug("Receiver waiting to start");
			try {
				rxSem.acquire();
			} catch (InterruptedException e1) {
				logger.error("run() - exception during sem acquire: " + e1);
				e1.printStackTrace();
			}
			logger.debug("Receiver starting");
			try {
				while (keepGoing) {
					CFDatagram pkt = cxn.rcv();
					if (keepGoing) {
						handleIncomingMsg(pkt.getDstPort(),pkt.getData());
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			logger.debug("Receiver stopped.");
		}
		

		public void shutdown() {
			logger.debug("Receiver shutting down");
			keepGoing = false;
			cxn.close();
		}
	}

	/**
	 * Standard constructor - specifiying cxn to FPGA
	 * 
	 * @param cxn
	 * @param CFPort - comms port of Cheetah Framework
	 */
	public CFComms(CxnInt cxn, int CFPort) {
		callback = null;
		hwStatus = new HwStatus();
		this.cxn = cxn;
		this.CFPort = CFPort;
		rxSem = new Semaphore(0);
		this.reader = new Reader();
		this.reader.start();
	}

	@Override
	public void shutdown() {
		reader.shutdown();
	}
	

	@Override
	public void setCallback(CFCallback cb) {
		this.callback = cb;
		logger.debug("setCallback(): " + cb);
	}

	@Override
	public CxnInt getCxn() {
		return cxn;
	}
	
	@Override
	public void resetConfigBuffer() {
		configBuf = new EncodedConfigItemList();
	}

	@Override
	public void addConfigItem(int cbId, int configId, int arg, int val) {
		configBuf.putConfigItem(cbId, configId, arg, val);
	}

	@Override
	public void addSysConfigItem(int cbId, int configId, int arg, int val) {
		configBuf.putSystemItem(cbId, configId, arg, val);
	}

	@Override
	public void addCmdItem(int cbId, int cmdId, int arg, int val) {
		configBuf.putCmdItem(cbId, cmdId, arg, val);
	}

	@Override
	public void addCxnItem(int srcCBId, int srcOpPort, int dstCBId, int dstIpPort) {
		int val = (dstIpPort << 16) | srcOpPort << 8 | srcCBId;
		addSysConfigItem(dstCBId, MatElementDefs.EL_C_SRC_ROUTE, 0, val);
	}

	@Override
	public int getConfigBufferSpace() {
		int bytes = configBuf.getLength();
		int bytesLeft = cxn.getMtuSize() - bytes;
		int itemsLeft = bytesLeft / configBuf.getItemSize();
		return itemsLeft;
	}

	@Override
	public int getConfigBufferItemCount() {
		return configBuf.getItemCount();
	}

	@Override
	public int getConfigBufferLength() {
		return configBuf.getLength();
	}

	@Override
	public void sendConfig() throws Exception {
		cxn.send(new CFDatagram(CFPort,configBuf.getData()));
		releaseRx();
	}


	@Override
	public void sendSingleCmd(int cbId, int cmdId, int arg, int val) throws Exception {
		EncodedConfigItemList buf = new EncodedConfigItemList();
		buf.putCmdItem(cbId, cmdId, arg, val);
		releaseRx();
		cxn.send(new CFDatagram(CFPort,buf.getData()));
	}

	@Override
	public void requestStatus() throws Exception {
		sendSingleCmd(MatElementDefs.EL_ID_SYSTEM_CONTROL, MatElementDefs.EL_C_STATUS_REQ, 0, 0xff);
	}

	@Override
	public void requestLkuAuditLogs() throws Exception {
		sendSingleCmd(MatElementDefs.EL_ID_SYSTEM_CONTROL, MatElementDefs.EL_C_LKU_AUDIT_REQ, 0, 0);
	}

	@Override
	public void requestRtrAuditLogs() throws Exception {
		sendSingleCmd(MatElementDefs.EL_ID_SYSTEM_CONTROL, MatElementDefs.EL_C_RTR_AUDIT_REQ, 0, 0);
	}

	@Override
	public void requestCFStatus() throws Exception {
		sendSingleCmd(MatElementDefs.EL_ID_SYSTEM_CONTROL, MatElementDefs.EL_C_HWSIG_REQ, 0, 0);
	}

	@Override
	public void resetCounters(int cbId) throws Exception {
		EncodedConfigItemList buf = new EncodedConfigItemList();
		buf.putSystemItem(cbId, MatElementDefs.EL_C_RESET_CNTRS, 0, 0);
		releaseRx();
		cxn.send(new CFDatagram(CFPort,buf.getData()));		
	}
	
	@Override
	public void resetErrorState(int cbId) throws IOException {
		EncodedConfigItemList buf = new EncodedConfigItemList();
		buf.putCmdItem(cbId, MatElementDefs.EL_C_RESET_ERR, 0, 0);
		releaseRx();
		cxn.send(new CFDatagram(CFPort,buf.getData()));		
	}	

	@Override
	public void resetCBConfig(int cbId) throws Exception {
		EncodedConfigItemList buf = new EncodedConfigItemList();
		buf.putSystemItem(cbId, MatElementDefs.EL_C_RESET_CONFIG, 0, 0);
		releaseRx();
		cxn.send(new CFDatagram(CFPort,buf.getData()));		
	}

	@Override
	public void synchroniseClock(long serverClockMs) throws Exception {
		sendSingleCmd(MatElementDefs.EL_ID_SYSTEM_CONTROL, MatElementDefs.EL_C_CLKSYNC_REQ, 0, (int)serverClockMs);		
	}
	

	private class EncodedConfigItemList {
		private int itemCount;
		private int upto;
		private byte[] data;
		
		public EncodedConfigItemList() {
			itemCount = 0;
			upto = 1;
			data = new byte[cxn.getMtuSize()];
		}

		public int getItemCount() {
			return itemCount;
		}

		public int getItemSize() {
			return 7;	// size of an item
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
			logger.debug("cfg.put(el=" + id + " cfgId=" + configId + " arg=" + arg +
					" data=0x" + Integer.toHexString(value) +
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

		public byte[] getData() {
			data[0] = (byte) itemCount;
			byte[] buf = new byte[upto];
			System.arraycopy(data, 0, buf, 0, upto);
			return buf;
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
	public void injectLoopbackMsg(int destPort, byte[] msg) {
		handleIncomingMsg(destPort,msg);
	}
		
		
		
	/**
	 * Process a message that has been received from the MAT
	 * @param destPort - UDP port on which data was received
	 * @param msg - the raw message
	 */
	@Override
	public void handleIncomingMsg(int destPort, byte[] msg) {
		logger.debug("--> RX MSG (destPort=" + destPort + ") " + toHexString(msg,0,msg.length-1));
		byte cmd = msg[0];
		boolean isVersioned = (cmd & 0x80) != 0;
		cmd = (byte) (cmd & 0x7f);
		try {
			if (destPort == MatElementDefs.CS_PORT_LOGGER) {
				switch (cmd) {
				case MatElementDefs.ST_TX_EVTLOG: processEventLogMsg(msg,isVersioned); break;
				default: logger.error("Unkown log message received on logger port: [" + Conversion.toHexString(msg)); break;
				}
			} else if (destPort == MatElementDefs.CS_PORT_STATUS) {
				switch (cmd) {
				case MatElementDefs.ST_TX_HWSIG: processCFStatusMsg(msg,isVersioned); break;
				case MatElementDefs.ST_TX_STATUS: processStatusMsg(msg,isVersioned);	break;
				case MatElementDefs.ST_TX_LKUAUDIT: processLkuAuditLogMsg(msg,isVersioned); break;
				case MatElementDefs.ST_TX_RTRAUDIT: processRtrAuditLogMsg(msg,isVersioned); break;
				default: logger.error("Unkown status message received on status port: [" + Conversion.toHexString(msg)); break;
				}
			} else {
				logger.debug("Unkown message received on port " + destPort +	" [" + Conversion.toHexString(msg) + "]");
				callback.processUnknownMsg(destPort,msg);
			}
		} catch (Exception e) {
			logger.error("handleIncomingMsg(): " + e.getMessage() +	" [" + Conversion.toHexString(msg) + "]");
		}
	}


	/**
	 * Process CF Status received
	 * @param msg
	 * @param isVersioned - indicates that the msg is versioned
	 * @throws Exception 
	 */
	private void processCFStatusMsg(byte[] msg, boolean isVersioned) throws Exception {
		if (isVersioned) {
			throw new Exception("processCFStatusMsg() - versioning not supported");
		}
		long hwSig = Conversion.getLongFromBytes(msg,1,8);
		int microtickPeriod = 0;
		byte flags = 0;
		int cfgEvents = 0;
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
		if (msg.length >= 14) {
			flags = msg[13];
		}
		if (msg.length >= 16) {
			cfgEvents = Conversion.getIntFromBytes(msg,14,4);
			st.setV2parameters(flags, cfgEvents);
		}
		
		logger.debug("processHWSigMsg() - CF Status received:" + st);
		if (callback == null) {
			logger.warn("processCFStatusMsg(): no callback to deliver to");
		} else {
			callback.processCFStatus(st);
		}
	}


	/**
	 * Process a CB status message. May contain one or more status elements
	 * 
	 * Handles unversioned and v2 formats
	 * 
	 * @param msg	- message, starting from cmd byte
	 * @param isVersioned - indicates that the msg is versioned
	 * @throws Exception
	 */
	private void processStatusMsg(byte[] msg, boolean isVersioned) throws Exception {
		byte ver = 1;
		byte size = 8;
		int upto = 1;
		if (isVersioned) {
			ver = msg[1];
			size = msg[2];
			upto = 3;
		}
		if (ver > 2) {
			throw new Exception("processStatusMsg() - version " + ver + " not supported. Size= " + size);
		}
		byte items = msg[upto++];
		List<CBRawStatus> statusList = new ArrayList<CBRawStatus>();
		while (items-- > 0) {
			// process first/next status item
			int id = msg[upto++];
			int type = msg[upto++];
			int basisState = msg[upto++];
			int intState = msg[upto++];
			int evtCount = Conversion.getIntFromBytes(msg,upto,4);
			upto += 4;
			int numErrs = 0;
			int lastErrCode = 0;
			if (ver >= 2) {
				numErrs = msg[upto++] & 0xff;
				lastErrCode = msg[upto++];
			}
			ErrorState es = new ErrorState(numErrs,lastErrCode);
			String typeStr = MatElementDefs.ElementTypeToString(type);
			String basisStateStr = "";
			switch(basisState) {
			case BS_INIT:	basisStateStr = Status.INIT;								break;
			case BS_CFG:	basisStateStr = Status.CFG;									break;
			case BS_RST:	basisStateStr = Status.RST;									break;
			case BS_RUN:	basisStateStr = Status.RUN;									break;
			default:		basisStateStr = Status.UNKNOWN + "(" + basisState + ")";	break;
			}
			CBRawStatus rst = new CBRawStatus(id,typeStr,basisStateStr,intState,evtCount,es);
			statusList.add(rst);
		}
		logger.debug("processStatusMsg(): received status for " + statusList.size() + " CBs");
		if (callback == null) {
			logger.warn("processCFStatusMsg(): no callback to deliver to");
		} else {
			callback.processCBStatus(statusList);
		}
	}

	/**
	 * Process a CB evtlog message. May contain one or more logs
	 * 
	 * @param msg	- message, starting from cmd byte
	 * @param isVersioned - indicates that the msg is versioned
	 * @throws Exception 
	 */
	private void processEventLogMsg(byte[] msg, boolean isVersioned) throws Exception {
		if (isVersioned) {
			throw new Exception("processEventLogMsg() - versioning not supported");
		}
		byte items = msg[1];
		int upto = 2;
		int mtp = hwStatus.getMicrotickPeriod();
		List<EvtLogRaw> logs = new ArrayList<EvtLogRaw>();
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
			EvtLogRaw log = new EvtLogRaw(new Timestamp(timestamp,mtp),src, port, instrId, tickref, data);
			logs.add(log);
		}
		logger.debug("processEventLogMsg(): received " + logs.size() + " logs");
		if (callback == null) {
			logger.warn("processCFStatusMsg(): no callback to deliver to");
		} else {
			callback.processEvtLogs(logs);
		}
	}

	/**
	 * @param msg
	 * @param isVersioned - indicates that the msg is versioned
	 * |#items|ts(48bit)|RQ_EL_ID|instr|lku_op|RSP_EL_ID|rsp time|rslt|data(32bit)| .. next ..|
	 * @throws Exception 
	 */
	private void processLkuAuditLogMsg(byte[] msg, boolean isVersioned) throws Exception {
		int version = 0;
		int upto = 1;
		if (isVersioned) {
			version = msg[upto++];
			switch (version) {
			case 0: break;
			case 2: break;
			default: throw new Exception("processLkuAuditLogMsg() - version " + version + " not supported");
			}
		}
		byte items = msg[upto];
		upto++;
		int mtp = hwStatus.getMicrotickPeriod();
		List<LkuAuditRawLog> logs = new ArrayList<LkuAuditRawLog>();
		while (items-- > 0) {
			// process first/next log item
			long timestamp = Conversion.getLongFromBytes(msg,upto,6);
			upto+=6;
			int requesterId = msg[upto++];
			int instrId = msg[upto++];
			int tickref = msg[upto++] & 0xFF;
			int op = msg[upto++];
			int arg = 0;
			if (version == 2) {
				arg = msg[upto++];
			}
			int responderId = msg[upto++];
			int rspTime = msg[upto++];
			int resultCode = msg[upto++];
			int data = Conversion.getIntFromBytes(msg,upto,4);
			float fdata = Float.intBitsToFloat(data);
			upto +=4;
			// resolve format of incoming binary data
			LkuResult rslt = LkuResult.UNKNOWN;
			switch(resultCode) {
			case 0:	rslt = LkuResult.NO_DATA;	break;
			case 1:	rslt = LkuResult.OK;		break;
			case 2:	rslt = LkuResult.ERROR;		break;
			case 3:	rslt = LkuResult.TIMEOUT;	break;
			}
			LkuAuditRawLog log = new LkuAuditRawLog(new Timestamp(timestamp,mtp),requesterId,instrId,
					tickref,op,arg,responderId,rspTime,rslt,fdata);
			logs.add(log);
		}
		logger.debug("processLkuAuditLogMsg(): received " + logs.size() + " logs");
		if (callback == null) {
			logger.warn("processCFStatusMsg(): no callback to deliver to");
		} else {
			callback.processLkuLogs(logs);
		}
	}
	
	/**
	 * @param msg
	 * @param isVersioned - indicates that the msg is versioned
	 * |#items|ts(48bit)|src|takers(32 bit map)|instr|data(32bit)|qtime|delivery time| .. next ..|
	 * @throws Exception 
	 */
	private void processRtrAuditLogMsg(byte[] msg, boolean isVersioned) throws Exception {
		if (isVersioned) {
			throw new Exception("processRtrAuditLogMsg() - versioning not supported");
		}
		logger.debug("Received RTR audit message with " + msg.length + " bytes.");
		byte items = msg[1];
		int upto = 2;
		int mtp = hwStatus.getMicrotickPeriod();
		List<RtrAuditRawLog> logs = new ArrayList<RtrAuditRawLog>();
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
			upto +=4;
			int qTime = msg[upto++];
			int delTime = msg[upto++];
			// resolve format of incoming binary data
			RtrAuditRawLog log = new RtrAuditRawLog(new Timestamp(timestamp,mtp),sourceId,
					sourcePort,takerBitmap,instrId,tickref,qTime,delTime,data);
			logs.add(log);
		}
		logger.debug("processRtrAuditLogMsg(): received " + logs.size() + " logs");
		if (callback == null) {
			logger.warn("processCFStatusMsg(): no callback to deliver to");
		} else {
			callback.processRtrLogs(logs);
		}
	}


	private String toHexString(byte[] data, int start, int end) {
		byte[] buf = new byte[end-start+1];
		System.arraycopy(data, start, buf, 0, end-start+1);
		return Conversion.toHexString(buf);
	}


	private void releaseRx() {
		if (rxSem.availablePermits() < 1) {
			rxSem.release();
		}
	}

}

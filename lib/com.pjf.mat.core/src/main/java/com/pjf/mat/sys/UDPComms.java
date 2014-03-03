package com.pjf.mat.sys;


import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collection;

import org.apache.log4j.Logger;

import com.pjf.mat.api.Cmd;
import com.pjf.mat.api.Comms;
import com.pjf.mat.api.Element;
import com.pjf.mat.api.MatElementDefs;
import com.pjf.mat.api.Status;
import com.pjf.mat.api.util.HwStatus;
import com.pjf.mat.impl.element.SystemCmd;
import com.pjf.mat.util.comms.UDPSktComms;
import com.pjf.mat.util.comms.UDPCxn;

public class UDPComms extends UDPSktComms implements Comms {
	private final static Logger logger = Logger.getLogger(UDPComms.class);
	private static final long HWSIG_TIMEOUT_MS = 2000;
	private int port;
	private TimeoutSemaphore hwSigSem;

	public UDPComms(String ip, int port) throws SocketException, UnknownHostException {
		super(ip);
		this.port = port;
		this.mat = null;
		hwSigSem = new TimeoutSemaphore(0);
		hwStatus = new HwStatus();
	}

	
	public void shutdown() {
		super.shutdown();
		hwSigSem.release();
	}
	
	@Override
	public void sendConfig(Collection<Element> elements) throws Exception {
		logger.info("Preparing to send config to ip:" + getIp() + " port:" + port);
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
		cfg.putCmdItem(cmd.getParentID(),cmd.getConfigId(),cmd.getArg(), cmd.getData());
		logger.info("sendCmd(" + cmd.getFullName() + "): encoded " + cfg.getLength() + " bytes");
		cxn.send(cfg.getData(),port);
	}


	@Override
	public Status requestStatus() throws Exception {
		SystemCmd cmd = new SystemCmd("Request_Status",MatElementDefs.EL_C_STATUS_REQ,0xff);
		sendCmd(cmd);
		return null;
	}

	@Override
	public long getHWSignature() throws Exception{
		// send request
		SystemCmd cmd = new SystemCmd("Request_HWSig",MatElementDefs.EL_C_HWSIG_REQ);
		sendCmd(cmd);
		// now wait till we have response back
		// TODO add timeout
		logger.info("getHWSignature() - waiting for signatue from HW ...");
		try {
			hwSigSem.acquire(HWSIG_TIMEOUT_MS);
		} catch (Exception e) {
			logger.debug("getHWSignature(): Exception in acquiring semaphore.");
		}
		if (hwSigSem.timedOut()) {
			throw new Exception("Request for HW Signature timed out");
		}
		return hwStatus.getHwSig();
	}

	@Override
	public Status requestStatus(Element element) throws Exception {
		SystemCmd cmd = new SystemCmd("Request_Status",MatElementDefs.EL_C_STATUS_REQ,element.getId());
		sendCmd(cmd);
		return null;
	}


	/**
	 * @return underlying cxn used to do the comms
	 */
	@Override
	public UDPCxn getCxn() {
		return cxn;
	}

	@Override
	protected void processRxHwSig(HwStatus hws) {
		logger.info("processRxHwSig() - signature received: " + hws);
		hwStatus = hws;
		hwSigSem.release();
	}


	@Override
	public void synchroniseClock(long syncOriginMs) throws IOException {
		EncodedConfigItemList cfg = new EncodedConfigItemList();
		cfg.putCmdItem(0,MatElementDefs.EL_C_CLKSYNC_REQ | 0x80,0, 0);
		logger.info("synchroniseClock(" + syncOriginMs + "): encoded " + cfg.getLength() + " bytes");
		cxn.send(cfg.getData(),port);
	}


	@Override
	public void requestLkuAuditLogs() throws Exception {
		EncodedConfigItemList cfg = new EncodedConfigItemList();
		cfg.putCmdItem(0,MatElementDefs.EL_C_LKU_AUDIT_REQ | 0x80,0, 0);
		logger.info("requestLkuAuditLogs(): encoded " + cfg.getLength() + " bytes");
		cxn.send(cfg.getData(),port);
	}


	@Override
	public void requestRtrAuditLogs() throws Exception {
		EncodedConfigItemList cfg = new EncodedConfigItemList();
		cfg.putCmdItem(0,MatElementDefs.EL_C_RTR_AUDIT_REQ,0, 0);
		logger.info("requestRtrAuditLogs(): encoded " + cfg.getLength() + " bytes");
		cxn.send(cfg.getData(),port);
	}


	@Override
	public void resetCounters() throws IOException {
		EncodedConfigItemList cfg = new EncodedConfigItemList();
		cfg.putSystemItem(MatElementDefs.EL_ID_ALL,MatElementDefs.EL_C_RESET_CNTRS,0, 0);
		logger.info("resetCounters(): encoded " + cfg.getLength() + " bytes");
		cxn.send(cfg.getData(),port);
	}


}

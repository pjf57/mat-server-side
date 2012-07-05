package com.pjf.mat.sys;

import java.io.IOException;
import java.util.Collection;
import java.util.Random;

import org.apache.log4j.Logger;

import com.pjf.mat.api.Cmd;
import com.pjf.mat.api.Comms;
import com.pjf.mat.api.Element;
import com.pjf.mat.api.Status;

public class DummyComms extends BaseComms implements Comms {
	private final static Logger logger = Logger.getLogger(DummyComms.class);
	Random rng;
	DummyStatusReader statusReader;
	DummyEventReader eventReader;

	/**
	 * Send random status updates
	 */
	class DummyStatusReader extends Thread {
		private boolean keepGoing = true;
		private int pause;
		
		public DummyStatusReader(int pauseMs) {
			super();
			this.pause = pauseMs;
			this.setName("dummy status reader");
		}
		
		@Override
		public void run() {
			logger.info("dummy status reader starting");
			try {
				while (keepGoing) {
					Thread.sleep(pause);
					// generate some random element to use
					int el_id = rng.nextInt(mat.getElements().size());
					Element el = mat.getElement(el_id);
					// get random basis state, el state, and count update
					int bstate = rng.nextInt(4) + 1;
					int estate = rng.nextInt(9) + 1;
					int cnt = (int) el.getElementStatus().getEventInCount();
					cnt += rng.nextInt(100);
					//  Make message:
					//	-----------------------------------------------------------------------
					// 	|1|#items|EL.ID|EL.TYP|basis state|int state|#evts (32bit)| .. next ..|
					//	-----------------------------------------------------------------------
					byte[] rep = new byte[10];
					rep[0] = 1;
					rep[1] = 1;
					rep[2] = (byte) el_id;
					rep[3] = 0;	//FIXME: should be type id
					rep[4] = (byte) bstate;
					rep[5] = (byte) estate;
					rep[6] = (byte) ((cnt >> 24) & 0xff);;
					rep[7] = (byte) ((cnt >> 16) & 0xff);;
					rep[8] = (byte) ((cnt >> 8) & 0xff);;
					rep[9] = (byte) (cnt & 0xff);
					
					if (keepGoing) {
						processIncomingMsg(rep);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			logger.info("dummy status reader stopped.");
		}
		

		public void shutdown() {
			logger.info("dummy status reader shutting down ...");
			keepGoing = false;
		}
	}

	class DummyEventReader extends Thread {
		private boolean keepGoing = true;
		int pause;
		
		public DummyEventReader(int pauseMs) {
			super();
			this.pause = pauseMs;
			this.setName("dummy event reader");
		}
		
		@Override
		public void run() {
			logger.info("Dummy event reader starting");
			try {
				while (keepGoing) {
					Thread.sleep(pause);
					// generate some random element to use
					int el_id = rng.nextInt(mat.getElements().size());
					// get random instrument and event data
					int instr = rng.nextInt(255) + 1;
					int data = rng.nextInt(32767);
					//  Make message:
					//	-------------------------------------------------------
					//	|3|#items|src.id|instr_id|0|0|data (32bit)| .. next ..|
					//	-------------------------------------------------------
					byte[] rep = new byte[10];
					rep[0] = 3;
					rep[1] = 1;
					rep[2] = (byte) el_id;
					rep[3] = (byte) instr;
					rep[4] = 0;
					rep[5] = 0;
					rep[6] = (byte) ((data >> 24) & 0xff);;
					rep[7] = (byte) ((data >> 16) & 0xff);;
					rep[8] = (byte) ((data >> 8) & 0xff);;
					rep[9] = (byte) (data & 0xff);
					
					if (keepGoing) {
						processIncomingMsg(rep);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			logger.info("Dummy event reader stopped.");
		}
		

		public void shutdown() {
			logger.info("Dummy event reader shutting down ...");
			keepGoing = false;
		}
	}

	public DummyComms() {
		rng = new Random();
		statusReader = new DummyStatusReader(300);
		eventReader = new DummyEventReader(80);
		statusReader.start();
		eventReader.start();
	}
	
	@Override
	public void sendConfig(Collection<Element> elements) throws Exception {
		logger.info("Preparing to send config ...");
		EncodedConfigItemList cfg = new EncodedConfigItemList();
		for (Element el : elements) {
			hwEncodeConfig(el,cfg);
		}
		logger.info("sendConfig(): encoded " + cfg.getItemCount() + " items into " + cfg.getLength() + " bytes");
	}
	
	@Override
	public void sendCmd(Cmd cmd) throws IOException {
		EncodedConfigItemList cfg = new EncodedConfigItemList();
		cfg.put(cmd.getParent().getId(),cmd.getConfigId() | 0x80,0);
		logger.info("sendCmd(" + cmd.getFullName() + "): encoded " + cfg.getLength() + " bytes");
	}

	@Override
	public Status requestStatus() {
		logger.info("Requesting status all.");
		return null;
	}

	@Override
	public void shutdown() {
		logger.info("Shutting down...");
		statusReader.shutdown();
		eventReader.shutdown();
	}

	@Override
	public Status requestStatus(Element element) throws Exception {
		logger.info("Requesting status for " + element);
		return null;
	}

}

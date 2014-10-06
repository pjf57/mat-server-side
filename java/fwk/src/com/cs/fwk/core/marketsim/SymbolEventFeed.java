package com.cs.fwk.core.marketsim;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import com.cs.fwk.api.comms.CFDatagram;
import com.cs.fwk.api.comms.CxnInt;
import com.cs.fwk.util.Conversion;
import com.cs.fwk.util.comms.UDPCxn;
import com.cs.fwk.util.data.DataSource;
import com.cs.fwk.util.data.TickData;


/**
 * Sends an event feed in symbol format:
 * --						       1		   n         n              n
 *--						-----------------------------------------------
 *--						|# events | event 1 | event 2 | ... | event N |
 *--						-----------------------------------------------
 *--
 *--						Each event is:
 *--						   1    8        4         1          4
 *--						---------------------------------------------
 *--						|type|symbol|  price  | price_ndp | volume  |
 *--						---------------------------------------------
 *--
 *--						type: 1=trade, 2=bid, 3=ask
 *--						symbol is 8 char, padded to right with spaces
 *--						price and volume are uint32. price_ndp is uint8 number of dp in the price
 * @author pjf
 *
 */
public class SymbolEventFeed extends Thread implements EventFeedInt {
	private final static String THREAD_NAME = "mkt_sim";
	private final static Logger logger = Logger.getLogger(SymbolEventFeed.class);
	private CxnInt cxn;
	private final String ip;
	private int port;
	private EventFeedCallbackInt cb;
	private long totalSent = 0L;
	private boolean loop;			// true if want to loop data for greater length
	private String resource;
	private int bursts;
	private int ticksPerPkt;
	private int gapMs;

	private class EncodedFeedItemList {
		private int itemCount;
		private int upto;
		private byte[] data;
		
		public EncodedFeedItemList() {
			itemCount = 0;
			upto = 1;
			data = new byte[1500];
		}

		public void put(TickData tick) {
			int intPrice = Conversion.floatToIntWhole(tick.price);
			int ndp = Conversion.floatToNdp(tick.price);
			int intVol = (int) tick.volume;
			data[upto++] = (byte) tick.evt.getIntCode();
			for (int i=0; i<8; i++) {
				data[upto++] = (byte) tick.symbol.charAt(i);
			}
			data[upto++] = (byte) ((intPrice >> 24) & 0xff);
			data[upto++] = (byte) ((intPrice >> 16) & 0xff);
			data[upto++] = (byte) ((intPrice >> 8) & 0xff);
			data[upto++] = (byte) (intPrice & 0xff);
			data[upto++] = (byte) (ndp & 0xff);
			data[upto++] = (byte) ((intVol >> 24) & 0xff);
			data[upto++] = (byte) ((intVol >> 16) & 0xff);
			data[upto++] = (byte) ((intVol >> 8) & 0xff);
			data[upto++] = (byte) (intVol & 0xff);
			itemCount++;
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

	public SymbolEventFeed(String ip, int port) throws SocketException, UnknownHostException {
		setName(THREAD_NAME);
		this.ip = ip;
		this.port = port;
		this.cxn = new UDPCxn(ip);
		this.cb = null;
		this.loop = false;
	}

	public SymbolEventFeed(CxnInt cxn, int port) throws SocketException, UnknownHostException {
		setName(THREAD_NAME);
		this.ip = cxn.getAddress();
		this.port = port;
		this.cxn = cxn;
		this.cb = null;
		this.loop = false;
	}
	
	public SymbolEventFeed(CxnInt cxn, int port, boolean loop) throws SocketException, UnknownHostException {
		setName(THREAD_NAME);
		this.ip = cxn.getAddress();
		this.port = port;
		this.cxn = cxn;
		this.cb = null;
		this.loop = loop;
	}
	
	/**
	 * Set callback to receive progress notifications
	 * 
	 * @param cb
	 */
	public void setCb(EventFeedCallbackInt cb) {
		this.cb = cb;
	}

	@Override
	public void sendTradeBurst(String resource, int bursts, int ticksPerPkt, int gapMs) throws Exception {
		this.resource = resource;
		this.bursts = bursts;
		this.ticksPerPkt = ticksPerPkt;
		this.gapMs = gapMs;
		start();
	}
	
	@Override
	public void run() {
		boolean error = false;
		logger.info("Starting ...");
		try {
			DataSource ds = new DataSource(resource);
			ds.setLoop(loop);
			for (int pkt=0; pkt<bursts; pkt++) {
				EncodedFeedItemList list = new EncodedFeedItemList();
				for (int tick=0; tick<ticksPerPkt; tick++) {
					TickData data = ds.getNext();
					list.put(data);
				}
				logger.info("Sending stream of " + list.getItemCount() + " ticks with " + list.getLength() + " bytes.");
				sendData(list);
				totalSent += list.getItemCount();
				notifyState("running",totalSent);
				if (gapMs > 0) {
					Thread.sleep(gapMs);
				}
			}
		} catch (Exception e) {
			error = true;
			logger.error("run(): error:" + e);
			notifyState("error",totalSent);
		}
		if (!error) {
			notifyState("stopped",totalSent);
		}
		logger.info("Stopped.");
	}
	
	/**
	 * Notify event feed state to cb if exists
	 * 
	 * @param status
	 * @param totalSent
	 */
	private void notifyState(String status, long totalSent) {
		if (cb != null) {
			cb.notifyEventFeedState(status, totalSent);
		}
	}
	
	
	/**
	 * Send one burst of notifications
	 * 
	 * @param data
	 * @throws IOException 
	 */
	private void sendData(EncodedFeedItemList data) throws IOException {
		cxn.send(new CFDatagram(port,data.getData()));
	}

	@Override
	public String toString() {
		return "SymbolEventFeed:" + ip + ":" + port;
	}

}

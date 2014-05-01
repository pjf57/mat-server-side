package com.pjf.marketsim;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.UnknownHostException;
import org.apache.log4j.Logger; 

import com.pjf.mat.api.comms.CFDatagram;
import com.pjf.mat.api.comms.CxnInt;
import com.pjf.mat.util.Conversion;
import com.pjf.mat.util.data.DataSource;
import com.pjf.mat.util.comms.UDPCxn;
import com.pjf.mat.util.data.TickData;


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
public class SymbolEventFeed implements EventFeedInt {
	private final static Logger logger = Logger.getLogger(SymbolEventFeed.class);
	private CxnInt cxn;
	private final String ip;
	private int port;
	private EventFeedCallbackInt cb;
	private long totalSent = 0L;

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
		this.ip = ip;
		this.port = port;
		this.cxn = new UDPCxn(ip);
		this.cb = null;
	}

	public SymbolEventFeed(CxnInt cxn, int port) throws SocketException, UnknownHostException {
		this.ip = cxn.getAddress();
		this.port = port;
		this.cxn = cxn;
		this.cb = null;
	}
	
	
	/**
	 * Set callback to receive progress notifications
	 * 
	 * @param cb
	 */
	public void setCb(EventFeedCallbackInt cb) {
		this.cb = cb;
	}

	/**
	 * Send a number of ticks as a single UDP frame
	 * 
	 * @param resource 		when to get the source data
	 * @param bursts		number of bursts (pkts) to send
	 * @param ticksPerPkt	number of ticks in each pkt 
	 * @param gapMs			inter packet gap (ms)
	 * @param i 
	 * @throws Exception 
	 */
	public void sendTradeBurst(InputStream is, int bursts, int ticksPerPkt, int gapMs) throws Exception {
		DataSource ds = new DataSource(is);
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
		notifyState("stopped",totalSent);
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

	/* (non-Javadoc)
	 * @see com.pjf.marketsim.EventFeedInt#sendTradeBurst(java.lang.String, int, int, int)
	 */
	@Override
	public void sendTradeBurst(String resource, int bursts, int ticksPerPkt, int gapMs) throws Exception {
		sendTradeBurst(new FileInputStream(resource), bursts, ticksPerPkt, gapMs);
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

package com.pjf.marketsim;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import org.apache.log4j.Logger;

import com.pjf.mat.util.comms.UDPCxn;


public class EventFeed {
	private final static Logger logger = Logger.getLogger(EventFeed.class);
	private UDPCxn cxn;
	private final String ip;
	private int port;

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
			int intPrice = Float.floatToIntBits(tick.price);
			int intVol = Float.floatToIntBits(tick.volume);
			data[upto++] = (byte) tick.type;
			data[upto++] = (byte) tick.instr_id;
			data[upto++] = (byte) ((intPrice >> 24) & 0xff);
			data[upto++] = (byte) ((intPrice >> 16) & 0xff);
			data[upto++] = (byte) ((intPrice >> 8) & 0xff);
			data[upto++] = (byte) (intPrice & 0xff);
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

	public EventFeed(String ip, int port) throws SocketException, UnknownHostException {
		this.ip = ip;
		this.port = port;
		this.cxn = new UDPCxn(ip);
	}

	public EventFeed(UDPCxn cxn, int port) throws SocketException, UnknownHostException {
		this.ip = cxn.getIp();
		this.port = port;
		this.cxn = cxn;
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
	public void sendTradeBurst(String resource, int bursts, int ticksPerPkt, int gapMs) throws Exception {
		DataSource ds = new DataSource(resource);
		for (int pkt=0; pkt<bursts; pkt++) {
			EncodedFeedItemList list = new EncodedFeedItemList();
			for (int tick=0; tick<ticksPerPkt; tick++) {
				TickData data = ds.getNext();
				list.put(data);
			}
			logger.info("Sending stream of " + list.getItemCount() + " ticks with " + list.getLength() + " bytes.");
			sendData(list);
			if (gapMs > 0) {
				Thread.sleep(gapMs);
			}
		}
	}
	
	
	/**
	 * Send one burst of notifications
	 * 
	 * @param data
	 * @throws IOException 
	 */
	private void sendData(EncodedFeedItemList data) throws IOException {
		cxn.send(data.getData(),port);
	}

	@Override
	public String toString() {
		return "EventFeed:" + ip + ":" + port;
	}

}

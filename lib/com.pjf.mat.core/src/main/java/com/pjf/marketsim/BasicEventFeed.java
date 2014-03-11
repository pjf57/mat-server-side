package com.pjf.marketsim;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.UnknownHostException;
import org.apache.log4j.Logger; 

import com.pjf.mat.api.comms.CxnInt;
import com.pjf.mat.util.Conversion;
import com.pjf.mat.util.data.DataSource;
import com.pjf.mat.util.comms.UDPCxn;
import com.pjf.mat.util.data.TickData;

/**
 * --						       1		   n         n              n
 * --						-----------------------------------------------
 * --						|# events | event 1 | event 2 | ... | event N |
 * --						-----------------------------------------------
 * --
 * --						Each event is:
 * --						   1      1        4         4
 * --						-----------------------------------
 * --						|type|instr_id|  price  | volume  |
 * --						-----------------------------------
 * --
 * --						type: 1=trade, 2=bid, 3=ask
 * --						instr_id is internal instr id index
 * --						price and volume are sp floats
 * 
 * @author pjf
 *
 */
public class BasicEventFeed implements EventFeedInt {
	private final static Logger logger = Logger.getLogger(BasicEventFeed.class);
	private CxnInt cxn;
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
			data[upto++] = (byte) tick.evt.getIntCode();
			data[upto++] = (byte) tick.symbol.charAt(0);
			Conversion.floatToBytes(tick.price,data,upto);
			upto += 4;
			Conversion.floatToBytes(tick.volume,data,upto);
			upto += 4;
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

	public BasicEventFeed(String ip, int port) throws SocketException, UnknownHostException {
		this.ip = ip;
		this.port = port;
		this.cxn = new UDPCxn(ip);
	}

	public BasicEventFeed(CxnInt cxn, int port) throws SocketException, UnknownHostException {
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
			if (gapMs > 0) {
				Thread.sleep(gapMs);
			}
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
		cxn.send(data.getData(),port);
	}

	@Override
	public String toString() {
		return "BasicEventFeed:" + ip + ":" + port;
	}

}

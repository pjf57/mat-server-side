package com.cs.fwk.core.marketsim;

public interface EventFeedInt {

	/**
	 * Send a number of ticks as <bursts> UDP frames, each containing <ticksPerPkt> ticks
	 * 
	 * @param resource 		when to get the source data
	 * @param bursts		number of bursts (pkts) to send
	 * @param ticksPerPkt	number of ticks in each pkt 
	 * @param gapMs			inter packet gap (ms)
	 * @param i 
	 * @throws Exception 
	 */
	public void sendTradeBurst(String resource, int bursts,
			int ticksPerPkt, int gapMs) throws Exception;

}
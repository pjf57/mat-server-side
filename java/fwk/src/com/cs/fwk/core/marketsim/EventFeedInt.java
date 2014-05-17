package com.cs.fwk.core.marketsim;

public interface EventFeedInt {

	public abstract void sendTradeBurst(String resource, int bursts,
			int ticksPerPkt, int gapMs) throws Exception;

}
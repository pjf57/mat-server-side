package com.pjf.mat.util.data;

public class MarketEventType {
	public static final int TRADE = 1;
	public static final int BID = 2;
	public static final int ASK = 3;

	private final int val;

	public MarketEventType(int val){
		this.val = val;
	}
	
	public int get() {
		return val;
	}

	/**
	 * Convert MarketEventType to an integer code
	 * 
	 * @return int code
	 */
	public byte getIntCode() {
		byte ret = (byte) val;
		return ret;
	}
}


package com.pjf.mat.util.data;

public class MarketEventType {
	public static final int TRADE = 1;
	public static final int BID = 2;
	public static final int ASK = 3;

	private final int val;

	public MarketEventType(int val){
		this.val = val;
	}

	public MarketEventType(String name){
		String nameu = name.toUpperCase();
		if (nameu.equals("TRADE")) {
			this.val = TRADE;
		} else if (nameu.equals("BID")) {
			this.val = BID;
		} else if (nameu.equals("ASK")) {
			this.val = ASK;
		} else {
			this.val = 0;
		}
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
	
	@Override
	public String toString() {
		String ret = "";
		switch(val) {
		case 1:  ret = "TRADE";				break;
		case 2:  ret = "BID";				break;
		case 3:  ret = "ASK";				break;
		default: ret = "Unknown: " + val;	break;
		}
		return ret;
	}
}


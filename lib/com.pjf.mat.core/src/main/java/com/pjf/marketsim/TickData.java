package com.pjf.marketsim;

public class TickData {
	public int type;
	public String symbol;
	public float price;
	public float volume;
	
	public TickData(int type, String symbol, float price, float volume) {
		this.type = type;
		this.symbol = format(symbol);
		this.price = price;
		this.volume = volume;
	}

	/**
	 * Format symbol so that it is right padded with spaces to size of 8 chars
	 * 
	 * @param sym - raw symbol
	 * @return formated symbol
	 */
	private String format(String sym) {
		String fsym = sym.trim();		// remove any leading spaces
		if (sym.length() > 8) {
			fsym.substring(1,8);		// take only first 8 chars of symbol
		}
		while (fsym.length() < 8) {
			fsym = fsym + " ";
		}
		return fsym;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append('[');
		buf.append(type); buf.append(",[");
		buf.append(symbol); buf.append("],");
		buf.append(price); buf.append(',');
		buf.append(volume); buf.append(']');
		return buf.toString();
	}
}

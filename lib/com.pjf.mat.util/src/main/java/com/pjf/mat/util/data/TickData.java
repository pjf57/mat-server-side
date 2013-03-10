package com.pjf.mat.util.data;

public class TickData {
	public MarketEventType evt;
	public String symbol;
	public float price;
	public float volume;
	public String mktRef;

	public TickData(MarketEventType evt, String symbol, float price, float volume) {
		this.evt = evt;
		this.symbol = format(symbol);
		this.price = price;
		this.volume = volume;
	}

	public TickData(int type, String symbol, float price, float volume) throws Exception {
		evt = new MarketEventType(type);
		this.symbol = format(symbol);
		this.price = price;
		this.volume = volume;
		this.mktRef = "        ";
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
		buf.append(evt); buf.append(",[");
		buf.append(symbol); buf.append("],");
		buf.append(price); buf.append(',');
		buf.append(volume);buf.append(',');
		buf.append(mktRef);  buf.append(']');
		return buf.toString();
	}
}

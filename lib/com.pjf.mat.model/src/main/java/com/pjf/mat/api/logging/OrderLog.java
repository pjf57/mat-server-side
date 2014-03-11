package com.pjf.mat.api.logging;

import com.pjf.mat.api.Timestamp;

/**
 * Model an order event
 * 
 * @author pjf
 */
public class OrderLog extends BaseLog {
	private final String symbol;
	private final char side;
	private final float price;
	private final int volume;
	
	/**
	 * Create order event without source and at time now
	 * 
	 * @param symbol
	 * @param side
	 * @param price
	 * @param volume
	 */
	public OrderLog(String symbol, char side, float price, int volume) {
		super(new Timestamp());
		this.symbol = symbol;
		this.side = side;
		this.price = price;
		this.volume = volume;
	}

	@Override
	public String getType() {
		return "ORD";
	}

	public int getRawValue() {
		return 0;
	}

	@Override
	public String getDispValue() {
		return "" + price;
	}

	public String getSymbol() {
		return symbol;
	}

	public char getSide() {
		return side;
	}

	public float getPrice() {
		return price;
	}

	public int getVolume() {
		return volume;
	}

	/**
	 * @return side of order as a descriptive string
	 */
	private String getBuySell() {
		switch (side) {
		case 'B':	return "buy";
		case 'S':	return "sell";
		}
	return "illegal side: " + side;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("Order: ");
		buf.append(getBuySell());
		buf.append(" " + volume);
		buf.append(" " + symbol);
		buf.append(" at " + price);
		return buf.toString();
	}

	public void setTimeStamp(Timestamp newTimeStamp) {
		timestamp = newTimeStamp;
	}


}

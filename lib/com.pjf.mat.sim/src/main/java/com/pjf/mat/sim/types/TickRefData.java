package com.pjf.mat.sim.types;

import com.pjf.mat.util.data.MarketEventType;
import com.pjf.mat.util.data.TickData;

public class TickRefData {
	private final int tickref;
	private final int instrumentId;
	private final String Symbol;
	private final int mktId;
	private final float price;
	private final float volume;
	private final MarketEventType evt;
	private final String mktRef;
	
	/**
	 * Create from discrete values
	 * 
	 * @param tickref
	 * @param instrumentId
	 * @param symbol
	 * @param mktId
	 * @param price
	 * @param volume
	 * @param evt
	 * @param mktRef
	 */
	public TickRefData(int tickref, int instrumentId, String symbol, int mktId,
			float price, float volume, MarketEventType evt, String mktRef) {
		this.tickref = tickref;
		this.instrumentId = instrumentId;
		Symbol = symbol;
		this.mktId = mktId;
		this.price = price;
		this.volume = volume;
		this.evt = evt;
		this.mktRef = mktRef;
	}

	/**
	 * Create from tick data
	 * 
	 * @param tickref - tick reference
	 * @param mktId - market ID
	 * @param instrId - instrument ID
	 * @param td - tick data
	 */
	public TickRefData(int tickref, int mktId, int instrId, TickData td) {
		this.tickref = tickref;
		this.instrumentId = instrId;
		Symbol = td.symbol;
		this.mktId = mktId;
		this.price = td.price;
		this.volume = td.volume;
		this.evt = td.evt;
		this.mktRef = td.mktRef;
	}

	public int getTickref() {
		return tickref;
	}

	public int getInstrumentId() {
		return instrumentId;
	}

	public String getSymbol() {
		return Symbol;
	}

	public int getMktId() {
		return mktId;
	}

	public float getPrice() {
		return price;
	}

	public float getVolume() {
		return volume;
	}

	public MarketEventType getEvt() {
		return evt;
	}

	public String getMktRef() {
		return mktRef;
	}

	@Override
	public String toString() {
		return "TickRefData [tickref=" + tickref + ", instrumentId="
				+ instrumentId + ", Symbol=" + Symbol + ", mktId=" + mktId
				+ ", price=" + price + ", volume=" + volume + ", evt=" + evt
				+ ", mktRef=" + mktRef + "]";
	}

	
}

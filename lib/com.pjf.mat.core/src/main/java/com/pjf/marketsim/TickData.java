package com.pjf.marketsim;

public class TickData {
	public int type;
	public int instr_id;
	public float price;
	public float volume;
	
	public TickData(int type, int instr_id, float price, float volume) {
		this.type = type;
		this.instr_id = instr_id;
		this.price = price;
		this.volume = volume;
	}
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append('[');
		buf.append(type); buf.append(',');
		buf.append(instr_id); buf.append(',');
		buf.append(price); buf.append(',');
		buf.append(volume); buf.append(']');
		return buf.toString();
	}
}

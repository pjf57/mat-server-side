package com.cs.fwk.api.gridattr;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds data for one row of a grid
 * 
 * @author pjf
 *
 */
public class GridRowData {
	private List<String> data;
	
	/**
	 * Create an empty row
	 */
	public GridRowData() {
		data = new ArrayList<String>();
	}
	
	/**
	 * Create a row with an ordered value set
	 * 
	 * @param values array of values in order
	 */
	public GridRowData(String[] values) {
		data = new ArrayList<String>(); 
		for (String s : values) {
			data.add(s);
		}
	}

	public String get(int idx) {
		return data.get(idx);
	}
	
	// decimal
	public int getInt(int idx) {
		String s = data.get(idx);
		return Integer.parseInt(s);
	}

	// decimal with default value
	public int getInt(int idx, int dflt) {
		int ret = dflt;
		String s = data.get(idx);
		if ((s != null) && (s.length() > 0)) {
			ret = Integer.parseInt(s);
		}
		return ret;
	}

	// decimal or hex
	public int decodeInt(int idx) {
		String s = data.get(idx);
		return Integer.decode(s);
	}


	public void set(int idx, String value) {
		data.set(idx,value);
	}
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer("[");
		boolean first = true;
		for (String s : data) {
			if (!first) {
				buf.append(",");
			}
			buf.append(s);
			first = false;
		}
		buf.append("]");
		return buf.toString();
	}




}

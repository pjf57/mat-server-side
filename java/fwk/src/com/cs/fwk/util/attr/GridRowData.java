package com.cs.fwk.util.attr;

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

	public void set(int idx, String value) {
		data.set(idx,value);
	}

}

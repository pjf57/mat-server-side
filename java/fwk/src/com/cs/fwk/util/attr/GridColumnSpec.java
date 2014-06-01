package com.cs.fwk.util.attr;

/**
 * holds a specification for a grid column
 * 
 * @author pjf
 *
 */
public class GridColumnSpec {
	private final String name;
	private final String type;
	private final int colNumber;
	
	/**
	 * 
	 * @param name - name of the column
	 * @param type - type of data in the column
	 * @param colNumber - number of col (0..n)
	 */
	public GridColumnSpec(String name, String type, int colNum) {
		super();
		this.name = name;
		this.type = type;
		this.colNumber = colNum;
	}
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}
	
	public int getColNumber() {
		return colNumber;
	}
}

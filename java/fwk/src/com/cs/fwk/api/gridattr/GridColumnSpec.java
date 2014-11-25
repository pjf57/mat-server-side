package com.cs.fwk.api.gridattr;

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
	private String enumSpec;		// form {"e1","e2","e3"} or null
	
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
		this.enumSpec = null;
	}

	/**
	 * Set a new spec for an enum
	 * 
	 * @param spec	{"e1","e2","e3"}
	 */
	public void setEnumSpec(String spec) {
		this.enumSpec = spec;
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

	public boolean isEnum() {
		return enumSpec != null;
	}

	public String getEnumValues() {
		return enumSpec;
	}

	@Override
	public String toString() {
		return "GridColumnSpec [name=" + name + ", type=" + type
				+ ", colNumber=" + colNumber + ", enumSpec=" + enumSpec + "]";
	}
	
	
}

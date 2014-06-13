package com.cs.fwk.api.gridattr;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import org.apache.log4j.Logger;

import com.cs.fwk.api.AttrSysType;
import com.cs.fwk.api.Attribute;
import com.cs.fwk.api.AttributeType;
import com.cs.fwk.api.util.ConfigItem;
import com.cs.fwk.api.Element;
import com.cs.fwk.api.EnumValue;
import com.cs.fwk.api.util.AttrConfigGenerator;

/**
 * Defines a GRID attribute which has a number of named columns populated by a list of rows.
 * 
 * @author pjf
 *
 */
public class GridAttribute implements Attribute, Cloneable {
	private final static Logger logger = Logger.getLogger(GridAttribute.class);
	private static final String CONVERTER_PKG = "com.cs.fwk.core.config.converters";
	private final Element parent;
	private final String name;
	private final List<GridColumnSpec> colSpecs;
	private List<GridRowData> rowData;
	private final AttrSysType sysType;
	private final int configId;
	private String converter;
	private final int order;
	private final String calcSpec;
	

	public GridAttribute(Element parent, String name, int configId, String converter, AttrSysType sysType, int order, String calcSpec, List<GridColumnSpec> colSpecs) throws Exception {
		this.parent = parent;
		this.name = name;
		this.configId = configId;
		this.sysType = sysType;
		this.converter = converter;
		this.order = order;
		this.calcSpec = calcSpec;
		this.colSpecs = colSpecs;
		clearGrid();
	}


	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getValue() {
		return "getValue() not supported";
	}

	@Override
	public void setValue(String value) throws Exception {
		// ignore setting of value
	}
	
	@Override
	public GridAttribute clone(Element newParent) {
		GridAttribute attr = null;
		try {
			attr = new GridAttribute(newParent,name,configId,converter,sysType,getOrder(),getCalcSpecs(),colSpecs);
		} catch (Exception e) {
			logger.error("Unable to set default value [" + getValue() + "] on [" + this + "]");
		}
		return attr;
	}

	/**
	 * @param colName
	 * @return colspec for name, or null
	 */
	public List<GridColumnSpec> getColumnSpecs() {
		return colSpecs;
	}

	/**
	 * @return short value description as number of rows
	 */
	private String getShortValueDescr() {
		return "" + rowData.size() + " rows";
	}


	@Override
	public int getConfigId() {
		return configId;
	}

	@Override
	public int getEncodedData() throws Exception {
		throw new Exception("Not supported - use getConfigList()");
	}

	@Override
	public AttributeType getType() {
		return AttributeType.GRID;
	}

	@Override
	public SortedSet<EnumValue> getEnumValues() {
		return null;	// doesnt have enum values
	}

	@Override
	public AttrSysType getSysType() {
		return sysType;
	}

	@Override
	public List<ConfigItem> getConfigList() throws Exception {
		String cn = CONVERTER_PKG + "." + converter;
		AttrConfigGenerator gen = (AttrConfigGenerator) ClassLoader.getSystemClassLoader().loadClass(cn).newInstance();
		List<ConfigItem> configs = gen.generate(this);
		return configs;
	}

	@Override
	public Element getParent() {
		return parent;
	}

	@Override
	public int getOrder() {
		return order;
	}

	@Override
	public boolean isCalculated() {
		return calcSpec != null;
	}
	
	@Override
	public String getCalcSpecs() {
		return calcSpec;
	}
	
	@Override
	public int getRawValue() {
		return 0;
	}

	/**
	 * Return String value data for one cell
	 * 
	 * @param row 		(0..n)
	 * @param colName 	Name of column
	 * @return string 	format data
	 * @throws Exception 
	 */
	public String getValueStringRC(int row, String colName) throws Exception {
		String val = "";
		if (row < 0  ||  row > rowData.size()-1) {
			throw new Exception("getValueStringRC(" + row + "," + colName + ") - row out of range");
		}
		GridRowData rd = rowData.get(row);
		GridColumnSpec cs = getColumnSpec(colName);
		if (cs == null) {
			throw new Exception("getValueStringRC(" + row + "," + colName + ") - no such column");
		}
		val = rd.get(cs.getColNumber());
		return val;		
	}

	/**
	 * Set String value data for one cell
	 * 
	 * @param row 		(0..n)
	 * @param colName 	Name of column
	 * @param value 	data to set
	 * @throws Exception 
	 */
	public void setValueStringRC(int row, String colName, String value) throws Exception {
		if (row < 0  ||  row > rowData.size()-1) {
			throw new Exception("getValueStringRC(" + row + "," + colName + ") - row out of range");
		}
		GridRowData rd = rowData.get(row);
		GridColumnSpec cs = getColumnSpec(colName);
		if (cs == null) {
			throw new Exception("getValueStringRC(" + row + "," + colName + ") - no such column");
		}
		rd.set(cs.getColNumber(),value);
	}


	/**
	 * @param colName
	 * @return colspec for name, or null
	 */
	private GridColumnSpec getColumnSpec(String colName) {
		GridColumnSpec ret = null;
		for (GridColumnSpec gcs:colSpecs) {
			if (colName.equals(gcs.getName())) {
				ret = gcs;
				break;
			}
		}
		return ret;
	}

	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer(name);
		buf.append('='); buf.append(getShortValueDescr());
		buf.append("[c"); buf.append(this.getConfigId()); buf.append(']');
		buf.append(" GridAttr: [");
		boolean first = true;
		for (GridColumnSpec gcs : colSpecs) {
			if (!first) {
				buf.append(",");
			}
			buf.append(gcs.getName());
			buf.append(":");
			buf.append(gcs.getType());
			first = false;
		}
		buf.append("]");
		return buf.toString();		
	}

	/**
	 * Add a row of data to the grid
	 * 
	 * @param values - array of values in correct order
	 * @throws Exception if the values cannot be set
	 */
	public void addRow(String[] values) throws Exception {
		if (values.length != colSpecs.size()) {
			throw new Exception("add(" + values + "): expected " + colSpecs.size() + " values");
		}		
		GridRowData row = new GridRowData(values);
		rowData.add(row);		
	}
	
	/**
	 * Clear all data from the grid
	 */
	public void clearGrid() {
		this.rowData = new ArrayList<GridRowData>();		
	}


	/**
	 * @return number of rows in the grid
	 */
	public int getNumRows() {
		return rowData.size();
	}
	
	/**
	 * 
	 * @param row (0..n)
	 * @return row data for the specified row
	 * @throws Exception if row is out of range
	 */
	public GridRowData getRow(int row) throws Exception {
		if (row < 0  ||  row >= rowData.size()) {
			throw new Exception("getRow(" + row + ") out of range on Attr " + name + ", parent=" + parent.getShortName());
		}
		return rowData.get(row);
	}

}

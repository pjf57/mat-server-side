package com.pjf.mat.api.util;

import com.pjf.mat.api.AttrSysType;
import com.pjf.mat.api.OutputPort;


/**
 * Configuration and command data passed into an element
 * 
 * @author pjf
 *
 */
public class ConfigItem {
	private final int rawData;
	private final int itemId;
	private final int elementId;	// which element this config is targeted at
	private final AttrSysType sysType;
	private final int arg;
	
	/**
	 * Construct config item with raw config data
	 * 
	 * 
	 * @param elementId - element to configure
	 * @param sysType	- the type of config
	 * @param itemId	- item
	 * @param rawData	- raw data
	 */
	public ConfigItem(int elementId, AttrSysType sysType, int itemId, int rawData) {
		this.elementId = elementId;
		this.sysType = sysType;
		this.rawData = rawData;
		this.itemId = itemId;
		this.arg = 0;
	}

	/**
	 * Construct config item with float data
	 * 
	 * @param elementId - element to configure
	 * @param sysType	- the type of config
	 * @param itemId	- item
	 * @param data		- float data
	 */
	public ConfigItem(int elementId, AttrSysType sysType, int itemId, float data) {
		this.elementId = elementId;
		this.sysType = sysType;
		this.rawData = Float.floatToIntBits(data);
		this.itemId = itemId;
		this.arg = 0;
	}

	/**
	 * Construct config item with String data
	 * 
	 * @param elementId - element to configure
	 * @param sysType	- the type of config
	 * @param itemId	- item
	 * @param data		- 4 char string data
	 */
	public ConfigItem(int elementId, AttrSysType sysType, int itemId, String data) {
		this.elementId = elementId;
		this.sysType = sysType;
		this.itemId = itemId;
		this.arg = 0;
		int d = 0;
		for (int i=0; i<4; i++) {
			d = d << 8;
			d |= ((int) data.charAt(i)) & 0xff;
		}
		this.rawData = d;
	}

	/**
	 * Construct a config item as a cxn config
	 * 
	 * @param elementId	- element to configure
	 * @param itemId	- config item
	 * @param out		- output to connect from
	 * @param input		- input number (1..4)
	 */
	public ConfigItem(int elementId, int itemId, OutputPort out, int input) {
		this.elementId = elementId;
		this.sysType = AttrSysType.SYSTEM;
		this.itemId = itemId;
		this.rawData = ((input-1) << 16) | out.getId() << 8 | out.getParent().getId();
		this.arg = 0;
	}

	/**
	 * Construct a config item as a cxn config - specifying the src element id and port 0
	 * 
	 * @param elementId		- element to configure
	 * @param itemId		- config item
	 * @param srcElementId	- output to connect from
	 * @param input			- input number (1..4)
	 */
	public ConfigItem(int elementId, int itemId, int srcElementId, int input) {
		this.elementId = elementId;
		this.sysType = AttrSysType.SYSTEM;
		this.itemId = itemId;
		this.rawData = ((input-1) << 16) | 0 << 8 | srcElementId;
		this.arg = 0;
	}

	public int getElementId() {
		return elementId;
	}

	public int getRawData() {
		return rawData;
	}

	public float getFloatData() {
		return Float.intBitsToFloat(rawData);
	}

	/**
	 * @return a 4 char string from the 32 bit config value
	 */
	public String getStringData() {
		StringBuffer buf = new StringBuffer();
		buf.append( (char) ((rawData>>24) & 0xff));
		buf.append( (char) ((rawData>>16) & 0xff));
		buf.append( (char) ((rawData>>8) & 0xff));
		buf.append( (char) (rawData & 0xff));
		return buf.toString();
	}

	public int getItemId() {
		return itemId;
	}
	
	public AttrSysType getSysType() {
		return sysType;
	}
	
	@Override
	public String toString() {
		return "[elid=" + elementId + 
		",cfgId=" + sysType + ":" + itemId + 
		",data=" + toHexIntString(rawData) + "]";
	}

	/**
	 * @param value integer value
	 * @return 8 character hex representation
	 */
	private static String toHexIntString(int value) {
		StringBuffer buf = new StringBuffer();
		buf.append(toHexByteString(value >> 24));
		buf.append(toHexByteString(value >> 16));
		buf.append(toHexByteString(value >> 8));
		buf.append(toHexByteString(value));
		return buf.toString();
		}

	/**
	 * @param data - byte
	 * @return 2 char string hex representation
	 */
	private static String toHexByteString(int data) {
		StringBuffer buf = new StringBuffer();
		char[] map = new char[] {'0', '1', '2', '3', '4', '5', '6', '7',
								 '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
		int d = data & 0xff;
		buf.append(map[(d >> 4) & 0xf]);
		buf.append(map[d & 0xf]);
		return buf.toString();
	}

	public int getArg() {
		return arg;
	}
	
	
}

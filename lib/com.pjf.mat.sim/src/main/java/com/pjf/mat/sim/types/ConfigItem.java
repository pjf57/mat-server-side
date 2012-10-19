package com.pjf.mat.sim.types;

import com.pjf.mat.api.AttrSysType;
import com.pjf.mat.api.OutputPort;
import com.pjf.mat.util.Conversion;

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
	}

	/**
	 * Construct a config item as a cxn config - specifying the src element and port 0
	 * 
	 * @param elementId	- element to configure
	 * @param itemId	- config item
	 * @param srcElementId	- output to connect from
	 * @param input		- input number (1..4)
	 */
	public ConfigItem(int elementId, int itemId, int srcElementId, int input) {
		this.elementId = elementId;
		this.sysType = AttrSysType.SYSTEM;
		this.itemId = itemId;
		this.rawData = ((input-1) << 16) | 0 << 8 | srcElementId;
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
		",data=" + Conversion.toHexIntString(rawData) + "]";
	}
	
}

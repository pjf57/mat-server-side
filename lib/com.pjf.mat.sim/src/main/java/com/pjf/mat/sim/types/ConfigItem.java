package com.pjf.mat.sim.types;

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
	
	/**
	 * Construct config item with raw config data
	 * 
	 * @param elementId - element to configure
	 * @param itemId	- item
	 * @param rawData	- raw data
	 */
	public ConfigItem(int elementId, int itemId, int rawData) {
		this.elementId = elementId;
		this.rawData = rawData;
		this.itemId = itemId;
	}

	/**
	 * Construct config item with float data
	 * 
	 * @param elementId - element to configure
	 * @param itemId	- item
	 * @param data		- float data
	 */
	public ConfigItem(int elementId, int itemId, float data) {
		this.elementId = elementId;
		this.rawData = Float.floatToIntBits(data);
		this.itemId = itemId;
	}
	
	/**
	 * Construct a config item as a cxn config
	 * 
	 * @param elementId	- element to configure
	 * @param itemId	- config item
	 * @param source	- source element to connect from
	 * @param input		- input number (1..4)
	 */
	public ConfigItem(int elementId, int itemId, int source, int input) {
		this.elementId = elementId;
		this.itemId = itemId;
		this.rawData = ((input-1) << 8) | source;
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
	
	@Override
	public String toString() {
		return "[elid=" + elementId + 
		",cfgId=" + itemId + 
		",data=" + Conversion.toHexIntString(rawData) + "]";
	}
	
}

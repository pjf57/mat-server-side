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
	
	public ConfigItem(int elementId, int itemId, int rawData) {
		this.elementId = elementId;
		this.rawData = rawData;
		this.itemId = itemId;
	}

	public ConfigItem(int elementId, int itemId, float data) {
		this.elementId = elementId;
		this.rawData = Float.floatToIntBits(data);
		this.itemId = itemId;
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

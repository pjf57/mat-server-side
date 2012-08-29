package com.pjf.mat.api;

/**
 * Class to hold one Attribute enum value
 * 
 * Equality is based solely on the name of the enum value
 * Natural order is given by the order value
 * 
 * @author pjf
 *
 */
public class EnumValue implements Comparable<EnumValue> {
	private final String name;
	private final int value;
	private final int order;			// display order
	private final String description;
	
	public EnumValue(String name, int value, int order, String description) {
		this.name = name;
		this.value = value;
		this.order = order;
		this.description = description;
	}

	public EnumValue(String name, int value, int order) {
		this.name = name;
		this.value = value;
		this.order = order;
		this.description = "";
	}

	public String getName() {
		return name;
	}

	public int getValue() {
		return value;
	}

	public int getOrder() {
		return order;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EnumValue other = (EnumValue) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public int compareTo(EnumValue other) {
		return order - other.getOrder();
	}
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer(name);
		buf.append("="); buf.append(value);
		buf.append(","); buf.append(order);
		buf.append(","); buf.append(description);
		return buf.toString();
	}
	
}

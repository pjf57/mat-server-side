package com.cs.fwk.core.impl.element;

import com.cs.fwk.api.Item;

public class BasicItem implements Item {
	private final int id;
	
	public BasicItem(int id) {
		this.id = id;
	}

	@Override
	public int getId() {
		return id;
	}
	
	@Override
	public int compareTo(Item other) {
		return this.getId() - other.getId();
	}
	

}

package com.pjf.mat.api;

import java.util.Collection;

public interface Status {
	public String getBaseState();
	public String getRunState();
	public int getRawRunState();
	public long getEventInCount();
	public Collection<Attribute> getAttributes();
}

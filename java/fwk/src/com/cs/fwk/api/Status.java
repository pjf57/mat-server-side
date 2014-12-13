package com.cs.fwk.api;

import java.util.Collection;

public interface Status {
	public String getBaseState();
	public String getRunState();
	public int getRawRunState();
	public long getEventInCount();
	public Collection<Attribute> getAttributes() throws Exception;
	public boolean isInConfigState();
	public boolean isInRunState();
	public ErrorState getErrorState();
	
	public final String INIT = "INIT";
	public final String CFG = "CFG";
	public final String RST = "RST";
	public final String RUN = "RUN";
	public final String UNKNOWN = "unknown";

}

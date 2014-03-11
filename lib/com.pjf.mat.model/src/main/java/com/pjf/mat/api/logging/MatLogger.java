package com.pjf.mat.api.logging;

public interface MatLogger {

	public void info(String message);
	public void debug(String message);
	public void error(Exception ex);
	
}

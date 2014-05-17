package com.cs.fwk.sim.model;

public enum LookupValidity {
	TIMEOUT, 	// this element doesnt produce this data
	NODATA, 	// this element produces this data, but doesnt have any at the moment
	OK			// data returned ok
}

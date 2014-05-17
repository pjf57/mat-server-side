package com.cs.fwk.api.util;

import java.util.List;

import com.cs.fwk.api.Attribute;

public interface AttrConfigGenerator {
	
	List<ConfigItem> generate(Attribute attr) throws Exception;

}

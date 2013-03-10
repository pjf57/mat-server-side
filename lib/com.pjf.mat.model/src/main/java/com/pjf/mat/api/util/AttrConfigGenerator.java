package com.pjf.mat.api.util;

import java.util.List;

import com.pjf.mat.api.Attribute;

public interface AttrConfigGenerator {
	
	List<ConfigItem> generate(Attribute attr);

}

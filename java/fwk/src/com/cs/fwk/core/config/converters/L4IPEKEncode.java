package com.cs.fwk.core.config.converters;

import java.util.ArrayList;
import java.util.List;

import com.cs.fwk.api.AttrSysType;
import com.cs.fwk.api.Attribute;
import com.cs.fwk.api.MatElementDefs;
import com.cs.fwk.api.gridattr.GridAttribute;
import com.cs.fwk.api.gridattr.GridRowData;
import com.cs.fwk.api.util.AttrConfigGenerator;
import com.cs.fwk.api.util.ConfigItem;

/**
 * Encode config for a grid based instrument specific constant list
 * 
 * Format of grid is 0:instr-id 1:value1 2:value2
 * Configs created are EL_L4IP_C_K1, and EL_L4IP_C_K2.
 * 
 * @author pjf
 *
 */
public class L4IPEKEncode implements AttrConfigGenerator {

	
	@Override
	public List<ConfigItem> generate(Attribute attr) throws Exception {
		List<ConfigItem> configs = new ArrayList<ConfigItem>();
		int elId = attr.getParent().getId();
		GridAttribute ga = (GridAttribute) attr;

		int rows = ga.getNumRows();
		for (int row=0; row<rows; row++) {			
			GridRowData rd = ga.getRow(row);
			int instrId = rd.getInt(0);
			int value1 = rd.getInt(1);
			int value2 = rd.getInt(2);
			ConfigItem cfg = new ConfigItem(elId,AttrSysType.NORMAL,MatElementDefs.EL_L4IP_C_K1, instrId, value1);
			configs.add(cfg);
			cfg = new ConfigItem(elId,AttrSysType.NORMAL,MatElementDefs.EL_L4IP_C_K2, instrId, value2);
			configs.add(cfg);
		}

		return configs;
	}

}

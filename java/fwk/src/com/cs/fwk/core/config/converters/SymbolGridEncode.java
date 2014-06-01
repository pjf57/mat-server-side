package com.cs.fwk.core.config.converters;

import java.util.ArrayList;
import java.util.List;

import com.cs.fwk.api.AttrSysType;
import com.cs.fwk.api.Attribute;
import com.cs.fwk.api.MatElementDefs;
import com.cs.fwk.api.util.AttrConfigGenerator;
import com.cs.fwk.api.util.ConfigItem;
import com.cs.fwk.util.attr.GridAttribute;
import com.cs.fwk.util.attr.GridRowData;

/**
 * Encode config for a grid based symbol list as a number of config items
 * 
 * Format of grid is symbol:string, instr_id:int
 * Configs created are SYM_L, SYM_R, and id.
 * 
 * @author pjf
 *
 */
public class SymbolGridEncode implements AttrConfigGenerator {

	@Override
	public List<ConfigItem> generate(Attribute attr) throws Exception {
		List<ConfigItem> configs = new ArrayList<ConfigItem>();
		GridAttribute ga = (GridAttribute) attr;
		int rows = ga.getNumRows();
		for (int row=0; row<rows; row++) {
			GridRowData rd = ga.getRow(row);
			String symName = rd.get(0) + "        ";
			symName = symName.substring(0,8);
			String idStr = rd.get(1);
			int symId = Integer.parseInt(idStr);
			int elId = attr.getParent().getId();
			ConfigItem cfg1 = new ConfigItem(elId,AttrSysType.NORMAL,
					MatElementDefs.EL_MDF_C_ISYM_L,	symName.substring(0,4));
			ConfigItem cfg2 = new ConfigItem(elId,AttrSysType.NORMAL,
					MatElementDefs.EL_MDF_C_ISYM_R,	symName.substring(4,8));
			ConfigItem cfg3 = new ConfigItem(elId,AttrSysType.NORMAL,
					MatElementDefs.EL_MDF_C_ISYM_ID, symId);
			configs.add(cfg1);
			configs.add(cfg2);
			configs.add(cfg3);	
		}
		return configs;
	}

}

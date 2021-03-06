package com.cs.fwk.core.config.converters;

import java.util.ArrayList;
import java.util.List;

import com.cs.fwk.api.AttrSysType;
import com.cs.fwk.api.Attribute;
import com.cs.fwk.api.MatElementDefs;
import com.cs.fwk.api.util.AttrConfigGenerator;
import com.cs.fwk.api.util.ConfigItem;

/**
 * Encode config for a symbol with ID as a number of config items
 * 
 * Format of config string is SYM1:instrID1,SYM2:instrID2 ...
 * Configs created are SYM_L, SYM_R, and id.
 * 
 * @author pjf
 *
 */
public class SymbolEncode implements AttrConfigGenerator {

	@Override
	public List<ConfigItem> generate(Attribute attr) throws Exception {
		List<ConfigItem> configs = new ArrayList<ConfigItem>();
		String val = attr.getValue();
		if (!val.isEmpty()) {
			String[] symbols = val.split(",");
			for (String sym : symbols) {
				String[] args = sym.split(":");
				if (args.length != 2) {				
					throw new Exception("Bad value for config item: " + attr.getName() + " on CB " + attr.getParent().getShortName());
				}
				String symName = args[0];
				String idStr = args[1];
				symName = symName + "        ";
				symName = symName.substring(0,8);
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
		}
		return configs;
	}

}

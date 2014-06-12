package com.cs.fwk.core.config.converters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cs.fwk.api.AttrSysType;
import com.cs.fwk.api.Attribute;
import com.cs.fwk.api.MatElementDefs;
import com.cs.fwk.api.gridattr.GridAttribute;
import com.cs.fwk.api.gridattr.GridRowData;
import com.cs.fwk.api.util.AttrConfigGenerator;
import com.cs.fwk.api.util.ConfigItem;

/**
 * Encode config for a grid based Synthetic Instrument list
 * 
 * Format of grid is xxx
 * Configs created arexxx.
 * 
 * @author pjf
 *
 */
public class SibEncode implements AttrConfigGenerator {
			
	@Override
	public List<ConfigItem> generate(Attribute attr) throws Exception {
		List<ConfigItem> configs = new ArrayList<ConfigItem>();
		GridAttribute ga = (GridAttribute) attr;
		int elId = attr.getParent().getId();
		
		// Start by inverting the grid so that instead of having rows of SI specs,
		// we get rows of leg instr ID specs
		
		Map<Integer,List<Integer>> legMap = new HashMap<Integer,List<Integer>>();	// key = leg instr id, data = si
		int rows = ga.getNumRows();
		for (int row=0; row<rows; row++) {			
			GridRowData rd = ga.getRow(row);
			Integer si = new Integer(rd.get(0));
			for (int slot=1; slot<=4; slot++) {
				String legInstrStr = rd.get(slot);
				if (legInstrStr.isEmpty()) {
					break;
				}
				Integer legInstr = new Integer(legInstrStr);
				List<Integer> entry = legMap.get(legInstr);
				if (entry == null) {
					// create new entry for this leg instrument id
					entry = new ArrayList<Integer>();
					entry.add(si);
					legMap.put(legInstr, entry);
				} else {
					// add to existing entry
					entry.add(si);
				}
			}
		}	
		
		// Now go through the leg index systematically and produce configs

		for (Integer legInstr : legMap.keySet()) {
			List<Integer> entry = legMap.get(legInstr);
			int slot = entry.size() - 1;
			int i = 0;
			while (slot >= 0) {
				Integer si = entry.get(i);
				int data = (slot * 256) + si;
				ConfigItem cfg = new ConfigItem(elId,AttrSysType.NORMAL,
						MatElementDefs.EL_SIB_C_LEG_DATA, legInstr, data);
				configs.add(cfg);
				i++;
				slot--;
			}
		}

		return configs;
	}

}

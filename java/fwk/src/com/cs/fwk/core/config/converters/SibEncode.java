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
 * Format of grid is 0:si 1:leg0, 2:leg1, 3:leg2, 4:leg3
 * Configs created are EL_SIB_C_LEG_DATA.
 * 
 * @author pjf
 *
 */
public class SibEncode implements AttrConfigGenerator {

	/**
	 * Inner class to hold spec for an underlying
	 */
	class ULSpec {
		private final int ulInstrId;
		private final int sid;
		private final int legNum; // 0..N
		
		public ULSpec(int ulInstrId, int sid, int legNum) {
			super();
			this.ulInstrId = ulInstrId;
			this.sid = sid;
			this.legNum = legNum;
		}

		public int getUlInstrId() {
			return ulInstrId;
		}

		public int getSid() {
			return sid;
		}

		public int getLegNum() {
			return legNum;
		}

		@Override
		public String toString() {
			return "ULSpec [ulInstrId=" + ulInstrId + ", sid=" + sid
					+ ", legNum=" + legNum + "]";
		}
		
		
	}
	
	@Override
	public List<ConfigItem> generate(Attribute attr) throws Exception {
		List<ConfigItem> configs = new ArrayList<ConfigItem>();
		GridAttribute ga = (GridAttribute) attr;
		int elId = attr.getParent().getId();
		
		// Start by inverting the grid so that instead of having rows of SI specs,
		// we get rows of leg instr ID (underlyings)
		// each underlying has a list of leg.si to which it belongs
		
		Map<Integer,List<ULSpec>> legMap = new HashMap<Integer,List<ULSpec>>();	// key = leg instr id, data = one UL spec
		int rows = ga.getNumRows();
		for (int row=0; row<rows; row++) {			
			GridRowData rd = ga.getRow(row);
			Integer si = new Integer(rd.get(0));
			for (int slot=1; slot<=4; slot++) {
				String legInstrStr = rd.get(slot);
				if (legInstrStr.isEmpty()) {
					break;
				}
				int legInstr = Integer.parseInt(legInstrStr);
				int legNum = slot-1;
				ULSpec uls = new ULSpec(legInstr,si,legNum);
				List<ULSpec> entry = legMap.get(legInstr);
				if (entry == null) {
					// create new entry for this leg instrument id
					entry = new ArrayList<ULSpec>();
					entry.add(uls);
					legMap.put(legInstr, entry);
				} else {
					// add to existing entry
					entry.add(uls);
				}
			}
		}	
		
		// Now go through the leg index systematically and produce configs

		for (Integer legInstr : legMap.keySet()) {
			List<ULSpec> entry = legMap.get(legInstr);
			int slot = entry.size() - 1;
			int i = 0;
			while (slot >= 0) {
				ULSpec uls = entry.get(i);
				int data = (slot * 4096) + (uls.getLegNum() * 256) + uls.getSid();
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

package com.cs.fwk.core.config.converters;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

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
 * Format of grid is 0:target 1:function, 2:arg, 3:event
 * Configs created are EL_LKU_C_CHN_SPEC.
 * 
 *--				C_CHN_SPEC	= CB.FN.ARG.[TY.EV.IP]
 *--					CB  = id of CB to address, or FF (8 bit)
 *--					FN  = id of LKU or TD fn (8 bit)
 *--					ARG = arg to provide to LKU fn (8 bit) or Tickdata bitselect (0:low 32 bits, 1: high 32 bits)
 *--					TY  = 00:Tickdata, 01:LKU (2 bit)
 *--					EV  = TF - sens to ip=True,False, or both. (2 bit)
 *--					IP  = which input to apply fn to (4 bit = 0,1,2,3)
 * @author pjf
 *
 */
public class LKUEncodeA implements AttrConfigGenerator {
	private final static Logger logger = Logger.getLogger(LKUEncodeA.class);

	
	@Override
	public List<ConfigItem> generate(Attribute attr) throws Exception {
		List<ConfigItem> configs = new ArrayList<ConfigItem>();
		GridAttribute ga = (GridAttribute) attr;
		int elId = attr.getParent().getId();
		
		// build config item for each row in the grid
		
		int rows = ga.getNumRows();
		for (int row=0; row<rows; row++) {	
			GridRowData rd = ga.getRow(row);
			logger.info("generate() - row: " + row + " is " + rd);
			// get data from the row
			int event = rd.getInt(0);
			int fnVal = rd.decodeInt(1);
			// function has arg.type.fn (each 8 bits).
			int type = (fnVal >> 8) & 0xff;
			int argv = (fnVal >> 16) & 0xff;
			int arg = rd.getInt(2,0) + argv;
			int target = rd.getInt(3,0);
			int fn = fnVal & 0xff;
			int channel = row+1; // 1..4
			// encode into the 32 bit config value
			if (target == 0) {
				target = MatElementDefs.EL_ID_ALL;
			}
			channel--;	// 0..3
			int cfgData = (target << 24) + (fn << 16) + (arg << 8) + (type << 6) + (event << 4) + channel;
			ConfigItem cfg = new ConfigItem(elId,AttrSysType.NORMAL,
						MatElementDefs.EL_LKU_C_CHN_SPEC, 0, cfgData);
			logger.info("generate() - cfg is " + cfg);
			configs.add(cfg);
		}

		return configs;
	}

}

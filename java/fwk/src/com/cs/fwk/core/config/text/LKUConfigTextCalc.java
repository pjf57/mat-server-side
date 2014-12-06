package com.cs.fwk.core.config.text;


import com.cs.fwk.api.Element;
import com.cs.fwk.api.MatElementDefs;
import com.cs.fwk.api.gridattr.GridAttribute;
import com.cs.fwk.api.gridattr.GridRowData;
import com.cs.fwk.api.util.CBConfigText;
import com.cs.fwk.api.util.ConfigTextCalcInt;

public class LKUConfigTextCalc implements ConfigTextCalcInt{

	@Override
	public CBConfigText calculate(Element cb) throws Exception {
		CBConfigText ct = new CBConfigText(cb);
		
		GridAttribute ga = cb.getGridAttribute("Functions");
		
		// build one line for each row in the grid
		
		int rows = ga.getNumRows();
		for (int row=0; row<rows; row++) {	
			GridRowData rd = ga.getRow(row);
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
			if (target == 0) {
				target = MatElementDefs.EL_ID_ALL;
			}
			StringBuffer line = new StringBuffer();
			line.append("Ip" + channel + ",");
			if (event != 0) {
				switch(event) {
				case 1:	line.append("F: ");	break;
				case 2:	line.append("T: ");	break;
				case 3:	line.append("A: ");	break;
				}
				line.append(getFn(type,fn,arg));
				if (target != MatElementDefs.EL_ID_ALL) {
					line.append(" ->CB" + target);
				}
				ct.addLine(line.toString());
			}
		}
		return ct;
	}

	/**
	 * Convert type, fn, arg to function string
	 * 
	 * @param type
	 * @param fn
	 * @param arg
	 * @return
	 */
	private String getFn(int type, int fn, int arg) {
		String ret = "???";
		switch(type) {
		case 0:
			// tickdata
			switch(fn) {
			case MatElementDefs.TDS_VOL_PRICE_SP: ret = (arg == 0) ? "price" : "vol";	break;
			}
			break;
			
		case 1:
			// lookup
			ret = MatElementDefs.LkuOpToString(fn).trim();
			break;
		}
		return ret;
	}

}

package com.cs.fwk.core.config.text;


import com.cs.fwk.api.Element;
import com.cs.fwk.api.gridattr.GridAttribute;
import com.cs.fwk.api.gridattr.GridRowData;
import com.cs.fwk.api.util.CBConfigText;
import com.cs.fwk.api.util.ConfigTextCalcInt;

public class MFDSymConfigTextCalc implements ConfigTextCalcInt{

	@Override
	public CBConfigText calculate(Element cb) throws Exception {
		CBConfigText ct = new CBConfigText(cb);
		
		GridAttribute ga = cb.getGridAttribute("symbols");
		
		// build one line for each row in the grid
		
		int rows = ga.getNumRows();
		for (int row=0; row<rows; row++) {	
			GridRowData rd = ga.getRow(row);
			// get data from the row
			String symName = rd.get(0) + "        ";
			symName = symName.substring(0,8);
			int id = rd.getInt(1);
			ct.addLine(symName + " : " + id);
		}
		return ct;
	}


}

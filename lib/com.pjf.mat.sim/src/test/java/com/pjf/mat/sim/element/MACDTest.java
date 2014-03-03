package com.pjf.mat.sim.element;


import com.pjf.mat.api.AttrSysType;
import com.pjf.mat.api.MatElementDefs;
import com.pjf.mat.sim.types.Event;
import com.pjf.mat.test.util.SimTestCase;
import com.pjf.mat.api.util.ConfigItem;


public class MACDTest extends SimTestCase {

	public void testAll() throws Exception {
		MACD el = new MACD(6,this);
		el.putConfig(new ConfigItem(6, AttrSysType.NORMAL, MatElementDefs.EL_MACD_C_FS_ALPHA, 0.5f));
		el.putConfig(new ConfigItem(6, AttrSysType.NORMAL, MatElementDefs.EL_MACD_C_FS_LEN, 3));
		el.putConfig(new ConfigItem(6, AttrSysType.NORMAL, MatElementDefs.EL_MACD_C_SL_ALPHA, 0.4f));
		el.putConfig(new ConfigItem(6, AttrSysType.NORMAL, MatElementDefs.EL_MACD_C_SL_LEN, 4));
		el.putConfig(new ConfigItem(6, AttrSysType.NORMAL, MatElementDefs.EL_MACD_C_SG_ALPHA, 0.5f));
		el.putConfig(new ConfigItem(6, AttrSysType.NORMAL, MatElementDefs.EL_MACD_C_SG_LEN, 3));
		el.putConfig(new ConfigItem(6, MatElementDefs.EL_C_SRC_ROUTE,2,1));
		el.putConfig(new ConfigItem(6, AttrSysType.SYSTEM, MatElementDefs.EL_C_CFG_DONE,0));
		
		el.putEvent(new Event(getTimestamp(1L), 2, 12, 1, 1f));
		el.putEvent(new Event(getTimestamp(2L), 2, 12, 2, 2f));
		el.putEvent(new Event(getTimestamp(3L), 2, 12, 3, 3f));
		el.putEvent(new Event(getTimestamp(4L), 2, 12, 4, 4f));
		el.putEvent(new Event(getTimestamp(5L), 2, 12, 5, 5f));
		el.putEvent(new Event(getTimestamp(6L), 2, 12, 6, 6f));
		el.putEvent(new Event(getTimestamp(7L), 2, 12, 7, 7f));
		el.putEvent(new Event(getTimestamp(8L), 2, 12, 8, 8f));
		el.putEvent(new Event(getTimestamp(9L), 2, 12, 9, 9f));
	}

}

package com.pjf.mat.sim.element;


import com.pjf.mat.api.AttrSysType;
import com.pjf.mat.api.MatElementDefs;
import com.pjf.mat.api.Timestamp;
import com.pjf.mat.sim.types.ConfigItem;
import com.pjf.mat.sim.types.Event;
import com.pjf.mat.test.util.SimTestCase;


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
		
		el.putEvent(new Event(new Timestamp(1L), 2, 12, 1f));
		el.putEvent(new Event(new Timestamp(2L), 2, 12, 2f));
		el.putEvent(new Event(new Timestamp(3L), 2, 12, 3f));
		el.putEvent(new Event(new Timestamp(4L), 2, 12, 4f));
		el.putEvent(new Event(new Timestamp(5L), 2, 12, 5f));
		el.putEvent(new Event(new Timestamp(6L), 2, 12, 6f));
		el.putEvent(new Event(new Timestamp(7L), 2, 12, 7f));
		el.putEvent(new Event(new Timestamp(8L), 2, 12, 8f));
		el.putEvent(new Event(new Timestamp(9L), 2, 12, 9f));
	}

}

package com.cs.fwk.sim.test.cb;


import com.cs.fwk.api.AttrSysType;
import com.cs.fwk.api.MatElementDefs;
import com.cs.fwk.sim.cbs.EMA;
import com.cs.fwk.sim.test.util.SimTestCase;
import com.cs.fwk.sim.types.Event;
import com.cs.fwk.api.util.ConfigItem;


public class EMATest extends SimTestCase {

	public void testAll() throws Exception {
		EMA el = new EMA(2,this);
		el.putConfig(new ConfigItem(2, AttrSysType.NORMAL, MatElementDefs.EL_EMA_C_ALPHA, 0.5f));
		el.putConfig(new ConfigItem(2, AttrSysType.NORMAL, MatElementDefs.EL_EMA_C_LEN, 3));
		el.putConfig(new ConfigItem(2, MatElementDefs.EL_C_SRC_ROUTE,2,1));
		el.putConfig(new ConfigItem(2, AttrSysType.SYSTEM, MatElementDefs.EL_C_CFG_DONE,0));

		float data = 50.0f;
		for (int i=1; i<10; i++) {
			el.putEvent(new Event(getTimestamp(1L), 2, 12, 2, data));
			data += 0.25;
		}
	}

}

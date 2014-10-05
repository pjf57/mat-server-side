package com.cs.fwk.sim.test.cb;

import com.cs.fwk.api.AttrSysType;
import com.cs.fwk.api.MatElementDefs;
import com.cs.fwk.sim.cbs.ATR;
import com.cs.fwk.sim.model.LookupResult;
import com.cs.fwk.sim.test.util.SimTestCase;
import com.cs.fwk.sim.types.Event;
import com.cs.fwk.sim.types.FloatValue;
import com.cs.fwk.api.util.ConfigItem;


public class ATRTest extends SimTestCase {
	float lkuData[];

	public void testOutput() throws Exception {
		ATR el = new ATR(6,this);
		el.putConfig(new ConfigItem(6, AttrSysType.NORMAL, MatElementDefs.EL_ATR_C_ALPHA, 0.666666f));
		el.putConfig(new ConfigItem(6, AttrSysType.NORMAL, MatElementDefs.EL_ATR_C_LEN, 2));
		el.putConfig(new ConfigItem(6, AttrSysType.NORMAL, MatElementDefs.EL_ATR_C_IP_CN1, 0));
		el.putConfig(new ConfigItem(6, MatElementDefs.EL_C_SRC_ROUTE,2,1));
		el.putConfig(new ConfigItem(6, AttrSysType.SYSTEM, MatElementDefs.EL_C_CFG_DONE,0));
		
		lkuData = new float[]{3f,2f,0.5f}; // T3: C(n-1)=3, H(n)=2, L(n)=0.5
		el.putEvent(new Event(getTimestamp(1L), 2, 12, 2, 2f));
		
		lkuData = new float[]{1f,2f,0.5f}; // T4: C(n-1)=1, H(n)=2, L(n)=0.5)
		el.putEvent(new Event(getTimestamp(2L), 2, 12, 2, 2f));

		lkuData = new float[]{0.1f,2f,0.5f}; // T5: C(n-1)=0.1, H(n)=2, L(n)=0.5)
		el.putEvent(new Event(getTimestamp(3L), 2, 12, 2, 2f));
	}

	@Override
	public LookupResult lookupBehaviour(int id, int instrumentId, int arg, int tickref, int lookupKey)
			throws Exception {
		int idx;
		switch(lookupKey) {
		case MatElementDefs.EL_HLOC_L_PRVM1_C:  idx = 0;	break;
		case MatElementDefs.EL_HLOC_L_PREV_H: 	idx = 1;	break;
		case MatElementDefs.EL_HLOC_L_PREV_L:  	idx = 2;	break;
		default: throw new Exception ("Lookup key not implemented");
		}
		LookupResult ret = new LookupResult(3,new FloatValue(lkuData[idx]),3);
		return ret;
	}

}

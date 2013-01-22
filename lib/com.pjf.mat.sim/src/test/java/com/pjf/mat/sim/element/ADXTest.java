package com.pjf.mat.sim.element;

import com.pjf.mat.api.AttrSysType;
import com.pjf.mat.api.MatElementDefs;
import com.pjf.mat.api.Timestamp;
import com.pjf.mat.sim.model.LookupResult;
import com.pjf.mat.sim.types.ConfigItem;
import com.pjf.mat.sim.types.Event;
import com.pjf.mat.sim.types.FloatValue;
import com.pjf.mat.test.util.SimTestCase;


public class ADXTest extends SimTestCase {
	float lkuData[];

	public void testOutput() throws Exception {
		ADX el = new ADX(6,this);
		el.putConfig(new ConfigItem(6, AttrSysType.NORMAL, MatElementDefs.EL_ADX_C_PDN_ALPHA, 0.666666f));
		el.putConfig(new ConfigItem(6, AttrSysType.NORMAL, MatElementDefs.EL_ADX_C_PDN_LEN, 2));
		el.putConfig(new ConfigItem(6, AttrSysType.NORMAL, MatElementDefs.EL_ADX_C_NDN_ALPHA, 0.666666f));
		el.putConfig(new ConfigItem(6, AttrSysType.NORMAL, MatElementDefs.EL_ADX_C_NDN_LEN, 2));
		el.putConfig(new ConfigItem(6, AttrSysType.NORMAL, MatElementDefs.EL_ADX_C_ADX_ALPHA, 0.5f));
		el.putConfig(new ConfigItem(6, AttrSysType.NORMAL, MatElementDefs.EL_ADX_C_ADX_LEN, 3));
		el.putConfig(new ConfigItem(6, MatElementDefs.EL_C_SRC_ROUTE,2,1));
		el.putConfig(new ConfigItem(6, AttrSysType.SYSTEM, MatElementDefs.EL_C_CFG_DONE,0));

		lkuData = new float[]{10,8,9,7,2};
		el.putEvent(new Event(new Timestamp(1L), 2, 12, 1, 2f));
		
		lkuData = new float[]{11,10,10,9,3};
		el.putEvent(new Event(new Timestamp(2L), 2, 12, 2, 2f));

		lkuData = new float[]{9,11,8,10,2};
		el.putEvent(new Event(new Timestamp(3L), 2, 12, 2, 2f));

		lkuData = new float[]{12,11,10,10,3};
		el.putEvent(new Event(new Timestamp(4L), 2, 12, 2, 2f));

		lkuData = new float[]{13,12,11,10,3};
		el.putEvent(new Event(new Timestamp(5L), 2, 12, 2, 2f));

		lkuData = new float[]{14,13,11,11,2};
		el.putEvent(new Event(new Timestamp(6L), 2, 12, 2, 2f));

		lkuData = new float[]{16,14,12,11,3};
		el.putEvent(new Event(new Timestamp(7L), 2, 12, 2, 2f));

		lkuData = new float[]{18,16,14,12,3};
		el.putEvent(new Event(new Timestamp(8L), 2, 12, 2, 2f));
	}

	@Override
	public LookupResult lookupBehaviour(int id, int instrumentId, int tickref, int lookupKey)
			throws Exception {
		int idx;
		switch(lookupKey) {
		case MatElementDefs.EL_HLOC_L_PREV_H:  idx = 0;	break;
		case MatElementDefs.EL_HLOC_L_PRVM1_H: idx = 1;	break;
		case MatElementDefs.EL_HLOC_L_PREV_L:  idx = 2;	break;
		case MatElementDefs.EL_HLOC_L_PRVM1_L: idx = 3;	break;
		case MatElementDefs.EL_ATR_L_ATR:      idx = 4;	break;
		default: throw new Exception ("Lookup key not implemented");
		}
		LookupResult ret = new LookupResult(3,new FloatValue(lkuData[idx]),3);
		return ret;
	}


}

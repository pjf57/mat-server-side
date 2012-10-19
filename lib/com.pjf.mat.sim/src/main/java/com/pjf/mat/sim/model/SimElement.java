package com.pjf.mat.sim.model;

import com.pjf.mat.api.Cmd;
import com.pjf.mat.sim.types.ConfigItem;
import com.pjf.mat.sim.types.Event;

public interface SimElement {
	public boolean putEvent(Event evt) throws Exception; // return true if took the event
	public void putConfig(ConfigItem cfg) throws Exception;
	public void putCmd(Cmd cmd);
	public void processTick(ClockTick tick);
	public void getStatus();
	public void shutdown();
	public LookupResult handleLookup(int instrumentId, int lookupKey, int target) throws Exception;
	public int getId();
}

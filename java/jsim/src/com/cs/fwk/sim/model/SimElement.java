package com.cs.fwk.sim.model;

import com.cs.fwk.sim.types.Event;
import com.cs.fwk.api.Cmd;
import com.cs.fwk.api.util.ConfigItem;

public interface SimElement {
	public boolean putEvent(Event evt) throws Exception; // return true if took the event
	public void putConfig(ConfigItem cfg) throws Exception;
	public void putCmd(Cmd cmd);
	public void processTick(ClockTick tick);
	public void getStatus();
	public void shutdown();
	public LookupResult handleLookup(int instrumentId, int arg, int tickref, int lookupKey, int target) throws Exception;
	public int getId();
	public TickdataResult handleTickdata(int tickref, int tickdataKey) throws Exception;
	
	/**
	 * @return true if the element is in an error state
	 */
	public boolean isInError();

	/**
	 * @return true if the element is in config state
	 */
	public boolean isInConfig();	
	/**
	 * @return true if the element is initialised
	 */
	public boolean isInitialised();

}

package com.cs.fwk.sim.bricks;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.cs.fwk.api.MatElementDefs;
import com.cs.fwk.api.util.ConfigItem;
import com.cs.fwk.sim.model.SimHost;
import com.cs.fwk.sim.types.Event;
import com.cs.fwk.util.Conversion;

/**
 * Emulates cc_sib_core - a core brick for a Synthetic Instrument Builder
 * 
 * Processes events and if they are on configured underlyings, calls a template
 * method to process the event, passing the synthetic instrument id and all the
 * underlying data.
 * 
 * @author pjf
 *
 */
public abstract class SIBCore extends BaseElement {
	private final static int MAX_SLOTS = 6;	// max # slots for config

	private final static Logger logger = Logger.getLogger(SIBCore.class);
	private Map<Integer,SlotConfig[]> i2sConfig; // map from underlying instr id to slots
	private Map<Integer,Integer[]> s2lConfig; // map from synth instr to leg instr_id
	private Map<Integer,SynthLegData> iStore[];	// map from instr id to instr data
	
	/**
	 * Class to hold one slot of config data for a leg of a synth instrument
	 */
	class SlotConfig {
		private int instrId;
		private int legNum;
		private int synthInstrId;
		
		public SlotConfig(int synthInstrId, int legNum, int instrId) {
			this.instrId = instrId;
			this.legNum = legNum;
			this.synthInstrId = synthInstrId;
		}

		public int getInstrId() {
			return instrId;
		}

		public int getLegNum() {
			return legNum;
		}

		public int getSynthInstrId() {
			return synthInstrId;
		}

		@Override
		public String toString() {
			return "[instrId=" + instrId + ", legNum=" + legNum
					+ ", synthInstrId=" + synthInstrId + "]";
		}
		
	}

	@SuppressWarnings("unchecked")
	public SIBCore(int id, int hwType, SimHost host) {
		super(id, hwType, host);
		i2sConfig = new HashMap<Integer,SlotConfig[]>();
		s2lConfig = new HashMap<Integer,Integer[]>();
		iStore = new Map[MAX_SLOTS];
		for (int i=0; i<MAX_SLOTS; i++) {
			iStore[i] = new HashMap<Integer,SynthLegData>();
		}
	}
	
	@Override
	protected void processEvent(int input, Event evt) {
		logger.info("processEvent(): " + evt);
		// store data point for this underlying
		Integer lid = new Integer(evt.getInstrument_id());
		for (int i=0; i<MAX_SLOTS; i++) {
			iStore[i].put(lid, new SynthLegData(evt.getInstrument_id(), evt.getTickref(), evt.getRawData()));
		}
		// get config for this underlying
		SlotConfig[] slots = i2sConfig.get(lid);
		// check all slots
		for (int slotId=0; slotId<MAX_SLOTS; slotId++) {
			SlotConfig slot = slots[slotId];
			if (slot == null) {
				break;
			}
			// collect all the data pertaining to this synthetic instrument event
			int sid = slot.getSynthInstrId();
			int legNum = slot.getLegNum();
			logger.info("processEvent(): slot " + slotId + ", sid=" + sid + " leg=" + legNum);
			// collect the leg data
			SynthLegData[] legData = new SynthLegData[MAX_SLOTS];
			Integer[] legs = s2lConfig.get(new Integer(sid));
			if (legs != null) {
				for (int legId=0; legId<MAX_SLOTS; legId++) {
					if (legs[legId] == null) {
						break;
					}
					int legInstrId = legs[legId];
					Map<Integer,SynthLegData> store = iStore[legId];
					SynthLegData sld = store.get(legInstrId);
					legData[legId] = sld;
				}
			}			
			processSyntheticLegEvent(sid, evt.getTickref(), legNum, legData);			
		}
	}


	@Override
	protected void processConfig(ConfigItem cfg) {
		switch(cfg.getItemId()) {
		case MatElementDefs.EL_SIB_C_LEG_DATA:	processLegConfig(cfg);	break;
		default:								processExtConfig(cfg);
		}
	}
	

	private void processLegConfig(ConfigItem cfg) {
		int legInstrId = cfg.getArg();
		int synthInstrId = cfg.getRawData() & 0xff;
		int legNum = (cfg.getRawData() >> 8) & 0xf;
		int slot = (cfg.getRawData() >> 12) & 0xf;
		
		Integer lid = new Integer(legInstrId);
		SlotConfig[] slots = i2sConfig.get(lid);
		if (slots == null) {
			slots = new SlotConfig[MAX_SLOTS];
		}
		SlotConfig lcfg = new SlotConfig(synthInstrId,legNum,legInstrId);
		slots[slot] = lcfg;
		i2sConfig.put(lid, slots);		
		logger.info("processLegConfig(): cfg=" + cfg + " add legInstr=" + legInstrId + " slot=" + slot +
				" snythInstr=" + synthInstrId + " legNum=" + legNum);

	}
	
	@Override
	protected void processConfigDone() {
		// build s2l config from i2s config
		for (Integer legInstrId:i2sConfig.keySet()) {
			SlotConfig[] slots = i2sConfig.get(legInstrId);
			logger.info("processConfigDone(): legInstrId=" + legInstrId + 
					" slotConfig = " + Conversion.arrayToString(slots));
			for (int slotId=0; slotId<MAX_SLOTS; slotId++) {
				SlotConfig slot = slots[slotId];
				if (slot == null) {
					break;
				}
				Integer sid = slot.getSynthInstrId();
				int legNum = slot.getLegNum();
				// get existing si leg config, or generate new
				Integer[] s2l = s2lConfig.get(sid);
				if (s2l == null) {
					s2l = new Integer[MAX_SLOTS];
				}
				// set instrument id for this leg
				s2l[legNum] = new Integer(slot.getInstrId());
				s2lConfig.put(sid, s2l);
				logger.info("processConfigDone(): add s2lConfig: sid=" + sid + 
						" val=" + Conversion.arrayToString(s2l));
			}
		}
		processExtConfigDone();
	}



	/**
	 * Template method for handling extended configuration
	 * 
	 * @param cfg
	 */
	protected void processExtConfig(ConfigItem cfg) {
		logger.error("processExtConfig() - unexpected config: " + cfg);
	}
	
	/**
	 * Template method for handling extended configuration done
	 */
	protected void processExtConfigDone() {
		logger.debug("processExtConfigDone() - default handling");
	}
	
	
	/**
	 * Template method for handling synthetic events created by an event on an underlying
	 * This method should be overriden by implementing class.
	 * 
	 * @param synthInstrId	- id of the synth instr
	 * @param tickref		- tickref of the underlying event
	 * @param leg			- leg# of the underlying instr that caused the evt (0..N)
	 * @param legData		- current data for each underlying (0..N)
	 */
	protected void processSyntheticLegEvent(int synthInstrId, int tickref, int leg, SynthLegData legData[]) {
		logger.warn("processSyntheticLegEvent() - processing not handeled.");		
	}



}

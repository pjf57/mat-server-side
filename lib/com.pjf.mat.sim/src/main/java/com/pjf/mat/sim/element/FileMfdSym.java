package com.pjf.mat.sim.element;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.pjf.mat.api.Cmd;
import com.pjf.mat.api.MatElementDefs;
import com.pjf.mat.sim.bricks.BaseElement;
import com.pjf.mat.sim.model.BaseState;
import com.pjf.mat.sim.model.ClockTick;
import com.pjf.mat.sim.model.SimElement;
import com.pjf.mat.sim.model.SimHost;
import com.pjf.mat.sim.types.Event;
import com.pjf.mat.sim.types.TickRefData;
import com.pjf.mat.api.util.ConfigItem;
import com.pjf.mat.util.data.DataSource;
import com.pjf.mat.util.data.MarketEventType;
import com.pjf.mat.util.data.TickData;

/**
 * FileMfdSym reads from a file to create tick data with the same logic as cb_raw_mfd_sym.
 * It has additional configuration to set the delivery rate and filepath
 * 
 * The file is a csv file with the following columns:
 * 
 * 
 * @author pjf
 *
 */
public class FileMfdSym extends BaseElement implements SimElement {
	private final static Logger logger = Logger.getLogger(FileMfdSym.class);
	private static final int LATENCY = 0;	// input to output latency (microticks)
	private OpConfig c_trade;				// output handling for trade events
	private OpConfig c_bid;					// output handling for bid events
	private OpConfig c_ask;					// output handling for ask events
	private int c_mktId;					// market identifier
	private int gap;						// # ticks between bursts
	private int burst;					// # ticks in a burst
	private String filepath;				// path to file to read
	private int gapCntr;
	private int lenCntr;
	private boolean started;
	private String symLeft;
	private String symRight;
	private DataSource ds;
	private final Map<String,Integer> c_symbols;	// map of allowed symbols, index by symbol
	
	public enum op_t{PRICE,VOLUME};
	
	private class OpConfig {
		final boolean isOn;
		final op_t type;
		final int port;
		
		OpConfig(int cfg) {
			if ((cfg & 0x10) == 0) {
				type = op_t.PRICE;
			} else {
				type = op_t.VOLUME;
			}
			if ((cfg & 0xf) == 0xf) {
				port = 0;
				isOn = false;
			} else {				
				port = cfg & 0xf;
				isOn = true;
			}
		}

		/**
		 * Construct as "off" config
		 */
		public OpConfig() {
			isOn = false;
			type = op_t.PRICE;
			port = 0;
		}
	}
	
		
	public FileMfdSym(int id, SimHost host) throws Exception {
		super(id, MatElementDefs.EL_TYP_TG1,host);
		started = false;
		filepath = "resources/GLP_27667_1.csv";
		ds = new DataSource(new FileInputStream(filepath));
		gap = 100;
		burst = 1;
		c_symbols = new HashMap<String,Integer>();
	}
	
	public void setFilepath(String fpath) throws FileNotFoundException, Exception {
		filepath = fpath;
		ds = new DataSource(new FileInputStream(filepath));
	}

	public void setBurst(int burst) {
		this.burst = burst;
	}

	public void setGap(int gap) {
		this.gap = gap;
	}

	@Override
	protected void processConfig(ConfigItem cfg) {
		switch (cfg.getItemId()) {
		case MatElementDefs.EL_MDF_C_TRADE	: c_trade = new OpConfig(cfg.getRawData());	break;
		case MatElementDefs.EL_MDF_C_BID	: c_bid = new OpConfig(cfg.getRawData());	break;
		case MatElementDefs.EL_MDF_C_ASK	: c_ask = new OpConfig(cfg.getRawData());	break;
		case MatElementDefs.EL_MDF_C_ISYM_L	: symLeft = cfg.getStringData();			break;
		case MatElementDefs.EL_MDF_C_ISYM_R	: symRight = cfg.getStringData();			break;
		case MatElementDefs.EL_MDF_C_ISYM_ID: putSym(symLeft,symRight,cfg.getRawData()&0xff);	break;
		case MatElementDefs.EL_MDF_C_UDPPORT: /* ignore */								break;
		case MatElementDefs.EL_MDF_C_MKTID	: c_mktId = cfg.getRawData() & 0xff;		break;
		default: logger.warn(getIdStr() + "Unexpected configuration: " + cfg);			break;
		}
	}

	/**
	 * Add symbol to list of allowed symbols
	 * 
	 * @param symLeft - left 4 chars of symbol
	 * @param symRight - right 4 chars of symbol
	 * @param instrId - instrument ID
	 */
	private void putSym(String symLeft, String symRight, int instrId) {
		String sym = symLeft + symRight;
		c_symbols.put(sym, instrId);
		logger.info(getIdStr() + "Adding Symbol [" + sym + "=" + instrId + "]");
	}

	@Override
	protected void processCmd(Cmd cmd) {
		switch (cmd.getConfigId()) {
		case MatElementDefs.EL_MFD_C_START: 
			logger.info(getIdStr() + "Starting, burst=" + burst + ", gap=" + gap);
			gapCntr = gap;
			lenCntr = burst;
			started = true;	
			break;
		default:
			logger.warn(getIdStr() + "Unexpected command: " + cmd);
			break;
		}
	}


	@Override
	public void processTick(ClockTick tick) {
		if (baseState == BaseState.RUN  &&  started) {
			logger.debug("processTick(): gapCntr= " + gapCntr);
			if (started  &&  gapCntr == 0) {
				gapCntr = gap;
				try {
					TickData td = ds.getNext();
					Integer instrId = c_symbols.get(td.symbol);
					if (instrId != null) {
						// this symbol is to be handled
						OpConfig opc = new OpConfig();
						switch (td.evt.get()) {
						case MarketEventType.TRADE 	: opc = c_trade;	break;
						case MarketEventType.BID 	: opc = c_bid;		break;
						case MarketEventType.ASK 	: opc = c_ask;		break;
						}
						if (opc.isOn) {
							// send event
							float data;
							if (opc.type == op_t.PRICE) {
								data = td.price;
							} else {
								data = td.volume;
							}
							int tickref = host.getTickref();
							TickRefData trd = new TickRefData(tickref,c_mktId,instrId,td);
							putTickrefData(tickref,trd);
							Event evt = new Event(host.getCurrentSimTime(),elementId, opc.port, instrId, tickref, data);
							host.publishEvent(evt,LATENCY);							
						}
					}
				} catch (Exception e) {
					logger.info(getIdStr() + "Stopping.");
					started = false;
				}
				lenCntr--;
				if (lenCntr < 0) {
					// end of burst
					gapCntr = gap;
				}
			} else {
				gapCntr--;
			}
		}		
	}



	@Override
	protected String getTypeName() {
		return "FileMFDSym";
	}

}

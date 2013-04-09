package com.pjf.mat.sim.element;

import org.apache.log4j.Logger;
import com.pjf.mat.api.MatElementDefs;
import com.pjf.mat.sim.bricks.BaseElement;
import com.pjf.mat.sim.model.SimElement;
import com.pjf.mat.sim.model.SimHost;
import com.pjf.mat.sim.types.BooleanValue;
import com.pjf.mat.sim.types.Event;
import com.pjf.mat.sim.types.FloatValue;
import com.pjf.mat.api.util.ConfigItem;
import com.pjf.mat.util.Conversion;

/**
 * Implements 4 input logic block
 * 
 *--		implements configuable logic: Z = p lop q, lop = (and/or/nand/nor) or p/q/!p/!q
 *--				p = A cf1 x, x = B|K1, cf1 = (</<=/>/>=/!=)
 *--				q = C cf2 y, y = D|K2, cf2 = (</<=/>/>=/!=)
 *--		where operations are configurable as are K1, K2.
 *--		A,B,C,D are ip0,ip1,ip2,ip3 resectively
 *--		all data is sp fp.
 *--		output z generates an event 1 on transition to true, and an event 0 on transition to false.
 *--
 *--		config is 
 *--					 16   15 12 11  8 7  6 5  4 3 0
 *--		  			--------------------------------
 *--		C_OPS		|1sht| cf1 | cf2 |xsel|ysel|lop|
 *--		  			--------------------------------
 *--
 *--					1sht	   cf          sel       lop
 *--					---        --	       ---		 ---
 *--					0 Follow  0000 EQ     00 IP		0000 p and q
 *--					1 1sht    0001 LT     01 K	   	0001 p or q
 *--				  			  0010 LE				0010 p nand q
 *--			     			  0011 GT				0011 p nor q
 *--				  			  0100 GE				0100 p
 *--				  			  0101 NE				0101 q
 *--													0110 not p
 *--													0111 not q
 *--
 *--		C_K1, C_K2
 *
 * @author pjf
 *
 */
public class L4IP extends BaseElement implements SimElement {
	private final static Logger logger = Logger.getLogger(L4IP.class);
	private static final int LATENCY = 5;	// input to output latency (microticks)
	private int c_cf1;			// p = fn(a,x)
	private int c_cf2;			// q = fn(c,y)
	private int c_xsel;			// x = b or K1
	private int c_ysel;			// y = d or K2
	private int c_lop;			// Z = fn(p,q)
	private boolean c_oneShot;	// event output only if Z changes
	private FloatValue c_k1;
	private FloatValue c_k2;
	private FloatValue[][] lastValue;	// last value on each input [instr][ip 1..4]
	private BooleanValue lastZ;
		
	public L4IP(int id, SimHost host) {
		super(id, MatElementDefs.EL_TYP_LOGIC_4IP,host);
		lastValue = new FloatValue[MatElementDefs.MAX_INSTRUMENTS][4];
		for (int instr=0; instr<MatElementDefs.MAX_INSTRUMENTS; instr++) {
			for (int ip=0; ip<4; ip++) {
				lastValue[instr][ip] = new FloatValue();
			}
		}
		lastZ = new BooleanValue();
		c_k1 = new FloatValue();
		c_k2 = new FloatValue();
	}

	@Override
	protected void processConfig(ConfigItem cfg) {
		switch (cfg.getItemId()) {
		case MatElementDefs.EL_L4IP_C_K1: c_k1.set(cfg.getFloatData());	break;
		case MatElementDefs.EL_L4IP_C_K2: c_k2.set(cfg.getFloatData());	break;
		case MatElementDefs.EL_L4IP_C_OPS: 
			c_lop = cfg.getRawData() & 0xf;
			c_ysel = (cfg.getRawData() >> 4) & 0x3;
			c_xsel = (cfg.getRawData() >> 6) & 0x3;
			c_cf2 = (cfg.getRawData() >> 8) & 0xf;
			c_cf1 = (cfg.getRawData() >> 12) & 0xf;
			if ((cfg.getRawData() & 0x10000) == 0) {
				c_oneShot = false;
			} else {
				c_oneShot = true;
			}
			break;
		default: logger.warn(getIdStr() + "Unexpected configuration: " + cfg); break;
		}
	}

	@Override
	protected void processEvent(int input, Event evt) {
		int instr = evt.getInstrument_id();
		lastValue[instr][input-1].set(evt.getFloatData());
		FloatValue x = (c_xsel == 0) ? lastValue[instr][1] : c_k1;
		FloatValue y = (c_ysel == 0) ? lastValue[instr][3] : c_k2;
		BooleanValue p = fn(c_cf1,lastValue[instr][0],x);
		BooleanValue q = fn(c_cf2,lastValue[instr][2],y);
		BooleanValue z = lop(c_lop,p,q);
		if (z.isValid()){
			if (!c_oneShot || !lastZ.isValid() ||  (lastZ.getValue() != z.getValue())) {
				Event evtOut = new Event(host.getCurrentSimTime(),elementId,instr,
						evt.getTickref(), z.getRawData());
				host.publishEvent(evtOut,LATENCY);
			}
		}
		lastZ = z;
	}

	private BooleanValue lop(int op, BooleanValue p, BooleanValue q) {
		BooleanValue val = new BooleanValue();	// start with invalid return result
		switch (op) {
		case 0x4:	val = p;	break;
		case 0x5:	val = q;	break;
		case 0x6:
			if (p.isValid()) {
				val.set(!p.getValue());
			}
			break;
		case 0x7:
			if (q.isValid()) {
				val.set(!q.getValue());
			}
			break;
		default:
			if (p.isValid()  &&  q.isValid()) {
				boolean cmp = false;;
				switch(op) {
				case 0x0: cmp = p.getValue()  &&  q.getValue();		break;	
				case 0x1: cmp = p.getValue()  ||  q.getValue();		break;	
				case 0x2: cmp = !(p.getValue()  &&  q.getValue());	break;	
				case 0x3: cmp = !(p.getValue()  ||  q.getValue());	break;	
				default:
					logger.warn(getIdStr() + "invalid lop operation: " + Conversion.toHexByteString(op));
					break;
				}
				val.set(cmp);
			}
		}
		return val;
	}

	private BooleanValue fn(int op, FloatValue a, FloatValue x) {
		BooleanValue val = new BooleanValue();	// start with invalid return result
		if (a.isValid()  &&  x.isValid()) {
			boolean cmp = false;
			switch (op) {
			case 0x0: cmp = a.getValue() == x.getValue();	break;	
			case 0x1: cmp = a.getValue() <  x.getValue();	break;	
			case 0x2: cmp = a.getValue() <= x.getValue();	break;	
			case 0x3: cmp = a.getValue() >  x.getValue();	break;	
			case 0x4: cmp = a.getValue() >= x.getValue();	break;	
			case 0x5: cmp = a.getValue() != x.getValue();	break;	
			default:
				logger.warn(getIdStr() + "invalid fn operation: " + Conversion.toHexByteString(op));
				break;
			}
			val.set(cmp);
		}
		return val;
	}

	@Override
	protected String getTypeName() {
		return "L4IP";
	}

}

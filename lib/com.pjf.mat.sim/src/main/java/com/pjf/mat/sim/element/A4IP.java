package com.pjf.mat.sim.element;

import org.apache.log4j.Logger;
import com.pjf.mat.api.MatElementDefs;
import com.pjf.mat.sim.bricks.BaseElement;
import com.pjf.mat.sim.model.SimElement;
import com.pjf.mat.sim.model.SimHost;
import com.pjf.mat.sim.types.ConfigItem;
import com.pjf.mat.sim.types.Event;
import com.pjf.mat.sim.types.FloatValue;
import com.pjf.mat.util.Conversion;

/**
 *--		implements configuable arithmetic: Z = p | q | p lop q, lop = (+ - * /)
 *--				p = A | x | A cf1 x, x = B|K1, cf1 = (+ - * /)
 *--				q = C | Y | C cf2 y, y = D|K2, cf2 = (+ - * /)
 *--		where operations are configurable as are K1, K2.
 *--		A,B,C,D are ip0,ip1,ip2,ip3 resectively
 *--		all data is sp fp.
 *--
 *--		config is 
 *--					 15 12 11  8 7  6 5  4 3 0
 *--		  			---------------------------
 *--		C_OPS		| cf1 | cf2 |xsel|ysel|lop|
 *--		  			---------------------------
 *--
 *--						cf         sel       lop
 *--						--		   ---       ---
 *--					  0000 A,C    00 B,D	0000 p
 *--					  0001 x,y    01 K		0001 q
 *--					  0010 +				0010 p + q
 *--				      0011 -				0011 p - q
 *--					  0100 *				0100 p * q
 *--					  0101 /				0101 p / q
 *--
 *--		C_K1, C_K2
 *
 * @author pjf
 *
 */
public class A4IP extends BaseElement implements SimElement {
	private final static Logger logger = Logger.getLogger(A4IP.class);
	private static final int LATENCY = 4;	// input to output latency (microticks)
	private int c_cf1;			// p = fn(a,x)
	private int c_cf2;			// q = fn(c,y)
	private int c_xsel;			// x = b or K1
	private int c_ysel;			// y = d or K2
	private int c_lop;			// Z = fn(p,q)
	private FloatValue c_k1;
	private FloatValue c_k2;
	private FloatValue[][] lastValue;	// last value on each input [instr][ip 1..4]
		
	public A4IP(int id, SimHost host) {
		super(id, MatElementDefs.EL_TYP_ARITH_4IP,host);
		lastValue = new FloatValue[MatElementDefs.MAX_INSTRUMENTS][5];
		for (int instr=0; instr<MatElementDefs.MAX_INSTRUMENTS; instr++) {
			for (int ip=1; ip<5; ip++) {
				lastValue[instr][ip] = new FloatValue();
			}
		}
		c_k1 = new FloatValue();
		c_k2 = new FloatValue();
	}

	@Override
	protected void processConfig(ConfigItem cfg) {
		switch (cfg.getItemId()) {
		case MatElementDefs.EL_A4IP_C_K1: c_k1.set(cfg.getFloatData());	break;
		case MatElementDefs.EL_A4IP_C_K2: c_k2.set(cfg.getFloatData());	break;
		case MatElementDefs.EL_A4IP_C_OPS: 
			c_lop = cfg.getRawData() & 0xf;
			c_ysel = (cfg.getRawData() / 16) & 0x3;
			c_ysel = (cfg.getRawData() / 64) & 0x3;
			c_cf2 = (cfg.getRawData() / 256) & 0xf;
			c_cf1 = (cfg.getRawData() / 4096) & 0xf;
			break;
		default: logger.warn(getIdStr() + "Unexpected configuration: " + cfg); break;
		}
	}

	@Override
	protected void processEvent(int input, Event evt) {
		int instr = evt.getInstrument_id();
		lastValue[instr][input].set(evt.getFloatData());
		FloatValue x = (c_xsel == 0) ? lastValue[instr][1] : c_k1;
		FloatValue y = (c_ysel == 0) ? lastValue[instr][3] : c_k2;
		FloatValue p = fn(c_cf1,lastValue[instr][0],x);
		FloatValue q = fn(c_cf2,lastValue[instr][2],y);
		FloatValue z = lop(c_lop,p,q);
		if (z.isValid()){ 
			Event evtOut = new Event(elementId,instr,z.getRawData());
			// TODO change to take into account cascaded ops, and make longer latency for divide
			host.publishEvent(evtOut,LATENCY);
		}
	}

	private FloatValue lop(int op, FloatValue p, FloatValue q) {
		FloatValue val = new FloatValue();	// start with invalid return result
		switch (op) {
		case 0x0:	val = p;	break;
		case 0x1:	val = q;	break;
		default:
			if (p.isValid()  &&  q.isValid()) {
				float result = 0.0f;
				switch(op) {
				case 0x2: result = p.getValue() + q.getValue();		break;	
				case 0x3: result = p.getValue() - q.getValue();		break;	
				case 0x4: result = p.getValue() * q.getValue();		break;	
				case 0x5: result = p.getValue() / q.getValue();		break;	
				default:
					logger.warn(getIdStr() + "invalid lop operation: " + Conversion.toHexByteString(op));
					break;
				}
				val.set(result);
			}
		}
		return val;
	}

	private FloatValue fn(int op, FloatValue a, FloatValue x) {
		FloatValue val = new FloatValue();	// start with invalid return result
		switch (op) {
		case 0x0:	val = a;	break;
		case 0x1:	val = x;	break;
		default:
			if (a.isValid()  &&  x.isValid()) {
				float result = 0.0f;
				switch(op) {
				case 0x2: result = a.getValue() + x.getValue();		break;	
				case 0x3: result = a.getValue() - x.getValue();		break;	
				case 0x4: result = a.getValue() * x.getValue();		break;	
				case 0x5: result = a.getValue() / x.getValue();		break;	
				default:
					logger.warn(getIdStr() + "invalid lop operation: " + Conversion.toHexByteString(op));
					break;
				}
				val.set(result);
			}
		}
		return val;
	}

	@Override
	protected String getTypeName() {
		return "A4IP";
	}

}

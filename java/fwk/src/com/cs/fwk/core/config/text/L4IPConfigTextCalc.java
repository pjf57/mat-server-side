package com.cs.fwk.core.config.text;


import com.cs.fwk.api.Element;
import com.cs.fwk.api.util.CBConfigText;
import com.cs.fwk.api.util.ConfigTextCalcInt;
import com.cs.fwk.core.config.calculators.BaseAttributeAccessor;


public class L4IPConfigTextCalc extends BaseAttributeAccessor implements ConfigTextCalcInt{

	@Override
	public CBConfigText calculate(Element cb) throws Exception {
		initialise(cb);
		CBConfigText ct = new CBConfigText(cb);

		int p = getRawVal("P");
		int q = getRawVal("Q");
		int z = getRawVal("Z");
		
		String pval = evalP(p);
		String qval = evalQ(q);
		
		// following logic to determine if pval & qval are compound exprs is a bit dodgy
		String cplxPval = (pval.length()>2) ? "(" + pval + ")" : pval;
		String cplxQval = (qval.length()>2) ? "(" + qval + ")" : qval;
		
		String val = "error";
		
		switch(z) {
		case 0x0000: val = cplxPval + " and " + cplxQval;	break;
		case 0x0001: val = cplxPval + " or " + cplxQval;	break;
		case 0x0002: val = cplxPval + " nand " + cplxQval;	break;
		case 0x0003: val = cplxPval + " nor " + cplxQval;	break;
		case 0x0004: val = pval;							break;
		case 0x0005: val = qval;							break;
		case 0x0006: val = "not " + cplxPval;				break;
		case 0x0007: val = "not " + cplxQval;				break;
		}

		ct.addLine(" Z = "+val);
		return ct;
	}

	private String evalP(int ival) {
		switch(ival) {
			case 0x0000:	return "A==B";
			case 0x1000:	return "A<B";
			case 0x2000:	return "A<=B";
			case 0x3000:	return "A>B";
			case 0x4000:	return "A>=B";
			case 0x5000:	return "A!=B";		
			case 0x1040:	return "A<K1";
			case 0x2040:	return "A<=K1";
			case 0x3040:	return "A>K1";
			case 0x4040:	return "A>=K1";
			case 0x5040:	return "A!=K1";
		}		
		return "error";
	}
	

	private String evalQ(int ival) {
		switch(ival) {
			case 0x0000:	return "C==D";
			case 0x0100:	return "C<D";
			case 0x0140:	return "C<K2";
			case 0x0200:	return "C<=D";
			case 0x0300:	return "C>D";
			case 0x0400:	return "C>=D";
			case 0x0500:	return "C!=D";		
			case 0x0210:	return "C<=K2";
			case 0x0310:	return "C>K2";
			case 0x0410:	return "C>=K2";
			case 0x0510:	return "C!=K2";
		}		
		return "error";
	}


}

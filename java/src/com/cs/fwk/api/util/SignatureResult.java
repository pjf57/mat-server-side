package com.cs.fwk.api.util;


public class SignatureResult {
	private final long hwSig;
	private final long swSig;
	
	public SignatureResult(long hwSig, long swSig) {
		super();
		this.hwSig = hwSig;
		this.swSig = swSig;
	}
	
	public long getHwSig() {
		return hwSig;
	}
	
	public long getSwSig() {
		return swSig;
	}
	
	public boolean match() {
		return hwSig == swSig;
	}

}

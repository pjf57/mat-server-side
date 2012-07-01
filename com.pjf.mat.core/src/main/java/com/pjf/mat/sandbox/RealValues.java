package com.pjf.mat.sandbox;

public class RealValues {
	String msg;
	
	public RealValues(String msg) {
		this.msg = msg;
	}
	
	void doDouble() {
		System.out.println(Long.toHexString(Double.doubleToLongBits(300)));
		System.out.println(Long.toHexString(Double.doubleToLongBits(400)));
		System.out.println(Double.longBitsToDouble(Long.parseLong("4079000000000000", 16)));
		System.out.println(Double.longBitsToDouble(Long.parseLong("4059000000000000", 16)));
		System.out.println(Long.parseLong("2000000000000000", 16));		
	}
	
	void doSingle() {
		System.out.println(Integer.toHexString(Float.floatToIntBits(1.555555f)));
		System.out.println(Integer.toHexString(Float.floatToIntBits(1.785184f)));
		System.out.println(Integer.toHexString(Float.floatToIntBits(13f)));
		System.out.println(Integer.toHexString(Float.floatToIntBits(14f)));
		System.out.println(Float.intBitsToFloat(Integer.parseInt("3e800000", 16)));
		System.out.println(Float.intBitsToFloat(Integer.parseInt("41200000", 16)));
		System.out.println(Float.intBitsToFloat(Integer.parseInt("40000000", 16)));
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RealValues hw = new RealValues("test 1");
		hw.doSingle();

	}

}

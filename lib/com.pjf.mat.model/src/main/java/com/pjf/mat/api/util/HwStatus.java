package com.pjf.mat.api.util;

/**
 * Class holds basic status of HW system
 * 
 * @author pjf
 *
 */
public class HwStatus {
	private long hwSig;
	private int microtickPeriod;
	private String cf_version;

	/**
	 * Create empty (default) hw status
	 */
	public HwStatus() {
		super();
		this.hwSig = 0;
		this.microtickPeriod = 10000;	// default
		this.cf_version = "";
	}

	/**
	 * Create hw status from parameters
	 * 
	 * @param hwSig
	 * @param microtickPeriod
	 * @param cf_version
	 */
	public HwStatus(long hwSig, int microtickPeriod, String cf_version) {
		super();
		this.hwSig = hwSig;
		this.microtickPeriod = microtickPeriod;
		this.cf_version = cf_version;
	}


	/**
	 * @return hardware signature (or 0)
	 */
	public long getHwSig() {
		return hwSig;
	}

	/**
	 * @return microtick period (ps)
	 */
	public int getMicrotickPeriod() {
		return microtickPeriod;
	}

	/**
	 * @return framework version
	 */
	public String getCf_version() {
		return cf_version;
	}

	@Override
	public String toString() {
		return "sig=" + hwSig + " cf_ver=" + cf_version + " mtp=" + microtickPeriod;		
	}

}

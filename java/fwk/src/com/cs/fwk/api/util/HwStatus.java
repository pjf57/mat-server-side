package com.cs.fwk.api.util;

/**
 * Class holds basic status of HW system
 * 
 * @author pjf
 *
 */
public class HwStatus {
	private long hwSig;
	private int microtickPeriod;	// picoseconds
	private String cf_version;
	private byte flags;				// flag byte from protocol
	private int cfgEvents;			// #config events processed
	// flag bits
	private final byte F_INIT = 1;		// initialised
	private final byte F_CFGRDY = 2;	// ready for config
	private final byte F_CFGERR = 4;	// config error
	
	/**
	 * Create empty (default) hw status
	 */
	public HwStatus() {
		super();
		this.hwSig = 0;
		this.microtickPeriod = 10000;	// default
		this.cf_version = "";
		this.flags = 0;
		this.cfgEvents = 0;
	}

	/**
	 * Create hw status from parameters
	 * 
	 * @param hwSig
	 * @param microtickPeriod (ps)
	 * @param cf_version
	 * @param flags - flagset
	 * @param cfgEvents;
	 */
	public HwStatus(long hwSig, int microtickPeriod, String cf_version, byte flags, int cfgEvents) {
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
	
	/**
	 * @return #config events received by the HW
	 */
	public int getCfgEventCount() {
		return cfgEvents;
	}
	
	/**
	 * @return true if HW is initialised
	 */
	public boolean isInitialised() {
		return (flags & F_INIT) != 0;
	}

	/**
	 * @return true if HW is ready for config
	 */
	public boolean isConfigReady() {
		return (flags & F_CFGRDY) != 0;
	}

	/**
	 * @return true if HW has had a config error
	 */
	public boolean hadConfigError() {
		return (flags & F_CFGERR) != 0;
	}

	@Override
	public String toString() {
		return "sig=" + toHexLongString(hwSig) + " cf_ver=" + cf_version + " mtp=" + microtickPeriod + " ps" +
				" flags=x" + toHexByteString(flags) + " cfgEvts=" + cfgEvents;		
	}

	private String toHexLongString(long value) {
		StringBuffer buf = new StringBuffer();
		buf.append(toHexByteString((int) (value >> 56)));
		buf.append(toHexByteString((int) (value >> 48)));
		buf.append(toHexByteString((int) (value >> 40)));
		buf.append(toHexByteString((int) (value >> 32)));
		buf.append(toHexByteString((int) (value >> 24)));
		buf.append(toHexByteString((int) (value >> 16)));
		buf.append(toHexByteString((int) (value >> 8)));
		buf.append(toHexByteString((int) (value)));
		return buf.toString();
	}

	/**
	 * @param data - byte
	 * @return 2 char string hex representation
	 */
	private String toHexByteString(int data) {
		StringBuffer buf = new StringBuffer();
		char[] map = new char[] {'0', '1', '2', '3', '4', '5', '6', '7',
								 '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
		int d = data & 0xff;
		buf.append(map[(d >> 4) & 0xf]);
		buf.append(map[d & 0xf]);
		return buf.toString();
	}


}

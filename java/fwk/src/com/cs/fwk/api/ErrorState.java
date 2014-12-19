package com.cs.fwk.api;

import com.cs.fwk.util.Conversion;

/**
 * Holds the error status of a CB
 *
 */
public class ErrorState {
	private int numErrors;
	private int lastErrorCode;
	
	public ErrorState(int numErrors, int lastErrorCode) {
		super();
		this.numErrors = numErrors;
		this.lastErrorCode = lastErrorCode;
	}
	
	public ErrorState() {
		this.numErrors = 0;
		this.lastErrorCode = 0;
	}

	/**
	 * @return number of errors since last error reset
	 */
	public int getNumErrors() {
		return numErrors;
	}
	
	/**
	 * @return last error code received (0 if none)
	 */
	public int getLastErrorCode() {
		return lastErrorCode;
	}

	/**
	 * @return true if in an error state
	 */
	public boolean isInError() {
		return numErrors != 0;
	}
	
	@Override
	public String toString() {
		return "ErrorState [numErrors=" + numErrors + ", lastErrorCode=x"
				+ Conversion.toHexByteString(lastErrorCode) + "]";
	}

	/**
	 * Set an error code
	 * 
	 * @param errorCode
	 */
	public void setErrorCode(byte errorCode) {		
		if (numErrors < 255) {
			numErrors++;
		}
		lastErrorCode = errorCode;
	}
	
	

}

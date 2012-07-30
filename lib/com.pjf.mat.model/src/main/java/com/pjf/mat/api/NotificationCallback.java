package com.pjf.mat.api;

public interface NotificationCallback {
	/**
	 * An output event log has been received from the element logger.
	 * 
	 * @param src			The element sending the event
	 * @param intrument_id	The ID of the instrument
	 * @param rawValue		The raw value of the event data
	 * @param dispValue		The display value of the event data
	 */
	public void notifyEventLog(Element src, int intrument_id, int rawValue, String dispValue);

	/**
	 * An element status update has been received. The element has been updated.
	 * 
	 * @param element
	 */
	public void notifyElementStatusUpdate(Element element);

}

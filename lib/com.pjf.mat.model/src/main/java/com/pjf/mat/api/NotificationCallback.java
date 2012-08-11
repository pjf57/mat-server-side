package com.pjf.mat.api;

import java.util.Collection;

public interface NotificationCallback {
	/**
	 * An output event log has been received from the element logger.
	 * 
	 * @param timestamp		time when the event occurred
	 * @param src			The element sending the event
	 * @param intrument_id	The ID of the instrument
	 * @param rawValue		The raw value of the event data
	 * @param dispValue		The display value of the event data
	 */
	public void notifyEventLog(Timestamp timestamp, Element src, int intrument_id, int rawValue, String dispValue);

	/**
	 * An element status update has been received. The element has been updated.
	 * 
	 * @param element
	 */
	public void notifyElementStatusUpdate(Element element);
	
	/**
	 * Notify the receipt of some lookup audit logs. These are in no particular order.
	 * 
	 * @param logs
	 */
	public void notifyLkuAuditLogReceipt(Collection<LkuAuditLog> logs);

}

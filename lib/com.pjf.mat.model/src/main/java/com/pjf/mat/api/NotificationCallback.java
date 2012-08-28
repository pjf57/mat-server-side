package com.pjf.mat.api;

import java.util.Collection;

public interface NotificationCallback {
	/**
	 * An output event log has been received from the element logger.
	 * 
	 * @param evt		EventLog to send
	 */
	public void notifyEventLog(EventLog evt);

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

	/**
	 * Notify the receipt of some router audit logs. These are in no particular order.
	 * 
	 * @param logs
	 */
	public void notifyRtrAuditLogReceipt(Collection<RtrAuditLog> logs);
	
	/**
	 * Notify the receipt of a unified log entry.
	 * These logs are delayed by a fixed time (typ 500ms) so that they
	 * can be presented in time order.
	 * 
	 * @param log
	 */
	public void notifyUnifiedEventLog(TimeOrdered log);

}

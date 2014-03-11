package com.pjf.mat.api;

import java.util.Collection;
import java.util.List;

import com.pjf.mat.api.logging.EventLog;
import com.pjf.mat.api.logging.LkuAuditLog;
import com.pjf.mat.api.logging.OrderLog;
import com.pjf.mat.api.logging.RtrAuditLog;

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
	 * Notify receipt of an order
	 * 
	 * @param order order log which describes the order
	 */
	public void notifyOrderReceipt(OrderLog order);


	/**
	 * Notify the receipt of a unified log entry.
	 * These logs are delayed by a fixed time (typ 500ms) so that they
	 * can be presented in time order.
	 * 
	 * @param log
	 */
	public void notifyUnifiedEventLog(List<TimeOrdered> logs);
	
	

}

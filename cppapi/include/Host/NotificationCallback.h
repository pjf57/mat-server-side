#ifndef NOTIFICATIONCALLBACK_H
#define NOTIFICATIONCALLBACK_H

#include <iostream>
#include <String>
using namespace std;

#include "NotificationCallbackInt.h"

class NotificationCallback {
  private:

  public:
	NotificationCallback();
	~NotificationCallback();
	
	void notifyEventLog(EventLog&);
	void notifyElementStatusUpdate(list<ElementInt>&);
	void notifyLkuAuditLogReceipt(list<LkuAuditLog>&);
	void notifyRtrAuditLogReceipt(list<RtrAuditLog>&);
	void notifyOrderReceipt(list<OrderLog>&);
	void notifyUnifiedEventLog(list<TimeOrdered>&);

};

#endif
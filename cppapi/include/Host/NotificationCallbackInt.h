#ifndef NOTIFICATIONCALLBACKINT_H
#define NOTIFICATIONCALLBACKINT_H

#include <iostream>
#include <String>
#include <list>
using namespace std;

#include "ElementInt.h"
#include "EventLog.h"
#include "OrderLog.h"
#include "TimeOrdered.h"
#include "LkuAuditRawLog.h"
#include "RtrAuditRawLog.h"
#include "LkuAuditLog.h"
#include "RtrAuditLog.h"

class NotificationCallbackInt {
  
  public:
	virtual void notifyEventLog(EventLog&)=0;
	virtual void notifyElementStatusUpdate(list<ElementInt>&)=0;
	virtual void notifyLkuAuditLogReceipt(list<LkuAuditLog>&)=0;
	virtual void notifyRtrAuditLogReceipt(list<RtrAuditLog>&)=0;
	virtual void notifyOrderReceipt(list<OrderLog>&)=0;
	virtual void notifyUnifiedEventLog(list<TimeOrdered>&)=0;

};

#endif
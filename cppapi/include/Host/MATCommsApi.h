#ifndef MATCOMMSAPI_H
#define MATCOMMSAPI_H

#include <iostream>
#include <String>
#include <list>
using namespace std;

#include "CxnInt.h"
#include "LoopbackInt.h"
#include "MATApi.h"


#include "ElementInt.h"
#include "Cmd.h"
#include "Status.h"
#include "CBRawStatus.h"
#include "EvtLogRaw.h"
#include "LkuAuditRawLog.h"
#include "RtrAuditRawLog.h"
#include "NotificationCallback.h"
#include "HwStatus.h"


class MATCommsApi {
	
  public:
	virtual void sendConfig(list<ElementInt>&)=0;
	virtual Status requestStatus()=0;
	virtual Status requestStatus(const ElementInt&)=0;
	virtual void requestLkuLogs(list<LkuAuditRawLog>&)=0;
	virtual void requestRtrLogs(list<RtrAuditRawLog>&)=0;
	virtual void sendCmd(Cmd &)=0;
	virtual void shutdown()=0;
	virtual void addNotificationSubscriber(NotificationCallback *)=0;	
	virtual long int getHwSignature()=0;
	virtual void setHwStatus(const HwStatus&)=0;
	virtual const HwStatus& getHwStatus()=0;
	virtual void synchroniseClock(long int)=0;
	virtual void subscribeIncomingMsgs(int,LoopbackInt *)=0;
	virtual void resetCounters()=0;
	virtual void resetConfig()=0;
	virtual void setMat(MATApi *)=0;
	virtual CxnInt *getCxn()=0;	
};

#endif
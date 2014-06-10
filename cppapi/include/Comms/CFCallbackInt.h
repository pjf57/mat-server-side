#ifndef CFCALLBACKINT_H
#define CFCALLBACKINT_H

#include <iostream>
#include <String>
#include <list>
using namespace std;


#include "HwStatus.h"
#include "CBRawStatus.h"
#include "EvtLogRaw.h"
#include "LkuAuditRawLog.h"
#include "RtrAuditRawLog.h"

class CFCallbackInt {
  private:

  public:
	virtual void processCFStatus(HwStatus*)=0;
	virtual void processCBStatus(list<CBRawStatus>&);
	virtual void processEvtLogs(list<EvtLogRaw>&);
	virtual void processLkuLogs(list<LkuAuditRawLog>&);
	virtual void processRtrLogs(list<RtrAuditRawLog>&);
	virtual void processUnknownMsg(int,unsigned char*)=0;
	
};

#endif
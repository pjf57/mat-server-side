#ifndef MATCOMMS_H
#define MATCOMMS_H

#include <iostream>
#include <String>
#include <list>
using namespace std;

#include "CFCallbackInt.h"
#include "LoopbackInt.h"
#include "CxnInt.h"
#include "MATCommsApi.h"


class MATComms : public MATCommsApi, public CFCallbackInt {
  private:
	MATComms();
	
  public:
	MATComms(CxnInt *,int);
	MATComms(int);
	~MATComms();
	
	
	// MATCommsAPi
	void sendConfig(list<ElementInt>&);
	Status requestStatus();
	Status requestStatus(const ElementInt&);
	void requestLkuLogs(list<LkuAuditRawLog>&);
	void requestRtrLogs(list<RtrAuditRawLog>&);
	void sendCmd(Cmd &);
	void shutdown();
	void addNotificationSubscriber(NotificationCallback *);	
	long int getHwSignature();
	void setHwStatus(const HwStatus&);
	const HwStatus& getHwStatus();
	void synchroniseClock(long int);
	void subscribeIncomingMsgs(int,LoopbackInt *);
	void resetCounters();
	void resetConfig();
	void setMat(MATApi *);
	CxnInt *getCxn();	
	
	
	//CFCallbackInt
	void processCFStatus(HwStatus*);
	void processCBStatus(list<CBRawStatus>&);
	void processEvtLogs(list<EvtLogRaw>&);
	void processLkuLogs(list<LkuAuditRawLog>&);
	void processRtrLogs(list<RtrAuditRawLog>&);
	void processUnknownMsg(int,unsigned char*);
	
	
	// other
	void setCxn(CxnInt *);
	void injectLoopbackMsg(int,unsigned char*);

};

#endif
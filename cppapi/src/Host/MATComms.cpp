#include "MATComms.h"



Status TMP_Status;
HwStatus TMP_HwStatus;
#include "UDPCxn.h"

UDPCxn TMP_CxnInt;

//
//
//
MATComms::MATComms() 
{ }

//
//
//
MATComms::MATComms(CxnInt *p_CxnInt,int p_Port)
{ }

//
//
//
MATComms::MATComms(int p_Port) 
{ }

//
//
//
MATComms::~MATComms() 
{ }

//
//
//
void   MATComms::sendConfig(list<ElementInt>&)
{ }



//
//
//
Status MATComms::requestStatus()
{ 
	return TMP_Status;
}


//
//
//
Status MATComms::requestStatus(const ElementInt&)
{ 
	return TMP_Status;
}



//
//
//
void MATComms::requestLkuLogs(list<LkuAuditRawLog>&)
{ }



//
//
//
void MATComms::requestRtrLogs(list<RtrAuditRawLog>&)
{ }

//
//
//
void MATComms::sendCmd(Cmd &)
{ }

//
//
//
void MATComms::shutdown()
{ }

//
//
//
void MATComms::addNotificationSubscriber(NotificationCallback *)
{ }

	
//
//
//
long int MATComms::getHwSignature()
{ 
	return -1;
}


//
//
//
void MATComms::setHwStatus(const HwStatus&)
{ }



//
//
//
const HwStatus& getHwStatus()
{ 
	return TMP_HwStatus;
}


	
	
//
//
//
void MATComms::synchroniseClock(long int)
{ }

//
//
//
void MATComms::subscribeIncomingMsgs(int,LoopbackInt *)
{ }


//
//
//
void MATComms::resetCounters()
{ }



//
//
//
void MATComms::resetConfig()
{ }


//
//
//
void MATComms::setMat(MATApi *)
{ }


//
//
//
CxnInt  *MATComms::getCxn()
{ 
	return &TMP_CxnInt;
}




//CFCallbackInt
//
//
//
void MATComms::processCFStatus(HwStatus*)
{ }

//
//
//
void MATComms::processCBStatus(list<CBRawStatus>&)
{ }

//
//
//
void MATComms::processEvtLogs(list<EvtLogRaw>&)
{ }

//
//
//
void MATComms::processLkuLogs(list<LkuAuditRawLog>&)
{ }

//
//
//
void MATComms::processRtrLogs(list<RtrAuditRawLog>&)
{ }

//
//
//
void MATComms::processUnknownMsg(int,unsigned char*)
{ }

	
// other
//
//
//
void MATComms::setCxn(CxnInt *)
{ }

//
//
//
void MATComms::injectLoopbackMsg(int,unsigned char*)
{ }



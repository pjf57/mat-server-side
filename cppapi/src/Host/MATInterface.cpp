#include "MATInterface.h"


//
//
//
MATInterface::MATInterface() 
{ }

//
//
//
MATInterface::MATInterface(MATCommsApi *p_Api,MATModelInt *p_Model)
{ }


//
//
//
MATInterface::~MATInterface()
{ }



//
//
//
MATModelInt *MATInterface::getModel()
{ }



//
//
//
void   MATInterface::requestHWStatus()
{ }



//
//
//
void   MATInterface::configureHW()
{ }



//
//
//
void   MATInterface::recalcCalculatedAttrs()
{ }




//
//
//
list<Attribute>&  	MATInterface::recalcElAttrs(ElementInt& p_Element)
{ }



//
//
//
void   MATInterface::sendCmd(Cmd& p_Cmd)
{ }

//
//
//
string MATInterface::toString()
{ }

//
//
//
void   MATInterface::shutdown()
{ }

//
//
//
void   MATInterface::putIntoConfigModel()
{ }



//
//
//
SignatureResult& MATInterface::checkHWSignature()
{ }


	
//
//
//
long int MATInterface::getHWSignature()
{ }


	

//
//
//
void   MATInterface::syncClock(long int)
{ }




//
//
//
TimeStamp& MATInterface::getCurrentTime()
{ }



//
//
//
void   MATInterface::reqLkuAuditLogs()
{ }



//
//
//
void   MATInterface::reqRtrAuditLogs()
{ }



//
//
//
void   MATInterface::resetCounters()
{ }



//
//
//
void   MATInterface::loadDesign(string &p_DesignName)
{ }



//
//
//
void   MATInterface::resetHWConfig()
{ }





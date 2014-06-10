#ifndef CFCOMMSINT_H
#define CFCOMMSINT_H

#include <iostream>
#include <String>
using namespace std;


#include "CxnInt.h"
#include "CFCallbackInt.h"

class CFCommsInt {
  public:
    virtual void   setCallback(CFCallbackInt *)=0;
	virtual void   resetConfigBuffer()=0;
	virtual void   addConfigBuffer()=0;
	virtual void   addSysConfigItem(int,int,int,int)=0;
	virtual void   addCmdItem(int,int,int,int)=0;
	virtual void   addCxnItem(int,int,int,int)=0;
	virtual int    getConfigBufferSpace()=0;
	virtual int    getConfigBufferItemCount()=0;
	virtual int    getConfigBufferLength()=0;
	virtual void   sendConfig()=0;
	virtual void   sendSingleCmd(int,int,int,int)=0;
	virtual void   requestStatus()=0;
	virtual void   requestLkuAuditLogs()=0;
	virtual void   requestCFStatus()=0;
	virtual void   resetCounters(int)=0;
	virtual void   resetCBConfig(int)=0;
	virtual void   synchroniseClock(long int)=0;
	virtual void   shutdown()=0;
	virtual CxnInt  *getCxn()=0;
	virtual void   handleIncomingMsg(int,unsigned char*)=0;
};

#endif
#ifndef MatApi_H
#define MatApi_H

#include <iostream>
#include <String>
using namespace std;

#include "MATModelInt.h"
#include "Attribute.h"
#include "SignatureResult.h"
#include "TimeStamp.h"
#include "ONObject.h"
#include "Cmd.h"


class MATApi {

  public:
	virtual MATModelInt 		*getModel()=0;
	virtual void      			requestHWStatus()=0;
	virtual void      			configureHW()=0;
	virtual void      			recalcCalculatedAttrs()=0;
	virtual list<Attribute>&   recalcElAttrs(ElementInt&)=0;
	virtual void				sendCmd(Cmd&)=0;
	virtual string				toString()=0;
	virtual void				shutdown()=0;
	virtual void				putIntoConfigModel()=0;
	virtual SignatureResult&	checkHWSignature()=0;
	virtual long int 			getHWSignature()=0;
	virtual void				syncClock(long int)=0;
	virtual TimeStamp&			getCurrentTime()=0;
	virtual void				reqLkuAuditLogs()=0;
	virtual void 				reqRtrAuditLogs()=0;
	virtual void				resetCounters()=0;
	virtual void				loadDesign(string &)=0;
	virtual void				loadDesign(ONObject &)=0;
	virtual void				resetHWConfig()=0;
};

#endif
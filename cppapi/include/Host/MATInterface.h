#ifndef MATInterface_H
#define MATInterface_H

#include <iostream>
#include <String>
#include <list>
using namespace std;

#include "MATApi.h"
#include "MATCommsApi.h"

class MATInterface : public MATApi {
  private:
	MATInterface();
	
  public:
	MATInterface(MATCommsApi *p_Api,MATModelInt *p_Model);
	~MATInterface();
	
	MATModelInt 		*getModel();
	void      			requestHWStatus();
	void      			configureHW();
	void      			recalcCalculatedAttrs();
	list<Attribute>&  	recalcElAttrs(ElementInt&);
	void				sendCmd(Cmd&);
	string				toString();
	void				shutdown();
	void				putIntoConfigModel();
	SignatureResult&	checkHWSignature();
	long int 			getHWSignature();
	void				syncClock(long int);
	TimeStamp&			getCurrentTime();
	void				reqLkuAuditLogs();
	void 				reqRtrAuditLogs();
	void				resetCounters();
	void				loadDesign(string &);
	void				loadDesign(ONObject &);
	void				resetHWConfig();
};

#endif
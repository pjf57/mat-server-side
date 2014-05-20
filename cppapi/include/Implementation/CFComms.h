#include <iostream>
#include <String>
using namespace std;
#include "CxnInt.h"
#include "CFCallback.h"
#include "HwStatus.h"
#include "Reader.h"
#include "EncodeConfigItemList.h"




class CFComms {
  private:
	int 					m_CFPort;
	CFCallback 				m_CFCallback;
	CxnInt				   *m_Cxn;
	HwStatus 				m_HwStatus;
	Reader 					m_Reader;
	EncodeConfigItemList 	m_configBuf;
	
	CFComms(); // don't allow default constructor
	
  private:
	static const unsigned char BS_INIT=1;
	static const unsigned char BS_CFG=2;
	static const unsigned char BS_RST=3;
	static const unsigned char BS_RUN=4;


  public:
	CFComms(int);
	~CFComms();
	
	void 		shutdown();
	void 		setCallback(CFCallback&);
	CxnInt& 	getCxn();
	void 		resetConfigBuffer();
	void		addConfigItem(int,int,int,int);
	void		addSysConfigItem(int,int,int,int);
	void		addCmdItem(int,int,int,int);
	void		addCnxItem(int,int,int,int);
	int			getConfigBufferSpace();
	int			getConfigBufferItemCount();
	int			getConfigBufferLength();
	void		sendConfig();
	void		sendSingleCmd(int,int,int,int);
	void		requestStatus();
	void		requestLkuAuditLogs();
	void		requestRtrAuditLogs();
	void		requestCFStatus();
	void		resetCounters();
	void		resetCBConfig();
	void		synchroniseClock(long int);
	void		injectLoopbackMsg(int,unsigned char);
	void		processCFStatusMsg(unsigned char);
	void		processStatusMsg(unsigned char);
	void		processEventLogMsg(unsigned char);
	void		processLkuAuditLogMsg(unsigned char);
	void		processRtrAuditLogMsg(unsigned char);
	string		toHexString(unsigned char,int,int);
	void		handleIncomingMsg(int,unsigned char);

};
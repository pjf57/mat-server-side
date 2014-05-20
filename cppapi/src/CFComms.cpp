#include <iostream>
#include <String>
using namespace std;
#include "CFComms.h"

//
//	
CFComms::CFComms()
{
}
//
// 
CFComms::CFComms(int)
{
}
 
//
//
CFComms::~CFComms()
{
}
 
//
//
	
void CFComms::shutdown()
{
}
 
//
//
void CFComms::setCallback(CFCallback&)
{
}
 
//
//
CxnInt& CFComms::getCxn()
{
	return *m_Cxn;
}
 
//
//
void CFComms::resetConfigBuffer()
{
}
 
//
//
void CFComms::addConfigItem(int a,int b,int c,int d)
{
}
 
//
//
void CFComms::addSysConfigItem(int a,int b,int c,int d)
{
}
 
//
//
void CFComms::addCmdItem(int a,int b,int c,int d)
{
}
 
//
//
void CFComms::addCnxItem(int a,int b,int c,int d)
{
}
 
//
//
int CFComms::getConfigBufferSpace()
{
	return 0;
}
 
//
//
int CFComms::getConfigBufferItemCount()
{
		return 0;
}
 
//
//
int CFComms::getConfigBufferLength()
{
		return 0;
}
 
//
//
void CFComms::sendConfig()
{
}
 
//
//
void CFComms::sendSingleCmd(int a,int b,int c,int d)
{
}
 
//
//
void CFComms::requestStatus()
{
}
 
//
//
void CFComms::requestLkuAuditLogs()
{
}
 
//
//
void CFComms::requestRtrAuditLogs()
{
}
 
//
//
void CFComms::requestCFStatus()
{
}
 
//
//
void CFComms::resetCounters()
{
}
 
//
//
void CFComms::resetCBConfig()
{
}
 
//
//
void CFComms::synchroniseClock(long int a)
{
}
 
//
//
void CFComms::injectLoopbackMsg(int a,unsigned char b)
{
}
 
//
//
void CFComms::processCFStatusMsg(unsigned char a)
{
}
 
//
//
void CFComms::processStatusMsg(unsigned char a)
{
}
 
//
//
void CFComms::processEventLogMsg(unsigned char a)
{
}
 
//
//
void CFComms::processLkuAuditLogMsg(unsigned char a)
{
}
 
//
//
void CFComms::processRtrAuditLogMsg(unsigned char a)
{
}
 
//
//
string CFComms::toHexString(unsigned char a,int b,int c)
{
	string myStr("test string");
	return myStr;
}
 
//
//
void CFComms::handleIncomingMsg(int a,unsigned char b)
{
}
 

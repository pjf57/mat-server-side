#include <iostream>
#include "UDPCxn.h"

//
//
UDPCxn::UDPCxn()
{
	//DatagramSocket m_skt;
	//InetAddress m_dstIP;
	m_shutdown = false;
	m_sktInUse = false;
}


//
//
UDPCxn::~UDPCxn()
{
}
	
//
//
void UDPCxn::setLoopbackCallback()
{
}
 
//
//
void UDPCxn::send(const CFDatagram& p_datagram)
{
}

//
//
CFDatagram* UDPCxn::rcv()
{
    CFDatagram *l_CFDatagram = new CFDatagram();
	return l_CFDatagram;
}

//
//
void UDPCxn::close()
{
}

//
//
const string& UDPCxn::getAddress()
{
	return m_Address;
}

//
//
int UDPCxn::getMtuSize()
{
	return 10;
}

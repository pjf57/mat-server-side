#include <iostream>
#include "UDPCxn.h"

//
//
UDPCxn::UDPCxn()
{
	m_shutdown = false;
	m_sktInUse = false;
	m_LoopbackInt = 0;
	
}


//
//
UDPCxn::~UDPCxn()
{
}


//
//
string UDPCxn::toHexString(unsigned char *p_byte)
{
}


//
//
void UDPCxn::initialise()
{
}


	
//
//
void UDPCxn::setLoopbackCallback(LoopbackInt *p_LoopbackInt)
{
	m_LoopbackInt = p_LoopbackInt;
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
	unsigned char l_tmp[SKT_RXBUF_SIZE];
    CFDatagram *l_CFDatagram = new CFDatagram(SKT_RXBUF_SIZE,l_tmp);
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
	return MTU_SIZE;
}

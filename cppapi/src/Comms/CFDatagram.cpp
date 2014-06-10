#include "CFDatagram.h"
#include <stdio.h>
#include <string.h>

CFDatagram::CFDatagram()
{
	m_data = 0;
}

CFDatagram::CFDatagram(int p_length,unsigned char *p_Data)
{
	m_data = new unsigned char[p_length];
	memcpy(m_data,p_Data,p_length);
}

CFDatagram::~CFDatagram()
{
	delete[] m_data;
}	
	


//
//
//
int CFDatagram::getDstPort()
{
	return m_dstPort;
}	


//
//
//
void CFDatagram::setDstPort(int p_dstPort)
{
	m_dstPort = p_dstPort;
}	

//
//
//
int CFDatagram::getSrcPort()
{
	return m_srcPort;
}	
	
//
//
//
void CFDatagram::setSrcPort(int p_srcPort)
{
	m_srcPort = p_srcPort;
}	
	
//
//
//
unsigned char* CFDatagram::getData()
{
	return m_data;
}	
	



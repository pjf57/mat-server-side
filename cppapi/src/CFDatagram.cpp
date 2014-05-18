#include "CFDatagram.h"

CFDatagram::CFDatagram()
{
	m_data = new unsigned char[10];
	m_data[0] = 'X';
	m_data[1] = '\n';
	m_data[1] = '\0';
	
	
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
	



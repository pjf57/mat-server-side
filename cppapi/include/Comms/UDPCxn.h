#ifndef UDPCXN_H
#define UDPCXN_H

#include <iostream>
#include <String>
#include "CxnInt.h"
#include "DatagramSocket.h"
#include "InetAddress.h"


using namespace std;

class UDPCxn : public CxnInt {
  private:
	string m_Address;
	DatagramSocket m_skt;
	InetAddress m_dstIP;
	bool m_shutdown;
	bool m_sktInUse;
	LoopbackInt *m_LoopbackInt;
	
	static const int SKT_TMO_MS = 500;
	static const int SKT_RXBUF_SIZE = 200000;
	static const int MTU_SIZE = 1500-20-8;
	static const int LOCAL_PORT = 3500;
	
	
  private:
    string toHexString(unsigned char *p_byte);
	
  public:
	UDPCxn();
	~UDPCxn();
	
	
	void initialise();

    // these functions implement the interface
	void setLoopbackCallback(LoopbackInt*);
    void send(const CFDatagram&);
	CFDatagram* rcv();
	void close();
	const string& getAddress();
    int getMtuSize();
};

#endif
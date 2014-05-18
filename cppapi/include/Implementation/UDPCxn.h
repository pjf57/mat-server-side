
#include "Cnx.h"
#include <iostream>
#include <String>

using namespace std;

class UDPCxn : public Cnx {
  private:
	string m_Address;
	//DatagramSocket m_skt;
	//InetAddress m_dstIP;
	bool m_shutdown;
	bool m_sktInUse;
	//Loopback
	
	static const int SKT_TMO_MS = 500;
	static const int SKT_RXBUF_SIZE = 200000;
	static const int MTU_SIZE = 1500-20-8;
	static const int LOCAL_PORT = 3500;
	
	
  private:
    void toHexString(unsigned char *p_byte);
	
  public:
	UDPCxn();
	~UDPCxn();
	
	
	void initialise();


    // these functions implement the interface
    void setLoopbackCallback(); 
    void send(const CFDatagram& p_datagram);
	CFDatagram* rcv();
	void close();
	const string& getAddress();
    int getMtuSize();
};
#include <iostream>
#include <String>
using namespace std;

class CFDatagram {
  private:
	int m_dstPort;
	int m_srcPort;
	unsigned char *m_data;
  public:
	CFDatagram();
	~CFDatagram();
	int getDstPort();
	void setDstPort(int p_dstPort);
	int getSrcPort();
	void setSrcPort(int p_srcPort);
	unsigned char* getData();
	
};
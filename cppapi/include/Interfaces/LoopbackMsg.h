#include <iostream>
#include <String>
using namespace std;

class LoopbackInt {
  public:
    virtual void setLoopbackCallback() =0; 
    virtual void send(const CFDatagram& p_datagram)=0;
	virtual CFDatagram* rcv()=0;
	virtual void close()=0;
	virtual const string& getAddress()=0;
    virtual int getMtuSize()=0;
};
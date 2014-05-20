#include <iostream>
#include <String>
#include "LoopbackInt.h"
#include "CFDatagram.h"
using namespace std;

class CxnInt {
  public:
    virtual void setLoopbackCallback(LoopbackInt&)=0; 
    virtual void send(const CFDatagram& p_datagram)=0;
	virtual CFDatagram* rcv()=0;
	virtual void close()=0;
	virtual const string& getAddress()=0;
    virtual int getMtuSize()=0;
};
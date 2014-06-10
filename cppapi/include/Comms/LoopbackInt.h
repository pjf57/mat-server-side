#ifndef LoopbackInt_H
#define LoopbackInt_H


#include <iostream>
#include <String>
using namespace std;

class LoopbackInt {
  public:
    virtual void injectLoopbackMsg(int,unsigned char)=0;
};

#endif
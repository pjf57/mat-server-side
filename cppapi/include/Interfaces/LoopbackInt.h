#include <iostream>
#include <String>
using namespace std;

class LoopbackInt {
  public:
    virtual void injectLoopBackMsg(int,unsigned char)=0;
};
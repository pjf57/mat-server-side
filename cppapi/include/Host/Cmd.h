#ifndef Cmd_H
#define Cmd_H

#include <iostream>
#include <String>
using namespace std;
#include "ElementInt.h"


class Cmd {
  public:
	virtual const string& getName();
	virtual int getConfigId()=0;
	//virtual ElementInt* getParent()=0;
	virtual int getParentId()=0;
	virtual const string& getFullName()=0;
	virtual int getArg()=0;
	virtual int getData()=0;
};

#endif
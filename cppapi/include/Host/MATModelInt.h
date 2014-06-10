#ifndef MATModelInt_H
#define MATModelInt_H

#include <iostream>
#include <String>
#include <list>
using namespace std;

#include "ElementInt.h"
#include "Properties.h"


class MATModelInt {
	
  public:
	virtual list<ElementInt>* getElementInts()=0;
	virtual ElementInt* getElementInt(int)=0;
	virtual long int getHWSignature()=0;
	virtual ElementInt* getType(int)=0;
	virtual Properties &getProperties()=0;
	virtual list<string>& getTypes()=0;
};

#endif
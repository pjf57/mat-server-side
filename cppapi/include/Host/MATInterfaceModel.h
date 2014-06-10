#ifndef MATInterfaceModel_H
#define MATInterfaceModel_H

#include <iostream>
#include <String>
#include <list>
using namespace std;

#include "MATModelInt.h"
#include "Properties.h"
#include "AttrSysType.h"

class MATInterfaceModel : public MATModelInt {
  private:
    // LOGGER!!!
  
	MATInterfaceModel();
	void initialise();
	ElementInt* readElement(int);
	ElementInt* readType(int);
	Attribute loadEnumAttribute(ElementInt*,string&,string&,int,AttrSysType&,string&,int,string&);
	
  public:
	MATInterfaceModel(Properties&);
	~MATInterfaceModel();
	
	// MATModelInt
	list<ElementInt>* getElementInts();
	ElementInt* getElementInt(int);
	long int getHWSignature();
	ElementInt* getType(int);
	Properties &getProperties();
	list<string>& getTypes();
	
	// other
	string& toString();
	long int getSWSignature();
	
	
};

#endif
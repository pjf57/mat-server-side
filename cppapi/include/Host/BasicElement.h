#ifndef BasicElement_H
#define BasicElement_H

#include <iostream>
#include <String>
#include <list>

using namespace std;


#include "ElementInt.h"


class BasicElement : public ElementInt {
  private:
	// MAKE PRIVATE!!! BasicElement(); 

  public:
  	BasicElement(); // MAKE PRIVATE!!
  	BasicElement(int, string&, int);
	BasicElement(int, string&, ElementInt *);
	~BasicElement();
	
	
    // LOGGER !!
	void getType();
	int  getHWType();
	list<Attribute>& getAttributes();
	list<Attribute>& getStatusAtts();
	list<InputPort>& getInputs();
	list<OutputPort>& getOutputs();
	list<Cmd>& getCmds();
	Status& getElementStatus();
	Attribute& getAttribute(string&);
	void setStatus(Status&);
	string getShortName();
	OutputPort& getOutput(string&);
	void removeAllConnections();
	bool hasStatusChanged(bool);
};

#endif
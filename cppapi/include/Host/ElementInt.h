#ifndef ElementInt_H
#define ElementInt_H

#include <iostream>
#include <String>
#include <list>

using namespace std;


#include "Attribute.h"
#include "InputPort.h"
#include "OutputPort.h"
#include "Cmd.h"
#include "Status.h"


class ElementInt {
  public:
	virtual void getType()=0;
	virtual int  getHWType()=0;
	virtual list<Attribute>& getAttributes()=0;
	virtual list<Attribute>& getStatusAtts()=0;
	virtual list<InputPort>& getInputs()=0;
	virtual list<OutputPort>& getOutputs()=0;
	virtual list<Cmd>& getCmds()=0;
	virtual Status& getElementStatus()=0;
	virtual Attribute& getAttribute(string&)=0;
	virtual void setStatus(Status&)=0;
	virtual string getShortName()=0;
	virtual OutputPort& getOutput(string&)=0;
	virtual void removeAllConnections()=0;
	virtual bool hasStatusChanged(bool);
};

#endif
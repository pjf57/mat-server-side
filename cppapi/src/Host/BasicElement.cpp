#include "BasicElement.h"
#include <stdio.h>
#include <string.h>

//
//
//
BasicElement::BasicElement()
{
}


//
//
//
BasicElement::BasicElement(int, string&, int)
{
}


//
//
//
BasicElement::BasicElement(int, string&, ElementInt *)
{
}


//
//
//
BasicElement::~BasicElement()
{

}	
	

//
//
//
void BasicElement::getType()
{
}

//
//
//
int  BasicElement::getHWType()
{
}

//
//
//
list<Attribute>& BasicElement::getAttributes()
{
}

//
//
//
list<Attribute>& BasicElement::getStatusAtts()
{
}

//
//
//
list<InputPort>& BasicElement::getInputs()
{
}

//
//
//
list<OutputPort>& BasicElement::getOutputs()
{
}

//
//
//
list<Cmd>& BasicElement::getCmds()
{
}

//
//
//
Status& BasicElement::getElementStatus()
{
}

//
//
//
Attribute& BasicElement::getAttribute(string&)
{
}

//
//
//
void BasicElement::setStatus(Status&)
{
}

//
//
//
string BasicElement::getShortName()
{
}

//
//
//
OutputPort& BasicElement::getOutput(string&)
{
}

//
//
//
void BasicElement::removeAllConnections()
{
}

//
//
//
bool BasicElement::hasStatusChanged(bool)
{
}


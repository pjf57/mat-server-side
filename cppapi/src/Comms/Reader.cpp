#include "Reader.h"

//
//
//
Reader::Reader() 
{ 
	m_keepGoing = true;
}

//
//
//
Reader::~Reader() 
{
}

//
//
//
void Reader::run() 
{ 

}

//
//
//
void Reader::shutdown() 
{ 
	m_keepGoing = false;
}
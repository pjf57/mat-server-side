#include "MATInterfaceModel.h"
#include <stdio.h>
#include <string.h>


//TMP
#include"BasicElement.h"
BasicElement       TMP_Element;
//list<BasicElement> TMP_BasicElementList;
//list<string>       TMP_StringList;
Properties		   TMP_Properties;
Attribute		   TMP_Attribute;


MATInterfaceModel::MATInterfaceModel()
{
}

//
//
//	
MATInterfaceModel::MATInterfaceModel(Properties &p_Properties)
{
}
//
//
//	
MATInterfaceModel::~MATInterfaceModel()
{

}	

//
//
//	

list<ElementInt>* MATInterfaceModel::getElementInts()
{
	//return &TMP_BasicElementList;
}	

//
//
//	
ElementInt* MATInterfaceModel::getElementInt(int p_ElementId)
{
	return &TMP_Element;
}	

//
//
//	
long int MATInterfaceModel::getHWSignature()
{
	return -1;
}	

//
//
//	
ElementInt* MATInterfaceModel::getType(int p_ElementId)
{
	return &TMP_Element;
}	

//
//
//	
Properties &MATInterfaceModel::getProperties()
{
	return TMP_Properties;
}	

//
//
//	
list<string>& MATInterfaceModel::getTypes()
{
	//return TMP_StringList;
}	

//
//
//
string& MATInterfaceModel::toString()
{
	//return TMP_StringList;
}	

//
//
//	
long int MATInterfaceModel::getSWSignature()
{
	return -1;
}	

//
//
//	
void MATInterfaceModel::initialise()
{

}	

//
//
//	
ElementInt* MATInterfaceModel::readElement(int p_ElementId)
{
	return &TMP_Element;
}	

//
//
//	
ElementInt* MATInterfaceModel::readType(int p_ElementId)
{
	return &TMP_Element;
}	

//
//
//	
Attribute MATInterfaceModel::loadEnumAttribute(
ElementInt *a,
string &b,
string &c,
int d,
AttrSysType &e,
string &f,
int g,
string &h)
{
	return TMP_Attribute;
}	




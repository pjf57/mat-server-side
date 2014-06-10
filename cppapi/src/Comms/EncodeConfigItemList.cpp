#include "EncodeConfigItemList.h"



//
//
//
EncodeConfigItemList::EncodeConfigItemList() 
{ 
	m_itemCount = -1;
	m_upTo -1;
	m_Data =0;
}


//
//
//
EncodeConfigItemList::~EncodeConfigItemList() 
{
}

//
//
//
int EncodeConfigItemList::getItemCount()
{
	return m_itemCount;
}

//
//
//
int EncodeConfigItemList::getItemSize()
{
	return 0;
}

//
//
//
void EncodeConfigItemList::putRaw(int,int,int,int)
{
}

//
//
//
void EncodeConfigItemList::putConfigItem(int,int,int,int)
{
}

//
//
//
void EncodeConfigItemList::putSystemItem(int,int,int,int)
{
}

//
//
//
void EncodeConfigItemList::putCmdItem(int,int,int,int)
{
}

//
//
//
unsigned char *EncodeConfigItemList::getData()
{
	return m_Data;
}

//
//
//
int EncodeConfigItemList::getLength()
{
}

//
//
//

string EncodeConfigItemList::toString()
{
	string l_DataAsString;
	return l_DataAsString;
}
	
#ifndef ENCODECONFIGITEMLIST_H
#define ENCODECONFIGITEMLIST_H


#include <iostream>
#include <String>
using namespace std;

class EncodeConfigItemList {
  private:
	int m_itemCount;
	int m_upTo;
	unsigned char *m_Data;

  public:
	EncodeConfigItemList();
	~EncodeConfigItemList();
	int getItemCount();
	int getItemSize();
	void putRaw(int,int,int,int);
	void putConfigItem(int,int,int,int);
	void putSystemItem(int,int,int,int);
	void putCmdItem(int,int,int,int);
	unsigned char *getData();
	int getLength();
	string toString();
	
};

#endif
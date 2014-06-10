#ifndef HWSTATUS_H
#define HWSTATUS_H

#include <iostream>
#include <String>
using namespace std;

class HwStatus {
  private:
	string m_name;
  public:
	HwStatus();
	~HwStatus();
	string getName();
	
};

#endif
#ifndef READER_H
#define READER_H

#include <iostream>
#include <String>
using namespace std;

class Reader {
  private:
	bool m_keepGoing;
  public:
	Reader();
	~Reader();
	void run();
	void shutdown();
	
	
};

#endif
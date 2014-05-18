#include <iostream>
#include <String>
using namespace std;
class Logger;

class Logger {

	
  public:
	static const int Info = 3;

  public:
	Logger();
	~Logger();
	void log(int, const char *p_msg);
	void log(int, const string& p_msg);
};
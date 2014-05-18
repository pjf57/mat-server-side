#include "Logger.h"

Logger::Logger()
{

}

Logger::~Logger()
{
}	
	

//
//
//
void Logger::log(int, const string& p_msg)
{
	cout << p_msg << "\n";
}	

//
//
//
void Logger::log(int, const char* p_msg)
{
	cout << string(p_msg) << "\n";
}



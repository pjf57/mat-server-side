#include <iostream>
#include "UDPCxn.h"
#include "Logger.h"




int main ()
{
  Logger logger;
  logger.log(logger.Info,"Creating a UDP connection.");
  
  UDPCxn l_UDPCxn;
  Cnx *l_Cnx = &l_UDPCxn;
  CFDatagram *l_CFDatagram = l_Cnx->rcv();
  
  
  logger.log(logger.Info,"CFDatagram data is: ");
  logger.log(logger.Info,(const char *) l_CFDatagram->getData());
  
  logger.log(logger.Info,"Done.");
}


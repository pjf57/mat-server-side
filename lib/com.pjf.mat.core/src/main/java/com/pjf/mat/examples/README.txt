Example 1 - CheetahExample1.java
--------------------------------

This example configures the hardware with a simple MACD price arbitrage algo
as shown in the CheetahExample1.configure() method.
It then starts the HW running and starts a market simulator to read tick values
from a spreadsheet file and transmit them over the UDP/IP link to the HW.
When the HW issues an order, this is received by the server and logged.

1. Create a new Java project in eclipse
2. Add src file CheetahExample1.java
3. Create folder resources under the folders dir
4. Copy all the example .csp and .csv files to the resources folder
	Note: .csp is Cheetah Solutions Palette file - describes the layout of CBs in the HW to the software
	      .csv file contains sample market data that can be sent to the hardware for testing
5. Project Properties -> BuildPath -> Add external libs
	Add all from Cheetah lib dir
	Add all from Cheetah extlib dir
6. Connect HW (UDP/IP)
7. Run CheetahExample1


--
--	Purpose: This package defines constants for configuration of the various elements
--
--

library IEEE;
use IEEE.STD_LOGIC_1164.all;
use IEEE.NUMERIC_STD.ALL;
use work.mat_common.all;

package mat_element_defs is

	-- element IDs for standardised elements
	constant EL_ID_SYSTEM_CONTROL : element_id_t := "000000";


	-- element type IDSs
	constant EL_TYP_EMA 			: el_type_t := x"10";
	constant EL_TYP_TG1 			: el_type_t := x"20";
	constant EL_TYP_LOG 			: el_type_t := x"30";
	constant EL_TYP_LOGIC_4IP	: el_type_t := x"40";
	constant EL_TYP_UDP_RAW_MKT: el_type_t := x"50";
	
	
	------------------------------
	-- STATUS transmission types
	------------------------------
	
	constant ST_TX_STATUS : integer := 1;
	constant ST_TX_CONFIG : integer := 2;
	constant ST_TX_EVTLOG : integer := 3;
	
	------------------------------
	-- Market event types
	------------------------------
	
	constant MKT_TYP_TRADE 	: integer := 1;
	constant MKT_TYP_BID 	: integer := 2;
	constant MKT_TYP_ASK 	: integer := 3;
	
	
	
	------------------------------
	-- CONFIG IDS
	------------------------------
	
	-- config IDs for standardised config cmds
	constant EL_C_RESET : config_id_t := x"0";
	constant EL_C_SRC_ROUTE : config_id_t := x"1";	-- xxxx xxxx xxxI xxSS (for source SS on input I)
	constant EL_C_CFG_DONE : config_id_t := x"2";	-- config is done

	------------------------------
	-- SYSTEM CONTROL
	------------------------------

	constant EL_C_STATUS_REQ : config_id_t := x"3";	-- request status for el id [data(5..0)](all if el id = 0)
	constant EL_C_CONFIG_REQ : config_id_t := x"4";	-- request config for el id [data(5..0)]
	

	------------------------------
	-- EMA
	------------------------------
	constant EL_EMA_C_ALPHA : config_id_t := x"3";	-- set EMA alpha parameter
	constant EL_EMA_C_LEN 	: config_id_t := x"4";	-- set EMA length parameter


	------------------------------
	-- Traffic gen v1
	------------------------------
	constant EL_TG1_C_START : config_id_t := x"3";	-- start the traffic generator
	constant EL_TG1_C_LEN 	: config_id_t := x"4";	-- set pattern length
	constant EL_TG1_C_GAP 	: config_id_t := x"5";	-- set gap between generation points
	constant EL_TG1_C_IV 	: config_id_t := x"6";	-- set initial value
	constant EL_TG1_C_P1 	: config_id_t := x"7";	-- set parameter p1

	------------------------------
	-- LOGIC_4IP block
	------------------------------
	constant EL_L4IP_C_OPS 	: config_id_t := x"3";	-- operators
	constant EL_L4IP_C_K1	: config_id_t := x"4";	-- constant K1
	constant EL_L4IP_C_K2	: config_id_t := x"5";	-- constant K2

	------------------------------
	-- MARKET FEED
	------------------------------
	constant EL_MDF_C_PRICE		: config_id_t := x"3";	-- enable price events on op xxxx xxxO (F=off)
	constant EL_MDF_C_VOL		: config_id_t := x"4";	-- enable volume events on op xxxx xxxO (F=off)
	constant EL_MDF_C_UDPPORT	: config_id_t := x"5";	-- UDP port to listen on xxxx PPPP 
	constant EL_MDF_C_MDTYPE	: config_id_t := x"6";	-- market data type xxxx xxTT 
	
	-- Declare functions and procedure

end mat_element_defs;

package body mat_element_defs is

-- Define functions

end mat_element_defs;

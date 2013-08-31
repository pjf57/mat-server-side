package com.pjf.mat.api;

public class MatElementDefs {
	
	public static final int MAX_INSTRUMENTS = 256;
	
	// element IDs for standardised elements
	public static final int EL_ID_SYSTEM_CONTROL = 0;
	public static final int EL_ID_ALL = 0x3f;			// addresses all elements

	// element type IDSs
	public static final int EL_TYP_ROUTER		= 0x01;
	public static final int EL_TYP_EMA 			= 0x10;
	public static final int EL_TYP_MACD			= 0x11;
	public static final int EL_TYP_TG1 			= 0x20;
	public static final int EL_TYP_LOG 			= 0x30;
	public static final int EL_TYP_LOGIC_4IP	= 0x40;
	public static final int EL_TYP_ARITH_4IP	= 0x41;
	public static final int EL_TYP_UDP_RAW_MKT	= 0x50;
	public static final int EL_TYP_UDP_MFD_SYM	= 0x51;
	public static final int EL_TYP_MFD_SYM		= 0x52;
	public static final int EL_TYP_HLOC			= 0x60;
	public static final int EL_TYP_ATR			= 0x61;
	public static final int EL_TYP_ADX			= 0x62;
	public static final int EL_TYP_RMO			= 0x70;

//	--------------------------------
//	-- Standard communications ports
//	--------------------------------

	public static final int CS_PORT_CONFIG		= 2000;		// port for sending config
	public static final int CS_PORT_STATUS		= 2001;		// port for status reports
	public static final int CS_PORT_LOGGER		= 2002;		// port for logger reports
	public static final int CS_RMO_ORDER_PORT 	= 5000;		// src port for RMO orders
	

//	------------------------------
//	-- STATUS transmission types
//	------------------------------
	
	public static final int ST_TX_STATUS 		= 1;
	public static final int ST_TX_CONFIG 		= 2;
	public static final int ST_TX_EVTLOG 		= 3;
	public static final int ST_TX_HWSIG  		= 4;
	public static final int ST_TX_LKUAUDIT		= 5;
	public static final int ST_TX_RTRAUDIT		= 6;
	
//	------------------------------
//	-- Market event types
//	------------------------------
	
	public static final int MKT_TYP_TRADE 		= 1;
	public static final int MKT_TYP_BID 		= 2;
	public static final int MKT_TYP_ASK 		= 3;
	
//	------------------------------
//	-- TICKDATA SELECTOR VALUES
//	------------------------------

	public static final int TDS_BASIC			= 0;	// basic data 	- type.8/mktref.8/instr.8/tickref.8/volume.u32
	public static final int TDS_VOL_PRICE_SP	= 1;	// vol/price 	- vol-sp32/price.sp32
	public static final int TDS_SYMBOL			= 2;	// symbol 		- symbol.64 (rt padded with spaces)
	public static final int TDS_EVT_REF			= 3;	// mkt evt ref - reference.64
	
	
//	------------------------------
//	-- CONFIG IDS
//	------------------------------
	
//	-- SYSTEM CONFIG IDS
	public static final int EL_C_RESET 			= 0x00;
	public static final int EL_C_SRC_ROUTE 		= 0x01;	// xxxx xxxx xxxI xxSS (for source SS on input I)
	public static final int EL_C_CFG_DONE 		= 0x02;	// config is done
	public static final int EL_C_CFG_LKU_TRG	= 0x03;	// xxxx xxxx xxxx xNTT (entry N with target TT)
	public static final int EL_C_CFG_OP_ENA		= 0x04;	// xxxx xxxx xxxx xxxE (set op enable bitmask)
	public static final int EL_C_RESET_CNTRS	= 0x05;	// xxxx xxxx xxxx xxxx

	
//	------------------------------
//	-- EL IDS for SYSTEM CONTROL
//	------------------------------
	// EL config
	public static final int EL_C_LKU_AUDIT_THRESH	= 0x01; // set threshold for lku audit logs autosend
	public static final int EL_C_RTR_AUDIT_THRESH	= 0x02; // set threshold for Router audit logs autosend
	// CMDS
	public static final int EL_C_STATUS_REQ 		= 0x01; // request status for el id [data(5..0)](all if el id = 0)
	public static final int EL_C_CONFIG_REQ 		= 0x02; // request config for el id [data(5..0)]
	public static final int EL_C_HWSIG_REQ  		= 0x03; // request hw signature
	public static final int EL_C_CLKSYNC_REQ  		= 0x04; // request a clk_ts sync [data is clk reference]
	public static final int EL_C_LKU_AUDIT_REQ		= 0x05; // request immed tx of lku audit logs	                                                    	                                         	constant EL_C_LKU_AUDIT_REQ: config_id_t := x"8";	-- request immediate tx of lku audit data
	public static final int EL_C_RTR_AUDIT_REQ		= 0x06; // request immed tx of Router audit logs	                                                    	                                         	constant EL_C_LKU_AUDIT_REQ: config_id_t := x"8";	-- request immediate tx of lku audit data


//	------------------------------
//	-- EMA
//	------------------------------
	public static final int EL_EMA_C_ALPHA 			= 0x03; //  set EMA alpha parameter
	public static final int EL_EMA_C_LEN 			= 0x04; //  set EMA length parameter

//	------------------------------
//	-- MACD
//	------------------------------
	public static final int EL_MACD_C_FS_ALPHA		= 0x03; //  set fast EMA alpha parameter
	public static final int EL_MACD_C_FS_LEN		= 0x04; //  set fast EMA length parameter
	public static final int EL_MACD_C_SL_ALPHA		= 0x05; //  set slow EMA alpha parameter
	public static final int EL_MACD_C_SL_LEN		= 0x06; //  set slow EMA length parameter
	public static final int EL_MACD_C_SG_ALPHA		= 0x07; //  set signal EMA alpha parameter
	public static final int EL_MACD_C_SG_LEN		= 0x08; //  set signal EMA length parameter
	public static final int EL_MACD_C_SET_OP		= 0x09; //  set MACD OP: 0=MACD, 1=SIGNAL, 2=HIST
	
//	------------------------------
//	-- Traffic gen v1
//	------------------------------
	public static final int EL_TG1_C_START 			= 0x03; // start the traffic generator
	public static final int EL_TG1_C_LEN 			= 0x04; // set pattern length
	public static final int EL_TG1_C_GAP 			= 0x05; // set gap between generation points (#clks)
	public static final int EL_TG1_C_IV 			= 0x06; // set initial value
	public static final int EL_TG1_C_P1 			= 0x07; // set parameter p1

//	------------------------------
//	-- LOGIC_4IP block
//	------------------------------
	public static final int EL_L4IP_C_OPS 			= 0x03; // operators
	public static final int EL_L4IP_C_K1			= 0x04; // public static final int K1
	public static final int EL_L4IP_C_K2			= 0x05; // public static final int K2

//	------------------------------
//	-- ARITH_4IP block
//	------------------------------
	public static final int EL_A4IP_C_OPS 			= 0x03; // operators
	public static final int EL_A4IP_C_K1			= 0x04; // public static final int K1
	public static final int EL_A4IP_C_K2			= 0x05; // public static final int K2

	
//	------------------------------
//	-- MARKET FEED
//	------------------------------
	
	public static final int EL_MDF_C_TRADE			= 0x03; // trade evts o/p=O data=V xxxx xxVO (V=1,vol V=0,price O=F,off)
	public static final int EL_MDF_C_BID			= 0x04; // trade evts o/p=O data=V xxxx xxVO (V=1,vol V=0,price O=F,off)
	public static final int EL_MDF_C_ASK			= 0x05; // trade evts o/p=O data=V xxxx xxVO (V=1,vol V=0,price O=F,off)
	public static final int EL_MDF_C_ISYM_L			= 0x06; // instrument symbol (left chars)
	public static final int EL_MDF_C_ISYM_R			= 0x07; // instrument symbol (right chars) (config L first)
	public static final int EL_MDF_C_ISYM_ID		= 0x08; // instrument symbol (ID) (config R first) xxID
	public static final int EL_MDF_C_UDPPORT		= 0x09; // UDP port to listen on xxxx PPPP 
	public static final int EL_MDF_C_MKTID			= 0x0a; // ID of this market xxxx xxID 

	public static final int EL_MFD_C_START			= 0x40; // Start the feeder

	public static final int EL_MDF_L_SYML			= 0x12; // lookup left 4 chars of symbol
	public static final int EL_MDF_L_SYMR			= 0x13; // lookup right 4 chars of symbol

//	------------------------------
//	-- HLOC
//	------------------------------
	public static final int EL_HLOC_C_PERIOD	 	= 0x03; // num ticks in period (int)
	public static final int EL_HLOC_C_OP_METRIC 	= 0x04; // metric to use for data in output events
	public static final int EL_HLOC_C_OP_THROT   	= 0x05; // min # clks between outputs
	
	public static final int EL_HLOC_L_CURR_H		= 0x00; // lookup current high
	public static final int EL_HLOC_L_CURR_L		= 0x01; // lookup current low
	public static final int EL_HLOC_L_CURR_O		= 0x02; // lookup current open
	public static final int EL_HLOC_L_CURR_C		= 0x03; // lookup current close
	public static final int EL_HLOC_L_PREV_H		= 0x04; // lookup previous (N) high
	public static final int EL_HLOC_L_PREV_L		= 0x05; // lookup previous (N) low
	public static final int EL_HLOC_L_PREV_O		= 0x06; // lookup previous (N) open
	public static final int EL_HLOC_L_PREV_C		= 0x07; // lookup previous (N) close
	public static final int EL_HLOC_L_PRVM1_H		= 0x08; // lookup previous (N-1) high
	public static final int EL_HLOC_L_PRVM1_L		= 0x09; // lookup previous (N-1) low
	public static final int EL_HLOC_L_PRVM1_O		= 0x0a; // lookup previous (N-1) open
	public static final int EL_HLOC_L_PRVM1_C		= 0x0b; // lookup previous (N-1) close
	
//	------------------------------
//	-- ATR
//	------------------------------
	public static final int EL_ATR_C_ALPHA 			= 0x03; // set EMA alpha parameter
	public static final int EL_ATR_C_LEN 			= 0x04; // set EMA length parameter
	public static final int EL_ATR_C_IP_CN1			= 0x05; // boolean. bit 0 indicates that evt has Close(N-1) data

	public static final int EL_ATR_L_ATR			= 0x10; // lookup current ATR value
	

//	------------------------------
//	-- ADX
//	------------------------------
	public static final int EL_ADX_C_PDN_ALPHA 		= 0x03; // set EMA alpha parameter for PDN EMA
	public static final int EL_ADX_C_PDN_LEN 		= 0x04; // set EMA length parameter for PDN EMA
	public static final int EL_ADX_C_NDN_ALPHA 		= 0x05; // set EMA alpha parameter for NDN EMA
	public static final int EL_ADX_C_NDN_LEN 		= 0x06; // set EMA length parameter for NDN EMA
	public static final int EL_ADX_C_ADX_ALPHA 		= 0x07; // set EMA alpha parameter for ADX EMA
	public static final int EL_ADX_C_ADX_LEN 		= 0x08; // set EMA length parameter for ADX EMA

	public static final int EL_ADX_L_ADX			= 0x11; // lookup current ADX value

//	------------------------------
//	-- RMO
//	------------------------------
	public static final int EL_RMO_C_UDPIP	 		= 0x03; // set market order IP address
	public static final int EL_RMO_C_UDPPORT	 	= 0x04; // set market order IP port
	public static final int EL_RMO_C_MIN_VOL	 	= 0x05; // set min order volume
	public static final int EL_RMO_C_MAX_VOL	 	= 0x06; // set max order volume
	public static final int EL_RMO_C_MAX_POSN	 	= 0x07; // set max position
	

// ---------------------- utilities ----------------------------------------
	
	public static String LkuOpToString(int op) {
		String str = "";
		switch (op) {
		case EL_HLOC_L_CURR_H:	str = "High(c)   ";		break;
		case EL_HLOC_L_CURR_L:	str = "Low(c)    ";		break;
		case EL_HLOC_L_CURR_O:	str = "Open(c)   ";		break;
		case EL_HLOC_L_CURR_C:	str = "Close(c)  ";		break;
		case EL_HLOC_L_PREV_H:	str = "High(n)   ";		break;
		case EL_HLOC_L_PREV_L:	str = "Low(n)    ";		break;
		case EL_HLOC_L_PREV_O:	str = "Open(n)   ";		break;
		case EL_HLOC_L_PREV_C:	str = "Close(n)  ";		break;
		case EL_HLOC_L_PRVM1_H:	str = "High(n-1) ";		break;
		case EL_HLOC_L_PRVM1_L:	str = "Low(n-1)  ";		break;
		case EL_HLOC_L_PRVM1_O:	str = "Open(n-1) ";		break;
		case EL_HLOC_L_PRVM1_C:	str = "Close(n-1)";		break;
		case EL_ATR_L_ATR:		str = "ATR(c)    ";		break;
		case EL_ADX_L_ADX:		str = "ADX(c)    ";		break;
		default:		str = "Unknown[" + op + "]";	break;
		}
		return str;
	}
	
	public static String ElementTypeToString(int type) {
		String typeStr ="";
		switch(type) {
		case MatElementDefs.EL_TYP_ROUTER		: typeStr = "Router";		break;
		case MatElementDefs.EL_TYP_MACD			: typeStr = "MACD";			break;
		case MatElementDefs.EL_TYP_HLOC			: typeStr = "HLOC";			break;
		case MatElementDefs.EL_TYP_ATR			: typeStr = "ATR";			break;
		case MatElementDefs.EL_TYP_ADX			: typeStr = "ADX";			break;
		case MatElementDefs.EL_TYP_TG1			: typeStr = "TG1";			break;
		case MatElementDefs.EL_TYP_EMA			: typeStr = "EMA";			break;
		case MatElementDefs.EL_TYP_LOG			: typeStr = "LOGGER";		break;
		case MatElementDefs.EL_TYP_LOGIC_4IP	: typeStr = "Logic_4IP";	break;
		case MatElementDefs.EL_TYP_ARITH_4IP	: typeStr = "Arith_4IP";	break;
		case MatElementDefs.EL_TYP_UDP_RAW_MKT	: typeStr = "UDPrawMKT";	break;
		case MatElementDefs.EL_TYP_RMO			: typeStr = "RMO";			break;
		default					: typeStr = "unknown(" + type + ")";		break;
		}
		return typeStr;
	}
}
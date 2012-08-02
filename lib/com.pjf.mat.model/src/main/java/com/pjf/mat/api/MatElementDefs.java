package com.pjf.mat.api;

public interface MatElementDefs {
	
	public final int MAX_INSTRUMENTS = 256;
	
	// element IDs for standardised elements
	public final int EL_ID_SYSTEM_CONTROL = 0;

	// element type IDSs
	public final int EL_TYP_ROUTER		= 0x01;
	public final int EL_TYP_EMA 		= 0x10;
	public final int EL_TYP_TG1 		= 0x20;
	public final int EL_TYP_LOG 		= 0x30;
	public final int EL_TYP_LOGIC_4IP	= 0x40;
	public final int EL_TYP_ARITH_4IP	= 0x41;
	public final int EL_TYP_UDP_RAW_MKT	= 0x50;
	public final int EL_TYP_HLOC		= 0x60;
	public final int EL_TYP_ATR			= 0x61;
	public final int EL_TYP_ADX			= 0x62;
	
//	------------------------------
//	-- STATUS transmission types
//	------------------------------
	
	public final int ST_TX_STATUS = 1;
	public final int ST_TX_CONFIG = 2;
	public final int ST_TX_EVTLOG = 3;
	public final int ST_TX_HWSIG  = 4;
	
//	------------------------------
//	-- Market event types
//	------------------------------
	
	public final int MKT_TYP_TRADE 	= 1;
	public final int MKT_TYP_BID 	= 2;
	public final int MKT_TYP_ASK 	= 3;
	
	
	
//	------------------------------
//	-- CONFIG IDS
//	------------------------------
	
//	-- config IDs for standardised config cmds
	public final int EL_C_RESET 			= 0x00;
	public final int EL_C_SRC_ROUTE 		= 0x01;	// xxxx xxxx xxxI xxSS (for source SS on input I)
	public final int EL_C_CFG_DONE 			= 0x02;	// config is done

//	------------------------------
//	-- SYSTEM CONTROL
//	------------------------------

	public final int EL_C_STATUS_REQ 		= 0x03; // request status for el id [data(5..0)](all if el id = 0)
	public final int EL_C_CONFIG_REQ 		= 0x04; // request config for el id [data(5..0)]
	public final int EL_C_HWSIG_REQ  		= 0x05; // request hw signature
	public final int EL_C_CLKSYNC_REQ  		= 0x06; // request a clk_ts sync [data is clk reference]

//	------------------------------
//	-- EMA
//	------------------------------
	public final int EL_EMA_C_ALPHA 		= 0x03; //  set EMA alpha parameter
	public final int EL_EMA_C_LEN 			= 0x04; // set EMA length parameter


//	------------------------------
//	-- Traffic gen v1
//	------------------------------
	public final int EL_TG1_C_START 		= 0x03; // start the traffic generator
	public final int EL_TG1_C_LEN 			= 0x04; // set pattern length
	public final int EL_TG1_C_GAP 			= 0x05; // set gap between generation points (#clks)
	public final int EL_TG1_C_IV 			= 0x06; // set initial value
	public final int EL_TG1_C_P1 			= 0x07; // set parameter p1

//	------------------------------
//	-- LOGIC_4IP block
//	------------------------------
	public final int EL_L4IP_C_OPS 			= 0x03; // operators
	public final int EL_L4IP_C_K1			= 0x04; // public final int K1
	public final int EL_L4IP_C_K2			= 0x05; // public final int K2

//	------------------------------
//	-- ARITH_4IP block
//	------------------------------
	public final int EL_A4IP_C_OPS 			= 0x03; // operators
	public final int EL_A4IP_C_K1			= 0x04; // public final int K1
	public final int EL_A4IP_C_K2			= 0x05; // public final int K2

	
//	------------------------------
//	-- MARKET FEED
//	------------------------------
	public final int EL_MDF_C_PRICE	 		= 0x03; // enable price events on op xxxx xxxO (F=off)
	public final int EL_MDF_C_VOL			= 0x04; // enable volume events on op xxxx xxxO (F=off)
	public final int EL_MDF_C_UDPPORT		= 0x05; // UDP port to listen on xxxx PPPP 
	public final int EL_MDF_C_MDTYPE		= 0x06; // market data type xxxx xxTT 
	
//	------------------------------
//	-- HLOC
//	------------------------------
	public final int EL_HLOC_C_PERIOD	 	= 0x03; // num ticks in period (int)
	public final int EL_HLOC_C_OP_METRIC 	= 0x04; // metric to use for data in output events
	public final int EL_HLOC_C_OP_THROT   	= 0x05; // min # clks between outputs
	
	public final int EL_HLOC_L_CURR_H		= 0x00; // lookup current high
	public final int EL_HLOC_L_CURR_L		= 0x01; // lookup current low
	public final int EL_HLOC_L_CURR_O		= 0x02; // lookup current open
	public final int EL_HLOC_L_CURR_C		= 0x03; // lookup current close
	public final int EL_HLOC_L_PREV_H		= 0x04; // lookup previous (N) high
	public final int EL_HLOC_L_PREV_L		= 0x05; // lookup previous (N) low
	public final int EL_HLOC_L_PREV_O		= 0x06; // lookup previous (N) open
	public final int EL_HLOC_L_PREV_C		= 0x07; // lookup previous (N) close
	public final int EL_HLOC_L_PRVM1_H		= 0x08; // lookup previous (N-1) high
	public final int EL_HLOC_L_PRVM1_L		= 0x09; // lookup previous (N-1) low
	public final int EL_HLOC_L_PRVM1_O		= 0x0a; // lookup previous (N-1) open
	public final int EL_HLOC_L_PRVM1_C		= 0x0b; // lookup previous (N-1) close
	
//	------------------------------
//	-- ATR
//	------------------------------
	public final int EL_ATR_C_ALPHA 		= 0x03; // set EMA alpha parameter
	public final int EL_ATR_C_LEN 			= 0x04; // set EMA length parameter
	public final int EL_ATR_C_IP_CN1		= 0x05; // boolean. bit 0 indicates that evt has Close(N-1) data

	public final int EL_ATR_L_ATR			= 0x10; // lookup current ATR value
	

//	------------------------------
//	-- ADX
//	------------------------------
	public final int EL_ADX_C_PDN_ALPHA 	= 0x03; // set EMA alpha parameter for PDN EMA
	public final int EL_ADX_C_PDN_LEN 		= 0x04; // set EMA length parameter for PDN EMA
	public final int EL_ADX_C_NDN_ALPHA 	= 0x05; // set EMA alpha parameter for NDN EMA
	public final int EL_ADX_C_NDN_LEN 		= 0x06; // set EMA length parameter for NDN EMA
	public final int EL_ADX_C_ADX_ALPHA 	= 0x07; // set EMA alpha parameter for ADX EMA
	public final int EL_ADX_C_ADX_LEN 		= 0x08; // set EMA length parameter for ADX EMA

	public final int EL_ADX_L_ADX			= 0x11; // lookup current ADX value
	
}
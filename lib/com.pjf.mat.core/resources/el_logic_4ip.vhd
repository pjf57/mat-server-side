----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date:    19:15:23 04/30/2012 
-- Design Name: 
-- Module Name:    el_logic_4ip - Behavioral 
-- Project Name: 
-- Target Devices: 
-- Tool versions: 
-- Description: 
--						implements configuable logic: Z = p lop q, lop = (and/or/nand/nor) or p/q/!p/!q
--								p = A cf1 x, x = B|K1, cf1 = (</<=/>/>=/!=)
--								q = C cf2 y, y = D|K2, cf2 = (</<=/>/>=/!=)
--						where operations are configurable as are K1, K2.
--						A,B,C,D are ip0,ip1,ip2,ip3 resectively
--						all data is sp fp.
--						output z generates an event 1 on transition to true, and an event 0 on transition to false.
--
--						config is 
--									 15 12 11  8 7  6 5  4 3 0
--						  			---------------------------
--						C_OPS		| cf1 | cf2 |xsel|ysel|lop|
--						  			---------------------------
--
--										cf        x/y sel    lop
--										--		  -------	 ---
--									  0000 EQ     00 IP		0000 p and q
--									  0001 LT     01 K		0001 p or q
--									  0010 LE				0010 p nand q
--								      0011 GT				0011 p nor q
--									  0100 GE				0100 p
--									  0101 NE				0101 q
--															0110 not p
--															0111 not q
--
--						C_K1, C_K2
--
--						initial implementation is for one instr only.
-- Dependencies: 
--
-- Revision: 
-- Revision 0.01 - File Created
-- Additional Comments: 
--
----------------------------------------------------------------------------------
library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.NUMERIC_STD.ALL;
use IEEE.std_logic_unsigned.ALL;
use work.mat_common.all;
use work.mat_element_defs.all;

entity el_logic_4ip is
    Port (
			-- Input port
			event_in					: in event_t;						-- the event input to the element
			ip_valid					: in std_logic;					-- input event is valid
			ip_ready					: out std_logic;					-- we are ready to receive event
			-- outputs
			event_out				: out event_t;						-- the event output from the element
			valid_out				: out std_logic;					-- input event is valid
			op_ready					: in std_logic;					-- we are ready to receive event			
			-- system
			id							: in element_id_t;				-- ID of this element
			time_clock				: in time_clock_t;				-- the current time
			config_rdy				: out std_logic;					-- element is ready to be configured
			config_in				: in config_t;						-- configuration
			status_rq				: in status_req_t;				-- requests for status from element
			status_stream			: out status_stream_t;			-- status data stream from element
			status_data_read		: in std_logic;					-- status byte has been read			
			initialised				: out std_logic;					-- element is initialised
			clk						: in std_logic;
			reset						: in std_logic
	);
end el_logic_4ip;

architecture Behavioral of el_logic_4ip is


COMPONENT el_basis
	 Generic (
			NUM_IP : integer := 1									-- number of inputs
			);
    Port (
			-- Input port
			event_in					: in event_t;						-- the event input to the element
			ip_valid					: in std_logic;					-- input event is valid
			ip_ready					: out std_logic;					-- we are ready to receive events
			-- outputs
			event_out				: out event_t;						-- the event output from the element
			valid_out				: out std_logic;					-- input event is valid
			op_ready					: in std_logic;					-- we are ready to receive event
			-- element port
			el_config				: out el_config_t;				-- config to element
			el_status				: in el_status_t;					-- status and requests from element
			el_reset					: out std_logic;					-- reset the element
			el_ip_events			: out el_ip_bus_t;				-- input events passed to the element
			el_op_event				: in el_event_op_t;				-- event to output
			el_op_taken				: out std_logic;					-- indicates that the op event has been taken
			el_time_clock			: out time_clock_t;				-- timeclock to element
			-- system
			id							: in element_id_t;				-- ID of this element
			time_clock				: in time_clock_t;				-- the current time
			config_rdy				: out std_logic;					-- element is ready to be configured
			config_in				: in config_t;						-- configuration
			status_rq				: in status_req_t;				-- requests for status from element
			status_stream			: out status_stream_t;			-- status data stream from element
			status_data_read		: in std_logic;					-- status byte has been read			
			initialised				: out std_logic;					-- element is initialised			
			clk						: in std_logic;
			reset						: in std_logic
	);
END COMPONENT;

COMPONENT sp_comp_L1
  PORT (
    aclk 						: IN STD_LOGIC;
    s_axis_a_tvalid 			: IN STD_LOGIC;
    s_axis_a_tdata 			: IN STD_LOGIC_VECTOR(31 DOWNTO 0);
    s_axis_b_tvalid 			: IN STD_LOGIC;
    s_axis_b_tdata 			: IN STD_LOGIC_VECTOR(31 DOWNTO 0);
    s_axis_operation_tvalid : IN STD_LOGIC;
    s_axis_operation_tdata : IN STD_LOGIC_VECTOR(7 DOWNTO 0);
    m_axis_result_tvalid 	: OUT STD_LOGIC;
    m_axis_result_tdata 	: OUT STD_LOGIC_VECTOR(7 DOWNTO 0)
  );
END COMPONENT;

	type state_t is (INIT,CFG,RST,RST_LVI_RAM,IDLE,WAIT_RAM1,WAIT_RAM2,WAIT_COMP);
	
	-- function to cvt state into a byte value to send as status
	function state_value(state : state_t) return el_state_t is
		variable ret : el_state_t;
	begin
		case state is
			when INIT		=> ret := x"01";
			when CFG 		=> ret := x"02";
			when RST 		=> ret := x"03";
			when IDLE 		=> ret := x"04";
			when WAIT_COMP => ret := x"05";
			when others 	=> ret := x"ff";
		end case;
		return ret;
	end state_value;
	
	type lv_ram_t is array (0 to MAX_INSTRUMENTS) of data_t;		-- store for holding value by instr
	
	type lvi_ram_t is array (0 to 3) of lv_ram_t;					-- for holindg last value at each input
	type lvi_t is array (0 to 3) of data_t;
	type z_ram_t is array (0 to MAX_INSTRUMENTS) of std_logic;
	
	type set_config_t is (HOLD,RST,SET_OPS,SET_K1,SET_K2);
	
	type set_clr_t is (CLR,SET,HOLD);
	type set_cntr_t is (RST,INCR,HOLD);
	type set_scntr_t is (RST,INCR,HOLD,SET);
	
	constant INVALID_FP : data_t := x"ffffffff";					-- invalid sp fp value to indicate

	-- basis interconnect busses
	signal el_config					: el_config_t;			-- config to element
	signal el_status					: el_status_t;			-- status and controls from element
	signal el_reset					: std_logic;			-- reset the element
	signal el_ip_events				: el_ip_bus_t(3 downto 0);		-- input events passed to the element
	signal el_op_event				: el_event_op_t;		-- event to output
	signal el_op_taken				: std_logic;			-- indicates that the op event has been taken
	signal el_time_clock				: time_clock_t;		-- timeclock to element
	
	-- state
	signal state						: state_t;
	signal lvi_ram						: lvi_ram_t;			-- last value by instr on each input
	signal z_ram						: z_ram_t;				-- last value of output per instrument 	
	signal instr_idx_reg				: integer := 0;
	signal c_k1							: data_t;								-- K1
	signal c_k2							: data_t;								-- K2
	signal c_cf1						: std_logic_vector(3 downto 0);	-- op for ip0/ip1
	signal c_cf2						: std_logic_vector(3 downto 0);	-- op for ip3/ip4
	signal c_xsel						: std_logic_vector(1 downto 0);	-- op for x selection
	signal c_ysel						: std_logic_vector(1 downto 0);	-- op for y selection
	signal c_lop						: std_logic_vector(3 downto 0);	-- op for p/q
	signal ips_needed					: std_logic_vector(3 downto 0);	-- which inputs are needed for the calc
	-- busses
	signal next_state					: state_t;
	signal z								: std_logic;							-- computed z value
	signal next_ips_needed			: std_logic_vector(3 downto 0);
	signal instr_idx					: integer := 0;
	signal lvi							: lvi_t;									-- last input values, read from ram
	signal last_z						: std_logic;							-- (last) z for an instr
	signal ip_idx						: integer := 0;
	signal lvi_val						: data_t;
	signal comp1_result				: std_logic_vector(7 downto 0);
	signal comp2_result				: std_logic_vector(7 downto 0);
	signal p								: std_logic;
	signal q								: std_logic;
	signal x_bus						: data_t;
	signal y_bus						: data_t;
	signal comp1_oper					: std_logic_vector(7 downto 0);
	signal comp2_oper					: std_logic_vector(7 downto 0);
	signal comp1_valid				: std_logic;
	signal comp2_valid				: std_logic;
	-- controls
	signal set_config					: set_config_t;
	signal start_calc					: std_logic;
	signal wr_lvi_ram					: std_logic_vector(3 downto 0);	-- wr control for each input
	signal set_instr_idx_reg		: set_scntr_t;
	signal got_inputs					: std_logic;
	signal wr_z_ram				: std_logic;
	
begin
	combinatorial: process(
			-- inputs
			el_config, el_reset, el_ip_events, el_op_taken, el_time_clock,
			-- state
			state, c_k1, c_k2, c_cf1, c_cf2, c_xsel, c_ysel, c_lop, z_ram, ips_needed, 
			instr_idx_reg, lvi_ram, 
			-- busses
			next_state, next_ips_needed, instr_idx, lvi_val, ip_idx, lvi, 
			comp1_result, comp2_result, last_z, 
			x_bus, y_bus, p, q, z, comp1_valid, comp2_valid, comp1_oper, comp2_oper,
			-- controls
			set_config, start_calc, wr_lvi_ram, set_instr_idx_reg, got_inputs, wr_z_ram
			)
		variable got_new : std_logic;
	begin
		-- output followers
		el_op_event.instrument_id <= instrument_id_t(to_unsigned(instr_idx,8));
		el_op_event.data <= (others => '0');
		el_op_event.data(0) <= z;
		el_op_event.enable <= '1';			
		
		-- initial values for combinatorially calculated outputs
		el_op_event.valid <= '0';
		el_status.ip_evts_processed <= '0';
		
		-- init controls
		set_config <= HOLD;
		start_calc <= '0';
		wr_lvi_ram <= (others => '0');
		set_instr_idx_reg <= HOLD;
		got_inputs <= '0';
		wr_z_ram <= '0';
		
		-- init busses
		el_status <= empty_el_status;
		el_status.el_type <= EL_TYP_LOGIC_4IP;
		el_status.el_int_state <= state_value(state);
		next_state <= state;		-- default is to stay in the same state
		z <= last_z;	
		next_ips_needed <= ips_needed;
		instr_idx <= instr_idx_reg;			-- keep rams indexed on the instrument id reg by default
		lvi_val <= (others => '0');
		ip_idx <= 0;
						
		-- combinatorial logic
		case c_xsel is
			when "00" => x_bus <= lvi(1);
			when "01" => x_bus <= c_k1;
			when others => x_bus <= (others => '0');
		end case;
		
		case c_ysel is
			when "00" => y_bus <= lvi(3);
			when "01" => y_bus <= c_k2;
			when others => y_bus <= (others => '0');
		end case;
		
		case c_cf1 is
			when "0000" => comp1_oper <= "00010100";		-- EQ
			when "0001" => comp1_oper <= "00001100";		-- LT
			when "0010" => comp1_oper <= "00011100";		-- LE
			when "0011" => comp1_oper <= "00100100";		-- GT
			when "0100" => comp1_oper <= "00110100";		-- GE
			when "0101" => comp1_oper <= "00101100";		-- NE
			when others => comp1_oper <= "00010100";		-- EQ
		end case;
			
		case c_cf2 is
			when "0000" => comp2_oper <= "00010100";		-- EQ
			when "0001" => comp2_oper <= "00001100";		-- LT
			when "0010" => comp2_oper <= "00011100";		-- LE
			when "0011" => comp2_oper <= "00100100";		-- GT
			when "0100" => comp2_oper <= "00110100";		-- GE
			when "0101" => comp2_oper <= "00101100";		-- NE
			when others => comp2_oper <= "00010100";		-- EQ
		end case;
		
		p <= comp1_result(0);
		q <= comp2_result(0);
			
		-- FSM
		
		case state is

			when INIT =>
					next_state <= CFG;
			
			when CFG =>
				if el_config.valid = '1' then
					case el_config.config_id is
						when EL_L4IP_C_OPS 	=> set_config <= SET_OPS;
						when EL_L4IP_C_K1 	=> set_config <= SET_K1;						
						when EL_L4IP_C_K2 	=> set_config <= SET_K2;						
						when others				=> -- ignore	
					end case;
				end if;
				if el_config.reset = '1' then
					set_config <= RST;
				end if;
				if el_reset = '1' then
					next_state <= RST;
				end if;

			when RST =>
				-- reset the entire previous state
				-- figure out which inputs we need data on, start with all needed, then knock some out
				next_ips_needed <= (others => '1');
				case c_lop is
					when "0100" => -- p
						next_ips_needed(2) <= '0'; -- no q, so no ip2
						next_ips_needed(3) <= '0'; -- no q, so no ip3
					when "0101" => -- q
						next_ips_needed(0) <= '0'; -- no p, so no ip0
						next_ips_needed(1) <= '0'; -- no p, so no ip1
					when "0110" => -- not p
						next_ips_needed(2) <= '0'; -- no q, so no ip2
						next_ips_needed(3) <= '0'; -- no q, so no ip3
					when "0111" => -- not q
						next_ips_needed(0) <= '0'; -- no p, so no ip0
						next_ips_needed(1) <= '0'; -- no p, so no ip1
					when others => -- do nothing;
				end case;

				case c_xsel is
					when "01" => next_ips_needed(1) <= '0';	-- k1, so no ip1
					when others => -- do nothing;
				end case;
				
				case c_ysel is
					when "01" => next_ips_needed(3) <= '0';	-- k2, so no ip3
					when others =>  -- do nothing;
				end case;
				set_instr_idx_reg <= RST;
				next_state <= RST_LVI_RAM;
				
			when RST_LVI_RAM =>
				instr_idx <= instr_idx_reg;
				lvi_val <= INVALID_FP;
				wr_lvi_ram <= (others => '1');		-- write for all inputs
				z <= '0';
				wr_z_ram <= '1';						-- and init op state as well
				set_instr_idx_reg <= INCR;
				if instr_idx = MAX_INSTRUMENTS then
					set_instr_idx_reg <= RST;
					el_status.initialised <= '1';
					next_state <= IDLE;
				end if;						
				
			when IDLE =>
				got_new := '0';
				for i in 0 to 3 loop
					if el_ip_events(i).new_evt = '1' then
						got_new := '1';
						ip_idx <= i;
						exit;
					end if;
				end loop;
				
				if got_new = '1' then
					-- capture the instrument_id from the event, and write the new ip data to the lvi ram
					lvi_val <= el_ip_events(ip_idx).evt.data;
					instr_idx <= to_integer(el_ip_events(ip_idx).evt.instrument_id);
					set_instr_idx_reg <= SET;
					wr_lvi_ram(ip_idx) <= '1';
					el_status.ip_evts_processed <= '1';
					next_state <= WAIT_RAM1;
				end if;
				
			when WAIT_RAM1 =>
				next_state <= WAIT_RAM2;
				
			when WAIT_RAM2 =>
				-- have the four last input values read from ram
				-- do we have enough inputs to start a calc?		
				got_inputs <= '1';
				for i in 0 to 3 loop
					if ips_needed(i) = '1' and lvi(i) = INVALID_FP then
						got_inputs <= '0';
						exit;
					end if;
				end loop;				
				if got_inputs = '1' then
					-- rerun the logic to calc new op value
					start_calc <= '1';
					next_state <= WAIT_COMP;
				else
					-- go back and wait for further inputs
					next_state <= IDLE;				
				end if;

			when WAIT_COMP =>
				-- wait until comparators are ready with their result
				if comp1_valid = '1' and comp2_valid = '1' then
					-- evaluate p lop q
					case c_lop is
						when "0000" => z <= p and q;
						when "0001" => z <= p or q;
						when "0010" => z <= p nand q;
						when "0011" => z <= p nor q;
						when "0100" => z <= p;
						when "0101" => z <= q;
						when "0110" => z <= not p;
						when "0111" => z <= not q;
						when others => -- do nothing;
					end case;
					
					-- check if op state (z) will change
					if z /= last_z then
						-- yes - write new value into z_ram, and generate event
						wr_z_ram <= '1';
						el_op_event.valid <= '1';
					end if;
					next_state <= IDLE;
				end if;
		end case;
		
	end process;

	sequential: process(clk)
	begin
		if rising_edge(clk) then
			-- block rams indexed by instrument
			for i in 0 to 3 loop
				lvi(i) <= lvi_ram(i)(instr_idx);
			end loop;
			for i in 0 to 3 loop
				if wr_lvi_ram(i) = '1' then
					lvi_ram(i)(instr_idx) <= lvi_val;
				end if;
			end loop;
			last_z <= z_ram(instr_idx);
			if wr_z_ram = '1' then
				z_ram(instr_idx) <= z;
			end if;

			if reset = '1' then
				-- reset processing
				state <= INIT;
				ips_needed <= (others => '0');
				instr_idx_reg <= 0;
				c_cf1 <= (others => '0');
				c_cf2 <= (others => '0');
				c_xsel <= (others => '0');
				c_ysel <= (others => '0');
				c_lop <= (others => '0');
				c_k1 <= (others => '0');
				c_k2 <= (others => '0');
			else
				-- normal (non reset) processing
				
				-- state transition
				state <= next_state;
				ips_needed <= next_ips_needed;

				-- instrument number reg
				case set_instr_idx_reg is
					when RST  => instr_idx_reg <= 0;
					when INCR => instr_idx_reg <= instr_idx_reg + 1;
					when HOLD => instr_idx_reg <= instr_idx_reg;
					when SET  => instr_idx_reg <= instr_idx;
				end case;
				
				-- config
				case set_config is
					when RST => 
						c_cf1 <= (others => '0');
						c_cf2 <= (others => '0');
						c_xsel <= (others => '0');
						c_ysel <= (others => '0');
						c_lop <= (others => '0');
						c_k1 <= (others => '0');
						c_k2 <= (others => '0');

					when SET_K1 => c_k1 <= el_config.data;					
					when SET_K2 => c_k2 <= el_config.data;	

					when SET_OPS =>
						c_cf1 <= el_config.data(15 downto 12);
						c_cf2 <= el_config.data(11 downto 8);
						c_xsel <= el_config.data(7 downto 6);
						c_ysel <= el_config.data(5 downto 4);
						c_lop <= el_config.data(3 downto 0);

					when HOLD		=>	-- do nothing
				end case;
				
			end if;
		end if;
		
	end process;



--------------------------------
-- Instantiate fp components
--------------------------------

comp1 : sp_comp_L1
  PORT MAP (
    aclk 						=> clk,
    s_axis_a_tvalid 			=> start_calc,
    s_axis_a_tdata 			=> lvi(0),
    s_axis_b_tvalid			=> start_calc,
    s_axis_b_tdata 			=> x_bus,
    s_axis_operation_tvalid => start_calc,
    s_axis_operation_tdata => comp1_oper,
    m_axis_result_tvalid 	=> comp1_valid,
    m_axis_result_tdata		=> comp1_result
  );

comp2 : sp_comp_L1
  PORT MAP (
    aclk 						=> clk,
    s_axis_a_tvalid 			=> start_calc,
    s_axis_a_tdata 			=> lvi(2),
    s_axis_b_tvalid			=> start_calc,
    s_axis_b_tdata 			=> y_bus,
    s_axis_operation_tvalid => start_calc,
    s_axis_operation_tdata => comp2_oper,
    m_axis_result_tvalid 	=> comp2_valid,
    m_axis_result_tdata		=> comp2_result
  );



--------------------------------
-- Instantiate basis component
--------------------------------

basis: el_basis
	 Generic map (
			NUM_IP 					=> 4
			)
    Port map(
			-- Input port
			event_in					=> event_in,
			ip_valid					=> ip_valid,
			ip_ready					=> ip_ready,
			-- outputs
			event_out				=> event_out,
			valid_out				=> valid_out,
			op_ready					=> op_ready,
			-- element port
			el_config				=> el_config,
			el_status				=> el_status,
			el_reset					=> el_reset,
			el_ip_events			=> el_ip_events,
			el_op_event				=> el_op_event,
			el_op_taken				=> el_op_taken,
			el_time_clock			=> el_time_clock,
			-- system
			id							=> id,
			time_clock				=> time_clock,
			config_rdy				=> config_rdy,
			config_in				=> config_in,
			status_rq				=> status_rq,
			status_stream			=> status_stream,
			status_data_read		=> status_data_read,
			initialised				=> initialised,
			clk						=> clk,
			reset						=> reset
	);


end Behavioral;

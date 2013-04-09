package com.pjf.marketsim;

import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;


public class MarketSim {
	private final static Logger logger = Logger.getLogger(MarketSim.class);
	private final EventFeedInt feed;
	
	public MarketSim() throws SocketException, UnknownHostException {
		feed = new BasicEventFeed("192.168.0.9",15000);
	}

	public void send() throws Exception {
		feed.sendTradeBurst("resources/GLP_27667_1.csv",1,4,0);
	}

	
	public static void main(String[] args) {
		try {
			MarketSim sim = new MarketSim();
			sim.send();
		} catch (Exception e) {
			logger.error("Outer error catcher: " + e.getMessage());
			e.printStackTrace();
		}		
	}

}

package com.pjf.marketsim;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

public class DataSource {
	private final static Logger logger = Logger.getLogger(DataSource.class);
	private FileInputStream fstream = null;
	DataInputStream in = null;
	BufferedReader br = null;
	
	public DataSource(String source) throws Exception {
		try{
			fstream = new FileInputStream(source);
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));
			br.readLine(); // read and discard headers
		} catch (Exception e) {
			logger.error("unable to initialise DataSource:" + source);
			throw new Exception("unable to initialise DataSource:" + source,e);
		}
	}
	
	public TickData getNext() throws Exception {
		String line = br.readLine();
		if (line == null) {
			throw new Exception("End of file");
		}
		String[] cols = line.split(";");
		int instr_id = Integer.parseInt(cols[2]);
		float price = Float.parseFloat(cols[3]);
		float vol = Float.parseFloat(cols[4]);
		TickData data = new TickData(1,instr_id,price,vol);
		return data;
	}

}

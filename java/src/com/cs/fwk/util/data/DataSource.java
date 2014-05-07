package com.cs.fwk.util.data;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DataSource {
//	private final static Logger logger = Logger.getLogger(DataSource.class);
	private InputStream stream = null;
	DataInputStream in = null;
	BufferedReader br = null;
	
	public DataSource(InputStream source) throws Exception {		
		try{
			stream = source;
			in = new DataInputStream(stream);
			br = new BufferedReader(new InputStreamReader(in));
			br.readLine(); // read and discard headers
		} catch (Exception e) {
//			logger.error("unable to initialise DataSource:" + source);
			throw new Exception("unable to initialise DataSource:" + source,e);
		}
	}
	
	public DataSource(String source) throws Exception {
		this(new FileInputStream(source));
	}
	
	public TickData getNext() throws Exception {
		String line = br.readLine();
		if (line == null) {
			throw new Exception("End of file");
		}
		String[] cols = line.split(";");
		String sym = cols[2];
		float price = Float.parseFloat(cols[3]);
		float vol = Float.parseFloat(cols[4]);
		MarketEventType evt = new MarketEventType(cols[5]);
		TickData data = new TickData(evt,sym,price,vol);
		return data;
	}

}

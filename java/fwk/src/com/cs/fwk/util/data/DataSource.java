package com.cs.fwk.util.data;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

public class DataSource {
	private final static Logger logger = Logger.getLogger(DataSource.class);
	private String path;		// path to stream
	private InputStream stream = null;
	DataInputStream in = null;
	BufferedReader br = null;
	private boolean loop;			// true if want to loop data for greater length
	
	public DataSource(InputStream source) throws Exception {
		this.path = null;
		loop = false;
		stream = source;
		openStream();
	}
	

	/**
	 * Create a data source with specified source path
	 * 
	 * @param source path to source
	 * @throws Exception
	 */
	public DataSource(String source) throws Exception {
		this.path = source;
		loop = false;
		openStream();
	}

	/**
	 * Set data source looping
	 * 
	 * @param loop true if want to loop datasource, false if throw exception on end
	 */
	public void setLoop(boolean loop) {
		this.loop = loop;
	}
	
	public TickData getNext() throws Exception {
		String line = br.readLine();
		if (line == null) {
			if (!loop) {
				logger.info("End of file.");
				throw new Exception("End of file");
			}
			// loop round to begining of file again
			logger.info("Rewinding to beginning of file data.");
			openStream();
			line = br.readLine();
			if (line == null) {
				logger.error("Unable to rewind file.");
				throw new Exception("Unable to rewind file");
			}
		}
		String[] cols = line.split(",");
		String sym = cols[2];
		float price = Float.parseFloat(cols[3]);
		float vol = Float.parseFloat(cols[4]);
		MarketEventType evt = new MarketEventType(cols[5]);
		TickData data = new TickData(evt,sym,price,vol);
		return data;
	}

	/**
	 * Open or reopen a stream for input
	 * 
	 * @param source
	 * @throws Exception
	 */
	private void openStream() throws Exception {
		try {
			if (path != null) {
				stream = new FileInputStream(path);
			}
			in = new DataInputStream(stream);
			br = new BufferedReader(new InputStreamReader(in));
			br.readLine(); // read and discard headers
		} catch (Exception e) {
			logger.error("unable to initialise DataSource: path=" + path + ": stream=" + stream);
			throw new Exception("unable to initialise DataSource: path=" + path + ": stream=" + stream,e);
		}
	}

}


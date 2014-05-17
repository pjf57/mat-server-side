package com.cs.fwk.test;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class DupChecker {
	InputStream fis = null;
	BufferedReader br = null;
	long numLines = 0;
	long numDups = 0;
	long[] cbEvtCntVector = new long[40];
	
	class Data {
		private String line;
		private int tickref=0;
		private int src=0;
		
		public Data(String line) {
			this.line = line;
			String[] tokens = line.split("[ ,]");
			for (String s : tokens) {
				String[] toks = s.split("[=:]");
				if (toks.length > 0) {
					if (toks[0].equals("tickref")) {
						tickref = parseInt(toks[1]);
					}					
					if (toks[0].equals("src")) {
						src = parseInt(toks[1]);
					}					
				}
			}
		}
		
		private int parseInt(String s) {
			String sc = s.replaceAll("[^0-9]"," ");
			String[] t = sc.split(" ");
			int val = 0;
			if (t.length > 0) {
				val = Integer.parseInt(t[0]);
			}
			return val;
		}
		
		public boolean isDup(Data other) {
			return (other.tickref == tickref) && (other.src == src);
		}

		@Override
		public String toString() {
			return "src=" + src + " tickref=" + tickref;
		}
		
		public String getLine() {
			return line;
		}

		public int getSrc() {
			return src;
		}
	}

	public DupChecker(String fname) throws FileNotFoundException {
		fis = new FileInputStream("c:/dev/tmp/" + fname);
		br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
	}
	
	public void check() throws IOException {
		String line;
		Data last = null;
		for (int i=0; i<cbEvtCntVector.length; i++) {
			cbEvtCntVector[i] = 0;
		}
		
		while ((line = br.readLine()) != null) {
			if (line.contains("RtrAuditLog")) {
				numLines++;
				Data d = new Data(line);
				int src = d.getSrc();
				if (src < cbEvtCntVector.length) {
					cbEvtCntVector[src]++;
				}
				if (last != null) {
					if (last.isDup(d)) {
						numDups++;
						System.out.println("got Dup at line " + numLines + ": " + d);
						System.out.println("line 1: " + last.getLine());
						System.out.println("line 2: " + d.getLine());
					}
				}
				last = d;
			}
		}
	}
	
	public void close() throws IOException {
		if (br != null) {
			br.close();
			br = null;
		}
		fis = null;
	}
	
	@Override
	public String toString() {
		return " " + numDups + "/" + numLines;
	}
	
	private void print() {
		System.out.println(this);
		for (int i=0; i<cbEvtCntVector.length; i++) {
			if (cbEvtCntVector[i] > 0) {
				System.out.println("CB " + i + ": #evts=" + cbEvtCntVector[i]);
			}
		}
	}

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DupChecker dc;
		try {
			dc = new DupChecker("soaktest.log.2");
			try {
				dc.check();
				dc.print();
			} catch (Exception e) {
				e.printStackTrace();
				dc.close();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}


}

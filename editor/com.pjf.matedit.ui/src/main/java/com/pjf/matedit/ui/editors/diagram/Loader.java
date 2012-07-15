/**
 * Copyright (c) 2012 Laurent Mihalkovic and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Distribution License v1.0
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php 
 * 
 * Contributors: 
 * 		Laurent Mihalkovic - Initial implementation 
 */
package com.pjf.matedit.ui.editors.diagram;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import com.pjf.mat.api.Element;
import com.pjf.mat.api.MatApi;

/**
 * @author <a href="mailto:laurent.mihalkovic@gmail.com">lmihalkovic</a>
 */
public class Loader {

    public static final String DEFAULT_BACKGROUND_START = "FFFFFF";
	
	@SuppressWarnings("serial")
	private static final Map<String, String> colLight = new HashMap<String, String>(){{
		put("ROUTER",  "D4E7F7");
		put("",  "D4D6F7");
		put("TG1",  "E4D4F7");
		put("",  "F5D4F7");
			
		put("L4IP",  "D4F7F5");
		put("",  "A0CBEE");
		put("A4IP",  "6CAEE4");
		put("",  "F7D4E7");

		put("LOGGER",  "D4F7E4");
		put("",  "E4A26C");			
		put("UDP_RAW_MFD",  "EEC3A0");
		put("",  "F7D4D6");
			
		put("EMA",  "D6F7D4");
		put("",  "E7F7D4");
		put("",  "F7F5D4");
		put("NOTE",  "F7E4D4");
	}};
	
	@SuppressWarnings("serial")
	private static final Map<String, String> colDark = new HashMap<String, String>(){{
		put("ROUTER",  "BEDBF3");
		put("",  "BEC1F3");
		put("TG1",  "D6BEF3");
		put("",  "F1BEF3");
			
		put("L4IP",  "BEF3F1");
		put("",  "8BBFEA");
		put("A4IP",  "57A2E0");
		put("",  "F3BEDB");
			
		put("LOGGER",  "BEF3D6");
		put("",  "E09557");			
		put("UDP_RAW_MFD",  "EAB58B");
		put("",  "F3BEC1");
			
		put("EMA",  "C1F3BE");
		put("",  "DBF3BE");
		put("",  "F3F1BE");
		put("NOTE",  "F3D6BE");
	}};

	
	
	
	private static final Random r = new Random(System.currentTimeMillis());
	
	public static Properties loadMacd16(MatApi mat, Properties props) {
		Element mfd = mat.getElement(15);
		Element ema_p = mat.getElement(3);
		Element ema_q = mat.getElement(4);
		Element ema_s = mat.getElement(5);
		Element a4ip_macd = mat.getElement(11);
		Element a4ip_hist = mat.getElement(12);
		Element lgr = mat.getElement(1);

		// configure element attributes
		mfd.getAttribute("udp listen port").setValue("15000");
		mfd.getAttribute("price_op").setValue("0");
		mfd.getAttribute("volume_op").setValue("f");
		mfd.getAttribute("mdtype").setValue("1");
		ema_p.getAttribute("len").setValue("7");
		ema_p.getAttribute("alpha").setValue("0.25");
		ema_q.getAttribute("len").setValue("3");
		ema_q.getAttribute("alpha").setValue("0.5");
		ema_s.getAttribute("len").setValue("5");
		ema_s.getAttribute("alpha").setValue("0.333333");
		a4ip_macd.getAttribute("oper").setValue("3000");	// Z = A - B
		a4ip_hist.getAttribute("oper").setValue("3000");	// Z = A - B

		// configure element connections
		ema_p.getInputs().get(0).connectTo(mfd.getOutputs().get(0));
		ema_q.getInputs().get(0).connectTo(mfd.getOutputs().get(0));
		a4ip_macd.getInputs().get(0).connectTo(ema_p.getOutputs().get(0));
		a4ip_macd.getInputs().get(1).connectTo(ema_q.getOutputs().get(0));
		ema_s.getInputs().get(0).connectTo(a4ip_macd.getOutputs().get(0));
		a4ip_hist.getInputs().get(0).connectTo(a4ip_macd.getOutputs().get(0));
		a4ip_hist.getInputs().get(1).connectTo(ema_s.getOutputs().get(0));
		lgr.getInputs().get(0).connectTo(mfd.getOutputs().get(0));
		lgr.getInputs().get(1).connectTo(a4ip_macd.getOutputs().get(0));
		lgr.getInputs().get(2).connectTo(a4ip_hist.getOutputs().get(0));
		lgr.getInputs().get(3).connectTo(ema_s.getOutputs().get(0));

		// fake locations
		Properties p = new Properties(props);		
		generateLocation(mfd, p);
		generateLocation(ema_p, p);
		generateLocation(ema_q, p);
		generateLocation(ema_s, p);
		generateLocation(a4ip_macd, p);
		generateLocation(a4ip_hist, p);
		generateLocation(lgr, p);
		for(int i = 0; i < 16; i++) {
			generateVisuals(i, mat.getElement(i).getType(), p);
		}

		// process other node types
		generateNote(20, "This is a longish note to be displayed on the diagram somewhere", p);
		
		
		return p;
	}
	
	public static Properties loadAdx16(MatApi mat, Properties props) {
		Element mfd = mat.getElement(15);
		Element ema_p = mat.getElement(3);
		Element ema_q = mat.getElement(4);
		Element ema_s = mat.getElement(5);
		Element a4ip_macd = mat.getElement(9);
		Element a4ip_hist = mat.getElement(10);
		Element lgr = mat.getElement(1);
		Element hloc = mat.getElement(12);

		// configure element attributes
		mfd.getAttribute("udp listen port").setValue("15000");
		mfd.getAttribute("price_op").setValue("0");
		mfd.getAttribute("volume_op").setValue("f");
		mfd.getAttribute("mdtype").setValue("1");
		ema_p.getAttribute("len").setValue("7");
		ema_p.getAttribute("alpha").setValue("0.25");
		ema_q.getAttribute("len").setValue("3");
		ema_q.getAttribute("alpha").setValue("0.5");
		ema_s.getAttribute("len").setValue("5");
		ema_s.getAttribute("alpha").setValue("0.333333");
		a4ip_macd.getAttribute("oper").setValue("3000");	// Z = A - B
		a4ip_hist.getAttribute("oper").setValue("3000");	// Z = A - B
		hloc.getAttribute("period").setValue("10000");	// 100us
		hloc.getAttribute("metric").setValue("4");			// High(N)
		hloc.getAttribute("throttle").setValue("1");

		// configure element connections
		ema_p.getInputs().get(0).connectTo(mfd.getOutputs().get(0));
		ema_q.getInputs().get(0).connectTo(mfd.getOutputs().get(0));
		hloc.getInputs().get(0).connectTo(mfd.getOutputs().get(0));
		a4ip_macd.getInputs().get(0).connectTo(ema_p.getOutputs().get(0));
		a4ip_macd.getInputs().get(1).connectTo(ema_q.getOutputs().get(0));
		ema_s.getInputs().get(0).connectTo(a4ip_macd.getOutputs().get(0));
		a4ip_hist.getInputs().get(0).connectTo(a4ip_macd.getOutputs().get(0));
		a4ip_hist.getInputs().get(1).connectTo(ema_s.getOutputs().get(0));
		lgr.getInputs().get(0).connectTo(mfd.getOutputs().get(0));
		lgr.getInputs().get(1).connectTo(a4ip_macd.getOutputs().get(0));
		lgr.getInputs().get(2).connectTo(a4ip_hist.getOutputs().get(0));
		lgr.getInputs().get(3).connectTo(hloc.getOutputs().get(0));

		// fake locations
		Properties p = new Properties(props);		
		generateLocation(mfd, p);
		generateLocation(ema_p, p);
		generateLocation(ema_q, p);
		generateLocation(ema_s, p);
		generateLocation(a4ip_macd, p);
		generateLocation(a4ip_hist, p);
		generateLocation(lgr, p);
		generateLocation(hloc, p);
		for(int i = 0; i < 16; i++) {
			generateVisuals(i, mat.getElement(i).getType(), p);
		}

		// process other node types
		generateNote(20, "This is a longish note to be displayed on the diagram somewhere", p);
		
		
		return p;
	}
	
	private static void generateNote(int id, String contents, Properties props) {
		String key = "element" + id + ".";
		props.put(key + "type", "NOTE");
		props.put(key + "contents", contents);
		
		generateVisuals(id, "NOTE", props);
	}

	static void generateLocation(Element elt, Properties props) {
		generateLocation(elt.getId(), elt.getType(), props);
	}
	
	static void generateVisuals(int id, String type, Properties props) {
		String key = "element" + id + ".";
		
		// Colors
		String name = type;
		String color = colLight.get(name);

		if (name.equals("NOTE")) {
			props.put(key + "background.type", "solid");
			props.put(key + "background.color", color);
		} else {
			props.put(key + "background.type", "gradient");
			props.put(key + "background.start", DEFAULT_BACKGROUND_START);
			props.put(key + "background.end", color);
		}
	}

	static void generateLocation(int id, String type, Properties props) {
		
		String key = "element" + id + ".";
		
		// Location
		double x = 700.0d * r.nextDouble();
		double y = 400.0d * r.nextDouble();
		props.put(key + "loc.x", x);
		props.put(key + "loc.y", y);
		
	}
	
}

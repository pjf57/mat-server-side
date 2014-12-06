package com.cs.fwk.api.util;

import java.util.ArrayList;
import java.util.List;

import com.cs.fwk.api.Element;

/**
 * This class holds config text for a CB
 * 
 * @author pjf
 *
 */
public class CBConfigText {
	private final List<String> textLines;
	private final Element cb;

	/**
	 * Create with cb and empty text line list
	 * 
	 * @param cb
	 */
	public CBConfigText(Element cb) {
		this.cb = cb;
		this.textLines = new ArrayList<String>();
	}
	
	/**
	 * Add one line of text
	 * 
	 * @param line
	 */
	public void addLine(String line) {
		textLines.add(line);
	}

	/**
	 * @return list of strings; one for each line
	 */
	public List<String> getTextLines() {
		return textLines;
	}

	/**
	 * @return cb to which this config applies
	 */
	public Element getCb() {
		return cb;
	}

	@Override
	public String toString() {
		return "CBConfigText [cb=" + cb.getShortName() + ", textLines=" + textLines + "]";
	}
	
	
	
}

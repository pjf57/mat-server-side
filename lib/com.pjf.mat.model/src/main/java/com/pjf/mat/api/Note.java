package com.pjf.mat.api;

public class Note implements Item {

	String contents;
	final int id;
	
	public Note(int id, String contents) {
		this.contents = contents;
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}
}

package com.minicontact;


public class ContactEntity {
	final private String name;
	final private String number;
	final private char sortKey;
	
	
	public ContactEntity(String name, String number, char sortKey) {
		super();
		this.name = name;
		this.number = number;
		this.sortKey = sortKey;
	}
	
	public String getName() {
		return name;
	}
	public String getNumber() {
		return number;
	}
	
	public char getSortKey(){
		return sortKey;
	}
}

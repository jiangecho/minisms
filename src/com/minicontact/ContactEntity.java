package com.minicontact;

public class ContactEntity {
	private String name;
	private String number;
	private String sortKey;
	
	
	public ContactEntity(String name, String number, String sortKey) {
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
	
	public String getSortKey(){
		return sortKey.charAt(0) + " ";
	}
}

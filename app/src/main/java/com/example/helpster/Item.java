package com.example.helpster;

public class Item {

	public String place, username, name, amount, request, compensation, endTime, id, contact,
	delivery, type;
	public boolean isAvailable;

	public enum SORTBY {
		place, name, amount, request, compensation, endTime, delivery, username,type
	}

	public Item() {

	}

	// single
	public Item(String r, String ct, String et, String d, String c) {
		this.request = r;
		this.compensation = ct;
		this.endTime = et;
		this.contact = c;
		this.delivery = d;
		this.isAvailable = true;
	}

	// list
	public Item(String p, String u, String a, String et, String isa, String i, String n) {
		this.place = p;
		this.username = u;
		this.amount = a;
		this.endTime = et;
		this.isAvailable = isa == "1";
		this.id = i;
		this.name = n;
	}

	public String[] getAttributes() {
		return new String[] { this.place, this.name, this.request, this.delivery, this.amount,
				this.compensation, this.contact, this.username, this.endTime,this.type };
	}

}

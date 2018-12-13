package com.vivo.globalsearchdemo.model;

public class ContactBean {
	private String name;
	private String number;
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	ContactBean(String name, String number,String id) {
		this.name = name;
		this.number = number;
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public String getNumber() {
		return number;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNumber(String number) {
		this.number = number;
	}


}

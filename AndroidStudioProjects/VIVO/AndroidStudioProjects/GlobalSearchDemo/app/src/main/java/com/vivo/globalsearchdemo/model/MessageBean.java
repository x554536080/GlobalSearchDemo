package com.vivo.globalsearchdemo.model;

public class MessageBean {
	private String id;
	private String content;
	private String number;
	private String contact;

	public MessageBean(String id, String content, String number, String contact) {
		this.id = id;
		this.content = content;
		this.number = number;
		this.contact = contact;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}

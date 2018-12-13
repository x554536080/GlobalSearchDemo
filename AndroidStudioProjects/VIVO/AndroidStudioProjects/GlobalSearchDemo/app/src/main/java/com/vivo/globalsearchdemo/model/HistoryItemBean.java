package com.vivo.globalsearchdemo.model;

public class HistoryItemBean {

	private String content;
	private int id;

	HistoryItemBean(String content,int id) {
		this.content = content;
		this.id = id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setContent(String content) {

		this.content = content;
	}


	public int getId() {

		return id;
	}

	public String getContent() {

		return content;
	}
}
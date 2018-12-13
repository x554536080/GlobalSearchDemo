package com.vivo.globalsearchdemo.model;

import android.graphics.drawable.Drawable;

public class LocalAppBean {
	private Drawable icon;
	private String name;
	private String pack;
	private String action;

	LocalAppBean(Drawable icon, String name) {
		this.icon = icon;
		this.name = name;
	}

	public String getPack() {
		return pack;
	}

	public String getAction() {
		return action;
	}

	public void setPack(String pack) {
		this.pack = pack;

	}

	public void setAction(String action) {
		this.action = action;
	}

	public Drawable getIcon() {
		return icon;
	}

	public String getName() {
		return name;
	}
}



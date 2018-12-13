package com.vivo.globalsearchdemo.model;

import java.util.List;

public interface ContactModel {

	void updateContacts();
	List<ContactBean> queryContacts();
	void readContactsFromPhone();

}

package com.vivo.globalsearchdemo.presenter;

import com.vivo.globalsearchdemo.model.HistoryItemBean;

import java.util.List;

public interface Presenter {
	void saveHist(String string);

	List<HistoryItemBean> loadHist();

	void deleteHist();

	void deleteOneHist(int id);

	void loadLocalApp();

	void loadContacts();

	void loadMessages();

	void doSearch(String input);
}

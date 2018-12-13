package com.vivo.globalsearchdemo.presenter;


import android.content.Context;

import com.vivo.globalsearchdemo.model.ContactModelImpl;
import com.vivo.globalsearchdemo.model.HistoryItemBean;
import com.vivo.globalsearchdemo.model.HistoryModelImpl;
import com.vivo.globalsearchdemo.model.LocalAppModelImpl;
import com.vivo.globalsearchdemo.model.MessageModelImpl;
import com.vivo.globalsearchdemo.model.SearchModelImpl;


import java.util.List;

public class PresenterImpl implements Presenter {


	//Model层的引用
	private HistoryModelImpl historyModel;
	private LocalAppModelImpl localAppModel;
	private SearchModelImpl searchModel;
	private ContactModelImpl contactModel;
	private MessageModelImpl messageModel;



	public PresenterImpl(Context context) {
		localAppModel = new LocalAppModelImpl();
		historyModel = new HistoryModelImpl(context);
		searchModel = new SearchModelImpl();
		contactModel = new ContactModelImpl(context);
		messageModel = new MessageModelImpl();
	}


	@Override
	public void saveHist(String string) {
		historyModel.saveHistoricSearch(string);
	}

	@Override
	public List<HistoryItemBean> loadHist() {
		return historyModel.loadHistoricSearch();
	}

	@Override
	public void deleteHist() {
		historyModel.clearAllHistory();
	}

	@Override
	public void deleteOneHist(int id) {
		historyModel.clearOneHistory(id);
	}

	@Override
	public void loadLocalApp() {
		localAppModel.loadLocalApps();
	}

	@Override
	public void loadContacts() {
		contactModel.readContactsFromPhone();
	}

	@Override
	public void loadMessages() {
		messageModel.readMessagesFromPhone();
	}

	@Override
	public void doSearch(String input) {
		searchModel.doSearch(input);
	}
}

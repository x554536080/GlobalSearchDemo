package com.vivo.globalsearchdemo.model;

import java.util.List;

public interface HistoryModel {
	List<HistoryItemBean> loadHistoricSearch();
	void saveHistoricSearch(String content);
	void clearAllHistory();
	void clearOneHistory(int id);
}

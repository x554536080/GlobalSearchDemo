package com.vivo.globalsearchdemo.presenter;


import android.view.View;

import com.vivo.globalsearchdemo.view.HistoryLayout;
import com.vivo.globalsearchdemo.view.SearchLayout;
import com.vivo.globalsearchdemo.view.SearchResultLayout;

public class PresenterForViewImpl implements PresenterForView {

    //View层的引用
    public HistoryLayout historyLayout;
    public SearchLayout searchLayout;
    public SearchResultLayout searchResultLayout;

    public void initialViews() {
        searchResultLayout.setVisibility(View.GONE);

        historyLayout.initItems(false);
        if (historyLayout.isEmpty())
            historyLayout.setVisibility(View.INVISIBLE);
    }

    public void setHistoryLayout(HistoryLayout historyLayout) {
        this.historyLayout = historyLayout;
    }

    public void setSearchLayout(SearchLayout searchLayout) {
        this.searchLayout = searchLayout;
    }

    public void setSearchResultLayout(SearchResultLayout searchResultLayout) {
        this.searchResultLayout = searchResultLayout;
    }

    @Override
    public void methodToAdd() {

    }
}

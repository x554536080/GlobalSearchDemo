package com.vivo.globalsearchdemo.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import android.view.MotionEvent;
import android.view.View;

import android.widget.LinearLayout;

import com.vivo.globalsearchdemo.MainActivity;
import com.vivo.globalsearchdemo.R;
import com.vivo.globalsearchdemo.model.ContactBean;
import com.vivo.globalsearchdemo.model.LocalAppBean;
import com.vivo.globalsearchdemo.model.MessageBean;
import com.vivo.globalsearchdemo.model.SearchModelImpl;
import com.vivo.globalsearchdemo.presenter.Presenter;
import com.vivo.globalsearchdemo.presenter.PresenterImpl;

import java.util.ArrayList;
import java.util.List;


public class SearchResultLayout extends LinearLayout {
	Presenter mPresenter;
	Context mContext;
	LinearLayoutManager layoutManager;
	RecyclerView recyclerView;
	public static List<LocalAppBean> apps;
	public static List<String> baiduResults;
	public static List<ContactBean> contacts;
	public static List<MessageBean> messages;

	public static ResultItemAdapter adapter;
	public static AdapterUpdateHandler handler = new AdapterUpdateHandler();


	public SearchResultLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		MainActivity.presenterForViewImpl.setSearchResultLayout(this);
		LayoutInflater.from(context).inflate(R.layout.search_result_layout, this);
		mPresenter = new PresenterImpl(mContext);

	}


	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		apps = new ArrayList<>();
		adapter = new ResultItemAdapter(mContext);
		layoutManager = new LinearLayoutManager(mContext);
		recyclerView = findViewById(R.id.search_result_recycler_view);
		recyclerView.setLayoutManager(layoutManager);
		recyclerView.setAdapter(adapter);

		recyclerView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				requestFocus();
				MainActivity.imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
				return false;
			}
		});


	}

	public static void clearItems() {
		adapter.clearAllItems();
	}

	public static class AdapterUpdateHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case SearchModelImpl.UPDATE_APPS:
					adapter.addAppViewItems(apps);
					adapter.updateItems();
					break;
				case SearchModelImpl.UPDATE_CONTACTS:
					adapter.addContactViewItems(contacts);
					adapter.updateItems();
					break;
				case SearchModelImpl.UPDATE_MESSAGE:
					adapter.addMessageViewItems(messages);
					adapter.updateItems();
					break;
				case SearchModelImpl.UPDATE_BAIDU:
					adapter.addBaiduViewItems(baiduResults);
					adapter.updateItems();
					break;
			}
			MainActivity.presenterForViewImpl.searchResultLayout.setVisibility(VISIBLE);
		}
	}

}

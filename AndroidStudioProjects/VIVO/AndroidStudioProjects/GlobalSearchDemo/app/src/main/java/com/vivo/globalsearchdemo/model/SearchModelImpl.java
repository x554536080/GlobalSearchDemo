package com.vivo.globalsearchdemo.model;

import android.os.Message;
import android.util.Log;

import com.vivo.globalsearchdemo.view.SearchResultLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchModelImpl implements SearchModel {

	final static public int UPDATE_APPS = 0;
	final static public int UPDATE_CONTACTS = 1;
	final static public int UPDATE_BAIDU = 2;
	final static public int UPDATE_MESSAGE = 3;


	private BaiduSearchImpl baiduSearch = new BaiduSearchImpl();
	static CountDownLatch latch;


	private List<LocalAppBean> appResults = new ArrayList<>();
	private List<ContactBean> contactResults = new ArrayList<>();
	private List<MessageBean> messageResults = new ArrayList<>();


	@Override
	public void doSearch(final String input) {

		final String REGEX = ".*" + input.trim() + ".*";
		final Pattern test = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);

		SearchResultLayout.clearItems();

		//搜索短信线程
		new Thread(new Runnable() {
			@Override
			public void run() {
				messageResults.clear();
				messageResults = LuceneTool.indexQuery(input);
				for (int i = 0; i < messageResults.size(); i++) {
					Log.d("xds", messageResults.get(i).getContent());
				}
				SearchResultLayout.messages = messageResults;
				Message message = new Message();
				message.what = UPDATE_MESSAGE;
				SearchResultLayout.handler.sendMessage(message);
			}

		}).start();

		//搜索本地应用线程
		new Thread(new Runnable() {
			@Override
			public void run() {
				appResults.clear();
				Matcher matcher;
				for (LocalAppBean appBeans : LocalAppModelImpl.appCache) {
					matcher = test.matcher(appBeans.getName());
					if (matcher.find()) {
						appResults.add(appBeans);
					}
				}
				SearchResultLayout.apps = appResults;
				Message message = new Message();
				message.what = UPDATE_APPS;
				SearchResultLayout.handler.sendMessage(message);
			}

		}).start();

		//百度搜索线程
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					latch = new CountDownLatch(1);
					baiduSearch.doSearch(input);
					latch.await();
					SearchResultLayout.baiduResults = baiduSearch.getSearchResult();
					Message message = new Message();
					message.what = UPDATE_BAIDU;
					SearchResultLayout.handler.sendMessage(message);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}).start();

		//搜索联系人线程
		new Thread(new Runnable() {
			@Override
			public void run() {
				contactResults.clear();
				Matcher matcher;
				for (ContactBean contactBean : ContactModelImpl.contacts) {
					matcher = test.matcher(contactBean.getName());
					if (matcher.find()) {
						contactResults.add(contactBean);
					}
				}
				SearchResultLayout.contacts = contactResults;
				Message message = new Message();
				message.what = UPDATE_CONTACTS;
				SearchResultLayout.handler.sendMessage(message);
			}

		}).start();
	}
}

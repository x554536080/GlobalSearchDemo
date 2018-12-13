package com.vivo.globalsearchdemo;

import android.app.IntentService;
import android.content.Intent;

import com.vivo.globalsearchdemo.presenter.PresenterImpl;

import java.util.concurrent.CountDownLatch;


public class LoadDataService extends IntentService {

	PresenterImpl mPresenter;


	public LoadDataService() {
		super("LoadDataService");
		mPresenter = new PresenterImpl(WelcomeActivity.context);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
			mPresenter.loadLocalApp();
			mPresenter.loadContacts();
			mPresenter.loadMessages();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}

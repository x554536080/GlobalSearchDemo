package com.vivo.globalsearchdemo.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.vivo.globalsearchdemo.MainActivity;
import com.vivo.globalsearchdemo.R;
import com.vivo.globalsearchdemo.presenter.PresenterImpl;


public class HistoryItem extends FrameLayout {

	PresenterImpl mPresenter;
	String mContent;
	Button imageButton;
	TextView textView;
	Context mContext;
	LocalReceiver localReceiver;
	LocalBroadcastManager localBroadcastManager;
	IntentFilter intentFilter;

	public HistoryItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	public HistoryItem(Context context, AttributeSet attrs, String string) {
		super(context, attrs);
		mContext = context;
		this.mContent = string;
		LayoutInflater.from(context).inflate(R.layout.history_item,this);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		//布局视图相关初始化
		mPresenter = new PresenterImpl(mContext);
		imageButton = findViewById(R.id.hist_item_button);
		imageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mPresenter.deleteOneHist(getId());
				MainActivity.presenterForViewImpl.historyLayout.initItems(true);

			}
		});
		imageButton.setVisibility(GONE);
		textView = findViewById(R.id.hist_item_text);
		textView.setText(mContent);
		textView.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View view) {
				textView.setOnClickListener(null);
				imageButton.setVisibility(VISIBLE);
				Intent intent = new Intent("com.vivo.globalsearchdemo.SHOW_CLEAR");
				localBroadcastManager.sendBroadcast(intent);
				return true;
			}
		});
		textView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				MainActivity.presenterForViewImpl.searchLayout.editText.setText(mContent);
				MainActivity.presenterForViewImpl.searchLayout.editText.setSelection(mContent.length());
				MainActivity.imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
			}
		});

		//注册一个本地广播，如果长按一个其他按键，该按键也会显示清除键
		intentFilter = new IntentFilter();
		intentFilter.addAction("com.vivo.globalsearchdemo.SHOW_CLEAR");
		localReceiver = new LocalReceiver();
		localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
		localBroadcastManager.registerReceiver(localReceiver, intentFilter);
	}


	//用于每个按键接受长按响应
	class LocalReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			imageButton.setVisibility(VISIBLE);
			textView.setOnClickListener(null);
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		localBroadcastManager.unregisterReceiver(localReceiver);
	}
}

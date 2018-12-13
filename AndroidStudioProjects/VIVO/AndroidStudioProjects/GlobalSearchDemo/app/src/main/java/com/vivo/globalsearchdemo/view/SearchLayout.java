package com.vivo.globalsearchdemo.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vivo.globalsearchdemo.MainActivity;
import com.vivo.globalsearchdemo.R;
import com.vivo.globalsearchdemo.presenter.PresenterImpl;


public class SearchLayout extends LinearLayout implements View.OnClickListener, TextWatcher, TextView.OnEditorActionListener {

	public EditText editText;
	Button clearButton;
	PresenterImpl presenter;
	Context mContext;

	public SearchLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		MainActivity.presenterForViewImpl.setSearchLayout(this);
		LayoutInflater.from(context).inflate(R.layout.search_view, this);
	}

	@Override
	protected void onFinishInflate() {
		//该函数中初始化变量以及设置监听
		super.onFinishInflate();
		presenter = new PresenterImpl(mContext);
		editText = findViewById(R.id.editText);
		editText.setOnEditorActionListener(this);
		editText.addTextChangedListener(this);
		clearButton = findViewById(R.id.button_clear);
		clearButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.button_clear:
				editText.setText("");
		}
	}

	@Override
	public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
	}

	@Override
	public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

		String input = editText.getText().toString();
		SearchResultLayout.adapter.setSearchString(input);
		if (!"".equals(input)) {
			clearButton.setVisibility(VISIBLE);
			if (!"".equals(input.trim())) {
				input = input.replace(".", "\\.");
				input = input.replace("*", "\\*");
				MainActivity.presenterForViewImpl.historyLayout.setVisibility(GONE);
				presenter.doSearch(input);
				ResultItemAdapter.input = input;
			}


		} else {
			MainActivity.presenterForViewImpl.searchResultLayout.setVisibility(GONE);
			clearButton.setVisibility(INVISIBLE);
			MainActivity.presenterForViewImpl.historyLayout.initItems(false);
			if (!MainActivity.presenterForViewImpl.historyLayout.isEmpty())
				MainActivity.presenterForViewImpl.historyLayout.setVisibility(VISIBLE);
		}
	}

	@Override
	public void afterTextChanged(Editable editable) {
	}

	@Override
	public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
//		if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//			String input = editText.getText().toString();
//			//判断字符串是否为空
//			String REGEX1 = "\\s*";
//			if (!input.matches(REGEX1)) {
//				String REGEX2 = "\\s+.*";
//				String REGEX3 = ".*\\s+";
//				//去掉首尾空格
//				if (input.matches(REGEX2) || input.matches(REGEX3)) {
//					input = input.replaceAll("\\s+", "");
//				}
//				presenter.saveHist(input);
//			}
//			return false;
//		}
		return true;
	}


}
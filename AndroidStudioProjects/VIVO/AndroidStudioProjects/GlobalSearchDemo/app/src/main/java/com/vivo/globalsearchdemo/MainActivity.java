package com.vivo.globalsearchdemo;


import android.content.Context;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.inputmethod.InputMethodManager;


import com.vivo.globalsearchdemo.presenter.PresenterForViewImpl;


import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

	static public DisplayMetrics metrics;
	static public InputMethodManager imm;

	static public PresenterForViewImpl presenterForViewImpl;
	static public Context context;

	IntentFilter intentFilter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		context = this;
		initNecessaryVariables();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		presenterForViewImpl.initialViews();
		MyActivityManager.addActivity(this);

		//自动弹出输入法
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}, 600);
	}

	//初始化相关变量
	void initNecessaryVariables() {

		//布局交互Presenter
		presenterForViewImpl = new PresenterForViewImpl();

		intentFilter = new IntentFilter();
		intentFilter.addAction("com.vivo.globalsearchdemo.LAUNCH_OTHER_APP");
		metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		presenterForViewImpl.searchLayout.editText.clearFocus();
		imm.hideSoftInputFromWindow(presenterForViewImpl.searchLayout.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}
}



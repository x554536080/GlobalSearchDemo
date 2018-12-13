package com.vivo.globalsearchdemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.vivo.globalsearchdemo.MainActivity;
import com.vivo.globalsearchdemo.R;
import com.vivo.globalsearchdemo.model.HistoryItemBean;
import com.vivo.globalsearchdemo.presenter.PresenterImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;


public class HistoryLayout extends LinearLayout implements View.OnClickListener {


	PresenterImpl presenter;
	static public CountDownLatch latch;
	Context mContext;
	List<HistoryItemBean> items;//该布局中存储的历史记录的变量
	LinearLayout layout;//待加载的该布局的变量
	LinearLayout layout2;
	LinearLayout layoutWhole;
	ImageView clearButton;//清除键的变量
	int sum1;//栏一使用的宽度
	int sum2;//栏二使用的宽度
	int layoutWidth;//栏的最大宽度
	float density;


	public HistoryLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		MainActivity.presenterForViewImpl.setHistoryLayout(this);
		LayoutInflater.from(context).inflate(R.layout.history_display_layout, this);
	}


	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		items = new ArrayList<>();
		presenter = new PresenterImpl(mContext);
		clearButton = findViewById(R.id.clear_image_view);
		clearButton.setOnClickListener(this);
		layout = findViewById(R.id.history_item_layout);
		layout2 = findViewById(R.id.history_item_layout2);
		layoutWidth = layout.getMeasuredWidth();
		layoutWhole = findViewById(R.id.history_display_layout_whole);
		layoutWhole.setOnClickListener(this);


	}

	//将存入数据库中的历史记录变量载入
	public void loadHistory() {
		items = presenter.loadHist();
	}

	public boolean isEmpty() {
		return items.isEmpty();
	}

	//创建并返回一项历史记录的方法
	public FrameLayout createHistoryItem(String text, int id, boolean showCross) {
		final HistoryItem historyItem = new HistoryItem(mContext, null, text);
		historyItem.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int) (40 * density)));
		historyItem.onFinishInflate();
		historyItem.setId(id);
		if (showCross) historyItem.imageButton.setVisibility(VISIBLE);

		//提前得到该item的宽高，以便于initItem函数的使用
		int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		historyItem.measure(w, h);
		if (sum1 <= layoutWidth) {
			sum1 += historyItem.getMeasuredWidth();
		}
		if (sum1 > layoutWidth) {
			sum2 += historyItem.getMeasuredWidth();
		}
		return historyItem;
	}

	//刷新按键显示方法，参数表示是否显示清除键
	public void initItems(boolean showCross) {
		try {
			latch = new CountDownLatch(1);
			density = MainActivity.metrics.density;
			layoutWidth = (int) (MainActivity.metrics.widthPixels - 40 * density);
			sum1 = 0;
			sum2 = 0;
			layout.removeAllViews();
			layout2.removeAllViews();
			layout2.setVisibility(GONE);
			loadHistory();
			latch.await();
			//如果历史记录不为空，两行历史记录的显示逻辑
			if (!items.isEmpty()) {
				for (int i = 0; i < items.size(); i++) {
					FrameLayout addItem = createHistoryItem(items.get(i).getContent(), items.get(i).getId(), showCross);
					if (sum1 <= layoutWidth) {
						layout.addView(addItem);
					} else if (sum2 <= layoutWidth) {
						layout2.setVisibility(VISIBLE);
						layout2.addView(addItem);
					} else break;
				}
			} else {
				MainActivity.presenterForViewImpl.historyLayout.setVisibility(INVISIBLE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.clear_image_view:
				presenter.deleteHist();
				MainActivity.presenterForViewImpl.historyLayout.setVisibility(INVISIBLE);
				initItems(false);
				break;
			case R.id.history_display_layout_whole:
				initItems(false);
				break;
			default:
				break;
		}
	}
}

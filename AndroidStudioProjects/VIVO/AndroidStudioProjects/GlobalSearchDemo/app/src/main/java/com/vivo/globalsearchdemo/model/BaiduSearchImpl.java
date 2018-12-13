package com.vivo.globalsearchdemo.model;



import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BaiduSearchImpl implements BaiduSearch {

	public List<String> getSearchResult() {
		return searchResult;
	}

	private List<String> searchResult = new ArrayList<>();

	@Override
	public void doSearch(final String s) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				HttpURLConnection connection;
				BufferedReader reader;
				try {
					String string = String.format("http://m.baidu.com/su?wd=%s&action=opensearch&ie=utf-8&from=1020761n", s);
					URL url = new URL(string);

					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					connection.setDoInput(true);
					int res = connection.getResponseCode();
					InputStream in = connection.getInputStream();
					StringBuilder response = new StringBuilder();

					if (res == HttpURLConnection.HTTP_OK) {
						reader = new BufferedReader(new InputStreamReader(in));
						String line;
						while ((line = reader.readLine()) != null) {
							response.append(line);
						}
					}

					JSONArray jsonArray = new JSONArray(response.toString());
					searchResult = parseJSONArray(jsonArray);
					SearchModelImpl.latch.countDown();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	private List<String> parseJSONArray(JSONArray jsonArray) {
		List<String> strings = new ArrayList<>();
		try {
			strings.add(jsonArray.getString(0));
			for (int i = 0; i < jsonArray.getJSONArray(1).length(); i++) {

				String s = jsonArray.getJSONArray(1).getString(i);
				strings.add(s);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strings;
	}
}


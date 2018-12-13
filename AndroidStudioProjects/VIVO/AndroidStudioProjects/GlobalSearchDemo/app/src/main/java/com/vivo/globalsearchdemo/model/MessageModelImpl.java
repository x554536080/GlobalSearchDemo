package com.vivo.globalsearchdemo.model;

import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import com.vivo.globalsearchdemo.LoadDataService;
import com.vivo.globalsearchdemo.MainActivity;
import com.vivo.globalsearchdemo.WelcomeActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageModelImpl implements MessageModel {

	private static List<MessageBean> messageContent = new ArrayList<>();

	public void readMessagesFromPhone() {
		int i = 0;
		Cursor cursor = WelcomeActivity.getMessageQueryCursor();
		if (cursor != null) {

			while (cursor.moveToNext()) {
				String number = cursor.getString(cursor.getColumnIndex("address"));//手机号
				String name = cursor.getString(cursor.getColumnIndex("person"));//联系人姓名列表
				String id = cursor.getString(cursor.getColumnIndex("_id"));
				String body = cursor.getString(cursor.getColumnIndex("body"));//短信内容
				MessageBean addMessage = new MessageBean(id,body,number,name);
				messageContent.add(addMessage);
				i++;
			}
		}
		Log.d("xds","短信数目："+i);
		WelcomeActivity.countDownLatch.countDown();
		LuceneTool.createIndex(messageContent);
		Log.d("xds","索引创建成功");

	}

}

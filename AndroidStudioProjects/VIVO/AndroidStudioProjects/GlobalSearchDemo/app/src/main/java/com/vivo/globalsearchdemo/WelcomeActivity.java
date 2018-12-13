package com.vivo.globalsearchdemo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.concurrent.CountDownLatch;

public class WelcomeActivity extends AppCompatActivity {

	static public Context context;
	public static CountDownLatch countDownLatch;

	static Cursor contactCursor;
	static Cursor messageCursor;

	static public PackageManager packageManager;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		context = this;
		countDownLatch = new CountDownLatch(3);
		MyActivityManager.addActivity(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
				ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED ||
				ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.READ_SMS,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
		else {
			initVariables();
			startService(new Intent(this, LoadDataService.class));
//			try {
//				WelcomeActivity.countDownLatch.await();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			finish();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode) {
			case 1:
				if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
						|| !(grantResults[1] == PackageManager.PERMISSION_GRANTED)|| !(grantResults[2] == PackageManager.PERMISSION_GRANTED))
					MyActivityManager.finishAll();
				else {
					initVariables();
					startService(new Intent(this, LoadDataService.class));
//					try {
//						WelcomeActivity.countDownLatch.await();
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
					Intent intent = new Intent(this, MainActivity.class);
					startActivity(intent);
					MyActivityManager.addActivity(this);
					finish();
				}
				break;
			default:
				break;

		}
	}

	public static Cursor getContactQueryCursor() {
		return contactCursor;
	}

	public static Cursor getMessageQueryCursor() {
		return messageCursor;
	}

	void initVariables() {
		//权限授予后进行初始化
		if (ContextCompat.checkSelfPermission(this,
				Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
			contactCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
					null, null, null, null, null);
		}
		if (ContextCompat.checkSelfPermission(this,
				Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
			String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
			Uri SMS_INBOX = Uri.parse("content://sms/");
			messageCursor = getContentResolver().query(SMS_INBOX, projection, null, null, "date desc");
		}
		packageManager = getPackageManager();

	}
}

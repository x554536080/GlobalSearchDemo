package com.vivo.globalsearchdemo.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;

import com.vivo.globalsearchdemo.LoadDataService;
import com.vivo.globalsearchdemo.WelcomeActivity;

import java.util.ArrayList;
import java.util.List;

public class ContactModelImpl implements ContactModel {

	public static List<ContactBean> contacts = new ArrayList<>();
	private Context mContext;

	public ContactModelImpl(Context context) {
		mContext = context;
	}


	@Override
	public void updateContacts() {
//		readContactsFromPhone();
//		SQLiteOpenHelper sqLiteOpenHelper = new ContactsDatabaseHelper(mContext, "Contact.db", null, 1);
//		SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();
//		ContentValues values1 = new ContentValues();
//		ContentValues values2 = new ContentValues();
//		for (ContactBean list : contacts) {
//			values1.put("name", list.getName());
//			db.insert("contacts", null, values1);
//			values1.clear();
//		}
//		int id = 1;
//		for (ContactBean list : contacts) {
//			values2.put("number", list.getNumber());
//			db.update("contacts", values2, "id = ?", new String[]{"" + id});
//			values2.clear();
//			id++;
//		}
	}


	@Override
	public List<ContactBean> queryContacts() {


		return null;
	}

	@Override
	public void readContactsFromPhone() {
		contacts.clear();
		Cursor cursor = WelcomeActivity.getContactQueryCursor();
		if (cursor != null)
			while (cursor.moveToNext()) {
				ContactBean addContact = new ContactBean(
						cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)),
						cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)),
						cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))
				);
				contacts.add(addContact);
			}
		if (cursor != null) cursor.close();
		WelcomeActivity.countDownLatch.countDown();
	}

	public class ContactsDatabaseHelper extends SQLiteOpenHelper {


		ContactsDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase sqLiteDatabase) {
			sqLiteDatabase.execSQL("create table" + " contacts (id integer primary key autoincrement,name text,number text)");
		}

		@Override
		public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

		}
	}
}

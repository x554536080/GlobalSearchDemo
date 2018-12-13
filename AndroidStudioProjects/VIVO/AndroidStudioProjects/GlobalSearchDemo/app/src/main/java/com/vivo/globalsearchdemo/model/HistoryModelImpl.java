package com.vivo.globalsearchdemo.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.vivo.globalsearchdemo.view.HistoryLayout;

import java.util.ArrayList;
import java.util.List;

public class HistoryModelImpl implements HistoryModel {

	private SQLiteDatabase db;

	public HistoryModelImpl(Context context) {
		HistorySearchDatabaseHelper helper = new HistorySearchDatabaseHelper(context, "HistorySearch.db", null, 1);
		db = helper.getWritableDatabase();
	}

	@Override
	public List<HistoryItemBean> loadHistoricSearch() {
		final List<HistoryItemBean> items = new ArrayList<>();
		new Thread(new Runnable() {
			@Override
			public void run() {
				Cursor cursor = db.rawQuery("select * from history order by id DESC", null);
				if (cursor.moveToNext()) {
					do {
						items.add(new HistoryItemBean(cursor.getString(cursor.getColumnIndex(("content"))), cursor.getInt(cursor.getColumnIndex(("id")))));
					}
					while (cursor.moveToNext());
				}
				cursor.close();
				HistoryLayout.latch.countDown();
			}
		}).start();
		return items;

	}

	@Override
	public void saveHistoricSearch(final String contentParameter) {
		final String[] strings = new String[1];
		strings[0] = contentParameter;
		//先读取数据库至existing
		final List<String> existing = new ArrayList<>();
		new Thread(new Runnable() {
			@Override
			public void run() {
				Cursor cursor = db.rawQuery("select * from history order by id DESC", null);
				if (cursor.moveToNext()) {
					do {
						existing.add(cursor.getString(cursor.getColumnIndex(("content"))));
					}
					while (cursor.moveToNext());
				}
				cursor.close();
				//如果本次数据已存在则放至最后
				if (existing.contains(contentParameter)) {
					db.execSQL("delete from history where content = " + "'" + contentParameter + "'");
					db.execSQL("insert into history (content) values(?)", strings);
					db.execSQL("update sqlite_sequence set seq=0 where name='history'");
				} else {
					db.execSQL("insert into history (content) values(?)", strings);
				}

			}
		}).start();
	}

	@Override
	public void clearAllHistory() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				db.execSQL("delete from history");
				db.execSQL("update sqlite_sequence set seq=0 where name='history'");
			}
		}).start();
	}

	@Override
	public void clearOneHistory(final int id) {
//		Cursor cursor = db.rawQuery("select count (id) from history", null);
//		if (cursor.moveToNext()) {
//			itemNumber = Integer.parseInt(cursor.getString(0));
//		}
//		cursor.close();
//		int deleteNumber = itemNumber - id;]

		new Thread(new Runnable() {
			@Override
			public void run() {
				db.execSQL("delete from history where id = " + id);
				db.execSQL("update sqlite_sequence set seq=0 where name='history'");
			}
		}).start();

	}

	public class HistorySearchDatabaseHelper extends SQLiteOpenHelper {

		private HistorySearchDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase sqLiteDatabase) {
			sqLiteDatabase.execSQL("create table history (id integer primary key autoincrement , content text)");
		}

		@Override
		public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

		}
	}


}

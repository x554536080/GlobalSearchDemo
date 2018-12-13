package com.vivo.globalsearchdemo.model;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;


import com.vivo.globalsearchdemo.WelcomeActivity;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class LuceneTool {

	public static void createIndex(List<MessageBean> content) {
		try {
			//索引库的存放位置对象
			Directory directory = FSDirectory.open(new File("/mnt/sdcard", "Lucene"));
			//分析器与配置对象
			Analyzer analyzer = new IKAnalyzer();
//			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_47);
//			Analyzer analyzer = new SmartChineseAnalyzer(Version.LUCENE_47);
			IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_47, analyzer);
			//indexWriter对象
			IndexWriter indexWriter = new IndexWriter(directory, config);

			//创建索引前删除全部，考虑更好的操作，较为费时
			indexWriter.deleteAll();

			for (int i = 0; i < content.size(); i++) {
				//创建Document对象
				Document document = new Document();
				//创建field对象，将field添加到document对象中
				//name域
				Field nameField;
//				if (content.get(i).getContact() != null)
//					nameField = new TextField("name", content.get(i).getContact(), Field.Store.YES);
//				else
					nameField = new TextField("name", getContactNameFromPhoneBook(content.get(i).getNumber()), Field.Store.YES);

				//id域
				Field idField = new TextField("id", content.get(i).getId(), Field.Store.YES);
				//number域
				Field numberField = new TextField("number", content.get(i).getNumber(), Field.Store.YES);

				//内容域
				Field contentField = new TextField("content", content.get(i).getContent(), Field.Store.YES);
				//将域加至文档
				document.add(idField);
				document.add(contentField);
				document.add(numberField);
				document.add(nameField);

				indexWriter.addDocument(document);
			}

			indexWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<MessageBean> indexQuery(String input) {
		List<MessageBean> idOutput = new ArrayList<>();
		try {
			Directory directory = FSDirectory.open(new File("/mnt/sdcard", "Lucene"));
			IndexReader indexReader = DirectoryReader.open(directory);
			IndexSearcher indexSearcher = new IndexSearcher(indexReader);

//			QueryParser queryParser = new QueryParser(Version.LUCENE_47, "content", new SmartChineseAnalyzer(Version.LUCENE_47));
			QueryParser queryParser = new QueryParser(Version.LUCENE_47, "content", new IKAnalyzer());
//			QueryParser queryParser = new QueryParser(Version.LUCENE_47, "content", new StandardAnalyzer(Version.LUCENE_47));
			Query query = queryParser.parse(input);

			TopDocs topDocs = indexSearcher.search(query, 10);

			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {

				Document document = indexSearcher.doc(scoreDoc.doc);
				//文件id
				idOutput.add(new MessageBean(document.get("id"), document.get("content"), document.get("number"), document.get("name")));

			}
			Log.d("xds", topDocs.totalHits + "");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return idOutput;
	}

	private static String getContactNameFromPhoneBook(String phoneNum) {
		Context context = WelcomeActivity.context;
		String contactName = "empty";
		ContentResolver cr = context.getContentResolver();
		Cursor pCur = cr.query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
				ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
				new String[]{phoneNum}, null);
		if (pCur != null) if (pCur.moveToFirst()) {
			contactName = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			pCur.close();
		}
		return contactName;
	}
}

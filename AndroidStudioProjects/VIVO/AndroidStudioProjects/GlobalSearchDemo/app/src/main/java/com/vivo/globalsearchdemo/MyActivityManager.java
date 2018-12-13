package com.vivo.globalsearchdemo;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

public class MyActivityManager {

	private static List<Activity> activityList = new ArrayList<>();

	public static void addActivity(Activity a) {
		activityList.add(a);
	}

	public static void finishAll() {
		for (Activity activity : activityList) {
			activity.finish();
		}
		activityList.clear();
	}
}

package com.vivo.globalsearchdemo.model;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

import com.vivo.globalsearchdemo.LoadDataService;
import com.vivo.globalsearchdemo.WelcomeActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LocalAppModelImpl implements LocalAppModel {

	static List<LocalAppBean> appCache = new ArrayList<>();

	@Override
	public void loadLocalApps() {
		appCache.clear();
		final PackageManager packageManager = WelcomeActivity.packageManager;
		Intent intent = new Intent(Intent.ACTION_MAIN, null);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent, PackageManager.PERMISSION_GRANTED);
		Collections.sort(resolveInfo, new Comparator<ResolveInfo>() {
			@Override
			public int compare(ResolveInfo r1, ResolveInfo r2) {
				String name1 = packageManager.getApplicationLabel(r1.activityInfo.applicationInfo).toString();
				String name2 = packageManager.getApplicationLabel(r2.activityInfo.applicationInfo).toString();
				return name1.compareTo(name2);
			}
		});
		appCache = new ArrayList<>();

		for (ResolveInfo info : resolveInfo) {
			String name = packageManager.getApplicationLabel(info.activityInfo.applicationInfo).toString();
			Drawable icon = packageManager.getApplicationIcon(info.activityInfo.applicationInfo);
			LocalAppBean app =  new LocalAppBean(icon, name);
			app.setAction(info.activityInfo.name);
			app.setPack(info.activityInfo.packageName);
			appCache.add(app);
		}
		WelcomeActivity.countDownLatch.countDown();
	}

}

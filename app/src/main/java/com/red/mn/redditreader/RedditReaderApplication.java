package com.red.mn.redditreader;

import android.app.Application;

import com.jakewharton.threetenabp.AndroidThreeTen;

/**
 * Created by manuMohan on 20/01/2016.
 */
public class RedditReaderApplication extends Application
{
	@Override
	public void onCreate()
	{
		super.onCreate();
		AndroidThreeTen.init(this);
	}
}

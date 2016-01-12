package com.red.mn.redditreader.network;

import com.red.mn.redditreader.Constants;
import com.red.mn.redditreader.model.Feed;

import java.util.ArrayList;

import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.RxJavaCallAdapterFactory;
import rx.Observable;

/**
 * Created by manuMohan on 15/07/12.
 */
public class NetworkManager
{
	private static NetworkManager mSharedNetworkManager;

	private RedditService mRedditService;

	public static NetworkManager getInstance()
	{
		if(mSharedNetworkManager == null)
		{
			mSharedNetworkManager = new NetworkManager();
		}
		return mSharedNetworkManager;
	}

	private NetworkManager()
	{
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(Constants.BASE_URL)
				.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
				.addConverterFactory(GsonConverterFactory.create())
				.build();
		mRedditService = retrofit.create(RedditService.class);
	}

	public Observable<ArrayList<Feed>> load(String text, int limit)
	{
		return mRedditService.search(text, limit, "", "")
		                     .flatMap(reddit -> Observable.just(reddit.getData().getFeeds()));
	}


	public Observable<ArrayList<Feed>> loadAfter(String text, int limit, String after)
	{
		return mRedditService.search(text, limit, after, "")
		                     .flatMap(reddit -> Observable.just(reddit.getData().getFeeds()));
	}

	public Observable<ArrayList<Feed>> loadBefore(String text, int limit, String before)
	{
		return mRedditService.search(text, limit, "", before)
		                     .flatMap(reddit -> Observable.just(reddit.getData().getFeeds()));
	}

	public RedditService getRedditService()
	{
		return mRedditService;
	}

	public void setRedditService(RedditService redditService)
	{
		this.mRedditService = redditService;
	}
}

package com.red.mn.redditreader.network;

import android.content.Context;

import com.red.mn.redditreader.Constants;
import com.red.mn.redditreader.model.Feed;

import java.util.ArrayList;

import retrofit.RestAdapter;
import rx.Observable;

/**
 * Created by manuMohan on 15/07/12.
 */
public class NetworkManager {
    private static NetworkManager mSharedNetworkManager;

    private RedditService mRedditService;
    private static final int FETCH_COUNT = 25;
    private static final String SEARCH_TEXT = "tennis";

    public static NetworkManager getInstance() {
        if (mSharedNetworkManager == null) {
            mSharedNetworkManager = new NetworkManager();
        }
        return mSharedNetworkManager;
    }

    private NetworkManager() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.BASE_URL)
                .build();
        mRedditService = restAdapter.create(RedditService.class);
    }

    public Observable<ArrayList<Feed>> load() {
        return mRedditService.search(SEARCH_TEXT, FETCH_COUNT, "", "")
                .flatMap(reddit -> Observable.just(reddit.getData().getFeeds()));
    }


    public Observable<ArrayList<Feed>> loadAfter( String after) {
        return mRedditService.search(SEARCH_TEXT, FETCH_COUNT, after, "")
                .flatMap(reddit -> Observable.just(reddit.getData().getFeeds()));
    }

    public Observable<ArrayList<Feed>> loadBefore( String before) {
        return mRedditService.search(SEARCH_TEXT, FETCH_COUNT, "", before)
                .flatMap(reddit -> Observable.just(reddit.getData().getFeeds()));
    }


}

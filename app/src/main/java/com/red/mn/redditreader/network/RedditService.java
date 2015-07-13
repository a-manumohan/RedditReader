package com.red.mn.redditreader.network;


import com.red.mn.redditreader.model.Reddit;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by manuMohan on 15/07/12.
 */
public interface RedditService {
    @GET("/search/.json")
    Observable<Reddit> search(
            @Query("q") String query,
            @Query("limit") int limit,
            @Query("after") String after,
            @Query("before") String before
    );
}

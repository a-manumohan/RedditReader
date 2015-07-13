package com.red.mn.redditreader.network;

import com.red.mn.redditreader.BuildConfig;
import com.red.mn.redditreader.model.Feed;
import com.red.mn.redditreader.model.Reddit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import rx.Observable;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Created by manuMohan on 15/07/13.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 21)
public class NetworkManagerTest {
    private NetworkManager networkManager;

    @Mock
    RedditService redditService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        networkManager = NetworkManager.getInstance();
        networkManager.setRedditService(redditService);
    }

    @Test
    public void load() {
        String searchText = "tennis", before = "", after = "";
        int limit = 3;
        Reddit reddit = new Reddit();
        Reddit.Data data = new Reddit.Data();
        ArrayList<Feed> feeds = new ArrayList<>();
        feeds.add(new Feed());
        feeds.add(new Feed());
        feeds.add(new Feed());
        data.setFeeds(feeds);
        reddit.setData(data);
        when(redditService.search(searchText, limit, after, before)).thenReturn(Observable.just(reddit));

        assertEquals("mapping error", limit, networkManager.load(searchText,limit).toBlocking().single().size());

    }
}

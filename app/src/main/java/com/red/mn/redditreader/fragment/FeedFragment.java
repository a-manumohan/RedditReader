package com.red.mn.redditreader.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.red.mn.redditreader.R;
import com.red.mn.redditreader.adapter.FeedsAdapter;
import com.red.mn.redditreader.db.FeedDAO;
import com.red.mn.redditreader.model.Feed;
import com.red.mn.redditreader.network.NetworkManager;

import org.joda.time.DateTime;
import org.joda.time.Minutes;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class FeedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private OnFragmentInteractionListener mListener;

    private static final String SEARCH_TEXT = "tennis";
    private static final int FETCH_COUNT = 25;

    @Bind(R.id.feeds_recyclerview)
    RecyclerView mFeedsRecyclerView;

    @Bind(R.id.progress_bar)
    ProgressBar mProgressBar;

    @Bind(R.id.refresh_feed)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private FeedsAdapter mFeedsAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private ArrayList<FeedDAO> mFeeds;

    private NetworkManager mNetworkManager;
    private boolean loading = false;


    public static FeedFragment newInstance() {
        return new FeedFragment();
    }

    public FeedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mNetworkManager = NetworkManager.getInstance();
        initViews();
        fetchFeeds();
    }

    private void initViews() {
        mFeedsAdapter = new FeedsAdapter();
        mFeedsRecyclerView.setAdapter(mFeedsAdapter);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mFeedsRecyclerView.setLayoutManager(mLinearLayoutManager);
        mFeedsRecyclerView.addOnScrollListener(new FeedScrollListener() {
            @Override
            public void onLoadMore() {
                if (!loading)
                    fetchOlder();
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    private void updateViews() {
        mFeedsAdapter.setFeeds(mFeeds);
        mFeedsAdapter.notifyDataSetChanged();
    }

    private void fetchFeeds() {

        FeedDAO feed = getLastFeed(getActivity());
        if (feed != null) {
            DateTime createdTime = new DateTime(feed.getUpdated());
            Minutes minutes = Minutes.minutesBetween(createdTime, new DateTime());
            if (minutes.getMinutes() <= 5) {
                mFeeds = getFeeds(getActivity());
                updateViews();
                return;
            }
        }
        mProgressBar.setVisibility(View.VISIBLE);
        loading = true;
        clearFeeds(getActivity());
        mNetworkManager.load(SEARCH_TEXT,FETCH_COUNT)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(feeds -> {
                    persistFeeds(getActivity(), feeds);
                    return getFeeds(getActivity());
                })
                .subscribe(
                        feeds -> {
                            mFeeds = feeds;
                            updateViews();
                        },
                        throwable -> mProgressBar.setVisibility(View.INVISIBLE),
                        () -> {
                            mProgressBar.setVisibility(View.INVISIBLE);
                            loading = false;
                        }
                );
    }

    private void fetchOlder() {
        loading = true;
        mProgressBar.setVisibility(View.VISIBLE);
        FeedDAO lastFeed = mFeeds.get(mFeeds.size() - 1);
        mNetworkManager.loadAfter(SEARCH_TEXT,FETCH_COUNT,lastFeed.getName())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(feeds -> {
                    persistFeeds(getActivity(), feeds);
                    return getFeeds(getActivity());
                })
                .subscribe(
                        feeds -> {
                            mFeeds = feeds;
                            updateViews();
                        },
                        throwable -> mProgressBar.setVisibility(View.INVISIBLE),
                        () -> {
                            mProgressBar.setVisibility(View.INVISIBLE);
                            loading = false;
                        }
                );
    }

    private void fetchNew() {
        loading = true;
        mProgressBar.setVisibility(View.VISIBLE);
        FeedDAO firstFeed = mFeeds != null && mFeeds.size() > 0 ? mFeeds.get(0) : null;
        Observable<ArrayList<Feed>> fetchObservable = firstFeed != null ? mNetworkManager.loadBefore(SEARCH_TEXT,FETCH_COUNT,firstFeed.getName()) : mNetworkManager.load(SEARCH_TEXT,FETCH_COUNT);
        fetchObservable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(feeds -> {
                    persistFeeds(getActivity(), feeds);
                    return getFeeds(getActivity());
                })
                .subscribe(
                        feeds -> {
                            mFeeds = feeds;
                            updateViews();
//                            if (feeds != null)
//                                mFeedsRecyclerView.smoothScrollToPosition(feeds.size());
                        },
                        throwable -> {
                            mProgressBar.setVisibility(View.INVISIBLE);
                            mSwipeRefreshLayout.setRefreshing(false);
                        },
                        () -> {
                            mProgressBar.setVisibility(View.INVISIBLE);
                            mSwipeRefreshLayout.setRefreshing(false);
                            loading = false;
                        }
                );
    }

    private void persistFeeds(Context context, ArrayList<Feed> feeds) {
        Realm realm = Realm.getInstance(context);
        long updatedTime = new DateTime().getMillis();
        realm.beginTransaction();
        for (Feed feed : feeds) {
            FeedDAO feedDAO = realm.createObject(FeedDAO.class);
            feedDAO.setTitle(feed.getData().getTitle());
            feedDAO.setCreated(feed.getData().getCreated());
            feedDAO.setName(feed.getData().getName());
            feedDAO.setThumbnail(feed.getData().getThumbnail());
            feedDAO.setUpdated(updatedTime);
        }
        realm.commitTransaction();


    }

    private ArrayList<FeedDAO> getFeeds(Context context) {
        Realm realm = Realm.getInstance(context);
        RealmResults<FeedDAO> feedDAORealmResults = realm.where(FeedDAO.class).findAll();
        ArrayList<FeedDAO> feedDAOs = new ArrayList<>();
        for (FeedDAO feed : feedDAORealmResults) {
            feedDAOs.add(feed);
        }
        return feedDAOs;
    }

    private FeedDAO getLastFeed(Context context) {
        Realm realm = Realm.getInstance(context);
        RealmResults<FeedDAO> feedDao = realm.where(FeedDAO.class).findAll();
        if (feedDao.size() > 0) {
            return feedDao.first();
        }
        return null;
    }

    private void clearFeeds(Context context) {
        Realm realm = Realm.getInstance(context);
        realm.beginTransaction();
        realm.clear(FeedDAO.class);
        realm.commitTransaction();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onRefresh() {
        fetchNew();
    }


    public interface OnFragmentInteractionListener {
    }

    public abstract class FeedScrollListener extends RecyclerView.OnScrollListener {
        private int visibleThreshold = 5;
        int firstVisibleItem, visibleItemCount, totalItemCount;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            visibleItemCount = recyclerView.getChildCount();
            totalItemCount = mLinearLayoutManager.getItemCount();
            firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

            if (!loading && (totalItemCount - visibleItemCount)
                    <= (firstVisibleItem + visibleThreshold)) {
                // End has been reached

                onLoadMore();
            }
        }

        public abstract void onLoadMore();
    }
}

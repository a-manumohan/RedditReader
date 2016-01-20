package com.red.mn.redditreader.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.red.mn.redditreader.R;
import com.red.mn.redditreader.db.FeedDAO;
import com.squareup.picasso.Picasso;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by manuMohan on 15/07/12.
 */
public class FeedsAdapter extends RecyclerView.Adapter<FeedsAdapter.FeedViewHolder>
{

	private ArrayList<FeedDAO> mFeeds;

	@Override
	public FeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed, parent, false);
		return new FeedViewHolder(view);
	}

	@Override
	public void onBindViewHolder(FeedViewHolder holder, int position)
	{
		FeedDAO feed = mFeeds.get(position);
		holder.feedTitleTextView.setText(feed.getTitle());
		LocalDateTime dateTime = Instant.ofEpochMilli(feed.getCreated()).atZone(ZoneId.systemDefault()).toLocalDateTime();
		holder.feedDateTextView.setText(dateTime.format(DateTimeFormatter.ofPattern("dd-MMM-YYYY kk:mm:ss")));
		if(!feed.getThumbnail().equals("") && !feed.getThumbnail().equals("self"))
		{
			holder.feedThumbnailImageView.setVisibility(View.VISIBLE);
			Picasso.with(holder.feedThumbnailImageView.getContext()).load(feed.getThumbnail()).into(holder.feedThumbnailImageView);
		}
		else
		{
			holder.feedThumbnailImageView.setVisibility(View.GONE);
		}
	}

	@Override
	public int getItemCount()
	{
		return mFeeds == null ? 0 : mFeeds.size();
	}

	public class FeedViewHolder extends RecyclerView.ViewHolder
	{
		@Bind(R.id.feed_title)
		TextView feedTitleTextView;

		@Bind(R.id.feed_date)
		TextView feedDateTextView;

		@Bind(R.id.feed_thumbnail)
		ImageView feedThumbnailImageView;

		public FeedViewHolder(View itemView)
		{
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}

	public void setFeeds(ArrayList<FeedDAO> feeds)
	{
		this.mFeeds = feeds;
	}
}

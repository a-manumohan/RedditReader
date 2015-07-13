package com.red.mn.redditreader.db;

import io.realm.RealmObject;

/**
 * Created by manuMohan on 15/07/13.
 */
public class FeedDAO extends RealmObject{
    private String title;
    private long created;
    private String name;
    private String thumbnail;
    private long updated;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public long getUpdated() {
        return updated;
    }

    public void setUpdated(long updated) {
        this.updated = updated;
    }
}

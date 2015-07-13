package com.red.mn.redditreader.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by manuMohan on 15/07/12.
 */
public class Feed {
    @SerializedName("data")
    private Data data;

    public static class Data {
        @SerializedName("title")
        private String title;

        @SerializedName("created")
        private long created;

        @SerializedName("name")
        private String name;

        @SerializedName("thumbnail")
        private String thumbnail;

        public String getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }

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
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}

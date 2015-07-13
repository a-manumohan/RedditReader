package com.red.mn.redditreader.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by manuMohan on 15/07/12.
 */
public class Reddit {
    @SerializedName("kind")
    private String kind;

    @SerializedName("data")
    private Data data;

    public static class Data {

        @SerializedName("children")
        private ArrayList<Feed> feeds;

        public ArrayList<Feed> getFeeds() {
            return feeds;
        }

        public void setFeeds(ArrayList<Feed> feeds) {
            this.feeds = feeds;
        }
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}

package com.example.countingshakes.model;

/**
 * Created by cyn on 02/20/2016.
 */
public class PostRankingItem {
    private final String api_dev_key = "12345";
    private final String api_option = "paste";
    private RankingItem api_paste_code;

    public PostRankingItem(RankingItem api_paste_code) {
        this.api_paste_code = api_paste_code;
    }

    public String getApi_dev_key() {
        return api_dev_key;
    }

    public String getApi_option() {
        return api_option;
    }

    public RankingItem getApi_paste_code() {
        return api_paste_code;
    }
}

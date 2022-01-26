package com.example.news.NewsFragment.Newsstand;

public class SourceModel {
    String key,searchText;

    public SourceModel() {
    }

    public SourceModel(String key, String searchText) {
        this.key = key;
        this.searchText = searchText;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }
}

package com.example.news.Search;

public class SearchModel {
    String key,searchText;

    public SearchModel() {
    }

    public SearchModel(String key, String searchText) {
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

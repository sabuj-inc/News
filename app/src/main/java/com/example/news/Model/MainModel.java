package com.example.news.Model;

import java.util.ArrayList;

public class MainModel {

    String totalResults;
    ArrayList<NewsModel> articles;

    public MainModel(String totalResults, ArrayList<NewsModel> articles) {
        this.totalResults = totalResults;
        this.articles = articles;
    }

    public String getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(String totalResults) {
        this.totalResults = totalResults;
    }

    public ArrayList<NewsModel> getArticles() {
        return articles;
    }

    public void setArticles(ArrayList<NewsModel> articles) {
        this.articles = articles;
    }
}

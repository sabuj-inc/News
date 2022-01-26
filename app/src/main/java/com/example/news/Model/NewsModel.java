package com.example.news.Model;

import com.google.gson.annotations.SerializedName;

public class NewsModel {

    @SerializedName("source")
    public Source source;

    @SerializedName("title")
    String headLine;

    @SerializedName("url")
    String newsUrl;

    @SerializedName("urlToImage")
    String imageUrl;

    @SerializedName("publishedAt")
    String published;

    public NewsModel(){

    }

    public NewsModel(Source source, String headLine, String newsUrl, String imageUrl, String published) {
        this.source = source;
        this.headLine = headLine;
        this.newsUrl = newsUrl;
        this.imageUrl = imageUrl;
        this.published = published;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public String getHeadLine() {
        return headLine;
    }

    public void setHeadLine(String headLine) {
        this.headLine = headLine;
    }

    public String getNewsUrl() {
        return newsUrl;
    }

    public void setNewsUrl(String newsUrl) {
        this.newsUrl = newsUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPublished() {
        return published;
    }

    public void setPublished(String published) {
        this.published = published;
    }
}

package com.example.news.Model;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
public interface NewsInterface {
    @GET("top-headlines")
    Call<MainModel> getCountry(
            @Query("country") String country,
            @Query("page") int page,
            @Query("pageSize") int pageSize,
            @Query("apiKey") String apiKey
    );

    @GET("top-headlines")
    Call<MainModel> getCategory(
            @Query("category") String category,
            @Query("country") String country,
            @Query("page") int page,
            @Query("pageSize") int pageSize,
            @Query("apiKey") String apiKey
    );

    @GET("everything")
    Call<MainModel> getSearch(
            @Query("q") String q,
            @Query("page") int page,
            @Query("pageSize") int pageSize,
            @Query("apiKey") String apiKey
    );
    @GET("everything")
    Call<MainModel> getDomains(
            @Query("domains") String domains,
            @Query("page") int page,
            @Query("pageSize") int pageSize,
            @Query("apiKey") String apiKey
    );

}
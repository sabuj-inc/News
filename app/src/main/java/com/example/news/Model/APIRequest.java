package com.example.news.Model;

import android.app.Activity;
import android.content.SharedPreferences;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIRequest {
    static Retrofit retrofit;
    public static final String BASE_URL = "https://newsapi.org/v2/";
    public static final int loadLimit = 100;

    public static Retrofit retrofitRequest() {
        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build();
        return retrofit;
    }

    public static String API(Activity activity) {
        SharedPreferences sh = activity.getSharedPreferences("MySharedPref", 0);
        int incrementApi = sh.getInt("incrementApi", 0);

        String[] apiArray = {
                "9106bdd4c86040f3b7a353e0336d92d0",
                "403d43c367c04e67a90990ce0984e7c5",
                "5d7421c354fb4d4f93c52ee0c4df7e7e",
                "f37e92f6056e4bcb8b4d546e04e76e2b",
                "b5539958892a4372a63be67ff79860fd",
                "78a8bfb065fe45d9aa80521ea96c97d4",
                "3ed42ecf2648448b8d34e795e92dd367",
                "d68bcad8e7ec48ce8a13e87fc95c5b6a",
                "8e099ac988c049748f070f64df9f1c1c",
                "d19e3595b1c547a4ae1099df38c0288f",
                "7c77eb856d9f447e8c755dbf23991929",
                "ab80f2f0d4e64f89b3e4152e7560b9a4",
        };

        if (incrementApi > apiArray.length) {
            incrementApi =0;
            SharedPreferences.Editor editor = sh.edit();
            editor.putInt("incrementApi",incrementApi);
            editor.commit();
        }

        return apiArray[incrementApi];
    }
}

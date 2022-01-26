package com.example.news.Network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.example.news.FullNews;
import com.example.news.MainActivity;

public class NetworkReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        String status = NetworkUtil.getConnectivityStatusString(context);

        Log.d("network", status);
        if (status.isEmpty() || status.equals("No internet is available") || status.equals("No Internet Connection")) {
            status = "No Internet Connection";
            MainActivity.internet_connection.setVisibility(View.VISIBLE);
            MainActivity.internet_connection.setText(status);

            if(FullNews.web_internet_connection !=null){
                FullNews.web_internet_connection.setVisibility(View.VISIBLE);
                FullNews.web_internet_connection.setText(status);
            }

        }else {
            MainActivity.internet_connection.setVisibility(View.GONE);

            if(FullNews.web_internet_connection !=null){
                FullNews.web_internet_connection.setVisibility(View.GONE);
            }
        }

    }
}

package com.example.news.NewsFragment.Newsstand;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.news.MainActivity;
import com.example.news.Model.APIRequest;
import com.example.news.Model.MainModel;
import com.example.news.Model.NewsAdapter;
import com.example.news.Model.NewsInterface;
import com.example.news.Model.NewsModel;
import com.example.news.R;
import com.example.news.Search.ResultFragment;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SourceActivity extends AppCompatActivity {
    ImageView sourceBack;
    TextView sourceText;
    NestedScrollView newsNestedScrollView;
    RecyclerView newsRecyclerView;
    ProgressBar newsProgressBar;
    TextView newsEndMessage;
    int page = 1;
    int limit = 15;
    NewsAdapter newsAdapter;
    ArrayList<NewsModel> newsModel;
    String sourceTitle,sourceULR;
    ShimmerFrameLayout shimmerContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source);
        whiteStatus();

        sourceTitle = getIntent().getStringExtra("sourceTitle");
        sourceULR = getIntent().getStringExtra("sourceULR");
        shimmerContainer = (ShimmerFrameLayout) findViewById(R.id.preloadAnimation);
        shimmerContainer.startShimmerAnimation();

        //api call
        newsModel = new ArrayList<>();

        //find
        sourceBack = findViewById(R.id.sourceBack);
        sourceText = findViewById(R.id.sourceText);
        sourceText.setText(sourceTitle);
        newsNestedScrollView = findViewById(R.id.newsNestedScrollView);
        newsRecyclerView = findViewById(R.id.newsRecyclerView);
        newsProgressBar = findViewById(R.id.newsProgressBar);
        newsEndMessage = findViewById(R.id.newsEndMessage);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        newsRecyclerView.setLayoutManager(linearLayoutManager);
        newsRecyclerView.setHasFixedSize(true);

        newsAdapter = new NewsAdapter(SourceActivity.this, newsModel, 2);
        newsRecyclerView.setAdapter(newsAdapter);
        launcher();

        newsNestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    page++;
                    launcher();
                    Toast.makeText(SourceActivity.this, "" + page, Toast.LENGTH_SHORT).show();
                }
            }
        });

        sourceBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void launcher() {
        NewsInterface newsInterface = APIRequest.retrofitRequest().create(NewsInterface.class);
        Call<MainModel> call = newsInterface.getDomains(sourceULR.toLowerCase().replaceAll(" ",""), page, limit, APIRequest.API(SourceActivity.this));

        call.enqueue(new Callback<MainModel>() {
            @Override
            public void onResponse(Call<MainModel> call, Response<MainModel> response) {
                if (response.isSuccessful()) {
                    shimmerContainer.stopShimmerAnimation();
                    shimmerContainer.setVisibility(View.GONE);
                    if ((page * limit) > Integer.parseInt(response.body().getTotalResults()) || (page * limit) > APIRequest.loadLimit) {
                        newsEndMessage.setVisibility(View.VISIBLE);
                        newsProgressBar.setVisibility(View.GONE);
                    } else {
                        newsProgressBar.setVisibility(View.VISIBLE);
                        newsModel.addAll(response.body().getArticles());
                        newsAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<MainModel> call, Throwable t) {
                Toast.makeText(SourceActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void whiteStatus() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
            getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));// set status background white
        }
    }

}
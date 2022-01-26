package com.example.news.NewsFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.news.MainActivity;
import com.example.news.Model.APIRequest;
import com.example.news.Model.MainModel;
import com.example.news.Model.NewsAdapter;
import com.example.news.Model.NewsInterface;
import com.example.news.Model.NewsModel;
import com.example.news.R;
import com.example.news.Search.SearchActivity;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForYouFragment extends Fragment {
    private final String TABLE_NAME = "main";
    NestedScrollView newsNestedScrollView;
    ImageView searchImage;
    RecyclerView newsRecyclerView;
    ProgressBar newsProgressBar;
    TextView toolbarTitle,newsEndMessage;
    int page = 1;
    int limit = 15;
    NewsAdapter newsAdapter;
    ArrayList<NewsModel> newsModel;
    ShimmerFrameLayout shimmerContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_for_you, container, false);

        shimmerContainer = (ShimmerFrameLayout) view.findViewById(R.id.preloadAnimation);
        shimmerContainer.startShimmerAnimation();
        //api call
        newsModel = new ArrayList<>();

        //find
        searchImage = view.findViewById(R.id.searchImage);
        toolbarTitle = view.findViewById(R.id.toolbarTitle);
        toolbarTitle.setText("Daily News");
        newsNestedScrollView = view.findViewById(R.id.newsNestedScrollView);
        newsRecyclerView = view.findViewById(R.id.newsRecyclerView);
        newsProgressBar = view.findViewById(R.id.newsProgressBar);
        newsEndMessage = view.findViewById(R.id.newsEndMessage);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        newsRecyclerView.setLayoutManager(linearLayoutManager);
        newsRecyclerView.setHasFixedSize(true);

        newsAdapter = new NewsAdapter(getActivity(), newsModel, 1);
        newsRecyclerView.setAdapter(newsAdapter);
        launcher();

        newsNestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    page++;
                    launcher();
                }
            }
        });
        searchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    private void launcher() {
        NewsInterface newsInterface = APIRequest.retrofitRequest().create(NewsInterface.class);

        Call<MainModel> call = newsInterface.getCountry("us", page, limit, APIRequest.API(getActivity()));

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

                        //store into local database
                        MainActivity.storeResponse(TABLE_NAME, newsModel);

                    }

                }

            }

            @Override
            public void onFailure(Call<MainModel> call, Throwable t) {

                //Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();

                newsModel.addAll(MainActivity.fetchResponse(TABLE_NAME));
                shimmerContainer.stopShimmerAnimation();
                shimmerContainer.setVisibility(View.GONE);
                newsAdapter.notifyDataSetChanged();

            }
        });
    }


}
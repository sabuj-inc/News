package com.example.news.HeadlinesFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SportsFragment extends Fragment {
    private final String TABLE_NAME = "sports";
    NestedScrollView newsNestedScrollView;
    RecyclerView newsRecyclerView;
    ProgressBar newsProgressBar;
    TextView newsEndMessage;

    int page = 1;
    int limit = 15;
    NewsAdapter newsAdapter;
    ArrayList<NewsModel> newsModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_general, container, false);
        //api call
        newsModel = new ArrayList<>();

        //find
        newsNestedScrollView = view.findViewById(R.id.newsNestedScrollView);
        newsRecyclerView = view.findViewById(R.id.newsRecyclerView);
        newsProgressBar = view.findViewById(R.id.newsProgressBar);
        newsEndMessage = view.findViewById(R.id.newsEndMessage);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        newsRecyclerView.setLayoutManager(linearLayoutManager);
        newsRecyclerView.setHasFixedSize(true);

        newsAdapter = new NewsAdapter(getActivity(), newsModel,2);
        newsRecyclerView.setAdapter(newsAdapter);
        launcher();

        newsNestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    page++;
                    //Toast.makeText(NewsActivity.this, ""+page, Toast.LENGTH_SHORT).show();
                    launcher();
                    Toast.makeText(getActivity(), "" + page, Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    private void launcher() {
        NewsInterface newsInterface = APIRequest.retrofitRequest().create(NewsInterface.class);
        Call<MainModel> call = newsInterface.getCategory("sports","us",page,limit,APIRequest.API(getActivity()));

        call.enqueue(new Callback<MainModel>() {
            @Override
            public void onResponse(Call<MainModel> call, Response<MainModel> response) {
                if (response.isSuccessful()) {
                    if ((page * limit) > Integer.parseInt(response.body().getTotalResults()) || (page * limit) > APIRequest.loadLimit) {
                        newsEndMessage.setVisibility(View.VISIBLE);
                        newsProgressBar.setVisibility(View.GONE);
                    } else {
                        newsProgressBar.setVisibility(View.VISIBLE);
                        newsModel.addAll(response.body().getArticles());
                        newsAdapter.notifyDataSetChanged();

                        //store into local database
                        MainActivity.storeResponse(TABLE_NAME,newsModel);
                    }
                }
            }

            @Override
            public void onFailure(Call<MainModel> call, Throwable t) {
                newsModel.addAll(MainActivity.fetchResponse(TABLE_NAME));
                newsAdapter.notifyDataSetChanged();
            }
        });
    }
}
package com.example.news.Search;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.news.Model.APIRequest;
import com.example.news.Model.MainModel;
import com.example.news.Model.NewsAdapter;
import com.example.news.Model.NewsInterface;
import com.example.news.Model.NewsModel;
import com.example.news.R;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResultFragment extends Fragment {
    NestedScrollView newsNestedScrollView;
    RecyclerView newsRecyclerView;
    ProgressBar newsProgressBar;
    TextView newsEndMessage;
    CardView toolBar;
    int page = 1;
    int limit = 15;
    NewsAdapter newsAdapter;
    ArrayList<NewsModel> newsModel;
    TextView resultText;

    ShimmerFrameLayout shimmerContainer;
    ResultPassFragment resultPassFragment;
    String currentSearch;

    ResultFragment(String currentSearch) {
        this.currentSearch = currentSearch;
    }

    public interface ResultPassFragment {
        void passResultData(String search);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, container, false);
        shimmerContainer = (ShimmerFrameLayout) view.findViewById(R.id.preloadAnimation);
        shimmerContainer.startShimmerAnimation();

        //api call
        newsModel = new ArrayList<>();

        //find
        toolBar = view.findViewById(R.id.toolBar);
        resultText = view.findViewById(R.id.resultText);
        newsNestedScrollView = view.findViewById(R.id.newsNestedScrollView);
        newsRecyclerView = view.findViewById(R.id.newsRecyclerView);
        newsProgressBar = view.findViewById(R.id.newsProgressBar);
        newsEndMessage = view.findViewById(R.id.newsEndMessage);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        newsRecyclerView.setLayoutManager(linearLayoutManager);
        newsRecyclerView.setHasFixedSize(true);

        newsAdapter = new NewsAdapter(getActivity(), newsModel, 2);
        newsRecyclerView.setAdapter(newsAdapter);
        launcher();

        newsNestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    page++;
                    launcher();
                    Toast.makeText(getActivity(), "" + page, Toast.LENGTH_SHORT).show();
                }
            }
        });

        toolBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultPassFragment.passResultData(resultText.getText().toString());
            }
        });
        return view;
    }

    private void launcher() {
        resultText.setText(currentSearch);
        NewsInterface newsInterface = APIRequest.retrofitRequest().create(NewsInterface.class);
        Call<MainModel> call = newsInterface.getSearch(currentSearch, page, limit, APIRequest.API(getActivity()));

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
                Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;
        try {
            resultPassFragment = (ResultPassFragment) activity;
        } catch (Exception exception) {

        }
    }
}
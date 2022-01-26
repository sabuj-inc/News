package com.example.news.NewsFragment;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.news.Database.DatabaseSQLite;
import com.example.news.HeadlinesFragment.TabLayoutAdapter;
import com.example.news.Model.NewsModel;
import com.example.news.R;
import com.example.news.Search.SearchActivity;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class HeadlinesFragment extends Fragment {
    TabLayout tabLayout;
    ViewPager viewPager;
    public static ShimmerFrameLayout shimmerContainer;
    ImageView searchImage;
    TextView toolbarTitle;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_headlines, container, false);
        shimmerContainer = (ShimmerFrameLayout) view.findViewById(R.id.preloadAnimation);
        shimmerContainer.startShimmerAnimation();
        searchImage = view.findViewById(R.id.searchImage);
        toolbarTitle = view.findViewById(R.id.toolbarTitle);
        toolbarTitle.setText("Headlines");

        tabLayout = view.findViewById(R.id.tablayout);
        viewPager = view.findViewById(R.id.viewPager);
        tabLayout.addTab(tabLayout.newTab().setText("Latest"));
        tabLayout.addTab(tabLayout.newTab().setText("Country"));
        tabLayout.addTab(tabLayout.newTab().setText("Entertainment"));
        tabLayout.addTab(tabLayout.newTab().setText("Business"));
        tabLayout.addTab(tabLayout.newTab().setText("Health"));
        tabLayout.addTab(tabLayout.newTab().setText("Science"));
        tabLayout.addTab(tabLayout.newTab().setText("Sports"));
        tabLayout.addTab(tabLayout.newTab().setText("Technology"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        TabLayoutAdapter adapter = new TabLayoutAdapter(getActivity(), getActivity().getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setOffscreenPageLimit(tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

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


}
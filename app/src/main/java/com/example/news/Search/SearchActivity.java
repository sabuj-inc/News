package com.example.news.Search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.news.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SearchActivity extends AppCompatActivity implements ResultFragment.ResultPassFragment,SearchFragment.SearchPassFragment{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        whiteStatus();

        Fragment fragment = new SearchFragment("");
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.search_container,fragment).commit();

    }
    public void whiteStatus() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
            getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));// set status background white
        }
    }

    //from(Result Fragment) call search fragment
    @Override
    public void passResultData(String search) {
        Fragment fragment = new SearchFragment(search);
        SearchFragment searchFragment = new SearchFragment("");
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.search_container,fragment)
                .addToBackStack(searchFragment.getClass().getName())
                .commit();
    }

    //from(Search Fragment) call result fragment
    @Override
    public void passSearchData(String search) {
        Fragment fragment = new ResultFragment(search);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.search_container,fragment).commit();
    }

}
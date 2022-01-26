package com.example.news;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.example.news.Database.DatabaseSQLite;
import com.example.news.Model.NewsModel;
import com.example.news.Model.Source;
import com.example.news.Network.NetworkReceiver;
import com.example.news.NewsFragment.ForYouFragment;
import com.example.news.NewsFragment.HeadlinesFragment;
import com.example.news.NewsFragment.Newsstand.SourceFragment;
import com.example.news.NewsFragment.Saved.SavedFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private BroadcastReceiver NetworkReceiver = null;
    FragmentManager fragmentManager = getSupportFragmentManager();
    public static TextView internet_connection;
    public static BottomNavigationView bottomNavigationView;
    //database
    static SQLiteDatabase sqLiteDatabase;
    static DatabaseSQLite database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        whiteStatus();
        internet_connection = findViewById(R.id.internet_connection);
        NetworkReceiver = new NetworkReceiver();


        broadcastIntent();

        //api increment
        apiIncrement();
        fragmentManager.beginTransaction().add(R.id.fragment_container, new ForYouFragment(), "one").commit();
        //database
        database = new DatabaseSQLite(this);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

    }


    public static void storeResponse(String TABLE_NAME, ArrayList<NewsModel> newsModel) {
        //store first response
        sqLiteDatabase = database.getWritableDatabase();
        DatabaseSQLite.TABLE_NAME = TABLE_NAME;
        sqLiteDatabase.execSQL(DatabaseSQLite.DELETE_TABLE(TABLE_NAME));
        sqLiteDatabase.execSQL(DatabaseSQLite.CREATE_TABLE(TABLE_NAME));
        for (int i = 0; i < newsModel.size(); i++) {
            database.insertData(
                    newsModel.get(i).getSource().getName(),
                    newsModel.get(i).getHeadLine(),
                    newsModel.get(i).getNewsUrl(),
                    newsModel.get(i).getImageUrl(),
                    newsModel.get(i).getPublished()
            );
        }
    }

    @SuppressLint("Range")
    public static ArrayList<NewsModel> fetchResponse(String TABLE_NAME) {
        ArrayList<NewsModel> newsModel = new ArrayList<>();
        DatabaseSQLite.TABLE_NAME = TABLE_NAME;
        sqLiteDatabase = database.getWritableDatabase();
        Cursor cursor = database.readData();
        if (cursor.getCount() == 0) {
            internet_connection.setText("The first time you use News app,you need to be online");
        } else {
            Source source = new Source();
            while (cursor.moveToNext()) {
                source.setName(cursor.getString(cursor.getColumnIndex("source")));
                newsModel.add(new NewsModel(
                        source,
                        cursor.getString(cursor.getColumnIndex("title")),
                        cursor.getString(cursor.getColumnIndex("url")),
                        cursor.getString(cursor.getColumnIndex("image")),
                        cursor.getString(cursor.getColumnIndex("publish"))
                ));
            }

        }
        return newsModel;
    }

    private void apiIncrement() {
        SharedPreferences sh = getSharedPreferences("MySharedPref", 0);
        int incrementApi = sh.getInt("incrementApi", 0);
        SharedPreferences.Editor editor = sh.edit();
        editor.putInt("incrementApi", incrementApi + 1);
        editor.commit();
    }

    public void broadcastIntent() {
        registerReceiver(NetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int clicked = item.getItemId();
                    if (clicked == R.id.forYou) {
                        if (fragmentManager.findFragmentByTag("one") != null) {
                            //if the fragment exists, show it.
                            fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("one")).commit();
                        } else {
                            //if the fragment does not exist, add it to fragment manager.
                            fragmentManager.beginTransaction().add(R.id.fragment_container, new ForYouFragment(), "one").commit();
                        }
                        hideFragment(new String[]{"", "two", "three", "four"});
                    } else if (clicked == R.id.headlines) {
                        if (fragmentManager.findFragmentByTag("two") != null) {
                            //if the fragment exists, show it.
                            fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("two")).commit();
                        } else {
                            //if the fragment does not exist, add it to fragment manager.
                            fragmentManager.beginTransaction().add(R.id.fragment_container, new HeadlinesFragment(), "two").commit();
                        }
                        hideFragment(new String[]{"one", "", "three", "four"});
                    } else if (clicked == R.id.following) {
                        if (fragmentManager.findFragmentByTag("three") != null) {
                            //if the fragment exists, show it.
                            fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("three")).commit();
                            SourceFragment.refresh();
                        } else {
                            //if the fragment does not exist, add it to fragment manager.
                            fragmentManager.beginTransaction().add(R.id.fragment_container, new SourceFragment(), "three").commit();
                        }
                        hideFragment(new String[]{"one", "two", "", "four"});
                    } else if (clicked == R.id.saved) {
                        if (fragmentManager.findFragmentByTag("four") != null) {
                            //if the fragment exists, show it.
                            fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("four")).commit();
                            SavedFragment.refresh();
                        } else {
                            //if the fragment does not exist, add it to fragment manager.
                            fragmentManager.beginTransaction().add(R.id.fragment_container, new SavedFragment(), "four").commit();
                        }
                        hideFragment(new String[]{"one", "two", "three", ""});
                    }

                    return true;
                }
            };

    private void hideFragment(String[] tag) {
        for (String fragmentTag : tag) {
            if (fragmentManager.findFragmentByTag(fragmentTag) != null) {
                //if the other fragment is visible, hide it.
                fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(fragmentTag)).commit();
            }
        }

    }

    public void whiteStatus() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
            getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));// set status background white
        }
    }


}
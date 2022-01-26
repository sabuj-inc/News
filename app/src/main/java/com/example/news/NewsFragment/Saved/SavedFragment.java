package com.example.news.NewsFragment.Saved;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.news.Database.DatabaseSQLite;
import com.example.news.Database.SourceDatabase;
import com.example.news.MainActivity;
import com.example.news.Model.NewsModel;
import com.example.news.NewsFragment.Newsstand.SourceFragment;
import com.example.news.R;
import com.example.news.Search.SearchActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;


public class SavedFragment extends Fragment {
    NestedScrollView newsNestedScrollView;
    static RecyclerView newsRecyclerView, savedSourceRecyclerView;
    ProgressBar newsProgressBar;
    TextView followSource, newsEndMessage;
    ImageView searchImage;
    TextView toolbarTitle;
    static SaveAdapter saveAdapter;
    static ArrayList<NewsModel> saveModel;

    //source
    static ArrayList<String> savedSourceArrayList;
    static SourceDatabase sourceDatabase;
    SQLiteDatabase database;
    static SavedSourceAdapter savedSourceAdapter;
    private static LinearLayout noSourceLayout;
    private static RelativeLayout noNewsLayout;
    static TextView viewAllSource;
    static int SOURCE_SIZE = 8;


    FragmentManager fragmentManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saved, container, false);
        fragmentManager = getActivity().getSupportFragmentManager();
        //default
        searchImage = view.findViewById(R.id.searchImage);
        toolbarTitle = view.findViewById(R.id.toolbarTitle);
        toolbarTitle.setText("Saved News");
        newsNestedScrollView = view.findViewById(R.id.newsNestedScrollView);
        newsProgressBar = view.findViewById(R.id.newsProgressBar);
        newsEndMessage = view.findViewById(R.id.newsEndMessage);

        //saved source
        savedSourceArrayList = new ArrayList<>();
        noSourceLayout = view.findViewById(R.id.noSourceLayout);
        followSource = view.findViewById(R.id.followSource);
        savedSourceRecyclerView = view.findViewById(R.id.savedSourceRecyclerView);
        viewAllSource = view.findViewById(R.id.viewAllSource);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 4);
        savedSourceRecyclerView.setLayoutManager(gridLayoutManager);
        savedSourceAdapter = new SavedSourceAdapter(getActivity(), savedSource());


        //saved news
        noNewsLayout = view.findViewById(R.id.noNewsLayout);
        newsRecyclerView = view.findViewById(R.id.newsRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        newsRecyclerView.setLayoutManager(linearLayoutManager);
        newsRecyclerView.setHasFixedSize(false);
        saveModel = new ArrayList<>();

        saveModel.addAll(MainActivity.fetchResponse(DatabaseSQLite.SAVED_TABLE));
        saveAdapter = new SaveAdapter(getActivity(), saveModel);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(newsRecyclerView);

        checkArrayList();
        savedSourceRecyclerView.setAdapter(savedSourceAdapter);
        newsRecyclerView.setAdapter(saveAdapter);

        searchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });

        viewAllSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewAllSource.setVisibility(View.GONE);
                SOURCE_SIZE = savedSourceArrayList.size();
                savedSourceAdapter.notifyDataSetChanged();
            }
        });
        followSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.bottomNavigationView.getMenu().findItem(R.id.following).setChecked(true);
                if (fragmentManager.findFragmentByTag("three") != null) {
                    fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("three")).commit();
                } else {
                    fragmentManager.beginTransaction().add(R.id.fragment_container, new SourceFragment(), "three").commit();
                }
                fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("four")).commit();
            }
        });
        savedSourceAdapter.setOnItemClickListener(new SavedSourceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                deleteDialog(position);
            }
        });
        return view;
    }

    private static void checkArrayList() {
        SOURCE_SIZE = 8;
        //load all
        if (SOURCE_SIZE >= savedSourceArrayList.size()) {
            SOURCE_SIZE = savedSourceArrayList.size();
        }

        if (savedSourceArrayList.size() > 8) {
            viewAllSource.setVisibility(View.VISIBLE);
        } else {
            viewAllSource.setVisibility(View.GONE);
        }

        if (savedSourceArrayList.size() > 0) {
            noSourceLayout.setVisibility(View.GONE);
        } else {
            noSourceLayout.setVisibility(View.VISIBLE);
        }
        if (saveModel.size() > 0) {
            noNewsLayout.setVisibility(View.GONE);
        } else {
            noNewsLayout.setVisibility(View.VISIBLE);
        }
    }

    public static void refresh() {
        //source
        savedSourceArrayList.clear();
        Cursor cursor = sourceDatabase.readData();
        while (cursor.moveToNext()) {
            savedSourceArrayList.add(cursor.getString(1));
        }

        //news
        saveModel.clear();
        saveModel.addAll(MainActivity.fetchResponse(DatabaseSQLite.SAVED_TABLE));


        checkArrayList();
        savedSourceAdapter.notifyDataSetChanged();
        saveAdapter.notifyDataSetChanged();
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            saveAdapter.saveItemDelete(saveModel.get(viewHolder.getAdapterPosition()).getNewsUrl());
            saveAdapter.saveModel.remove(viewHolder.getAdapterPosition());
            saveAdapter.notifyDataSetChanged();
            checkArrayList();
        }
    };

    private ArrayList<String> savedSource() {
        sourceDatabase = new SourceDatabase(getActivity());
        database = sourceDatabase.getReadableDatabase();
        Cursor cursor = sourceDatabase.readData();
        while (cursor.moveToNext()) {
            savedSourceArrayList.add(cursor.getString(1));
        }
        return savedSourceArrayList;
    }

    private void deleteDialog(int position) {
        BottomSheetDialog dialog = new BottomSheetDialog(getActivity());
        dialog.setContentView(R.layout.remove_source_dialog);
        dialog.show();
        LinearLayout RemoveSourceNews = dialog.findViewById(R.id.RemoveSourceNews);
        RemoveSourceNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveItemDelete(savedSourceArrayList.get(position));
                dialog.dismiss();
                refresh();
            }
        });
    }

    public void saveItemDelete(String search) {
        database = sourceDatabase.getWritableDatabase();
        ArrayList<String> searchArray = new ArrayList<>();
        Cursor searchCursor = sourceDatabase.searchData(search);
        if (searchCursor.getCount() != 0) {
            while (searchCursor.moveToNext()) {
                searchArray.add(searchCursor.getString(0));
            }
            database.delete(SourceDatabase.TABLE_NAME, "_id=?", new String[]{searchArray.get(0)});
        }

    }


}
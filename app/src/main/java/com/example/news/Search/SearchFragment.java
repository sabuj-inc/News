package com.example.news.Search;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.news.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchFragment extends Fragment {
    ArrayList<SearchModel> searchArray,filteredlist;
    ImageView searchBack;
    EditText searchNews;
    RecyclerView searchRecyclerView;
    SearchAdapter searchAdapter;
    DatabaseReference databaseReference;
    String lastSearch;

    SearchPassFragment searchPassFragment;

    public interface SearchPassFragment {
        void passSearchData(String search);
    }

    SearchFragment(String lastSearch) {
        this.lastSearch = lastSearch;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        searchArray = new ArrayList();
        //database
        databaseReference = FirebaseDatabase.getInstance().getReference("search");
        searchBack = view.findViewById(R.id.searchBack);
        searchNews = view.findViewById(R.id.searchNews);
        searchRecyclerView = view.findViewById(R.id.search_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        searchRecyclerView.setLayoutManager(linearLayoutManager);
        searchAdapter = new SearchAdapter(getActivity(), searchArray);

        //append search text from result fragment
        searchNews.setText(lastSearch);
        filter(lastSearch);
        searchNews.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                filter(s.toString().trim().toLowerCase());


            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        searchNews.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchPassFragment.passSearchData(searchNews.getText().toString());
                    return true;
                }
                return false;
            }
        });

        searchAdapter.setOnItemClickListener(new SearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                searchPassFragment.passSearchData(filteredlist.get(position).getSearchText());
            }
        });


        searchBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });


        return view;
    }


    private void filter(String text) {
        filteredlist = new ArrayList<>();
        for (SearchModel item : searchArray) {
            if (item.getSearchText().toLowerCase().contains(text.toLowerCase())) {
                filteredlist.add(item);
            }
        }
        if (text.isEmpty()) {
            filteredlist.clear();
        }
        searchAdapter.filterList(filteredlist);
    }

    @Override
    public void onStart() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                searchArray.clear();
                for (DataSnapshot customDataSnapshot : snapshot.getChildren()) {
                    SearchModel searchModelObject = customDataSnapshot.getValue(SearchModel.class);
                    searchArray.add(searchModelObject);
                }
                searchRecyclerView.setAdapter(searchAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        super.onStart();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        Activity activity = (Activity) context;
        super.onAttach(context);
        try {
            searchPassFragment = (SearchPassFragment) activity;
        } catch (Exception ignored) {

        }
    }
}
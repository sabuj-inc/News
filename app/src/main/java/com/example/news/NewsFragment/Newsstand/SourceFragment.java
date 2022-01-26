package com.example.news.NewsFragment.Newsstand;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.news.R;
import com.example.news.Search.SearchActivity;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class SourceFragment extends Fragment {
    ArrayList<SourceModel> sourceArray;
    static RecyclerView sourceRecyclerView;
    static SourceAdapter sourceAdapter;
    DatabaseReference databaseReference;
    ShimmerFrameLayout preloadAnimation;
    ImageView searchImage;
    TextView toolbarTitle;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_source, container, false);
        preloadAnimation = view.findViewById(R.id.preloadAnimation);
        preloadAnimation.startShimmerAnimation();
        //database
        databaseReference = FirebaseDatabase.getInstance().getReference("source");

        sourceArray = new ArrayList<>();
        searchImage = view.findViewById(R.id.searchImage);
        toolbarTitle = view.findViewById(R.id.toolbarTitle);
        toolbarTitle.setText("Top News Source");
        sourceRecyclerView = view.findViewById(R.id.sourceRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        sourceRecyclerView.setLayoutManager(linearLayoutManager);
        sourceRecyclerView.setHasFixedSize(true);
        sourceAdapter = new SourceAdapter(getActivity(), sourceArray);
        sourceRecyclerView.setAdapter(sourceAdapter);

        searchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
    public static void refresh(){
        sourceAdapter.readAll();
        sourceAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sourceArray.clear();
                for (DataSnapshot customDataSnapshot : snapshot.getChildren()) {
                    SourceModel sourceModelObject = customDataSnapshot.getValue(SourceModel.class);
                    sourceArray.add(sourceModelObject);
                }
                sourceAdapter.notifyDataSetChanged();
                preloadAnimation.stopShimmerAnimation();
                preloadAnimation.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        super.onStart();
    }


}
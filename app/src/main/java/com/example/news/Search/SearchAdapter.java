package com.example.news.Search;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.news.R;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    Activity activity;
    ArrayList<SearchModel> searchArray;

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public SearchAdapter(Activity activity, ArrayList<SearchModel> searchArray) {
        this.activity = activity;
        this.searchArray = searchArray;
    }

    // method for filtering our recyclerview items.
    public void filterList(ArrayList<SearchModel> filterllist) {
        searchArray = filterllist;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.search_simple, parent, false);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.searchTextView.setText(searchArray.get(position).getSearchText());

        String baseUrl = searchArray.get(position).getSearchText().concat(".com").toLowerCase();
        String finalUrl = "https://logo.clearbit.com/" + baseUrl + "?size=" + 300 + "&format=png";
        Glide.with(activity).load(finalUrl)
                .error(R.drawable.search_icon)
                .into(holder.logo);

    }

    @Override
    public int getItemCount() {
        return searchArray.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView logo;
        TextView searchTextView;

        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            logo = itemView.findViewById(R.id.logo);
            searchTextView = itemView.findViewById(R.id.searchTextView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}

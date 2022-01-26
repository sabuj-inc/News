package com.example.news.NewsFragment.Saved;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.news.NewsFragment.Newsstand.SourceActivity;
import com.example.news.R;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;


public class SavedSourceAdapter extends RecyclerView.Adapter<SavedSourceAdapter.ViewHolder> {
    Context context;
    ArrayList<String> savedSourceArrayList;

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    public SavedSourceAdapter(Context context, ArrayList<String> savedSourceArrayList) {
        this.context = context;
        this.savedSourceArrayList = savedSourceArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.saved_source_simple, parent, false);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        String original = savedSourceArrayList.get(position);
        String sourceTitle = original;
        if(original.contains(".")){
            // new string "Daily News"
            sourceTitle = original.substring(0,original.indexOf("."));

            //first latter upper case
            sourceTitle =sourceTitle.substring(0, 1).toUpperCase() + sourceTitle.substring(1);
        }


        String finalUrl = "https://logo.clearbit.com/" + original.replaceAll(" ","") + "?size=" + 500 + "&format=png";
        Glide.with(context).load(finalUrl).into(holder.savedSourceImage);
        holder.savedSourceText.setText(sourceTitle);

        String finalSourceTitle = sourceTitle;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SourceActivity.class);
                intent.putExtra("sourceTitle", finalSourceTitle);
                intent.putExtra("sourceULR", original);
                context.startActivity(intent);
            }
        });


    }



    @Override
    public int getItemCount() {
        return SavedFragment.SOURCE_SIZE;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView savedSourceImage;
        TextView savedSourceText;

        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            savedSourceImage = itemView.findViewById(R.id.savedSourceImage);
            savedSourceText = itemView.findViewById(R.id.savedSourceText);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                    return true;
                }
            });
        }
    }

}

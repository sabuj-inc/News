package com.example.news.NewsFragment.Saved;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.news.Database.DatabaseSQLite;
import com.example.news.FullNews;
import com.example.news.Model.NewsModel;
import com.example.news.R;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class SaveAdapter extends RecyclerView.Adapter<SaveAdapter.ViewHolder> {
    Activity context;
    public ArrayList<NewsModel> saveModel;
    DatabaseSQLite database;
    SQLiteDatabase sqLiteDatabase;

    public SaveAdapter(Activity context, ArrayList<NewsModel> saveModel) {
        this.context = context;
        this.saveModel = saveModel;
        database = new DatabaseSQLite(context);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.news_save_simple, parent, false);
        return new ViewHolder(view);
    }

    public void saveItemDelete(String search) {
        sqLiteDatabase = database.getWritableDatabase();
        ArrayList<String> searchArray = new ArrayList<>();
        Cursor searchCursor = database.searchData(search);
        if (searchCursor.getCount() != 0) {
            while (searchCursor.moveToNext()) {
                searchArray.add(searchCursor.getString(0));
            }
            sqLiteDatabase.delete(DatabaseSQLite.SAVED_TABLE, "_id=?", new String[]{searchArray.get(0)});
        }
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.newsCount.setText(String.valueOf(holder.getAdapterPosition()+1));
        holder.author.setText(saveModel.get(position).getSource().getName());

        String finalUrl="";
        try {
            URL url = new URL(saveModel.get(position).getNewsUrl());
            String baseUrl = url.getProtocol() + "://" + url.getHost();
            finalUrl = "https://logo.clearbit.com/" + baseUrl + "?size=" + 500 + "&format=png";
            Glide.with(context).load(finalUrl).into(holder.newsLogo);
        } catch (MalformedURLException e) {
            // do something
        }



        Glide.with(context)
                .load(saveModel.get(position).getImageUrl())
                .error(Glide.with(context).load(finalUrl))
                .into(holder.urlImage);

        holder.headline.setText(saveModel.get(position).getHeadLine());

        holder.newsRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveItemDelete(saveModel.get(holder.getAdapterPosition()).getNewsUrl());
                saveModel.remove(holder.getAdapterPosition());
                notifyDataSetChanged();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FullNews.class);
                intent.putExtra("link", saveModel.get(position).getNewsUrl());
                context.startActivity(intent);
            }
        });


    }


    @Override
    public int getItemCount() {
        return saveModel.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView newsLogo,urlImage, newsRemove;
        TextView newsCount,author,headline;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            newsCount = itemView.findViewById(R.id.newsCount);
            newsLogo = itemView.findViewById(R.id.newsLogo);
            author = itemView.findViewById(R.id.author);
            urlImage = itemView.findViewById(R.id.urlImage);
            newsRemove = itemView.findViewById(R.id.newsRemove);
            headline = itemView.findViewById(R.id.headline);

        }
    }
}

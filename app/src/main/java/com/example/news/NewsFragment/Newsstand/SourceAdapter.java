package com.example.news.NewsFragment.Newsstand;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.news.Database.DatabaseSQLite;
import com.example.news.Database.SourceDatabase;
import com.example.news.R;
import com.example.news.Search.SearchModel;

import java.util.ArrayList;

public class SourceAdapter extends RecyclerView.Adapter<SourceAdapter.ViewHolder>{
    Activity activity;
    ArrayList<SourceModel> sourceModel;
    ArrayList<String> allData;
    //database
    SourceDatabase sourceDatabase;
    SQLiteDatabase database;
    public SourceAdapter(Activity activity, ArrayList<SourceModel> sourceModel) {
        this.activity = activity;
        this.sourceModel = sourceModel;
        sourceDatabase = new SourceDatabase(activity);
        readAll();

    }

    public void readAll(){
        database = sourceDatabase.getReadableDatabase();
        allData = new ArrayList<>();
        allData.clear();
        Cursor cursor = sourceDatabase.readData();
        while (cursor.moveToNext()){
            allData.add(cursor.getString(1));
        }


    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.source_simple,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String original = sourceModel.get(position).getSearchText();
        //daily news.com
        String baseUrl = sourceModel.get(position).getSearchText();
        String sourceTitle="";
        if(baseUrl.contains(".")){
            // new string "Daily News"
            sourceTitle = baseUrl.substring(0,baseUrl.indexOf("."));

            //first latter upper case
            sourceTitle =sourceTitle.substring(0, 1).toUpperCase() + sourceTitle.substring(1);
        }



        holder.sourceCount.setText(String.valueOf(position+1));
        holder.sourceTextView.setText(sourceTitle);
        String finalUrl = "https://logo.clearbit.com/" +baseUrl.replaceAll(" ","") + "?size=" + 300 + "&format=png";
        Glide.with(activity).load(finalUrl)
                .error(R.drawable.search_icon)
                .into(holder.sourceLogo);

        String finalSourceTitle = sourceTitle;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity,SourceActivity.class);
                intent.putExtra("sourceTitle", finalSourceTitle);
                intent.putExtra("sourceULR", baseUrl);
                activity.startActivity(intent);
            }
        });

        //database
        if(allData.contains(original)){
            holder.sourceStar.setImageResource(R.drawable.active_star);
        }else {
            holder.sourceStar.setImageResource(R.drawable.unactive_star);
        }

        holder.sourceStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveItemDelete(original);
                readAll();
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return sourceModel.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView sourceLogo,sourceStar;
        TextView sourceCount,sourceTextView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            sourceCount = itemView.findViewById(R.id.sourceCount);
            sourceLogo = itemView.findViewById(R.id.sourceLogo);
            sourceTextView = itemView.findViewById(R.id.sourceTextView);
            sourceStar = itemView.findViewById(R.id.sourceStar);

        }
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
        } else {
            sourceDatabase.insertData(search);
        }
    }
}

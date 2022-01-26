package com.example.news.Model;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.news.Database.DatabaseSQLite;
import com.example.news.FullNews;
import com.example.news.NewsFragment.Newsstand.SourceActivity;
import com.example.news.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
    SQLiteDatabase sqLiteDatabase;
    DatabaseSQLite database;
    Activity context;
    ArrayList<NewsModel> newsModel;
    int viewPoint;

    public NewsAdapter(Activity context, ArrayList<NewsModel> newsModel, int viewPoint) {
        this.context = context;
        this.newsModel = newsModel;
        this.viewPoint = viewPoint;
        database = new DatabaseSQLite(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        if (viewPoint == 1) {
            view = LayoutInflater.from(context).inflate(R.layout.news_simple, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.news_headlines_simple, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        //String headline = newsModel.get(position).getHeadLine().substring(0,newsModel.get(position).getHeadLine().lastIndexOf("-"));
        holder.headline.setText(newsModel.get(position).getHeadLine());
        holder.author.setText(newsModel.get(position).getSource().getName());
        holder.published.setText(newsModel.get(position).getPublished());

        String finalUrl = "";
        try {
            URL url = new URL(newsModel.get(position).getNewsUrl());
            String baseUrl = url.getProtocol() + "://" + url.getHost();
            finalUrl = "https://logo.clearbit.com/" + baseUrl + "?size=" + 500 + "&format=png";
            Glide.with(context).load(finalUrl).into(holder.newsLogo);
        } catch (MalformedURLException e) {
            // do something
        }

        Glide.with(context)
                .load(newsModel.get(position).getImageUrl())
                .error(Glide.with(context).load(finalUrl))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.urlImage);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FullNews.class);
                intent.putExtra("link", newsModel.get(position).getNewsUrl());
                context.startActivity(intent);
            }
        });
        holder.newsMoreIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newsMore(position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return newsModel.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView headline, author, published;
        ImageView newsLogo, urlImage, newsMoreIcon;
        CardView root;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            headline = itemView.findViewById(R.id.headline);
            author = itemView.findViewById(R.id.author);
            published = itemView.findViewById(R.id.published);
            urlImage = itemView.findViewById(R.id.urlImage);
            newsLogo = itemView.findViewById(R.id.newsLogo);
            newsMoreIcon = itemView.findViewById(R.id.newsMoreIcon);
            root = itemView.findViewById(R.id.root);
        }
    }


    public void saveItemDelete(String search, int position) {
        sqLiteDatabase = database.getWritableDatabase();
        ArrayList<String> searchArray = new ArrayList<>();
        Cursor searchCursor = database.searchData(search);
        if (searchCursor.getCount() != 0) {
            while (searchCursor.moveToNext()) {
                searchArray.add(searchCursor.getString(0));
            }
            sqLiteDatabase.delete(DatabaseSQLite.SAVED_TABLE, "_id=?", new String[]{searchArray.get(0)});
            Toast.makeText(context, "News Removed", Toast.LENGTH_SHORT).show();
        } else {
            database.insertDataIntoBookmark(
                    newsModel.get(position).getSource().getName(),
                    newsModel.get(position).getHeadLine(),
                    newsModel.get(position).getNewsUrl(),
                    newsModel.get(position).getImageUrl(),
                    newsModel.get(position).getPublished()
            );
            Toast.makeText(context, "News Saved", Toast.LENGTH_SHORT).show();
        }
    }

    private void newsMore(int position) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(R.layout.more_new_layout);
        bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        bottomSheetDialog.show();

        LinearLayout savedNews, shareNews, newsWebsite;
        TextView savedNewsText, newsWebsiteName;
        ImageView savedNewsImage;
        savedNews = bottomSheetDialog.findViewById(R.id.savedNews);
        shareNews = bottomSheetDialog.findViewById(R.id.shareNews);
        newsWebsite = bottomSheetDialog.findViewById(R.id.newsWebsite);
        savedNewsImage = bottomSheetDialog.findViewById(R.id.savedNewsImage);
        savedNewsText = bottomSheetDialog.findViewById(R.id.savedNewsText);
        newsWebsiteName = bottomSheetDialog.findViewById(R.id.newsWebsiteName);
        newsWebsiteName.setText("Go to " + newsModel.get(position).getSource().getName());

        sqLiteDatabase = database.getReadableDatabase();
        Cursor searchCursor = database.searchData(newsModel.get(position).getNewsUrl());
        if (searchCursor.getCount() != 0) {
            savedNewsText.setText("Remove for later");
            savedNewsImage.setColorFilter(context.getResources().getColor(R.color.icon_color_blue));
        } else {
            savedNewsText.setText("Save for later");
            savedNewsImage.setColorFilter(context.getResources().getColor(R.color.icon_color));
        }


        savedNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveItemDelete(newsModel.get(position).getNewsUrl(), position);
                bottomSheetDialog.dismiss();

            }
        });

        shareNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    bottomSheetDialog.dismiss();
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, newsModel.get(position).getNewsUrl());
                    context.startActivity(Intent.createChooser(intent, "Share News"));
                } catch (Exception ignored) {
                }
            }
        });

        newsWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(context, SourceActivity.class);
                    intent.putExtra("sourceTitle",newsModel.get(position).getSource().getName());
                    intent.putExtra("sourceULR",getDomainName(newsModel.get(position).getNewsUrl()));
                    context.startActivity(intent);
                    bottomSheetDialog.dismiss();
                } catch (Exception ignored) {

                }

            }
        });
    }
    public static String getDomainName(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }
}

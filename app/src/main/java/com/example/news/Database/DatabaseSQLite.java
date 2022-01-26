package com.example.news.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseSQLite extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "mydata.db";
    public static final int VERSION = 1;
    public static String TABLE_NAME = "tbl";
    public static String SAVED_TABLE = "saved";

    public static final String SOURCE = "source";
    public static final String TITLE = "title";
    public static final String URL = "url";
    public static final String IMAGE = "image";
    public static final String PUBLISH = "publish";
    String[] table = {"main", "latest", "country", "entertainment", "business", "health", "science", "sports", "technology","saved"};


    public DatabaseSQLite(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    //create database table
    public static String CREATE_TABLE(String tableName) {
        String create = "CREATE TABLE IF NOT EXISTS " + tableName +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SOURCE + " TEXT ,"
                + TITLE + " TEXT ,"
                + URL + " TEXT ,"
                + IMAGE + " TEXT ,"
                + PUBLISH + " TEXT);";
        return create;
    }
    //delete database table
    public static String DELETE_TABLE(String tableName) {
        String delete = "DROP TABLE IF EXISTS " + tableName;
        return delete;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (int i = 0; i < table.length; i++) {
            db.execSQL(CREATE_TABLE(table[i]));
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int i = 0; i < table.length; i++) {
            db.execSQL(DELETE_TABLE(table[i]));
        }
        onCreate(db);
    }

    public void insertData(String source, String title, String url, String image, String publish) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues(5);
        values.put(SOURCE, source);
        values.put(TITLE, title);
        values.put(URL, url);
        values.put(IMAGE, image);
        values.put(PUBLISH, publish);
        db.insert(TABLE_NAME, null, values);
    }
    public void insertDataIntoBookmark(String source, String title, String url, String image, String publish) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues(5);
        values.put(SOURCE, source);
        values.put(TITLE, title);
        values.put(URL, url);
        values.put(IMAGE, image);
        values.put(PUBLISH, publish);
        db.insert(SAVED_TABLE, null, values);
    }

    public Cursor readData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY _id DESC";
        return db.rawQuery(query, null);
    }

    public Cursor searchData(String result) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = new String[]{result};
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + SAVED_TABLE +
                        " WHERE " + URL + " = ? ", selectionArgs
        );
        return cursor;
    }


}

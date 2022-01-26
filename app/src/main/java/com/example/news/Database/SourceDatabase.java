package com.example.news.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SourceDatabase extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "source.db";
    public static final int VERSION = 1;
    public static String TABLE_NAME = "source";
    public static String SOURCE_NAME ="source_name";

    public static String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    public static String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
            "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SOURCE_NAME + " TEXT);";




    public SourceDatabase(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DELETE_TABLE);
        onCreate(db);
    }
    public void insertData(String sourceName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues(2);
        values.put(SOURCE_NAME, sourceName);
        db.insert(TABLE_NAME, null, values);
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
                "SELECT * FROM " + TABLE_NAME +
                        " WHERE " + SOURCE_NAME + " = ? ", selectionArgs
        );
        return cursor;
    }




}

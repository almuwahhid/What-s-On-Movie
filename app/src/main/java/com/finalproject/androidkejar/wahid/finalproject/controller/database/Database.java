package com.finalproject.androidkejar.wahid.finalproject.controller.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by gueone on 5/10/2016.
 */
public class Database extends SQLiteOpenHelper {
    final static String DB_NAME = "db_favourite";

    public Database(Context context) {
        super(context, DB_NAME, null, 1);
        // TODO Auto-generated constructor stub
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS favorit(_id INTEGER PRIMARY KEY AUTOINCREMENT, id_Movie TEXT, title TEXT, pathPoster TEXT, bgPath TEXT, rate TEXT, rilis TEXT, sinopsis TEXT, trailer TEXT)";
        db.execSQL(sql);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS favorit");
        onCreate(db);
    }
}

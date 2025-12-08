package com.example.workio.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SessionDatabase extends SQLiteOpenHelper {

    private static final String DB_NAME = "workio_session.db";
    private static final int DB_VERSION = 1;

    public static final String TABLE_SESSION = "user_session";
    public static final String COL_ID = "id";
    public static final String COL_USERNAME = "username";
    public static final String COL_TOKEN = "access_token";
    public static final String COL_REMEMBER = "remember_me";

    public SessionDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_SESSION + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT, " +
                COL_TOKEN + " TEXT, " +
                COL_REMEMBER + " INTEGER)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSION);
        onCreate(db);
    }
}

package com.example.workio.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SessionDAO {

    private SessionDatabase dbHelper;

    public SessionDAO(Context context) {
        dbHelper = new SessionDatabase(context);
    }

    // Lưu session (xoá cũ rồi ghi mới)
    public void saveSession(String username, String token, boolean rememberMe) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(SessionDatabase.TABLE_SESSION, null, null);

        ContentValues values = new ContentValues();
        values.put(SessionDatabase.COL_USERNAME, username);
        values.put(SessionDatabase.COL_TOKEN, token);
        values.put(SessionDatabase.COL_REMEMBER, rememberMe ? 1 : 0);

        db.insert(SessionDatabase.TABLE_SESSION, null, values);
        db.close();
    }

    // Lấy token
    public String getToken() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(SessionDatabase.TABLE_SESSION,
                null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            String token = cursor.getString(
                    cursor.getColumnIndexOrThrow(SessionDatabase.COL_TOKEN)
            );
            cursor.close();
            return token;
        }

        cursor.close();
        return null;
    }

    // Kiểm tra có remember không
    public boolean isRemembered() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(SessionDatabase.TABLE_SESSION,
                null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int rememberValue = cursor.getInt(
                    cursor.getColumnIndexOrThrow(SessionDatabase.COL_REMEMBER)
            );
            cursor.close();
            return rememberValue == 1;
        }

        cursor.close();
        return false;
    }

    // Xoá session (logout)
    public void clearSession() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(SessionDatabase.TABLE_SESSION, null, null);
        db.close();
    }
}

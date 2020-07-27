package com.insomniac.pdfviewer.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.insomniac.pdfviewer.fragments.models.Recent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RecentDatabaseHeper extends SQLiteOpenHelper {

    public static final String DATBASE_NAME = "name";
    public static final int DATABASE_VERSION = 1;
    private static final String RECENT_TABLE = "recent";
    private static final String RECENT_ID = "id";
    private static final String RECENT_PATH = "path";
    private static final String RECENT_NAME = "name";
    private static final String RECENT_TIME = "time";


    public RecentDatabaseHeper(@Nullable Context context) {
        super(context, DATBASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        crateTableRecent(sqLiteDatabase);
    }

    private void crateTableRecent(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + RECENT_TABLE + "(" +
                RECENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                RECENT_PATH + " TEXT," +
                RECENT_NAME + " TEXT," +
                RECENT_TIME + " INTEGER" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void insertIntoResent(Recent recent) {
        int recntId = existsInRecent(recent.getPath());
        if (recntId != -1) {
            updateTime(recntId);
        } else {
            insert(recent);
        }
    }

    public void insertIntoResent(File recent) {
        int recntId = existsInRecent(recent.getAbsolutePath());
        if (recntId != -1) {
            updateTime(recntId);
        } else {
            insert(recent);
        }
    }

    private void updateTime(int recntId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(RECENT_TIME, System.currentTimeMillis());

        getWritableDatabase().update(RECENT_TABLE, contentValues, RECENT_ID + " = ?", new String[]{String.valueOf(recntId)});
    }

    private void insert(Recent recent) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(RECENT_PATH, recent.getPath());
        contentValues.put(RECENT_NAME, recent.getName());
        contentValues.put(RECENT_TIME, recent.getTimeStamp());
        getWritableDatabase().insert(RECENT_TABLE, null, contentValues);
    }

    private void insert(File recent) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(RECENT_PATH, recent.getAbsolutePath());
        contentValues.put(RECENT_NAME, recent.getName());
        contentValues.put(RECENT_TIME, System.currentTimeMillis());
        getWritableDatabase().insert(RECENT_TABLE, null, contentValues);
    }

    public int existsInRecent(String filePath) {
        try (Cursor cursor = getReadableDatabase().query(RECENT_TABLE, null, RECENT_PATH + " = ?", new String[]{filePath}, null, null, null)) {
            if (cursor.moveToFirst()) {
                return cursor.getInt(cursor.getColumnIndex(RECENT_ID));
            } else {
                return -1;
            }
        }
    }

    public List<Recent> getAllRecent() {
        List<Recent> recents = new ArrayList<>();
        Cursor cursor = getReadableDatabase().query(RECENT_TABLE, null, null, null, null, null, RECENT_TIME + " DESC");
        if (cursor.moveToFirst()) {
            do {
                Recent recent = new Recent();
                recent.setId(cursor.getInt(cursor.getColumnIndex(RECENT_ID)));
                recent.setName(cursor.getString(cursor.getColumnIndex(RECENT_NAME)));
                recent.setPath(cursor.getString(cursor.getColumnIndex(RECENT_PATH)));
                recent.setTimeStamp(cursor.getLong(cursor.getColumnIndex(RECENT_TIME)));
                recents.add(recent);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return recents;
    }

    public List<Recent> getAllRecent(int limit) {
        List<Recent> recents = new ArrayList<>();
        Cursor cursor = getReadableDatabase().query(RECENT_TABLE, null, null, null, null, null, RECENT_TIME + " DESC", String.valueOf(limit));
        if (cursor.moveToFirst()) {
            do {
                Recent recent = new Recent();
                recent.setId(cursor.getInt(cursor.getColumnIndex(RECENT_ID)));
                recent.setName(cursor.getString(cursor.getColumnIndex(RECENT_NAME)));
                recent.setPath(cursor.getString(cursor.getColumnIndex(RECENT_PATH)));
                recent.setTimeStamp(cursor.getLong(cursor.getColumnIndex(RECENT_TIME)));
                recents.add(recent);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return recents;
    }

    public void purgeData() {
        List<Recent> allRecents = getAllRecent();
        List<Recent> limited = getAllRecent(20);

        if (allRecents.size() > 20) {
            allRecents.removeAll(limited);
            for (Recent recent : allRecents) {
                deleteRecent(recent);
            }

        }
    }

    private void deleteRecent(Recent recent) {
        getWritableDatabase().delete(RECENT_TABLE, RECENT_ID + "=?", new String[]{String.valueOf(recent.getId())});
    }
}
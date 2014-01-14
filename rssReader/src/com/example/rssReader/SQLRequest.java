package com.example.rssReader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created with IntelliJ IDEA.
 * User: Ruslan
 * Date: 13.01.14
 * Time: 23:00
 */
public class SQLRequest {

    private SQLiteDatabase database;
    private Context context;
    private boolean opened;
    private DBHelper helper;

    public static final String DBName = "RSSDATABASE";
    public static final int DBVersion = 1;
    public static final String linksTable = "LinksTable";
    public static final String feedsTable = "FeedsTable";
    public static final String keyId = "_id";
    public static final String keyUrl = "keyUrl";
    public static final String keyDescription = "keyDescription";
    public static final String keyDate = "keyDate";
    public static final String keyTitle = "keyTitle";
    public static final String keyLink = "keyLink";
    public static final String linksTableCreator = "CREATE TABLE " + linksTable + " (" + keyId + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            keyUrl + " TEXT NOT NULL);";
    public static final String feedsTableCreator = "CREATE TABLE " + feedsTable + " (" + keyId + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            keyUrl + " TEXT NOT NULL, " + keyTitle + " TEXT NOT NULL, " + keyDescription + " TEXT NOT NULL, " +
            keyLink + " TEXT NOT NULL, " + keyDate + "TEXT NOT NULL);";


    private static class DBHelper extends SQLiteOpenHelper {

        DBHelper(Context context) {
            super(context, DBName, null, DBVersion);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(linksTableCreator);
            db.execSQL(feedsTableCreator);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
            db.execSQL("DROP TABLE IF EXISTS " + linksTable);
            db.execSQL("DROP TABLE IF EXISTS " + feedsTable);
        }

    }

    public SQLRequest(Context context) {
        this.context = context;
        opened = false;
    }

    public void openDB() {
        opened = true;
        helper = new DBHelper(context);
        database = helper.getWritableDatabase();
    }

    public void closeDB() {
        opened = false;
        helper.close();
    }

    public Cursor getAllLinks() {
        Cursor cursor = null;
        if (opened) {
            cursor = database.query(linksTable, null, null, null, null, null, keyId + " asc", null);
        }
        return cursor;
    }

    public Cursor getAllFeeds(String url) {
        Cursor cursor = null;
        if (opened){
            cursor = database.query(feedsTable, null, keyUrl + "=\"" + url + "\"", null, null, null, null);
        }
        return cursor;
    }

    public void deleteAllFeeds(String url) {
        if (opened) {
            database.delete(feedsTable, keyUrl + "=?", new String[]{"\"" + url + "\""});
        }
    }

    public void delete(String url) {
        if (opened) {
            database.delete(linksTable, keyUrl + "=?", new String[]{"\"" + url + "\""});
            database.delete(feedsTable, keyUrl + "=?", new String[]{"\"" + url + "\""});
        }
    }

    public void addFeed(String url, String title, String description, String date, String link) {
        if (opened) {
            ContentValues values = new ContentValues();
            values.put(keyUrl, "\"" + url + "\"");
            values.put(keyDate, date);
            values.put(keyTitle, title);
            values.put(keyDescription, description);
            values.put(keyLink, link);
            database.insert(feedsTable, null, values);
        }
    }

    public void addLink(String url){
        if(opened){
            ContentValues values = new ContentValues();
            values.put(keyUrl, "\"" + url + "\"");
            database.insert(linksTable, null, values);
        }
    }
}

package com.example.sungwon.vicenews;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Owen on 9/26/2016.
 */

public class ViceDBHelper extends SQLiteOpenHelper{
    //instantiating database
    private static final int DATABASE_VERSION = 6;
    private static final String DATABASE_NAME = "ViceNews.db";
    //instantiating columns in database
    public static final String DATABASE_TABLE_NAME_LATEST = "getlatest";
    public static final String DATABASE_TABLE_NAME_POPULAR = "getmostpopular";
    public static final String DATABASE_TABLE_NAME_CATEGORY = "category";
    public static final String VICENEWS_COLUMN_ID = "_id";
    public static final String VICENEWS_TITLE = "title";
    public static final String VICENEWS_AUTHOR = "arturl";
    public static final String VICENEWS_PREVIEW = "largeimgurl";
    public static final String VICENEWS_THUMBNAIL = "smallimgurl";
    public static final String [] VICENEWS_COLUMNS = {VICENEWS_COLUMN_ID, VICENEWS_TITLE, VICENEWS_AUTHOR, VICENEWS_PREVIEW, VICENEWS_THUMBNAIL};

    public static final String SQL_CREATE_VICENEWS_TABLE_LATEST =
            "CREATE TABLE " + DATABASE_TABLE_NAME_LATEST +
                    "( " +
                    VICENEWS_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    VICENEWS_TITLE + " TEXT," +
                    VICENEWS_AUTHOR + " TEXT," +
                    VICENEWS_PREVIEW + " TEXT," +
                    VICENEWS_THUMBNAIL + " TEXT )";
    public static final String SQL_DROP_VICENEWS_TABLE_LATEST = "DROP TABLE IF EXISTS "+ DATABASE_TABLE_NAME_LATEST;

    public static final String SQL_CREATE_VICENEWS_TABLE_POPULAR =
            "CREATE TABLE " + DATABASE_TABLE_NAME_POPULAR +
                    "( " +
                    VICENEWS_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    VICENEWS_TITLE + " TEXT," +
                    VICENEWS_AUTHOR + " TEXT," +
                    VICENEWS_PREVIEW + " TEXT," +
                    VICENEWS_THUMBNAIL + " TEXT )";
    public static final String SQL_DROP_VICENEWS_TABLE_POPULAR = "DROP TABLE IF EXISTS "+ DATABASE_TABLE_NAME_POPULAR;

    public static final String SQL_CREATE_VICENEWS_TABLE_CATEGORY =
            "CREATE TABLE " + DATABASE_TABLE_NAME_CATEGORY +
                    "( " +
                    VICENEWS_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    VICENEWS_TITLE + " TEXT," +
                    VICENEWS_AUTHOR + " TEXT," +
                    VICENEWS_PREVIEW + " TEXT," +
                    VICENEWS_THUMBNAIL + " TEXT )";
    public static final String SQL_DROP_VICENEWS_TABLE_CATEGORY = "DROP TABLE IF EXISTS "+ DATABASE_TABLE_NAME_CATEGORY;

    //creating as Singleton class
    private static ViceDBHelper instance;

    public static ViceDBHelper getInstance(Context context){
        if(instance == null){
            instance =  new ViceDBHelper(context);
        }
        return instance;
    }

    public ViceDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //creating table
        db.execSQL(SQL_CREATE_VICENEWS_TABLE_LATEST);
        db.execSQL(SQL_CREATE_VICENEWS_TABLE_POPULAR);
        db.execSQL(SQL_CREATE_VICENEWS_TABLE_CATEGORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_VICENEWS_TABLE_LATEST);
        db.execSQL(SQL_DROP_VICENEWS_TABLE_POPULAR);
        db.execSQL(SQL_DROP_VICENEWS_TABLE_CATEGORY);
        onCreate(db);
    }

    public long addArticleLatest(ContentValues values) {
        SQLiteDatabase db = getWritableDatabase();
        long insertedRowLatest = db.insert(DATABASE_TABLE_NAME_LATEST, null, values);
        return insertedRowLatest;

    }
    public long addArticlePopular(ContentValues values) {
        SQLiteDatabase db = getWritableDatabase();
        long insertedRowPopular = db.insert(DATABASE_TABLE_NAME_POPULAR, null, values);
        return insertedRowPopular;
    }
    public long addArticleCategory(ContentValues values) {
        SQLiteDatabase db = getWritableDatabase();
        long insertedRowCategory = db.insert(DATABASE_TABLE_NAME_CATEGORY, null, values);
        return insertedRowCategory;
    }

    public int deleteAllArticlesLatest() {
        SQLiteDatabase db = getWritableDatabase();
        int rowsDeleted = db.delete(DATABASE_TABLE_NAME_LATEST,null,null);
        return rowsDeleted;
    }
    public int deleteAllArticlesPopular() {
        SQLiteDatabase db = getWritableDatabase();
        int rowsDeleted = db.delete(DATABASE_TABLE_NAME_POPULAR,null,null);
        return rowsDeleted;
    }
    public int deleteAllArticlesCategory() {
        SQLiteDatabase db = getWritableDatabase();
        int rowsDeleted = db.delete(DATABASE_TABLE_NAME_CATEGORY, null, null);
        return rowsDeleted;
    }

    public Cursor getRecentArticles(String selection, String[] selectionArgs) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(DATABASE_TABLE_NAME_LATEST,VICENEWS_COLUMNS,selection,selectionArgs,null,null,null);
        return cursor;
    }
    public Cursor getPopularArticles(String selection, String[] selectionArgs) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(DATABASE_TABLE_NAME_POPULAR,VICENEWS_COLUMNS,selection,selectionArgs,null,null,null);
        return cursor;
    }
    public Cursor getCategoryArticles(String selection, String[] selectionArgs) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(DATABASE_TABLE_NAME_CATEGORY,VICENEWS_COLUMNS,selection,selectionArgs,null,null,null);
        return cursor;
    }

    public Cursor getArticlesByIdLatest(String id){
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(DATABASE_TABLE_NAME_LATEST,VICENEWS_COLUMNS,VICENEWS_COLUMN_ID +" = ?", new String[]{id}, null, null, null);

        return cursor;
    }
    public Cursor getArticlesByIdPopular(String id){
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(DATABASE_TABLE_NAME_POPULAR,VICENEWS_COLUMNS,VICENEWS_COLUMN_ID +" = ?", new String[]{id}, null, null, null);

        return cursor;
    }
    public Cursor getArticlesByIdCategory(String id){
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(DATABASE_TABLE_NAME_CATEGORY,VICENEWS_COLUMNS,VICENEWS_COLUMN_ID +" = ?", new String[]{id}, null, null, null);

        return cursor;
    }

    //cursor for searching News Article
//    public Cursor searchArticles(String query, String table) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor scursor = null;
//        switch (table) {
//            case "latest":
//
//                scursor = db.query(DATABASE_TABLE_NAME_LATEST,
//                        VICENEWS_COLUMNS,
//                        VICENEWS_TITLE + " LIKE ? OR " + VICENEWS_AUTHOR + " LIKE ? OR " + VICENEWS_CATEGORY + " LIKE ? ",
//                        new String[]{"%" + query + "%", "%" + query + "%", "%" + query + "%"},
//                        null,
//                        null,
//                        null);
//
//                break;
//            case "popular":
//
//                scursor = db.query(DATABASE_TABLE_NAME_POPULAR,
//                        VICENEWS_COLUMNS,
//                        VICENEWS_TITLE + " LIKE ? OR " + VICENEWS_AUTHOR + " LIKE ? OR " + VICENEWS_CATEGORY + " LIKE ? ",
//                        new String[]{"%" + query + "%", "%" + query + "%", "%" + query + "%"},
//                        null,
//                        null,
//                        null);
//                break;
//            case "category":
//
//                scursor = db.query(DATABASE_TABLE_NAME_CATEGORY,
//                        VICENEWS_COLUMNS,
//                        VICENEWS_TITLE + " LIKE ? OR " + VICENEWS_AUTHOR + " LIKE ? OR " + VICENEWS_CATEGORY + " LIKE ? ",
//                        new String[]{"%" + query + "%", "%" + query + "%", "%" + query + "%"},
//                        null,
//                        null,
//                        null);
//                break;
//        }
//            return scursor;
//
//    }
    //cursor for showing News Article details
    public Cursor detailsArticlesLatest(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor dcursor = db.query(DATABASE_TABLE_NAME_LATEST,
                VICENEWS_COLUMNS,
                VICENEWS_COLUMN_ID+ "= ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null);
        return dcursor;
    }
    public Cursor detailsArticlesPopular(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor dcursor = db.query(DATABASE_TABLE_NAME_POPULAR,
                VICENEWS_COLUMNS,
                VICENEWS_COLUMN_ID+ "= ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null);
        return dcursor;
    }
    public Cursor detailsArticlesCategory(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor dcursor = db.query(DATABASE_TABLE_NAME_CATEGORY,
                VICENEWS_COLUMNS,
                VICENEWS_COLUMN_ID + "= ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null);
        return dcursor;
    }
}


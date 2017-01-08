package com.example.sungwon.vicenews;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import cz.msebera.android.httpclient.Header;

/**
 * Created by SungWon on 9/26/2016.
 */

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private static String TAG = SyncAdapter.class.getCanonicalName();

    ContentResolver mContentResolver;

    private static final String AUTHORITY = "com.example.sungwon.vicenews.NewsContentProvider";
    private static final String STOCKS_TABLE = ViceDBHelper.DATABASE_TABLE_NAME_LATEST;


    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs, ContentResolver mContentResolver) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
    }

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

        mContentResolver = context.getContentResolver();


    }


    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        Log.d(SyncAdapter.class.getName(), "Starting Sync");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        String page = sp.getString("select_categories", "news");
        mContentResolver.delete(NewsContentProvider.CONTENT_POPULAR_URI_FULL, null, null);
        mContentResolver.delete(NewsContentProvider.CONTENT_RECENT_URI_FULL, null, null);
        mContentResolver.delete(NewsContentProvider.CONTENT_CATEGORY_URI_FULL, null, null);
        getPopularArticles();
        getRecentArticles();
        getCategoryArticles(page);
    }

    private void getRecentArticles() {
        final Gson gson = new Gson();
        String data = null;

        SyncHttpClient client = new SyncHttpClient();

        client.get("http://vice.com/api/getlatest/0", null,
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.d(TAG, "TextHttpResponseHandler failed");
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String data) {
                        SearchResult result = gson.fromJson(data, SearchResult.class);
                        NewsItem newsItemArray = result.data;
                        ContentValues values = new ContentValues();
//            values.put()
//            TODOne: do value put based on db
                        for (int i = 0; i < newsItemArray.getResults().length; i++) {
                            NewsDetail details = newsItemArray.getResults()[i];
                            values.put("title",details.getTitle());
                            values.put("author",details.getAuthor());
                            values.put("body", details.getBody());
                            values.put("preview", details.getPreview());
                            values.put("category", details.getCategory());
                            values.put("thumbnail", details.getThumb());
                            mContentResolver.insert(NewsContentProvider.CONTENT_RECENT_URI_FULL, values);
                            if (i>19){Log.d(TAG, "Story Added: "+details.getTitle());}
                        }
                    }
                });
    }

    private void getPopularArticles() {
        final Gson gson = new Gson();
        String data = null;

        SyncHttpClient client = new SyncHttpClient();

        client.get("http://api.nytimes.com/svc/mostpopular/v2/mostemailed/all-sections/1.json?&api-key=9a31996473054cef8734182d5811e043", null,
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.d(TAG, "TextHttpResponseHandler failed");
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String data) {
                        SearchResult result = gson.fromJson(data, SearchResult.class);
                        NewsItem newsItemArray = result.data;
                        ContentValues values = new ContentValues();
//            values.put()
//            TODOne: do value put based on db
                        for (int i = 0; i < 30 || i < newsItemArray.getResults().length; i++) {
                            NewsDetail details = newsItemArray.getResults()[i];
                            values.put("title",details.getTitle());
                            values.put("author",details.getAuthor());
                            values.put("body", details.getBody());
                            values.put("preview", details.getPreview());
                            values.put("category", details.getCategory());
                            values.put("thumbnail", details.getThumb());
                            mContentResolver.insert(NewsContentProvider.CONTENT_POPULAR_URI_FULL, values);
                            if (i>19){Log.d(TAG, "Story Added: "+details.getTitle());}
                        }
                    }
                });
    }
    private void getCategoryArticles(String cat) {
//        categoryPreferences();
        final Gson gson = new Gson();
        String data = null;

        SyncHttpClient client = new SyncHttpClient();

        client.get("http://vice.com/api/getlatest/category/" + cat, null,
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.d(TAG, "TextHttpResponseHandler failed");
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String data) {
                        SearchResult result = gson.fromJson(data, SearchResult.class);
                        NewsItem newsItemArray = result.data;
                        ContentValues values = new ContentValues();
//            values.put()
//            TODOne: do value put based on db
                        for (int i = 0; i < newsItemArray.getResults().length; i++) {
                            NewsDetail details = newsItemArray.getResults()[i];
                            values.put("title", details.getTitle());
                            values.put("author", details.getAuthor());
                            values.put("body", details.getBody());
                            values.put("preview", details.getPreview());
                            values.put("category", details.getCategory());
                            values.put("thumbnail", details.getThumb());
                            mContentResolver.insert(NewsContentProvider.CONTENT_CATEGORY_URI_FULL, values);
                            if (i > 19) {
                                Log.d(TAG, "Story Added: " + details.getTitle());
                            }
                        }
                    }
                });
    }

    private String getInputData(InputStream inStream) throws IOException {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));

        String data = null;

        while ((data = reader.readLine()) != null){
            builder.append(data);
        }

        reader.close();

        return builder.toString();
    }
}

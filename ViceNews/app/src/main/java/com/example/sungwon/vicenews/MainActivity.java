package com.example.sungwon.vicenews;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeTransform;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.text.DateFormat;
import java.util.Date;

import static com.example.sungwon.vicenews.R.id.recyclerView;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = MainActivity.class.getName();
    public static final String AUTHORITY = "com.example.sungwon.vicenews.NewsContentProvider";
    public static final String ACCOUNT_TYPE = "com.example.sungwon.vicenews";
    public static final String ACCOUNT = "default_account";

    static Account mAccount;
    static ContentResolver mResolver;
    private final NewsContentObserver contentObserver = new NewsContentObserver(new Handler());
    static int spanCount = 1;
    static String catName = "latest";

    public static final int NOTIFICATION = 1;


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadPreferences();
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){

            TransitionSet transition = new TransitionSet();
            transition.addTransition(new ChangeTransform());
            getWindow().setSharedElementEnterTransition(transition);
            getWindow().setSharedElementReturnTransition(transition);
        }
        setContentView(R.layout.activity_main);

        /* Instantiating for SyncAdapter*/
        mAccount = createSyncAccount(this);

        getContentResolver().registerContentObserver(NewsContentProvider.CONTENT_RECENT_URI,true,contentObserver);
        Bundle bundle = new Bundle();
        bundle.putString("page", catName);
        ImageView background = (ImageView)findViewById(R.id.background);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
//        boolean backswitch = sp.getBoolean("background_switch", true);
//        if(backswitch){
//            Picasso.with(MainActivity.this).load(R.drawable.whitevice).fit().into(background);
////            View view = this.getWindow().getDecorView();
////            view.setBackgroundColor(0x000000);
//        }

        ContentResolver.requestSync(mAccount, AUTHORITY, bundle);
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        /*
         * Request the sync for the default account, authority, and
         * manual sync settings4
         */
        ContentResolver.requestSync(mAccount, AUTHORITY, settingsBundle);

        ContentResolver.setSyncAutomatically(mAccount,AUTHORITY,false);
        ContentResolver.addPeriodicSync(
                mAccount,
                AUTHORITY,
                Bundle.EMPTY,
                3000);//set the time         //TODO SYNC SETTING: value is in seconds, 3599 is max

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected void onDestroy() {
        getContentResolver().unregisterContentObserver(contentObserver);
        super.onDestroy();
    }

    /*Necessary dummy account method for syncadapter */
    private Account createSyncAccount(Context context) {
        Account newAccount = new Account(
                ACCOUNT, ACCOUNT_TYPE);
        AccountManager accountManager = (AccountManager) context.getSystemService(ACCOUNT_SERVICE);
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
/*
           * If you don't set android:syncable="true" in
           * in your <provider> element in the manifest,
           * then call context.setIsSyncable(account, AUTHORITY, 1)
           * here.
           */
        } else {
 /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
        }
        return newAccount;
    }

    public class NewsContentObserver extends ContentObserver {

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public NewsContentObserver(Handler handler) {
            super(handler);

        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            //do stuff on UI thread
            Log.d(MainActivity.class.getName(),"Changed observed at "+uri);

            PlaceholderFragment frag = (PlaceholderFragment)getSupportFragmentManager().findFragmentById(R.id.container);
            frag.fragChangeCursor();

            //sends setting values to resolver
            Bundle bundle = new Bundle();
            bundle.putString("page", catName);

            mResolver.requestSync(mAccount, AUTHORITY, bundle);

            notification();

            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
            Log.d(TAG, "Last updated: "+currentDateTimeString);
        }
    }
    //NOTIFICATION
    private void notification (){

        Intent intent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.fire);
        builder.setContentTitle("Vice News");
        builder.setContentText("Updated with new articles!");
        builder.setAutoCancel(true);
        builder.setContentIntent(pendingIntent);
        builder.setPriority(Notification.PRIORITY_MAX);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION, builder.build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds results to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //SEARCH FUNCTION
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        handleIntent(intent);
//    }
//
//    private void handleIntent(Intent intent) {
//        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
//            String query = intent.getStringExtra(SearchManager.QUERY);
//            Cursor cursor2 = ViceDBHelper.getInstance(this).searchArticles(query, "latest");
//            Cursor cursor3 = ViceDBHelper.getInstance(this).searchArticles(query, "popular");
//
//            madapter.changeCursor(cursor2);
//
//            Toast.makeText(MainActivity.this, "Searching for " + query, Toast.LENGTH_SHORT).show();
//
//
//        }
//    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, ViceSettings.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given mPage.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Top";
                case 1:
                    return "Recent";
                case 2:
                    return "Favorite";
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        /* allows frag to get mPage number*/
        public int mPage;


        private RecyclerView mRecyclerView;
        RecyclerViewAdapter mAdapter;
        private StaggeredGridLayoutManager mLayoutManager;
        private SwipeRefreshLayout mSwipeRefreshLayout;




        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */


        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mPage = getArguments().getInt(ARG_SECTION_NUMBER, 0);

        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            Cursor dummycursor = getActivity().getContentResolver().query(NewsContentProvider.CONTENT_POPULAR_URI_FULL,null,null,null,null);
            mRecyclerView = (RecyclerView) rootView.findViewById(recyclerView);
            mRecyclerView.setHasFixedSize(true);
            mLayoutManager = new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL); //TODOne make setting to change columns
            mRecyclerView.setLayoutManager(mLayoutManager);
            final Bundle settingsBundle = new Bundle();
            settingsBundle.putBoolean(
                    ContentResolver.SYNC_EXTRAS_MANUAL, true);


                mAdapter = new RecyclerViewAdapter(getContext(), dummycursor);

            mRecyclerView.setAdapter(mAdapter);

            switch (mPage){
                case(1)://top
                    String top = "getmostpopular/";
//                    textView.setText(top);
//                    also return cursor and swapadapter?
                    fragChangeCursor();
                    break;
                case(2)://latest
                    String latest = "getlatest/";
//                    textView.setText(latest);
                    fragChangeCursor();
                    break;
                case(3):/* mostly for shared pref and settings*/
                    String category = "getlatest/category/";
//                    textView.setText("custom");
                    fragChangeCursor();
                    break;
            }

            return rootView;
        }

        public void fragChangeCursor(){
            Cursor cursor = getActivity().getContentResolver().query(NewsContentProvider.CONTENT_POPULAR_URI_FULL,null,null,null,null);;
            switch (mPage){
                case(1)://top
                    String top = "getmostpopular/";
                    cursor = getActivity().getContentResolver().query(NewsContentProvider.CONTENT_POPULAR_URI_FULL,null,null,null,null);
                    mAdapter.changeCursor(cursor);
                    //TODOne?: get the right cursor to reinsert to RecyclerView
                    break;
                case(2)://latest
                    String latest = "getlatest/";
                    cursor = getActivity().getContentResolver().query(NewsContentProvider.CONTENT_RECENT_URI_FULL,null,null,null,null);
                    mAdapter.changeCursor(cursor);
                    break;
                case(3):/* mostly for shared pref*/
                    cursor = getActivity().getContentResolver().query(NewsContentProvider.CONTENT_CATEGORY_URI_FULL,null,null,null,null);
                    mAdapter.changeCursor(cursor);
                    break;
            }
        }

        public String getURLEndpoint(){
            String endpoint = "";
            switch (mPage){
                case(1):
                    endpoint = "getmostpopular/";
                    break;
                case(2):
                    endpoint = "getlatest/";
                    break;
                case(3):
                    endpoint = "getlatest/category/";
                    break;
            }
            return endpoint;
        }
    }
    private void loadPreferences(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        sharedPreferences.edit().putString("select_categories", "sports").apply();

//        boolean isBackgroundDark = sharedPreferences.getBoolean("background_switch", true);
//        if(isBackgroundDark){
//            CoordinatorLayout mainLayout = (CoordinatorLayout) findViewById(R.id.main_content);
//            mainLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
//        }
        boolean changeCards = sharedPreferences.getBoolean("view_switch", false);
        spanCount = (changeCards)? 1:2;


        //TODO Make Sync Settings Preference
    }
}

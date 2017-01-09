package com.example.sungwon.vicenews;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    TextView mTitleText;
    ImageView mImageView;
    private static final String TAG = "DetailActivity";
    private ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTitleText = (TextView)findViewById(R.id.titleText);
        mImageView = (ImageView)findViewById(R.id.imageView_Detail);


        //TODO TRANSISTION ANIMATION

//        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        Intent intent = getIntent();


        //WEBVIEW
        WebView webview;
        webview = (WebView)findViewById(R.id.webview);
        String title = intent.getStringExtra("title");
        String html = intent.getStringExtra("body");
        String url = intent.getStringExtra("image");
        webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webview.setScrollbarFadingEnabled(false);
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        Picasso.with(DetailActivity.this).load(url).fit().into(mImageView);
        mTitleText.setText(title);

        setFadeAnimation(webview);
        setScaleAnimation(webview);

        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        progressBar = ProgressDialog.show(DetailActivity.this, "Article Loading...", "Loading...");

        webview.setWebViewClient(new WebViewClient());
        webview.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if(newProgress<100){
                    Log.d(TAG, String.valueOf(newProgress));
                } else {
                    progressBar.dismiss();
                }
            }

        });
        webview.loadUrl(html);


//        FloatingActionButton fab_share = (FloatingActionButton) findViewById(R.id.fab_share);
//        fab_share.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//            }
//        });


    }
    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1000);
        view.startAnimation(anim);
    }
    private void setScaleAnimation(View view) {
        ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(800);
        view.startAnimation(anim);
    }
}

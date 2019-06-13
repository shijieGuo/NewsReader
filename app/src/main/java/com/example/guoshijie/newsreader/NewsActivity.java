package com.example.guoshijie.newsreader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class NewsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_detail);
        setTitle(R.string.app_name);

        // 接受intent传递过来的数据
        Intent intent = this.getIntent();
        final mytest.newsreader.bean.NewsBean news = (mytest.newsreader.bean.NewsBean) intent.getSerializableExtra("news");

        // 初始化组件
        TextView titleView = (TextView) findViewById(R.id.newsTitle);
        TextView pubDateView = (TextView) findViewById(R.id.newsPubDate);
        final WebView webview = (WebView) findViewById(R.id.newsDetail);

        // 新闻标题
        titleView.setText(news.title);

        // 新闻发布时间
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(
                    "EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date d = sdf.parse(news.pubDate);
            //
            SimpleDateFormat formatter = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss", Locale.US);
            String s = formatter.format(d);
            //
            pubDateView.setText("(发布日期：" + s + ")");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // webview组件参数设置
        WebSettings settings = webview.getSettings();
        settings.setSupportMultipleWindows(false);
        settings.setSupportZoom(false);
        // 加载新闻描述内容
        webview.loadDataWithBaseURL(news.guid, news.description, null, "utf-8", null);

        // 返回动作
        ImageView back = (ImageView) findViewById(R.id.imageViewBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 浏览详细页面
        ImageView browser = (ImageView) findViewById(R.id.imageViewBrowser);
        browser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建进度条并设置其显示样式     
                final ProgressDialog progressBar = new ProgressDialog(NewsActivity.this);
                progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressBar.setMessage("Loading...");//设置信息     
                progressBar.setCancelable(true);//设置是否响应back键取消进度条     
                progressBar.show();// 设置WebView组件加载网页内容的回调接口 
                webview.setWebChromeClient(new WebChromeClient() {
                public void onProgressChanged(WebView view, int progress){
                    progressBar.setProgress(progress);// Make the bar disappear after URL is loaded         
                    if(progress==100&&progressBar.isShowing()){
                        progressBar.dismiss();
                    }
                }
                });
                webview.loadUrl(news.guid);
            }
        });

    }

}


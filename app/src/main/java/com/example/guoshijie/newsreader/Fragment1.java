package com.example.guoshijie.newsreader;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class Fragment1 extends Fragment {
    private static final int NODE_CHANNEL = 0;
    private static final int NODE_ITEM = 1;
    private static final int MSG_NEWS_LOADED = 100;

    private View layoutView;
    private ProgressDialog pd; // 进度指示dialog

    private List<mytest.newsreader.bean.NewsBean> newsList = new ArrayList<mytest.newsreader.bean.NewsBean>();
    private ListView listView1;
    private NewsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(layoutView != null){
            return layoutView; }
        layoutView = inflater.inflate(R.layout.fragment1, null);
        listView1 = (ListView) layoutView.findViewById(R.id.listView1);

        // 显示一个进度条
        pd = ProgressDialog.show(getActivity(), "请稍后...", "正在加载数据", true, true);

        // 初始化ListView显示的数据源
        adapter = new NewsAdapter(newsList);
        listView1.setAdapter(adapter);

        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int location, long id) {
                Intent intent = new Intent(Fragment1.this.getActivity(), NewsActivity.class);
                mytest.newsreader.bean.NewsBean news = newsList.get(location);
                intent.putExtra("news", news);
                startActivity(intent);
            }
        });

        // 启动一个线程，以执行网络连接操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 通过HttpGet获取RSS数据
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new
                        HttpGet("http://news.163.com/special/00011K6L/rss_newstop.xml");
                try {
                    HttpResponse response = client.execute(get);
                    // 检查服务器返回的响应码，200表示成功
                    if (response.getStatusLine().getStatusCode() == 200) {
                        // 获取网络连接的输入流，然后解析收到的rss数据
                        InputStream is = response.getEntity().getContent();
                        List<Map<String, String>> items = getRssItems(is);

                        newsList.clear();

                        for (Map<String, String> item : items) {
                            mytest.newsreader.bean.NewsBean news = new mytest.newsreader.bean.NewsBean();
                            news.title = item.get("title");
                            news.description = item.get("description");
                            news.link = item.get("link");
                            news.pubDate = item.get("pubDate");
                            news.guid = item.get("guid");
                            newsList.add(news);
                        }

                        // 数据加载完毕，通知ListView显示
                        //adapter.notifyDataSetChanged();
                        Message msg = mUIHandler.obtainMessage(MSG_NEWS_LOADED);
                        //msg.obj = newsList;
                        msg.sendToTarget();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return layoutView;
    }

    private Handler mUIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_NEWS_LOADED:
                    // 更新ListView显示
                    adapter.notifyDataSetChanged();
                    // 销毁进度条
                    pd.dismiss();
                    break;
            }
        }
    };

    public List<Map<String, String>> getRssItems(InputStream xml) throws Exception {
        List<Map<String, String>> itemList = new ArrayList<Map<String, String>>();
        Map<String, String> item = new HashMap<String, String>();
        String name, value;
        int currNode = -1;

        // 准备XmlPullParser
        XmlPullParser pullParser = Xml.newPullParser();
        pullParser.setInput(xml, "UTF-8");
        int event = pullParser.getEventType();

        // 循环解析每个节点
        while (event != XmlPullParser.END_DOCUMENT) {
            switch (event) {
                case XmlPullParser.START_TAG:
                    name = pullParser.getName();
                    // 确定是channel还是item节点
                    if ("channel".equalsIgnoreCase(name)) {
                        currNode = NODE_CHANNEL;
                        break;
                    } else if ("item".equalsIgnoreCase(name)) {
                        currNode = NODE_ITEM;
                        break;
                    }
                    // 若是item节点，则提取其中的各子元素(title,link,description,pubDate,guid)
                    if (currNode == NODE_ITEM) {
                        value = pullParser.nextText();
                        item.put(name, value);
                    }
                    break;
                case XmlPullParser.END_TAG:
                    name = pullParser.getName();
                    if ("item".equals(name)) {
                        itemList.add(item);
                        item = new HashMap<String, String>();
                    }
                    break;
            } //of switch

            // 处理下一节点
            event = pullParser.next();
            // 继续下一次循环
        }

        return itemList;
    }

    class NewsAdapter extends BaseAdapter {
        private List<mytest.newsreader.bean.NewsBean> newsItems;

        public NewsAdapter(List<mytest.newsreader.bean.NewsBean> newsitems){
            this.newsItems = newsitems;
        }

        @Override
        public int getCount() {
            return newsItems.size();
        }
        @Override
        public Object getItem(int position) {
            return newsItems.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if(view == null){
                view = getActivity().getLayoutInflater().inflate(R.layout.news_item, null);
                // 查找View对象的各个子组件，将子组件引用保存到View的tag标签中                  
                 view.setTag(R.id.news_title, view.findViewById(R.id.news_title));
                 view.setTag(R.id.news_description, view.findViewById(R.id.news_description));
                 view.setTag(R.id.news_pubdate, view.findViewById(R.id.news_pubdate));
                 view.setTag(R.id.news_icon, view.findViewById(R.id.news_icon));
            }

             TextView newsTitle = (TextView) view.getTag(R.id.news_title);
             TextView newsDescr = (TextView)view.getTag(R.id.news_description);
             TextView newsPubdate = (TextView) view.getTag(R.id.news_pubdate);
             ImageView newsIcon = (ImageView) view.getTag(R.id.news_icon);

            mytest.newsreader.bean.NewsBean item = newsItems.get(position);

            newsTitle.setText(item.title);
            newsDescr.setText(item.description);
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(
                        "EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
                sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                Date d = sdf.parse(item.pubDate);
                //
                SimpleDateFormat formatter = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss", Locale.US);
                String s = formatter.format(d);
                //
                newsPubdate.setText(s);
            } catch (Exception e) {
                e.printStackTrace();
            }


            return view;
        }
    }


}


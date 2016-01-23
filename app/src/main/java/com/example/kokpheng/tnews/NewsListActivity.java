package com.example.kokpheng.tnews;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.amigold.fundapter.BindDictionary;
import com.amigold.fundapter.FunDapter;
import com.amigold.fundapter.extractors.StringExtractor;
import com.amigold.fundapter.interfaces.DynamicImageLoader;
import com.kosalgeek.android.json.JsonConverter;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.ArrayList;

public class NewsListActivity extends AppCompatActivity implements AsyncResponse {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        PostResponseAsyncTask task = new PostResponseAsyncTask(NewsListActivity.this,this);
        task.execute("http://10.0.2.2:8080/TNews/newsList.php");
    }

    @Override
    public void processFinish(String s) {
       ImageLoader.getInstance().init(UILConfig());

        ArrayList<News> newsList = new JsonConverter<News>().toArrayList(s, News.class);

        BindDictionary<News> dictionary = new BindDictionary<News>();
        dictionary.addStringField(R.id.tvTitle, new StringExtractor<News>() {
            @Override
            public String getStringValue(News news, int position) {
                return "" + news.title;
            }
        });

        dictionary.addStringField(R.id.tvPublishDate, new StringExtractor<News>() {
            @Override
            public String getStringValue(News news, int position) {
                return "" + news.publish_date;
            }
        });

        dictionary.addStringField(R.id.tvDescription, new StringExtractor<News>() {
            @Override
            public String getStringValue(News news, int position) {
                return "" + news.description;
            }
        });

        dictionary.addDynamicImageField(R.id.imageView, new StringExtractor<News>() {
            @Override
            public String getStringValue(News news, int position) {
                return news.image_url;
            }
        }, new DynamicImageLoader() {
            @Override
            public void loadImage(String url, ImageView view) {
                ImageLoader.getInstance().displayImage(url, view);
                view.setPadding(0,0,0,0);
                view.setAdjustViewBounds(true);
            }
        });

        FunDapter<News> adapter = new FunDapter<>(NewsListActivity.this,
                newsList,
                R.layout.news_layout,
                dictionary);
        ListView lvNews = (ListView) findViewById(R.id.lvNews);
        lvNews.setAdapter(adapter);
    }

    private ImageLoaderConfiguration UILConfig(){
        DisplayImageOptions defaultOptions =
                new DisplayImageOptions.Builder()
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .showImageOnLoading(android.R.drawable.stat_sys_download)
                        .showImageForEmptyUri(android.R.drawable.ic_dialog_alert)
                        .showImageOnFail(android.R.drawable.stat_notify_error)
                        .considerExifParams(true)
                        .bitmapConfig(Bitmap.Config.RGB_565)
                        .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                        .build();

        ImageLoaderConfiguration config =
                new ImageLoaderConfiguration
                        .Builder(getApplicationContext())
                        .threadPriority(Thread.NORM_PRIORITY - 2)
                        .denyCacheImageMultipleSizesInMemory()
                        .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                        .tasksProcessingOrder(QueueProcessingType.LIFO)
                        .defaultDisplayImageOptions(defaultOptions)
                        .build();

        return config;
    }
}

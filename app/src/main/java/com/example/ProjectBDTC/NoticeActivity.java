package com.example.ProjectBDTC;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zzhoujay.okhttpimagedownloader.OkHttpImageDownloader;
import com.zzhoujay.richtext.ImageHolder;
import com.zzhoujay.richtext.RichText;
import com.zzhoujay.richtext.callback.ImageFixCallback;
import com.zzhoujay.richtext.callback.SimpleImageFixCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class NoticeActivity extends AppCompatActivity {
    //从Adapter传入的News对象
    public static News newsPeice;
    //接收到的json文本
    public static String data;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        //让toolbar支持ActionBar操作
        Toolbar toolbar = findViewById(R.id.notice_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        parseJSONWhithGson(data);
        TextView newsContent = (TextView) findViewById(R.id.content_text);
        TextView newsAuthor = (TextView) findViewById(R.id.news_author);
        TextView newsTime = (TextView) findViewById(R.id.news_time);
        TextView newsTitle = (TextView) findViewById(R.id.news_title);
        newsAuthor.setText(newsPeice.getAuthor());
        newsTime.setText(newsPeice.getTime());
        newsTitle.setText(newsPeice.getTitle());

        //TextView支持滑动
        //newsContent.setMovementMethod(ScrollingMovementMethod.getInstance());
//        newsContent.setText(newsPeice.getContent());
        Log.d("Text",newsPeice.getContent());

        RichText.fromMarkdown(newsPeice.getContent()).autoFix(false).fix(new SimpleImageFixCallback() {
            @Override
            public void onInit(ImageHolder holder) {
                String source = holder.getSource();
                Log.d("source",source);
                source = "http://cdn.skyletter.cn/" + source;
                Log.d("new_source",source);
                holder.setSource(source);
                Log.d("图片成功","获取Source为" + source);

            }

            @Override
            public void onSizeReady(ImageHolder holder, int imageWidth, int imageHeight, ImageHolder.SizeHolder sizeHolder) {
                int screenWidth = 500;
                float ratio = screenWidth / imageWidth * 1f;
                int loadImageWidth = imageWidth;
                int loadImageHeight = imageHeight;
                if (ratio < 1f) {
                    loadImageWidth = screenWidth;
                    loadImageHeight = (int) (imageHeight * ratio);
                }
                sizeHolder.setSize(loadImageWidth, loadImageHeight);
            }

            @Override
            public void onImageReady(ImageHolder holder, int width, int height) {
                if(width < 30){
                    holder.setWidth(28);
                    holder.setHeight(28);
                }
                Log.d("图片成功","图片成功");

            }
            @Override
            public void onFailure(ImageHolder holder, Exception e) {
                Log.d("图片失败","图片失败");
            }

        }).imageDownloader(new OkHttpImageDownloader()).into(newsContent);
    }

/*    private void parseJSONWhithGson(String jsonData) {
        Log.d("NoticeActivity","getting newsList");
        Gson gson = new Gson();
        newsPeice = gson.fromJson(jsonData, News.class);
    }*/

    private void parseJSONWhithGson(String jsonData) {
        Log.d("NoticeActivity","Parsing News Content");
        try{
            JSONObject json = new JSONObject(jsonData);
            String content = json.getString("data");
            //解析出新闻正文并存入传入的News对象之中
            newsPeice.setContent(content);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notice, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.exit_item:
                ActivityCollector.finishAll();
                break;
            case R.id.about_item:
                Toast.makeText(this, "作者: Group3 吴迪 & 王龙逊",
                        Toast.LENGTH_LONG).show();
                break;
            case R.id.background_color:
                Toast.makeText(this, "仍在测试中...",
                        Toast.LENGTH_LONG).show();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

}
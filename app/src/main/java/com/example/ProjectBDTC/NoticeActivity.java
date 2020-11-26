package com.example.ProjectBDTC;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zzhoujay.richtext.RichText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class NoticeActivity extends AppCompatActivity {
    //从Adapter传入的News对象
    public static News newsPeice;
    //接收到的json文本
    public static String data = new String();


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
        RichText.fromMarkdown(newsPeice.getContent()).into(newsContent);
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
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
            default:
        }
        return super.onOptionsItemSelected(item);
    }

}
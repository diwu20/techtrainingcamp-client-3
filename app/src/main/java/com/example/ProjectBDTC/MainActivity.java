package com.example.ProjectBDTC;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<News> newsList = new ArrayList<>();
//    private String[] data = {"Apple","Apple","Apple","Apple","Apple","Apple","Apple","Apple","Apple","Apple","Apple","Apple","Apple","Apple"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //隐藏标题栏
        /*ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.hide();
        }*/

        //初始化新闻数据，用于测试
        initNews();
        //将得到的新闻list传入Adapter
        custAdapter adapter = new custAdapter(MainActivity.this, R.layout.news_type2, newsList);
        ListView listView  = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
    }
    //四条新闻，重复写是为了占满屏幕
    private void initNews() {
        for (int i = 0; i < 5; i++) {
            News newsPeice = new News(1,"111","这是第一个新闻","张三","2020年11月14日",null, R.drawable.tancheng);
            newsList.add(newsPeice);
            News newsPeice4 = new News(0,"112","重大新闻：这是一条纯文字标题","上条当麻","2050年9月6日",null, 0);
            newsList.add(newsPeice4);
            News newsPeice3 = new News(1,"111","这是第二个新闻，采用第一种布局","张三","2020年11月14日",null, R.drawable.tb09_1);
            newsList.add(newsPeice3);
            News newsPeice2 = new News(2,"112","这是第三个新闻，采用第二种布局","李四","2020年11月15日",null, R.drawable.teambuilding_04);
            newsList.add(newsPeice2);
            News newsPeice5 = new News(0,"112","文字标题能不能再长点再长点再长点再长点再长点再长点再长点再长点","上条当麻","2050年9月6日",null, 0);
            newsList.add(newsPeice5);

        }
    }
}

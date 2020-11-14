package com.example.ProjectBDTC;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<News> newsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //可选的隐藏标题栏选项
        //ActionBar actionbar = getSupportActionBar();
        //if (actionbar != null) {
        //actionbar.hide();
        //}

        //初始化新闻数据，用于测试
        initNews();

        //将得到的新闻list传入自定义的Adapter，然后使用listView的setAdapater方法进行展示
        custAdapter adapter = new custAdapter(MainActivity.this, R.layout.news_type2, newsList);
        ListView listView  = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
    }

    //直接把新闻内容写在这里，用于测试显示效果
    //由于对应的cover字符串没有被使用，直接使用null代替，使用drawable内的图片id进行代替
    private void initNews() {
        News News0 = new News(
                0, "event_01","2020字节跳动全球员工摄影大赛邀请函", "bytedance",
                "2020年10月7日");
        newsList.add(News0);

        News News1 = new News(
                1,"event_02", "Lark·巡洋计划开发者大赛圆满结束", "bytedance",
                "2019年10月7日",null, new int[] {R.drawable.event_02});
        newsList.add(News1);

        News News2 = new News(
                2,"bytetalk_01", "绝对坦率：打造反馈文化", "bytedance",
                "2020年7月7日", null, new int[] {R.drawable.tancheng});
        newsList.add(News2);

        News News3 = new News(
                3,"teamBuilding_04", "4-12 虹桥天地，蹦起来吧！", "vc team",
                "2019年4月11日", null, new int[] {R.drawable.teambuilding_04});
        newsList.add(News3);

        News News4 = new News(
                4,"teamBuilding_09", "9月18日淀山湖户外团建", "vc mobile team",
                "2020年9月7日", null,
                new int[] {R.drawable.tb09_1,R.drawable.tb09_2,R.drawable.tb09_3,R.drawable.tb09_4});
        newsList.add(News4);
    }
}

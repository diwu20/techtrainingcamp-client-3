package com.example.ProjectBDTC;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends BaseActivity {
    private List<News> newsList;
    private RecyclerView newsView;
    private CoordinatorLayout container;
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCollector.addActivity(this);
        ActivityCollector.getCacheToken(this);
        container = (CoordinatorLayout) findViewById(R.id.container);

        //让toolbar支持ActionBar操作
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        newsView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayout = new LinearLayoutManager(MainActivity.this);
        newsView.setLayoutManager(linearLayout);
        newsView.addItemDecoration(new RecycleViewDivider(
                MainActivity.this, LinearLayoutManager.VERTICAL, 5, getResources().getColor(R.color.gray1)));

        newsList = new ArrayList<>();

        //回调
        okhttp3.Callback callback = new okhttp3.Callback (){
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }
            //重写onResponse，在主线程更新UI
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                parseJSONWhithGson(responseData);
                Log.d("initNews","callback ok");
                //主线程操作
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        showNews(newsList, MainActivity.this, newsView);
                    }
                });
            }
        };

        //初始化新闻列表
        initNews(callback);

        //按钮刷新操作
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OvershootInterpolator interpolator = new OvershootInterpolator();
                ViewCompat.animate(fab).
                        rotationBy(180f).
                        withLayer().
                        setDuration(1000).
                        setInterpolator(interpolator).
                        start();

                initNews(callback);
                Snackbar.make(view, "刷新成功", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (menu != null) {
            //根据登录状态切换菜单
            if (ActivityCollector.token == null) {
                MenuItem exitLogin_item = menu.findItem(R.id.exitLogin_item);
                exitLogin_item.setVisible(false);
            }
            //显示菜单图标
            if (menu.getClass() == MenuBuilder.class) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                }
            }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.exit_item:
                ActivityCollector.finishAll();
                break;

            case R.id.about_item:
                ActivityCollector.showAbout(this);
                break;

            case R.id.exitLogin_item:
                ActivityCollector.token = null;
                ActivityCollector.clearCacheToken(this);
                Snackbar.make(this.findViewById(android.R.id.content),"账号已退出",Snackbar.LENGTH_LONG).setAction("Action", null).show();
                break;

            case R.id.sort_item:
                List<News> sortList = new ArrayList<>(newsList);
                if (ActivityCollector.order == 0) {
                    ActivityCollector.order = -1;
                    item.setIcon(R.drawable.sort_desc);
                    sortListNews(sortList, ActivityCollector.order);
                    Snackbar.make(this.findViewById(android.R.id.content),"时间顺序",Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } else if (ActivityCollector.order == -1) {
                    ActivityCollector.order = 1;
                    item.setIcon(R.drawable.sort_asc);
                    sortListNews(sortList, ActivityCollector.order);
                    Snackbar.make(this.findViewById(android.R.id.content),"时间倒序",Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } else {
                    ActivityCollector.order = 0;
                    item.setIcon(R.drawable.sort_origin);
                  Snackbar.make(this.findViewById(android.R.id.content),"原始排序",Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
                //排序后重新展示公告列表
                showNews(sortList, MainActivity.this, newsView);

                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    /*此方法将任务转向后台的同时会返回桌面
    @Override
    public void onBackPressed() {
        //方式一：将此任务转向后台
        moveTaskToBack(false);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }*/

    @Override
    //按下返回后台运行,回到进入前页面
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.invalidateOptionsMenu();
    }

    //调用getNews方法发送请求
    private void initNews(okhttp3.Callback callback) {
        //String jsonURL = "http://192.168.1.106/metadata.json";
        String jsonURL = "http://cdn.skyletter.cn/metadata.json";
        getNews(jsonURL, callback);
        Log.d("initNews","get ok");
    }

    private static void getNews(String address, okhttp3.Callback callback) {
        OkHttpClient client  = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .build();
        client.newCall(request).enqueue(callback);
    }

    private static void showNews(List<News> newsList, Context context, RecyclerView newsView) {
        recyclerAdapter adapter  = new recyclerAdapter(newsList,context);
        newsView.setAdapter(adapter);
    }

    //调用GSON解析获取的json,此方法已经经过完整测试
    private void parseJSONWhithGson(String jsonData) {
        Log.d("MainActivity","getting newsList");
        Gson gson = new Gson();
        newsList = gson.fromJson(jsonData, new TypeToken<List<News>>(){}.getType());
        //排序
        sortListNews(newsList, ActivityCollector.order);
    }

    //辅助方法，List<News>排序
    private void sortListNews(List<News> newsList, int order) {
        Collections.sort(newsList, new Comparator<News>() {
            @Override
            public int compare(News o1, News o2) {
                char[] time1 = o1.getTime().toCharArray();
                char[] time2 = o2.getTime().toCharArray();
                for (int i = 0; i < time1.length; i++) {
                    if(isChineseChar(time1[i])) {
                        time1[i] = ',';
                    }
                }
                for (int i = 0; i < time2.length; i++) {
                    if(isChineseChar(time2[i])) {
                        time2[i] = ',';
                    }
                }
                String[] s1 = new String(time1).split(",");
                String[] s2 = new String(time2).split(",");
                for (int i = 0; i < Math.min(s1.length, s2.length); i++) {
                   if(s1[i].length() == s2[i].length()) {
                       if (s1[i].equals(s2[i])) {
                        continue;
                       } else  {
                           return s1[i].compareTo(s2[i]) * order;
                       }
                   } else {
                       return (s1[i].length() - s2[i].length()) * order;
                   }
               }
                return order;
            }
        });
    }

    //辅助方法，判断字符是否为中文
    public static boolean isChineseChar(char c) {
        try {
            return String.valueOf(c).getBytes("UTF-8").length > 1;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
    }
}

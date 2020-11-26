package com.example.ProjectBDTC;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends BaseActivity {
    private List<News> newsList = new ArrayList<>();

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //让toolbar支持ActionBar操作
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        //布局
        //TextView mainText = (TextView) findViewById(R.id.main_title);
        //mainText.setBackgroundResource(R.color.pink);
        RecyclerView newsView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        newsView.setLayoutManager(linearLayout);
        newsView.addItemDecoration(new RecycleViewDivider(
                this, LinearLayoutManager.VERTICAL, 5, getResources().getColor(R.color.gray1)));

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
                        recyclerAdapter adapter  = new recyclerAdapter(newsList,MainActivity.this);
                        newsView.setAdapter(adapter);
                    }
                });
            }
        };

        //初始化新闻列表
        initNews(callback);
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

    //调用GSON解析获取的json,此方法已经经过完整测试
    private void parseJSONWhithGson(String jsonData) {
        Log.d("MainActivity","getting newsList");
        Gson gson = new Gson();
        newsList = gson.fromJson(jsonData, new TypeToken<List<News>>(){}.getType());
    }
}

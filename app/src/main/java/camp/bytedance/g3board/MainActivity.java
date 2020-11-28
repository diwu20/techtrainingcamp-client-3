package camp.bytedance.g3board;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import androidx.appcompat.widget.Toolbar;

import camp.bytedance.g3board.R;
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

/**
 *
 * @author Bytedance Technical Camp, Client Group 3, 吴迪 & 王龙逊
 * @date 2020/11/29
 * @descripation 主活动，公告列表展示页，通过OkHttp异步加载公告列表
 *
 */

public class MainActivity extends BaseActivity {

    private List<News> newsList;
    private RecyclerView newsView;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //启动时，调用该方法读取缓存的token和username
        ActivityCollector.getCacheToken(this);

        //让toolbar支持ActionBar操作
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        newsView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayout = new LinearLayoutManager(MainActivity.this);
        newsView.setLayoutManager(linearLayout);
        //给RecyclerView添加分割线
        newsView.addItemDecoration(new RecycleViewDivider(
                MainActivity.this, LinearLayoutManager.VERTICAL, 5, ContextCompat.getColor(this, R.color.gray1)));
        newsList = new ArrayList<>();
        /**
         * 回调
         * 调用parseJSONWithGson方法对获取的json字符串进行解析
         * 然后在主线程使用showNews方法更新UI，显示公告列表
         */
        okhttp3.Callback callback = new okhttp3.Callback (){
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }
            //重写onResponse，在主线程更新UI
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                parseJsonWhithGson(responseData);
                Log.d("initNews","callback ok");
                //主线程操作
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showNews(newsList, MainActivity.this, newsView);
                    }
                });
            }
        };

        /**使用initNews方法，会调用getNews方法，并使用回调方法更新UI**/
        initNews(callback);

        /**
         *刷新按钮
         * 点击刷新按钮，调用initNews方法重新获取新闻列表
         * 点击按钮有旋转180度的动画
         */
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
                MenuItem exitLoginItem = menu.findItem(R.id.exitLogin_item);
                exitLoginItem.setVisible(false);
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
                /**
                 * 三种排序，每点击一次更换一种排序
                 * 原始顺序 -> 时间顺序（时间近的在上面）
                 * 时间顺序 -> 时间倒序（时间远的在上面）
                 * 时间倒序 -> 原始排序（时间远的在上面）
                 * 弹出snacbar提示，调用showNews方法重新展示公告列表
                 */
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

                showNews(sortList, MainActivity.this, newsView);

                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    /**按下返回后台运行,回到进入前页面**/
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    /**从其他活动进入，接收到Intent时，刷新菜单**/
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.invalidateOptionsMenu();
    }

    /**调用getNews方法发送请求**/
    private void initNews(okhttp3.Callback callback) {
        //String jsonURL = "http://192.168.1.106/metadata.json";
        String jsonUrl = "http://cdn.skyletter.cn/metadata.json";
        getNews(jsonUrl, callback);
        Log.d("initNews","get ok");
    }

    /**创建OkHttp实例，并异步加载回调**/
    private static void getNews(String address, okhttp3.Callback callback) {
        OkHttpClient client  = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .build();
        client.newCall(request).enqueue(callback);
    }

    /**创建adapter，将List<News>加载到RecyclerView**/
    private static void showNews(List<News> newsList, Context context, RecyclerView newsView) {
        recyclerAdapter adapter  = new recyclerAdapter(newsList,context);
        newsView.setAdapter(adapter);
    }

    /**调用GSON解析获取的json,内容存入List<News>中**/
    private void parseJsonWhithGson(String jsonData) {
        Log.d("MainActivity","getting newsList");
        Gson gson = new Gson();
        newsList = gson.fromJson(jsonData, new TypeToken<List<News>>(){}.getType());
        //排序
        sortListNews(newsList, ActivityCollector.order);
    }

    /**辅助方法，List<News>排序，重写sort方法
     * order == 1 时间倒序
     * order == -1 时间顺序
     * **/
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

    /**辅助方法，判断字符是否为中文，对公告列表进行排序时使用**/
    public static boolean isChineseChar(char c) {
        try {
            return String.valueOf(c).getBytes("UTF-8").length > 1;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
    }
}

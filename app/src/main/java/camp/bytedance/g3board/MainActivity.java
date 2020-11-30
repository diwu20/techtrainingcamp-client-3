package camp.bytedance.g3board;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
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

    /**
     *
     * @params cmMainRefresh 用于刷新页面的SwipeRefreshLayout控件
     * @params bulletinList 公告列表
     * @params bulletinView 用于显示公告列表的RecyclerView
     **/

    private SwipeRefreshLayout mMainRefresh;
    private List<Bulletin> bulletinList;
    private RecyclerView bulletinView;
    private Callback callback;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (ActivityCollector.dayNightTheme == 1) {
            setTheme(R.style.Theme_nightTime);
        } else {
            setTheme(R.style.Theme_dayTime);

        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ActivityCollector.dayNightTheme == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }

        mMainRefresh = (SwipeRefreshLayout) findViewById(R.id.main_refresh);

        //启动时，调用该方法读取缓存的token和username
        ActivityCollector.getCacheToken(this);

        //让toolbar支持ActionBar操作
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);


        bulletinView = (RecyclerView) findViewById(R.id.main_recycler_view);
        LinearLayoutManager linearLayout = new LinearLayoutManager(MainActivity.this);
        bulletinView.setLayoutManager(linearLayout);

        /**
         * 回调
         * 调用parseJSONWithGson方法对获取的json字符串进行解析
         * 然后在主线程使用showBulletin方法更新UI，显示公告列表
         */
        callback = new Callback (){
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView bottomView = (TextView) findViewById(R.id.reach_bottom);
                        bottomView.setText("加载失败，请点击重试");
                        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.main_touch_aera);
                        relativeLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mMainRefresh.setRefreshing(true);
                                bottomView.setText("重新加载中...");
                                doRefresh();
                            }
                        });
                    }

                });
            }
            //重写onResponse，在主线程更新UI
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                parseJsonWhithGson(responseData);
                Log.d("initBulletin","callback ok");
                //主线程操作
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showBulletin(bulletinList, MainActivity.this, bulletinView);
                    }
                });
            }
        };

        /**若公告列表已存在，则优先加载已有公告列表
         * 否则调用initBulletin方法进行加载*/
        if (ActivityCollector.bulletinList == null) {
            /**使用initBulletin方法，会调用getBulletin方法，并使用回调方法更新UI**/
            initBulletin(callback);
        } else {
            this.bulletinList = ActivityCollector.bulletinList;
            showBulletin(bulletinList,MainActivity.this, bulletinView);
        }

        /**下拉刷新公告列表*/
        mMainRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initBulletin(callback);
                Log.d("SwipeView","Refrehing");
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mMainRefresh.setRefreshing(false);
                        Log.d("SwipeView ",mMainRefresh.isRefreshing()+"");
                    }
                }, 1000);
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
            MenuItem daynight = menu.findItem(R.id.night);
            if (ActivityCollector.dayNightTheme == 0) {
                daynight.setTitle("夜间模式");
                daynight.setIcon(R.drawable.moon);
            } else {
                daynight.setTitle("日间模式");
                daynight.setIcon(R.drawable.sun);
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

            case R.id.night:
                Toolbar toolbar = findViewById(R.id.main_toolbar);
                RelativeLayout layout = (RelativeLayout) findViewById(R.id.main_layout);
                if (ActivityCollector.dayNightTheme == 1) {
                    ActivityCollector.dayNightTheme = 0;
                    switchTheme();
                } else {
                    ActivityCollector.dayNightTheme = 1;
                    switchTheme();
                }
                break;

            case R.id.sort_item:
                /**
                 * 三种排序，每点击一次更换一种排序
                 * 原始顺序 -> 时间顺序（时间近的在上面）
                 * 时间顺序 -> 时间倒序（时间远的在上面）
                 * 时间倒序 -> 原始排序（时间远的在上面）
                 * 弹出snacbar提示，调用showBulletin方法重新展示公告列表
                 */
                if (ActivityCollector.order == 0) {
                    ActivityCollector.order = -1;
                    item.setIcon(R.drawable.sort_desc);
                    Snackbar.make(this.findViewById(android.R.id.content),"时间顺序",Snackbar.LENGTH_LONG).setAction("Action", null).show();

                } else if (ActivityCollector.order == -1) {
                    ActivityCollector.order = 1;
                    item.setIcon(R.drawable.sort_asc);
                    Snackbar.make(this.findViewById(android.R.id.content),"时间倒序",Snackbar.LENGTH_LONG).setAction("Action", null).show();

                } else {
                    ActivityCollector.order = 0;
                    item.setIcon(R.drawable.sort_origin);
                  Snackbar.make(this.findViewById(android.R.id.content),"原始排序",Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }

                showBulletin(bulletinList, MainActivity.this, bulletinView);

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

    /**调用getBulletin方法发送请求**/
    private void initBulletin(okhttp3.Callback callback) {
        //String jsonUrl = "http://192.168.1.106/metadata.json";
        String jsonUrl = "http://cdn.skyletter.cn/metadata.json";
        getBulletin(jsonUrl, callback);
        Log.d("initBulletin","get ok");
    }

    /**创建OkHttp实例，并异步加载回调**/
    private static void getBulletin(String address, okhttp3.Callback callback) {
        OkHttpClient client  = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .build();
        client.newCall(request).enqueue(callback);
    }

    /**创建adapter，将List<Bulletin>加载到RecyclerView**/
    private static void showBulletin(List<Bulletin> bulletinList, Context context, RecyclerView bulletinView) {
        if (bulletinList == null) {
            return;
        }
        RecyclerAdapter adapter  = new RecyclerAdapter(SortBulletinList.sort(bulletinList, ActivityCollector.order),context);
        bulletinView.setAdapter(adapter);
    }

    /**调用GSON解析获取的json,内容存入List<Bulletin>中**/
    private void parseJsonWhithGson(String jsonData) {
        Log.d("MainActivity","getting bulletinList");
        Gson gson = new Gson();
        bulletinList = gson.fromJson(jsonData, new TypeToken<List<Bulletin>>(){}.getType());
        //排序
        SortBulletinList.sort(bulletinList, ActivityCollector.order);
        ActivityCollector.bulletinList = bulletinList;
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView bottomView = (TextView) findViewById(R.id.reach_bottom);
                bottomView.setText("到底了");
            }
        });
    }

    /**辅助方法，List<Bulletin>排序，重写sort方法
     * order == 1 时间倒序
     * order == -1 时间顺序
     * **/

    /**切换主题时调用的方法*/
    private void switchTheme() {
        finish();
        startActivity(new Intent(this, MainActivity.class));
        this.overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out);
    }

    /**进行刷新操作，重新获取文章内容*/
    private void doRefresh() {
        initBulletin(callback);
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                mMainRefresh.setRefreshing(false);
                Log.d("SwipeView ",mMainRefresh.isRefreshing()+"");
            }
        }, 1000);
    }

}

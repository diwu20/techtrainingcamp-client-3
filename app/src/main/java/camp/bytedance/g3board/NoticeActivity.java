package camp.bytedance.g3board;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author Bytedance Technical Camp, Client Group 3, 吴迪 & 王龙逊
 * @date 2020/11/29
 * @descripation 用于公告内容的获取与展示
 *
 */

public class NoticeActivity extends AppCompatActivity {
    /**
     *
     * @params cmNoticeRefresh 用于刷新页面的SwipeRefreshLayout控件
     * @params code 通过网络获取公告时返回的code值
     * @params time 验证时用于计时的变量
     * @params bulletinPeice 从MainActivity传来的公告对象
     * @params bulletinPeice 从MainActivity传来的公告对象d
     * @params data 网络获取公告时返回的json
     * @params nowActivity 存储当前的Context
     *
     */
    private SwipeRefreshLayout mNoticeRefresh;
    static private int code = 100;
    static private int time = 0;
    private Bulletin bulletinPeice;
    public static String data;
    private Context nowActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (ActivityCollector.dayNightTheme == 1) {
            setTheme(R.style.Theme_nightTime);
        } else {
            setTheme(R.style.Theme_dayTime);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        if (ActivityCollector.dayNightTheme == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }

        nowActivity = this;
        //接收Intent传递的Bulletin对象
        Intent intent = getIntent();
        int index = intent.getIntExtra("bulletinPeiceIndex",0);
        bulletinPeice = ActivityCollector.bulletinList.get(index);

        Log.d("Notice_Intent接收","接收到的Bulletin为" + bulletinPeice.getTitle());

        mNoticeRefresh = (SwipeRefreshLayout) findViewById(R.id.notice_refresh);

        //让toolbar支持ActionBar操作
        Toolbar toolbar = findViewById(R.id.notice_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        ScrollView scrollView = (ScrollView) findViewById(R.id.notice_scroll);
        scrollView.setBackgroundResource(ActivityCollector.readerBgColor);

        //调制背景和文字颜色
        if (ActivityCollector.dayNightTheme == 1 && ActivityCollector.readerBgColor != 0) {
            setTextColor(R.color.Blackgray);
        }
        if (ActivityCollector.dayNightTheme == 0 && ActivityCollector.readerBgColor == R.color.grayshadow) {
                ActivityCollector.readerBgColor = 0;
                scrollView.setBackgroundResource(0);
        }

        TextView bulletinAuthor = (TextView) findViewById(R.id.bulletin_author);
        TextView bulletinTime = (TextView) findViewById(R.id.bulletin_time);
        TextView bulletinTitle = (TextView) findViewById(R.id.bulletin_title);
        bulletinAuthor.setText(bulletinPeice.getAuthor());
        bulletinTime.setText(bulletinPeice.getTime());
        bulletinTitle.setText(bulletinPeice.getTitle());
        Log.d("Notice判断前", String.valueOf(ActivityCollector.token));

        if (bulletinPeice.getContent() == null) {
            //调用方法获取文章内容
            sendGetRequestWithHttpUrlConnection(bulletinPeice.getId());
            Log.d("获取正文","正在获取" + bulletinPeice.getTitle());
        } else {
            showContent();
        }

        /**下拉刷新公告列表*/
        mNoticeRefresh.setOnRefreshListener(refreshListener);

        /**更新菜单项*/
        invalidateOptionsMenu();
    }

    /**用于刷新操作的refreshListener，调用doRefresh方法*/
    private  SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            doRefresh();
        }
    };

    /**进行刷新操作，重新获取文章内容*/
    private void doRefresh() {
        sendGetRequestWithHttpUrlConnection(bulletinPeice.getId());
        Log.d("刷新正文","正在获取" + bulletinPeice.getTitle());
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                mNoticeRefresh.setRefreshing(false);
                Log.d("SwipeView ",mNoticeRefresh.isRefreshing()+"");
            }
        }, 1000);
    }

    /**解析获取的公告内容字符串，存入Bulletin对象中*/
    private void parseJson(String jsonData) {
        Log.d("NoticeActivity","Parsing Bulletin Content");
        try{
            JSONObject json = new JSONObject(jsonData);
            String content = json.getString("data");
            //解析出新闻正文并存入传入的Bulletin对象之中
            bulletinPeice.setContent(content);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**调用MdSupport展示文本*/
    private void showContent() {
        Log.d("Text", bulletinPeice.getContent());
        TextView bulletinContent = (TextView) findViewById(R.id.content_text);
        new MdSupprt().showMdString(this, bulletinPeice.getContent(),bulletinContent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notice, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (menu != null) {
            //根据登录状态切换菜单
            if (ActivityCollector.dayNightTheme == 1) {
                MenuItem white = menu.findItem(R.id.white);
                white.setTitle("黑色");
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        ScrollView scrollView = (ScrollView) findViewById(R.id.notice_scroll);
        switch (item.getItemId()) {

            case R.id.exit_item:
                ActivityCollector.finishAll();
                break;

            case R.id.about_item:
                ActivityCollector.showAbout(this);
                break;

            case R.id.green:
                scrollView.setBackgroundResource(R.color.Green);
                ActivityCollector.readerBgColor = R.color.Green;
                if (ActivityCollector.dayNightTheme == 1) {
                    setTextColor(R.color.Blackgray);
                }

                break;

            case R.id.paper:
                scrollView.setBackgroundResource(R.drawable.paper);
                ActivityCollector.readerBgColor = R.drawable.paper;
                if (ActivityCollector.dayNightTheme == 1) {
                    setTextColor(R.color.Blackgray);
                }
                break;

            case R.id.white:
                if (ActivityCollector.dayNightTheme == 1) {
                    scrollView.setBackgroundResource(0);
                    ActivityCollector.readerBgColor = 0;
                    setTextColor(R.color.Blackgrayshadow);
                } else {
                    ActivityCollector.readerBgColor = 0;
                    scrollView.setBackgroundResource(0);
                }
            break;

            /**向上按钮设置为返回功能*/
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    /**发送网络请求，处理异常*/
    private void sendGetRequestWithHttpUrlConnection(String id) {
        //开启线程进行定时监控
        verifyResponse();
        Thread connect = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                /**获取内容*/
                try {
                    URL url = new URL("https://vcapi.lvdaqian.cn/article/"+id+"?markdown=true");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Authorization","Bearer " + ActivityCollector.token);
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    Log.d("MainActivity", "run connection: " + connection.toString());
                    InputStream in = connection.getInputStream();
                    Log.d("MainActivity", "run connection in: " + in);
                    //读取返回
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    String responseStr = response.toString();
                    Log.d("MainActivity", "response为: " + responseStr);
                    JSONObject jsonObject = new JSONObject(responseStr);
                    NoticeActivity.code = (int) jsonObject.get("code");
                    NoticeActivity.data = responseStr;
                    Log.d("AdapterToNotice",responseStr);
                } catch (Exception e) {
                    /**异常处理，错误，重新登录*/
                    if (String.valueOf(e).contains("java.io.FileNotFoundException")) {
                        //清空登录信息
                        ActivityCollector.token = null;
                        ActivityCollector.clearCacheToken(NoticeActivity.this);
                        //启动登陆活动
                        Intent login = new Intent("camp.bytedance.g3board.LOGIN_START");
                        login.putExtra("bulletinPeice", ActivityCollector.bulletinList.indexOf(bulletinPeice));
                        nowActivity.startActivity(login);
                        finish();
                        NoticeActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(NoticeActivity.this,"验证失败，请重新登录...",Toast.LENGTH_LONG).show();
                            }
                        });
                        Log.d("获取正文失败","TOKEN验证失败");
                    }
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        });
        connect.start();
    }

    /**监控网络请求返回值，超时进行处理*/
    private void verifyResponse() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("获取正文","进入子线程操作");
                while (NoticeActivity.code == 100) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.d("获取正文次数",String.valueOf(time));
                    if (time++ > 20) {
                        break;
                    }
                }
                //获取成功解析内容并展示
                if (code == 0) {
                    time = 0 ;
                    code = 100;
                    parseJson(data);
                    NoticeActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showContent();
                            Log.d("正文内容", "文章内容为" + bulletinPeice.getContent());
                        }
                    });
                } else {
                    NoticeActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView textView = (TextView) findViewById(R.id.content_text);
                            textView.setText("加载失败，点击屏幕重新加载");
                            RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.notice_touch_aera);
                            relativeLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    time = 0;
                                    mNoticeRefresh.setRefreshing(true);
                                    textView.setText("重新加载中...");
                                    doRefresh();
                                }
                            });
                        }
                    });
                    Log.d("获取正文失败","网络获取失败");
                }
            }
        }).start();
    }

    /**更改页面内的文字颜色，在更换背景时使用*/
    private void setTextColor(int color){
        color = ContextCompat.getColor(this, color);
        TextView bulletinAuthor = (TextView) findViewById(R.id.bulletin_author);
        TextView bulletinTime = (TextView) findViewById(R.id.bulletin_time);
        TextView bulletinTitle = (TextView) findViewById(R.id.bulletin_title);
        TextView bulletinContent = (TextView) findViewById(R.id.content_text);
        bulletinAuthor.setTextColor(color);
        bulletinTime.setTextColor(color);
        bulletinTitle.setTextColor(color);
        bulletinContent.setTextColor(color);
    }
}
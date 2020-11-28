package camp.bytedance.g3board;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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

public class NoticeActivity extends AppCompatActivity {
    static private int code = 100;
    static private int time = 0;
    //从Adapter传入的Bulletin对象
    private Bulletin bulletinPeice;
    //接收到的json文本
    public static String data;
    private Context nowActivity;
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        ActivityCollector.addActivity(this);
        nowActivity = this;
        //接收Intent传递的Bulletin对象
        Intent intent = getIntent();
        bulletinPeice = (Bulletin) intent.getParcelableExtra("bulletinPeice");
        Log.d("Notice_Intent接收","接收到的Bulletin为" + bulletinPeice.getTitle());

        //让toolbar支持ActionBar操作
        Toolbar toolbar = findViewById(R.id.notice_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        toolbar.setBackground(new ColorDrawable(ActivityCollector.bgColor));

        //背景颜色记忆
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.notice_layout);
        layout.setBackgroundResource(ActivityCollector.bgColor);
        ScrollView scrollView = (ScrollView) findViewById(R.id.notice_scroll);
        scrollView.setBackgroundResource(ActivityCollector.readerBgColor);

        TextView bulletinAuthor = (TextView) findViewById(R.id.bulletin_author);
        TextView bulletinTime = (TextView) findViewById(R.id.bulletin_time);
        TextView bulletinTitle = (TextView) findViewById(R.id.bulletin_title);
        bulletinAuthor.setText(bulletinPeice.getAuthor());
        bulletinTime.setText(bulletinPeice.getTime());
        bulletinTitle.setText(bulletinPeice.getTitle());
        Log.d("Notice判断前", String.valueOf(ActivityCollector.token));
        //调用方法获取文章内容
        sendGetRequestWithHttpUrlConnection(bulletinPeice.getId());
        Log.d("获取正文","正在获取" + bulletinPeice.getTitle());
        //获取失败自动重试，超时停止
        //veryfy();
    }

    private void parseJsonWhithGson(String jsonData) {
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

    private void showContent() {
        Log.d("Text", bulletinPeice.getContent());
        TextView bulletinContent = (TextView) findViewById(R.id.content_text);
        new MdSupprt().showMdString(this, bulletinPeice.getContent(),bulletinContent);
//        final Markwon markwon = Markwon.create(this);
//        markwon.setMarkdown(bulletinContent, bulletinPeice.getContent());
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notice, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("ResourceType")
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
                break;

            case R.id.paper:
                scrollView.setBackgroundResource(R.drawable.paper);
                ActivityCollector.readerBgColor = R.drawable.paper;
                break;

            case R.id.white:
                scrollView.setBackgroundResource(R.color.white);
                ActivityCollector.readerBgColor = R.color.white;
            break;

            //向上按钮设置为返回功能
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    //重写返回方法，直接返回到MainActivity
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(NoticeActivity.this, MainActivity.class);
        //添加Flag，使Activity不被重新创建
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    private void sendGetRequestWithHttpUrlConnection(String id) {
        veryfy();
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL("https://vcapi.lvdaqian.cn/article/"+id+"?markdown=true");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    Log.d("获取内容时的Token为",String.valueOf(ActivityCollector.token));
                    connection.setRequestProperty("Authorization","Bearer " + ActivityCollector.token);
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    Log.d("MainActivity", "run: " + connection.toString());
                    InputStream in = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    String response_str = response.toString();
                    Log.d("MainActivity", "run: " + response_str);
                    JSONObject jsonObject = new JSONObject(response_str);
                    NoticeActivity.code = (int) jsonObject.get("code");
                    NoticeActivity.data = response_str;
                    Log.d("AdapterToNotice",response_str);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Intent login = new Intent("camp.bytedance.g3board.LOGIN_START");
                            //使用Intent传递Bulletin对象
                            login.putExtra("bulletinPeice", bulletinPeice);
                            nowActivity.startActivity(login);
                            NoticeActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(NoticeActivity.this,"验证失败，请重新登录...",Toast.LENGTH_LONG).show();
                                }
                            });
                            Log.d("获取正文失败","TOKEN验证失败");
                        }
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    /**验证内容获取子线程*/
    private void veryfy() {
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
                    if (time++ > 500) {
                        break;
                    }
                }
                //获取成功解析内容并展示
                if (code == 0) {
                    time = 0 ;
                    code = 100;
                    parseJsonWhithGson(data);
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
                            Toast.makeText(NoticeActivity.this,"加载失败，请检查网络",Toast.LENGTH_LONG).show();
                            TextView textView = (TextView) findViewById(R.id.content_text);
                            textView.setText("加载失败，点击屏幕重新加载");
                            ScrollView scrollView = (ScrollView) findViewById(R.id.notice_scroll);
                            scrollView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    sendGetRequestWithHttpUrlConnection(bulletinPeice.getId());
                                }
                            });
                        }
                    });
                    Log.d("获取正文失败","网络获取失败");
                }
            }
        }).start();
    }
}
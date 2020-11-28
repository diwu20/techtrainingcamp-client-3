package camp.bytedance.g3board;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
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
    private Context NowActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        ActivityCollector.addActivity(this);
        NowActivity = this;
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

        //背景颜色记忆
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.notice_layout);
        if (ActivityCollector.bgColor != 0) {
        layout.setBackgroundResource(ActivityCollector.bgColor);
        }
        TextView bulletinAuthor = (TextView) findViewById(R.id.bulletin_author);
        TextView bulletinTime = (TextView) findViewById(R.id.bulletin_time);
        TextView bulletinTitle = (TextView) findViewById(R.id.bulletin_title);
        bulletinAuthor.setText(bulletinPeice.getAuthor());
        bulletinTime.setText(bulletinPeice.getTime());
        bulletinTitle.setText(bulletinPeice.getTitle());

        Log.d("Notice判断前", String.valueOf(ActivityCollector.token));
        //调用方法获取文章内容
        sendGetRequestWithHttpURLConnection(bulletinPeice.getId());
        Log.d("获取正文","正在获取" + bulletinPeice.getTitle());
        //获取失败自动重试，超时停止

    }

    private void parseJSONWhithGson(String jsonData) {
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
        TextView bulletinContent = (TextView) findViewById(R.id.content_text);
        Log.d("Text", bulletinPeice.getContent());
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
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.notice_layout);
        switch (item.getItemId()) {
            case R.id.exit_item:
                ActivityCollector.finishAll();
                break;
            case R.id.about_item:
                ActivityCollector.showAbout(this);
                break;
            case R.id.green:
                layout.setBackgroundResource(R.color.Green);
                ActivityCollector.bgColor = R.color.Green;
//                Snackbar.make(new View(this),"更换绿色背景成功",Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                break;
            case R.id.paper:
                layout.setBackgroundResource(R.drawable.paper);
                ActivityCollector.bgColor = R.drawable.paper;
//                Snackbar.make(new View(this),"更换类纸背景成功",Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                break;
                case R.id.white:
                layout.setBackgroundResource(R.color.white);
                ActivityCollector.bgColor = R.color.white;
//                Snackbar.make(new View(this),"更换类纸背景成功",Snackbar.LENGTH_SHORT).setAction("Action", null).show();
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

    private void sendGetRequestWithHttpURLConnection(String id) {
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
                    //主线程操作
                    NoticeActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            while (code == 100) {
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                if (time++ > 20) {
                                    break;
                                }
                            }
                            //获取成功解析内容并展示
                            if (code == 0) {
                                time = 0 ;
                                parseJSONWhithGson(data);
                                showContent();
                                Log.d("正文内容","文章内容为" + bulletinPeice.getContent());
                            } else {
                                Intent login = new Intent("com.example.ProjectBDTC.LOGIN_START");
                                //使用Intent传递Bulletin对象
                                login.putExtra("bulletinPeice", bulletinPeice);
                                NowActivity.startActivity(login);
                                Toast.makeText(NoticeActivity.this,"验证失败，请重新登录...",Toast.LENGTH_LONG).show();
                                Log.d("获取正文失败","网络获取失败");
                            }
                        }
                    });
                } catch (Exception e) {
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
        }).start();
    }
}
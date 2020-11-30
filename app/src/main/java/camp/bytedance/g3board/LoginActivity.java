package camp.bytedance.g3board;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Bytedance Technical Camp, Client Group 3, 吴迪 & 王龙逊
 * @date 2020/11/29
 * @descripation 登录活动，传入Intent中包含Bulletin对象，登录完成之后将Bulletin对象传递给NoticeActivity进行展示
 *
 */

public class LoginActivity extends BaseActivity {
    private boolean flag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (ActivityCollector.dayNightTheme == 1) {
            setTheme(R.style.Theme_nightTime);
        } else {
            setTheme(R.style.Theme_dayTime);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent intent = getIntent();
        int index = intent.getIntExtra("bulletinPeiceIndex",0);
        Bulletin bulletinPeice = ActivityCollector.bulletinList.get(index);
        Log.d("Login_Intent接收","接收到的Bulletin为" + bulletinPeice.getTitle());

        //让toolbar支持ActionBar操作
        Toolbar toolbar = findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);

        EditText username = (EditText) findViewById(R.id.username);
        EditText password = (EditText) findViewById(R.id.password);
        Button button_login = (Button) findViewById(R.id.button_login);
        button_login.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                //点击登录隐藏输入法
                InputMethodManager imm =(InputMethodManager)getSystemService(
                        INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(password.getWindowToken(), 0);
                //网络请求
                sendPostRequestWithHttpUrlConnection(username.getText().toString(),
                        password.getText().toString().hashCode());
                flag = false;
                int time = 0;
                while (flag == false) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (time++ > 20) {
                        break;
                    }
                }
                //获取登录反馈
                if (flag == true) {
                    Intent notice = new Intent("camp.bytedance.g3board.NOTICE_START");
                    //使用intent传递公告在list中的下标
                    notice.putExtra("bulletinPeiceIndex", index);
                    Snackbar.make(v,"登录成功",Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            startActivity(notice);
                            LoginActivity.this.finish();
                        }
                    };
                    Timer timer = new Timer();
                    //延迟两秒切换至新的Activity
                    timer.schedule(task, 1000);
                    ActivityCollector.removeActivity(LoginActivity.this);
                } else {
                    Toast.makeText(LoginActivity.this,"登录失败...",Toast.LENGTH_LONG).show();
                    Log.d("登录失败","获取Token失败");
                }
            }
        });
    }

    /**重写返回方法，点击返回直接返回到MainActivity*/
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        //添加Flag，使Activity不被重新创建
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
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
                ActivityCollector.showAbout(this);
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *
     * 在子线程中进行网络请求，获取TOKEN，并将username/token存入ActivityCollector
     *
     */
    private void sendPostRequestWithHttpUrlConnection(String username, int password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL("https://vcapi.lvdaqian.cn/login");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    out.writeBytes("username="+username+"&password="+password);
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while((line = reader.readLine())!=null){
                        response.append(line);
                    }
                    String response_str = response.toString();
                    Log.d("LoginActivity", "run: "+response_str);
                    JSONObject jsonObject = new JSONObject(response_str);
                    ActivityCollector.token = jsonObject.get("token").toString();
                    ActivityCollector.username = username;
                    ActivityCollector.cacheToken(LoginActivity.this);
                    flag = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    if(reader!=null){
                        try{
                            reader.close();
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                    if(connection!= null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}
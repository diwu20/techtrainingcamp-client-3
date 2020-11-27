package com.example.ProjectBDTC;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginActivity extends BaseActivity {
    private boolean flag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActivityCollector.addActivity(this);

        Intent intent = getIntent();
        News newsPeice = (News) intent.getParcelableExtra("newsPeice");
        Log.d("Login_Intent接收","接收到的News为" + newsPeice.getTitle());

        //让toolbar支持ActionBar操作
        Toolbar toolbar = findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);

        EditText username = (EditText) findViewById(R.id.username);
        EditText password = (EditText) findViewById(R.id.password);
        Button button_login = (Button) findViewById(R.id.button_login);
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPostRequestWithHttpURLConnection(username.getText().toString(),
                        password.getText().toString().hashCode());
                flag = false;
                int time = 0;
                while (flag == false) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (time++ > 90) {
                        break;
                    }
                }
                //获取登录反馈
                if (flag == true) {
                    Intent notice = new Intent("com.example.ProjectBDTC.NOTICE_START");
                    notice.putExtra("newsPeice", newsPeice);
                    startActivity(notice);
                } else {
                    Toast.makeText(LoginActivity.this,"登录失败...",Toast.LENGTH_LONG).show();
                    Log.d("登录失败","获取Token失败");
                }
            }
        });
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

    private void sendPostRequestWithHttpURLConnection(String username, int password) {
        //开启线程发起网络请求
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
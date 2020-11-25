package com.example.ProjectBDTC;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

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
    protected static int code = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //让toolbar支持ActionBar操作
        Toolbar toolbar = findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);
        EditText username = (EditText) findViewById(R.id.username);
        EditText password = (EditText) findViewById(R.id.password);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar_login);
        progressBar.setVisibility(View.INVISIBLE);
        Button button_login = (Button) findViewById(R.id.button_login);
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPostRequestWithHttpURLConnection(username.getText().toString(),
                        password.getText().toString().hashCode());
                int i = 0;
                progressBar.setVisibility(View.VISIBLE);
                while (code == 100) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    i = i + 1;
                    if (i > 90) {
                        break;
                    }
                }
                progressBar.setVisibility(View.INVISIBLE);
                if (code == 0) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("username", username.getText().toString());
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "哎呀，出了些问题哦",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.exit_item:
                ActivityCollector.finishAll();
                break;
            case R.id.about_item:
                Toast.makeText(this, "作者：Group3 吴迪 & 王龙逊",
                        Toast.LENGTH_LONG).show();
                break;
            default:
        }
        return true;
    }

    private void sendPostRequestWithHttpURLConnection(String username, int password) {
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
                    out.writeBytes("username=" + username + "&password=" + password);
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    String response_str = response.toString();
                    Log.d("LoginActivity", "run: " + response_str);
                    JSONObject jsonObject = new JSONObject(response_str);
                    ActivityCollector.token = jsonObject.get("token").toString();
                    LoginActivity.code = (int) jsonObject.get("code");
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
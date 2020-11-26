package com.example.ProjectBDTC;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class NoticeActivity extends AppCompatActivity {

    public static String data = new String();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        TextView textView = (TextView) findViewById(R.id.notice_text);
        textView.setText(data);
    }
}
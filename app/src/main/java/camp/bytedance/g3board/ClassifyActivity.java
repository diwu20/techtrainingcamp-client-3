package camp.bytedance.g3board;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Bytedance Technical Camp, Client Group 3, 吴迪 & 王龙逊
 * @date 2020/11/29
 * @descripation 用于展示某一个来源的所有公告
 *
 */

public class ClassifyActivity extends AppCompatActivity {

    private static String authorName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classify);

        Toolbar toolbar = findViewById(R.id.classify_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        authorName = intent.getStringExtra("bulletinPeice");
        Log.d("Classify_Intent接收","接收到的作者为" + authorName);

        TextView authorView =  (TextView) findViewById(R.id.author_view);
        authorView.setText(authorName + "发布的全部公告");

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.classify_recycler_view);
        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayout);

        RecyclerAdapter adapter  = new RecyclerAdapter(classifyByAuthor(authorName),this);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        ScrollView scrollView = (ScrollView) findViewById(R.id.notice_scroll);
        switch (item.getItemId()) {
            //向上按钮设置为返回功能
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private List<Bulletin> classifyByAuthor(String authorName) {
        List<Bulletin> list = new ArrayList<>(ActivityCollector.bulletinList);
        for (int i = 0; i < list.size(); i++) {
            String peiceAuthor = list.get(i).getAuthor();
            Log.d("Classify_移除","作者名是" + peiceAuthor);
            if (!peiceAuthor.equals(authorName)) {
                Log.d("Classify_移除",list.get(i).getAuthor()+"/" + authorName);
                list.remove(i);
                i--;
            }
        }
        return list;
    }
}
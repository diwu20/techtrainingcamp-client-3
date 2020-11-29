package camp.bytedance.g3board;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Bytedance Technical Camp, Client Group 3, 吴迪 & 王龙逊
 * @date 2020/11/29
 * @descripation 用于展示某一个作者发布的所有公告
 *
 */

public class ClassifyActivity extends AppCompatActivity {
    /**
     * @params author 传入的author字符串
     */
    private String author;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (ActivityCollector.dayNightTheme == 1) {
            setTheme(R.style.Theme_nightTime);
        } else {
            setTheme(R.style.Theme_dayTime);
        }

        ActivityCollector.classifyActivityOn = true;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classify);

        //让toolbar支持ActionBar操作
        Toolbar toolbar = findViewById(R.id.classify_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        author = intent.getStringExtra("author");

        TextView name = (TextView) findViewById(R.id.name);
        name.setText(author + "发布的全部公告");
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.classify_recycler_view);
        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayout);

        RecyclerAdapter adapter  = new RecyclerAdapter(sortByAuthor(author),this);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        ScrollView scrollView = (ScrollView) findViewById(R.id.notice_scroll);
        switch (item.getItemId()) {
            /**向上按钮设置为返回功能*/
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    /**将author对应的bulletin挑出来*/
    private List<Bulletin> sortByAuthor(String author) {
        List<Bulletin> authorList = new ArrayList<>(ActivityCollector.bulletinList);
        for (int i = 0; i < authorList.size(); i++) {
            Bulletin bulletin = authorList.get(i);
            if (!bulletin.getAuthor().equals(author)) {
                authorList.remove(i);
                i--;
            }
        }
        return authorList;
    }
}


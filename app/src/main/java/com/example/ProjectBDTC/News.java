package com.example.ProjectBDTC;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.List;

public class News {
    //组成新闻类的几个元素，和json文件中的定义对应
    private int type;
    private String id;
    private String title;
    private String cover;
    private List<String> covers;
    private String author;
    private String publishTime;
    private String data;
    private Bitmap[] bitmap;

    public String getTitle() {
        return title;
    }
    public String getId() {
        return id;
    }
    public String getAuthor() {
        return author;
    }
    public String getTime() {
        return publishTime;
    }
    public String getCover() {
        return cover;
    }
    public List<String> getCovers() {
        return covers;
    }
    public int getType() {
        return type;
    }
    public String getContent() {
        return data;
    }
    public void setContent(String content) {
        data = content;
    }
    public void setBitmap(Bitmap[] bm) {
        bitmap = bm;
    }
    public Bitmap[] getBitmap() {
        return bitmap;
    }

}

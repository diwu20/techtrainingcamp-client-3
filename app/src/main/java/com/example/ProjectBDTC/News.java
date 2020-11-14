package com.example.ProjectBDTC;

import java.util.List;

public class News {
//    组成新闻类的几个元素，和json文件中的定义对应
    private int type;
    private String id;
    private String title;
    private List<String> cover;
    private String author;
    private String time;

    private int imageId; //imageID为drawable内对应的图片id，仅用于测试展示，后期应当去除，改成根据cover内的字符串读取本地图片文件

    public News(int type, String id, String title, String author, String time, List<String> cover, int imageId) {
        this.type= type;
        this.id = id;
        this.title = title;
        this.author = author;
        this.time = time;
        this.cover = cover;

        this.imageId = imageId;
    }

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
        return time;
    }
    public List<String> getCover() {
        return cover;
    }
    public int getType() {
        return type;
    }

    public int getImageId() {
        return imageId;
    }
}

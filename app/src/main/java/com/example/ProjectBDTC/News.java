package com.example.ProjectBDTC;

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

    //imageID为drawable内对应的图片id，仅用于测试展示，后期应当改成根据cover内的字符串读取本地图片文件
    private int[] imageId;

    //无图type0的构造方法
    public News(int type, String id, String title, String author, String time) {
        this.type= type;
        this.id = id;
        this.title = title;
        this.author = author;
        this.publishTime = time;
        this.cover = cover;
        this.imageId = imageId;
    }

    //有图type的构造方法，使用ImageId读取drawable内的测试图片，cover暂时不启用，输入null即可
    public News(int type, String id, String title, String author, String time, List<String> covers, int[] imageId) {
        this.type= type;
        this.id = id;
        this.title = title;
        this.author = author;
        this.publishTime = time;
        this.covers = covers;
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
    public int[] getImageId() {
        return imageId;
    }

    //测试使用，利用Cover名称获取图片id，代替网络图片读取功能
    public int[] getCoverId() {
        if (getCover() == null && getCovers() == null) {
            return null;
        }
        String cover = getCover();
        String p1 = "tangcheng.jpg";
        String p2 = "event_02.png";
        String p3 = "teamBuilding_04.png";
        if (cover == null) {
            return new int[] {R.drawable.tb09_1,R.drawable.tb09_2,R.drawable.tb09_3,R.drawable.tb09_4};
        } else if (cover.equals(p1)) {
            return new int[] {R.drawable.tancheng};
        }
        if (cover.equals(p2)) {
            return new int[] {R.drawable.event_02};
        }
        if (cover.equals(p3)) {
            return new int[] {R.drawable.teambuilding_04};
        }
        else return null;
    }
}

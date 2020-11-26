package com.example.ProjectBDTC;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.List;

public class News implements Parcelable {
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

    protected News(Parcel in) {
        type = in.readInt();
        id = in.readString();
        title = in.readString();
        cover = in.readString();
        covers = in.createStringArrayList();
        author = in.readString();
        publishTime = in.readString();
        data = in.readString();
        bitmap = in.createTypedArray(Bitmap.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(cover);
        dest.writeStringList(covers);
        dest.writeString(author);
        dest.writeString(publishTime);
        dest.writeString(data);
        dest.writeTypedArray(bitmap, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<News> CREATOR = new Creator<News>() {
        @Override
        public News createFromParcel(Parcel in) {
            return new News(in);
        }

        @Override
        public News[] newArray(int size) {
            return new News[size];
        }
    };

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

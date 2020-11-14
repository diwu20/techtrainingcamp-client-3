package com.example.ProjectBDTC;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class custAdapter extends ArrayAdapter<News> {
private int resourceId;
    public custAdapter(Context context,int layoutId, List<News> objects) {
        super(context, layoutId, objects);
        }
    //重写View，指定布局方式
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        News peice = getItem(position);
        int layoutId = getlayoutId(peice.getType());
        View view = LayoutInflater.from(getContext()).inflate(layoutId, parent, false);

        ImageView newsImage = (ImageView) view.findViewById(R.id.news_image);
        TextView newsTitle = (TextView) view.findViewById(R.id.news_title);
        TextView newsAuthor = (TextView) view.findViewById(R.id.news_author);
        TextView newsTime = (TextView) view.findViewById(R.id.news_time);

        newsImage.setImageResource(peice.getImageId()); //暂且使用drawable内图片id进行展示，需添加本地文件读取功能
        newsTitle.setText(peice.getTitle());
        newsAuthor.setText(peice.getAuthor());
        newsTime.setText(peice.getTime());
        return view;
    }
    //根据news中的type值，确定对应的layout布局，返回布局ID到View方法中
    private int getlayoutId(int type) {
        switch (type) {
            case 0 : return R.layout.news_type0;
            case 1 : return R.layout.news_type1;
            case 2 : return R.layout.news_type2;
            case 3 : return R.layout.news_type3;
            case 4 : return R.layout.news_type4;
            default: return R.layout.news_type2;
        }
    }
}



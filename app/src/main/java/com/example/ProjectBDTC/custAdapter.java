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
        //peice为输入的News对象
        News peice = getItem(position);
        int layoutId = getlayoutId(peice.getType());
        View view = LayoutInflater.from(getContext()).inflate(layoutId, parent, false);

        //四种layout都包含的三个TextView可以直接处理
        TextView newsTitle = (TextView) view.findViewById(R.id.news_title);
        TextView newsAuthor = (TextView) view.findViewById(R.id.news_author);
        TextView newsTime = (TextView) view.findViewById(R.id.news_time);
        newsTitle.setText(peice.getTitle());
        newsAuthor.setText(peice.getAuthor());
        newsTime.setText(peice.getTime());

        //type0中没有ImageView，因此不需要做处理，使用判断跳过
        if (peice.getImageId() != null)
        //getImageId方法得到一个数组，需要判断是单图还是多图
        //如果是多图，则传入imageId前四个元素，如果是单图，则传入imageId[0]
            if(peice.getType() == 4) {
                ImageView newsImage1 = (ImageView) view.findViewById(R.id.news_image1);
                ImageView newsImage2 = (ImageView) view.findViewById(R.id.news_image2);
                ImageView newsImage3 = (ImageView) view.findViewById(R.id.news_image3);
                ImageView newsImage4 = (ImageView) view.findViewById(R.id.news_image4);
                newsImage1.setImageResource(peice.getImageId()[0]);
                newsImage2.setImageResource(peice.getImageId()[1]);
                newsImage3.setImageResource(peice.getImageId()[2]);
                newsImage4.setImageResource(peice.getImageId()[3]);
            } else {
                ImageView newsImage = (ImageView) view.findViewById(R.id.news_image);
                newsImage.setImageResource(peice.getImageId()[0]);
            }
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



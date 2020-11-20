package com.example.ProjectBDTC;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.motion.widget.KeyCycle;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class recyclerAdapter extends RecyclerView.Adapter<recyclerAdapter.ViewHolder> {

    //储存传入的新闻列表
    private List<News> newsList;

    //重写ViewHolder
    static class ViewHolder extends RecyclerView.ViewHolder {

        //用于点击事件
        View newsView;

        //先声明holder中的几种view
        TextView newsTitle;
        TextView newsAuthor;
        TextView newsTime;
        ImageView newsImage;
        ImageView newsImage1;
        ImageView newsImage2;
        ImageView newsImage3;
        ImageView newsImage4;

        //链接到layout上的定义
        public ViewHolder(View view) {
            super(view);
            newsView = view;
            newsTitle = (TextView) view.findViewById(R.id.news_title);
            newsAuthor = (TextView) view.findViewById(R.id.news_author);
            newsTime = (TextView) view.findViewById(R.id.news_time);
            newsImage = (ImageView) view.findViewById(R.id.news_image);
            newsImage1 = (ImageView) view.findViewById(R.id.news_image1);
            newsImage2 = (ImageView) view.findViewById(R.id.news_image2);
            newsImage3 = (ImageView) view.findViewById(R.id.news_image3);
            newsImage4 = (ImageView) view.findViewById(R.id.news_image4);
        }
    }

    public recyclerAdapter(List<News> objects) {
        //传入新闻列表
        newsList = objects;
    }

    @Override
    public int getItemViewType(int position) {
        //根据position返回对应type
        return newsList.get(position).getType();
    }

    //重写onCreateViewHolder,添加点击事件，使用Toast测试
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //使用getlayoutID方法创建合适的ViewHolder并返回
        View view = LayoutInflater.from(parent.getContext()).inflate(getlayoutId(viewType), parent,false);
        final ViewHolder holder = new ViewHolder(view);

        //点击view的事件
        holder.newsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAbsoluteAdapterPosition();
                News newsPeice = newsList.get(position);
                Toast.makeText(v.getContext(),"测试，你点击了id为" + newsPeice.getId() +"的新闻", Toast.LENGTH_LONG).show();
            }
        });
        //点击标题的事件
        holder.newsTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAbsoluteAdapterPosition();
                News newsPeice = newsList.get(position);
                Toast.makeText(v.getContext(),"你点击了新闻标题，新闻id为" + newsPeice.getId() +"的新闻", Toast.LENGTH_LONG).show();
            }
        });
        //点击日期事件
        holder.newsTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAbsoluteAdapterPosition();
                News newsPeice = newsList.get(position);
                Toast.makeText(v.getContext(),"日期为" + newsPeice.getTime(), Toast.LENGTH_LONG).show();
            }
        });
        //点击作者事件
        holder.newsAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAbsoluteAdapterPosition();
                News newsPeice = newsList.get(position);
                Toast.makeText(v.getContext(),"作者是" + newsPeice.getAuthor(), Toast.LENGTH_LONG).show();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //获取单条新闻信息
        News peice = newsList.get(position);
        holder.newsTitle.setText(peice.getTitle());
        holder.newsAuthor.setText(peice.getAuthor());
        holder.newsTime.setText(peice.getTime());
        if (peice.getImageId() != null) {
            if(peice.getType() == 4) {
                holder.newsImage1.setImageResource(peice.getImageId()[0]);
                holder.newsImage2.setImageResource(peice.getImageId()[1]);
                holder.newsImage3.setImageResource(peice.getImageId()[2]);
                holder.newsImage4.setImageResource(peice.getImageId()[3]);
            } else {
                holder.newsImage.setImageResource(peice.getImageId()[0]);
            }
        }
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    //辅助方法，根据type值，返回对应的布局ID
    private int getlayoutId (int type){
        switch (type) {
            case 0:
                return R.layout.news_type0;
            case 1:
                return R.layout.news_type1;
            case 2:
                return R.layout.news_type2;
            case 3:
                return R.layout.news_type3;
            case 4:
                return R.layout.news_type4;
            default:
                return R.layout.news_type2;
        }
    }
}



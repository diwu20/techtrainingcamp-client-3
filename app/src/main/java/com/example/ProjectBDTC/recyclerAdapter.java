package com.example.ProjectBDTC;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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

        //存储四个ImageView，以供循环使用
        ImageView[] imageViews = {holder.newsImage1,holder.newsImage2,holder.newsImage3,holder.newsImage4};

        if (peice.getCover() != null || peice.getCovers() != null) {
            //根据图片数量的不同，调用getImage，并使用handler返回数据
            if(peice.getType() == 4) {
                //多个图片的情况下，使用循环依次get所需的图片
                String[] URLs = new String[4];
                for (int i = 0; i < 4; i++) {
                    URLs[i] = "http://cdn.skyletter.cn/" + peice.getCovers().get(i);
                }
                for (int i = 0; i < 4; i++) {
                    int finalI = i;
                    Handler handler = new Handler(new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            switch (msg.what) {
                                case 1://成功
                                    byte[] result = (byte[]) msg.obj;
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(result, 0, result.length);//利用BitmapFactory将数据转换成bitmap类型
                                    //使用ScaleBitmap进行裁剪和缩放
                                    ScaleBitmap cut = new ScaleBitmap();
                                    bitmap = cut.scaleBitmap(bitmap,50,30);
                                    Log.d("Bitmap", "Bitmap长度是" + result.length);
                                    //setImage
                                    imageViews[finalI].setImageBitmap(bitmap);
                                    return true;
                            }
                            return false;
                        }
                    });
                    getImage(URLs[i],handler);
                }
                //之前的测试方案，暂且保留
                /*holder.newsImage1.setImageResource(peice.getCoverId()[0]);
                holder.newsImage2.setImageResource(peice.getCoverId()[1]);
                holder.newsImage3.setImageResource(peice.getCoverId()[2]);
                holder.newsImage4.setImageResource(peice.getCoverId()[3]);*/
            } else {
                //单个图片的情况下，只需要发起一次网络请求
                //String URL = "http://192.168.1.106/" + peice.getCover();
                String URL = "http://cdn.skyletter.cn/" + peice.getCover();
                Handler handler = new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        switch (msg.what) {
                            case 1://成功
                                byte[] result = (byte[]) msg.obj;
                                Bitmap bitmap = BitmapFactory.decodeByteArray(result, 0, result.length);//利用BitmapFactory将数据转换成bitmap类型
                                Log.d("Bitmap", "Bitmap长度是" + result.length);
                                //根据不同新闻类型进行图片裁剪与缩放
                                ScaleBitmap cut = new ScaleBitmap();
                                if (peice.getType() == 3) {
                                    bitmap = cut.zoomBitMap(bitmap,0.2);
                                } else {
                                    bitmap = cut.zoomBitMap(bitmap,0.2);
                                }
                                holder.newsImage.setImageBitmap(bitmap);
                                return true;
                        }
                        return false;
                    }
                });
                getImage(URL,handler);
                //之前的测试方案，暂且保留
                 /*holder.newsImage.setImageResource(peice.getCoverId()[0]);*/
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

    private void getImage(String URL, Handler handler) {
        //实例化
        OkHttpClient client=new OkHttpClient();
        //传入图片网址
        final Request request = new Request.Builder().url(URL).build();
        //实例化一个call的对象
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("onFailure","fail a lot");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //声明
                Message message= handler.obtainMessage();
                if (response.isSuccessful()){
                    Log.e("YF", "onResponse: "+"YES" );
                    //设置成功的指令为1
                    message.what=1;
                    //带入图片的数据
                    message.obj=response.body().bytes();
                    //将指令和数据传出去
                    handler.sendMessage(message);
                }else{//失败
                    Log.e("YF", "onResponse: "+"NO" );
                    handler.sendEmptyMessage(0);
                }
            }
        });
    }

}



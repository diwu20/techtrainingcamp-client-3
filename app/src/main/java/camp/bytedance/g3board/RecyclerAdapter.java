package camp.bytedance.g3board;

import android.content.Context;
import android.content.Intent;
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
/**
 *
 * @author Bytedance Technical Camp, Client Group 3, 吴迪 & 王龙逊
 * @date 2020/11/29
 * @descripation 用于RecyclerView的内容展示和点击事件处理
 *
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    /**
     *@params nowActivity 当前的活动Context
     *@params bulletinList 储存传入的新闻列表
     * */
    static private Context nowActivity = null;
    private List<Bulletin> bulletinList;

    public RecyclerAdapter(List<Bulletin> objects, Context context) {
        /**传入新闻列表*/
        bulletinList = objects;
        nowActivity = context;
    }

    /**重写ViewHolder*/
    static class ViewHolder extends RecyclerView.ViewHolder {

        /**用于点击事件*/
        View bulletinView;
        TextView bulletinTitle;
        TextView bulletinAuthor;
        TextView bulletinTime;
        ImageView bulletinImage;
        ImageView bulletinImage1;
        ImageView bulletinImage2;
        ImageView bulletinImage3;
        ImageView bulletinImage4;

        //链接到layout上的定义
        public ViewHolder(View view) {
            super(view);
            bulletinView = view;
            bulletinTitle = (TextView) view.findViewById(R.id.bulletin_title);
            bulletinAuthor = (TextView) view.findViewById(R.id.bulletin_author);
            bulletinTime = (TextView) view.findViewById(R.id.bulletin_time);
            bulletinImage = (ImageView) view.findViewById(R.id.bulletin_image);
            bulletinImage1 = (ImageView) view.findViewById(R.id.bulletin_image1);
            bulletinImage2 = (ImageView) view.findViewById(R.id.bulletin_image2);
            bulletinImage3 = (ImageView) view.findViewById(R.id.bulletin_image3);
            bulletinImage4 = (ImageView) view.findViewById(R.id.bulletin_image4);
        }
    }

    @Override
    /**根据position返回对应type*/
    public int getItemViewType(int position) {
        return bulletinList.get(position).getType();
    }

    @Override
    /**重写onCreateViewHolder,添加点击事件，使用Toast测试*/
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /**使用getlayoutID方法创建合适的ViewHolder并返回*/
        View view = LayoutInflater.from(parent.getContext()).inflate(getlayoutId(viewType), parent, false);
        final ViewHolder holder = new ViewHolder(view);

        /**点击view的事件*/
        holder.bulletinView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAbsoluteAdapterPosition();
                Bulletin bulletinPeice = ActivityCollector.bulletinList.get(position);
                int index = ActivityCollector.bulletinList.indexOf(bulletinPeice);
                //判断是否登录
                if (ActivityCollector.token == null){
                    Toast.makeText(v.getContext(),"请先登录",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent("camp.bytedance.g3board.LOGIN_START");
                    //使用intent传递公告在list中的下标
                    intent.putExtra("bulletinPeice", index);
                    nowActivity.startActivity(intent);
                } else {
                    Intent intent = new Intent("camp.bytedance.g3board.NOTICE_START");
                    //使用intent传递公告在list中的下标
                    intent.putExtra("bulletinPeice", index);
                    nowActivity.startActivity(intent);
                }
            }
        });

        /**点击作者事件*/
        holder.bulletinAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAbsoluteAdapterPosition();
                Bulletin bulletinPeice = bulletinList.get(position);
                Intent intent = new Intent("camp.bytedance.g3board.CLASSIFY_START");
                //使用Intent传递作者名称
                intent.putExtra("bulletinPeice", bulletinPeice.getAuthor());
                nowActivity.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        /**获取单条新闻信息*/
        Bulletin peice = bulletinList.get(position);
        holder.bulletinTitle.setText(peice.getTitle());
        holder.bulletinAuthor.setText(peice.getAuthor());
        holder.bulletinTime.setText(peice.getTime());

        //存储四个ImageView，以供循环使用
        ImageView[] imageViews = {holder.bulletinImage1, holder.bulletinImage2, holder.bulletinImage3, holder.bulletinImage4};

        if (peice.getCover() != null || peice.getCovers() != null) {
            //根据图片数量的不同，调用getImage，并使用handler返回数据
            if (peice.getType() == 4) {
                //多个图片的情况下，使用循环依次get所需的图片
                String[] URLs = new String[4];
                //用于存入Bulletin对象的Bitmap数组
                Bitmap[] bm = new Bitmap[4];
                for (int i = 0; i < 4; i++) {
                    URLs[i] = "http://cdn.skyletter.cn/" + peice.getCovers().get(i);
                }
                if (peice.getBitmap()==null) {
                    for (int i = 0; i < 4; i++) {
                        int finalI = i;
                        Bitmap[] finalBm = bm;
                        Handler handler = new Handler(new Handler.Callback() {
                            @Override
                            public boolean handleMessage(Message msg) {
                                switch (msg.what) {
                                    case 1://成功
                                        byte[] result = (byte[]) msg.obj;
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(result, 0, result.length);//利用BitmapFactory将数据转换成bitmap类型
                                        //使用ScaleBitmap进行裁剪和缩放
                                        bitmap = ScaleBitmap.scaleBitmap(bitmap, 100, 60);
                                        Log.d("Bitmap", "Bitmap长度是" + result.length);
                                        finalBm[finalI] = bitmap;
                                        //setImage
                                        imageViews[finalI].setImageBitmap(bitmap);
                                        return true;
                                }
                                return false;
                            }
                        });
                        getImage(URLs[i], handler);
                    }
                    peice.setBitmap(bm);
                } else {
                    bm = peice.getBitmap();
                    for (int i = 0; i < 4; i++) {
                        imageViews[i].setImageBitmap(bm[i]);
                    }
                }
            } else {
                /**单个图片的情况下，只需要发起一次网络请求*/
                //String URL = "http://192.168.1.106/" + peice.getCover();
                String Url = "http://cdn.skyletter.cn/" + peice.getCover();
                Bitmap[] bm = new Bitmap[1];
                if (peice.getBitmap() == null) {
                    Bitmap[] finalBm = bm;
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
                                        bitmap = cut.zoomBitMap(bitmap, 0.4);
                                    } else {
                                        bitmap = cut.zoomBitMap(bitmap, 0.4);
                                    }
                                    holder.bulletinImage.setImageBitmap(bitmap);
                                    finalBm[0] = bitmap;
                                    return true;
                            }
                            return false;
                        }
                    });
                    getImage(Url, handler);
                    peice.setBitmap(bm);
                } else {
                    bm = peice.getBitmap();
                    holder.bulletinImage.setImageBitmap(bm[0]);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return bulletinList.size();
    }

    /**辅助方法，根据type值，返回对应的布局ID*/
    private int getlayoutId(int type) {
        switch (type) {
            case 0:
                return R.layout.bulletin_type0;
            case 1:
                return R.layout.bulletin_type1;
            case 2:
                return R.layout.bulletin_type2;
            case 3:
                return R.layout.bulletin_type3;
            case 4:
                return R.layout.bulletin_type4;
            default:
                return R.layout.bulletin_type2;
        }
    }

    private void getImage(String URL, Handler handler) {

        OkHttpClient client = new OkHttpClient();
        //传入图片网址
        final Request request = new Request.Builder().url(URL).build();
        //实例化一个call的对象
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("onFailure", "fail a lot");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //声明
                Message message = handler.obtainMessage();
                if (response.isSuccessful()) {
                    Log.e("YF", "onResponse: " + "YES");
                    //设置成功的指令为1
                    message.what = 1;
                    //带入图片的数据
                    message.obj = response.body().bytes();
                    //将指令和数据传出去
                    handler.sendMessage(message);
                } else {//失败
                    Log.e("YF", "onResponse: " + "NO");
                    handler.sendEmptyMessage(0);
                }
            }
        });
    }
}



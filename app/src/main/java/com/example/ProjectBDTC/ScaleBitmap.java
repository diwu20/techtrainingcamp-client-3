package com.example.ProjectBDTC;

import android.graphics.Bitmap;
import android.util.Log;

public class ScaleBitmap {
    //Bitmap裁剪缩放方法,先按比例裁剪，再缩放到指定尺寸
    public static Bitmap scaleBitmap(Bitmap bitmap,float w,float h){
        float width = bitmap.getWidth();
        float height = bitmap.getHeight();
        float x = 0,y = 0,scaleWidth = width,scaleHeight = height;
        Bitmap newbmp;
        //Log.e("gacmy","width:"+width+" height:"+height);
        if(w > h){//比例宽度大于高度的情况
            float scale = w/h;
            float tempH = width/scale;
            if(height > tempH){//
                x = 0;
                y=(height-tempH)/2;
                scaleWidth = width;
                scaleHeight = tempH;
            }else{
                scaleWidth = height*scale;
                x = (width - scaleWidth)/2;
                y= 0;
            }


            Log.e("gacmy","scale:"+scale+" scaleWidth:"+scaleWidth+" scaleHeight:"+scaleHeight);
        }else if(w < h){//比例宽度小于高度的情况
            float scale = h/w;
            float tempW = height/scale;
            if(width > tempW){
                y = 0;
                x = (width -tempW)/2;
                scaleWidth = tempW;
                scaleHeight = height;
            }else{
                scaleHeight = width*scale;
                y = (height - scaleHeight)/2;
                x = 0;
                scaleWidth = width;
            }

        }else{//比例宽高相等的情况
            if(width > height){
                x= (width-height)/2;
                y = 0;
                scaleHeight = height;
                scaleWidth = height;
            }else {
                y=(height - width)/2;
                x = 0;
                scaleHeight = width;
                scaleWidth = width;
            }
        }
        try {
            newbmp = Bitmap.createBitmap(bitmap, (int) x, (int) y, (int) scaleWidth, (int) scaleHeight, null, false);// createBitmap()方法中定义的参数x+width要小于或等于bitmap.getWidth()，y+height要小于或等于bitmap.getHeight()
            //bitmap.recycle();
            //缩放
            newbmp = Bitmap.createScaledBitmap(newbmp, (int) w, (int) h, true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return newbmp;
    }

    //单纯进行缩放，不进行裁剪
    public Bitmap zoomBitMap(Bitmap bitmap, double ratio) {
        float width = bitmap.getWidth();
        float height = bitmap.getHeight();
        Log.d("newbmp","仅缩放");
        return Bitmap.createScaledBitmap(bitmap, (int) (width * ratio), (int) (height *ratio), true);
    }
}

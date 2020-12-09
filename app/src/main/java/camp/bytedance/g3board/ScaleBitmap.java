package camp.bytedance.g3board;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * @author bytedance Technical Camp Group3 吴迪 & 王龙逊
 * @date 2020.11.28
 *
 * ScaleBitmap工具类，包含三个方法，分别是：
 * 1.scaleBitmap 按照输入的长宽计算比例，对Bitmap进行适当的裁剪和缩放
 * 2.zoomBitMap 按照输入的缩放比例ratio对Bitmap进行缩放
 * 3.zoomWidthBitMap 按照输入的目标宽度值，对Bitmap进行等比例缩放
 * */

public class ScaleBitmap {

    public static Bitmap scaleBitmap(Bitmap bitmap,float w,float h){
        float width = bitmap.getWidth();
        float height = bitmap.getHeight();
        float x = 0,y = 0,scaleWidth = width,scaleHeight = height;
        Bitmap newbmp;

        /**比例宽度大于高度的情况*/
        if(w > h){
            float scale = w/h;
            float tempH = width/scale;
            if(height > tempH){
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

        }else if(w < h){
            /**比例宽度小于高度的情况*/
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

        }else{
            /**比例宽高相等的情况*/
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
            /**createBitmap()方法中定义的参数x+width要小于或等于bitmap.getWidth()，y+height要小于或等于bitmap.getHeight()*/
            newbmp = Bitmap.createBitmap(bitmap, (int) x, (int) y, (int) scaleWidth, (int) scaleHeight, null, false);
            //缩放
            newbmp = Bitmap.createScaledBitmap(newbmp, (int) w, (int) h, true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return newbmp;
    }

    public Bitmap zoomBitMap(Bitmap bitmap, double ratio) {
        float width = bitmap.getWidth();
        float height = bitmap.getHeight();
        Log.d("newbmp","仅缩放");
        return Bitmap.createScaledBitmap(bitmap, (int) (width * ratio), (int) (height *ratio), true);
    }

    public Bitmap zoomWidthBitMap(Bitmap bitmap, int finalWidth) {
        float width = bitmap.getWidth();
        float height = bitmap.getHeight();
        Log.d("newbmp","仅缩放");
        return Bitmap.createScaledBitmap(bitmap, (int) width, (int) (height * finalWidth / width), true);
    }
}

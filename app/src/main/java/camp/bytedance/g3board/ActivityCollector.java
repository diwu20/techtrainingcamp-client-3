package camp.bytedance.g3board;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;

import camp.bytedance.g3board.R;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Bytedance Technical Camp, Client Group 3, 吴迪 & 王龙逊
 * @date 2020/11/29
 * @descripation 活动管理器，用于记录当前存在的活动，执行全体的退出命令，以及存储各活动会使用的公共变量
 *
 */

public class ActivityCollector {

    //用户信息
    public static String username;
    public static String token;

    //定义SharedPreferences，用于缓存信息
    private static SharedPreferences tokenSP;

    //背景颜色存储
    public static int bgColor;

    //公告板列表展示顺序
    public static int order = 0;


    public static List<Activity> activityList = new ArrayList<>();

    public static void addActivity(Activity activity){
        activityList.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activityList.remove(activity);
    }

    /**点击退出程序按钮时执行的方法*/
    public static void finishAll(){
        for(Activity activity:activityList){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
        activityList.clear();
    }

    /**对当前的username和token进行缓存*/
    public static void cacheToken(Context context) {
        if (tokenSP == null) {
            tokenSP = context.getSharedPreferences("cache", Context.MODE_PRIVATE);
        }
        tokenSP.edit().putString("TOKEN", token).apply();
        tokenSP.edit().putString("USERNAME", username).apply();
    }

    /**从缓存中读取username和token*/
    public static void getCacheToken(Context context) {
        if (tokenSP == null) {
            tokenSP = context.getSharedPreferences("cache", Context.MODE_PRIVATE);
        }
        token = tokenSP.getString("TOKEN",null);
        username = tokenSP.getString("USERNAME",null);
    }

    /**清除缓存，退出登录时使用*/
    public static void clearCacheToken(Context context) {
        if (tokenSP == null) {
            tokenSP = context.getSharedPreferences("cache", Context.MODE_PRIVATE);
        }
        tokenSP.edit().clear().apply();
    }

    /**点击菜单中关于项时展示的弹窗*/
    public static void showAbout(Context context) {
        AlertDialog alertDialog1 = new AlertDialog.Builder(context)
                .setTitle("G3公告板")
                .setMessage("G3公告板 version 0.1.1\n作者：吴迪 & 王龙逊\n字节跳动技术训练营-客户端 Group3")
                .setIcon(R.mipmap.ic_launcher)
                .create();
        alertDialog1.show();
    }
}

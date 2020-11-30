package camp.bytedance.g3board;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Bytedance Technical Camp, Client Group 3, 吴迪 & 王龙逊
 * @date 2020/11/29
 * @descripation 活动管理器，用于记录当前存在的活动，执行全体的退出命令，以及存储各活动会使用的公共变量和公共方法
 *
 */

public class ActivityCollector {
    /**
     * @params username/token 全局使用的用户信息
     * @params tokenSP 用于缓存用于信息
     * @params bulletinList 公告列表
     * @params order 公告列表展示的顺序
     * @params dayNightTheme 用于指示当前主题
     * 0 -> 日间主题
     * 1 -> 夜间主题
     * @params readerBgColor 公告内容页的背景颜色
     * 0 -> 原始顺序
     * 1 -> 时间倒序
     * -1 -> 时间顺序
     * @params activityList 当前活动列表
     */

    public static String username;
    public static String token;
    private static SharedPreferences tokenSP;

    public static List<Bulletin> bulletinList;
    public static int order = 0;

    public static int dayNightTheme = 0;
    public static int readerBgColor = 0;

    public static boolean classifyActivityOn = false;


    public static List<Activity> activityList = new ArrayList<>();

    public static void addActivity(Activity activity){
        activityList.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activityList.remove(activity);
    }

    public static String getTopActivity(Activity activity) {
        ActivityManager am = (ActivityManager) activity.getBaseContext().getSystemService(Context.ACTIVITY_SERVICE);
        String topActivityName = am.getRunningTasks(1).get(0).topActivity.getShortClassName();
        return topActivityName;
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
                .setTitle("G3公告板 V0.9.9")
                .setMessage("作者：吴迪 & 王龙逊\n字节跳动技术训练营 客户端Group3")
                .setIcon(R.mipmap.icon_launcher)
                .create();
        alertDialog1.show();
    }


}

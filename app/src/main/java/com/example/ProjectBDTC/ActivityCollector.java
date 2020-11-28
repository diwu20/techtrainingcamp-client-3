package com.example.ProjectBDTC;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class ActivityCollector {
    //用户信息
    public static String username;
    public static String token;
    private static SharedPreferences tokenSP;
    //背景颜色存储
    public static int bgColor;
    public static int order = 0;


    public static List<Activity> activityList = new ArrayList<>();
    public static void addActivity(Activity activity){
        activityList.add(activity);
    }
    public static void removeActivity(Activity activity) {
        activityList.remove(activity);
    }
    public static void finishActivity(Activity activity) {
        activity.finish();
    }
    public static boolean ifHave(Activity activity) {
        return activityList.contains(activity);
    }

    public static void finishAll(){
        for(Activity activity:activityList){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
        activityList.clear();
    }

    public static void cacheToken(Context context) {
        if (tokenSP == null) {
            tokenSP = context.getSharedPreferences("cache", Context.MODE_PRIVATE);
        }
        tokenSP.edit().putString("TOKEN", token).apply();
        tokenSP.edit().putString("USERNAME", username).apply();
    }

    public static void getCacheToken(Context context) {
        if (tokenSP == null) {
            tokenSP = context.getSharedPreferences("cache", Context.MODE_PRIVATE);
        }
        token = tokenSP.getString("TOKEN",null);
        username = tokenSP.getString("USERNAME",null);
    }

    public static void clearCacheToken(Context context) {
        if (tokenSP == null) {
            tokenSP = context.getSharedPreferences("cache", Context.MODE_PRIVATE);
        }
        tokenSP.edit().clear().apply();
    }

    public static void showAbout(Context context) {
        AlertDialog alertDialog1 = new AlertDialog.Builder(context)
                .setTitle("G3公告板")//标题
                .setMessage("G3公告板 version 0.1.1\n作者：吴迪 & 王龙逊\n字节跳动技术训练营-客户端 Group3")//内容
                .setIcon(R.mipmap.ic_launcher)//图标
                .create();
        alertDialog1.show();
    }
}

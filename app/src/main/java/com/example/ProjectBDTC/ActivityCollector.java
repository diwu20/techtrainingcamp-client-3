package com.example.ProjectBDTC;

import android.app.Activity;
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
}

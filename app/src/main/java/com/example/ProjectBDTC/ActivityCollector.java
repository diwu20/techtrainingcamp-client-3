package com.example.ProjectBDTC;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

public class ActivityCollector {

    public static List<Activity> activityList = new ArrayList<>();
    public static String token;

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
}

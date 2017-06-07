package utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import java.util.List;

import application.MyApplicationClass;


/**
 * Created by haseeb on 9/4/16.
 */
public class CommonFinish {

    //    Broadcast reciever
//    ------------------------------------------------

    public static BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        public void onReceive(Context context, Intent intent) {
            ////System.out.println("inside reciewveerrrrr");
            String action = intent.getStringExtra("action");
            String activity_name = intent.getStringExtra("activityIndex");
            ////System.out.println("activity_name:" + activity_name);

            for (int j = 0; j < 26; j++) {
                ////System.out.println(ExternalFunctions.cContextArray[j]);
            }


            if (action.equals("close")) {

                if (activity_name.equals("ALL")) {
                    for (int i = 0; i < ExternalFunctions.cContextArray.length; i++) {
                        Context context2 = ExternalFunctions.cContextArray[i];
                        ////System.out.println("kkkkkkkkkkkkkkkk");

                        try {
//                            if(isApplicationSentToBackground(context2)){
                            if(context2 != null){
                                SharedPreferences settings = context.getSharedPreferences("AppFiniShSession",
                                        Context.MODE_PRIVATE);

                                SharedPreferences.Editor editor = settings.edit();
                                editor.putBoolean("FinishStatus",true);
                                editor.apply();
                                ////System.out.println("kkkkkkkkkkkkkkkkzzz");
                                ((Activity) context2).finish();
                            }
                        } catch (Exception e) {

                        }//                        }
                    }

                } else {

//                    if(!((Activity) MyApplicationClass.cContextArray[Integer.parseInt(activity_name)]).isDestroyed()) {
                try {
                    Context context1 = ExternalFunctions.cContextArray[Integer.parseInt(activity_name)];
//                    if(isApplicationSentToBackground(context1)){
                    if(context1 != null){
                        SharedPreferences settings = context.getSharedPreferences("AppFiniShSession",
                                Context.MODE_PRIVATE);

                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("FinishStatus",true);
                        editor.apply();
                        ////System.out.println("kkkkkkkkkkkkkkkkxxxx");
                        ((Activity) context1).finish();
                    }

                    ////System.out.println("jjjjjjjjjjjjjjjjjjjjjjjjjj");

                    }
                    catch (Exception e){

                    }
                    }
                }


        }
    };

    public static boolean isApplicationSentToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }

        return false;
    }

}

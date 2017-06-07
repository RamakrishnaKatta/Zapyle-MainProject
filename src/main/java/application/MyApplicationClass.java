package application;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.appvirality.android.AppviralityAPI;
import com.appvirality.android.ConversionEventListner;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.zapyle.zapyle.R;

import org.json.JSONObject;

import java.util.ArrayList;

import DataBase.DatabaseDB;
import activity.SplashScreen;
import io.branch.referral.Branch;


/**
 * Created by haseeb on 1/2/16.
 */
public class MyApplicationClass extends Application {
    //public static boolean sFirstRun = false;
    private static MyApplicationClass singleton;
    private Tracker mTracker;




    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

//    public static boolean fetchFirstRun() {
//        boolean old = sFirstRun;
//        sFirstRun = false;
//        return old;
//    }

    //--called when app process is created--

    @Override
    public void onCreate() {
        super.onCreate();
        // Automatic session tracking
       // sFirstRun = true;
        Branch.getAutoInstance(this);
        singleton = this;

        //Check for FriendReward, Listner will get called if there is any Friend Reward.
        AppviralityAPI.checkFriendReward(getApplicationContext(), new AppviralityAPI.FriendRewardListner() {
            @Override
            public void onFriendReward(JSONObject friendRewardListner) {
                try {
                    //Use the following code block
                    //to mark the friend reward as Distributed if the reward type is in-store credits
                    //to approve the dynamic coupon if the reward type is Dynamic Coupon.
                    //Example: You will call this method after rewarding the user with In-store credits(wallet balance).
					/*JSONObject acceptedList = new JSONObject();
					acceptedList.put("rewardid", friendRewardListner.getString("rewardid"));
					acceptedList.put("reward_type", friendRewardListner.getString("reward_type"));
					acceptedList.put("status", "Distribute");
					// add this call after accepting the reward to confirm.
					AppviralityAPI.acceptReward(new JSONObject().put("rewards", new JSONArray().put(acceptedList)));*/
                    Log.d("AppviralityAPI", "Friend Reward Details : " + friendRewardListner);
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });

        AppviralityAPI.onSuccessfullConversion(getApplicationContext(), new ConversionEventListner() {
            @Override
            public void onConversionEventSuccess(JSONObject conversionDetails) {
                Log.d("AppviralityAPI", "user SaveConversionEvent result : " + conversionDetails);
            }
        });

    }
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }



    public static MyApplicationClass getInstance() {
        return singleton;
    }

}
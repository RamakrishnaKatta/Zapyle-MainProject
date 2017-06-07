package application;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.clevertap.android.sdk.ActivityLifecycleCallback;
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
    //--called when app process is created--

    @Override
    public void onCreate() {
        ActivityLifecycleCallback.register(this);
        super.onCreate();
        // Automatic session tracking
       // sFirstRun = true;

        Branch.getAutoInstance(getApplicationContext());
        singleton = this;


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
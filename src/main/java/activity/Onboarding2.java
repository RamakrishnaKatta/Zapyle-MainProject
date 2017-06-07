package activity;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
//import com.//Appsee.//Appsee;
import com.appvirality.android.AppviralityAPI;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
//import com.uxcam.UXCam;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import AnimOnboard.ProgressBarAnimation;
import application.MyApplicationClass;
import network.ApiCommunication;
import network.ApiService;
import utils.CommonFinish;
import utils.CustomMessage;
import utils.ExternalFunctions;
import utils.FontUtils;
import utils.GetSharedValues;
import utils.Mixpanelutils;

/**
 * Created by haseeb on 8/1/16.
 */
public class Onboarding2 extends ActionBarActivity implements ApiCommunication{


    int []intarr=new int[100];
    int []intarr1=new int[100];
    int intc;
    int intf;
    int intcheck=0;
    int intalready=0;
    int intbtnvisindex=0;
    int intbtstartindex=0;

    private int CLICK_COUNT =0 ;
    LinearLayout lnBrand;
    ProgressBar seekingProgress;
    private int intRowsize;
    TextView Next,Showmore,Skip;
    String SCREEN_NAME = "BRANDTAGS";

    int start = 0;
    int end = 500;
    Boolean action;
    ProgressDialog mProgress;
    boolean blcheck=false;


    HashMap<String, String> h1 = new HashMap<String,String>();
    HashMap<String, String> h2 = new HashMap<String,String>();
    ArrayList<String> Brantaglist = new ArrayList<String>();
    ArrayList<String> Brantaglist1 = new ArrayList<String>();


    int intLoadtype = 0;
   // TextView tvmore;
    int intStart = 0, intEnd = 0;
    int intTest = 0, intcount = 0;
    int chekval=0;
    Date today;
    public  static boolean isskip=false;

    int intwidth, inthieght;
    LinearLayout ln[];
    TextView bt[];
    View view;
    boolean booltype = false;
    int intcheckval=0;
    ProgressBar ob2progressBar;

    MixpanelAPI mixpanel ;
    int intloadcount=0;
    public static String  activityname;
    boolean isExistingUser;
    String strappviraluserkey = "";
    Tracker mTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.brandtags);
        MyApplicationClass application = (MyApplicationClass) getApplication();
        mTracker = application.getDefaultTracker();
        //Appsee.start("4fb55809ab45411c909521bda720f8e6");
        //UXCam.startWithKey("1dfb25141864376");         overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        //////Thread.currentThread().setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
        ob2progressBar = (ProgressBar) findViewById(R.id.ob2progressBar);


        mProgress = new ProgressDialog(Onboarding2.this);
        mProgress.setCancelable(false);
        LocalBroadcastManager.getInstance(this).registerReceiver(CommonFinish.broadcastReceiver, new IntentFilter("Onboarding2"));
//        if(!MyApplicationClass.cContextArray.contains(getApplicationContext())) {
        ExternalFunctions.cContextArray[4] = Onboarding2.this;
//        }

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        View customView = getLayoutInflater().inflate(R.layout.onboarding2, null);
        getSupportActionBar().setCustomView(customView);
        Toolbar parent = (Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0, 0);
        view = findViewById(R.id.rlbrandtag);
        try{
            Bundle bundle=this.getIntent().getExtras();
            booltype =bundle.getBoolean("booltype");
            activityname=bundle.getString("activity");
        }catch (Exception e){
            activityname="SplashScreen";
            booltype=true;
        }
        mixpanel =  MixpanelAPI.getInstance(Onboarding2.this, getResources().getString(R.string.mixpanelToken));




        try {
            AppviralityAPI.getInstance(getApplicationContext(), new AppviralityAPI.UserInstance() {
                @Override
                public void onInstance(AppviralityAPI instance) {

                    boolean hasReferrer = instance.hasReferrer();
                    isExistingUser = AppviralityAPI.isExistingUser();
                    strappviraluserkey = instance.getUserKey().toString();

                    Log.i("AV isExisting User: ", "" + strappviraluserkey);
                    String refCode = AppviralityAPI.getFriendReferralCode();
                    Log.i("AV isExisting User: ", "" + refCode);


                }
            });

        } catch (Exception e) {

            ////System.out.println("Error" + e.getMessage());
        }
        AppviralityAPI.UserDetails.setInstance(getApplicationContext())
                .setUserEmail(GetSharedValues.getUseremail(this))
                .setUserName(GetSharedValues.getZapname(this))
                .setUseridInStore(String.valueOf(GetSharedValues.getuserId(this)))
                .isExistingUser(isExistingUser)
                .Update(new AppviralityAPI.UpdateUserDetailsListner() {
                    @Override
                    public void onSuccess(boolean isSuccess) {

                        ////System.out.println("Truer"+GetSharedValues.getUseremail(Onboarding2.this)+GetSharedValues.getZapname(Onboarding2.this));
                        Log.i("AppViralitySDK", "Update UserDetails Status : " + isSuccess);
                        setAppViralityUserKey();
                    }
                });


//        JSONObject superprop = new JSONObject();
//        try {
//            superprop.put("Event Name", "Tags selected");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        mixpanel.registerSuperProperties(superprop);

        GetBrandTags();
        FontUtils.setCustomFont(view, getAssets());


        Skip = (TextView) findViewById(R.id.skip1);
        if (booltype == false) {
            Skip.setVisibility(View.INVISIBLE);
        }
        Skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (booltype == true) {

                    isskip = true;
                    mProgress.setMessage("Loading ...");
                    mProgress.show();
                    if (h2.size() > 0) {
                        h2.clear();
                    }
                    DataToServer();


                }

            }
        });


        seekingProgress = (ProgressBar) findViewById(R.id.brandtags_progress);
        seekingProgress.setProgress(0);

        lnBrand = (LinearLayout) findViewById(R.id.linearBrand);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        Next = (TextView) findViewById(R.id.next1);
        Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isskip=false;
                DataToServer();
            }
        });

        if(booltype==false){
            Next.setText("DONE");
        }
        intwidth = metrics.widthPixels;
        inthieght = metrics.heightPixels;

    }



    public void setAppViralityUserKey() {
        final JSONObject userkey = new JSONObject();
        try {
            userkey.put("user_key", strappviraluserkey);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        ////System.out.println(userkey + "__Details");
        ApiService.getInstance(Onboarding2.this, 1).postData(Onboarding2.this, EnvConstants.APP_BASE_URL + "/user/appvirality_key/", userkey, SCREEN_NAME, "setuserkey");

    }

    public Object getKeyFromValue(Map hm, Object value) {
        for (Object o : hm.keySet()) {
            if (hm.get(o).equals(value)) {
                return o;
            }
        }
        return null;
    }

    public void addBrandTag(int instart, int count, String strItem) {
        for (int i = 1; i < count; i++) {
            h1.put(String.valueOf(instart * i), strItem + (intStart * i));
            Brantaglist.add(strItem + i);

        }


    }
    @Override
    public void onBackPressed() {
        if (activityname.equals("SplashScreen")){
        if (CLICK_COUNT < 1) {
            CustomMessage.getInstance().CustomMessage(this, "Press again to close the app");
            CLICK_COUNT=CLICK_COUNT+1;
        } else {
            mixpanel =  MixpanelAPI.getInstance(Onboarding2.this, getResources().getString(R.string.mixpanelToken));

            today = new Date();
            DateFormat dfclosetime = new SimpleDateFormat("hh:mm:ss aa", Locale.ENGLISH);
            dfclosetime.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
            String strclosetime = dfclosetime.format(today);
            Mixpanelutils mx=new Mixpanelutils();                SharedPreferences settings = getSharedPreferences("MixPanelSession",
                    Context.MODE_PRIVATE);
            int count = settings.getInt("scrollCount", 0);
            int productviewCount = settings.getInt("productviewCount", 0);
            JSONObject prop = new JSONObject();
            try {
                prop.put("Time taken", mx.getTimeDiff(SplashScreen.starttime, strclosetime));
                prop.put("Event Name", "Close app");
                mixpanel.track("Close app", prop);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("scrollCount", 0);
                editor.putInt("productviewCount", 0);
                editor.apply();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
            super.onDestroy();
            finish();           }        }
    else{
        if (booltype) {
            android.os.Process.killProcess(android.os.Process.myPid());            }
        else {
            Intent on2 = new Intent(Onboarding2.this, Mypreference.class);
            on2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(on2);
            finish();
        }
    }
    }

    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }







// Draw progress bar function
//    ------------------------------------------------------------

    public void DrawProgress(int arraySize) {

        if (arraySize >= 5) {
            if(arraySize == 5) {
                if(action) {
                    ProgressBarAnimation anim = new ProgressBarAnimation(seekingProgress, 80, 100);
                    anim.setDuration(600);
                    seekingProgress.startAnimation(anim);
                    seekingProgress.setProgress(100);
                }
                else {
                    seekingProgress.setProgress(100);
                }
            }
            else {
                seekingProgress.setProgress(100);
            }

        } else {
            if(action) {
                int startStatus = (arraySize - 1) * 20;
                int seekStatus = arraySize * 20;
                ProgressBarAnimation anim = new ProgressBarAnimation(seekingProgress, startStatus, seekStatus);
                anim.setDuration(600);
                seekingProgress.startAnimation(anim);
                seekingProgress.setProgress(seekStatus);
            }
            else {
                int startStatus = (arraySize + 1) * 20;
                int seekStatus = arraySize * 20;
                ProgressBarAnimation anim = new ProgressBarAnimation(seekingProgress, startStatus, seekStatus);
                anim.setDuration(600);
                seekingProgress.startAnimation(anim);
                seekingProgress.setProgress(seekStatus);            }
        }
    }




//    Main functions
//    --------------------------------------------------------------


    private void GetBrandTags() {
        mProgress.setMessage("Loading ...");
        mProgress.show();
        if(booltype==true){
            //ApiService.getInstance(Onboarding2.this, 1).getData(Onboarding2.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/onboarding/2/", "brandtags");
            ApiService.getInstance(Onboarding2.this, 1).getData(Onboarding2.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/onboarding/2/?start=" + start + "&end=" + end, "morebrandtags");
        }
        else{
            ApiService.getInstance(Onboarding2.this, 1).getData(Onboarding2.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/mybrandtags/", "selectedbrandtags");
        }


    }

    private void GetMore() {
        end=200;
        //////System.out.println("more"+EnvConstants.APP_BASE_URL + "/onboarding/2/?start=" + start + "&end=" + end);
        ApiService.getInstance(Onboarding2.this, 1).getData(Onboarding2.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/onboarding/2/?start=" + start + "&end=" + end, "morebrandtags");
    }

    private void DataToServer() {
        Next.setEnabled(false);
//        seekingProgress.setVisibility(View.VISIBLE);
        JSONArray data = new JSONArray();
        for ( String key : h2.keySet() ) {
            data.put(key);
        }
        //////System.out.println("data for sending__"+data);
        final JSONObject onboarding2Object = new JSONObject();
        try {
            onboarding2Object.put("btags_selected", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiService.getInstance(Onboarding2.this, 1).postData(Onboarding2.this, EnvConstants.APP_BASE_URL + "/onboarding/2/", onboarding2Object, SCREEN_NAME, "onboarding2");

    }


//    Responses from server
//    --------------------------------------------------------------

    @Override
    public void onResponseCallback(JSONObject response, String flag) {
        if (flag.equals("onboarding2")) {
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            //////System.out.println("response___" + resp);
            if(resp != null){
                try {
                    String status = resp.getString("status");
                    if(status.equals("success")){
                        mProgress.dismiss();
                        Next.setEnabled(true);
                        JSONObject propTagselected = new JSONObject();
                        try {
                            propTagselected.put("Number of tags",h2.size() );
                            propTagselected.put("Load more count",intloadcount );
                            propTagselected.put("Event Name", "Tags selected");
                            mixpanel.track("Tags selected", propTagselected);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(booltype==true){

                            Intent onboarding3 = new Intent(Onboarding2.this, Onboarding3.class);
                            onboarding3.putExtra("booltype",true);
                            onboarding3.putExtra("activity","Onboarding2");
                            startActivity(onboarding3);
                            finish();

                        }
                        else{

                            Intent myprefe = new Intent(Onboarding2.this,Mypreference.class);
                            startActivity(myprefe);
                            finish();

                        }


                    }
                    else {
                        Next.setEnabled(true);
                        mProgress.dismiss();
                        Intent mydialog = new Intent(Onboarding2.this, AlertActivity.class);
                        //int imgid=R.drawable.alertoop;
                        String strmessage="OOPS!";
                       // mydialog.putExtra("imgid", imgid);
                        mydialog.putExtra("Message",strmessage);
                        mydialog.putExtra("Buttontext"," RETRY ");
                        mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                        if(booltype==true){
                            mydialog.putExtra("activity", "Onboarding2");
                        }
                        else{
                            mydialog.putExtra("activity", "Mypreference");
                        }

                        startActivity(mydialog);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mProgress.dismiss();
                    Intent mydialog = new Intent(Onboarding2.this, AlertActivity.class);
                    //int imgid=R.drawable.alertoop;
                    String strmessage="OOPS!";
                   // mydialog.putExtra("imgid", imgid);
                    mydialog.putExtra("Message",strmessage);
                    mydialog.putExtra("Buttontext"," RETRY ");
                    mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                    if(booltype==true){
                        mydialog.putExtra("activity", "Onboarding2");
                    }
                    else{
                        mydialog.putExtra("activity", "Mypreference");
                    }

                    startActivity(mydialog);
                    finish();
                }
            }


        } else if (flag.equals("brandtags")) {
            //////System.out.println("brandtags__" + response);
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                try {
                    String status = resp.getString("status");

                    if (status.equals("success")) {
                        mProgress.dismiss();
                        JSONArray data = resp.getJSONArray("data");
                        //////System.out.println("inside success__" + data);
                        if (h1.size() > 0) {
                            h1.clear();
                        }
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject tag = data.getJSONObject(i);
                            String id = String.valueOf(tag.getInt("id"));
                            String brand = tag.getString("tag");
                            h1.put(id,brand.toUpperCase());
                        }

                        ln = new LinearLayout[h1.size()];
                        bt=new TextView[h1.size()];
                        for (Map.Entry<String,String> entry : h1.entrySet()) {
                            System.out.printf("%s -> %s%n", entry.getKey(), entry.getValue());
                            Brantaglist.add(entry.getValue());

                        }
                        //tvmore.setVisibility(View.VISIBLE);
                        intRowsize = 0;
                        intc=0;
                        start=0;
                        end=0;
                        intcheck=0;
                        intalready=0;
                        intbtnvisindex=0;
                        intbtstartindex=0;
                        for (int i = 0; i < Brantaglist.size(); i++) {

                            intRowsize = intRowsize +Brantaglist.get(i).toString().length();
                            if (intRowsize < (getResources().getDisplayMetrics().density*10)+3 || intcount<2){
                                //////System.out.println("inside " + i);
                                if (intTest == 1) {
                                    if (i == 0) {
                                        intStart = i;
                                        intTest = 0;
                                    } else {
                                        intStart = i;
                                        intTest = 0;
                                    }
                                }
                                if (intcount>=3){
                                    intcount=3;
                                    intRowsize=((int)(getResources().getDisplayMetrics().density)*10)+6;
                                }
                                else {
                                    intcount = intcount + 1;
                                }

                            } else {

                                intEnd = i;
                                intRowsize = 0;
                                intTest = 1;
                                intcount = 0;
                                intarr[intc]=intStart;
                                intarr1[intc]=i;
                                intc=intc+1;
                                if (i<Brantaglist.size()){
                                    i = i - 1;

                                }


                            }


                        }
                        if (intRowsize > 0) {
                            intRowsize = 0;
                            intarr[intc]=intStart;
                            intarr1[intc]=intStart + intcount ;
                            intc=intc+1;

                        }


                        for (int k = 0; k < intc; k++) {

                            start = intarr[k];
                            end = intarr1[k];
                            intcheck=0;
                            ln[start] = new LinearLayout(Onboarding2.this);
                            ln[start].setOrientation(LinearLayout.HORIZONTAL);
                            ln[start].setVisibility(View.INVISIBLE);
                            //   ln[start].setBackgroundColor(Color.GREEN);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
                            params.weight = 1.0f;
                            params.gravity = Gravity.CENTER;
                            params.setMargins(5, 5, 5, 5);
                            ln[start].setLayoutParams(params);
                            //////System.out.println(start + "=j=" + end);
                            for (int i = start; i < end; i++) {
                                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                                params1.setMargins(10, 15, 10, 10);
                                params1.weight = 1;
                                //////System.out.println("inside for====" + i);
                                bt[i] = new TextView(Onboarding2.this);

                                final TextView tvindex = new TextView(Onboarding2.this);

                                bt[i].setLayoutParams(params1);

                                bt[i].setBackgroundColor(Color.TRANSPARENT);
                                bt[i].setTextColor(Color.WHITE);
                                bt[i].setBackgroundResource(R.drawable.shape_boarder);

                                bt[i].setTextSize(11);
                                bt[i].setText(Brantaglist.get(i) + " +");
                                bt[i].setTag(getKeyFromValue(h1, Brantaglist.get(i)));
                                bt[i].setPadding(15, 15, 15, 15);
                                ln[start].addView(bt[i]);
                                ln[start].addView(tvindex);
                                //////System.out.println(i + "=j" + Brantaglist.get(i));
                                tvindex.setText(String.valueOf(i));

                                tvindex.setVisibility(View.GONE);

                                bt[i].setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        int i = Integer.parseInt(tvindex.getText().toString());

                                        int colorId = bt[i].getCurrentTextColor();


                                        if (colorId == Color.WHITE) {
                                            bt[i].setBackgroundResource(R.drawable.clickshape_boarder);

                                            bt[i].setTextColor(Color.BLACK);
                                            action = true;

                                            h2.put(bt[i].getTag().toString(), h1.get(bt[i].getTag().toString()));
                                            Brantaglist.remove(bt[i].getText().toString().replace(" +", " ").toString().trim());
                                            Brantaglist1.add(bt[i].getText().toString().replace(" +", " ").toString().trim());

                                            h1.remove(bt[i].getTag().toString());


                                            DrawProgress(h2.size());

                                        } else {
                                            action = false;

                                            bt[i].setBackgroundColor(Color.TRANSPARENT);
                                            bt[i].setBackgroundResource(R.drawable.shape_boarder);
                                            bt[i].setTextColor(Color.WHITE);
                                            Brantaglist.add(bt[i].getText().toString().replace(" +", " ").toString().trim());
                                            Brantaglist1.remove(bt[i].getText().toString().replace(" +", " ").toString().trim());
                                            h1.put(bt[i].getTag().toString(), h2.get(bt[i].getTag().toString()));
                                            h2.remove(bt[i].getTag().toString());

                                            DrawProgress(h2.size());

                                        }


                                    }
                                });

                            }

                            lnBrand.addView(ln[start]);
                            Animation fadeIn = new AlphaAnimation(0, 1);
                            fadeIn.setInterpolator(new AccelerateInterpolator()); //add this
                            fadeIn.setDuration(500);
                            ln[start].setVisibility(View.VISIBLE);
                            AnimationSet animation = new AnimationSet(false); //change to false
                            animation.addAnimation(fadeIn);
                            ln[start].setAnimation(fadeIn);
                            //////System.out.println("inside timerxxxx" + intEnd);


                        }
                        if (Brantaglist1.size()>0){
                            Brantaglist.removeAll(Brantaglist1);
                        }
//                        --------------------------------
                        start = h1.size();

                    } else {
                        mProgress.dismiss();
                        Intent mydialog = new Intent(Onboarding2.this, AlertActivity.class);
                        //int imgid=R.drawable.alertoop;
                        String strmessage="OOPS!";
                       // mydialog.putExtra("imgid", imgid);
                        mydialog.putExtra("Message",strmessage);
                        mydialog.putExtra("Buttontext"," RETRY ");
                        mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                        if(booltype==true){
                            mydialog.putExtra("activity", "Onboarding2");
                        }
                        else{
                            mydialog.putExtra("activity", "Mypreference");
                        }

                        startActivity(mydialog);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mProgress.dismiss();
                    Intent mydialog = new Intent(Onboarding2.this, AlertActivity.class);
                    //int imgid=R.drawable.alertoop;
                    String strmessage="OOPS!";
                   // mydialog.putExtra("imgid", imgid);
                    mydialog.putExtra("Message",strmessage);
                    mydialog.putExtra("Buttontext"," RETRY ");
                    mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                    if(booltype==true){
                        mydialog.putExtra("activity", "Onboarding2");
                    }
                    else{
                        mydialog.putExtra("activity", "Mypreference");
                    }

                    startActivity(mydialog);
                    finish();
                }
            }
        }
        else if (flag.equals("selectedbrandtags")) {
            h1.clear();
            //////System.out.println("selectedbrandtags__" + response);
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                //////System.out.println("inside not null check");
                try {
                    String status = resp.getString("status");
                    //////System.out.println("inside status check___" + status);

                    if (status.equals("success")) {
                        mProgress.dismiss();
                        JSONArray data = resp.getJSONArray("data");
                        //////System.out.println("inside selected tag" + data.length());

                        if(data.length()>0) {
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject tag = data.getJSONObject(i);
                                String id = String.valueOf(tag.getInt("id"));
                                String brand = tag.getString("tag");
                                //////System.out.println(brand + "__" + id);
                                h1.put(id, brand.toUpperCase());
                                h2.put(id, brand.toUpperCase());
                                Brantaglist1.add(brand.trim().toUpperCase());
                                Brantaglist.add(brand.trim().toUpperCase());
                            }

                            end = start + (20 - h2.size());

                            GetMore();

                            if (h2.size() >= 5) {
                                seekingProgress.setProgress(100);
                            } else {
                                seekingProgress.setProgress(h2.size() * 20);
                            }

                            FontUtils.setCustomFont(view, getAssets());
                        }
                        else{
                            start=1;
                            end=20;
                            GetMore();

                        }
//                        ---------------------------------

                    } else {
                        mProgress.dismiss();
                        Intent mydialog = new Intent(Onboarding2.this, AlertActivity.class);
                        //int imgid=R.drawable.alertoop;
                        String strmessage="OOPS!";
                       // mydialog.putExtra("imgid", imgid);
                        mydialog.putExtra("Message",strmessage);
                        mydialog.putExtra("Buttontext"," RETRY ");
                        mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                        if(booltype==true){
                            mydialog.putExtra("activity", "Onboarding2");
                        }
                        else{
                            mydialog.putExtra("activity", "Mypreference");
                        }

                        startActivity(mydialog);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mProgress.dismiss();
                    Intent mydialog = new Intent(Onboarding2.this, AlertActivity.class);
                    //int imgid=R.drawable.alertoop;
                    String strmessage="OOPS!";
                   // mydialog.putExtra("imgid", imgid);
                    mydialog.putExtra("Message",strmessage);
                    mydialog.putExtra("Buttontext"," RETRY ");
                    mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                    if(booltype==true){
                        mydialog.putExtra("activity", "Onboarding2");
                    }
                    else{
                        mydialog.putExtra("activity", "Mypreference");
                    }

                    startActivity(mydialog);
                    finish();
                }
            }
        }
        else if(flag.equals("morebrandtags")){
            //////System.out.println("morebrandtags__" + response);
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if(resp != null){
                try {
                    String status = resp.getString("status");
                    final String next=resp.getString("next");
                    if(status.equals("success")){
                        lnBrand.removeAllViews();
                        mProgress.dismiss();
                        JSONArray data = resp.getJSONArray("data");

                        if(data.length()>0){

                            blcheck=true;

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject tag = data.getJSONObject(i);
                                String id = String.valueOf(tag.getInt("id"));
                                String brand = tag.getString("tag");
                                //////System.out.println(brand + "__" + id);
                                Brantaglist.add(brand.trim().toUpperCase());
                                h1.put(id,brand.toUpperCase());
                            }

                            ln = new LinearLayout[h1.size()];
                            bt=new TextView[h1.size()];


                            // tvmore.setVisibility(View.GONE);


                            intLoadtype = 1;
                            intRowsize = 0;
                            intStart = 0;

                            ln = new LinearLayout[Brantaglist.size()];
                            bt=new TextView[Brantaglist.size()];

                            intc=0;
                            start=0;
                            end=0;
                            intcheck=0;
                            intalready=0;
                            intbtnvisindex=0;
                            intbtstartindex=0;
                            for (int i = 0; i < Brantaglist.size(); i++) {


                                intRowsize = intRowsize +Brantaglist.get(i).toString().length();
                                if (intRowsize < (getResources().getDisplayMetrics().density*10)+3 || intcount<2){
                                    //////System.out.println("inside " + i);
                                    if (intTest == 1) {
                                        if (i == 0) {
                                            intStart = i;
                                            intTest = 0;
                                        } else {
                                            intStart = i;
                                            intTest = 0;
                                        }
                                    }
                                    if (intcount>=3){
                                        intcount=3;
                                        intRowsize=((int)(getResources().getDisplayMetrics().density)*10)+6;
                                    }
                                    else {
                                        intcount = intcount + 1;
                                    }


                                } else {
                                    //////System.out.println("inside main" + intStart + "=" + i);
                                    intEnd = i;
                                    intRowsize = 0;
                                    intTest = 1;
                                    intcount = 0;
                                    intarr[intc]=intStart;
                                    intarr1[intc]=i;
                                    intc=intc+1;

                                    if (i<Brantaglist.size()){

                                        i = i - 1;

                                    }

                                }


                            }
                            if (intRowsize > 0) {
                                intRowsize = 0;
                                intarr[intc]=intStart;
                                intarr1[intc]=intStart + intcount ;
                                intc=intc+1;

                            }

                            for (int k = 0; k < intc; k++) {

                                start = intarr[k];
                                end = intarr1[k];
                                intcheck=0;
                                ln[start] = new LinearLayout(Onboarding2.this);
                                ln[start].setOrientation(LinearLayout.HORIZONTAL);
                                ln[start].setVisibility(View.INVISIBLE);
                                //   ln[start].setBackgroundColor(Color.GREEN);
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
                                params.weight = 1.0f;
                                params.gravity = Gravity.CENTER;
                                params.setMargins(5, 5, 5, 5);
                                ln[start].setLayoutParams(params);
                                //////System.out.println(start + "=j=" + end);
                                for (int i = start; i < end; i++) {
                                    LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                                    params1.setMargins(10, 15, 10, 10);
                                    params1.weight = 1;
                                    //////System.out.println(h1.size()+"llll1====" +Brantaglist1.size());
                                    bt[i] = new TextView(Onboarding2.this);

                                    final TextView tvindex = new TextView(Onboarding2.this);

                                    bt[i].setLayoutParams(params1);
                                    if (Brantaglist1.size()>0) {
                                        if (intLoadtype == 1) {

                                            bt[i].setBackgroundResource(R.drawable.clickshape_boarder);
                                            bt[i].setTextColor(Color.BLACK);
                                            //////System.out.println("RRR" + i);

                                            if (i > Brantaglist1.size() - 1) {
                                                intcheck = 1;
                                                intbtstartindex = i;
                                                //////System.out.println("INSIDE selected" + i);

                                                bt[i].setBackgroundColor(Color.TRANSPARENT);
                                                bt[i].setBackgroundResource(R.drawable.shape_boarder);
                                                bt[i].setTextColor(Color.WHITE);


                                                intLoadtype = 0;
                                            } else if ((i == Brantaglist1.size()-1 )){

                                                //////System.out.println("kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk"+blcheck);

                                                final int s = start;

                                                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, inthieght);
                                                intf = ln[start].getLayoutParams().height;
                                                ln[start].setLayoutParams(params2);
                                                ValueAnimator va;
                                                if (start == 0) {
                                                    va = ValueAnimator.ofInt(inthieght, LinearLayout.LayoutParams.WRAP_CONTENT);
                                                    bt[0].setVisibility(View.GONE);

                                                } else {
                                                    va = ValueAnimator.ofInt(inthieght, intf+bt[0].getHeight());
                                                }

                                                va.setDuration(1500);
                                                va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                                    public void onAnimationUpdate(ValueAnimator animation) {
                                                        Integer value = (Integer) animation.getAnimatedValue();
                                                        ln[s].getLayoutParams().height = value.intValue();
                                                        ln[s].requestLayout();
                                                    }


                                                });
                                                va.addListener(new AnimatorListenerAdapter() {
                                                    @Override
                                                    public void onAnimationEnd(Animator animation) {
                                                        //////System.out.println("btnstart" + intbtstartindex + "btnend" + intbtnvisindex);

                                                        for (int j = intbtstartindex; j <= intbtnvisindex; j++) {

                                                            bt[j].setVisibility(View.VISIBLE);

                                                        }
                                                        if (s == 0) {

                                                            bt[0].setVisibility(View.VISIBLE);

                                                        }
                                                        intcheckval = 0;


                                                    }

                                                    @Override
                                                    public void onAnimationStart(Animator animation) {

                                                    }
                                                });

                                                va.start();

                                            }


                                        }
                                        else {

                                            bt[i].setBackgroundColor(Color.TRANSPARENT);
                                            bt[i].setTextColor(Color.WHITE);
                                            bt[i].setBackgroundResource(R.drawable.shape_boarder);


                                        }
                                        if (intcheck == 1) {
                                            bt[i].setVisibility(View.GONE);
                                            intbtnvisindex = i;
                                        }
                                    }else {

                                        bt[i].setBackgroundColor(Color.TRANSPARENT);
                                        bt[i].setTextColor(Color.WHITE);
                                        bt[i].setBackgroundResource(R.drawable.shape_boarder);


                                    }

                                    bt[i].setTextSize(11);
                                    bt[i].setText(Brantaglist.get(i) + " +");
                                    bt[i].setTag(getKeyFromValue(h1, Brantaglist.get(i)));
                                    bt[i].setPadding(15, 15, 15, 15);
                                    ln[start].addView(bt[i]);
                                    ln[start].addView(tvindex);
                                    //////System.out.println(i + "=j" + Brantaglist.get(i));
                                    tvindex.setText(String.valueOf(i));

                                    tvindex.setVisibility(View.GONE);

                                    bt[i].setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            int i = Integer.parseInt(tvindex.getText().toString());

                                            int colorId = bt[i].getCurrentTextColor();


                                            if (colorId == Color.WHITE) {
                                                bt[i].setBackgroundResource(R.drawable.clickshape_boarder);

                                                bt[i].setTextColor(Color.BLACK);
                                                action = true;

                                                h2.put(bt[i].getTag().toString(), h1.get(bt[i].getTag().toString()));
                                                Brantaglist.remove(bt[i].getText().toString().replace(" +", " ").toString().trim());
                                                Brantaglist1.add(bt[i].getText().toString().replace(" +", " ").toString().trim());

                                                h1.remove(bt[i].getTag().toString());

                                                //////System.out.println("h1size" + h1.size());
                                                //////System.out.println("else-h2size" + h2.size() + "x" + bt[i].getTag().toString());

                                                DrawProgress(h2.size());

                                            } else {
                                                action = false;

                                                bt[i].setBackgroundColor(Color.TRANSPARENT);
                                                bt[i].setBackgroundResource(R.drawable.shape_boarder);
                                                bt[i].setTextColor(Color.WHITE);
                                                Brantaglist.add(bt[i].getText().toString().replace(" +", " ").toString().trim());
                                                Brantaglist1.remove(bt[i].getText().toString().replace(" +", " ").toString().trim());
                                                h1.put(bt[i].getTag().toString(), h2.get(bt[i].getTag().toString()));
                                                h2.remove(bt[i].getTag().toString());
                                                //////System.out.println("else-h1size" + h1.size());
                                                //////System.out.println("else-h2size" + h2.size() + "x" + bt[i].getTag().toString());
                                                DrawProgress(h2.size());

                                            }


                                        }
                                    });
                                    if (i < h2.size() - 1) {
                                        intalready = 1;
                                    } else {
                                        intalready = 0;
                                    }

                                    if (h2.size() > 0) {

                                    }
                                }

                                lnBrand.addView(ln[start]);
                                Animation fadeIn = new AlphaAnimation(0, 1);
                                fadeIn.setInterpolator(new AccelerateInterpolator()); //add this
                                fadeIn.setDuration(500);
                                ln[start].setVisibility(View.VISIBLE);
                                AnimationSet animation = new AnimationSet(false); //change to false
                                animation.addAnimation(fadeIn);
                                ln[start].setAnimation(fadeIn);
                                //////System.out.println("inside timerxxxx" + intEnd);

                                //////System.out.println("nextdd" + h2.size());

                            }


                        }else {
                            blcheck=false;
                         //   tvmore.setVisibility(View.INVISIBLE);
                            mProgress.dismiss();
                            if (h1.size()>0){
                                ln = new LinearLayout[h1.size()];
                                bt=new TextView[h1.size()];


                             //   tvmore.setVisibility(View.GONE);


                                intLoadtype = 1;
                                intRowsize = 0;
                                intStart = 0;



                                //////System.out.println("zzzzzzzzz" + Brantaglist1.size() + "/" + Brantaglist.size());
                                ln = new LinearLayout[Brantaglist.size()];
                                bt=new TextView[Brantaglist.size()];
                                intRowsize=0;
                                intc=0;
                                start=0;
                                end=0;
                                intcheck=0;
                                intalready=0;
                                intbtnvisindex=0;
                                intbtstartindex=0;
                                for (int i = 0; i < Brantaglist.size(); i++) {


                                    //////System.out.println("screen density"+getResources().getDisplayMetrics().density);
                                    //////System.out.println("---count"+Brantaglist.get(i).toString().length());
                                    intRowsize = intRowsize +Brantaglist.get(i).toString().length();
                                    if (intRowsize < (getResources().getDisplayMetrics().density*10)+3 ){
                                        //////System.out.println("inside " + i);
                                        if (intTest == 1) {
                                            if (i == 0) {
                                                intStart = i;
                                                intTest = 0;
                                            } else {
                                                intStart = i;
                                                intTest = 0;
                                            }
                                        }
                                        if (intcount>=3){
                                            intcount=3;
                                            intRowsize=((int)(getResources().getDisplayMetrics().density)*10)+6;
                                        }
                                        else {
                                            intcount = intcount + 1;
                                        }

                                    } else {
                                        //////System.out.println("inside main" + intStart + "=" + i);
                                        intEnd = i;
                                        intRowsize = 0;
                                        intTest = 1;
                                        intcount = 0;
                                        intarr[intc]=intStart;
                                        intarr1[intc]=i;
                                        intc=intc+1;
                                        if (i<=Brantaglist.size()){
                                            i=i-1;
                                        }

                                    }


                                }
                                if (intRowsize > 0) {
                                    intRowsize = 0;
                                    intarr[intc]=intStart;
                                    intarr1[intc]=intStart + intcount ;
                                    intc=intc+1;

                                }




                                for (int k = 0; k < intc; k++) {

                                    start = intarr[k];
                                    end = intarr1[k];
                                    intcheck=0;
                                    ln[start] = new LinearLayout(Onboarding2.this);
                                    ln[start].setOrientation(LinearLayout.HORIZONTAL);
                                    ln[start].setVisibility(View.INVISIBLE);
                                    //   ln[start].setBackgroundColor(Color.GREEN);
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
                                    params.weight = 1.0f;
                                    params.gravity = Gravity.CENTER;
                                    params.setMargins(5, 5, 5, 5);
                                    ln[start].setLayoutParams(params);
                                    //////System.out.println(start + "=j=" + end);
                                    for (int i = start; i < end; i++) {
                                        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                                        params1.setMargins(10, 15, 10, 10);
                                        params1.weight = 1;
                                        //////System.out.println("inside for====" + i);
                                        bt[i] = new TextView(Onboarding2.this);

                                        final TextView tvindex = new TextView(Onboarding2.this);

                                        bt[i].setLayoutParams(params1);
                                        if (Brantaglist1.size()>0) {
                                            if (intLoadtype == 1) {

                                                bt[i].setBackgroundResource(R.drawable.clickshape_boarder);
                                                bt[i].setTextColor(Color.BLACK);
                                                //////System.out.println("RRR" + i);

                                                if (i > Brantaglist1.size() - 1) {
                                                    intcheck = 1;
                                                    intbtstartindex = i;
                                                    //////System.out.println("INSIDE selected" + i);

                                                    bt[i].setBackgroundColor(Color.TRANSPARENT);
                                                    bt[i].setBackgroundResource(R.drawable.shape_boarder);
                                                    bt[i].setTextColor(Color.WHITE);


                                                    intLoadtype = 0;
                                                } else if (i == Brantaglist1.size() - 1&& blcheck){

                                                    final int s = start;

                                                    LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, inthieght);
                                                    intf = ln[start].getLayoutParams().height;
                                                    ln[start].setLayoutParams(params2);
                                                    ValueAnimator va;
                                                    if (start == 0) {
                                                        va = ValueAnimator.ofInt(inthieght, LinearLayout.LayoutParams.WRAP_CONTENT);
                                                        bt[0].setVisibility(View.GONE);

                                                    } else {
                                                        va = ValueAnimator.ofInt(inthieght, intf+bt[0].getHeight());
                                                    }

                                                    va.setDuration(1500);
                                                    va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                                        public void onAnimationUpdate(ValueAnimator animation) {
                                                            Integer value = (Integer) animation.getAnimatedValue();
                                                            ln[s].getLayoutParams().height = value.intValue();
                                                            ln[s].requestLayout();
                                                        }


                                                    });
                                                    va.addListener(new AnimatorListenerAdapter() {
                                                        @Override
                                                        public void onAnimationEnd(Animator animation) {
                                                            //////System.out.println("btnstart" + intbtstartindex + "btnend" + intbtnvisindex);

                                                            for (int j = intbtstartindex; j <= intbtnvisindex; j++) {

                                                                bt[j].setVisibility(View.VISIBLE);

                                                            }
                                                            if (s == 0) {

                                                                bt[0].setVisibility(View.VISIBLE);

                                                            }
                                                            intcheckval = 0;


                                                        }

                                                        @Override
                                                        public void onAnimationStart(Animator animation) {

                                                        }
                                                    });

                                                    va.start();

                                                }


                                            }
                                            else {

                                                bt[i].setBackgroundColor(Color.TRANSPARENT);
                                                bt[i].setTextColor(Color.WHITE);
                                                bt[i].setBackgroundResource(R.drawable.shape_boarder);


                                            }
                                            if (intcheck == 1) {
                                                bt[i].setVisibility(View.GONE);
                                                intbtnvisindex = i;
                                            }
                                        }else {

                                            bt[i].setBackgroundColor(Color.TRANSPARENT);
                                            bt[i].setTextColor(Color.WHITE);
                                            bt[i].setBackgroundResource(R.drawable.shape_boarder);


                                        }

                                        bt[i].setTextSize(11);
                                        bt[i].setText(Brantaglist.get(i) + " +");
                                        bt[i].setTag(getKeyFromValue(h1, Brantaglist.get(i)));
                                        bt[i].setPadding(15, 15, 15, 15);
                                        ln[start].addView(bt[i]);
                                        ln[start].addView(tvindex);
                                        //////System.out.println(i + "=j" + Brantaglist.get(i));
                                        tvindex.setText(String.valueOf(i));

                                        tvindex.setVisibility(View.GONE);

                                        bt[i].setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                int i = Integer.parseInt(tvindex.getText().toString());

                                                int colorId = bt[i].getCurrentTextColor();


                                                if (colorId == Color.WHITE) {
                                                    bt[i].setBackgroundResource(R.drawable.clickshape_boarder);

                                                    bt[i].setTextColor(Color.BLACK);
                                                    action = true;

                                                    h2.put(bt[i].getTag().toString(), h1.get(bt[i].getTag().toString()));
                                                    Brantaglist.remove(bt[i].getText().toString().replace(" +", " ").toString().trim());
                                                    Brantaglist1.add(bt[i].getText().toString().replace(" +", " ").toString().trim());

                                                    h1.remove(bt[i].getTag().toString());

                                                    //////System.out.println("h1size" + h1.size());
                                                    //////System.out.println("else-h2size" + h2.size() + "x" + bt[i].getTag().toString());

                                                    DrawProgress(h2.size());

                                                } else {
                                                    action = false;

                                                    bt[i].setBackgroundColor(Color.TRANSPARENT);
                                                    bt[i].setBackgroundResource(R.drawable.shape_boarder);
                                                    bt[i].setTextColor(Color.WHITE);
                                                    Brantaglist.add(bt[i].getText().toString().replace(" +", " ").toString().trim());
                                                    Brantaglist1.remove(bt[i].getText().toString().replace(" +", " ").toString().trim());
                                                    h1.put(bt[i].getTag().toString(), h2.get(bt[i].getTag().toString()));
                                                    h2.remove(bt[i].getTag().toString());
                                                    //////System.out.println("else-h1size" + h1.size());
                                                    //////System.out.println("else-h2size" + h2.size() + "x" + bt[i].getTag().toString());
                                                    DrawProgress(h2.size());

                                                }


                                            }
                                        });
                                        if (i < h2.size() - 1) {
                                            intalready = 1;
                                        } else {
                                            intalready = 0;
                                        }

                                        if (h2.size() > 0) {

                                        }
                                    }

                                    lnBrand.addView(ln[start]);
                                    Animation fadeIn = new AlphaAnimation(0, 1);
                                    fadeIn.setInterpolator(new AccelerateInterpolator()); //add this
                                    fadeIn.setDuration(500);
                                    ln[start].setVisibility(View.VISIBLE);
                                    AnimationSet animation = new AnimationSet(false); //change to false
                                    animation.addAnimation(fadeIn);
                                    ln[start].setAnimation(fadeIn);
                                    //////System.out.println("inside timerxxxx" + intEnd);


                                }

//                                if( next.equals("true")){
//                                    tvmore.setVisibility(View.VISIBLE);
//                                }
//                                else{
//                                    tvmore.setVisibility(View.INVISIBLE);
//                                }
                            }



                        }
                    }
                    else {
                        mProgress.dismiss();
                         CustomMessage.getInstance().CustomMessage(Onboarding2.this, "Oops. Something went wrong!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mProgress.dismiss();
                     CustomMessage.getInstance().CustomMessage(Onboarding2.this, "Oops. Something went wrong!");
                }
            }

        }
        else if (flag.equals("setuserkey")) {
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            try {
                //System.out.println("appviral" + resp);
                String status = resp.getString("status");
                if (status.equals("success")) {

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onErrorCallback(VolleyError error, String flag) {
        if(mProgress.isShowing()){
            mProgress.dismiss();
        }
    }



    public void unbindDrawables(View view) {
        //////System.out.println("inside unbinddrawables");
        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();

        }

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        Runtime.getRuntime().gc();
        //System.gc();
        RelativeLayout v = (RelativeLayout) findViewById(R.id.rlbrandtag);
        if (v != null) {
            if (v.getBackground() != null) v.getBackground().setCallback(null);
        }

      //  View view = findViewById(R.id.rlbrandtag);
        unbindDrawables(findViewById(R.id.rlbrandtag));
        h1 =null;
        h2 =null;
        Brantaglist = null;
        Brantaglist1 = null;
        intarr=null;
        intarr1=null;

        Runtime.getRuntime().gc();


    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        if(!GetSharedValues.GetgcmId(Onboarding2.this).equals("")) {
            ApiService.getInstance(Onboarding2.this, 1).getData(Onboarding2.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/?gcm_reg_id="+GetSharedValues.GetgcmId(Onboarding2.this), "session");
        }
        else {
            ApiService.getInstance(Onboarding2.this, 1).getData(Onboarding2.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/", "session");
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        mTracker.set("UserName", GetSharedValues.getUsername(getApplicationContext()));mTracker.set("ZapUsername", GetSharedValues.getZapname(getApplicationContext()));   mTracker.setScreenName("Onboarding2");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
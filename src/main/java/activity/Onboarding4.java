package activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.android.volley.VolleyError;
//import com.//Appsee.//Appsee;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
//import com.uxcam.UXCam;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import application.MyApplicationClass;
import network.ApiCommunication;
import network.ApiService;
import utils.CommonFinish;
import utils.CustomMessage;
import utils.FontUtils;
import utils.ExternalFunctions;
import utils.GetSharedValues;
import utils.Mixpanelutils;
import utils.NumberPickerStylingUtils;

/**
 * Created by haseeb on 13/1/16.
 */
public class Onboarding4 extends ActionBarActivity implements ApiCommunication {
    private int CLICK_COUNT = 0;
    String SCREEN_NAME = "ONBOARDING4";
    android.widget.NumberPicker npcloths;
    android.widget.NumberPicker npwaist;
    android.widget.NumberPicker npfoot;
    TextView Skip;
    TextView tvcloth, tvwaist, tvfoot, tvnext;
    ArrayList<JSONObject> cloths = new ArrayList<JSONObject>();
    ArrayList<JSONObject> waists = new ArrayList<JSONObject>();
    ArrayList<JSONObject> foots = new ArrayList<JSONObject>();
    String strcloth[], strwaist[], strfoot[];
    public ArrayList<String> dataToservercloth = new ArrayList<String>();
    public ArrayList<String> dataToserverwaist = new ArrayList<String>();
    public ArrayList<String> dataToserverfoot = new ArrayList<String>();

    boolean booltype;
    ProgressDialog mProgress;
    ProgressBar progressBar;
    RelativeLayout rl;
    MixpanelAPI mixpanel;
    Date today;
    public static String activityname;
    Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboarding4);
        MyApplicationClass application = (MyApplicationClass) getApplication();
        mTracker = application.getDefaultTracker();
        //Appsee.start("4fb55809ab45411c909521bda720f8e6");
        //UXCam.startWithKey("1dfb25141864376");         overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        //////Thread.currentThread().setDefaultUncaughtExceptionHandler(new SplashScreen.MyUncaughtExceptionHandler());
        LocalBroadcastManager.getInstance(this).registerReceiver(CommonFinish.broadcastReceiver, new IntentFilter("SplashScreen"));
//        if(!MyApplicationClass.cContextArray.contains(getApplicationContext())) {
        ExternalFunctions.cContextArray[6] = Onboarding4.this;
//        }
        //  progressBar = (ProgressBar) findViewById(R.id.ob4secondaryprogressBar);
        mProgress = new ProgressDialog(Onboarding4.this);
        mProgress.setCancelable(false);
        rl = (RelativeLayout) findViewById(R.id.rl);
        rl.setVisibility(View.INVISIBLE);


        SharedPreferences settings = getSharedPreferences("FeedSession",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("SWITCH_STATUS_LIST", true);
        editor.apply();

        today = new Date();
        mixpanel = MixpanelAPI.getInstance(Onboarding4.this, getResources().getString(R.string.mixpanelToken));
//
//        JSONObject superprop = new JSONObject();
//        try {
//            superprop.put("Event Name", "Completed Onboarding");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        mixpanel.registerSuperProperties(superprop);

        mProgress.setMessage("Loading ...");
        mProgress.show();
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        View customView = getLayoutInflater().inflate(R.layout.onboarding5bar, null);
        getSupportActionBar().setCustomView(customView);
        Toolbar parent = (Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0, 0);
        npcloths = (android.widget.NumberPicker) findViewById(R.id.numberPicker1);
        npwaist = (android.widget.NumberPicker) findViewById(R.id.numberPicker2);
        npfoot = (android.widget.NumberPicker) findViewById(R.id.numberPicker3);
        tvcloth = (TextView) findViewById(R.id.tvcloth);
        tvwaist = (TextView) findViewById(R.id.tvwaist);
        tvfoot = (TextView) findViewById(R.id.tvfoot);
        tvnext = (TextView) findViewById(R.id.next2);

        try{
            Bundle bundle=this.getIntent().getExtras();
            booltype =bundle.getBoolean("booltype");
            activityname=bundle.getString("activity");
        }catch (Exception e){
            activityname="SplashScreen";
            booltype=true;
        }
        FontUtils.setCustomFont(rl, getAssets());


        NumberPickerStylingUtils.applyStyling(npcloths);
        NumberPickerStylingUtils.applyStyling(npwaist);
        NumberPickerStylingUtils.applyStyling(npfoot);
        NumberPickerStylingUtils.setNumberPickerTextColor(npcloths, Color.parseColor("#ff7477"), 70, convertPixelsToDp(60, Onboarding4.this));
        NumberPickerStylingUtils.setNumberPickerTextColor(npwaist, Color.parseColor("#ff7477"), 70, convertPixelsToDp(60, Onboarding4.this));
        NumberPickerStylingUtils.setNumberPickerTextColor(npfoot, Color.parseColor("#ff7477"), 70, convertPixelsToDp(60, Onboarding4.this));
        npcloths.setFormatter(new android.widget.NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format("%03d", i);
            }
        });
        npwaist.setFormatter(new android.widget.NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format("%02d", i);
            }
        });
        npfoot.setFormatter(new android.widget.NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format("%03d", i);
            }
        });
        npcloths.setOnValueChangedListener(new android.widget.NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(android.widget.NumberPicker picker, int oldVal, int newVal) {
                tvcloth.setText(strcloth[npcloths.getValue() - 1]);
                tvcloth.setTag(npcloths.getValue() - 1);


            }
        });
        npwaist.setOnValueChangedListener(new android.widget.NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(android.widget.NumberPicker picker, int oldVal, int newVal) {
                tvwaist.setText(strwaist[npwaist.getValue() - 1]);
                tvwaist.setTag(npwaist.getValue() - 1);

            }
        });
        npfoot.setOnValueChangedListener(new android.widget.NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(android.widget.NumberPicker picker, int oldVal, int newVal) {

                tvfoot.setText(strfoot[npfoot.getValue() - 1]);
                tvfoot.setTag(npfoot.getValue() - 1);
            }
        });
        GetSizes();
        tvnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    //////System.out.println("getid" + String.valueOf(cloths.get(Integer.parseInt(tvcloth.getTag().toString())).getInt("id")));
                    dataToserverfoot.clear();
                    dataToserverwaist.clear();
                    dataToservercloth.clear();
                    dataToservercloth.add(String.valueOf(cloths.get(Integer.parseInt(tvcloth.getTag().toString())).getInt("id")));
                    dataToserverwaist.add(String.valueOf(waists.get(Integer.parseInt(tvwaist.getTag().toString())).getInt("id")));
                    dataToserverfoot.add(String.valueOf(foots.get(Integer.parseInt(tvfoot.getTag().toString())).getInt("id")));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                SizeToServer();
            }
        });
        if (booltype == false) {
            tvnext.setText("DONE");
        } else {


        }
        Skip = (TextView) findViewById(R.id.skip3);
        if (booltype == false) {
            Skip.setVisibility(View.INVISIBLE);
        }
        Skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataToservercloth.clear();
                dataToserverwaist.clear();
                dataToserverfoot.clear();
                SizeToServer();
            }
        });
    }

    @Override
    public void onBackPressed() {


        if (activityname.equals("SplashScreen")) {
            if (CLICK_COUNT < 1) {
                CustomMessage.getInstance().CustomMessage(this, "Press again to close the app");
                CLICK_COUNT = CLICK_COUNT + 1;
            } else {

                CLICK_COUNT = 0;
                android.os.Process.killProcess(android.os.Process.myPid());
                super.onDestroy();

            }


        } else {
            if (booltype){
                Intent on3 = new Intent(Onboarding4.this, Onboarding3.class);
                on3.putExtra("activity", "Onboarding3");
                on3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                on3.putExtra("booltype",true);
                startActivity(on3);
                finish();
            }else{
                Intent on3 = new Intent(Onboarding4.this, Mypreference.class);
                on3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(on3);
                finish();
            }


        }
    }


    //    Server request
//    --------------------------------------------------------------
    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    private void GetSizes() {

        ApiService.getInstance(Onboarding4.this, 1).getData(Onboarding4.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/onboarding/4/", "getsizes");


    }

    private void GetSelectedSizes() {

        ApiService.getInstance(Onboarding4.this, 1).getData(Onboarding4.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/mysizes/", "getselectedsizes");

    }

    private void SizeToServer() {
        Skip.setEnabled(false);
        tvnext.setEnabled(false);
        JSONArray data = new JSONArray(dataToservercloth);
        JSONArray data1 = new JSONArray(dataToserverwaist);
        JSONArray data2 = new JSONArray(dataToserverfoot);
        //////System.out.println("data for sending__" + data);
        final JSONObject onboarding2Object = new JSONObject();
        try {
            onboarding2Object.put("cloth_sizes", data);
            onboarding2Object.put("waist_sizes", data1);
            onboarding2Object.put("foot_sizes", data2);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiService.getInstance(Onboarding4.this, 1).postData(Onboarding4.this, EnvConstants.APP_BASE_URL + "/onboarding/4/", onboarding2Object, SCREEN_NAME, "datatoserver");

    }

//    Server Response
//    --------------------------------------------------------------


    @Override
    public void onResponseCallback(JSONObject response, String flag) {
        //////System.out.println("response getsize___" + response);
        if (flag.equals("getsizes")) {
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                try {

                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        JSONObject data = resp.getJSONObject("data");
                        JSONArray array_cloth_size = data.getJSONArray("cloth_sizes");
                        JSONArray array_foot_size = data.getJSONArray("foot_sizes");
                        JSONArray array_waist_size = data.getJSONArray("waist_sizes");
                        //////System.out.println("lenght" + array_cloth_size.length());
                        strcloth = new String[array_cloth_size.length()];
                        strwaist = new String[array_waist_size.length()];
                        strfoot = new String[array_foot_size.length()];


                        for (int i = 0; i < array_cloth_size.length(); i++) {
                            cloths.add(array_cloth_size.getJSONObject(i));
                            strcloth[i] = array_cloth_size.getJSONObject(i).getString("size");
                        }
                        for (int i = 0; i < array_waist_size.length(); i++) {
                            waists.add(array_waist_size.getJSONObject(i));
                            strwaist[i] = array_waist_size.getJSONObject(i).getString("size");
                        }
                        for (int i = 0; i < array_foot_size.length(); i++) {
                            foots.add(array_foot_size.getJSONObject(i));
                            strfoot[i] = array_foot_size.getJSONObject(i).getString("us_size");
                        }
                        npcloths.setMinValue(1);
                        npcloths.setMaxValue(strcloth.length);
                        npwaist.setMinValue(1);
                        npwaist.setMaxValue(strwaist.length);
                        npfoot.setMinValue(1);
                        npfoot.setMaxValue(strfoot.length);


                        npcloths.setDisplayedValues(strcloth);
                        npwaist.setDisplayedValues(strwaist);
                        npfoot.setDisplayedValues(strfoot);


                        if (booltype == false) {
                            GetSelectedSizes();
                        } else {
                            npcloths.setValue(2);
                            npwaist.setValue(2);
                            npfoot.setValue(2);

                            npcloths.setWrapSelectorWheel(false);
                            npwaist.setWrapSelectorWheel(false);
                            npfoot.setWrapSelectorWheel(false);
                            tvcloth.setText(strcloth[npcloths.getValue() - 1]);
                            tvwaist.setText(strwaist[npwaist.getValue() - 1]);
                            tvfoot.setText(strfoot[npfoot.getValue() - 1]);
                            tvcloth.setTag(npcloths.getValue() - 1);
                            tvwaist.setTag(npwaist.getValue() - 1);
                            tvfoot.setTag(npfoot.getValue() - 1);

                            dataToservercloth.add(String.valueOf(cloths.get(Integer.parseInt(tvcloth.getTag().toString())).getInt("id")));
                            dataToserverwaist.add(String.valueOf(waists.get(Integer.parseInt(tvwaist.getTag().toString())).getInt("id")));
                            dataToserverfoot.add(String.valueOf(foots.get(Integer.parseInt(tvfoot.getTag().toString())).getInt("id")));

                        }
                        mProgress.dismiss();
                        rl.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Intent mydialog = new Intent(Onboarding4.this, AlertActivity.class);
                   // int imgid = R.drawable.alertoop;
                    String strmessage = "OOPS!";
                   // mydialog.putExtra("imgid", imgid);
                    mydialog.putExtra("Message", strmessage);
                    mydialog.putExtra("Buttontext", " RETRY ");
                    mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                    if (booltype == true) {
                        mydialog.putExtra("activity", "Onboarding4");
                        mydialog.putExtra("calling", "Onboarding4");
                    } else {
                        mydialog.putExtra("activity", "Mypreference");
                        mydialog.putExtra("calling", "Mypreference");
                    }
                    startActivity(mydialog);
                    finish();
                }

            }


        } else if (flag.equals("getselectedsizes")) {
            //////System.out.println("selectedfffffffff___" + response);
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                try {
                    //////System.out.println("selectedfffffffff 1___");
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        //////System.out.println("selectedfffffffff2___");
                        JSONObject data = resp.getJSONArray("data").getJSONObject(0);
                        JSONArray array_cloth_size = data.getJSONArray("size");
                        JSONArray array_foot_size = data.getJSONArray("foot_size");
                        JSONArray array_waist_size = data.getJSONArray("waist_size");
                        //////System.out.println("lenght" + array_cloth_size.length());


                        String strcloth1 = null, strwaist1 = null, strfoot1 = null;
                        for (int i = 0; i < array_cloth_size.length(); i++) {

                            strcloth1 = array_cloth_size.getJSONObject(i).getString("size");
                            //////System.out.println("arraylenghth___" + array_cloth_size.length() + "" + strcloth1);
                        }
                        for (int i = 0; i < array_waist_size.length(); i++) {
                            // waists.add(array_waist_size.getJSONObject(i));
                            strwaist1 = array_waist_size.getJSONObject(i).getString("size");
                        }
                        for (int i = 0; i < array_foot_size.length(); i++) {

                            //foots.add(array_foot_size.getJSONObject(i));
                            strfoot1 = array_foot_size.getJSONObject(i).getString("us_size");
                            //////System.out.println("arraylenghth___" + array_foot_size.length() + "" + strfoot1);
                        }

                        for (int i = 0; i < cloths.size(); i++) {
                            if (strcloth[i].equals(strcloth1)) {
                                //////System.out.println("selectedfffffffff 1___" + strcloth[i]);
                                npcloths.setValue(i + 1);
                            }
                        }
                        for (int i = 0; i < waists.size(); i++) {
                            if (strwaist[i].equals(strwaist1)) {
                                //////System.out.println("selectedfffffffff 1___" + strwaist[i]);
                                npwaist.setValue(i + 1);
                            }
                        }
                        for (int i = 0; i < foots.size(); i++) {
                            if (strfoot[i].equals(strfoot1)) {
                                //////System.out.println("selectedfffffffff 1___" + strfoot[i]);
                                npfoot.setValue(i + 1);
                            }
                        }


                        npcloths.setWrapSelectorWheel(false);
                        npwaist.setWrapSelectorWheel(false);
                        npfoot.setWrapSelectorWheel(false);
                        tvcloth.setText(strcloth[npcloths.getValue() - 1]);
                        tvwaist.setText(strwaist[npwaist.getValue() - 1]);
                        tvfoot.setText(strfoot[npfoot.getValue() - 1]);
                        tvcloth.setTag(npcloths.getValue() - 1);
                        tvwaist.setTag(npwaist.getValue() - 1);
                        tvfoot.setTag(npfoot.getValue() - 1);

                        dataToservercloth.add(String.valueOf(cloths.get(Integer.parseInt(tvcloth.getTag().toString())).getInt("id")));
                        dataToserverwaist.add(String.valueOf(waists.get(Integer.parseInt(tvwaist.getTag().toString())).getInt("id")));
                        dataToserverfoot.add(String.valueOf(foots.get(Integer.parseInt(tvfoot.getTag().toString())).getInt("id")));
//                        mProgress.dismiss();
//                        rl.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Intent mydialog = new Intent(Onboarding4.this, AlertActivity.class);
                   // int imgid = R.drawable.alertoop;
                    String strmessage = "OOPS!";
                   // mydialog.putExtra("imgid", imgid);
                    mydialog.putExtra("Message", strmessage);
                    mydialog.putExtra("Buttontext", " RETRY ");
                    if (booltype == true) {
                        mydialog.putExtra("activity", "Onboarding4");
                        mydialog.putExtra("calling", "Onboarding4");
                    } else {
                        mydialog.putExtra("activity", "Mypreference");
                        mydialog.putExtra("calling", "Mypreference");
                    }
                    mydialog.putExtra("tip", "SOMETHING WENT WRONG");


                    startActivity(mydialog);
                    finish();
                }

            }


        } else if (flag.equals("datatoserver")) {
            //////System.out.println("response_____" + response);
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        ////progressBar.setVisibility(View.GONE);
                        tvnext.setEnabled(true);
                        Skip.setEnabled(true);
                        if (booltype == true) {
                            today = new Date();
                            DateFormat dfclosetime = new SimpleDateFormat("hh:mm:ss aa", Locale.ENGLISH);
                            dfclosetime.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
                            String strclosetime = dfclosetime.format(today);


                            Mixpanelutils mx = new Mixpanelutils();

                            //////System.out.println("mixpaneltimespent" + mx.getTimeDiff(SplashScreen.starttime, strclosetime) + "iii" + strclosetime);
                            JSONObject proponbcompleted = new JSONObject();
                            try {
                                proponbcompleted.put("Onboarding time", strclosetime);
                                proponbcompleted.put("Onboarding time taken", mx.getTimeDiff(SplashScreen.starttime, strclosetime));
                                proponbcompleted.put("Event Name", "Completed Onboarding");
                                mixpanel.track("Completed Onboarding", proponbcompleted);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            Intent onboarding4 = new Intent(Onboarding4.this, HomePageNew.class);
                            onboarding4.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(onboarding4);
                            finish();
                        } else {

                            Intent mypre = new Intent(Onboarding4.this, Mypreference.class);
                            startActivity(mypre);
                            finish();
                        }

                    } else {
                        //progressBar.setVisibility(View.GONE);
                        tvnext.setEnabled(true);
                        Skip.setEnabled(true);
                        Intent mydialog = new Intent(Onboarding4.this, AlertActivity.class);
                       // int imgid = R.drawable.alertoop;
                        String strmessage = "OOPS!";
                       // mydialog.putExtra("imgid", imgid);
                        mydialog.putExtra("Message", strmessage);
                        mydialog.putExtra("Buttontext", " RETRY ");
                        mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                        if (booltype == true) {
                            mydialog.putExtra("activity", "Onboarding4");
                            mydialog.putExtra("calling", "Onboarding4");
                        } else {
                            mydialog.putExtra("activity", "Mypreference");
                            mydialog.putExtra("calling", "Mypreference");
                        }
                        startActivity(mydialog);
                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    //progressBar.setVisibility(View.GONE);
                    tvnext.setEnabled(true);
                    Skip.setEnabled(true);
                    Intent mydialog = new Intent(Onboarding4.this, AlertActivity.class);
                   // int imgid = R.drawable.alertoop;
                    String strmessage = "OOPS!";
                   // mydialog.putExtra("imgid", imgid);
                    mydialog.putExtra("Message", strmessage);
                    mydialog.putExtra("Buttontext", " RETRY ");
                    mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                    if (booltype == true) {
                        mydialog.putExtra("activity", "Onboarding4");
                        mydialog.putExtra("calling", "Onboarding4");
                    } else {
                        mydialog.putExtra("activity", "Mypreference");
                        mydialog.putExtra("calling", "Mypreference");
                    }
                    startActivity(mydialog);
                    finish();

                }
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
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        finish();
        startActivity(getIntent());
    }


    public void onLowMemory(){
        unbindDrawables(findViewById(R.id.onboarding3Layout));

        ExternalFunctions.deleteCache(Onboarding4.this);
    }



    @Override
    protected void onDestroy() {

        super.onDestroy();
        Runtime.getRuntime().gc();
        RelativeLayout v = (RelativeLayout) findViewById(R.id.onboarding4Layout);
        if (v != null) {
            if (v.getBackground() != null) v.getBackground().setCallback(null);
        }
        View view = findViewById(R.id.onboarding4Layout);
        unbindDrawables(findViewById(R.id.onboarding4Layout));
        Runtime.getRuntime().gc();
        //////System.out.println(Runtime.getRuntime().freeMemory()+"__freememory");
        //////System.out.println(Runtime.getRuntime().maxMemory()+"__maxmemory");
        //////System.out.println(Runtime.getRuntime().totalMemory() + "__totalmemory");
        //System.gc();

    }
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        if(!GetSharedValues.GetgcmId(Onboarding4.this).equals("")) {
            ApiService.getInstance(Onboarding4.this, 1).getData(Onboarding4.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/?gcm_reg_id="+GetSharedValues.GetgcmId(Onboarding4.this), "session");
        }
        else {
            ApiService.getInstance(Onboarding4.this, 1).getData(Onboarding4.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/", "session");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mTracker.set("UserName", GetSharedValues.getUsername(getApplicationContext()));mTracker.set("ZapUsername", GetSharedValues.getZapname(getApplicationContext()));   mTracker.setScreenName("Onboarding4");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}

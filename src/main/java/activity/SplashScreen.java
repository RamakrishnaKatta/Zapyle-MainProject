package activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.appvirality.android.AppviralityAPI;
import com.crashlytics.android.Crashlytics;

import com.freshdesk.hotline.Hotline;
import com.freshdesk.hotline.HotlineConfig;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
////import com.uxcam.UXCam;
import com.zapyle.zapyle.CheckConnectivity;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import DataBase.DatabaseDB;
import application.MyApplicationClass;
import io.fabric.sdk.android.Fabric;
import network.ApiCommunication;
import network.ApiService;
import notifications.MyGcmRegistrationService;
import recievers.UploadReceiver;
import notifications.MyGcmRegistrationService;
import utils.CustomMessage;
import utils.ExternalFunctions;
import utils.GetSharedValues;
import utils.Mixpanelutils;
import utils.ServiceHandle;

//import com.//Appsee.//Appsee;

public class SplashScreen extends ActionBarActivity implements ApiCommunication, UploadReceiver.Receiver {
    private static final long SPLASH_TIME_OUT = 1000;
    public static Integer int_Buildnumber;
    Boolean bool_updatecheck = false;
    Boolean bool_gcmcheck = false, bool_loginstatus = false;
    GoogleCloudMessaging gcm;
    public static String regId, csrf_token;
    String SCREEN_NAME = "SPLASHSCREEN";
    private static final String TAG ="hotline_new" ;
    public static String startdatetime;
    public static String starttime;
    MixpanelAPI mixpanel;
    Date today;
    public static Boolean referralCheck = false;
    Boolean offlinecheck = false;
    private Tracker mTracker;
    public static UploadReceiver mReceiver;
    private DatabaseDB db;

    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 4329;
    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplicationClass application = (MyApplicationClass) getApplication();
        mTracker = application.getDefaultTracker();


        //Hotline.clearUserData(getApplicationContext());
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }

//        ----------------------------------------------------------------
        HotlineConfig hlConfig=new HotlineConfig("0c01aec8-e550-443b-9546-3f534dc85deb","b5888037-d305-4abe-addb-4a716e8f484b");
        Hotline.getInstance(getApplicationContext()).init(hlConfig);
        hlConfig.setVoiceMessagingEnabled(false);
        hlConfig.setCameraCaptureEnabled(true);
        hlConfig.setPictureMessagingEnabled(true);

        Hotline.getInstance(getApplicationContext()).init(hlConfig);
        SharedPreferences settingshomedata = getSharedPreferences("MyPref",
                Context.MODE_PRIVATE);

        SharedPreferences.Editor editorhomedata = settingshomedata.edit();
        editorhomedata.clear();
        editorhomedata.apply();
     //   //UXCam.startWithKey("1dfb25141864376");
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        db = new DatabaseDB(getApplicationContext());
        if (!db.checkDataBaseexist()) {
            new DbOperation().execute();
        }
        offlinecheck = true;
        ServiceHandle.StartService(SplashScreen.this);
        //Thread.currentThread().setDefaultUncaughtExceptionHandler(new SplashScreen.MyUncaughtExceptionHandler());

        mReceiver = new UploadReceiver(new Handler());
        mReceiver.setReceiver(this);

        SharedPreferences FeedSession = getSharedPreferences("FeedSession",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor FeedSessioneditor = FeedSession.edit();
        FeedSessioneditor.putBoolean("SWITCH_STATUS_LIST", true);
        FeedSessioneditor.apply();
        try {
            Bundle bundle = this.getIntent().getExtras();
            ExternalFunctions.referalmsg = bundle.getString("msg");
        } catch (Exception e) {
            ExternalFunctions.referalmsg = "";
        }


        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            int_Buildnumber = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        // super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash_screen);
        today = new Date();
        //Start date time
        DateFormat df = new SimpleDateFormat("dd-MM-yy HH:mm:ss aa");
        df.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        startdatetime = df.format(today);

        DateFormat dfstarttime = new SimpleDateFormat("hh:mm:ss aa");
        dfstarttime.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        starttime = dfstarttime.format(today);

        mixpanel = MixpanelAPI.getInstance(SplashScreen.this, getResources().getString(R.string.mixpanelToken));
        JSONObject propOpenapp = new JSONObject();
        try {
            propOpenapp.put("Open time", startdatetime);
            propOpenapp.put("Event Name", "Open app");
            mixpanel.track("Open app", propOpenapp);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        SharedPreferences settings = getSharedPreferences("LoginSession",
                Context.MODE_PRIVATE);
        bool_gcmcheck = settings.getBoolean("GcmCheck", false);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("STATIC_DATA", true);
        editor.apply();
        AppviralityAPI.init(getApplicationContext());



                if (CheckConnectivity.isNetworkAvailable(this)) {

                    BuildNumberCheck();


                } else {

                    InternetMessage();
                   // CustomMessage.getInstance().CustomMessage(this,"Check Your Internet connection.");
                }
            }



//        if (isInternetOn()) {
//
//        } else {
//            SharedPreferences settings1 = getSharedPreferences("LoginSession",
//                    Context.MODE_PRIVATE);
//            Boolean logincheck = settings1.getBoolean("LoginStatus", false);
//            if (logincheck) {
//                Intent feed = new Intent(SplashScreen.this, HomePageNew.class);
//                feed.putExtra("activity", "SplashScreen");
//                startActivity(feed);
//                finish();
//            } else {
//                Alerts.InternetAlert(SplashScreen.this);
//            }
//        }





//    Main functions
//    -----------------------------------------------------------------

    private void InternetMessage(){

        final View v = this.getWindow().getDecorView().findViewById(android.R.id.content);
         final Snackbar snackBar = Snackbar.make(v, "Check Your Internet connection.", Snackbar.LENGTH_INDEFINITE);
        snackBar.setActionTextColor(Color.parseColor("#FF7373"));
        snackBar.setAction("RETRY", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackBar.dismiss();
                if (CheckConnectivity.isNetworkAvailable(SplashScreen.this)) {

                    BuildNumberCheck();


                } else {

                    InternetMessage();
                    // CustomMessage.getInstance().CustomMessage(this,"Check Your Internet connection.");
                }


            }
        });
        snackBar.show();
    }
    private void BuildNumberCheck() {
        ApiService.getInstance(SplashScreen.this, 1).getData(SplashScreen.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/mydetails/?number=" + int_Buildnumber, "buildnumber");

    }

    public class DbOperation extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            db.openDB();
            System.out.println("inside databasecreate");
            String query = "CREATE TABLE IF NOT EXISTS " + "FEED" + " (" + "pageno" + " INTEGER PRIMARY KEY, " + "json" + " TEXT " + ");";
            db.processData(query);
            String query1 = "CREATE TABLE IF NOT EXISTS " + "DISCOVER" + " (" + "pageno" + " INTEGER PRIMARY KEY, " + "json" + " TEXT " + ");";
            db.processData(query1);
            String query2= "CREATE TABLE IF NOT EXISTS " + "CART" + " (" + "albumId" + " VARCHAR, " + "json" + " TEXT " + ");";
            db.processData(query2);
            String query3 = "CREATE TABLE IF NOT EXISTS " + "CLOSET" + " (" + "pageno" + " INTEGER PRIMARY KEY, " + "json" + " TEXT " + ");";
            db.processData(query3);
            return null;
        }
    }

    public final boolean isInternetOn() {

        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            System.out.println("internet" + !ipAddr.equals(""));
            //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }


    private void registerInBackground(final int profile_completed) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    regId = gcm.register("1038239410729");
                    Log.d("RegisterActivity", "registerInBackground - regId: "
                            + regId);
                    msg = "Device registered, registration ID=" + regId;

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    Log.d("RegisterActivity", "Error: " + msg);
                }
                Log.d("RegisterActivity", "AsyncTask completed: " + msg);
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                System.out.println("Registered with GCM Server." + msg);
//
                SharedPreferences settings = getSharedPreferences("LoginSession",
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("GcmCheck", true);
                editor.putString("GcMToken", regId);
                editor.apply();

                if (profile_completed == 0) {
                    Intent login = new Intent(SplashScreen.this, HomePageNew.class);
                    login.putExtra("booltype", true);
                    startActivity(login);
                    finish();
                }


            }
        }.execute(null, null, null);
    }


    //  Main response
//---------------------------------------------------------------------
    @Override
    public void onResponseCallback(JSONObject response, String flag) {


//
        if (flag.equals("buildnumber")) {
            System.out.println("BUILDNUMBER : " + response);
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);

            if (resp != null) {
                try {
                    System.out.println("BUILDNUMBER : status : " + resp.getString("status"));
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        System.out.println("BUILDNUMBER : " + resp.getJSONObject("data") + "_____" + resp.getJSONObject("data").getBoolean("build"));
                        SharedPreferences settings = getSharedPreferences("LoginSession",
                                Context.MODE_PRIVATE);

                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("csrfToken", csrf_token);
                        editor.apply();
                        JSONObject data = resp.getJSONObject("data");
                        System.out.println("BUILDNUMBER : " + data + "_____" + data.getBoolean("build"));
                        bool_updatecheck = data.getBoolean("build");
                        if (bool_updatecheck) {

                            if (data.getString("zap_username").length() >= 4) {
                                bool_loginstatus = true;
                                SharedPreferences settings1 = getSharedPreferences("LoginSession",
                                        Context.MODE_PRIVATE);

                                SharedPreferences.Editor editor1 = settings1.edit();
                                editor1.putBoolean("LoginStatus", true);
                                editor1.apply();

                                editor1.putInt("USER_ID", data.getInt("user_id"));
                                editor1.putString("USER_ZAPNAME", data.getString("zap_username"));
                                editor1.putString("USER_NAME", data.getString("username"));
                                editor1.putString("USER_PROFILEPIC", data.getString("profile_pic"));
                                editor1.putString("USER_EMAIL", data.getString("email"));
                                editor1.putString("USER_FULLNAME", data.getString("full_name"));
                                editor1.putString("USER_TYPE", data.getString("user_type"));
                                editor1.putString("USER_PHONENUMBER", data.getString("phone_number"));
                                editor1.putBoolean("LOGIN_STATUS", true);
                                editor1.apply();

                                if (data.has("testimonial")) {
                                    try {
                                        JSONObject testimonial = data.getJSONObject("testimonial");
                                        String quote = testimonial.getString("text");
                                        String author = testimonial.getString("user");
                                        String location = testimonial.getString("location");
                                        SharedPreferences Testimonialsettings = getSharedPreferences("TestimonialSession",
                                                Context.MODE_PRIVATE);

                                        SharedPreferences.Editor editor2 = Testimonialsettings.edit();
                                        editor2.putBoolean("TestimonialStatus", true);
                                        editor2.putString("Quote", quote);
                                        editor2.putString("Author", author);
                                        editor2.putString("Location", location);
                                        editor2.apply();
                                    } catch (Exception e) {

                                    }
                                }
                                JSONObject superprop = new JSONObject();
                                superprop.put("Session_Start", startdatetime);
                                superprop.put("User Type", data.getString("user_type"));
                                superprop.put("User Name", data.getString("zap_username"));
                                superprop.put("platfrom", "Android");
                                mixpanel.registerSuperPropertiesOnce(superprop);
                                String gcmtoken = settings1.getString("GcMToken", "");
                                int profile_completed = data.getInt("profile_completed");
                                int intfilter = 0;
                                int intdisapprove = 0;
                                try {
                                    intfilter = ExternalFunctions.referalmsg.length();
                                } catch (Exception e) {
                                    intfilter = 0;
                                }

                                try {
                                    intdisapprove = ExternalFunctions.disapprovemsg.length();
                                } catch (Exception e) {
                                    intdisapprove = 0;
                                }

                                if (intfilter > 1) {
                                    Intent earncash = new Intent(SplashScreen.this, EarnCash.class);
                                    startActivity(earncash);
                                    finish();
                                } else if (intdisapprove > 1) {
                                    Intent notif = new Intent(SplashScreen.this, Notifications.class);
                                    startActivity(notif);
                                    finish();
                                } else if (gcmtoken.contains("dummygcm") || gcmtoken.isEmpty()) {

                                    if (profile_completed > 0) {
                                        if (bool_loginstatus) {
                                            if (profile_completed == 1) {


                                                Intent service = new Intent(Intent.ACTION_SYNC, null, getApplicationContext(), MyGcmRegistrationService.class);


                                                startService(service);
                                                Intent onboarding1 = new Intent(SplashScreen.this, LoginHandler.class);
                                                onboarding1.putExtra("activity", "SplashScreen");
                                                startActivity(onboarding1);
                                                finish();

                                            } else if (profile_completed == 2) {
                                                Intent service = new Intent(Intent.ACTION_SYNC, null, getApplicationContext(), MyGcmRegistrationService.class);


                                                startService(service);
                                                Intent onboardin2 = new Intent(SplashScreen.this, Onboarding2.class);
                                                onboardin2.putExtra("booltype", true);
                                                onboardin2.putExtra("activity", "SplashScreen");
                                                startActivity(onboardin2);
                                                finish();
                                            } else if (profile_completed == 3) {
                                                Intent service = new Intent(Intent.ACTION_SYNC, null, getApplicationContext(), MyGcmRegistrationService.class);


                                                startService(service);
                                                Intent onboardin3 = new Intent(SplashScreen.this, Onboarding3.class);
                                                onboardin3.putExtra("booltype", true);
                                                onboardin3.putExtra("activity", "SplashScreen");
                                                startActivity(onboardin3);
                                                finish();
                                            } else if (profile_completed == 4) {
                                                Intent service = new Intent(Intent.ACTION_SYNC, null, getApplicationContext(), MyGcmRegistrationService.class);


                                                startService(service);
                                                Intent onboardin4 = new Intent(SplashScreen.this, Onboarding4.class);
                                                onboardin4.putExtra("booltype", true);
                                                onboardin4.putExtra("activity", "SplashScreen");
                                                startActivity(onboardin4);
                                                finish();
                                            } else {
                                                Intent service = new Intent(Intent.ACTION_SYNC, null, getApplicationContext(), MyGcmRegistrationService.class);
                                                startService(service);
                                                System.out.println("insidegcmserver1230");
                                                Intent onboarding5 = new Intent(SplashScreen.this, HomePageNew.class);
                                                onboarding5.putExtra("activity", "SplashScreen");
                                                startActivity(onboarding5);
                                                finish();
                                            }
                                        }
                                    } else {
                                        registerInBackground(profile_completed);
                                    }
                                } else {
                                    if (profile_completed == 1) {

                                        Intent onboarding1 = new Intent(SplashScreen.this, LoginHandler.class);
                                        onboarding1.putExtra("activity", "SplashScreen");
                                        startActivity(onboarding1);
                                        finish();

                                    } else if (profile_completed == 2) {
                                        Intent onboardin2 = new Intent(SplashScreen.this, Onboarding2.class);
                                        onboardin2.putExtra("booltype", true);
                                        onboardin2.putExtra("activity", "SplashScreen");
                                        startActivity(onboardin2);
                                        finish();
                                    } else if (profile_completed == 3) {
                                        Intent onboardin3 = new Intent(SplashScreen.this, Onboarding3.class);
                                        onboardin3.putExtra("booltype", true);
                                        onboardin3.putExtra("activity", "SplashScreen");
                                        startActivity(onboardin3);
                                        finish();
                                    } else if (profile_completed == 4) {
                                        Intent onboardin4 = new Intent(SplashScreen.this, Onboarding4.class);
                                        onboardin4.putExtra("booltype", true);
                                        onboardin4.putExtra("activity", "SplashScreen");
                                        startActivity(onboardin4);
                                        finish();
                                    } else {

                                        Intent onboarding5 = new Intent(SplashScreen.this, HomePageNew.class);
                                        onboarding5.putExtra("activity", "SplashScreen");
                                        startActivity(onboarding5);
                                        finish();
                                    }
                                }

                            } else {
                                bool_loginstatus = false;
                                JSONObject data1 = resp.getJSONObject("data");
                                Boolean show_guest = false;
                                if (data1.has("show_guest")) {
                                    show_guest = data.getBoolean("show_guest");
                                }
                                if (data1.has("referral") && data1.getBoolean("referral")) {
                                    referralCheck = true;
                                }

                                if (data1.has("testimonial")) {
                                    try {
                                        JSONObject testimonial = data1.getJSONObject("testimonial");
                                        String quote = testimonial.getString("text");
                                        String author = testimonial.getString("user");
                                        String location = testimonial.getString("location");
                                        SharedPreferences Testimonialsettings = getSharedPreferences("TestimonialSession",
                                                Context.MODE_PRIVATE);

                                        SharedPreferences.Editor editor1 = Testimonialsettings.edit();
                                        editor1.putBoolean("TestimonialStatus", true);
                                        editor1.putString("Quote", quote);
                                        editor1.putString("Author", author);
                                        editor1.putString("Location", location);
                                        editor1.apply();
                                    } catch (Exception e) {

                                    }
                                }

                                SharedPreferences settings1 = getSharedPreferences("LoginSession",
                                        Context.MODE_PRIVATE);

                                SharedPreferences.Editor editor1 = settings1.edit();
                                editor1.putBoolean("LoginStatus", false);
                                editor1.putBoolean("GuestLoginStatus", show_guest);
                                editor1.apply();

                                if (!bool_gcmcheck) {
                                    editor1.putString("GcMToken", "dummygcm");
                                    editor1.apply();
                                    registerInBackground(0);
                                } else {

                                    String gcmtoken = settings1.getString("GcMToken", "");
                                    if (gcmtoken.contains("dummygcm") || gcmtoken.isEmpty()) {
                                        registerInBackground(0);
                                    } else {
                                        Intent login = new Intent(SplashScreen.this, HomePageNew.class);
                                        login.putExtra("booltype", true);
                                        startActivity(login);
                                        finish();
                                    }
                                }

                            }

                        } else {
                            System.out.println("BUILDNUMBER : firt else");
                            bool_updatecheck = false;
                            Intent force_update = new Intent(SplashScreen.this, ForceUpdate.class);
                            startActivity(force_update);
                            finish();
                        }

                    } else {
                        System.out.println("BUILDNUMBER : second else");
                        JSONObject data = resp.getJSONObject("data");
                        bool_updatecheck = data.getBoolean("build");
                        if (bool_updatecheck) {
                            Boolean show_guest = false;
                            if (data.has("show_guest")) {
                                show_guest = data.getBoolean("show_guest");
                            }
                            if (data.has("referral") && data.getBoolean("referral")) {
                                referralCheck = true;
                            }

                            if (data.has("testimonial")) {
                                try {
                                    JSONObject testimonial = data.getJSONObject("testimonial");
                                    String quote = testimonial.getString("text");
                                    String author = testimonial.getString("user");
                                    String location = testimonial.getString("location");
                                    SharedPreferences Testimonialsettings = getSharedPreferences("TestimonialSession",
                                            Context.MODE_PRIVATE);

                                    SharedPreferences.Editor editor1 = Testimonialsettings.edit();
                                    editor1.putBoolean("TestimonialStatus", true);
                                    editor1.putString("Quote", quote);
                                    editor1.putString("Author", author);
                                    editor1.putString("Location", location);
                                    editor1.apply();
                                } catch (Exception e) {

                                }
                            }

                            SharedPreferences settings1 = getSharedPreferences("LoginSession",
                                    Context.MODE_PRIVATE);

                            SharedPreferences.Editor editor1 = settings1.edit();
                            editor1.putBoolean("LoginStatus", false);
                            editor1.putBoolean("GuestLoginStatus", show_guest);
                            editor1.apply();

                            if (!bool_gcmcheck) {
                                editor1.putString("GcMToken", "dummygcm");
                                editor1.apply();
                                registerInBackground(0);
                            } else {

                                String gcmtoken = settings1.getString("GcMToken", "");
                                if (gcmtoken.contains("dummygcm") || gcmtoken.isEmpty()) {
                                    registerInBackground(0);
                                } else {
                                    Intent login = new Intent(SplashScreen.this, HomePageNew.class);
                                    login.putExtra("booltype", true);
                                    startActivity(login);
                                    finish();
                                }
                            }
                        } else {
                            bool_updatecheck = false;
                            JSONArray features = data.getJSONArray("features");
                            System.out.println("FEATURES : "+features);
                            Intent force_update = new Intent(SplashScreen.this, ForceUpdate.class);
                            force_update.putExtra("Features", features.toString());
                            startActivity(force_update);
                            finish();
                        }

                    }
                } catch (Exception e) {
                    System.out.println("BUILDNUMBER : main catch : " + e);
                }
            } else {
                 CustomMessage.getInstance().CustomMessage(SplashScreen.this, "Oops. Something went wrong!");
            }
        }


    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
        finish();
        finish();

    }

    @Override
    protected void onDestroy() {
        try {

            today = new Date();
            DateFormat df = new SimpleDateFormat("dd-MM-yy HH:mm:ss aa");
            df.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
            String closedatetime = df.format(today);

            DateFormat dfclosetime = new SimpleDateFormat("hh:mm:ss aa");
            dfclosetime.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
            String strclosetime = dfclosetime.format(today);
            RelativeLayout v = (RelativeLayout) findViewById(R.id.splashScreen_layout);
            if (v != null) {
                if (v.getBackground() != null) v.getBackground().setCallback(null);
            }
            Mixpanelutils mx = new Mixpanelutils();
            JSONObject superprop = new JSONObject();
            if (isInternetOn()) {
                try {
                    superprop.put("Time spent", mx.getTimeDiff(starttime, strclosetime));
                    superprop.put("Event Name", "Time spent on app");
                    mixpanel.track("Time spent on app", superprop);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            JSONObject propcloseapp = new JSONObject();
            try {
                propcloseapp.put("Closed time", closedatetime);
                propcloseapp.put("Event Name", "Close app");
                mixpanel.track("Close app", propcloseapp);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            mixpanel.flush();

            Runtime runtime = Runtime.getRuntime();
        } catch (Exception e) {

        }

        super.onDestroy();


    }

    @Override
    public void onErrorCallback(VolleyError error, String flag) {
        if (flag.equals("getcsrf")) {
            SharedPreferences settings = getSharedPreferences("LoginSession",
                    Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = settings.edit();
            editor.putString("csrfToken", csrf_token);
            editor.apply();
            BuildNumberCheck();

        } else if (flag.equals("referral")) {
            SharedPreferences settings = getSharedPreferences("LoginSession",
                    Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("Referral", false);
            editor.apply();
//            LoginCheck();
        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {

    }


    @Override
    protected void onRestart() {
        super.onRestart();
        if (!GetSharedValues.GetgcmId(SplashScreen.this).equals("")) {
            ApiService.getInstance(SplashScreen.this, 1).getData(SplashScreen.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/?gcm_reg_id=" + GetSharedValues.GetgcmId(SplashScreen.this), "session");
        } else {
            ApiService.getInstance(SplashScreen.this, 1).getData(SplashScreen.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/", "session");
        }
    }
    private boolean checkPlayServices(Activity activityContext) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(activityContext);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(activityContext, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                //  finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.set("UserName", GetSharedValues.getUsername(getApplicationContext()));mTracker.set("ZapUsername", GetSharedValues.getZapname(getApplicationContext()));
        mTracker.setScreenName(GetSharedValues.getZapname(getApplicationContext()) + "~" + "Splash Screen");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


}
package activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
//import com.//Appsee.//Appsee;
import com.appvirality.CampaignHandler;
import com.appvirality.android.AppviralityAPI;
import com.appvirality.android.CampaignDetails;
import com.appvirality.wom.ReferrerDetails;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
//import com.uxcam.UXCam;
import com.zapyle.zapyle.AppEula;
import com.zapyle.zapyle.ApplicationData;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import DataBase.DatabaseDB;
import application.MyApplicationClass;
import network.ApiCommunication;
import network.ApiService;


import notifications.MyGcmRegistrationService;
import services.GcmRegService;
import utils.CommonFinish;
import utils.CustomMessage;
import utils.FacebookCustomLoginButton;
import utils.ExternalFunctions;
import utils.FontUtils;
import utils.GetSharedValues;
import utils.InstagramApp;
import utils.Mixpanelutils;


public class Parallax extends Activity implements ApiCommunication {
//    -------------------------------------

    private TextView mTextDetails;
    private CallbackManager mCallbackManager;
    private AccessTokenTracker mTokenTracker;
    private ProfileTracker mProfileTracker;
    private ProgressDialog mProgress;
    MixpanelAPI mixpanel;
    Date today;
    DatabaseDB db;


    String SCREEN_NAME = "SOCIALLOGIN";


    //    Vars For Insta
    private InstagramApp mApp;
    private Button btnConnect;
    private TextView tvSummary;


    GoogleCloudMessaging gcm;
    public static String regId;
    private LinearLayout l1cont, l1viral;
    RelativeLayout testimonialLayout;
    private Button email_button, signup_button;
    private TextView terms, GuestLogin;
    Typeface fb;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    //    ----------------------------------
    private static final int MAX_PAGES = 1;

    public int[] intBackground;
    public int[] intBackground1;
    public int[] intBackground2;
    //    public static TextView tvhead, tvdesc;
//    public static ImageView img1, img2, img3;
    boolean booltype;
    TextView tvrefer;

    public static Context CommonContext;
    private static ReferrerDetails referrerDetails;
    boolean hasReferrer;
    boolean isExistingUser;
    Boolean TestimonialStatus = false;
    int profileCompleted = 0;
    Tracker mTracker;
    private BroadcastReceiver mRegistrationBroadcastReceiver;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.parallax);
        MyApplicationClass application = (MyApplicationClass) getApplication();
        mTracker = application.getDefaultTracker();
        //Appsee.start("4fb55809ab45411c909521bda720f8e6");
        //UXCam.startWithKey("1dfb25141864376");
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        //////Thread.currentThread().setDefaultUncaughtExceptionHandler(new SplashScreen.MyUncaughtExceptionHandler());
        LocalBroadcastManager.getInstance(this).registerReceiver(CommonFinish.broadcastReceiver, new IntentFilter("Parallax"));
//        if(!MyApplicationClass.cContextArray.contains(getApplicationContext())) {
        ExternalFunctions.cContextArray[1] = Parallax.this;
//        }

//        GcmReg();

        //        Database function
//        ----------------------------------------------------

        db = new DatabaseDB(getApplicationContext());
        db.openDB();

        ExternalFunctions.sort = 0;
        mCallbackManager = CallbackManager.Factory.create();
        setupTokenTracker();
        setupProfileTracker();
        mTokenTracker.startTracking();
        mProfileTracker.startTracking();
//        -------------------------------------------------------
        Bundle bundle = this.getIntent().getExtras();
        booltype = bundle.getBoolean("booltype");
        mixpanel = MixpanelAPI.getInstance(Parallax.this, getResources().getString(R.string.mixpanelToken));

        final ReferrerDetails referrerDetails = CampaignHandler.getReferrerDetails();
        email_button = (Button) findViewById(R.id.email_button);
        signup_button = (Button) findViewById(R.id.signup_button);
        email_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signup = new Intent(Parallax.this, EmailLogin.class);
                signup.putExtra("activity", "Parallax");
                signup.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(signup);
                finish();

            }
        });


        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signup = new Intent(Parallax.this, Signup.class);
                signup.putExtra("activity", "Parallax");
                startActivity(signup);
                finish();

            }
        });

        email_button.setText(" Sign in");
        setupLoginButton();
        setupTextDetails();
        mProgress = new ProgressDialog(Parallax.this);
        mProgress.setCancelable(false);
        l1cont = (LinearLayout) findViewById(R.id.l1continue);
        terms = (TextView) findViewById(R.id.terms);
        GuestLogin = (TextView) findViewById(R.id.GuestLogin);
        SharedPreferences loginsettings = getSharedPreferences("LoginSession",
                Context.MODE_PRIVATE);
        Boolean GuestLoginStatus = loginsettings.getBoolean("GuestLoginStatus", false);
        //System.out.println("GuestLoginStatus:" + GuestLoginStatus);

//        if (booltype == false) {
////            ////System.out.println("GuestLoginStatus: inside first if");
////            if (EmailLogin.k == 1) {
////                ////System.out.println("GuestLoginStatus: inside second if");
////                if (GuestLoginStatus) {
////                    ////System.out.println("GuestLoginStatus: inside third if");
////                   l1cont.setVisibility(View.VISIBLE);
////                    EmailLogin.k = 0;
////                } else {
////                    ////System.out.println("GuestLoginStatus: inside first else");
////                    //l1cont.setVisibility(View.INVISIBLE);
////                }
////            } else {
////                ////System.out.println("GuestLoginStatus: inside second else");
////              //  l1cont.setVisibility(View.INVISIBLE);
////            }
//
//        } else {
        if (GuestLoginStatus) {
            //System.out.println("GuestLoginStatus: inside third if");
            l1cont.setVisibility(View.VISIBLE);
            EmailLogin.k = 0;
        } else {
            //System.out.println("GuestLoginStatus: inside first else");
            l1cont.setVisibility(View.INVISIBLE);
        }
//        }

        GuestLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent feed = new Intent(Parallax.this, HomePageNew.class);
                feed.putExtra("activity", "Continue");
                feed.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(feed);
                finish();
            }
        });


        l1cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent feed = new Intent(Parallax.this, HomePageNew.class);
                feed.putExtra("activity", "Continue");
                feed.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(feed);
            }
        });
        String sourceString = "By joining you agree to our" + "<b>" + " Terms and Privacy Policy" + "</b> ";
        terms.setText(Html.fromHtml(sourceString));
        terms.setTextColor(Color.GRAY);
        SpannableString ss = new SpannableString(terms.getText());
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                new AppEula(Parallax.this).show();
            }
        };

        ss.setSpan(clickableSpan, 28, 53, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(Color.GRAY), 28, 53, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

        terms.setText(ss);
        terms.setMovementMethod(LinkMovementMethod.getInstance());

        mApp = new InstagramApp(this, ApplicationData.CLIENT_ID,
                ApplicationData.CLIENT_SECRET, ApplicationData.CALLBACK_URL);
        mApp.setListener(listener);

        tvSummary = (TextView) findViewById(R.id.text);
        tvrefer = (TextView) findViewById(R.id.tvreferalname);
        l1viral = (LinearLayout) findViewById(R.id.l1viral);

        btnConnect = (Button) findViewById(R.id.insta_button);
        btnConnect.setTransformationMethod(null);
        btnConnect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mApp.authorize();
            }
        });

        testimonialLayout = (RelativeLayout) findViewById(R.id.testimonialLayout);
        float j = getResources().getDisplayMetrics().density;

//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        lp.setMargins(30, 0, 30, (int) j * 20);
        ////System.out.println("DENSITY:" + j);


        try {
            AppviralityAPI.getInstance(getApplicationContext(), new AppviralityAPI.UserInstance() {
                @Override
                public void onInstance(AppviralityAPI instance) {

                    hasReferrer = instance.hasReferrer();
                    isExistingUser = AppviralityAPI.isExistingUser();
                    ////System.out.println("EXIST:" + hasReferrer + "___" + isExistingUser);
                    Log.i("AV isExisting User: ", "" + isExistingUser);
                    String refCode = AppviralityAPI.getFriendReferralCode();
                    Log.i("AV isExisting User: ", "" + refCode);

                }
            });

        } catch (Exception e) {

            ////System.out.println("Error" + e.getMessage());
        }
        AppviralityAPI.setReferrerDetailsHandler(new AppviralityAPI.ReferrerDetailsListner() {
            @Override
            public void onReferrerDetailsReady(ReferrerDetails referrerDetails) {
                try {
                    setReferrerDetails(referrerDetails);
                    ////System.out.println("EXIST:Inside second function");
                    if (!isExistingUser && hasReferrer) {
                        TestimonialStatus = true;
                        tvrefer.setText(referrerDetails.ReferrerName.toString());
                    }


                } catch (ActivityNotFoundException e) {
                    // ////Log.e("AppviralitySDK", "please add WelcomeScreenActivity in your manifest");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        if (TestimonialStatus) {
            ////System.out.println("EXIST:Inside if");
            l1viral.setVisibility(View.VISIBLE);
            testimonialLayout.setVisibility(View.GONE);
//            Log.i("AV isExisting User: ", "" + referrerDetails.ReferrerName.toString());
        } else {
            ////System.out.println("EXIST:Inside else");
            ////System.out.println("ssssssssssssss");

            SharedPreferences Testimonialsettings = getSharedPreferences("TestimonialSession",
                    Context.MODE_PRIVATE);
            ////System.out.println("tttttttttttt:" + Testimonialsettings.getString("Quote", "") + "___" + "-" + Testimonialsettings.getString("Author", "") + ", " + Testimonialsettings.getString("Location", ""));
            if (!Testimonialsettings.getString("Quote", "").equals("") || !Testimonialsettings.getString("Quote", "").isEmpty()) {
                testimonialLayout.setVisibility(View.VISIBLE);
                l1viral.setVisibility(View.GONE);
                TextView TestimonialText = (TextView) findViewById(R.id.TestimonialText);
//                View view = findViewById(R.id.upload1_layout);
                FontUtils.setPlayfairDisplayFont(TestimonialText, getAssets());
                TextView TestimonialAuthor = (TextView) findViewById(R.id.TestimonialAuthor);
                TextView TestimonialZapAuthor = (TextView) findViewById(R.id.TestimonialZapAuthor);
                TestimonialText.setText(Testimonialsettings.getString("Quote", ""));
                TestimonialAuthor.setText(Testimonialsettings.getString("Author", ""));
                TestimonialZapAuthor.setText("@" + Testimonialsettings.getString("Author", ""));
                TestimonialAuthor.setVisibility(View.GONE);

            }

        }
    }





    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // //////System.out.println("cccc" + pos);
    }

    public static ReferrerDetails getReferrerDetails() {
        return referrerDetails;
    }

    public static void setReferrerDetails(ReferrerDetails details) {
        referrerDetails = details;
    }


    //    Facebook functions
//    -----------------------------------------------------------------
    InstagramApp.OAuthAuthenticationListener listener = new InstagramApp.OAuthAuthenticationListener() {

        @Override
        public void onSuccess() {
            //////System.out.println("inside oauthlistener__"+mApp);
            //////System.out.println("inside insta listener__"+mApp.getAccessToken());
            mProgress.setMessage("Loading ...");
            mProgress.show();
            sendInstaData(mApp.getAccessToken());
        }

        @Override
        public void onFail(String error) {
            //////System.out.println("inside insta listener error__" + error);
        }
    };

    private FacebookCallback<LoginResult> mCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {

            System.out.println("response fb login : "+loginResult);
            AccessToken accessToken = loginResult.getAccessToken();
            Profile profile = Profile.getCurrentProfile();
//            mTextDetails.setText(constructWelcomeMessage(profile));
            mProgress.setMessage("Loading ...");
            mProgress.show();
            sendData(accessToken);

        }

        @Override
        public void onCancel() {
            System.out.println("OnCancel");
        }

        @Override
        public void onError(FacebookException e) {
            System.out.println("OnError :" + e);
        }
    };


    private void sendData(AccessToken accessToken) {
        SharedPreferences settings = getSharedPreferences("LoginSession",
                Context.MODE_PRIVATE);
        String gcm_id = settings.getString("GcMToken", "");

        System.out.println("gcm___"+gcm_id);

        final JSONObject loginObject = new JSONObject();
        try {
            loginObject.put("access_token", accessToken.getToken());
            loginObject.put("logged_from", "fb");
            loginObject.put("logged_device", "android");
            loginObject.put("gcm_reg_id", gcm_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiService.getInstance(Parallax.this, 1).postData(Parallax.this, EnvConstants.APP_BASE_URL + "/account/login/", loginObject, SCREEN_NAME, "login");

    }


    private void setupTextDetails() {
//        mTextDetails = (TextView) findViewById(R.id.text_details);
    }


    private void setupTokenTracker() {
        mTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                //////System.out.println(currentAccessToken);
            }
        };
    }


    private void setupProfileTracker() {
        mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
//                mTextDetails.setText(constructWelcomeMessage(currentProfile));
            }
        };
    }


    private void setupLoginButton() {
        FacebookCustomLoginButton mButtonLogin = (FacebookCustomLoginButton) findViewById(R.id.login_button);
        //////System.out.println(mButtonLogin.getTextSize() + "__mButtonLogin.getTextSize()");
        mButtonLogin.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
//        mButtonLogin.setFragment(Parallax.this);
        mButtonLogin.setPadding(0, 0, 0, 0);
//        mButtonLogin.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.fb_margin_override_textpadding));
        mButtonLogin.setReadPermissions(Arrays.asList("public_profile, email, user_birthday, user_friends"));
        mButtonLogin.registerCallback(mCallbackManager, mCallback);
    }


    private String constructWelcomeMessage(Profile profile) {
        StringBuffer stringBuffer = new StringBuffer();
        if (profile != null) {
            stringBuffer.append("");
        }
        return stringBuffer.toString();
    }


//    Instagram functions
//    ------------------------------------------------------


    private void sendInstaData(String accessToken) {
        SharedPreferences settings = getSharedPreferences("LoginSession",
                Context.MODE_PRIVATE);
        String gcm_id = settings.getString("GcMToken", "");

        //////System.out.println("gcm___"+gcm_id);
        final JSONObject loginObject = new JSONObject();
        try {
            loginObject.put("access_token", accessToken);
            loginObject.put("logged_from", "instagram");
            loginObject.put("logged_device", "android");
            loginObject.put("gcm_reg_id", gcm_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiService.getInstance(Parallax.this, 1).postData(Parallax.this, EnvConstants.APP_BASE_URL + "/account/login/", loginObject, SCREEN_NAME, "login");

    }

//    Other functions
//    ----------------------------------------------------------------------------------------


    private void GetUserDetails() {
        ApiService.getInstance(Parallax.this, 1).getData(Parallax.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/mydetails/", "loginCheck");

    }


    public void GetCartCount() {
        ApiService.getInstance(Parallax.this, 1).getData(Parallax.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/zapcart/?action=count", "cartcount");
    }


// Responses
//    ----------------------------------------------------------------------------------------

    @Override
    public void onResponseCallback(JSONObject response, String flag) {
        if (flag.equals("login")) {
            System.out.println("response  :"+response);
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            try {
                String status = resp.getString("status");
                if (status.equals("success")) {
                    JSONObject data = resp.getJSONObject("data");
                    String SessionId = data.getString("session_id");
                    SharedPreferences settings = this.getSharedPreferences("LoginSession",
                            Context.MODE_PRIVATE);

                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("session_id", SessionId);
                    editor.putBoolean("loginStatus", true);
                    editor.apply();
                    GetUserDetails();
                } else {
                     CustomMessage.getInstance().CustomMessage(Parallax.this, "Oops. Something went wrong!");
                }
            } catch (Exception e) {
                 CustomMessage.getInstance().CustomMessage(Parallax.this, "Oops. Something went wrong!");
            }
        } else if (flag.equals("loginCheck")) {
            System.out.println("response"+response);
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);

            if (resp != null) {
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {

                        JSONObject data = resp.getJSONObject("data");
                        SharedPreferences settings = this.getSharedPreferences("LoginSession",
                                Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putInt("USER_ID", data.getInt("user_id"));
                        editor.putString("USER_ZAPNAME", data.getString("zap_username"));
                        editor.putString("USER_NAME", data.getString("username"));
                        editor.putString("USER_PROFILEPIC", data.getString("profile_pic"));
                        editor.putString("USER_EMAIL", data.getString("email"));
                        editor.putString("USER_FULLNAME", data.getString("full_name"));
                        editor.putString("USER_TYPE", data.getString("user_type"));
                        editor.putString("USER_PHONENUMBER", data.getString("phone_number"));
                        editor.putBoolean("LOGIN_STATUS", true);
                        editor.apply();


                        JSONObject superprop = new JSONObject();
                        superprop.put("Session_Start", SplashScreen.startdatetime);
                        superprop.put("User Type", data.getString("user_type"));
                        superprop.put("User Name", data.getString("zap_username"));
                        superprop.put("platfrom", "Android");
                        mixpanel.registerSuperPropertiesOnce(superprop);

                        profileCompleted = data.getInt("profile_completed");
                        //System.out.println("CART CHECK out:"+db.getTableRecordsCount("CART"));
                        if (db.getTableRecordsCount("CART") > 0) {
                            //System.out.println("CART CHECK in if:"+db.getTableRecordsCount("CART"));
                            UpdateCart();
                        } else {
                            //System.out.println("CART CHECK in else:"+db.getTableRecordsCount("CART"));
                            GetCartCount();
                        }


                    } else {
                         CustomMessage.getInstance().CustomMessage(Parallax.this, "Oops. Something went wrong!");
                    }
                } catch (Exception e) {
                    //////System.out.println("Error in response object!");
                     CustomMessage.getInstance().CustomMessage(Parallax.this, "Oops. Something went wrong!");

                }
            } else {
                 CustomMessage.getInstance().CustomMessage(Parallax.this, "Oops. Something went wrong!");
            }
        } else if (flag.equals("onboarding1")) {
            //////System.out.println(response);
        } else if (flag.equals("cart")) {
            //System.out.println("CART:" + response);
            GetCartCount();
        } else if (flag.equals("cartcount")) {
            //System.out.println("CART:" + response);
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            try {
                String status = resp.getString("status");
                //System.out.println("CART COUNT parallax:" + "inside success");
                if (status.equals("success")) {
                    JSONObject data = resp.getJSONObject("data");
                    //System.out.println("CART COUNT parallax:" + data.getInt("count"));
                    ExternalFunctions.cartcount = data.getInt("count");
                    String deleteQuery = "delete from CART";
                    db.processData(deleteQuery);
                    if (profileCompleted == 1) {
                        Intent onboarding1 = new Intent(Parallax.this, LoginHandler.class);
                        onboarding1.putExtra("activity", "SplashScreen");
                        startActivity(onboarding1);
                        finish();
                    } else if (profileCompleted == 2) {

                        Intent onboardin2 = new Intent(Parallax.this, Onboarding2.class);
                        onboardin2.putExtra("booltype", true);
                        startActivity(onboardin2);
                        finish();

                    } else if (profileCompleted == 3) {
                        Intent onboarding3 = new Intent(Parallax.this, Onboarding3.class);
                        onboarding3.putExtra("booltype", true);
                        startActivity(onboarding3);
                        finish();

                    } else if (profileCompleted == 4) {
                        Intent onboardin4 = new Intent(Parallax.this, Onboarding4.class);
                        onboardin4.putExtra("booltype", true);
                        startActivity(onboardin4);
                        finish();

                    } else {
                        Intent onboarding5 = new Intent(Parallax.this, HomePageNew.class);
                        onboarding5.putExtra("activity", "SplashScreen");
                        onboarding5.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(onboarding5);
                        finish();
                    }

                } else {
                    ExternalFunctions.cartcount = 0;
                    String deleteQuery = "delete from CART";
                    db.processData(deleteQuery);
                    if (profileCompleted == 1) {
                        Intent onboarding1 = new Intent(Parallax.this, LoginHandler.class);
                        onboarding1.putExtra("activity", "SplashScreen");
                        startActivity(onboarding1);
                        finish();
                    } else if (profileCompleted == 2) {

                        Intent onboardin2 = new Intent(Parallax.this, Onboarding2.class);
                        onboardin2.putExtra("booltype", true);
                        startActivity(onboardin2);
                        finish();

                    } else if (profileCompleted == 3) {
                        Intent onboarding3 = new Intent(Parallax.this, Onboarding3.class);
                        onboarding3.putExtra("booltype", true);
                        startActivity(onboarding3);
                        finish();

                    } else if (profileCompleted == 4) {
                        Intent onboardin4 = new Intent(Parallax.this, Onboarding4.class);
                        onboardin4.putExtra("booltype", true);
                        startActivity(onboardin4);
                        finish();

                    } else {
                        Intent onboarding5 = new Intent(Parallax.this, HomePageNew.class);
                        onboarding5.putExtra("activity", "SplashScreen");
                        onboarding5.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(onboarding5);
                        finish();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Intent onboarding5 = new Intent(Parallax.this, HomePageNew.class);
                onboarding5.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                onboarding5.putExtra("activity", "SplashScreen");
                startActivity(onboarding5);
                finish();
            }


        }
    }


//    Update cart function
//    ---------------------------------------------------------

    private void UpdateCart() {

        //System.out.println("CART :"+"inside update cart");
        String query = "SELECT * FROM CART";
        Cursor cursordata = db.getData(query);
        JSONArray ObjectList = new JSONArray();
        if (db.getTableRecordsCount("CART") == 1) {
            String data = cursordata.getString(1).replace("z*p*", "'");
            JSONObject datatemp = null;
            try {
                datatemp = new JSONObject(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONObject dataObj = new JSONObject();
            try {
                dataObj.put("product", datatemp.getInt("product_id"));
                dataObj.put("size", datatemp.getString("product_size_id"));
                dataObj.put("quantity", datatemp.getString("product_quantity"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ObjectList.put(dataObj);
            //System.out.println("CART :" + "inside update cart1:"+ObjectList);

            final JSONObject buyObjectcart = new JSONObject();

            try {
                buyObjectcart.put("cart_data", ObjectList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //System.out.println("CART DATA:" + buyObjectcart);
            ApiService.getInstance(Parallax.this, 1).postData(Parallax.this, EnvConstants.APP_BASE_URL + "/zapcart/", buyObjectcart, SCREEN_NAME, "cart");

        } else {
            while (!cursordata.isAfterLast()) {
                //System.out.println("DATABASE: cursor data2:" + cursordata.getString(1));
                String data = cursordata.getString(1).replace("z*p*", "'");
                JSONObject datatemp = null;
                try {
                    datatemp = new JSONObject(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONObject dataObj = new JSONObject();
                try {
                    dataObj.put("product", datatemp.getInt("product_id"));
                    dataObj.put("size", datatemp.getString("product_size_id"));
                    dataObj.put("quantity", datatemp.getString("product_quantity"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ObjectList.put(dataObj);
                cursordata.moveToNext();

                //System.out.println("CART :" + "inside update cart2:" + ObjectList);

            }

            final JSONObject buyObjectcart = new JSONObject();
            try {
                buyObjectcart.put("cart_data", ObjectList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //System.out.println("CART DATA:" + buyObjectcart);
            ApiService.getInstance(Parallax.this, 1).postData(Parallax.this, EnvConstants.APP_BASE_URL + "/zapcart/", buyObjectcart, SCREEN_NAME, "cart");

        }

   }

    @Override
    public void onBackPressed() {
        mixpanel = MixpanelAPI.getInstance(Parallax.this, getResources().getString(R.string.mixpanelToken));

//        JSONObject superprop2 = new JSONObject();
//        try {
//            superprop2.put("Event Name", "Close app");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        // mixpanel.registerSuperProperties(superprop2);
        today = new Date();
        DateFormat dfclosetime = new SimpleDateFormat("hh:mm:ss aa", Locale.ENGLISH);
        dfclosetime.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        String strclosetime = dfclosetime.format(today);


        Mixpanelutils mx = new Mixpanelutils();


        SharedPreferences settings = getSharedPreferences("MixPanelSession",
                Context.MODE_PRIVATE);
        int count = settings.getInt("scrollCount", 0);
        int productviewCount = settings.getInt("productviewCount", 0);
        JSONObject prop = new JSONObject();
        try {
            prop.put("Time taken", mx.getTimeDiff(SplashScreen.starttime, strclosetime));
            prop.put("number of products viewed in one session", productviewCount);
            prop.put("number of products scrolled through in one session", count);
            prop.put("Event Name", "Close app");
            mixpanel.track("Close app", prop);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("scrollCount", 0);
            editor.putInt("productviewCount", 0);
            editor.apply();


        } catch (JSONException e) {
            e.printStackTrace();
        }


        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
        finish();


    }


    @Override
    public void onErrorCallback(VolleyError error, String flag) {

        if (mProgress.isShowing()) {
            mProgress.dismiss();
        }
    }


//    Activity functions
//    -------------------------------------------------------

    @Override
    public void onResume() {
        super.onResume();
        Intent service = new Intent(Intent.ACTION_SYNC, null, getApplicationContext(), MyGcmRegistrationService.class);


        startService(service);
        mTracker.set("UserName", GetSharedValues.getUsername(getApplicationContext()));mTracker.set("ZapUsername", GetSharedValues.getZapname(getApplicationContext()));   mTracker.setScreenName("Login page");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        Profile profile = Profile.getCurrentProfile();
//        mTextDetails.setText(constructWelcomeMessage(profile));
        AppEventsLogger.activateApp(Parallax.this);
    }


    @Override
    public void onPause() {
        super.onPause();
        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(Parallax.this);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);

    }


    @Override
    public void onStop() {
        super.onStop();
        mTokenTracker.stopTracking();
        mProfileTracker.stopTracking();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //////System.out.println("inside on destroy");

        Runtime.getRuntime().gc();
        //////System.out.println(Runtime.getRuntime().freeMemory()+"__freememory");
        //////System.out.println(Runtime.getRuntime().maxMemory()+"__maxmemory");
        //////System.out.println(Runtime.getRuntime().totalMemory() + "__totalmemory");
        //System.gc();
    }

//    public void unbindDrawables(View view) {
//        if (view.getBackground() != null) {
//            view.getBackground().setCallback(null);
//        }
//        if (view instanceof ViewGroup) {
//            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
//
//                unbindDrawables(((ViewGroup) view).getChildAt(i));
//            }
//            ((ViewGroup) view).removeAllViews();
//
//        }
//
//    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        finish();
        startActivity(getIntent());
    }


    public void onLowMemory() {


        ExternalFunctions.deleteCache(Parallax.this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!GetSharedValues.GetgcmId(Parallax.this).equals("")) {
            ApiService.getInstance(Parallax.this, 1).getData(Parallax.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/?gcm_reg_id=" + GetSharedValues.GetgcmId(Parallax.this), "session");
        } else {
            ApiService.getInstance(Parallax.this, 1).getData(Parallax.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/", "session");
        }
    }



}

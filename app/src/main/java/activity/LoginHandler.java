package activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.exceptions.CleverTapMetaDataNotFoundException;
import com.clevertap.android.sdk.exceptions.CleverTapPermissionsNotSatisfied;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

import application.MyApplicationClass;
import io.branch.referral.Branch;
import network.ApiCommunication;
import network.ApiService;
import utils.CommonFinish;
import utils.CustomMessage;
import utils.ExternalFunctions;
import utils.FontUtils;
import utils.GetSharedValues;

//import com.//Appsee.//Appsee;
//import com.uxcam.UXCam;


public class LoginHandler extends Activity implements ApiCommunication {
    private int CLICK_COUNT = 0;
    private TextView Submit;
    private EditText Username, Email, Phoneno, referral;
    private String email, username, PhoneInput, referral_text;
    SharedPreferences sharedPreferences;
    String SCREEN_NAME = "LOGIN_HANDLER";

    ProgressDialog mProgress;
    Boolean old_user = false;
    Tracker mTracker;


    TextView Error;
    public static String activityname;

    String stime = "";
    String endtime = "";
    CleverTapAPI cleverTap;
    String strReferalId="0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_handler);
        MyApplicationClass application = (MyApplicationClass) getApplication();
        mTracker = application.getDefaultTracker();
        stime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        try {
            cleverTap = CleverTapAPI.getInstance(getApplicationContext());
        } catch (CleverTapMetaDataNotFoundException e) {
            // thrown if you haven't specified your CleverTap Account ID or Token in your AndroidManifest.xml
        } catch (CleverTapPermissionsNotSatisfied e) {
            // thrown if you haven’t requested the required permissions in your AndroidManifest.xml
        }
        //UXCam.startWithKey("1dfb25141864376");         overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        //Appsee.start("4fb55809ab45411c909521bda720f8e6");
        //////Thread.currentThread().setDefaultUncaughtExceptionHandler(new SplashScreen.MyUncaughtExceptionHandler());
        LocalBroadcastManager.getInstance(this).registerReceiver(CommonFinish.broadcastReceiver, new IntentFilter("LoginHandler"));
//        if(!MyApplicationClass.cContextArray.contains(getApplicationContext())) {
        ExternalFunctions.cContextArray[3] = LoginHandler.this;
//        }
        View view = findViewById(R.id.loginhandler);
        FontUtils.setCustomFont(view, getAssets());
        mProgress = new ProgressDialog(LoginHandler.this);
        mProgress.setCancelable(false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences settings = this.getSharedPreferences("LoginSession",
                Context.MODE_PRIVATE);

        Submit = (TextView) findViewById(R.id.login_submit);
        Email = (EditText) findViewById(R.id.editEmail);
        Username = (EditText) findViewById(R.id.userHandler);
        Phoneno = (EditText) findViewById(R.id.userHandlerphone);
        referral = (EditText) findViewById(R.id.referral);
        if (SplashScreen.referralCheck) {
            referral.setVisibility(View.VISIBLE);
        }
        //////System.out.println("username:"+settings.getString("USER_ZAPNAME", ""));
        if (settings.getString("USER_EMAIL", "").toString().trim().length() > 0 || settings.getString("email", "").toString() != null) {
            Email.setText(settings.getString("USER_EMAIL", ""));
        } else {
            Email.setText("");
        }
        if (settings.getString("USER_ZAPNAME", "").toString().trim().length() > 0 || settings.getString("USER_ZAPNAME", "") != null) {
            Username.setText(settings.getString("USER_ZAPNAME", ""));
        } else {
            Username.setText("");
        }
        if (settings.getString("USER_PHONENUMBER", "").toString().trim().length() > 0 || settings.getString("USER_PHONENUMBER", "") != null) {
            Phoneno.setText(settings.getString("USER_PHONENUMBER", ""));
        } else {
            Phoneno.setText("");
        }

        if (Email.getText().toString().contains("null")) {
            Email.setText("");
        }
        if (Username.getText().toString().contains("null")) {
            Username.setText("");
        } else {
            old_user = true;
        }
        if (Phoneno.getText().toString().contains("null")) {
            Phoneno.setText("");
        }

//        if(old_user){
//            Username.setEnabled(false);
//            Email.setEnabled(false);
//        }
        SharedPreferences settings1 = getSharedPreferences("LoginSession",
                Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = settings1.edit();
        editor.putBoolean("GuestLoginStatus", true);
        editor.apply();

        email = Email.getText().toString();
        username = Username.getText().toString();
        PhoneInput = Phoneno.getText().toString();


        Bundle bundle = this.getIntent().getExtras();
        try {
            activityname = bundle.getString("activity");
            strReferalId=bundle.getString("referral_user_id");
        }catch (Exception e){

        }
//      Email Validation

        Email.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

        });


        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = Email.getText().toString();
                username = Username.getText().toString();
                PhoneInput = Phoneno.getText().toString();
                if (username.isEmpty() || email.isEmpty() || PhoneInput.isEmpty()) {
                    CustomMessage.getInstance().CustomMessage(LoginHandler.this, "Fill all the fields!");
                } else if (username.length() < 4) {
                    CustomMessage.getInstance().CustomMessage(LoginHandler.this, "Username should be minimum 6 characters");
                } else if (!isValidEmail(email)) {
                    CustomMessage.getInstance().CustomMessage(LoginHandler.this, "Invalid email!");
                } else if (PhoneInput.length() < 10) {
                    CustomMessage.getInstance().CustomMessage(LoginHandler.this, "Invalid phone number!");
                } else {
                    mProgress.setMessage("Loading ...");
                    mProgress.show();
                    Submit.setEnabled(false);
                    SendHandler();
                }


            }
        });
    }

    public final static boolean isValidEmail(String target) {
        if (target == null) {
            return false;
        } else {
            //android Regex to check the email address Validation
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }


    public void CheckHandler() {
        final JSONObject loginHandlerObject = new JSONObject();
        try {
            loginHandlerObject.put("zap_username", username);
            loginHandlerObject.put("email", email);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiService.getInstance(LoginHandler.this, 1).postData(LoginHandler.this, EnvConstants.APP_BASE_URL + "/user/check_username/", loginHandlerObject, SCREEN_NAME, "checkHandler");
    }

    public void SendHandler() {
        if (SplashScreen.referralCheck) {
            referral_text = referral.getText().toString();
        }
        final JSONObject loginHandlerObject = new JSONObject();
        try {
            loginHandlerObject.put("zap_username", username);
            loginHandlerObject.put("email", email);
            loginHandlerObject.put("phone_number", PhoneInput);
            if (SplashScreen.referralCheck && referral_text.trim().length() > 0) {
                loginHandlerObject.put("referral_code", referral_text);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiService.getInstance(LoginHandler.this, 1).postData(LoginHandler.this, EnvConstants.APP_BASE_URL + "/onboarding/1/", loginHandlerObject, SCREEN_NAME, "sendHandler");
    }

    private void GetUserDetails() {
        ApiService.getInstance(LoginHandler.this, 1).getData(LoginHandler.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/mydetails/", "loginCheck");

    }


    @Override
    protected void onRestart() {
        super.onRestart();
        if (!GetSharedValues.GetgcmId(LoginHandler.this).equals("")) {
            ApiService.getInstance(LoginHandler.this, 1).getData(LoginHandler.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/?gcm_reg_id=" + GetSharedValues.GetgcmId(LoginHandler.this), "session");
        } else {
            ApiService.getInstance(LoginHandler.this, 1).getData(LoginHandler.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/", "session");
        }
    }


    @Override
    public void onResponseCallback(JSONObject response, String flag) {
        if (flag.equals("checkHandler")) {
            JSONObject checkhandler_resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (checkhandler_resp != null) {
                //////System.out.println(checkhandler_resp);
                try {
                    String status = checkhandler_resp.getString("status");
                    if (status.equals("success")) {
                        SendHandler();
//                        Intent size = new Intent(LoginHandler.this, SelectSize.class);
//                        startActivity(size);
                    } else {
                        Submit.setEnabled(true);
                        mProgress.dismiss();
                        JSONObject detail = checkhandler_resp.getJSONObject("detail");
                        if (detail.has("email")) {
                            CustomMessage.getInstance().CustomMessage(LoginHandler.this, "Email is not avalilable!");
                        } else if (detail.has("zap_username")) {
                            CustomMessage.getInstance().CustomMessage(LoginHandler.this, "Username is not available!");
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Submit.setEnabled(true);
                }
            } else {
                Submit.setEnabled(true);
                CustomMessage.getInstance().CustomMessage(LoginHandler.this, "Oops. Something went wrong!");
            }
        } else if (flag.equals("sendHandler")) {
            JSONObject handler_resp = network.JsonParser.getInstance().parserJsonObject(response);
            //////System.out.println("handler response__" + response);
            if (handler_resp != null) {
                try {
                    String status = handler_resp.getString("status");
                    if (status.equals("success")) {
                        String deviceId = Settings.Secure.getString(this.getContentResolver(),
                                Settings.Secure.ANDROID_ID);
                        // Toast.makeText(this, deviceId, Toast.LENGTH_SHORT).show();

                        //Branch.getInstance().userCompletedAction("sign_up");
                        Submit.setEnabled(true);
                        SplashScreen.referralCheck = false;
                        GetUserDetails();
                    } else {
                        Submit.setEnabled(true);
                        mProgress.dismiss();
                        JSONObject detail = handler_resp.getJSONObject("detail");
                        if (detail.has("referral_code")) {

                            String err = detail.getString("referral_code");
                            CustomMessage.getInstance().CustomMessage(this, err);
                            referral.setText("");
                            SplashScreen.referralCheck = false;
                        } else {
//                         CustomMessage.getInstance().CustomMessage(LoginHandler.this, "Oops. Something went wrong!");
                            if (detail.has("email")) {
                                String error_msg = detail.getJSONArray("email").getString(0);
                                CustomMessage.getInstance().CustomMessage(LoginHandler.this, error_msg);
                            } else if (detail.has("zap_username")) {
                                String error_msg = detail.getJSONArray("zap_username").getString(0);
                                CustomMessage.getInstance().CustomMessage(LoginHandler.this, error_msg);
                            } else if (detail.has("phone_number")) {
                                String error_msg = detail.getJSONArray("phone_number").getString(0);
                                CustomMessage.getInstance().CustomMessage(LoginHandler.this, error_msg);
                            }
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Submit.setEnabled(true);
                    mProgress.dismiss();
                    CustomMessage.getInstance().CustomMessage(LoginHandler.this, "Oops. Something went wrong!");

                }

            } else {
                Submit.setEnabled(true);
                CustomMessage.getInstance().CustomMessage(LoginHandler.this, "Oops. Something went wrong!");
            }
        } else if (flag.equals("loginCheck")) {
           System.out.println("loginCheck" + response);
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);

            if (resp != null) {
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        mProgress.dismiss();
                        JSONObject data = resp.getJSONObject("data");
                        SharedPreferences settings = getSharedPreferences("LoginSession",
                                Context.MODE_PRIVATE);

                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("LoginStatus", true);
                        editor.putInt("USER_ID", data.getInt("user_id"));
                        editor.putString("USER_ZAPNAME", data.getString("zap_username"));
                        editor.putString("USER_NAME", data.getString("username"));
                        editor.putString("USER_PROFILEPIC", data.getString("profile_pic"));
                        editor.putString("USER_EMAIL", data.getString("email"));
                        editor.putString("USER_FULLNAME", data.getString("full_name"));
                        editor.putString("USER_TYPE", data.getString("user_type"));
                        editor.putString("USER_PHONENUMBER", data.getString("phone_number"));
                        editor.apply();
                     //   Branch.getInstance().setIdentity(String.valueOf(data.getInt("user_id")));
                        JSONObject metaData = new JSONObject();
                        try {

                            metaData.put("friend_user_id" ,String.valueOf(data.getInt("user_id")));
                            metaData.put("referral_user_id" ,strReferalId);

                        } catch (JSONException e) { }
                        Branch.getInstance(getApplicationContext()).userCompletedAction("sign_up" , metaData);

                        if (old_user) {
                            Username.setEnabled(true);
                            Email.setEnabled(true);
                        }
                        Intent onboardin2 = new Intent(LoginHandler.this, Onboarding2.class);
                        onboardin2.putExtra("booltype", true);
                        startActivity(onboardin2);
                        finish();

                    } else {
                        mProgress.dismiss();
                        CustomMessage.getInstance().CustomMessage(LoginHandler.this, "Oops. Something went wrong!");
                    }
                } catch (Exception e) {
                    mProgress.dismiss();
                    //////System.out.println("Error in response object!");
                    CustomMessage.getInstance().CustomMessage(LoginHandler.this, "Oops. Something went wrong!");

                }
            } else {
                mProgress.dismiss();
                CustomMessage.getInstance().CustomMessage(LoginHandler.this, "Oops. Something went wrong!");
            }
        }
    }

    public void onBackPressed() {


        if (activityname.equals("SplashScreen")) {
//            if (CLICK_COUNT < 1) {
//                CustomMessage.getInstance().CustomMessage(this, "Press again to close the app");
//                CLICK_COUNT=CLICK_COUNT+1;
//            } else {

            CLICK_COUNT = 0;
//                android.os.Process.killProcess(android.os.Process.myPid());
//                super.onDestroy();
            SharedPreferences settings = getSharedPreferences("LoginSession",
                    Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("LOGIN_STATUS", false);
            editor.putString("session_id", "");
            editor.apply();

            Intent parallax = new Intent(LoginHandler.this, Parallax.class);
            parallax.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            EmailLogin.k = 1;
            parallax.putExtra("booltype", false);
            startActivity(parallax);
            finish();

//            }


        } else {
            finish();

        }
    }

//        Log.d("CDA", "onBackPressed Called");
//        Intent setIntent = new Intent(Intent.ACTION_MAIN);
//        setIntent.addCategory(Intent.CATEGORY_HOME);
//        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(setIntent);

    @Override
    public void onErrorCallback(VolleyError error, String flag) {
        if (mProgress.isShowing()) {
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


    public void onLowMemory() {
        unbindDrawables(findViewById(R.id.pager));

        ExternalFunctions.deleteCache(LoginHandler.this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        endtime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        HashMap<String, Object> page_change = new HashMap<String, Object>();
        page_change.put("new_page", SCREEN_NAME);
        page_change.put("old_page", ExternalFunctions.prevActivity);
        page_change.put("page_view_starttime", stime);
        page_change.put("page_view_endtime", endtime);
        cleverTap.event.push("page_change", page_change);
        ExternalFunctions.prevActivity = SCREEN_NAME;
        LinearLayout v = (LinearLayout) findViewById(R.id.loginhandler);
        if (v != null) {
            if (v.getBackground() != null) v.getBackground().setCallback(null);
        }
        View view = findViewById(R.id.loginhandler);
        unbindDrawables(findViewById(R.id.loginhandler));
        Runtime.getRuntime().gc();
        //////System.out.println(Runtime.getRuntime().freeMemory()+"__freememory");
        //////System.out.println(Runtime.getRuntime().maxMemory()+"__maxmemory");
        //////System.out.println(Runtime.getRuntime().totalMemory() + "__totalmemory");
        //System.gc();

    }


    @Override
    protected void onResume() {
        super.onResume();
        mTracker.set("UserName", GetSharedValues.getUsername(getApplicationContext()));
        mTracker.set("ZapUsername", GetSharedValues.getZapname(getApplicationContext()));
        mTracker.setScreenName("Onboarding1");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
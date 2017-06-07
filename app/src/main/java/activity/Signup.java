package activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.SpannableString;
import android.text.method.PasswordTransformationMethod;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.exceptions.CleverTapMetaDataNotFoundException;
import com.clevertap.android.sdk.exceptions.CleverTapPermissionsNotSatisfied;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

import DataBase.DatabaseDB;
import application.MyApplicationClass;
import io.branch.referral.Branch;
import network.ApiCommunication;
import network.ApiService;
import utils.CommonFinish;
import utils.CustomMessage;
import utils.ExternalFunctions;
import utils.FontUtils;
import utils.GetSharedValues;

//import com.uxcam.UXCam;

/**
 * Created by haseeb on 26/5/16.
 */
public class Signup extends Activity implements ApiCommunication {

    private static final String SCREEN_NAME = "SIGN_UP";
    ProgressDialog mProgress;
    String gcm_id;
    EditText sUsername, sEmail, sPassword, firstname, phonenumber;
    TextView signupClick;
    ImageView show_password;
    TextView fullname_error, zapname_error, email_error, phonenumber_error, password_error, signup_error;
    String gender_selected = "F";

    ImageView maleClick, femaleClick;
    String callingActivity = "Parallax";
    DatabaseDB db;
    int profileComplted = 0;
    Tracker mTracker;
    String stime = "";
    String endtime = "";
    CleverTapAPI cleverTap;
    String strReferalId="0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);
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
        //UXCam.startWithKey("1dfb25141864376");
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        SharedPreferences settings = getSharedPreferences("LoginSession",
                Context.MODE_PRIVATE);

        db = new DatabaseDB(getApplicationContext());
        db.openDB();

        try {
            callingActivity = "activity." + getIntent().getStringExtra("activity");

                strReferalId=getIntent().getStringExtra("referral_user_id");


        } catch (Exception e) {
            callingActivity = "activity." + "EmailLogin";

        }

        //////Thread.currentThread().setDefaultUncaughtExceptionHandler(new SplashScreen.MyUncaughtExceptionHandler());
        LocalBroadcastManager.getInstance(this).registerReceiver(CommonFinish.broadcastReceiver, new IntentFilter("Signup"));
        mProgress = new ProgressDialog(Signup.this);
        mProgress.setCancelable(false);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        gcm_id = settings.getString("GcMToken", "");

        sUsername = (EditText) findViewById(R.id.signupname);
        sEmail = (EditText) findViewById(R.id.signupemail);
        firstname = (EditText) findViewById(R.id.fullname);
        sPassword = (EditText) findViewById(R.id.signup_password);
        phonenumber = (EditText) findViewById(R.id.signupphonenumber);

        signupClick = (TextView) findViewById(R.id.signup);
        show_password = (ImageView) findViewById(R.id.show_password);

        fullname_error = (TextView) findViewById(R.id.fullname_error);
        zapname_error = (TextView) findViewById(R.id.zapname_error);
        email_error = (TextView) findViewById(R.id.email_error);
        phonenumber_error = (TextView) findViewById(R.id.phonenumber_error);
        password_error = (TextView) findViewById(R.id.password_error);
        signup_error = (TextView) findViewById(R.id.signup_error);

        maleClick = (ImageView) findViewById(R.id.male_click);
        femaleClick = (ImageView) findViewById(R.id.female_click);
        femaleClick.setImageResource(R.drawable.select_gender_checked);
        gender_selected = "F";


        maleClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gender_selected.contains("F")) {
                    femaleClick.setImageResource(android.R.color.transparent);
                    maleClick.setImageResource(R.drawable.select_gender_checked);
                    gender_selected = "M";
                }
//                } else {
//                    maleClick.setImageResource(android.R.color.transparent);
//                    femaleClick.setImageResource(R.drawable.select_gender_checked);
//                    gender_selected = "F";
//                }
            }
        });

        femaleClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gender_selected.contains("M")) {
                    maleClick.setImageResource(android.R.color.transparent);
                    femaleClick.setImageResource(R.drawable.select_gender_checked);
                    gender_selected = "F";
                }
            }
        });

        fullname_error.setVisibility(View.GONE);
        zapname_error.setVisibility(View.GONE);
        email_error.setVisibility(View.GONE);
        phonenumber_error.setVisibility(View.GONE);
        password_error.setVisibility(View.GONE);
        signup_error.setVisibility(View.GONE);

        sPassword.setTypeface(Typeface.DEFAULT);
        sPassword.setTransformationMethod(new PasswordTransformationMethod());

        SpannableString show = new SpannableString("SHOW");
        show.setSpan(new UnderlineSpan(), 0, show.length(), 0);
        show_password.setImageResource(R.drawable.show_password);

        show_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sPassword.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
                    sPassword.setTransformationMethod(null);
                    show_password.setImageResource(R.drawable.hide_password);
                    sPassword.setSelection(sPassword.getText().length());

                } else {
                    sPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    show_password.setImageResource(R.drawable.show_password);
                    sPassword.setSelection(sPassword.getText().length());
                }
            }
        });

        View view = findViewById(R.id.l1_signup);
        FontUtils.setCustomFont(view, getAssets());

        signupClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Signup REGISTERING");
                mProgress.setMessage("Registering ...");
                mProgress.show();
                SignUp();

            }
        });
    }


    //    Back button functions
//    --------------------------------------------------------
    @Override
    public void onBackPressed() {
        Intent mydialog = null;

        try {
            mydialog = new Intent(Signup.this, Class.forName(callingActivity));
            mydialog.putExtra("activity", "Signup");
            mydialog.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mydialog);
            finish();
        } catch (ClassNotFoundException e) {
            mydialog = new Intent(Signup.this, EmailLogin.class);
            mydialog.putExtra("activity", "Signup");
            mydialog.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mydialog);
            finish();


        }
    }


    //    Update cart function
//    ---------------------------------------------------------

    private void UpdateCart() {
        //System.out.println("CHECK:"+"inside updatecart");
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
            }
        }


        JSONObject buyObjectcart = new JSONObject();

        try {
            buyObjectcart.put("cart_data", ObjectList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //System.out.println("CART DATA:"+buyObjectcart);
        ApiService.getInstance(Signup.this, 1).postData(Signup.this, EnvConstants.APP_BASE_URL + "/zapcart/", buyObjectcart, SCREEN_NAME, "cart");
    }


    //    Signup function
//    -----------------------------------------------
    private void SignUp() {
        fullname_error.setVisibility(View.GONE);
        zapname_error.setVisibility(View.GONE);
        email_error.setVisibility(View.GONE);
        phonenumber_error.setVisibility(View.GONE);
        password_error.setVisibility(View.GONE);
        signup_error.setVisibility(View.GONE);
        String zapname = sUsername.getText().toString();
        String phone_number = phonenumber.getText().toString();
        String email = sEmail.getText().toString();
        String first_name = firstname.getText().toString();
        String password = sPassword.getText().toString();
        Boolean Check = true;


        if (zapname.isEmpty() && email.isEmpty() && first_name.isEmpty() && password.isEmpty()) {
            Check = false;
            mProgress.dismiss();

            if (password.isEmpty()) {
                Check = false;
                mProgress.dismiss();
                sPassword.requestFocus();
                password_error.setVisibility(View.VISIBLE);
                password_error.setText("Please enter password.");
            }

            if (phone_number.isEmpty()) {
                Check = false;
                mProgress.dismiss();
                //  phonenumber.setBackgroundResource(R.drawable.shape_boarderred);
                phonenumber.requestFocus();
                phonenumber_error.setVisibility(View.VISIBLE);
                phonenumber_error.setText("Please enter phonenumber.");


            }


            if (email.isEmpty()) {
                Check = false;
                mProgress.dismiss();
                email_error.setVisibility(View.VISIBLE);
                email_error.setText("Please enter email ID.");
            }
            if (email.isEmpty()) {
                Check = false;
                mProgress.dismiss();
                email_error.setVisibility(View.VISIBLE);
                email_error.setText("Please enter email.");
                //   sEmail.setBackgroundResource(R.drawable.shape_boarderred);
                sEmail.requestFocus();
            } else if (!LoginHandler.isValidEmail(email)) {
                Check = false;
                mProgress.dismiss();
                email_error.setVisibility(View.VISIBLE);
                email_error.setText("Invalid email ID.");
                // sEmail.setBackgroundResource(R.drawable.shape_boarderred);
                sEmail.requestFocus();
            }


            if (zapname.isEmpty()) {
                Check = false;
                mProgress.dismiss();
                zapname_error.setVisibility(View.VISIBLE);
                zapname_error.setText("Please enter full name.");
                sUsername.requestFocus();
            }


            if (first_name.isEmpty()) {
                Check = false;
                mProgress.dismiss();
                fullname_error.setVisibility(View.VISIBLE);
                fullname_error.setText("Please enter full name.");
                firstname.requestFocus();
            }

//                signup_error.setVisibility(View.VISIBLE);
//                signup_error.setText("Please fill all the required fields.");

        } else {
            mProgress.dismiss();


            if (password.isEmpty()) {
                Check = false;
                mProgress.dismiss();
                sPassword.requestFocus();
                password_error.setVisibility(View.VISIBLE);
                password_error.setText("Please enter password.");
            }

            if (phone_number.isEmpty()) {
                Check = false;
                mProgress.dismiss();
                //  phonenumber.setBackgroundResource(R.drawable.shape_boarderred);
                phonenumber.requestFocus();
                phonenumber_error.setVisibility(View.VISIBLE);
                phonenumber_error.setText("Please enter phonenumber.");


            } else if (phone_number.length() < 10) {
                Check = false;
                mProgress.dismiss();
                //  phonenumber.setBackgroundResource(R.drawable.shape_boarderred);
                phonenumber.requestFocus();
                phonenumber_error.setVisibility(View.VISIBLE);
                phonenumber_error.setText("Invalid phone number.");

            }


            if (email.isEmpty()) {
                Check = false;
                mProgress.dismiss();
                email_error.setVisibility(View.VISIBLE);
                email_error.setText("Please enter email ID.");
            }
            if (email.isEmpty()) {
                Check = false;
                mProgress.dismiss();
                email_error.setVisibility(View.VISIBLE);
                email_error.setText("Please enter email.");
                //   sEmail.setBackgroundResource(R.drawable.shape_boarderred);
                sEmail.requestFocus();
            } else if (!LoginHandler.isValidEmail(email)) {
                Check = false;
                mProgress.dismiss();
                email_error.setVisibility(View.VISIBLE);
                email_error.setText("Invalid email ID.");
                // sEmail.setBackgroundResource(R.drawable.shape_boarderred);
                sEmail.requestFocus();
            }


            if (zapname.isEmpty()) {
                Check = false;
                mProgress.dismiss();
                zapname_error.setVisibility(View.VISIBLE);
                zapname_error.setText("Please enter full name.");
                sUsername.requestFocus();
            }


            if (first_name.isEmpty()) {
                Check = false;
                mProgress.dismiss();
                fullname_error.setVisibility(View.VISIBLE);
                fullname_error.setText("Please enter full name.");
                firstname.requestFocus();
            }

        }

        if (Check) {
            signupClick.setEnabled(false);
            final JSONObject loginObject = new JSONObject();
            try {
                loginObject.put("email", email);
                loginObject.put("zap_username", zapname);
                loginObject.put("full_name", first_name);
                loginObject.put("phone_number", phone_number);
                loginObject.put("password", password);
                loginObject.put("sex", gender_selected);
                loginObject.put("logged_from", "zapyle");
                loginObject.put("logged_device", "android");
                loginObject.put("gcm_reg_id", gcm_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            ApiService.getInstance(Signup.this, 1).postData(Signup.this, EnvConstants.APP_BASE_URL + "/account/reducedsignup/", loginObject, SCREEN_NAME, "signup");

        }


    }


//    Getuser details
//    --------------------------------------------------------

    private void GetUserDetails() {
        ApiService.getInstance(Signup.this, 1).getData(Signup.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/mydetails/", "loginCheck");
    }


//    Server responses
//    --------------------------------------------------

    @Override
    public void onResponseCallback(JSONObject response, String flag) {
        //System.out.println(response);
        if (flag.equals("signup")) {
            fullname_error.setVisibility(View.GONE);
            zapname_error.setVisibility(View.GONE);
            email_error.setVisibility(View.GONE);
            phonenumber_error.setVisibility(View.GONE);
            password_error.setVisibility(View.GONE);
            signup_error.setVisibility(View.GONE);
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            try {
                String status = resp.getString("status");
                if (status.equals("success")) {
                    JSONObject data = resp.getJSONObject("data");
                    String SessionId = data.getString("session_id");
                    SharedPreferences settings = getSharedPreferences("LoginSession",
                            Context.MODE_PRIVATE);

                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("session_id", SessionId);
                    editor.putBoolean("loginStatus", true);
                    editor.apply();
                    signupClick.setEnabled(true);
                    SplashScreen.referralCheck = false;
                    GetUserDetails();
                } else {
                    ////////System.out.println("inside jjj");
                    signupClick.setEnabled(true);
                    mProgress.dismiss();
                    JSONObject detail = resp.getJSONObject("detail");
                    if (detail.has("referral_code")) {
                        String err = detail.getString("referral_code");
                        CustomMessage.getInstance().CustomMessage(this, err);
//                        referralText.setText("");
                        SplashScreen.referralCheck = false;
                    } else {

//                        String[] errorlist = {"email", "phone_number", "password", "confirm_password", "first_name", "zap_username", "referral_code"};
                        if (detail.has("password")) {
                            password_error.setVisibility(View.VISIBLE);
                            password_error.setText(detail.getJSONArray("password").getString(0));
                            password_error.requestFocus();
                        }

                        if (detail.has("phone_number")) {
                            phonenumber_error.setVisibility(View.VISIBLE);
                            phonenumber_error.setText(detail.getJSONArray("phone_number").getString(0));
                            phonenumber_error.requestFocus();
                        }

                        if (detail.has("email")) {
                            email_error.setVisibility(View.VISIBLE);
                            email_error.setText(detail.getJSONArray("email").getString(0));
                            email_error.requestFocus();
                        }

                        if (detail.has("first_name")) {
                            fullname_error.setVisibility(View.VISIBLE);
                            fullname_error.setText(detail.getJSONArray("first_name").getString(0));
                            fullname_error.requestFocus();
                        }
                        if (detail.has("zap_username")) {
                            zapname_error.setVisibility(View.VISIBLE);
                            zapname_error.setText(detail.getJSONArray("zap_username").getString(0));
                            zapname_error.requestFocus();
                        }


//                        int intcheck = 0;
//                        for (int i = 0; i < errorlist.length; i++) {
//                            try {
//                                intcheck = 0;
//
//                                for (int j = 0; j < detail.getJSONArray(errorlist[i]).length(); j++) {
//
//                                     CustomMessage.getInstance().CustomMessage(this, detail.getJSONArray(errorlist[i]).getString(0));
//                                    intcheck = 1;
//
//                                }
//                            } catch (Exception e) {
//
//                            }
//
//                            if (intcheck == 1) {
//                                i = errorlist.length;
//                            }
//
//                        }
                    }

                }
            } catch (Exception e) {
                signupClick.setEnabled(true);
                mProgress.dismiss();
                Intent mydialog = new Intent(Signup.this, AlertActivity.class);
                // int imgid = R.drawable.alertoop;
                String strmessage = "OOPS!";
                // mydialog.putExtra("imgid", imgid);
                mydialog.putExtra("Message", strmessage);
                mydialog.putExtra("Buttontext", " RETRY ");
                mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                mydialog.putExtra("activity", "Signup");

                startActivity(mydialog);
                finish();
            }
        } else if (flag.equals("loginCheck")) {
            ////////System.out.println("loginCheck" + response);
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

                        JSONObject metaData = new JSONObject();
                        try {

                            metaData.put("friend_user_id" ,String.valueOf(data.getInt("user_id")));
                            metaData.put("referral_user_id" ,strReferalId);

                        } catch (JSONException e) { }
                        Branch.getInstance(getApplicationContext()).userCompletedAction("sign_up" , metaData);
                        profileComplted = data.getInt("profile_completed");
                        //System.out.println("CHECK:"+"outside");
                        if (db.getTableRecordsCount("CART") > 0) {
                            //System.out.println("CHECK:"+"inside if");
                            UpdateCart();
                        } else {
                            //System.out.println("CHECK:"+"outside if");
                            if (profileComplted == 1) {
                                Intent onboarding1 = new Intent(Signup.this, LoginHandler.class);
                                startActivity(onboarding1);
                                finish();
                            } else if (profileComplted == 2) {
                                Intent onboardin2 = new Intent(Signup.this, Onboarding2.class);
                                onboardin2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                onboardin2.putExtra("activity", "SplashScreen");
                                onboardin2.putExtra("booltype", true);
                                startActivity(onboardin2);
                                finish();
                            } else if (profileComplted == 3) {
                                Intent onboarding3 = new Intent(Signup.this, Onboarding3.class);
                                onboarding3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                onboarding3.putExtra("activity", "SplashScreen");
                                onboarding3.putExtra("booltype", true);
                                startActivity(onboarding3);
                                finish();

                            } else if (profileComplted == 4) {
                                Intent onboardin4 = new Intent(Signup.this, Onboarding4.class);
                                onboardin4.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                onboardin4.putExtra("activity", "SplashScreen");
                                onboardin4.putExtra("booltype", true);
                                startActivity(onboardin4);
                                finish();

                            } else {
                                Intent onboarding5 = new Intent(Signup.this, discover.class);
                                onboarding5.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                onboarding5.putExtra("activity", "SplashScreen");
                                startActivity(onboarding5);
                                finish();
                            }
                        }
                    } else {
                        mProgress.dismiss();
                        Intent mydialog = new Intent(Signup.this, AlertActivity.class);
                        // int imgid = R.drawable.alertoop;
                        String strmessage = "OOPS!";
                        // mydialog.putExtra("imgid", imgid);
                        mydialog.putExtra("Message", strmessage);
                        mydialog.putExtra("Buttontext", " RETRY ");
                        mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                        mydialog.putExtra("activity", "Signup");
                        mydialog.putExtra("activity", "Signup");
                        startActivity(mydialog);
                        finish();
                    }
                } catch (Exception e) {
                    mProgress.dismiss();
                    ////////System.out.println("Error in response object!");
                    Intent mydialog = new Intent(Signup.this, AlertActivity.class);
                    // int imgid = R.drawable.alertoop;
                    String strmessage = "OOPS!";
                    // mydialog.putExtra("imgid", imgid);
                    mydialog.putExtra("Message", strmessage);
                    mydialog.putExtra("Buttontext", " RETRY ");
                    mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                    mydialog.putExtra("activity", "Signup");
                    mydialog.putExtra("activity", "Signup");
                    startActivity(mydialog);
                    finish();

                }
            } else {
                mProgress.dismiss();
                Intent mydialog = new Intent(Signup.this, AlertActivity.class);
                // int imgid = R.drawable.alertoop;
                String strmessage = "OOPS!";
                // mydialog.putExtra("imgid", imgid);
                mydialog.putExtra("Message", strmessage);
                mydialog.putExtra("Buttontext", " RETRY ");
                mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                mydialog.putExtra("activity", "Signup");
                mydialog.putExtra("activity", "Signup");
                startActivity(mydialog);
                finish();
            }
        } else if (flag.equals("cart")) {

            //System.out.println("CART:"+response);
            SharedPreferences CartSession = getSharedPreferences("CartSession",
                    Context.MODE_PRIVATE);
            Boolean FromCart = false;
            FromCart = CartSession.getBoolean("FromCart", false);
            try {
                String status = response.getString("status");

                if (status.equals("success")) {

                    ExternalFunctions.cartcount = db.getTableRecordsCount("CART");

                    String deleteQuery = "delete from CART";
                    db.processData(deleteQuery);
                    if (profileComplted == 1) {
                        Intent onboarding1 = new Intent(Signup.this, LoginHandler.class);
                        startActivity(onboarding1);
                        finish();
                    } else if (profileComplted == 2) {
                        Intent onboardin2 = new Intent(Signup.this, Onboarding2.class);
                        onboardin2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        onboardin2.putExtra("activity", "SplashScreen");
                        onboardin2.putExtra("booltype", true);
                        startActivity(onboardin2);
                        finish();
                    } else if (profileComplted == 3) {
                        Intent onboarding3 = new Intent(Signup.this, Onboarding3.class);
                        onboarding3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        onboarding3.putExtra("activity", "SplashScreen");
                        onboarding3.putExtra("booltype", true);
                        startActivity(onboarding3);
                        finish();

                    } else if (profileComplted == 4) {
                        Intent onboardin4 = new Intent(Signup.this, Onboarding4.class);
                        onboardin4.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        onboardin4.putExtra("activity", "SplashScreen");
                        onboardin4.putExtra("booltype", true);
                        startActivity(onboardin4);
                        finish();

                    } else {
                        if (FromCart) {
                            Intent onboarding5 = new Intent(Signup.this, Cart.class);
                            startActivity(onboarding5);
                            finish();
                        } else {
                            Intent onboarding5 = new Intent(Signup.this, discover.class);
                            onboarding5.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            onboarding5.putExtra("activity", "SplashScreen");
                            startActivity(onboarding5);
                            finish();
                        }
                    }
                } else {
                    ExternalFunctions.cartcount = 0;

                    String deleteQuery = "delete from CART";
                    db.processData(deleteQuery);


                    if (profileComplted == 1) {
                        Intent onboarding1 = new Intent(Signup.this, LoginHandler.class);
                        startActivity(onboarding1);
                        finish();
                    } else if (profileComplted == 2) {
                        Intent onboardin2 = new Intent(Signup.this, Onboarding2.class);
                        onboardin2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        onboardin2.putExtra("activity", "SplashScreen");
                        onboardin2.putExtra("booltype", true);
                        startActivity(onboardin2);
                        finish();
                    } else if (profileComplted == 3) {
                        Intent onboarding3 = new Intent(Signup.this, Onboarding3.class);
                        onboarding3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        onboarding3.putExtra("activity", "SplashScreen");
                        onboarding3.putExtra("booltype", true);
                        startActivity(onboarding3);
                        finish();

                    } else if (profileComplted == 4) {
                        Intent onboardin4 = new Intent(Signup.this, Onboarding4.class);
                        onboardin4.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        onboardin4.putExtra("activity", "SplashScreen");
                        onboardin4.putExtra("booltype", true);
                        startActivity(onboardin4);
                        finish();

                    } else {
                        if (FromCart) {
                            Intent onboarding5 = new Intent(Signup.this, Cart.class);
                            startActivity(onboarding5);
                            finish();
                        } else {
                            Intent onboarding5 = new Intent(Signup.this, discover.class);
                            onboarding5.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            onboarding5.putExtra("activity", "SplashScreen");
                            startActivity(onboarding5);
                            finish();
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    public void onErrorCallback(VolleyError error, String flag) {

    }


    @Override
    protected void onResume() {
        super.onResume();
        mTracker.set("UserName", GetSharedValues.getUsername(getApplicationContext()));
        mTracker.set("ZapUsername", GetSharedValues.getZapname(getApplicationContext()));
        mTracker.setScreenName("Email signup");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}

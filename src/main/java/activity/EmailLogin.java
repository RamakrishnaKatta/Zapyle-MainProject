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
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
//import com.uxcam.UXCam;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import DataBase.DatabaseDB;
import application.MyApplicationClass;
import network.ApiCommunication;
import network.ApiService;
import utils.CommonFinish;
import utils.CustomMessage;
import utils.ExternalFunctions;
import utils.GetSharedValues;

/**
 * Created by haseeb on 26/5/16.
 */
public class EmailLogin extends Activity implements ApiCommunication {

    private static final String SCREEN_NAME = "EMAILLOGIN";
    ProgressDialog mProgress;
    MixpanelAPI mixpanel;
    EditText lUsername, lPassword;
    TextView loginClick, registerClick, forgotClick;
    ImageView show_password;
    TextView login_error, login_name_error, login_password_error;
    String gcm_id;

    public static int k = 0;
    String callingActivity = "Parallax";
    DatabaseDB db;
    int processCompleted = 0;
    Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_login_activity);
        MyApplicationClass application = (MyApplicationClass) getApplication();
        mTracker = application.getDefaultTracker();
        //UXCam.startWithKey("1dfb25141864376");
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        SharedPreferences settings = getSharedPreferences("LoginSession",
                Context.MODE_PRIVATE);
        gcm_id = settings.getString("GcMToken", "");
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        try {
            callingActivity = "activity." + "Parallax";
        } catch (Exception e) {
            callingActivity = "activity." + "Parallax";

        }


        //        Database function
//        ----------------------------------------------------

        db = new DatabaseDB(getApplicationContext());
        db.openDB();


        //////Thread.currentThread().setDefaultUncaughtExceptionHandler(new SplashScreen.MyUncaughtExceptionHandler());
        LocalBroadcastManager.getInstance(this).registerReceiver(CommonFinish.broadcastReceiver, new IntentFilter("EmailLogin"));
        mProgress = new ProgressDialog(EmailLogin.this);
        mProgress.setCancelable(false);
        mixpanel = MixpanelAPI.getInstance(EmailLogin.this, getResources().getString(R.string.mixpanelToken));
        lUsername = (EditText) findViewById(R.id.login_name);
        lPassword = (EditText) findViewById(R.id.login_password);
        loginClick = (TextView) findViewById(R.id.loginClick);
        registerClick = (TextView) findViewById(R.id.register);
        forgotClick = (TextView) findViewById(R.id.forgotPassword);
        show_password = (ImageView) findViewById(R.id.show_password);


        login_error = (TextView) findViewById(R.id.login_error);
        login_name_error = (TextView) findViewById(R.id.login_name_error);
        login_password_error = (TextView) findViewById(R.id.login_password_error);

        login_error.setVisibility(View.GONE);
        login_name_error.setVisibility(View.GONE);
        login_password_error.setVisibility(View.GONE);


        loginClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginClick.setEnabled(false);
                mProgress.setMessage("Authenticating ...");
                mProgress.show();
                Login();
            }
        });
        SpannableString content = new SpannableString("Register");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        registerClick.setText(content);

        SpannableString content1 = new SpannableString("Forgot?");
        content1.setSpan(new UnderlineSpan(), 0, content1.length(), 0);
        forgotClick.setText(content1);

        registerClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reg = new Intent(EmailLogin.this, Signup.class);
                reg.putExtra("activity", "EmailLogin");
                startActivity(reg);
                finish();
            }
        });

        forgotClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forgot = new Intent(EmailLogin.this, ForgotPassword.class);
                startActivity(forgot);
                finish();
            }
        });
        lPassword.setTypeface(Typeface.DEFAULT);
        lPassword.setTransformationMethod(new PasswordTransformationMethod());


        show_password.setImageResource(R.drawable.show_password);

        show_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lPassword.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
                    lPassword.setTransformationMethod(null);
                    show_password.setImageResource(R.drawable.hide_password);
                    lPassword.setSelection(lPassword.getText().length());

                } else {
                    lPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    show_password.setImageResource(R.drawable.show_password);
                    lPassword.setSelection(lPassword.getText().length());
                }
            }
        });
    }


//    Login function
//    ---------------------------------------------------------------------


    public void Login() {
        login_error.setVisibility(View.GONE);
        login_name_error.setVisibility(View.GONE);
        login_password_error.setVisibility(View.GONE);
        String email = lUsername.getText().toString();
        String password = lPassword.getText().toString();

        if (!email.isEmpty()) {
            if (!password.isEmpty()) {
                final JSONObject loginDetails = new JSONObject();
                try {
                    loginDetails.put("email", email);
                    loginDetails.put("password", password);
                    loginDetails.put("logged_from", "zapyle");
                    loginDetails.put("logged_device", "android");
                    loginDetails.put("gcm_reg_id", gcm_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ////////System.out.println(loginDetails + "__loginobject");
                ApiService.getInstance(EmailLogin.this, 1).postData(EmailLogin.this, EnvConstants.APP_BASE_URL + "/account/login/", loginDetails, SCREEN_NAME, "login");

            } else {
                loginClick.setEnabled(true);
                mProgress.dismiss();
//                 CustomMessage.getInstance().CustomMessage(this, "Please enter password.");
                login_password_error.setVisibility(View.VISIBLE);
                login_password_error.setText("Please enter password.");
            }
        } else {
            loginClick.setEnabled(true);
            mProgress.dismiss();
//             CustomMessage.getInstance().CustomMessage(this, "Please enter email ID or username.");
            login_name_error.setVisibility(View.VISIBLE);
            login_name_error.setText("Please enter email ID or username.");
        }
    }


//    Getuser details
//    --------------------------------------------------------

    private void GetUserDetails() {
        ApiService.getInstance(EmailLogin.this, 1).getData(EmailLogin.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/mydetails/", "loginCheck");
    }


    //    Back button functions
//    --------------------------------------------------------
    @Override
    public void onBackPressed() {
        Intent mydialog = null;

        try {
            mydialog = new Intent(EmailLogin.this, Class.forName(callingActivity));
            mydialog.putExtra("activity", "EmailLogin");
            mydialog.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mydialog);
            finish();
        } catch (ClassNotFoundException e) {
            mydialog = new Intent(EmailLogin.this, Parallax.class);
            mydialog.putExtra("activity", "");
            mydialog.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mydialog);
            finish();


        }
    }


//    Update cart function
//    ---------------------------------------------------------

    private void UpdateCart() {
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


        final JSONObject buyObjectcart = new JSONObject();

        try {
            buyObjectcart.put("cart_data", ObjectList);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //System.out.println("CART DATA:" + buyObjectcart);
        ApiService.getInstance(EmailLogin.this, 1).postData(EmailLogin.this, EnvConstants.APP_BASE_URL + "/zapcart/", buyObjectcart, SCREEN_NAME, "cart");
    }


    public void GetCartCount() {
        ApiService.getInstance(EmailLogin.this, 1).getData(EmailLogin.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/zapcart/?action=count", "cartcount");
    }


    //    Server responses
//    -----------------------------------------------------------------------
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    public void onResponseCallback(JSONObject response, String flag) {
        login_error.setVisibility(View.GONE);
        login_name_error.setVisibility(View.GONE);
        login_password_error.setVisibility(View.GONE);
        if (flag.equals("login")) {
            //System.out.println("login response  :" + response);
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
                    loginClick.setEnabled(true);
                    GetUserDetails();
                } else {
                    loginClick.setEnabled(true);
                    mProgress.dismiss();
                    String detail = resp.getString("detail");
//                    if (detail.contains("Username or password")) {
//                     CustomMessage.getInstance().CustomMessage(this, detail);
                    login_error.setVisibility(View.VISIBLE);
                    login_error.setText(detail);

//                    }

                }
            } catch (Exception e) {
                loginClick.setEnabled(true);
                Intent mydialog = new Intent(EmailLogin.this, AlertActivity.class);
               // int imgid = R.drawable.alertoop;
                String strmessage = "OOPS!";
               // mydialog.putExtra("imgid", imgid);
                mydialog.putExtra("Message", strmessage);
                mydialog.putExtra("Buttontext", " RETRY ");
                mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                mydialog.putExtra("activity", "EmailLogin");
                mydialog.putExtra("activity", "EmailLogin");
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


                        JSONObject superprop = new JSONObject();
                        superprop.put("Session_Start", SplashScreen.startdatetime);
                        superprop.put("User Type", data.getString("user_type"));
                        superprop.put("User Name", data.getString("zap_username"));
                        superprop.put("platfrom", "Android");
                        mixpanel.registerSuperPropertiesOnce(superprop);

                        processCompleted = data.getInt("profile_completed");

                        if (db.getTableRecordsCount("CART") > 0) {
                            UpdateCart();
                        } else {
                            GetCartCount();
                        }
//                            Intent onboarding5 = new Intent(EmailLogin.this, HomePageNew.class);
//                            onboarding5.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            onboarding5.putExtra("activity", "SplashScreen");
//                            startActivity(onboarding5);
//                            finish();


                    } else {
                        mProgress.dismiss();
                        Intent mydialog = new Intent(EmailLogin.this, AlertActivity.class);
                       // int imgid = R.drawable.alertoop;
                        String strmessage = "OOPS!";
                       // mydialog.putExtra("imgid", imgid);
                        mydialog.putExtra("Message", strmessage);
                        mydialog.putExtra("Buttontext", " RETRY ");
                        mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                        mydialog.putExtra("activity", "EmailLogin");
                        mydialog.putExtra("activity", "EmailLogin");
                        startActivity(mydialog);
                        finish();
                    }
                } catch (Exception e) {
                    mProgress.dismiss();
                    ////////System.out.println("Error in response object!");
                    Intent mydialog = new Intent(EmailLogin.this, AlertActivity.class);
                   // int imgid = R.drawable.alertoop;
                    String strmessage = "OOPS!";
                   // mydialog.putExtra("imgid", imgid);
                    mydialog.putExtra("Message", strmessage);
                    mydialog.putExtra("Buttontext", " RETRY ");
                    mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                    mydialog.putExtra("activity", "EmailLogin");
                    mydialog.putExtra("activity", "EmailLogin");
                    startActivity(mydialog);
                    finish();

                }
            } else {
                mProgress.dismiss();
                Intent mydialog = new Intent(EmailLogin.this, AlertActivity.class);
               // int imgid = R.drawable.alertoop;
                String strmessage = "OOPS!";
               // mydialog.putExtra("imgid", imgid);
                mydialog.putExtra("Message", strmessage);
                mydialog.putExtra("Buttontext", " RETRY ");
                mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                mydialog.putExtra("activity", "EmailLogin");
                mydialog.putExtra("activity", "EmailLogin");
                startActivity(mydialog);
                finish();
            }
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
                    ExternalFunctions.cartcount = resp.getJSONObject("data").getInt("count");
                    String deleteQuery = "delete from CART";
                    db.processData(deleteQuery);

                    if (processCompleted == 1) {
                        Intent onboarding1 = new Intent(EmailLogin.this, LoginHandler.class);
                        startActivity(onboarding1);
                        finish();
                    } else if (processCompleted == 2) {
                        Intent onboardin2 = new Intent(EmailLogin.this, Onboarding2.class);
                        onboardin2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        onboardin2.putExtra("activity", "SplashScreen");
                        onboardin2.putExtra("booltype", true);
                        startActivity(onboardin2);
                        finish();
                    } else if (processCompleted == 3) {
                        Intent onboarding3 = new Intent(EmailLogin.this, Onboarding3.class);
                        onboarding3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        onboarding3.putExtra("activity", "SplashScreen");
                        onboarding3.putExtra("booltype", true);
                        startActivity(onboarding3);
                        finish();

                    } else if (processCompleted == 4) {
                        Intent onboardin4 = new Intent(EmailLogin.this, Onboarding4.class);
                        onboardin4.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        onboardin4.putExtra("activity", "SplashScreen");
                        onboardin4.putExtra("booltype", true);
                        startActivity(onboardin4);
                        finish();

                    } else {
                        Intent onboarding5 = new Intent(EmailLogin.this, HomePageNew.class);
                        onboarding5.putExtra("activity", "SplashScreen");
                        startActivity(onboarding5);
                        finish();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
                ExternalFunctions.cartcount = 0;
                String deleteQuery = "delete from CART";
                db.processData(deleteQuery);

                if (processCompleted == 1) {
                    Intent onboarding1 = new Intent(EmailLogin.this, LoginHandler.class);
                    startActivity(onboarding1);
                    finish();
                } else if (processCompleted == 2) {
                    Intent onboardin2 = new Intent(EmailLogin.this, Onboarding2.class);
                    onboardin2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    onboardin2.putExtra("activity", "SplashScreen");
                    onboardin2.putExtra("booltype", true);
                    startActivity(onboardin2);
                    finish();
                } else if (processCompleted == 3) {
                    Intent onboarding3 = new Intent(EmailLogin.this, Onboarding3.class);
                    onboarding3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    onboarding3.putExtra("activity", "SplashScreen");
                    onboarding3.putExtra("booltype", true);
                    startActivity(onboarding3);
                    finish();

                } else if (processCompleted == 4) {
                    Intent onboardin4 = new Intent(EmailLogin.this, Onboarding4.class);
                    onboardin4.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    onboardin4.putExtra("activity", "SplashScreen");
                    onboardin4.putExtra("booltype", true);
                    startActivity(onboardin4);
                    finish();

                } else {
                    Intent onboarding5 = new Intent(EmailLogin.this, HomePageNew.class);
                    onboarding5.putExtra("activity", "SplashScreen");
                    startActivity(onboarding5);
                    finish();
                }
            }


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }


    @Override
    public void onErrorCallback(VolleyError error, String flag) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.set("UserName", GetSharedValues.getUsername(getApplicationContext()));mTracker.set("ZapUsername", GetSharedValues.getZapname(getApplicationContext()));   mTracker.setScreenName("Email login");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}

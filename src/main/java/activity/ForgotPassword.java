package activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
//import com.uxcam.UXCam;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONException;
import org.json.JSONObject;

import application.MyApplicationClass;
import network.ApiCommunication;
import network.ApiService;
import utils.CommonFinish;
import utils.CustomMessage;
import utils.GetSharedValues;

/**
 * Created by haseeb on 26/5/16.
 */
public class ForgotPassword extends Activity implements ApiCommunication {

    private static final String SCREEN_NAME = "FORGOTPASSWORD";
    LinearLayout phonenumber_layout, reset_password_layout, otp_layout;
    EditText phonenumber, forgot_otp, new_password, confirm_password;
    String cta_action, OTPphone_number, OTP, gcm_id;
    TextView cta;

    ProgressDialog mProgress;
    TextView phone_error, password_match_error, otp_match_error, otp_message;
    TextView resendOtp, backToLogin;
    String callingActivity = "Parallax";
    Tracker mTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_activity);
        MyApplicationClass application = (MyApplicationClass) getApplication();
        mTracker = application.getDefaultTracker();
        //UXCam.startWithKey("1dfb25141864376");
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        SharedPreferences settings = getSharedPreferences("LoginSession",
                Context.MODE_PRIVATE);
        try {
            callingActivity = "activity." + getIntent().getStringExtra("activity");

        } catch (Exception e) {
            callingActivity = "activity." + "EmailLogin";

        }

        gcm_id = settings.getString("GcMToken", "");
        mProgress = new ProgressDialog(ForgotPassword.this);
        mProgress.setCancelable(false);
        //////Thread.currentThread().setDefaultUncaughtExceptionHandler(new SplashScreen.MyUncaughtExceptionHandler());
        LocalBroadcastManager.getInstance(this).registerReceiver(CommonFinish.broadcastReceiver, new IntentFilter("EmailLogin"));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        phonenumber_layout = (LinearLayout) findViewById(R.id.phonenumber_layout);
        phonenumber_layout.setVisibility(View.VISIBLE);
        cta_action = "step1";
        reset_password_layout = (LinearLayout) findViewById(R.id.reset_password_layout);
        reset_password_layout.setVisibility(View.GONE);
        otp_layout = (LinearLayout) findViewById(R.id.otp_layout);
        otp_layout.setVisibility(View.GONE);

        phonenumber = (EditText) findViewById(R.id.phonenumber);
        forgot_otp = (EditText) findViewById(R.id.forgot_otp);
        new_password = (EditText) findViewById(R.id.new_password);
        confirm_password = (EditText) findViewById(R.id.confirm_password);

        phone_error = (TextView) findViewById(R.id.phone_error);
        password_match_error = (TextView) findViewById(R.id.password_match_error);
        otp_match_error = (TextView) findViewById(R.id.otp_match_error);
        phone_error.setVisibility(View.GONE);
        password_match_error.setVisibility(View.GONE);
        otp_match_error.setVisibility(View.GONE);


        resendOtp = (TextView) findViewById(R.id.resendOtp);
        backToLogin = (TextView) findViewById(R.id.backToLogin);
        otp_message = (TextView) findViewById(R.id.otp_message);

        SpannableString content = new SpannableString("Resend");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        resendOtp.setText(content);




        SpannableString content1 = new SpannableString("Back to Login");
        content1.setSpan(new UnderlineSpan(), 0, content1.length(), 0);
        backToLogin.setText(content1);

        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(ForgotPassword.this, EmailLogin.class);
                startActivity(login);
                finish();
            }
        });


        resendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (OTPphone_number.length() > 0) {
                    ResendOtp(OTPphone_number);
                }
            }
        });


        cta = (TextView) findViewById(R.id.Action);
        cta.setText("SEND OTP");
        cta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cta_action.contains("step1")) {
                    SendOTP();
                } else if (cta_action.contains("step2")) {
                    String otp = forgot_otp.getText().toString();
                    if (otp.length() > 0) {
                        VerifyOtp();
                    } else {
                        otp_match_error.setVisibility(View.VISIBLE);
                        otp_match_error.setText("Please enter OTP.");
                    }
                } else {
                    cta.setEnabled(false);
                    ChangepasswodToServer();
                }
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    //    Back button functions
//    --------------------------------------------------------
    @Override
    public void onBackPressed() {
        Intent mydialog = null;

        try {
            mydialog = new Intent(ForgotPassword.this, Class.forName(callingActivity));
            mydialog.putExtra("activity", "EmailLogin");
            mydialog.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mydialog);
            finish();
        } catch (ClassNotFoundException e) {
            mydialog = new Intent(ForgotPassword.this, EmailLogin.class);
            mydialog.putExtra("activity", "EmailLogin");
            mydialog.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mydialog);
            finish();


        }
    }


    //    Server requests
//    --------------------------------------------------------
    private void SendOTP() {
        phone_error.setVisibility(View.GONE);
        password_match_error.setVisibility(View.GONE);
        otp_match_error.setVisibility(View.GONE);
        String p_number = phonenumber.getText().toString();
        ////////System.out.println("p_number__" + p_number);
        if (p_number.length() != 0) {
            if (p_number.length() >= 10) {
                OTPphone_number = p_number;
                cta.setEnabled(false);
                GetOtp(p_number);

            } else {
//                 CustomMessage.getInstance().CustomMessage(this, "Incorrect phone number");
                phone_error.setVisibility(View.VISIBLE);
                phone_error.setText("Incorrect phone number");
                phonenumber.setBackgroundResource(R.drawable.shape_boarderred);
                phonenumber.requestFocus();
            }
        } else {
            phone_error.setVisibility(View.VISIBLE);
            phone_error.setText("Fill the phone number");
//             CustomMessage.getInstance().CustomMessage(this, "Fill the phone number");
        }
    }


    private void GetOtp(String phone_number) {
        mProgress.setMessage("Verifying phone number ...");
        mProgress.show();
        ApiService.getInstance(ForgotPassword.this, 1).getData(ForgotPassword.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/account/zapotp/get/" + phone_number + "/", "getotp");
    }

    private void ResendOtp(String phone_number) {
        mProgress.setMessage("Requesting OTP ...");
        mProgress.show();
        ApiService.getInstance(ForgotPassword.this, 1).getData(ForgotPassword.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/account/zapotp/get/" + phone_number + "/", "getotp");
    }

    private void VerifyOtp() {
        cta.setEnabled(false);
        mProgress.setMessage("Verifying OTP ...");
        mProgress.show();
        String entered_otp = forgot_otp.getText().toString();
        OTP = entered_otp;
        final JSONObject otpObj = new JSONObject();
        try {
            otpObj.put("phone_number", OTPphone_number);
            otpObj.put("otp", entered_otp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ////////System.out.println(otpObj + "__resetDetails");
        ApiService.getInstance(ForgotPassword.this, 1).postData(ForgotPassword.this, EnvConstants.APP_BASE_URL + "/user/otpverify/", otpObj, SCREEN_NAME, "verifyotp");

    }


    public void ChangepasswodToServer() {
        String password = new_password.getText().toString();
        String confirmPassword = confirm_password.getText().toString();


        if (password.length() > 0 && confirmPassword.length() > 0) {
            if (password.equals(confirmPassword)) {
                postPassword(password, confirmPassword);
            } else {
                cta.setEnabled(true);
//                 CustomMessage.getInstance().CustomMessage(ForgotPassword.this, "Passwords do not match!");
                password_match_error.setVisibility(View.VISIBLE);
                password_match_error.setText("Passwords do not match!");
                new_password.setBackgroundResource(R.drawable.shape_boarderred);
                new_password.requestFocus();
            }
        } else {
            cta.setEnabled(true);
//             CustomMessage.getInstance().CustomMessage(ForgotPassword.this, "Please fill all the required fields.");
            password_match_error.setVisibility(View.VISIBLE);
            password_match_error.setText("Please fill all the required fields.");
        }
    }


    public void postPassword(String password, String confirmPassword) {
        final JSONObject resetDetails = new JSONObject();
        try {
            resetDetails.put("password", password);
            resetDetails.put("confirm_password", confirmPassword);
            resetDetails.put("phone_number", OTPphone_number);
            resetDetails.put("logged_device", "android");
            resetDetails.put("otp", OTP);
            resetDetails.put("gcm_reg_id", gcm_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ////////System.out.println(resetDetails + "__resetDetails");
        ApiService.getInstance(ForgotPassword.this, 1).postData(ForgotPassword.this, EnvConstants.APP_BASE_URL + "/account/zapotp/get/" + OTPphone_number + "/", resetDetails, SCREEN_NAME, "resetpost");

    }


//    Server responses
//    -----------------------------------------------------------

    @Override
    public void onResponseCallback(JSONObject response, String flag) {
        //System.out.println(response);
        phone_error.setVisibility(View.GONE);
        password_match_error.setVisibility(View.GONE);
        otp_match_error.setVisibility(View.GONE);
        if (flag.equals("getotp")) {
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        mProgress.dismiss();
                        phonenumber_layout.setVisibility(View.GONE);
                        cta_action = "step2";
                        cta.setText("VERIFY OTP");
                        reset_password_layout.setVisibility(View.GONE);
                        otp_layout.setVisibility(View.VISIBLE);
                        cta.setEnabled(true);

                        String p_num = "XXXXXX"+OTPphone_number.substring(7,OTPphone_number.length());

                        SpannableString content = new SpannableString("An OTP has been sent to "+p_num);
                        content.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 24, content.length(), 0);
                        otp_message.setText(content);

                    } else {
                        cta.setEnabled(true);
                        mProgress.dismiss();
                        String detail = resp.getString("detail");
                        phone_error.setVisibility(View.VISIBLE);
                        phone_error.setText(detail);
//                         CustomMessage.getInstance().CustomMessage(this, detail);

                    }

                } catch (JSONException e) {
                    cta.setEnabled(true);
                    e.printStackTrace();
                    mProgress.dismiss();
                    Intent mydialog = new Intent(ForgotPassword.this, AlertActivity.class);
                   // int imgid = R.drawable.alertoop;
                    String strmessage = "OOPS!";
                   // mydialog.putExtra("imgid", imgid);
                    mydialog.putExtra("Message", strmessage);
                    mydialog.putExtra("Buttontext", " RETRY ");
                    mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                    mydialog.putExtra("activity", "ForgotPassword");
                    mydialog.putExtra("activity", "ForgotPassword");
                    startActivity(mydialog);
                    finish();

                }
            } else {
                mProgress.dismiss();
                Intent mydialog = new Intent(ForgotPassword.this, AlertActivity.class);
               // int imgid = R.drawable.alertoop;
                String strmessage = "OOPS!";
               // mydialog.putExtra("imgid", imgid);
                mydialog.putExtra("Message", strmessage);
                mydialog.putExtra("Buttontext", " RETRY ");
                mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                mydialog.putExtra("activity", "ForgotPassword");
                mydialog.putExtra("activity", "ForgotPassword");
                startActivity(mydialog);
                finish();
            }
        } else if (flag.equals("verifyotp")) {
            ////////System.out.println(response);
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        mProgress.dismiss();
                        phonenumber_layout.setVisibility(View.GONE);
                        cta_action = "step3";
                        cta.setText("RESET");
                        reset_password_layout.setVisibility(View.VISIBLE);
                        otp_layout.setVisibility(View.GONE);
                        cta.setEnabled(true);

                    } else {
                        cta.setEnabled(true);
                        mProgress.dismiss();
                        String detail = resp.getString("detail");
                        otp_match_error.setVisibility(View.VISIBLE);
                        otp_match_error.setText(detail);
//                         CustomMessage.getInstance().CustomMessage(ForgotPassword.this, detail);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    cta.setEnabled(true);
                    mProgress.dismiss();
                }
            } else {
                cta.setEnabled(true);
                mProgress.dismiss();
            }
        } else if (flag.equals("resetpost")) {
            ////////System.out.println(response);
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        ////////System.out.println(resp);
                        phonenumber_layout.setVisibility(View.GONE);
                        cta_action = "step3";
                        reset_password_layout.setVisibility(View.GONE);
                        otp_layout.setVisibility(View.GONE);
                        cta.setEnabled(true);
                        String data = resp.getString("data");
                         CustomMessage.getInstance().CustomMessage(this, data);

                        Intent login = new Intent(ForgotPassword.this, EmailLogin.class);
                        startActivity(login);
                        finish();

                    } else {
                        cta.setEnabled(true);
                        password_match_error.setVisibility(View.VISIBLE);
                        password_match_error.setText("Please enter OTP.");

                    }
                } catch (JSONException e) {
                    cta.setEnabled(true);
                    e.printStackTrace();
                    Intent mydialog = new Intent(ForgotPassword.this, AlertActivity.class);
                   // int imgid = R.drawable.alertoop;
                    String strmessage = "OOPS!";
                   // mydialog.putExtra("imgid", imgid);
                    mydialog.putExtra("Message", strmessage);
                    mydialog.putExtra("Buttontext", " RETRY ");
                    mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                    mydialog.putExtra("activity", "ForgotPassword");
                    mydialog.putExtra("activity", "ForgotPassword");
                    startActivity(mydialog);
                    finish();
                }
            } else {
                Intent mydialog = new Intent(ForgotPassword.this, AlertActivity.class);
               // int imgid = R.drawable.alertoop;
                String strmessage = "OOPS!";
               // mydialog.putExtra("imgid", imgid);
                mydialog.putExtra("Message", strmessage);
                mydialog.putExtra("Buttontext", " RETRY ");
                mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                mydialog.putExtra("activity", "ForgotPassword");
                mydialog.putExtra("activity", "ForgotPassword");
                startActivity(mydialog);
                finish();
            }
        }
    }

    @Override
    public void onErrorCallback(VolleyError error, String flag) {

    }


    @Override
    protected void onResume() {
        super.onResume();
        mTracker.set("UserName", GetSharedValues.getUsername(getApplicationContext()));mTracker.set("ZapUsername", GetSharedValues.getZapname(getApplicationContext()));   mTracker.setScreenName("Forgot password");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}

package activity;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
//import com.//Appsee.//Appsee;
import com.appvirality.CampaignHandler;
import com.appvirality.android.AppviralityAPI;
import com.appvirality.android.CampaignDetails;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
//import com.uxcam.UXCam;
import com.zapyle.zapyle.Alerts;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONException;
import org.json.JSONObject;


import java.text.NumberFormat;

import application.MyApplicationClass;
import network.ApiCommunication;
import network.ApiService;
import utils.CustomMessage;
import utils.ExternalFunctions;
import utils.FontUtils;
import utils.GetSharedValues;
import utils.Mixpanelutils;

/**
 * Created by haseeb on 6/4/16.
 */
public class EarnCash extends ActionBarActivity implements ApiCommunication {
    String SCREEN_NAME = "EARNCASH";
    TextView txtReferrals, invite, promocode;
    RelativeLayout price_layout;
    String share_url_text;
    ProgressBar progress;
    LinearLayout referral_layout;
    String callingActivity = "activity.HomePage";
    Tracker mTracker;
    String refcode;
    Integer tamount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.referral_earning);
        MyApplicationClass application = (MyApplicationClass) getApplication();
        mTracker = application.getDefaultTracker();
//        //Appsee.start("4fb55809ab45411c909521bda720f8e6");
//        //UXCam.startWithKey("1dfb25141864376");
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        //////Thread.currentThread().setDefaultUncaughtExceptionHandler(new SplashScreen.MyUncaughtExceptionHandler());
        progress = (ProgressBar) findViewById(R.id.e_progressBar);
        progress.setVisibility(View.VISIBLE);


        try {
            callingActivity = "activity." + getIntent().getStringExtra("activity");
        } catch (Exception e) {
            callingActivity = "activity.HomePage";
        }

        referral_layout = (LinearLayout) findViewById(R.id.referral_layout);
        FontUtils.setCustomFont(referral_layout, getAssets());
        referral_layout.setVisibility(View.INVISIBLE);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final View customView = getLayoutInflater().inflate(R.layout.product_bar, null);
        getSupportActionBar().setCustomView(customView);
        Toolbar parent = (Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0, 0);
        promocode = (TextView) findViewById(R.id.promocode);
        SpannableString content1 = new SpannableString("HAVE A PROMO CODE?");
        content1.setSpan(new UnderlineSpan(), 7, content1.length(), 0);
        promocode.setText(content1);
        promocode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowPromoDialog();
            }
        });

        TextView tv_headTitle = (TextView) findViewById(R.id.product_title_text);
        tv_headTitle.setText("DISCOUNTS");

        ExternalFunctions.bloverlay = false;
        ImageView Skip = (ImageView) findViewById(R.id.productfeedButton);
        Skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

        txtReferrals = (TextView) findViewById(R.id.txtReferrals);
        invite = (TextView) findViewById(R.id.invite);
        price_layout = (RelativeLayout) findViewById(R.id.price_layout);

        try {
            if (ExternalFunctions.referalmsg.length() > 1) {

                new AlertDialog.Builder(this)
                        .setTitle("ZAPYLE")
                        .setIcon(R.drawable.icon)
                        .setMessage(ExternalFunctions.referalmsg)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ExternalFunctions.referalmsg = "1";
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        } catch (Exception e) {

        }
        final CampaignDetails[] cDetails = {null};
        AppviralityAPI.setCampaignHandler(EarnCash.this, AppviralityAPI.GH.Word_of_Mouth, new AppviralityAPI.CampaignReadyListner() {
            @Override
            public void onCampaignReady(CampaignDetails campaignDetails) {
                if (campaignDetails != null) {
                    Log.i("AVLOG", "Campaign Details Ready now");
                    //Set Campaign Details
                    CampaignHandler.setCampaignDetails(campaignDetails);
                    cDetails[0] = campaignDetails;
//                    view.setVisibility(View.VISIBLE);
                    getEarnings();
                    share_url_text = "Hey there! I want to you to join me on Zapyle - the ultimate luxury fashion destination. Let's shop together!" + "\n\n" + "Don't forget to download the app using my referral link: " + (cDetails[0].ShareUrl + (!TextUtils.isEmpty(cDetails[0].UserCustomLink) ? "/" + cDetails[0].UserCustomLink : "")) + "/14" + " to get Rs 100 off on your first purchase.";
                } else {
                    progress.setVisibility(View.GONE);
                    referral_layout.setVisibility(View.VISIBLE);
                }

            }
        });


        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GetSharedValues.LoginStatus(EarnCash.this)) {
                    //////System.out.println("invite click"+share_url_text);
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT, share_url_text);
                    intent.putExtra(Intent.EXTRA_SUBJECT, "sample");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Join Zapyle: India's favourite luxury fashion destination!");
                    intent.setType("text/plain");
                    startActivity(Intent.createChooser(intent, "Share"));
                }
                else {
                    Alerts.loginAlert(EarnCash.this);
                }
            }
        });


    }


    public void getEarnings() {
        ApiService.getInstance(EarnCash.this, 1).getData(EarnCash.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/extra/appvirality/campaign", "GetCash");


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();

    }

    @Override
    public void onBackPressed() {
        Intent dintent = null;
        if (callingActivity.contains("HomePageNew")) {
            dintent = new Intent(EarnCash.this, HomePageNew.class);
            dintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            dintent.putExtra("activity", "SplashScreen");
            startActivity(dintent);
            finish();
        } else {
            try {
                dintent = new Intent(EarnCash.this, Class.forName(callingActivity));
                dintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                dintent.putExtra("activity", "HomePageNew");
                startActivity(dintent);
                finish();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                dintent = new Intent(EarnCash.this, HomePageNew.class);
                dintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                dintent.putExtra("activity", "HomePageNew");
                startActivity(dintent);
                finish();
            }

        }

    }

    @Override
    public void onResponseCallback(JSONObject response, String flag) {

        if (flag.equals("GetCash")) {
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            //System.out.println("resp:zap-getzap"+response);

            if (resp != null) {
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        JSONObject data = resp.getJSONObject("data");
                        tamount = data.getInt("zapcash");
                        if (tamount > 0) {
                            progress.setVisibility(View.GONE);
                            referral_layout.setVisibility(View.VISIBLE);
                            //////System.out.println("inside not 0");
                            price_layout.setVisibility(View.VISIBLE);
                            txtReferrals.setText(getResources().getString(R.string.Rs) + tamount.toString());
//                        cDetails.isRewardExists = true;
                        } else {
                            progress.setVisibility(View.GONE);
                            referral_layout.setVisibility(View.VISIBLE);
                            txtReferrals.setText(getResources().getString(R.string.Rs) + "0");
                        }

                    } else {
                       CustomMessage.getInstance().CustomMessage(EarnCash.this, "Oops. Something went wrong!");
                        progress.setVisibility(View.GONE);
                        referral_layout.setVisibility(View.VISIBLE);
                        txtReferrals.setText(getResources().getString(R.string.Rs) + "0");
                    }
                } catch (JSONException e) {
                    progress.setVisibility(View.GONE);
                    referral_layout.setVisibility(View.VISIBLE);
                    txtReferrals.setText(getResources().getString(R.string.Rs) + "0");
                    e.printStackTrace();
                   CustomMessage.getInstance().CustomMessage(EarnCash.this, "Oops. Something went wrong!");

                }
            }
        }else if (flag.equals("promotoserver")) {
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            //System.out.println("resp:zap-promo"+response);
            //System.out.println(resp);
            progress.setVisibility(View.GONE);
            if (resp != null) {
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        JSONObject data = resp.getJSONObject("data");
                        Integer amount = data.getInt("amount");

                        tamount=tamount+amount;
                        //System.out.println("zamount"+amount+"="+tamount);
                        Toast.makeText(EarnCash.this, "Promo code applied successfully", Toast.LENGTH_SHORT).show();
                        if (tamount > 0) {

                            referral_layout.setVisibility(View.VISIBLE);
                            //////System.out.println("inside not 0");
                            price_layout.setVisibility(View.VISIBLE);
                            txtReferrals.setText(getResources().getString(R.string.Rs) + tamount.toString());
//                        cDetails.isRewardExists = true;
                        } else {
                            referral_layout.setVisibility(View.VISIBLE);
                           // emptyreward.setVisibility(View.VISIBLE);
                        }

                    } else {
                        if(!TextUtils.isEmpty(refcode) && !TextUtils.isEmpty(AppviralityAPI.getAttributionSetting())
                                && !AppviralityAPI.getAttributionSetting().equals("0") && !AppviralityAPI.isAttributionConfirmed()) {

                            AppviralityAPI.SubmitReferralCode(refcode, new AppviralityAPI.SubmitReferralCodeListner() {
                                @Override
                                public void onResponse(boolean isSuccess) {
                                    if (isSuccess) {
                                        Toast.makeText(EarnCash.this, "Referral code applied successfully", Toast.LENGTH_SHORT).show();
                                        // getEarnings();
                                    } else {

                                        Toast.makeText(EarnCash.this, "Failed to apply promo code", Toast.LENGTH_SHORT).show();
                                    }

                                }

                            });
                        }


                    }

                } catch (Exception e) {

                }
            }


        }
    }
    private void ShowPromoDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(EarnCash.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog_promo, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EarnCash.this);
        alertDialogBuilder.setView(promptView);
        final EditText editText = (EditText) promptView.findViewById(R.id.edtpromo);
        final ImageView imgclose = (ImageView) promptView.findViewById(R.id.imgclose);
        final RelativeLayout rlredeem=(RelativeLayout)promptView.findViewById(R.id.rlpromo);
        final AlertDialog alert = alertDialogBuilder.create();
        alertDialogBuilder.setCancelable(false);

        imgclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });

        rlredeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refcode=editText.getText().toString();
                progress.setVisibility(View.VISIBLE);
                postPromo(refcode);
                alert.dismiss();
            }
        });

        // create an alert dialog

        alert.show();
    }

    public void postPromo(String strref){
        JSONObject data=new JSONObject();

        //////System.out.println("data for sending__" + data);postPromo
        try {
            data.put("code",strref);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //////System.out.println( EnvConstants.APP_BASE_URL + "/user/myinfo/"+ data);
        ApiService.getInstance(EarnCash.this, 1).postData(EarnCash.this, EnvConstants.APP_BASE_URL + "/coupon/promo/", data, SCREEN_NAME, "promotoserver");


    }
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    public void onErrorCallback(VolleyError error, String flag) {
        progress.setVisibility(View.GONE);
        referral_layout.setVisibility(View.VISIBLE);
        txtReferrals.setText(getResources().getString(R.string.Rs) + "0");
    }


    @Override
    protected void onResume() {
        super.onResume();
        mTracker.set("&uid", GetSharedValues.getUsername(getApplicationContext()));
        mTracker.setScreenName(GetSharedValues.getZapname(getApplicationContext()) + "~" + "Earncash");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}

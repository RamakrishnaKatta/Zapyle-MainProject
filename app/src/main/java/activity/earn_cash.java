package activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
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
import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.SharingHelper;
import io.branch.referral.util.LinkProperties;
import io.branch.referral.util.ShareSheetStyle;
import network.ApiCommunication;
import network.ApiService;
import utils.CustomMessage;
import utils.ExternalFunctions;
import utils.FontUtils;
import utils.GetSharedValues;

//import com.//Appsee.//Appsee;
//import com.uxcam.UXCam;

/**
 * Created by haseeb on 6/4/16.
 */

public class earn_cash extends ActionBarActivity implements ApiCommunication {
    String SCREEN_NAME = "EARN_CASH";
    TextView txtReferrals, invite, promocode;
    RelativeLayout price_layout;
    String share_url_text;
    ProgressBar progress;
    LinearLayout referral_layout;
    String callingActivity = "activity.HomePage";
    Tracker mTracker;
    String refcode;
    Integer tamount = 0;
    String stime = "";
    String endtime = "";
    CleverTapAPI cleverTap;
    TextView tvrefer,tvreferred;
    int int_referring=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.referral_earning);
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
//Appsee.start("4fb55809ab45411c909521bda720f8e6");
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
        tvrefer=(TextView)findViewById(R.id.tvrefer);
        tvreferred=(TextView)findViewById(R.id.tvreferred);
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
        getEarnings();


        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//             if (GetSharedValues.LoginStatus(earn_cash.this)) {
//                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
//
//                // what type of data needs to be send by sharing
//                sharingIntent.setType("text/plain");
//
//                // package names
//                PackageManager pm = getPackageManager();
//
//                // list package
//                List<ResolveInfo> activityList = pm.queryIntentActivities(sharingIntent, 0);
//
//                final ShareIntentListAdapter objShareIntentListAdapter = new ShareIntentListAdapter(earn_cash.this, activityList.toArray());
//
//                // Create alert dialog box
//                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
//                builder.setTitle("Share via");
//                builder.setAdapter(objShareIntentListAdapter, new DialogInterface.OnClickListener() {
//
//                    public void onClick(DialogInterface dialog, int item) {
//
//                        ResolveInfo info = (ResolveInfo) objShareIntentListAdapter.getItem(item);
//
//                      //  System.out.println("channel selected"+  info.activityInfo.applicationInfo.loadLabel(getApplicationContext().getPackageManager()).toString());
//                        // start respective activity
//                        HashMap<String, Object> invite_user  = new HashMap<String, Object>();
//                        invite_user.put("invite_channel", info.activityInfo.applicationInfo.loadLabel(getApplicationContext().getPackageManager()).toString());
//                        cleverTap.event.push("invite_user ", invite_user );
//
//
//                        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
//                        intent.setClassName(info.activityInfo.packageName, info.activityInfo.name);
//                        intent.setType("text/plain");
//                        intent.putExtra(Intent.EXTRA_TEXT, share_url_text);
//                        intent.putExtra(Intent.EXTRA_SUBJECT, "sample");
//                        intent.putExtra(Intent.EXTRA_SUBJECT, "Join Zapyle: India's favourite luxury fashion destination!");
//
//                        startActivity(intent);
//
//                    }// end onClick
//                });
//
//                AlertDialog alert = builder.create();
//                alert.show();
//             } else {
          //       Alerts.loginAlert(earn_cash.this);
         //    }

                Share();



            }
        });


    }


    public void getEarnings() {
        ApiService.getInstance(earn_cash.this, 1).getData(earn_cash.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/extra/appvirality/campaign", "GetCash");


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
        Runtime.getRuntime().gc();

    }

    @Override
    public void onBackPressed() {
        Intent dintent = null;
        if (callingActivity.contains("discover")) {
            dintent = new Intent(earn_cash.this, discover.class);
            dintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            dintent.putExtra("activity", "SplashScreen");
            startActivity(dintent);
            finish();
        } else {
            try {
                dintent = new Intent(earn_cash.this, Class.forName(callingActivity));
                dintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                dintent.putExtra("activity", "discover");
                startActivity(dintent);
                finish();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                dintent = new Intent(earn_cash.this, discover.class);
                dintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                dintent.putExtra("activity", "discover");
                startActivity(dintent);
                finish();
            }

        }

    }

    @Override
    public void onResponseCallback(JSONObject response, String flag) {
        if (flag.equals("GetCash")) {
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            System.out.println("resp:zap-getzap"+response);

            if (resp != null) {
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        JSONObject data = resp.getJSONObject("data");
                        tamount = data.getInt("zapcash");
                        tvrefer.setVisibility(View.VISIBLE);
                        tvreferred.setVisibility(View.VISIBLE);
                        tvrefer.setText("Invite your friends to Zapyle\n" +
                                "and earn ₹"+ data.getInt("referrer_amount") +" for every new sign-up.");
                        tvreferred.setText("Your friend will get a sign-up bonus\n" +
                                "of ₹ "+data.getInt("friend_amount") +" too. It’s a win-win. :)");
                        int_referring=data.getInt("friend_amount");
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
                        CustomMessage.getInstance().CustomMessage(earn_cash.this, "Oops. Something went wrong!");
                        progress.setVisibility(View.GONE);
                        referral_layout.setVisibility(View.VISIBLE);
                        txtReferrals.setText(getResources().getString(R.string.Rs) + "0");
                    }
                } catch (JSONException e) {
                    progress.setVisibility(View.GONE);
                    referral_layout.setVisibility(View.VISIBLE);
                    txtReferrals.setText(getResources().getString(R.string.Rs) + "0");
                    e.printStackTrace();
                    CustomMessage.getInstance().CustomMessage(earn_cash.this, "Oops. Something went wrong!");

                }
            }
        } else if (flag.equals("promotoserver")) {
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            progress.setVisibility(View.GONE);
            if (resp != null) {
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        JSONObject data = resp.getJSONObject("data");
                        Integer amount = data.getInt("amount");

                        tamount = tamount + amount;
                        Toast.makeText(earn_cash.this, "Promo code applied successfully", Toast.LENGTH_SHORT).show();
                        if (tamount > 0) {

                            referral_layout.setVisibility(View.VISIBLE);
                            price_layout.setVisibility(View.VISIBLE);
                            txtReferrals.setText(getResources().getString(R.string.Rs) + tamount.toString());
                        } else {
                            referral_layout.setVisibility(View.VISIBLE);
                        }

                    }

                } catch (Exception e) {

                }
            }


        }
    }

    private void ShowPromoDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(earn_cash.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog_promo, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(earn_cash.this);
        alertDialogBuilder.setView(promptView);
        final EditText editText = (EditText) promptView.findViewById(R.id.edtpromo);
        final ImageView imgclose = (ImageView) promptView.findViewById(R.id.imgclose);
        final RelativeLayout rlredeem = (RelativeLayout) promptView.findViewById(R.id.rlpromo);
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
                refcode = editText.getText().toString();
                progress.setVisibility(View.VISIBLE);
                postPromo(refcode);
                alert.dismiss();
            }
        });

        // create an alert dialog

        alert.show();
    }

    public void postPromo(String strref) {
        JSONObject data = new JSONObject();

        //////System.out.println("data for sending__" + data);postPromo
        try {
            data.put("code", strref);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //////System.out.println( EnvConstants.APP_BASE_URL + "/user/myinfo/"+ data);
        ApiService.getInstance(earn_cash.this, 1).postData(earn_cash.this, EnvConstants.APP_BASE_URL + "/coupon/promo/", data, SCREEN_NAME, "promotoserver");


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

    public void Share() {
        BranchUniversalObject branchUniversalObject = null;


        branchUniversalObject = new BranchUniversalObject()
                .setCanonicalIdentifier("item/12345")
                .setTitle("INVITE YOUR FRIEND")
                .setContentDescription("Hey there! I want to you to join me on Zapyle - the ultimate luxury fashion destination. Let's shop together!" + "\n\n" + "Don't forget to download the app using my referral link: " )
                .addContentMetadata("referral_user_name",GetSharedValues.getZapname(this))
                .addContentMetadata("referral_user_id",String.valueOf(GetSharedValues.getuserId(this)))
                .addContentMetadata("friend_amount", String.valueOf(int_referring))
                .addContentMetadata("profileImage",GetSharedValues.getUserprofilepic(this))
            .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC);

        LinkProperties linkProperties = new LinkProperties()
                .setChannel("facebook")
                .setFeature("sharing")
                .addControlParameter("$android_url","http://appesoftproduct.com/app-production-debug.apk" );
        // .addControlParameter("$zapyle", "item/12345");

        ShareSheetStyle shareSheetStyle = new ShareSheetStyle(earn_cash.this, "zapyle", "")
                //  .setCopyUrlStyle(R.id., copyUrlMessage, copiedUrlMessage)
                // .setMoreOptionStyle(getResources().getDrawable(android.R.drawable.ic_menu_search), "More options")
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.FACEBOOK)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.EMAIL)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.MESSAGE)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.TWITTER);

        branchUniversalObject.showShareSheet(earn_cash.this, linkProperties, shareSheetStyle, null);
    }
}

package activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
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

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

import application.MyApplicationClass;
import network.ApiCommunication;
import network.ApiService;
import utils.ExternalFunctions;
import utils.FontUtils;
import utils.GetSharedValues;

//import com.uxcam.UXCam;

//import com.//Appsee.////Appsee;


public class About_us extends ActionBarActivity implements ApiCommunication {

    private Button gotoHome_btn;
    String callingActivity = "activity.HomePage";
    private Tracker mTracker;
    String SCREEN_NAME="ABOUT_US";

    String stime = "";
    String endtime = "";
    CleverTapAPI cleverTap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        MyApplicationClass application = (MyApplicationClass) getApplication();
        mTracker = application.getDefaultTracker();


        stime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        //UXCam.startWithKey("1dfb25141864376");
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);


        try {
            cleverTap = CleverTapAPI.getInstance(getApplicationContext());
        } catch (CleverTapMetaDataNotFoundException e) {
            // thrown if you haven't specified your CleverTap Account ID or Token in your AndroidManifest.xml
        } catch (CleverTapPermissionsNotSatisfied e) {
            // thrown if you haven’t requested the required permissions in your AndroidManifest.xml
        }
        ////////Thread.currentThread().setDefaultUncaughtExceptionHandler(new SplashScreen.MyUncaughtExceptionHandler());

        try {
            callingActivity = "activity." + getIntent().getStringExtra("activity");
        } catch (Exception e) {
            callingActivity = "activity.HomePage";
        }

        View view = findViewById(R.id.aboutuslayout);
        FontUtils.setCustomFont(view, getAssets());

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final View customView = getLayoutInflater().inflate(R.layout.product_bar, null);
        getSupportActionBar().setCustomView(customView);
        Toolbar parent = (Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0, 0);
        TextView tv_headTitle = (TextView) findViewById(R.id.product_title_text);
        tv_headTitle.setText("ABOUT US");


        ImageView Skip = (ImageView) findViewById(R.id.productfeedButton);
        Skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent dintent = null;
        try {
            dintent = new Intent(About_us.this, Class.forName(callingActivity));
            dintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            dintent.putExtra("activity", "SplashScreen");
            startActivity(dintent);
            finish();
        } catch (Exception e) {
            dintent = new Intent(About_us.this, discover.class);
            dintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            dintent.putExtra("activity", "SplashScreen");
            startActivity(dintent);
            finish();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.set("UserName", GetSharedValues.getUsername(getApplicationContext()));
        mTracker.set("ZapUsername", GetSharedValues.getZapname(getApplicationContext()));
        mTracker.setScreenName("About us");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

    }


    @Override
    protected void onRestart() {
        super.onRestart();
        if (!GetSharedValues.GetgcmId(About_us.this).equals("")) {
            ApiService.getInstance(About_us.this, 1).getData(About_us.this, true, "ABOUTUS", EnvConstants.APP_BASE_URL + "/user/session/?gcm_reg_id=" + GetSharedValues.GetgcmId(About_us.this), "session");
        } else {
            ApiService.getInstance(About_us.this, 1).getData(About_us.this, true, "ABOUTUS", EnvConstants.APP_BASE_URL + "/user/session/", "session");
        }

    }

    @Override
    public void onResponseCallback(JSONObject response, String flag) {

    }

    @Override
    public void onErrorCallback(VolleyError error, String flag) {

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
}

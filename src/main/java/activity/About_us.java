package activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
//import com.uxcam.UXCam;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONObject;

import application.MyApplicationClass;
import network.ApiCommunication;
import network.ApiService;
import utils.FontUtils;
import utils.GetSharedValues;

//import com.//Appsee.////Appsee;


public class About_us extends ActionBarActivity implements ApiCommunication {

    private Button gotoHome_btn;
    String callingActivity = "activity.HomePage";
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        MyApplicationClass application = (MyApplicationClass) getApplication();
        mTracker = application.getDefaultTracker();


        //UXCam.startWithKey("1dfb25141864376");
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
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
            dintent = new Intent(About_us.this, HomePageNew.class);
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
}

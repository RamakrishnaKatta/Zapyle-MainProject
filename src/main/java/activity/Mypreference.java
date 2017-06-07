package activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
//import com.//Appsee.//Appsee;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.api.Api;
//import com.uxcam.UXCam;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONObject;

import application.MyApplicationClass;
import network.ApiCommunication;
import network.ApiService;
import utils.FontUtils;
import utils.GetSharedValues;

public class Mypreference extends AppCompatActivity implements ApiCommunication{
    RelativeLayout rl1,rl2,rl3;
    String SCREEN_NAME = "MYPREFERENCE";
    Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypreference);
        MyApplicationClass application = (MyApplicationClass) getApplication();
        mTracker = application.getDefaultTracker();
        //Appsee.start("4fb55809ab45411c909521bda720f8e6");
        //UXCam.startWithKey("1dfb25141864376");         overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        //////Thread.currentThread().setDefaultUncaughtExceptionHandler(new SplashScreen.MyUncaughtExceptionHandler());
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        View customView = getLayoutInflater().inflate(R.layout.profile_actionbar, null);
        getSupportActionBar().setCustomView(customView);
        Toolbar parent = (Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0, 0);
        TextView tvbartitle=(TextView)findViewById(R.id.product_title_text);
        tvbartitle.setText("MY PREFERENCES");
        View view = findViewById(R.id.rl);
        FontUtils.setCustomFont(view, getAssets());
        rl1=(RelativeLayout)findViewById(R.id.rl1);
        rl2=(RelativeLayout)findViewById(R.id.rl2);
        rl3=(RelativeLayout)findViewById(R.id.rl3);

        ImageView imgback=(ImageView)findViewById(R.id.profilebackButton);
        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        rl1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rl1.setBackgroundColor(Color.parseColor("#FFDBDEE1"));
                rl2.setBackgroundColor(Color.WHITE);
                rl3.setBackgroundColor(Color.WHITE);
                Intent onboardin2 = new Intent(Mypreference.this, Onboarding2.class);
                onboardin2.putExtra("booltype", false);
                onboardin2.putExtra("activity","Mypreference");
                startActivity(onboardin2);
                finish();

            }
        });

        rl2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl2.setBackgroundColor(Color.parseColor("#FFDBDEE1"));
                rl3.setBackgroundColor(Color.WHITE);
                rl1.setBackgroundColor(Color.WHITE);
                Onboarding2.isskip=false;
                Onboarding3.seemoreStatus=false;
                Intent onboardin3 = new Intent(Mypreference.this, Onboarding3.class);
                onboardin3.putExtra("booltype", false);
                onboardin3.putExtra("activity","Mypreference");
                startActivity(onboardin3);
                finish();

            }
        });

        rl3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl3.setBackgroundColor(Color.parseColor("#FFDBDEE1"));
                rl2.setBackgroundColor(Color.WHITE);
                rl1.setBackgroundColor(Color.WHITE);
                Intent onboardin4 = new Intent(Mypreference.this, Onboarding4.class);
                onboardin4.putExtra("booltype",false);
                onboardin4.putExtra("activity","Mypreference");
                startActivity(onboardin4);
                finish();

            }
        });


    }
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }
    @Override
    public void onBackPressed() {

        Intent dintent = new Intent(Mypreference.this, Myaccountpage.class);
        startActivity(dintent);
        finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(!GetSharedValues.GetgcmId(Mypreference.this).equals("")) {
            ApiService.getInstance(Mypreference.this, 1).getData(Mypreference.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/?gcm_reg_id="+GetSharedValues.GetgcmId(Mypreference.this), "session");
        }
        else {
            ApiService.getInstance(Mypreference.this, 1).getData(Mypreference.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/", "session");
        }
    }

    @Override
    public void onResponseCallback(JSONObject response, String flag) {

    }

    @Override
    public void onErrorCallback(VolleyError error, String flag) {

    }


    @Override
    protected void onResume() {
        super.onResume();
        mTracker.set("UserName", GetSharedValues.getUsername(getApplicationContext()));mTracker.set("ZapUsername", GetSharedValues.getZapname(getApplicationContext()));   mTracker.setScreenName("Onboarding2");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}

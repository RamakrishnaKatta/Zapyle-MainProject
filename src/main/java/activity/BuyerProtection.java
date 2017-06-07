package activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

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

/**
 * Created by haseeb on 16/1/16.
 */
public class BuyerProtection extends ActionBarActivity implements ApiCommunication{

    String SCREEN_NAME = "BUYERPROTECTION";

    Integer album_id = 0;
    Tracker mTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buyerprotection);
        MyApplicationClass application = (MyApplicationClass) getApplication();
        mTracker = application.getDefaultTracker();

        //UXCam.startWithKey("1dfb25141864376");         overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        ////Appsee.start("4fb55809ab45411c909521bda720f8e6");
        //////Thread.currentThread().setDefaultUncaughtExceptionHandler(new SplashScreen.MyUncaughtExceptionHandler());
        View view = findViewById(R.id.bpLayout);
        FontUtils.setCustomFont(view, getAssets());

        album_id = getIntent().getIntExtra("album_id",0);

        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final View customView = getLayoutInflater().inflate(R.layout.buyerprotection_bar, null);
        getSupportActionBar().setCustomView(customView);
        Toolbar parent = (Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0, 0);

        ImageView Skip = (ImageView) findViewById(R.id.bpbackButton);
        Skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
    @Override
    public void onBackPressed() {
        Intent mydialog = new Intent(BuyerProtection.this, ProductPage.class);
        mydialog.putExtra("activity", "1");
        mydialog.putExtra("album_id", album_id);
        mydialog.putExtra("pta", false);
        mydialog.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mydialog);
        finish();

    }
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        if(!GetSharedValues.GetgcmId(BuyerProtection.this).equals("")) {
            ApiService.getInstance(BuyerProtection.this, 1).getData(BuyerProtection.this, true, "ADDADDRESS", EnvConstants.APP_BASE_URL + "/user/session/?gcm_reg_id="+GetSharedValues.GetgcmId(BuyerProtection.this), "session");
        }
        else {
            ApiService.getInstance(BuyerProtection.this, 1).getData(BuyerProtection.this, true, "ADDADDRESS", EnvConstants.APP_BASE_URL + "/user/session/", "session");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.set("UserName", GetSharedValues.getUsername(getApplicationContext()));mTracker.set("ZapUsername", GetSharedValues.getZapname(getApplicationContext()));   mTracker.setScreenName("Buyer protection");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onResponseCallback(JSONObject response, String flag) {

    }

    @Override
    public void onErrorCallback(VolleyError error, String flag) {

    }
}

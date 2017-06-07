package activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
//import com.uxcam.UXCam;
import com.zapyle.zapyle.R;

import application.MyApplicationClass;
import utils.FontUtils;
import utils.GetSharedValues;
//import com.//Appsee.////Appsee;

/**
 * Created by haseeb on 16/1/16.
 */
public class ConditionGuide extends ActionBarActivity {

    Integer album_id = 0;
    String activityName;
    Boolean pta = false;
    Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conditionguide);
        MyApplicationClass application = (MyApplicationClass) getApplication();
        mTracker = application.getDefaultTracker();
        ////Appsee.start("4fb55809ab45411c909521bda720f8e6");
        //UXCam.startWithKey("1dfb25141864376");         overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        //////Thread.currentThread().setDefaultUncaughtExceptionHandler(new SplashScreen.MyUncaughtExceptionHandler());
        View view = findViewById(R.id.cgLayout);
        FontUtils.setCustomFont(view, getAssets());
        try {
            album_id = getIntent().getIntExtra("album_id", 0);
            activityName = getIntent().getStringExtra("activity");
            pta = getIntent().getBooleanExtra("pta", false);
        } catch (Exception e) {

        }

        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final View customView = getLayoutInflater().inflate(R.layout.conditionguide_bar, null);
        getSupportActionBar().setCustomView(customView);
        Toolbar parent = (Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0, 0);


        ImageView Skip = (ImageView) findViewById(R.id.cgbackButton);
        Skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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
        if(activityName.contains("ProductPage")) {
            Intent mydialog = new Intent(ConditionGuide.this, ProductPage.class);
            mydialog.putExtra("activity", "1");
            mydialog.putExtra("album_id", album_id);
            mydialog.putExtra("pta", false);
            mydialog.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mydialog);
            finish();
        }
        else {
            Intent uploadIntent = new Intent(ConditionGuide.this, Upload2.class);
            setResult(RESULT_OK, uploadIntent);
            finish();
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        mTracker.set("UserName", GetSharedValues.getUsername(getApplicationContext()));mTracker.set("ZapUsername", GetSharedValues.getZapname(getApplicationContext()));   mTracker.setScreenName("Condition guide");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}

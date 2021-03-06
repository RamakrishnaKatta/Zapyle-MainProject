package activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

//import com.//Appsee.////Appsee;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
//import com.uxcam.UXCam;
import com.zapyle.zapyle.R;

import application.MyApplicationClass;
import utils.FontUtils;
import utils.GetSharedValues;

/**
 * Created by haseeb on 6/4/16.
 */
public class Authenticity extends ActionBarActivity {

    String callingActivity = "activity.HomePage";
    Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authenticity);
        MyApplicationClass application = (MyApplicationClass) getApplication();
        mTracker = application.getDefaultTracker();
        //UXCam.startWithKey("1dfb25141864376");         overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);

        try {
            callingActivity = "activity." + getIntent().getStringExtra("activity");
        }
        catch (Exception e){
            callingActivity = "activity.HomePage";
        }

        ////Appsee.start("4fb55809ab45411c909521bda720f8e6");
        ////////Thread.currentThread().setDefaultUncaughtExceptionHandler(new SplashScreen.MyUncaughtExceptionHandler());

        View view = findViewById(R.id.authenticitylayout);
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
        tv_headTitle.setText("AUTHENTICITY");


        ImageView Skip = (ImageView) findViewById(R.id.productfeedButton);
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
        Intent dintent = null;
        try {
            dintent = new Intent(this, Class.forName(callingActivity));
            dintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            dintent.putExtra("activity","SplashScreen");
            startActivity(dintent);
            finish();
        }
        catch (Exception e){
            dintent = new Intent(this, HomePageNew.class);
            dintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            dintent.putExtra("activity","SplashScreen");
            startActivity(dintent);
            finish();
        }



    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.set("UserName", GetSharedValues.getUsername(getApplicationContext()));mTracker.set("ZapUsername", GetSharedValues.getZapname(getApplicationContext()));   mTracker.setScreenName("Authenticity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}

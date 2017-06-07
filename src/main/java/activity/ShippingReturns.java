package activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

//import com.//Appsee.//Appsee;
//import com.uxcam.UXCam;
import com.zapyle.zapyle.R;

import utils.FontUtils;

/**
 * Created by haseeb on 16/1/16.
 */
public class ShippingReturns extends ActionBarActivity{
    Integer album_id = 0, orderID = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shipping_returns);
        //Appsee.start("4fb55809ab45411c909521bda720f8e6");
        //UXCam.startWithKey("1dfb25141864376");         overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        //////Thread.currentThread().setDefaultUncaughtExceptionHandler(new SplashScreen.MyUncaughtExceptionHandler());
        View view = findViewById(R.id.srLayout);
        FontUtils.setCustomFont(view, getAssets());

        try {
            album_id = getIntent().getIntExtra("album_id",0);
        }catch (Exception e){

        }
        try {
            orderID = getIntent().getIntExtra("orderID",0);
        }catch (Exception e){

        }

        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final View customView = getLayoutInflater().inflate(R.layout.shippingreturns_bar, null);
        getSupportActionBar().setCustomView(customView);
        Toolbar parent = (Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0, 0);


        ImageView Skip = (ImageView) findViewById(R.id.SRbackButton);
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
        if (album_id != 0) {
            Intent mydialog = new Intent(ShippingReturns.this, ProductPage.class);
            mydialog.putExtra("activity", "1");
            mydialog.putExtra("album_id", album_id);
            mydialog.putExtra("pta", false);
            mydialog.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mydialog);
            finish();
        }else {
            Intent orderTrack = new Intent(ShippingReturns.this, OrderTrack.class);
            orderTrack.putExtra("orderID", String.valueOf(orderID));
            startActivity(orderTrack);
            finish();
        }

    }


}

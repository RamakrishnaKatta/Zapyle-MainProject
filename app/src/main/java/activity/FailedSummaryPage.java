package activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


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
import in.juspay.godel.ui.JuspayBrowserFragment;
import in.juspay.juspaysafe.BrowserCallback;
import in.juspay.juspaysafe.BrowserParams;
import in.juspay.juspaysafe.JuspaySafeBrowser;

import utils.ExternalFunctions;
import utils.GetSharedValues;

//import com.uxcam.UXCam;

//import com.//Appsee.//Appsee;

/**
 * Created by haseeb on 19/11/15.
 */
public class FailedSummaryPage extends ActionBarActivity {
    public String SCREEN_NAME = "FAILED_TRANSACTION";

    //    selected items
    ProgressBar progressbar;
    Boolean zapCashStatus = false;
    TextView tryAgain;



    String selectedPaymentMethod;

    ProgressDialog mProgress;

    String TxnId;
    Boolean CouponApplied = false;

    int AlbumId = 0;
    int CouponPrice = 0;

    //int selectedAddressId = 0;


    Tracker mTracker;
    String mobile_url;

    String stime = "";
    String endtime = "";
    CleverTapAPI cleverTap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.failed_transaction);
        MyApplicationClass application = (MyApplicationClass) getApplication();
        mTracker = application.getDefaultTracker();
        //UXCam.startWithKey("1dfb25141864376");
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        //////Thread.currentThread().setDefaultUncaughtExceptionHandler(new SplashScreen.MyUncaughtExceptionHandler());
        stime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        try {
            cleverTap = CleverTapAPI.getInstance(getApplicationContext());
        } catch (CleverTapMetaDataNotFoundException e) {
            // thrown if you haven't specified your CleverTap Account ID or Token in your AndroidManifest.xml
        } catch (CleverTapPermissionsNotSatisfied e) {
            // thrown if you haven’t requested the required permissions in your AndroidManifest.xml
        }
        mProgress = new ProgressDialog(FailedSummaryPage.this);
        mProgress.setCancelable(false);




        SharedPreferences settings = getSharedPreferences("BuySession",
                Context.MODE_PRIVATE);



        AlbumId = settings.getInt("AlbumId", 0);
        CouponApplied = settings.getBoolean("CouponStatus", false);
        CouponPrice = settings.getInt("CouponPrice", 0);
        zapCashStatus = settings.getBoolean("ZapCashUsed", false);
        tryAgain = (TextView) findViewById(R.id.tryAgain);
        mobile_url= settings.getString("mobile_url","");
        TxnId=settings.getString("TxnId","");





        //System.out.println("zapCashStatus:" + zapcashUsed);


        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final View customView = getLayoutInflater().inflate(R.layout.summary_action_bar, null);
        getSupportActionBar().setCustomView(customView);
        Toolbar parent = (Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0, 0);
        TextView heading = (TextView) customView.findViewById(R.id.heading);
        heading.setText("Payment Failed");


        //System.out.println("PAYMENTETHOD:" + selectedPaymentMethod + "__" + netbankingOption + "__" + paymentOption + "__" + cardOption);

        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                        Confirm();


            }
        });



    }







//    Confirm function
//    --------------------------------------------------------------

    public void Confirm() {
        //System.out.println("PAYMENT:" + "inside confirm");
        BrowserParams browserParams = new BrowserParams();

        browserParams.setUrl(mobile_url);

        JuspayBrowserFragment.openJuspayConnection(this);
        JuspaySafeBrowser.start(this, browserParams, callBack);


    }


//    Server responses
//    ----------------------------------------------------------------







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

    @Override
    public void onBackPressed() {

        Intent feed = new Intent(getApplicationContext(), MainFeed.class);
        feed.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        feed.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);//        feed.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        feed.putExtra("activity", "FeedPage");
        startActivity(feed);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }



    @Override
    protected void onResume() {
        super.onResume();
        mTracker.set("UserName", GetSharedValues.getUsername(getApplicationContext()));
        mTracker.set("ZapUsername", GetSharedValues.getZapname(getApplicationContext()));
        mTracker.setScreenName(GetSharedValues.getZapname(getApplicationContext()) + "~" + "Failed summary page");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
    public BrowserCallback callBack = new BrowserCallback() {

        @Override
        public void ontransactionAborted() {
            // ...

            Toast.makeText(getApplicationContext(),  "Transaction cancelled.", Toast.LENGTH_LONG).show();

        }

        @Override
        public void endUrlReached(String url) {
            //  GodelTracker.getInstance().trackPaymentStatus(transactionId, GodelTracker.PaymentStatus.SUCCESS);
            Toast.makeText(getApplicationContext(), "Transaction successful!", Toast.LENGTH_LONG).show();

            Intent success = new Intent(FailedSummaryPage.this, SummaryPage.class);
            success.putExtra("TxnId", TxnId);
            startActivity(success);
            finish();
        }

    };

}
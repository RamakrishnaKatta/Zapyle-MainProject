package activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import DataBase.DatabaseDB;
import application.MyApplicationClass;
import network.ApiCommunication;
import network.ApiService;
import utils.CommonFinish;
import utils.CustomMessage;
import utils.ExternalFunctions;
import utils.FontUtils;
import utils.GetSharedValues;

//import com.//Appsee.//Appsee;
//import com.uxcam.UXCam;

/**
 * Created by haseeb on 4/10/15.
 */
public class SummaryPage extends ActionBarActivity implements ApiCommunication {
    public String SCREEN_NAME = "SUMMARY_PAGE";

    String TxnId;
    Integer cod_amount = 0;
    Boolean Cod_Status;
    TextView summarymessage;
    TextView  cod_cash;
    RelativeLayout summarylayout;
    ProgressDialog progress;


    JSONObject product;
    Date today;
   DatabaseDB db;
    Double cod_price = 0.0;
    Tracker mTracker;
    CleverTapAPI cleverTap;
    String stime = "";
    String endtime = "";



    SharedPreferences settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summary);
        MyApplicationClass application = (MyApplicationClass) getApplication();
        mTracker = application.getDefaultTracker();
        try {
            cleverTap = CleverTapAPI.getInstance(getApplicationContext());
        } catch (CleverTapMetaDataNotFoundException e) {
            // thrown if you haven't specified your CleverTap Account ID or Token in your AndroidManifest.xml
        } catch (CleverTapPermissionsNotSatisfied e) {
            // thrown if you haven’t requested the required permissions in your AndroidManifest.xml
        }


        stime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        db = new DatabaseDB(getApplicationContext());
        db.openDB();
        SharedPreferences CartSession = getSharedPreferences("CartSession",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1 = CartSession.edit();
        editor1.putInt("cartCount", 0);
        editor1.apply();
        ExternalFunctions.cartcount = 0;

        settings = getSharedPreferences("PAYMENT_MODE",
                Context.MODE_PRIVATE);
        progress = new ProgressDialog(SummaryPage.this);
        progress.setCancelable(false);
        progress.setMessage("Processing Payment ...Please Wait...");
        progress.show();

        SharedPreferences settings = getApplicationContext().getSharedPreferences("BuySession",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("AMOUNT", 0);
        editor.putString("ZAPCASH", String.valueOf(0));
        editor.putBoolean("CouponStatus", false);
        editor.putBoolean("ZapCashStatus", false);
        editor.putInt("SelectedAddress", 0);
        editor.putInt("AlbumId", 0);
        editor.putInt("CouponPrice", 0);
        editor.apply();

        String query = "delete from CART";
        db.processData(query);
        ExternalFunctions.cartcount = 0;

        LocalBroadcastManager.getInstance(this).registerReceiver(CommonFinish.broadcastReceiver, new IntentFilter("SummaryPage"));
        ExternalFunctions.cContextArray[16] = SummaryPage.this;


        summarylayout = (RelativeLayout) findViewById(R.id.summarylayout);
        summarylayout.setVisibility(View.INVISIBLE);


        View view = findViewById(R.id.summarylayout);
        FontUtils.setCustomFont(view, getAssets());
        TxnId = getIntent().getStringExtra("TxnId");
        Cod_Status = getIntent().getBooleanExtra("Cod_Status", false);
        cod_amount = getIntent().getIntExtra("Cod_Amount", 0);
        cod_price = ExternalFunctions.round(Double.parseDouble(String.valueOf(cod_amount)), 2);
        NumberFormat myFormat = NumberFormat.getInstance();
        //System.out.println("CART PRICE:"+cod_price.toString());
        summarymessage = (TextView) findViewById(R.id.summarymessage);
        cod_cash = (TextView) findViewById(R.id.cod_cash);
        if (Cod_Status) {
            cod_cash.setVisibility(View.VISIBLE);
            summarymessage.setText("Your order has been placed successfully. Please pay the delivery person an amount of");
            cod_cash.setText(getResources().getString(R.string.Rs) + myFormat.format(cod_price));
            HashMap<String, Object> charged = new HashMap<String, Object>();
            charged.put("amount", cod_price);
            charged.put("payment_mode", settings.getString("mode", "cod"));
            charged.put("charged_id", TxnId);
            cleverTap.event.push("charged", charged);

        } else {
            cod_cash.setVisibility(View.GONE);
            summarymessage.setText("Your order has been placed successfully.");
            HashMap<String, Object> charged = new HashMap<String, Object>();
            charged.put("amount", cod_price);
            charged.put("payment_mode", settings.getString("mode", "online"));
            charged.put("charged_id", TxnId);
            cleverTap.event.push("charged", charged);
        }
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final View customView = getLayoutInflater().inflate(R.layout.summary_action_bar, null);
        getSupportActionBar().setCustomView(customView);
        Toolbar parent = (Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0, 0);

        TextView heading = (TextView) customView.findViewById(R.id.heading);
        heading.setText("Payment Successful");
        TextView view_order = (TextView) findViewById(R.id.view_order);
        String sourceString1 = "VIEW ORDER DETAILS";
        view_order.setText(Html.fromHtml(sourceString1));

        SpannableString ss1 = new SpannableString(view_order.getText());
        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                try {
                    Intent order = new Intent(SummaryPage.this, Myorderlist.class);
                    order.putExtra("activity", "SummaryPage");
                    order.putExtra("TxnId", TxnId);
                    //order.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(order);
                    finish();
                }catch(Exception e){

                }
            }
        };
        ss1.setSpan(clickableSpan1, 0, view_order.getText().toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        view_order.setMovementMethod(LinkMovementMethod.getInstance());
        view_order.setText(ss1);

        TextView invite = (TextView) findViewById(R.id.invite);
        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent invite_page = new Intent(SummaryPage.this, earn_cash.class);
                startActivity(invite_page);
                finish();
            }
        });

        GetSummary();

    }

    public void gotofeed(View v) {
        Intent feed = new Intent(getApplicationContext(), discover.class);
        startActivity(feed);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        }else{
            this.finish();
        }
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


        unbindDrawables(findViewById(R.id.summarylayout));
        //System.gc();
    }

    private void unbindDrawables(View view) {
        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();

        }
    }

    @Override
    public void onBackPressed() {
        today = new Date();
        DateFormat dfclosetime = new SimpleDateFormat("hh:mm:ss aa", Locale.ENGLISH);
        dfclosetime.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));

        Intent dintent = new Intent(SummaryPage.this, discover.class);
//        dintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//        dintent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        dintent.putExtra("activity", "discover");
        startActivity(dintent);
        finish();


    }


    // Get summary
//    ----------------------------------------------------------------------
    public void GetSummary() {
        ApiService.getInstance(SummaryPage.this, 1).getData(SummaryPage.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/payment/summary/?txid=" + TxnId, "summary");

    }


//    Display values
//    ------------------------------------------------------------------------

    public void DisplayValues(Double data) {
        NumberFormat myFormat = NumberFormat.getInstance();
        myFormat.setGroupingUsed(true);
        cod_cash.setText(getResources().getString(R.string.Rs) + myFormat.format(cod_price));
        progress.dismiss();
        summarylayout.setVisibility(View.VISIBLE);
    }

//    Server response
//    --------------------------------------------------------------------

    @Override
    public void onResponseCallback(JSONObject response, String flag) {
        if (flag.equals("summary")) {
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            ////System.out.println("RESPONSE SUMMARY:"+resp);
            //System.out.println(resp);
            if (resp != null) {
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        JSONObject data = resp.getJSONObject("data");
                        if (data.getString("status").equals("success")) {
                            Double price = data.getDouble("listing_price");
                            Double shipping_charge = data.getDouble("shipping_charge");
                            Double final_price = price + shipping_charge;
                            DisplayValues(final_price);
                        } else {
                            progress.dismiss();
                            CustomMessage.getInstance().CustomMessage(SummaryPage.this, "Oops, Something went wrong!");
                        }

                    } else {
                        progress.dismiss();
                        CustomMessage.getInstance().CustomMessage(SummaryPage.this, "Oops, Something went wrong!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progress.dismiss();
                    CustomMessage.getInstance().CustomMessage(SummaryPage.this, "Oops, Something went wrong!");
                }
            } else {
                progress.dismiss();
                CustomMessage.getInstance().CustomMessage(SummaryPage.this, "Oops, Something went wrong!");
            }
        }
    }

    @Override
    public void onErrorCallback(VolleyError error, String flag) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!GetSharedValues.GetgcmId(SummaryPage.this).equals("")) {
            ApiService.getInstance(SummaryPage.this, 1).getData(SummaryPage.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/?gcm_reg_id" + GetSharedValues.GetgcmId(SummaryPage.this), "session");
        } else {
            ApiService.getInstance(SummaryPage.this, 1).getData(SummaryPage.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/", "session");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mTracker.set("UserName", GetSharedValues.getUsername(getApplicationContext()));
        mTracker.set("ZapUsername", GetSharedValues.getZapname(getApplicationContext()));
        mTracker.setScreenName("Summary page");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}

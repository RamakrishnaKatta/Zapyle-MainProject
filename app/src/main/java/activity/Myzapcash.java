package activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.exceptions.CleverTapMetaDataNotFoundException;
import com.clevertap.android.sdk.exceptions.CleverTapPermissionsNotSatisfied;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.HashMap;

import network.ApiCommunication;
import network.ApiService;
import utils.CustomMessage;
import utils.ExternalFunctions;
import utils.FontUtils;

//import com.//Appsee.//Appsee;
//import com.uxcam.UXCam;

public class Myzapcash extends AppCompatActivity implements ApiCommunication {
    String SCREEN_NAME = "MY_ZAPCASH";
    LinearLayout l1earned, l1used, l1earnedshow, l1usedshow;
    TextView tvearned, tvused, tvavail;
    int intwidth;
    int intcheckearned = 0, intcheckused = 0;
    String stime = "";
    String endtime = "";
    CleverTapAPI cleverTap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myzapcash);
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
        TextView tvbartitle = (TextView) findViewById(R.id.product_title_text);
        tvbartitle.setText("MY ZAPWALLET");
        stime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        try {
            cleverTap = CleverTapAPI.getInstance(getApplicationContext());
        } catch (CleverTapMetaDataNotFoundException e) {
            // thrown if you haven't specified your CleverTap Account ID or Token in your AndroidManifest.xml
        } catch (CleverTapPermissionsNotSatisfied e) {
            // thrown if you haven’t requested the required permissions in your AndroidManifest.xml
        }
        ImageView imgback = (ImageView) findViewById(R.id.profilebackButton);
        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tvused = (TextView) findViewById(R.id.tvused);
        tvearned = (TextView) findViewById(R.id.tvearned);
        tvavail = (TextView) findViewById(R.id.tvavail);
        l1earned = (LinearLayout) findViewById(R.id.l1earn);
        l1earnedshow = (LinearLayout) findViewById(R.id.l1showearn);
        l1used = (LinearLayout) findViewById(R.id.l1used);
        l1usedshow = (LinearLayout) findViewById(R.id.l1showused);
        View view = findViewById(R.id.rl);
        FontUtils.setCustomFont(view, getAssets());
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        intwidth = metrics.widthPixels;
        GetMyzapcash();
        l1earned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (intcheckearned == 0) {
                    l1earnedshow.setVisibility(View.VISIBLE);
                    intcheckearned = 1;
                } else {
                    l1earnedshow.setVisibility(View.GONE);
                    intcheckearned = 0;
                }


            }
        });
        l1used.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intcheckused == 0) {
                    intcheckused = 1;
                    l1usedshow.setVisibility(View.VISIBLE);
                } else {
                    intcheckused = 0;
                    l1usedshow.setVisibility(View.GONE);
                }


            }
        });

    }

    @Override
    public void onBackPressed() {

        Intent dintent = new Intent(Myzapcash.this, Myaccountpage.class);
        startActivity(dintent);
        finish();
    }

    private void GetMyzapcash() {

        ApiService.getInstance(Myzapcash.this, 1).getData(Myzapcash.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/extra/appvirality/campaign", "getzapcash");


    }

    @Override
    public void onResponseCallback(JSONObject response, String flag) {
        //System.out.println("responsezapcash___" + response);
        if (flag.equals("getzapcash")) {
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {

                try {

                    String status = resp.getString("status");
                    if (status.equals("success")) {
//                        JSONObject data = resp.getJSONObject("data");
//                        JSONArray array_zapcashused = data.getJSONArray("used_details");
//                        JSONArray array_zapcashearned = data.getJSONArray("earned_details");
//                        JSONObject array_bance = data.getJSONObject("balance");
                        JSONObject data = resp.getJSONObject("data");
                        Integer amount = data.getInt("zapcash");
                        NumberFormat myFormat = NumberFormat.getInstance();
                        myFormat.setGroupingUsed(true);


//                        if (array_bance.getString("status").equals("success")){
                        if (amount > 0) {
                            tvavail.setText("Rs. " + myFormat.format(Double.parseDouble(String.valueOf(amount))));
                        } else {
                            tvavail.setText("Rs. 0");
                        }

//                        tvused.setText("Rs. "+myFormat.format(Double.parseDouble(String.valueOf(data.getDouble("total_used")))));
//                        tvearned.setText("Rs. "+myFormat.format(Double.parseDouble(String.valueOf(data.getDouble("total_earned")))));
//
//                        int a=0,b=0;
//                        for(int i=0;i<array_zapcashearned.length();i++){
//                            LinearLayout l2h=new LinearLayout(this);
//                            l2h.setOrientation(LinearLayout.HORIZONTAL);
//                            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(intwidth/2+intwidth/3+intwidth/5, LinearLayout.LayoutParams.WRAP_CONTENT);
//                            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                            LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                           params1.gravity= Gravity.CENTER;
//                             params1.setMargins(0, 20, 0, 20);
//                            l2h.setLayoutParams(params1);
//                            ImageView img1=new ImageView(this);
//                            img1.setImageResource(R.drawable.blue);
//                            params2.gravity= Gravity.CENTER_VERTICAL;
//                            params2.setMargins(50, 0, 0, 0);
//                            img1.setLayoutParams(params2);
//                            TextView tv=new TextView(this);
//                            params3.setMargins(40, 0, 0, 0);
//                            params3.gravity= Gravity.CENTER_VERTICAL;
//                            tv.setText("Rs."+myFormat.format(Double.parseDouble(String.valueOf(array_zapcashearned.getJSONObject(i).getDouble("price"))))+" Credited against the return of "+array_zapcashearned.getJSONObject(i).getString("product"));
//                            tv.setLayoutParams(params3);
//                            tv.setTextSize(10);
//                            l2h.addView(img1);
//                            l2h.addView(tv);
//                            l1earnedshow.addView(l2h);
//                            a=1;
//                        }

//                        if (a==1){
//                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2);
//                            TextView tv=new TextView(this);
//                            tv.setLayoutParams(params);
//                            tv.setBackgroundColor(Color.parseColor("#bfbbbb"));
//                            l1earnedshow.addView(tv);
//                        }
//
//                        for(int i=0;i<array_zapcashused.length();i++){
//                            LinearLayout l2h=new LinearLayout(this);
//                            l2h.setOrientation(LinearLayout.HORIZONTAL);
//                            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(intwidth/2+intwidth/3+intwidth/5, LinearLayout.LayoutParams.WRAP_CONTENT);
//                            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                            LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                           params1.gravity= Gravity.CENTER;
//                            params1.setMargins(0, 20, 0, 20);
//                            l2h.setLayoutParams(params1);
//                            ImageView img1=new ImageView(this);
//                            img1.setImageResource(R.drawable.orange);
//                            params2.gravity= Gravity.CENTER_VERTICAL;
//                            params2.setMargins(50, 0, 0, 0);
//                            img1.setLayoutParams(params2);
//                            TextView tv=new TextView(this);
//                            params3.setMargins(40, 0, 0, 0);
//                            params3.gravity= Gravity.CENTER_VERTICAL;
//                            tv.setText("Rs." +myFormat.format(Double.parseDouble(String.valueOf(array_zapcashused.getJSONObject(i).getDouble("price")))) + " Debited for the purchase of  " + array_zapcashused.getJSONObject(i).getString("product"));
//                            tv.setLayoutParams(params3);
//                            tv.setTextSize(10);
//                            l2h.addView(img1);
//                            l2h.addView(tv);
//                            l1usedshow.addView(l2h);
//
//                        }

                        l1earnedshow.setVisibility(View.GONE);
                        l1usedshow.setVisibility(View.GONE);


                    } else {
                        // progressBar.setVisibility(View.GONE);
                        CustomMessage.getInstance().CustomMessage(Myzapcash.this, "Oops. Something went wrong!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    //////System.out.println(e.getMessage());
                    //progressBar.setVisibility(View.GONE);
                }

            }

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
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    public void onErrorCallback(VolleyError error, String flag) {

    }
}

package activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
//import com.uxcam.UXCam;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import adapters.OrderAdapter;
import application.MyApplicationClass;
import models.MyorderListmodel;
import network.ApiCommunication;
import network.ApiService;
import network.JsonParser;
import utils.CustomMessage;
import utils.ExternalFunctions;
import utils.GetSharedValues;

public class Myorderlist extends AppCompatActivity implements ApiCommunication {
    String SCREEN_NAME = "MYSALES";
    ArrayList<MyorderListmodel> dataToAdapter = new ArrayList<MyorderListmodel>();
    ProgressBar progressBar;
    Context ctx;

    RecyclerView lsv;
    String callingActivity = "BuyPage";
    String TxnId;

    private Timer t;
    private int TimeCounter = 0;
    Tracker mTracker;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myorderlist);
        MyApplicationClass application = (MyApplicationClass) getApplication();
        mTracker = application.getDefaultTracker();

        //Appsee.start("4fb55809ab45411c909521bda720f8e6");
        //UXCam.startWithKey("1dfb25141864376");
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
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
        tvbartitle.setText("MY ORDERS");
        ImageView imgback=(ImageView)findViewById(R.id.profilebackButton);

        try {
            callingActivity = "activity." + getIntent().getStringExtra("activity");
            TxnId = getIntent().getStringExtra("TxnId");
        } catch (Exception e) {
            callingActivity = "activity." + "BuyPage";

        }

        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ctx=this;
        lsv=(RecyclerView) findViewById(R.id.listView);
        progressBar = (ProgressBar) findViewById(R.id.ob3progressBar);
        progressBar.setVisibility(View.VISIBLE);



        GetOrders();

    }
    private void GetOrders() {
        //////System.out.println("xxxx"+EnvConstants.APP_BASE_URL + "/user/myorders/");
        ApiService.getInstance(Myorderlist.this, 1).getData(Myorderlist.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/myorders/", "getorders");


    }
    private void GetOverlay() {
        ApiService.getInstance(Myorderlist.this, 1).getData(Myorderlist.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/marketing/overlay/my_order", "getoverlay");
    }

    @Override
    public void onBackPressed() {
        try {
            if(callingActivity.contains("Myaccountpage")){
                Intent dintent = new Intent(Myorderlist.this, Myaccountpage.class);
                startActivity(dintent);
                finish();
            }
            else if(callingActivity.contains("SummaryPage")){
                Intent success = new Intent(Myorderlist.this, SummaryPage.class);
                success.putExtra("TxnId", TxnId);
                startActivity(success);
                finish();
            }
            else {
                Intent success = new Intent(Myorderlist.this, HomePageNew.class);
                success.putExtra("activity", "SplashScreen");
                startActivity(success);
                finish();
            }
        }
        catch (Exception e){
            Intent dintent = new Intent(Myorderlist.this, HomePageNew.class);
            startActivity(dintent);
            finish();
        }

    }
    @Override
    public void onResponseCallback(JSONObject response, String flag) {


        System.out.println("response getsales___" + response);
        if (flag.equals("getorders")) {
            JSONObject resp = JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                try {

                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        progressBar.setVisibility(View.GONE);
                        JSONObject data = resp.getJSONObject("data");
                        JSONArray arrdata=data.getJSONArray("data");
                        JSONObject data2=data.getJSONObject("reasons");
                        SharedPreferences reasons = getSharedPreferences("ReturnReason",
                                Context.MODE_PRIVATE);
                        SharedPreferences.Editor reasonseditor = reasons.edit();
                        reasonseditor.putString("Reasons", data2.toString());
                        reasonseditor.apply();


                        System.out.println("REASONS : "+data2);
                        if(arrdata.length() > 0) {
                            for (int i = 0; i < arrdata.length(); i++) {

                                JSONObject data1 = arrdata.getJSONObject(i);
                                MyorderListmodel mylist = new MyorderListmodel();
                                mylist.setimagepath(data1.getString("image"));
                                mylist.setproductname(data1.getString("title"));
                                mylist.setamount(data1.getInt("amount"));
                                mylist.setOriginalPrice(String.valueOf(data1.getInt("original_price")));
                                mylist.setreturn(data1.getBoolean("can_return"));
                                mylist.setorderid(data1.getString("order_id"));
                                mylist.setstatus(data1.getString("status"));
                                mylist.setsize(data1.getString("size"));
                                mylist.setPlaced_at(data1.getString("placed_at"));


                                mylist.setreq(data2.getString("0") + "," + data2.getString("1") + "," + data2.getString("2") + "," + data2.getString("3") + "," + data2.getString("4"));


                                //////System.out.println("IIIIIIIII" + size);
                                dataToAdapter.add(mylist);
                                if (ExternalFunctions.strOverlayurl.length() == 0) {
                                    GetOverlay();
                                }

                            }

                            OrderAdapter lst = new OrderAdapter(dataToAdapter, this);
                            lsv.setLayoutManager(new LinearLayoutManager(this));
                            lsv.setAdapter(lst);
                        }
                        else {

                            progressBar.setVisibility(View.GONE);
                            Intent mydialog = new Intent(Myorderlist.this, AlertActivity.class);
                            //int imgid=R.drawable.alertmyorder;
                            String strmessage="YOU HAVE'NT ORDER ANYTHING YET?";
                           // mydialog.putExtra("imgid", imgid);
                            mydialog.putExtra("Message",strmessage);
                            mydialog.putExtra("Buttontext"," GO SHOPPING ");
                            mydialog.putExtra("tip","");
                            mydialog.putExtra("activity","BuyPage");
                            mydialog.putExtra("header","MY ORDERS");
                            mydialog.putExtra("calling", "Myaccountpage");
                            startActivity(mydialog);
                            finish();
                        }


                    }


                    else {
                        progressBar.setVisibility(View.GONE);
                        Intent mydialog = new Intent(Myorderlist.this, AlertActivity.class);
                        //int imgid=R.drawable.alertoop;
                        String strmessage="OOPS!";
                       // mydialog.putExtra("imgid", imgid);
                        mydialog.putExtra("Message",strmessage);
                        mydialog.putExtra("Buttontext"," RETRY ");
                        mydialog.putExtra("tip","SOMETHING WENT WRONG");
                        mydialog.putExtra("activity","Myaccountpage");
                        mydialog.putExtra("calling", "Myaccountpage");
                        startActivity(mydialog);
                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    Intent mydialog = new Intent(Myorderlist.this, AlertActivity.class);
                    //int imgid=R.drawable.alertoop;
                    String strmessage="OOPS!";
                   // mydialog.putExtra("imgid", imgid);
                    mydialog.putExtra("Message",strmessage);
                    mydialog.putExtra("Buttontext"," RETRY ");
                    mydialog.putExtra("tip","SOMETHING WENT WRONG");
                    mydialog.putExtra("activity", "Myaccountpage");
                    mydialog.putExtra("calling", "Myaccountpage");
                    startActivity(mydialog);
                    finish();

                }

            }


        }
        else if (flag.equals("getoverlay")) {
            JSONObject resp = JsonParser.getInstance().parserJsonObject(response);
            //System.out.println("overlay"+resp);
            try {
                String status=resp.getString("status");
                if(!status.equals("error")){
                    JSONObject data = resp.getJSONObject("data");
                    final String strimage=data.getString("image");
                    final boolean bl_close=data.getBoolean("can_close");
                    final String strtilte=data.getString("title");
                    final String str_uri=data.getString("uri_target");
                    final String str_description=data.getString("description");
                    String actname=data.getString("android_activity");
                    final String activityname = "activity."+actname;
                    // final String activityname = "activity.ProductPage";
                    //System.out.println("overlay new"+activityname);
                    boolean bl_active = data.getBoolean("active");
                    final int intDelay = data.getInt("delay");
                    final boolean bl_fullscreen = data.getBoolean("full_screen");
                    final String strbutton = data.getString("cta_text");
                    ExternalFunctions.strOverlayactivity = actname;
                    ExternalFunctions.bloverlay=true;

                    ExternalFunctions.strOverlayurl = str_uri;
                    if (intDelay > 0) {

                        ExternalFunctions.bloverlay = true;
                        t = new Timer();
                        t.scheduleAtFixedRate(new TimerTask() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                runOnUiThread(new Runnable() {
                                    public void run() {

                                        TimeCounter++;
                                        if (TimeCounter == intDelay) {

                                            t.cancel();
                                            ExternalFunctions.showOverlay(getApplicationContext(), strtilte, str_description, strbutton, activityname, bl_fullscreen, bl_close,strimage);

                                        }
                                    }
                                });

                            }
                        }, 500, 500);
                    }


                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        else  if (flag.equals("Return")) {
            JSONObject resp = JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                try {

                    String status = resp.getString("status");
                    if (status.equals("success")) {

                        //////System.out.println("Returned");

                    }

                } catch (JSONException e) {
                    progressBar.setVisibility(View.GONE);
                    Intent mydialog = new Intent(Myorderlist.this, AlertActivity.class);
                    //int imgid=R.drawable.alertoop;
                    String strmessage="OOPS!";
                   // mydialog.putExtra("imgid", imgid);
                    mydialog.putExtra("Message",strmessage);
                    mydialog.putExtra("Buttontext"," RETRY ");
                    mydialog.putExtra("tip","SOMETHING WENT WRONG");
                    mydialog.putExtra("activity","Myaccountpage");
                    mydialog.putExtra("calling", "Myaccountpage");
                    startActivity(mydialog);
                    finish();
                }

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
        if(!GetSharedValues.GetgcmId(Myorderlist.this).equals("")) {
            ApiService.getInstance(Myorderlist.this, 1).getData(Myorderlist.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/?gcm_reg_id="+GetSharedValues.GetgcmId(Myorderlist.this), "session");
        }
        else {
            ApiService.getInstance(Myorderlist.this, 1).getData(Myorderlist.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/", "session");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.set("UserName", GetSharedValues.getUsername(getApplicationContext()));mTracker.set("ZapUsername", GetSharedValues.getZapname(getApplicationContext()));   mTracker.setScreenName("My orders");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}

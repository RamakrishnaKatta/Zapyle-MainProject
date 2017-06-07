package activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
//import com.//Appsee.//Appsee;
import com.appvirality.CampaignHandler;
import com.appvirality.android.AppviralityAPI;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
//import com.uxcam.UXCam;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONException;
import org.json.JSONObject;

import DataBase.DatabaseDB;
import application.MyApplicationClass;
import network.ApiCommunication;
import network.ApiService;
import utils.CommonFinish;
import utils.CustomMessage;
import utils.ExternalFunctions;
import utils.FontUtils;
import utils.GetSharedValues;

public class Myaccountpage extends AppCompatActivity implements ApiCommunication {
    public String SCREEN_NAME = "MYACCOUNTPAGE";
    LinearLayout l1mypref, l1mysale, l1myorder, l1mylove, l1myadd, l1myzap, l1mybank, l1logout;
    Context ctx;
    String callingActivity = "activity.HomePage";
    DatabaseDB db;
    Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myaccount);
        MyApplicationClass application = (MyApplicationClass) getApplication();
        mTracker = application.getDefaultTracker();
        //Appsee.start("4fb55809ab45411c909521bda720f8e6");
        //UXCam.startWithKey("1dfb25141864376");
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        //////Thread.currentThread().setDefaultUncaughtExceptionHandler(new SplashScreen.MyUncaughtExceptionHandler());


//        Database function
//        ----------------------------------------------------

        db = new DatabaseDB(getApplicationContext());
        db.openDB();


        try {
            callingActivity = "activity." + getIntent().getStringExtra("activity");
        } catch (Exception e) {
            callingActivity = "activity.HomePage";
        }


        LocalBroadcastManager.getInstance(this).registerReceiver(CommonFinish.broadcastReceiver, new IntentFilter("Myaccountpage"));
        ExternalFunctions.cContextArray[25] = Myaccountpage.this;


        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        View customView = getLayoutInflater().inflate(R.layout.profile_actionbar, null);
        getSupportActionBar().setCustomView(customView);
        Toolbar parent = (Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0, 0);
        TextView tvbartitle = (TextView) findViewById(R.id.product_title_text);
        tvbartitle.setText("MY ACCOUNT");
        ctx = this;
        View view = findViewById(R.id.rl);
        ExternalFunctions.bloverlay = false;
        FontUtils.setCustomFont(view, getAssets());
        l1mypref = (LinearLayout) findViewById(R.id.l1mypref);
        l1mysale = (LinearLayout) findViewById(R.id.l1mysale);
        l1myorder = (LinearLayout) findViewById(R.id.l1myord);
        l1mylove = (LinearLayout) findViewById(R.id.l1mylove);
        l1myadd = (LinearLayout) findViewById(R.id.l1myadd);
        l1myzap = (LinearLayout) findViewById(R.id.l1myzapcash);
        l1mybank = (LinearLayout) findViewById(R.id.l1mybank);
        l1logout = (LinearLayout) findViewById(R.id.l1logout);
        ImageView imgback = (ImageView) findViewById(R.id.profilebackButton);
        MyAddress.intcheck = 1;
        Mybankcash.intcheck = 2;
        ExternalFunctions.bloverlay = false;

        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        l1mypref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Mypreference.class);
                startActivity(intent);
                finish();
//                Intent intent = new Intent(getApplicationContext(), searchnew.class);
//                startActivity(intent);
                // finish();
            }
        });
        l1mysale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Mysales.class);
                startActivity(intent);
                finish();
            }
        });

        l1myorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Myorderlist.class);
                intent.putExtra("activity", "Myaccountpage");
                startActivity(intent);
                finish();
            }
        });

        l1mylove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyLoves.class);
                startActivity(intent);
                finish();
            }
        });
        l1myadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyAddress.class);
                startActivity(intent);
                finish();
            }
        });
        l1myzap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Myzapcash.class);
                startActivity(intent);
                finish();
            }
        });
        l1mybank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Mybankcash.class);
                startActivity(intent);
                finish();
            }
        });
        l1logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
                alertDialogBuilder.setTitle("LOGOUT");
                String query = "delete from CART";
                db.processData(query);

                // set dialog message
                alertDialogBuilder
                        .setMessage("Do you want to logout ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String deleteQuery = "delete from CART";
                                db.processData(deleteQuery);
                                ExternalFunctions.cartcount = 0;

                                CampaignHandler.setCampaignDetails(null);
                                CampaignHandler.setReferrerDetails(null);
                                AppviralityAPI.LogOut(getApplicationContext());

                                ApiService.getInstance(Myaccountpage.this, 1).getData(Myaccountpage.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/account/logout/", "logout");
                                SharedPreferences settings = getSharedPreferences("LoginSession",
                                        Context.MODE_PRIVATE);

                                SharedPreferences.Editor editor = settings.edit();
                                editor.putBoolean("LOGIN_STATUS", false);
                                editor.putBoolean("GuestLoginStatus", false);
                                editor.putString("session_id", "");
                                editor.apply();
                                ExternalFunctions.strfilter = "";
                                ExternalFunctions.sort = 0;


                                ExternalFunctions.intprice = 0;
                                ExternalFunctions.intcat = 0;
                                ExternalFunctions.intsize = 0;


                                Intent intent = new Intent("FeedPage");
                                intent.putExtra("action", "close");
                                intent.putExtra("activityIndex", "ALL");
                                LocalBroadcastManager.getInstance(Myaccountpage.this).sendBroadcast(intent);
                                AppviralityAPI.init(getApplicationContext());
                                Intent parallax = new Intent(Myaccountpage.this, Parallax.class);
                                parallax.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                EmailLogin.k = 1;
                                parallax.putExtra("booltype", false);
                                startActivity(parallax);
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }

            //finish();

        });

    }

    @Override
    public void onResponseCallback(JSONObject response, String flag) {
        if (flag.equals("logout")) {
            //System.out.println(response);
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {

                    } else {
                        Intent mydialog = new Intent(Myaccountpage.this, AlertActivity.class);
                       // int imgid = R.drawable.alertoop;
                        String strmessage = "OOPS!";
                       // mydialog.putExtra("imgid", imgid);
                        mydialog.putExtra("Message", strmessage);
                        mydialog.putExtra("Buttontext", " RETRY ");
                        mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                        mydialog.putExtra("activity", "Myaccountpage");
                        startActivity(mydialog);
                        finish();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
//                    Intent mydialog = new Intent(Myaccountpage.this, AlertActivity.class);
//                   // int imgid = R.drawable.alertoop;
//                    String strmessage = "OOPS!";
//                   // mydialog.putExtra("imgid", imgid);
//                    mydialog.putExtra("Message", strmessage);
//                    mydialog.putExtra("Buttontext", " RETRY ");
//                    mydialog.putExtra("tip", "SOMETHING WENT WRONG");
//                    mydialog.putExtra("activity", "Myaccountpage");
//                    startActivity(mydialog);
//                    finish();


                }
            } else {
                Intent mydialog = new Intent(Myaccountpage.this, AlertActivity.class);
               // int imgid = R.drawable.alertoop;
                String strmessage = "OOPS!";
               // mydialog.putExtra("imgid", imgid);
                mydialog.putExtra("Message", strmessage);
                mydialog.putExtra("Buttontext", " RETRY ");
                mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                mydialog.putExtra("activity", "Myaccountpage");
                startActivity(mydialog);
                finish();

            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent mydialog = null;
        try {
            mydialog = new Intent(Myaccountpage.this, Class.forName(callingActivity));
            mydialog.putExtra("activity", "SplashScreen");
            startActivity(mydialog);
            finish();
        } catch (Exception e) {
            mydialog = new Intent(Myaccountpage.this, HomePageNew.class);
            mydialog.putExtra("activity", "SplashScreen");
            startActivity(mydialog);
            finish();
        }

    }

    @Override
    public void onErrorCallback(VolleyError error, String flag) {

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!GetSharedValues.GetgcmId(Myaccountpage.this).equals("")) {
            ApiService.getInstance(Myaccountpage.this, 1).getData(Myaccountpage.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/?gcm_reg_id=" + GetSharedValues.GetgcmId(Myaccountpage.this), "session");
        } else {
            ApiService.getInstance(Myaccountpage.this, 1).getData(Myaccountpage.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/", "session");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ////System.out.println("inside on destroy");
        View view = findViewById(R.id.rl);
        unbindDrawables(findViewById(R.id.rl));
        Runtime.getRuntime().gc();
        ////System.out.println(Runtime.getRuntime().freeMemory()+"__freememory");
        ////System.out.println(Runtime.getRuntime().maxMemory()+"__maxmemory");
        ////System.out.println(Runtime.getRuntime().totalMemory() + "__totalmemory");
        //System.gc();
    }

    public void unbindDrawables(View view) {
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
    protected void onResume() {
        super.onResume();
        mTracker.set("UserName", GetSharedValues.getUsername(getApplicationContext()));mTracker.set("ZapUsername", GetSharedValues.getZapname(getApplicationContext()));
        mTracker.setScreenName(GetSharedValues.getZapname(getApplicationContext()) + "~" + "MyAccountpage");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
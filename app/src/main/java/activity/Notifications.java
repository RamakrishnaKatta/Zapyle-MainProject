package activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.exceptions.CleverTapMetaDataNotFoundException;
import com.clevertap.android.sdk.exceptions.CleverTapPermissionsNotSatisfied;
import com.zapyle.zapyle.Alerts;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import network.ApiCommunication;
import network.ApiService;
import notifications.NotificationAdaptor;
import notifications.NotificationData;
import utils.CommonFinish;
import utils.CustomMessage;
import utils.ExternalFunctions;
import utils.FontUtils;
import utils.GetSharedValues;

//import com.//Appsee.//Appsee;
//import com.uxcam.UXCam;


public class Notifications extends ActionBarActivity implements ApiCommunication {
    ListView NotifList;
    RelativeLayout EmptyLayout;
    ArrayList<NotificationData> myNotifiersList = new ArrayList<NotificationData>();
    ProgressBar progressBar;


    public String SCREEN_NAME = "NOTIFICATIONS";
    String callingActivity = "discover";
    String stime = "";
    String endtime = "";
    CleverTapAPI cleverTap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        //Appsee.start("4fb55809ab45411c909521bda720f8e6");
        //UXCam.startWithKey("1dfb25141864376");         overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        //////Thread.currentThread().setDefaultUncaughtExceptionHandler(new SplashScreen.MyUncaughtExceptionHandler());
        LocalBroadcastManager.getInstance(this).registerReceiver(CommonFinish.broadcastReceiver, new IntentFilter("Notifications"));
//        if(!MyApplicationClass.cContextArray.contains(getApplicationContext())) {
        ExternalFunctions.cContextArray[23] = Notifications.this;
        stime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        try {
            cleverTap = CleverTapAPI.getInstance(getApplicationContext());
        } catch (CleverTapMetaDataNotFoundException e) {
            // thrown if you haven't specified your CleverTap Account ID or Token in your AndroidManifest.xml
        } catch (CleverTapPermissionsNotSatisfied e) {
            // thrown if you haven’t requested the required permissions in your AndroidManifest.xml
        }
//        }
        progressBar = (ProgressBar) findViewById(R.id.notif_progressBar);

        try {
            callingActivity = "activity." + getIntent().getStringExtra("activity");
        } catch (Exception e) {
            callingActivity = "activity." + getIntent().getStringExtra("activity");
        }


        View view = findViewById(R.id.notiflayout);


        if (ExternalFunctions.disapprovemsg.length() > 1) {

            new AlertDialog.Builder(this)
                    .setTitle("ZAPYLE")
                    .setIcon(R.drawable.icon)
                    .setMessage(ExternalFunctions.disapprovemsg)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ExternalFunctions.disapprovemsg = "1";
                            dialog.dismiss();
                            ActivityCompat.finishAffinity(Notifications.this);
                            finish();
                        }
                    })
                    .show();
        }

        FontUtils.setCustomFont(view, getAssets());
        if (GetSharedValues.GetSessionId(Notifications.this).length() > 0) {
            GetNotifications();
        } else {
            Alerts.loginAlert(Notifications.this);
        }

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        View customView = getLayoutInflater().inflate(R.layout.notif_actionbar, null);
        getSupportActionBar().setCustomView(customView);
        Toolbar parent = (Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0, 0);

        EmptyLayout = (RelativeLayout) findViewById(R.id.EmptyLayout);
        NotifList = (ListView) findViewById(R.id.NotifiersListView);
//        NotifList.setDivider(null);

        TextView imgUpload = (TextView) findViewById(R.id.imgUpload);
        imgUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent hintent = new Intent(getApplicationContext(), upload.class);
                ExternalFunctions.uploadbackcheck = 1;
                startActivity(hintent);
                finish();
            }
        });


        ImageView lupload = (ImageView) findViewById(R.id.ngotoupload);
        ImageView nhome = (ImageView) findViewById(R.id.nhome);

        nhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

    }

    @Override
    public void onBackPressed() {

        Intent dintent = null;
        try {
            dintent = new Intent(Notifications.this, Class.forName(callingActivity));
            dintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            dintent.putExtra("activity", "SplashScreen");
            startActivity(dintent);
            finish();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            dintent = new Intent(Notifications.this, discover.class);
            dintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            dintent.putExtra("activity", "SplashScreen");
            startActivity(dintent);
            finish();
        }


    }

//    server requests
//    ----------------------------------------------------------------

    public void GetNotifications() {
        progressBar.setVisibility(View.VISIBLE);
        ApiService.getInstance(Notifications.this, 1).getData(Notifications.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/notification/getmynotifs/", "getnotifications");

    }


//    Server responses

    @Override
    public void onResponseCallback(JSONObject response, String flag) {
        if (flag.equals("getnotifications")) {
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                //////System.out.println(resp);
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        progressBar.setVisibility(View.GONE);
                        JSONArray data = resp.getJSONArray("data");
                        if (data.length() > 0) {
                            EmptyLayout.setVisibility(View.GONE);
                            NotifList.setVisibility(View.VISIBLE);
                            for (int i = 0; i < data.length(); i++) {
                                NotificationData notificationData = new NotificationData();
                                notificationData.setAction(data.getJSONObject(i).getString("action"));
                                if (data.getJSONObject(i).getString("action").contains("love") || data.getJSONObject(i).getString("action").contains("comment")) {
                                    notificationData.setNotif_time(data.getJSONObject(i).getString("notif_time"));
                                    notificationData.setMessage(data.getJSONObject(i).getString("message"));
                                    notificationData.setProductid(data.getJSONObject(i).getJSONObject("product").getInt("id"));
                                    notificationData.setProductname(data.getJSONObject(i).getJSONObject("product").getString("name"));
                                    notificationData.setProductImage(data.getJSONObject(i).getJSONObject("product").getString("img_url"));
                                    notificationData.setUsername(data.getJSONObject(i).getJSONObject("notified_by").getString("name"));
                                    notificationData.setProfile_pic(data.getJSONObject(i).getJSONObject("notified_by").getString("profile_img_url"));
                                    notificationData.setUser_type(data.getJSONObject(i).getJSONObject("notified_by").getString("user_type"));
                                    notificationData.setUser_id(String.valueOf(data.getJSONObject(i).getJSONObject("notified_by").getInt("id")));
                                } else if (data.getJSONObject(i).getString("action").contains("admire")) {
                                    notificationData.setNotif_time(data.getJSONObject(i).getString("notif_time"));
                                    notificationData.setMessage(data.getJSONObject(i).getString("message"));
                                    notificationData.setProductid(0);
                                    notificationData.setProductname("");
                                    notificationData.setProductImage("");
                                    notificationData.setUsername(data.getJSONObject(i).getJSONObject("notified_by").getString("name"));
                                    notificationData.setProfile_pic(data.getJSONObject(i).getJSONObject("notified_by").getString("profile_img_url"));
                                    notificationData.setUser_type(data.getJSONObject(i).getJSONObject("notified_by").getString("user_type"));
                                    notificationData.setUser_id(String.valueOf(data.getJSONObject(i).getJSONObject("notified_by").getInt("id")));
                                } else if (data.getJSONObject(i).getString("action").contains("approve")) {
                                    notificationData.setNotif_time(data.getJSONObject(i).getString("notif_time"));
                                    notificationData.setMessage(data.getJSONObject(i).getString("message"));
                                    notificationData.setProductid(data.getJSONObject(i).getJSONObject("product").getInt("id"));
                                    notificationData.setProductname(data.getJSONObject(i).getJSONObject("product").getString("name"));
                                    notificationData.setProductImage(data.getJSONObject(i).getJSONObject("product").getString("img_url"));
                                    notificationData.setUsername("Zapyle");
                                    notificationData.setProfile_pic("");
                                    notificationData.setUser_type("");
                                    notificationData.setUser_id("");
                                }

                                myNotifiersList.add(notificationData);

                            }
                            NotifList.setAdapter(new NotificationAdaptor(Notifications.this, myNotifiersList));

                        } else {
                            EmptyLayout.setVisibility(View.VISIBLE);
                            NotifList.setVisibility(View.GONE);

//                            Intent mydialog = new Intent(Notifications.this, AlertActivity.class);
//                            //int imgid=R.drawable.notificationicon;
//                            String strmessage="YIKES! YOU HAVE NO NOTIFICATIONS.";
//                           // mydialog.putExtra("imgid", imgid);
//                            mydialog.putExtra("Message",strmessage);
//                            mydialog.putExtra("Buttontext","UPLOAD");
//                            mydialog.putExtra("tip","UPLOAD PRODUCTS IN YOUR CLOSET");
//                            mydialog.putExtra("activity","upload");
//                            mydialog.putExtra("header","MY NOTIFICATIONS");
//                            mydialog.putExtra("calling", "FeedPage");
//                            startActivity(mydialog);
//                            finish();
//                            //  EmptyLayout.setVisibility(View.VISIBLE);
//                            NotifList.setVisibility(View.GONE);
                        }
                    } else {
                        //////System.out.println("1");
                        progressBar.setVisibility(View.GONE);
                        CustomMessage.getInstance().CustomMessage(Notifications.this, "Oops. Something went wrong!");
                    }
                } catch (JSONException e) {
                    //////System.out.println("2"+"__"+e);
                    progressBar.setVisibility(View.GONE);
                    CustomMessage.getInstance().CustomMessage(Notifications.this, "Oops. Something went wrong!");

                }

            } else {
                //////System.out.println("3");
                progressBar.setVisibility(View.GONE);
                CustomMessage.getInstance().CustomMessage(Notifications.this, "Oops. Something went wrong!");
            }
        }
    }

    @Override
    public void onErrorCallback(VolleyError error, String flag) {
        //////System.out.println("4___"+error.getMessage());
        progressBar.setVisibility(View.GONE);
        CustomMessage.getInstance().CustomMessage(Notifications.this, "Oops. Something went wrong!");
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
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
    protected void onRestart() {
        super.onRestart();
        if (!GetSharedValues.GetgcmId(Notifications.this).equals("")) {
            ApiService.getInstance(Notifications.this, 1).getData(Notifications.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/?gcm_reg_id=" + GetSharedValues.GetgcmId(Notifications.this), "session");
        } else {
            ApiService.getInstance(Notifications.this, 1).getData(Notifications.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/", "session");
        }
    }
}

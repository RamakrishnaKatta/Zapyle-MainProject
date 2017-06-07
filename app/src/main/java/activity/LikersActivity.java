package activity;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.exceptions.CleverTapMetaDataNotFoundException;
import com.clevertap.android.sdk.exceptions.CleverTapPermissionsNotSatisfied;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import adapters.LikeAdaptor;
import application.MyApplicationClass;
import models.LikeData;
import network.ApiCommunication;
import network.ApiService;
import utils.CommonFinish;
import utils.CustomMessage;
import utils.ExternalFunctions;
import utils.FontUtils;
import utils.GetSharedValues;

//import com.//Appsee.//Appsee;
//import com.uxcam.UXCam;


public class LikersActivity extends ActionBarActivity implements ApiCommunication {

    //    changed
    String SCREEN_NAME = "LIKE_PAGE";
    ArrayList<LikeData> myLikersList = new ArrayList<LikeData>();
    public String heading;
    public static String albumId;
    LikeAdaptor likeadapter;
    ProgressBar progressBar;

    LinearLayout EmptyAddressHolder;
    Tracker mTracker;
    RecyclerView recyclerView;
    TextView p_title;


    Context context = LikersActivity.this;

    String stime = "";
    String endtime = "";
    CleverTapAPI cleverTap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likers);
        MyApplicationClass application = (MyApplicationClass) getApplication();
        mTracker = application.getDefaultTracker();
        //UXCam.startWithKey("1dfb25141864376");
        stime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        try {
            cleverTap = CleverTapAPI.getInstance(getApplicationContext());
        } catch (CleverTapMetaDataNotFoundException e) {
            // thrown if you haven't specified your CleverTap Account ID or Token in your AndroidManifest.xml
        } catch (CleverTapPermissionsNotSatisfied e) {
            // thrown if you haven’t requested the required permissions in your AndroidManifest.xml
        }
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        LocalBroadcastManager.getInstance(this).registerReceiver(CommonFinish.broadcastReceiver, new IntentFilter("LikersActivity"));
        ExternalFunctions.cContextArray[24] = LikersActivity.this;

        View view = findViewById(R.id.likersTypeface);
        FontUtils.setCustomFont(view, getAssets());

        heading = this.getIntent().getStringExtra("album_user");
        albumId = this.getIntent().getStringExtra("album_id");

        progressBar = (ProgressBar) findViewById(R.id.like_progressBar);
        EmptyAddressHolder = (LinearLayout) findViewById(R.id.EmptyAddressHolder);

        Getlikers();


        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        View customView = getLayoutInflater().inflate(R.layout.likers_actionbar, null);
        getSupportActionBar().setCustomView(customView);
        Toolbar parent = (Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0, 0);
        p_title = (TextView) findViewById(R.id.li_title_text);

        ImageView lhome = (ImageView) findViewById(R.id.lhome);

        lhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });


        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    //    Server requests
//    -----------------------------------------------------------------

    private void Getlikers() {
        progressBar.setVisibility(View.VISIBLE);
        ApiService.getInstance(context, 1).getData((ApiCommunication) context, true, "LIKERPAGE", EnvConstants.URL_FEED + "/get_likes/?product_id=" + albumId, "getLikes");

    }


//    Server responses
//    -----------------------------------------------------------------


    @Override
    public void onResponseCallback(JSONObject response, String flag) {
        if (flag.equals("getLikes")) {
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            System.out.println("LIKERSDATA : " + resp);
            if (resp != null) {
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        progressBar.setVisibility(View.GONE);

                        JSONArray data = resp.getJSONArray("data");
                        if (data.length() > 0) {
                            recyclerView.setVisibility(View.VISIBLE);
                            EmptyAddressHolder.setVisibility(View.GONE);
                            for (int i = 0; i < data.length(); i++) {
                                LikeData likersdata = new LikeData();
                                likersdata.setLikersname(data.getJSONObject(i).getJSONObject("loved_by").getString("zap_username"));
                                likersdata.setprofilePic(data.getJSONObject(i).getJSONObject("loved_by").getString("profile_pic"));
                                likersdata.setLikeid(data.getJSONObject(i).getInt("id"));
                                likersdata.setUserid(data.getJSONObject(i).getJSONObject("loved_by").getInt("id"));
                                myLikersList.add(likersdata);
                            }
                            if (myLikersList.size() > 1) {
                                p_title.setText(String.valueOf(myLikersList.size()) + " Loves");
                            } else if (myLikersList.size() == 1) {
                                p_title.setText(String.valueOf(myLikersList.size()) + " Love");
                            }

                            likeadapter = new LikeAdaptor(myLikersList, this);
                            recyclerView.setLayoutManager(new LinearLayoutManager(this));
                            recyclerView.setAdapter(likeadapter);
                        } else {
                            recyclerView.setVisibility(View.GONE);
                            EmptyAddressHolder.setVisibility(View.VISIBLE);
                        }

                    } else {
                        //////System.out.println("inside status errorssss");
                        progressBar.setVisibility(View.GONE);
                        CustomMessage.getInstance().CustomMessage(context, "Oops. Something went wrong!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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
        if (!GetSharedValues.GetgcmId(LikersActivity.this).equals("")) {
            ApiService.getInstance(LikersActivity.this, 1).getData(LikersActivity.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/?gcm_reg_id=" + GetSharedValues.GetgcmId(LikersActivity.this), "session");
        } else {
            ApiService.getInstance(LikersActivity.this, 1).getData(LikersActivity.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/", "session");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.set("UserName", GetSharedValues.getUsername(getApplicationContext()));
        mTracker.set("ZapUsername", GetSharedValues.getZapname(getApplicationContext()));
        mTracker.setScreenName("Likespage");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}

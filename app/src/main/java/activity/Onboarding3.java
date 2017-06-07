package activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import adapters.BrandAdapter;
import adapters.BrandAdapterSkip;
import application.MyApplicationClass;
import models.BrandListmodel;
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
 * Created by haseeb on 12/1/16.
 */
public class Onboarding3 extends ActionBarActivity implements ApiCommunication {
    private int CLICK_COUNT = 0;
    String SCREEN_NAME = "ONBOARDING3";
    TextView Skip, Next, tvhead, tvhead1;

    ListView lvbrand;
    Context context;
    int screen_height, bar_height;
    int intselected, intunselected, intshowsel;
    public static ArrayList<JSONObject> selected = new ArrayList<JSONObject>();
    ArrayList<JSONObject> unselected = new ArrayList<JSONObject>();
    ArrayList<BrandListmodel> dataToAdapter = new ArrayList<BrandListmodel>();
    public static ArrayList<String> dataToserver = new ArrayList<String>();
    ProgressBar progressBar, secondaryProgress;
    int start, end;
    BrandAdapter brandAdapter;
    int DummyListnumber = 25;
    Boolean isLoading = false;
    public static Boolean seemoreStatus = false;
    boolean booltype;
    ProgressDialog mProgress;
    RelativeLayout rl;
    int intBrandAdded = 0, intBrandremoved = 0, intdefaultBrandAdded;
    boolean blunselected;
    public static String activityname;
    Tracker mTracker;
    String stime = "";
    String endtime = "";
    CleverTapAPI cleverTap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboarding3);
        MyApplicationClass application = (MyApplicationClass) getApplication();
        mTracker = application.getDefaultTracker();
        stime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        try {
            cleverTap = CleverTapAPI.getInstance(getApplicationContext());
        } catch (CleverTapMetaDataNotFoundException e) {
            // thrown if you haven't specified your CleverTap Account ID or Token in your AndroidManifest.xml
        } catch (CleverTapPermissionsNotSatisfied e) {
            // thrown if you haven’t requested the required permissions in your AndroidManifest.xml
        }
        //Appsee.start("4fb55809ab45411c909521bda720f8e6");
        //UXCam.startWithKey("1dfb25141864376");         overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        //////Thread.currentThread().setDefaultUncaughtExceptionHandler(new SplashScreen.MyUncaughtExceptionHandler());
        mProgress = new ProgressDialog(Onboarding3.this);
        mProgress.setCancelable(false);
        rl = (RelativeLayout) findViewById(R.id.rl);
        rl.setVisibility(View.INVISIBLE);
        mProgress.setMessage("Loading ...");
        mProgress.show();
        LocalBroadcastManager.getInstance(this).registerReceiver(CommonFinish.broadcastReceiver, new IntentFilter("Onboarding3"));
//        if(!MyApplicationClass.cContextArray.contains(getApplicationContext())) {
        ExternalFunctions.cContextArray[5] = Onboarding3.this;
//        }

//        JSONObject superprop = new JSONObject();
//        try {
//            superprop.put("Event Name", "Brand selection changed");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        mixpanel.registerSuperProperties(superprop);


        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        View customView = getLayoutInflater().inflate(R.layout.onboarding3bar, null);
        getSupportActionBar().setCustomView(customView);
        Toolbar parent = (Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0, 0);
        selected.clear();
        dataToserver.clear();

        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            bar_height = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        tvhead = (TextView) findViewById(R.id.tvhead2);
        tvhead1 = (TextView) findViewById(R.id.tvhead1);
        progressBar = (ProgressBar) findViewById(R.id.ob3progressBar);
        secondaryProgress = (ProgressBar) findViewById(R.id.ob3secondaryprogressBar);
        View view = findViewById(R.id.rl);
        FontUtils.setCustomFont(view, getAssets());

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screen_height = displaymetrics.heightPixels;

        int mid = screen_height / bar_height;
        Double mid1 = mid * 0.6;
        intshowsel = mid1.intValue();

        //////System.out.println("bar__:" + bar_height + "__" + "screen__:" + screen_height + "__" + intshowsel);

        try {
            Bundle bundle = this.getIntent().getExtras();
            booltype = bundle.getBoolean("booltype");
            activityname = bundle.getString("activity");
        } catch (Exception e) {
            activityname = "SplashScreen";
            booltype = true;
        }
        Getbrands();

        Skip = (TextView) findViewById(R.id.skip2);
        if (booltype == false) {
            Skip.setVisibility(View.INVISIBLE);
        } else {

        }
        Skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl.setVisibility(View.INVISIBLE);
                dataToserver.clear();
                BrandsToServer();
            }
        });
        Next = (TextView) findViewById(R.id.next2);
        Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BrandsToServer();
            }
        });
        if (booltype == false) {

            Next.setText("DONE");
        }


        lvbrand = (ListView) findViewById(R.id.lvBrand);
        context = this;
        Resources res = this.getResources();


        lvbrand.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //////System.out.println("inside onscroll changed");
            }

            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {


                int mid = firstVisibleItem + visibleItemCount;
                if (totalItemCount < DummyListnumber) {
                    totalItemCount = DummyListnumber;
                }

                //////System.out.println(mid + "____" + dataToAdapter.size());
                if (totalItemCount == mid) {
                    //////System.out.println("you reached last of the list");
                    end = start + 25;
                    if (!isLoading) {
                        GetMorebrands(start, end);
                        isLoading = true;
                    }
                }
            }

        });

    }


//    Data manipulation functions
//    --------------------------------------------------------------

    public void dataTomodel() {


        if (Onboarding2.isskip || blunselected) {
            if (selected.size() < intshowsel) {
                intshowsel = selected.size();
            }

            for (int i = 0; i < intshowsel; i++) {
                BrandListmodel brandListmodel = new BrandListmodel();
                try {
                    brandListmodel.setBrand_id(selected.get(i).getInt("id"));
                    brandListmodel.setBrandname(selected.get(i).getString("brand"));
                    brandListmodel.setIs_selected(false);
                    dataToAdapter.add(brandListmodel);
//                //////System.out.println("selected__" + selected.get(i).getString("brand"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            for (int i = 0; i < unselected.size(); i++) {
                BrandListmodel brandListmodel = new BrandListmodel();
                try {
                    brandListmodel.setBrand_id(unselected.get(i).getInt("id"));
                    brandListmodel.setBrandname(unselected.get(i).getString("brand"));
                    brandListmodel.setIs_selected(false);
                    dataToAdapter.add(brandListmodel);
//                //////System.out.println("unselected__" + unselected.get(i).getString("brand"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            BrandAdapterSkip brandAdapter = new BrandAdapterSkip(context, dataToAdapter, intselected, intunselected, intshowsel);
            lvbrand.setAdapter(brandAdapter);
            mProgress.dismiss();
            rl.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);


        } else if (selected.size() < intshowsel) {

            intshowsel = selected.size();


            for (int i = 0; i < intshowsel; i++) {
                BrandListmodel brandListmodel = new BrandListmodel();
                try {
                    brandListmodel.setBrand_id(selected.get(i).getInt("id"));
                    brandListmodel.setBrandname(selected.get(i).getString("brand"));
                    brandListmodel.setIs_selected(true);
                    dataToAdapter.add(brandListmodel);
//                //////System.out.println("selected__" + selected.get(i).getString("brand"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            for (int i = 0; i < unselected.size(); i++) {
                BrandListmodel brandListmodel = new BrandListmodel();
                try {
                    brandListmodel.setBrand_id(unselected.get(i).getInt("id"));
                    brandListmodel.setBrandname(unselected.get(i).getString("brand"));
                    brandListmodel.setIs_selected(false);
                    dataToAdapter.add(brandListmodel);
//                //////System.out.println("unselected__" + unselected.get(i).getString("brand"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            BrandAdapterSkip brandAdapter = new BrandAdapterSkip(context, dataToAdapter, intselected, intunselected, intshowsel);
            lvbrand.setAdapter(brandAdapter);
            mProgress.dismiss();
            rl.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        } else {
            if (selected.size() < intshowsel) {
                intshowsel = selected.size();
            }

            for (int i = 0; i < intshowsel; i++) {
                BrandListmodel brandListmodel = new BrandListmodel();
                try {
                    brandListmodel.setBrand_id(selected.get(i).getInt("id"));
                    brandListmodel.setBrandname(selected.get(i).getString("brand"));
                    brandListmodel.setIs_selected(true);
                    dataToAdapter.add(brandListmodel);
//                //////System.out.println("selected__" + selected.get(i).getString("brand"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            for (int i = 0; i < unselected.size(); i++) {
                BrandListmodel brandListmodel = new BrandListmodel();
                try {
                    brandListmodel.setBrand_id(unselected.get(i).getInt("id"));
                    brandListmodel.setBrandname(unselected.get(i).getString("brand"));
                    brandListmodel.setIs_selected(false);
                    dataToAdapter.add(brandListmodel);
//                //////System.out.println("unselected__" + unselected.get(i).getString("brand"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
            if (!seemoreStatus) {
                BrandListmodel brandListmodel1 = new BrandListmodel();
                brandListmodel1.setBrand_id(0);
                brandListmodel1.setBrandname("");
                brandListmodel1.setIs_selected(false);
                dataToAdapter.add(intshowsel, brandListmodel1);
            }
            brandAdapter = new BrandAdapter(context, dataToAdapter, intselected, intunselected, intshowsel);
            lvbrand.setAdapter(brandAdapter);
            mProgress.dismiss();
            rl.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }

//        seemoreStatus = false;

    }


    public void SeeMore(View v) {
        //////System.out.println("clickeddddddd");
        seemoreStatus = true;
        progressBar.setVisibility(View.VISIBLE);
        dataToAdapter.clear();
        intshowsel = selected.size();
        // //////System.out.println ("XXX"+dataToserver.size());
        v.setVisibility(View.GONE);
        Onboarding2.isskip = false;
        dataTomodel();
//        progressBar.setVisibility(View.GONE);
//        brandAdapter.notifyDataSetChanged();
    }


//    Server request
//    --------------------------------------------------------------

    private void Getbrands() {
        progressBar.setVisibility(View.VISIBLE);
        if (booltype == true) {
            ApiService.getInstance(Onboarding3.this, 1).getData(Onboarding3.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/onboarding/3/", "getbrands");
        } else {
            ApiService.getInstance(Onboarding3.this, 1).getData(Onboarding3.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/mybrands/", "getbrands");
        }

    }

    @Override
    public void onBackPressed() {
        if (activityname.equals("SplashScreen")) {
            if (CLICK_COUNT < 1) {
                CustomMessage.getInstance().CustomMessage(this, "Press again to close the app");
                CLICK_COUNT = CLICK_COUNT + 1;
            } else {

                CLICK_COUNT = 0;
                android.os.Process.killProcess(android.os.Process.myPid());
                super.onDestroy();

            }


        } else {
            if (booltype) {
                Intent on2 = new Intent(Onboarding3.this, Onboarding2.class);
                on2.putExtra("booltype", true);
                on2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                on2.putExtra("activity", "Onboarding2");
                startActivity(on2);
                finish();
            } else {
                Intent on2 = new Intent(Onboarding3.this, Mypreference.class);
                on2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(on2);
                finish();
            }

        }
    }


    private void GetMorebrands(int start, int end) {
        //////System.out.println("inside get more brands");
        secondaryProgress.setVisibility(View.VISIBLE);
        ApiService.getInstance(Onboarding3.this, 1).getData(Onboarding3.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/onboarding/3/?start=" + start + "&end=" + end, "getmorebrands");
    }

    private void BrandsToServer() {
        Next.setEnabled(false);
        Skip.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        JSONArray data = new JSONArray(dataToserver);

        //////System.out.println("data for sending__" + data);
        final JSONObject onboarding2Object = new JSONObject();
        try {
            onboarding2Object.put("brands_selected", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        intBrandAdded = onboarding2Object.length();
        if (intBrandAdded > intdefaultBrandAdded) {
            intBrandremoved = intBrandAdded - intdefaultBrandAdded;
        } else {
            intBrandremoved = intdefaultBrandAdded - intBrandAdded;
        }

        //////System.out.println(EnvConstants.APP_BASE_URL + "/onboarding/3/" + onboarding2Object);
        ApiService.getInstance(Onboarding3.this, 1).postData(Onboarding3.this, EnvConstants.APP_BASE_URL + "/onboarding/3/", onboarding2Object, SCREEN_NAME, "datatoserver");

    }

    //    Server Response
//    --------------------------------------------------------------
    @Override
    public void onResponseCallback(JSONObject response, String flag) {
        //////System.out.println("response___" + response);
        if (flag.equals("datatoserver")) {
            //////System.out.println("response_____" + response);
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        Next.setEnabled(true);
                        Skip.setEnabled(true);
                        progressBar.setVisibility(View.GONE);


                        if (booltype == true) {

                            Intent onboardin4 = new Intent(Onboarding3.this, Onboarding4.class);
                            onboardin4.putExtra("booltype", true);
                            onboardin4.putExtra("activity", "Onboarding3");
                            startActivity(onboardin4);
                            finish();
                        } else {

                            Intent mypref = new Intent(Onboarding3.this, Mypreference.class);
                            startActivity(mypref);
                            finish();
                        }
                    } else {
                        Next.setEnabled(true);
                        Skip.setEnabled(true);
                        progressBar.setVisibility(View.GONE);
                        Intent mydialog = new Intent(Onboarding3.this, AlertActivity.class);
                        // int imgid = R.drawable.alertoop;
                        String strmessage = "OOPS!";
                        // mydialog.putExtra("imgid", imgid);
                        mydialog.putExtra("Message", strmessage);
                        mydialog.putExtra("Buttontext", " RETRY ");
                        mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                        if (booltype == true) {
                            mydialog.putExtra("activity", "Onboarding3");
                            mydialog.putExtra("calling", "Onboarding3");
                        } else {
                            mydialog.putExtra("activity", "Mypreference");
                            mydialog.putExtra("calling", "Mypreference");
                        }
                        startActivity(mydialog);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Skip.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                    Intent mydialog = new Intent(Onboarding3.this, AlertActivity.class);
                    // int imgid = R.drawable.alertoop;
                    String strmessage = "OOPS!";
                    // mydialog.putExtra("imgid", imgid);
                    mydialog.putExtra("Message", strmessage);
                    mydialog.putExtra("Buttontext", " RETRY ");
                    mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                    if (booltype == true) {
                        mydialog.putExtra("activity", "Onboarding3");
                        mydialog.putExtra("calling", "Onboarding3");
                    } else {
                        mydialog.putExtra("activity", "Mypreference");
                        mydialog.putExtra("calling", "Mypreference");
                    }

                    startActivity(mydialog);
                    finish();
                }
            }
        } else if (flag.equals("getbrands")) {
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {

                try {
                    JSONObject test = new JSONObject();

                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        JSONObject data = resp.getJSONObject("data");
                        JSONArray array_selected = data.getJSONArray("selected_brands");
                        JSONArray array_unselected = data.getJSONArray("unselected_brands");
                        intselected = array_selected.length();
                        intunselected = array_unselected.length();
                        tvhead.setText("We've found " + String.valueOf(intselected) + " brands to get your Zapyle Feed started.");
                        //////System.out.println("arraylength__"+array_selected.length());
                        if (!booltype) {
                            tvhead.setText("Follow your favourite Brands and we'll help you own them.");
                            tvhead1.setVisibility(View.GONE);

                        }

                        if (array_selected.length() == 0 || Onboarding2.isskip) {
                            Onboarding2.isskip = true;
                            tvhead.setText("Help us understand your preferences better, select brands to get your Zapyle feed started.");
                            tvhead1.setText("PERFECT!");

                            blunselected = true;

                        }

                        for (int i = 0; i < array_selected.length(); i++) {
                            selected.add(array_selected.getJSONObject(i));
                            dataToserver.add(String.valueOf(array_selected.getJSONObject(i).getInt("id")));
                            //////System.out.println("inside datatoserver"+array_selected.length()+"ddd"+array_selected.getJSONObject(i).getInt("id"));
                        }
                        intdefaultBrandAdded = array_selected.length();
                        for (int i = 0; i < array_unselected.length(); i++) {
                            unselected.add(array_unselected.getJSONObject(i));
                        }
                        //////System.out.println("size_____________" + dataToAdapter.size());
                        dataTomodel();
                        start = 25;

                    } else {
                        progressBar.setVisibility(View.GONE);
                        Intent mydialog = new Intent(Onboarding3.this, AlertActivity.class);
                        // int imgid = R.drawable.alertoop;
                        String strmessage = "OOPS!";
                        // mydialog.putExtra("imgid", imgid);
                        mydialog.putExtra("Message", strmessage);
                        mydialog.putExtra("Buttontext", " RETRY ");
                        mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                        if (booltype == true) {
                            mydialog.putExtra("activity", "Onboarding3");
                            mydialog.putExtra("calling", "Onboarding3");
                        } else {
                            mydialog.putExtra("activity", "Mypreference");
                            mydialog.putExtra("calling", "Mypreference");
                        }

                        startActivity(mydialog);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Intent mydialog = new Intent(Onboarding3.this, AlertActivity.class);
                    // int imgid = R.drawable.alertoop;
                    String strmessage = "OOPS!";
                    // mydialog.putExtra("imgid", imgid);
                    mydialog.putExtra("Message", strmessage);
                    mydialog.putExtra("Buttontext", " RETRY ");
                    mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                    if (booltype == true) {
                        mydialog.putExtra("activity", "Onboarding3");
                        mydialog.putExtra("calling", "Onboarding3");
                    } else {
                        mydialog.putExtra("activity", "Mypreference");
                        mydialog.putExtra("calling", "Mypreference");
                    }
                    startActivity(mydialog);
                    finish();
                }

            }

        } else if (flag.equals("getmorebrands")) {
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {

                try {
                    JSONObject test = new JSONObject();
                    test.put("brand", "test");
                    test.put("id", 400);
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        JSONObject data = resp.getJSONObject("data");
                        JSONArray array_unselected = data.getJSONArray("unselected_brands");
                        if (array_unselected.length() == 0) {
                            isLoading = true;

                        }
                        for (int i = 0; i < array_unselected.length(); i++) {
                            unselected.add(array_unselected.getJSONObject(i));
                        }

                        for (int i = 0; i < array_unselected.length(); i++) {
                            BrandListmodel brandListmodel = new BrandListmodel();
                            try {
                                brandListmodel.setBrand_id(array_unselected.getJSONObject(i).getInt("id"));
                                brandListmodel.setBrandname(array_unselected.getJSONObject(i).getString("brand"));
                                brandListmodel.setIs_selected(false);
                                dataToAdapter.add(brandListmodel);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        start = start + array_unselected.length();
//                        brandAdapter.notifyDataSetChanged();
                        secondaryProgress.setVisibility(View.GONE);
                        isLoading = false;

                    } else {
                        secondaryProgress.setVisibility(View.GONE);
                        Intent mydialog = new Intent(Onboarding3.this, AlertActivity.class);
                        // int imgid = R.drawable.alertoop;
                        String strmessage = "OOPS!";
                        // mydialog.putExtra("imgid", imgid);
                        mydialog.putExtra("Message", strmessage);
                        mydialog.putExtra("Buttontext", " RETRY ");
                        mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                        if (booltype == true) {
                            mydialog.putExtra("activity", "Onboarding3");
                            mydialog.putExtra("calling", "Onboarding3");
                        } else {
                            mydialog.putExtra("activity", "Mypreference");
                            mydialog.putExtra("calling", "Mypreference");
                        }

                        startActivity(mydialog);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    secondaryProgress.setVisibility(View.GONE);
                    Intent mydialog = new Intent(Onboarding3.this, AlertActivity.class);
                    // int imgid = R.drawable.alertoop;
                    String strmessage = "OOPS!";
                    // mydialog.putExtra("imgid", imgid);
                    mydialog.putExtra("Message", strmessage);
                    mydialog.putExtra("Buttontext", " RETRY ");
                    mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                    if (booltype == true) {
                        mydialog.putExtra("activity", "Onboarding3");
                        mydialog.putExtra("calling", "Onboarding3");
                    } else {
                        mydialog.putExtra("activity", "Mypreference");
                        mydialog.putExtra("calling", "Mypreference");
                    }

                    startActivity(mydialog);
                    finish();
                }

            }
        }

    }

    @Override
    public void onErrorCallback(VolleyError error, String flag) {
        if (mProgress.isShowing()) {
            mProgress.dismiss();
        }
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
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        finish();
        startActivity(getIntent());
    }


    public void onLowMemory() {
        unbindDrawables(findViewById(R.id.onboarding3Layout));

        ExternalFunctions.deleteCache(Onboarding3.this);
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
        View view = findViewById(R.id.onboarding3Layout);
//        unbindDrawables(findViewById(R.id.onboarding3Layout));
        Runtime.getRuntime().gc();
        //////System.out.println(Runtime.getRuntime().freeMemory()+"__freememory");
        //////System.out.println(Runtime.getRuntime().maxMemory()+"__maxmemory");
        //////System.out.println(Runtime.getRuntime().totalMemory() + "__totalmemory");
        //System.gc();

    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!GetSharedValues.GetgcmId(Onboarding3.this).equals("")) {
            ApiService.getInstance(Onboarding3.this, 1).getData(Onboarding3.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/?gcm_reg_id=" + GetSharedValues.GetgcmId(Onboarding3.this), "session");
        } else {
            ApiService.getInstance(Onboarding3.this, 1).getData(Onboarding3.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/", "session");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mTracker.set("UserName", GetSharedValues.getUsername(getApplicationContext()));
        mTracker.set("ZapUsername", GetSharedValues.getZapname(getApplicationContext()));
        mTracker.setScreenName("Onboarding3");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}

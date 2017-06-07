package activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.freshdesk.hotline.Hotline;
import com.freshdesk.hotline.HotlineCallbackStatus;
import com.freshdesk.hotline.HotlineUser;
import com.freshdesk.hotline.UnreadCountCallback;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.zapyle.zapyle.Alerts;
import com.zapyle.zapyle.CheckConnectivity;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


import application.MyApplicationClass;
import fragments.bannerfragement;
import fragments.closetfragement;
import fragments.customfragement;
import fragments.genericfragement;
import fragments.messagefragement;
import fragments.productfragement;
import fragments.userfragement;
import network.ApiCommunication;
import network.ApiService;
import notifications.MyGcmRegistrationService;
import utils.CustomMessage;
import utils.ExternalFunctions;
import utils.FontUtils;
import utils.GetSharedValues;

/**
 * Created by haseeb on 12/5/16.
 */

public class HomePageNew extends ActionBarActivity implements ApiCommunication {
    public String SCREEN_NAME = "HomePageNew";
    JSONArray data=null;
    ScrollView mainScrollView;
    LinearLayout mainLinearView;
    JSONObject screenSize;
    public static int screenWidth = 0;
    public static int screenHieght = 0;
    TextView closet_admire;
    TextView user_collection_admire_button;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private DrawerAdaptor mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    public static ArrayList<String> drawerdata = new ArrayList<String>();
    ProgressBar discover_progressBar;
    ImageView banner_image;
    HorizontalScrollView closet_horizontal_scrollview, user_collection_scrollview, product_collection_scrollview;
    LinearLayout linearLayout, product_linearLayout, user_linearLayout, custom_inearLayout;
    String activityname = "activity.HomePage";
    private int CLICK_COUNT = 0;
   // DatabaseDB db;
    private static final String PAGE = "pageno";
    private static final String BODY = "json";
    Boolean dbStatus = false;
    TextView cartcount;
    private Timer t;
    private int TimeCounter = 0;
    TextView test;
    Tracker mTracker;
    FragmentManager manager;
    boolean bldrawtype=true;
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 4329;
    TextView tvchatcount;
    static final String STATE_SCORE = "data";

//    RecyclerView recyclerView;
//    RecyclerView.LayoutManager mLayoutManager = null;
//    public RecyclerView.Adapter adapter;

    public static ArrayList<JSONObject> dataList = new ArrayList<JSONObject>();
    SharedPreferences sharedpreferences;

    @Override
    protected void onStart() {
        super.onStart();

        MyApplicationClass application = (MyApplicationClass) getApplication();
        mTracker = application.getDefaultTracker();
        mDrawerList.setVisibility(View.INVISIBLE);
        if (mDrawerLayout.isDrawerVisible(Gravity.LEFT)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }
        Runtime runtime = Runtime.getRuntime();



      //  System.out.printf("CARTCOUNT HomePage: start" + String.valueOf(db.getTableRecordsCount("CART")));

        if (ExternalFunctions.cartcount > 0) {
            System.out.printf("CART inside if");
                    cartcount.setVisibility(View.VISIBLE);
            cartcount.setText(String.valueOf(ExternalFunctions.cartcount));
        }
        else {
            SharedPreferences CartSession = getSharedPreferences("CartSession",
                    Context.MODE_PRIVATE);

            if (CartSession.getInt("cartCount", 0) > 0) {
                cartcount.setVisibility(View.VISIBLE);
                cartcount.setText(String.valueOf(CartSession.getInt("cartCount", 0)));
            } else {
                cartcount.setVisibility(View.INVISIBLE);
            }
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepagenew);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        ExternalFunctions.strFilterdata = null;
        ExternalFunctions.strfilter = "";
        ExternalFunctions.FilterStatus = false;

        //        Database function
//        ----------------------------------------------------
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        HotlineUser hlUser=Hotline.getInstance(getApplicationContext()).getUser();
        sharedpreferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        hlUser.setName(GetSharedValues.getZapname(this));
        hlUser.setEmail(GetSharedValues.getUseremail(this));
        hlUser.setExternalId(String.valueOf(GetSharedValues.getuserId(this)));
        hlUser.setPhone("+91", GetSharedValues.getUserephonenumber(this));
        Hotline.getInstance(getApplicationContext()).updateUser(hlUser);
//
        System.out.println("aasasas10");
        if(checkPlayServices(HomePageNew.this)) {
            System.out.println("aasasas11");
            Intent intent = new Intent(this, MyGcmRegistrationService.class);
            startService(intent);
        }
        SharedPreferences FeedSession = getSharedPreferences("FeedSession",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor FeedSessioneditor = FeedSession.edit();
        FeedSessioneditor.putString("FeedBackAction", "");
        FeedSessioneditor.putBoolean("FeedActionStatus", false);
        FeedSessioneditor.apply();
        try {
            activityname = "activity." + getIntent().getStringExtra("activity");
        } catch (Exception e) {
            activityname = "activity.HomePage";
        }
        ExternalFunctions.cContextArray[26] = getBaseContext();
        screenSize = ExternalFunctions.displaymetrics(this);
        screenWidth = screenSize.optInt("width");
        screenHieght = screenSize.optInt("width");
//        Drawer initialisation
//        -----------------------------------------------

        mDrawerList = (ListView) findViewById(R.id.homenavList);
        mDrawerList.getLayoutParams().width = (int) (screenWidth * (0.7));
        mDrawerLayout = (DrawerLayout) findViewById(R.id.home_drawer_layout);
        mDrawerList.setBackgroundColor(Color.BLACK);
        mActivityTitle = getTitle().toString();
        mDrawerList.setDivider(null);
        addDrawerItems();
        setupDrawer();
        if (mDrawerLayout.isDrawerVisible(Gravity.LEFT)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }

//      Custom actionbar
//        ------------------------------------------

        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final View customView = getLayoutInflater().inflate(R.layout.home_actionbar, null);
        getSupportActionBar().setCustomView(customView);
        Toolbar parent = (Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0, 0);

        TextView heading = (TextView) findViewById(R.id.heading);
        heading.setText("HOME");
        RelativeLayout cart = (RelativeLayout) findViewById(R.id.home_cart);



        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cart = new Intent(getBaseContext(), ShopCart.class);
                cart.putExtra("activity", "HomePageNew");
                startActivity(cart);

            }
        });

        cartcount = (TextView) customView.findViewById(R.id.cartcount);
        tvchatcount=(TextView) customView.findViewById(R.id.chatcount);
        tvchatcount.setVisibility(View.GONE);
        //System.out.printf("CARTCOUNT: create" + String.valueOf(db.getTableRecordsCount("CART")) + "___" + ExternalFunctions.cartcount);
        if (ExternalFunctions.cartcount > 0) {
            cartcount.setVisibility(View.VISIBLE);
            cartcount.setText(String.valueOf(ExternalFunctions.cartcount));
        } else {

            SharedPreferences CartSession = getSharedPreferences("CartSession",
                    Context.MODE_PRIVATE);
            if (CartSession.getInt("cartCount", 0) > 0) {
                cartcount.setVisibility(View.VISIBLE);
                cartcount.setText(String.valueOf(CartSession.getInt("cartCount", 0)));
            } else {
                cartcount.setVisibility(View.INVISIBLE);
            }
        }

        ImageView notifier_btn = (ImageView) findViewById(R.id.home_notification);
        notifier_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 Hotline.showConversations(getApplicationContext());
//                if (GetSharedValues.LoginStatus(getBaseContext())) {
//                    Intent notif = new Intent(getBaseContext(), Notifications.class);
//                    notif.putExtra("activity", "HomePageNew");
//                    startActivity(notif);
////                    finish();
//                } else {
//                    Alerts.loginAlert(HomePageNew.this);
//                }
            }
        });

        ImageView hamburg = (ImageView) customView.findViewById(R.id.drawer_home);
        hamburg.setImageResource(R.drawable.menu);
        hamburg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerList.setVisibility(View.VISIBLE);
                if (mDrawerLayout.isDrawerVisible(Gravity.LEFT))
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                else
                    mDrawerLayout.openDrawer(Gravity.LEFT);

            }
        });

        ImageView search = (ImageView) customView.findViewById(R.id.home_search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                 CustomMessage.getInstance().CustomMessage(getBaseContext(), "Search is clicked");
                Intent search = new Intent(getBaseContext(), searchnew.class);
                startActivity(search);
                finish();
            }
        });


        discover_progressBar = (ProgressBar) findViewById(R.id.discover_progressBar);
        discover_progressBar.setVisibility(View.VISIBLE);

//
//
        if (sharedpreferences.getString("homedata", "").toString().length()<=1) {
            System.out.println("Saved instance knull");
            if (CheckConnectivity.isNetworkAvailable(this)) {
                System.out.println("Check::: inside if");
                GetData();
            }
        }else{
            System.out.println("Saved instance kold");
            discover_progressBar.setVisibility(View.GONE);
            try {
                data = new JSONArray(sharedpreferences.getString("homedata", ""));
                LinearLayout ln=(LinearLayout)findViewById(R.id.lnnew) ;
                manager=getSupportFragmentManager();//create an instance of fragment manager
                FragmentTransaction transaction=manager.beginTransaction();//c

                String discoverType = null;
                FrameLayout frameLayout1 = new FrameLayout(this);
                FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                frameLayout1.setLayoutParams(params1);
                int k=50;
                frameLayout1.setId(k);
                ln.addView(frameLayout1);
                Bundle bundle=new Bundle();

                bundle.putString("object",data.getJSONObject(0).toString());
                bannerfragement bfrag=new bannerfragement();
                bfrag.setArguments(bundle);
                transaction.add(frameLayout1.getId(), bfrag, "Frag_Top_tag");
                for (int i=0;i<data.length();i++){
                    //dataList.add(data.getJSONObject(i));
                    try {
                        discoverType = data.getJSONObject(i).getJSONObject("content_data").getString("discover_type");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    System.out.println("check x"+discoverType);

                    if (discoverType.contains("banner")) {
                        System.out.println("check x"+discoverType);

                        FrameLayout frameLayout = new FrameLayout(this);
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                        frameLayout.setLayoutParams(params);
                        frameLayout.setId(i);
                        ln.addView(frameLayout);
                        Bundle bundle1=new Bundle();
                        bundle1.putString("object",data.getJSONObject(i).toString());
                        bannerfragement bfrag1=new bannerfragement();
                        bfrag1.setArguments(bundle1);

                        transaction.add(frameLayout.getId(), bfrag1, "Frag_Top_tag");
                    } else if (discoverType.contains("message")) {
                        FrameLayout frameLayout = new FrameLayout(this);
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                        frameLayout.setLayoutParams(params);
                        frameLayout.setId(i);
                        ln.addView(frameLayout);
                        Bundle bundle1=new Bundle();
                        bundle1.putString("object",data.getJSONObject(i).toString());
                        messagefragement frag=new messagefragement();
                        frag.setArguments(bundle1);

                        transaction.add(frameLayout.getId(), frag, "Frag_Top_tag");

                    } else if (discoverType.contains("closet")) {
                        FrameLayout frameLayout = new FrameLayout(this);
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                        frameLayout.setLayoutParams(params);
                        frameLayout.setId(i);
                        ln.addView(frameLayout);
                        Bundle bundle1=new Bundle();
                        bundle1.putString("object",data.getJSONObject(i).toString());
                        closetfragement frag=new closetfragement();
                        frag.setArguments(bundle1);
                        transaction.add(frameLayout.getId(), frag, "Frag_Top_tag");

                    } else if (discoverType.contains("product_collection")) {
                        FrameLayout frameLayout = new FrameLayout(this);
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                        frameLayout.setLayoutParams(params);
                        frameLayout.setId(i);
                        ln.addView(frameLayout);
                        Bundle bundle1=new Bundle();
                        bundle1.putString("object",data.getJSONObject(i).toString());
                        productfragement frag=new productfragement();
                        frag.setArguments(bundle1);
                        transaction.add(frameLayout.getId(), frag, "Frag_Top_tag");

                    } else if (discoverType.contains("user_collection")) {
                        FrameLayout frameLayout = new FrameLayout(this);
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                        frameLayout.setLayoutParams(params);
                        frameLayout.setId(i);
                        ln.addView(frameLayout);
                        Bundle bundle1=new Bundle();
                        bundle1.putString("object",data.getJSONObject(i).toString());
                        userfragement frag=new userfragement();
                        frag.setArguments(bundle1);
                        transaction.add(frameLayout.getId(), frag, "Frag_Top_tag");
                    } else if (discoverType.contains("custom_collection")) {
                        FrameLayout frameLayout = new FrameLayout(this);
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                        frameLayout.setLayoutParams(params);
                        frameLayout.setId(i);
                        ln.addView(frameLayout);
                        Bundle bundle1=new Bundle();
                        bundle1.putString("object",data.getJSONObject(i).toString());
                        customfragement frag=new customfragement();
                        frag.setArguments(bundle1);
                        transaction.add(frameLayout.getId(), frag, "Frag_Top_tag");
                    } else if (discoverType.contains("generic")) {
                        FrameLayout frameLayout = new FrameLayout(this);
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                        frameLayout.setLayoutParams(params);
                        frameLayout.setId(i);
                        ln.addView(frameLayout);
                        Bundle bundle1=new Bundle();
                        bundle1.putString("object",data.getJSONObject(i).toString());
                        genericfragement frag=new  genericfragement();
                        frag.setArguments(bundle1);
                        transaction.add(frameLayout.getId(), frag, "Frag_Top_tag");
                    }





                }
                transaction.commit();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (ExternalFunctions.cartcount > 0) {
            System.out.printf("CART inside if");
            cartcount.setVisibility(View.VISIBLE);
            cartcount.setText(String.valueOf(ExternalFunctions.cartcount));
        } else {

            SharedPreferences CartSession = getSharedPreferences("CartSession",
                    Context.MODE_PRIVATE);
            if (CartSession.getInt("cartCount",0) > 0) {
                cartcount.setVisibility(View.VISIBLE);
                cartcount.setText(String.valueOf(CartSession.getInt("cartCount", 0)));
            } else {
                cartcount.setVisibility(View.INVISIBLE);
            }
        }



    }

    private void GetOverlay() {
        ApiService.getInstance(getBaseContext(), 1).getData(HomePageNew.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/marketing/overlay/discover", "getoverlay");
    }

//  Getmain data
//    ----------------------------------------------------

    private void GetData() {
        ApiService.getInstance(getBaseContext(), 1).getData(HomePageNew.this, true, SCREEN_NAME, EnvConstants.URL_HOME, "home");
    }
    private boolean checkPlayServices(Activity activityContext) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(activityContext);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(activityContext, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {

                finish();
            }
            return false;
        }
        return true;
    }


    //    Drawer functions
//    -----------------------------------------------------

    private void addDrawerItems() {
        drawerdata.clear();
        drawerdata.add("");
        mAdapter = new DrawerAdaptor(this, drawerdata);

        mDrawerList.setAdapter(mAdapter);
        mDrawerList.setBackgroundColor(Color.BLACK);
    }


    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                getSupportActionBar().setTitle("Navigation!");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu();

                // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }


    //    Onpause
//    ------------------------------------------------------------

    @Override
    protected void onPause() {
        super.onPause();
         // manager.popBackStack();

        //System.gc();
        try {
            mainLinearView.setVisibility(View.INVISIBLE);
        }catch(Exception e){

        }

        mDrawerList.setVisibility(View.INVISIBLE);
        if (mDrawerLayout.isDrawerVisible(Gravity.LEFT)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }

        if (banner_image != null) {
            if (banner_image.getDrawable() != null)
                banner_image.getDrawable().setCallback(null);
        }
        try {
            System.out.println("Destroy inside if closet:" );
            int count0 = linearLayout.getChildCount();
            for (int i = 0; i < count0; i++) {
                ImageView v = (ImageView) linearLayout.getChildAt(i).findViewById(R.id.inside_closet_image);
                //System.out.println("Destroy inside if closet:" + v);
                if (v != null) {
                    if (v.getDrawable() != null)
                        v.getDrawable().setCallback(null);
                }
            }

            int count1 = product_linearLayout.getChildCount();
            for (int i = 0; i < count1; i++) {
                ImageView v1 = (ImageView) product_linearLayout.getChildAt(i).findViewById(R.id.product_collection_image);
                if (v1 != null) {
                    System.out.println("Destroy inside if product:" + v1);
                    if (v1.getDrawable() != null)
                        v1.getDrawable().setCallback(null);
                }
            }

            int count2 = user_linearLayout.getChildCount();
            for (int i = 0; i < count2; i++) {
                ImageView v2 = (ImageView) user_linearLayout.getChildAt(i).findViewById(R.id.user_collection_image);
                if (v2 != null) {
                    System.out.println("Destroy inside if user:" + v2);
                    if (v2.getDrawable() != null)
                        v2.getDrawable().setCallback(null);
                }
            }

            int count3 = custom_inearLayout.getChildCount();
            for (int i = 0; i < count3; i++) {
                ImageView v3 = (ImageView) custom_inearLayout.getChildAt(i).findViewById(R.id.custom_collection_image);
                if (v3 != null) {
                    System.out.println("Destroy inside if custom:" + v3);
                    if (v3.getDrawable() != null)
                        v3.getDrawable().setCallback(null);
                }
            }
            linearLayout.removeAllViews();
            product_linearLayout.removeAllViews();
            user_linearLayout.removeAllViews();
            custom_inearLayout.removeAllViews();

            closet_horizontal_scrollview.removeAllViews();
            product_collection_scrollview.removeAllViews();
            user_collection_scrollview.removeAllViews();

        } catch (Exception e) {

        }


    }


    //    Onpause
//    ------------------------------------------------------------

    @Override
    protected void onStop() {
        super.onStop();
        //System.gc();
        handlelayoutOutofMemmory();
        Runtime.getRuntime().gc();
        mDrawerList.setVisibility(View.INVISIBLE);
        if (mDrawerLayout.isDrawerVisible(Gravity.LEFT)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }


        if (banner_image != null) {
            if (banner_image.getDrawable() != null)
                banner_image.getDrawable().setCallback(null);
                Glide.clear(banner_image);
        }
        try {

            int count0 = linearLayout.getChildCount();
            for (int i = 0; i < count0; i++) {
                ImageView v = (ImageView) linearLayout.getChildAt(i).findViewById(R.id.inside_closet_image);
                //System.out.println("Destroy inside if closet:" + v);
                if (v != null) {
                    if (v.getDrawable() != null)
                        v.getDrawable().setCallback(null);
                        Glide.clear(v);
                }
            }

            int count1 = product_linearLayout.getChildCount();
            for (int i = 0; i < count1; i++) {
                ImageView v1 = (ImageView) product_linearLayout.getChildAt(i).findViewById(R.id.product_collection_image);
                if (v1 != null) {
                    //System.out.println("Destroy inside if product:" + v1);
                    if (v1.getDrawable() != null)
                        v1.getDrawable().setCallback(null);
                    Glide.clear(v1);
                }
            }

            int count2 = user_linearLayout.getChildCount();
            for (int i = 0; i < count2; i++) {
                ImageView v2 = (ImageView) user_linearLayout.getChildAt(i).findViewById(R.id.user_collection_image);
                if (v2 != null) {
                    //System.out.println("Destroy inside if user:" + v2);
                    if (v2.getDrawable() != null)
                        v2.getDrawable().setCallback(null);
                    Glide.clear(v2);
                }
            }

            int count3 = custom_inearLayout.getChildCount();
            for (int i = 0; i < count3; i++) {
                ImageView v3 = (ImageView) custom_inearLayout.getChildAt(i).findViewById(R.id.custom_collection_image);
                if (v3 != null) {
                    //System.out.println("Destroy inside if custom:" + v3);
                    if (v3.getDrawable() != null)
                        v3.getDrawable().setCallback(null);
                    Glide.clear(v3);
                }
            }
            linearLayout.removeAllViews();
            product_linearLayout.removeAllViews();
            user_linearLayout.removeAllViews();
            custom_inearLayout.removeAllViews();

            closet_horizontal_scrollview.removeAllViews();
            product_collection_scrollview.removeAllViews();
            user_collection_scrollview.removeAllViews();
            mainLinearView.setVisibility(View.INVISIBLE);
//            MyApplicationClass.sFirstRun = false;
            ExternalFunctions.strOverlayurl = "";
        } catch (Exception e) {

        }


    }

//    Onbackpressed
//    ---------------------------------------------------------

    @Override
    public void onBackPressed() {

            if (CLICK_COUNT < 1) {

                CustomMessage.getInstance().CustomMessage(this, "Press again to close the app");
                CLICK_COUNT = CLICK_COUNT + 1;
            } else {
                CLICK_COUNT = 0;
                ExternalFunctions.strfilter = "";
                ExternalFunctions.sort = 0;
                ExternalFunctions.blfiteropen = false;
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
                finish();
                System.exit(0);
            }
            ExternalFunctions.strfilter = "";
            ExternalFunctions.sort = 0;
            ExternalFunctions.blfiteropen = false;

    }


//    Ondestroy
//    ----------------------------------------------------------

    public void handlelayoutOutofMemmory() {
        ExternalFunctions.nullViewDrawablesRecursive(mainLinearView);
        mainLinearView = null;
        System.gc();


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handlelayoutOutofMemmory();
        Runtime.getRuntime().gc();
        if (banner_image != null) {
            if (banner_image.getDrawable() != null)
                banner_image.getDrawable().setCallback(null);
                Glide.clear(banner_image);
        }
        try {
            int count0 = linearLayout.getChildCount();
            for (int i = 0; i < count0; i++) {
                ImageView v = (ImageView) linearLayout.getChildAt(i).findViewById(R.id.inside_closet_image);
                System.out.println("Destroy inside if closet:" + v);
                if (v != null) {
                    if (v.getDrawable() != null)
                        v.getDrawable().setCallback(null);
                    Glide.clear(v);
                    ((BitmapDrawable)v.getDrawable()).getBitmap().recycle();
                    System.gc();

                }
            }

            int count1 = product_linearLayout.getChildCount();
            for (int i = 0; i < count1; i++) {
                ImageView v1 = (ImageView) product_linearLayout.getChildAt(i).findViewById(R.id.product_collection_image);
                if (v1 != null) {
                    System.out.println("Destroy inside if product:" + v1);
                    if (v1.getDrawable() != null)
                        v1.getDrawable().setCallback(null);
                    Glide.clear(v1);
                    ((BitmapDrawable)v1.getDrawable()).getBitmap().recycle();
                    System.gc();
                }
            }

            int count2 = user_linearLayout.getChildCount();
            for (int i = 0; i < count2; i++) {
                ImageView v2 = (ImageView) user_linearLayout.getChildAt(i).findViewById(R.id.user_collection_image);
                if (v2 != null) {
                    //System.out.println("Destroy inside if user:" + v2);
                    if (v2.getDrawable() != null)
                        v2.getDrawable().setCallback(null);
                    Glide.clear(v2);
                    ((BitmapDrawable)v2.getDrawable()).getBitmap().recycle();
                    System.gc();

                }
            }

            int count3 = custom_inearLayout.getChildCount();
            for (int i = 0; i < count3; i++) {
                ImageView v3 = (ImageView) custom_inearLayout.getChildAt(i).findViewById(R.id.custom_collection_image);
                if (v3 != null) {
                    //System.out.println("Destroy inside if custom:" + v3);
                    if (v3.getDrawable() != null)
                        v3.getDrawable().setCallback(null);
                    Glide.clear(v3);
                    ((BitmapDrawable)v3.getDrawable()).getBitmap().recycle();
                    System.gc();
                }
            }

            linearLayout.removeAllViews();
            product_linearLayout.removeAllViews();
            user_linearLayout.removeAllViews();
            custom_inearLayout.removeAllViews();


            closet_horizontal_scrollview.removeAllViews();
            product_collection_scrollview.removeAllViews();
            user_collection_scrollview.removeAllViews();

            ExternalFunctions.strOverlayurl = "";

        } catch (Exception e) {

        }

    }


//   DRAWER ADAPTOR
//    -----------------------------------------------------------
//    ----------------------------------------------------------

    public class DrawerAdaptor extends BaseAdapter {
        Boolean clickStatus = false;
        Boolean clickPrelovedStatus = true;
        ArrayList<String> drawerdata = new ArrayList<String>();

        LayoutInflater l_inflater;
        Context context;


        public DrawerAdaptor(Context context, ArrayList<String> drawerdata) {

            this.drawerdata = drawerdata;
            this.context = context;
            l_inflater = LayoutInflater.from(this.context);


        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public int getCount() {
            return drawerdata.size();
        }

        @Override
        public String getItem(int position) {
            return drawerdata.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getView(final int position, View convertView1, ViewGroup parent) {

            final MyViewHolder myViewHolder;
            if (convertView1 == null) {
                convertView1 = l_inflater.inflate(R.layout.simple_list_item_drawer, parent, false);
                myViewHolder = new MyViewHolder(convertView1);
                convertView1.setTag(myViewHolder);
            } else {
                myViewHolder = (MyViewHolder) convertView1.getTag();
            }

            ShowmainList(myViewHolder.mainLayout);
            FontUtils.setCustomFont(myViewHolder.mainLayout, context.getAssets());
            return convertView1;
        }


        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }

        private class MyViewHolder {
            RelativeLayout mainLayout;


            public MyViewHolder(View item) {
                mainLayout = (RelativeLayout) item.findViewById(R.id.drawerListView);

            }
        }

        public void ShowmainList(RelativeLayout mainlayout) {
            final LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams lpline = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            View tvline = new TextView(context);
            lpline.setMargins(20, 10, 20, 25);
            tvline.setLayoutParams(lpline);
            linearLayout.addView(tvline);
            tvline.setVisibility(View.INVISIBLE);
            if (GetSharedValues.LoginStatus(context)) {
                bldrawtype=true;
                mainlayout.removeAllViews();
                final String[] mainArray = {"HOME", "SHOP BRAND NEW", "SHOP PRE LOVED","SELL","DISCOUNTS","AUTHENTICITY","MY PROFILE", "MY ACCOUNT", "ABOUT"};
                for (int i = 0; i < mainArray.length; i++) {
                    LayoutInflater comment_inflater = LayoutInflater.from(context);
                    final View view;
                    view = comment_inflater.inflate(R.layout.drawer_list_item, null, false);
                    TextView tvTitle = (TextView) view.findViewById(R.id.drawerText);
                    final ImageView imgarrow=(ImageView)view.findViewById(R.id.imgarrow);
                    if(i==1 || i==2){
                        tvTitle.setTextColor(Color.parseColor("#FFFFFF"));
                        if(i==2){
                            imgarrow.setVisibility(View.VISIBLE);
                        }
                    }else{
                        tvTitle.setTextColor(Color.parseColor("#B3FFFFFF"));
                        imgarrow.setVisibility(View.GONE);
                    }
                    if(i==8){
                        imgarrow.setVisibility(View.VISIBLE);
                    }
//                    if(i==0) {
//                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                        lp.setMargins(10, 100, 10, 5);
//                        linearLayout.setLayoutParams(lp);
//
//                    }
                    tvTitle.setTypeface(null, Typeface.BOLD);
                    final TextView tvIcon = (TextView) view.findViewById(R.id.drawerImage);
                    final LinearLayout lvSecondary = (LinearLayout) view.findViewById(R.id.secondarylistview);
                    lvSecondary.setVisibility(View.GONE);
                    tvTitle.setText(mainArray[i]);

//                    if (mainArray[i].contains("ABOUT")) {
//
//                    }
//                    if(mainArray[i].equals("SHOP PRE LOVED")) {
//                        lvSecondary.setVisibility(View.VISIBLE);
//                        ShowBuy(lvSecondary);
//                    }
                    final int finalI = i;
                    tvTitle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            switch (mainArray[finalI]) {
                                case "HOME":
                                    if (CheckConnectivity.isNetworkAvailable(context)) {
                                        if (mDrawerLayout.isDrawerVisible(Gravity.LEFT)) {
                                            mDrawerLayout.closeDrawer(Gravity.LEFT);
                                        }
                                       // ClearViews();
                                        //discover_progressBar.setVisibility(View.VISIBLE);
                                        //GetData();

                                    } else {
                                       CustomMessage.getInstance().CustomMessage(context, "Internet is not available!");

                                    }
                                    break;

                                case "SELL":
                                    if (CheckConnectivity.isNetworkAvailable(context)) {
                                        if (GetSharedValues.LoginStatus(context)) {
                                            Intent intent = new Intent(context, Upload1.class);
                                            ExternalFunctions.uploadbackcheck = 1;
                                            intent.putExtra("activity", "HomePageNew");
                                            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                            context.startActivity(intent);
                                            finish();
//                                    finish();

                                        } else {
                                            Alerts.loginAlert(context);
                                        }
                                    } else {

                                       CustomMessage.getInstance().CustomMessage(context, "Internet is not available!");

                                    }

                                    break;

                                case "SHOP BRAND NEW":
                                    if (CheckConnectivity.isNetworkAvailable(context)) {
                                        mTracker.set("Buypage Choice","Designer");
                                        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
                                        ExternalFunctions.ActivityParam = "base";
                                        ExternalFunctions.DiffParam = "designer";
                                        Intent designer = new Intent(getApplicationContext(), BuySecondPage.class);
                                        designer.putExtra("activity", "HomePageNew");
                                        startActivity(designer);
                                        finish();
//                                finish();
                                    } else {
//                                Alerts.InternetAlert(getApplicationContext());
                                        CustomMessage.getInstance().CustomMessage(context, "Internet is not available!");

                                    }

                                    break;


                                case "SHOP PRE LOVED":

                                    if (clickPrelovedStatus) {
                                        clickPrelovedStatus = false;
//                                        tvIcon.setText(R.string.downarrow);
                                        lvSecondary.setVisibility(View.GONE);
                                        imgarrow.setImageResource(R.drawable.down);
                                    } else {
                                        clickPrelovedStatus = true;
//                                        tvIcon.setText(R.string.uparrow);
                                        imgarrow.setImageResource(R.drawable.up);
                                        lvSecondary.setVisibility(View.VISIBLE);
                                        ShowBuy(lvSecondary);
                                        FontUtils.setCustomFont(lvSecondary, context.getAssets());

                                    }
                                    break;

                                case "MY PROFILE":
                                    if (CheckConnectivity.isNetworkAvailable(context)) {
                                        if (GetSharedValues.LoginStatus(context)) {
                                            Intent intent = new Intent(context, ProfilePage.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                            intent.putExtra("user_id", GetSharedValues.getuserId(HomePageNew.this));
                                            intent.putExtra("p_username",GetSharedValues.getUsername(HomePageNew.this));
                                            context.startActivity(intent);
                                            Runtime.getRuntime().gc();
                                            finish();

                                        } else {
                                            Alerts.loginAlert(context);
                                        }
                                    } else {
//                                Alerts.InternetAlert(getApplicationContext());
                                       CustomMessage.getInstance().CustomMessage(context, "Internet is not available!");

                                    }
                                    break;


                                case "MY ACCOUNT":
                                    if (GetSharedValues.LoginStatus(getApplicationContext())) {
                                        Intent intent = new Intent(getApplicationContext(), Myaccountpage.class);
                                        intent.putExtra("activity", "HomePageNew");
                                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                        startActivity(intent);
//                                    finish();

                                    } else {
                                        Alerts.loginAlert(getApplicationContext());
                                    }
                                    break;



                                case "DISCOUNTS":
                                    if (CheckConnectivity.isNetworkAvailable(context)) {
                                        if (GetSharedValues.LoginStatus(context)) {
                                            Intent myorders = new Intent(context, EarnCash.class);
                                            myorders.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                            myorders.putExtra("activity", "HomePageNew");
                                            context.startActivity(myorders);
//                                    finish();
                                        } else {
                                            Alerts.loginAlert(context);
                                        }
                                    } else {
//                                Alerts.InternetAlert(FeedPage.this);
                                       CustomMessage.getInstance().CustomMessage(context, "Internet is not available!");
                                    }
                                    break;

                                case "AUTHENTICITY":


                                    Intent intent2 = new Intent(context, Authenticity.class);
                                    intent2.putExtra("activity", "HomePageNew");
                                    intent2.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                    context.startActivity(intent2);

                                    break;


                                case "ABOUT":

                                    if (clickStatus) {
                                        clickStatus = false;
//                                        tvIcon.setText(R.string.downarrow);
                                        lvSecondary.setVisibility(View.GONE);
                                        imgarrow.setImageResource(R.drawable.down);
                                    } else {
                                        clickStatus = true;
//                                        tvIcon.setText(R.string.uparrow);
                                        lvSecondary.setVisibility(View.VISIBLE);
                                        imgarrow.setImageResource(R.drawable.up);
                                        ShowList(lvSecondary);

                                    }

                                    break;


                            }
                        }
                    });


                    linearLayout.addView(view);
                }
            } else {
                bldrawtype=false;
                mainlayout.removeAllViews();
                final String[] mainArray = {"HOME", "SHOP BRAND NEW", "SHOP PRE LOVED","SELL", "AUTHENTICITY","LOGIN", "ABOUT"};
                for (int i = 0; i < mainArray.length; i++) {
                    LayoutInflater comment_inflater = LayoutInflater.from(context);
                    final View view;
                    view = comment_inflater.inflate(R.layout.drawer_list_item, null, false);
                    TextView tvTitle = (TextView) view.findViewById(R.id.drawerText);
                    final ImageView imgarrow=(ImageView)view.findViewById(R.id.imgarrow);
                    if(i==1 || i==2){
                        tvTitle.setTextColor(Color.parseColor("#FFFFFF"));
                        if(i==2){
                            imgarrow.setVisibility(View.VISIBLE);
                        }
                    }else{
                        tvTitle.setTextColor(Color.parseColor("#B3FFFFFF"));
                        imgarrow.setVisibility(View.GONE);
                    }
                    if(i==6){
                        imgarrow.setVisibility(View.VISIBLE);
                    }
                    tvTitle.setTypeface(null, Typeface.BOLD);
                    final TextView tvIcon = (TextView) view.findViewById(R.id.drawerImage);
                    final LinearLayout lvSecondary = (LinearLayout) view.findViewById(R.id.secondarylistview);
                    lvSecondary.setVisibility(View.GONE);
                    tvTitle.setText(mainArray[i]);
                    if (mainArray[i].contains("ABOUT")) {
                        tvIcon.setText(R.string.downarrow);
                    } else {
                        tvIcon.setText("");
                    }
//                    if(mainArray[i].equals("SHOP PRE LOVED")) {
//                        lvSecondary.setVisibility(View.VISIBLE);
//                        ShowBuy(lvSecondary);
//                    }
                    final int finalI = i;
                    tvTitle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            switch (mainArray[finalI]) {
                                case "HOME":
                                    if (CheckConnectivity.isNetworkAvailable(context)) {
                                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                                        ClearViews();
                                        discover_progressBar.setVisibility(View.VISIBLE);
                                        GetData();

                                    } else {
                                       CustomMessage.getInstance().CustomMessage(context, "Internet is not available!");
                                    }
                                    break;

                                case "SELL":
                                    if (CheckConnectivity.isNetworkAvailable(context)) {
                                        if (GetSharedValues.LoginStatus(context)) {
                                            Intent intent = new Intent(context, Upload1.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                            ExternalFunctions.uploadbackcheck = 1;
                                            context.startActivity(intent);
//                                    finish();

                                        } else {
                                            Alerts.loginAlert(context);
                                        }
                                    } else {
                                       CustomMessage.getInstance().CustomMessage(context, "Internet is not available!");

                                    }

                                    break;

                                case "SHOP BRAND NEW":
                                    if (CheckConnectivity.isNetworkAvailable(context)) {
                                        mTracker.set("Buypage Choice","Designer");
                                        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
                                        ExternalFunctions.ActivityParam = "base";
                                        ExternalFunctions.DiffParam = "designer";
                                        Intent designer = new Intent(getApplicationContext(), BuySecondPage.class);
                                        designer.putExtra("activity", "HomePageNew");
                                        startActivity(designer);
                                        finish();
//                                finish();
                                    } else {
//                                Alerts.InternetAlert(getApplicationContext());
                                        CustomMessage.getInstance().CustomMessage(context, "Internet is not available!");

                                    }

                                    break;


                                case "SHOP PRE LOVED":

                                    if (clickPrelovedStatus) {
                                        clickPrelovedStatus = false;
//                                        tvIcon.setText(R.string.downarrow);
                                        lvSecondary.setVisibility(View.GONE);
                                        imgarrow.setImageResource(R.drawable.down);
                                    } else {
                                        clickPrelovedStatus = true;
//                                        tvIcon.setText(R.string.uparrow);
                                        imgarrow.setImageResource(R.drawable.up);
                                        lvSecondary.setVisibility(View.VISIBLE);
                                        ShowBuy(lvSecondary);
                                        FontUtils.setCustomFont(lvSecondary, context.getAssets());

                                    }
                                    break;

                                case "LOGIN":
                                    if (CheckConnectivity.isNetworkAvailable(context)) {
                                            Intent parallax = new Intent(context, Parallax.class);
                                            parallax.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            parallax.putExtra("booltype", false);
                                            context.startActivity(parallax);
                                            ((Activity) context).finish();
                                    } else {
                                       CustomMessage.getInstance().CustomMessage(context, "Internet is not available!");

                                    }

                                    break;


                                case "AUTHENTICITY":

                                    Intent intent2 = new Intent(context, Authenticity.class);
                                    intent2.putExtra("activity", "HomePageNew");
                                    intent2.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                    context.startActivity(intent2);

                                    break;


                                case "ABOUT":

                                    if (clickStatus) {
                                        clickStatus = false;
//                                        tvIcon.setText(R.string.downarrow);
                                        lvSecondary.setVisibility(View.GONE);
                                        imgarrow.setImageResource(R.drawable.down);
                                    } else {
                                        clickStatus = true;
//                                        tvIcon.setText(R.string.uparrow);
                                        lvSecondary.setVisibility(View.VISIBLE);
                                        imgarrow.setImageResource(R.drawable.up);
                                        ShowList(lvSecondary);

                                    }

                                    break;


                            }
                        }
                    });


                    linearLayout.addView(view);
                }
            }

            if( bldrawtype) {
                View view1;
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view1 = inflater.inflate(R.layout.drawerchat, null);
                view1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Hotline.showConversations(getApplicationContext());
                    }
                });
                linearLayout.addView(view1);
            }
            mainlayout.addView(linearLayout);


        }




        public void ShowList(LinearLayout view) {
            final String[] osArray = {"STORY", "FAQ", "RATE APP", "CALL ZAPYLE"};
            view.removeAllViews();
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(40, 25, 10, 25);
            for (int j = 0; j < osArray.length; j++) {
                LinearLayout.LayoutParams lpline = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,2);
                View tvline = new TextView(context);
                tvline.setBackgroundColor(Color.parseColor("#33FFFFFF"));
                lpline.setMargins(20, 10, 20, 30);
                tvline.setLayoutParams(lpline);
                View tvline1 = new TextView(context);
                tvline1.setBackgroundColor(Color.parseColor("#33FFFFFF"));
                lpline.setMargins(20, 10, 20, 30);
                tvline1.setLayoutParams(lpline);
                if (j==0) {
                    view.addView(tvline);
                }
                TextView tv = new TextView(context);
                tv.setText(osArray[j]);

                tv.setTextColor(Color.parseColor("#B3FFFFFF"));
                tv.setTypeface(null, Typeface.BOLD);
                tv.setLayoutParams(lp);

                final int finalJ = j;
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (osArray[finalJ]) {

                            case "STORY":
                                if (CheckConnectivity.isNetworkAvailable(context)) {
                                    Intent intent1 = new Intent(context, About_us.class);
                                    intent1.putExtra("activity", "HomePageNew");
                                    intent1.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                    context.startActivity(intent1);
//                                finish();
                                } else {
//                                Alerts.InternetAlert(getApplicationContext());
                                   CustomMessage.getInstance().CustomMessage(context, "Internet is not available!");

                                }
                                break;


                            case "FAQ":

                                if (CheckConnectivity.isNetworkAvailable(context)) {
                                    Intent intent5 = new Intent(context, FAQ.class);
                                    intent5.putExtra("activity", "HomePageNew");
                                    intent5.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                    context.startActivity(intent5);
//                                finish();
                                } else {
//                                Alerts.InternetAlert(getApplicationContext());
                                   CustomMessage.getInstance().CustomMessage(context, "Internet is not available!");

                                }
                                break;


                            case "RATE APP":
                                context.startActivity(new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("https://play.google.com/store/apps/details?id=com.zapyle.zapyle")));
                                break;


                            case "CALL ZAPYLE":


                                if (CheckConnectivity.isNetworkAvailable(context)) {
                                    Intent intent = new Intent(Intent.ACTION_CALL);
                                    intent.setData(Uri.parse("tel:" + "08040004541"));
                                    context.startActivity(intent);
//                                finish();
                                } else {
//                                Alerts.InternetAlert(getApplicationContext());
                                   CustomMessage.getInstance().CustomMessage(context, "Internet is not available!");

                                }
                                break;

                        }
                    }
                });
                view.addView(tv);
                    if(j!=3) {
                        view.addView(tvline1);
                    }

            }


        }



        public void ShowBuy(LinearLayout view) {
            final String[] osArray = {"CURATED", "MARKETPLACE"};
            view.removeAllViews();
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(40, 25, 10, 25);
            for (int j = 0; j < osArray.length; j++) {
                TextView tv = new TextView(context);
                tv.setText(osArray[j]);
                tv.setTextColor(Color.WHITE);
                tv.setTextSize(14);
                tv.setTypeface(null, Typeface.BOLD);
                tv.setLayoutParams(lp);

                TextView tvtext = new TextView(context);
                if(j==0) {
                    tvtext.setText("Handpicked by Zapyle");
                }else{
                    tvtext.setText("Buy & Sell Luxury Fashion");
                }
                tvtext.setTextColor(Color.parseColor("#66FFFFFF"));
                tvtext.setTypeface(null, Typeface.NORMAL);
                lp.setMargins(40, 0, 10, 0);
                tvtext.setLayoutParams(lp);

                LinearLayout.LayoutParams lpline = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,2);
                View tvline = new TextView(context);
                tvline.setBackgroundColor(Color.parseColor("#33FFFFFF"));
                lpline.setMargins(20, 10, 20, 30);
                tvline.setLayoutParams(lpline);
                View tvline1 = new TextView(context);
                tvline1.setBackgroundColor(Color.parseColor("#33FFFFFF"));
                lpline.setMargins(20, 10, 20, 30);
                tvline1.setLayoutParams(lpline);
                final int finalJ = j;
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (osArray[finalJ]) {

                            case "CURATED":
                                if (CheckConnectivity.isNetworkAvailable(context)) {
                                    mTracker.set("Buypage Choice","Curated");
                                    mTracker.send(new HitBuilders.ScreenViewBuilder().build());
                                    ExternalFunctions.ActivityParam = "base";
                                    ExternalFunctions.DiffParam = "curated";
                                    Intent vintage = new Intent(getApplicationContext(), BuySecondPage.class);
                                    vintage.putExtra("activity", "HomePageNew");
                                    startActivity(vintage);
                                    finish();
//                                finish();
                                } else {
//                                Alerts.InternetAlert(getApplicationContext());
                                    CustomMessage.getInstance().CustomMessage(context, "Internet is not available!");

                                }
                                break;


                            case "MARKETPLACE":
                                if (CheckConnectivity.isNetworkAvailable(context)) {
                                    mTracker.set("Buypage Choice","Buypage");
                                    mTracker.send(new HitBuilders.ScreenViewBuilder().build());
                                    ExternalFunctions.ActivityParam = "base";
                                    ExternalFunctions.DiffParam = "marketplace";
                                    Intent market = new Intent(getApplicationContext(), BuySecondPage.class);
                                    market.putExtra("activity", "BuyPage");
                                    startActivity(market);
                                    finish();

                                } else {
                                    CustomMessage.getInstance().CustomMessage(context, "Internet is not available!");

                                }
                                break;




                        }
                    }
                });
                tvtext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (osArray[finalJ]) {

                            case "CURATED":
                                if (CheckConnectivity.isNetworkAvailable(context)) {
                                    mTracker.set("Buypage Choice","Curated");
                                    mTracker.send(new HitBuilders.ScreenViewBuilder().build());
                                    ExternalFunctions.ActivityParam = "base";
                                    ExternalFunctions.DiffParam = "curated";
                                    Intent vintage = new Intent(getApplicationContext(), BuySecondPage.class);
                                    vintage.putExtra("activity", "HomePageNew");
                                    startActivity(vintage);
                                    finish();
//                                finish();
                                } else {
//                                Alerts.InternetAlert(getApplicationContext());
                                    CustomMessage.getInstance().CustomMessage(context, "Internet is not available!");

                                }
                                break;


                            case "MARKETPLACE":
                                if (CheckConnectivity.isNetworkAvailable(context)) {
                                    mTracker.set("Buypage Choice","Buypage");
                                    mTracker.send(new HitBuilders.ScreenViewBuilder().build());
                                    ExternalFunctions.ActivityParam = "base";
                                    ExternalFunctions.DiffParam = "marketplace";
                                    Intent market = new Intent(getApplicationContext(), BuySecondPage.class);
                                    market.putExtra("activity", "BuyPage");
                                    startActivity(market);
                                    finish();

                                } else {
                                    CustomMessage.getInstance().CustomMessage(context, "Internet is not available!");

                                }
                                break;




                        }
                    }
                });

                if (j==0) {
                    view.addView(tvline);
                    view.addView(tv);
                    view.addView(tvtext);
                    view.addView(tvline1);

                }else{
                    view.addView(tv);
                    view.addView(tvtext);
                }

            }

        }
    }


//    Response handler
//    ---------------------------------------------------------

    @Override
    public void onResponseCallback(JSONObject response, String flag) {
        System.out.println("HOMEPAGE DATA: "+response);
        if (flag.equals("home")) {
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            try {
                String status = resp.getString("status");
                if (status.contains("success")) {
                    discover_progressBar.setVisibility(View.GONE);
                    data = resp.getJSONArray("data");
                    SharedPreferences.Editor editor = sharedpreferences.edit();

                    editor.putString("homedata", data.toString());
                    editor.commit();
                   // dataList.clear();
                    LinearLayout ln=(LinearLayout)findViewById(R.id.lnnew) ;
                    manager=getSupportFragmentManager();//create an instance of fragment manager
                    FragmentTransaction transaction=manager.beginTransaction();//c

                    String discoverType = null;
                    FrameLayout frameLayout1 = new FrameLayout(this);
                    FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    frameLayout1.setLayoutParams(params1);
                    int k=50;
                    frameLayout1.setId(k);
                    ln.addView(frameLayout1);
                    Bundle bundle=new Bundle();

                    bundle.putString("object",data.getJSONObject(0).toString());
                    bannerfragement bfrag=new bannerfragement();
                    bfrag.setArguments(bundle);
                    transaction.add(frameLayout1.getId(), bfrag, "Frag_Top_tag");
                    for (int i=0;i<data.length();i++){
                        //dataList.add(data.getJSONObject(i));
                        try {
                            discoverType = data.getJSONObject(i).getJSONObject("content_data").getString("discover_type");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println("check x"+discoverType);

                            if (discoverType.contains("banner")) {
                                System.out.println("check x"+discoverType);

                                FrameLayout frameLayout = new FrameLayout(this);
                                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                                frameLayout.setLayoutParams(params);
                                frameLayout.setId(i);
                                ln.addView(frameLayout);
                                Bundle bundle1=new Bundle();
                                bundle1.putString("object",data.getJSONObject(i).toString());
                                bannerfragement bfrag1=new bannerfragement();
                                bfrag1.setArguments(bundle1);

                                transaction.add(frameLayout.getId(), bfrag1, "Frag_Top_tag");
                            } else if (discoverType.contains("message")) {
                                FrameLayout frameLayout = new FrameLayout(this);
                                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                                frameLayout.setLayoutParams(params);
                                frameLayout.setId(i);
                                ln.addView(frameLayout);
                                Bundle bundle1=new Bundle();
                                bundle1.putString("object",data.getJSONObject(i).toString());
                                messagefragement frag=new messagefragement();
                                frag.setArguments(bundle1);

                                transaction.add(frameLayout.getId(), frag, "Frag_Top_tag");

                            } else if (discoverType.contains("closet")) {
                                FrameLayout frameLayout = new FrameLayout(this);
                                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                                frameLayout.setLayoutParams(params);
                                frameLayout.setId(i);
                                ln.addView(frameLayout);
                                Bundle bundle1=new Bundle();
                                bundle1.putString("object",data.getJSONObject(i).toString());
                                closetfragement frag=new closetfragement();
                                frag.setArguments(bundle1);
                                transaction.add(frameLayout.getId(), frag, "Frag_Top_tag");

                            } else if (discoverType.contains("product_collection")) {
                                FrameLayout frameLayout = new FrameLayout(this);
                                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                                frameLayout.setLayoutParams(params);
                                frameLayout.setId(i);
                                ln.addView(frameLayout);
                                Bundle bundle1=new Bundle();
                                bundle1.putString("object",data.getJSONObject(i).toString());
                                productfragement frag=new productfragement();
                                frag.setArguments(bundle1);
                                transaction.add(frameLayout.getId(), frag, "Frag_Top_tag");

                            } else if (discoverType.contains("user_collection")) {
                                FrameLayout frameLayout = new FrameLayout(this);
                                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                                frameLayout.setLayoutParams(params);
                                frameLayout.setId(i);
                                ln.addView(frameLayout);
                                Bundle bundle1=new Bundle();
                                bundle1.putString("object",data.getJSONObject(i).toString());
                                userfragement frag=new userfragement();
                                frag.setArguments(bundle1);
                                transaction.add(frameLayout.getId(), frag, "Frag_Top_tag");
                            } else if (discoverType.contains("custom_collection")) {
                                FrameLayout frameLayout = new FrameLayout(this);
                                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                                frameLayout.setLayoutParams(params);
                                frameLayout.setId(i);
                                ln.addView(frameLayout);
                                Bundle bundle1=new Bundle();
                                bundle1.putString("object",data.getJSONObject(i).toString());
                                customfragement frag=new customfragement();
                                frag.setArguments(bundle1);
                                transaction.add(frameLayout.getId(), frag, "Frag_Top_tag");
                            } else if (discoverType.contains("generic")) {
                                FrameLayout frameLayout = new FrameLayout(this);
                                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                                frameLayout.setLayoutParams(params);
                                frameLayout.setId(i);
                                ln.addView(frameLayout);
                                Bundle bundle1=new Bundle();
                                bundle1.putString("object",data.getJSONObject(i).toString());
                                genericfragement frag=new  genericfragement();
                                frag.setArguments(bundle1);
                                transaction.add(frameLayout.getId(), frag, "Frag_Top_tag");
                            }




                    }
try {
    transaction.commit();
}catch (IllegalStateException e)
{

}

//                    for (int i=0;i<data.length();i++){
//                        dataList.add(data.getJSONObject(i));
//                    }
//                    recyclerView.removeAllViews();
//                    adapter = new DiscoverAdaptor(dataList, HomePageNew.this, recyclerView);
//                    recyclerView.setAdapter(adapter);                              // Setting the adapter to RecyclerView
//                    recyclerView.setLayoutManager(mLayoutManager);
//                    adapter.notifyDataSetChanged();
                  GetOverlay();
                } else {
                    discover_progressBar.setVisibility(View.GONE);
                     CustomMessage.getInstance().CustomMessage(HomePageNew.this, "Oops. Unable to load data");
                }
            } catch (JSONException e) {
                discover_progressBar.setVisibility(View.GONE);
                e.printStackTrace();
            }

        } else if (flag.equals("getoverlay")) {
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);

            try {
                String status = resp.getString("status");
                if (!status.equals("error")) {
                    JSONObject data = resp.getJSONObject("data");
                    final String strimage = data.getString("image");
                    final boolean bl_close = data.getBoolean("can_close");
                    final String strtilte = data.getString("title");
                    final String str_uri = data.getString("uri_target");
                    final String str_description = data.getString("description");

                    String actname = data.getString("android_activity");
                    final String activityname = "activity." + actname;
                    // final String activityname = "activity.ProductPage";
                    //System.out.println("overlay new" + activityname);
                    boolean bl_active = data.getBoolean("active");
                    final int intDelay = data.getInt("delay");
                    final boolean bl_fullscreen = data.getBoolean("full_screen");
                    final String strbutton = data.getString("cta_text");
                    ExternalFunctions.strOverlayactivity = actname;
                    ExternalFunctions.bloverlay = true;

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
                                            try {
                                                ExternalFunctions.showOverlay(getBaseContext(), strtilte, str_description, strbutton, activityname, bl_fullscreen, bl_close, strimage);
                                            } catch (Exception e) {


                                            }
                                        } else {
                                            t.cancel();

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


        } else if (flag.equals("admire")) {
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                try {
                    String status = resp.getString("status");
                    //System.out.println("ADMIRE : closet resp admire:" + resp);
                    if (status.equals("success")) {
                        closet_admire.setEnabled(true);
                        closet_admire.setTag("Admiring");
                        closet_admire.setText("Admiring");
                    } else {
                        closet_admire.setEnabled(true);
                         CustomMessage.getInstance().CustomMessage(getBaseContext(), "Oops. Something went wrong!");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    closet_admire.setEnabled(true);
                     CustomMessage.getInstance().CustomMessage(getBaseContext(), "Oops. Something went wrong!");
                }
            } else {
                 CustomMessage.getInstance().CustomMessage(getBaseContext(), "Oops. Something went wrong!");
            }
        } else if (flag.equals("unadmire")) {
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            closet_admire.setEnabled(true);
            if (resp != null) {
                try {
                    String status = resp.getString("status");
                    //System.out.println("ADMIRE : closet resp unadmire:" + resp);
                    if (status.equals("success")) {
                        closet_admire.setTag("Admire");
                        closet_admire.setText("Admire");

                    } else {
                         CustomMessage.getInstance().CustomMessage(getBaseContext(), "Oops. Something went wrong!");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                     CustomMessage.getInstance().CustomMessage(getBaseContext(), "Oops. Something went wrong!");
                }
            } else {
                 CustomMessage.getInstance().CustomMessage(getBaseContext(), "Oops. Something went wrong!");
            }
        } else if (flag.equals("useradmire")) {
            user_collection_admire_button.setEnabled(true);
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                try {
                    String status = resp.getString("status");
                    //System.out.println("ADMIRE : user resp admire:" + resp);
                    if (status.equals("success")) {
//                        user_collection_admire_button.setTag("Admiring");
//                        user_collection_admire_button.setText("Admiring");
                    } else {
                         CustomMessage.getInstance().CustomMessage(getBaseContext(), "Oops. Something went wrong!");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                     CustomMessage.getInstance().CustomMessage(getBaseContext(), "Oops. Something went wrong!");
                }
            } else {
                 CustomMessage.getInstance().CustomMessage(getBaseContext(), "Oops. Something went wrong!");
            }
        } else if (flag.equals("userunadmire")) {
            user_collection_admire_button.setEnabled(true);
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                try {
                    String status = resp.getString("status");
                    //System.out.println("ADMIRE : user resp unadmire:" + resp);
                    if (status.equals("success")) {
//                        user_collection_admire_button.setText("Admire");
//                        user_collection_admire_button.setTag("Admire");
                    } else {
                         CustomMessage.getInstance().CustomMessage(getBaseContext(), "Oops. Something went wrong!");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                     CustomMessage.getInstance().CustomMessage(getBaseContext(), "Oops. Something went wrong!");
                }
            } else {
                 CustomMessage.getInstance().CustomMessage(getBaseContext(), "Oops. Something went wrong!");
            }
        }
    }


    @Override
    public void onErrorCallback(VolleyError error, String flag) {
        discover_progressBar.setVisibility(View.GONE);
    }


//    Activity functions
//    ------------------------------------------------------
public class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        //MyApplicationClass.sFirstRun= false;
        Intent intent = new Intent("HomePageNew");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        if (ex.getClass().equals(OutOfMemoryError.class)) {
            try {
                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File(sdCard.getAbsolutePath() + "/dump.hprof");
                dir.mkdirs();

                android.os.Debug.dumpHprofData(String.valueOf(dir));
                ////////System.out.println("OUTOFMEMORYDUMP");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ex.printStackTrace();
    }
}

//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        // TODO Auto-generated method stub
//        super.onRestoreInstanceState(savedInstanceState);
//
//        String stateSaved = savedInstanceState.getString("saved_state");
//
//        if(stateSaved == null){
//            Toast.makeText(HomePageNew.this,
//                    "onRestoreInstanceState:\n" +
//                            "NO state saved!",
//                    Toast.LENGTH_LONG).show();
//        }else{
//            Toast.makeText(HomePageNew.this,
//                    "onRestoreInstanceState:\n" +
//                            "saved state = " + stateSaved,
//                    Toast.LENGTH_LONG).show();
//
//        }
//
//    }
//
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        // TODO Auto-generated method stub
//        super.onSaveInstanceState(outState);
//
//        String stateToSave = "hello";
//        outState.putString("saved_state", stateToSave);
//        outState.putString(STATE_SCORE,data.toString());
//        Toast.makeText(HomePageNew.this,
//                "onSaveInstanceState:\n" +
//                        "saved_state = " + stateToSave,
//                Toast.LENGTH_LONG).show();
//    }

//    @Override
//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        super.onSaveInstanceState(savedInstanceState);
//        // Save the user's current game state
//        System.out.println("Saved instance kstored");
//
//      //  savedInstanceState.putString(STATE_SCORE,data.toString());
//
//        // Always call the superclass so it can save the view hierarchy state
//
//    }
//    @Override
//    public void onRestoreInstanceState(Bundle savedInstanceState) {
//        // Always call the superclass so it can restore the view hierarchy
//        super.onRestoreInstanceState(savedInstanceState);
//
//        System.out.println("Saved instance krestored");
//        // Restore state members from saved instance
//

//
//    }
    @Override
    public void onResume() {
        super.onResume();
        Hotline.getInstance(getApplicationContext()).getUnreadCountAsync(new UnreadCountCallback() {
            @Override
            public void onResult(HotlineCallbackStatus hotlineCallbackStatus, int unreadCount) {
                //Assuming "badgeTextView" is a text view to show the count on

                System.out.println("Hotline count:"+unreadCount);
                if(unreadCount>0){
                    tvchatcount.setText(String.valueOf(unreadCount));
                    tvchatcount.setVisibility(View.VISIBLE);
                }else{
                    tvchatcount.setVisibility(View.GONE);
                }



            }
        });
        mTracker.set("UserName", GetSharedValues.getUsername(getApplicationContext()));mTracker.set("ZapUsername", GetSharedValues.getZapname(getApplicationContext()));
         mTracker.setScreenName("Discoverpage");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        //System.out.printf("CARTCOUNT: resume" + String.valueOf(db.getTableRecordsCount("CART")));

        SharedPreferences CartSession = getSharedPreferences("CartSession",
                Context.MODE_PRIVATE);
//        if (db.getTableRecordsCount("CART") > 0) {
//            cartcount.setVisibility(View.VISIBLE);
//            cartcount.setText(String.valueOf(CartSession.getInt("cartCount", 0)));
//        } else {
//            cartcount.setVisibility(View.INVISIBLE);
//        }
//        mDrawerList.setVisibility(View.INVISIBLE);
//        if (mDrawerLayout.isDrawerVisible(Gravity.LEFT)) {
//            mDrawerLayout.closeDrawer(Gravity.LEFT);
//        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mDrawerList.setVisibility(View.INVISIBLE);
        if (mDrawerLayout.isDrawerVisible(Gravity.LEFT)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }
        ////System.out.println("APPSTATUS:" + "restart");
        //System.gc();
        Runtime runtime = Runtime.getRuntime();

        if (!GetSharedValues.GetgcmId(getBaseContext()).equals("")) {
            ApiService.getInstance(getBaseContext(), 1).getData(HomePageNew.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/?gcm_reg_id=" + GetSharedValues.GetgcmId(getBaseContext()), "session");
        } else {
            ApiService.getInstance(getBaseContext(), 1).getData(HomePageNew.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/", "session");
        }
    }

    public void ClearViews() {
        //System.gc();
        Runtime runtime = Runtime.getRuntime();
       // recyclerView.removeAllViews();

        if (banner_image != null) {
            if (banner_image.getDrawable() != null)
                banner_image.getDrawable().setCallback(null);
        }
        try {

            int count0 = linearLayout.getChildCount();
            for (int i = 0; i < count0; i++) {
                ImageView v = (ImageView) linearLayout.getChildAt(i).findViewById(R.id.inside_closet_image);
                //System.out.println("Destroy inside if closet:" + v);
                if (v != null) {
                    if (v.getDrawable() != null)
                        v.getDrawable().setCallback(null);
                    ((BitmapDrawable)v.getDrawable()).getBitmap().recycle();
                    System.gc();
                }
            }

            int count1 = product_linearLayout.getChildCount();
            for (int i = 0; i < count1; i++) {
                ImageView v1 = (ImageView) product_linearLayout.getChildAt(i).findViewById(R.id.product_collection_image);
                if (v1 != null) {
                    //System.out.println("Destroy inside if product:" + v1);
                    if (v1.getDrawable() != null)
                        v1.getDrawable().setCallback(null);
                    ((BitmapDrawable)v1.getDrawable()).getBitmap().recycle();
                    System.gc();

                }
            }

            int count2 = user_linearLayout.getChildCount();
            for (int i = 0; i < count2; i++) {
                ImageView v2 = (ImageView) user_linearLayout.getChildAt(i).findViewById(R.id.user_collection_image);
                if (v2 != null) {
                    //System.out.println("Destroy inside if user:" + v2);
                    if (v2.getDrawable() != null)
                        v2.getDrawable().setCallback(null);
                    ((BitmapDrawable)v2.getDrawable()).getBitmap().recycle();
                    System.gc();
                }
            }

            int count3 = custom_inearLayout.getChildCount();
            for (int i = 0; i < count3; i++) {
                ImageView v3 = (ImageView) custom_inearLayout.getChildAt(i).findViewById(R.id.custom_collection_image);
                if (v3 != null) {
                    //System.out.println("Destroy inside if custom:" + v3);
                    if (v3.getDrawable() != null)
                        v3.getDrawable().setCallback(null);
                    ((BitmapDrawable)v3.getDrawable()).getBitmap().recycle();
                    System.gc();
                }
            }

            linearLayout.removeAllViews();
            product_linearLayout.removeAllViews();
            user_linearLayout.removeAllViews();
            custom_inearLayout.removeAllViews();

            closet_horizontal_scrollview.removeAllViews();
            product_collection_scrollview.removeAllViews();
            user_collection_scrollview.removeAllViews();
            mainScrollView.removeAllViews();
        } catch (Exception e) {
            //recyclerView.removeAllViews();
        }
    }


}

package activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
//import com.uxcam.UXCam;
import com.zapyle.zapyle.Alerts;
import com.zapyle.zapyle.CheckConnectivity;
import com.zapyle.zapyle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import DataBase.DatabaseDB;
import application.MyApplicationClass;
import utils.CustomMessage;
import utils.ExternalFunctions;
import utils.FontUtils;
import utils.GetSharedValues;

/**
 * Created by haseeb on 5/8/16.
 */
public class BuyPage extends ActionBarActivity {

    FrameLayout lv_designer, lv_vintage, lv_market;
    DatabaseDB db;
    DrawerLayout mDrawerLayout;
    ListView mDrawerList;
    TextView tvcount;
    public ArrayList<String> drawerdata = new ArrayList<String>();
    DrawerAdaptor mAdapter;
    ActionBarDrawerToggle mDrawerToggle;
    Boolean clickStatus = false;
    String callingActivity;
    JSONObject objMainList = new JSONObject();
    Tracker mTracker;
    TextView tvchatcount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_but_page);
        ExternalFunctions.dicActivity="";
        MyApplicationClass application = (MyApplicationClass) getApplication();
        mTracker = application.getDefaultTracker();
       // overridePendingTransition( R.anim.slide_in, R.anim.slide_down );
        //UXCam.startWithKey("1dfb25141864376");
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ApplyCustomFont();
        InitialiseHeader();

        ExternalFunctions.strfilter = "";
        ExternalFunctions.blfiteropen = false;
        ExternalFunctions.strFilterdata = new String[1];
        try {
            Intent intent = getIntent();
            Uri data = intent.getData();
            String path = data.getPath();
//            System.out.println("query1" + "https:wwww.zapyle.com" + data.getPath() + "?" + data.getQuery());
            String urlpath = "https://wwww.zapyle.com" + data.getPath() + "?" + data.getQuery();
            if (data.getQuery() == null) {
                getParamFromUrl(path, "", "");
            } else {
                getParamFromUrl(path, data.getQuery(), urlpath);
            }
        } catch (Exception e) {

        }

        try {
            callingActivity = "activity." + getIntent().getStringExtra("activity");
        } catch (Exception e) {
            callingActivity = "activity.HomePage";
        }

        lv_designer = (FrameLayout) findViewById(R.id.designer);
        lv_market = (FrameLayout) findViewById(R.id.market);
        lv_vintage = (FrameLayout) findViewById(R.id.vintage);

        lv_designer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mTracker.set("Buypage Choice","Designer");
                mTracker.send(new HitBuilders.ScreenViewBuilder().build());
                ExternalFunctions.ActivityParam = "base";
                ExternalFunctions.DiffParam = "designer";
                Intent designer = new Intent(getApplicationContext(), BuySecondPage.class);
                designer.putExtra("activity", "BuyPage");
                startActivity(designer);
                finish();
            }
        });

        lv_vintage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTracker.set("Buypage Choice","Curated");
                mTracker.send(new HitBuilders.ScreenViewBuilder().build());
                ExternalFunctions.ActivityParam = "base";
                ExternalFunctions.DiffParam = "curated";
                Intent vintage = new Intent(getApplicationContext(), BuySecondPage.class);
                vintage.putExtra("activity", "BuyPage");
                startActivity(vintage);
                finish();
            }
        });


        lv_market.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTracker.set("Buypage Choice","Buypage");
                mTracker.send(new HitBuilders.ScreenViewBuilder().build());
                ExternalFunctions.ActivityParam = "base";
                ExternalFunctions.DiffParam = "marketplace";
                Intent market = new Intent(getApplicationContext(), BuySecondPage.class);
                market.putExtra("activity", "BuyPage");
                startActivity(market);
                finish();
            }
        });

    }

    private void ApplyCustomFont() {
        FontUtils.setCustomFont(mDrawerLayout, getAssets());
    }


    private void getParamFromUrl(String path, String strquery, String strpath) {
        int lastindex, length;
        lastindex = path.lastIndexOf("/");
        length = path.length();
        String strParam = path.substring(lastindex + 1, length);
        ExternalFunctions.ActivityParam = "base";
        System.out.println("querykk" + strquery);
        if (strquery != null && !strquery.toLowerCase().contains("search")) {
            ExternalFunctions.strfilter = strquery;
            ExternalFunctions.FilterStatus = true;
            String strDiff="";

                strDiff = path.substring(0, lastindex);



            lastindex = strDiff.lastIndexOf("/");
            strDiff = strDiff.substring(lastindex + 1, strDiff.length());
            ExternalFunctions.DiffParam = strDiff;
            System.out.println(strDiff+"websitezz" + ExternalFunctions.strfilter);
            Intent intent_web_link = new Intent(getApplicationContext(), FilteredFeed.class);
            ExternalFunctions.ActivityParam = "base";
            ExternalFunctions.BroadCastedActivity = "";
            ExternalFunctions.BroadCastedUrl = "";
            startActivity(intent_web_link);
            finish();
        } else if (strquery != null && strquery.toLowerCase().contains("search")) {
            //String url = "http://www.example.com/?argument=value&argument2=value2&...";
            ExternalFunctions.strfilter = strquery;
            ExternalFunctions.blapplysearch = true;
            ExternalFunctions.blapplysug = false;
            JSONArray arrForcat = new JSONArray();
            JSONArray arrForsubcat  = new JSONArray();

            JSONArray arrForstyle  = new JSONArray();

            JSONArray arrForoccasion  = new JSONArray();
            JSONArray arrForcolor  = new JSONArray();

            JSONArray arrForbrand  = new JSONArray();
            String stringval=null,strquerystring=null;
            Uri uri = Uri.parse(strpath);
            for (String key : uri.getQueryParameterNames()) {
                String value = uri.getQueryParameter(key);

                if (key.toLowerCase().equals("category")) {
                    arrForcat = new JSONArray();
                    arrForcat.put(value);

                }else if(key.toLowerCase().equals("subcategory")){
                    arrForsubcat = new JSONArray();
                    arrForsubcat.put(value);
                }else if(key.toLowerCase().equals("style")){
                    arrForstyle = new JSONArray();
                    arrForstyle.put(value);
                }else if(key.toLowerCase().equals("brand")){
                    System.out.println( "query1 brand");
                    arrForbrand = new JSONArray();
                    arrForbrand.put(value);
                    System.out.println( "query1 brand1"+arrForbrand);
                }else if(key.toLowerCase().equals("occasion")){
                    arrForoccasion = new JSONArray();
                    arrForoccasion.put(value);
                }else if(key.toLowerCase().equals("color")){
                    arrForcolor = new JSONArray();
                    arrForcolor.put(value);
                }else if(key.toLowerCase().equals("string")){
                    stringval=value;
                }else if(key.toLowerCase().equals("search")){

                    strquerystring=value;
                }
                System.out.println(key + "query1" + value);
            }
            try {
                objMainList.put("Category", arrForcat);
            objMainList.put("Style", arrForstyle);
            objMainList.put("SubCategory", arrForsubcat);
            objMainList.put("Occasion", arrForoccasion);
            objMainList.put("Color", arrForcolor);
            objMainList.put("Brand", arrForbrand);
            objMainList.put("string", stringval);
                ExternalFunctions.jsonObjsearch=new JSONObject();
            ExternalFunctions.jsonObjsearch.put("query_string",strquerystring);
            ExternalFunctions.jsonObjsearch.put("filter", objMainList);
                ExternalFunctions.strsearch=strquerystring;
                System.out.println( "query1" +ExternalFunctions.jsonObjsearch);
            }catch (Exception e){
                System.out.println( "query1"+e.getMessage());
            }

            Intent feed = new Intent(getApplicationContext(), searchFeedPage.class);
            feed.putExtra("activity", "SplashScreen");
            startActivity(feed);
            finish();
        }

        else {
            ExternalFunctions.DiffParam = strParam;
            System.out.println("websitezz" + ExternalFunctions.strfilter);
            Intent intent_web_link = new Intent(getApplicationContext(), BuySecondPage.class);
            intent_web_link.putExtra("activity", "BuyPage");
            startActivity(intent_web_link);
            finish();
        }


    }

    private void InitialiseHeader() {

        db = new DatabaseDB(getApplicationContext());
        db.openDB();

        mDrawerList = (ListView) findViewById(R.id.navList);
        try {
            mDrawerList.getLayoutParams().width = (int) (ExternalFunctions.displaymetrics(this).getInt("width") * (0.7));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mDrawerList.setBackgroundColor(Color.BLACK);
        mDrawerList.setDivider(null);
        addDrawerItems();
        setupDrawer();
        if (mDrawerLayout.isDrawerVisible(Gravity.LEFT)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }


//      Custom actionbar
//        ------------------------------------------


        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        View customView = getLayoutInflater().inflate(R.layout.home_actionbar, null);
        getSupportActionBar().setCustomView(customView);
        Toolbar parent = (Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0, 0);

        TextView heading = (TextView) findViewById(R.id.heading);
        heading.setText("Buy");


        RelativeLayout cart = (RelativeLayout) findViewById(R.id.home_cart);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                customToast = new CustomToast(HomePageNew.this, "Cart is clicked");
                Intent cart = new Intent(getApplicationContext(), ShopCart.class);
                cart.putExtra("activity", "FeedPage");
                startActivity(cart);
                finish();

            }
        });


        tvcount = (TextView) customView.findViewById(R.id.cartcount);
        if (ExternalFunctions.cartcount > 0) {
            tvcount.setVisibility(View.VISIBLE);
            tvcount.setText(String.valueOf(ExternalFunctions.cartcount));
        } else {
            if (db.getTableRecordsCount("CART") > 0) {
                tvcount.setVisibility(View.VISIBLE);
                tvcount.setText(String.valueOf(db.getTableRecordsCount("CART")));
            } else {
                tvcount.setVisibility(View.INVISIBLE);
            }
        }

        ImageView notifier_btn = (ImageView) findViewById(R.id.home_notification);
        notifier_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (GetSharedValues.LoginStatus(getApplicationContext())) {

                    Intent notif = new Intent(getApplicationContext(), Notifications.class);
                    notif.putExtra("activity", "FeedPage");
                    startActivity(notif);
//                    finish();
                } else {
                    Alerts.loginAlert(BuyPage.this);
                }
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
//                customToast = new CustomToast(HomePageNew.this, "Search is clicked");
                Intent search = new Intent(getApplicationContext(), searchnew.class);
                search.putExtra("activity", "FeedPage");
                startActivity(search);
//                finish();
            }
        });
    }


    private void addDrawerItems() {
        drawerdata.clear();
        drawerdata.add("");
        mAdapter = new DrawerAdaptor(this, drawerdata);
//
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.setBackgroundColor(Color.BLACK);
    }


    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

//                getSupportActionBar().setTitle("Navigation!");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
//                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    public class DrawerAdaptor extends BaseAdapter {
        Boolean clickStatus = false;
        Boolean buyclickStatus = false;
        ArrayList<String> drawerdata = new ArrayList<String>();

        List<Integer> hiddenItems;
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
            FontUtils.setCustomFont(convertView1, context.getAssets());
            ShowmainList(myViewHolder.mainLayout);
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
    }


    public void ShowmainList(RelativeLayout mainlayout) {
        final LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        if (GetSharedValues.LoginStatus(this)) {
            mainlayout.removeAllViews();
            final String[] mainArray = {"Discover", "Sell", "Buy", "My Profile", "My Account", "Discounts", "Authenticity", "About"};
            for (int i = 0; i < mainArray.length; i++) {
                LayoutInflater comment_inflater = LayoutInflater.from(this);
                final View view;
                view = comment_inflater.inflate(R.layout.drawer_list_item, null, false);
                TextView tvTitle = (TextView) view.findViewById(R.id.drawerText);
                tvTitle.setTextColor(Color.WHITE);
                final TextView tvIcon = (TextView) view.findViewById(R.id.drawerImage);
                final LinearLayout lvSecondary = (LinearLayout) view.findViewById(R.id.secondarylistview);
                lvSecondary.setVisibility(View.GONE);
                tvTitle.setText(mainArray[i]);
                if (mainArray[i].contains("About")) {
                    tvIcon.setText(R.string.downarrow);
                } else {
                    tvIcon.setText("");
                }

                final int finalI = i;
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (mainArray[finalI]) {
                            case "Discover":
                                if (CheckConnectivity.isNetworkAvailable(getApplicationContext())) {
                                    if (mDrawerLayout.isDrawerVisible(Gravity.LEFT)) {
                                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                                    }

                                    Intent discover = new Intent(BuyPage.this, HomePageNew.class);
                                    discover.putExtra("activity", "BuyPage");
                                    discover.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                    startActivity(discover);
                                    finish();

                                } else {
                                     CustomMessage.getInstance().CustomMessage(BuyPage.this, "Internet is not available!");
                                }
                                break;

                            case "Sell":
                                if (CheckConnectivity.isNetworkAvailable(getApplicationContext())) {
                                    if (GetSharedValues.LoginStatus(getApplicationContext())) {
                                        Intent intent = new Intent(BuyPage.this, Upload1.class);
                                        intent.putExtra("activity", "BuyPage");
                                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                        ExternalFunctions.uploadbackcheck = 1;
                                        startActivity(intent);
//                                    finish();

                                    } else {
                                        Alerts.loginAlert(getApplicationContext());
                                    }
                                } else {
//                                Alerts.InternetAlert(FeedPage.this);
                                     CustomMessage.getInstance().CustomMessage(BuyPage.this, "Internet is not available!");

                                }

                                break;

                            case "Buy":
//                                Intent buy = new Intent(BuyPage.this, BuyPage.class);
//                                startActivity(buy);
//                                finish();

                                break;


                            case "My Profile":
                                if (CheckConnectivity.isNetworkAvailable(getApplicationContext())) {
                                    if (GetSharedValues.LoginStatus(getApplicationContext())) {
                                        Intent intent = new Intent(getApplicationContext(), ProfilePage.class);
                                        intent.putExtra("user_id", GetSharedValues.getuserId(getApplicationContext()));
                                        intent.putExtra("p_username", GetSharedValues.getUsername(getApplicationContext()));
                                        intent.putExtra("activity", "BuyPage");
                                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                        startActivity(intent);
//                                    finish();

                                    } else {
                                        Alerts.loginAlert(getApplicationContext());
                                    }
                                } else {
//                                Alerts.InternetAlert(getApplicationContext());
                                     CustomMessage.getInstance().CustomMessage(BuyPage.this, "Internet is not available!");

                                }
                                break;

                            case "My Account":
                                if (GetSharedValues.LoginStatus(getApplicationContext())) {
                                    Intent intent = new Intent(getApplicationContext(), Myaccountpage.class);
                                    intent.putExtra("activity", "BuyPage");
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                    startActivity(intent);
//                                    finish();

                                } else {
                                    Alerts.loginAlert(getApplicationContext());
                                }
                                break;


                            case "Discounts":
                                if (CheckConnectivity.isNetworkAvailable(getApplicationContext())) {
                                    if (GetSharedValues.LoginStatus(getApplicationContext())) {
                                        Intent myorders = new Intent(getApplicationContext(), EarnCash.class);
                                        myorders.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                        myorders.putExtra("activity", "BuyPage");
                                        startActivity(myorders);
//                                    finish();
                                    } else {
                                        Alerts.loginAlert(getApplicationContext());
                                    }
                                } else {
//                                Alerts.InternetAlert(FeedPage.this);
                                     CustomMessage.getInstance().CustomMessage(BuyPage.this, "Internet is not available!");
                                }
                                break;


                            case "Authenticity":


                                Intent intent2 = new Intent(getApplicationContext(), Authenticity.class);
                                intent2.putExtra("activity", "BuyPage");
                                intent2.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                startActivity(intent2);

                                break;


                            case "About":
//
                                if (clickStatus) {
                                    clickStatus = false;
//                                        tvIcon.setText(R.string.downarrow);
                                    lvSecondary.setVisibility(View.GONE);
                                } else {
                                    clickStatus = true;
//                                        tvIcon.setText(R.string.uparrow);
                                    lvSecondary.setVisibility(View.VISIBLE);
                                    ShowList(lvSecondary);

                                }

                                break;


                        }
                    }
                });


                linearLayout.addView(view);
            }
        } else {
            mainlayout.removeAllViews();
            final String[] mainArray = {"Discover", "Sell", "Buy", "Login", "Authenticity", "About"};
            for (int i = 0; i < mainArray.length; i++) {
                LayoutInflater comment_inflater = LayoutInflater.from(getApplicationContext());
                final View view;
                view = comment_inflater.inflate(R.layout.drawer_list_item, null, false);
                TextView tvTitle = (TextView) view.findViewById(R.id.drawerText);
                tvTitle.setTextColor(Color.WHITE);
                final TextView tvIcon = (TextView) view.findViewById(R.id.drawerImage);
                final LinearLayout lvSecondary = (LinearLayout) view.findViewById(R.id.secondarylistview);
                lvSecondary.setVisibility(View.GONE);
                tvTitle.setText(mainArray[i]);
                if (mainArray[i].contains("About")) {
                    tvIcon.setText(R.string.downarrow);
                } else {
                    tvIcon.setText("");
                }
                final int finalI = i;
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (mainArray[finalI]) {
                            case "Discover":
                                if (CheckConnectivity.isNetworkAvailable(getApplicationContext())) {
                                    mDrawerLayout.closeDrawer(Gravity.LEFT);

                                    Intent discover = new Intent(getApplicationContext(), HomePageNew.class);
                                    discover.putExtra("activity", "BuyPage");
                                    discover.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                    startActivity(discover);
                                    finish();

                                } else {
//                                Alerts.InternetAlert(FeedPage.this);
                                     CustomMessage.getInstance().CustomMessage(BuyPage.this, "Internet is not available!");
                                }
                                break;

                            case "Sell":
                                if (CheckConnectivity.isNetworkAvailable(getApplicationContext())) {
                                    if (GetSharedValues.LoginStatus(getApplicationContext())) {
                                        Intent intent = new Intent(getApplicationContext(), Upload1.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                        ExternalFunctions.uploadbackcheck = 1;
                                        startActivity(intent);

                                    } else {
                                        Alerts.loginAlert(BuyPage.this);
                                    }
                                } else {
//                                Alerts.InternetAlert(FeedPage.this);
                                     CustomMessage.getInstance().CustomMessage(BuyPage.this, "Internet is not available!");

                                }

                                break;


                            case "Buy":
//                                Intent buy = new Intent(BuyPage.this, BuyPage.class);
//                                startActivity(buy);
//                                finish();

                                    mDrawerLayout.closeDrawer(Gravity.LEFT);

                                break;

                            case "Login":
                                if (CheckConnectivity.isNetworkAvailable(getApplicationContext())) {
//                                        if (GetSharedValues.LoginStatus(context)) {
                                    Intent parallax = new Intent(getApplicationContext(), Parallax.class);
                                    parallax.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    parallax.putExtra("booltype", false);
                                    startActivity(parallax);
                                    finish();
//                                        } else {
//                                            Alerts.loginAlert(context);
//                                        }
                                } else {
//                                Alerts.InternetAlert(FeedPage.this);
                                     CustomMessage.getInstance().CustomMessage(BuyPage.this, "Internet is not available!");

                                }

                                break;


                            case "Authenticity":


                                Intent intent2 = new Intent(getApplicationContext(), Authenticity.class);
                                intent2.putExtra("activity", "BuyPage");
                                intent2.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                startActivity(intent2);

                                break;


                            case "About":

                                if (clickStatus) {
                                    clickStatus = false;
                                    tvIcon.setText(R.string.downarrow);
                                    lvSecondary.setVisibility(View.GONE);
                                } else {
                                    clickStatus = true;
                                    tvIcon.setText(R.string.uparrow);
                                    lvSecondary.setVisibility(View.VISIBLE);
                                    ShowList(lvSecondary);

                                }

                                break;


                        }
                    }
                });


                linearLayout.addView(view);
            }
        }


        mainlayout.addView(linearLayout);
    }


    public void ShowList(LinearLayout view) {
        final String[] osArray = {"-STORY", "-FAQ", "-RATE APP", "-CALL ZAPYLE"};
        view.removeAllViews();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(20, 25, 10, 25);
        for (int j = 0; j < osArray.length; j++) {
            TextView tv = new TextView(this);
            tv.setText(osArray[j]);
            tv.setTextColor(Color.WHITE);
            tv.setTypeface(null, Typeface.BOLD);
            tv.setLayoutParams(lp);
            final int finalJ = j;
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (osArray[finalJ]) {

                        case "-STORY":
                            if (CheckConnectivity.isNetworkAvailable(BuyPage.this)) {
                                Intent intent1 = new Intent(BuyPage.this, About_us.class);
                                intent1.putExtra("activity", "BuyPage");
                                intent1.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                startActivity(intent1);
//                                finish();
                            } else {
//                                Alerts.InternetAlert(getApplicationContext());
                               CustomMessage.getInstance().CustomMessage(BuyPage.this, "Internet is not available!");

                            }
                            break;


                        case "-FAQ":

                            if (CheckConnectivity.isNetworkAvailable(BuyPage.this)) {
                                Intent intent5 = new Intent(BuyPage.this, FAQ.class);
                                intent5.putExtra("activity", "BuyPage");
                                intent5.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                startActivity(intent5);
//                                finish();
                            } else {
//                                Alerts.InternetAlert(getApplicationContext());
                               CustomMessage.getInstance().CustomMessage(BuyPage.this, "Internet is not available!");

                            }
                            break;


                        case "-RATE APP":
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("https://play.google.com/store/apps/details?id=com.zapyle.zapyle")));
                            break;


                        case "-CALL ZAPYLE":


                            if (CheckConnectivity.isNetworkAvailable(BuyPage.this)) {
                                Intent intent = new Intent(Intent.ACTION_CALL);
                                intent.setData(Uri.parse("tel:" + "08040004541"));

                                startActivity(intent);
//                                finish();
                            } else {
//                                Alerts.InternetAlert(getApplicationContext());
                               CustomMessage.getInstance().CustomMessage(BuyPage.this, "Internet is not available!");

                            }
                            break;

                    }
                }
            });
            view.addView(tv);

        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        try {
            Intent intent = new Intent(this, Class.forName(callingActivity));
            startActivity(intent);
            finish();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Intent discover = new Intent(this, HomePageNew.class);
            discover.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(discover);
            finish();
        }

    }



    @Override
    protected void onResume() {
        super.onResume();

        ExternalFunctions.dicActivity="";
        mTracker.set("UserName", GetSharedValues.getUsername(getApplicationContext()));mTracker.set("ZapUsername", GetSharedValues.getZapname(getApplicationContext()));
        mTracker.setScreenName("Buypage");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        if (ExternalFunctions.cartcount > 0) {
            tvcount.setVisibility(View.VISIBLE);
            tvcount.setText(String.valueOf(ExternalFunctions.cartcount));
        } else {
            if (db.getTableRecordsCount("CART") > 0) {
                tvcount.setVisibility(View.VISIBLE);
                tvcount.setText(String.valueOf(db.getTableRecordsCount("CART")));
            } else {
                tvcount.setVisibility(View.INVISIBLE);
            }
        }

        if (mDrawerLayout.isDrawerVisible(Gravity.LEFT)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }
    }
}

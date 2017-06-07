package activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.appvirality.android.AppviralityAPI;
import com.google.android.gms.common.api.GoogleApiClient;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
//import com.uxcam.UXCam;
import com.zapyle.zapyle.Alerts;
import com.zapyle.zapyle.CheckConnectivity;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import DataBase.DatabaseDB;
import adapters.RecyclerViewProductAdapter;
import adapters.searchAdapter;
import adapters.searchGridAdaptor;
import models.HomeFeedItem;
import network.ApiCommunication;
import network.ApiService;
import network.JsonParser;
import utils.Animations;
import utils.CommonFinish;
import utils.CustomMessage;
import utils.ExternalFunctions;
import utils.FontUtils;
import utils.GetSharedValues;


public class searchFeedPage extends ActionBarActivity implements ApiCommunication {

    SwipeRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    searchAdapter adapter;
    public ArrayList<HomeFeedItem> feeds;
    final String SCREEN_NAME = "FEED";
    ProgressBar progressBar;
    public static ProgressBar gridloader;

    // private ListView mDrawerList;

    Boolean StaticDataStatus;
    public static JSONObject screenSize;
    public static int screenHeight, screenWeight;
    public static int comment_scroll_position = 0;
    public static int Scroll_count = 0;


    int pageNo = 1;
    private int lastVisibleItem, totalItemCount;
    private boolean loading, gridloading;

    // LinearLayout viewSwitcher;


    //  ImageView viewSwitcherImage;

    public static Boolean LOGINCHECK = false;
    MixpanelAPI mixpanel;
    Date today;
    public static String activityname;

    //    grid params
    searchGridAdaptor feedGridAdaptor;
    //    GridView gridView;
    RelativeLayout topbar;
    View seperation;
    SharedPreferences settings;
    SharedPreferences.Editor editor;

    RecyclerView.LayoutManager mLayoutManager = null;
    RecyclerView.LayoutManager mGridLayoutManager = null;
    Boolean ScrollStatus = false;
    TextView tv_sortby;

    public static Boolean SortStatus = false;
    public static Boolean SocketStatus = false;
    public static Boolean FilterStatus = false;
    public static Boolean FeedGcmStatus = false;
    public static Boolean switchStatus = false;
    TextView GotoSearch;

    RelativeLayout EmptyFeedLayout;
    DatabaseDB db;
    private static final String PAGE = "pageno";
    private static final String BODY = "json";
    TextView tvsearchtext;
    ImageView imgback;
    ImageView imggrid, imglist;
    boolean blswitch = true;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_search);
        //  //////Thread.currentThread().setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
        //UXCam.startWithKey("1dfb25141864376");
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);

//        Database function
//        ----------------------------------------------------

        db = new DatabaseDB(getApplicationContext());
        db.openDB();


//        Applying font
//        ---------------------------------------------------
        View view = findViewById(R.id.feedrecyclerviewitem);
        FontUtils.setCustomFont(view, getAssets());

//        declaring shared values
//        --------------------------------------------------
        settings = getSharedPreferences("FeedSession",
                Context.MODE_PRIVATE);
        editor = settings.edit();
        ExternalFunctions.sortsearch=5;

//        Initialise broadcast reciever
//        ---------------------------------------------------
       // LocalBroadcastManager.getInstance(this).registerReceiver(CommonFinish.broadcastReceiver, new IntentFilter("searchFeedPage"));
//        Checking constatnts
//        --------------------------------------------------
        ExternalFunctions.cContextArray[7] = searchFeedPage.this;
        ProfilePage.loadtoupload = 0;
        ExternalFunctions.disapprovemsg = "1";
        ExternalFunctions.referalmsg = "1";
        screenSize = ExternalFunctions.displaymetrics(searchFeedPage.this);
        screenHeight = screenSize.optInt("width");
        screenWeight = screenSize.optInt("height");
        LinearLayout l11 = (LinearLayout) findViewById(R.id.l11);
        LinearLayout.LayoutParams fieldparams = new LinearLayout.LayoutParams(screenHeight / 2, LinearLayout.LayoutParams.MATCH_PARENT);

        l11.setLayoutParams(fieldparams);
//        Topbar declaration
//        -------------------------------------------------
        topbar = (RelativeLayout) findViewById(R.id.topbar);
        seperation = findViewById(R.id.seperation);
        seperation.setVisibility(View.INVISIBLE);
        topbar.setVisibility(View.INVISIBLE);
        //  new_stories = (TextView) findViewById(R.id.new_stories);
        // new_stories.setText(getString(R.string.uparrow) + " JUST IN");
        // new_stories.setVisibility(View.GONE);
        tvsearchtext = (TextView) findViewById(R.id.editText);
        tvsearchtext.setText( ExternalFunctions.strsearch);
        imgback = (ImageView) findViewById(R.id.ImageView);
        tv_sortby = (TextView) findViewById(R.id.sortby);
        imggrid = (ImageView) findViewById(R.id.imggrid);
        imglist = (ImageView) findViewById(R.id.imglist);

        // = (TextView) findViewById(R.id.tvrefine);
        tv_sortby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSortDialog();
            }
        });


//
//        if (FilterStatus) {
//            tvrefine.setTextColor(Color.parseColor("#ff7474"));
//        } else {
//            tvrefine.setTextColor(Color.BLACK);
//        }

        if (SortStatus || ExternalFunctions.sortsearch > 0) {
            //tv_sortby.setTextColor(Color.parseColor("#ff7474"));
        } else {
            //tv_sortby.setTextColor(Color.BLACK);
        }


//        Getting shared values
//        -------------------------------------------------
        final SharedPreferences login_settings = getApplicationContext().getSharedPreferences("LoginSession",
                Context.MODE_PRIVATE);
        StaticDataStatus = login_settings.getBoolean("STATIC_DATA", false);
        LOGINCHECK = login_settings.getBoolean("LOGIN_STATUS", false);


//        Get bundle values
//        --------------------------------------------

        try {
            Bundle bundle = this.getIntent().getExtras();
            activityname = bundle.getString("activity");
        } catch (Exception e) {
            activityname = "SplashScreen";
        }


//        Recyclerview functions
//        -----------------------------------------------------

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        gridloader = (ProgressBar) findViewById(R.id.gridloader);
        gridloader.setVisibility(View.GONE);
        feeds = new ArrayList<>();

        adapter = new searchAdapter(feeds, this);
        feedGridAdaptor = new searchGridAdaptor(feeds, this);
        recyclerView.setAdapter(adapter);                              // Setting the adapter to RecyclerView
        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager
        recyclerView.setLayoutManager(mLayoutManager);
        mGridLayoutManager = new GridLayoutManager(searchFeedPage.this, 2);
        progressBar.setVisibility(View.VISIBLE);
        EmptyFeedLayout = (RelativeLayout) findViewById(R.id.EmptyFeedLayout);
        GotoSearch = (TextView) findViewById(R.id.GotoSearch);

        GotoSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        EmptyFeedLayout.setVisibility(View.GONE);


        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (CheckConnectivity.isNetworkAvailable(searchFeedPage.this)) {
                    refreshFeed();
                } else {
                    refreshLayout.setRefreshing(false);
                     CustomMessage.getInstance().CustomMessage(searchFeedPage.this, "Internet is not available!");
                }
            }
        });
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 40) {

                    if (!ScrollStatus) {
                        Animations.collapse(topbar);
                        ScrollStatus = true;

                    }
                    // Scrolling up
                } else if (dy <= -10) {
                    if (ScrollStatus) {
                        ScrollStatus = false;
                        Animations.expand(topbar);

                    }
                    // Scrolling down
                }


                if (blswitch) {
                    totalItemCount = mGridLayoutManager.getItemCount();
                    lastVisibleItem = ((GridLayoutManager) mGridLayoutManager).findLastVisibleItemPosition();

                } else {
                    totalItemCount = mLayoutManager.getItemCount();
                    lastVisibleItem = ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();

                }
                if (!loading && lastVisibleItem == totalItemCount - 1) {
                    ////Log.e(SCREEN_NAME, "Loding more items");
                    feeds.add(null);
                    adapter.notifyItemInserted(feeds.size() - 1);
                    loading = true;
                    pageNo = pageNo + 1;

                    Scroll_count = recyclerView.getScrollState();

                    if (CheckConnectivity.isNetworkAvailable(searchFeedPage.this)) {

                        if (FilterStatus) {
                            editor.putBoolean("SWITCH_STATUS_LIST", false);
                            editor.apply();
                            if (SortStatus || ExternalFunctions.sortsearch > 0) {
                                if (ExternalFunctions.sortsearch < 5) {
                                    ApiService.getInstance(searchFeedPage.this, 1).postData(searchFeedPage.this, EnvConstants.APP_BASE_URL + "/search/product/?page=" + pageNo + "&sort=" + ExternalFunctions.sortsearch, ExternalFunctions.jsonObjsearch, SCREEN_NAME, "more");
                                } else {
                                    ApiService.getInstance(searchFeedPage.this, 1).getData(searchFeedPage.this, true, SCREEN_NAME, EnvConstants.URL_BUY + "/" + pageNo + "/an/?" + ExternalFunctions.strfilter, "more");
                                }
                            } else {

                                ApiService.getInstance(searchFeedPage.this, 1).postData(searchFeedPage.this, EnvConstants.APP_BASE_URL + "/search/product/?page=" + pageNo, ExternalFunctions.jsonObjsearch, SCREEN_NAME, "more");
                            }
                        } else {
                            if (SortStatus || ExternalFunctions.sortsearch > 0) {
                                if (ExternalFunctions.sortsearch < 5) {
                                    ApiService.getInstance(searchFeedPage.this, 1).postData(searchFeedPage.this, EnvConstants.APP_BASE_URL + "/search/product/?page=" + pageNo + "&sort=" + ExternalFunctions.sortsearch, ExternalFunctions.jsonObjsearch, SCREEN_NAME, "more");
                                } else {
                                    ApiService.getInstance(searchFeedPage.this, 1).getData(searchFeedPage.this, true, SCREEN_NAME, EnvConstants.URL_BUY + "/" + pageNo + "/an/", "more");
                                }
                            } else {
                                ApiService.getInstance(searchFeedPage.this, 1).postData(searchFeedPage.this, EnvConstants.APP_BASE_URL + "/search/product/?page=" + pageNo, ExternalFunctions.jsonObjsearch, SCREEN_NAME, "more");
                            }
                        }
                    } else {

                         CustomMessage.getInstance().CustomMessage(searchFeedPage.this, "Internet is not available!");
//
                    }

                }
            }
        });


        if (ExternalFunctions.strfilter.length() > 0) {
            if (CheckConnectivity.isNetworkAvailable(searchFeedPage.this)) {
                SortStatus = false;
                FilterStatus = true;
                progressBar.setVisibility(View.VISIBLE);
                pageNo = 1;
                GetFeeds();
                recyclerView.removeAllViews();
                feeds.clear();
                comment_scroll_position = 0;
            } else {
                progressBar.setVisibility(View.GONE);
                String query = "SELECT * FROM " + "FEED" + " where pageno = '" + pageNo + "'";
                Cursor cursordata = db.getData(query);
                String data = cursordata.getString(1).replace("z*p*", "'");
                //System.out.println("DATABASE: cursor data:" + cursordata.getString(1));
                SnapShot(data);

            }

        } else {
            if (CheckConnectivity.isNetworkAvailable(searchFeedPage.this)) {
                GetFeeds();
            } else {
                progressBar.setVisibility(View.GONE);
//                Cursor c = MyData.rawQuery("SELECT * FROM " + tableName + " where Category = '" +categoryex + "'" , null);
                String query = "SELECT * FROM " + "FEED" + " where pageno = '" + pageNo + "'";
                Cursor cursordata = db.getData(query);
                String data = cursordata.getString(1).replace("z*p*", "'");
                //System.out.println("DATABASE: cursor data:" + cursordata.getString(1));
                SnapShot(data);

            }
        }


        imggrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imggrid.setImageResource(R.drawable.gridview);
                imglist.setImageResource(R.drawable.greylistv);
                blswitch = true;


                recyclerView.setLayoutManager(mGridLayoutManager);
                recyclerView.removeAllViews();
                recyclerView.setAdapter(feedGridAdaptor);
                feedGridAdaptor.notifyDataSetChanged();
            }
        });
        imglist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imggrid.setImageResource(R.drawable.greygrid);
                imglist.setImageResource(R.drawable.listv);

                blswitch = false;

                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.removeAllViews();
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });


    }


    //    Get feeds function
//    -------------------------------------------------------------

    public void GetFeeds() {
        pageNo = 1;
        if (CheckConnectivity.isNetworkAvailable(searchFeedPage.this)) {
            gridloading = false;
         // Toast.makeText(getApplicationContext(), ExternalFunctions.jsonObjsearch.toString(), Toast.LENGTH_LONG).show();
            System.out.println("searnew" + ExternalFunctions.jsonObjsearch);
            if (!switchStatus) {
                editor.putBoolean("SWITCH_STATUS_LIST", false);
                editor.apply();
            }
            if (SortStatus) {
              //System.out.println("search by sort");
                ApiService.getInstance(searchFeedPage.this, 1).postData(searchFeedPage.this, EnvConstants.APP_BASE_URL + "/search/product/?page=" + pageNo + "&sort=" + ExternalFunctions.sortsearch, ExternalFunctions.jsonObjsearch, SCREEN_NAME, "feed");

            } else {
                System.out.println("search without sort"+ExternalFunctions.jsonObjsearch);
                ApiService.getInstance(this, 1).postData(this, EnvConstants.APP_BASE_URL + "/search/product/?page=" + pageNo, ExternalFunctions.jsonObjsearch, SCREEN_NAME, "feed");
            }
        }

    }


//    Refresh feed function
//    ------------------------------------------------------------


    public void refreshFeed() {
        pageNo = 1;
        refreshLayout.setRefreshing(true);
        comment_scroll_position = 0;
        if (CheckConnectivity.isNetworkAvailable(searchFeedPage.this)) {

            if (FilterStatus) {
                editor.putBoolean("SWITCH_STATUS_LIST", false);
                editor.apply();
                if (SortStatus || ExternalFunctions.sortsearch > 0) {
                    if (ExternalFunctions.sortsearch < 5) {
                        ApiService.getInstance(searchFeedPage.this, 1).postData(searchFeedPage.this, EnvConstants.APP_BASE_URL + "/search/product/?page=" + pageNo + "&sort=" + ExternalFunctions.sortsearch, ExternalFunctions.jsonObjsearch, SCREEN_NAME, "more");
                    } else {
                        ApiService.getInstance(searchFeedPage.this, 1).getData(searchFeedPage.this, true, SCREEN_NAME, EnvConstants.URL_BUY + "/" + pageNo + "/an/?" + ExternalFunctions.strfilter, "more");
                    }
                } else {

                    ApiService.getInstance(searchFeedPage.this, 1).postData(searchFeedPage.this, EnvConstants.APP_BASE_URL + "/search/product/?page=" + pageNo, ExternalFunctions.jsonObjsearch, SCREEN_NAME, "more");
                }
            } else {
                if (SortStatus || ExternalFunctions.sortsearch > 0) {
                    if (ExternalFunctions.sortsearch < 5) {
                        ApiService.getInstance(searchFeedPage.this, 1).postData(searchFeedPage.this, EnvConstants.APP_BASE_URL + "/search/product/?page=" + pageNo + "&sort=" + ExternalFunctions.sortsearch, ExternalFunctions.jsonObjsearch, SCREEN_NAME, "more");
                    } else {
                        ApiService.getInstance(searchFeedPage.this, 1).getData(searchFeedPage.this, true, SCREEN_NAME, EnvConstants.URL_BUY + "/" + pageNo + "/an/", "more");
                    }
                } else {
                    ApiService.getInstance(searchFeedPage.this, 1).postData(searchFeedPage.this, EnvConstants.APP_BASE_URL + "/search/product/?page=" + pageNo, ExternalFunctions.jsonObjsearch, SCREEN_NAME, "more");
                }
            }
        } else {
            Alerts.InternetAlert(searchFeedPage.this);
            refreshLayout.setRefreshing(false);
        }
    }


//    UI Change functions
//    -------------------------------------------------------------



    public void onLowMemory() {
        Runtime.getRuntime().gc();
        if (feeds.size() > 0) {
            feeds.clear();
            adapter.notifyDataSetChanged();
        }
        //System.gc();
        ExternalFunctions.deleteCache(searchFeedPage.this);
    }


//    Volley response handler
//    --------------------------------------------------------------

    @Override
    public void onResponseCallback(JSONObject response, String flag) {
        System.out.println("RESPONSE:" + response);
        if (flag.equals("feed")) {
            progressBar.setVisibility(View.GONE);

            if (recyclerView.getVisibility() != View.VISIBLE) {
                recyclerView.setVisibility(View.VISIBLE);
            }


            ArrayList<HomeFeedItem> temp = JsonParser.getInstance().parserFeed(response);

            if (temp != null) {

                gridloading = true;
                loading = false;
                topbar.setVisibility(View.VISIBLE);
                seperation.setVisibility(View.VISIBLE);
                feeds.clear();
                feeds.addAll(temp);
                refreshLayout.setRefreshing(false);
                if (temp.size() > 0) {
                    EmptyFeedLayout.setVisibility(View.GONE);
                    if (blswitch) {
                        recyclerView.removeAllViews();
                        // viewSwitcherImage.setImageResource(R.drawable.listfeed);
                        refreshLayout.setVisibility(View.VISIBLE);
                        recyclerView.setAdapter(feedGridAdaptor);                              // Setting the adapter to RecyclerView
                        recyclerView.setLayoutManager(mGridLayoutManager);
                        mGridLayoutManager.scrollToPosition(0);
                        mGridLayoutManager.scrollToPosition(comment_scroll_position);
//                    recyclerView.scrollToPosition(comment_scroll_position);
                        feedGridAdaptor.notifyDataSetChanged();

                    } else {
                        //  viewSwitcher.setTag("list");
                        refreshLayout.setVisibility(View.VISIBLE);
                        //viewSwitcherImage.setImageResource(R.drawable.gridfeed);
                        adapter = new searchAdapter(feeds, searchFeedPage.this);
                        recyclerView.setAdapter(adapter);                              // Setting the adapter to RecyclerView
                        recyclerView.setLayoutManager(mLayoutManager);
                        mLayoutManager.scrollToPosition(0);
                        mLayoutManager.scrollToPosition(comment_scroll_position);
                        adapter.notifyDataSetChanged();
                    }

//                    GetOverlay();
                } else {
                    EmptyFeedLayout.setVisibility(View.VISIBLE);
                }

            } else {
                 CustomMessage.getInstance().CustomMessage(searchFeedPage.this, "Unable to load feed.");

            }
        } else if (flag.equals("more")) {
            progressBar.setVisibility(View.GONE);
            //System.out.println("DATABASE:Inside more:" + db.getRecordsCount() + "__" + pageNo);

            ArrayList<HomeFeedItem> temp = JsonParser.getInstance().parserFeed(response);
            refreshLayout.setRefreshing(false);
            ////System.out.println("morez"+temp.size());
            if (temp != null && temp.size() > 0) {
                ////System.out.println("morez1"+temp.size());
                loading = false;
                if (feeds.size() > 0) {
                    feeds.remove(feeds.size() - 1);
                }
                feeds.addAll(temp);
                if (settings.getBoolean("SWITCH_STATUS_LIST", false)) {
                    feedGridAdaptor.notifyDataSetChanged();
                } else {
                    adapter.notifyDataSetChanged();
                }
            } else {
                feeds.remove(feeds.size() - 1);
                adapter.notifyDataSetChanged();
            }
        } else if (flag.equals("like")) {
            JSONObject likeResponse = JsonParser.getInstance().parserLike(response);
        } else if (flag.equals("unlike")) {
            JSONObject likeResponse = JsonParser.getInstance().parserLike(response);
        } else if (flag.equals("setuserkey")) {
            JSONObject resp = JsonParser.getInstance().parserJsonObject(response);
            try {
                ////System.out.println("appviral" + resp);
                String status = resp.getString("status");
                if (status.equals("success")) {

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public void onErrorCallback(VolleyError error, String flag) {
        if (flag.equals("feed")) {
            progressBar.setVisibility(View.GONE);
            refreshLayout.setRefreshing(false);
             CustomMessage.getInstance().CustomMessage(searchFeedPage.this, "Unable to load feed.");

        } else if (flag.equals("more")) {

        } else if (flag.equals("like")) {
        } else if (flag.equals("unlike")) {
        }
    }


//    Sort Dialoguebox
//    -----------------------------------------------------------

    protected void showSortDialog() {

        LayoutInflater layoutInflater = LayoutInflater.from(searchFeedPage.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog_search, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(searchFeedPage.this);
        alertDialogBuilder.setView(promptView);

        final TextView pHL = (TextView) promptView.findViewById(R.id.pHL);
        final TextView pLH = (TextView) promptView.findViewById(R.id.pLH);
        final TextView popularity = (TextView) promptView.findViewById(R.id.popularity);
        final TextView sort_discount = (TextView) promptView.findViewById(R.id.sort_discount);
        final TextView relavance = (TextView) promptView.findViewById(R.id.RLT);


        // create an alert dialog
        final AlertDialog alert = alertDialogBuilder.create();
        alert.show();
        switch (ExternalFunctions.sortsearch) {
            case 5:
                relavance.setTextColor(Color.parseColor("#ff7474"));
                break;
            case 2:
                pLH.setTextColor(Color.parseColor("#ff7474"));
                break;
            case 1:
                pHL.setTextColor(Color.parseColor("#ff7474"));

                break;

            case 3:
                popularity.setTextColor(Color.parseColor("#ff7474"));
                break;
            case 4:
                sort_discount.setTextColor(Color.parseColor("#ff7474"));
                break;

        }
        pHL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ExternalFunctions.sortsearch != 1) {
                    SortStatus = true;
                    ExternalFunctions.sortsearch = 1;
                    tv_sortby.setText(pHL.getText().toString());
                    progressBar.setVisibility(View.VISIBLE);
                    GetFeeds();
                    alert.dismiss();
                    comment_scroll_position = 0;
                    recyclerView.removeAllViews();
                    feeds.clear();
                    //tv_sortby.setTextColor(Color.parseColor("#ff7474"));

                } else {
                    SortStatus = false;
                    ExternalFunctions.sortsearch = 0;
                    pHL.setTextColor(Color.BLACK);
                    progressBar.setVisibility(View.VISIBLE);
                    GetFeeds();
                    alert.dismiss();
                    comment_scroll_position = 0;
                    recyclerView.removeAllViews();
                    feeds.clear();
                    //tv_sortby.setTextColor(Color.BLACK);
                }
            }
        });

        pLH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ExternalFunctions.sortsearch != 2) {
                    SortStatus = true;
                    ExternalFunctions.sortsearch = 2;
                    progressBar.setVisibility(View.VISIBLE);
                    tv_sortby.setText(pLH.getText().toString());
                    GetFeeds();
                    alert.dismiss();
                    comment_scroll_position = 0;
                    recyclerView.removeAllViews();
                    feeds.clear();
                    //tv_sortby.setTextColor(Color.parseColor("#ff7474"));

                } else {
                    SortStatus = false;
                    ExternalFunctions.sortsearch = 0;
                    pLH.setTextColor(Color.BLACK);
                    progressBar.setVisibility(View.VISIBLE);
                    GetFeeds();
                    alert.dismiss();
                    comment_scroll_position = 0;
                    recyclerView.removeAllViews();
                    feeds.clear();
                    //tv_sortby.setTextColor(Color.BLACK);
                }
            }
        });

        popularity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ExternalFunctions.sortsearch != 3) {
                    SortStatus = true;
                    ExternalFunctions.sortsearch = 3;
                    tv_sortby.setText(popularity.getText().toString());
                    progressBar.setVisibility(View.VISIBLE);
                    GetFeeds();
                    alert.dismiss();
                    comment_scroll_position = 0;
                    recyclerView.removeAllViews();
                    feeds.clear();
                    //tv_sortby.setTextColor(Color.parseColor("#ff7474"));

                } else {
                    SortStatus = false;
                    ExternalFunctions.sortsearch = 0;
                    popularity.setTextColor(Color.BLACK);
                    progressBar.setVisibility(View.VISIBLE);
                    GetFeeds();
                    alert.dismiss();
                    comment_scroll_position = 0;
                    recyclerView.removeAllViews();
                    feeds.clear();
                    //tv_sortby.setTextColor(Color.BLACK);
                }
            }
        });

        sort_discount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ExternalFunctions.sortsearch != 4) {
                    SortStatus = true;
                    ExternalFunctions.sortsearch = 4;
                    tv_sortby.setText(sort_discount.getText().toString());
                    progressBar.setVisibility(View.VISIBLE);
                    GetFeeds();
                    alert.dismiss();
                    comment_scroll_position = 0;
                    recyclerView.removeAllViews();
                    feeds.clear();
                    //tv_sortby.setTextColor(Color.parseColor("#ff7474"));

                } else {
                    SortStatus = false;
                    ExternalFunctions.sortsearch = 0;
                    sort_discount.setTextColor(Color.BLACK);
                    progressBar.setVisibility(View.VISIBLE);
                    GetFeeds();
                    alert.dismiss();
                    comment_scroll_position = 0;
                    recyclerView.removeAllViews();
                    feeds.clear();
                    //tv_sortby.setTextColor(Color.BLACK);
                }
            }
        });

        relavance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ExternalFunctions.sortsearch != 5) {
                    SortStatus = true;
//                FilterStatus = false;
                    ExternalFunctions.sortsearch = 5;
                    tv_sortby.setText(relavance.getText().toString());
                    progressBar.setVisibility(View.VISIBLE);
                    GetFeeds();
                    alert.dismiss();
                    comment_scroll_position = 5;
                    recyclerView.removeAllViews();
                    feeds.clear();
                    //tv_sortby.setTextColor(Color.parseColor("#ff7474"));

                } else {
                    SortStatus = false;
                    ExternalFunctions.sortsearch = 5;
                    relavance.setTextColor(Color.BLACK);
                    progressBar.setVisibility(View.VISIBLE);
                    GetFeeds();
                    alert.dismiss();
                    comment_scroll_position = 5;
                    recyclerView.removeAllViews();
                    feeds.clear();
                    //tv_sortby.setTextColor(Color.BLACK);

                }
            }
        });


    }


   //    ------------------------------------------------------

    @Override
    public void onResume() {
        super.onResume();
        ////System.out.println("APPSTATUS:" + "resume");
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }

        if (FeedGcmStatus) {
            ////System.out.println("INSIDE RESUME:"+ExternalFunctions.strfilter);
            feeds.clear();
            adapter.notifyDataSetChanged();
            // tvrefine.setTextColor(Color.parseColor("#ff7474"));
            GetFeeds();
            FeedGcmStatus = false;
        }


    }


    @Override
    public void onPause() {

        super.onPause();

            overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);

        Runtime.getRuntime().gc();
        int count1 = recyclerView.getChildCount();
        for (int i = 0; i < count1; i++) {
            RoundedImageView rv = (RoundedImageView) recyclerView.getChildAt(i).findViewById(R.id.user_image);
            ImageView v = (ImageView) recyclerView.getChildAt(i).findViewById(R.id.product_image);
            ImageView v1 = (ImageView) recyclerView.getChildAt(i).findViewById(R.id.feedgrid);
            if (rv != null) {
                if (rv.getDrawable() != null)
                    rv.getDrawable().setCallback(null);
            }
            if (v != null) {
                if (v.getDrawable() != null)
                    v.getDrawable().setCallback(null);
            }

            if (v1 != null) {
                if (v1.getDrawable() != null)
                    v1.getDrawable().setCallback(null);
            }
        }

    }

    private static final String PICASSO_CACHE = "picasso-cache";

    public static void clearCache(Context context) {
        final File cache = new File(
                context.getApplicationContext().getCacheDir(),
                PICASSO_CACHE);
        if (cache.exists()) {
            deleteFolder(cache);
        }
    }

    private static void deleteFolder(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles())
                deleteFolder(child);
        }
        fileOrDirectory.delete();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Runtime.getRuntime().gc();
        //// Runtime.getRuntime().gc();
        if (feeds.size() > 0) {
            feeds.clear();
            adapter.notifyDataSetChanged();
        }
        int count1 = recyclerView.getChildCount();
        for (int i = 0; i < count1; i++) {
            RoundedImageView rv = (RoundedImageView) recyclerView.getChildAt(i).findViewById(R.id.user_image);
            ImageView v = (ImageView) recyclerView.getChildAt(i).findViewById(R.id.product_image);
            ImageView v1 = (ImageView) recyclerView.getChildAt(i).findViewById(R.id.feedgrid);
            if (rv != null) {
                if (rv.getDrawable() != null)
                    rv.getDrawable().setCallback(null);
            }
            if (v != null) {
                if (v.getDrawable() != null)
                    v.getDrawable().setCallback(null);
            }

            if (v1 != null) {
                if (v1.getDrawable() != null)
                    v1.getDrawable().setCallback(null);
            }
        }

    }


//    @Override
//    public void onRestoreInstanceState(Bundle savedInstanceState) {
//        finish();
//        startActivity(getIntent());
//    }


    @Override
    protected void onRestart() {
        super.onRestart();
        //System.out.println("vzxcv");
        ExternalFunctions.bloverlay = false;
        if (!GetSharedValues.GetgcmId(searchFeedPage.this).equals("")) {
            ApiService.getInstance(searchFeedPage.this, 1).getData(searchFeedPage.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/?gcm_reg_id=" + GetSharedValues.GetgcmId(searchFeedPage.this), "session");
        } else {
            ApiService.getInstance(searchFeedPage.this, 1).getData(searchFeedPage.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/", "session");
        }
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
        ExternalFunctions.blapplysearch = false;
        if (activityname.contains("SplashScreen")) {
            finish();
        }
        else {
            Intent search = new Intent(this, searchnew.class);
            search.putExtra("activity", "HomePageNew");
            Runtime.getRuntime().gc();
            startActivity(search);

        }

    }


//   DRAWER ADAPTOR
//    -----------------------------------------------------------
//    ----------------------------------------------------------


//    Snapshot
//   --------------------------------------------------------

    public void SnapShot(String data) {
        JSONObject dataObject = null;
        try {
            dataObject = new JSONObject(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayList<HomeFeedItem> temp = JsonParser.getInstance().parserFeed(dataObject);

        gridloading = true;
        loading = false;
        topbar.setVisibility(View.VISIBLE);
        seperation.setVisibility(View.VISIBLE);
        feeds.clear();
        feeds.addAll(temp);
        refreshLayout.setRefreshing(false);
        if (temp.size() > 0) {
            EmptyFeedLayout.setVisibility(View.GONE);
            if (blswitch) {
                // viewSwitcher.setTag("grid");
                recyclerView.removeAllViews();
                //   viewSwitcherImage.setImageResource(R.drawable.listfeed);
                refreshLayout.setVisibility(View.VISIBLE);
                recyclerView.setAdapter(feedGridAdaptor);                              // Setting the adapter to RecyclerView
                recyclerView.setLayoutManager(mGridLayoutManager);
                mGridLayoutManager.scrollToPosition(0);
                mGridLayoutManager.scrollToPosition(comment_scroll_position);
//                    recyclerView.scrollToPosition(comment_scroll_position);
                feedGridAdaptor.notifyDataSetChanged();

            } else {
                //.setTag("list");
                refreshLayout.setVisibility(View.VISIBLE);
                // viewSwitcherImage.setImageResource(R.drawable.gridfeed);
//                adapter = new searchAdapter(feeds, searchFeedPage.this);
                recyclerView.setAdapter(adapter);                              // Setting the adapter to RecyclerView
                recyclerView.setLayoutManager(mLayoutManager);
//                    recyclerView.scrollToPosition(comment_scroll_position);
                mLayoutManager.scrollToPosition(0);
                mLayoutManager.scrollToPosition(comment_scroll_position);
                adapter.notifyDataSetChanged();
            }
        } else {
            EmptyFeedLayout.setVisibility(View.VISIBLE);
        }

    }


//    SnapShot for loadmore
//    -----------------------------------------------------

    public void SnapShotMore(String data) {
        JSONObject dataObject = null;
        try {
            dataObject = new JSONObject(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayList<HomeFeedItem> temp = JsonParser.getInstance().parserFeed(dataObject);
        refreshLayout.setRefreshing(false);
        ////System.out.println("morez"+temp.size());
        if (temp != null && temp.size() > 0) {
            ////System.out.println("morez1"+temp.size());
            loading = false;
            if (feeds.size() > 0) {
                feeds.remove(feeds.size() - 1);
            }
            feeds.addAll(temp);
            if (settings.getBoolean("SWITCH_STATUS_LIST", false)) {
                feedGridAdaptor.notifyDataSetChanged();
            } else {
                adapter.notifyDataSetChanged();
            }
        } else {
            feeds.remove(feeds.size() - 1);
            adapter.notifyDataSetChanged();
        }
    }
}




package activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.exceptions.CleverTapMetaDataNotFoundException;
import com.clevertap.android.sdk.exceptions.CleverTapPermissionsNotSatisfied;
import com.freshdesk.hotline.Hotline;
import com.freshdesk.hotline.HotlineCallbackStatus;
import com.freshdesk.hotline.UnreadCountCallback;
////import com.uxcam.UXCam;
import com.zapyle.zapyle.CheckConnectivity;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


import adapters.RecyclerViewProductAdapter;
import models.HomeFeedItem;
import network.ApiCommunication;
import network.ApiService;
import network.JsonParser;
import recievers.FeedReciever;
import services.FeedService;
import utils.CustomMessage;
import utils.ExternalFunctions;
import utils.GetSharedValues;

/**
 * Created by haseeb on 25/8/16.
 */
public class filtered extends ActionBarActivity implements ApiCommunication, FeedReciever.Receiver {

    public String START = "START";
    public String STOP = "STOP";

    RecyclerView recyclerView;
    RelativeLayout rlloadmore;
    boolean blendpage = false;

    CountDownTimer countDownTimer = null;


    TextView tv_sort, tv_refine, tv_newstories, tv_refresh, tvcount, tv_flash_title, tv_flash_sale_info, tv_flash_description, tv_flash_sale_timer;
    LinearLayout lv_emptylayout, lv_refineLayout, lv_flashSaleLayout, lv_flash_timr_layout;
    SwipeRefreshLayout refreshLayout;
    ImageView im_flashSaleBanner;
    FloatingActionButton floatingActionButton;
    ProgressBar progressBar;
    //gridloader;
    AppBarLayout appBarLayout;

    String Url = "";
    Integer pageNo = 1;
    FeedReciever mReceiver;
    Boolean refreshStatus = false, loading = false, SocketStatus = false, clickStatus = false;

    ArrayList<HomeFeedItem> feeds = new ArrayList<HomeFeedItem>();
    RecyclerViewProductAdapter p_adapter;
    Handler handler = new Handler();

    GridLayoutManager mGridLayoutManager;
    //WebSocketClient mWebSocketClient;
    JSONObject new_data = new JSONObject();
    private int lastVisibleItem, totalItemCount;
    TextView heading;
    JSONObject CollectionData = new JSONObject();
    TextView tvchatcount;
    Toolbar parent;
    String BaseUrl;
    String stime = "";
    String endtime = "";
    CleverTapAPI cleverTap;
    String SCREEN_NAME="FILTERED_FEED";
    String callActivity="";
    private Timer t;
    private int TimeCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtered_feed);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        // //UXCam.startWithKey("1dfb25141864376");
        stime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        try {
            cleverTap = CleverTapAPI.getInstance(getApplicationContext());
        } catch (CleverTapMetaDataNotFoundException e) {
            // thrown if you haven't specified your CleverTap Account ID or Token in your AndroidManifest.xml
        } catch (CleverTapPermissionsNotSatisfied e) {
            // thrown if you haven’t requested the required permissions in your AndroidManifest.xml
        }
        mReceiver = new FeedReciever(new Handler());
        mReceiver.setReceiver(this);

        ExternalFunctions.SortStatus = false;
        try {
            ExternalFunctions.FilteredUrl =  EnvConstants.URL_BUYNew +"/"+ getIntent().getStringExtra("ForwardUrl");
            System.out.println("FILTERED : 1 else :zzz" + ExternalFunctions.FilteredUrl);

        } catch (Exception e) {

        }

        InitialiseHeader();

        recyclerView = (RecyclerView) findViewById(R.id.main_recyclerview);

        tv_sort = (TextView) findViewById(R.id.sortby);
        tv_refine = (TextView) findViewById(R.id.refine);
        tv_newstories = (TextView) findViewById(R.id.new_stories);
        tv_refresh = (TextView) findViewById(R.id.refresh);

        lv_emptylayout = (LinearLayout) findViewById(R.id.emptyLayout);
        lv_refineLayout = (LinearLayout) findViewById(R.id.refineLayout);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingButton);
        progressBar = (ProgressBar) findViewById(R.id.buy_progress);
        //gridloader = (ProgressBar) findViewById(R.id.//gridloader);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);

        tv_newstories.setText(getString(R.string.uparrow) + " JUST IN");
        tv_newstories.setVisibility(View.GONE);

        rlloadmore = (RelativeLayout) findViewById(R.id.rlladmore);

        lv_flashSaleLayout = (LinearLayout) findViewById(R.id.FlashSaleLayout);
        lv_flash_timr_layout = (LinearLayout) findViewById(R.id.flash_sale_timer_layout);

        tv_flash_title = (TextView) findViewById(R.id.flash_sale_title);
        tv_flash_sale_info = (TextView) findViewById(R.id.flash_sale_info);
        tv_flash_description = (TextView) findViewById(R.id.flash_sale_description);
        tv_flash_sale_timer = (TextView) findViewById(R.id.flash_sale_timer);
        im_flashSaleBanner = (ImageView) findViewById(R.id.flash_sale_banner);


        lv_flashSaleLayout.setVisibility(View.GONE);


        mGridLayoutManager = new GridLayoutManager(this, 2);
        GetIntentData(0);

        tv_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.postDelayed(sortDialog, 100);
            }
        });

        tv_newstories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFeedItem feedObj = JsonParser.getInstance().parserHomefeedObject(new_data);
                feeds.add(0, feedObj);
                tv_newstories.setVisibility(View.GONE);
                p_adapter.notifyDataSetChanged();

            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.scrollToPosition(0);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        appBarLayout.setExpanded(true, false);
                        GetUrl();
                    }//public void run() {
                });
            }
        });

        tv_refine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callActivity="REFINE";
                Intent filter = new Intent(filtered.this, filter_activity.class);
                filter.putExtra("activity", "filtered");
                startActivity(filter);
                finish();
            }
        });

        UpdateFilterLayoutParams();
        callActivity="PRODUCT_PAGE";
        tv_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckConnectivity.isNetworkAvailable(filtered.this)) {
                    if (lv_emptylayout.getVisibility() == View.VISIBLE) {
                        lv_emptylayout.setVisibility(View.INVISIBLE);
                    }
                    GetData(Url, 0);
                } else {
                    CustomMessage.getInstance().CustomMessage(filtered.this, "Internet is not available!");
                }
            }
        });


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (CheckConnectivity.isNetworkAvailable(filtered.this)) {
                    GetUrl();
                    refreshLayout.setRefreshing(true);
                    refreshStatus = true;
                    if (lv_emptylayout.getVisibility() == View.VISIBLE) {
                        lv_emptylayout.setVisibility(View.INVISIBLE);
                    }
                    Url = Url.replace(String.valueOf(pageNo), "1");
                    pageNo = 1;
                    GetData(Url, 0);
                } else {
                    refreshLayout.setRefreshing(false);
                    refreshStatus = false;
                    CustomMessage.getInstance().CustomMessage(filtered.this, "Internet is not available!");
                }
            }
        });


        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);


                totalItemCount = mGridLayoutManager.getItemCount();
                lastVisibleItem = mGridLayoutManager.findLastVisibleItemPosition();

                if (!loading && lastVisibleItem == totalItemCount - 1) {
                    //gridloader.setVisibility(View.VISIBLE);
                    if (!blendpage)
                        rlloadmore.setVisibility(View.VISIBLE);
                    pageNo = pageNo + 1;

                    if (CheckConnectivity.isNetworkAvailable(getApplicationContext())) {
//                        loading = true;
                        GetIntentData(1);
                    } else {
                        CustomMessage.getInstance().CustomMessage(filtered.this, "Internet is not available!");

                    }

                }
            }
        });
    }

    private void UpdateFilterLayoutParams() {
        if (ExternalFunctions.FilterStatus) {
            //  if(!Url.toLowerCase().contains("campaign")) {
            tv_refine.setTextColor(Color.parseColor("#ff7477"));
            // }
        } else {
            tv_refine.setTextColor(Color.BLACK);
        }

        if (ExternalFunctions.SortStatus || ExternalFunctions.sort > 0) {
            tv_sort.setTextColor(Color.parseColor("#ff7477"));
        } else {
            tv_sort.setTextColor(Color.BLACK);
        }
    }


    Runnable sortDialog = new Runnable() {
        @Override
        public void run() {
            ShowSortDialog();
        }
    };


    private void InitialiseHeader() {


//        Drawer initialisation
//        -----------------------------------------------

//        mDrawerList = (ListView) findViewById(R.id.navList);
//        try {
//            mDrawerList.getLayoutParams().width = (int) (ExternalFunctions.displaymetrics(this).getInt("width") * (0.7));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        mDrawerList.setBackgroundColor(Color.BLACK);
//        mDrawerList.setDivider(null);
//        addDrawerItems();
//        setupDrawer();
//        if (mDrawerLayout.isDrawerVisible(Gravity.LEFT)) {
//            mDrawerLayout.closeDrawer(Gravity.LEFT);
//        }


//      Custom actionbar
//        ------------------------------------------


        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        View customView = getLayoutInflater().inflate(R.layout.home_actionbar_new, null);
        getSupportActionBar().setCustomView(customView);
        parent = (Toolbar) findViewById(R.id.toolbar);
        parent.setContentInsetsAbsolute(0, 0);

        heading = (TextView) findViewById(R.id.heading);


        RelativeLayout cart = (RelativeLayout) findViewById(R.id.home_cart);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                customToast = new CustomToast(HomePage.this, "Cart is clicked");
                callActivity="SHOP_CART";
                Intent cart = new Intent(getApplicationContext(), Cart.class);
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
            SharedPreferences CartSession = getSharedPreferences("CartSession",
                    Context.MODE_PRIVATE);
            if (CartSession.getInt("cartCount", 0) > 0) {
                tvcount.setVisibility(View.VISIBLE);
                tvcount.setText(String.valueOf(CartSession.getInt("cartCount", 0)));
            } else {
                tvcount.setVisibility(View.INVISIBLE);
            }
        }
        tvchatcount = (TextView) customView.findViewById(R.id.chatcount);
        tvchatcount.setVisibility(View.GONE);
        ImageView notifier_btn = (ImageView) findViewById(R.id.home_notification);
        notifier_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Hotline.showConversations(getApplicationContext());
//                if (GetSharedValues.LoginStatus(getApplicationContext())) {
//
//                    Intent notif = new Intent(getApplicationContext(), Notifications.class);
//                    notif.putExtra("activity", "FeedPage");
//                    startActivity(notif);
////                    finish();
//                } else {
//                    Alerts.loginAlert(getApplicationContext());
//                }
            }
        });

//
        ImageView hamburg = (ImageView) customView.findViewById(R.id.drawer_home);
        //hamburg.setImageResource(R.drawable.backnew);
        hamburg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ImageView search = (ImageView) customView.findViewById(R.id.home_search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                customToast = new CustomToast(HomePage.this, "Search is clicked");
                callActivity="SEARCH";
                Intent search = new Intent(getApplicationContext(), searchnew.class);
                search.putExtra("activity", "FeedPage");
                startActivity(search);
//                finish();
            }
        });
    }



    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
//        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        Hotline.getInstance(getApplicationContext()).getUnreadCountAsync(new UnreadCountCallback() {
            @Override
            public void onResult(HotlineCallbackStatus hotlineCallbackStatus, int unreadCount) {
                //Assuming "badgeTextView" is a text view to show the count on

                System.out.println("Hotline count:" + unreadCount);
                if (unreadCount > 0) {
                    tvchatcount.setText(String.valueOf(unreadCount));
                    tvchatcount.setVisibility(View.VISIBLE);
                } else {
                    tvchatcount.setVisibility(View.GONE);
                }


            }
        });

        super.onResume();
    }





    private void ShowSortDialog() {

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog_sort, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);

        final TextView pHL = (TextView) promptView.findViewById(R.id.pHL);
        final TextView pLH = (TextView) promptView.findViewById(R.id.pLH);
        final TextView popularity = (TextView) promptView.findViewById(R.id.popularity);
        final TextView sort_discount = (TextView) promptView.findViewById(R.id.sort_discount);
        final TextView whatsnew = (TextView) promptView.findViewById(R.id.whatsnew);

        System.out.println("urlhit : " + ExternalFunctions.sort);
        // create an alert dialog
        final AlertDialog alert = alertDialogBuilder.create();
        alert.show();
        switch (ExternalFunctions.sort) {
            case 1:
                pHL.setTextColor(Color.parseColor("#ff7477"));
                break;
            case 2:
                pLH.setTextColor(Color.parseColor("#ff7477"));
                break;
            case 3:
                popularity.setTextColor(Color.parseColor("#ff7477"));
                break;
            case 4:
                sort_discount.setTextColor(Color.parseColor("#ff7477"));
                break;
            case 5:
                whatsnew.setTextColor(Color.parseColor("#ff7477"));
                break;
        }
        pHL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pageNo = 1;
                if (ExternalFunctions.sort != 1) {
                    ExternalFunctions.SortStatus = true;
                    ExternalFunctions.sort = 1;
                    tv_sort.setTextColor(Color.parseColor("#ff7477"));


                    recyclerView.removeAllViews();
                    if (Url.contains("sort")) {
                        if (Url.contains("?sort")) {
                            Url = Url.substring(0, Url.length() - 7);
                            Url = Url + "?sort=" + 1;
                        } else if (Url.contains("?")) {
                            Url = Url.substring(0, Url.length() - 7);
                            if (Url.substring(Url.length() - 1).equals("&")) {
                                Url = Url + "sort=" + 1;
                            } else {
                                Url = Url + "&sort=" + 1;
                            }

                        } else {
                            Url = Url.substring(0, Url.length() - 7);
                            Url = Url + "?sort=" + 1;
                        }
                    } else {
                        if (Url.contains("?")) {
                            ExternalFunctions.sort = 1;
                            if (Url.substring(Url.length() - 1).equals("&")) {
                                Url = Url + "sort=" + 1;
                            } else {
                                Url = Url + "&sort=" + 1;
                            }
                        } else {
                            ExternalFunctions.sort = 1;
                            Url = Url + "?sort=" + 1;
                        }
                    }

                    recyclerView.removeAllViews();
                    GetUrl();
                    GetData(Url, 0);
                    alert.dismiss();

                } else {
                    ExternalFunctions.SortStatus = false;
                    if (Url.contains("sort")) {
                        Url = Url.substring(0, Url.length() - 7);
                    }
                    ExternalFunctions.sort = 0;
                    pHL.setTextColor(Color.BLACK);
                    GetUrl();
                    GetData(Url, 0);
                    alert.dismiss();
                    recyclerView.removeAllViews();
                    tv_sort.setTextColor(Color.BLACK);
                }
            }
        });

        pLH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageNo = 1;
                if (ExternalFunctions.sort != 2) {
                    ExternalFunctions.SortStatus = true;

                    if (Url.contains("sort")) {
                        if (Url.contains("?sort")) {
                            Url = Url.substring(0, Url.length() - 7);
                            Url = Url + "?sort=" + 2;
                        } else if (Url.contains("?")) {
                            Url = Url.substring(0, Url.length() - 7);
                            if (Url.substring(Url.length() - 1).equals("&")) {
                                Url = Url + "sort=" + 2;
                            } else {
                                Url = Url + "&sort=" + 2;
                            }
                        } else {
                            Url = Url.substring(0, Url.length() - 7);
                            Url = Url + "?sort=" + 2;
                        }
                    } else {
                        if (Url.contains("?")) {
                            ExternalFunctions.sort = 2;
                            if (Url.substring(Url.length() - 1).equals("&")) {
                                Url = Url + "sort=" + 2;
                            } else {
                                Url = Url + "&sort=" + 2;
                            }
                        } else {
                            ExternalFunctions.sort = 2;
                            Url = Url + "?sort=" + 2;
                        }
                    }

                    ExternalFunctions.sort = 2;
                    GetUrl();
                    GetData(Url, 0);
                    alert.dismiss();
                    tv_sort.setTextColor(Color.parseColor("#ff7477"));

                } else {
                    ExternalFunctions.SortStatus = false;
                    if (Url.contains("sort")) {
                        Url = Url.substring(0, Url.length() - 7);
                    }
                    ExternalFunctions.sort = 0;
                    pLH.setTextColor(Color.BLACK);
                    GetUrl();
                    GetData(Url, 0);
                    alert.dismiss();
                    feeds.clear();
                    tv_sort.setTextColor(Color.BLACK);
                }
            }
        });

        popularity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageNo = 1;
                if (ExternalFunctions.sort != 3) {
                    ExternalFunctions.SortStatus = true;

                    if (Url.contains("sort")) {
                        if (Url.contains("?sort")) {
                            Url = Url.substring(0, Url.length() - 7);
                            Url = Url + "?sort=" + 3;
                        } else if (Url.contains("?")) {
                            Url = Url.substring(0, Url.length() - 7);
                            if (Url.substring(Url.length() - 1).equals("&")) {
                                Url = Url + "sort=" + 3;
                            } else {
                                Url = Url + "&sort=" + 3;
                            }
                        } else {
                            Url = Url.substring(0, Url.length() - 7);
                            Url = Url + "?sort=" + 3;
                        }
                    } else {
                        if (Url.contains("?")) {
                            ExternalFunctions.sort = 3;
                            if (Url.substring(Url.length() - 1).equals("&")) {
                                Url = Url + "sort=" + 3;
                            } else {
                                Url = Url + "&sort=" + 3;
                            }
                        } else {
                            ExternalFunctions.sort = 3;
                            Url = Url + "?sort=" + 3;
                        }
                    }

                    ExternalFunctions.sort = 3;
                    GetUrl();
                    GetData(Url, 0);
                    alert.dismiss();
                    feeds.clear();
                    tv_sort.setTextColor(Color.parseColor("#ff7477"));

                } else {
                    ExternalFunctions.SortStatus = false;
                    if (Url.contains("sort")) {
                        Url = Url.substring(0, Url.length() - 7);
                    }
                    ExternalFunctions.sort = 0;
                    popularity.setTextColor(Color.BLACK);
                    GetUrl();
                    GetData(Url, 0);
                    alert.dismiss();
                    feeds.clear();
                    tv_sort.setTextColor(Color.BLACK);
                }
            }
        });

        sort_discount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageNo = 1;
                if (ExternalFunctions.sort != 4) {
                    ExternalFunctions.SortStatus = true;
                    if (Url.contains("sort")) {
                        if (Url.contains("?sort")) {
                            Url = Url.substring(0, Url.length() - 7);
                            Url = Url + "?sort=" + 4;
                        } else if (Url.contains("?")) {
                            Url = Url.substring(0, Url.length() - 7);
                            if (Url.substring(Url.length() - 1).equals("&")) {
                                Url = Url + "sort=" + 4;
                            } else {
                                Url = Url + "&sort=" + 4;
                            }
                        } else {
                            Url = Url.substring(0, Url.length() - 7);
                            Url = Url + "?sort=" + 4;
                        }
                    } else {
                        if (Url.contains("?")) {
                            ExternalFunctions.sort = 4;
                            if (Url.substring(Url.length() - 1).equals("&")) {
                                Url = Url + "sort=" + 4;
                            } else {
                                Url = Url + "&sort=" + 4;
                            }
                        } else {
                            ExternalFunctions.sort = 4;
                            Url = Url + "?sort=" + 4;
                        }
                    }
                    GetUrl();
                    GetData(Url, 0);
                    ExternalFunctions.sort = 4;
                    alert.dismiss();
                    tv_sort.setTextColor(Color.parseColor("#ff7477"));

                } else {
                    ExternalFunctions.SortStatus = false;
                    if (Url.contains("sort")) {
                        Url = Url.substring(0, Url.length() - 7);
                    }

                    ExternalFunctions.sort = 0;
                    sort_discount.setTextColor(Color.BLACK);
                    GetUrl();
                    GetData(Url, 0);
                    alert.dismiss();
                    feeds.clear();
                    tv_sort.setTextColor(Color.BLACK);
                }
            }
        });

        whatsnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageNo = 1;
                if (ExternalFunctions.sort != 5) {
                    ExternalFunctions.SortStatus = true;
                    if (Url.contains("sort")) {
                        if (Url.contains("?sort")) {
                            Url = Url.substring(0, Url.length() - 7);
                            Url = Url + "?sort=" + 5;
                        } else if (Url.contains("?")) {
                            Url = Url.substring(0, Url.length() - 7);
                            if (Url.substring(Url.length() - 1).equals("&")) {
                                Url = Url + "sort=" + 5;
                            } else {
                                Url = Url + "&sort=" + 5;
                            }
                        } else {
                            Url = Url.substring(0, Url.length() - 7);
                            Url = Url + "?sort=" + 5;
                        }
                    } else {
                        if (Url.contains("?")) {
                            ExternalFunctions.sort = 5;
                            if (Url.substring(Url.length() - 1).equals("&")) {
                                Url = Url + "sort=" + 5;
                            } else {
                                Url = Url + "&sort=" + 5;
                            }
                        } else {
                            ExternalFunctions.sort = 5;
                            Url = Url + "?sort=" + 5;
                        }
                    }

                    ExternalFunctions.sort = 5;
                    GetUrl();
                    GetData(Url, 0);
                    alert.dismiss();
                    feeds.clear();
                    tv_sort.setTextColor(Color.parseColor("#ff7477"));

                } else {
                    ExternalFunctions.SortStatus = false;
                    if (Url.contains("sort")) {
                        Url = Url.substring(0, Url.length() - 7);
                    }

                    ExternalFunctions.sort = 0;
                    whatsnew.setTextColor(Color.BLACK);
                    GetUrl();
                    GetData(Url, 0);
                    alert.dismiss();
                    feeds.clear();
                    tv_sort.setTextColor(Color.BLACK);

                }
            }
        });
    }

    private void GetIntentData(int status) {
        String strextra="";
        if (ExternalFunctions.strOverlayurl.length() > 1) {
            System.out.println("FILTERED : 1 if");
            try {
                String filterData = "";
                String filterDataFirst = "";
                String[] filterDataList = ExternalFunctions.FilteredUrl.split("\\?");
                if (filterDataList.length > 1) {
                    filterDataFirst = ExternalFunctions.FilteredUrl.split("\\?")[0];
                    System.out.println("aaaa 1: "+filterDataFirst);
                    try {
                        strextra = filterDataFirst.split("/an/")[1];
                        System.out.println("aaaa : " + strextra);
                    }catch(Exception e){
                        strextra="";
                    }
                    filterData = ExternalFunctions.FilteredUrl.split("\\?")[1];
                    if (filterData.length() > 0 & filterData != null) {
                        if (!ExternalFunctions.FilterStatus) {
                            ExternalFunctions.FilterStatus = true;
                            ExternalFunctions.strfilter = filterData;
                        }
                        filterData = filterData + "&";
                    }
                } else {
                    filterData = "";
                }
                System.out.println("FILTERED : 1 if try : " + filterData);


                if (ExternalFunctions.FilterStatus && ExternalFunctions.SortStatus && ExternalFunctions.sort > 0 && ExternalFunctions.strfilter.length() > 1) {
                    Url = EnvConstants.URL_BUY + "/" + pageNo + "/an/"+strextra+"?" + ExternalFunctions.strfilter + "&" + "sort=" + ExternalFunctions.sort;
                } else if (ExternalFunctions.FilterStatus && !ExternalFunctions.SortStatus && ExternalFunctions.strfilter.length() > 1) {
                    Url = EnvConstants.URL_BUY + "/" + pageNo + "/an/"+strextra+"?" + ExternalFunctions.strfilter;
                } else if (ExternalFunctions.SortStatus && !ExternalFunctions.FilterStatus) {
                    Url = EnvConstants.URL_BUY + "/" + pageNo + "/an/"+strextra+"?" + filterData + "sort=" + ExternalFunctions.sort;
                } else {
                    Url = EnvConstants.URL_BUY + "/" + pageNo + "/an/"+strextra+"?"  + filterData;
                }

                ExternalFunctions.FilteredUrl = ExternalFunctions.strOverlayurl;
                ExternalFunctions.strOverlayurl = "";
                System.out.println("FILTERED : 1 if try last : " + ExternalFunctions.FilteredUrl + "___" + ExternalFunctions.strOverlayurl);
                GetData(Url, status);

            } catch (Exception e) {
                Url = EnvConstants.URL_BUY + "/" + pageNo + "/an/";
                GetData(Url, status);
            }
            BaseUrl = Url;
            UpdateFilterLayoutParams();
        } else {

            System.out.println("FILTERED : 1 else : " + ExternalFunctions.FilteredUrl);
            try {

                if (ExternalFunctions.strfilter.length() > 0) {

                    if (ExternalFunctions.FilterStatus && ExternalFunctions.SortStatus && ExternalFunctions.sort > 0 && ExternalFunctions.strfilter.length() > 1) {
                        Url = EnvConstants.URL_BUY + "/" + pageNo + "/an/"+strextra+"?" + ExternalFunctions.strfilter + "&" + "sort=" + ExternalFunctions.sort;
                    } else if (ExternalFunctions.FilterStatus && !ExternalFunctions.SortStatus && ExternalFunctions.strfilter.length() > 1) {
                        Url = EnvConstants.URL_BUY + "/" + pageNo + "/an/"+strextra+"?" + ExternalFunctions.strfilter;
                    } else if (ExternalFunctions.SortStatus && !ExternalFunctions.FilterStatus) {
                        Url = EnvConstants.URL_BUY + "/" + pageNo + "/an/"+strextra+"?" + ExternalFunctions.strfilter + "&" + "sort=" + ExternalFunctions.sort;
                    } else {
                        Url = EnvConstants.URL_BUY + "/" + pageNo + "/an/"+strextra+"?" + ExternalFunctions.strfilter;
                    }
                    BaseUrl = Url;
                    GetData(Url, status);
                    System.out.println("FILTERED : 1 else try last");
                } else {
                    String filterData = "";
                    String filterDataFirst = "";
                    String[] filterDataList = ExternalFunctions.FilteredUrl.split("\\?");

                    if (filterDataList.length > 1) {
                        filterDataFirst = ExternalFunctions.FilteredUrl.split("\\?")[0];
                        System.out.println("aaaa 1: "+filterDataFirst);
                        try {
                            strextra = filterDataFirst.split("/an/")[1];
                            System.out.println("aaaa : " + strextra);
                        }catch(Exception e){
                            strextra="";
                        }
                        filterData = ExternalFunctions.FilteredUrl.split("\\?")[1];
                        if (filterData.length() > 0 & filterData != null) {
                            if (!ExternalFunctions.FilterStatus) {
                                //ExternalFunctions.FilterStatus = true;
                                ExternalFunctions.strfilter = filterData;
                            }
                            filterData = filterData + "&";
                        }
                    } else {
                        filterData = "";
                    }
                    System.out.println("FILTERED : 1 else try : " + filterData);
                    if (ExternalFunctions.FilterStatus && ExternalFunctions.SortStatus && ExternalFunctions.sort > 0 && ExternalFunctions.strfilter.length() > 1) {
                        Url = EnvConstants.URL_BUY + "/" + pageNo + "/an/"+strextra+"?" + filterData + "&" + "sort=" + ExternalFunctions.sort;
                    } else if (ExternalFunctions.FilterStatus && !ExternalFunctions.SortStatus && ExternalFunctions.strfilter.length() > 1) {
                        Url = EnvConstants.URL_BUY + "/" + pageNo + "/an/"+strextra+"?" + filterData;
                    } else if (ExternalFunctions.SortStatus && !ExternalFunctions.FilterStatus) {
                        Url = EnvConstants.URL_BUY + "/" + pageNo + "/an/"+strextra+"?"+ filterData + "&" + "sort=" + ExternalFunctions.sort;
                    } else {
                        Url = EnvConstants.URL_BUY + "/" + pageNo + "/an/"+strextra+"?" + filterData;
                    }
                    BaseUrl = Url;
                    GetData(Url, status);
                    System.out.println("FILTERED : 1 else try last");
                }
            } catch (Exception e) {
                System.out.println("FILTERED : 1 else catch : " + e);
                if (ExternalFunctions.FilterStatus && ExternalFunctions.SortStatus && ExternalFunctions.sort > 0 && ExternalFunctions.strfilter.length() > 1) {
                    Url = EnvConstants.URL_BUY + "/" + pageNo + "/an/"+strextra+"?" + ExternalFunctions.strfilter + "&" + "sort=" + ExternalFunctions.sort;
                } else if (ExternalFunctions.FilterStatus && !ExternalFunctions.SortStatus && ExternalFunctions.strfilter.length() > 1) {
                    Url = EnvConstants.URL_BUY + "/" + pageNo + "/an/"+strextra+"?"+ ExternalFunctions.strfilter;
                } else if (ExternalFunctions.SortStatus && !ExternalFunctions.FilterStatus) {
                    Url = EnvConstants.URL_BUY + "/" + pageNo + "/an/"+strextra+"?" + "sort=" + ExternalFunctions.sort;
                } else {
                    Url = EnvConstants.URL_BUY + "/" + pageNo + "/an/";
                }
                BaseUrl = Url;
                GetData(Url, status);
            }

            UpdateFilterLayoutParams();
        }
    }


    private void GetData(String Url, int status) {
        System.out.println(status + "aqw3" + Url);
        if (!refreshStatus && pageNo == 1) {
            handleProgress(START);
        }
        if (!loading) {
            if (status == 0) {
                loading = true;
                Intent service = new Intent(Intent.ACTION_SYNC, null, this, FeedService.class);
                service.putExtra("url", Url);
                service.putExtra("receiver", mReceiver);
                service.putExtra("Method", "product");
                service.putExtra("ScreenName", "FEEDPAGE");
                service.putExtra("PageNo", 1);
                startService(service);
            } else {
                loading = true;
                Intent service = new Intent(Intent.ACTION_SYNC, null, this, FeedService.class);
                service.putExtra("url", Url);
                service.putExtra("receiver", mReceiver);
                service.putExtra("Method", "product");
                service.putExtra("ScreenName", "FEEDPAGE");
                service.putExtra("PageNo", pageNo);
                startService(service);
            }
        }
    }


    //    Network requests
//    -------------------------------------------------------

    @Override
    public void onResponseCallback(JSONObject response, String flag) {

    }

    @Override
    public void onErrorCallback(VolleyError error, String flag) {

    }

    //    Reciever from service
//    --------------------------------------------------------
    @Override
    public void onReceiveResult(int resultCode, final Bundle resultData) {
        System.out.println("DATAAAAAAA : " + resultData);

        switch (resultCode) {
            case FeedService.STATUS_RUNNING:
                System.out.println("SERVICE RUNNING");
                break;


            case FeedService.STATUS_FINISHED:
                String method = resultData.getString("Method");
                String collectiondata = resultData.getString("Collection");
                // System.out.println("COLLECTIONADAT : " + collectiondata);

                switch (method) {
                    case "product":
                        rlloadmore.setVisibility(View.GONE);
                        //  System.out.println("COLLECTIONADAT:dddd " + resultData.getSerializable("DATALIST"));
                        handleProgress(STOP);
                        refreshLayout.setRefreshing(false);
                        refreshStatus = false;
                        feeds.clear();
                        feeds = (ArrayList<HomeFeedItem>) resultData.getSerializable("DATALIST");
                        if (collectiondata != null) {
                            try {
                                CollectionData = new JSONObject(collectiondata);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                CollectionData = null;
                            }
                        } else {
                            CollectionData = null;
                        }

                        if (CollectionData != null) {
                            System.out.println("COLLECTIONADAT : not null");
                            lv_flashSaleLayout.setVisibility(View.VISIBLE);
                            ShowCollectionData(CollectionData);
                        } else {
                            System.out.println("COLLECTIONADAT : null");
                            heading.setText("Buy");
                            parent.setVisibility(View.GONE);
                            appBarLayout.setVisibility(View.GONE);
                            lv_flashSaleLayout.setVisibility(View.GONE);
                        }


                        if (feeds != null) {
                            System.out.println("INSIDE DATA: first if ! null");
                            if (feeds.size() > 0) {
                                loading = false;
                                System.out.println("INSIDE DATA: secoonf if ! null");
                                recyclerView.setVisibility(View.VISIBLE);
                                lv_emptylayout.setVisibility(View.GONE);
                                p_adapter = new RecyclerViewProductAdapter(feeds, this, ExternalFunctions.DiffParam, recyclerView, "filtered");
                                recyclerView.removeAllViews();
                                recyclerView.setAdapter(p_adapter);                              // Setting the adapter to RecyclerView
                                recyclerView.setLayoutManager(mGridLayoutManager);
                                p_adapter.notifyDataSetChanged();
                                if (ExternalFunctions.strOverlayurl.length() == 0) {
                                    String overlay_URL = EnvConstants.APP_BASE_URL + "/marketing/overlay/feed";
                                    Intent service = new Intent(Intent.ACTION_SYNC, null, this, FeedService.class);
                                    service.putExtra("url", overlay_URL);
                                    service.putExtra("receiver", mReceiver);
                                    service.putExtra("Method", "Overlay");
                                    service.putExtra("ScreenName", "FEEDPAGE");
                                    service.putExtra("PageNo", 1);
                                    startService(service);
                                }
                                ExternalFunctions.strOverlayurl = "";

                            } else {
                                System.out.println("INSIDE DATA: sec else ! null");
                                recyclerView.setVisibility(View.INVISIBLE);
                                lv_emptylayout.setVisibility(View.VISIBLE);
                                floatingActionButton.setVisibility(View.GONE);
                            }

                        } else {
                            System.out.println("INSIDE DATA: first else ! null");
                            recyclerView.setVisibility(View.INVISIBLE);
                            lv_emptylayout.setVisibility(View.VISIBLE);
                            lv_refineLayout.setVisibility(View.INVISIBLE);
                            CustomMessage.getInstance().CustomMessage(this, "Unable to load feed");
                        }

                        break;

                    case "ProductMore":
                        ArrayList<HomeFeedItem> data = (ArrayList<HomeFeedItem>) resultData.getSerializable("DATALIST");
                        if (data.size() > 0) {
                            loading = false;
                        }
                        //gridloader.setVisibility(View.GONE);
                        rlloadmore.setVisibility(View.GONE);
                        feeds.addAll((ArrayList<HomeFeedItem>) resultData.getSerializable("DATALIST"));
                        p_adapter.notifyDataSetChanged();
                        break;

                    case "Overlay":
                        System.out.println("DATALIST OVERLAY : " + resultData.getString("DATALIST"));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    JSONObject resp = new JSONObject(resultData.getString("DATALIST"));
                                    ExternalFunctions.strOverlayactivity = "";
                                    ExternalFunctions.bloverlay = false;

                                    ExternalFunctions.strOverlayurl = "";
                                    //System.out.println("INSIDE getoverlay");
                                    //System.out.println("overlay" + resp);
                                    String status = resp.getString("status");
                                    if (!status.equals("error")) {
                                        JSONObject data = resp.getJSONObject("data");
                                        JSONObject action=data.getJSONObject("action");
                                        final String str_uri = action.getString("target");

                                        final String strimage = data.getString("image");
                                        final boolean bl_close = data.getBoolean("can_close");
                                        final String strtilte = data.getString("title");

                                        final String str_description = data.getString("description");
                                        String actname = action.getString("action_type");
                                        final String activityname =  actname;
                                        boolean bl_active = data.getBoolean("active");
                                        final int intDelay = data.getInt("delay");
                                        final boolean bl_fullscreen = data.getBoolean("full_screen");
                                        final String strbutton = data.getString("cta_text");
                                        // ExternalFunctions.strOverlayactivity = actname;
                                        ExternalFunctions.bloverlay = true;

                                        // ExternalFunctions.strOverlayurl = str_uri;
                                        //UXCam.startWithKey("1dfb25141864376");
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
                                                            System.out.println(intDelay + "INSIDE getoverlay1" + TimeCounter);
                                                            if (TimeCounter == intDelay) {

                                                                t.cancel();
                                                                try {
//                                                                    ExternalFunctions.showOverlay(MainFeed.this, "", "", "", activityname, bl_fullscreen, bl_close, strimage);
                                                                    ExternalFunctions.showOverlay(filtered.this, strtilte, str_description, strbutton, activityname, bl_fullscreen, bl_close, strimage,str_uri);

                                                                } catch (Exception e) {

                                                                }
                                                            }


                                                        }
                                                    });

                                                }
                                            }, 500, 500);
                                        }
                                        else{
                                            ExternalFunctions.showOverlay(filtered.this, strtilte, str_description, strbutton, activityname, bl_fullscreen, bl_close, strimage,str_uri);
                                        }


                                    } else {
                                        ExternalFunctions.strOverlayactivity = "";
                                        ExternalFunctions.strOverlayurl = "";
                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        });
                        break;

                }
                break;

            case FeedService.STATUS_ERROR:
                loading = false;
                //gridloader.setVisibility(View.GONE);
                String mtd = resultData.getString("Method");
                switch (mtd) {

                    case "Closet":
                        CustomMessage.getInstance().CustomMessage(this, "Unable to load feed");
                        break;

                    case "product":
                        CustomMessage.getInstance().CustomMessage(this, "Unable to load feed");
                        break;
                    case "ClosetPageFinish":
                        blendpage = true;
                        rlloadmore.setVisibility(View.GONE);
                        // CustomMessage.getInstance().CustomMessage(this, "Unable to load feed");
                        break;
                    case "ProductPageFinish":
                        blendpage = true;
                        rlloadmore.setVisibility(View.GONE);
                        // CustomMessage.getInstance().CustomMessage(this, "Unable to load feed");
                        break;
                }

        }
    }

    private void ShowCollectionData(JSONObject collectionData) {
        if (!collectionData.optString("title").isEmpty() || collectionData.optString("title") != null) {
            tv_flash_title.setText(collectionData.optString("title"));
        } else {
            tv_flash_title.setVisibility(View.GONE);
        }
        SharedPreferences FeedSession = getSharedPreferences("FeedSession",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor FeedSessioneditor = FeedSession.edit();
        FeedSessioneditor.putString("FilteredHeading", collectionData.optString("title").toUpperCase());
        FeedSessioneditor.apply();


        String headText = FeedSession.getString("FilteredHeading", "Buy");
        System.out.println("zza" + headText);


        if (headText.length() <= 0 || headText.isEmpty() || headText.equals(null)) {
            heading.setText("Buy");
        } else {
            heading.setText(headText);
        }
        if (collectionData.has("sale_info")) {
            if (!collectionData.optString("sale_info").isEmpty() || collectionData.optString("sale_info") != null) {
                tv_flash_sale_info.setText(collectionData.optString("sale_info"));
            } else {
                tv_flash_sale_info.setVisibility(View.GONE);
            }
        } else {
            tv_flash_sale_info.setVisibility(View.GONE);
        }
        if (!collectionData.optString("description").isEmpty()) {
            tv_flash_description.setText(collectionData.optString("description"));
        } else {
            tv_flash_description.setVisibility(View.GONE);
        }
        String image="";
        if(collectionData.optString("image").contains("https:")|| collectionData.optString("image").contains("http:")){
            image=collectionData.optString("image");
        }else{
            image= EnvConstants.APP_MEDIA_URL + collectionData.optString("image");
        }

        System.out.println("gifdata"+image);
        if (collectionData.has("image")) {
            if (!collectionData.isNull("image")) {
                Glide.with(filtered.this)
                        .load(image)
                        .fitCenter()
                        .placeholder(R.drawable.playholderscreen)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(im_flashSaleBanner);
            } else {
                im_flashSaleBanner.setVisibility(View.GONE);
            }
        } else {
            im_flashSaleBanner.setVisibility(View.GONE);
        }

        if (collectionData.optBoolean("show_timer")) {
            lv_flash_timr_layout.setVisibility(View.VISIBLE);

            long flashSaleTime = collectionData.optLong("end_timestamp") - (System.currentTimeMillis() / 1000);
            countDownTimer = new CountDownTimer(flashSaleTime * 1000 + 1000, 1000) {

                public void onTick(long millisUntilFinished) {
                    long millis = millisUntilFinished;
                    String hms = String.format("%02d:%02d:%02d:%02d",
                            TimeUnit.MILLISECONDS.toDays(millis),
                            TimeUnit.MILLISECONDS.toHours(millis) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millis)),
                            TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                            TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

                    tv_flash_sale_timer.setText(hms);
                }

                public void onFinish() {
                    showFlashSalePop();

                }

            }.start();
        } else {
            lv_flash_timr_layout.setVisibility(View.GONE);
        }
    }


    public void showFlashSalePop() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog_flash_sale_pop, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);


        final AlertDialog alert = alertDialogBuilder.create();
        alert.show();
        alert.setCancelable(true);

        TextView okay = (TextView) promptView.findViewById(R.id.okay);
        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
                lv_flash_timr_layout.setVisibility(View.GONE);
            }
        });


    }

    private void handleProgress(final String event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (event) {
                    case "START":
                        recyclerView.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.VISIBLE);
                        appBarLayout.setVisibility(View.GONE);
                        parent.setVisibility(View.GONE);
                        break;

                    case "STOP":
                        parent.setVisibility(View.VISIBLE);
                        appBarLayout.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        break;

                    default:
                        break;
                }
            }
        });
    }


    //    Sockect connection functions
//    ----------------------------------------------------------------
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ExternalFunctions.SortStatus = false;
        ExternalFunctions.sort = 0;
        Runtime.getRuntime().gc();
        if (feeds.size() > 0) {
            feeds.clear();
        }
        int count1 = recyclerView.getChildCount();
        for (int i = 0; i < count1; i++) {
            ImageView v1 = (ImageView) recyclerView.getChildAt(i).findViewById(R.id.feedgrid);
            if (v1 != null) {
                if (v1.getDrawable() != null)
                    v1.getDrawable().setCallback(null);
                Glide.clear(v1);
            }
        }

        feeds.clear();
        ExternalFunctions.FilteredString = "";
        ExternalFunctions.strOverlayurl = "";
    }

    @Override
    protected void onStop() {
        super.onStop();
        endtime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        System.out.println("first0"+ExternalFunctions.prevActivity);
        if(SCREEN_NAME.equals(ExternalFunctions.prevActivity)){
            ExternalFunctions.prevActivity=callActivity;
        }
        System.out.println("first1"+ExternalFunctions.prevActivity);
        HashMap<String, Object> page_change = new HashMap<String, Object>();
        page_change.put("new_page", SCREEN_NAME);
        page_change.put("old_page", ExternalFunctions.prevActivity);
        page_change.put("page_view_starttime", stime);
        page_change.put("page_view_endtime", endtime);
        cleverTap.event.push("page_change", page_change);
        ExternalFunctions.prevActivity = SCREEN_NAME;
        System.out.println("first2"+ExternalFunctions.prevActivity);
        Runtime.getRuntime().gc();


        ExternalFunctions.FilteredString = "";
        ExternalFunctions.strOverlayurl = "";
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ExternalFunctions.SortStatus = false;
        ExternalFunctions.FilterStatus = false;
        tv_refine.setTextColor(Color.BLACK);
        tv_sort.setTextColor(Color.BLACK);
        ExternalFunctions.strfilter = "";
        ExternalFunctions.sort = 0;
        ExternalFunctions.intprice = 0;
        ExternalFunctions.intcat = 0;
        ExternalFunctions.intsize = 0;
        ExternalFunctions.ActivityParam = "base";
        ExternalFunctions.BroadCastedActivity = "";
        ExternalFunctions.BroadCastedUrl = "";
        ExternalFunctions.DiffParam = "";

        ExternalFunctions.blfiteropen = false;
        ExternalFunctions.SortStatus = false;
        ExternalFunctions.sort = 0;
        callActivity="HOME_PAGE";
//        Intent discover = new Intent(filtered.this, activity.discover.class);
//        startActivity(discover);
//        finish();
    }


    public void GetUrl() {
        try{
        System.out.println("aqw1" + Url);
        String a = Url.substring("https://prod.zapyle.com/filters/getProducts/".length(), Url.length());
        int b = a.indexOf("/an");
        String c = a.substring(b, a.length());
        Url = "https://prod.zapyle.com/filters/getProducts/1" + c;
        System.out.println("aqw2" + Url);
        pageNo = 1;
        loading = false;
    }catch (StringIndexOutOfBoundsException e){

    }
//        recyclerView.scrollToPosition(0);
    }


    @Override
    protected void onRestart() {
        super.onRestart();

        System.out.println("APPSTATUS:" + "restart");
        //System.gc();
        Runtime runtime = Runtime.getRuntime();

        if (!GetSharedValues.GetgcmId(getBaseContext()).equals("")) {
            ApiService.getInstance(getBaseContext(), 1).getData(filtered.this, true, "filtered", EnvConstants.APP_BASE_URL + "/user/session/?gcm_reg_id=" + GetSharedValues.GetgcmId(getBaseContext()), "session");
        } else {
            ApiService.getInstance(getBaseContext(), 1).getData(filtered.this, true, "filtered", EnvConstants.APP_BASE_URL + "/user/session/", "session");
        }
    }
}

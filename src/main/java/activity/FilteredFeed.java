package activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.freshdesk.hotline.Hotline;
import com.freshdesk.hotline.HotlineCallbackStatus;
import com.freshdesk.hotline.UnreadCountCallback;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
////import com.uxcam.UXCam;
import com.zapyle.zapyle.Alerts;
import com.zapyle.zapyle.CheckConnectivity;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;


import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


import DataBase.DatabaseDB;
import adapters.RecyclerViewProductAdapter;
import models.HomeFeedItem;
import network.ApiCommunication;
import network.ApiService;
import network.JsonParser;
import recievers.FeedReciever;
import services.FeedService;
import utils.CustomMessage;
import utils.ExternalFunctions;
import utils.FontUtils;
import utils.GetSharedValues;

/**
 * Created by haseeb on 25/8/16.
 */
public class FilteredFeed extends ActionBarActivity implements ApiCommunication, FeedReciever.Receiver {

    public String START = "START";
    public String STOP = "STOP";

    RecyclerView recyclerView;
    CountDownTimer countDownTimer = null;
    public ArrayList<String> drawerdata = new ArrayList<String>();

    TextView tv_sort, tv_refine, tv_newstories, tv_refresh, tvcount, tv_flash_title, tv_flash_sale_info, tv_flash_description, tv_flash_sale_timer;
    LinearLayout lv_emptylayout, lv_refineLayout, lv_flashSaleLayout, lv_flash_timr_layout;
    SwipeRefreshLayout refreshLayout;
    ImageView im_flashSaleBanner;
    FloatingActionButton floatingActionButton;
    ProgressBar progressBar, gridloader;
    AppBarLayout appBarLayout;

    String Url = "";
    Integer pageNo = 1;
    FeedReciever mReceiver;
    Boolean refreshStatus = false, loading = false, SocketStatus = false, clickStatus = false;

    ArrayList<HomeFeedItem> feeds = new ArrayList<HomeFeedItem>();
    RecyclerViewProductAdapter p_adapter;
    Handler handler = new Handler();

    MixpanelAPI mixpanel;
    GridLayoutManager mGridLayoutManager;
    //WebSocketClient mWebSocketClient;
    JSONObject new_data = new JSONObject();
    private int lastVisibleItem, totalItemCount;
    TextView heading;
    JSONObject CollectionData = new JSONObject();
    TextView tvchatcount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtered_feed);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        // //UXCam.startWithKey("1dfb25141864376");
        mixpanel = MixpanelAPI.getInstance(this, getResources().getString(R.string.mixpanelToken));

        mReceiver = new FeedReciever(new Handler());
        mReceiver.setReceiver(this);


        try {
            ExternalFunctions.FilteredUrl = getIntent().getStringExtra("ForwardUrl");
        } catch (Exception e) {

        }

        InitialiseHeader();

        recyclerView = (RecyclerView) findViewById(R.id.main_recyclerview);
//        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        mDrawerList = (ListView) findViewById(R.id.navList);

        tv_sort = (TextView) findViewById(R.id.sortby);
        tv_refine = (TextView) findViewById(R.id.refine);
        tv_newstories = (TextView) findViewById(R.id.new_stories);
        tv_refresh = (TextView) findViewById(R.id.refresh);

        lv_emptylayout = (LinearLayout) findViewById(R.id.emptyLayout);
        lv_refineLayout = (LinearLayout) findViewById(R.id.refineLayout);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingButton);
        progressBar = (ProgressBar) findViewById(R.id.buy_progress);
        gridloader = (ProgressBar) findViewById(R.id.gridloader);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);

        tv_newstories.setText(getString(R.string.uparrow) + " JUST IN");
        tv_newstories.setVisibility(View.GONE);


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
                        appBarLayout.setExpanded(true,false);
                    }//public void run() {
                });
            }
        });

        tv_refine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent filter = new Intent(FilteredFeed.this, filter_activity.class);
                filter.putExtra("activity", "FilteredFeed");
                startActivity(filter);
                finish();
            }
        });

        UpdateFilterLayoutParams();

        tv_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckConnectivity.isNetworkAvailable(FilteredFeed.this)) {
                    if (lv_emptylayout.getVisibility() == View.VISIBLE) {
                        lv_emptylayout.setVisibility(View.INVISIBLE);
                    }
                    GetData(Url, 0);
                } else {
                    CustomMessage.getInstance().CustomMessage(FilteredFeed.this, "Internet is not available!");
                }
            }
        });


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (CheckConnectivity.isNetworkAvailable(FilteredFeed.this)) {
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
                    CustomMessage.getInstance().CustomMessage(FilteredFeed.this, "Internet is not available!");
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
                    gridloader.setVisibility(View.VISIBLE);
                    loading = true;
                    pageNo = pageNo + 1;
                    JSONObject superprop = new JSONObject();
                    try {
                        superprop.put("page count", pageNo);
                        superprop.put("Event Name", "next page called in pagination");
                        mixpanel.track("next page called in pagination", superprop);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    if (CheckConnectivity.isNetworkAvailable(getApplicationContext())) {
                        GetIntentData(1);
                    } else {
                        CustomMessage.getInstance().CustomMessage(FilteredFeed.this, "Internet is not available!");

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
        View customView = getLayoutInflater().inflate(R.layout.home_actionbar, null);
        getSupportActionBar().setCustomView(customView);
        Toolbar parent = (Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0, 0);

        heading = (TextView) findViewById(R.id.heading);
        if (ExternalFunctions.FilteredString.length() <= 0 || ExternalFunctions.FilteredString.isEmpty()) {
            heading.setText("Buy");
        } else {
            heading.setText(ExternalFunctions.FilteredString);
        }


        RelativeLayout cart = (RelativeLayout) findViewById(R.id.home_cart);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                customToast = new CustomToast(HomePage.this, "Cart is clicked");
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
            SharedPreferences CartSession = getSharedPreferences("CartSession",
                    Context.MODE_PRIVATE);
            if (CartSession.getInt("cartCount", 0) > 0) {
                tvcount.setVisibility(View.VISIBLE);
                tvcount.setText(String.valueOf(CartSession.getInt("cartCount", 0)));
            } else {
                tvcount.setVisibility(View.INVISIBLE);
            }
        }
        tvchatcount=(TextView) customView.findViewById(R.id.chatcount);
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
        hamburg.setImageResource(R.drawable.backnew);
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
                Intent search = new Intent(getApplicationContext(), searchnew.class);
                search.putExtra("activity", "FeedPage");
                startActivity(search);
//                finish();
            }
        });
    }


    //    Drawer functions
//    -----------------------------------------------------

//    private void addDrawerItems() {
//        drawerdata.clear();
//        drawerdata.add("");
//        mAdapter = new DrawerAdaptor(this, drawerdata);
////
//        mDrawerList.setAdapter(mAdapter);
//        mDrawerList.setBackgroundColor(Color.BLACK);
//    }


//    private void setupDrawer() {
//        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
//
//            /** Called when a drawer has settled in a completely open state. */
//            public void onDrawerOpened(View drawerView) {
//                super.onDrawerOpened(drawerView);
//
////                getSupportActionBar().setTitle("Navigation!");
//                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
//            }
//
//            /** Called when a drawer has settled in a completely closed state. */
//            public void onDrawerClosed(View view) {
//                super.onDrawerClosed(view);
////                getSupportActionBar().setTitle(mActivityTitle);
//                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
//            }
//        };
//
//        mDrawerToggle.setDrawerIndicatorEnabled(true);
//        mDrawerLayout.setDrawerListener(mDrawerToggle);
//    }

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

                System.out.println("Hotline count:"+unreadCount);
                if(unreadCount>0){
                    tvchatcount.setText(String.valueOf(unreadCount));
                    tvchatcount.setVisibility(View.VISIBLE);
                }else{
                    tvchatcount.setVisibility(View.GONE);
                }



            }
        });
        if (ExternalFunctions.FilteredString.length() <= 0 || ExternalFunctions.FilteredString.isEmpty()) {
            heading.setText("Buy");
        } else {
            heading.setText(ExternalFunctions.FilteredString);
        }
        super.onResume();
    }

    //    Drawer adaptor
//    ==============================================

    //   DRAWER ADAPTOR
//    -----------------------------------------------------------
//    ----------------------------------------------------------

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
                                    recyclerView.setVisibility(View.INVISIBLE);

                                    feeds.clear();
//                                    if (mDrawerLayout.isDrawerVisible(Gravity.LEFT)) {
//                                        mDrawerLayout.closeDrawer(Gravity.LEFT);
//                                    }

                                    Intent discover = new Intent(FilteredFeed.this, HomePageNew.class);
                                    discover.putExtra("activity", "HomePage");
                                    discover.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                    startActivity(discover);
                                    finish();

                                } else {
                                    CustomMessage.getInstance().CustomMessage(getApplicationContext(), "Internet is not available!");
                                }
                                break;

                            case "Sell":
                                if (CheckConnectivity.isNetworkAvailable(getApplicationContext())) {
                                    if (GetSharedValues.LoginStatus(getApplicationContext())) {
                                        Intent intent = new Intent(FilteredFeed.this, Upload1.class);
                                        intent.putExtra("activity", "FilteredFeed");
                                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                        ExternalFunctions.uploadbackcheck = 1;
                                        startActivity(intent);
//                                    finish();

                                    } else {
                                        Alerts.loginAlert(getApplicationContext());
                                    }
                                } else {
//                                Alerts.InternetAlert(FeedPage.this);
                                    CustomMessage.getInstance().CustomMessage(getApplicationContext(), "Internet is not available!");

                                }

                                break;

                            case "Buy":
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
                                Intent buy = new Intent(FilteredFeed.this, BuyPage.class);
                                buy.putExtra("activity", "FilteredFeed");
                                startActivity(buy);
                                break;


                            case "My Profile":
                                if (CheckConnectivity.isNetworkAvailable(getApplicationContext())) {
                                    if (GetSharedValues.LoginStatus(getApplicationContext())) {
                                        Intent intent = new Intent(getApplicationContext(), ProfilePage.class);
                                        intent.putExtra("user_id", GetSharedValues.getuserId(getApplicationContext()));
                                        intent.putExtra("p_username", GetSharedValues.getUsername(getApplicationContext()));
                                        intent.putExtra("activity", "FilteredFeed");
                                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                        startActivity(intent);
//                                    finish();

                                    } else {
                                        Alerts.loginAlert(getApplicationContext());
                                    }
                                } else {
//                                Alerts.InternetAlert(getApplicationContext());
                                    CustomMessage.getInstance().CustomMessage(getApplicationContext(), "Internet is not available!");

                                }
                                break;


                            case "My Account":
                                if (GetSharedValues.LoginStatus(getApplicationContext())) {
                                    Intent intent = new Intent(getApplicationContext(), Myaccountpage.class);
                                    intent.putExtra("activity", "FilteredFeed");
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
                                        myorders.putExtra("activity", "FilteredFeed");
                                        startActivity(myorders);
//                                    finish();
                                    } else {
                                        Alerts.loginAlert(getApplicationContext());
                                    }
                                } else {
//                                Alerts.InternetAlert(FeedPage.this);
                                    CustomMessage.getInstance().CustomMessage(getApplicationContext(), "Internet is not available!");
                                }
                                break;


                            case "Authenticity":


                                Intent intent2 = new Intent(getApplicationContext(), Authenticity.class);
                                intent2.putExtra("activity", "FilteredFeed");
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
                                    recyclerView.setVisibility(View.INVISIBLE);
                                    feeds.clear();
//                                    mDrawerLayout.closeDrawer(Gravity.LEFT);

                                    Intent discover = new Intent(getApplicationContext(), HomePageNew.class);
                                    discover.putExtra("activity", "HomePage");
                                    discover.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(discover);
                                    finish();

                                } else {
//                                Alerts.InternetAlert(FeedPage.this);
                                    CustomMessage.getInstance().CustomMessage(getApplicationContext(), "Internet is not available!");
                                }
                                break;

                            case "Sell":
                                if (CheckConnectivity.isNetworkAvailable(getApplicationContext())) {
                                    if (GetSharedValues.LoginStatus(getApplicationContext())) {
                                        Intent intent = new Intent(getApplicationContext(), Upload1.class);
                                        intent.putExtra("activity", "FilteredFeed");
                                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                        ExternalFunctions.uploadbackcheck = 1;
                                        startActivity(intent);

                                    } else {
                                        Alerts.loginAlert(getApplicationContext());
                                    }
                                } else {
//                                Alerts.InternetAlert(FeedPage.this);
                                    CustomMessage.getInstance().CustomMessage(getApplicationContext(), "Internet is not available!");

                                }

                                break;


                            case "Buy":
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
                                Intent buy = new Intent(FilteredFeed.this, BuyPage.class);
                                buy.putExtra("activity", "FilteredFeed");
                                startActivity(buy);
                                finish();
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
                                    CustomMessage.getInstance().CustomMessage(getApplicationContext(), "Internet is not available!");

                                }

                                break;


                            case "Authenticity":


                                Intent intent2 = new Intent(getApplicationContext(), Authenticity.class);
                                intent2.putExtra("activity", "FilteredFeed");
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
                            if (CheckConnectivity.isNetworkAvailable(FilteredFeed.this)) {
                                Intent intent1 = new Intent(FilteredFeed.this, About_us.class);
                                intent1.putExtra("activity", "FilteredFeed");
                                intent1.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                startActivity(intent1);
//                                finish();
                            } else {
//                                Alerts.InternetAlert(getApplicationContext());
                                CustomMessage.getInstance().CustomMessage(FilteredFeed.this, "Internet is not available!");

                            }
                            break;


                        case "-FAQ":

                            if (CheckConnectivity.isNetworkAvailable(FilteredFeed.this)) {
                                Intent intent5 = new Intent(FilteredFeed.this, FAQ.class);
                                intent5.putExtra("activity", "FilteredFeed");
                                intent5.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                startActivity(intent5);
//                                finish();
                            } else {
//                                Alerts.InternetAlert(getApplicationContext());
                                CustomMessage.getInstance().CustomMessage(FilteredFeed.this, "Internet is not available!");

                            }
                            break;


                        case "-RATE APP":
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("https://play.google.com/store/apps/details?id=com.zapyle.zapyle")));
                            break;


                        case "-CALL ZAPYLE":


                            if (CheckConnectivity.isNetworkAvailable(FilteredFeed.this)) {
                                Intent intent = new Intent(Intent.ACTION_CALL);
                                intent.setData(Uri.parse("tel:" + "08040004541"));
//                                if (ActivityCompat.checkSelfPermission(BuySecondPage.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                                    // TODO: Consider calling
//                                    //    ActivityCompat#requestPermissions
//                                    // here to request the missing permissions, and then overriding
//                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                    //                                          int[] grantResults)
//                                    // to handle the case where the user grants the permission. See the documentation
//                                    // for ActivityCompat#requestPermissions for more details.
//                                    return;
//                                }
                                startActivity(intent);
//                                finish();
                            } else {
//                                Alerts.InternetAlert(getApplicationContext());
                                CustomMessage.getInstance().CustomMessage(FilteredFeed.this, "Internet is not available!");

                            }
                            break;

                    }
                }
            });
            view.addView(tv);

        }

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

                if (ExternalFunctions.sort != 1) {
                    ExternalFunctions.SortStatus = true;
                    ExternalFunctions.sort = 1;
                    tv_sort.setTextColor(Color.parseColor("#ff7477"));
                    JSONObject prop = new JSONObject();
                    try {
                        prop.put("sort event", "Price high to low");
                        prop.put("Event Name", "Apply sort");
                        mixpanel.track("Apply sort", prop);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    recyclerView.removeAllViews();
                    if (Url.contains("sort")) {
                        if (Url.contains("?sort")) {
                            Url = Url.substring(0, Url.length() - 7);
                            Url = Url + "?sort=" + 1;
                        } else if (Url.contains("?")) {
                            Url = Url.substring(0, Url.length() - 7);
                            if (Url.substring(Url.length() - 1).equals("&")) {
                                Url = Url + "sort=" + 1;
                            }
                            else {
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
                            }
                            else {
                                Url = Url + "&sort=" + 1;
                            }
                        } else {
                            ExternalFunctions.sort = 1;
                            Url = Url + "?sort=" + 1;
                        }
                    }

                    recyclerView.removeAllViews();
                    GetData(Url, 0);
                    alert.dismiss();

                } else {
                    ExternalFunctions.SortStatus = false;
                    if (Url.contains("sort")) {
                        Url = Url.substring(0, Url.length() - 7);
                    }
                    ExternalFunctions.sort = 0;
                    pHL.setTextColor(Color.BLACK);
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
                            }
                            else {
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
                            }
                            else {
                                Url = Url + "&sort=" + 2;
                            }
                        } else {
                            ExternalFunctions.sort = 2;
                            Url = Url + "?sort=" + 2;
                        }
                    }

                    ExternalFunctions.sort = 2;
                    GetData(Url, 0);
                    alert.dismiss();
                    tv_sort.setTextColor(Color.parseColor("#ff7477"));
                    JSONObject prop = new JSONObject();
                    try {
                        prop.put("sort event", "Price low to high");
                        prop.put("Event Name", "Apply sort");
                        mixpanel.track("Apply sort", prop);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ExternalFunctions.SortStatus = false;
                    if (Url.contains("sort")) {
                        Url = Url.substring(0, Url.length() - 7);
                    }
                    ExternalFunctions.sort = 0;
                    pLH.setTextColor(Color.BLACK);
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
                            }
                            else {
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
                            }
                            else {
                                Url = Url + "&sort=" + 3;
                            }
                        } else {
                            ExternalFunctions.sort = 3;
                            Url = Url + "?sort=" + 3;
                        }
                    }

                    ExternalFunctions.sort = 3;
                    GetData(Url, 0);
                    alert.dismiss();
                    feeds.clear();
                    tv_sort.setTextColor(Color.parseColor("#ff7477"));
                    JSONObject prop = new JSONObject();
                    try {
                        prop.put("sort event", "Popularity");
                        prop.put("Event Name", "Apply sort");
                        mixpanel.track("Apply sort", prop);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ExternalFunctions.SortStatus = false;
                    if (Url.contains("sort")) {
                        Url = Url.substring(0, Url.length() - 7);
                    }
                    ExternalFunctions.sort = 0;
                    popularity.setTextColor(Color.BLACK);
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
                            }
                            else {
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
                            }
                            else {
                                Url = Url + "&sort=" + 4;
                            }
                        } else {
                            ExternalFunctions.sort = 4;
                            Url = Url + "?sort=" + 4;
                        }
                    }
                    GetData(Url, 0);
                    ExternalFunctions.sort = 4;
                    alert.dismiss();
                    tv_sort.setTextColor(Color.parseColor("#ff7477"));
                    JSONObject prop = new JSONObject();
                    try {
                        prop.put("sort event", "Discount");
                        prop.put("Event Name", "Apply sort");
                        mixpanel.track("Apply sort", prop);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ExternalFunctions.SortStatus = false;
                    if (Url.contains("sort")) {
                        Url = Url.substring(0, Url.length() - 7);
                    }

                    ExternalFunctions.sort = 0;
                    sort_discount.setTextColor(Color.BLACK);

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
                            }
                            else {
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
                            }
                            else {
                                Url = Url + "&sort=" + 5;
                            }
                        } else {
                            ExternalFunctions.sort = 5;
                            Url = Url + "?sort=" + 5;
                        }
                    }

                    ExternalFunctions.sort = 5;
                    GetData(Url, 0);
                    alert.dismiss();
                    feeds.clear();
                    tv_sort.setTextColor(Color.parseColor("#ff7477"));
                    JSONObject prop = new JSONObject();
                    try {
                        prop.put("sort event", "Whatsnew");
                        prop.put("Event Name", "Apply sort");
                        mixpanel.track("Apply sort", prop);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ExternalFunctions.SortStatus = false;
                    if (Url.contains("sort")) {
                        Url = Url.substring(0, Url.length() - 7);
                    }

                    ExternalFunctions.sort = 0;
                    whatsnew.setTextColor(Color.BLACK);
                    GetData(Url, 0);
                    alert.dismiss();
                    feeds.clear();
                    tv_sort.setTextColor(Color.BLACK);

                }
            }
        });
    }




    private void GetIntentData(int status) {
        if (ExternalFunctions.strOverlayurl.length() > 1) {
            System.out.println("FILTERED : 1 if");
            try {
                String filterData = "";
                String[] filterDataList = ExternalFunctions.strOverlayurl.split("\\?");
                if (filterDataList.length > 1) {
                    filterData = ExternalFunctions.strOverlayurl.split("\\?")[1];
                    if (filterData.length() > 0 & filterData != null) {
                        ExternalFunctions.FilterStatus = true;
                        filterData = filterData + "&";
                    }
                } else {
                    filterData = "";
                }
                System.out.println("FILTERED : 1 if try : " + filterData);

                if (ExternalFunctions.FilterStatus && ExternalFunctions.SortStatus && ExternalFunctions.sort > 0 && ExternalFunctions.strfilter.length() > 1) {
                    Url = EnvConstants.URL_BUY + "/" + pageNo + "/an/?" + filterData + ExternalFunctions.strfilter + "&" + "sort=" + ExternalFunctions.sort;
                } else if (ExternalFunctions.FilterStatus && !ExternalFunctions.SortStatus && ExternalFunctions.strfilter.length() > 1) {
                    Url = EnvConstants.URL_BUY + "/" + pageNo + "/an/?" + filterData + ExternalFunctions.strfilter;
                } else if (ExternalFunctions.SortStatus && !ExternalFunctions.FilterStatus) {
                    Url = EnvConstants.URL_BUY + "/" + pageNo + "/an/?" + filterData + "sort=" + ExternalFunctions.sort;
                } else {
                    Url = EnvConstants.URL_BUY + "/" + pageNo + "/an/?" + filterData;
                }

                ExternalFunctions.FilteredUrl = ExternalFunctions.strOverlayurl;
                ExternalFunctions.strOverlayurl = "";
                System.out.println("FILTERED : 1 if try last : " + ExternalFunctions.FilteredUrl + "___" + ExternalFunctions.strOverlayurl);
                GetData(Url, status);

            } catch (Exception e) {
                Url = EnvConstants.URL_BUY + "/" + pageNo + "/an/";
                GetData(Url, status);
            }
            UpdateFilterLayoutParams();
        } else {
            System.out.println("FILTERED : 1 else : " + ExternalFunctions.FilteredString);
            try {
                String filterData = "";
                String[] filterDataList = ExternalFunctions.FilteredUrl.split("\\?");
                if (filterDataList.length > 1) {
                    filterData = ExternalFunctions.FilteredUrl.split("\\?")[1];
                    if (filterData.length() > 0 & filterData != null) {
                        ExternalFunctions.FilterStatus = true;
                        filterData = filterData + "&";
                    }
                } else {
                    filterData = "";
                }
                System.out.println("FILTERED : 1 else try : " + filterData);
                if (ExternalFunctions.FilterStatus && ExternalFunctions.SortStatus && ExternalFunctions.sort > 0 && ExternalFunctions.strfilter.length() > 1) {
                    Url = EnvConstants.URL_BUY + "/" + pageNo + "/an/?" + filterData + "&" + ExternalFunctions.strfilter + "&" + "sort=" + ExternalFunctions.sort;
                } else if (ExternalFunctions.FilterStatus && !ExternalFunctions.SortStatus && ExternalFunctions.strfilter.length() > 1) {
                    Url = EnvConstants.URL_BUY + "/" + pageNo + "/an/?" + filterData + "&" + ExternalFunctions.strfilter;
                } else if (ExternalFunctions.SortStatus && !ExternalFunctions.FilterStatus) {
                    Url = EnvConstants.URL_BUY + "/" + pageNo + "/an/?" + filterData + "&" + "sort=" + ExternalFunctions.sort;
                } else {
                    Url = EnvConstants.URL_BUY + "/" + pageNo + "/an/?" + filterData;
                }
                GetData(Url, status);
                System.out.println("FILTERED : 1 else try last");
            } catch (Exception e) {
                System.out.println("FILTERED : 1 else catch : " + e);
                if (ExternalFunctions.FilterStatus && ExternalFunctions.SortStatus && ExternalFunctions.sort > 0 && ExternalFunctions.strfilter.length() > 1) {
                    Url = EnvConstants.URL_BUY + "/" + pageNo + "/an/" + "?" + ExternalFunctions.strfilter + "&" + "sort=" + ExternalFunctions.sort;
                } else if (ExternalFunctions.FilterStatus && !ExternalFunctions.SortStatus && ExternalFunctions.strfilter.length() > 1) {
                    Url = EnvConstants.URL_BUY + "/" + pageNo + "/an/" + "?" + ExternalFunctions.strfilter;
                } else if (ExternalFunctions.SortStatus && !ExternalFunctions.FilterStatus) {
                    Url = EnvConstants.URL_BUY + "/" + pageNo + "/an/" + "?" + "sort=" + ExternalFunctions.sort;
                } else {
                    Url = EnvConstants.URL_BUY + "/" + pageNo + "/an/";
                }

                GetData(Url, status);
            }

            UpdateFilterLayoutParams();
        }
    }


    private void GetData(String Url, int status) {
        if (!refreshStatus && !loading) {
            handleProgress(START);
        }
        if (status == 0) {
            Intent service = new Intent(Intent.ACTION_SYNC, null, this, FeedService.class);
            service.putExtra("url", Url);
            service.putExtra("receiver", mReceiver);
            service.putExtra("Method", "Product");
            service.putExtra("ScreenName", "FEEDPAGE");
            service.putExtra("PageNo", 1);
            startService(service);
        } else {
            Intent service = new Intent(Intent.ACTION_SYNC, null, this, FeedService.class);
            service.putExtra("url", Url);
            service.putExtra("receiver", mReceiver);
            service.putExtra("Method", "Product");
            service.putExtra("ScreenName", "FEEDPAGE");
            service.putExtra("PageNo", pageNo);
            startService(service);
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
    public void onReceiveResult(int resultCode, Bundle resultData) {
        System.out.println("DATAAAAAAA : " + resultData);

        switch (resultCode) {
            case FeedService.STATUS_RUNNING:
                System.out.println("SERVICE RUNNING");
                break;


            case FeedService.STATUS_FINISHED:
                String method = resultData.getString("Method");
                String collectiondata = resultData.getString("Collection");
                System.out.println("COLLECTIONADAT : " + collectiondata);

                switch (method) {
                    case "Product":
                        System.out.println("INSIDE DATA: " + resultData.getSerializable("DATALIST"));
                        handleProgress(STOP);
                        refreshLayout.setRefreshing(false);
                        refreshStatus = false;
                        loading = false;
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
                            lv_flashSaleLayout.setVisibility(View.VISIBLE);
                            ShowCollectionData(CollectionData);
                        }


                        if (feeds != null) {
                            System.out.println("INSIDE DATA: first if ! null");
                            if (feeds.size() > 0) {
                                System.out.println("INSIDE DATA: secoonf if ! null");
                                recyclerView.setVisibility(View.VISIBLE);
                                lv_emptylayout.setVisibility(View.GONE);
                                p_adapter = new RecyclerViewProductAdapter(feeds, this, ExternalFunctions.DiffParam, recyclerView, "FilteredFeed");
                                recyclerView.removeAllViews();
                                recyclerView.setAdapter(p_adapter);                              // Setting the adapter to RecyclerView
                                recyclerView.setLayoutManager(mGridLayoutManager);
                                p_adapter.notifyDataSetChanged();

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
                        loading = false;
                        gridloader.setVisibility(View.GONE);
                        feeds.addAll((ArrayList<HomeFeedItem>) resultData.getSerializable("DATALIST"));
                        p_adapter.notifyDataSetChanged();
                        break;

                }
                break;

            case FeedService.STATUS_ERROR:
                gridloader.setVisibility(View.GONE);
                String mtd = resultData.getString("Method");
                switch (mtd) {

                    case "Closet":
                        CustomMessage.getInstance().CustomMessage(this, "Unable to load feed");
                        break;

                    case "Product":
                        CustomMessage.getInstance().CustomMessage(this, "Unable to load feed");
                        break;
                }

        }
    }

    private void ShowCollectionData(JSONObject collectionData) {
        tv_flash_title.setText(collectionData.optString("title"));
        if (collectionData.has("sale_info")) {
            if (!collectionData.optString("sale_info").isEmpty()) {
                tv_flash_sale_info.setText(collectionData.optString("sale_info"));
            }
            else {
                tv_flash_sale_info.setVisibility(View.GONE);
            }
        }
        else {
            tv_flash_sale_info.setVisibility(View.GONE);
        }
        if (!collectionData.optString("description").isEmpty()) {
            tv_flash_description.setText(collectionData.optString("description"));
        }
        else {
            tv_flash_description.setVisibility(View.GONE);
        }
        if (collectionData.has("image")) {
            if (!collectionData.isNull("image")) {
                Glide.with(FilteredFeed.this)
                        .load(EnvConstants.APP_MEDIA_URL+collectionData.optString("image"))
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

            long flashSaleTime = collectionData.optLong("end_timestamp") - (System.currentTimeMillis()/1000);
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


    public void showFlashSalePop(){
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
                        break;

                    case "STOP":
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
        Intent discover = new Intent(FilteredFeed.this, HomePageNew.class);
        startActivity(discover);
        finish();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
//        mDrawerList.setVisibility(View.INVISIBLE);
//        if (mDrawerLayout.isDrawerVisible(Gravity.LEFT)) {
//            mDrawerLayout.closeDrawer(Gravity.LEFT);
//        }
        ////System.out.println("APPSTATUS:" + "restart");
        //System.gc();
        Runtime runtime = Runtime.getRuntime();

        if (!GetSharedValues.GetgcmId(getBaseContext()).equals("")) {
            ApiService.getInstance(getBaseContext(), 1).getData(FilteredFeed.this, true, "FilteredFeed", EnvConstants.APP_BASE_URL + "/user/session/?gcm_reg_id=" + GetSharedValues.GetgcmId(getBaseContext()), "session");
        } else {
            ApiService.getInstance(getBaseContext(), 1).getData(FilteredFeed.this, true, "FilteredFeed", EnvConstants.APP_BASE_URL + "/user/session/", "session");
        }
    }
}

package activity;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.appvirality.android.AppviralityAPI;
import com.bumptech.glide.Glide;
import com.freshdesk.hotline.Hotline;
import com.freshdesk.hotline.HotlineCallbackStatus;
import com.freshdesk.hotline.UnreadCountCallback;
import com.google.android.gms.analytics.Tracker;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
//import com.uxcam.UXCam;
import com.zapyle.zapyle.Alerts;
import com.zapyle.zapyle.CheckConnectivity;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import adapters.RecyclerViewClosetAdapter;
import adapters.RecyclerViewProductAdapter;
import application.MyApplicationClass;
import models.ClosetData;
import models.HomeFeedItem;
import network.ApiCommunication;
import network.JsonParser;
import recievers.FeedReciever;
import services.FeedService;
import utils.CommonFinish;
import utils.CustomMessage;
import utils.ExternalFunctions;
import utils.FontUtils;
import utils.GetSharedValues;
/**
 * Created by haseeb on 5/8/16.
 */
public class BuySecondPage extends ActionBarActivity implements FeedReciever.Receiver, ApiCommunication {

    private String START = "START";
    private String STOP = "STOP";
    String callingActvity;

    FeedReciever mReceiver;
    RecyclerView recyclerview;
    SwipeRefreshLayout refreshLayout;
    RecyclerViewClosetAdapter c_adapter;
    RecyclerViewProductAdapter p_adapter;
    ArrayList<ClosetData> closetData = new ArrayList<ClosetData>();
    ArrayList<HomeFeedItem> feeds = new ArrayList<HomeFeedItem>();
    int switchStatus = 1;
    FloatingActionButton floatingButton;
    private int mTotalScrolled=0;
    AppBarLayout appBarLayout;

    TextView tv_closetview,
            tv_productview,
            tv_refine,
            tv_sort,
            tvcount,
            tv_new_stories,
            tv_pageTitle,
            tv_refresh,
            heading,
            tv_closetutorial;

    LinearLayout lv_clickholder,
            lv_refineLayout,
            lv_emptylayout;
    FrameLayout frame_layout, lv_toplayout;
    ImageView im_toplayout_img;

    String Url;
    int pageNo = 1;
    ProgressBar progressBar, gridloader;


    Handler handler = new Handler();
    MixpanelAPI mixpanel;
    boolean blCollapse=false;
    private int lastVisibleItem, totalItemCount;
    RecyclerView.LayoutManager mLayoutManager = null;
    RecyclerView.LayoutManager mGridLayoutManager = null;

    private boolean loading, refreshStatus;
    Tracker mTracker;
    boolean isExistingUser = false, hasReferrer = false, SocketStatus = false, ScrollStatus = false, clickStatus = false;

    JSONObject new_data;
    private Timer t;
    private int TimeCounter = 0;
    ScrollView tutorialPage;
    boolean blProductLoading = false;
    boolean blClosetLoading = false;
    Toolbar toolbartop;
    TextView tvchatcount;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buysecond_page);
        ////UXCam.startWithKey("1dfb25141864376");
        appBarLayout = (AppBarLayout)findViewById(R.id.appbar);
        InitialiseCommonParams();
        ApplyCustomFont();
        AppViralityInitialise();
        System.out.println("websitezz"+ExternalFunctions.strfilter);
        mReceiver = new FeedReciever(new Handler());
        mReceiver.setReceiver(this);
        InitialiseHeader();
        try {
            callingActvity = getIntent().getStringExtra("activity");
        } catch (Exception e) {
            callingActvity = "BuyPage";
        }


        mixpanel = MixpanelAPI.getInstance(this, getResources().getString(R.string.mixpanelToken));
        tv_closetview = (TextView) findViewById(R.id.closetview_click);
        tv_closetutorial = (TextView) findViewById(R.id.close_button);
        tv_productview = (TextView) findViewById(R.id.productview_click);
        tv_sort = (TextView) findViewById(R.id.sortby);
        tv_pageTitle = (TextView) findViewById(R.id.pageTitle);
        FontUtils.setGeorgiaItalicDisplayFont(tv_pageTitle, getAssets());
        tv_new_stories = (TextView) findViewById(R.id.new_stories);
        tv_refresh = (TextView) findViewById(R.id.refresh);
        tv_new_stories.setText(getString(R.string.uparrow) + " JUST IN");
        tv_new_stories.setVisibility(View.GONE);
        tv_refine = (TextView) findViewById(R.id.refine);
        lv_clickholder = (LinearLayout) findViewById(R.id.clickholder);
        lv_refineLayout = (LinearLayout) findViewById(R.id.refineLayout);
        lv_toplayout = (FrameLayout) findViewById(R.id.topLayout);
        toolbartop=(Toolbar) findViewById(R.id.toolbar);
        lv_emptylayout = (LinearLayout) findViewById(R.id.emptyLayout);
        // lv_filterlayout=(LinearLayout) findViewById(R.id.filter_layout);
        im_toplayout_img = (ImageView) findViewById(R.id.topLayout_image);
        frame_layout = (FrameLayout) findViewById(R.id.frame_layout);

        floatingButton = (FloatingActionButton) findViewById(R.id.floatingButton);

        tutorialPage = (ScrollView) findViewById(R.id.tutorialPage);
        tutorialPage.setVisibility(View.GONE);

        recyclerview = (RecyclerView) findViewById(R.id.main_recyclerview);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        progressBar = (ProgressBar) findViewById(R.id.buy_progress);
        gridloader = (ProgressBar) findViewById(R.id.gridloader);

        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager
        mGridLayoutManager = new GridLayoutManager(this, 2);


        if (ExternalFunctions.FilterStatus) {
            tv_refine.setTextColor(Color.parseColor("#ff7477"));
        } else {
            tv_refine.setTextColor(Color.BLACK);
        }

        if (ExternalFunctions.SortStatus || ExternalFunctions.sort > 0) {
            tv_sort.setTextColor(Color.parseColor("#ff7477"));
        } else {
            tv_sort.setTextColor(Color.BLACK);
        }
        tv_productview.setBackgroundColor(Color.BLACK);
        tv_productview.setTextColor(Color.WHITE);

//        p_adapter = new RecyclerViewProductAdapter(feeds, BuySecondPage.this, ExternalFunctions.DiffParam);
//        c_adapter = new RecyclerViewClosetAdapter(closetData, this, ExternalFunctions.DiffParam);

        lv_clickholder.setBackground(getResources().getDrawable(R.drawable.designer_page_bg));
        GetIntentData();


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (CheckConnectivity.isNetworkAvailable(BuySecondPage.this)) {
                    if (lv_toplayout.getVisibility() == View.VISIBLE) {
                        refreshLayout.setRefreshing(true);
                        refreshStatus = true;
                        if (lv_emptylayout.getVisibility() == View.VISIBLE) {
                            lv_emptylayout.setVisibility(View.INVISIBLE);
                        }
                        Url = Url.replace(String.valueOf(pageNo), "1");
                        pageNo = 1;
                     //   mTotalScrolled = 0;
                        GetData(Url, switchStatus);
                        blCollapse = true;
                    }
                    else {
                        refreshLayout.setRefreshing(false);
                    }
                } else {
                    refreshLayout.setRefreshing(false);
                    refreshStatus = false;
                   CustomMessage.getInstance().CustomMessage(BuySecondPage.this, "Internet is not available!");
                }
            }
        });


        tv_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckConnectivity.isNetworkAvailable(BuySecondPage.this)) {
                    if (lv_emptylayout.getVisibility() == View.VISIBLE) {
                        lv_emptylayout.setVisibility(View.INVISIBLE);
                    }
                    pageNo = 1;
                    GetData(Url, switchStatus);
                } else {
                   CustomMessage.getInstance().CustomMessage(BuySecondPage.this, "Internet is not available!");
                }
            }
        });

        tv_new_stories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFeedItem feedObj = JsonParser.getInstance().parserHomefeedObject(new_data);
                feeds.add(0, feedObj);
                tv_new_stories.setVisibility(View.GONE);
                p_adapter.notifyDataSetChanged();

            }
        });
        lv_clickholder.setBackgroundColor(getResources().getColor(R.color.transparent));
        tv_productview.setBackground(getResources().getDrawable(R.drawable.left_transparent_curved));
        tv_productview.setTextColor(Color.BLACK);
        tv_closetview.setBackground(getResources().getDrawable(R.drawable.right_black_curved));
        tv_closetview.setTextColor(Color.WHITE);
        tv_closetview.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {

                if (switchStatus != 0) {
                    if (!blProductLoading) {
                        SharedPreferences FeedSession = getSharedPreferences("FeedSession",
                                Context.MODE_PRIVATE);
                        SharedPreferences.Editor FeedSessioneditor = FeedSession.edit();
                        if (!FeedSession.getBoolean("TutorialSession", false)) {
                            tutorialPage.setVisibility(View.VISIBLE);
                            FeedSessioneditor.putBoolean("TutorialSession", true);
                            FeedSessioneditor.apply();
                        }
//                    showcaseView();
                        loading = true;
                        if (lv_emptylayout.getVisibility() == View.VISIBLE) {
                            lv_emptylayout.setVisibility(View.INVISIBLE);
                        }
                        pageNo = 1;
                        if (lv_refineLayout.getVisibility() == View.VISIBLE) {
                            lv_refineLayout.setVisibility(View.GONE);
                        }
                        tv_closetview.setBackground(getResources().getDrawable(R.drawable.right_transparent_curved));
                        tv_closetview.setTextColor(Color.BLACK);
                        tv_productview.setBackground(getResources().getDrawable(R.drawable.left_black_curved));
                        tv_productview.setTextColor(Color.WHITE);
                        lv_clickholder.setBackgroundColor(getResources().getColor(R.color.transparent));
                        recyclerview.removeAllViews();
                        Url = EnvConstants.URL_SELLERS + pageNo + "/?closet=" + ExternalFunctions.DiffParam.toString();
                        GetData(Url, 0);
                        switchStatus = 0;
                    }
                }

            }
        });
        tv_productview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (switchStatus != 1) {
                    if (!blClosetLoading) {
                        loading = true;
                        if (lv_emptylayout.getVisibility() == View.VISIBLE) {
                            lv_emptylayout.setVisibility(View.INVISIBLE);
                        }
                        pageNo = 1;
                        if (lv_refineLayout.getVisibility() != View.VISIBLE) {
                            lv_refineLayout.setVisibility(View.VISIBLE);
                        }
                        tv_productview.setBackground(getResources().getDrawable(R.drawable.left_transparent_curved));
                        tv_productview.setTextColor(Color.BLACK);
                        tv_closetview.setBackground(getResources().getDrawable(R.drawable.right_black_curved));
                        tv_closetview.setTextColor(Color.WHITE);
                        lv_clickholder.setBackgroundColor(getResources().getColor(R.color.transparent));
                        recyclerview.removeAllViews();
                        switchStatus = 1;
                        GetIntentData();
//                    lv_clickholder.setBackground(getResources().getDrawable(R.drawable.designer_page_bg));
                    }
                }
            }
        });

        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchStatus == 0) {
                    recyclerview.scrollToPosition(0);
                     mTotalScrolled=0;

                    if (ExternalFunctions.DiffParam != "curated" && lv_toplayout.getVisibility() != View.VISIBLE) {
                        ScrollStatus = false;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            appBarLayout.setExpanded(true,true);
                        }//public void run() {
                    });
                } else {
                    recyclerview.scrollToPosition(0);
                    mTotalScrolled=0;

                    if (ExternalFunctions.DiffParam != "curated" && lv_toplayout.getVisibility() != View.VISIBLE) {
                        ScrollStatus = false;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            appBarLayout.setExpanded(true,true);
                        }//public void run() {
                    });
                }
            }
        });

        tv_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (GetSharedValues.LoginStatus(BuySecondPage.this)) {
                    handler.postDelayed(sortDialog, 100);
              //  } else {
                     Alerts.loginAlert(BuySecondPage.this);
               // }
            }
        });

        tv_refine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckConnectivity.isNetworkAvailable(BuySecondPage.this)) {
                    ExternalFunctions.ActivityParam = "base";
                    ExternalFunctions.BroadCastedActivity = "";
                    ExternalFunctions.BroadCastedUrl = "";
                    Intent filter = new Intent(BuySecondPage.this, filter_activity.class);
                    startActivity(filter);
                    finish();
                } else {
                     CustomMessage.getInstance().CustomMessage(BuySecondPage.this, "Internet is not available!");
                   // CustomMessage.getInstance().CustomMessage(BuySecondPage.this, "Internet is not available!");
                }
            }
        });

        SpannableString sp = new SpannableString("Close Tutorial");
        sp.setSpan(new UnderlineSpan(),0, sp.length(),0);
        tv_closetutorial.setText(sp);

        tv_closetutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tutorialPage.setVisibility(View.GONE);
            }
        });
        recyclerview
                .addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView,
                                           int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                            mTotalScrolled -= dy;

                            if(mTotalScrolled==0){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        appBarLayout.setExpanded(true,false);

                                    }
                                });

                            }
                        if (switchStatus == 1) {
                            totalItemCount = mGridLayoutManager.getItemCount();
                            lastVisibleItem = ((GridLayoutManager) mGridLayoutManager).findLastVisibleItemPosition();
                        } else {
                            totalItemCount = mLayoutManager.getItemCount();
                            lastVisibleItem = ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();
                        }


                      //  System.out.println("SCROLLL : " + switchStatus + "___" + loading + "__" + lastVisibleItem + "___" + totalItemCount);
                        if (!loading && lastVisibleItem == totalItemCount - 1) {
                            if (switchStatus == 0) {
                                closetData.add(null);
                                c_adapter.notifyItemInserted(closetData.size() - 1);
                            } else {
                                gridloader.setVisibility(View.VISIBLE);
                            }
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
                                if (ExternalFunctions.FilterStatus) {
                                    if (ExternalFunctions.SortStatus || ExternalFunctions.sort > 0) {
                                        if (ExternalFunctions.sort < 5) {
                                            if (switchStatus == 1) {
                                                Url = EnvConstants.URL_BUY + "/" + pageNo + "/an/" + productCheck() + "?" + ExternalFunctions.strfilter + "&" + "sort=" + ExternalFunctions.sort;
                                            } else {
                                                Url = EnvConstants.URL_SELLERS + pageNo + "/?closet=" + ExternalFunctions.DiffParam.toString();
                                            }
                                        } else {
                                            if (switchStatus == 1) {
                                                Url = EnvConstants.URL_BUY + "/" + pageNo + "/an/" + productCheck() + "?" + ExternalFunctions.strfilter;
                                            } else {
                                                Url = EnvConstants.URL_SELLERS + pageNo + "/?closet=" + ExternalFunctions.DiffParam.toString();
                                            }
                                        }
                                    } else {
                                        if (switchStatus == 1) {
                                            Url = EnvConstants.URL_BUY + "/" + pageNo + "/an/" + productCheck() + "?" + ExternalFunctions.strfilter;
                                        } else {
                                            Url = EnvConstants.URL_SELLERS + pageNo + "/?closet=" + ExternalFunctions.DiffParam.toString();
                                        }
                                    }
                                } else {
                                    if (ExternalFunctions.SortStatus || ExternalFunctions.sort > 0) {
                                        if (ExternalFunctions.sort < 5) {
                                            if (switchStatus == 1) {
                                                Url = EnvConstants.URL_BUY + "/" + pageNo + "/an/" + productCheck() + "?sort=" + ExternalFunctions.sort;
                                            } else {
                                                Url = EnvConstants.URL_SELLERS + pageNo + "/?closet=" + ExternalFunctions.DiffParam.toString();
                                            }
                                        } else {
                                            if (switchStatus == 1) {
                                                Url = EnvConstants.URL_BUY + "/" + pageNo + "/an/" + productCheck();
                                            } else {
                                                Url = EnvConstants.URL_SELLERS + pageNo + "/?closet=" + ExternalFunctions.DiffParam.toString();
                                            }
                                        }
                                    } else {
                                        if (switchStatus == 1) {
                                            Url = EnvConstants.URL_BUY + "/" + pageNo + "/an/" + productCheck();
                                        } else {
                                            Url = EnvConstants.URL_SELLERS + pageNo + "/?closet=" + ExternalFunctions.DiffParam.toString();
                                        }
                                    }
                                }

                                if (switchStatus == 0) {
                                    Intent service = new Intent(Intent.ACTION_SYNC, null, getApplicationContext(), FeedService.class);
                                    service.putExtra("url", Url);
                                    service.putExtra("receiver", mReceiver);
                                    service.putExtra("Method", "Closet");
                                    service.putExtra("ScreenName", "BUYPAGE");
                                    service.putExtra("PageNo", pageNo);
                                    startService(service);
                                } else {
                                    Intent service = new Intent(Intent.ACTION_SYNC, null, getApplicationContext(), FeedService.class);
                                    service.putExtra("url", Url);
                                    service.putExtra("receiver", mReceiver);
                                    service.putExtra("Method", "Product");
                                    service.putExtra("ScreenName", "BUYPAGE");
                                    service.putExtra("PageNo", pageNo);
                                    startService(service);
                                }


                            } else {
                                CustomMessage.getInstance().CustomMessage(BuySecondPage.this, "Internet is not available!");

                            }

                        }
                    }
                });


    }


//    FUNCTIONS
//    ==========================================


    //    Sockect connection functions
//    ----------------------------------------------------------------

    private void AppViralityInitialise() {

        try {
            AppviralityAPI.getInstance(getApplicationContext(), new AppviralityAPI.UserInstance() {
                @Override
                public void onInstance(AppviralityAPI instance) {

                    hasReferrer = instance.hasReferrer();
                    isExistingUser = AppviralityAPI.isExistingUser();
                    ////System.out.println("EXIST:" + hasReferrer + "___" + isExistingUser);
                    Log.i("AV isExisting User: ", "" + isExistingUser);
                    String refCode = AppviralityAPI.getFriendReferralCode();
                    Log.i("AV isExisting User: ", "" + refCode);
                    if (!hasReferrer && !isExistingUser) {
                        AppviralityAPI.UserDetails.setInstance(getApplicationContext())
                                .setUserEmail(GetSharedValues.getUseremail(getApplicationContext()))
                                .setUserName(GetSharedValues.getZapname(getApplicationContext()))
                                .setUseridInStore(String.valueOf(GetSharedValues.getuserId(getApplicationContext())))
                                .isExistingUser(isExistingUser)
                                .Update(new AppviralityAPI.UpdateUserDetailsListner() {
                                    @Override
                                    public void onSuccess(boolean isSuccess) {

                                        ////System.out.println("Truer"+GetSharedValues.getUseremail(Onboarding2.this)+GetSharedValues.getZapname(Onboarding2.this));
                                        Log.i("AppViralitySDK", "Update UserDetails Status : " + isSuccess);
                                    }
                                });
                    }


                }
            });

        } catch (Exception e) {
            //System.out.println("Error" + e.getMessage());
        }
    }


    private void ApplyCustomFont() {
        View view = findViewById(R.id.drawer_layout);
        FontUtils.setCustomFont(view, getAssets());
    }


    private void GetIntentData() {
        System.out.println("ACTIVITYPARAMCHECK : " + ExternalFunctions.ActivityParam+ExternalFunctions.DiffParam+"zz"+ExternalFunctions.strfilter);
        switch (ExternalFunctions.ActivityParam) {
            case "base":
                if (ExternalFunctions.FilterStatus && ExternalFunctions.SortStatus && ExternalFunctions.sort > 0 && ExternalFunctions.strfilter.length() > 1) {
                    Url = EnvConstants.URL_BUY + "/" + pageNo + "/an/" + productCheck() + "?" + ExternalFunctions.strfilter + "&" + "sort=" + ExternalFunctions.sort;
                } else if (ExternalFunctions.FilterStatus && !ExternalFunctions.SortStatus && ExternalFunctions.strfilter.length() > 1) {
                    Url = EnvConstants.URL_BUY + "/" + pageNo + "/an/" + productCheck() + "?" + ExternalFunctions.strfilter;
                } else if (ExternalFunctions.SortStatus && !ExternalFunctions.FilterStatus) {
                    Url = EnvConstants.URL_BUY + "/" + pageNo + "/an/" + productCheck() + "?" + "sort=" + ExternalFunctions.sort;
                } else {
                    Url = EnvConstants.URL_BUY + "/" + pageNo + "/an/" + productCheck();
                }
                headerCheck();
                GetData(Url, 1);

                break;

            case "Broadcasted":
                Url = EnvConstants.APP_BASE_URL + ExternalFunctions.BroadCastedUrl;
                String split;
                if (Url.contains("?")) {
                    split = Url.split("\\?")[1];
                    if (split.length() > 0) {
                        if (split.contains("sort")) {
                            ExternalFunctions.SortStatus = true;
                            String sortCheck = split.split("sort=")[1];
                            String filterCheck = split.split("sort=")[0];
                            ExternalFunctions.sort = Integer.parseInt(sortCheck);
                            if (filterCheck.length() > 0) {
                                ExternalFunctions.strfilter = filterCheck;
                                ExternalFunctions.FilterStatus = true;
                            } else {
                                ExternalFunctions.strfilter = "";
                                ExternalFunctions.FilterStatus = false;
                            }
                        } else {
                            ExternalFunctions.strfilter = split;
                            ExternalFunctions.FilterStatus = true;
                        }
                    } else {
                        ExternalFunctions.strfilter = "";
                        ExternalFunctions.FilterStatus = false;
                        ExternalFunctions.sort = 0;
                        ExternalFunctions.SortStatus = false;
                    }
                    String param = Url.split("\\?")[0];
                    String temp_param = param.split("/an/")[1];
                    System.out.println("CALL PARAMCHECK : " + temp_param);
                    paramCheck(temp_param);
                    GetData(Url, 1);
                } else {
                    String param = Url.split("/an/")[1];
                    System.out.println("CALL PARAMCHECK : " + param);
                    paramCheck(param);
                    GetData(Url, 1);
                }

                break;

            default:
                if (ExternalFunctions.FilterStatus && ExternalFunctions.SortStatus && ExternalFunctions.sort > 0) {
                    Url = EnvConstants.URL_BUY + "/" + pageNo + "/an/" + productCheck() + "?" + ExternalFunctions.strfilter + "&" + "sort=" + ExternalFunctions.sort;
                } else if (ExternalFunctions.FilterStatus && !ExternalFunctions.SortStatus) {
                    Url = EnvConstants.URL_BUY + "/" + pageNo + "/an/" + productCheck() + "?" + ExternalFunctions.strfilter;
                } else if (ExternalFunctions.SortStatus && !ExternalFunctions.FilterStatus) {
                    Url = EnvConstants.URL_BUY + "/" + pageNo + "/an/" + productCheck() + "?" + "sort=" + ExternalFunctions.sort;
                } else {
                    Url = EnvConstants.URL_BUY + "/" + pageNo + "/an/" + productCheck();
                }
                headerCheck();
                GetData(Url, 1);
                break;

        }

    }


    private void InitialiseCommonParams() {
        MyApplicationClass application = (MyApplicationClass) getApplication();
        mTracker = application.getDefaultTracker();
        //UXCam.startWithKey("1dfb25141864376");
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        ExternalFunctions.bloverlay = false;

        LocalBroadcastManager.getInstance(this).registerReceiver(CommonFinish.broadcastReceiver, new IntentFilter("FeedPage"));


//        Checking constatnts
//        --------------------------------------------------
        ExternalFunctions.cContextArray[7] = BuySecondPage.this;
        ExternalFunctions.disapprovemsg = "1";
        ExternalFunctions.referalmsg = "1";
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void headerCheck() {
        System.out.println("ExternalFunctions.DiffParam : " + ExternalFunctions.DiffParam);
        switch (ExternalFunctions.DiffParam) {
            case "marketplace":
                tv_closetview.setText("closets");
                 toolbartop.setBackgroundResource(R.drawable.market);
                im_toplayout_img.setAlpha((float) 0.4);
                heading.setText("Market");
              //  lv_toplayout.requestFocus();
                break;

            case "curated":
                tv_closetview.setText("closets");
                toolbartop.setVisibility(View.GONE);
                heading.setText("Curated");
              //  lv_toplayout.requestFocus();
                break;

            case "designer":
                tv_closetview.setText("designers");
                toolbartop.setBackgroundResource(R.drawable.designer);
                im_toplayout_img.setAlpha((float) 0.4);
                heading.setText("Designer");
              //  lv_toplayout.requestFocus();
                break;

            default:
                toolbartop.setVisibility(View.GONE);
                heading.setText("Curated");
                break;
        }


    }

    private String productCheck() {
        String param = null;
        switch (ExternalFunctions.DiffParam) {
            case "marketplace":
                param = "zap_market";
                break;

            case "curated":
                param = "zap_curated";
                break;

            case "designer":
                param = "designer";
                break;

            default:
                param = "zap_curated";
                break;
        }
        return param;

    }


    private void paramCheck(String param) {
        System.out.println("PARAMCHECK : " + param);
        if (param.endsWith("/")) {
            param = param.substring(0, param.length() - 1);
        }
        System.out.println("PARAMCHECK 1: " + param);
        switch (param) {
            case "zap_market":
                ExternalFunctions.DiffParam = "marketplace";
                headerCheck();
                break;

            case "zap_curated":
                ExternalFunctions.DiffParam = "curated";
                headerCheck();
                break;

            case "designer":
                ExternalFunctions.DiffParam = "designer";
                headerCheck();
                break;

            default:
                ExternalFunctions.DiffParam = "curated";
                headerCheck();
                break;
        }
    }


    Runnable sortDialog = new Runnable() {
        @Override
        public void run() {
            ShowSortDialog();
        }
    };


    private void InitialiseHeader() {

//        db = new DatabaseDB(getApplicationContext());
//        db.openDB();

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
                tvcount.setText(String.valueOf(CartSession.getInt("cartCount",0)));
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
                if (GetSharedValues.LoginStatus(getApplicationContext())) {

                    Intent notif = new Intent(getApplicationContext(), Notifications.class);
                    notif.putExtra("activity", "FeedPage");
                    startActivity(notif);
//                    finish();
                } else {
                    Alerts.loginAlert(BuySecondPage.this);
                }
            }
        });

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


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
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

                    recyclerview.removeAllViews();
                    if (Url.contains("sort")) {
                        if (Url.contains("?sort")) {
                            Url = Url.substring(0, Url.length() - 7);
                            Url = Url + "?sort=" + 1;
                        } else if (Url.contains("?")) {
                            Url = Url.substring(0, Url.length() - 7);
                            Url = Url + "&sort=" + 1;

                        } else {
                            Url = Url.substring(0, Url.length() - 7);
                            Url = Url + "?sort=" + 1;
                        }
                    } else {
                        if (Url.contains("?")) {
                            ExternalFunctions.sort = 1;
                            Url = Url + "&sort=" + 1;
                        } else {
                            ExternalFunctions.sort = 1;
                            Url = Url + "?sort=" + 1;
                        }
                    }

                    recyclerview.removeAllViews();
                    GetData(Url, 1);
                    alert.dismiss();

                } else {
                    ExternalFunctions.SortStatus = false;
                    if (Url.contains("sort")) {
                        Url = Url.substring(0, Url.length() - 7);
                    }
                    ExternalFunctions.sort = 0;
                    pHL.setTextColor(Color.BLACK);
                    GetData(Url, 1);
                    alert.dismiss();
                    recyclerview.removeAllViews();
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
                            Url = Url + "&sort=" + 2;
                        } else {
                            Url = Url.substring(0, Url.length() - 7);
                            Url = Url + "?sort=" + 2;
                        }
                    } else {
                        if (Url.contains("?")) {
                            ExternalFunctions.sort = 2;
                            Url = Url + "&sort=" + 2;
                        } else {
                            ExternalFunctions.sort = 2;
                            Url = Url + "?sort=" + 2;
                        }
                    }

                    ExternalFunctions.sort = 2;
                    GetData(Url, 1);
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
                    GetData(Url, 1);
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
                            Url = Url + "&sort=" + 3;
                        } else {
                            Url = Url.substring(0, Url.length() - 7);
                            Url = Url + "?sort=" + 3;
                        }
                    } else {
                        if (Url.contains("?")) {
                            ExternalFunctions.sort = 3;
                            Url = Url + "&sort=" + 3;
                        } else {
                            ExternalFunctions.sort = 3;
                            Url = Url + "?sort=" + 3;
                        }
                    }

                    ExternalFunctions.sort = 3;
                    GetData(Url, 1);
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
                    GetData(Url, 1);
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
                            Url = Url + "&sort=" + 4;
                        } else {
                            Url = Url.substring(0, Url.length() - 7);
                            Url = Url + "?sort=" + 4;
                        }
                    } else {
                        if (Url.contains("?")) {
                            ExternalFunctions.sort = 4;
                            Url = Url + "&sort=" + 4;
                        } else {
                            ExternalFunctions.sort = 4;
                            Url = Url + "?sort=" + 4;
                        }
                    }
                    GetData(Url, 1);
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

                    GetData(Url, 1);
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
                            Url = Url + "&sort=" + 5;
                        } else {
                            Url = Url.substring(0, Url.length() - 7);
                            Url = Url + "?sort=" + 5;
                        }
                    } else {
                        if (Url.contains("?")) {
                            ExternalFunctions.sort = 5;
                            Url = Url + "&sort=" + 5;
                        } else {
                            ExternalFunctions.sort = 5;
                            Url = Url + "?sort=" + 5;
                        }
                    }

                    ExternalFunctions.sort = 5;
                    GetData(Url, 1);
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
                    GetData(Url, 1);
                    alert.dismiss();
                    feeds.clear();
                    tv_sort.setTextColor(Color.BLACK);

                }
            }
        });
    }


    //  Server requests
//    ==========================================
    private void GetData(String Url, int status) {
        if (CheckConnectivity.isNetworkAvailable(this)) {
            if (status == 0) {
                blClosetLoading = true;
                if (!refreshStatus) {
                    handleProgress(START);
                }
                Intent service = new Intent(Intent.ACTION_SYNC, null, this, FeedService.class);
                service.putExtra("url", Url);
                service.putExtra("receiver", mReceiver);
                service.putExtra("Method", "Closet");
                service.putExtra("ScreenName", "FEEDPAGE");
                service.putExtra("PageNo", 1);
                startService(service);
            } else if (status == 1) {
                blProductLoading = true;
                if (!refreshStatus) {
                    handleProgress(START);
                }
                Intent service = new Intent(Intent.ACTION_SYNC, null, this, FeedService.class);
                service.putExtra("url", Url);
                service.putExtra("receiver", mReceiver);
                service.putExtra("Method", "Product");
                service.putExtra("ScreenName", "FEEDPAGE");
                service.putExtra("PageNo", 1);
                startService(service);
            } else if (status == 2) {
                System.out.println("check overlay : ");
                Intent service = new Intent(Intent.ACTION_SYNC, null, this, FeedService.class);
                service.putExtra("url", Url);
                service.putExtra("receiver", mReceiver);
                service.putExtra("Method", "Overlay");
                service.putExtra("ScreenName", "FEEDPAGE");
                service.putExtra("PageNo", 1);
                startService(service);
            }
        } else {
            handleProgress(START);
            CustomMessage.getInstance().CustomMessage(this, "Internet not available");
        }

    }


    //    Action
//    =========================================
    @Override
    public void onReceiveResult(int resultCode, final Bundle resultData) {

        switch (resultCode) {
            case FeedService.STATUS_RUNNING:
                System.out.println("SERVICE RUNNING");
                break;


            case FeedService.STATUS_FINISHED:
                String method = resultData.getString("Method");
                switch (method) {
                    case "Closet":
                        handleProgress(STOP);
                        refreshLayout.setRefreshing(false);
                        refreshStatus = false;
                        loading = false;
                        System.out.println("DATAALLLL: " + resultData.getSerializable("DATALIST"));
                        closetData.clear();
                        closetData = (ArrayList<ClosetData>) resultData.getSerializable("DATALIST");
                        System.out.println("DATAALLLL: " + closetData);
                        if (closetData != null) {
                            if (closetData.size() > 0) {
                                recyclerview.setVisibility(View.VISIBLE);
                                lv_emptylayout.setVisibility(View.GONE);
                                System.out.println("DATAALLLL: inside not null" + closetData);
                                c_adapter = new RecyclerViewClosetAdapter(closetData, this, ExternalFunctions.DiffParam);
                                recyclerview.removeAllViews();
                                try {
                                    recyclerview.setAdapter(c_adapter);
                                    // Setting the adapter to RecyclerView
                                } catch (Exception e) {

                                }
                                recyclerview.setLayoutManager(mLayoutManager);
                                c_adapter.notifyDataSetChanged();
                            } else {
                                recyclerview.setVisibility(View.INVISIBLE);
                                lv_emptylayout.setVisibility(View.VISIBLE);
                                floatingButton.setVisibility(View.GONE);
                            }
                        } else {
                            recyclerview.setVisibility(View.INVISIBLE);
                            lv_emptylayout.setVisibility(View.VISIBLE);
                            CustomMessage.getInstance().CustomMessage(this, "Unable to load feed");
                        }
                        blClosetLoading = false;
                        break;
                    case "Product":
                        handleProgress(STOP);
                        refreshLayout.setRefreshing(false);
                        refreshStatus = false;
                        loading = false;
                        feeds.clear();
                        feeds = (ArrayList<HomeFeedItem>) resultData.getSerializable("DATALIST");
                        if (feeds != null) {
                            if (feeds.size() > 0) {
                                recyclerview.setVisibility(View.VISIBLE);
                                lv_refineLayout.setVisibility(View.VISIBLE);
                                lv_emptylayout.setVisibility(View.GONE);
                                System.out.println("DATAALLLL: inside not null" + closetData);
                                ProductAsync obj=new  ProductAsync( this,recyclerview,feeds);
                                obj.execute(null,null,null);
//                                p_adapter = new RecyclerViewProductAdapter(feeds, this, ExternalFunctions.DiffParam);
//                                recyclerview.removeAllViews();
//                                try {
//                                    recyclerview.setAdapter(p_adapter);                              // Setting the adapter to RecyclerView
//                                } catch (Exception e) {
//
//                                }
//                                recyclerview.setLayoutManager(mGridLayoutManager);
//                                p_adapter.notifyDataSetChanged();
                                System.out.println("Datalist : "+ExternalFunctions.strOverlayurl);
                                if (ExternalFunctions.strOverlayurl.length() == 0) {
                                    String overlay_URL = EnvConstants.APP_BASE_URL + "/marketing/overlay/feed";
                                    GetData(overlay_URL, 2);
                                }
                                ExternalFunctions.strOverlayurl = "";
                            } else {
                                recyclerview.setVisibility(View.INVISIBLE);
                                lv_emptylayout.setVisibility(View.VISIBLE);
//                                lv_refineLayout.setVisibility(View.INVISIBLE);
                                floatingButton.setVisibility(View.GONE);
                            }

                        } else {
                            recyclerview.setVisibility(View.INVISIBLE);
                            lv_emptylayout.setVisibility(View.VISIBLE);
//                            lv_refineLayout.setVisibility(View.INVISIBLE);
                            CustomMessage.getInstance().CustomMessage(this, "Unable to load feed");
                        }
                        blProductLoading = false;
                        break;

                    case "ProductMore":
                        loading = false;
                        gridloader.setVisibility(View.GONE);
                        feeds.addAll((ArrayList<HomeFeedItem>) resultData.getSerializable("DATALIST"));
                        p_adapter.notifyDataSetChanged();
                        break;

                    case "ClosetMore":
                        System.out.println("INSIDE CLOSET MORE : ");
                        loading = false;
                        if (closetData.size() > 0) {
                            closetData.remove(closetData.size() - 1);
                        }
                        closetData.addAll((ArrayList<ClosetData>) resultData.getSerializable("DATALIST"));
                        c_adapter.notifyDataSetChanged();
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
//                                                                    ExternalFunctions.showOverlay(BuySecondPage.this, "", "", "", activityname, bl_fullscreen, bl_close, strimage);
                                                                    ExternalFunctions.showOverlay(BuySecondPage.this, strtilte, str_description, strbutton, activityname, bl_fullscreen, bl_close, strimage);

                                                                } catch (Exception e) {

                                                                }
                                                            }


                                                        }
                                                    });

                                                }
                                            }, 500, 500);
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


//    Handle progressbar
//    ======================================

    private void handleProgress(final String event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (event) {
                    case "START":
                        recyclerview.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.VISIBLE);
                        break;

                    case "STOP":
                        recyclerview.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        break;

                    default:
                        break;
                }
            }
        });
    }






//    Activity functions
//    ==============================================


    @Override
    protected void onDestroy() {
        super.onDestroy();

        System.out.println("FINISHH : destry");
        Runtime.getRuntime().gc();
        if (feeds.size() > 0) {
            feeds.clear();
        }
        if (closetData.size() > 0) {
            closetData.clear();
        }
        int count1 = recyclerview.getChildCount();
        for (int i = 0; i < count1; i++) {
            RoundedImageView rv = (RoundedImageView) recyclerview.getChildAt(i).findViewById(R.id.user_image);
            ImageView v = (ImageView) recyclerview.getChildAt(i).findViewById(R.id.hv_image);
            ImageView v1 = (ImageView) recyclerview.getChildAt(i).findViewById(R.id.feedgrid);
            if (rv != null) {
                if (rv.getDrawable() != null)
                    rv.getDrawable().setCallback(null);

                Glide.clear(rv);
            }
            if (v != null) {
                if (v.getDrawable() != null)
                    v.getDrawable().setCallback(null);
                Glide.clear(v);
            }

            if (v1 != null) {
                if (v1.getDrawable() != null)
                    v1.getDrawable().setCallback(null);
                Glide.clear(v1);
            }
        }



        ExternalFunctions.FilteredString = "";
        ExternalFunctions.strOverlayurl = "";
    }


    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("FINISHH : stop");
        Runtime.getRuntime().gc();

//        int count1 = recyclerview.getChildCount();
//        for (int i = 0; i < count1; i++) {
//            RoundedImageView rv = (RoundedImageView) recyclerview.getChildAt(i).findViewById(R.id.user_image);
//            ImageView v = (ImageView) recyclerview.getChildAt(i).findViewById(R.id.hv_image);
//            ImageView v1 = (ImageView) recyclerview.getChildAt(i).findViewById(R.id.feedgrid);
//            if (rv != null) {
//                if (rv.getDrawable() != null)
//                    rv.getDrawable().setCallback(null);
//
//                Glide.clear(rv);
//            }
//            if (v != null) {
//                if (v.getDrawable() != null)
//                    v.getDrawable().setCallback(null);
//                Glide.clear(v);
//            }
//
//            if (v1 != null) {
//                if (v1.getDrawable() != null)
//                    v1.getDrawable().setCallback(null);
//                Glide.clear(v1);
//            }
//        }




        ExternalFunctions.FilteredString = "";
        ExternalFunctions.strOverlayurl = "";
    }

    @Override
    protected void onResume() {
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

    }

    @Override
    public void onResponseCallback(JSONObject response, String flag) {

    }

    @Override
    public void onErrorCallback(VolleyError error, String flag) {

    }

//
////    AsyncTask
////    ======================================
//
//
//    public class DbOperations extends AsyncTask<Void, Void, String> {
//        String data;
//        String out = null;
//
//        @Override
//        protected String doInBackground(Void... params) {
//
//            System.out.println("DBOPERATION BS");
//            if (switchStatus == 0) {
//                System.out.println("DBOPERATION BS FIRST " + db.getTableRecordsCount("CLOSET"));
//                if (db.getTableRecordsCount("CLOSET") > 0) {
//                    String query = "SELECT * FROM " + "CLOSET" + " where pageno = '" + 1 + "'";
//                    Cursor cursordata = db.getData(query);
//                    data = cursordata.getString(1).replace("z*p*", "'");
//                    out = "CLOSET";
//                    System.out.println("DBDATABASE : CLOSET : " + data);
//                }
//            } else {
//                System.out.println("DBOPERATION BS SECOND " + db.getTableRecordsCount("FEED"));
//                if (db.getTableRecordsCount("FEED") > 0) {
//                    String query = "SELECT * FROM " + "FEED" + " where pageno = '" + 1 + "'";
//                    Cursor cursordata = db.getData(query);
//                    data = cursordata.getString(1).replace("z*p*", "'");
//                    out = "FEED";
//                    System.out.println("DBDATABASE : FEED : " + data);
//                }
//            }
//            return out;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            System.out.println("DBOPERATION BS POSTEXECUTE : " + s + "___" + out + "___" + data);
//            if (out != null) {
//                System.out.println("DBOPERATION BS POSTEXECUTE NULLCHECK IF");
//                if (s.equals("CLOSET")) {
//                    System.out.println("DBOPERATION BS POSTEXECUTE CLOSET");
//                    JSONObject dataObject = null;
//                    try {
//                        dataObject = new JSONObject(data);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//
//                    }
//                    JSONObject Maindata = JsonParser.getInstance().parserJsonObject(dataObject);
//                    JSONObject data = null;
//                    try {
//                        data = Maindata.getJSONObject("data");
//                        JSONArray dataArray = data.getJSONArray("data");
//                        ArrayList<ClosetData> dataList = new ArrayList<ClosetData>();
//                        System.out.println("DBOPERATION ARRAY LIST" + dataArray);
//                        for (int i = 0; i < dataArray.length(); i++) {
//                            ClosetData closetdata = new ClosetData();
//                            closetdata.setUserName(dataArray.getJSONObject(i).getString("zap_username"));
//                            closetdata.setAdmire(dataArray.getJSONObject(i).getBoolean("admiring"));
////                            closetdata.setProducts(dataArray.getJSONObject(i).getJSONArray("product"));
//                            ArrayList<SingleItemData> singleItemDatas = new ArrayList<SingleItemData>();
//                            for (int j = 0; j < dataArray.getJSONObject(i).getJSONArray("product").length(); j++) {
//                                SingleItemData itemData = new SingleItemData();
//                                try {
//                                    System.out.println("DBOPERATION ARRAY2" + dataArray.getJSONObject(i).getJSONArray("product").getJSONObject(j).getString("image"));
//                                    itemData.setImageUrl(dataArray.getJSONObject(i).getJSONArray("product").getJSONObject(j).getString("image"));
//                                    itemData.setProductId(dataArray.getJSONObject(i).getJSONArray("product").getJSONObject(j).getInt("id"));
//                                    itemData.setTitle(dataArray.getJSONObject(i).getJSONArray("product").getJSONObject(j).getString("title"));
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                                singleItemDatas.add(itemData);
//                            }
//                            closetdata.setProducts(singleItemDatas);
//                            closetdata.setUserId(dataArray.getJSONObject(i).getInt("id"));
//                            closetdata.setUserImage(dataArray.getJSONObject(i).getString("profile_pic"));
//                            dataList.add(closetdata);
//                            handleProgress(STOP);
//                            loading = false;
//                            closetData.clear();
//                            closetData.addAll(dataList);
//                            System.out.println("DATAALLLL DATALIST: " + dataList);
//                            if (closetData != null) {
//                                System.out.println("DATAALLLL: inside not null" + closetData);
//                                c_adapter = new RecyclerViewClosetAdapter(closetData, BuySecondPage.this, ExternalFunctions.DiffParam);
//                                recyclerview.removeAllViews();
//                                try {
//                                    recyclerview.setAdapter(c_adapter);
//                                } catch (Exception e) {
//
//                                }
//                                // Setting the adapter to RecyclerView
//                                recyclerview.setLayoutManager(mLayoutManager);
//                                recyclerview.setNestedScrollingEnabled(false);
//                                c_adapter.notifyDataSetChanged();
//                            } else {
//                                CustomMessage.getInstance().CustomMessage(BuySecondPage.this, "Unable to load feed");
//                            }
//
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                } else {
//                    System.out.println("DBOPERATION BS POSTEXECUTE PRODUCT");
//                    JSONObject dObject = null;
//                    try {
//                        dObject = new JSONObject(data);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        System.out.println("JSONEXCEPTION: " + e + "=" + data);
//                    }
//
//
//                    System.out.println("DBOPERATION BS POSTEXECUTE PRODUCT : object : " + dObject);
//                    ArrayList<HomeFeedItem> temp = JsonParser.getInstance().parserFeed(dObject);
//                    handleProgress(STOP);
//                    loading = false;
//                    feeds.clear();
//                    try {
//                        feeds.addAll(temp);
//                    } catch (Exception e) {
//
//                    }
//                    if (feeds != null) {
//                        System.out.println("DATAALLLL: inside not null" + closetData);
//                        ProductAsync obj=new  ProductAsync(BuySecondPage.this,recyclerview,feeds);
//                        obj.execute(null,null,null);
////                        p_adapter = new RecyclerViewProductAdapter(feeds, BuySecondPage.this, ExternalFunctions.DiffParam);
////                        recyclerview.removeAllViews();
////                        try {
////                            recyclerview.setAdapter(p_adapter);                              // Setting the adapter to RecyclerView
////                        }
////                        catch (Exception e){
////
////                        }
////                        recyclerview.setLayoutManager(mGridLayoutManager);
////                        p_adapter.notifyDataSetChanged();
//
//                    } else {
//                        CustomMessage.getInstance().CustomMessage(BuySecondPage.this, "Unable to load feed");
//                    }
//                }
//            } else {
//                handleProgress(STOP);
//                System.out.println("DBOPERATION BS POSTEXECUTE NULLCHECK ELSE");
//            }
//        }
//
//    }

    public class ProductAsync extends AsyncTask<String, String, String> {
        RecyclerView  rv;
        ArrayList<HomeFeedItem> feeds ;
        Context context;
        ProductAsync(Context ctc,RecyclerView  v,  ArrayList<HomeFeedItem> feeds ){
            this.rv=v;
            this.feeds=feeds;
            this.context=ctc;

        }
        @Override
        protected String doInBackground(String... params) {
            p_adapter = new RecyclerViewProductAdapter(feeds, context, ExternalFunctions.DiffParam,rv, "BuySecondPage");

            return null;
        }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                recyclerview.removeAllViews();
                try {
                    recyclerview.setAdapter(p_adapter);
                } catch (Exception e) {

                }
                recyclerview.setLayoutManager(mGridLayoutManager);
                p_adapter.notifyDataSetChanged();
            }



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
        if (callingActvity == null)
            callingActvity = "HomePageNew";
        if (callingActvity.contains("HomePageNew")) {
            Intent buy = new Intent(BuySecondPage.this, HomePageNew.class);
            startActivity(buy);
            finish();

        } else {
            Intent home = new Intent(BuySecondPage.this, HomePageNew.class);
            startActivity(home);
            finish();

        }
    }
}

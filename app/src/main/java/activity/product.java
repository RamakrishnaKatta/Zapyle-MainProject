package activity;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.exceptions.CleverTapMetaDataNotFoundException;
import com.clevertap.android.sdk.exceptions.CleverTapPermissionsNotSatisfied;

import com.freshdesk.hotline.Hotline;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
//import com.squareup.picasso.Picasso;
////import com.uxcam.UXCam;
import com.zapyle.zapyle.Alerts;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import DataBase.DatabaseDB;
import application.MyApplicationClass;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.SharingHelper;
import io.branch.referral.util.LinkProperties;
import io.branch.referral.util.ShareSheetStyle;
import network.ApiCommunication;
import network.ApiService;
import network.JsonParser;
import utils.CustomMessage;
import utils.ExternalFunctions;
import utils.FontUtils;
import utils.GetSharedValues;


public class product extends ActionBarActivity implements ApiCommunication {

    //    edited
    String SCREEN_NAME = "PRODUCT_PAGE";
    Integer ALBUM_ID, height, width, selectedSize, buttonwidth, Qty, QtyMax, ProfileUserId;
    ArrayList<String> images = new ArrayList<String>();
    ArrayList<JSONObject> SizeTable = new ArrayList<JSONObject>();
    HashMap<Integer, Integer> NoOfProductsTable = new HashMap<Integer, Integer>();
    Handler handler = new Handler();
    JSONArray offerdata = new JSONArray();


    Boolean pta = false;
    Boolean BuyStatus = false;
    Boolean AdmireStatus = false;
    Boolean FS_Status = false;
    RelativeLayout rl, p_bottomlayout;
    Date today;
    public static String starttime;
    ScrollView product_scrollview;
    String CategoryType, profile_user_name;
    String callingActivity = "discover";
    String userType, size_type;
    ProgressBar progressBar;
    ProgressDialog progress;
    int CommentCount = 0;
    int LikeCount = 0;
    Boolean HomePageStatus = false;
    LinearLayout lnprod;
    String ForwardUrl;


    private Timer t;
    DatabaseDB db;
    private int TimeCounter = 0;
    private static final String PAGE = "albumId";
    private static final String BODY = "json";
    public int DIALOGE_UP = 0;
    public int DIALOGE_DOWN = 1;
    int shipping_charge = 0;
    Integer original_price = 0;
    Integer discount = 0;
    Integer total_price = 0;
    Integer listing_price = 0;


    String product_brand;
    String title;
    String product_size;
    String product_style;
    Tracker mTracker;
    Boolean overlaystatus = false;


    ImageView profileImage;
    TextView tvcount,
            tvUsername,
            tvAdmire,
            tvProductname,
            tvListprice,
            tvOriginalprice,
            tvDiscount,
            tvDescription,
            tvMainlovetext,
            tvCommenttextmain,
            tvShareTextmain,
            tvBrand,
            tvSizeguide,
            tvsize,
            tvage,
            tvCondition,
            tvColor,
            tvStyle,
            tvOccasion,
            tvBuyerprotection,
            tvShoppingreturns,
            tvLikecount,
            tvUnlikecount,
            tvcommentcount,
            tvsharecount,
            tvBuy,
            tvMoreCtaText,
            tvEdit,
            tvDelete,
            tvCategory,
            tvTitlehead,
            tvSizeguideText,
            tvQuantityAlert,
            tvQuantity,
            tvQminus,
            tvQplus,
            tvQuantityheading,
            tvReturnText,
            tvTimer,
            tvFlashDiscount,
            tvFlashBannerDiscount,
            tvFlashSaleTitle,
            tvPrdoductPageTitle,
            tvChat;

    ViewPager viewPager;
    ImageView imMainlove,
            imCommentmain,
            imLike,
            imUnlike,
            imComment,
            imShare,
            imMoreCtaImage;
    LinearLayout lvIndicator,
            lvMainlovelayout,
            lvCommentMain,
            lvBrand,
            lvSize,
            lvage,
            lvcondition,
            lvMore,
            lvMoreaction,
            lvColor,
            lvStyle,
            lvOccasion,
            lvLikeplaceholder,
            lvunLikeplaceholder,
            lvEDlayout,
            lvFClayout,
            lvBuyLayout,
            lvSeperation,
            lvSizeDialog,
            lvBottomSAlayout,
            lvProductSizeHolder,
            lvQuantityLayout,
            lvAuthenticityLayout,
            lvReturnLayout,
            lvFlashsaleLayout,
            lvOfferlayout;


    RelativeLayout rl_productHeaderLayout;


    public ImageButton[] imageButtons;
    int indicatorcount = 0;
    SliderAdaptor sliderAdaptor;
    public int BuyCheck = 0;
    CountDownTimer countDownTimer;
    CleverTapAPI cleverTap;
    String stime = "";
    String endtime = "";
    String selectedOffer = null;
    int selectedOfferId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_page);
        MyApplicationClass application = (MyApplicationClass) getApplication();
        mTracker = application.getDefaultTracker();
//        //UXCam.startWithKey("1dfb25141864376");
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        progressBar = (ProgressBar) findViewById(R.id.p_progressBar);
        // lnprod=(LinearLayout) findViewById(R.id.lnprod);
        product_scrollview = (ScrollView) findViewById(R.id.product_scrollview);
        product_scrollview.setVisibility(View.INVISIBLE);
        try {
            cleverTap = CleverTapAPI.getInstance(getApplicationContext());
        } catch (CleverTapMetaDataNotFoundException e) {
            // thrown if you haven't specified your CleverTap Account ID or Token in your AndroidManifest.xml
        } catch (CleverTapPermissionsNotSatisfied e) {
            // thrown if you haven’t requested the required permissions in your AndroidManifest.xml
        }
        stime = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        String strAction = null;
        String strPath = null;
        try {
            Intent intent = getIntent();
            strAction = intent.getAction();
            Uri data = intent.getData();
            strPath = data.getPath();
            System.out.println("weblink" + strPath);
        } catch (Exception e) {

        }


        //        Database function
//        ----------------------------------------------------
        db = new DatabaseDB(getApplicationContext());
        db.openDB();

        progress = new ProgressDialog(product.this);
        progress.setCancelable(false);

        ExternalFunctions.bloverlay = false;
        p_bottomlayout = (RelativeLayout) findViewById(R.id.p_bottomlayout);
        p_bottomlayout.setVisibility(View.INVISIBLE);
        SharedPreferences settings = getSharedPreferences("MixPanelSession",
                Context.MODE_PRIVATE);


        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        height = displaymetrics.heightPixels;
        width = displaymetrics.widthPixels;
        buttonwidth = ((width - 50) / 200);

        SharedPreferences.Editor editor = settings.edit();
        int productviewCount = settings.getInt("productviewCount", 0);
        editor.putInt("productviewCount", productviewCount + 1);
        editor.apply();
        ALBUM_ID = getIntent().getIntExtra("album_id", 0);
        pta = getIntent().getBooleanExtra("pta", true);
        try {
            callingActivity = "activity." + getIntent().getStringExtra("activity");
            if (getIntent().getStringExtra("ForwardUrl") != null || getIntent().getStringExtra("ForwardUrl").toString().isEmpty()) {
                ALBUM_ID = Integer.valueOf(getIntent().getStringExtra("ForwardUrl"));
                ForwardUrl = EnvConstants.URL_FEED + "/singleproduct/" + getIntent().getStringExtra("ForwardUrl") + "/an/?action=p_t_a";
                HomePageStatus = true;
            }
        } catch (Exception e) {
            callingActivity = "activity." + "discover";
        }

        if (ExternalFunctions.strOverlayurl.length() > 1) {
            ForwardUrl = ExternalFunctions.strOverlayurl;
            HomePageStatus = true;
        }


        try {
            callingActivity = "activity." + getIntent().getStringExtra("activity");

        } catch (Exception e) {
            callingActivity = "activity." + "discover";
        }

        hideSoftKeyboard();
        rl = (RelativeLayout) findViewById(R.id.productLayout);
        FontUtils.setCustomFont(rl, getAssets());
        today = new Date();
        DateFormat dfstarttime = new SimpleDateFormat("hh:mm:ss aa");
        dfstarttime.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        starttime = dfstarttime.format(today);


        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final View customView = getLayoutInflater().inflate(R.layout.product_bar_cart, null);
        getSupportActionBar().setCustomView(customView);
        Toolbar parent = (Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0, 0);
        tvTitlehead = (TextView) customView.findViewById(R.id.product_title_text);


        RelativeLayout cart = (RelativeLayout) findViewById(R.id.cartClick);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shopcart = new Intent(product.this, Cart.class);
                shopcart.putExtra("AlbumId", ALBUM_ID);
                startActivity(shopcart);
                finish();
            }
        });

        tvcount = (TextView) customView.findViewById(R.id.tvcount);
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


        ImageView imgback = (ImageView) customView.findViewById(R.id.imgback);
        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


//        Placeholders
//        -----------------------------------------------------------------
        profileImage = (ImageView) findViewById(R.id.product_profileimage);
        viewPager = (ViewPager) findViewById(R.id.viewpager);


        viewPager.getLayoutParams().height = GetSharedValues.getScreenWidth(this);
        viewPager.getLayoutParams().width = GetSharedValues.getScreenWidth(this);

        rl_productHeaderLayout = (RelativeLayout) findViewById(R.id.productHeaderLayout);
        tvUsername = (TextView) findViewById(R.id.product_profilename);
        tvQuantityheading = (TextView) findViewById(R.id.quantityHeading);
        tvAdmire = (TextView) findViewById(R.id.admire_button);
        tvProductname = (TextView) findViewById(R.id.productname);
        tvListprice = (TextView) findViewById(R.id.p_listingPrice);
        tvOriginalprice = (TextView) findViewById(R.id.p_originalPrice);
        tvOriginalprice.setPaintFlags(tvOriginalprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        tvDiscount = (TextView) findViewById(R.id.product_discount);
        tvDescription = (TextView) findViewById(R.id.p_description);
        tvMainlovetext = (TextView) findViewById(R.id.tvLovetext);
        tvCommenttextmain = (TextView) findViewById(R.id.commentTextMain);
        tvBrand = (TextView) findViewById(R.id.brand);
        tvSizeguide = (TextView) findViewById(R.id.sizeguide);
        tvsize = (TextView) findViewById(R.id.size);
        tvage = (TextView) findViewById(R.id.age);
        tvCondition = (TextView) findViewById(R.id.condition);
        tvColor = (TextView) findViewById(R.id.color);
        tvStyle = (TextView) findViewById(R.id.style);
        tvOccasion = (TextView) findViewById(R.id.occasion);
        tvBuyerprotection = (TextView) findViewById(R.id.buyerprotection);
        tvShoppingreturns = (TextView) findViewById(R.id.shippingreturns);
        tvLikecount = (TextView) findViewById(R.id.likecount);
        tvUnlikecount = (TextView) findViewById(R.id.unlikecount);
        tvcommentcount = (TextView) findViewById(R.id.commentCount);
        tvsharecount = (TextView) findViewById(R.id.sharecount);
        tvBuy = (TextView) findViewById(R.id.buybutton);
        tvMoreCtaText = (TextView) findViewById(R.id.more_cta_text);
        tvEdit = (TextView) findViewById(R.id.tvEdit);
        tvDelete = (TextView) findViewById(R.id.tvDelete);
        tvCategory = (TextView) findViewById(R.id.category);
        tvSizeguideText = (TextView) findViewById(R.id.sizeguide_textview);
        tvQuantityAlert = (TextView) findViewById(R.id.quantityAlert);
        tvQuantity = (TextView) findViewById(R.id.Quantity);
        tvQminus = (TextView) findViewById(R.id.qminus);
        tvQplus = (TextView) findViewById(R.id.qplus);
        tvReturnText = (TextView) findViewById(R.id.returnText);
        tvTimer = (TextView) findViewById(R.id.timer);
        tvFlashDiscount = (TextView) findViewById(R.id.flash_discount);
        tvFlashBannerDiscount = (TextView) findViewById(R.id.flash_banner_discount);
        tvFlashSaleTitle = (TextView) findViewById(R.id.flash_sale_title);
        tvPrdoductPageTitle = (TextView) findViewById(R.id.producttitle);
        tvChat = (TextView) findViewById(R.id.chat);


        imMainlove = (ImageView) findViewById(R.id.imLovemain);
        imCommentmain = (ImageView) findViewById(R.id.commentClickMain);

//        imsharemain = (ImageView) findViewById(R.id.shareclickmain);
        imLike = (ImageView) findViewById(R.id.product_like);
        imUnlike = (ImageView) findViewById(R.id.product_unlike);
        imComment = (ImageView) findViewById(R.id.product_comment);
        imShare = (ImageView) findViewById(R.id.product_share);
        imMoreCtaImage = (ImageView) findViewById(R.id.more_cta_image);

        lvIndicator = (LinearLayout) findViewById(R.id.indicator_layout);
        lvQuantityLayout = (LinearLayout) findViewById(R.id.lv_quantity);
        lvReturnLayout = (LinearLayout) findViewById(R.id.returnLayout);
        lvAuthenticityLayout = (LinearLayout) findViewById(R.id.authenticityLayout);

        lvMainlovelayout = (LinearLayout) findViewById(R.id.lvLovelayout);
        lvCommentMain = (LinearLayout) findViewById(R.id.commentLayout);
//        lvSharemain = (LinearLayout) findViewById(R.id.shareLayout);
        lvBrand = (LinearLayout) findViewById(R.id.brandLayout);
        lvSize = (LinearLayout) findViewById(R.id.sizelayout);
        lvage = (LinearLayout) findViewById(R.id.agelayout);
        lvcondition = (LinearLayout) findViewById(R.id.conditionlayout);
        lvMore = (LinearLayout) findViewById(R.id.more_cta);
        lvMoreaction = (LinearLayout) findViewById(R.id.more_action);
        lvOfferlayout = (LinearLayout) findViewById(R.id.offer_layout);

        lvColor = (LinearLayout) findViewById(R.id.colorlayout);
        lvStyle = (LinearLayout) findViewById(R.id.stylelayout);
        lvOccasion = (LinearLayout) findViewById(R.id.occasionlayout);
        lvLikeplaceholder = (LinearLayout) findViewById(R.id.likeplaceholder);
        lvunLikeplaceholder = (LinearLayout) findViewById(R.id.unlikeplaceholder);
        lvEDlayout = (LinearLayout) findViewById(R.id.EDLayout);
        lvFClayout = (LinearLayout) findViewById(R.id.fcLayout);
        lvBuyLayout = (LinearLayout) findViewById(R.id.buybuttonlayout);
        lvSeperation = (LinearLayout) findViewById(R.id.sepration_style_color);
        lvSizeDialog = (LinearLayout) findViewById(R.id.MainSizeDialoglayout);
        lvBottomSAlayout = (LinearLayout) findViewById(R.id.bottomSAlayout);
        lvProductSizeHolder = (LinearLayout) findViewById(R.id.ProductSizeHolder);
        lvFlashsaleLayout = (LinearLayout) findViewById(R.id.flash_sale_layout);


        lvLikeplaceholder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p_unlike();
            }
        });

        lvunLikeplaceholder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p_like();
            }
        });


        lvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                lvMore.setVisibility(View.GONE);
                if (lvSizeDialog.getVisibility() == View.VISIBLE) {
                    handleDialogueUiThread(DIALOGE_DOWN);
                } else {
                    if (tvMoreCtaText.getText().equals("more details")) {
                        lvMoreaction.setVisibility(View.VISIBLE);
                        // YoYo.with(Techniques.FadeIn).duration(600).playOn(lvMoreaction);
                        tvMoreCtaText.setText("less details");
                        imMoreCtaImage.setImageResource(R.drawable.grey_up);
                    } else {
                        // YoYo.with(Techniques.FadeIn).duration(600).playOn(lvMoreaction);
                        lvMoreaction.setVisibility(View.GONE);
                        tvMoreCtaText.setText("more details");
                        imMoreCtaImage.setImageResource(R.drawable.grey_down);
                    }
                }
            }
        });

        imShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Share();
            }
        });
        tvSizeguideText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sizeGuideIntent();
            }
        });

        tvChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Hotline.showConversations(getApplicationContext());
            }
        });


        String cmt = "Buyer Protection";
        SpannableString content = new SpannableString(cmt);
        content.setSpan(new UnderlineSpan(), 0, cmt.length(), 0);
        tvBuyerprotection.setText(content);

        String cmt1 = getString(R.string.shipndreturns);
        SpannableString content1 = new SpannableString(getString(R.string.shipndreturns));
        content1.setSpan(new UnderlineSpan(), 0, cmt1.length(), 0);

        tvShoppingreturns.setText(content1);
        lvAuthenticityLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lvSizeDialog.getVisibility() == View.VISIBLE) {
                    handleDialogueUiThread(DIALOGE_DOWN);
                } else {
                    Intent bp = new Intent(product.this, BuyerProtection.class);
                    bp.putExtra("album_id", ALBUM_ID);
                    bp.putExtra("pta", false);
                    startActivity(bp);
                }
            }
        });

        lvReturnLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lvSizeDialog.getVisibility() == View.VISIBLE) {
                    handleDialogueUiThread(DIALOGE_DOWN);
                } else {
                    Intent sr = new Intent(product.this, ShippingReturns.class);
                    sr.putExtra("album_id", ALBUM_ID);
                    sr.putExtra("pta", false);
                    startActivity(sr);
                }
            }
        });

        if (strAction != null) {
            getParamFromUrl(strPath);
        } else {
            GetProductdata(ALBUM_ID);
        }


        product_scrollview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (lvSizeDialog.getVisibility() == View.VISIBLE) {
                    handleDialogueUiThread(DIALOGE_DOWN);
                } else {
                    if (BuyCheck == 0) {
                        if (!tvBuy.getText().toString().contains("Sold")) {
                            tvBuy.setText("ADD TO TOTE");
                        }
                    }

                }
                return false;
            }
        });


        lvSizeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("TOUCHESH");
            }
        });


        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                if (lvSizeDialog.getVisibility() == View.VISIBLE) {
                    handleDialogueUiThread(DIALOGE_DOWN);
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < images.size(); i++) {
                                imageButtons[i].clearAnimation();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    imageButtons[i].setBackground(ContextCompat.getDrawable(product.this, R.drawable.white_round_bg));
                                }
                            }
                            Animation logoMoveAnimation = AnimationUtils.loadAnimation(product.this, R.anim.indicator_anim);
                            imageButtons[position].startAnimation(logoMoveAnimation);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                imageButtons[position].setBackground(ContextCompat.getDrawable(product.this, R.drawable.black_round_bg));
                            }
                        }
                    });

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        tvQminus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("inside tv qminus");
                if (lvSizeDialog.getVisibility() == View.VISIBLE) {
//                    if (GetSharedValues.LoginStatus(product.this)) {
                    if (selectedSize != null) {
                        if (Integer.parseInt(tvQuantity.getText().toString()) <= 1) {
//                            customToast = new CustomToast(product.this, "quantity is 0");
//                            qminus.setEnabled(false);
                        } else {
                            int qty = Integer.parseInt(tvQuantity.getText().toString()) - 1;
                            tvQuantity.setText(String.valueOf(qty));
                        }
                    } else {
                        CustomMessage.getInstance().CustomMessage(product.this, "Select size");

                    }
//                    } else {
//                        Alerts.loginAlert(product.this);
//                    }
                }
            }
        });

        tvQplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("inside tv qplus");
                if (lvSizeDialog.getVisibility() == View.VISIBLE) {
//                    if (GetSharedValues.LoginStatus(product.this)) {

                    if (selectedSize != null) {
                        if (Integer.parseInt(tvQuantity.getText().toString()) < Qty) {
                            int qty = Integer.parseInt(tvQuantity.getText().toString()) + 1;
                            tvQuantity.setText(String.valueOf(qty));

                        } else {

                        }
                    } else {
                        CustomMessage.getInstance().CustomMessage(product.this, "Select size");
                    }
//                    } else {
//                        Alerts.loginAlert(product.this);
//                    }
                }
            }
        });


        tvSizeguide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sizeGuideIntent();
            }
        });

    }

    private void getParamFromUrl(String path) {
        try {
            System.out.println("check url" + path);
            int lastindex, length;
            lastindex = path.lastIndexOf("/");

            length = path.length();
            String strParam = path.substring(lastindex + 1, length);
            String strParam1 = path.substring(1, lastindex);
            if (TextUtils.isDigitsOnly(strParam)) {
                ALBUM_ID = Integer.parseInt(strParam);
            } else {
                lastindex = strParam1.lastIndexOf("/");

                length = strParam1.length();
                strParam = strParam1.substring(lastindex + 1, length);
                ALBUM_ID = Integer.parseInt(strParam);
            }


            GetProductdata(ALBUM_ID);
        } catch (NullPointerException e) {

        }

    }

    private void sizeGuideIntent() {
        if (CategoryType.contains("C")) {
            Intent guide = new Intent(product.this, SizeGuide.class);
            guide.putExtra("album_id", ALBUM_ID);
            guide.putExtra("pta", false);
            guide.putExtra("activity", "product");
            guide.putExtra("TYPE", "C");
            startActivity(guide);
            finish();
        } else if (CategoryType.contains("FW")) {
            Intent guide = new Intent(product.this, SizeGuide.class);
            guide.putExtra("album_id", ALBUM_ID);
            guide.putExtra("pta", false);
            guide.putExtra("activity", "product");
            guide.putExtra("TYPE", "FW");
            startActivity(guide);
            finish();
        } else {
            Intent guide = new Intent(product.this, SizeGuide.class);
            guide.putExtra("album_id", ALBUM_ID);
            guide.putExtra("pta", false);
            guide.putExtra("activity", "product");
            guide.putExtra("TYPE", "C");
            startActivity(guide);
            finish();
        }
    }


//    Share album
//    =====================


    public void Share() {
        BranchUniversalObject branchUniversalObject = null;


        branchUniversalObject = new BranchUniversalObject()
                .setCanonicalIdentifier("item/12345")
                .setTitle(tvProductname.getText().toString())
                .setContentDescription(tvDescription.getText().toString())
                .setContentImageUrl(EnvConstants.APP_MEDIA_URL + images.get(0))
                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .addContentMetadata("action_type", "product")
                .addContentMetadata("target", String.valueOf(ALBUM_ID))
                .addContentMetadata("social", "false")
                .addContentMetadata("user", "000");


        LinkProperties linkProperties = new LinkProperties()
                .setChannel("facebook")
                .setFeature("sharing")
                .addControlParameter("$desktop_url", "http://zapyle.com/#/product/" + ALBUM_ID)
                .addControlParameter("$ios_url", "http://zapyle.com/#/product/" + ALBUM_ID);
        // .addControlParameter("$zapyle", "item/12345");


        ShareSheetStyle shareSheetStyle = new ShareSheetStyle(product.this, "zapyle", "Check this out -" + tvProductname.getText().toString())
                //  .setCopyUrlStyle(R.id., copyUrlMessage, copiedUrlMessage)
                // .setMoreOptionStyle(getResources().getDrawable(android.R.drawable.ic_menu_search), "More options")
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.FACEBOOK)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.EMAIL)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.MESSAGE)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.TWITTER);

        branchUniversalObject.showShareSheet(product.this, linkProperties, shareSheetStyle, null);
    }


    public void ShareSmall(View v) {
        BranchUniversalObject branchUniversalObject = null;


        branchUniversalObject = new BranchUniversalObject()
                .setCanonicalIdentifier("item/12345")
                .setTitle(tvProductname.getText().toString())
                .setContentDescription(tvDescription.getText().toString())
                .setContentImageUrl(EnvConstants.APP_MEDIA_URL + images.get(0))
                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .addContentMetadata("albumid", String.valueOf(ALBUM_ID));


        LinkProperties linkProperties = new LinkProperties()
                .setChannel("facebook")
                .setFeature("sharing")
//                .addControlParameter("$desktop_url", "http://zapyle.com/#/product/" + ALBUM_ID)
//                .addControlParameter("$ios_url", "http://zapyle.com/#/product/" + ALBUM_ID)
                .addControlParameter("$android_url", "http://appesoftproduct.com/app-production-debug.apk");

        // .addControlParameter("$zapyle", "item/12345");


        ShareSheetStyle shareSheetStyle = new ShareSheetStyle(product.this, "zapyle", "Check this out -" + tvProductname.getText().toString())
                //  .setCopyUrlStyle(R.id., copyUrlMessage, copiedUrlMessage)
                // .setMoreOptionStyle(getResources().getDrawable(android.R.drawable.ic_menu_search), "More options")
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.FACEBOOK)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.EMAIL)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.MESSAGE)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.TWITTER);

        branchUniversalObject.showShareSheet(product.this, linkProperties, shareSheetStyle, null);
    }

//    Edit album
//    ====================


    public void EditAlbum(View v) {
        if (GetSharedValues.LoginStatus(product.this)) {
            Intent upload1 = new Intent(product.this, upload.class);
            ExternalFunctions.uploadbackcheck = 0;
            upload1.putExtra("EditStatus", true);
            upload1.putExtra("ProductId", ALBUM_ID);
            upload1.putExtra("PTA", pta);
            startActivity(upload1);
            finish();


        } else {
            Alerts.loginAlert(product.this);
        }
    }


//    Delete ALbum

    public void DeleteAlbum(View v) {
        DeleteAlert();
    }


    //    Prompt function
//    --------------------------------------------------------------

    public void DeleteAlert() {
        LayoutInflater layoutInflater = LayoutInflater.from(product.this);

        View promptView = layoutInflater.inflate(R.layout.input_dialog_delete_product, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(product.this);
        alertDialogBuilder.setView(promptView);

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Delete();
                    }
                })
                .setNegativeButton("NO",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }


    public void Delete() {
        if (GetSharedValues.LoginStatus(product.this)) {
            tvDelete.setEnabled(false);
            if (pta == true) {
                ApiService.getInstance(product.this, 1).deleteData(product.this, true, SCREEN_NAME, EnvConstants.URL_FEED + "/editproduct/" + ALBUM_ID + "/?action=p_t_a", "deleteAlbum");
            } else {
                ApiService.getInstance(product.this, 1).deleteData(product.this, true, SCREEN_NAME, EnvConstants.URL_FEED + "/editproduct/" + ALBUM_ID + "/", "deleteAlbum");
            }
        } else {
            Alerts.loginAlert(product.this);
        }

    }


    //    Display functions
//    --------------------------------------------------------------

    public void AddIndicators() {
        indicatorcount = images.size();
        imageButtons = new ImageButton[indicatorcount];
        lvIndicator.removeAllViews();
        for (int i = 0; i < images.size(); i++) {
            imageButtons[i] = new ImageButton(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(18, 18);
            params.setMargins(8, 8, 8, 8);
            imageButtons[i].setLayoutParams(params);
            if (i == 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    imageButtons[i].setBackground(ContextCompat.getDrawable(product.this, R.drawable.black_round_bg));
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    imageButtons[i].setBackground(ContextCompat.getDrawable(product.this, R.drawable.white_round_bg));
                }
            }
            lvIndicator.addView(imageButtons[i]);
        }
    }


    public void displayImages() {
        AddIndicators();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                System.out.println("CHECK 1");
                sliderAdaptor = new SliderAdaptor(product.this, images);
                System.out.println("CHECK 2: " + sliderAdaptor);
                viewPager.setAdapter(sliderAdaptor);
                System.out.println("CHECK 3: ");
                viewPager.setCurrentItem(0);
                System.out.println("CHECK 4 :");
//                    viewPager.setClipToPadding(false);
//                    viewPager.setPadding(0, 0, 100, 0);
            }
        });

    }


    public void DisplaySizes() {
        System.out.println("SIZE : inside display sizes: " + SizeTable.size() + "__" + buttonwidth);
        lvProductSizeHolder.removeAllViews();

        if (FS_Status) {
            LinearLayout linearLayout1 = new LinearLayout(this);
            linearLayout1.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout1.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(5, 0, 5, 0);
            linearLayout1.setLayoutParams(params);

            final Button btn = new Button(this);
            LinearLayout.LayoutParams btnparams = new LinearLayout.LayoutParams(220, 80);
            btnparams.setMargins(15, 5, 15, 5);
            btn.setLayoutParams(btnparams);
            final int sdk = Build.VERSION.SDK_INT;

            btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.margin_14));
            btn.setTextColor(Color.WHITE);
            btn.setTypeface(null, Typeface.BOLD);
            btn.setPadding(3, 3, 3, 3);
            btn.setGravity(Gravity.CENTER);
            btn.setText(product_size);
            if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_sizeselected));
            } else {
                Log.d("SIZE", "inside sizeeeee ee second else");
                btn.setBackground(getResources().getDrawable(R.drawable.bg_sizeselected));
            }
            btn.setTextColor(Color.WHITE);
            System.out.println("Quantity__" + Qty);
            tvQuantity.setText("1");
            if (Qty < 5) {
                String txt = "Only " + Qty + " available";
                tvQuantityAlert.setVisibility(View.VISIBLE);
                tvQuantityAlert.setText(txt);
                if (Qty == 0) {
                    tvQuantityAlert.setVisibility(View.GONE);
                }
//                if (Qty <= 1) {
//                    tvQuantityAlert.setVisibility(View.GONE);
//                    tvQuantityheading.setVisibility(View.GONE);
//                    lvQuantityLayout.setVisibility(View.GONE);
//                }else {
//                    tvQuantityAlert.setVisibility(View.VISIBLE);
//                    tvQuantityheading.setVisibility(View.VISIBLE);
//                    lvQuantityLayout.setVisibility(View.VISIBLE);
//                }
            } else {
                tvQuantityAlert.setVisibility(View.GONE);
            }
            Qty = 1;


            linearLayout1.addView(btn);
            lvProductSizeHolder.addView(linearLayout1);
            tvBuy.setText("CONFIRM");
            BuyCheck = 1;

        } else {
            ArrayList<JSONObject> SizeTableNew = new ArrayList<JSONObject>();
            for (int i = 0; i < SizeTable.size(); i++) {
                if (SizeTable.get(i).optInt("quantity") > 0) {
                    SizeTableNew.add(SizeTable.get(i));
                }
            }

            int a = SizeTableNew.size();
            int k = 0;
            while (a >= 3) {
                a = a % 3;
                LinearLayout linearLayout1 = new LinearLayout(this);
                linearLayout1.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout1.setGravity(Gravity.CENTER);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(5, 0, 5, 0);
                linearLayout1.setLayoutParams(params);
                for (int j = 0; j < 3; j++) {

//                if(final_limit < SizeTable.size()) {
                    final Button btn = new Button(this);
                    LinearLayout.LayoutParams btnparams = new LinearLayout.LayoutParams(190, 80);
                    btnparams.setMargins(15, 5, 15, 5);
                    btn.setLayoutParams(btnparams);
                    final int sdk = Build.VERSION.SDK_INT;

                    String size = "";
                    final int sizeId;
                    try {
                        if (size_type.equals("UK")) {
                            size = size_type + "-" + SizeTableNew.get(k).getString("uk_size");
                        } else if (size_type.equals("US")) {
                            size = size_type + "-" + SizeTableNew.get(k).getString("us_size");
                        } else if (size_type.equals("EU")) {
                            size = size_type + "-" + SizeTableNew.get(k).getString("eu_size");
                        }
                        sizeId = SizeTableNew.get(k).getInt("id");
                        btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.margin_15));
                        btn.setTextColor(Color.WHITE);
                        btn.setTypeface(null, Typeface.BOLD);
                        btn.setPadding(3, 3, 3, 3);
                        btn.setGravity(Gravity.CENTER);
                        btn.setText(size);
                        btn.setTag(sizeId);
                        System.out.println("SIZE_TABLE:" + SizeTableNew.size() + "__" + sizeId + "___" + btn.getTag().toString());


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    linearLayout1.addView(btn);


                    try {
                        Log.d("SIZE", "inside sizeeeee ee");
                        if (btn.getTag().toString().equals(selectedSize.toString())) {
                            Log.d("SIZE", "inside sizeeeee ee first if");
                            if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                                btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_sizeselected));
                            } else {
                                Log.d("SIZE", "inside sizeeeee ee second else");
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    btn.setBackground(ContextCompat.getDrawable(product.this, R.drawable.bg_sizeselected));
                                }
                            }
                            btn.setTextColor(Color.WHITE);
                        } else {
                            Log.d("SIZE", "inside sizeeeee ee first else");
                            if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                                btn.setBackgroundDrawable(ContextCompat.getDrawable(product.this, R.drawable.bg_sizeunselected));
                            } else {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    btn.setBackground(ContextCompat.getDrawable(product.this, R.drawable.bg_sizeunselected));
                                }
                            }
                            btn.setTextColor(Color.WHITE);
                        }
                    } catch (Exception e) {
                        if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                            btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_sizeunselected));
                        } else {
                            btn.setBackground(getResources().getDrawable(R.drawable.bg_sizeunselected));
                        }
                        btn.setTextColor(Color.WHITE);
                    }


//                Onclick listener for select size
//                ---------------------------------
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            System.out.println("SIZE CLIECKED");
//                            if (GetSharedValues.LoginStatus(product.this)) {
                            System.out.println("SIZE CLIECKED first if");
                            if (selectedSize != null) {
                                System.out.println("SIZE CLIECKED second if");
                                if (selectedSize.toString().equals(btn.getTag().toString())) {
                                    System.out.println("SIZE CLIECKED thrid if");
                                    if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                                        btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_sizeunselected));
                                    } else {
                                        btn.setBackground(getResources().getDrawable(R.drawable.bg_sizeunselected));
                                    }
                                    btn.setTextColor(Color.WHITE);
                                    selectedSize = null;

                                } else {
                                    System.out.println("SIZE CLIECKED first else");
                                    if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                                        btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_sizeselected));
                                    } else {
                                        btn.setBackground(getResources().getDrawable(R.drawable.bg_sizeselected));
                                    }
                                    btn.setTextColor(Color.WHITE);
                                    selectedSize = Integer.parseInt(btn.getTag().toString());
                                    product_size = btn.getText().toString();
                                    if (getCartCount() > 0) {
                                        CustomMessage.getInstance().CustomMessage(product.this, "This item is already in your tote.");
                                        tvBuy.setText("GO TO TOTE");
                                        BuyCheck = 2;
                                    } else {
                                        tvBuy.setText("CONFIRM");
                                        BuyCheck = 1;
                                    }
                                }
                            } else {
                                System.out.println("SIZE CLIECKED second else");
                                if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                                    btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_sizeselected));
                                } else {
                                    btn.setBackground(getResources().getDrawable(R.drawable.bg_sizeselected));
                                }
                                btn.setTextColor(Color.WHITE);
                                selectedSize = Integer.parseInt(btn.getTag().toString());
                                product_size = btn.getText().toString();
                                if (getCartCount() > 0) {
                                    CustomMessage.getInstance().CustomMessage(product.this, "This item is already in your tote.");
                                    tvBuy.setText("GO TO TOTE");
                                    BuyCheck = 2;
                                } else {
                                    tvBuy.setText("CONFIRM");
                                    BuyCheck = 1;
                                }
                            }
                            lvProductSizeHolder.removeAllViews();
                            DisplaySizes();

                            if (selectedSize != null) {
                                Qty = NoOfProductsTable.get(selectedSize);
                                System.out.println("Quantity__inside if" + Qty);
                                tvQuantity.setText("1");
                                if (Qty < 5) {
                                    String txt = "Only " + Qty + " available";
                                    tvQuantityAlert.setVisibility(View.VISIBLE);
                                    tvQuantityAlert.setText(txt);
                                    if (Qty == 0) {
                                        tvQuantityAlert.setVisibility(View.GONE);
                                    }
                                }
                            } else {
                                System.out.println("Quantity__inside else" + Qty);
                                tvQuantityAlert.setVisibility(View.GONE);
                                Qty = 0;
                                tvQuantity.setText("1");
                            }

                        }
                    });

                    k = k + 1;

                }
                lvProductSizeHolder.addView(linearLayout1);
            }

            if (a == 0 && k < SizeTableNew.size()) {

                LinearLayout linearLayout1 = new LinearLayout(this);
                linearLayout1.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout1.setGravity(Gravity.CENTER);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(5, 0, 5, 0);
                linearLayout1.setLayoutParams(params);

                for (int j = 0; j < 3; j++) {

//                if(final_limit < SizeTable.size()) {
                    final Button btn = new Button(this);
                    LinearLayout.LayoutParams btnparams = new LinearLayout.LayoutParams(190, 80);
                    btnparams.setMargins(15, 5, 15, 5);
                    btn.setLayoutParams(btnparams);
                    final int sdk = Build.VERSION.SDK_INT;

                    String size = "";
                    final int sizeId;
                    try {
                        if (size_type.equals("UK")) {
                            size = size_type + "-" + SizeTableNew.get(k).getString("uk_size");
                        } else if (size_type.equals("US")) {
                            size = size_type + "-" + SizeTableNew.get(k).getString("us_size");
                        } else if (size_type.equals("EU")) {
                            size = size_type + "-" + SizeTableNew.get(k).getString("eu_size");
                        }
                        sizeId = SizeTableNew.get(k).getInt("id");
                        btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.margin_15));
                        btn.setTextColor(Color.WHITE);
                        btn.setTypeface(null, Typeface.BOLD);
                        btn.setPadding(3, 3, 3, 3);
                        btn.setGravity(Gravity.CENTER);
                        btn.setText(size);
                        btn.setTag(sizeId);
                        System.out.println("SIZE_TABLE:" + SizeTableNew.size() + "__" + sizeId + "___" + btn.getTag().toString());


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    linearLayout1.addView(btn);


                    try {
                        Log.d("SIZE", "inside sizeeeee ee");
                        if (btn.getTag().toString().equals(selectedSize.toString())) {
                            Log.d("SIZE", "inside sizeeeee ee first if");
                            if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                                btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_sizeselected));
                            } else {
                                Log.d("SIZE", "inside sizeeeee ee second else");
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    btn.setBackground(ContextCompat.getDrawable(product.this, R.drawable.bg_sizeselected));
                                }
                            }
                            btn.setTextColor(Color.WHITE);
                        } else {
                            Log.d("SIZE", "inside sizeeeee ee first else");
                            if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                                btn.setBackgroundDrawable(ContextCompat.getDrawable(product.this, R.drawable.bg_sizeunselected));
                            } else {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    btn.setBackground(ContextCompat.getDrawable(product.this, R.drawable.bg_sizeunselected));
                                }
                            }
                            btn.setTextColor(Color.WHITE);
                        }
                    } catch (Exception e) {
                        if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                            btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_sizeunselected));
                        } else {
                            btn.setBackground(getResources().getDrawable(R.drawable.bg_sizeunselected));
                        }
                        btn.setTextColor(Color.WHITE);
                    }


//                Onclick listener for select size
//                ---------------------------------
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            System.out.println("SIZE CLIECKED");
//                            if (GetSharedValues.LoginStatus(product.this)) {
                            System.out.println("SIZE CLIECKED first if");
                            if (selectedSize != null) {
                                System.out.println("SIZE CLIECKED second if");
                                if (selectedSize.toString().equals(btn.getTag().toString())) {
                                    System.out.println("SIZE CLIECKED thrid if");
                                    if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                                        btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_sizeunselected));
                                    } else {
                                        btn.setBackground(getResources().getDrawable(R.drawable.bg_sizeunselected));
                                    }
                                    btn.setTextColor(Color.WHITE);
                                    selectedSize = null;

                                } else {
                                    System.out.println("SIZE CLIECKED first else");
                                    if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                                        btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_sizeselected));
                                    } else {
                                        btn.setBackground(getResources().getDrawable(R.drawable.bg_sizeselected));
                                    }
                                    btn.setTextColor(Color.WHITE);
                                    selectedSize = Integer.parseInt(btn.getTag().toString());
                                    product_size = btn.getText().toString();
                                    if (getCartCount() > 0) {
                                        CustomMessage.getInstance().CustomMessage(product.this, "This item is already in your tote.");
                                        tvBuy.setText("GO TO TOTE");
                                        BuyCheck = 2;
                                    } else {
                                        tvBuy.setText("CONFIRM");
                                        BuyCheck = 1;
                                    }
                                }
                            } else {
                                System.out.println("SIZE CLIECKED second else");
                                if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                                    btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_sizeselected));
                                } else {
                                    btn.setBackground(getResources().getDrawable(R.drawable.bg_sizeselected));
                                }
                                btn.setTextColor(Color.WHITE);
                                selectedSize = Integer.parseInt(btn.getTag().toString());
                                product_size = btn.getText().toString();
                                if (getCartCount() > 0) {
                                    CustomMessage.getInstance().CustomMessage(product.this, "This item is already in your tote.");
                                    tvBuy.setText("GO TO TOTE");
                                    BuyCheck = 2;
                                } else {
                                    tvBuy.setText("CONFIRM");
                                    BuyCheck = 1;
                                }
                            }
                            lvProductSizeHolder.removeAllViews();
                            DisplaySizes();

                            if (selectedSize != null) {
                                Qty = NoOfProductsTable.get(selectedSize);
                                System.out.println("Quantity__inside if" + Qty);
                                tvQuantity.setText("1");
                                if (Qty < 5) {
                                    String txt = "Only " + Qty + " available";
                                    tvQuantityAlert.setVisibility(View.VISIBLE);
                                    tvQuantityAlert.setText(txt);
                                    if (Qty == 0) {
                                        tvQuantityAlert.setVisibility(View.GONE);
                                    }
                                }
                            } else {
                                System.out.println("Quantity__inside else" + Qty);
                                tvQuantityAlert.setVisibility(View.GONE);
                                Qty = 0;
                                tvQuantity.setText("1");
                            }

                        }
                    });

                    k = k + 1;
                }
                lvProductSizeHolder.addView(linearLayout1);
            } else {
                LinearLayout linearLayout1 = new LinearLayout(this);
                linearLayout1.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout1.setGravity(Gravity.CENTER);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(5, 0, 5, 0);
                linearLayout1.setLayoutParams(params);

                for (int j = 0; j < a; j++) {

//                if(final_limit < SizeTable.size()) {
                    final Button btn = new Button(this);
                    LinearLayout.LayoutParams btnparams = new LinearLayout.LayoutParams(190, 80);
                    btnparams.setMargins(15, 5, 15, 5);
                    btn.setLayoutParams(btnparams);
                    final int sdk = Build.VERSION.SDK_INT;

                    String size = "";
                    final int sizeId;
                    try {
                        if (size_type.equals("UK")) {
                            size = size_type + "-" + SizeTableNew.get(k).getString("uk_size");
                        } else if (size_type.equals("US")) {
                            size = size_type + "-" + SizeTableNew.get(k).getString("us_size");
                        } else if (size_type.equals("EU")) {
                            size = size_type + "-" + SizeTableNew.get(k).getString("eu_size");
                        }
                        sizeId = SizeTableNew.get(k).getInt("id");
                        btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.margin_15));
                        btn.setTextColor(Color.WHITE);
                        btn.setTypeface(null, Typeface.BOLD);
                        btn.setPadding(3, 3, 3, 3);
                        btn.setGravity(Gravity.CENTER);
                        btn.setText(size);
                        btn.setTag(sizeId);
                        System.out.println("SIZE_TABLE:" + SizeTableNew.size() + "__" + sizeId + "___" + btn.getTag().toString());

//                        if (SizeTable.size() == 1) {
//                            selectedSize = sizeId;
//                            product_size = btn.getText().toString();
//                            Qty = NoOfProductsTable.get(selectedSize);
//                            System.out.println("Quantity__" + Qty);
//                            tvQuantity.setText("1");
//                            if (Qty < 5) {
//                                String txt = "Only " + Qty + " available";
//                                tvQuantityAlert.setVisibility(View.VISIBLE);
//                                tvQuantityAlert.setText(txt);
//                            } else {
//                                tvQuantityAlert.setVisibility(View.GONE);
//                            }
//                            Qty = 1;
//                            tvBuy.setText("CONFIRM");
//                            BuyCheck = 0;
//
//                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    linearLayout1.addView(btn);


                    try {
                        Log.d("SIZE", "inside sizeeeee ee");
                        if (btn.getTag().toString().equals(selectedSize.toString())) {
                            Log.d("SIZE", "inside sizeeeee ee first if");
                            if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                                btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_sizeselected));
                            } else {
                                Log.d("SIZE", "inside sizeeeee ee second else");
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    btn.setBackground(ContextCompat.getDrawable(product.this, R.drawable.bg_sizeselected));
                                }
                            }
                            btn.setTextColor(Color.WHITE);
                        } else {
                            Log.d("SIZE", "inside sizeeeee ee first else");
                            if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                                btn.setBackgroundDrawable(ContextCompat.getDrawable(product.this, R.drawable.bg_sizeunselected));
                            } else {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    btn.setBackground(ContextCompat.getDrawable(product.this, R.drawable.bg_sizeunselected));
                                }
                            }
                            btn.setTextColor(Color.WHITE);
                        }
                    } catch (Exception e) {
                        if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                            btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_sizeunselected));
                        } else {
                            btn.setBackground(getResources().getDrawable(R.drawable.bg_sizeunselected));
                        }
                        btn.setTextColor(Color.WHITE);
                    }


//                Onclick listener for select size
//                ---------------------------------
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            System.out.println("SIZE CLIECKED");
//                            if (GetSharedValues.LoginStatus(product.this)) {
                            System.out.println("SIZE CLIECKED first if");
                            if (selectedSize != null) {
                                System.out.println("SIZE CLIECKED second if");
                                if (selectedSize.toString().equals(btn.getTag().toString())) {
                                    System.out.println("SIZE CLIECKED thrid if");
                                    if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                                        btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_sizeunselected));
                                    } else {
                                        btn.setBackground(getResources().getDrawable(R.drawable.bg_sizeunselected));
                                    }
                                    btn.setTextColor(Color.WHITE);
                                    selectedSize = null;

                                } else {
                                    System.out.println("SIZE CLIECKED first else");
                                    if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                                        btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_sizeselected));
                                    } else {
                                        btn.setBackground(getResources().getDrawable(R.drawable.bg_sizeselected));
                                    }
                                    btn.setTextColor(Color.WHITE);
                                    selectedSize = Integer.parseInt(btn.getTag().toString());
                                    product_size = btn.getText().toString();
                                    if (getCartCount() > 0) {
                                        CustomMessage.getInstance().CustomMessage(product.this, "This item is already in your tote.");
                                        tvBuy.setText("GO TO TOTE");
                                        BuyCheck = 2;
                                    } else {
                                        tvBuy.setText("CONFIRM");
                                        BuyCheck = 1;
                                    }
                                }
                            } else {
                                System.out.println("SIZE CLIECKED second else");
                                if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                                    btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_sizeselected));
                                } else {
                                    btn.setBackground(getResources().getDrawable(R.drawable.bg_sizeselected));
                                }
                                btn.setTextColor(Color.WHITE);
                                selectedSize = Integer.parseInt(btn.getTag().toString());
                                product_size = btn.getText().toString();
                                if (getCartCount() > 0) {
                                    CustomMessage.getInstance().CustomMessage(product.this, "This item is already in your tote.");
                                    tvBuy.setText("GO TO TOTE");
                                    BuyCheck = 2;
                                } else {
                                    tvBuy.setText("CONFIRM");
                                    BuyCheck = 1;
                                }
                            }
                            lvProductSizeHolder.removeAllViews();
                            DisplaySizes();

                            if (selectedSize != null) {
                                Qty = NoOfProductsTable.get(selectedSize);
                                System.out.println("Quantity__inside if" + Qty);
                                tvQuantity.setText("1");
                                if (Qty < 5) {
                                    String txt = "Only " + Qty + " available";
                                    tvQuantityAlert.setVisibility(View.VISIBLE);
                                    tvQuantityAlert.setText(txt);
                                    if (Qty == 0) {
                                        tvQuantityAlert.setVisibility(View.GONE);
                                    }
                                }
                            } else {
                                System.out.println("Quantity__inside else" + Qty);
                                tvQuantityAlert.setVisibility(View.GONE);
                                Qty = 0;
                                tvQuantity.setText("1");
                            }

                        }
                    });

                    k = k + 1;
                }
                lvProductSizeHolder.addView(linearLayout1);

            }


        }

    }
    // }
//

    public void p_comment(View v) {
        if (GetSharedValues.LoginStatus(product.this)) {
            Intent comment = new Intent(product.this, CommentActivity.class);
            comment.putExtra("activity", "product");
            comment.putExtra("album_id", String.valueOf(ALBUM_ID));
            startActivity(comment);
            finish();
        } else {
            Alerts.loginAlert(product.this);
        }
    }

    private void GetOverlay() {
        progressBar.setVisibility(View.VISIBLE);
        ApiService.getInstance(product.this, 1).getData(product.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/marketing/overlay/product", "getoverlay");
    }

    public void p_share(View v) {

        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.putExtra(Intent.EXTRA_TEXT, "http://zapyle.com/#/product/" + ALBUM_ID);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Check this out!");
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, "Share"));
    }


    public void conditionguide(View v) {

        Intent cg = new Intent(product.this, ConditionGuide.class);
        cg.putExtra("activity", "product");
        cg.putExtra("album_id", ALBUM_ID);
        cg.putExtra("pta", false);
        startActivity(cg);

    }

    public void BuyProduct(View v) {
        String PrimaryKey = "";
        if (selectedSize != null) {
            PrimaryKey = ALBUM_ID.toString() + "-" + selectedSize.toString();
        } else {
            PrimaryKey = ALBUM_ID.toString();
        }
        String sql = "select * from CART where albumId='" + PrimaryKey + "'";
        Cursor cursor = db.getData(sql);


        HomePageStatus = false;
        if (BuyStatus) {
            if (BuyCheck == 0) {
                if (cursor.getCount() == 0) {
                    if (lvSizeDialog.getVisibility() == View.GONE) {
                        handleDialogueUiThread(DIALOGE_UP);
                    } else {
                        CustomMessage.getInstance().CustomMessage(product.this, "Select your size");
                    }
                } else {
//                    SizeDialoge();
                    CustomMessage.getInstance().CustomMessage(product.this, "This item is already in your tote.");
                    tvBuy.setText("GO TO TOTE");
                    BuyCheck = 2;
                    progress.dismiss();
                }

            } else if (BuyCheck == 1) {
                lvSizeDialog.setVisibility(View.GONE);
                if (selectedSize != null) {
                    if (Qty > 0) {
                        if (GetSharedValues.LoginStatus(product.this)) {
                            progress.setMessage("Processing request ...Please Wait...");
                            progress.show();
                            if (cursor.getCount() == 0) {
                                Integer qty = Integer.parseInt(tvQuantity.getText().toString());
                                total_price = qty * total_price;
                                Postdata(ALBUM_ID);
                            } else {
                                CustomMessage.getInstance().CustomMessage(getApplicationContext(), "This item is already in your tote.");
                                tvBuy.setText("GO TO TOTE");
                                BuyCheck = 2;
                                progress.dismiss();
                            }
                        } else {
                            if (cursor.getCount() == 0) {
                                Integer qty = Integer.parseInt(tvQuantity.getText().toString());
                                total_price = qty * total_price;
                                AddToDb(true);
                            } else {
                                CustomMessage.getInstance().CustomMessage(getApplicationContext(), "This item is already in your tote.");
                                tvBuy.setText("GO TO TOTE");
                                BuyCheck = 2;
                                progress.dismiss();
                            }
                        }
                    } else {
                        BuyCheck = 0;
                        CustomMessage.getInstance().CustomMessage(product.this, "Invalid quantity");
                    }
                } else {
                    BuyCheck = 0;
                    CustomMessage.getInstance().CustomMessage(product.this, "Select your size");
                }


            } else {
                Intent checkout = new Intent(product.this, Cart.class);
                checkout.putExtra("AlbumId", ALBUM_ID);
                startActivity(checkout);
                finish();
            }
        } else {
            Intent upload1 = new Intent(product.this, upload.class);
            upload1.putExtra("EditStatus", true);
            upload1.putExtra("ProductId", ALBUM_ID);
            startActivity(upload1);
        }
    }


    public void handleDialogueUiThread(final int event) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (event == DIALOGE_DOWN) {
                    System.out.println("SIZE : inside dialog down");
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_down);
                    animation.setDuration(500);
                    lvSizeDialog.setAnimation(animation);
                    lvSizeDialog.setVisibility(View.GONE);
                } else {
                    System.out.println("SIZE : inside dialog up");
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up_dialog);
                    animation.setDuration(500);
                    lvSizeDialog.setAnimation(animation);
                    lvSizeDialog.setVisibility(View.VISIBLE);

                    SpannableString spannableString = new SpannableString("See Size Guide");
                    spannableString.setSpan(new UnderlineSpan(), 0, spannableString.length(), 0);
                    tvSizeguideText.setText(spannableString);
                    tvQuantityAlert.setVisibility(View.GONE);
                    DisplaySizes();
                }

            }
        });

    }


    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void p_like() {
        if (GetSharedValues.LoginStatus(product.this)) {
//            //////System.out.println("inside like click");
            final JSONObject likeObject = new JSONObject();
            try {
                likeObject.put("product_id", ALBUM_ID);
                likeObject.put("action", "like");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ApiService.getInstance(product.this, 1).postData(product.this, EnvConstants.APP_BASE_URL + "/user/like_product/", likeObject, SCREEN_NAME, "like");

            if (lvMainlovelayout.getVisibility() != View.VISIBLE) {
                lvMainlovelayout.setVisibility(View.VISIBLE);
            }
            System.out.println("LIKECOUNTTTTT: like " + LikeCount);
            LikeCount = LikeCount + 1;
            System.out.println("LIKECOUNTTTTT: like " + LikeCount);
            lvunLikeplaceholder.setVisibility(View.GONE);
            lvLikeplaceholder.setVisibility(View.VISIBLE);
            tvLikecount.setText(String.valueOf(LikeCount));
            // YoYo.with(Techniques.SlideInUp).duration(600).playOn(tvLikecount);
            tvMainlovetext.setVisibility(View.VISIBLE);
            if (LikeCount == 1) {
                String lkd = " You loved this";
                tvMainlovetext.setText(lkd);
            } else {
                String lkd = "You and " + String.valueOf(LikeCount - 1) + " others loved this";
                tvMainlovetext.setText(lkd);
            }

            lvMainlovelayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent gotoLike = new Intent(product.this, LikersActivity.class);
                    gotoLike.putExtra("album_user", profile_user_name);
                    gotoLike.putExtra("album_id", String.valueOf(ALBUM_ID));
                    startActivity(gotoLike);

                }
            });

        } else {
            Alerts.loginAlert(product.this);
        }
    }

    public void p_unlike() {
        if (GetSharedValues.LoginStatus(product.this)) {
            final JSONObject likeObject = new JSONObject();
            try {
                likeObject.put("product_id", ALBUM_ID);
                likeObject.put("action", "unlike");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ApiService.getInstance(product.this, 1).postData(product.this, EnvConstants.APP_BASE_URL + "/user/like_product/", likeObject, SCREEN_NAME, "unlike");

            if (lvMainlovelayout.getVisibility() != View.VISIBLE) {
                lvMainlovelayout.setVisibility(View.VISIBLE);
            }
            System.out.println("LIKECOUNTTTTT: unlike " + LikeCount);
            LikeCount = LikeCount - 1;
            System.out.println("LIKECOUNTTTTT: unlike " + LikeCount);
            lvunLikeplaceholder.setVisibility(View.VISIBLE);
            lvLikeplaceholder.setVisibility(View.GONE);
            tvUnlikecount.setText(String.valueOf(LikeCount));
            // YoYo.with(Techniques.SlideInDown).duration(600).playOn(tvUnlikecount);
            if (LikeCount != 0) {
                lvMainlovelayout.setVisibility(View.VISIBLE);
                if (LikeCount == 1) {
                    String lkd = "1 people loved this";
                    tvMainlovetext.setText(lkd);
                } else {
                    String lkd = String.valueOf(LikeCount) + " people loved this";
                    tvMainlovetext.setText(lkd);
                }

                tvMainlovetext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent gotoLike = new Intent(product.this, LikersActivity.class);
                        gotoLike.putExtra("album_user", profile_user_name);
                        gotoLike.putExtra("album_id", String.valueOf(ALBUM_ID));
                        startActivity(gotoLike);

                    }
                });
            } else {
                tvMainlovetext.setVisibility(View.GONE);
                lvMainlovelayout.setVisibility(View.GONE);
            }

        } else {
            Alerts.loginAlert(product.this);
        }
    }

//    Request to server
//    ---------------------------------------------------------------

    private void GetProductdata(Integer ALBUM_ID) {
        progressBar.setVisibility(View.VISIBLE);
        overlaystatus = false;
        if (HomePageStatus) {
            ApiService.getInstance(product.this, 1).getData(product.this, true, SCREEN_NAME, ForwardUrl, "getproductdata");
        } else {

            if (ExternalFunctions.strOverlayactivity.equals("product") && ExternalFunctions.strOverlayurl.length() > 0) {
                ApiService.getInstance(product.this, 1).getData(product.this, true, SCREEN_NAME, ExternalFunctions.strOverlayurl, "getproductdata");
                ExternalFunctions.strOverlayactivity = "";
                ExternalFunctions.strOverlayurl = "";

            } else {

                if (pta == true) {
                    //System.out.println("overlay url" + EnvConstants.URL_FEED + "/singleproduct/" + ALBUM_ID + "/an/");
                    ApiService.getInstance(product.this, 1).getData(product.this, true, SCREEN_NAME, EnvConstants.URL_FEED + "/singleproduct/" + ALBUM_ID + "/an/?action=p_t_a", "getproductdata");
                } else {
                    //System.out.println("overlay url" + EnvConstants.URL_FEED + "/singleproduct/" + ALBUM_ID + "/an/");
                    ApiService.getInstance(product.this, 1).getData(product.this, true, SCREEN_NAME, EnvConstants.URL_FEED + "/singleproduct/" + ALBUM_ID + "/an/", "getproductdata");
                }
            }
        }

    }


    private void Postdata(Integer ALBUM_ID) {
        tvBuy.setEnabled(false);
        int quantitySelected = Integer.parseInt(tvQuantity.getText().toString());
        final JSONObject buyObjectcart = new JSONObject();
        final JSONObject buyObject = new JSONObject();
        // final JSONArray buyList = new JSONArray();

        try {
            buyObject.put("product", ALBUM_ID);
            buyObject.put("size", selectedSize);
            buyObject.put("quantity", quantitySelected);
//            buyList.put(buyObject);
            buyObjectcart.put("cart_data", buyObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AddToDb(false);
        ApiService.getInstance(product.this, 1).postData(product.this, EnvConstants.APP_BASE_URL + "/zapcart/", buyObjectcart, SCREEN_NAME, "buy");
    }


    //  Add to database
//    ---------------------------------------------------------
    private void AddToDb(Boolean toShopcart) {
        String PrimaryKey = ALBUM_ID.toString() + "-" + selectedSize.toString();

        String sql = "select * from CART where albumId='" + PrimaryKey + "'";
        Cursor cursor = db.getData(sql);

        System.out.println("CURSOR : " + PrimaryKey);
        System.out.println("CURSOR 2 : " + cursor.getCount());
        JSONObject ObjToStore = new JSONObject();
        try {
            ObjToStore.put("original_price", original_price);
            ObjToStore.put("shipping_charge", shipping_charge);
            ObjToStore.put("discount", discount);
            ObjToStore.put("product_brand", product_brand);
            ObjToStore.put("total_price", total_price);
            ObjToStore.put("product_id", ALBUM_ID);
            ObjToStore.put("title", title);
            ObjToStore.put("product_size_id", selectedSize);
            ObjToStore.put("product_size", product_size);
            ObjToStore.put("product_image", images.get(0));
            ObjToStore.put("product_quantity", Integer.parseInt(tvQuantity.getText().toString()));
            ObjToStore.put("quantity_available", Qty);
            ObjToStore.put("product_style", product_style);
            ObjToStore.put("listing_price", total_price);
        } catch (Exception e) {
            System.out.println("ADDTODB : " + e);
        }

        System.out.println("ADDTODB : " + cursor.getCount());
        if (cursor.getCount() > 0) {
            //PID Found
            String deleteQuery = "delete from CART where albumId = '" + PrimaryKey + "'";
            db.processData(deleteQuery);
            String dataToDb = ObjToStore.toString().replace("'", "z*p*");
            String query = "insert into " + "CART" + " (" + PAGE + ", " + BODY + ") values('" + PrimaryKey + "', '" + dataToDb + "');";
            db.processData(query);
            System.out.println("ADDTODB : " + cursor.getCount() + "___" + "first if");
            CustomMessage.getInstance().CustomMessage(product.this, "This item is already in your tote.");
            tvBuy.setText("GO TO TOTE");
            BuyCheck = 2;
        } else {
            //PID Not Found
            String dataToDb = ObjToStore.toString().replace("'", "z*p*");
            String query = "insert into " + "CART" + " (" + PAGE + ", " + BODY + ") values('" + PrimaryKey + "', '" + dataToDb + "');";
            db.processData(query);
            System.out.println("ADDTODB : " + cursor.getCount() + "___" + "first else");
        }

        //Clever Tap
        HashMap<String, Object> add_to_tote = new HashMap<String, Object>();
        add_to_tote.put("product_id", ALBUM_ID);
        add_to_tote.put("size", product_size);
        add_to_tote.put("quantity", Integer.parseInt(tvQuantity.getText().toString()));
        add_to_tote.put("price", total_price);
        cleverTap.event.push("add_to_tote", add_to_tote);

        tvBuy.setText("GO TO TOTE");
        BuyCheck = 2;
        progress.dismiss();

        if (ExternalFunctions.cartcount == 0) {
            tvcount.setVisibility(View.VISIBLE);
            tvcount.setText("1");
            ExternalFunctions.cartcount = 1;
        } else {
            ExternalFunctions.cartcount = ExternalFunctions.cartcount + 1;
            tvcount.setText(String.valueOf(ExternalFunctions.cartcount));
        }

        SharedPreferences CartSession = getSharedPreferences("CartSession",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = CartSession.edit();
        editor.putInt("cartCount", ExternalFunctions.cartcount);
        editor.apply();

        if (toShopcart) {
            Intent checkout = new Intent(product.this, Cart.class);
            checkout.putExtra("AlbumId", ALBUM_ID);
            startActivity(checkout);
            finish();
        }

    }

    public void productAdmire(View v) {

        if (GetSharedValues.LoginStatus(product.this)) {
            if (AdmireStatus) {
                JSONObject admireObject = null;
                try {
                    admireObject = new JSONObject();
                    admireObject.put("action", "unadmire");
                    admireObject.put("user", ProfileUserId);
                } catch (Exception e) {

                }
                ApiService.getInstance(product.this, 1).postData(product.this, EnvConstants.APP_BASE_URL + "/user/admire/", admireObject, SCREEN_NAME, "unadmire");
                tvAdmire.setEnabled(false);
                tvAdmire.setBackgroundResource(R.drawable.brand_unfollow_bg);
                tvAdmire.setTextColor(Color.parseColor("#ff7477"));
                tvAdmire.setText("Admire");

            } else {
                JSONObject admireObject = null;
                try {
                    admireObject = new JSONObject();
                    admireObject.put("action", "admire");
                    admireObject.put("user", ProfileUserId);
                } catch (Exception e) {

                }
                ApiService.getInstance(product.this, 1).postData(product.this, EnvConstants.APP_BASE_URL + "/user/admire/", admireObject, SCREEN_NAME, "admire");
                tvAdmire.setEnabled(false);
                tvAdmire.setBackgroundResource(R.drawable.brand_follow_bg);
                tvAdmire.setTextColor(Color.WHITE);
                tvAdmire.setText("Admiring");

                HashMap<String, Object> admire = new HashMap<String, Object>();
                admire.put("admired_user_id", ProfileUserId);
                cleverTap.event.push("admire", admire);
            }
        } else {
            Alerts.loginAlert(product.this);
        }
    }

    //    MainOperations
//    ==============================================================
    private void PerformOperations(JSONObject resp) {
        System.out.println("PRODUCT RESPONSE : " + resp);
        try {
            String status = resp.getString("status");
            if (status.equals("success")) {
                final JSONObject data = resp.getJSONObject("data");
//                if (!data.isNull("user")) {
                ProfileUserId = data.getJSONObject("user").getInt("id");
                userType = data.getJSONObject("user").getString("user_type");
//                }
//                else {
//                    userType = "zap_user";
//                    ProfileUserId = 0;
//                }

                CommentCount = data.getInt("commentCount");
                LikeCount = data.getInt("likesCount");

                NumberFormat myFormat = NumberFormat.getInstance();
                myFormat.setGroupingUsed(true);
                switch (userType) {
                    case "store_front":
                        lvFClayout.setVisibility(View.GONE);
                        lvMore.setVisibility(View.GONE);
                        if (ProfileUserId != GetSharedValues.getuserId(product.this)) {
                            tvAdmire.setVisibility(View.VISIBLE);
                            lvEDlayout.setVisibility(View.GONE);
                            lvBuyLayout.setVisibility(View.VISIBLE);

                            switch (data.getString("sale")) {
                                case "SALE":
                                    shipping_charge = data.getInt("shipping_charge");
                                    tvListprice.setVisibility(View.VISIBLE);
                                    tvOriginalprice.setVisibility(View.VISIBLE);
                                    lvBuyLayout.setVisibility(View.VISIBLE);
                                    if (data.isNull("original_price") || data.getInt("original_price") <= data.getInt("listing_price")) {
                                        if (ProfileUserId == GetSharedValues.getuserId(this)) {
                                            tvOriginalprice.setText("Not set");
                                        } else {
                                            tvOriginalprice.setVisibility(View.GONE);
                                        }
                                    } else {
                                        original_price = data.getInt("original_price");
                                        tvOriginalprice.setText(getResources().getString(R.string.Rs) + myFormat.format(Double.parseDouble(String.valueOf(data.getInt("original_price")))));
                                    }

                                    if (data.isNull("listing_price")) {
                                        tvListprice.setText("Not set");
                                    } else {
                                        total_price = data.getInt("listing_price");
                                        listing_price = data.getInt("listing_price");
                                        tvListprice.setText(getResources().getString(R.string.Rs) + myFormat.format(Double.parseDouble(String.valueOf(data.getInt("listing_price")))));
                                    }


                                    if (data.isNull("discount") || data.getInt("discount") <= 0) {
                                        tvDiscount.setVisibility(View.GONE);
                                    } else {
                                        tvDiscount.setText(String.valueOf((data.getInt("discount")) + "% OFF"));
                                        discount = data.getInt("discount");
                                    }

                                    if (!data.getBoolean("sold_out")) {
                                        BuyStatus = true;
                                        tvBuy.setText("ADD TO TOTE");
                                        tvBuy.setBackgroundColor(Color.parseColor("#FF7373"));
                                        tvBuy.setEnabled(true);
                                    } else {
                                        tvBuy.setText(R.string.sold);
                                        tvBuy.setBackgroundColor(Color.GRAY);
                                        tvBuy.setEnabled(false);
                                    }


                                    break;

                                case "SOCIAL":
                                    tvListprice.setVisibility(View.GONE);
                                    tvOriginalprice.setVisibility(View.GONE);
                                    lvBuyLayout.setVisibility(View.GONE);
                                    tvDiscount.setText("STYLE INSPIRATION");
                                    lvFClayout.setVisibility(View.GONE);
                                    break;

                                default:
                                    shipping_charge = data.getInt("shipping_charge");
                                    tvListprice.setVisibility(View.VISIBLE);
                                    tvOriginalprice.setVisibility(View.VISIBLE);
                                    lvBuyLayout.setVisibility(View.VISIBLE);
                                    if (data.isNull("original_price") || data.getInt("original_price") <= data.getInt("listing_price")) {
                                        if (ProfileUserId == GetSharedValues.getuserId(this)) {
                                            tvOriginalprice.setText("Not set");
                                        } else {
                                            tvOriginalprice.setVisibility(View.GONE);
                                        }
                                    } else {
                                        original_price = data.getInt("original_price");
                                        tvOriginalprice.setText(getResources().getString(R.string.Rs) + myFormat.format(Double.parseDouble(String.valueOf(data.getInt("original_price")))));
                                    }

                                    if (data.isNull("listing_price")) {
                                        tvListprice.setText("Not set");
                                    } else {
                                        total_price = data.getInt("listing_price");
                                        tvListprice.setText(getResources().getString(R.string.Rs) + myFormat.format(Double.parseDouble(String.valueOf(data.getInt("listing_price")))));
                                    }

                                    if (data.isNull("discount") || data.getInt("discount") <= 0) {
                                        tvDiscount.setVisibility(View.GONE);
                                    } else {
                                        tvDiscount.setText(String.valueOf((data.getInt("discount")) + "% OFF"));
                                        discount = data.getInt("discount");
                                    }

                                    if (!data.getBoolean("sold_out")) {
                                        BuyStatus = true;
                                        tvBuy.setText("ADD TO TOTE");
                                        tvBuy.setBackgroundColor(Color.parseColor("#FF7373"));
                                        tvBuy.setEnabled(true);
                                    } else {
                                        tvBuy.setText(R.string.sold);
                                        tvBuy.setBackgroundColor(Color.GRAY);
                                        tvBuy.setEnabled(false);
                                    }

                                    break;
                            }

                        } else {
                            tvAdmire.setVisibility(View.GONE);
                            lvEDlayout.setVisibility(View.VISIBLE);
                            lvBuyLayout.setVisibility(View.GONE);
//                            lvMore.setVisibility(View.VISIBLE);
                            switch (data.getString("sale")) {
                                case "SALE":
                                    shipping_charge = data.getInt("shipping_charge");
                                    tvListprice.setVisibility(View.VISIBLE);
                                    tvOriginalprice.setVisibility(View.VISIBLE);
                                    lvBuyLayout.setVisibility(View.GONE);
                                    if (data.isNull("original_price") || data.getInt("original_price") <= data.getInt("listing_price")) {
                                        if (ProfileUserId == GetSharedValues.getuserId(this)) {
                                            tvOriginalprice.setText("Not set");
                                        } else {
                                            tvOriginalprice.setVisibility(View.GONE);
                                        }
                                    } else {
                                        original_price = data.getInt("original_price");
                                        tvOriginalprice.setText(getResources().getString(R.string.Rs) + myFormat.format(Double.parseDouble(String.valueOf(data.getInt("original_price")))));
                                    }

                                    if (data.isNull("listing_price")) {
                                        tvListprice.setText("Not set");
                                    } else {
                                        total_price = data.getInt("listing_price");
                                        tvListprice.setText(getResources().getString(R.string.Rs) + myFormat.format(Double.parseDouble(String.valueOf(data.getInt("listing_price")))));
                                    }

                                    if (data.isNull("discount") || data.getInt("discount") <= 0) {
                                        tvDiscount.setVisibility(View.GONE);
                                    } else {
                                        tvDiscount.setText(String.valueOf((data.getInt("discount")) + "% OFF"));
                                        discount = data.getInt("discount");
                                    }
                                    break;

                                case "SOCIAL":
                                    tvListprice.setVisibility(View.GONE);
                                    tvOriginalprice.setVisibility(View.GONE);
                                    tvDiscount.setText("STYLE INSPIRATION");
                                    lvFClayout.setVisibility(View.GONE);
                                    break;

                                default:
                                    shipping_charge = data.getInt("shipping_charge");
                                    tvListprice.setVisibility(View.VISIBLE);
                                    tvOriginalprice.setVisibility(View.VISIBLE);
                                    lvBuyLayout.setVisibility(View.GONE);
                                    if (data.isNull("original_price") || data.getInt("original_price") <= data.getInt("listing_price")) {
                                        if (ProfileUserId == GetSharedValues.getuserId(this)) {
                                            tvOriginalprice.setText("Not set");
                                        } else {
                                            tvOriginalprice.setVisibility(View.GONE);
                                        }
                                    } else {
                                        original_price = data.getInt("original_price");
                                        tvOriginalprice.setText(getResources().getString(R.string.Rs) + myFormat.format(Double.parseDouble(String.valueOf(data.getInt("original_price")))));
                                    }

                                    if (data.isNull("listing_price")) {
                                        tvListprice.setText("Not set");
                                    } else {
                                        total_price = data.getInt("listing_price");
                                        tvListprice.setText(getResources().getString(R.string.Rs) + myFormat.format(Double.parseDouble(String.valueOf(data.getInt("listing_price")))));
                                    }

                                    if (data.isNull("discount") || data.getInt("discount") <= 0) {
                                        tvDiscount.setVisibility(View.GONE);
                                    } else {
                                        tvDiscount.setText(String.valueOf((data.getInt("discount")) + "% OFF"));
                                        discount = data.getInt("discount");
                                    }
                                    break;
                            }
                        }

                        break;

                    case "zap_user":
                        lvMore.setVisibility(View.VISIBLE);
                        lvFClayout.setVisibility(View.VISIBLE);
                        if (!data.isNull("condition")) {
                            tvCondition.setText(data.getString("condition"));
                        } else {
                            tvCondition.setText("Not set");
                        }

                        if (!data.isNull("age")) {
                            tvage.setText(data.getString("age"));
                        } else {
                            tvage.setText("Not set");
                        }
                        if (ProfileUserId != GetSharedValues.getuserId(product.this)) {
                            tvAdmire.setVisibility(View.VISIBLE);
                            lvEDlayout.setVisibility(View.GONE);
                            lvBuyLayout.setVisibility(View.VISIBLE);

                            switch (data.getString("sale")) {
                                case "SALE":
                                    shipping_charge = data.getInt("shipping_charge");
                                    tvListprice.setVisibility(View.VISIBLE);
                                    tvOriginalprice.setVisibility(View.VISIBLE);
                                    lvBuyLayout.setVisibility(View.VISIBLE);
                                    if (data.isNull("original_price") || data.getInt("original_price") <= data.getInt("listing_price")) {
                                        if (ProfileUserId == GetSharedValues.getuserId(this)) {
                                            tvOriginalprice.setText("Not set");
                                        } else {
                                            tvOriginalprice.setVisibility(View.GONE);
                                        }
                                    } else {
                                        original_price = data.getInt("original_price");
                                        tvOriginalprice.setText(getResources().getString(R.string.Rs) + myFormat.format(Double.parseDouble(String.valueOf(data.getInt("original_price")))));
                                    }

                                    if (data.isNull("listing_price")) {
                                        tvListprice.setText("Not set");
                                    } else {
                                        total_price = data.getInt("listing_price");
                                        tvListprice.setText(getResources().getString(R.string.Rs) + myFormat.format(Double.parseDouble(String.valueOf(data.getInt("listing_price")))));
                                    }


                                    if (data.isNull("discount") || data.getInt("discount") <= 0) {
                                        tvDiscount.setVisibility(View.GONE);
                                    } else {
                                        tvDiscount.setText(String.valueOf((data.getInt("discount")) + "% OFF"));
                                        discount = data.getInt("discount");
                                    }
                                    System.out.println("SOLLLLLLLLL: " + data.getBoolean("sold_out"));

                                    if (!data.getBoolean("sold_out")) {

                                        System.out.println("SOLLLLLLLLL: " + data.getBoolean("sold_out") + "___inside");
                                        BuyStatus = true;
                                        tvBuy.setText("ADD TO TOTE");
                                        tvBuy.setBackgroundColor(Color.parseColor("#FF7373"));
                                        tvBuy.setEnabled(true);
                                    } else {
                                        System.out.println("SOLLLLLLLLL: " + data.getBoolean("sold_out") + "____outside");
                                        tvBuy.setText(R.string.sold);
                                        tvBuy.setBackgroundColor(Color.GRAY);
                                        tvBuy.setEnabled(false);
                                    }


                                    break;

                                case "SOCIAL":
                                    tvListprice.setVisibility(View.GONE);
                                    tvOriginalprice.setVisibility(View.GONE);
                                    lvBuyLayout.setVisibility(View.GONE);
                                    tvDiscount.setText("STYLE INSPIRATION");
                                    lvFClayout.setVisibility(View.GONE);
                                    break;

                                default:
                                    tvListprice.setVisibility(View.VISIBLE);
                                    tvOriginalprice.setVisibility(View.VISIBLE);
                                    lvBuyLayout.setVisibility(View.VISIBLE);
                                    shipping_charge = data.getInt("shipping_charge");
                                    if (data.isNull("original_price") || data.getInt("original_price") <= data.getInt("listing_price")) {
                                        if (ProfileUserId == GetSharedValues.getuserId(this)) {
                                            tvOriginalprice.setText("Not set");
                                        } else {
                                            tvOriginalprice.setVisibility(View.GONE);
                                        }
                                    } else {
                                        original_price = data.getInt("original_price");
                                        tvOriginalprice.setText(getResources().getString(R.string.Rs) + myFormat.format(Double.parseDouble(String.valueOf(data.getInt("original_price")))));
                                    }

                                    if (data.isNull("listing_price")) {
                                        tvListprice.setText("Not set");
                                    } else {
                                        total_price = data.getInt("listing_price");
                                        tvListprice.setText(getResources().getString(R.string.Rs) + myFormat.format(Double.parseDouble(String.valueOf(data.getInt("listing_price")))));
                                    }


                                    if (data.isNull("discount") || data.getInt("discount") <= 0) {
                                        tvDiscount.setVisibility(View.GONE);
                                    } else {
                                        tvDiscount.setText(String.valueOf((data.getInt("discount")) + "% OFF"));
                                        discount = data.getInt("discount");
                                    }

                                    if (!data.getBoolean("sold_out")) {
                                        BuyStatus = true;
                                        tvBuy.setText("ADD TO TOTE");
                                        tvBuy.setBackgroundColor(Color.parseColor("#FF7373"));
                                        tvBuy.setEnabled(true);
                                    } else {
                                        tvBuy.setText(R.string.sold);
                                        tvBuy.setBackgroundColor(Color.GRAY);
                                        tvBuy.setEnabled(false);
                                    }

                                    break;
                            }

                        } else {
                            tvAdmire.setVisibility(View.GONE);
                            lvEDlayout.setVisibility(View.VISIBLE);
                            lvBuyLayout.setVisibility(View.GONE);
                            switch (data.getString("sale")) {
                                case "SALE":
                                    tvListprice.setVisibility(View.VISIBLE);
                                    tvOriginalprice.setVisibility(View.VISIBLE);
                                    lvBuyLayout.setVisibility(View.GONE);
                                    if (data.isNull("original_price") || data.getInt("original_price") <= data.getInt("listing_price")) {
                                        if (ProfileUserId == GetSharedValues.getuserId(this)) {
                                            tvOriginalprice.setText("Not set");
                                        } else {
                                            tvOriginalprice.setVisibility(View.GONE);
                                        }
                                    } else {
                                        original_price = data.getInt("original_price");
                                        tvOriginalprice.setText(getResources().getString(R.string.Rs) + myFormat.format(Double.parseDouble(String.valueOf(data.getInt("original_price")))));
                                    }

                                    if (data.isNull("listing_price")) {
                                        tvListprice.setText("Not set");
                                    } else {
                                        total_price = data.getInt("listing_price");
                                        tvListprice.setText(getResources().getString(R.string.Rs) + myFormat.format(Double.parseDouble(String.valueOf(data.getInt("listing_price")))));
                                    }

                                    if (data.isNull("discount") || data.getInt("discount") <= 0) {
                                        tvDiscount.setVisibility(View.GONE);
                                    } else {
                                        tvDiscount.setText(String.valueOf((data.getInt("discount")) + "% OFF"));
                                        discount = data.getInt("discount");
                                    }
                                    break;

                                case "SOCIAL":
                                    tvListprice.setVisibility(View.GONE);
                                    tvOriginalprice.setVisibility(View.GONE);
                                    tvDiscount.setText("STYLE INSPIRATION");
                                    lvFClayout.setVisibility(View.GONE);
                                    break;

                                default:
                                    tvListprice.setVisibility(View.VISIBLE);
                                    tvOriginalprice.setVisibility(View.VISIBLE);
                                    lvBuyLayout.setVisibility(View.GONE);
                                    if (data.isNull("original_price") || data.getInt("original_price") <= data.getInt("listing_price")) {
                                        if (ProfileUserId == GetSharedValues.getuserId(this)) {
                                            tvOriginalprice.setText("Not set");
                                        } else {
                                            tvOriginalprice.setVisibility(View.GONE);
                                        }
                                    } else {
                                        original_price = data.getInt("original_price");
                                        tvOriginalprice.setText(getResources().getString(R.string.Rs) + myFormat.format(Double.parseDouble(String.valueOf(data.getInt("original_price")))));
                                    }

                                    if (data.isNull("listing_price")) {
                                        tvListprice.setText("Not set");
                                    } else {
                                        total_price = data.getInt("listing_price");
                                        tvListprice.setText(getResources().getString(R.string.Rs) + myFormat.format(Double.parseDouble(String.valueOf(data.getInt("listing_price")))));
                                    }

                                    if (data.isNull("discount") || data.getInt("discount") <= 0) {
                                        tvDiscount.setVisibility(View.GONE);
                                    } else {
                                        tvDiscount.setText(String.valueOf((data.getInt("discount")) + "% OFF"));
                                        discount = data.getInt("discount");
                                    }
                                    break;
                            }
                        }
                        break;

                    default:
                        lvMore.setVisibility(View.VISIBLE);
                        lvFClayout.setVisibility(View.VISIBLE);
                        if (!data.isNull("condition")) {
                            tvCondition.setText(data.getString("condition"));
                        } else {
                            tvCondition.setText("Not set");
                        }

                        if (!data.isNull("age")) {
                            tvage.setText(data.getString("age"));
                        } else {
                            tvage.setText("Not set");
                        }
                        if (ProfileUserId != GetSharedValues.getuserId(product.this)) {
                            tvAdmire.setVisibility(View.VISIBLE);
                            lvEDlayout.setVisibility(View.GONE);
                            lvBuyLayout.setVisibility(View.VISIBLE);

                            switch (data.getString("sale")) {
                                case "SALE":
                                    shipping_charge = data.getInt("shipping_charge");
                                    tvListprice.setVisibility(View.VISIBLE);
                                    tvOriginalprice.setVisibility(View.VISIBLE);
                                    lvBuyLayout.setVisibility(View.VISIBLE);
                                    if (data.isNull("original_price") || data.getInt("original_price") <= data.getInt("listing_price")) {
                                        if (ProfileUserId == GetSharedValues.getuserId(this)) {
                                            tvOriginalprice.setText("Not set");
                                        } else {
                                            tvOriginalprice.setVisibility(View.GONE);
                                        }
                                    } else {
                                        original_price = data.getInt("original_price");
                                        tvOriginalprice.setText(getResources().getString(R.string.Rs) + myFormat.format(Double.parseDouble(String.valueOf(data.getInt("original_price")))));
                                    }

                                    if (data.isNull("listing_price")) {
                                        tvListprice.setText("Not set");
                                    } else {
                                        total_price = data.getInt("listing_price");
                                        tvListprice.setText(getResources().getString(R.string.Rs) + myFormat.format(Double.parseDouble(String.valueOf(data.getInt("listing_price")))));
                                    }

                                    if (data.isNull("discount") || data.getInt("discount") <= 0) {
                                        tvDiscount.setVisibility(View.GONE);
                                    } else {
                                        tvDiscount.setText(String.valueOf((data.getInt("discount")) + "% OFF"));
                                        discount = data.getInt("discount");
                                    }
                                    System.out.println("SOLLLLLLLLL: " + data.getBoolean("sold_out"));

                                    if (!data.getBoolean("sold_out")) {

                                        System.out.println("SOLLLLLLLLL: " + data.getBoolean("sold_out") + "___inside");
                                        BuyStatus = true;
                                        tvBuy.setText("ADD TO TOTE");
                                        tvBuy.setBackgroundColor(Color.parseColor("#FF7373"));
                                        tvBuy.setEnabled(true);
                                    } else {
                                        System.out.println("SOLLLLLLLLL: " + data.getBoolean("sold_out") + "____outside");
                                        tvBuy.setText(R.string.sold);
                                        tvBuy.setBackgroundColor(Color.GRAY);
                                        tvBuy.setEnabled(false);
                                    }


                                    break;

                                case "SOCIAL":
                                    tvListprice.setVisibility(View.GONE);
                                    tvOriginalprice.setVisibility(View.GONE);
                                    lvBuyLayout.setVisibility(View.GONE);
                                    tvDiscount.setText("STYLE INSPIRATION");
                                    lvFClayout.setVisibility(View.GONE);
                                    break;

                                default:
                                    tvListprice.setVisibility(View.VISIBLE);
                                    tvOriginalprice.setVisibility(View.VISIBLE);
                                    lvBuyLayout.setVisibility(View.VISIBLE);
                                    shipping_charge = data.getInt("shipping_charge");
                                    if (data.isNull("original_price") || data.getInt("original_price") <= data.getInt("listing_price")) {
                                        if (ProfileUserId == GetSharedValues.getuserId(this)) {
                                            tvOriginalprice.setText("Not set");
                                        } else {
                                            tvOriginalprice.setVisibility(View.GONE);
                                        }
                                    } else {
                                        original_price = data.getInt("original_price");
                                        tvOriginalprice.setText(getResources().getString(R.string.Rs) + myFormat.format(Double.parseDouble(String.valueOf(data.getInt("original_price")))));
                                    }

                                    if (data.isNull("listing_price")) {
                                        tvListprice.setText("Not set");
                                    } else {
                                        total_price = data.getInt("listing_price");
                                        tvListprice.setText(getResources().getString(R.string.Rs) + myFormat.format(Double.parseDouble(String.valueOf(data.getInt("listing_price")))));
                                    }

                                    if (data.isNull("discount") || data.getInt("discount") <= 0) {
                                        tvDiscount.setVisibility(View.GONE);
                                    } else {
                                        tvDiscount.setText(String.valueOf((data.getInt("discount")) + "% OFF"));
                                        discount = data.getInt("discount");
                                    }

                                    if (!data.getBoolean("sold_out")) {
                                        BuyStatus = true;
                                        tvBuy.setText("ADD TO TOTE");
                                        tvBuy.setBackgroundColor(Color.parseColor("#FF7373"));
                                        tvBuy.setEnabled(true);
                                    } else {
                                        tvBuy.setText(R.string.sold);
                                        tvBuy.setBackgroundColor(Color.GRAY);
                                        tvBuy.setEnabled(false);
                                    }

                                    break;
                            }

                        } else {
                            tvAdmire.setVisibility(View.GONE);
                            lvEDlayout.setVisibility(View.VISIBLE);
                            lvBuyLayout.setVisibility(View.GONE);
                            switch (data.getString("sale")) {
                                case "SALE":
                                    tvListprice.setVisibility(View.VISIBLE);
                                    tvOriginalprice.setVisibility(View.VISIBLE);
                                    lvBuyLayout.setVisibility(View.GONE);
                                    if (data.isNull("original_price") || data.getInt("original_price") <= data.getInt("listing_price")) {
                                        if (ProfileUserId == GetSharedValues.getuserId(this)) {
                                            tvOriginalprice.setText("Not set");
                                        } else {
                                            tvOriginalprice.setVisibility(View.GONE);
                                        }
                                    } else {
                                        original_price = data.getInt("original_price");
                                        tvOriginalprice.setText(getResources().getString(R.string.Rs) + myFormat.format(Double.parseDouble(String.valueOf(data.getInt("original_price")))));
                                    }

                                    if (data.isNull("listing_price")) {
                                        tvListprice.setText("Not set");
                                    } else {
                                        total_price = data.getInt("listing_price");
                                        tvListprice.setText(getResources().getString(R.string.Rs) + myFormat.format(Double.parseDouble(String.valueOf(data.getInt("listing_price")))));
                                    }

                                    if (data.isNull("discount") || data.getInt("discount") == 0) {
                                        tvDiscount.setVisibility(View.GONE);
                                    } else {
                                        tvDiscount.setText(String.valueOf((data.getInt("discount")) + "% OFF"));
                                        discount = data.getInt("discount");
                                    }
                                    break;

                                case "SOCIAL":
                                    tvListprice.setVisibility(View.GONE);
                                    tvOriginalprice.setVisibility(View.GONE);
                                    tvDiscount.setText("STYLE INSPIRATION");
                                    lvFClayout.setVisibility(View.GONE);
                                    break;

                                default:
                                    tvListprice.setVisibility(View.VISIBLE);
                                    tvOriginalprice.setVisibility(View.VISIBLE);
                                    lvBuyLayout.setVisibility(View.GONE);
                                    if (data.isNull("original_price") || data.getInt("original_price") <= data.getInt("listing_price")) {
                                        if (ProfileUserId == GetSharedValues.getuserId(this)) {
                                            tvOriginalprice.setText("Not set");
                                        } else {
                                            tvOriginalprice.setVisibility(View.GONE);
                                        }
                                    } else {
                                        original_price = data.getInt("original_price");
                                        tvOriginalprice.setText(getResources().getString(R.string.Rs) + myFormat.format(Double.parseDouble(String.valueOf(data.getInt("original_price")))));
                                    }

                                    if (data.isNull("listing_price")) {
                                        tvListprice.setText("Not set");
                                    } else {
                                        total_price = data.getInt("listing_price");
                                        tvListprice.setText(getResources().getString(R.string.Rs) + myFormat.format(Double.parseDouble(String.valueOf(data.getInt("listing_price")))));
                                    }

                                    if (data.isNull("discount") || data.getInt("discount") == 0) {
                                        tvDiscount.setVisibility(View.GONE);
                                    } else {
                                        tvDiscount.setText(String.valueOf((data.getInt("discount")) + "% OFF"));
                                        discount = data.getInt("discount");
                                    }
                                    break;
                            }
                        }
                        break;
                }


                tvcommentcount.setText(String.valueOf(CommentCount));
                if (CommentCount != 0) {
                    lvCommentMain.setVisibility(View.VISIBLE);
                    if (CommentCount == 1) {
                        String cmt = String.valueOf(CommentCount) + " comment";
                        tvCommenttextmain.setText(cmt);
                    } else {
                        String cmt = String.valueOf(CommentCount) + " comments";
                        tvCommenttextmain.setText(cmt);
                    }
                    lvCommentMain.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (GetSharedValues.LoginStatus(product.this)) {
                                Intent comment = new Intent(product.this, CommentActivity.class);
                                comment.putExtra("activity", "product");
                                comment.putExtra("album_id", String.valueOf(ALBUM_ID));
                                startActivity(comment);
                                finish();
                            } else {
                                Alerts.loginAlert(product.this);
                            }
                        }
                    });
                } else {
                    lvCommentMain.setVisibility(View.GONE);
                }


                if (data.getBoolean("liked_by_user")) {
                    lvLikeplaceholder.setVisibility(View.VISIBLE);
                    lvunLikeplaceholder.setVisibility(View.GONE);
                    tvLikecount.setText(String.valueOf(LikeCount));
                } else {
                    lvunLikeplaceholder.setVisibility(View.VISIBLE);
                    lvLikeplaceholder.setVisibility(View.GONE);
                    tvUnlikecount.setText(String.valueOf(LikeCount));

                }


                if (LikeCount != 0) {
                    lvMainlovelayout.setVisibility(View.VISIBLE);
                    if (LikeCount == 1) {
                        String lkd;
                        if (data.getBoolean("liked_by_user")) {
                            lkd = "You loved this";
                        } else {
                            lkd = String.valueOf(LikeCount) + " loved this";
                        }
                        tvMainlovetext.setText(lkd);
                    } else {
                        String lkd;
                        if (data.getBoolean("liked_by_user")) {
                            lkd = "You and " + String.valueOf(LikeCount - 1) + " others loved this";
                        } else {
                            lkd = String.valueOf(LikeCount) + " loved this.";
                        }
                        tvMainlovetext.setText(lkd);
                    }


                    lvMainlovelayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Intent gotoLike = new Intent(product.this, LikersActivity.class);
                                gotoLike.putExtra("album_user", data.getJSONObject("user").getString("zap_username"));
                                gotoLike.putExtra("album_id", String.valueOf(ALBUM_ID));
                                startActivity(gotoLike);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                } else {
                    lvMainlovelayout.setVisibility(View.GONE);
                }

                if (data.getBoolean("admired_by_user")) {
                    AdmireStatus = true;
                    //    tv_admire.setBackgroundColor(Color.);
                    tvAdmire.setTextColor(Color.WHITE);
                    tvAdmire.setBackgroundResource(R.drawable.brand_follow_bg);

                    tvAdmire.setText("Admiring");
                } else {
                    AdmireStatus = false;
                    tvAdmire.setTextColor(Color.parseColor("#ff7477"));
                    tvAdmire.setBackgroundResource(R.drawable.brand_unfollow_bg);
                    tvAdmire.setText("Admire");
                }

                if (data.has("time_to_make")) {
                    if (!data.isNull("time_to_make")) {
                        lvAuthenticityLayout.setVisibility(View.GONE);
                        String s = "This product will be shipped to you after" + data.getString("time_to_make") + " from the date of order placed. All custom made orders are not returnable.";
                        tvReturnText.setText(s);

                        tvShoppingreturns.setVisibility(View.GONE);
                    } else {
                        lvAuthenticityLayout.setVisibility(View.VISIBLE);
                        tvShoppingreturns.setVisibility(View.VISIBLE);
                    }

                }


                if (!data.getJSONObject("user").isNull("profile_pic")) {
//                    Glide.with(product.this)
//                            .load(data.getJSONObject("user").getString("profile_pic"))
//                            .fitCenter()
//                            .placeholder(R.drawable.playholderscreen)
//                            .crossFade()
//                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                            .into(profileImage);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        if (!this.isDestroyed()) {
//                            Picasso.with(this)
//                                    .load(data.getJSONObject("user").getString("profile_pic"))
//                                    .placeholder(R.drawable.prof_placeholder)   // optional
//                                    .error(R.drawable.prof_placeholder)      // optional
//                                    .into(profileImage);

                            Glide.with(this)
                                    .load(data.getJSONObject("user").getString("profile_pic"))
                                    .asBitmap()
                                    .placeholder(R.drawable.prof_placeholder)
                                    .error(R.drawable.prof_placeholder)
                                    .centerCrop()
                                    .into(new BitmapImageViewTarget(profileImage) {
                                        @Override
                                        protected void setResource(Bitmap resource) {
                                            RoundedBitmapDrawable circularBitmapDrawable =
                                                    RoundedBitmapDrawableFactory.create(getResources(), resource);
                                            circularBitmapDrawable.setCircular(true);
                                            profileImage.setImageDrawable(circularBitmapDrawable);
                                        }
                                    });
                        }
                    }

                    if (!data.getJSONObject("user").isNull("zap_username")) {
                        profile_user_name = data.getJSONObject("user").getString("zap_username");
                    }
                    if (!data.getJSONObject("user").isNull("profile_pic")) {
                        tvUsername.setText(data.getJSONObject("user").getString("zap_username"));
                    }
                }


                if (!data.isNull("flash_sale_data")) {
                    lvFlashsaleLayout.setVisibility(View.VISIBLE);
                    tvFlashBannerDiscount.setText("(EXTRA " + data.getJSONObject("flash_sale_data").getString("extra_discount") + "% OFF)");
                    tvFlashDiscount.setVisibility(View.VISIBLE);
                    String disc = null;
                    if (data.isNull("discount") || data.getInt("discount") == 0) {
                        disc = data.getJSONObject("flash_sale_data").getString("extra_discount") + " % OFF";
                    } else {
                        if (tvDiscount.getVisibility() == View.VISIBLE) {
                            if (data.getJSONObject("flash_sale_data").has("original_discount")) {
                                if (Integer.parseInt(data.getJSONObject("flash_sale_data").getString("original_discount")) != 0) {
                                    tvDiscount.setText(data.getJSONObject("flash_sale_data").getString("original_discount") + "% OFF");
                                    disc = "+  Extra " + data.getJSONObject("flash_sale_data").getString("extra_discount") + " % OFF";
                                } else {
//                                tvDiscount.setVisibility(View.GONE);
                                    disc = data.getJSONObject("flash_sale_data").getString("extra_discount") + " % OFF";
                                }
                            }
                        }

                    }
                    if (disc != null) {
                        tvFlashDiscount.setText(disc.toUpperCase());
                    }
//                        tvFlashSaleTitle.setText(data.getJSONObject("flash_sale_data").getString("sale_name"));
                    tvFlashSaleTitle.setText("This item is on sale");
                    TextView hurrytext = (TextView) findViewById(R.id.hurrytext);
                    hurrytext.setVisibility(View.GONE);
                    tvTimer.setVisibility(View.GONE);
                    try {
                        if (data.getJSONObject("flash_sale_data").getBoolean("show_timer")) {
                            tvTimer.setVisibility(View.VISIBLE);
                            hurrytext.setVisibility(View.VISIBLE);
                            long flashSaleTime = data.getJSONObject("flash_sale_data").getLong("end_timestamp") - (System.currentTimeMillis() / 1000);
                            countDownTimer = new CountDownTimer(flashSaleTime * 1000 + 1000, 1000) {

                                public void onTick(long millisUntilFinished) {
                                    long millis = millisUntilFinished;
                                    String hms = String.format("%02d:%02d:%02d:%02d",
                                            TimeUnit.MILLISECONDS.toDays(millis),
                                            TimeUnit.MILLISECONDS.toHours(millis) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millis)),
                                            TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                                            TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

                                    tvTimer.setText(hms);
                                }

                                public void onFinish() {
                                    System.out.println("inside finish");
                                    finish();
                                    Intent intent = new Intent(product.this, product.class);
                                    intent.putExtra("album_id", ALBUM_ID);
                                    intent.putExtra("activity", callingActivity);
                                    startActivity(intent);

                                }

                            }.start();
                        }
                    } catch (Exception e) {

                    }
//                    else {
//
//                        lvFlashsaleLayout.setVisibility(View.GONE);
//                    }


                } else {
                    if (tvDiscount.getVisibility() == View.VISIBLE) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            tvDiscount.setBackground(ContextCompat.getDrawable(product.this, R.drawable.discount_bg));
                        }
                    }
                    lvFlashsaleLayout.setVisibility(View.GONE);
                }
                tvUsername.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent pro_page = new Intent(product.this, profile.class);
                            pro_page.putExtra("user_id", data.getJSONObject("user").getInt("id"));
                            pro_page.putExtra("activity", "product");
                            pro_page.putExtra("p_username", data.getJSONObject("user").getString("zap_username"));
                            pro_page.putExtra("product_id", ALBUM_ID);
                            pro_page.putExtra("p_usertype", data.getJSONObject("user").getString("user_type"));
                            startActivity(pro_page);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

                profileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent pro_page = new Intent(product.this, profile.class);
                            pro_page.putExtra("user_id", data.getJSONObject("user").getInt("id"));
                            pro_page.putExtra("activity", "product");
                            pro_page.putExtra("p_username", data.getJSONObject("user").getString("zap_username"));
                            pro_page.putExtra("product_id", ALBUM_ID);
                            pro_page.putExtra("p_usertype", data.getJSONObject("user").getString("user_type"));
                            startActivity(pro_page);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
//
//                if (data.getJSONObject("user").has("zap_username") || data.getJSONObject("user").has("profile_pic")){
//                    rl_productHeaderLayout.setVisibility(View.VISIBLE);
//                }
//                else {
//                    rl_productHeaderLayout.setVisibility(View.GONE);
//                }

                if (data.getJSONObject("user").getString("user_type").equals("zap_exclusive")) {
                    rl_productHeaderLayout.setVisibility(View.GONE);
                }


                if (!data.isNull("color")) {
                    lvColor.setVisibility(View.VISIBLE);
                    tvColor.setText(data.getString("color"));
                } else {
                    lvColor.setVisibility(View.GONE);
                }
                if (!data.isNull("brand")) {
                    tvBrand.setText(data.getString("brand"));
                    product_brand = data.getString("brand");
                } else {
                    lvBrand.setVisibility(View.GONE);
                    tvBrand.setText("");
                }
                if (!data.isNull("product_category")) {
                    tvCategory.setText(data.getString("product_category"));
                } else {
                    tvCategory.setText("");
                }

                if (!data.isNull("occasion")) {
                    tvOccasion.setText(data.getString("occasion"));
                } else {
                    lvOccasion.setVisibility(View.GONE);
                    tvOccasion.setText("");
                }

                if (!data.isNull("style")) {
                    tvStyle.setText(data.getString("style"));
                    product_style = data.getString("style");
                } else {
                    lvStyle.setVisibility(View.GONE);

                }

                if (!data.isNull("description")) {
                    tvDescription.setText(data.getString("description"));
                } else {
                    tvDescription.setText("");
                }

                if (data.isNull("style") && data.isNull("color")) {
                    lvSeperation.setVisibility(View.GONE);
                }

                tvProductname.setText(data.getString("brand").toUpperCase());
                title = data.getString("title");
                tvTitlehead.setText(data.getString("brand").toUpperCase());
                tvPrdoductPageTitle.setText(title);
                tvDescription.setText(data.getString("description"));

                for (int i = 0; i < data.getJSONArray("images").length(); i++) {
                    images.add(data.getJSONArray("images").get(i).toString());
                }

                for (int i = 0; i < data.getJSONArray("size").length(); i++) {
                    CategoryType = data.getJSONArray("size").getJSONObject(i).getString("category_type");
                    if (data.getJSONArray("size").getJSONObject(i).getString("category_type").equals("FS")) {
                        selectedSize = data.getJSONArray("size").getJSONObject(i).getInt("id");
                        FS_Status = true;
                    } else {
                        SizeTable.add(data.getJSONArray("size").getJSONObject(i));
                        FS_Status = false;
                    }
                }
                for (int i = 0; i < data.getJSONArray("no_of_products").length(); i++) {
                    NoOfProductsTable.put(data.getJSONArray("no_of_products").getJSONObject(i).getInt("size_id"), data.getJSONArray("no_of_products").getJSONObject(i).getInt("quantity"));
                }

                displayImages();

                System.out.println("PRODUCTSIZE : productsize " + product_size);
                if (FS_Status) {
                    product_size = "FREESIZE";
                    tvsize.setText("FREESIZE");
                    tvSizeguide.setVisibility(View.GONE);
                    Qty = data.getJSONArray("no_of_products").getJSONObject(0).getInt("quantity");

                } else {
                    tvSizeguide.setVisibility(View.VISIBLE);
                    size_type = data.getString("size_type");
                    if (size_type.isEmpty() || size_type.equals("") || size_type == null) {
                        size_type = "UK";
                    }
                    String size = null;
                    for (int i = 0; i < SizeTable.size(); i++) {
                        if (size_type.equals("UK")) {
                            size = size + "/" + size_type + "-" + SizeTable.get(i).getString("uk_size");
                        } else if (size_type.equals("US")) {
                            size = size + "/" + size_type + "-" + SizeTable.get(i).getString("us_size");
                        } else if (size_type.equals("EU")) {
                            size = size + "/" + size_type + "-" + SizeTable.get(i).getString("eu_size");
                        }
                    }
                    System.out.println("PRODUCTSIZE : " + size);
                    if (size != null) {
                        tvsize.setText(size.substring(5, size.length()));
                    }
                }

                if (ExternalFunctions.strOverlayurl.length() == 0) {
                    GetOverlay();
                }
                ExternalFunctions.strOverlayurl = "";
                ExternalFunctions.strOverlayactivity = "";
                try {
                    product_scrollview.setVisibility(View.VISIBLE);
                    p_bottomlayout.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                } catch (Exception e) {

                }
            } else {
                progressBar.setVisibility(View.GONE);
                CustomMessage.getInstance().CustomMessage(product.this, "Oops. Something went wrong!");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            progressBar.setVisibility(View.GONE);
            CustomMessage.getInstance().CustomMessage(product.this, "Oops. Something went wrong!");

        }

    }


//    On resume funtion
//    ----------------------------------------------------------------

    @Override
    protected void onResume() {
        super.onResume();
        Log.w("MainActivity", "onResume");

        mTracker.set("UserName", GetSharedValues.getUsername(getApplicationContext()));
        mTracker.set("ZapUsername", GetSharedValues.getZapname(getApplicationContext()));
        mTracker.setScreenName(GetSharedValues.getZapname(getApplicationContext()) + "~" + "product page");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        if (ExternalFunctions.strOverlayurl.length() > 1 && overlaystatus) {
            ForwardUrl = ExternalFunctions.strOverlayurl;
            HomePageStatus = true;
            GetProductdata(0);
        }
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

    }


//    Response from server
//    ----------------------------------------------------------------

    @Override
    public void onResponseCallback(JSONObject response, String flag) {

        if (flag.equals("getproductdata")) {
            HomePageStatus = false;
            // System.out.println("ggggg:"+response);
            JSONObject resp = JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                PerformOperations(resp);
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        getOffer();
                    }
                };
                handler.postDelayed(r, 500);

            } else {
                progressBar.setVisibility(View.GONE);
                CustomMessage.getInstance().CustomMessage(product.this, "Oops. Something went wrong!");

            }
        } else if (flag.equals("getoverlay")) {
            JSONObject resp = JsonParser.getInstance().parserJsonObject(response);
            System.out.println("overlay" + resp);
            progressBar.setVisibility(View.GONE);
            try {
                String status = resp.getString("status");
                if (!status.equals("error")) {
                    JSONObject data = resp.getJSONObject("data");
                    JSONObject action = data.getJSONObject("action");
                    final String str_uri = action.getString("target");

                    final String strimage = data.getString("image");
                    final boolean bl_close = data.getBoolean("can_close");
                    final String strtilte = data.getString("title");

                    final String str_description = data.getString("description");
                    String actname = action.getString("action_type");
                    final String activityname = actname;
                    boolean bl_active = data.getBoolean("active");
                    final int intDelay = data.getInt("delay");
                    final boolean bl_fullscreen = data.getBoolean("full_screen");
                    final String strbutton = data.getString("cta_text");
                    // ExternalFunctions.strOverlayactivity = actname;
                    ExternalFunctions.bloverlay = true;
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
                                                ExternalFunctions.showOverlay(product.this, strtilte, str_description, strbutton, activityname, bl_fullscreen, bl_close, strimage, str_uri);

                                            } catch (Exception e) {

                                            }
                                        }


                                    }
                                });

                            }
                        }, 500, 500);
                    } else {
                        ExternalFunctions.showOverlay(product.this, strtilte, str_description, strbutton, activityname, bl_fullscreen, bl_close, strimage, str_uri);
                    }


                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (flag.equals("like")) {
            JSONObject resp = JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
//                        lvunLikeplaceholder.setVisibility(View.VISIBLE);
//                        lvLikeplaceholder.setVisibility(View.GONE);
                    } else {
                        CustomMessage.getInstance().CustomMessage(product.this, "Oops. Something went wrong!");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                CustomMessage.getInstance().CustomMessage(product.this, "Oops. Something went wrong!");

            }
        } else if (flag.equals("unlike")) {
            JSONObject resp = JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
//                        lvunLikeplaceholder.setVisibility(View.GONE);
//                        lvLikeplaceholder.setVisibility(View.VISIBLE);
                    } else {
                        CustomMessage.getInstance().CustomMessage(product.this, "Oops. Something went wrong!");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                CustomMessage.getInstance().CustomMessage(product.this, "Oops. Something went wrong!");

            }
        } else if (flag.equals("buy")) {
            JSONObject resp = JsonParser.getInstance().parserJsonObject(response);
            //System.out.println("buy new" + resp);
            if (resp != null) {
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        progress.dismiss();
                        tvBuy.setEnabled(true);
                        DateFormat dfstarttime = new SimpleDateFormat("hh:mm:ss aa");
                        dfstarttime.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
                        starttime = dfstarttime.format(today);
                        tvBuy.setText("GO TO TOTE");
                        BuyCheck = 2;

                    } else {
                        progress.dismiss();
                        tvBuy.setEnabled(true);
                        CustomMessage.getInstance().CustomMessage(product.this, "Oops. Something went wrong!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progress.dismiss();
                    tvBuy.setEnabled(true);
                    CustomMessage.getInstance().CustomMessage(product.this, "Oops. Something went wrong!");
                }
            } else {
                progress.dismiss();
                tvBuy.setEnabled(true);
                CustomMessage.getInstance().CustomMessage(product.this, "Oops. Something went wrong!");
            }
        } else if (flag.equals("admire")) {
            JSONObject resp = JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        tvAdmire.setBackgroundResource(R.drawable.brand_follow_bg);
                        tvAdmire.setTextColor(Color.WHITE);
                        tvAdmire.setText("Admiring");
                        tvAdmire.setEnabled(true);
                        AdmireStatus = true;
                    } else {
                        CustomMessage.getInstance().CustomMessage(product.this, "Oops. Something went wrong!");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    CustomMessage.getInstance().CustomMessage(product.this, "Oops. Something went wrong!");
                }
            } else {
                CustomMessage.getInstance().CustomMessage(product.this, "Oops. Something went wrong!");
            }
        } else if (flag.equals("unadmire")) {
            JSONObject resp = JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        tvAdmire.setBackgroundResource(R.drawable.brand_unfollow_bg);
                        tvAdmire.setTextColor(Color.parseColor("#ff7373"));
                        tvAdmire.setText("Admire");
                        tvAdmire.setEnabled(true);
                        AdmireStatus = false;


                    } else {
                        CustomMessage.getInstance().CustomMessage(product.this, "Oops. Something went wrong!");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    CustomMessage.getInstance().CustomMessage(product.this, "Oops. Something went wrong!");
                }
            } else {
                CustomMessage.getInstance().CustomMessage(product.this, "Oops. Something went wrong!");
            }
        } else if (flag.equals("deleteAlbum")) {
            JSONObject resp = JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                //////System.out.println(resp);
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        tvDelete.setEnabled(true);
                        Intent myprofile = new Intent(product.this, profile.class);
                        myprofile.putExtra("user_id", GetSharedValues.getuserId(this));
                        myprofile.putExtra("p_username", GetSharedValues.getUsername(this));
                        startActivity(myprofile);
                        finish();
                    } else {
                        tvDelete.setEnabled(true);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    tvDelete.setEnabled(true);
                }
            } else {
                tvDelete.setEnabled(true);
                CustomMessage.getInstance().CustomMessage(product.this, "Oops. Something went wrong!");

            }
        } else if (flag.equals("getoffer")) {
            JSONObject resp = JsonParser.getInstance().parserJsonObject(response);
            if (resp.optString("status").equals("success")) {
                offerdata = resp.optJSONArray("data");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DisplayOffers(offerdata, 1);
                    }
                });
            } else {
                lvOfferlayout.setVisibility(View.GONE);
            }
        } else if (flag.equals("offerApply")) {
            System.out.println("TESTTTS : " + response);
            if (response.optString("status").equals("success")) {
                JSONObject Data = response.optJSONObject("data");
                Boolean offerApplied = Data.optJSONObject(ALBUM_ID.toString()).optBoolean("applied");
                final int newPrice = Data.optJSONObject(ALBUM_ID.toString()).optInt("offer_price");
                if (offerApplied) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startCountAnimation(total_price, newPrice);
                        }
                    });
//                    tvListprice.setText(getResources().getString(R.string.Rs) + String.valueOf(newPrice));
                }
            } else {
                CustomMessage.getInstance().CustomMessage(this, "Offer not valid");
                selectedOfferId = 0;
                DisplayOffers(offerdata, offerdata.length());
            }
        }
    }


    private void startCountAnimation(int start, int last) {
        ValueAnimator animator = new ValueAnimator();
        animator.setObjectValues(start,last);
        animator.reverse();
        animator.setDuration(2000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                tvListprice.setText(getResources().getString(R.string.Rs) +  (int) animation.getAnimatedValue());
            }
        });
        animator.start();
    }

    private void DisplayOffers(final JSONArray data, final int limit) {
//        System.out.println("INSDIE FOR : MAIN"+data.length()+"---"+limit);
        lvOfferlayout.setVisibility(View.VISIBLE);
        lvOfferlayout.removeAllViews();
        try {
            for (int i = 0; i < limit; i++) {
                //   System.out.println("INSDIE FOR : LIMIT "+limit);
                // System.out.println("INSDIE FOR : DATA "+data.optJSONObject(i));
                LayoutInflater address_inflater = LayoutInflater.from(product.this);
                final View view;
                view = address_inflater.inflate(R.layout.offer_placeholder, null, false);
                TextView offer_code = (TextView) view.findViewById(R.id.offer_code);
                final TextView offer_applied = (TextView) view.findViewById(R.id.offer_applied);
                TextView offer_title = (TextView) view.findViewById(R.id.offer_title);
                final TextView offer_benifit = (TextView) view.findViewById(R.id.offer_benifit);
                final TextView offer_toggle = (TextView) view.findViewById(R.id.offer_toggle);
                final RadioButton radio_offer_select = (RadioButton) view.findViewById(R.id.radio_offer_select);
                offer_code.setText(data.optJSONObject(i).optString("code"));
                offer_title.setText(data.optJSONObject(i).optString("description"));
                offer_benifit.setText(data.optJSONObject(i).optString("benefit"));
                offer_benifit.setVisibility(View.GONE);
                if (selectedOfferId == data.optJSONObject(i).optInt("id")) {
                    radio_offer_select.setChecked(true);
                } else {
                    radio_offer_select.setChecked(false);
                }


                if (radio_offer_select.isChecked()) {
                    offer_applied.setVisibility(View.VISIBLE);
                    offer_benifit.setVisibility(View.VISIBLE);
                } else {
                    offer_applied.setVisibility(View.GONE);
                    offer_benifit.setVisibility(View.GONE);
                }

                if (data.length() > 1 & limit == 1) {
                    offer_toggle.setVisibility(View.VISIBLE);
                    offer_toggle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            lvOfferlayout.removeAllViews();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DisplayOffers(data, data.length());
                                }
                            });
                            offer_toggle.setVisibility(View.GONE);

                        }
                    });
                } else {
                    offer_toggle.setVisibility(View.GONE);
                }


                final int finalI = i;
                radio_offer_select.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                    if (!radio_offer_select.isChecked()) {
//                        radio_offer_select.setChecked(true);
//                        selectedOffer = String.valueOf(data.optJSONObject(finalI).optInt("id"));
//                        offer_applied.setVisibility(View.VISIBLE);
//                        offer_benifit.setVisibility(View.VISIBLE);
//
//                    }
//
//                    if (offer_applied.getVisibility() == View.GONE) {
//                        offer_applied.setVisibility(View.VISIBLE);
//                        offer_benifit.setVisibility(View.VISIBLE);
//                        selectedOfferId.add(data.optJSONObject(finalI).optInt("id"));
//                        ApplyOffer(data.optJSONObject(finalI).optInt("id"));
//                    }

                        System.out.println("RadioButton  : " + radio_offer_select.isChecked());
                        if (radio_offer_select.isChecked()) {
                            selectedOfferId = data.optJSONObject(finalI).optInt("id");
                            selectedOffer = String.valueOf(data.optJSONObject(finalI).optInt("id"));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ApplyOffer(data.optJSONObject(finalI).optInt("id"));
                                    DisplayOffers(data, limit);

                                }
                            });
                        }

                    }
                });


                lvOfferlayout.addView(view);
            }
        }catch (Exception e){

        }
    }

    private void ApplyOffer(int id) {
        List<Integer> ids = new ArrayList<Integer>();
        ids.add(ALBUM_ID);
        JSONArray idData = new JSONArray(ids);
        JSONObject data = new JSONObject();
        try {
            data.put("products", idData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("DATA : " + data);
        ApiService.getInstance(product.this, 1).postData(product.this, EnvConstants.APP_BASE_URL + "/offer/apply/" + id, data, SCREEN_NAME, "offerApply");
    }


    public void handlelayoutOutofMemmory() {
        ExternalFunctions.nullViewDrawablesRecursive(product_scrollview);
        product_scrollview = null;
        System.gc();


    }

    @Override
    protected void onDestroy() {
        today = new Date();


        endtime = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        HashMap<String, Object> page_change = new HashMap<String, Object>();
        page_change.put("new_page", SCREEN_NAME);
        page_change.put("old_page", ExternalFunctions.prevActivity);
        page_change.put("page_view_starttime", stime);
        page_change.put("page_view_endtime", endtime);
        page_change.put("id", ALBUM_ID);
        cleverTap.event.push("page_change", page_change);
        ExternalFunctions.prevActivity = SCREEN_NAME;
        DateFormat dfclosetime = new SimpleDateFormat("hh:mm:ss aa", Locale.ENGLISH);
        dfclosetime.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        String strclosetime = dfclosetime.format(today);

        super.onDestroy();
        View view = findViewById(R.id.product_scrollview);
//        unbindDrawables(findViewById(R.id.onboarding3Layout));

        int count1 = viewPager.getChildCount();
        for (int i = 0; i < count1; i++) {
            ImageView v1 = (ImageView) viewPager.getChildAt(i).findViewById(R.id.pager_image);
            if (v1 != null) {
                if (v1.getDrawable() != null)
                    v1.getDrawable().setCallback(null);
                Glide.clear(v1);
            }
        }

//        Glide.clear(profileImage);
        //////System.out.println(Runtime.getRuntime().freeMemory() + "__freememory");
        //////System.out.println(Runtime.getRuntime().maxMemory() + "__maxmemory");
        //////System.out.println(Runtime.getRuntime().totalMemory() + "__totalmemory");
        //System.gc();
        ExternalFunctions.FilteredString = "";
        ExternalFunctions.strOverlayurl = "";
        handlelayoutOutofMemmory();
        Runtime.getRuntime().gc();

    }

    @Override
    protected void onStop() {
        super.onStop();
        ExternalFunctions.FilteredString = "";
        ExternalFunctions.strOverlayurl = "";
    }

    @Override
    public void onErrorCallback(VolleyError error, String flag) {
        //////System.out.println(error.getMessage() + "___" + error.getStackTrace());
        if (progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.GONE);
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
        unbindDrawables(findViewById(R.id.product_scrollview));

        ExternalFunctions.deleteCache(product.this);
    }

    @Override
    public void onBackPressed() {
//        Glide.clear(profileImage);
        Intent mydialog = null;
        ExternalFunctions.strOverlayurl = "";
        SharedPreferences FeedSession = getSharedPreferences("FeedSession",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor FeedSessioneditor = FeedSession.edit();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (FeedSession.getBoolean("FeedActionStatus", false)) {
            callingActivity = "activity." + FeedSession.getString("FeedBackAction", "");
            FeedSessioneditor.putString("FeedBackAction", null);
            FeedSessioneditor.putBoolean("FeedActionStatus", false);
            FeedSessioneditor.apply();
        }
        try {
            mydialog = new Intent(product.this, Class.forName(callingActivity));
            mydialog.putExtra("activity", "1");
            mydialog.putExtra("user_id", ProfileUserId);
            mydialog.putExtra("product_id", ALBUM_ID);
            mydialog.putExtra("p_username", profile_user_name);
            mydialog.putExtra("p_usertype", userType);
            mydialog.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mydialog);
            finish();
        } catch (ClassNotFoundException e) {
            if (ExternalFunctions.blapplysearch) {
                mydialog = new Intent(product.this, searchFeedPage.class);
                mydialog.putExtra("activity", "SplashScreen");
                mydialog.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(mydialog);
                finish();
            } else if (ExternalFunctions.blapplysug) {
                finish();
            } else if (ExternalFunctions.dicActivity == "MainFeed") {
                mydialog = new Intent(product.this, MainFeed.class);
                mydialog.putExtra("activity", "SplashScreen");
                startActivity(mydialog);
                finish();
            } else {
                mydialog = new Intent(product.this, discover.class);
                mydialog.putExtra("activity", "SplashScreen");
                //mydialog.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(mydialog);
                finish();
            }

        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
        overlaystatus = true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!GetSharedValues.GetgcmId(product.this).equals("")) {
            ApiService.getInstance(product.this, 1).getData(product.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/?gcm_reg_id=" + GetSharedValues.GetgcmId(product.this), "session");
        } else {
            ApiService.getInstance(product.this, 1).getData(product.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/", "session");
        }
    }

    public void getOffer() {
        ApiService.getInstance(product.this, 1).getData(product.this, true, SCREEN_NAME, EnvConstants.URL_FEED + "/offers/" + ALBUM_ID, "getoffer");
    }


    public class SliderAdaptor extends PagerAdapter {


        private ArrayList<String> IMAGES;
        private LayoutInflater inflater;
        private Context context;


        public SliderAdaptor(Context context, ArrayList<String> IMAGES) {
            try {
                this.context = context;
                this.IMAGES = IMAGES;
                inflater = LayoutInflater.from(context);
            } catch (IndexOutOfBoundsException e) {

            }

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            System.out.println("IMAGES DATA check6: ");
            container.removeView((LinearLayout) object);
        }

        @Override
        public int getCount() {
            System.out.println("IMAGES DATA check5: " + IMAGES.size());
            return IMAGES.size();
        }

        @Override
        public Object instantiateItem(ViewGroup view, final int position) {
            System.out.println("IMAGES DATA check1: ");
            View imageLayout = inflater.inflate(R.layout.product_pager_item, view, false);
            System.out.println("IMAGES DATA check2: ");
            assert imageLayout != null;
            final ImageView imageView = (ImageView) imageLayout
                    .findViewById(R.id.pager_image);

            imageView.getLayoutParams().width = GetSharedValues.getScreenWidth(context);
            imageView.getLayoutParams().height = GetSharedValues.getScreenWidth(context);


            Glide.with(context)
                    .load(EnvConstants.APP_MEDIA_URL + IMAGES.get(position))
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {

                            int width = resource.getWidth(); //prints 0
                            int height = resource.getHeight(); //prints 0

                            System.out.println("CHEKKKKK : " + width + "----" + height);

                            if (width > height) {
                                resource.recycle();
                                System.out.println("outside sqaure if : " + GetSharedValues.getScreenWidth(context) + "---" + (int) (GetSharedValues.getScreenWidth(context) * 0.75));
                                Glide.with(context)
                                        .load(EnvConstants.APP_MEDIA_URL + IMAGES.get(position))
                                        .centerCrop()
                                        .placeholder(R.drawable.playholderscreen)
                                        .crossFade()
//                                        .override(GetSharedValues.getScreenWidth(context), 150)
                                        .override(GetSharedValues.getScreenWidth(context), (int) ((GetSharedValues.getScreenWidth(context)) * 0.75))
                                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                        .into(imageView);

                            } else if (height > width) {
                                resource.recycle();
                                System.out.println("outside sqaure else if");
                                Glide.with(context)
                                        .load(EnvConstants.APP_MEDIA_URL + IMAGES.get(position))
                                        .fitCenter()
                                        .placeholder(R.drawable.playholderscreen)
                                        .crossFade()
                                        .override((int) ((GetSharedValues.getScreenWidth(context)) * 0.75), GetSharedValues.getScreenWidth(context))
                                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                        .into(imageView);
                            } else {
                                resource.recycle();
                                System.out.println("outside sqaure else");
                                Glide.with(context)
                                        .load(EnvConstants.APP_MEDIA_URL + IMAGES.get(position))
                                        .placeholder(R.drawable.playholderscreen)
                                        .crossFade()
                                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                        .into(imageView);


                                if (width >= 1000) {
                                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                } else {
                                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                                }

                            }


                        }


                    });


            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (lvSizeDialog.getVisibility() == View.VISIBLE) {
                        handleDialogueUiThread(DIALOGE_DOWN);
                    } else {
                        Intent zoom = new Intent(context, ZoomActivity.class);
                        zoom.putExtra("currentItem", position);
                        zoom.putStringArrayListExtra("data", IMAGES);
                        context.startActivity(zoom);
                    }
                }
            });
//        imageView.setImageResource(IMAGES.get(position));

            view.addView(imageLayout, 0);
            System.out.println("IMAGES DATA check3: ");
            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            System.out.println("IMAGES DATA check4: ");
            return (view == object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

    }


    public Integer getCartCount() {
        String PrimaryKey = ALBUM_ID.toString() + "-" + selectedSize.toString();
        String sql = "select * from CART where albumId='" + PrimaryKey + "'";
        Cursor cursor = db.getData(sql);
        return cursor.getCount();
    }

}




package activity;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.VolleyError;
//import com.//Appsee.//Appsee;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.naver.android.helloyako.imagecrop.util.BitmapLoadUtils;
//import com.uxcam.UXCam;
import com.zapyle.zapyle.Alerts;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import application.MyApplicationClass;
import models.ColorData;
import models.OccasionData;
import network.JsonParser;
import recievers.UploadReceiver;
import services.UploadService;
import network.ApiCommunication;
import network.ApiService;
import utils.CheckMemory;
import utils.CommonFinish;
import utils.CustomMessage;
import utils.FontUtils;
import utils.GetSharedValues;
import utils.ExternalFunctions;
import utils.Mixpanelutils;


/**
 * Created by haseeb on 21/1/16.
 */
public class Upload1 extends ActionBarActivity implements ApiCommunication, UploadReceiver.Receiver {

    private static final int GALLERY_INTENT_CALLED = 98;
    private static final int GALLERY_KITKAT_INTENT_CALLED = 97;
    private static final int ACTION_REQUEST_CROP = 1;
    private static final int ACTION_REQUEST_BRAND_LIST = 2;
    private static final int ACTION_REQUEST_OCCASION_LIST = 3;
    private static final int ACTION_REQUEST_STYLE_LIST = 4;
    private static final int ACTION_REQUEST_COLOR_LIST = 5;
    private static final int ACTION_REQUEST_CATEGORY_LIST = 6;
    private static final int ACTION_REQUEST_SIZE_LIST = 7;
    private static final int REQUEST_PERMISSION =1 ;
    public static ArrayList<ColorData> colorDatas = new ArrayList<ColorData>();
    public static ArrayList<String> products = new ArrayList<String>();
    public static ArrayList<Integer> productsId = new ArrayList<Integer>();
    public static ArrayList<OccasionData> occasionList = new ArrayList<OccasionData>();
    public static HashMap<String,Integer> brandMap = new HashMap<String, Integer>();
    public static ArrayList<String> brand_products = new ArrayList<String>();
    public static JSONArray data = new JSONArray();


    public String SCREEN_NAME = "UPLOAD1";

    int galleryClick = 0;
    int editgalleryClick = 0;
    public static UploadReceiver mReceiver;

    private Timer t;
    private int TimeCounter = 0;
//  declaration of placeholders

    ImageView Skip, iv_img1, iv_img2, iv_img3, iv_img4, iv_img5, iv_img6;
    ImageView iv_del1, iv_del2, iv_del3, iv_del4, iv_del5, iv_del6;
    TextView display_brand, display_occasion, display_style, display_color, display_size, display_category;
    HorizontalScrollView imageScrollview;
    Switch saleSwitch;
    LinearLayout image_layout, brandlayout, occasionlayout, stylelayout, colorlayout, categorylayout, sizelayout,cat_layout;
    RelativeLayout rlFirst,rl1,rl2,rl3,rl4,rl5;


    ArrayList<String> cropped_images = new ArrayList<String>();
    ArrayList<ImageView> imageholder_list = new ArrayList<ImageView>();
    ArrayList<ImageView> deleteholder_list = new ArrayList<ImageView>();
    ArrayList<RelativeLayout> mainholder_list = new ArrayList<RelativeLayout>();
    //    displaying items
    String selectedBrand = " ", selectedOccasion = " ", selectedStyle = " ", selectedColor = " ", selectedCategory = " ";
    String CategoryType = " ";
    String SizeType = " ";
    Boolean To_Sale = false;
    Boolean color_edit = false;
    Boolean style_edit = false;


    //    selected items
    int selectedBrandid = 0;
    int selectedOccasionid = 0;
    int selectedStyleid = 0;
    int selectedColorid = 0;
    int selectedCategoryId = 0;
    int productId;
    String selctedSale;
    public static ArrayList<String> selectedSize = new ArrayList<String>();
    public static ArrayList<String> selectedQty = new ArrayList<String>();
    public static String selectedSizeType;
    ArrayList<String> selectedSizenames = new ArrayList<String>();

    public static String selectedTitle;
    boolean iseditable=false;
    String selectedDesc;
    Uri originalUri = null;

    EditText title, description;


    ProgressDialog mProgress;
    ProgressBar progressBar;

    Boolean EditStatus;
    int ProductId;
    int CLICK_COUNT = 0;
    JSONObject screenSize;


//    edit details------------

    JSONObject editObject = new JSONObject();
    ArrayList<String> Eimages = new ArrayList<String>();
    ArrayList<String> EimagesReplaced = new ArrayList<String>();
    ArrayList<String> EimagesIds = new ArrayList<String>();
    ArrayList<Integer> tempId = new ArrayList<Integer>();
    ScrollView scv;
    MixpanelAPI mixpanel;
    Date today;
    public static String starttime;
    public static String boolsel;
    TextView next;
    Boolean PTA = false;
    String pta_string;

    File myInternalFile;
    File myExternalFile;
    public static Context CommonContext;
    String callingActivity = "BuySecondPage";
    Tracker mTracker;
    String filePath = null;
    Uri filePathUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_upload1);
        MyApplicationClass application = (MyApplicationClass) getApplication();
        mTracker = application.getDefaultTracker();
        //////Thread.currentThread().setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
        LocalBroadcastManager.getInstance(this).registerReceiver(CommonFinish.broadcastReceiver, new IntentFilter("Upload1"));
//        if(!MyApplicationClass.cContextArray.contains(getApplicationContext())) {
        ExternalFunctions.cContextArray[18] = Upload1.this;
//        }
        //UXCam.startWithKey("1dfb25141864376");
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        mProgress = new ProgressDialog(Upload1.this);
        mixpanel = MixpanelAPI.getInstance(Upload1.this, getResources().getString(R.string.mixpanelToken));
        //Appsee.start("4fb55809ab45411c909521bda720f8e6");
        screenSize = ExternalFunctions.displaymetrics(Upload1.this);
        int screenWeight = screenSize.optInt("height");

        try {
            callingActivity = "activity." + getIntent().getStringExtra("activity");

        } catch (Exception e) {
            callingActivity = "activity." + "BuySecondPage";

        }
        GetOverlay();
//        JSONObject superprop = new JSONObject();
//        try {
//            superprop.put("Event Name", "Upload Start");
//            superprop.put("Event Name", "Upload product");
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        mixpanel.registerSuperPropertiesOnce(superprop);

        today = new Date();
        DateFormat dfstarttime = new SimpleDateFormat("hh:mm:ss aa");
        dfstarttime.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        starttime = dfstarttime.format(today);
        //////System.out.println("mixpaneltime" + starttime);
        next = (TextView) findViewById(R.id.upload_next1);
        JSONObject propOpenapp = new JSONObject();
        try {
            propOpenapp.put("Time", starttime);
            propOpenapp.put("Event Name", "Upload Start");
            mixpanel.track("Upload Start", propOpenapp);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        // next
        mProgress.setCancelable(false);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        progressBar = (ProgressBar) findViewById(R.id.edit_progressBar);

        View view = findViewById(R.id.upload1_layout);
        FontUtils.setCustomFont(view, getAssets());
        ProductId = getIntent().getIntExtra("ProductId", 0);
        EditStatus = getIntent().getBooleanExtra("EditStatus", false);
        PTA = getIntent().getBooleanExtra("PTA", false);


        if (EditStatus) {
            getDataToEdit(ProductId);
        }
        ExternalFunctions.bloverlay=false;
        scv = (ScrollView) findViewById(R.id.desc_layout);
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final View customView = getLayoutInflater().inflate(R.layout.product_bar, null);
        getSupportActionBar().setCustomView(customView);
        Toolbar parent = (Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0, 0);
        TextView tv_headTitle = (TextView) findViewById(R.id.product_title_text);
        tv_headTitle.setText("LISTING DETAILS");

        title = (EditText) findViewById(R.id.editText_title);
        description = (EditText) findViewById(R.id.descriptext);



        Skip = (ImageView) findViewById(R.id.productfeedButton);
        Skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        image_layout = (LinearLayout) findViewById(R.id.image_layout);
        brandlayout = (LinearLayout) findViewById(R.id.brandlayout);
        occasionlayout = (LinearLayout) findViewById(R.id.occasionlayout);
        stylelayout = (LinearLayout) findViewById(R.id.stylelayout);
        colorlayout = (LinearLayout) findViewById(R.id.colorlayout);
        categorylayout = (LinearLayout) findViewById(R.id.category_layout);
        sizelayout = (LinearLayout) findViewById(R.id.size_layout);
        cat_layout = (LinearLayout) findViewById(R.id.cat_layout);

        cat_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(next.getVisibility() != View.VISIBLE){
                    next.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });

        imageScrollview = (HorizontalScrollView) findViewById(R.id.horizontal_scrollview);



        rl1 = (RelativeLayout) findViewById(R.id.u_rl1);
        rl2 = (RelativeLayout) findViewById(R.id.u_rl2);
        rl3 = (RelativeLayout) findViewById(R.id.u_rl3);
        rl4 = (RelativeLayout) findViewById(R.id.u_rl4);
        rl5 = (RelativeLayout) findViewById(R.id.u_rl5);
        rlFirst = (RelativeLayout) findViewById(R.id.u_rl_first);

        rlFirst.setTag(1);
        rl1.setTag(2);
        rl2.setTag(3);
        rl3.setTag(4);
        rl4.setTag(5);
        rl5.setTag(6);


        iv_img1 = (ImageView) findViewById(R.id.up_img1);
        iv_img2 = (ImageView) findViewById(R.id.up_img2);
        iv_img3 = (ImageView) findViewById(R.id.up_img3);
        iv_img4 = (ImageView) findViewById(R.id.up_img4);
        iv_img5 = (ImageView) findViewById(R.id.up_img5);
        iv_img6 = (ImageView) findViewById(R.id.up_img6);


        iv_img1.setVisibility(View.VISIBLE);
        iv_img2.setVisibility(View.GONE);
        iv_img3.setVisibility(View.GONE);
        iv_img4.setVisibility(View.GONE);
        iv_img5.setVisibility(View.GONE);
        iv_img6.setVisibility(View.GONE);

        rl1.setVisibility(View.GONE);
        rl2.setVisibility(View.GONE);
        rl3.setVisibility(View.GONE);
        rl4.setVisibility(View.GONE);
        rl5.setVisibility(View.GONE);
        mainholder_list.add(rl1);
        mainholder_list.add(rl2);
        mainholder_list.add(rl3);
        mainholder_list.add(rl4);
        mainholder_list.add(rl5);

        imageholder_list.add(iv_img1);
        imageholder_list.add(iv_img2);
        imageholder_list.add(iv_img3);
        imageholder_list.add(iv_img4);
        imageholder_list.add(iv_img5);
        imageholder_list.add(iv_img6);


        iv_del1 = (ImageView) findViewById(R.id.del1);
        iv_del2 = (ImageView) findViewById(R.id.del2);
        iv_del3 = (ImageView) findViewById(R.id.del3);
        iv_del4 = (ImageView) findViewById(R.id.del4);
        iv_del5 = (ImageView) findViewById(R.id.del5);
        iv_del6 = (ImageView) findViewById(R.id.del6);
        iv_del1.setEnabled(false);

        deleteholder_list.add(iv_del1);
        deleteholder_list.add(iv_del2);
        deleteholder_list.add(iv_del3);
        deleteholder_list.add(iv_del4);
        deleteholder_list.add(iv_del5);
        deleteholder_list.add(iv_del6);

        display_brand = (TextView) findViewById(R.id.display_brand);
        display_occasion = (TextView) findViewById(R.id.display_occasion);
        display_style = (TextView) findViewById(R.id.display_style);
        display_color = (TextView) findViewById(R.id.display_color);
        display_category = (TextView) findViewById(R.id.display_category);
        display_size = (TextView) findViewById(R.id.display_size);
        saleSwitch = (Switch) findViewById(R.id.saleSwitch);

        if (next.getVisibility() == View.INVISIBLE) {
            next.setVisibility(View.VISIBLE);
        }

        categorylayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CLICK_COUNT = 0;
                if(next.getVisibility() != View.VISIBLE){
                    next.setVisibility(View.VISIBLE);
                }
                if (GetSharedValues.LoginStatus(Upload1.this)) {
                    if (next.getVisibility() == View.INVISIBLE) {
                        next.setVisibility(View.VISIBLE);
                    }
                    if(EditStatus){
                        Intent brandAct = new Intent(Upload1.this, Category.class);
                        brandAct.putExtra("EditStatus", true);
                        brandAct.putExtra("SelectedID",selectedCategoryId);
                        startActivityForResult(brandAct, ACTION_REQUEST_CATEGORY_LIST);
                    }
                    else {
                        Intent brandAct = new Intent(Upload1.this, Category.class);
                        brandAct.putExtra("EditStatus", false);
                        startActivityForResult(brandAct, ACTION_REQUEST_CATEGORY_LIST);
                    }
                } else {
                    Alerts.loginAlert(Upload1.this);
                }
            }
        });

        sizelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CLICK_COUNT = 0;
                if(next.getVisibility() != View.VISIBLE){
                    next.setVisibility(View.VISIBLE);
                }
                if (GetSharedValues.LoginStatus(Upload1.this)) {
                    if (next.getVisibility() == View.INVISIBLE) {
                        next.setVisibility(View.VISIBLE);
                    }
                    SizeguideActivity.selectedqty.clear();
                    if (selectedCategoryId != 0) {
                        if (!iseditable){
                            selectedSize.clear();
                            selectedQty.clear();
                            selectedSizenames.clear();


                        }

                        Intent brandAct = new Intent(Upload1.this, SizeguideActivity.class);
                        brandAct.putExtra("sizecategory", CategoryType);
                        brandAct.putExtra("user_type", GetSharedValues.getUsertype(Upload1.this));
                        startActivityForResult(brandAct, ACTION_REQUEST_SIZE_LIST);
                    } else {
                         CustomMessage.getInstance().CustomMessage(Upload1.this, "Select category");
                    }
                } else {
                    Alerts.loginAlert(Upload1.this);
                }
            }
        });


        brandlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CLICK_COUNT = 0;
                if(next.getVisibility() != View.VISIBLE){
                    next.setVisibility(View.VISIBLE);
                }
                if (GetSharedValues.LoginStatus(Upload1.this)) {
                    if (next.getVisibility() == View.INVISIBLE) {
                        next.setVisibility(View.VISIBLE);
                    }
                    if(EditStatus){
                        Intent brandAct = new Intent(Upload1.this, BrandList.class);
                        brandAct.putExtra("EditStatus", true);
                        brandAct.putExtra("SelectedID",selectedBrandid);
                        brandAct.putExtra("SelectedBrand",selectedBrand);
                        startActivityForResult(brandAct, ACTION_REQUEST_BRAND_LIST);

                    } else {
                        Intent brandAct = new Intent(Upload1.this, BrandList.class);
                        brandAct.putExtra("EditStatus", false);
                        startActivityForResult(brandAct, ACTION_REQUEST_BRAND_LIST);
                    }
                } else {
                    Alerts.loginAlert(Upload1.this);
                }
            }
        });

        occasionlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CLICK_COUNT = 0;
                if(next.getVisibility() != View.VISIBLE){
                    next.setVisibility(View.VISIBLE);
                }
                if (GetSharedValues.LoginStatus(Upload1.this)) {
                    if (next.getVisibility() == View.INVISIBLE) {
                        next.setVisibility(View.VISIBLE);
                    }
                    if(EditStatus){
                        Intent brandAct = new Intent(Upload1.this, Occasion.class);
                        brandAct.putExtra("EditStatus", true);
                        brandAct.putExtra("SelectedID",selectedOccasionid);
                        startActivityForResult(brandAct, ACTION_REQUEST_OCCASION_LIST);

                    }
                    else {
                        Intent brandAct = new Intent(Upload1.this, Occasion.class);
                        brandAct.putExtra("EditStatus", false);
                        startActivityForResult(brandAct, ACTION_REQUEST_OCCASION_LIST);
                    }
                } else {
                    Alerts.loginAlert(Upload1.this);
                }
            }
        });

        stylelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CLICK_COUNT = 0;
                if(next.getVisibility() != View.VISIBLE){
                    next.setVisibility(View.VISIBLE);
                }
                if (GetSharedValues.LoginStatus(Upload1.this)) {
                    if (next.getVisibility() == View.INVISIBLE) {
                        next.setVisibility(View.VISIBLE);
                    }
                    if(style_edit){
                        Intent brandAct = new Intent(Upload1.this, Style.class);
                        brandAct.putExtra("EditStatus", true);
                        brandAct.putExtra("SelectedID",selectedStyleid);
                        startActivityForResult(brandAct, ACTION_REQUEST_STYLE_LIST);
                    }
                    else {
                        Intent brandAct = new Intent(Upload1.this, Style.class);
                        brandAct.putExtra("EditStatus", false);
                        startActivityForResult(brandAct, ACTION_REQUEST_STYLE_LIST);
                    }
                } else {
                    Alerts.loginAlert(Upload1.this);
                }
            }
        });

        colorlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CLICK_COUNT = 0;
                if(next.getVisibility() != View.VISIBLE){
                    next.setVisibility(View.VISIBLE);
                }
                if (GetSharedValues.LoginStatus(Upload1.this)) {
                    if (next.getVisibility() == View.INVISIBLE) {
                        next.setVisibility(View.VISIBLE);
                    }
                    if(color_edit){
                        Intent brandAct = new Intent(Upload1.this, Colors.class);
                        brandAct.putExtra("EditStatus", true);
                        brandAct.putExtra("SelectedID",selectedColorid);
                        startActivityForResult(brandAct, ACTION_REQUEST_COLOR_LIST);

                    }
                    else {
                        Intent brandAct = new Intent(Upload1.this, Colors.class);
                        brandAct.putExtra("EditStatus", false);
                        startActivityForResult(brandAct, ACTION_REQUEST_COLOR_LIST);
                    }

                } else {
                    Alerts.loginAlert(Upload1.this);
                }
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case GALLERY_INTENT_CALLED:
                    originalUri = data.getData();
                    filePath = BitmapLoadUtils.getPathFromUri(this, originalUri);
                    filePathUri = Uri.parse(filePath);
                    doCrop(filePathUri);
                    break;

                case GALLERY_KITKAT_INTENT_CALLED:
                    originalUri = data.getData();
                    String wholeID = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        wholeID = DocumentsContract.getDocumentId(originalUri);
                        String id = wholeID.split(":")[1];
                        String[] column = {MediaStore.Images.Media.DATA};
                        String sel = MediaStore.Images.Media._ID + "=?";
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    REQUEST_PERMISSION);
                            return;
                        }
                        else{
                            //dada
                            Cursor cursor = getContentResolver().
                                    query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                            column, sel, new String[]{id}, null);
                            int columnIndex = cursor.getColumnIndex(column[0]);

                            if (cursor.moveToFirst()) {
                                filePath = cursor.getString(columnIndex);
                            }
                            filePathUri = Uri.parse(filePath);
                            cursor.close();
                            doCrop(filePathUri);
                        }

                        break;
                    }


                case ACTION_REQUEST_CROP:

                    String getImgUrl = data.getStringExtra("imgUrl");
                    if (getImgUrl != null) {
                        //////System.out.println("urrrrrrrrrrrrrrrrrrl" + getImgUrl);
                        if (!EditStatus) {
                            if (cropped_images.size() >= galleryClick) {
                                cropped_images.remove(galleryClick - 1);
                                cropped_images.add((galleryClick - 1), getImgUrl);
                            } else {
                                cropped_images.add(getImgUrl);

                            }
                            displayImages(cropped_images.size());
                        } else {
                            if (cropped_images.size() >= galleryClick) {
                                cropped_images.remove(galleryClick - 1);
                                cropped_images.add((galleryClick - 1), getImgUrl);
                                EimagesReplaced.add(getImgUrl);
                                if (!tempId.contains(galleryClick - 1)) {
                                    tempId.add(galleryClick - 1);
                                }
                                if (Eimages.size() >= galleryClick) {
                                    Eimages.remove(galleryClick - 1);
                                    Eimages.add((galleryClick - 1), getImgUrl);
                                }

                            } else {
                                cropped_images.add(getImgUrl);
                                Eimages.add(getImgUrl);

                            }
                            displayImages(cropped_images.size());
                            ////System.out.println("TEP:tempid__" + tempId);
                            ////System.out.println("TEP:Eid__" + EimagesIds);
                            ////System.out.println("TEP:Eidimages__" + Eimages);
                            for (int i = 0; i < tempId.size(); i++) {
                                if (tempId.get(i) < EimagesIds.size()) {
                                    //////System.out.println("inside that if");

                                    int index = tempId.get(i);
                                    //////System.out.println("tempId.get(i)__" + tempId.get(i));
                                    EimagesIds.remove(index);
                                }
                            }
                            //////System.out.println("SecEid__" + EimagesIds);
                            //////System.out.println("SecEidimages__" + Eimages);
                        }
                        break;


                    }

                case ACTION_REQUEST_BRAND_LIST:
                    scv.scrollTo(0, 0);
                    selectedBrand = data.getStringExtra("brand");
                    selectedBrandid = data.getIntExtra("brandid", 0);
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    //////System.out.println("jjjjjjjjjjjjjjjj" + selectedBrand + "___" + selectedBrandid);
                    if (selectedBrand.length() > 0) {
                        display_brand.setText(selectedBrand.toUpperCase());
                    }
                    if (selectedOccasion.length() > 0) {
                        display_occasion.setText(selectedOccasion.toUpperCase());
                    }
                    if (selectedStyle.length() > 0) {
                        display_style.setText(selectedStyle.toUpperCase());
                    }
                    if (selectedColor.length() > 0) {
                        display_color.setText(selectedColor.toUpperCase());

                    }
                    if (selectedCategory.length() > 0) {
                        display_category.setText(selectedCategory.toUpperCase());
                    }
                    if (CategoryType.length() > 0) {
                        if (CategoryType.equals("FS")) {
                            display_size.setText("FREESIZE");
                            sizelayout.setEnabled(false);
                            selectedSize.clear();
                            selectedSize.add("freesize");
                            SizeType = "FREESIZE";
                        } else {
                            display_size.setText("");
                            sizelayout.setEnabled(true);
                        }
                    }
                    if (selectedSizenames.size() > 0) {
                        SizeType = selectedSizenames.get(0).toString().substring(0, 2);
                        if (GetSharedValues.getUsertype(Upload1.this).equals("store_front")) {

                            if (selectedSize.size() > 2) {
                                display_size.setText(selectedSizenames.get(0).toUpperCase() + "," + selectedSizenames.get(1).toUpperCase() + "...");
                            } else {
                                display_size.setText(selectedSizenames.get(0).toUpperCase() + "...");
                            }
                        } else {
                            display_size.setText(selectedSizenames.get(0).toUpperCase());
                        }
                    }
                    break;


                case ACTION_REQUEST_OCCASION_LIST:
                    scv.scrollTo(0, 0);
                    //////System.out.println("inside onresult__" + data);
                    selectedOccasion = data.getStringExtra("occasion");
                    selectedOccasionid = data.getIntExtra("occasionid", 0);
                    //////System.out.println("jjjjjjjjjjjjjjjj" + selectedOccasion + "___" + selectedOccasionid);
                    if (selectedBrand.length() > 0) {
                        display_brand.setText(selectedBrand.toUpperCase());
                    }
                    if (selectedOccasion.length() > 0) {
                        display_occasion.setText(selectedOccasion.toUpperCase());
                    }
                    if (selectedStyle.length() > 0) {
                        display_style.setText(selectedStyle.toUpperCase());
                    }
                    if (selectedColor.length() > 0) {
                        display_color.setText(selectedColor.toUpperCase());

                    }
                    if (selectedCategory.length() > 0) {
                        display_category.setText(selectedCategory.toUpperCase());
                    }
                    if (CategoryType.length() > 0) {
                        if (CategoryType.equals("FS")) {
                            display_size.setText("FREESIZE");
                            sizelayout.setEnabled(false);
                            selectedSize.clear();
                            selectedSize.add("freesize");
                            SizeType = "FREESIZE";
                        } else {
                            display_size.setText("");
                            sizelayout.setEnabled(true);
                        }
                    }
                    if (selectedSizenames.size() > 0) {
                        SizeType = selectedSizenames.get(0).toString().substring(0, 2);
                        if (GetSharedValues.getUsertype(Upload1.this).equals("store_front")) {

                            if (selectedSize.size() > 2) {
                                display_size.setText(selectedSizenames.get(0).toUpperCase() + "," + selectedSizenames.get(1).toUpperCase() + "...");
                            } else {
                                display_size.setText(selectedSizenames.get(0).toUpperCase() + "...");
                            }
                        } else {
                            display_size.setText(selectedSizenames.get(0).toUpperCase());
                        }
                    }
                    break;


                case ACTION_REQUEST_STYLE_LIST:
                    scv.scrollTo(0, 0);
                    //////System.out.println("inside onresult__" + data);
                    selectedStyle = data.getStringExtra("style");
                    selectedStyleid = data.getIntExtra("styleid", 0);
                    //////System.out.println("jjjjjjjjjjjjjjjj" + selectedStyle + "___" + selectedStyleid);
                    if (selectedBrand.length() > 0) {
                        display_brand.setText(selectedBrand.toUpperCase());
                    }
                    if (selectedOccasion.length() > 0) {
                        display_occasion.setText(selectedOccasion.toUpperCase());
                    }
                    if (selectedStyle.length() > 0) {
                        display_style.setText(selectedStyle.toUpperCase());
                    }
                    if (selectedColor.length() > 0) {
                        display_color.setText(selectedColor.toUpperCase());

                    }
                    if (selectedCategory.length() > 0) {
                        display_category.setText(selectedCategory.toUpperCase());
                    }
                    if (CategoryType.length() > 0) {
                        if (CategoryType.equals("FS")) {
                            display_size.setText("FREESIZE");
                            sizelayout.setEnabled(false);
                            selectedSize.clear();
                            selectedSize.add("freesize");
                            SizeType = "FREESIZE";
                        } else {
                            display_size.setText("");
                            sizelayout.setEnabled(true);
                        }
                    }
                    if (selectedSizenames.size() > 0) {
                        SizeType = selectedSizenames.get(0).toString().substring(0, 2);
                        if (GetSharedValues.getUsertype(Upload1.this).equals("store_front")) {

                            if (selectedSize.size() > 2) {
                                display_size.setText(selectedSizenames.get(0).toUpperCase() + "," + selectedSizenames.get(1).toUpperCase() + "...");
                            } else {
                                display_size.setText(selectedSizenames.get(0).toUpperCase() + "...");
                            }
                        } else {
                            display_size.setText(selectedSizenames.get(0).toUpperCase());
                        }
                    }
                    break;

                case ACTION_REQUEST_COLOR_LIST:
                    scv.scrollTo(0, 0);
                    //////System.out.println("inside onresult__" + data);
                    selectedColor = data.getStringExtra("color");
                    selectedColorid = data.getIntExtra("colorid", 0);
                    //////System.out.println("jjjjjjjjjjjjjjjj" + selectedColor + "___" + selectedColorid);
                    if (selectedBrand.length() > 0) {
                        display_brand.setText(selectedBrand.toUpperCase());
                    }
                    if (selectedOccasion.length() > 0) {
                        display_occasion.setText(selectedOccasion.toUpperCase());
                    }
                    if (selectedStyle.length() > 0) {
                        display_style.setText(selectedStyle.toUpperCase());
                    }
                    if (selectedColor.length() > 0) {
                        display_color.setText(selectedColor.toUpperCase());

                    }
                    if (selectedCategory.length() > 0) {
                        display_category.setText(selectedCategory.toUpperCase());
                    }
                    if (CategoryType.length() > 0) {
                        if (CategoryType.equals("FS")) {
                            display_size.setText("FREESIZE");
                            sizelayout.setEnabled(false);
                            selectedSize.clear();
                            selectedSize.add("freesize");
                            SizeType = "FREESIZE";
                        } else {
                            display_size.setText("");
                            sizelayout.setEnabled(true);
                        }
                    }
                    if (selectedSizenames.size() > 0) {
                        SizeType = selectedSizenames.get(0).toString().substring(0, 2);
                        if (GetSharedValues.getUsertype(Upload1.this).equals("store_front")) {

                            if (selectedSize.size() > 2) {
                                display_size.setText(selectedSizenames.get(0).toUpperCase() + "," + selectedSizenames.get(1).toUpperCase() + "...");
                            } else {
                                display_size.setText(selectedSizenames.get(0).toUpperCase() + "...");
                            }
                        } else {
                            display_size.setText(selectedSizenames.get(0).toUpperCase());
                        }
                    }
                    break;

                case ACTION_REQUEST_CATEGORY_LIST:
                    scv.scrollTo(0, 0);
                    //////System.out.println("inside onresult__" + data);
                    selectedCategory = data.getStringExtra("subcatname");
                    selectedCategoryId = data.getIntExtra("subcatid", 0);
                    CategoryType = data.getStringExtra("catType");
                    ////System.out.println("jjjjjjjjjjjjjjjj" + selectedCategory + "___" + selectedCategoryId + "__" + CategoryType);
                    if (selectedBrand.length() > 0) {
                        display_brand.setText(selectedBrand.toUpperCase());
                    }
                    if (selectedOccasion.length() > 0) {
                        display_occasion.setText(selectedOccasion.toUpperCase());
                    }
                    if (selectedStyle.length() > 0) {
                        display_style.setText(selectedStyle.toUpperCase());
                    }
                    if (selectedColor.length() > 0) {
                        display_color.setText(selectedColor.toUpperCase());

                    }
                    if (selectedCategory.length() > 0) {
                        display_category.setText(selectedCategory.toUpperCase());
                    }
                    if (CategoryType.length() > 0) {
                        if (CategoryType.equals("FS")) {
                            ////System.out.println("oooooooooooooooo:"+CategoryType);
                            display_size.setText("FREESIZE");
                            sizelayout.setEnabled(false);
                            selectedSize.clear();
                            selectedSize.add("freesize");
                            SizeType = "FREESIZE";
                        } else {
                            ////System.out.println("ooee:"+CategoryType);
                            display_size.setText("");
                            sizelayout.setEnabled(true);
                        }
                    }
                    ////System.out.println("ooee:"+selectedSizenames.size());
                    if (selectedSizenames.size() > 0) {
                        SizeType = selectedSizenames.get(0).toString().substring(0, 2);
                        if (GetSharedValues.getUsertype(Upload1.this).equals("store_front")) {

                            if (selectedSize.size() > 2) {
                                display_size.setText(selectedSizenames.get(0).toUpperCase() + "," + selectedSizenames.get(1).toUpperCase() + "...");
                            } else {
                                display_size.setText(selectedSizenames.get(0).toUpperCase() + "...");
                            }
                        } else {
                            display_size.setText(selectedSizenames.get(0).toUpperCase());
                        }
                    }
                    break;


                case ACTION_REQUEST_SIZE_LIST:
                    scv.scrollTo(0, 0);

                    // //////System.out.println("inside onresult__" +  sizeitemListToJsonConvert());
                    selectedSize.clear();
                    selectedSizenames.clear();
                    selectedSize = data.getStringArrayListExtra("SelectedID");
                    selectedSizenames = data.getStringArrayListExtra("selectedSizes");
                    ////System.out.println("jjjjjjjjjjjjjjjj" + selectedSize + "___" + selectedSizenames.get(0).toString().substring(0, 2));
                    if (selectedBrand.length() > 0) {
                        display_brand.setText(selectedBrand.toUpperCase());
                    }
                    if (selectedOccasion.length() > 0) {
                        display_occasion.setText(selectedOccasion.toUpperCase());
                    }
                    if (selectedStyle.length() > 0) {
                        display_style.setText(selectedStyle.toUpperCase());
                    }
                    if (selectedColor.length() > 0) {
                        display_color.setText(selectedColor.toUpperCase());

                    }
                    if (selectedCategory.length() > 0) {
                        display_category.setText(selectedCategory.toUpperCase());
                    }
                    if (CategoryType.length() > 0) {
                        if (CategoryType.equals("FS")) {
                            display_size.setText("FREESIZE");
                            sizelayout.setEnabled(false);
                            selectedSize.clear();
                            selectedSize.add("freesize");
                            SizeType = "FREESIZE";
                        } else {
                            display_size.setText("");
                            sizelayout.setEnabled(true);
                        }
                    }
                    if (selectedSizenames.size() > 0) {
                        SizeType = selectedSizenames.get(0).toString().substring(0, 2);
                        if (GetSharedValues.getUsertype(Upload1.this).equals("store_front")) {
                            if (selectedSize.size() > 2) {
                                display_size.setText(selectedSizenames.get(0).toUpperCase() + "," + selectedSizenames.get(1).toUpperCase() + "...");
                            } else {
                                display_size.setText(selectedSizenames.get(0).toUpperCase() + "...");
                            }
                        } else {
                            display_size.setText(selectedSizenames.get(0).toUpperCase());
                        }
                    }
                    break;

            }
        }

    }


//    Activity functions
//    -------------------------------------------------------------------
private void GetOverlay() {
//    progressBar.setVisibility(View.VISIBLE);
    ApiService.getInstance(Upload1.this, 1).getData(Upload1.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/marketing/overlay/upload", "getoverlay");
}

    public void choosegallery(View v) {
        View parent = (View) v.getParent();
        galleryClick = Integer.parseInt(parent.getTag().toString());
        editgalleryClick = Integer.parseInt(parent.getTag().toString());
        if (Build.VERSION.SDK_INT < 19) {
            Intent intent = new Intent();
            intent.setType("image/jpeg");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, getString(R.string.ChoosePicture)), GALLERY_INTENT_CALLED);
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/jpeg");
            startActivityForResult(intent, GALLERY_KITKAT_INTENT_CALLED);
        }

    }


    private void doCrop(Uri path) {
        //////System.out.println("jjjjjjjjjjjjjjjj" + path);
        Intent intent = new Intent(getApplicationContext(), CropActivity.class);
        intent.setData(path);
        startActivityForResult(intent, ACTION_REQUEST_CROP);
        onResume();


    }

    public void clearViews() {
        for (int i = 0; i < 6; i++) {
            imageholder_list.get(i).setImageURI(null);
            imageholder_list.get(i).setImageResource(android.R.color.transparent);
            deleteholder_list.get(i).setVisibility(View.GONE);
            imageholder_list.get(i).setVisibility(View.GONE);
        }
    }


    private void displayImages(int size) {
        //////System.out.println("deisplayimages__" + cropped_images);
        if(size <= 5) {
            ////System.out.println("iiiiii555555555<<<");
            for (int j = 0; j < size; j++) {
                ////System.out.println("iiiiii555555555<<<:"+mainholder_list.get(j));
                mainholder_list.get(j).setVisibility(View.VISIBLE);
            }
        }
        else {
            ////System.out.println("iiiiiiiii555555>>>>");
            for (int j = 0; j < size-1; j++) {
                ////System.out.println("iiiiiiiii555555>>>>:"+mainholder_list.get(j));
                mainholder_list.get(j).setVisibility(View.VISIBLE);
            }
        }

        if (size > 0) {
            if (size <= 6) {
                for (int i = 0; i < size; i++) {

                    imageholder_list.get(i).setVisibility(View.VISIBLE);
                    imageholder_list.get(i).setImageURI(null);
                    imageholder_list.get(i).setImageResource(android.R.color.transparent);
                    if (cropped_images.get(i).contains("image_crop_sample")) {
                        BitmapFactory.Options mBitmapOptions = new BitmapFactory.Options();
                        mBitmapOptions.inScaled = true;
                        mBitmapOptions.inSampleSize = 4;
                        int srcWidth = mBitmapOptions.outWidth;
                        int srcHeight = mBitmapOptions.outHeight;
                        mBitmapOptions.inDensity = srcWidth;
                        mBitmapOptions.inTargetDensity =  100 * mBitmapOptions.inSampleSize;

// will load & resize the image to be 1/inSampleSize dimensions
                        Bitmap mCurrentBitmap = BitmapFactory.decodeFile(cropped_images.get(i), mBitmapOptions);
//                        imageholder_list.get(i).setImageURI(Uri.parse(cropped_images.get(i)));
                        imageholder_list.get(i).setImageBitmap(mCurrentBitmap);
                    } else {

                        Glide.with(Upload1.this)
                                .load(EnvConstants.APP_MEDIA_URL + cropped_images.get(i))
                                .fitCenter()
                                .placeholder(R.drawable.playholderscreen)
                                .crossFade()
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .override(100,133)
                                .into(imageholder_list.get(i));
                    }
                    imageholder_list.get(i).setScaleType(ImageView.ScaleType.FIT_XY);
                    deleteholder_list.get(i).setVisibility(View.VISIBLE);
                    deleteholder_list.get(i).setTag(cropped_images.get(i));
                }
            }
            if (size < 6) {
                imageholder_list.get(size).setVisibility(View.VISIBLE);
                imageholder_list.get(size).setImageResource(R.drawable.plus);
                imageholder_list.get(size).setScaleType(ImageView.ScaleType.CENTER);
            }
        }
    }


    public void deleteImage(View v) {


        ////System.out.println("delete__" + v.getTag().toString() + cropped_images);
        v.setVisibility(View.VISIBLE);
        if (cropped_images.contains(v.getTag())) {
            int index = cropped_images.indexOf(v.getTag().toString());
            cropped_images.remove(index);
            if (EimagesIds.size() >= index) {
                EimagesIds.remove(index);
            }
        }
        if (Eimages.contains(v.getTag())) {
            int index = Eimages.indexOf(v.getTag().toString());
            Eimages.remove(index);
        }
        ////System.out.println("TEP:"+Eimages+"___"+Eimages.size());
        //////System.out.println("after delete__" + v.getTag().toString() + cropped_images);
        clearViews();
        if (cropped_images.size() > 0) {
            for (int j = 0; j < 5; j++) {
                ////System.out.println("iiiiii555555555<<<delete:"+mainholder_list.get(j));
                mainholder_list.get(j).setVisibility(View.GONE);
            }
            displayImages(cropped_images.size());

        } else {
            choosegallery(v);
        }
    }


//    validation functions
//    -------------------------------------------------------------------

    public Boolean validateData() {
        selectedDesc = description.getText().toString();
        selectedTitle = title.getText().toString();
        if (selectedDesc.length() > 0 && selectedTitle.length() > 0) {
            if (selectedCategoryId > 0 && selectedBrandid > 0 && selectedColorid > 0 && selectedOccasionid > 0 && selectedStyleid > 0) {
                if (selectedSize.size() > 0) {
                    return cropped_images.size() > 0;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public String validate() {

        selectedDesc = description.getText().toString();
        selectedTitle = title.getText().toString();
        if (cropped_images.size() <= 0) {
            return "Please add image.";
        } else if (selectedDesc.trim().length() <= 1) {
            return "Please add description.";

        } else if (selectedDesc.trim().length() < 20) {
            return "Description should contain minimum 20 characters.";

        } else if (selectedTitle.trim().length() <= 1) {
            return "Please add title.";
        } else if (selectedCategoryId <= 0) {
            categorylayout.setFocusable(true);
            return "Please add category.";

        } else if (selectedBrandid <= 0) {
            brandlayout.setFocusable(true);
            return "Please add brand.";

        } else if (selectedSize.size() <= 0) {
            sizelayout.setFocusable(true);
            return "Please add size.";
        } else if (selectedOccasionid <= 0) {
            occasionlayout.setFocusable(true);
            return "Please add occasion.";
        }

        return "true";
    }


//    Server requests functions
//    -------------------------------------------------------------------

    public void uploadNext1(View v) {
        if (GetSharedValues.LoginStatus(Upload1.this)) {
            if (!EditStatus) {
                String strvalidate = validate();
                if (strvalidate.equals("true")) {
                    next.setEnabled(false);
                    postData();
                } else {
                     CustomMessage.getInstance().CustomMessage(Upload1.this, strvalidate);
                }
            } else {
                next.setEnabled(false);
                if(!CategoryType.equals("FS")){
                    if(sizeitemListToJsonConvert().length() > 0){
                        postEditData();
                    }
                    else {
                        next.setEnabled(true);
                         CustomMessage.getInstance().CustomMessage(Upload1.this, "Select size");
                    }
                }
                else {
                    postEditData();
                }
            }
        } else {
            Alerts.loginAlert(Upload1.this);
        }

    }

    private void postData() {
        mProgress.setMessage("Uploading ...");
        mProgress.show();
        JSONArray size = new JSONArray();
        JSONObject sizeObject = new JSONObject();
        ArrayList<JSONObject> sizeList = new ArrayList<JSONObject>();
        if (CategoryType.equals("FS")) {
            try {
                sizeObject.put("id", "freesize");
                sizeObject.put("qty", 1);
                sizeList.add(sizeObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        size = new JSONArray(sizeList);

//        if (saleSwitch.isChecked()) {
        boolsel = "true";
        selctedSale = "2";
//        } else {
//            boolsel = "false";
//            selctedSale = "1";
//
//        }
        //////System.out.println("sizeitemListToJsonConvert :" + sizeitemListToJsonConvert());

        JSONObject toserverData = new JSONObject();
        try {
            toserverData.put("title", selectedTitle);
            toserverData.put("description", selectedDesc);
            toserverData.put("sub_category", selectedCategoryId);
            toserverData.put("brand", selectedBrandid);
            if (CategoryType.equals("FS")) {
                toserverData.put("size", size);
            } else {
                toserverData.put("size", sizeitemListToJsonConvert());
            }

            if (selectedColorid > 0) {
                toserverData.put("color", selectedColorid);
            }
            if (selectedStyleid > 0) {
                toserverData.put("style", selectedStyleid);
            }

            toserverData.put("occasion", selectedOccasionid);
            toserverData.put("sale", selctedSale);
            toserverData.put("size_type", SizeType);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //////System.out.println("objectcheck___" + toserverData);
        ApiService.getInstance(Upload1.this, 1).postData(Upload1.this, EnvConstants.APP_BASE_URL + "/upload/album/", toserverData, SCREEN_NAME, "postfirstdata");

    }


    private void postEditData() {
        mProgress.setMessage("Updating album...");
        mProgress.show();


        selectedTitle = title.getText().toString();
        selectedDesc = description.getText().toString();
        JSONArray size = new JSONArray();
        JSONObject sizeObject = new JSONObject();
        ArrayList<JSONObject> sizeList = new ArrayList<JSONObject>();
        if (CategoryType.equals("FS")) {
            try {
                sizeObject.put("id", "freesize");
                sizeObject.put("qty", 1);
                sizeList.add(sizeObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        size = new JSONArray(sizeList);
        JSONArray imageArray = new JSONArray();
        JSONArray oldimageArray = new JSONArray(EimagesIds);
        //////System.out.println("size:" + size);

//        if (saleSwitch.isChecked()) {
        selctedSale = "2";
//        } else {
//            selctedSale = "1";

//        }
        JSONObject toserverData = new JSONObject();

        if (selectedColorid == 0 || selectedStyleid == 0) {
            //////System.out.println("inside check");
            try {
                toserverData.put("title", selectedTitle);
                toserverData.put("description", selectedDesc);
                toserverData.put("sub_category", selectedCategoryId);
                toserverData.put("brand", selectedBrandid);
                if (CategoryType.equals("FS")) {
                    toserverData.put("size", size);
                } else {
                    toserverData.put("size", sizeitemListToJsonConvert());
                }
                toserverData.put("color", "");
                toserverData.put("occasion", selectedOccasionid);
                toserverData.put("style", "");
                toserverData.put("sale", selctedSale);
                toserverData.put("size_type", SizeType);
                toserverData.put("old_images", oldimageArray);
                toserverData.put("images", imageArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                toserverData.put("title", selectedTitle);
                toserverData.put("description", selectedDesc);
                toserverData.put("sub_category", selectedCategoryId);
                toserverData.put("brand", selectedBrandid);
                if (CategoryType.equals("FS")) {
                    toserverData.put("size", size);
                } else {
                    toserverData.put("size", sizeitemListToJsonConvert());
                }
                toserverData.put("color", selectedColorid);
                toserverData.put("occasion", selectedOccasionid);
                toserverData.put("style", selectedStyleid);
                toserverData.put("sale", selctedSale);
                toserverData.put("size_type", SizeType);
                toserverData.put("old_images", oldimageArray);
                toserverData.put("images", imageArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        ////System.out.println("editDATA:"+toserverData);

        //////System.out.println("objectcheck___" + toserverData);
        if (PTA) {
            ApiService.getInstance(Upload1.this, 1).putData(Upload1.this, EnvConstants.APP_BASE_URL + "/upload/album/" + ProductId + "/1/?action=p_t_a", toserverData, SCREEN_NAME, "posteditdata");

        } else {
            ApiService.getInstance(Upload1.this, 1).putData(Upload1.this, EnvConstants.APP_BASE_URL + "/upload/album/" + ProductId + "/1/", toserverData, SCREEN_NAME, "posteditdata");

        }
    }


    public void postEditImages(){
        long bitmapSize = 0;
        //////System.out.println("inside postedit images");
        //////System.out.println("inside postedit inside: "+Eimages+"__"+Eimages.size());
        //////System.out.println("inside postedit inside cropped_images: "+cropped_images+"__"+cropped_images.size());
        //////System.out.println("inside postedit inside EimagesReplaced: "+EimagesReplaced+"__"+EimagesReplaced.size());
        JSONArray imageArray = new JSONArray();

//
        for (int i = 0; i < Eimages.size(); i++) {
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(Eimages.get(i), bmOptions);
            bitmap = Bitmap.createBitmap(bitmap);
            bitmapSize = bitmapSize + bitmap.getByteCount();
            imageArray.put(encodeTobase64(bitmap));
        }


        //////System.out.println("bitmap size:" + bitmapSize);
        if (CheckMemory.CheckInternalMemory(bitmapSize)) {
            String filepath = "ZAPYLE" + String.valueOf(ProductId);

            for (int i = 0; i < Eimages.size(); i++) {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(Eimages.get(i), bmOptions);
                bitmap = Bitmap.createBitmap(bitmap);
                String content = encodeTobase64(bitmap);

                String fpath = filepath + "img" + String.valueOf(i) + ".txt";
                String fpathuri = filepath + "imguri" + String.valueOf(i) + ".txt";

                try {
                    File myDir = new File(getFilesDir().getAbsolutePath());
                    String s = "";

                    FileWriter fw = new FileWriter(myDir + "/" + fpath);
                    fw.write(content);
                    fw.close();

                    FileWriter fw1 = new FileWriter(myDir + "/" + fpathuri);
                    fw1.write(Eimages.get(i));
                    fw1.close();

                    BufferedReader br = new BufferedReader(new FileReader(myDir + "/" + fpath));
                    s = br.readLine();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();

                }

            }
            ExternalFunctions.AlbumCRUD_Shared(Upload1.this, String.valueOf(ProductId), String.valueOf(imageArray.length()), true);

            mReceiver = new UploadReceiver(new Handler());
            mReceiver.setReceiver(this);
            Intent intent = new Intent(Intent.ACTION_SYNC, null, this, UploadService.class);
            intent.putExtra("receiver", mReceiver);
            intent.putExtra("Filepath", filepath);
            intent.putExtra("FromActivity", true);
            intent.putExtra("ProductId", ProductId);
            intent.putExtra("Add", false);
            intent.putExtra("PTA", PTA);
            intent.putExtra("NumberOfProducts", Eimages.size());
            startService(intent);
            mProgress.dismiss();

        }  else {

            JSONObject ImagetoServer = new JSONObject();
            try {
                ImagetoServer.put("images", imageArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            ApiService.getInstance(Upload1.this, 1).postData(Upload1.this, EnvConstants.APP_BASE_URL + "/upload/album/" + productId + "/", ImagetoServer, SCREEN_NAME, "postEditimagedata");

        }
    }


    public void postImages(int productId) {
        long bitmapSize = 0;

        JSONArray imageArray = new JSONArray();
//
        for (int i = 0; i < cropped_images.size(); i++) {
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(cropped_images.get(i), bmOptions);
            bitmap = Bitmap.createBitmap(bitmap);
            bitmapSize = bitmapSize + bitmap.getByteCount();
            imageArray.put(encodeTobase64(bitmap));
        }


        //////System.out.println("bitmap size:" + bitmapSize);
        if (CheckMemory.CheckInternalMemory(bitmapSize)) {
            String filepath = "ZAPYLE" + String.valueOf(productId);

            for (int i = 0; i < cropped_images.size(); i++) {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(cropped_images.get(i), bmOptions);
                bitmap = Bitmap.createBitmap(bitmap);
                String content = encodeTobase64(bitmap);

                String fpath = filepath + "img" + String.valueOf(i) + ".txt";
                String fpathuri = filepath + "imguri" + String.valueOf(i) + ".txt";

                try {
                    File myDir = new File(getFilesDir().getAbsolutePath());
                    String s = "";

                    FileWriter fw = new FileWriter(myDir + "/" + fpath);
                    fw.write(content);
                    fw.close();

                    FileWriter fw1 = new FileWriter(myDir + "/" + fpathuri);
                    fw1.write(cropped_images.get(i));
                    fw1.close();

                    BufferedReader br = new BufferedReader(new FileReader(myDir + "/" + fpath));
                    s = br.readLine();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();

                }

            }
            ExternalFunctions.AlbumCRUD_Shared(Upload1.this, String.valueOf(productId), String.valueOf(imageArray.length()), true);
            SharedPreferences settings = getSharedPreferences("UploadServiceSession",
                    Context.MODE_PRIVATE);

            //////System.out.println("upload1 shared album values: "+settings.getString("AlbumIdString","")+"___"+settings.getString("AlbumCountString", ""));


            mReceiver = new UploadReceiver(new Handler());
            mReceiver.setReceiver(this);
            Intent intent = new Intent(Intent.ACTION_SYNC, null, this, UploadService.class);
            intent.putExtra("receiver", mReceiver);
            intent.putExtra("Filepath", filepath);
            intent.putExtra("FromActivity", true);
            intent.putExtra("ProductId", productId);
            intent.putExtra("PTA", true);
            intent.putExtra("Add", false);
            intent.putExtra("NumberOfProducts", imageArray.length());
            startService(intent);

            mProgress.dismiss();
        }  else {

            JSONObject ImagetoServer = new JSONObject();
            try {
                ImagetoServer.put("images", imageArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            ApiService.getInstance(Upload1.this, 1).postData(Upload1.this, EnvConstants.APP_BASE_URL + "/upload/album/" + productId + "/", ImagetoServer, SCREEN_NAME, "postimagedata");

        }

    }

    public JSONArray sizeitemListToJsonConvert() {
        String aa;
        JSONObject jResult = new JSONObject();// main object
        JSONArray jArray = new JSONArray();// /ItemDetail jsonArray
        //////System.out.println(SizeguideActivity.selectedqty);

        for (int i = 0; i < SizeguideActivity.selectedqty.size(); i++) {
            JSONObject jsize = new JSONObject();// /sub Object

            try {

                aa = SizeguideActivity.selectedqty.get(i).toString().substring(3, SizeguideActivity.selectedqty.get(i).toString().length());
                ////System.out.println("uuuuuuuuuuuuuuuuuuuuuuuuuu"+aa+"___"+SizeguideActivity.selectedqty.get(i).toString());

                String[] dd = aa.split("-");
                for (int j = 0; j < 2; j++) {
                    jsize.put("id", dd[0]);
                    jsize.put("qty", dd[1]);

                }

                jArray.put(jsize);


                // /itemDetail Name is JsonArray Name
                jResult.put("", jArray);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            ////System.out.println("jjjjjjjjjarray:"+jArray);

        }
        return jArray;
    }

    public void getDataToEdit(int ProductId) {
        progressBar.setVisibility(View.VISIBLE);
        if (PTA) {
            ApiService.getInstance(Upload1.this, 1).getData(Upload1.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/upload/album/" + ProductId + "/1/?action=" + "p_t_a", "getEditdata");
        } else {
            ApiService.getInstance(Upload1.this, 1).getData(Upload1.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/upload/album/" + ProductId + "/1/", "getEditdata");
        }
    }


    //    Other functions
//    ------------------------------------------------------------------
    public static String encodeTobase64(Bitmap image) {
        Bitmap immagex = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        ////Log.e("LOOK", imageEncoded);
        return imageEncoded;
    }


//    Server responses
//    -------------------------------------------------------------------

    @Override
    public void onResponseCallback(JSONObject response, String flag) {
        if (flag.equals("postfirstdata")) {
            //////System.out.println(response);
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        if (EimagesIds.size() > 0) {
                            mProgress.dismiss();
                        }
                        JSONObject data = resp.getJSONObject("data");
                        productId = data.getInt("product_id");
                        postImages(productId);
                        next.setEnabled(true);

                        if (selctedSale.equals("2")) {

                            today = new Date();
                            DateFormat dfclosetime = new SimpleDateFormat("hh:mm:ss aa", Locale.ENGLISH);
                            dfclosetime.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
                            String strclosetime = dfclosetime.format(today);


                            Mixpanelutils mx = new Mixpanelutils();

                            //////System.out.println("mixpaneltimespent" + mx.getTimeDiff(starttime, strclosetime) + "iii" + strclosetime);
                            JSONObject proponbcompleted = new JSONObject();
                            try {
                                proponbcompleted.put("Step", "1");
                                proponbcompleted.put("Time taken", mx.getTimeDiff(starttime, strclosetime));
                                proponbcompleted.put("Product title", title.getText().toString());
                                proponbcompleted.put("Sale", boolsel);
                                proponbcompleted.put("Event Name", "Upload product");
                                mixpanel.track("Upload product", proponbcompleted);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Intent upload2 = new Intent(Upload1.this, Upload2.class);
                            upload2.putExtra("productId", productId);
                            upload2.putExtra("PTA", PTA);
                            startActivity(upload2);
                            finish();
                            //  finish();
                        } else {
                            if (GetSharedValues.getUsertype(Upload1.this).equals("store_front")) {
                                Intent myprofile = new Intent(Upload1.this, ProfilePage.class);
                                myprofile.putExtra("user_id", GetSharedValues.getuserId(this));
                                myprofile.putExtra("p_username",GetSharedValues.getUsername(this));
                                startActivity(myprofile);
                                finish();
                            } else {


                                LayoutInflater layoutInflater = LayoutInflater.from(this);
                                View promptView = layoutInflater.inflate(R.layout.input_dialog_listing_alert, null);
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                                alertDialogBuilder.setView(promptView);
                                TextView touploadClick = (TextView) promptView.findViewById(R.id.touploadClick);
                                TextView toprofile = (TextView) promptView.findViewById(R.id.toprofile);

                                touploadClick.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        finish();
                                        startActivity( getIntent());
//                Intent login = new Intent(context, Upload1.class);
//                context.startActivity(login);
//                ((Activity) context).finish();
                                    }
                                });

                                toprofile.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent login = new Intent(Upload1.this, ProfilePage.class);
                                        login.putExtra("user_id", GetSharedValues.getuserId(Upload1.this));
                                        login.putExtra("p_username",GetSharedValues.getUsername(Upload1.this));
//                                        login.putExtra("zap_user", GetSharedValues.getZapname(Upload1.this));
                                        startActivity(login);
                                        finish();
                                    }
                                });

                                // setup a dialog window
                                alertDialogBuilder.setCancelable(false);
                                // create an alert dialog
                                AlertDialog alert = alertDialogBuilder.create();
                                alert.show();



                            }
                        }
                        next.setEnabled(true);
                    } else {
                        mProgress.dismiss();
                        next.setEnabled(true);
                        Intent mydialog = new Intent(Upload1.this, AlertActivity.class);
                       // int imgid = R.drawable.alertoop;
                        String strmessage = "OOPS!";
                       // mydialog.putExtra("imgid", imgid);
                        mydialog.putExtra("Message", strmessage);
                        mydialog.putExtra("Buttontext", " RETRY ");
                        mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                        mydialog.putExtra("activity", "Upload1");
                        startActivity(mydialog);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    next.setEnabled(true);
                    mProgress.dismiss();
                    Intent mydialog = new Intent(Upload1.this, AlertActivity.class);
                   // int imgid = R.drawable.alertoop;
                    String strmessage = "OOPS!";
                   // mydialog.putExtra("imgid", imgid);
                    mydialog.putExtra("Message", strmessage);
                    mydialog.putExtra("Buttontext", " RETRY ");
                    mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                    mydialog.putExtra("activity", "Upload1");
                    startActivity(mydialog);
                    finish();
                }
            } else {
                mProgress.dismiss();
                next.setEnabled(true);
                Intent mydialog = new Intent(Upload1.this, AlertActivity.class);
               // int imgid = R.drawable.alertoop;
                String strmessage = "OOPS!";
               // mydialog.putExtra("imgid", imgid);
                mydialog.putExtra("Message", strmessage);
                mydialog.putExtra("Buttontext", " RETRY ");
                mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                mydialog.putExtra("activity", "Upload1");
                startActivity(mydialog);
                finish();
            }

        }
        else if (flag.equals("getoverlay")) {
            JSONObject resp = JsonParser.getInstance().parserJsonObject(response);
            System.out.println("overlay"+resp);
            try {
                String status=resp.getString("status");
                if(!status.equals("error")){
                    JSONObject data = resp.getJSONObject("data");
                    final String strimage=data.getString("image");
                    final boolean bl_close=data.getBoolean("can_close");
                    final String strtilte=data.getString("title");
                    final String str_uri=data.getString("uri_target");
                    final String str_description=data.getString("description");
                    final String activityname=data.getString("android_activity");
                    boolean bl_active=data.getBoolean("active");
                    final int intDelay=data.getInt("delay");
                    final boolean bl_fullscreen=data.getBoolean("full_screen");
                    final String strbutton=data.getString("cta_text");
                    ExternalFunctions.strOverlayactivity=activityname;

                    // ExternalFunctions.strOverlayurl="http://test.zapyle.com/catalogue/singleproduct/1236/an/";
                    ExternalFunctions.strOverlayurl= str_uri;
                    if (intDelay > 0) {

                        ExternalFunctions.bloverlay = true;
                        t = new Timer();
                        System.out.println("asasasasa1"+intDelay);
                        t.scheduleAtFixedRate(new TimerTask() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                runOnUiThread(new Runnable() {
                                    public void run() {

                                        TimeCounter=TimeCounter+1;
                                        System.out.println("asasasasa2"+TimeCounter);
                                        if (TimeCounter == intDelay) {

                                            t.cancel();
                                            try {
                                                System.out.println("asasasasa2");
                                                ExternalFunctions.showOverlay(Upload1.this, "", "", strbutton, activityname, bl_fullscreen, bl_close, strimage);
                                                //ExternalFunctions.showOverlay(Upload1.this, strtilte, str_description, strbutton, activityname, bl_fullscreen, bl_close, strimage);
                                            } catch (Exception e) {


                                            }
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


        }
        else if (flag.equals("postimagedata")) {
            //////System.out.println(response);
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        mProgress.dismiss();
                        if (selctedSale.equals("2")) {

                            today = new Date();
                            DateFormat dfclosetime = new SimpleDateFormat("hh:mm:ss aa", Locale.ENGLISH);
                            dfclosetime.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
                            String strclosetime = dfclosetime.format(today);


                            Mixpanelutils mx = new Mixpanelutils();

                            //////System.out.println("mixpaneltimespent" + mx.getTimeDiff(starttime, strclosetime) + "iii" + strclosetime);
                            JSONObject proponbcompleted = new JSONObject();
                            try {
                                proponbcompleted.put("Step", "1");
                                proponbcompleted.put("Time taken", mx.getTimeDiff(starttime, strclosetime));
                                proponbcompleted.put("Product title", title.getText().toString());
                                proponbcompleted.put("Sale", boolsel);
                                proponbcompleted.put("Event Name", "Upload product");
                                mixpanel.track("Upload product", proponbcompleted);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Intent upload2 = new Intent(Upload1.this, Upload2.class);
                            upload2.putExtra("productId", productId);
                            upload2.putExtra("PTA", PTA);
                            startActivity(upload2);
                            finish();
                            //  finish();
                        } else {
                            if (GetSharedValues.getUsertype(Upload1.this).equals("store_front")) {
                                Intent myprofile = new Intent(Upload1.this, ProfilePage.class);
                                myprofile.putExtra("user_id", GetSharedValues.getuserId(this));
                                myprofile.putExtra("p_username",GetSharedValues.getUsername(this));
                                startActivity(myprofile);
                                finish();
                            } else {
                                Alerts.listingAlert(this, GetSharedValues.getZapname(Upload1.this));
                            }
                        }
                        next.setEnabled(true);
                    } else {
                        next.setEnabled(true);
                        mProgress.dismiss();
                        Intent mydialog = new Intent(Upload1.this, AlertActivity.class);
                       // int imgid = R.drawable.alertoop;
                        String strmessage = "OOPS!";
                       // mydialog.putExtra("imgid", imgid);
                        mydialog.putExtra("Message", strmessage);
                        mydialog.putExtra("Buttontext", " RETRY ");
                        mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                        mydialog.putExtra("activity", "Upload1");
                        mydialog.putExtra("calling", "Upload1");
                        startActivity(mydialog);
                        finish();

                    }
                } catch (JSONException e) {
                    next.setEnabled(true);
                    e.printStackTrace();
                    mProgress.dismiss();
                    Intent mydialog = new Intent(Upload1.this, AlertActivity.class);
                   // int imgid = R.drawable.alertoop;
                    String strmessage = "OOPS!";
                   // mydialog.putExtra("imgid", imgid);
                    mydialog.putExtra("Message", strmessage);
                    mydialog.putExtra("Buttontext", " RETRY ");
                    mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                    mydialog.putExtra("activity", "Upload1");
                    mydialog.putExtra("calling", "Upload1");
                    startActivity(mydialog);
                    finish();

                }
            } else {
                next.setEnabled(true);
                mProgress.dismiss();
                Intent mydialog = new Intent(Upload1.this, AlertActivity.class);
               // int imgid = R.drawable.alertoop;
                String strmessage = "OOPS!";
               // mydialog.putExtra("imgid", imgid);
                mydialog.putExtra("Message", strmessage);
                mydialog.putExtra("Buttontext", " RETRY ");
                mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                mydialog.putExtra("activity", "Upload1");
                mydialog.putExtra("calling", "Upload1");
                startActivity(mydialog);
                finish();
            }
        }
        else if (flag.equals("postEditimagedata")) {
            //////System.out.println(response);
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        mProgress.dismiss();
                        if (selctedSale.equals("2")) {

                            today = new Date();
                            DateFormat dfclosetime = new SimpleDateFormat("hh:mm:ss aa", Locale.ENGLISH);
                            dfclosetime.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
                            String strclosetime = dfclosetime.format(today);


                            Mixpanelutils mx = new Mixpanelutils();

                            //////System.out.println("mixpaneltimespent" + mx.getTimeDiff(starttime, strclosetime) + "iii" + strclosetime);
                            JSONObject proponbcompleted = new JSONObject();
                            try {
                                proponbcompleted.put("Step", "1");
                                proponbcompleted.put("Time taken", mx.getTimeDiff(starttime, strclosetime));
                                proponbcompleted.put("Product title", title.getText().toString());
                                proponbcompleted.put("Sale", boolsel);
                                proponbcompleted.put("Event Name", "Upload product");
                                mixpanel.track("Upload product", proponbcompleted);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Intent upload2 = new Intent(Upload1.this, Upload2.class);
                            upload2.putExtra("productId", productId);
                            upload2.putExtra("PTA", PTA);
                            startActivity(upload2);
                            finish();
                            //  finish();
                        } else {
                            if (GetSharedValues.getUsertype(Upload1.this).equals("store_front")) {
                                Intent myprofile = new Intent(Upload1.this, ProfilePage.class);
                                myprofile.putExtra("user_id", GetSharedValues.getuserId(this));
                                myprofile.putExtra("p_username",GetSharedValues.getUsername(this));
                                startActivity(myprofile);
                                finish();
                            } else {
                                Alerts.listingAlert(this, GetSharedValues.getZapname(Upload1.this));
                            }
                        }
                        next.setEnabled(true);
                    } else {
                        next.setEnabled(true);
                        mProgress.dismiss();
                        Intent mydialog = new Intent(Upload1.this, AlertActivity.class);
                       // int imgid = R.drawable.alertoop;
                        String strmessage = "OOPS!";
                       // mydialog.putExtra("imgid", imgid);
                        mydialog.putExtra("Message", strmessage);
                        mydialog.putExtra("Buttontext", " RETRY ");
                        mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                        mydialog.putExtra("activity", "Upload1");
                        mydialog.putExtra("calling", "Upload1");
                        startActivity(mydialog);
                        finish();

                    }
                } catch (JSONException e) {
                    next.setEnabled(true);
                    e.printStackTrace();
                    mProgress.dismiss();
                    Intent mydialog = new Intent(Upload1.this, AlertActivity.class);
                   // int imgid = R.drawable.alertoop;
                    String strmessage = "OOPS!";
                   // mydialog.putExtra("imgid", imgid);
                    mydialog.putExtra("Message", strmessage);
                    mydialog.putExtra("Buttontext", " RETRY ");
                    mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                    mydialog.putExtra("activity", "Upload1");
                    mydialog.putExtra("calling", "Upload1");
                    startActivity(mydialog);
                    finish();

                }
            } else {
                next.setEnabled(true);
                mProgress.dismiss();
                Intent mydialog = new Intent(Upload1.this, AlertActivity.class);
               // int imgid = R.drawable.alertoop;
                String strmessage = "OOPS!";
               // mydialog.putExtra("imgid", imgid);
                mydialog.putExtra("Message", strmessage);
                mydialog.putExtra("Buttontext", " RETRY ");
                mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                mydialog.putExtra("activity", "Upload1");
                mydialog.putExtra("calling", "Upload1");
                startActivity(mydialog);
                finish();
            }
        }






        else if (flag.equals("getEditdata")) {
            //////System.out.println(response);
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        selectedSize.clear();
                        iseditable=true;
                        //////System.out.println(resp);
                        JSONObject data = resp.getJSONObject("data");
                        editObject = data;
                        title.setText(data.getString("title"));
                        description.setText(data.getString("description"));
//                        if (data.getString("sale").equals("2")) {
//                            To_Sale = true;
//                            saleSwitch.setChecked(true);
//                        } else {
//                            To_Sale = true;
//                            saleSwitch.setChecked(false);
//                        }

                        for (int i = 0; i < data.getJSONArray("images").length(); i++) {
                            cropped_images.add(data.getJSONArray("images").getJSONObject(i).getString("url"));
                            EimagesIds.add(String.valueOf(data.getJSONArray("images").getJSONObject(i).getInt("id")));
                        }

                        displayImages(cropped_images.size());
                        display_category.setText(data.getJSONObject("product_category").getString("name").toUpperCase());
                        selectedCategory = data.getJSONObject("product_category").getString("name");
                        selectedCategoryId = data.getJSONObject("product_category").getInt("id");
                        display_brand.setText(data.getJSONObject("brand").getString("name").toUpperCase());
                        selectedBrand = data.getJSONObject("brand").getString("name");
                        selectedBrandid = data.getJSONObject("brand").getInt("id");

                        if (!data.isNull("color")) {
                            display_color.setText(data.getJSONObject("color").getString("name").toUpperCase());
                            selectedColor = data.getJSONObject("color").getString("name");
                            selectedColorid = data.getJSONObject("color").getInt("id");

                        }
                        else {
                            color_edit = true;
                        }


                        if (!data.isNull("style")) {
                            display_style.setText(data.getJSONObject("style").getString("name").toUpperCase());
                            selectedStyle = data.getJSONObject("style").getString("name");
                            selectedStyleid = data.getJSONObject("style").getInt("id");
                        }
                        else {
                            style_edit = true;
                        }


                        display_occasion.setText(data.getJSONObject("occasion").getString("name").toUpperCase());
                        selectedOccasion = data.getJSONObject("occasion").getString("name");
                        selectedOccasionid = data.getJSONObject("occasion").getInt("id");
                        for (int i = 0; i < data.getJSONArray("size").length(); i++) {
                            selectedSize.add(String.valueOf(data.getJSONArray("size").getJSONObject(i).getInt("id")));
                            selectedQty.add(String.valueOf(data.getJSONArray("size").getJSONObject(i).getInt("quantity")));
                            selectedSizeType = data.getJSONArray("size").getJSONObject(i).getString("size_type");
                        }

                        ////System.out.println("selectedsize:"+selectedSize.size()+"__"+selectedQty+"__"+selectedSize+"__"+data.getJSONArray("size")+"__"+data.getJSONArray("size").length());
                        if (data.getJSONArray("size").length() == 1) {
                            SizeguideActivity.selectedqty.clear();
                            if (data.getJSONArray("size").getJSONObject(0).getString("size_type").contains("FREESIZE")) {
                                CategoryType = "FS";
                                display_size.setText("FREESIZE");
                                display_size.setEnabled(false);
                            } else {
//                                //////System.out.println();
                                display_size.setEnabled(true);
                                String type = data.getJSONArray("size").getJSONObject(0).getString("size_type");
                                ////System.out.println(type+"categorytype___" + data.getJSONArray("size").getJSONObject(0).getString("category_type"));
                                CategoryType = data.getJSONArray("size").getJSONObject(0).getString("category_type");
                                if (type.equals("UK")) {
                                    SizeguideActivity.selectedqty.add("UK "+data.getJSONArray("size").getJSONObject(0).getInt("id")+"-"+data.getJSONArray("size").getJSONObject(0).getInt("quantity"));
                                    display_size.setText("UK " + data.getJSONArray("size").getJSONObject(0).getString("uk_size"));
                                } else if (type.equals("US")) {
                                    SizeguideActivity.selectedqty.add("US "+data.getJSONArray("size").getJSONObject(0).getInt("id")+"-"+data.getJSONArray("size").getJSONObject(0).getInt("quantity"));
                                    display_size.setText("US " + data.getJSONArray("size").getJSONObject(0).getString("us_size"));

                                } else if (type.equals("EU")) {
                                    SizeguideActivity.selectedqty.add("EU "+data.getJSONArray("size").getJSONObject(0).getInt("id")+"-"+data.getJSONArray("size").getJSONObject(0).getInt("quantity"));
                                    display_size.setText("EU " + data.getJSONArray("size").getJSONObject(0).getString("eu_size"));
                                }
                                else{
                                    SizeguideActivity.selectedqty.add("UK "+data.getJSONArray("size").getJSONObject(0).getInt("id")+"-"+data.getJSONArray("size").getJSONObject(0).getInt("quantity"));
                                    display_size.setText("UK " + data.getJSONArray("size").getJSONObject(0).getString("uk_size"));
                                }
                            }
                        } else {
                            SizeguideActivity.selectedqty.clear();
                            if (data.getJSONArray("size").getJSONObject(0).getString("size_type").contains("FREESIZE")) {
                                CategoryType = "FS";
                                display_size.setText("FREESIZE");
                                display_size.setEnabled(false);
                            } else {
                                CategoryType = data.getJSONArray("size").getJSONObject(0).getString("category_type");
                            }

                            for (int i=0;i<data.getJSONArray("size").length();i++){
                                String type = data.getJSONArray("size").getJSONObject(i).getString("size_type");
                                if(type.contains("UK")) {
                                    SizeguideActivity.selectedqty.add("UK " + data.getJSONArray("size").getJSONObject(i).getInt("id") + "-" + data.getJSONArray("size").getJSONObject(i).getInt("quantity"));
                                }
                                else if(type.contains("US")) {
                                    SizeguideActivity.selectedqty.add("US " + data.getJSONArray("size").getJSONObject(i).getInt("id") + "-" + data.getJSONArray("size").getJSONObject(i).getInt("quantity"));
                                }
                                else if(type.contains("EU")) {
                                    SizeguideActivity.selectedqty.add("EU " + data.getJSONArray("size").getJSONObject(i).getInt("id") + "-" + data.getJSONArray("size").getJSONObject(i).getInt("quantity"));
                                }
                            }
                            if (data.getJSONArray("size").length() == 2) {
//                                for (int i=0;i<data.getJSONArray("size").length();i++) {
                                String size;
                                String type = data.getJSONArray("size").getJSONObject(0).getString("size_type");
                                if (type.equals("UK")) {
                                    size = "UK " + data.getJSONArray("size").getJSONObject(0).getString("uk_size") + "," + "UK " + data.getJSONArray("size").getJSONObject(1).getString("uk_size");
                                    display_size.setText(size);
                                } else if (type.equals("US")) {
                                    size = "US " + data.getJSONArray("size").getJSONObject(0).getString("us_size") + "," +  "US " + data.getJSONArray("size").getJSONObject(1).getString("us_size");
                                    display_size.setText(size);

                                } else if (type.equals("EU")) {
                                    size = "EU " + data.getJSONArray("size").getJSONObject(0).getString("eu_size") + "," +  "EU " + data.getJSONArray("size").getJSONObject(1).getString("eu_size");
                                    display_size.setText(size);

                                }
//                                }
                            } else if (data.getJSONArray("size").length() > 2) {
                                for (int i=0;i<data.getJSONArray("size").length();i++) {
                                    String type = data.getJSONArray("size").getJSONObject(0).getString("size_type");
                                    if (type.equals("UK")) {
                                        display_size.setText("UK " + data.getJSONArray("size").getJSONObject(0).getString("uk_size") + " ," + "UK " + data.getJSONArray("size").getJSONObject(1).getString("uk_size") + "...");
                                    } else if (type.equals("US")) {
                                        display_size.setText("US " + data.getJSONArray("size").getJSONObject(0).getString("us_size") + " ," + "US " + data.getJSONArray("size").getJSONObject(1).getString("us_size") + "...");

                                    } else if (type.equals("EU")) {
                                        display_size.setText("EU " + data.getJSONArray("size").getJSONObject(0).getString("eu_size") + " ," + "EU " + data.getJSONArray("size").getJSONObject(1).getString("eu_size") + "...");

                                    }
                                }
                            }
                        }
                        progressBar.setVisibility(View.GONE);

                    } else {
                        progressBar.setVisibility(View.GONE);
                        Intent mydialog = new Intent(Upload1.this, AlertActivity.class);
                       // int imgid = R.drawable.alertoop;
                        String strmessage = "OOPS!";
                       // mydialog.putExtra("imgid", imgid);
                        mydialog.putExtra("Message", strmessage);
                        mydialog.putExtra("Buttontext", " RETRY ");
                        mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                        mydialog.putExtra("activity", "Upload1");
                        mydialog.putExtra("calling", "Upload1");
                        startActivity(mydialog);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                    Intent mydialog = new Intent(Upload1.this, AlertActivity.class);
                   // int imgid = R.drawable.alertoop;
                    String strmessage = "OOPS!";
                   // mydialog.putExtra("imgid", imgid);
                    mydialog.putExtra("Message", strmessage);
                    mydialog.putExtra("Buttontext", " RETRY ");
                    mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                    mydialog.putExtra("activity", "Upload1");
                    mydialog.putExtra("calling", "Upload1");
                    startActivity(mydialog);
                    finish();
                }
            } else {
                progressBar.setVisibility(View.GONE);
                Intent mydialog = new Intent(Upload1.this, AlertActivity.class);
               // int imgid = R.drawable.alertoop;
                String strmessage = "OOPS!";
               // mydialog.putExtra("imgid", imgid);
                mydialog.putExtra("Message", strmessage);
                mydialog.putExtra("Buttontext", " RETRY ");
                mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                mydialog.putExtra("activity", "Upload1");
                mydialog.putExtra("calling", "Upload1");
                startActivity(mydialog);
                finish();
            }
        } else if (flag.equals("posteditdata")) {
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                //////System.out.println(resp);
//                progressBar.setVisibility(View.GONE);
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        Eimages.addAll(EimagesReplaced);
                        if(Eimages.size() > 0) {
                            postEditImages();
                        }
                        else {
                            mProgress.dismiss();
                            SharedPreferences settings = getSharedPreferences("UploadServiceSession",
                                    Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("BackgroundUploadStatus", true);
                            editor.apply();
                        }
                        if (selctedSale == "2") {
                            if (PTA) {
                                Intent upload2 = new Intent(Upload1.this, Upload2.class);
                                upload2.putExtra("productId", ProductId);
                                upload2.putExtra("PTA", true);
                                upload2.putExtra("EditStatus", true);
                                startActivity(upload2);
                                finish();
                            } else {
                                Intent upload2 = new Intent(Upload1.this, Upload2.class);
                                upload2.putExtra("productId", ProductId);
                                upload2.putExtra("PTA", false);
                                upload2.putExtra("EditStatus", true);
                                startActivity(upload2);
                                finish();
                            }
                        } else {
                            Intent myprofile = new Intent(Upload1.this, ProfilePage.class);
                            myprofile.putExtra("user_id", GetSharedValues.getuserId(this));
                            myprofile.putExtra("p_username",GetSharedValues.getUsername(this));
                            startActivity(myprofile);
                            finish();
                        }
                    } else {
                        mProgress.dismiss();
                        progressBar.setVisibility(View.GONE);
                        Intent mydialog = new Intent(Upload1.this, AlertActivity.class);
                       // int imgid = R.drawable.alertoop;
                        String strmessage = "OOPS!";
                       // mydialog.putExtra("imgid", imgid);
                        mydialog.putExtra("Message", strmessage);
                        mydialog.putExtra("Buttontext", " RETRY ");
                        mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                        mydialog.putExtra("activity", "Upload1");
                        startActivity(mydialog);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mProgress.dismiss();
                    progressBar.setVisibility(View.GONE);
                    Intent mydialog = new Intent(Upload1.this, AlertActivity.class);
                   // int imgid = R.drawable.alertoop;
                    String strmessage = "OOPS!";
                   // mydialog.putExtra("imgid", imgid);
                    mydialog.putExtra("Message", strmessage);
                    mydialog.putExtra("Buttontext", " RETRY ");
                    mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                    mydialog.putExtra("activity", "Upload1");
                    startActivity(mydialog);
                    finish();
                }

            } else {
                mProgress.dismiss();
                progressBar.setVisibility(View.GONE);
                Intent mydialog = new Intent(Upload1.this, AlertActivity.class);
               // int imgid = R.drawable.alertoop;
                String strmessage = "OOPS!";
               // mydialog.putExtra("imgid", imgid);
                mydialog.putExtra("Message", strmessage);
                mydialog.putExtra("Buttontext", " RETRY ");
                mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                mydialog.putExtra("activity", "Upload1");
                startActivity(mydialog);
                finish();
            }
        }


    }

    @Override
    public void onBackPressed() {
        mProgress.dismiss();
        if (CLICK_COUNT < 1) {

            CustomMessage.getInstance().CustomMessage(this, "Press again to leave the page");
            CLICK_COUNT = CLICK_COUNT + 1;
        } else {
            CLICK_COUNT = 0;
            if (!EditStatus) {
                if (ProfilePage.loadtoupload == 1) {
                    ProfilePage.loadtoupload = 0;
                    Intent profile = new Intent(Upload1.this, ProfilePage.class);
                    profile.putExtra("user_id", GetSharedValues.getuserId(this));
                    profile.putExtra("p_username",GetSharedValues.getUsername(this));
                    profile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(profile);
                    finish();

                } else {
                    Intent dintent = null;
                    try {
                        dintent = new Intent(Upload1.this, Class.forName(callingActivity));
                        dintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        dintent.putExtra("activity", "SplashScreen");
                        startActivity(dintent);
                        finish();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        dintent = new Intent(Upload1.this, HomePageNew.class);
                        dintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(dintent);
                        finish();
                    }

                }
            } else {
                ////System.out.println("OUTSIDE:"+ExternalFunctions.uploadbackcheck);
                if(ExternalFunctions.uploadbackcheck == 0) {
                    Intent styleinspiration = new Intent(Upload1.this, ProductPage.class);
                    styleinspiration.putExtra("album_id", ProductId);
                    styleinspiration.putExtra("sale", To_Sale);
                    styleinspiration.putExtra("pta", PTA);
                    styleinspiration.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(styleinspiration);
                    finish();
                }
                else if(ExternalFunctions.uploadbackcheck == 1){
                    Intent dintent = new Intent(Upload1.this, BuySecondPage.class);
                    dintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    dintent.putExtra("activity", "SplashScreen");
                    startActivity(dintent);
                    finish();
                }
                else {
                    ////System.out.println("OUTSIDE");
                    if (ProfilePage.loadtoupload == 1) {
                        ////System.out.println("OUTSIDE:IN");
                        ProfilePage.loadtoupload = 0;
                        Intent dintent = new Intent(Upload1.this, ProfilePage.class);
                        dintent.putExtra("user_id", GetSharedValues.getuserId(this));
                        dintent.putExtra("p_username",GetSharedValues.getUsername(this));
                        dintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(dintent);
                        finish();
                    }
                    else {
                        Intent dintent = new Intent(Upload1.this, HomePageNew.class);
                        dintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(dintent);
                        finish();
                    }
                }
            }
        }


    }

    @Override
    public void onErrorCallback(VolleyError error, String flag) {
        mProgress.dismiss();
        progressBar.setVisibility(View.GONE);
        Intent mydialog = new Intent(Upload1.this, AlertActivity.class);
       // int imgid = R.drawable.alertoop;
        String strmessage = "OOPS!";
       // mydialog.putExtra("imgid", imgid);
        mydialog.putExtra("Message", strmessage);
        mydialog.putExtra("Buttontext", " RETRY ");
        mydialog.putExtra("tip", "SOMETHING WENT WRONG");
        mydialog.putExtra("activity", "Upload1");
        mydialog.putExtra("calling", "Upload1");
        startActivity(mydialog);
        finish();
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        //////System.out.println("result from service:" + resultData.toString());
        switch (resultCode) {
            case UploadService.STATUS_RUNNING:
                break;
            case UploadService.STATUS_FINISHED:
                //////System.out.println("finisheddddddd");


                break;
            case UploadService.STATUS_ERROR:
                break;
        }

    }

    public class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            //MyApplicationClass.sFirstRun= false;
            Intent intent = new Intent("BuySecondPage");
            intent.putExtra("action", "close");
            intent.putExtra("activityIndex", "ALL");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(!GetSharedValues.GetgcmId(Upload1.this).equals("")) {
            ApiService.getInstance(Upload1.this, 1).getData(Upload1.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/?gcm_reg_id="+GetSharedValues.GetgcmId(Upload1.this), "session");
        }
        else {
            ApiService.getInstance(Upload1.this, 1).getData(Upload1.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/", "session");
        }
    }
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                String wholeID = null;
                wholeID = DocumentsContract.getDocumentId(originalUri);
                String id = wholeID.split(":")[1];
                String[] column = {MediaStore.Images.Media.DATA};
                String sel = MediaStore.Images.Media._ID + "=?";
                Cursor cursor = getContentResolver().
                        query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                column, sel, new String[]{id}, null);
                int columnIndex = cursor.getColumnIndex(column[0]);

                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex);
                }
                filePathUri = Uri.parse(filePath);
                cursor.close();
                doCrop(filePathUri);
            } else {
                // User refused to grant permission.
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        mTracker.set("UserName", GetSharedValues.getUsername(getApplicationContext()));mTracker.set("ZapUsername", GetSharedValues.getZapname(getApplicationContext()));   mTracker.setScreenName("Upload1");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}

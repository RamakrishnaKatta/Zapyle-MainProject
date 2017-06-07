package activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
//import com.uxcam.UXCam;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import adapters.DesignerAdapter;
import adapters.ProfileAdaptor;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.SharingHelper;
import io.branch.referral.util.LinkProperties;
import io.branch.referral.util.ShareSheetStyle;
import models.ProfileData;
import models.ProfileDetails;
import network.ApiCommunication;
import network.ApiService;
import recievers.FeedReciever;
import services.FeedService;
import utils.ExternalFunctions;
import utils.FontUtils;
import utils.GetSharedValues;

/**
 * Created by haseeb on 18/1/16.
 */
public class ProfilePage extends ActionBarActivity implements ApiCommunication, FeedReciever.Receiver {

    private String START = "START", STOP = "STOP";
    String SCREEN_NAME = "PROFILEPAGE";
    public static int loadtoupload = 0;
    public static int intpassactivity = 0;
    RecyclerView recyclerView;
    ProfileAdaptor adaptor;
    String Url, callingActivity = "activity.HomePage";
    FeedReciever mReceiver;
    ProgressBar progress;
    int Pro_id = 0, ALBUM_ID = 0;
    ArrayList<ProfileData> profiledata = new ArrayList<ProfileData>();
    ProfileDetails profileDetails;
    TextView upload, tv_title;
    private Timer t;
    private int TimeCounter = 0;

    int d=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profilepage);
        //UXCam.startWithKey("1dfb25141864376");
        mReceiver = new FeedReciever(new Handler());
        mReceiver.setReceiver(this);

        try {
            Pro_id = getIntent().getIntExtra("user_id", 0);
        } catch (Exception e) {
            Pro_id = GetSharedValues.getuserId(getApplicationContext());
        }

        String strAction = null;
        String strProfile = null;
        try {
            Intent intent = getIntent();
            strAction = intent.getAction();
            Uri data = intent.getData();
            strProfile = data.getPath();
            System.out.println("weblink" + strProfile);
        } catch (Exception e) {

        }


        try {
            callingActivity = "activity." + getIntent().getStringExtra("activity");
            ALBUM_ID = getIntent().getIntExtra("product_id", 0);
            if (getIntent().getStringExtra("ForwardUrl") != null || getIntent().getStringExtra("ForwardUrl").toString().isEmpty()) {
                Url = EnvConstants.APP_BASE_URL + getIntent().getStringExtra("ForwardUrl");
            }


        } catch (Exception e) {
            Url = EnvConstants.APP_BASE_URL + "/user/profile/" + Pro_id + "/an/";
        }

        initialiseHeader();


        ExternalFunctions.bloverlay = false;
        View view = findViewById(R.id.profilePlaceholder);
        FontUtils.setCustomFont(view, getAssets());

        recyclerView = (RecyclerView) findViewById(R.id.profile_recyclerview);
        progress = (ProgressBar) findViewById(R.id.profile_progress);

        upload = (TextView) findViewById(R.id.gototsell);
        System.out.println("PROFILEPAGE CHECK : " + Pro_id + "____" + GetSharedValues.getuserId(this));
        if (Pro_id == GetSharedValues.getuserId(this)) {
            upload.setVisibility(View.VISIBLE);
        } else {
            upload.setVisibility(View.GONE);
        }

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadtoupload = 1;
                Intent upload = new Intent(getApplicationContext(), Upload1.class);
                startActivity(upload);
                finish();

            }
        });


        if (strAction != null) {
            getParamFromUrl(strProfile);
        } else {
            Getdata(Url, 0);
        }


    }

    //    Functions
//    ==========================================
    private void getParamFromUrl(String path) {
        int lastindex, length;
        lastindex = path.lastIndexOf("/");
        length = path.length();
        String strParam = path.substring(lastindex + 1, length);
        Url = EnvConstants.APP_BASE_URL + "/user/profile/" + strParam + "/an/";
        Getdata(Url, 0);

    }

    private void initialiseHeader() {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final View customView = getLayoutInflater().inflate(R.layout.product_bar, null);
        getSupportActionBar().setCustomView(customView);
        Toolbar parent = (Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0, 0);
        tv_title = (TextView) findViewById(R.id.product_title_text);

        ImageView iv_back = (ImageView) findViewById(R.id.productfeedButton);
        iv_back.setVisibility(View.GONE);
//        iv_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });

    }



//    Main get function
//    ============================================

    private void Getdata(String Url, int status) {
        if (status == 0) {
            handleProgress(START);
            Intent service = new Intent(Intent.ACTION_SYNC, null, this, FeedService.class);
            service.putExtra("url", Url);
            service.putExtra("receiver", mReceiver);
            service.putExtra("Method", "Profile");
            service.putExtra("ScreenName", "PROFILEPAGE");
            startService(service);
        } else if (status == 1) {
            Intent service = new Intent(Intent.ACTION_SYNC, null, this, FeedService.class);
            service.putExtra("url", Url);
            service.putExtra("receiver", mReceiver);
            service.putExtra("Method", "Overlay");
            service.putExtra("ScreenName", "FEEDPAGE");
            service.putExtra("PageNo", 1);
            startService(service);
        }
    }


//    Server response
//    =============================================

    @Override
    public void onResponseCallback(JSONObject response, String flag) {

    }

    @Override
    public void onErrorCallback(VolleyError error, String flag) {

    }


//    Recievr response
//    ==========================================


    @Override
    public void onReceiveResult(int resultCode, final Bundle resultData) {
        switch (resultCode) {

            case FeedService.STATUS_RUNNING:
                System.out.println("Serveice running");
                break;

            case FeedService.STATUS_FINISHED:
                System.out.println("PROFILECHECK result: " + resultData);
                String method = resultData.getString("Method");
                switch (method) {
                    case "Profile":
                        System.out.println("CHECK data inside switch");
                        if (GetSharedValues.getuserId(this) == resultData.getInt("id")) {
                            tv_title.setText("My Profile".toUpperCase());
                        } else {
                            tv_title.setText(resultData.getString("Username").toUpperCase());
                        }
                        profileDetails = new ProfileDetails();
                        System.out.println("PROFILECHECK : " + resultData.getString("Designer"));
                        if (resultData.getString("Designer") != null) {
                            System.out.println("PROFILECHECK : insdie if");
                            profileDetails.setAdmirecount(resultData.getInt("admireCount"));
                            profileDetails.setLisingcount(resultData.getInt("listingCount"));
                            profileDetails.setAdmiredBy(resultData.getBoolean("admiredBy"));
                            profileDetails.setProfilepic(resultData.getString("Profilepic"));
//                            profileDetails.setDescription(resultData.getString("Description"));
                            if (resultData.getString("Description") != null || resultData.getString("Description").length() > 0) {
                                profileDetails.setDescription(resultData.getString("Description"));
                            }
                            else {
                                profileDetails.setDescription(null);
                            }
                            profileDetails.setUsertype(resultData.getString("Usertype"));
                            profileDetails.setUsername(resultData.getString("Username"));
                            profileDetails.setUserId(resultData.getInt("id"));
                            profileDetails.setCoverPic(EnvConstants.APP_MEDIA_URL + resultData.getString("Coverpic"));
                            profileDetails.setShortDescription(resultData.getString("ShortDescription"));
                            profiledata = (ArrayList<ProfileData>) resultData.getSerializable("DATALIST");
                            if (profiledata.size() > 1) {
                                if (profiledata.get(1).getAlbumid().equals("0")) {
                                    if (upload.getVisibility() == View.VISIBLE) {
                                        upload.setText("Start selling");
                                    }
                                } else {
                                    if (upload.getVisibility() == View.VISIBLE) {
                                        upload.setText("Upload a new item");
                                    }
                                }
                            }

                            final DesignerAdapter designeradapter = new DesignerAdapter(profiledata, this, profileDetails);
                            recyclerView.setAdapter(designeradapter);
                            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);

//                            recyclerViewHeader.attachTo(recyclerView);
//                            DisplayHeader(1, profileDetails);
                            designeradapter.notifyDataSetChanged();
                            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                                @Override
                                public int getSpanSize(int position) {
                                    return designeradapter.getItemViewType(position) == 0 ? 1 : 2;
                                }
                            }
                            );
                            recyclerView.setLayoutManager(gridLayoutManager);

                        } else {
                            System.out.println("PROFILECHECK : insdie else");
                            profileDetails.setAdmirecount(resultData.getInt("admireCount"));
                            profileDetails.setAdmiringCount(resultData.getInt("admiringCount"));
                            profileDetails.setLisingcount(resultData.getInt("listingCount"));
                            profileDetails.setAdmiredBy(resultData.getBoolean("admiredBy"));
                            profileDetails.setProfilepic(resultData.getString("Profilepic"));
                            profileDetails.setDescription(resultData.getString("Description"));
                            profileDetails.setUsertype(resultData.getString("Usertype"));
                            profileDetails.setUsername(resultData.getString("Username"));
                            profileDetails.setUserId(resultData.getInt("id"));
                            profiledata = (ArrayList<ProfileData>) resultData.getSerializable("DATALIST");
                            if (profiledata.size() > 1) {
                                if (profiledata.get(1).getAlbumid().equals("0")) {
                                    if (upload.getVisibility() == View.VISIBLE) {
                                        upload.setText("Start selling");
                                    }
                                } else {
                                    if (upload.getVisibility() == View.VISIBLE) {
                                        upload.setText("Upload a new item");
                                    }
                                }
                            }

//                            DisplayHeader(0, profileDetails);

                            adaptor = new ProfileAdaptor(profiledata, this, profileDetails);
                            recyclerView.setAdapter(adaptor);
                            recyclerView.setLayoutManager(new LinearLayoutManager(this));
                            adaptor.notifyDataSetChanged();
                           // recyclerViewHeader.attachTo(recyclerView);
                           //DisplayHeader(0, profileDetails);

                        }

                        handleProgress(STOP);
                        if (ExternalFunctions.strOverlayurl.length() == 0) {
                            String overlay_URL = EnvConstants.APP_BASE_URL + "/marketing/overlay/profile";
                            Getdata(overlay_URL, 1);
                        }
                        ExternalFunctions.strOverlayurl = "";
                        break;


                    case "Overlay":
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject resp = new JSONObject(resultData.getString("DATALIST"));
                                    String status = resp.getString("status");
                                    if (!status.equals("error")) {
                                        JSONObject data = resp.getJSONObject("data");
                                        final String strimage = data.getString("image");
                                        final boolean bl_close = data.getBoolean("can_close");
                                        final String strtilte = data.getString("title");
                                        final String str_uri = data.getString("uri_target");
                                        final String str_description = data.getString("description");
                                        String actname = data.getString("android_activity");
                                        final String activityname = "activity." + data.getString("android_activity");
                                        boolean bl_active = data.getBoolean("active");
                                        final int intDelay = data.getInt("delay");
                                        final boolean bl_fullscreen = data.getBoolean("full_screen");
                                        final String strbutton = data.getString("cta_text");
                                        ExternalFunctions.strOverlayactivity = actname;

                                        // ExternalFunctions.strOverlayurl="http://test.zapyle.com/catalogue/singleproduct/1236/an/";
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
                                                                    ExternalFunctions.showOverlay(ProfilePage.this, strtilte, str_description, strbutton, activityname, bl_fullscreen, bl_close, strimage);
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
                            }
                        });
                        break;
                }

        }

    }


    @Override
    protected void onRestart() {
        super.onRestart();
        if (!GetSharedValues.GetgcmId(ProfilePage.this).equals("")) {
            ApiService.getInstance(ProfilePage.this, 1).getData(ProfilePage.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/?gcm_reg_id=" + GetSharedValues.GetgcmId(ProfilePage.this), "session");
        } else {
            ApiService.getInstance(ProfilePage.this, 1).getData(ProfilePage.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/", "session");
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
                        recyclerView.setVisibility(View.INVISIBLE);
                        upload.setVisibility(View.INVISIBLE);
                        progress.setVisibility(View.VISIBLE);
                        break;

                    case "STOP":
                        recyclerView.setVisibility(View.VISIBLE);
                        if (Pro_id == GetSharedValues.getuserId(ProfilePage.this)) {
                            upload.setVisibility(View.VISIBLE);
                        } else {
                            upload.setVisibility(View.GONE);
                        }
                        progress.setVisibility(View.GONE);
                        break;

                    default:
                        break;
                }
            }
        });
    }


//    Activity lifecycle functions
//    =============================================


    @Override
    protected void onDestroy() {
        super.onDestroy();
        int count1 = recyclerView.getChildCount();
        for (int i = 0; i < count1; i++) {
            ImageView v = (ImageView) recyclerView.getChildAt(i).findViewById(R.id.productImage);

            if (v != null) {
                if (v.getDrawable() != null)
                    v.getDrawable().setCallback(null);
                Glide.clear(v);
            }

        }
        ExternalFunctions.FilteredString = "";
        ExternalFunctions.strOverlayurl = "";

    }


    @Override
    protected void onStop() {
        super.onStop();
        int count1 = recyclerView.getChildCount();
        for (int i = 0; i < count1; i++) {
            ImageView v = (ImageView) recyclerView.getChildAt(i).findViewById(R.id.productImage);

            if (v != null) {
                if (v.getDrawable() != null)
                    v.getDrawable().setCallback(null);
                Glide.clear(v);
            }

        }

        ExternalFunctions.FilteredString = "";
        ExternalFunctions.strOverlayurl = "";
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mydialog = null;
        try {
            if (callingActivity.equals("BuySecondPage")) {
                mydialog = new Intent(ProfilePage.this, BuySecondPage.class);
                mydialog.putExtra("activity", "SplashScreen");
                startActivity(mydialog);
                finish();
            } else if (callingActivity.contains("ProductPage")) {
                Intent productPage = new Intent(ProfilePage.this, ProductPage.class);
                productPage.putExtra("pta", false);
                productPage.putExtra("album_id", ALBUM_ID);
                productPage.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(productPage);
                finish();
            } else if (callingActivity.contains("CommentActivity")) {
                Intent comment = new Intent(ProfilePage.this, CommentActivity.class);
                comment.putExtra("album_id", String.valueOf(ALBUM_ID));
                comment.putExtra("FeedStatus", true);
//                comment.putExtra("product_title", feed.getProductName());
                startActivity(comment);
                finish();
            } else if (callingActivity.contains("searchFeedPage")) {
                Intent feed = new Intent(this, searchFeedPage.class);
                feed.putExtra("activity", "SplashScreen");
                startActivity(feed);
            } else if (callingActivity.contains("searchnew")) {

                finish();
            } else if (callingActivity.contains("LikersActivity")) {
                finish();
            } else {
                mydialog = new Intent(ProfilePage.this, Class.forName(callingActivity));
                mydialog.putExtra("activity", "SplashScreen");
                startActivity(mydialog);
                finish();
            }
        } catch (Exception e) {
            try {
                mydialog = new Intent(ProfilePage.this, Class.forName(callingActivity));
                mydialog.putExtra("activity", "SplashScreen");
                startActivity(mydialog);
                finish();
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
                mydialog = new Intent(ProfilePage.this, HomePageNew.class);
                mydialog.putExtra("activity", "SplashScreen");
                startActivity(mydialog);
                finish();
            }

        }
    }

    public static void showUploadDialog(Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Almost There.");

        // set dialog message
        alertDialogBuilder
                .setMessage("The listing is being uploaded now. Please wait for a few minutes to preview it.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }


    public void Share() {
        BranchUniversalObject branchUniversalObject = null;


        branchUniversalObject = new BranchUniversalObject()
                .setCanonicalIdentifier("item/12345")
                .setTitle(String.valueOf(profileDetails.getUsername())+"'s Closet")
                .setContentDescription("")
                .setContentImageUrl(EnvConstants.APP_MEDIA_URL + profileDetails.getProfilepic())
                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .addContentMetadata("prof_id", String.valueOf(profileDetails.getUserId()))
                .addContentMetadata("social", "false")
                .addContentMetadata("user", "000");


        LinkProperties linkProperties = new LinkProperties()
                .setChannel("facebook")
                .setFeature("sharing")
                .addControlParameter("$desktop_url", "http://zapyle.com/#/user/profile/" + String.valueOf(profileDetails.getUserId()))
                .addControlParameter("$ios_url", "http://zapyle.com/#/user/profile/" + String.valueOf(profileDetails.getUserId()));
        // .addControlParameter("$zapyle", "item/12345");


        ShareSheetStyle shareSheetStyle = new ShareSheetStyle(ProfilePage.this, "zapyle", "You'll love the stuff in my closet. Check it out!" + profileDetails.getUsername())
                //  .setCopyUrlStyle(R.id., copyUrlMessage, copiedUrlMessage)
                // .setMoreOptionStyle(getResources().getDrawable(android.R.drawable.ic_menu_search), "More options")
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.FACEBOOK)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.EMAIL)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.MESSAGE)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.TWITTER);

        branchUniversalObject.showShareSheet(ProfilePage.this, linkProperties, shareSheetStyle, null);
    }
}
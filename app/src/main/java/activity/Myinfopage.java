package activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;
//import com.//Appsee.//Appsee;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.exceptions.CleverTapMetaDataNotFoundException;
import com.clevertap.android.sdk.exceptions.CleverTapPermissionsNotSatisfied;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import com.naver.android.helloyako.imagecrop.util.BitmapLoadUtils;
//import com.uxcam.UXCam;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import application.MyApplicationClass;
import network.ApiCommunication;
import network.ApiService;
import network.JsonParser;
import utils.CustomMessage;
import utils.ExternalFunctions;
import utils.FontUtils;
import utils.GetSharedValues;


public class Myinfopage extends AppCompatActivity implements ApiCommunication {
    String SCREEN_NAME = "MY_INFO";
    private int pYear;
    private int pMonth;
    private int pDay;
    static final int DATE_DIALOG_ID = 0;
    LinearLayout l1male, l1female, l1loc;
    LinearLayout l1location;
    ImageView imgmale, imgfemale;
    TextView tvdob, tvzap, tvname, tvemail, tvphone, tvloc, tvdesc, tvnext, tvcancel, image_loading;
    EditText edtdesc, edtname;
    private static final int REQUEST_PERMISSION = 1;
    String[] values;
    Integer[] ids;
    JSONArray array_locations;
    RelativeLayout rlbottom;
    int intcheckid;
    JSONObject data;
    Context ctx;
    ScrollView sv;
    ProgressDialog mProgress;
    boolean booltype;
    Spinner sploc;
    String[] loc;
    int inta = 0;
    Date today;
    String dateofb, locat, gendr, gendrnew;
    String valuechanged = "";
    private Timer t;
    private int TimeCounter = 0;
    Tracker mTracker;
    ImageView iv_profilepic;

    private static final int GALLERY_INTENT_CALLED_PROFILE = 1;
    private static final int GALLERY_KITKAT_INTENT_CALLED_PROFILE = 2;
    String stime = "";
    String endtime = "";
    CleverTapAPI cleverTap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myinfopage);
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
        //UXCam.startWithKey("1dfb25141864376");
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        //////Thread.currentThread().setDefaultUncaughtExceptionHandler(new SplashScreen.MyUncaughtExceptionHandler());
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        View customView = getLayoutInflater().inflate(R.layout.profile_actionbar, null);
        getSupportActionBar().setCustomView(customView);
        Toolbar parent = (Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0, 0);
        TextView tvbartitle = (TextView) findViewById(R.id.product_title_text);
        ImageView imgback = (ImageView) findViewById(R.id.profilebackButton);
        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        l1male = (LinearLayout) findViewById(R.id.l1male);
        l1female = (LinearLayout) findViewById(R.id.l1female);
        l1location = (LinearLayout) findViewById(R.id.l1location);
        l1loc = (LinearLayout) findViewById(R.id.l1lc);
        rlbottom = (RelativeLayout) findViewById(R.id.bottomLayout3);
        sv = (ScrollView) findViewById(R.id.scrv);
        sv.setVisibility(View.INVISIBLE);
        sploc = (Spinner) findViewById(R.id.sploc);
        tvbartitle.setText("MY INFO");
        tvdob = (TextView) findViewById(R.id.tvdob);
        tvzap = (TextView) findViewById(R.id.tvuser);
        tvname = (TextView) findViewById(R.id.tvname);
        tvemail = (TextView) findViewById(R.id.tvemail);
        tvphone = (TextView) findViewById(R.id.tvphone);
        tvdesc = (TextView) findViewById(R.id.tvdesc);
        tvnext = (TextView) findViewById(R.id.next);
        tvcancel = (TextView) findViewById(R.id.cancel);
        edtdesc = (EditText) findViewById(R.id.etdesc);
        edtname = (EditText) findViewById(R.id.etname);
        View view = findViewById(R.id.rl);


        Bundle bundle = this.getIntent().getExtras();
        booltype = bundle.getBoolean("booltype");

        ExternalFunctions.bloverlay = false;
        FontUtils.setCustomFont(view, getAssets());

        Getinfo();
        final Calendar cal = Calendar.getInstance();
        pYear = cal.get(Calendar.YEAR);
        pMonth = cal.get(Calendar.MONTH);
        pDay = cal.get(Calendar.DAY_OF_MONTH);
        imgmale = (ImageView) findViewById(R.id.imgmale);
        imgfemale = (ImageView) findViewById(R.id.imgfemale);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        tvname.setVisibility(View.GONE);
        edtname.setVisibility(View.VISIBLE);
        tvdesc.setVisibility(View.GONE);
        edtdesc.setVisibility(View.VISIBLE);
        rlbottom.setVisibility(View.VISIBLE);
        image_loading = (TextView) findViewById(R.id.image_loading);
        //image_loading.bringToFront();
        iv_profilepic = (ImageView) findViewById(R.id.myprofile_image);
        //iv_profilepic.bringToFront();
        edtname.requestFocus();
        l1loc.removeAllViews();
        ctx = this;
        mProgress = new ProgressDialog(Myinfopage.this);
        mProgress.setCancelable(false);
        mProgress.setMessage("Loading ...");
        mProgress.show();
        l1male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gendrnew = "M";
                imgmale.setImageResource(R.drawable.background_rectangle);
                imgfemale.setImageResource(0);

            }
        });

        l1female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gendrnew = "F";

                imgfemale.setImageResource(R.drawable.background_rectangle);


                imgmale.setImageResource(0);


            }
        });
        tvnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edtname.getText().length() < 2) {
                    CustomMessage.getInstance().CustomMessage(Myinfopage.this, "Please add your name");
                } else {
                    tvnext.setEnabled(false);
                    InfToServer();
                }

            }
        });
        tvcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvnext.setEnabled(true);
                if (!data.isNull("full_name")) {
                    try {
                        edtname.setText(data.getString("full_name"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (!data.isNull("phone_number")) {
                    try {
                        tvphone.setText(data.getString("phone_number"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (!data.isNull("description")) {
                    try {
                        edtdesc.setText(data.getString("description"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        tvdesc.setText(data.getString("description"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    edtdesc.setText("");
                    tvdesc.setText("");
                }

                if (!data.isNull("dob")) {
                    try {
                        tvdob.setText(data.getString("dob"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        tvdob.setText(data.getString("dob"));
                        if (tvdob.getText().toString().contains("null")) {
                            tvdob.setText("Add your date of birth here");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (!data.isNull("gender")) {
                    try {
                        if (data.getString("gender").equals("Male")) {
                            imgmale.setImageResource(R.drawable.background_rectangle);
                            imgfemale.setImageResource(0);
                        } else {
                            imgfemale.setImageResource(R.drawable.background_rectangle);
                            imgmale.setImageResource(0);


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    imgfemale.setImageResource(R.drawable.background_rectangle);
                    imgmale.setImageResource(0);
                }
                sploc.setSelection(inta);

            }
        });


        sploc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                try {
                    intcheckid = array_locations.getJSONObject(position).getInt("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


        /** Display the current date in the TextView */
        // updateDisplay();
        tvdob.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                createdDialog(DATE_DIALOG_ID).show();

            }
        });


    }

    private void GetOverlay() {

        ApiService.getInstance(Myinfopage.this, 1).getData(Myinfopage.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/marketing/overlay/my_info", "getoverlay");
    }

    private void Getinfo() {

        ApiService.getInstance(Myinfopage.this, 1).getData(Myinfopage.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/myinfo/", "getinfo");


    }

    private DatePickerDialog.OnDateSetListener pDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {

                    pYear = year;
                    pMonth = monthOfYear;
                    pDay = dayOfMonth;
                    updateDisplay();
                }
            };

    /**
     * Updates the date in the TextView
     */
    private void updateDisplay() {
        String dd, mm;
        dd = String.valueOf(pDay);
        mm = String.valueOf(pMonth + 1);
        if (pDay < 10) {
            dd = "0" + String.valueOf(pDay);
        }
        if (pMonth < 10) {
            mm = "0" + String.valueOf(pMonth + 1);
        }

        if (pYear > Calendar.getInstance().get(Calendar.YEAR)) {
            tvdob.setText("Please select valid date");
        } else {
            tvdob.setText(

                    new StringBuilder()
                            .append(dd).append("-")
                            .append(mm).append("-")
                            .append(pYear));
        }

    }


    private void sendScroll() {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        sv.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        }).start();
    }

    @Override
    public void onResponseCallback(JSONObject response, String flag) {


        //////System.out.println("response ___" + response);
        if (flag.equals("datatoserver")) {
            // System.out.println("response_____" + response);
            JSONObject resp = JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {

                        if (!dateofb.equals(tvdob.getText().toString())) {
                            valuechanged = valuechanged + tvdob.getText().toString();
                        }
                        if (!locat.equals(sploc.getSelectedItem().toString())) {
                            valuechanged = valuechanged + sploc.getSelectedItem().toString();
                        }
                        if (!gendr.equals(gendrnew)) {
                            valuechanged = valuechanged + gendrnew;
                        }
                        if (valuechanged.length() > 1) {

                        }
                        //CustomMessage.getInstance().CustomMessage(Myinfopage.this, "Updated successfully");
                        Intent profile = new Intent(Myinfopage.this, activity.profile.class);
                        profile.putExtra("user_id", GetSharedValues.getuserId(this));
                        profile.putExtra("p_username", GetSharedValues.getUsername(this));
                        startActivity(profile);
                        finish();


                        tvnext.setEnabled(true);
                    } else {
                        tvnext.setEnabled(true);
                        Intent mydialog = new Intent(Myinfopage.this, AlertActivity.class);
                        // int imgid = R.drawable.alertoop;
                        String strmessage = "OOPS!";
                        // mydialog.putExtra("imgid", imgid);
                        mydialog.putExtra("Message", strmessage);
                        mydialog.putExtra("Buttontext", " RETRY ");
                        mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                        mydialog.putExtra("activity", "Myinfopage");
                        startActivity(mydialog);
                        finish();
                        //CustomMessage.getInstance().CustomMessage(Myinfopage.this, "Oops. Something went wrong!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    tvnext.setEnabled(true);
                }
            }
        } else if (flag.equals("getoverlay")) {
            JSONObject resp = JsonParser.getInstance().parserJsonObject(response);
            //System.out.println("overlay"+resp);
            try {
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
                                                ExternalFunctions.showOverlay(Myinfopage.this, strtilte, str_description, strbutton, activityname, bl_fullscreen, bl_close, strimage,str_uri);

                                            } catch (Exception e) {

                                            }
                                        }


                                    }
                                });

                            }
                        }, 500, 500);
                    }
                    else{
                        ExternalFunctions.showOverlay(Myinfopage.this, strtilte, str_description, strbutton, activityname, bl_fullscreen, bl_close, strimage,str_uri);
                    }


                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        } else if (flag.equals("postimagedata")) {
            JSONObject resp = JsonParser.getInstance().parserJsonObject(response);
            ////System.out.println(resp);

            if (resp != null) {
                ////System.out.println(resp);
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        image_loading.setText("Change");
//                        image_loading.setVisibility(View.GONE);
                        iv_profilepic.setEnabled(true);
                    } else {
                        iv_profilepic.setEnabled(true);
                        image_loading.setText("Change");
                    }
                } catch (JSONException e) {
                    iv_profilepic.setEnabled(true);
                    e.printStackTrace();
                    //image_loading.setVisibility(View.GONE);
                }
            } else {
                //image_loading.setVisibility(View.GONE);
            }
        } else if (flag.equals("getinfo")) {
            System.out.println("response_____" + response);
            JSONObject resp = JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                try {

                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        data = resp.getJSONObject("data");
                        array_locations = data.getJSONArray("locations");
                        values = new String[array_locations.length()];
                        ids = new Integer[array_locations.length()];


                        tvzap.setText(data.getString("zap_username"));

                        if (!data.isNull("full_name")) {
                            tvname.setText(data.getString("full_name"));
                        }

                        if (!data.isNull("email")) {
                            tvemail.setText(data.getString("email"));
                        }
                        if (!data.isNull("full_name")) {
                            edtname.setText(data.getString("full_name"));
                        }

                        if (!data.isNull("phone_number")) {
                            tvphone.setText(data.getString("phone_number"));
                        }

                        if (!data.isNull("description")) {
                            edtdesc.setText(data.getString("description"));
                            tvdesc.setText(data.getString("description"));
                        } else {
                            edtdesc.setText(" ");
                            tvdesc.setText(" ");
                        }

                        if (!data.isNull("dob")) {

                            tvdob.setText(data.getString("dob"));

                        } else {
                            tvdob.setText(data.getString("dob"));
                            if (tvdob.getText().toString().length() <= 4) {
                                tvdob.setText("Add your date of birth here");
                            }
                        }
                        dateofb = tvdob.getText().toString();
                        if (!data.isNull("gender")) {
                            if (data.getString("gender").equals("Male")) {
                                imgmale.setImageResource(R.drawable.background_rectangle);
                                imgfemale.setImageResource(0);
                                gendr = "M";
                            } else {
                                imgfemale.setImageResource(R.drawable.background_rectangle);
                                imgmale.setImageResource(0);
                                gendr = "F";

                            }
                        } else {
                            imgfemale.setImageResource(R.drawable.background_rectangle);
                            imgmale.setImageResource(0);
                            gendr = "female";
                        }

                        loc = new String[array_locations.length()];

                        for (int i = 0; i < array_locations.length(); i++) {
                            values[i] = array_locations.getJSONObject(i).getString("name");
                            ids[i] = array_locations.getJSONObject(i).getInt("id");
                            loc[i] = array_locations.getJSONObject(i).getString("name");
                            if (!data.isNull("selected_location_id")) {

                                if (array_locations.getJSONObject(i).getInt("id") == data.getInt("selected_location_id")) {

                                    intcheckid = data.getInt("selected_location_id");

                                    inta = i;
                                }
                            }

                        }

//                        Glide.with(Myinfopage.this)
//                                .load(data.getString("profile_pic"))
//                                .fitCenter()
//                                .placeholder(R.drawable.prof_placeholder)
//                                .crossFade()
//                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                                .into(iv_profilepic);
//

                        Glide.with(this)
                                .load(data.getString("profile_pic"))
                                .asBitmap()
                                .placeholder(R.drawable.prof_placeholder)
                                .error(R.drawable.prof_placeholder)
                                .centerCrop()
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .into(new BitmapImageViewTarget(iv_profilepic) {
                                    @Override
                                    protected void setResource(Bitmap resource) {
                                        RoundedBitmapDrawable circularBitmapDrawable =
                                                RoundedBitmapDrawableFactory.create(getResources(), resource);
                                        circularBitmapDrawable.setCircular(true);
                                        iv_profilepic.setImageDrawable(circularBitmapDrawable);
                                    }
                                });

                        iv_profilepic.setScaleType(ImageView.ScaleType.CENTER_CROP);


                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                                R.layout.simple_spinner_item_filter, loc);
                        sploc.setAdapter(adapter);
                        locat = sploc.getSelectedItem().toString();

                        sploc.setSelection(inta);
                        mProgress.dismiss();
                        sv.setVisibility(View.VISIBLE);


                    } else {
                        Intent mydialog = new Intent(Myinfopage.this, AlertActivity.class);
                        // int imgid = R.drawable.alertoop;
                        String strmessage = "OOPS!";
                        // mydialog.putExtra("imgid", imgid);
                        mydialog.putExtra("Message", strmessage);
                        mydialog.putExtra("Buttontext", " RETRY ");
                        mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                        mydialog.putExtra("activity", "Myinfopage");
                        startActivity(mydialog);
                        finish();
                        //CustomMessage  CustomMessage.getInstance().CustomMessage(Myinfopage.this, "Oops. Something went wrong!");
                    }


                } catch (JSONException e) {
                    e.printStackTrace();

                }

            }


        }

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
    }

    @Override
    public void onBackPressed() {
        Intent mypref = new Intent(Myinfopage.this, profile.class);
        mypref.putExtra("user_id", GetSharedValues.getuserId(this));
        mypref.putExtra("p_username", GetSharedValues.getUsername(this));
        startActivity(mypref);
        finish();
    }

    public void ProfileImageUpload(View v) {
        iv_profilepic.setEnabled(false);
        iv_profilepic.setImageBitmap(null);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent();
            intent.setType("image/jpeg");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, getString(R.string.ChoosePicture)), GALLERY_INTENT_CALLED_PROFILE);
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/jpeg");
            startActivityForResult(intent, GALLERY_KITKAT_INTENT_CALLED_PROFILE);
        }
    }

    private void InfToServer() {


        JSONObject data = new JSONObject();


        try {

            HashMap<String, Object> profileUpdate = new HashMap<String, Object>();
            profileUpdate.put("zapyle_fullname", edtname.getText().toString());// Disable SMS notifications

            data.put("full_name", edtname.getText().toString());
            if (edtdesc.getText().length() > 2) {
                data.put("description", edtdesc.getText().toString());
            }
            if (tvdob.getText().toString().contains("-")) {
                data.put("dob", tvdob.getText().toString());
                profileUpdate.put("zapyle_dob", tvdob.getText().toString());
            }
            cleverTap.profile.push(profileUpdate);
            data.put("gender", gendrnew);
            data.put("selected_location_id", intcheckid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiService.getInstance(Myinfopage.this, 1).putData(Myinfopage.this, EnvConstants.APP_BASE_URL + "/user/myinfo/", data, SCREEN_NAME, "datatoserver");

    }

    protected Dialog createdDialog(int id) {

        switch (id) {
            case DATE_DIALOG_ID:
                DatePickerDialog dd = new DatePickerDialog(this,
                        pDateSetListener,
                        pYear, pMonth, pDay);
                dd.setCancelable(false);
                return dd;
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri originalUri = null;
        String filePath = null;
        Uri filePathUri = null;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {


                case GALLERY_KITKAT_INTENT_CALLED_PROFILE:
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
                        } else {
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
                            Bitmap bitmap2 = BitmapFactory.decodeFile(filePath);
                            if (bitmap2.getHeight() < 300) {
                                bitmap2 = Bitmap.createScaledBitmap(bitmap2, bitmap2.getWidth(), bitmap2.getHeight(), true);
                            } else {
                                bitmap2 = Bitmap.createScaledBitmap(bitmap2, 300, 300, true);
                            }
                            String pro_image2 = upload.encodeTobase64(ExternalFunctions.rotateBitmap(filePath, bitmap2));
                            iv_profilepic.setImageBitmap(ExternalFunctions.rotateBitmap(filePath, bitmap2));
                            iv_profilepic.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            postProfilePicData(pro_image2);
                        }
//
//
//                        Cursor cursor = getContentResolver().
//                                query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                                        column, sel, new String[]{id}, null);
//
//                        int columnIndex = cursor.getColumnIndex(column[0]);
//
//                        if (cursor.moveToFirst()) {
//                            filePath = cursor.getString(columnIndex);
//                        }
//                        filePathUri = Uri.parse(filePath);
//                        cursor.close();
//
//                        Bitmap bitmap2 = BitmapFactory.decodeFile(filePath);
//                        if (bitmap2.getHeight() < 300) {
//                            bitmap2 = Bitmap.createScaledBitmap(bitmap2, bitmap2.getWidth(), bitmap2.getHeight(), true);
//                        } else {
//                            bitmap2 = Bitmap.createScaledBitmap(bitmap2, 300, 300, true);
//                        }
//                        String pro_image2 = upload.encodeTobase64(ExternalFunctions.rotateBitmap(filePath, bitmap2));
//                        iv_profilepic.setImageBitmap(ExternalFunctions.rotateBitmap(filePath, bitmap2));
//                        iv_profilepic.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                        postProfilePicData(pro_image2);


                        break;


                    }


                case GALLERY_INTENT_CALLED_PROFILE:
//                    originalUri = data.getData();
//                    filePath = BitmapLoadUtils.getPathFromUri(this, originalUri);
//                    filePathUri = Uri.parse(filePath);

                    originalUri = data.getData();
                    filePath = BitmapLoadUtils.getPathFromUri(this, originalUri);
                    filePathUri = Uri.parse(filePath);

                    Bitmap bitmap2 = BitmapFactory.decodeFile(filePath);
                    if (bitmap2.getHeight() < 300) {
                        bitmap2 = Bitmap.createScaledBitmap(bitmap2, bitmap2.getWidth(), bitmap2.getHeight(), true);
                    } else {
                        bitmap2 = Bitmap.createScaledBitmap(bitmap2, 300, 300, true);
                    }
                    String pro_image2 = upload.encodeTobase64(ExternalFunctions.rotateBitmap(filePath, bitmap2));
                    iv_profilepic.setImageBitmap(ExternalFunctions.rotateBitmap(filePath, bitmap2));
                    iv_profilepic.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    postProfilePicData(pro_image2);
                    break;

            }
        }

    }


    private void postProfilePicData(String imageArray) {
//        image_loading.setVisibility(View.VISIBLE);
        image_loading.setText("Uploading..");
        JSONObject ImagetoServer = new JSONObject();
        try {
            ImagetoServer.put("pro_pic", imageArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiService.getInstance(Myinfopage.this, 1).postData(Myinfopage.this, EnvConstants.APP_BASE_URL + "/upload/pro_pic/", ImagetoServer, SCREEN_NAME, "postimagedata");
    }


    @Override
    public void onErrorCallback(VolleyError error, String flag) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!GetSharedValues.GetgcmId(Myinfopage.this).equals("")) {
            ApiService.getInstance(Myinfopage.this, 1).getData(Myinfopage.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/?gcm_reg_id=" + GetSharedValues.GetgcmId(Myinfopage.this), "session");
        } else {
            ApiService.getInstance(Myinfopage.this, 1).getData(Myinfopage.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/", "session");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.set("UserName", GetSharedValues.getUsername(getApplicationContext()));
        mTracker.set("ZapUsername", GetSharedValues.getZapname(getApplicationContext()));
        mTracker.setScreenName(GetSharedValues.getZapname(getApplicationContext()) + "~" + "My infopage");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}

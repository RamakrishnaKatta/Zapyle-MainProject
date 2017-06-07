package activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.exceptions.CleverTapMetaDataNotFoundException;
import com.clevertap.android.sdk.exceptions.CleverTapPermissionsNotSatisfied;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
//import com.uxcam.UXCam;
import com.zapyle.zapyle.Alerts;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;

import adapters.searchpageadapter;
import application.MyApplicationClass;
import network.ApiCommunication;
import network.ApiService;
import utils.ExternalFunctions;
import utils.GetSharedValues;

public class searchnew extends AppCompatActivity implements ApiCommunication {

    public static int width;
    public ViewPager mViewPager;
    public String SCREEN_NAME = "SEARCH";
    public static ViewPager viewPager;
    public static TabLayout tabLayout;
    ImageView imgclose, imgserach;
    // public static  TextView tvsug;
    public static EditText edtsearch;
    searchpageadapter adapter;
    public static final int RESULT_OK = -1;
    String a;


    public static JSONObject objMainList = new JSONObject();
    boolean bltextchanged = false;
    int tmptab;
    int CLICK_COUNT = 0;
    Tracker mTracker;

    public FragmentRefreshListener getFragmentRefreshListener() {


        return fragmentRefreshListener;
    }

    public FragmentRefreshListener1 getFragmentRefreshListener1() {


        return fragmentRefreshListener1;
    }

    public void setFragmentRefreshListener(FragmentRefreshListener fragmentRefreshListener) {
        this.fragmentRefreshListener = fragmentRefreshListener;
    }

    private FragmentRefreshListener fragmentRefreshListener;

    public void setFragmentRefreshListener1(FragmentRefreshListener1 fragmentRefreshListener) {
        this.fragmentRefreshListener1 = fragmentRefreshListener;
    }

    private FragmentRefreshListener1 fragmentRefreshListener1;
    String callingActivity = "activity.HomePage";
    String stime = "";
    String endtime = "";
    CleverTapAPI cleverTap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        MyApplicationClass application = (MyApplicationClass) getApplication();
        mTracker = application.getDefaultTracker();

        try {
            callingActivity = "activity." + getIntent().getStringExtra("activity");
        } catch (Exception e) {
            callingActivity = "activity.HomePage";
        }

        stime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        try {
            cleverTap = CleverTapAPI.getInstance(getApplicationContext());
        } catch (CleverTapMetaDataNotFoundException e) {
            // thrown if you haven't specified your CleverTap Account ID or Token in your AndroidManifest.xml
        } catch (CleverTapPermissionsNotSatisfied e) {
            // thrown if you haven’t requested the required permissions in your AndroidManifest.xml
        }
        //UXCam.startWithKey("1dfb25141864376");
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        ExternalFunctions.jsonObjsearch = new JSONObject();
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        ExternalFunctions.bloverlay = false;
        edtsearch = (EditText) findViewById(R.id.edtsearch);
        imgclose = (ImageView) findViewById(R.id.imgclose);
        imgserach = (ImageView) findViewById(R.id.imgsearch);
        // tvsug=(TextView)findViewById(R.id.tvsug);
        viewPager = (ViewPager) findViewById(R.id.pager);

        adapter = new searchpageadapter
                (getSupportFragmentManager(), 2, getApplicationContext());
        viewPager.setAdapter(adapter);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setBackgroundColor(Color.WHITE);

        try {
            //prepare item array for "A"
            JSONArray arrForcat = new JSONArray();

            JSONArray arrForsubcat = new JSONArray();

            JSONArray arrForstyle = new JSONArray();

            JSONArray arrForoccasion = new JSONArray();

            JSONArray arrForcolor = new JSONArray();

            JSONArray arrForbranch = new JSONArray();
//            //Finally add item arrays for "A" and "B" to main list with key
            objMainList.put("Category", arrForcat);
            objMainList.put("Style", arrForstyle);
            objMainList.put("SubCategory", arrForsubcat);
            objMainList.put("Occasion", arrForoccasion);
            objMainList.put("Color", arrForcolor);
            objMainList.put("Brand", arrForbranch);
            objMainList.put("string", "");


            ExternalFunctions.jsonObjsearch.put("filter", objMainList);
            edtsearch.setSelection(edtsearch.getText().length());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final Handler mHandler = new Handler();
        edtsearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {


            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                tmptab = viewPager.getCurrentItem();
                //System.out.println("zvzvzvzv  :" + tmptab);
            }

            @Override
            public void afterTextChanged(Editable s) {
                bltextchanged = true;


                if (edtsearch.getText().toString().isEmpty()) {
                    ExternalFunctions.selsugest = "";
                    // tvsug.setText("");
                } else {
                    try {
                        String[] str = edtsearch.getText().toString().split(" ");
                        String[] str1 = ExternalFunctions.selsugest.split(" ");
                        //System.out.println("koi"+str.length+""+str1.length);
                        if (str.length > 1 && str1.length > 1) {
                            // //System.out.println("koi" + str[str.length - 1]+" "+str1[str1.length - 1]);
                            String s1, s2, stemp;
                            s1 = str[str.length - 1];
                            s2 = str1[str1.length - 1];
                            stemp = s1.substring(0, s1.length() - 1);
                            s1 = s1.substring(0, s1.length());
                            s2 = s2.substring(0, s2.length() - 1);
                            //System.out.println("koi" +s1+" "+s2);

                            if ((s1.equals(s2)) || (s1.equals(stemp))) {
                                //System.out.println("koiss0");
                                //System.out.println("koissword" + str[str.length - 2]);
                                if (str[str.length - 2].equals("in")) {

                                    String st = str[str.length - 2] + " " + s1;
                                    String st1 = str1[str1.length - 2] + " " + str1[str1.length - 1];
                                    //System.out.println("koiss" + st);
                                    edtsearch.setText(edtsearch.getText().toString().replace(st, "").trim());
                                    ExternalFunctions.selsugest = ExternalFunctions.selsugest.replace(st1, "").trim();
                                    if (!edtsearch.getText().toString().isEmpty())
                                        edtsearch.setSelection(edtsearch.getText().length());
                                    JSONArray arrForcolor = new JSONArray();
                                    objMainList.put("Color", arrForcolor);

                                } else if (str[str.length - 2].equals("or")) {
                                    String st = str[str.length - 3] + " " + str[str.length - 2] + " " + s1;
                                    String st1 = str1[str1.length - 3] + " " + str1[str1.length - 2] + " " + str1[str1.length - 1];
                                    //System.out.println("koissword" + st);

                                    if (!edtsearch.getText().toString().isEmpty())
                                        edtsearch.setSelection(edtsearch.getText().length());
                                    if (objMainList.getJSONArray("Category") != null) {
                                        for (int i = 0; i < objMainList.getJSONArray("Category").length(); i++) {
                                            String stri = objMainList.getJSONArray("Category").get(i).toString();
                                            if (ExternalFunctions.selsugest.contains(stri)) {
                                                JSONArray arrForcat = new JSONArray();
                                                objMainList.put("Category", arrForcat);
                                            }
                                        }
                                    } else if (objMainList.getJSONArray("SubCategory") != null) {
                                        for (int i = 0; i < objMainList.getJSONArray("SubCategory").length(); i++) {
                                            String stri = objMainList.getJSONArray("SubCategory").get(i).toString();
                                            if (ExternalFunctions.selsugest.contains(stri)) {
                                                JSONArray arrForsubcat = new JSONArray();
                                                objMainList.put("SubCategory", arrForsubcat);
                                            }
                                        }
                                    } else if (objMainList.getJSONArray("Style") != null) {
                                        for (int i = 0; i < objMainList.getJSONArray("Style").length(); i++) {
                                            String stri = objMainList.getJSONArray("Style").get(i).toString();
                                            if (ExternalFunctions.selsugest.contains(stri)) {
                                                JSONArray arrForstyle = new JSONArray();
                                                objMainList.put("Style", arrForstyle);
                                            }
                                        }
                                    } else if (objMainList.getJSONArray("Occasion") != null) {
                                        for (int i = 0; i < objMainList.getJSONArray("Occasion").length(); i++) {
                                            String stri = objMainList.getJSONArray("Occasion").get(i).toString();
                                            if (ExternalFunctions.selsugest.contains(stri)) {
                                                JSONArray arrForoccasion = new JSONArray();
                                                objMainList.put("Occasion", arrForoccasion);
                                            }
                                        }
                                    } else if (objMainList.getJSONArray("Color") != null) {
                                        for (int i = 0; i < objMainList.getJSONArray("Color").length(); i++) {
                                            String stri = objMainList.getJSONArray("Color").get(i).toString();
                                            if (ExternalFunctions.selsugest.contains(stri)) {
                                                JSONArray arrForcolor = new JSONArray();
                                                objMainList.put("Color", arrForcolor);
                                            }
                                        }
                                    } else if (objMainList.getJSONArray("Brand") != null) {
                                        for (int i = 0; i < objMainList.getJSONArray("Brand").length(); i++) {
                                            String stri = objMainList.getJSONArray("Brand").get(i).toString();
                                            if (ExternalFunctions.selsugest.contains(stri)) {
                                                JSONArray arrForbranch = new JSONArray();
                                                objMainList.put("Brand", arrForbranch);
                                            }
                                        }
                                    }
                                    edtsearch.setText(edtsearch.getText().toString().replace(st, ""));
                                    ExternalFunctions.selsugest = ExternalFunctions.selsugest.replace(st1, "");

                                } else if (str[str.length - 2].equals("and")) {
                                    String st = str[str.length - 3] + " " + str[str.length - 2] + " " + s1;
                                    String st1 = str1[str1.length - 3] + " " + str1[str1.length - 2] + " " + str1[str1.length - 1];
                                    //System.out.println("koiss" + st);

                                    if (!edtsearch.getText().toString().isEmpty())
                                        edtsearch.setSelection(edtsearch.getText().length());
                                    if (objMainList.getJSONArray("Category") != null) {
                                        for (int i = 0; i < objMainList.getJSONArray("Category").length(); i++) {
                                            String stri = objMainList.getJSONArray("Category").get(i).toString();
                                            if (ExternalFunctions.selsugest.contains(stri)) {
                                                JSONArray arrForcat = new JSONArray();
                                                objMainList.put("Category", arrForcat);
                                            }
                                        }
                                    } else if (objMainList.getJSONArray("SubCategory") != null) {
                                        for (int i = 0; i < objMainList.getJSONArray("SubCategory").length(); i++) {
                                            String stri = objMainList.getJSONArray("SubCategory").get(i).toString();
                                            if (ExternalFunctions.selsugest.contains(stri)) {
                                                JSONArray arrForsubcat = new JSONArray();
                                                objMainList.put("SubCategory", arrForsubcat);
                                            }
                                        }
                                    } else if (objMainList.getJSONArray("Style") != null) {
                                        for (int i = 0; i < objMainList.getJSONArray("Style").length(); i++) {
                                            String stri = objMainList.getJSONArray("Style").get(i).toString();
                                            if (ExternalFunctions.selsugest.contains(stri)) {
                                                JSONArray arrForstyle = new JSONArray();
                                                objMainList.put("Style", arrForstyle);
                                            }
                                        }
                                    } else if (objMainList.getJSONArray("Occasion") != null) {
                                        for (int i = 0; i < objMainList.getJSONArray("Occasion").length(); i++) {
                                            String stri = objMainList.getJSONArray("Occasion").get(i).toString();
                                            if (ExternalFunctions.selsugest.contains(stri)) {
                                                JSONArray arrForoccasion = new JSONArray();
                                                objMainList.put("Occasion", arrForoccasion);
                                            }
                                        }
                                    } else if (objMainList.getJSONArray("Color") != null) {
                                        for (int i = 0; i < objMainList.getJSONArray("Color").length(); i++) {
                                            String stri = objMainList.getJSONArray("Color").get(i).toString();
                                            if (ExternalFunctions.selsugest.contains(stri)) {
                                                JSONArray arrForcolor = new JSONArray();
                                                objMainList.put("Color", arrForcolor);
                                            }
                                        }
                                    } else if (objMainList.getJSONArray("Brand") != null) {
                                        for (int i = 0; i < objMainList.getJSONArray("Brand").length(); i++) {
                                            String stri = objMainList.getJSONArray("Brand").get(i).toString();
                                            if (ExternalFunctions.selsugest.contains(stri)) {
                                                JSONArray arrForbranch = new JSONArray();
                                                objMainList.put("Brand", arrForbranch);
                                            }
                                        }
                                    }
                                    ExternalFunctions.jsonObjsearch.put("filter", objMainList);
                                    edtsearch.setText(edtsearch.getText().toString().replace(st, ""));
                                    ExternalFunctions.selsugest = ExternalFunctions.selsugest.replace(st1, "");

                                } else if (str[str.length - 2].equals("for")) {
                                    String st = str[str.length - 2] + " " + s1;
                                    String st1 = str1[str1.length - 2] + " " + str1[str1.length - 1];
                                    //System.out.println("koiss"+st);
                                    edtsearch.setText(edtsearch.getText().toString().replace(st, ""));
                                    ExternalFunctions.selsugest = ExternalFunctions.selsugest.replace(st1, "");
                                    if (!edtsearch.getText().toString().isEmpty())
                                        edtsearch.setSelection(edtsearch.getText().length());
                                    JSONArray arrForoccasion = new JSONArray();
                                    objMainList.put("Occasion", arrForoccasion);
                                    ExternalFunctions.jsonObjsearch.put("filter", objMainList);

                                } else if (str[str.length - 2].equals("from")) {
                                    String st = str[str.length - 2] + " " + s1;
                                    String st1 = str1[str1.length - 2] + " " + str1[str1.length - 1];
                                    //System.out.println("koiss"+st);
                                    edtsearch.setText(edtsearch.getText().toString().replace(st, "").trim());
                                    ExternalFunctions.selsugest = ExternalFunctions.selsugest.replace(st1, "").trim();
                                    if (!edtsearch.getText().toString().isEmpty())
                                        edtsearch.setSelection(edtsearch.getText().length());
                                    JSONArray arrForbranch = new JSONArray();
                                    objMainList.put("Brand", arrForbranch);
                                    ExternalFunctions.jsonObjsearch.put("filter", objMainList);

                                } else {
                                    //System.out.println("wsas");
                                    //  //System.out.println("wsas" + objMainList.getJSONArray("SubCategory").get(0).toString()+"="+edtsearch.getText().toString());

                                    String cat = "";
                                    String cat1 = "";
                                    String subcat = "";
                                    String subcat1 = "";
                                    String style = "";
                                    String style1 = "";
                                    try {
                                        cat1 = objMainList.getJSONArray("Category").get(0).toString();
                                        cat = objMainList.getJSONArray("Category").get(0).toString();
                                        cat = cat.substring(0, cat.length() - 1);
                                    } catch (Exception e) {

                                    }
                                    try {
                                        subcat1 = objMainList.getJSONArray("SubCategory").get(0).toString();
                                        subcat = objMainList.getJSONArray("SubCategory").get(0).toString();
                                        subcat = subcat.substring(0, subcat.length() - 1);
                                    } catch (Exception e) {

                                    }
                                    try {
                                        style1 = objMainList.getJSONArray("Style").get(0).toString();
                                        style = objMainList.getJSONArray("Style").get(0).toString();
                                        style = subcat.substring(0, style.length() - 1);
                                    } catch (Exception e) {

                                    }

                                    //System.out.println("wsas"+subcat);
                                    if (cat.equals(edtsearch.getText().toString())) {

                                        //System.out.println("wsas");
                                        edtsearch.setText(edtsearch.getText().toString().replace(cat, ""));
                                        ExternalFunctions.selsugest = ExternalFunctions.selsugest.replace(cat1, "");
                                        if (!edtsearch.getText().toString().isEmpty())
                                            edtsearch.setSelection(edtsearch.getText().length());
                                        JSONArray arrForcat = new JSONArray();
                                        objMainList.put("Category", arrForcat);
                                        ExternalFunctions.jsonObjsearch.put("filter", objMainList);
                                    } else if (subcat.equals(edtsearch.getText().toString())) {
                                        edtsearch.setText(edtsearch.getText().toString().replace(subcat, ""));
                                        ExternalFunctions.selsugest = ExternalFunctions.selsugest.replace(subcat1, "");
                                        if (!edtsearch.getText().toString().isEmpty())
                                            edtsearch.setSelection(edtsearch.getText().length());
                                        JSONArray arrForsubcat = new JSONArray();
                                        objMainList.put("SubCategory", arrForsubcat);
                                        //System.out.println("wsasggjgg");
                                        ExternalFunctions.jsonObjsearch.put("filter", objMainList);
                                    } else if (style.equals(edtsearch.getText().toString())) {

                                        edtsearch.setText(edtsearch.getText().toString().replace(style, ""));
                                        ExternalFunctions.selsugest = ExternalFunctions.selsugest.replace(style1, "");
                                        if (!edtsearch.getText().toString().isEmpty())
                                            edtsearch.setSelection(edtsearch.getText().length());
                                        JSONArray arrForstyle = new JSONArray();
                                        objMainList.put("Style", arrForstyle);
                                        //System.out.println("wsasggjgg");
                                        ExternalFunctions.jsonObjsearch.put("filter", objMainList);
                                    } else if (edtsearch.getText().toString().contains("and") || edtsearch.getText().toString().contains("or")) {

                                        //System.out.println("kitti");
                                        JSONArray arrForcat = new JSONArray();

                                        JSONArray arrForsubcat = new JSONArray();

                                        JSONArray arrForstyle = new JSONArray();

                                        JSONArray arrForoccasion = new JSONArray();

                                        JSONArray arrForcolor = new JSONArray();

                                        JSONArray arrForbranch = new JSONArray();

                                        objMainList.put("Category", arrForcat);
                                        objMainList.put("Style", arrForstyle);
                                        objMainList.put("SubCategory", arrForsubcat);
                                        objMainList.put("Occasion", arrForoccasion);
                                        objMainList.put("Color", arrForcolor);
                                        objMainList.put("Brand", arrForbranch);
                                        objMainList.put("string", "");
                                        ExternalFunctions.jsonObjsearch.put("filter", objMainList);
                                        ExternalFunctions.selsugest = "";
                                        ExternalFunctions.jsonObjsearch.put("query_string", "");
                                        edtsearch.setText("");

                                    }

                                }
                            }
                        } else {
                            //System.out.println("wsas");
                            //System.out.println("wsas" + objMainList.getJSONArray("SubCategory").get(0).toString()+"="+edtsearch.getText().toString());

                            String cat = "";
                            String cat1 = "";
                            String subcat = "";
                            String subcat1 = "";
                            String style = "";
                            String style1 = "";
                            try {
                                cat1 = objMainList.getJSONArray("Category").get(0).toString();
                                cat = objMainList.getJSONArray("Category").get(0).toString();
                                cat = cat.substring(0, cat.length() - 1);
                            } catch (Exception e) {

                            }
                            try {
                                subcat1 = objMainList.getJSONArray("SubCategory").get(0).toString();
                                subcat = objMainList.getJSONArray("SubCategory").get(0).toString();
                                subcat = subcat.substring(0, subcat.length() - 1);
                            } catch (Exception e) {

                            }
                            try {
                                style1 = objMainList.getJSONArray("Style").get(0).toString();
                                style = objMainList.getJSONArray("Style").get(0).toString();
                                style = subcat.substring(0, style.length() - 1);
                            } catch (Exception e) {

                            }

                            //System.out.println("wsas"+subcat);
                            if (cat.equals(edtsearch.getText().toString())) {
                                JSONArray arrForcat = new JSONArray();
                                objMainList.put("Category", arrForcat);
                                ExternalFunctions.jsonObjsearch.put("filter", objMainList);
                                //System.out.println("wsas");
                                edtsearch.setText(edtsearch.getText().toString().replace(cat, ""));
                                ExternalFunctions.selsugest = ExternalFunctions.selsugest.replace(cat1, "");
                                if (!edtsearch.getText().toString().isEmpty())
                                    edtsearch.setSelection(edtsearch.getText().length());
                            } else if (subcat.equals(edtsearch.getText().toString())) {
                                edtsearch.setText(edtsearch.getText().toString().replace(subcat, ""));
                                ExternalFunctions.selsugest = ExternalFunctions.selsugest.replace(subcat1, "");
                                if (!edtsearch.getText().toString().isEmpty())
                                    edtsearch.setSelection(edtsearch.getText().length());
                                JSONArray arrForsubcat = new JSONArray();
                                objMainList.put("SubCategory", arrForsubcat);
                                //System.out.println("wsasggjgg");
                                ExternalFunctions.jsonObjsearch.put("filter", objMainList);
                            } else if (style.equals(edtsearch.getText().toString())) {

                                edtsearch.setText(edtsearch.getText().toString().replace(style, ""));
                                ExternalFunctions.selsugest = ExternalFunctions.selsugest.replace(style1, "");
                                if (!edtsearch.getText().toString().isEmpty())
                                    edtsearch.setSelection(edtsearch.getText().length());
                                JSONArray arrForstyle = new JSONArray();
                                objMainList.put("Style", arrForstyle);
                                //System.out.println("wsasggjgg");
                                ExternalFunctions.jsonObjsearch.put("filter", objMainList);
                            }

                        }

                    } catch (Exception e) {

                    }
                    edtsearch.setSelection(edtsearch.getText().length());

                }

                mHandler.removeCallbacksAndMessages(null);
                mHandler.postDelayed(userStoppedTyping, 500); // 1 second
            }

            Runnable userStoppedTyping = new Runnable() {

                @Override
                public void run() {

                    //if(edtsearch.getText().length()>0) {
                    // user didn't typed for 2 seconds,
                    if (bltextchanged) {

                        try {
                            //System.out.println("searchzx ccc" + ExternalFunctions.selsugest);
                            if (edtsearch.getText().toString().trim().isEmpty() && ExternalFunctions.selsugest.length() <= 0) {
                                ExternalFunctions.selsugest = "";
                                JSONArray arrForcat = new JSONArray();

                                JSONArray arrForsubcat = new JSONArray();

                                JSONArray arrForstyle = new JSONArray();

                                JSONArray arrForoccasion = new JSONArray();

                                JSONArray arrForcolor = new JSONArray();

                                JSONArray arrForbranch = new JSONArray();

                                objMainList.put("Category", arrForcat);
                                objMainList.put("Style", arrForstyle);
                                objMainList.put("SubCategory", arrForsubcat);
                                objMainList.put("Occasion", arrForoccasion);
                                objMainList.put("Color", arrForcolor);
                                objMainList.put("Brand", arrForbranch);
                                objMainList.put("string", "");
                                ExternalFunctions.jsonObjsearch.put("filter", objMainList);

                            }
                            if (ExternalFunctions.selsugest.length() > 1) {
                                if (edtsearch.getText().toString().trim().isEmpty()) {
                                    ExternalFunctions.jsonObjsearch.put("query_string", "");

                                } else {
                                    //System.out.println("searchzx else " + edtsearch.getText().toString().trim() + "=" + ExternalFunctions.selsugest);
                                    if (edtsearch.getText().toString().trim().contains(ExternalFunctions.selsugest)) {

                                        String strremain = (removeWords(edtsearch.getText().toString().trim(), ExternalFunctions.selsugest));
                                        //System.out.println("searchzx " + strremain);
                                        ExternalFunctions.jsonObjsearch.put("query_string", strremain);
                                    } else {
                                        ExternalFunctions.jsonObjsearch.put("query_string", edtsearch.getText().toString());
                                    }


                                }
                            } else {
                                objMainList.put("string", "");
                                ExternalFunctions.jsonObjsearch.put("query_string", edtsearch.getText());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // Toast.makeText(getApplicationContext(),ExternalFunctions.jsonObjsearch.toString(),Toast.LENGTH_LONG).show();
                        //System.out.println(ExternalFunctions.selsugest+"searchzx" + ExternalFunctions.jsonObjsearch);
                        if (!ExternalFunctions.selsugest.toString().trim().equals(edtsearch.getText().toString().trim()) || (edtsearch.getText().toString().isEmpty())) {

                            ApiService.getInstance(searchnew.this, 1).postData(searchnew.this, EnvConstants.APP_BASE_URL + "/search/suggestions/product", ExternalFunctions.jsonObjsearch, SCREEN_NAME, "searchproduct");
                            if (tmptab == 0)
                                viewPager.setCurrentItem(0);
                            else
                                viewPager.setCurrentItem(1);
                        }
                    }
                    bltextchanged = false;
                }

                //  ApiService.getInstance(searchnew.this, 1).getData(searchnew.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/search/suggestions/product/",jsonObjsearch ,"getsearch");
                //}
            };
        });

        imgclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExternalFunctions.blapplysearch = false;
                onBackPressed();
            }
        });

        edtsearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    if (!edtsearch.getText().toString().isEmpty()) {
                        ExternalFunctions.blapplysearch = true;
                        ExternalFunctions.blapplysug = false;
                        ExternalFunctions.strsearch = edtsearch.getText().toString();
                        Intent feed = new Intent(searchnew.this, searchFeedPage.class);
                        feed.putExtra("activity", "SplashScreen");
                        feed.putExtra("activityfrom",ExternalFunctions.prevActivity );
                        startActivity(feed);
                        //Runtime.getRuntime().gc();
                    } else {
                        try {
                            JSONArray arrForcat = new JSONArray();

                            JSONArray arrForsubcat = new JSONArray();

                            JSONArray arrForstyle = new JSONArray();

                            JSONArray arrForoccasion = new JSONArray();

                            JSONArray arrForcolor = new JSONArray();

                            JSONArray arrForbranch = new JSONArray();
                            //            //Finally add item arrays for "A" and "B" to main list with key
                            objMainList.put("Category", arrForcat);
                            objMainList.put("Style", arrForstyle);
                            objMainList.put("SubCategory", arrForsubcat);
                            objMainList.put("Occasion", arrForoccasion);
                            objMainList.put("Color", arrForcolor);
                            objMainList.put("Brand", arrForbranch);
                            objMainList.put("string", "");
                            ExternalFunctions.jsonObjsearch.put("query_string", "");
                            ExternalFunctions.selsugest = "";
                            ExternalFunctions.jsonObjsearch.put("filter", objMainList);
                        } catch (Exception e) {

                        }
                        Toast.makeText(getApplicationContext(), "Please enter word to search", Toast.LENGTH_LONG).show();

                    }
                    return true;
                }
                return false;
            }
        });

        imgserach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edtsearch.getText().toString().isEmpty()) {
                    ExternalFunctions.blapplysearch = true;
                    ExternalFunctions.blapplysug = false;
                    ExternalFunctions.strsearch = edtsearch.getText().toString();
                    Intent feed = new Intent(searchnew.this, searchFeedPage.class);
                    feed.putExtra("activityfrom",ExternalFunctions.prevActivity );
                    feed.putExtra("activity", "SplashScreen");
                    startActivity(feed);
                    //  finish();
                } else {
                    try {
                        JSONArray arrForcat = new JSONArray();

                        JSONArray arrForsubcat = new JSONArray();

                        JSONArray arrForstyle = new JSONArray();

                        JSONArray arrForoccasion = new JSONArray();

                        JSONArray arrForcolor = new JSONArray();

                        JSONArray arrForbranch = new JSONArray();
                        //            //Finally add item arrays for "A" and "B" to main list with key
                        objMainList.put("Category", arrForcat);
                        objMainList.put("Style", arrForstyle);
                        objMainList.put("SubCategory", arrForsubcat);
                        objMainList.put("Occasion", arrForoccasion);
                        objMainList.put("Color", arrForcolor);
                        objMainList.put("Brand", arrForbranch);
                        objMainList.put("string", "");
                        ExternalFunctions.jsonObjsearch.put("query_string", "");
                        ExternalFunctions.selsugest = "";
                        ExternalFunctions.jsonObjsearch.put("filter", objMainList);
                    } catch (Exception e) {

                    }
                    Toast.makeText(getApplicationContext(), "Please enter word to search", Toast.LENGTH_LONG).show();

                }
                // ApiService.getInstance(searchnew.this, 1).postData(searchnew.this, EnvConstants.APP_BASE_URL + "/search/product",  ExternalFunctions.jsonObjsearch , SCREEN_NAME, "searchvalue");;

            }
        });


        tabLayout.setupWithViewPager(viewPager);
        // Iterate over all tabs and set the custom view
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(adapter.getTabView(i));
        }
        viewPager.setOffscreenPageLimit(1);


        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {


                viewPager.setCurrentItem(tab.getPosition());

                if (tab.getPosition() == 0) {
                    if (isInternetOn()) {

                    } else {

                        Alerts.InternetAlert(searchnew.this);
                    }
                } else if (tab.getPosition() == 1) {
                    if (isInternetOn()) {

                    } else {

                        Alerts.InternetAlert(searchnew.this);
                    }


                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }


    public static void gettabcount() {

    }

    public static String removeWords(String word, String remove) {
        return word.replace(remove, " ");
    }

    public final boolean isInternetOn() {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {

            // if connected with internet

            // Toast.makeText(this, " Connected ", Toast.LENGTH_LONG).show();
            return true;

        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {

            //Toast.makeText(this, " Not Connected ", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
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
        endtime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        HashMap<String, Object> page_change = new HashMap<String, Object>();
        page_change.put("new_page", SCREEN_NAME);
        page_change.put("old_page", ExternalFunctions.prevActivity);
        page_change.put("page_view_starttime", stime);
        page_change.put("page_view_endtime", endtime);
        cleverTap.event.push("page_change", page_change);
        ExternalFunctions.prevActivity = SCREEN_NAME;
        Runtime.getRuntime().gc();
        //clearCache(this.getApplicationContext());
    }


    @Override
    public void onBackPressed() {

        ExternalFunctions.intcat = 1;
        ExternalFunctions.intsize = 1;
        ExternalFunctions.intprice = 1;
        ExternalFunctions.blfiteropen = false;
        getFragmentManager().popBackStack();
        try {
            Intent refine = new Intent(searchnew.this, Class.forName(callingActivity));
            refine.putExtra("activity", "searchnew");
            startActivity(refine);
                      finish();


        } catch (ClassNotFoundException e) {

            Intent refine = new Intent(searchnew.this, discover.class);
            refine.putExtra("activity", "discover");
            startActivity(refine);
            finish();
        }

    }


    @Override
    public void onResponseCallback(JSONObject response, String flag) {
        if (flag.equals("searchproduct")) {
            ExternalFunctions.blsearch = true;
            //System.out.println("search response  :" + response);
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            try {
                String status = resp.getString("status");
                if (status.equals("success")) {
                    //  ExternalFunctions.selsugest="";
                    // hideKeyboard(searchnew.this);
                    ExternalFunctions.datasearch = resp.getJSONObject("data");


                    ApiService.getInstance(searchnew.this, 1).postData(searchnew.this, EnvConstants.APP_BASE_URL + "/search/suggestions/closet", ExternalFunctions.jsonObjsearch, SCREEN_NAME, "searchcloset");

                }
            } catch (Exception e) {

            }
        } else if (flag.equals("searchcloset")) {
            //System.out.println("searchcloset response  :" + response);
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            try {
                String status = resp.getString("status");
                if (status.equals("success")) {
                    ExternalFunctions.blsearch1 = true;
                    ExternalFunctions.datasearchcloset = resp.getJSONObject("data");

                    //System.out.println("zzz"+tmptab);
                    if (tmptab == 0) {

                        if (getFragmentRefreshListener() != null) {

                            getFragmentRefreshListener().onRefresh();

                        }
                    } else {
                        if (getFragmentRefreshListener1() != null) {

                            getFragmentRefreshListener1().onRefresh();

                        }
                    }

//                    adapter = new searchpageadapter
//                            (getSupportFragmentManager(), 2, getApplicationContext());
//                    viewPager.setAdapter(adapter);
                    //viewPager.getAdapter().notifyDataSetChanged();

                }
            } catch (Exception e) {
            }
        } else if (flag.equals("searchvalue")) {

            //System.out.println("searchoutput:" + response);

        }


    }

    public interface FragmentRefreshListener {
        void onRefresh();
    }

    public interface FragmentRefreshListener1 {
        void onRefresh();
    }

    @Override
    public void onErrorCallback(VolleyError error, String flag) {

    }

    public static void hideKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View v = ((Activity) ctx).getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //System.out.println("vzxcv");
        ExternalFunctions.bloverlay = false;
    }


    @Override
    protected void onResume() {
        super.onResume();
        mTracker.set("UserName", GetSharedValues.getUsername(getApplicationContext()));
        mTracker.set("ZapUsername", GetSharedValues.getZapname(getApplicationContext()));
        mTracker.setScreenName("Search page");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}

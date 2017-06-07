package activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import network.ApiCommunication;
import network.ApiService;
import utils.CustomAlert;
import utils.CustomMessage;
import utils.GetSharedValues;

//import com.//Appsee.//Appsee;
//import com.uxcam.UXCam;

public class SizeguideActivity extends AppCompatActivity implements ApiCommunication {
    LinearLayout l1p, l1h;
    int intwidth, inthieght;
    TextView[] t1;
    public static TextView[] tus;
    public static TextView[] tuk;
    public static TextView[] teu;
    public static LinearLayout[] l2h;
    String SCREEN_NAME = "SIZEGUIDE";
    String strgetsize;
    TextView tvnext;
    JSONArray data;
    int intselectedID;
    int intselectedTag;
    ImageView Skip;
    String struser;
    String[] strsize = {"TOP SIZE", " ", "UK", "US", "EU"};
    public static ArrayList<String> selectedid = new ArrayList<String>();
    public static ArrayList<String> selectedSizes = new ArrayList<String>();
    public static ArrayList<String> selectedqty = new ArrayList<String>();
    ProgressBar progressBar;

    Context ctx;
    public static int intj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sizeguide);
        //Appsee.start("4fb55809ab45411c909521bda720f8e6");
        //UXCam.startWithKey("1dfb25141864376");         overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        //////Thread.currentThread().setDefaultUncaughtExceptionHandler(new SplashScreen.MyUncaughtExceptionHandler());
        progressBar = (ProgressBar) findViewById(R.id.sizeguide_progressBar);
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final View customView = getLayoutInflater().inflate(R.layout.product_bar, null);
        getSupportActionBar().setCustomView(customView);
        Toolbar parent = (Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0, 0);
        TextView tv_headTitle = (TextView) findViewById(R.id.product_title_text);
        tv_headTitle.setText("SELECT SIZE");
        ctx = this;

        Skip = (ImageView) findViewById(R.id.productfeedButton);
        Skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        l1p = (LinearLayout) findViewById(R.id.l1p);
        l1h = (LinearLayout) findViewById(R.id.l1h);
        tvnext = (TextView) findViewById(R.id.next);
        tvnext.setVisibility(View.INVISIBLE);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        intwidth = metrics.widthPixels;
        inthieght = metrics.heightPixels;
        selectedid.clear();
        selectedSizes.clear();
        selectedqty.clear();
        ////////System.out.println("dddactivity");
        Bundle bundle = this.getIntent().getExtras();

        strgetsize = bundle.getString("sizecategory");
        struser = GetSharedValues.getUsertype(SizeguideActivity.this);

//        if (upload.selectedSize.size()>0){
//            //////System.out.println("preselected size"+upload.selectedSize);
//            //////System.out.println("preselected qty"+upload.selectedQty);
//        }
        GetSizes();


        tvnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String aa;
//                    for(int i=0;i<selectedqty.size();i++){
//                        aa=selectedqty.get(i);
//                        aa=aa.substring(3,aa.length());
//                        selectedqty.remove(i);
//                        ////////System.out.println("zaq"+aa);
//                        selectedqty.add(aa.trim());
//
//                    }
                    ////////System.out.println("zaq"+selectedqty);

                    if (selectedid.size() > 0) {
                        upload.selectedSize.clear();
                        upload.selectedQty.clear();

                        Intent newintent = new Intent(getApplicationContext(), upload.class);
                        newintent.putStringArrayListExtra("SelectedID", selectedid);
                        newintent.putStringArrayListExtra("selectedSizes", selectedSizes);
                        newintent.putStringArrayListExtra("selectedQTY", selectedqty);
                        setResult(RESULT_OK, newintent);
                        finish();
                    } else {
                         CustomMessage.getInstance().CustomMessage(SizeguideActivity.this, "Select size");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }


    //    Server request
//    --------------------------------------------------------------
    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    private void GetSizes() {
        progressBar.setVisibility(View.VISIBLE);
        ApiService.getInstance(SizeguideActivity.this, 1).getData(SizeguideActivity.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/catalogue/getsizes/" + strgetsize + "/", "getsizes");
    }

    @Override
    public void onResponseCallback(JSONObject response, String flag) {
        ////////System.out.println("response___" + response);
        if (flag.equals("getsizes")) {
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                try {

                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        tvnext.setVisibility(View.VISIBLE);
                        data = resp.getJSONArray("data");
                        l2h = new LinearLayout[data.length()];
                        t1 = new TextView[data.length()];
                        teu = new TextView[data.length()];
                        tuk = new TextView[data.length()];
                        tus = new TextView[data.length()];
                        if (strgetsize.equals("C")) {
                            ////////System.out.println("responsedata___" + data);
                            for (int j = 0; j < strsize.length; j++) {

                                LinearLayout l2h = new LinearLayout(this);
                                l2h.setOrientation(LinearLayout.HORIZONTAL);
                                l2h.setBackgroundColor(Color.parseColor("#ffffff"));
                                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                LinearLayout.LayoutParams params;
                                if (j == 1) {
                                    params = new LinearLayout.LayoutParams(1, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    params.setMargins(5, 0, 5, 0);
                                } else if (j == 0) {
                                    params = new LinearLayout.LayoutParams(intwidth / 4, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    params.setMargins(7, 0, 0, 0);
                                } else {
                                    params = new LinearLayout.LayoutParams(intwidth / 4, LinearLayout.LayoutParams.WRAP_CONTENT);
                                }

                                params1.weight = 1.0f;

                                TextView t1 = new TextView(this);

                                t1.setText(strsize[j]);
                                t1.setLayoutParams(params1);
                                t1.setGravity(Gravity.CENTER);
                                t1.setTextColor(Color.BLACK);
                                t1.setTextSize(16);
                                l2h.setLayoutParams(params);
                                l2h.addView(t1);
                                l1h.addView(l2h);

                            }
                            for (int j = 0; j < data.length(); j++) {


                                //////////System.out.println("gggg" + data.getJSONObject(i).getString("size"));


                                l2h[j] = new LinearLayout(this);
                                l2h[j].setOrientation(LinearLayout.HORIZONTAL);
                                l2h[j].setBackgroundColor(Color.parseColor("#ffffff"));
                                t1[j] = new TextView(this);
                                LinearLayout.LayoutParams paramsHoriz = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 150);
                                paramsHoriz.weight = 1.0f;

                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(intwidth / 4, LinearLayout.LayoutParams.MATCH_PARENT);
                                // params.setMargins(0, 10, 0, 10);
                                t1[j].setTextColor(Color.BLACK);
                                t1[j].setGravity(Gravity.CENTER);
                                t1[j].setTextSize(14);
                                t1[j].setText(data.getJSONObject(j).getString("size"));
                                t1[j].setLayoutParams(params);

                                l2h[j].addView(t1[j]);
//-----------------------------------------------------------------------------------------------------------
                                final TextView tline1 = new TextView(this);
                                final TextView dummy = new TextView(this);
                                LinearLayout.LayoutParams paramsxy = new LinearLayout.LayoutParams(1, 1);
                                dummy.setTag(data.getJSONObject(j));
                                dummy.setLayoutParams(paramsxy);
                                LinearLayout.LayoutParams paramsx = new LinearLayout.LayoutParams(1, LinearLayout.LayoutParams.MATCH_PARENT);

                                paramsx.setMargins(7, 0, 5, 0);
                                tline1.setBackgroundColor(Color.BLACK);
                                tline1.setLayoutParams(paramsx);
                                // tline1.setTextSize(20);
                                tline1.setGravity(Gravity.CENTER);
                                tline1.setTag(j);

                                l2h[j].addView(tline1);
                                l2h[j].addView(dummy);


                                l2h[j].setLayoutParams(paramsHoriz);

//------------------------------------------------------------------------------------------------------------------------------
                                tuk[j] = new TextView(this);
                                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(intwidth / 4, LinearLayout.LayoutParams.MATCH_PARENT);
                                // params2.set(0, 10, 0, 10);
                                tuk[j].setLayoutParams(params2);
                                ///  tuk[i].setPadding(0, 10, 0, 10);
                                tuk[j].setText(data.getJSONObject(j).getString("uk_size"));
                                tuk[j].setGravity(Gravity.CENTER);
                                tuk[j].setTextSize(14);
                                l2h[j].addView(tuk[j]);

                                l2h[j].setLayoutParams(paramsHoriz);
//----------------------------------------------------------------------------------
                                tus[j] = new TextView(this);
                                LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(intwidth / 4, LinearLayout.LayoutParams.MATCH_PARENT);
                                // params3.setMargins(0, 10, 0, 10);
                                tus[j].setLayoutParams(params3);
                                // tus[i].setPadding(0, 10, 0, 10);
                                tus[j].setText(data.getJSONObject(j).getString("us_size"));
                                tus[j].setTextSize(14);
                                tus[j].setGravity(Gravity.CENTER);
                                l2h[j].addView(tus[j]);

                                l2h[j].setLayoutParams(paramsHoriz);
//---------------------------------------------------------------------------------------
                                teu[j] = new TextView(this);
                                LinearLayout.LayoutParams params4 = new LinearLayout.LayoutParams(intwidth / 4, LinearLayout.LayoutParams.MATCH_PARENT);
                                // params4.setMargins(0, 10, 0, 10);
                                teu[j].setLayoutParams(params4);
                                // teu[i].setPadding(0, 10, 0, 10);
                                teu[j].setText(data.getJSONObject(j).getString("eu_size"));
                                teu[j].setGravity(Gravity.CENTER);
                                teu[j].setTextSize(14);
                                l2h[j].addView(teu[j]);

                                l2h[j].setLayoutParams(paramsHoriz);
//---------------------------------------------------------------------------------------
                                l1p.addView(l2h[j]);

                                tuk[j].setBackgroundColor(Color.TRANSPARENT);
                                tus[j].setBackgroundColor(Color.TRANSPARENT);
                                teu[j].setBackgroundColor(Color.TRANSPARENT);

                                //////System.out.println("test==="+upload.selectedSize);

                                if (upload.selectedSize.size() > 0) {
                                    if (struser.equals("zap_user")) {
                                        for (int i = 0; i < upload.selectedSize.size(); i++) {
                                            if (!upload.selectedSize.get(i).equals("freesize")) {
                                                if (data.getJSONObject(j).getInt("id") == Integer.parseInt(upload.selectedSize.get(i))) {
                                                    if (upload.selectedSizeType.contains("UK")) {

                                                        l2h[j].setBackgroundColor(Color.parseColor("#FFCDD2"));
                                                        tuk[j].setBackgroundColor(Color.parseColor("#ff7477"));
                                                        tuk[j].setText(tuk[j].getText().toString());
                                                        selectedSizes.add("UK " + data.getJSONObject(j).getString("uk_size"));
                                                        selectedid.add(String.valueOf(upload.selectedSize.get(i)));
                                                        selectedqty.add("UK " + String.valueOf(upload.selectedSize.get(i)) + "-" + String.valueOf(upload.selectedQty.get(i)));

                                                    } else if (upload.selectedSizeType.contains("US")) {
                                                        l2h[j].setBackgroundColor(Color.parseColor("#FFCDD2"));
                                                        tus[j].setBackgroundColor(Color.parseColor("#ff7477"));
                                                        tus[j].setText(tus[j].getText().toString());
                                                        selectedSizes.add("US " + data.getJSONObject(j).getString("us_size"));
                                                        selectedid.add(String.valueOf(upload.selectedSize.get(i)));
                                                        selectedqty.add("US " + String.valueOf(upload.selectedSize.get(i)) + "-" + String.valueOf(upload.selectedQty.get(i)));

                                                    } else {
                                                        l2h[j].setBackgroundColor(Color.parseColor("#FFCDD2"));
                                                        teu[j].setBackgroundColor(Color.parseColor("#ff7477"));
                                                        teu[j].setText(teu[j].getText().toString());
                                                        selectedSizes.add("EU " + data.getJSONObject(j).getString("eu_size"));
                                                        selectedid.add(String.valueOf(upload.selectedSize.get(i)));
                                                        selectedqty.add("EU " + String.valueOf(upload.selectedSize.get(i)) + "-" + String.valueOf(upload.selectedQty.get(i)));

                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        for (int i = 0; i < upload.selectedSize.size(); i++) {
                                            if (data.getJSONObject(j).getInt("id") == Integer.parseInt(upload.selectedSize.get(i))) {
                                                if (upload.selectedSizeType.contains("UK")) {

                                                    l2h[j].setBackgroundColor(Color.parseColor("#FFCDD2"));
                                                    tuk[j].setBackgroundColor(Color.parseColor("#ff7477"));
                                                    tuk[j].setText(tuk[j].getText().toString() + "-(" + upload.selectedQty.get(i) + ")");
                                                    selectedSizes.add("UK " + data.getJSONObject(j).getString("uk_size"));
                                                    selectedid.add(String.valueOf(upload.selectedSize.get(i)));
                                                    selectedqty.add("UK " + String.valueOf(upload.selectedSize.get(i)) + "-" + String.valueOf(upload.selectedQty.get(i)));

                                                } else if (upload.selectedSizeType.contains("US")) {
                                                    l2h[j].setBackgroundColor(Color.parseColor("#FFCDD2"));
                                                    tus[j].setBackgroundColor(Color.parseColor("#ff7477"));
                                                    tus[j].setText(tus[j].getText().toString() + "-(" + upload.selectedQty.get(i) + ")");
                                                    selectedSizes.add("US " + data.getJSONObject(j).getString("us_size"));
                                                    selectedid.add(String.valueOf(upload.selectedSize.get(i)));
                                                    selectedqty.add("US " + String.valueOf(upload.selectedSize.get(i)) + "-" + String.valueOf(upload.selectedQty.get(i)));

                                                } else {
                                                    l2h[j].setBackgroundColor(Color.parseColor("#FFCDD2"));
                                                    teu[j].setBackgroundColor(Color.parseColor("#ff7477"));
                                                    teu[j].setText(teu[j].getText().toString() + "-(" + upload.selectedQty.get(i) + ")");
                                                    selectedSizes.add("EU " + data.getJSONObject(j).getString("eu_size"));
                                                    selectedid.add(String.valueOf(upload.selectedSize.get(i)));
                                                    selectedqty.add("EU " + String.valueOf(upload.selectedSize.get(i)) + "-" + String.valueOf(upload.selectedQty.get(i)));

                                                }
                                            }
                                        }
                                    }
                                }
                                tuk[j].setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (struser.equals("zap_user")) {
                                            selectedid.clear();
                                            selectedSizes.clear();
                                            selectedqty.clear();

                                            JSONObject dummyTag = (JSONObject) dummy.getTag();
                                            ////////System.out.println("dummyyyyy__"+dummyTag);

                                            try {
                                                String dummytagstring = dummyTag.getString("uk_size");
                                                selectedSizes.add("UK " + dummytagstring);
                                                selectedqty.add("UK " + dummyTag.getInt("id") + "-1");
                                                selectedid.add(String.valueOf(dummyTag.getInt("id")));
                                                //////System.out.println(String.valueOf(dummyTag.getInt("id")));

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            for (int i = 0; i < data.length(); i++) {
                                                l2h[i].setBackgroundColor(Color.WHITE);
                                                tus[i].setBackgroundColor(Color.TRANSPARENT);
                                                tuk[i].setBackgroundColor(Color.TRANSPARENT);
                                                teu[i].setBackgroundColor(Color.TRANSPARENT);
                                            }

                                            l2h[(int) tline1.getTag()].setBackgroundColor(Color.parseColor("#FFCDD2"));
                                            tuk[(int) tline1.getTag()].setBackgroundColor(Color.parseColor("#ff7477"));


                                        } else {

                                            JSONObject dummyTag = (JSONObject) dummy.getTag();
                                            ////////System.out.println(dummyTag);
                                            try {
                                                String dummytagstring = dummyTag.getString("uk_size");
                                                selectedSizes.add("UK " + dummytagstring);
                                                if (!selectedid.contains(String.valueOf(dummyTag.getInt("id")))) {
                                                    selectedid.add(String.valueOf(dummyTag.getInt("id")));
                                                    //////System.out.println(String.valueOf("cxcxcxc" + dummyTag.getInt("id")));


                                                } else {
                                                    tus[(int) tline1.getTag()].setBackgroundColor(Color.TRANSPARENT);
                                                    teu[(int) tline1.getTag()].setBackgroundColor(Color.TRANSPARENT);
                                                    selectedSizes.remove("US " + dummyTag.getString("us_size"));
                                                    selectedSizes.remove("EU " + dummyTag.getString("eu_size"));
                                                }

//                                                for (int i = 0; i < data.length(); i++) {
//                                                    l2h[i].setBackgroundColor(Color.WHITE);
//                                                    tus[i].setBackgroundColor(Color.TRANSPARENT);
//                                                    tuk[i].setBackgroundColor(Color.TRANSPARENT);
//                                                    teu[i].setBackgroundColor(Color.TRANSPARENT);
//                                                }
                                                l2h[(int) tline1.getTag()].setBackgroundColor(Color.parseColor("#FFCDD2"));
                                                tuk[(int) tline1.getTag()].setBackgroundColor(Color.parseColor("#ff7477"));
                                                intj = (int) tline1.getTag();
                                                CustomAlert ct = new CustomAlert(ctx, "UK " + dummyTag.getInt("id") + "-", String.valueOf(dummyTag.getInt("id")));

//                                                tuk[(int) tline1.getTag()].setText(tuk[(int) tline1.getTag()].getText().toString()+"-"+intqty);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }


                                    }
                                });
                                tus[j].setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (struser.equals("zap_user")) {
                                            selectedid.clear();
                                            selectedSizes.clear();
                                            selectedqty.clear();
                                            JSONObject dummyTag = (JSONObject) dummy.getTag();
                                            try {
                                                String dummytagstring = dummyTag.getString("us_size");
                                                selectedSizes.add("US " + dummytagstring);
                                                selectedid.add(String.valueOf(dummyTag.getInt("id")));
                                                selectedqty.add("US " + dummyTag.getInt("id") + "-1");
                                                //////System.out.println(String.valueOf(dummyTag.getInt("id")));

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            for (int i = 0; i < data.length(); i++) {
                                                l2h[i].setBackgroundColor(Color.WHITE);
                                                tus[i].setBackgroundColor(Color.TRANSPARENT);
                                                tuk[i].setBackgroundColor(Color.TRANSPARENT);
                                                teu[i].setBackgroundColor(Color.TRANSPARENT);
                                            }


                                            l2h[(int) tline1.getTag()].setBackgroundColor(Color.parseColor("#FFCDD2"));
                                            tus[(int) tline1.getTag()].setBackgroundColor(Color.parseColor("#ff7477"));

                                        } else {

                                            JSONObject dummyTag = (JSONObject) dummy.getTag();
                                            try {
                                                String dummytagstring = dummyTag.getString("us_size");
                                                selectedSizes.add("US " + dummytagstring);

                                                if (!selectedid.contains(String.valueOf(dummyTag.getInt("id")))) {

                                                    selectedid.add(String.valueOf(dummyTag.getInt("id")));
                                                    //////System.out.println(String.valueOf("cxcxcxc"+dummyTag.getInt("id")));


                                                } else {
                                                    tuk[(int) tline1.getTag()].setBackgroundColor(Color.TRANSPARENT);
                                                    teu[(int) tline1.getTag()].setBackgroundColor(Color.TRANSPARENT);
                                                    selectedSizes.remove("EU " + dummyTag.getString("eu_size"));
                                                    selectedSizes.remove("UK " + dummyTag.getString("uk_size"));
                                                }


                                                l2h[(int) tline1.getTag()].setBackgroundColor(Color.parseColor("#FFCDD2"));
                                                tus[(int) tline1.getTag()].setBackgroundColor(Color.parseColor("#ff7477"));
                                                intj = (int) tline1.getTag();
                                                CustomAlert ct = new CustomAlert(ctx, "US " + dummyTag.getInt("id") + "-", String.valueOf(dummyTag.getInt("id")));
                                                // tus[(int) tline1.getTag()].setText(tus[(int) tline1.getTag()].getText().toString()+"-"+intqty);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });
                                teu[j].setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        intselectedTag = Integer.parseInt(tline1.getTag().toString());
                                        if (struser.equals("zap_user")) {
                                            selectedid.clear();
                                            selectedSizes.clear();
                                            selectedqty.clear();
                                            JSONObject dummyTag = (JSONObject) dummy.getTag();
                                            try {
                                                String dummytagstring = dummyTag.getString("eu_size");
                                                selectedSizes.add("EU " + dummytagstring);
                                                selectedqty.add("EU " + dummyTag.getInt("id") + "-1");
                                                selectedid.add(String.valueOf(dummyTag.getInt("id")));
                                                //////System.out.println(String.valueOf(dummyTag.getInt("id")));

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            for (int i = 0; i < data.length(); i++) {
                                                l2h[i].setBackgroundColor(Color.WHITE);
                                                tus[i].setBackgroundColor(Color.TRANSPARENT);
                                                tuk[i].setBackgroundColor(Color.TRANSPARENT);
                                                teu[i].setBackgroundColor(Color.TRANSPARENT);
                                            }


                                            l2h[(int) tline1.getTag()].setBackgroundColor(Color.parseColor("#FFCDD2"));
                                            teu[(int) tline1.getTag()].setBackgroundColor(Color.parseColor("#ff7477"));

                                        } else {
                                            JSONObject dummyTag = (JSONObject) dummy.getTag();
                                            try {
                                                String dummytagstring = dummyTag.getString("eu_size");
                                                selectedSizes.add("EU " + dummytagstring);
                                                if (!selectedid.contains(String.valueOf(dummyTag.getInt("id")))) {
                                                    selectedid.add(String.valueOf(dummyTag.getInt("id")));
                                                    //////System.out.println(String.valueOf("cxcxcxc"+dummyTag.getInt("id")));


                                                } else {
                                                    tus[(int) tline1.getTag()].setBackgroundColor(Color.TRANSPARENT);
                                                    tuk[(int) tline1.getTag()].setBackgroundColor(Color.TRANSPARENT);
                                                    selectedSizes.remove("US " + dummyTag.getString("us_size"));
                                                    selectedSizes.remove("UK " + dummyTag.getString("uk_size"));
                                                }


                                                l2h[(int) tline1.getTag()].setBackgroundColor(Color.parseColor("#FFCDD2"));
                                                teu[(int) tline1.getTag()].setBackgroundColor(Color.parseColor("#ff7477"));
                                                intj = (int) tline1.getTag();
                                                CustomAlert ct = new CustomAlert(ctx, "EU " + dummyTag.getInt("id") + "-", String.valueOf(dummyTag.getInt("id")));
                                                //  teu[(int) tline1.getTag()].setText(teu[(int) tline1.getTag()].getText().toString()+"-"+intqty);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });
                            }


                        } else if (strgetsize.equals("FW")) {

                            for (int j = 2; j < strsize.length; j++) {

                                LinearLayout l2h = new LinearLayout(this);
                                l2h.setOrientation(LinearLayout.HORIZONTAL);

                                l2h.setBackgroundColor(Color.parseColor("#ffffff"));
                                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(intwidth / 3, LinearLayout.LayoutParams.WRAP_CONTENT);


                                params1.weight = 1.0f;

                                TextView t1 = new TextView(this);

                                t1.setText(strsize[j]);
                                t1.setLayoutParams(params1);
                                t1.setGravity(Gravity.CENTER);
                                t1.setTextColor(Color.BLACK);
                                t1.setTextSize(15);
                                l2h.setLayoutParams(params);
                                l2h.addView(t1);
                                l1h.addView(l2h);

                            }
                            for (int j = 0; j < data.length(); j++) {


                                l2h[j] = new LinearLayout(this);
                                l2h[j].setOrientation(LinearLayout.HORIZONTAL);
                                l2h[j].setBackgroundColor(Color.parseColor("#ffffff"));
                                LinearLayout.LayoutParams paramsHoriz = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 150);
                                //  paramsHoriz.weight = 1.0f;
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(intwidth / 3, LinearLayout.LayoutParams.MATCH_PARENT);
                                // params.setMargins(0, 10, 0, 10);

//-----------------------------------------------------------------------------------------------------------
                                final TextView tline1 = new TextView(this);
                                LinearLayout.LayoutParams paramsx = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);

                                paramsx.setMargins(0, 5, 0, 5);

                                final TextView dummy1 = new TextView(this);
                                LinearLayout.LayoutParams paramsxy = new LinearLayout.LayoutParams(1, 1);
                                dummy1.setTag(data.getJSONObject(j));
                                dummy1.setLayoutParams(paramsxy);
                                tline1.setLayoutParams(paramsx);
                                tline1.setGravity(Gravity.CENTER);
                                tline1.setTag(j);
                                l2h[j].addView(tline1);
                                l2h[j].addView(dummy1);

                                l2h[j].setLayoutParams(paramsHoriz);

//------------------------------------------------------------------------------------------------------------------------------
                                tuk[j] = new TextView(this);
                                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(intwidth / 3, LinearLayout.LayoutParams.MATCH_PARENT);
                                //  params2.setMargins(0, 10, 0, 10);
                                tuk[j].setLayoutParams(params2);
                                ///  tuk[i].setPadding(0, 10, 0, 10);
                                tuk[j].setText(data.getJSONObject(j).getString("uk_size"));
                                tuk[j].setGravity(Gravity.CENTER);
                                tuk[j].setTextSize(14);
                                l2h[j].addView(tuk[j]);

                                l2h[j].setLayoutParams(paramsHoriz);
//----------------------------------------------------------------------------------
                                tus[j] = new TextView(this);
                                LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(intwidth / 3, LinearLayout.LayoutParams.MATCH_PARENT);
                                // params3.setMargins(0, 10, 0, 10);
                                tus[j].setLayoutParams(params3);
                                // tus[i].setPadding(0, 10, 0, 10);
                                tus[j].setText(data.getJSONObject(j).getString("us_size"));
                                tus[j].setTextSize(14);
                                tus[j].setGravity(Gravity.CENTER);
                                l2h[j].addView(tus[j]);

                                l2h[j].setLayoutParams(paramsHoriz);
//---------------------------------------------------------------------------------------
                                teu[j] = new TextView(this);
                                LinearLayout.LayoutParams params4 = new LinearLayout.LayoutParams(intwidth / 3, LinearLayout.LayoutParams.MATCH_PARENT);
                                // params4.setMargins(0, 10, 0, 10);
                                teu[j].setLayoutParams(params4);
                                // teu[i].setPadding(0, 10, 0, 10);
                                teu[j].setText(data.getJSONObject(j).getString("eu_size"));
                                teu[j].setGravity(Gravity.CENTER);
                                teu[j].setTextSize(14);
                                l2h[j].addView(teu[j]);

                                l2h[j].setLayoutParams(paramsHoriz);
//---------------------------------------------------------------------------------------
                                l1p.addView(l2h[j]);
                                tuk[j].setBackgroundColor(Color.parseColor("#ffffff"));
                                tus[j].setBackgroundColor(Color.parseColor("#ffffff"));
                                teu[j].setBackgroundColor(Color.parseColor("#ffffff"));
                                //////System.out.println("test===" + upload.selectedSize);
                                if (upload.selectedSize.size() > 0) {
                                    if (struser.equals("zap_user")) {
                                        for (int i = 0; i < upload.selectedSize.size(); i++) {
                                            if (data.getJSONObject(j).getInt("id") == Integer.parseInt(upload.selectedSize.get(i))) {
                                                if (upload.selectedSizeType.contains("UK")) {

                                                    l2h[j].setBackgroundColor(Color.parseColor("#FFCDD2"));
                                                    tuk[j].setBackgroundColor(Color.parseColor("#ff7477"));
                                                    tuk[j].setText(tuk[j].getText().toString());
                                                    selectedSizes.add("UK " + data.getJSONObject(j).getString("uk_size"));
                                                    selectedid.add(String.valueOf(upload.selectedSize.get(i)));
                                                    selectedqty.add("UK " + String.valueOf(upload.selectedSize.get(i)) + "-" + String.valueOf(upload.selectedQty.get(i)));

                                                } else if (upload.selectedSizeType.contains("US")) {
                                                    l2h[j].setBackgroundColor(Color.parseColor("#FFCDD2"));
                                                    tus[j].setBackgroundColor(Color.parseColor("#ff7477"));
                                                    tus[j].setText(tus[j].getText().toString());
                                                    selectedSizes.add("US " + data.getJSONObject(j).getString("us_size"));
                                                    selectedid.add(String.valueOf(upload.selectedSize.get(i)));
                                                    selectedqty.add("US " + String.valueOf(upload.selectedSize.get(i)) + "-" + String.valueOf(upload.selectedQty.get(i)));

                                                } else {
                                                    l2h[j].setBackgroundColor(Color.parseColor("#FFCDD2"));
                                                    teu[j].setBackgroundColor(Color.parseColor("#ff7477"));
                                                    teu[j].setText(teu[j].getText().toString());
                                                    selectedSizes.add("EU " + data.getJSONObject(j).getString("eu_size"));
                                                    selectedid.add(String.valueOf(upload.selectedSize.get(i)));
                                                    selectedqty.add("EU " + String.valueOf(upload.selectedSize.get(i)) + "-" + String.valueOf(upload.selectedQty.get(i)));

                                                }
                                            }
                                        }
                                    } else {
                                        for (int i = 0; i < upload.selectedSize.size(); i++) {
                                            if (data.getJSONObject(j).getInt("id") == Integer.parseInt(upload.selectedSize.get(i))) {
                                                if (upload.selectedSizeType.contains("UK")) {

                                                    l2h[j].setBackgroundColor(Color.parseColor("#FFCDD2"));
                                                    tuk[j].setBackgroundColor(Color.parseColor("#ff7477"));
                                                    tuk[j].setText(tuk[j].getText().toString() + "-(" + upload.selectedQty.get(i) + ")");
                                                    selectedSizes.add("UK " + data.getJSONObject(j).getString("uk_size"));
                                                    selectedid.add(String.valueOf(upload.selectedSize.get(i)));
                                                    selectedqty.add("UK " + String.valueOf(upload.selectedSize.get(i)) + "-" + String.valueOf(upload.selectedQty.get(i)));

                                                } else if (upload.selectedSizeType.contains("US")) {
                                                    l2h[j].setBackgroundColor(Color.parseColor("#FFCDD2"));
                                                    tus[j].setBackgroundColor(Color.parseColor("#ff7477"));
                                                    tus[j].setText(tus[j].getText().toString() + "-(" + upload.selectedQty.get(i) + ")");
                                                    selectedSizes.add("US " + data.getJSONObject(j).getString("us_size"));
                                                    selectedid.add(String.valueOf(upload.selectedSize.get(i)));
                                                    selectedqty.add("US " + String.valueOf(upload.selectedSize.get(i)) + "-" + String.valueOf(upload.selectedQty.get(i)));

                                                } else {
                                                    l2h[j].setBackgroundColor(Color.parseColor("#FFCDD2"));
                                                    teu[j].setBackgroundColor(Color.parseColor("#ff7477"));
                                                    teu[j].setText(teu[j].getText().toString() + "-(" + upload.selectedQty.get(i) + ")");
                                                    selectedSizes.add("EU " + data.getJSONObject(j).getString("eu_size"));
                                                    selectedid.add(String.valueOf(upload.selectedSize.get(i)));
                                                    selectedqty.add("EU " + String.valueOf(upload.selectedSize.get(i)) + "-" + String.valueOf(upload.selectedQty.get(i)));

                                                }
                                            }
                                        }
                                    }
                                }
                                tuk[j].setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (struser.equals("zap_user")) {
                                            selectedid.clear();
                                            selectedSizes.clear();
                                            selectedqty.clear();
                                            JSONObject dummyTag = (JSONObject) dummy1.getTag();
                                            try {
                                                String dummytagstring = dummyTag.getString("uk_size");
                                                selectedSizes.add("UK " + dummytagstring);
                                                selectedqty.add("UK " + dummyTag.getInt("id") + "-1");
                                                selectedid.add(String.valueOf(dummyTag.getInt("id")));
                                                //////System.out.println(String.valueOf(dummyTag.getInt("id")));

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }


                                            for (int i = 0; i < data.length(); i++) {
                                                l2h[i].setBackgroundColor(Color.WHITE);
                                                tus[i].setBackgroundColor(Color.TRANSPARENT);
                                                tuk[i].setBackgroundColor(Color.TRANSPARENT);
                                                teu[i].setBackgroundColor(Color.TRANSPARENT);
                                            }

                                            l2h[(int) tline1.getTag()].setBackgroundColor(Color.parseColor("#FFCDD2"));
                                            tuk[(int) tline1.getTag()].setBackgroundColor(Color.parseColor("#ff7477"));


                                        } else {

                                            JSONObject dummyTag = (JSONObject) dummy1.getTag();
                                            try {
                                                String dummytagstring = dummyTag.getString("uk_size");
                                                selectedSizes.add("UK " + dummytagstring);
                                                if (!selectedid.contains(String.valueOf(dummyTag.getInt("id")))) {
                                                    selectedid.add(String.valueOf(dummyTag.getInt("id")));
                                                    //////System.out.println(String.valueOf("cxcxcxc"+dummyTag.getInt("id")));

                                                } else {
                                                    tus[(int) tline1.getTag()].setBackgroundColor(Color.TRANSPARENT);
                                                    teu[(int) tline1.getTag()].setBackgroundColor(Color.TRANSPARENT);
                                                    selectedSizes.remove("US " + dummyTag.getString("us_size"));
                                                    selectedSizes.remove("EU " + dummyTag.getString("eu_size"));
                                                }

                                                l2h[(int) tline1.getTag()].setBackgroundColor(Color.parseColor("#FFCDD2"));
                                                tuk[(int) tline1.getTag()].setBackgroundColor(Color.parseColor("#ff7477"));
                                                intj = (int) tline1.getTag();
                                                CustomAlert ct = new CustomAlert(ctx, "UK " + dummyTag.getInt("id") + "-", String.valueOf(dummyTag.getInt("id")));
                                                //  tuk[(int) tline1.getTag()].setText(tuk[(int) tline1.getTag()].getText().toString()+"-"+intqty);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }


                                    }
                                });
                                tus[j].setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (struser.equals("zap_user")) {
                                            selectedid.clear();
                                            selectedSizes.clear();
                                            selectedqty.clear();
                                            JSONObject dummyTag = (JSONObject) dummy1.getTag();
                                            try {
                                                String dummytagstring = dummyTag.getString("us_size");
                                                selectedSizes.add("US " + dummytagstring);
                                                selectedid.add(String.valueOf(dummyTag.getInt("id")));
                                                selectedqty.add("US " + dummyTag.getInt("id") + "-1");
                                                //////System.out.println(String.valueOf(dummyTag.getInt("id")));

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            for (int i = 0; i < data.length(); i++) {
                                                l2h[i].setBackgroundColor(Color.WHITE);
                                                tus[i].setBackgroundColor(Color.TRANSPARENT);
                                                tuk[i].setBackgroundColor(Color.TRANSPARENT);
                                                teu[i].setBackgroundColor(Color.TRANSPARENT);
                                            }


                                            l2h[(int) tline1.getTag()].setBackgroundColor(Color.parseColor("#FFCDD2"));
                                            tus[(int) tline1.getTag()].setBackgroundColor(Color.parseColor("#ff7477"));

                                        } else {
//                                            selectedSizes.clear();
                                            JSONObject dummyTag = (JSONObject) dummy1.getTag();
                                            try {
                                                String dummytagstring = dummyTag.getString("us_size");
                                                selectedSizes.add("US " + dummytagstring);
                                                if (!selectedid.contains(String.valueOf(dummyTag.getInt("id")))) {
                                                    selectedid.add(String.valueOf(dummyTag.getInt("id")));
                                                    //////System.out.println(String.valueOf("cxcxcxc"+dummyTag.getInt("id")));

                                                } else {
                                                    teu[(int) tline1.getTag()].setBackgroundColor(Color.TRANSPARENT);
                                                    tuk[(int) tline1.getTag()].setBackgroundColor(Color.TRANSPARENT);
                                                    selectedSizes.remove("EU " + dummyTag.getString("eu_size"));
                                                    selectedSizes.remove("UK " + dummyTag.getString("uk_size"));
                                                }

                                                l2h[(int) tline1.getTag()].setBackgroundColor(Color.parseColor("#FFCDD2"));
                                                tus[(int) tline1.getTag()].setBackgroundColor(Color.parseColor("#ff7477"));
                                                intj = (int) tline1.getTag();
                                                CustomAlert ct = new CustomAlert(ctx, "US " + dummyTag.getInt("id") + "-", String.valueOf(dummyTag.getInt("id")));
                                                // tus[(int) tline1.getTag()].setText(tus[(int) tline1.getTag()].getText().toString()+"-"+intqty);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });
                                teu[j].setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        intselectedTag = Integer.parseInt(tline1.getTag().toString());
                                        if (struser.equals("zap_user")) {
                                            selectedid.clear();
                                            selectedSizes.clear();
                                            selectedqty.clear();
                                            JSONObject dummyTag = (JSONObject) dummy1.getTag();
                                            try {
                                                String dummytagstring = dummyTag.getString("eu_size");
                                                selectedSizes.add("EU " + dummytagstring);
                                                selectedid.add(String.valueOf(dummyTag.getInt("id")));
                                                selectedqty.add("EU " + dummyTag.getInt("id") + "-1");
                                                ////////System.out.println(String.valueOf(dummyTag.getInt("id")));

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            for (int i = 0; i < data.length(); i++) {
                                                l2h[i].setBackgroundColor(Color.WHITE);
                                                tus[i].setBackgroundColor(Color.TRANSPARENT);
                                                tuk[i].setBackgroundColor(Color.TRANSPARENT);
                                                teu[i].setBackgroundColor(Color.TRANSPARENT);
                                            }


                                            l2h[(int) tline1.getTag()].setBackgroundColor(Color.parseColor("#FFCDD2"));
                                            teu[(int) tline1.getTag()].setBackgroundColor(Color.parseColor("#ff7477"));
                                        } else {
//                                            selectedSizes.clear();
                                            JSONObject dummyTag = (JSONObject) dummy1.getTag();
                                            try {
                                                String dummytagstring = dummyTag.getString("eu_size");

                                                selectedSizes.add("EU " + dummytagstring);
                                                if (!selectedid.contains(String.valueOf(dummyTag.getInt("id")))) {
                                                    selectedid.add(String.valueOf(dummyTag.getInt("id")));
                                                    //////System.out.println(String.valueOf("cxcxcxc"+dummyTag.getInt("id")));

                                                } else {
                                                    tus[(int) tline1.getTag()].setBackgroundColor(Color.TRANSPARENT);
                                                    tuk[(int) tline1.getTag()].setBackgroundColor(Color.TRANSPARENT);
                                                    selectedSizes.remove("US " + dummyTag.getString("us_size"));
                                                    selectedSizes.remove("UK " + dummyTag.getString("uk_size"));
                                                }

                                                l2h[(int) tline1.getTag()].setBackgroundColor(Color.parseColor("#FFCDD2"));
                                                teu[(int) tline1.getTag()].setBackgroundColor(Color.parseColor("#ff7477"));
                                                intj = (int) tline1.getTag();
                                                CustomAlert ct = new CustomAlert(ctx, "EU " + dummyTag.getInt("id") + "-", String.valueOf(dummyTag.getInt("id")));
                                                // teu[(int) tline1.getTag()].setText(teu[(int) tline1.getTag()].getText().toString()+"-"+intqty);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });
                            }

                        }
                        progressBar.setVisibility(View.GONE);

                    } else {
                        progressBar.setVisibility(View.GONE);
                         CustomMessage.getInstance().CustomMessage(SizeguideActivity.this, "Oops. Something went wrong!");
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                     CustomMessage.getInstance().CustomMessage(SizeguideActivity.this, "Oops. Something went wrong!");
                }

            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }
    @Override
    public void onErrorCallback(VolleyError error, String flag) {
        progressBar.setVisibility(View.GONE);
         CustomMessage.getInstance().CustomMessage(SizeguideActivity.this, "Oops. Something went wrong!");
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        if(!GetSharedValues.GetgcmId(SizeguideActivity.this).equals("")) {
            ApiService.getInstance(SizeguideActivity.this, 1).getData(SizeguideActivity.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/?gcm_reg_id="+ GetSharedValues.GetgcmId(SizeguideActivity.this), "session");
        }
        else {
            ApiService.getInstance(SizeguideActivity.this, 1).getData(SizeguideActivity.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/", "session");
        }
    }
}

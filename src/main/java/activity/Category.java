package activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
//import com.uxcam.UXCam;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import application.MyApplicationClass;
import network.ApiCommunication;
import network.ApiService;
import utils.CustomMessage;
import utils.GetSharedValues;

//import com.//Appsee.////Appsee;

public class Category extends AppCompatActivity implements ApiCommunication {
    String SCREEN_NAME = "CATEGORY";
    LinearLayout l1p;
    //   TextView tvnext;

    LinearLayout[] l2h, l3h, l2p;
    int intwidth, inthieght;
    int intcheckradio;
    String[] strsubcategory;
    int intln = 0;
    int intcheckd = 0;
    JSONArray jsSub;
    ImageView Skip;
    ProgressBar progressBar;

    ScrollView sv;
    Tracker mTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category);

        MyApplicationClass application = (MyApplicationClass) getApplication();
        mTracker = application.getDefaultTracker();

        //////Thread.currentThread().setDefaultUncaughtExceptionHandler(new SplashScreen.MyUncaughtExceptionHandler());
        progressBar = (ProgressBar) findViewById(R.id.category_progressBar);
        //UXCam.startWithKey("1dfb25141864376");         overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        ////Appsee.start("4fb55809ab45411c909521bda720f8e6");

        l1p = (LinearLayout) findViewById(R.id.l1p);
        //tvnext=(TextView)findViewById(R.id.next);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        intwidth = metrics.widthPixels;
        inthieght = metrics.heightPixels;
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final View customView = getLayoutInflater().inflate(R.layout.product_bar, null);
        getSupportActionBar().setCustomView(customView);
        Toolbar parent = (Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0, 0);
        TextView tv_headTitle = (TextView) findViewById(R.id.product_title_text);
        tv_headTitle.setText("CATEGORY");
        sv = (ScrollView) findViewById(R.id.scv);

        Skip = (ImageView) findViewById(R.id.productfeedButton);
        Skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if(Upload1.data.length() > 0){
            DisplayValues(Upload1.data);
        }
        else {
            Getcategory();
        }
    }

    //    Server request
//    --------------------------------------------------------------
    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    private void Getcategory() {
        progressBar.setVisibility(View.VISIBLE);
        ApiService.getInstance(Category.this, 1).getData(Category.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/onboarding/getcategories/", "getcategory");
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
    protected void onRestart() {
        super.onRestart();
        if(!GetSharedValues.GetgcmId(Category.this).equals("")) {
            ApiService.getInstance(Category.this, 1).getData(Category.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/?gcm_reg_id="+GetSharedValues.GetgcmId(Category.this), "session");
        }
        else {
            ApiService.getInstance(Category.this, 1).getData(Category.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/", "session");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.set("UserName", GetSharedValues.getUsername(getApplicationContext()));mTracker.set("ZapUsername", GetSharedValues.getZapname(getApplicationContext()));   mTracker.setScreenName("Category");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }


    //    Displaying values
//    -------------------------------------------------------------

    public void DisplayValues(final JSONArray data) {



        l2h = new LinearLayout[data.length()];
        l2p = new LinearLayout[data.length()];

        for (int i = 0; i < data.length(); i++) {
            ////////System.out.println("ddsdsdsd " + data.getJSONObject(i).getString("name"));
            JSONObject js1 = null;
            try {
                js1 = data.getJSONObject(i);

                jsSub = js1.getJSONArray("sub_cats");
                l2h[i] = new LinearLayout(this);
                l2h[i].setOrientation(LinearLayout.HORIZONTAL);
                final TextView t1 = new TextView(this);
                ImageView img1 = new ImageView(this);

                LinearLayout.LayoutParams paramsHoriz = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                paramsHoriz.weight = 1.0f;

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);

                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(100, LinearLayout.LayoutParams.MATCH_PARENT);

//                try {
//                    img1.setImageResource(id[i]);
//                }catch (ArrayIndexOutOfBoundsException e){
//                    img1.setImageResource(id[1]);
//                }
                img1.setVisibility(View.GONE);

                params1.setMargins(50, 20, 30, 20);
                params.setMargins(20, 10, 20, 10);
                img1.setLayoutParams(params1);
                t1.setTextColor(Color.BLACK);
                t1.setGravity(Gravity.CENTER_VERTICAL);
                t1.setTextSize(15);
                t1.setText(data.getJSONObject(i).getString("name"));
                t1.setTag(data.getJSONObject(i).getInt("id"));
                t1.setLayoutParams(params);
                t1.setTag(i);
                l2h[i].addView(img1);
                l2h[i].addView(t1);
                l1p.addView(l2h[i]);

                l2p[i] = new LinearLayout(this);
                l2p[i].setOrientation(LinearLayout.VERTICAL);
                int intlength = jsSub.length() + data.length();
                ////////System.out.println("fdff"+intlength);

                for (int j = 0; j < jsSub.length(); j++) {
                    ////////System.out.println("vv" + jsSub.getJSONObject(j).getString("name"));
                    final LinearLayout l3h = new LinearLayout(this);
                    l3h.setOrientation(LinearLayout.HORIZONTAL);


                    LinearLayout.LayoutParams paramsHoriz1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    paramsHoriz1.weight = 1.0f;
                    LinearLayout.LayoutParams paramsx = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    LinearLayout.LayoutParams paramsx1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    l3h.setLayoutParams(paramsHoriz1);
                    paramsHoriz1.weight = 1.0f;


                    paramsx1.setMargins(200, 25, 30, 20);
                    //---------------------------------
                    paramsx.setMargins(20, 10, 20, 10);
                    final ImageView rd = new ImageView(this);
                    rd.setLayoutParams(paramsx1);
                    final TextView tsub = new TextView(this);

                    tsub.setTextColor(Color.BLACK);
                    tsub.setGravity(Gravity.CENTER_VERTICAL);

                    tsub.setTextSize(12);
                    tsub.setText(jsSub.getJSONObject(j).getString("name"));
                    tsub.setTag(jsSub.getJSONObject(j).getInt("id"));
                    tsub.setLayoutParams(paramsx);
                    rd.setImageResource(R.drawable.greycircle);


                    ////////System.out.println("zzzzz" + intln);
                    l3h.setTag(data.getJSONObject(i).getString("category_type"));
                    l3h.addView(rd);
                    l3h.addView(tsub);

                    l2p[i].addView(l3h);
                    // tsub1.setVisibility(View.GONE);
                    intln = intln + 1;
                    l3h.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rd.setImageResource(R.drawable.greencircle);
                            Intent newintent = new Intent(getApplicationContext(), Upload1.class);
                            newintent.putExtra("subcatid", Integer.parseInt(tsub.getTag().toString()));
                            newintent.putExtra("subcatname", tsub.getText().toString());
                            newintent.putExtra("catType", l3h.getTag().toString());
                            setResult(RESULT_OK, newintent);
                            finish();
                        }
                    });
                    rd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ////////System.out.println("bbbbbbbbbbb" + tsub.getTag().toString() + "ll" + intln);
                            rd.setImageResource(R.drawable.greencircle);

                            ////////System.out.println("bbbbbbbbbbb" + tsub.getText().toString() + "ll" + intln);
                            Intent newintent = new Intent(getApplicationContext(), Upload1.class);
                            newintent.putExtra("subcatid", Integer.parseInt(tsub.getTag().toString()));
                            newintent.putExtra("subcatname", tsub.getText().toString());
                            newintent.putExtra("catType", l3h.getTag().toString());
                            setResult(RESULT_OK, newintent);
                            finish();

                        }
                    });


                }
                ////////System.out.println("fdff");

                l2p[i].setVisibility(View.GONE);
                l2p[i].setTag(0);
                l1p.addView(l2p[i]);

                l2h[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (l2p[Integer.parseInt(t1.getTag().toString())].getTag().equals(0)) {
                            for (int m = 0; m < data.length(); m++) {
                                ////////System.out.println("mval "+m +"t1 "+t1.getTag().toString() );
                                if (Integer.parseInt(t1.getTag().toString()) != m) {
                                    l2p[m].setVisibility(View.GONE);
                                    l2p[m].setTag(0);
                                } else {
                                    l2p[m].setVisibility(View.VISIBLE);
                                    l2p[m].setTag(1);
                                    sendScroll();
                                }
                            }
                        } else {
                            l2p[Integer.parseInt(t1.getTag().toString())].setVisibility(View.GONE);
                            l2p[Integer.parseInt(t1.getTag().toString())].setTag(0);
                        }

                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        progressBar.setVisibility(View.GONE);
    }


    @Override
    public void onResponseCallback(JSONObject response, String flag) {

        //////////System.out.println("response server" + response);

        if (flag.equals("getcategory")) {
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                try {

                    String status = resp.getString("status");
                    if (status.equals("success")) {
//                        //////System.out.println("");
                        Upload1.data = resp.getJSONArray("data");
                        DisplayValues(Upload1.data);

                    } else {
                        progressBar.setVisibility(View.GONE);
                         CustomMessage.getInstance().CustomMessage(Category.this, "Oops. Something went wrong!");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                     CustomMessage.getInstance().CustomMessage(Category.this, "Oops. Something went wrong!");
                }

            }
        }

    }
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }
    @Override
    public void onErrorCallback(VolleyError error, String flag) {
        progressBar.setVisibility(View.GONE);
         CustomMessage.getInstance().CustomMessage(Category.this, "Oops. Something went wrong!");
    }
}
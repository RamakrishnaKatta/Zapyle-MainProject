package activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import adapters.ColorAdaptor;
import application.MyApplicationClass;
import models.ColorData;
import network.ApiCommunication;
import network.ApiService;
import utils.CustomMessage;
import utils.FontUtils;
import utils.GetSharedValues;

//import com.uxcam.UXCam;

//import com.//Appsee.////Appsee;

/**
 * Created by haseeb on 22/1/16.
 */
public class Colors extends ActionBarActivity implements ApiCommunication, ColorAdaptor.customButtonListener{

    public String SCREEN_NAME = "COLORS";

    ProgressBar progressBar;

    ArrayList<String> color = new ArrayList<String>();
    ArrayList<String> colorCode = new ArrayList<String>();
    ArrayList<Integer> colorId = new ArrayList<Integer>();
    ColorAdaptor adaptor;
    LinearLayout color_layout;
    GridView gridView;
    Tracker mTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.color);
        MyApplicationClass application = (MyApplicationClass) getApplication();
        mTracker = application.getDefaultTracker();
        //UXCam.startWithKey("1dfb25141864376");         overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        ////Appsee.start("4fb55809ab45411c909521bda720f8e6");
        //////Thread.currentThread().setDefaultUncaughtExceptionHandler(new SplashScreen.MyUncaughtExceptionHandler());
        progressBar = (ProgressBar) findViewById(R.id.color_progressBar);
//        color_layout = (LinearLayout) findViewById(R.id.color_layout);
        gridView = (GridView) findViewById(R.id.grid_colors);
        if(upload.colorDatas.size() > 0){
            DisplayValues();
        }
        else {
            GetColors();
        }

//        DisplayMetrics displaymetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
//        width = displaymetrics.widthPixels;
//
//
//        no_products = ((width - 200) / 300);
//        //////////System.out.println("buttonwidth__" + no_products);


        View view = findViewById(R.id.brandlistlayout);
        FontUtils.setCustomFont(view, getAssets());
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final View customView = getLayoutInflater().inflate(R.layout.product_bar, null);
        getSupportActionBar().setCustomView(customView);
        Toolbar parent = (Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0, 0);

        TextView title = (TextView) findViewById(R.id.product_title_text);
        title.setText("COLOR");

        ImageView Skip = (ImageView) findViewById(R.id.productfeedButton);
        Skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });




    }


//    Display functions
//    --------------------------------------------------------------------


    public void DisplayValues() {
        for (int i = 0; i < color.size(); i++) {
            ColorData colorData = new ColorData();
            colorData.setColor(color.get(i));
            colorData.setColorCode(colorCode.get(i));
            colorData.setId(colorId.get(i));

            upload.colorDatas.add(colorData);
        }


        adaptor = new ColorAdaptor(Colors.this, upload.colorDatas);
        adaptor.setCustomButtonListner(Colors.this);
        gridView.setAdapter(adaptor);
        gridView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);


        progressBar.setVisibility(View.GONE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.set("UserName", GetSharedValues.getUsername(getApplicationContext()));mTracker.set("ZapUsername", GetSharedValues.getZapname(getApplicationContext()));   mTracker.setScreenName("Colors");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }


    //    Click listener
//    -----------------------------------------------------------------------

    @Override
    public void onButtonClickListner(int position, Integer colorid, String color, View v, ColorAdaptor.MyColorViewHolder viewHolder) {
        ////////System.out.println(colorid+"__jdjdj_"+color);
        Intent colorIntent = new Intent(Colors.this, upload.class);
        colorIntent.putExtra("color", color);
        colorIntent.putExtra("colorid", colorid);
        setResult(RESULT_OK, colorIntent);
        finish();
    }



//    Server requests
//    ----------------------------------------------------------------------

    private void GetColors() {
        progressBar.setVisibility(View.VISIBLE);
        ApiService.getInstance(Colors.this, 1).getData(Colors.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/onboarding/getcolors/", "getcolors");

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(!GetSharedValues.GetgcmId(Colors.this).equals("")) {
            ApiService.getInstance(Colors.this, 1).getData(Colors.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/?gcm_reg_id="+ GetSharedValues.GetgcmId(Colors.this), "session");
        }
        else {
            ApiService.getInstance(Colors.this, 1).getData(Colors.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/", "session");
        }
    }


//    server responses
//    -----------------------------------------------------------------------

    @Override
    public void onResponseCallback(JSONObject response, String flag) {
        ////////System.out.println(response);
        if (flag.equals("getcolors")) {
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        JSONArray data = resp.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            color.add(data.getJSONObject(i).getString("color"));
                            colorCode.add(data.getJSONObject(i).getString("code"));
                            colorId.add(data.getJSONObject(i).getInt("id"));
                        }
                        DisplayValues();
                    } else {
                        progressBar.setVisibility(View.GONE);
                         CustomMessage.getInstance().CustomMessage(Colors.this, "Oops. Something went wrong!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                     CustomMessage.getInstance().CustomMessage(Colors.this, "Oops. Something went wrong!");
                }
            } else {
                progressBar.setVisibility(View.GONE);
                 CustomMessage.getInstance().CustomMessage(Colors.this, "Oops. Something went wrong!");
            }
        }
    }

    @Override
    public void onErrorCallback(VolleyError error, String flag) {

    }
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

}

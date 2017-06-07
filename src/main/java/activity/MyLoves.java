package activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
//import com.//Appsee.//Appsee;
//import com.uxcam.UXCam;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import adapters.MyLoveAdaptor;
import models.ProfileData;
import network.ApiCommunication;
import network.ApiService;
import utils.CustomMessage;
import utils.FontUtils;
import utils.ExternalFunctions;
import utils.GetSharedValues;

/**
 * Created by haseeb on 24/1/16.
 */
public class MyLoves extends ActionBarActivity implements ApiCommunication {
    public String SCREEN_NAME = "MYLOVES";

    ProgressBar progressBar;

    ArrayList<ProfileData> posts = new ArrayList<ProfileData>();
    MyLoveAdaptor adaptor;
    GridView gridView;
    public static int screenHeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myloves);
        //UXCam.startWithKey("1dfb25141864376");
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        JSONObject screenSize = ExternalFunctions.displaymetrics(MyLoves.this);
        screenHeight = screenSize.optInt("width");
        progressBar = (ProgressBar) findViewById(R.id.myloves_progressBar);

        GetData();
        gridView = (GridView) findViewById(R.id.grid_loves);


        View view = findViewById(R.id.upload1_layout);
        FontUtils.setCustomFont(view, getAssets());

        ExternalFunctions.bloverlay=false;
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final View customView = getLayoutInflater().inflate(R.layout.product_bar, null);
        getSupportActionBar().setCustomView(customView);
        Toolbar parent = (Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0, 0);
        TextView tv_headTitle = (TextView) findViewById(R.id.product_title_text);
        tv_headTitle.setText("MY LOVES");


        ImageView Skip = (ImageView) findViewById(R.id.productfeedButton);
        Skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }



//    server requests
//    --------------------------------------------------------------

    private void GetData() {
        progressBar.setVisibility(View.VISIBLE);
        ApiService.getInstance(MyLoves.this, 1).getData(MyLoves.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/my_loved_products/", "getloveddata");

    }


    @Override
    public void onBackPressed() {

        Intent dintent = new Intent(MyLoves.this, Myaccountpage.class);
        startActivity(dintent);
        finish();
    }

//    server responses
//    --------------------------------------------------------------

    @Override
    public void onResponseCallback(JSONObject response, String flag) {
        //////System.out.println(response);
        if(flag.equals("getloveddata")){
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if(resp != null){
                try {
                    String status = resp.getString("status");
                    if(status.equals("success")){
                        JSONArray data = resp.getJSONArray("data");
                        if (data.length()>0) {
                            for (int i = 0; i < data.length(); i++) {
                                ProfileData profileData = new ProfileData();
                                profileData.setAlbumid(String.valueOf(data.getJSONObject(i).getInt("id")));
                                profileData.setImage(data.getJSONObject(i).getString("image"));
                                profileData.setSale(data.getJSONObject(i).getBoolean("sale"));
                                posts.add(profileData);
                            }
                            adaptor = new MyLoveAdaptor(this, posts);
                            gridView.setAdapter(adaptor);
                            progressBar.setVisibility(View.GONE);
                        }
                        else{
                            progressBar.setVisibility(View.GONE);
                            Intent mydialog = new Intent(MyLoves.this, AlertActivity.class);
                            //int imgid=R.drawable.alertlove;
                            String strmessage="WHY YOU NO LOVE?";
                           // mydialog.putExtra("imgid", imgid);
                            mydialog.putExtra("Message",strmessage);
                            mydialog.putExtra("Buttontext"," LET'S EXPLORE ");
                            mydialog.putExtra("tip","");
                            mydialog.putExtra("activity","BuyPage");
                            mydialog.putExtra("header","MY LOVES");
                            mydialog.putExtra("calling", "Myaccountpage");
                            startActivity(mydialog);
                            finish();

                        }
                    }
                    else {
                        progressBar.setVisibility(View.GONE);
                        Intent mydialog = new Intent(MyLoves.this, AlertActivity.class);
                        //int imgid=R.drawable.alertoop;
                        String strmessage="OOPS!";
                       // mydialog.putExtra("imgid", imgid);
                        mydialog.putExtra("Message",strmessage);
                        mydialog.putExtra("Buttontext"," RETRY ");
                        mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                        mydialog.putExtra("activity", "Myaccountpage");
                        mydialog.putExtra("calling", "Myaccountpage");
                        startActivity(mydialog);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                    Intent mydialog = new Intent(MyLoves.this, AlertActivity.class);
                    //int imgid=R.drawable.alertoop;
                    String strmessage="OOPS!";
                   // mydialog.putExtra("imgid", imgid);
                    mydialog.putExtra("Message",strmessage);
                    mydialog.putExtra("Buttontext"," RETRY ");
                    mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                    mydialog.putExtra("activity", "Myaccountpage");
                    mydialog.putExtra("calling", "Myaccountpage");
                    startActivity(mydialog);
                    finish();
                }
            }
            else {
                progressBar.setVisibility(View.GONE);
                Intent mydialog = new Intent(MyLoves.this, AlertActivity.class);
                //int imgid=R.drawable.alertoop;
                String strmessage="OOPS!";
               // mydialog.putExtra("imgid", imgid);
                mydialog.putExtra("Message",strmessage);
                mydialog.putExtra("Buttontext"," RETRY ");
                mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                mydialog.putExtra("activity", "Myaccountpage");
                mydialog.putExtra("calling", "Myaccountpage");
                startActivity(mydialog);
                finish();
            }
        }
    }

    @Override
    public void onErrorCallback(VolleyError error, String flag) {
        progressBar.setVisibility(View.GONE);
        Intent mydialog = new Intent(MyLoves.this, AlertActivity.class);
        //int imgid=R.drawable.alertoop;
        String strmessage="OOPS!";
       // mydialog.putExtra("imgid", imgid);
        mydialog.putExtra("Message",strmessage);
        mydialog.putExtra("Buttontext"," RETRY ");
        mydialog.putExtra("tip", "SOMETHING WENT WRONG");
        mydialog.putExtra("activity", "Myaccountpage");
        mydialog.putExtra("calling", "Myaccountpage");
        startActivity(mydialog);
        finish();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(!GetSharedValues.GetgcmId(MyLoves.this).equals("")) {
            ApiService.getInstance(MyLoves.this, 1).getData(MyLoves.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/?gcm_reg_id="+GetSharedValues.GetgcmId(MyLoves.this), "session");
        }
        else {
            ApiService.getInstance(MyLoves.this, 1).getData(MyLoves.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/", "session");
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        //overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }
    @Override
    protected void onDestroy() {

        super.onDestroy();

//        int count = gridView.getCount();
//        if (count > 0) {
//            //////System.out.println("inside on destroy if");
//            for (int i = 0; i < count; i++) {
//                ImageView v = (ImageView) gridView.getChildAt(i).findViewById(R.id.gimage2);
//                if (v != null) {
//                    if (v.getDrawable() != null) v.getDrawable().setCallback(null);
//                }
//            }
//        }
        Runtime.getRuntime().gc();

    }
}

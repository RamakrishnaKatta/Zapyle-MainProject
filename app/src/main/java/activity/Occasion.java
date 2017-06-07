package activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.exceptions.CleverTapMetaDataNotFoundException;
import com.clevertap.android.sdk.exceptions.CleverTapPermissionsNotSatisfied;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import adapters.OccasionAdaptor;
import models.OccasionData;
import network.ApiCommunication;
import network.ApiService;
import utils.CustomMessage;
import utils.ExternalFunctions;
import utils.FontUtils;

//import com.//Appsee.//Appsee;
//import com.uxcam.UXCam;

/**
 * Created by haseeb on 22/1/16.
 */
public class Occasion extends ActionBarActivity implements ApiCommunication, OccasionAdaptor.customButtonListener {

    public String SCREEN_NAME = "OCCASION";
    public ArrayList<String> occasionName = new ArrayList<String>();
    public ArrayList<Integer> occasionId = new ArrayList<Integer>();


    ListView listView;
    ArrayAdapter<String> adapter;

    OccasionAdaptor occasionAdaptor;
    ProgressBar progressBar;


    String stime = "";
    String endtime = "";
    CleverTapAPI cleverTap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.occasion);
        //Appsee.start("4fb55809ab45411c909521bda720f8e6");
        //UXCam.startWithKey("1dfb25141864376");         overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        //////Thread.currentThread().setDefaultUncaughtExceptionHandler(new SplashScreen.MyUncaughtExceptionHandler());
        progressBar = (ProgressBar) findViewById(R.id.occasion_progressBar);
        listView = (ListView) findViewById(R.id.occasion_list);

        stime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        try {
            cleverTap = CleverTapAPI.getInstance(getApplicationContext());
        } catch (CleverTapMetaDataNotFoundException e) {
            // thrown if you haven't specified your CleverTap Account ID or Token in your AndroidManifest.xml
        } catch (CleverTapPermissionsNotSatisfied e) {
            // thrown if you haven’t requested the required permissions in your AndroidManifest.xml
        }
        if (upload.occasionList.size() > 0) {
            occasionAdaptor = new OccasionAdaptor(Occasion.this, upload.occasionList);
            occasionAdaptor.setCustomButtonListner(Occasion.this);
            listView.setAdapter(occasionAdaptor);
            listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        } else {
            GetOccasions();
        }

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
        title.setText("OCCASION");

        ImageView Skip = (ImageView) findViewById(R.id.productfeedButton);
        Skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    //    Other functions
//    --------------------------------------------------------------------
    private void getDataInList() {
        for (int i = 0; i < occasionName.size(); i++) {
            OccasionData occasionData = new OccasionData();
            occasionData.setOccasion(occasionName.get(i));
            occasionData.setOccasionId(occasionId.get(i));
            upload.occasionList.add(occasionData);
        }


        occasionAdaptor = new OccasionAdaptor(Occasion.this, upload.occasionList);
        occasionAdaptor.setCustomButtonListner(Occasion.this);
        listView.setAdapter(occasionAdaptor);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);


        //////System.out.println("occasionList__" + occasionList.get(0).getOccasionId());
        progressBar.setVisibility(View.GONE);


//        progressBar.setVisibility(View.GONE);


    }


    @Override
    public void onButtonClickListner(int position, Integer occasionId, String occasion, View v, OccasionAdaptor.MyOccasionViewHolder holder) {
        holder.rb_select.setChecked(true);

        //////System.out.println(occasionId+"__jdjdj_"+occasion);
        Intent occasionIntent = new Intent(Occasion.this, upload.class);
        occasionIntent.putExtra("occasion", occasion);
        occasionIntent.putExtra("occasionid", occasionId);
        setResult(RESULT_OK, occasionIntent);
        finish();
    }


//    Server requests
//    ----------------------------------------------------------------------

    private void GetOccasions() {
        progressBar.setVisibility(View.VISIBLE);
        ApiService.getInstance(Occasion.this, 1).getData(Occasion.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/onboarding/getoccasions/", "getBrands");

    }


//

    @Override
    public void onResponseCallback(JSONObject response, String flag) {
        //////System.out.println(response);
        if (flag.equals("getBrands")) {
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        JSONArray data = resp.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            occasionName.add(data.getJSONObject(i).getString("occasion"));
                            occasionId.add(data.getJSONObject(i).getInt("id"));
                        }
                        //////System.out.println("occasionName____"+occasionName);
                        //////System.out.println("occasionId"+occasionId);
                        getDataInList();
                    } else {
                        progressBar.setVisibility(View.GONE);

                        CustomMessage.getInstance().CustomMessage(Occasion.this, "Oops. Something went wrong!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);

                    CustomMessage.getInstance().CustomMessage(Occasion.this, "Oops. Something went wrong!");
                }
            } else {
                progressBar.setVisibility(View.GONE);

                CustomMessage.getInstance().CustomMessage(Occasion.this, "Oops. Something went wrong!");
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
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    public void onErrorCallback(VolleyError error, String flag) {
        progressBar.setVisibility(View.GONE);

    }
}

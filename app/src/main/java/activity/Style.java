package activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import network.ApiCommunication;
import network.ApiService;
import utils.CustomMessage;
import utils.FontUtils;

//import com.//Appsee.//Appsee;
//import com.uxcam.UXCam;

/**
 * Created by haseeb on 23/1/16.
 */
public class Style extends ActionBarActivity implements ApiCommunication {

    //    HashMap<String,Integer> styleMap = new HashMap<String, Integer>();

    public String SCREEN_NAME = "STYLE";

    ProgressBar progressbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.style);
        //UXCam.startWithKey("1dfb25141864376");         overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        //////Thread.currentThread().setDefaultUncaughtExceptionHandler(new SplashScreen.MyUncaughtExceptionHandler());
        progressbar = (ProgressBar) findViewById(R.id.style_progressBar);
        if(upload.products.size() > 0){
            DisplayValues();
        }
        else {
            GetStyles();
        }
        //Appsee.start("4fb55809ab45411c909521bda720f8e6");
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
        title.setText("STYLE");

        ImageView Skip = (ImageView) findViewById(R.id.productfeedButton);
        Skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }


//    Display functions
//    ---------------------------------------------------------

    private void DisplayValues() {
        final LinearLayout mainLayout = (LinearLayout) findViewById(R.id.layout_add_style_layout);
        final RadioGroup radioGroup = new RadioGroup(this);

        for (int i = 0; i < upload.products.size(); i++) {
            LayoutInflater address_inflater = LayoutInflater.from(Style.this);
            final View view;
            view = address_inflater.inflate(R.layout.item_style_list, null, false);
            final RadioButton rSelect = (RadioButton) view.findViewById(R.id.styel_button);
            TextView rText = (TextView) view.findViewById(R.id.style_name);
            rText.setText(upload.products.get(i).toUpperCase());


            final int finalI = i;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //////System.out.println(products.get(finalI) + "----" + productsId.get(finalI));
//                    //////System.out.println(products.get(finalI)+"__jdjdj_"+roductsId.get(finalI));
                    rSelect.setChecked(true);
                    Intent styleIntent = new Intent(Style.this, upload.class);
                    styleIntent.putExtra("style", upload.products.get(finalI));
                    styleIntent.putExtra("styleid", upload.productsId.get(finalI));
                    setResult(RESULT_OK, styleIntent);
                    finish();
                }
            });

            rSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //////System.out.println(products.get(finalI) + "----" + productsId.get(finalI));
//                    //////System.out.println(products.get(finalI)+"__jdjdj_"+roductsId.get(finalI));
                    rSelect.setChecked(true);
                    Intent styleIntent = new Intent(Style.this, upload.class);
                    styleIntent.putExtra("style", upload.products.get(finalI));
                    styleIntent.putExtra("styleid", upload.productsId.get(finalI));
                    setResult(RESULT_OK, styleIntent);
                    finish();
                }
            });
            radioGroup.addView(view);

        }
        mainLayout.addView(radioGroup);
        progressbar.setVisibility(View.GONE);

    }


//    Server requests
//    -----------------------------------------------------------

    private void GetStyles() {
        progressbar.setVisibility(View.VISIBLE);
        ApiService.getInstance(Style.this, 1).getData(Style.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/onboarding/getstyles/", "getstyles");

    }


//    Server responses
//    -------------------------------------------------------------

    @Override
    public void onResponseCallback(JSONObject response, String flag) {
        //////System.out.println(response);
        if (flag.equals("getstyles")) {
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        JSONArray data = resp.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            upload.products.add(data.getJSONObject(i).getString("style"));
                            upload.productsId.add(data.getJSONObject(i).getInt("id"));
                        }
                        DisplayValues();
                    } else {
                        progressbar.setVisibility(View.GONE);
                         CustomMessage.getInstance().CustomMessage(Style.this, "Oops. Something went wrong!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressbar.setVisibility(View.GONE);
                     CustomMessage.getInstance().CustomMessage(Style.this, "Oops. Something went wrong!");
                }
            } else {
                progressbar.setVisibility(View.GONE);
                 CustomMessage.getInstance().CustomMessage(Style.this, "Oops. Something went wrong!");
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
        progressbar.setVisibility(View.GONE);
         CustomMessage.getInstance().CustomMessage(Style.this, "Oops. Something went wrong!");
    }

    public static class searchactivity extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_searchactivity);
        }
    }
}

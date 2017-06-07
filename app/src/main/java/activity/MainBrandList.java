package activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import adapters.Pager;
import models.BrandModel;
import network.ApiCommunication;
import network.ApiService;
import network.JsonParser;
import utils.CustomMessage;

/**
 * Created by haseeb on 9/12/16.
 */

public class MainBrandList extends ActionBarActivity implements TabLayout.OnTabSelectedListener, ApiCommunication {

    public String SCREEN_NAME = "BRANDS";
    //This is our tablayout
    private TabLayout tabLayout;

    //This is our viewPager
    private ViewPager viewPager;
    ProgressBar progressBar;
    LinearLayout main_layout;

    ArrayList<BrandModel> indian = new ArrayList<BrandModel>();
    ArrayList<BrandModel> international = new ArrayList<BrandModel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_brand_list);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final View customView = getLayoutInflater().inflate(R.layout.product_bar, null);
        getSupportActionBar().setCustomView(customView);
        Toolbar parent = (Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0, 0);
        TextView tv_title = (TextView) findViewById(R.id.product_title_text);
        tv_title.setText("Brands");
        ImageView iv_back = (ImageView) findViewById(R.id.productfeedButton);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });


        main_layout = (LinearLayout) findViewById(R.id.main_layout);
        main_layout.setVisibility(View.INVISIBLE);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        String url = "/catalogue/lists/brand/shop/designer";
        getData(url, "getindian");
        //Initializing the tablayout
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setTabTextColors(Color.parseColor("#BDBDBD"), Color.BLACK);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //Initializing viewPager
        viewPager = (ViewPager) findViewById(R.id.pager);
        tabLayout.setOnTabSelectedListener(this);


    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


    public void getData(String url, String flag) {
        ApiService.getInstance(MainBrandList.this, 1).getData(MainBrandList.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + url, flag);
    }


    @Override
    public void onResponseCallback(JSONObject response, String flag) {

        JSONObject resp = JsonParser.getInstance().parserJsonObject(response);
        if (flag.equals("getindian")) {
            if (resp.optString("status").equals("success")) {
                JSONArray data = resp.optJSONArray("data");
                indian.clear();
                for (int i = 0; i < data.length(); i++) {
                    BrandModel model = new BrandModel();
                    model.setName(data.optJSONObject(i).optString("name"));
                    model.setTarget(data.optJSONObject(i).optString("target"));
                    indian.add(model);
                }
                String url = "/catalogue/lists/brand/shop/brand";
                getData(url, "getinternational");
            } else {
                CustomMessage.getInstance().CustomMessage(this, "Oops. Something went wrong!");
            }
        } else if (flag.equals("getinternational")) {
            if (resp.optString("status").equals("success")) {
                JSONArray data = resp.optJSONArray("data");
                international.clear();
                for (int i = 0; i < data.length(); i++) {
                    BrandModel model = new BrandModel();
                    model.setName(data.optJSONObject(i).optString("name"));
                    model.setTarget(data.optJSONObject(i).optString("target"));
                    international.add(model);
                }
                Pager adapter;
                try {
                    if (getIntent().getStringExtra("type").contains("INDIAN")) {
                        adapter = new Pager(getSupportFragmentManager(), tabLayout.getTabCount(), indian, international,"INDIAN","INTERNATIONAL");
                    }
                    else {
                        adapter = new Pager(getSupportFragmentManager(), tabLayout.getTabCount(), indian, international,"INTERNATIONAL","INDIAN");
                    }
                } catch (Exception e) {
                    adapter = new Pager(getSupportFragmentManager(), tabLayout.getTabCount(), indian, international,"INDIAN","INTERNATIONAL");
                }
                //Adding adapter to pager
                viewPager.setAdapter(adapter);
                tabLayout.setupWithViewPager(viewPager);


                progressBar.setVisibility(View.GONE);
                main_layout.setVisibility(View.VISIBLE);

            } else {
                CustomMessage.getInstance().CustomMessage(this, "Oops. Something went wrong!");
            }
        }

    }

    @Override
    public void onErrorCallback(VolleyError error, String flag) {
        CustomMessage.getInstance().CustomMessage(this, "Oops. Something went wrong!");
    }
}

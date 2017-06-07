package activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.brandongogetap.stickyheaders.StickyLayoutManager;
import com.brandongogetap.stickyheaders.exposed.StickyHeaderListener;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import Viewholder.DividerItemDecoration;
import adapters.CategoryListAdapter;
import models.BrandHeaderItem;
import models.category_list_model;
import network.ApiCommunication;
import network.ApiService;
import network.JsonParser;
import utils.CustomMessage;

/**
 * Created by haseeb on 8/12/16.
 */

public class category_list extends ActionBarActivity implements ApiCommunication,View.OnClickListener {

    public String SCREEN_NAME = "CATEGORY_LIST";
    ProgressBar progress;
    LinearLayout sideLayout;
    RecyclerView recyclerview;
    CategoryListAdapter adapter;
    ArrayList<category_list_model> category_data = new ArrayList<category_list_model>();
    Map<String, Integer> mapIndex;
    String forwardurl="";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_list);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final View customView = getLayoutInflater().inflate(R.layout.product_bar, null);
        getSupportActionBar().setCustomView(customView);
        Toolbar parent = (Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0, 0);
        TextView tv_title = (TextView) findViewById(R.id.product_title_text);

        tv_title.setText("Categories");
        ImageView iv_back = (ImageView) findViewById(R.id.productfeedButton);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });
        progress = (ProgressBar) findViewById(R.id.category_progressBar);
//        sideLayout = (LinearLayout) findViewById(R.id.side_index);
        recyclerview = (RecyclerView) findViewById(R.id.list_catgories);
        progress = (ProgressBar) findViewById(R.id.catgorylist_progressBar);
        try {
          forwardurl=  getIntent().getStringExtra("ForwardUrl");
        }catch (Exception e){

        }

        getData();

    }

    public void getData() {
        progress.setVisibility(View.VISIBLE);
        ApiService.getInstance(category_list.this, 1).getData(category_list.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL +"/"+ forwardurl, "getCategory");
    }

    @Override
    public void onResponseCallback(JSONObject response, String flag) {
        if (flag.equals("getCategory")){
            System.out.println("CATEGORY LIST : "+response);
            JSONObject resp = JsonParser.getInstance().parserJsonObject(response);
            if (resp.optString("status").equals("success")){
                progress.setVisibility(View.GONE);
                parseObject(resp.optJSONArray("data"));
            }
            else {
                progress.setVisibility(View.GONE);
                CustomMessage.getInstance().CustomMessage(this, "Oops. Something went wrong!");
            }
        }

    }


    @Override
    public void onErrorCallback(VolleyError error, String flag) {
        System.out.println("CATEGORY LIST : error "+error);
        progress.setVisibility(View.GONE);
        CustomMessage.getInstance().CustomMessage(this, "Oops. Something went wrong!");
    }

    private void parseObject(JSONArray data) {
        category_data.clear();

        for (int i=0;i<data.length();i++){
            BrandHeaderItem model = new BrandHeaderItem();
            model.setName(data.optJSONObject(i).optString("name"));
            model.setTarget(data.optJSONObject(i).optString("target"));
            model.setType("category");
            model.setIndex(i);
            category_data.add(model);
            for (int j=0;j<data.optJSONObject(i).optJSONArray("subcategorys").length();j++){
                category_list_model submodel = new category_list_model();
                submodel.setName(data.optJSONObject(i).optJSONArray("subcategorys").optJSONObject(j).optString("name"));
                submodel.setTarget(data.optJSONObject(i).optJSONArray("subcategorys").optJSONObject(j).optString("target"));
                submodel.setType("subcategory");
                submodel.setIndex(j);
                category_data.add(submodel);
            }

        }


//        getIndexList(category_data);
//
//        displayIndex();
        adapter = new CategoryListAdapter(category_data, category_list.this);

        StickyLayoutManager layoutManager = new StickyLayoutManager(this, adapter);
        layoutManager.elevateHeaders(true); // Default elevation of 5dp
        // You can also specify a specific dp for elevation
//        layoutManager.elevateHeaders(10);
        recyclerview.setLayoutManager(layoutManager);
        layoutManager.setStickyHeaderListener(new StickyHeaderListener() {
            @Override
            public void headerAttached(View headerView, int adapterPosition) {
                Log.d("Listener", "Attached with position: " + adapterPosition);
            }

            @Override
            public void headerDetached(View headerView, int adapterPosition) {
                Log.d("Listener", "Detached with position: " + adapterPosition);
            }
        });
        recyclerview.addItemDecoration(
                new DividerItemDecoration(ContextCompat.getDrawable(getApplicationContext(),
                        R.drawable.item_decorator)));
        recyclerview.setAdapter(adapter);

    }

    private void displayIndex() {
        TextView textView;
        Map<String, Integer> map = new TreeMap<String, Integer>(mapIndex);
        List<String> indexList = new ArrayList<String>(map.keySet());
        for (String index : indexList) {
            textView = (TextView) getLayoutInflater().inflate(
                    R.layout.side_index_item, null);
            textView.setText(index);
            textView.setOnClickListener(category_list.this);
            sideLayout.addView(textView);
        }
        progress.setVisibility(View.GONE);
    }

    private void getIndexList(ArrayList<category_list_model> category_data) {
        mapIndex = new LinkedHashMap<String, Integer>();
        for (int i = 0; i < category_data.size(); i++) {
            String category = category_data.get(i).getName();
            String index = category.substring(0, 1);

            if (mapIndex.get(index) == null)
                mapIndex.put(index, i);
        }
    }

    @Override
    public void onClick(View v) {
        TextView selectedIndex = (TextView) v;
        recyclerview.scrollToPosition(mapIndex.get(selectedIndex.getText()));
    }
}
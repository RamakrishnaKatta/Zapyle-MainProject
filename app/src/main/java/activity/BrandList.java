package activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import application.MyApplicationClass;
import network.ApiCommunication;
import network.ApiService;
import utils.FontUtils;
import utils.GetSharedValues;

//import com.uxcam.UXCam;

//import com.//Appsee.////Appsee;


public class BrandList extends ActionBarActivity implements ApiCommunication {
    ListView listView ;
    ArrayAdapter<String> adapter;

    EditText inputSearch;
    public String SCREEN_NAME = "BRANDLIST";

    ProgressBar progressBar;
    RelativeLayout rltop;
    Tracker mTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand_list);
        MyApplicationClass application = (MyApplicationClass) getApplication();
        mTracker = application.getDefaultTracker();
        //UXCam.startWithKey("1dfb25141864376");         overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        ////Appsee.start("4fb55809ab45411c909521bda720f8e6");
        //////Thread.currentThread().setDefaultUncaughtExceptionHandler(new SplashScreen.MyUncaughtExceptionHandler());
        progressBar = (ProgressBar) findViewById(R.id.brand_progressBar);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        View view = findViewById(R.id.brandlistlayout);

        FontUtils.setCustomFont(view, getAssets());
        rltop=(RelativeLayout)findViewById(R.id.topLayout);
        rltop.setVisibility(View.INVISIBLE);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final View customView = getLayoutInflater().inflate(R.layout.product_bar, null);
        getSupportActionBar().setCustomView(customView);
        Toolbar parent = (Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0, 0);
        listView = (ListView) findViewById(R.id.list);
        inputSearch = (EditText) findViewById(R.id.inputSearch);
        TextView title = (TextView) findViewById(R.id.product_title_text);
        title.setText("BRAND");

        if(upload.brand_products.size() > 0){
            progressBar.setVisibility(View.VISIBLE);
            adapter = new ArrayAdapter<String>(this, R.layout.brand_list_item, R.id.product_name, upload.brand_products);
            listView.setAdapter(adapter);
            progressBar.setVisibility(View.GONE);
            rltop.setVisibility(View.VISIBLE);
        }
        else {
            GetBrands();
        }

        ImageView Skip = (ImageView) findViewById(R.id.productfeedButton);
        Skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        // Adding items to listview

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value
                String itemValue = (String) listView.getItemAtPosition(position);
                ////////System.out.println(itemValue);
                int b_id = upload.brandMap.get(itemValue);
                ////////System.out.println(brandMap.get(itemValue));

                Intent brandintent = new Intent(getApplicationContext(), upload.class);
                brandintent.putExtra("brand", itemValue);
                brandintent.putExtra("brandid", b_id);
                setResult(RESULT_OK, brandintent);
                finish();

            }


        });
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                BrandList.this.adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });
    }


//    Main get function
//    ---------------------------------------------------------

    private void GetBrands() {
        progressBar.setVisibility(View.VISIBLE);
        ApiService.getInstance(BrandList.this, 1).getData(BrandList.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/onboarding/getbrands/", "getBrands");

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(!GetSharedValues.GetgcmId(BrandList.this).equals("")) {
            ApiService.getInstance(BrandList.this, 1).getData(BrandList.this, true, "ADDADDRESS", EnvConstants.APP_BASE_URL + "/user/session/?gcm_reg_id="+ GetSharedValues.GetgcmId(BrandList.this), "session");
        }
        else {
            ApiService.getInstance(BrandList.this, 1).getData(BrandList.this, true, "ADDADDRESS", EnvConstants.APP_BASE_URL + "/user/session/", "session");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.set("UserName", GetSharedValues.getUsername(getApplicationContext()));mTracker.set("ZapUsername", GetSharedValues.getZapname(getApplicationContext()));   mTracker.setScreenName("Brands");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }


    //    Server responses
//    ---------------------------------------------------------

    @Override
    public void onResponseCallback(JSONObject response, String flag) {
        ////////System.out.println(response);
        if(flag.equals("getBrands")){
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if(resp != null){
                try {
                    String status = resp.getString("status");
                    if(status.equals("success")){
                        JSONArray data = resp.getJSONArray("data");
                        for(int i=0;i<data.length();i++){
                            upload.brand_products.add(data.getJSONObject(i).getString("brand"));
                            upload.brandMap.put(data.getJSONObject(i).getString("brand"),data.getJSONObject(i).getInt("id"));
                        }
                        adapter = new ArrayAdapter<String>(this, R.layout.brand_list_item, R.id.product_name, upload.brand_products);
                        listView.setAdapter(adapter);
                        progressBar.setVisibility(View.GONE);
                        rltop.setVisibility(View.VISIBLE);

                    }
                    else {
                        progressBar.setVisibility(View.GONE);
                        Intent mydialog = new Intent(BrandList.this, AlertActivity.class);
                        //int imgid=R.drawable.alertoop;
                        String strmessage="OOPS!";
                       // mydialog.putExtra("imgid", imgid);
                        mydialog.putExtra("Message",strmessage);
                        mydialog.putExtra("Buttontext"," RETRY ");
                        mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                        mydialog.putExtra("activity", "upload");
                        startActivity(mydialog);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                    Intent mydialog = new Intent(BrandList.this, AlertActivity.class);
                    //int imgid=R.drawable.alertoop;
                    String strmessage="OOPS!";
                   // mydialog.putExtra("imgid", imgid);
                    mydialog.putExtra("Message",strmessage);
                    mydialog.putExtra("Buttontext"," RETRY ");
                    mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                    mydialog.putExtra("activity", "upload");
                    startActivity(mydialog);
                    finish();
                }
            }
            else {
                progressBar.setVisibility(View.GONE);
                Intent mydialog = new Intent(BrandList.this, AlertActivity.class);
                //int imgid=R.drawable.alertoop;
                String strmessage="OOPS!";
               // mydialog.putExtra("imgid", imgid);
                mydialog.putExtra("Message",strmessage);
                mydialog.putExtra("Buttontext"," RETRY ");
                mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                mydialog.putExtra("activity", "upload");
                startActivity(mydialog);
                finish();
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
        Intent mydialog = new Intent(BrandList.this, AlertActivity.class);
        //int imgid=R.drawable.alertoop;
        String strmessage="OOPS!";
       // mydialog.putExtra("imgid", imgid);
        mydialog.putExtra("Message",strmessage);
        mydialog.putExtra("Buttontext"," RETRY ");
        mydialog.putExtra("tip", "SOMETHING WENT WRONG");
        mydialog.putExtra("activity", "upload");
        startActivity(mydialog);
        finish();
    }
}

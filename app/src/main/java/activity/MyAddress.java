package activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.exceptions.CleverTapMetaDataNotFoundException;
import com.clevertap.android.sdk.exceptions.CleverTapPermissionsNotSatisfied;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import application.MyApplicationClass;
import network.ApiCommunication;
import network.ApiService;
import utils.CustomMessage;
import utils.ExternalFunctions;
import utils.FontUtils;
import utils.GetSharedValues;

//import com.//Appsee.//Appsee;
//import com.uxcam.UXCam;

/**
 * Created by haseeb on 28/1/16.
 */
public class MyAddress extends ActionBarActivity implements ApiCommunication {


//    Changed

    public String SCREEN_NAME = "ADDRESS_UPLOAD";
    LinearLayout addressLayout, addaddress;
    ArrayList<JSONObject> addressList = new ArrayList<JSONObject>();

    ProgressBar progressbar;
    int EditIndex = 0;
    static int intcheck = 1;

    Boolean EditStatus = false;
    LinearLayout AddressHolder, EmptyAddressHolder;

    //    selected items
    int selectedAddressId = 0;
    int ProductId;

    ScrollView address_scroll;
    Tracker mTracker;
    String stime = "";
    String endtime = "";
    CleverTapAPI cleverTap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myaddress);
        MyApplicationClass application = (MyApplicationClass) getApplication();
        mTracker = application.getDefaultTracker();
        stime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        try {
            cleverTap = CleverTapAPI.getInstance(getApplicationContext());
        } catch (CleverTapMetaDataNotFoundException e) {
            // thrown if you haven't specified your CleverTap Account ID or Token in your AndroidManifest.xml
        } catch (CleverTapPermissionsNotSatisfied e) {
            // thrown if you haven’t requested the required permissions in your AndroidManifest.xml
        }
        //Appsee.start("4fb55809ab45411c909521bda720f8e6");
        //UXCam.startWithKey("1dfb25141864376");         overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        //////Thread.currentThread().setDefaultUncaughtExceptionHandler(new SplashScreen.MyUncaughtExceptionHandler());
        ProductId = getIntent().getIntExtra("ProductId", 0);
        progressbar = (ProgressBar) findViewById(R.id.address_progressBar);
        addaddress = (LinearLayout) findViewById(R.id.addaddress_label);
        address_scroll = (ScrollView) findViewById(R.id.address_scroll);
        GetAddress();

        View view = findViewById(R.id.rl);
        FontUtils.setCustomFont(view, getAssets());


        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final View customView = getLayoutInflater().inflate(R.layout.product_bar, null);
        getSupportActionBar().setCustomView(customView);
        Toolbar parent = (Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0, 0);
        TextView tv_headTitle = (TextView) findViewById(R.id.product_title_text);
        tv_headTitle.setText("MY ADDRESSES");
        ImageView imgback = (ImageView) findViewById(R.id.productfeedButton);


        AddressHolder = (LinearLayout) findViewById(R.id.addressLayout);
        EmptyAddressHolder = (LinearLayout) findViewById(R.id.EmptyAddressHolder);

        addaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intcheck = 2;
                Intent add = new Intent(MyAddress.this, AddAddress.class);
                add.putExtra("EditStatus", false);
                startActivityForResult(add, 1);
            }
        });


        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();


            }
        });
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
    protected void onRestart() {
        super.onRestart();
        if (!GetSharedValues.GetgcmId(MyAddress.this).equals("")) {
            ApiService.getInstance(MyAddress.this, 1).getData(MyAddress.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/?gcm_reg_id=" + GetSharedValues.GetgcmId(MyAddress.this), "session");
        } else {
            ApiService.getInstance(MyAddress.this, 1).getData(MyAddress.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/", "session");
        }
    }

    //    On activity result
//    ----------------------------------------------------------
    @Override
    public void onBackPressed() {

        Intent dintent = new Intent(MyAddress.this, Myaccountpage.class);
        startActivity(dintent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case 1:

                    String newAddress = data.getStringExtra("data");
                    try {
                        JSONObject obj1 = new JSONObject(newAddress);
                        EmptyAddressHolder.setVisibility(View.GONE);
                        AddressHolder.setVisibility(View.VISIBLE);
                        addressList.add(obj1);
                        AddressHolder.removeAllViews();
                        displayAddress();
//                        progressbar.setVisibility(View.VISIBLE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        CustomMessage.getInstance().CustomMessage(MyAddress.this, "Oops. Something went wrong!");
                    }
                    //////System.out.println("jjjjjjjjjjjjjjjj" + newAddress);
                    break;


                case 2:
                    String editAddress = data.getStringExtra("edited_data");
                    addressList.remove(EditIndex);
                    try {
                        JSONObject obj1 = new JSONObject(editAddress);
                        addressList.add(EditIndex, obj1);
                        AddressHolder.removeAllViews();
                        displayAddress();
//                        progressbar.setVisibility(View.VISIBLE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        CustomMessage.getInstance().CustomMessage(MyAddress.this, "Oops. Something went wrong!");
                    }
                    //////System.out.println("jjjjjjjjjjjjjjjj" + editAddress);
                    break;


            }
        }

    }


//    Display functions
//    ----------------------------------------------------------

    public void displayAddress() {
        final RadioGroup radioGroup = new RadioGroup(this);

        for (int i = 0; i < addressList.size(); i++) {
            LayoutInflater address_inflater = LayoutInflater.from(MyAddress.this);
            final View view;
            view = address_inflater.inflate(R.layout.item_address_list_address, null, false);
            TextView rText = (TextView) view.findViewById(R.id.address_text);
            final ImageView rEdit = (ImageView) view.findViewById(R.id.address_editButton);
            String address = null;


            try {
                address = addressList.get(i).getString("name") + " , " +
                        addressList.get(i).getString("address") + " , " +
                        addressList.get(i).getString("city") + " , " +
                        addressList.get(i).getString("state") + " , " +
                        addressList.get(i).getString("phone") + " , " +
                        addressList.get(i).getString("pincode");
                rEdit.setTag(i);
                rText.setText(address);

            } catch (JSONException e) {
                e.printStackTrace();
            }


            final int finalI = i;
            rEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditIndex = finalI;
                    EditAddress(addressList.get(finalI));
                }
            });
            radioGroup.addView(view);

        }
        AddressHolder.addView(radioGroup);
        progressbar.setVisibility(View.GONE);
    }


    private void EditAddress(JSONObject s) {

        try {
            Intent add = new Intent(MyAddress.this, AddAddress.class);
            add.putExtra("EditStatus", true);
            add.putExtra("EditName", s.getString("name"));
            add.putExtra("EditAddress", s.getString("address"));
            add.putExtra("EditCity", s.getString("city"));
            add.putExtra("EditState", s.getString("state"));
            add.putExtra("EditPhone", s.getString("phone"));
            add.putExtra("EditPincode", s.getString("pincode"));
            add.putExtra("EditId", s.getInt("id"));
            startActivityForResult(add, 2);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void checkout_AddAddress(View v) {
        Intent add = new Intent(MyAddress.this, AddAddress.class);
        add.putExtra("EditStatus", false);
        startActivityForResult(add, 1);
    }


//    Server requests
//    -----------------------------------------------------------

    private void GetAddress() {
        progressbar.setVisibility(View.VISIBLE);
        ApiService.getInstance(MyAddress.this, 1).getData(MyAddress.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/address/crud/", "getdata");

    }


//    Server responses
//    -------------------------------------------------------------

    @Override
    public void onResponseCallback(JSONObject response, String flag) {

        //////System.out.println(response);
        if (flag.equals("getdata")) {
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        JSONArray data = resp.getJSONArray("data");
                        if (data.length() > 0) {
                            AddressHolder.setVisibility(View.VISIBLE);
                            EmptyAddressHolder.setVisibility(View.GONE);
                            for (int i = 0; i < data.length(); i++) {
                                addressList.add(data.getJSONObject(i));
                            }
                            displayAddress();
                        } else {
                            //    //////System.out.println("ssss"+this.getCallingActivity().getClassName().toString());
                            if (intcheck == 1) {
                                progressbar.setVisibility(View.GONE);
                                intcheck = 2;
                                Intent mydialog = new Intent(MyAddress.this, AlertActivity.class);
                                //int imgid=R.drawable.alertaddress;
                                String strmessage = "HOW DO WE FIND YOU?";
                                // mydialog.putExtra("imgid", imgid);
                                mydialog.putExtra("Message", strmessage);
                                mydialog.putExtra("Buttontext", " ADD ADDRESS ");
                                mydialog.putExtra("tip", "");
                                mydialog.putExtra("activity", "MyAddress");
                                mydialog.putExtra("calling", "Myaccountpage");
                                startActivity(mydialog);
                                finish();
                            } else {
                                progressbar.setVisibility(View.GONE);
                                AddressHolder.setVisibility(View.GONE);
                                EmptyAddressHolder.setVisibility(View.VISIBLE);
                            }

                        }
                    } else {
                        progressbar.setVisibility(View.GONE);
                        Intent mydialog = new Intent(MyAddress.this, AlertActivity.class);
                        //int imgid=R.drawable.alertoop;
                        String strmessage = "OOPS!";
                        // mydialog.putExtra("imgid", imgid);
                        mydialog.putExtra("Message", strmessage);
                        mydialog.putExtra("Buttontext", " RETRY ");
                        mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                        mydialog.putExtra("activity", "Myaccountpage");
                        mydialog.putExtra("calling", "Myaccountpage");
                        startActivity(mydialog);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressbar.setVisibility(View.GONE);
                    Intent mydialog = new Intent(MyAddress.this, AlertActivity.class);
                    //int imgid=R.drawable.alertoop;
                    String strmessage = "OOPS!";
                    // mydialog.putExtra("imgid", imgid);
                    mydialog.putExtra("Message", strmessage);
                    mydialog.putExtra("Buttontext", " RETRY ");
                    mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                    mydialog.putExtra("activity", "Myaccountpage");
                    mydialog.putExtra("calling", "Myaccountpage");
                    startActivity(mydialog);
                    finish();
                }
            } else {
                progressbar.setVisibility(View.GONE);
                Intent mydialog = new Intent(MyAddress.this, AlertActivity.class);
                //int imgid=R.drawable.alertoop;
                String strmessage = "OOPS!";
                // mydialog.putExtra("imgid", imgid);
                mydialog.putExtra("Message", strmessage);
                mydialog.putExtra("Buttontext", " RETRY ");
                mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                mydialog.putExtra("activity", "Myaccountpage");
                mydialog.putExtra("calling", "Myaccountpage");
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
        CustomMessage.getInstance().CustomMessage(MyAddress.this, "Oops. Something went wrong!");
    }


    @Override
    protected void onResume() {
        super.onResume();
        mTracker.set("UserName", GetSharedValues.getUsername(getApplicationContext()));
        mTracker.set("ZapUsername", GetSharedValues.getZapname(getApplicationContext()));
        mTracker.setScreenName("My address");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}

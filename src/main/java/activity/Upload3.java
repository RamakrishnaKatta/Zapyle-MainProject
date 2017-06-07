package activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;
//import com.//Appsee.//Appsee;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
//import com.uxcam.UXCam;
import com.zapyle.zapyle.Alerts;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import application.MyApplicationClass;
import network.ApiCommunication;
import network.ApiService;
import network.JsonParser;
import utils.CommonFinish;
import utils.CustomMessage;
import utils.ExternalFunctions;
import utils.FontUtils;
import utils.GetSharedValues;
import utils.Mixpanelutils;

/**
 * Created by haseeb on 23/1/16.
 */
public class Upload3 extends ActionBarActivity implements ApiCommunication {

    public String SCREEN_NAME = "UPLOAD3";
    LinearLayout addressLayout, addaddressform, addaddress;
    ArrayList<JSONObject> addressList = new ArrayList<JSONObject>();
    HashMap<String, Integer> states = new HashMap<String, Integer>();
    ArrayList<String> stateName = new ArrayList<String>();

    ProgressBar progressbar;
    ImageView addSign;
    Boolean EditStatus = false;
    Boolean Address_EditStatus = false;

    //    selected items
    int selectedAddressId = 0;
    int EditAddressId, EditAddressIndex, ProductId;
    TextView formlabel, upload_next3;
    EditText a_name, a_address, a_city, a_postcode, a_phonenumber;
    Spinner a_stateSpinner;
    ScrollView address_scroll;

    MixpanelAPI mixpanel;
    Date today;
    static String starttime;
    public static String boolsel;
    Boolean PTA = false;
    TextView AddButton;
    LinearLayout.LayoutParams params;
    public static Context CommonContext;
    ProgressDialog progress;
    Tracker mTracker;
    JSONObject PincodeObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload3);
        MyApplicationClass application = (MyApplicationClass) getApplication();
        mTracker = application.getDefaultTracker();
        //Appsee.start("4fb55809ab45411c909521bda720f8e6");
        //UXCam.startWithKey("1dfb25141864376");         overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        //////Thread.currentThread().setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
        mixpanel = MixpanelAPI.getInstance(Upload3.this, getResources().getString(R.string.mixpanelToken));

        LocalBroadcastManager.getInstance(this).registerReceiver(CommonFinish.broadcastReceiver, new IntentFilter("Upload3"));
//        if(!MyApplicationClass.cContextArray.contains(getApplicationContext())) {
        ExternalFunctions.cContextArray[20] = Upload3.this;
//        }
        progress = new ProgressDialog(Upload3.this);
        progress.setCancelable(false);

        today = new Date();
        DateFormat dfstarttime = new SimpleDateFormat("hh:mm:ss aa");
        dfstarttime.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        starttime = dfstarttime.format(today);
        //////System.out.println("mixpaneltime" + starttime);
        JSONObject propOpenapp = new JSONObject();
        try {
            propOpenapp.put("Time", starttime);
            propOpenapp.put("Event Name", "Upload Start");
            mixpanel.track("Upload Start", propOpenapp);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        PTA = getIntent().getBooleanExtra("PTA", false);


        ProductId = getIntent().getIntExtra("ProductId", 0);
        EditStatus = getIntent().getBooleanExtra("EditStatus", false);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        progressbar = (ProgressBar) findViewById(R.id.address_progressBar);
        addressLayout = (LinearLayout) findViewById(R.id.addressLayout);
        addaddressform = (LinearLayout) findViewById(R.id.layout_feilds_addresses);
        addaddress = (LinearLayout) findViewById(R.id.addaddress_label);
        addSign = (ImageView) findViewById(R.id.addSign);
        address_scroll = (ScrollView) findViewById(R.id.address_scroll);
        GetStates();
        GetAddress();

        params = new LinearLayout.LayoutParams(60, 60);

        View view = findViewById(R.id.upload1_layout);
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
        tv_headTitle.setText("SELECT PICKUP ADDRESS");


        a_name = (EditText) findViewById(R.id.edittext_name);
        a_address = (EditText) findViewById(R.id.edittext_Address);
        a_city = (EditText) findViewById(R.id.edittext_city);
        a_stateSpinner = (Spinner) findViewById(R.id.spinner_state);
        a_postcode = (EditText) findViewById(R.id.edittext_postcode);
        a_phonenumber = (EditText) findViewById(R.id.edittext_phone);
        formlabel = (TextView) findViewById(R.id.formlabel);
        upload_next3 = (TextView) findViewById(R.id.upload_next3);
        AddButton = (TextView) findViewById(R.id.AddButton);

//        a_phonenumber.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                AddButton.setVisibility(View.VISIBLE);
//                return false;
//            }
//        });





        ImageView Skip = (ImageView) findViewById(R.id.productfeedButton);
        Skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        addaddress.setTag(1);
        addaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(addaddress.getTag().toString()) == 1) {
                    Address_EditStatus = false;
                    addaddressform.setVisibility(View.VISIBLE);
                    AddButton.setVisibility(View.VISIBLE);
                    addSign.setImageResource(R.drawable.closeform);
                    addSign.setLayoutParams(params);
                    upload_next3.setVisibility(View.GONE);
                    formlabel.setText("ADD NEW ADDRESS");
                    addaddress.setTag(2);
                    address_scroll.fullScroll(View.FOCUS_DOWN);

                } else {
                    upload_next3.setVisibility(View.VISIBLE);
                    AddButton.setVisibility(View.GONE);
                    Address_EditStatus = false;
                    addaddressform.setVisibility(View.GONE);
                    addSign.setImageResource(R.drawable.bigplus);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(70, 70);
                    addSign.setLayoutParams(params);
                    addaddress.setTag(1);

                }

            }
        });


        a_postcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 6) {
                    new PincodeAutofill().execute(Integer.parseInt(s.toString()));
                }
            }
        });

    }


//    Display functions
//    ----------------------------------------------------------


    public void displayAddress() {
        final RadioGroup radioGroup = new RadioGroup(this);

        for (int i = 0; i < addressList.size(); i++) {
            LayoutInflater address_inflater = LayoutInflater.from(Upload3.this);
            final View view;
            view = address_inflater.inflate(R.layout.item_address_list, null, false);
            final LinearLayout selectLayout = (LinearLayout) view.findViewById(R.id.selectLayout);
            final RadioButton rSelect = (RadioButton) view.findViewById(R.id.radio_address_select);
            rSelect.setEnabled(true);
            TextView rText = (TextView) view.findViewById(R.id.address_text);
            final ImageView rEdit = (ImageView) view.findViewById(R.id.address_editButton);
            String address = null;

            try {
                if (addressList.size() == 1) {
                    selectedAddressId = addressList.get(0).getInt("id");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            try {
                if (addressList.get(i).getInt("id") == selectedAddressId) {
                    rSelect.setChecked(true);
                } else {

                }
            } catch (Exception e) {
                rSelect.setChecked(false);
            }


            try {
                address = addressList.get(i).getString("name") + " , " +
                        addressList.get(i).getString("address") + " , " +
                        addressList.get(i).getString("city") + " , " +
                        addressList.get(i).getString("state") + " , " +
                        addressList.get(i).getString("phone") + " , " +
                        addressList.get(i).getString("pincode");
                selectLayout.setId(addressList.get(i).getInt("id"));
                rEdit.setTag(i);
                rText.setText(address);
//                rText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.margin_15));

            } catch (JSONException e) {
                e.printStackTrace();
            }


            final int finalI = i;
            selectLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedAddressId = selectLayout.getId();
                    rSelect.setChecked(true);
                    radioGroup.removeAllViews();
                    displayAddress();
                }
            });
            rEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Address_EditStatus = true;
                    upload_next3.setVisibility(View.GONE);
                    AddButton.setVisibility(View.VISIBLE);
                    EditAddress(rEdit.getTag().toString());
                }
            });
            rSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedAddressId = selectLayout.getId();
                    rSelect.setChecked(true);
                    radioGroup.removeAllViews();
                    displayAddress();
                }
            });

            radioGroup.addView(view);

        }
        addressLayout.addView(radioGroup);
        progressbar.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if (!EditStatus) {
            Intent upload2 = new Intent(Upload3.this, Upload2.class);
            upload2.putExtra("productId", ProductId);
            upload2.putExtra("PTA", true);
            upload2.putExtra("EditStatus", true);
            startActivity(upload2);
            finish();
        } else {
            Intent upload2 = new Intent(Upload3.this, Upload2.class);
            upload2.putExtra("productId", ProductId);
            upload2.putExtra("PTA", PTA);
            upload2.putExtra("EditStatus", true);
            startActivity(upload2);
            finish();
        }

    }

    private void setDataToSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.simple_spinner_item, stateName);
        a_stateSpinner.setAdapter(adapter);
    }


    public void onSaveAddress(View v) {

        if (!Address_EditStatus) {
            String name = a_name.getText().toString();
            String address = a_address.getText().toString();
            String city = a_city.getText().toString();
            String phone = a_phonenumber.getText().toString();
            String pincode = a_postcode.getText().toString();
            String state = a_stateSpinner.getSelectedItem().toString();
            System.out.println("STATE : "+state);
            int s_state = states.get(state);

            //////System.out.println(name + "__" + address + "__" + city + "__" + phone + "__" + pincode + "__" + state);

            if (name.length() != 0 && address.length() != 0 && city.length() != 0 && phone.length() != 0 && pincode.length() != 0) {
                if (s_state > 0) {
                    if (pincode.length() == 6) {
                        if (phone.length() >= 10) {
                            postAddress();
                        } else {
                             CustomMessage.getInstance().CustomMessage(Upload3.this, "Invalid phonenumber");

                        }
                    } else {
                         CustomMessage.getInstance().CustomMessage(Upload3.this, "Invalid pincode");
                    }
                } else {
                     CustomMessage.getInstance().CustomMessage(Upload3.this, "Select state");
                }

            } else {
                 CustomMessage.getInstance().CustomMessage(Upload3.this, "Fill all the fields");
            }
        } else {
            updateToServer();
        }


    }

    private void updateToServer() {

        String name = a_name.getText().toString();
        String address = a_address.getText().toString();
        String city = a_city.getText().toString();
        String phone = a_phonenumber.getText().toString();
        String pincode = a_postcode.getText().toString();
        String state = a_stateSpinner.getSelectedItem().toString();
        int s_state = states.get(state);

        final JSONObject addressObject = new JSONObject();
        try {
            addressObject.put("city", city);
            addressObject.put("name", name);
            addressObject.put("pincode", pincode);
            addressObject.put("phone", phone);
            addressObject.put("state", s_state);
            addressObject.put("address", address);
            addressObject.put("address_id", EditAddressId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //////System.out.println(addressObject + "____");
        ApiService.getInstance(Upload3.this, 1).putData(Upload3.this, EnvConstants.APP_BASE_URL + "/address/crud/", addressObject, SCREEN_NAME, "Put");

    }


    public void EditAddress(String addressId) {
        //////System.out.println(addressId + "__" + addressList.get(Integer.parseInt(addressId)));
        addaddressform.setVisibility(View.VISIBLE);
        addSign.setImageResource(R.drawable.closeform);
        addSign.setLayoutParams(params);

        formlabel.setText("UPDATE ADDRESS");
        addaddress.setTag(2);
        address_scroll.fullScroll(View.FOCUS_DOWN);
        EditAddressIndex = Integer.parseInt(addressId);
        JSONObject jb = addressList.get(Integer.parseInt(addressId));


        try {
            EditAddressId = jb.getInt("id");

            a_name.setText(jb.getString("name"));
            a_address.setText(jb.getString("address"));
            a_city.setText(jb.getString("city"));
            a_phonenumber.setText(jb.getString("phone"));
            a_postcode.setText(jb.getString("pincode"));
            //////System.out.println(jb.getString("state") + "___" + states.get(jb.getString("state")));
            a_stateSpinner.setSelection(stateName.indexOf(jb.getString("state")));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


//    Server requests
//    -----------------------------------------------------------

    private void GetAddress() {
        progressbar.setVisibility(View.VISIBLE);
        ApiService.getInstance(Upload3.this, 1).getData(Upload3.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/address/crud/", "getdata");

    }


    private void GetStates() {
        ApiService.getInstance(Upload3.this, 1).getData(Upload3.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/address/get_states/", "getstates");
    }

    public void getEditData(int ProductId) {
        if (PTA) {
            ApiService.getInstance(Upload3.this, 1).getData(Upload3.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/upload/album/" + ProductId + "/3/?action=p_t_a", "getEditdata");
        } else {
            ApiService.getInstance(Upload3.this, 1).getData(Upload3.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/upload/album/" + ProductId + "/3/", "getEditdata");
        }
    }

    private void postAddress() {

        String name = a_name.getText().toString();
        String address = a_address.getText().toString();
        String city = a_city.getText().toString();
        String phone = a_phonenumber.getText().toString();
        String pincode = a_postcode.getText().toString();
        String state = a_stateSpinner.getSelectedItem().toString();
        int s_state = states.get(state);
        JSONArray data = new JSONArray();

        //////System.out.println("data for sending__" + data);
        final JSONObject addressObject = new JSONObject();
        try {
            addressObject.put("city", city);
            addressObject.put("name", name);
            addressObject.put("pincode", pincode);
            addressObject.put("phone", phone);
            addressObject.put("state", s_state);
            addressObject.put("address", address);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiService.getInstance(Upload3.this, 1).postData(Upload3.this, EnvConstants.APP_BASE_URL + "/address/crud/", addressObject, SCREEN_NAME, "Add");
    }


    public void upload_next3(View v) {
        if (selectedAddressId > 0) {
            progress.setMessage("Processing upload ...Please Wait...");
            progress.show();
            upload_next3.setEnabled(false);
            if (!EditStatus) {
                final JSONObject albumObject = new JSONObject();
                try {
                    albumObject.put("pickup_address", selectedAddressId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ApiService.getInstance(Upload3.this, 1).postData(Upload3.this, EnvConstants.APP_BASE_URL + "/upload/album/" + ProductId + "/", albumObject, SCREEN_NAME, "postToserver");
            } else {
                final JSONObject albumObject = new JSONObject();
                try {
                    albumObject.put("pickup_address", selectedAddressId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (PTA) {
                    ApiService.getInstance(Upload3.this, 1).putData(Upload3.this, EnvConstants.APP_BASE_URL + "/upload/album/" + ProductId + "/3/?action=p_t_a", albumObject, SCREEN_NAME, "editToserver");
                } else {
                    ApiService.getInstance(Upload3.this, 1).putData(Upload3.this, EnvConstants.APP_BASE_URL + "/upload/album/" + ProductId + "/3/", albumObject, SCREEN_NAME, "editToserver");
                }
            }
        } else {
             CustomMessage.getInstance().CustomMessage(Upload3.this, "Select address");
        }
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
                        for (int i = 0; i < data.length(); i++) {
                            addressList.add(data.getJSONObject(i));
                        }
                        if (!EditStatus) {
                            displayAddress();
                        }
                        if (EditStatus) {
                            getEditData(ProductId);
                        }
                    } else {
                        progressbar.setVisibility(View.GONE);
                        Intent mydialog = new Intent(Upload3.this, AlertActivity.class);
                       // int imgid = R.drawable.alertoop;
                        String strmessage = "OOPS!";
                       // mydialog.putExtra("imgid", imgid);
                        mydialog.putExtra("Message", strmessage);
                        mydialog.putExtra("Buttontext", " RETRY ");
                        mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                        mydialog.putExtra("activity", "Upload3");
                        mydialog.putExtra("calling", "Upload3");
                        startActivity(mydialog);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressbar.setVisibility(View.GONE);
                    Intent mydialog = new Intent(Upload3.this, AlertActivity.class);
                   // int imgid = R.drawable.alertoop;
                    String strmessage = "OOPS!";
                   // mydialog.putExtra("imgid", imgid);
                    mydialog.putExtra("Message", strmessage);
                    mydialog.putExtra("Buttontext", " RETRY ");
                    mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                    mydialog.putExtra("activity", "Upload3");
                    mydialog.putExtra("calling", "Upload3");

                    startActivity(mydialog);
                    finish();
                }
            } else {
                progressbar.setVisibility(View.GONE);
                Intent mydialog = new Intent(Upload3.this, AlertActivity.class);
               // int imgid = R.drawable.alertoop;
                String strmessage = "OOPS!";
               // mydialog.putExtra("imgid", imgid);
                mydialog.putExtra("Message", strmessage);
                mydialog.putExtra("Buttontext", " RETRY ");
                mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                mydialog.putExtra("activity", "Upload3");
                mydialog.putExtra("calling", "Upload3");
                startActivity(mydialog);
                finish();
            }
        } else if (flag.equals("getstates")) {
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        JSONArray data = resp.getJSONArray("data");
                        states.put("Select state", 0);
                        stateName.add("Select state");
                        for (int i = 0; i < data.length(); i++) {
                            states.put(data.getJSONObject(i).getString("name").toUpperCase(), data.getJSONObject(i).getInt("id"));
                            stateName.add(data.getJSONObject(i).getString("name").toUpperCase());
                        }
                        setDataToSpinner();
                    } else {
                        Intent mydialog = new Intent(Upload3.this, AlertActivity.class);
                       // int imgid = R.drawable.alertoop;
                        String strmessage = "OOPS!";
                       // mydialog.putExtra("imgid", imgid);
                        mydialog.putExtra("Message", strmessage);
                        mydialog.putExtra("Buttontext", " RETRY ");
                        mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                        mydialog.putExtra("activity", "Upload3");
                        mydialog.putExtra("calling", "Upload3");
                        startActivity(mydialog);
                        finish();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Intent mydialog = new Intent(Upload3.this, AlertActivity.class);
                   // int imgid = R.drawable.alertoop;
                    String strmessage = "OOPS!";
                   // mydialog.putExtra("imgid", imgid);
                    mydialog.putExtra("Message", strmessage);
                    mydialog.putExtra("Buttontext", " RETRY ");
                    mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                    mydialog.putExtra("activity", "Upload3");
                    mydialog.putExtra("calling", "Upload3");
                    startActivity(mydialog);
                    finish();
                }

            }
        } else if (flag.equals("Add")) {
            //////System.out.println(response);
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {

                        JSONObject data = resp.getJSONObject("data");
                        addressList.add(data);
                        a_name.setText("");
                        a_address.setText("");
                        a_city.setText("");
                        a_phonenumber.setText("");
                        a_postcode.setText("");
                        addaddressform.setVisibility(View.GONE);
                        addSign.setImageResource(R.drawable.bigplus);
                        addaddress.setTag(1);
                        addressLayout.removeAllViews();
                        if (upload_next3.getVisibility() == View.GONE) {
                            upload_next3.setVisibility(View.VISIBLE);
                        }
                        displayAddress();
                        View view = this.getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }

                    } else {

//                        {"status":"error","detail":{"pincode":["This pincode is not available with ZAPYLE."]}}
                        JSONObject detail = resp.getJSONObject("detail");
                        if (detail.has("pincode")) {
                             CustomMessage.getInstance().CustomMessage(Upload3.this, detail.getJSONArray("pincode").get(0).toString());
                        }

//                        Intent mydialog = new Intent(Upload3.this, AlertActivity.class);
//                       // int imgid = R.drawable.alertoop;
//                        String strmessage = "OOPS!";
//                       // mydialog.putExtra("imgid", imgid);
//                        mydialog.putExtra("Message", strmessage);
//                        mydialog.putExtra("Buttontext", " RETRY ");
//                        mydialog.putExtra("tip", "SOMETHING WENT WRONG");
//                        mydialog.putExtra("activity", "Upload3");
//                        mydialog.putExtra("calling", "Upload3");
//                        startActivity(mydialog);
//                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Intent mydialog = new Intent(Upload3.this, AlertActivity.class);
                   // int imgid = R.drawable.alertoop;
                    String strmessage = "OOPS!";
                   // mydialog.putExtra("imgid", imgid);
                    mydialog.putExtra("Message", strmessage);
                    mydialog.putExtra("Buttontext", " RETRY ");
                    mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                    mydialog.putExtra("activity", "Upload3");
                    mydialog.putExtra("calling", "Upload3");
                    startActivity(mydialog);
                    finish();
                }
            } else {
                Intent mydialog = new Intent(Upload3.this, AlertActivity.class);
               // int imgid = R.drawable.alertoop;
                String strmessage = "OOPS!";
               // mydialog.putExtra("imgid", imgid);
                mydialog.putExtra("Message", strmessage);
                mydialog.putExtra("Buttontext", " RETRY ");
                mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                mydialog.putExtra("activity", "Upload3");
                mydialog.putExtra("calling", "Upload3");
                startActivity(mydialog);
                finish();
            }
        } else if (flag.equals("Put")) {
            //////System.out.println(response);
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        JSONObject data = resp.getJSONObject("data");
                        addressList.remove(EditAddressIndex);
                        addressList.add(EditAddressIndex, data);
                        a_name.setText("");
                        a_address.setText("");
                        a_city.setText("");
                        a_phonenumber.setText("");
                        a_postcode.setText("");
                        addaddressform.setVisibility(View.GONE);
                        upload_next3.setVisibility(View.VISIBLE);
                        AddButton.setVisibility(View.GONE);
                        addSign.setImageResource(R.drawable.bigplus);
                        addaddress.setTag(1);
                        addressLayout.removeAllViews();
                        displayAddress();
                        View view = this.getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }

                    } else {
                        JSONObject detail = resp.getJSONObject("detail");
                        if (detail.has("pincode")) {
                             CustomMessage.getInstance().CustomMessage(Upload3.this, detail.getJSONArray("pincode").get(0).toString());
                        }
//                        Intent mydialog = new Intent(Upload3.this, AlertActivity.class);
//                       // int imgid = R.drawable.alertoop;
//                        String strmessage = "OOPS!";
//                       // mydialog.putExtra("imgid", imgid);
//                        mydialog.putExtra("Message", strmessage);
//                        mydialog.putExtra("Buttontext", " RETRY ");
//                        mydialog.putExtra("tip", "SOMETHING WENT WRONG");
//                        mydialog.putExtra("activity", "Upload3");
//                        mydialog.putExtra("calling", "Upload3");
//                        startActivity(mydialog);
//                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Intent mydialog = new Intent(Upload3.this, AlertActivity.class);
                   // int imgid = R.drawable.alertoop;
                    String strmessage = "OOPS!";
                   // mydialog.putExtra("imgid", imgid);
                    mydialog.putExtra("Message", strmessage);
                    mydialog.putExtra("Buttontext", " RETRY ");
                    mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                    mydialog.putExtra("activity", "Upload3");
                    mydialog.putExtra("calling", "Upload3");
                    startActivity(mydialog);
                    finish();
                }
            } else {
                Intent mydialog = new Intent(Upload3.this, AlertActivity.class);
               // int imgid = R.drawable.alertoop;
                String strmessage = "OOPS!";
               // mydialog.putExtra("imgid", imgid);
                mydialog.putExtra("Message", strmessage);
                mydialog.putExtra("Buttontext", " RETRY ");
                mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                mydialog.putExtra("activity", "Upload3");
                mydialog.putExtra("calling", "Upload3");
                startActivity(mydialog);
                finish();
            }
        } else if (flag.equals("postToserver")) {
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                //////System.out.println(resp);
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        progress.dismiss();

                        today = new Date();
                        DateFormat dfclosetime = new SimpleDateFormat("hh:mm:ss aa", Locale.ENGLISH);
                        dfclosetime.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
                        String strclosetime = dfclosetime.format(today);


                        Mixpanelutils mx = new Mixpanelutils();

                        //////System.out.println("mixpaneltimespent" + mx.getTimeDiff(starttime, strclosetime) + "iii" + strclosetime);
                        JSONObject proponbcompleted = new JSONObject();
                        try {
                            proponbcompleted.put("Step", "3");
                            proponbcompleted.put("Time taken", mx.getTimeDiff(starttime, strclosetime));
                            proponbcompleted.put("Product title", Upload1.selectedTitle);
                            proponbcompleted.put("Sale", Upload1.boolsel);
                            proponbcompleted.put("Total time", mx.getTimeDiff(Upload1.starttime, strclosetime));
                            proponbcompleted.put("Event Name", "Upload product");
                            mixpanel.track("Upload product", proponbcompleted);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (GetSharedValues.getUsertype(Upload3.this).equals("store_front")) {
                            Intent profile = new Intent(Upload3.this, ProfilePage.class);
                            profile.putExtra("user_id", GetSharedValues.getuserId(this));
                            profile.putExtra("p_username",GetSharedValues.getUsername(this));
                            startActivity(profile);
                            finish();
                        } else {
//
//                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Upload3.this);
////        alertDialogBuilder.setTitle("Thanks for your listing. Our stylist will review i and get back to youwith the status real soon!");
//
//                            // set dialog message
//                            alertDialogBuilder
//                                    .setCancelable(false)
//                                    .setMessage("Thanks for your listing. Our stylist will review it and get back to you with the status real soon!")
//                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int id) {
//                                            Intent login = new Intent(Upload3.this, MyProfile.class);
//                                            login.putExtra("zap_user", GetSharedValues.getZapname(Upload3.this));
//                                            startActivity(login);
//                                            finish();
//
//                                        }
//                                    });
//
//                            // create alert dialog
//                            AlertDialog alertDialog = alertDialogBuilder.create();
//
//                            // show it
//                            alertDialog.show();
                            Alerts.listingAlert(this, GetSharedValues.getZapname(Upload3.this));
                            // finish();
                        }
                        upload_next3.setEnabled(true);
                    } else {
                        progress.dismiss();
                        upload_next3.setEnabled(true);
                        Intent mydialog = new Intent(Upload3.this, AlertActivity.class);
                       // int imgid = R.drawable.alertoop;
                        String strmessage = "OOPS!";
                       // mydialog.putExtra("imgid", imgid);
                        mydialog.putExtra("Message", strmessage);
                        mydialog.putExtra("Buttontext", " RETRY ");
                        mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                        mydialog.putExtra("activity", "Upload3");
                        mydialog.putExtra("calling", "Upload3");
                        startActivity(mydialog);
                        finish();
                    }
                } catch (JSONException e) {
                    progress.dismiss();
                    upload_next3.setEnabled(true);
                    e.printStackTrace();
                    Intent mydialog = new Intent(Upload3.this, AlertActivity.class);
                   // int imgid = R.drawable.alertoop;
                    String strmessage = "OOPS!";
                   // mydialog.putExtra("imgid", imgid);
                    mydialog.putExtra("Message", strmessage);
                    mydialog.putExtra("Buttontext", " RETRY ");
                    mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                    mydialog.putExtra("activity", "Upload3");
                    mydialog.putExtra("calling", "Upload3");
                    startActivity(mydialog);
                    finish();
                }
            } else {
                progress.dismiss();
                upload_next3.setEnabled(true);
                Intent mydialog = new Intent(Upload3.this, AlertActivity.class);
               // int imgid = R.drawable.alertoop;
                String strmessage = "OOPS!";
               // mydialog.putExtra("imgid", imgid);
                mydialog.putExtra("Message", strmessage);
                mydialog.putExtra("Buttontext", " RETRY ");
                mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                mydialog.putExtra("activity", "Upload3");
                mydialog.putExtra("calling", "Upload3");
                startActivity(mydialog);
                finish();
            }
        } else if (flag.equals("getEditdata")) {
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                //////System.out.println(resp);
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        JSONObject data = resp.getJSONObject("data");
                        if (!data.isNull("pickup_address")) {
                            selectedAddressId = data.getInt("pickup_address");
                        }
                        displayAddress();

                    } else {
                        Intent mydialog = new Intent(Upload3.this, AlertActivity.class);
                       // int imgid = R.drawable.alertoop;
                        String strmessage = "OOPS!";
                       // mydialog.putExtra("imgid", imgid);
                        mydialog.putExtra("Message", strmessage);
                        mydialog.putExtra("Buttontext", " RETRY ");
                        mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                        mydialog.putExtra("activity", "Upload3");
                        mydialog.putExtra("calling", "Upload3");
                        startActivity(mydialog);
                        finish();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Intent mydialog = new Intent(Upload3.this, AlertActivity.class);
                   // int imgid = R.drawable.alertoop;
                    String strmessage = "OOPS!";
                   // mydialog.putExtra("imgid", imgid);
                    mydialog.putExtra("Message", strmessage);
                    mydialog.putExtra("Buttontext", " RETRY ");
                    mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                    mydialog.putExtra("activity", "Upload3");
                    mydialog.putExtra("calling", "Upload3");
                    startActivity(mydialog);
                    finish();

                }
            } else {
                Intent mydialog = new Intent(Upload3.this, AlertActivity.class);
               // int imgid = R.drawable.alertoop;
                String strmessage = "OOPS!";
               // mydialog.putExtra("imgid", imgid);
                mydialog.putExtra("Message", strmessage);
                mydialog.putExtra("Buttontext", " RETRY ");
                mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                mydialog.putExtra("activity", "Upload3");
                mydialog.putExtra("calling", "Upload3");
                startActivity(mydialog);
                finish();

            }
        } else if (flag.equals("editToserver")) {
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                //////System.out.println(resp);
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        progress.dismiss();
                        Intent profile = new Intent(Upload3.this, ProfilePage.class);
                        profile.putExtra("user_id", GetSharedValues.getuserId(this));
                        profile.putExtra("p_username",GetSharedValues.getUsername(this));
                        startActivity(profile);
                        finish();
                        upload_next3.setEnabled(true);
                    } else {
                        progress.dismiss();
                        upload_next3.setEnabled(true);
                        Intent mydialog = new Intent(Upload3.this, AlertActivity.class);
                       // int imgid = R.drawable.alertoop;
                        String strmessage = "OOPS!";
                       // mydialog.putExtra("imgid", imgid);
                        mydialog.putExtra("Message", strmessage);
                        mydialog.putExtra("Buttontext", " RETRY ");
                        mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                        mydialog.putExtra("activity", "Upload3");
                        mydialog.putExtra("calling", "Upload3");
                        startActivity(mydialog);
                        finish();
                    }
                } catch (JSONException e) {
                    progress.dismiss();
                    upload_next3.setEnabled(true);
                    e.printStackTrace();
                    Intent mydialog = new Intent(Upload3.this, AlertActivity.class);
                   // int imgid = R.drawable.alertoop;
                    String strmessage = "OOPS!";
                   // mydialog.putExtra("imgid", imgid);
                    mydialog.putExtra("Message", strmessage);
                    mydialog.putExtra("Buttontext", " RETRY ");
                    mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                    mydialog.putExtra("activity", "Upload3");
                    mydialog.putExtra("calling", "Upload3");
                    startActivity(mydialog);
                    finish();
                }
            } else {
                progress.dismiss();
                upload_next3.setEnabled(true);
                Intent mydialog = new Intent(Upload3.this, AlertActivity.class);
               // int imgid = R.drawable.alertoop;
                String strmessage = "OOPS!";
               // mydialog.putExtra("imgid", imgid);
                mydialog.putExtra("Message", strmessage);
                mydialog.putExtra("Buttontext", " RETRY ");
                mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                mydialog.putExtra("activity", "Upload3");
                mydialog.putExtra("calling", "Upload3");
                startActivity(mydialog);
                finish();
            }
        }
        else if (flag.equals("pincodeAutofill")){
            PincodeObject = JsonParser.getInstance().parserJsonObject(response);
            System.out.println("PINCODE : 2"+PincodeObject);
            try {
                String status = PincodeObject.getString("status");
                if (addaddressform.getVisibility() == View.VISIBLE){
                    if (status.equals("success")){
                        JSONObject data = PincodeObject.getJSONObject("data");
                        String city = data.getString("city");
                        String state = data.getString("state");
                        a_city.setText(city);
                        a_stateSpinner.setSelection(stateName.indexOf(state));
                    }
                    else {
                         CustomMessage.getInstance().CustomMessage(Upload3.this, PincodeObject.getString("detail"));
                        a_city.setText("");
                        a_stateSpinner.setSelection(0);
                    }
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }


    @Override
    public void onErrorCallback(VolleyError error, String flag) {
        Intent mydialog = new Intent(Upload3.this, AlertActivity.class);
       // int imgid = R.drawable.alertoop;
        String strmessage = "OOPS!";
       // mydialog.putExtra("imgid", imgid);
        mydialog.putExtra("Message", strmessage);
        mydialog.putExtra("Buttontext", " RETRY ");
        mydialog.putExtra("tip", "SOMETHING WENT WRONG");
        mydialog.putExtra("activity", "Upload3");
        mydialog.putExtra("calling", "Upload3");
        startActivity(mydialog);
        finish();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!GetSharedValues.GetgcmId(Upload3.this).equals("")) {
            ApiService.getInstance(Upload3.this, 1).getData(Upload3.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/?gcm_reg_id=" + GetSharedValues.GetgcmId(Upload3.this), "session");
        } else {
            ApiService.getInstance(Upload3.this, 1).getData(Upload3.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/", "session");
        }
    }


    public class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
           // //MyApplicationClass.sFirstRun= false;
            Intent intent = new Intent("BuySecondPage");
            intent.putExtra("action", "close");
            intent.putExtra("activityIndex", "ALL");
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            if (ex.getClass().equals(OutOfMemoryError.class)) {
                try {
                    File sdCard = Environment.getExternalStorageDirectory();
                    File dir = new File(sdCard.getAbsolutePath() + "/dump.hprof");
                    dir.mkdirs();

                    android.os.Debug.dumpHprofData(String.valueOf(dir));
                    ////////System.out.println("OUTOFMEMORYDUMP");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            ex.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.set("UserName", GetSharedValues.getUsername(getApplicationContext()));mTracker.set("ZapUsername", GetSharedValues.getZapname(getApplicationContext()));   mTracker.setScreenName("Upload3");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }




    private class PincodeAutofill extends AsyncTask<Integer,View,JSONObject>{

        @Override
        protected JSONObject doInBackground(Integer... params) {
            ApiService.getInstance(Upload3.this, 1).getData(Upload3.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/address/pincode/?pincode="+params[0], "pincodeAutofill");
            return PincodeObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            System.out.println("PINCODE : "+jsonObject);
        }
    }

}

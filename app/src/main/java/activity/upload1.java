package activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
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

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import network.ApiCommunication;
import network.ApiService;
import utils.CommonFinish;
import utils.CustomMessage;
import utils.ExternalFunctions;
import utils.FashionCalculator;
import utils.FontUtils;
import utils.GetSharedValues;

//import com.//Appsee.//Appsee;
//import com.uxcam.UXCam;

/**
 * Created by haseeb on 23/1/16.
 */
public class upload1 extends ActionBarActivity implements ApiCommunication {

    private static final int ACTION_REQUEST_CONDITION_GUIDE = 1;
    EditText orgPrice, ListPrice;
    Spinner ageSpinner, conditionSpinner;
    SeekBar seekBar;
    TextView startPrice, endprice, earning, conditionguide;
    LinearLayout ageLayout, conditionLayout;

    public String SCREEN_NAME = "UPLOAD2";

    HashMap<String, Integer> ageMap = new HashMap<String, Integer>();
    HashMap<String, Integer> conditionMap = new HashMap<String, Integer>();

    ArrayList<String> age = new ArrayList<String>();
    ArrayList<String> condition = new ArrayList<String>();

    ProgressBar progressBar;
    TextView desc_head, descriptionText;
    Double EarningPercentage;


//    Values
//    ----------------

    int ProductId, OPrice, Earning, ageId, conditionId;
    Double LPrice = 0.0;
    int ageposition = 0;
    int New_LPrice = 0;
    Boolean EditStatus = false;
    String fc_condition;
    String fc_age;
    Boolean lp_status = false;
    Boolean PTA = false;
    Date today;
    static String starttime;
    public static String boolsel;
    String stime = "";
    String endtime = "";
    CleverTapAPI cleverTap;


//    selected values
//    ---------------------

    String selectedOrgPrice, selectedListPrice, selectedAge, selectedCondition;
    public static Context CommonContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload2);
        //////Thread.currentThread().setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
        LocalBroadcastManager.getInstance(this).registerReceiver(CommonFinish.broadcastReceiver, new IntentFilter("upload1"));
//        if(!MyApplicationClass.cContextArray.contains(getApplicationContext())) {
        ExternalFunctions.cContextArray[19] = upload1.this;
        stime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        try {
            cleverTap = CleverTapAPI.getInstance(getApplicationContext());
        } catch (CleverTapMetaDataNotFoundException e) {
            // thrown if you haven't specified your CleverTap Account ID or Token in your AndroidManifest.xml
        } catch (CleverTapPermissionsNotSatisfied e) {
            // thrown if you haven’t requested the required permissions in your AndroidManifest.xml
        }
//        }
        //Appsee.start("4fb55809ab45411c909521bda720f8e6");
        //UXCam.startWithKey("1dfb25141864376");         overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        today = new Date();
        DateFormat dfstarttime = new SimpleDateFormat("hh:mm:ss aa");
        dfstarttime.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        starttime = dfstarttime.format(today);
        //////System.out.println("mixpaneltime" + starttime);

        progressBar = (ProgressBar) findViewById(R.id.edit2_progressBar);
        ProductId = getIntent().getIntExtra("productId", 0);
        EditStatus = getIntent().getBooleanExtra("EditStatus", false);
        PTA = getIntent().getBooleanExtra("PTA", false);
        //////System.out.println("EDITSTATUS" + EditStatus);
        ageLayout = (LinearLayout) findViewById(R.id.agelayout);
        conditionLayout = (LinearLayout) findViewById(R.id.conditionLayout);


        View view = findViewById(R.id.upload1_layout);
        FontUtils.setCustomFont(view, getAssets());

        orgPrice = (EditText) findViewById(R.id.fs_originalprice);
        ListPrice = (EditText) findViewById(R.id.fs_enterprice);
        ListPrice.setSelection(ListPrice.getText().length());
        ageSpinner = (Spinner) findViewById(R.id.fs_age);
        conditionSpinner = (Spinner) findViewById(R.id.fs_condition);
        seekBar = (SeekBar) findViewById(R.id.seekBar);

        conditionguide = (TextView) findViewById(R.id.conditionguide);
        SpannableString content = new SpannableString("Condition Guide");
        content.setSpan(new UnderlineSpan(), 0, 15, 0);
        conditionguide.setText(content);
        conditionguide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent cg = new Intent(upload1.this, ConditionGuide.class);
                cg.putExtra("activity", "upload1");
                startActivityForResult(cg, ACTION_REQUEST_CONDITION_GUIDE);
            }
        });
        desc_head = (TextView) findViewById(R.id.desc_head);
        descriptionText = (TextView) findViewById(R.id.descriptionText);
        startPrice = (TextView) findViewById(R.id.startPrice);
        startPrice.setText("0");
        endprice = (TextView) findViewById(R.id.endprice);
        endprice.setVisibility(View.INVISIBLE);
        earning = (TextView) findViewById(R.id.fs_earning);
        earning.setText(getResources().getString(R.string.Rs) + " 0");


        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final View customView = getLayoutInflater().inflate(R.layout.product_bar, null);
        getSupportActionBar().setCustomView(customView);
        Toolbar parent = (Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0, 0);
        TextView tv_headTitle = (TextView) findViewById(R.id.product_title_text);
        tv_headTitle.setText("FASHION CALCULATOR");


        ImageView Skip = (ImageView) findViewById(R.id.productfeedButton);
        Skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (GetSharedValues.getUsertype(upload1.this).equals("store_front")) {
            ageLayout.setVisibility(View.GONE);
            conditionLayout.setVisibility(View.GONE);
            conditionguide.setVisibility(View.GONE);
            descriptionText.setVisibility(View.GONE);
            desc_head.setVisibility(View.GONE);
//            if (EditStatus) {
//                getEditData(ProductId);
//            }
//
            GetData();

        } else {
            GetData();
        }


//        listeners
//        --------------------

        ListPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    seekBar.setProgress(Integer.parseInt(s.toString()));
                } catch (Exception ex) {
                }
            }

            @Override
            public void afterTextChanged(Editable s) {


                //////System.out.println("inside lisprice onclick__" + s);
                if (s.length() > 0 && Integer.parseInt(s.toString()) <= New_LPrice) {
                    seekBar.setProgress(Integer.parseInt(s.toString()));
                    lp_status = true;
                } else {
                    ListPrice.setText(String.valueOf(New_LPrice));
                    lp_status = false;
                }
            }
        });


        orgPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    lp_status = true;
                    if (GetSharedValues.getUsertype(upload1.this).equals("store_front")) {
                        selectedOrgPrice = orgPrice.getText().toString();
                        orgPrice.setSelection(selectedOrgPrice.length());
                        New_LPrice = Integer.parseInt(selectedOrgPrice);
                        seekBar.setMax(New_LPrice);
                        seekBar.setProgress(New_LPrice);
                        endprice.setVisibility(View.VISIBLE);
                        endprice.setText(String.valueOf(New_LPrice));
                        earning.setText(getResources().getString(R.string.Rs) + " " + String.valueOf(New_LPrice * ((100 - EarningPercentage) / 100)));
                    } else {
                        selectedOrgPrice = orgPrice.getText().toString();
//                        fashionCalculator();
                        if (ageSpinner.getSelectedItemPosition() > 0 && conditionSpinner.getSelectedItemPosition() > 0) {
                            orgPrice.setSelection(selectedOrgPrice.length());
                            LPrice = FashionCalculator.GetPrice(ageposition, fc_condition, Double.parseDouble(selectedOrgPrice));
                            New_LPrice = (int) LPrice.floatValue();
                            seekBar.setMax(New_LPrice);
                            seekBar.setProgress(New_LPrice);
                            endprice.setVisibility(View.VISIBLE);
                            endprice.setText(String.valueOf(New_LPrice));
                            earning.setText(getResources().getString(R.string.Rs) + " " + String.valueOf(New_LPrice * ((100 - EarningPercentage) / 100)));
                        }

                    }
                }
            }
        });


        ageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                View view = upload1.this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                if (!GetSharedValues.getUsertype(upload1.this).equals("store_front")) {
                    //////System.out.println("inside sgeapinner__" + ageSpinner.getSelectedItem().toString());
                    ageposition = position - 1;
                    String test = ageSpinner.getSelectedItem().toString();
                    selectedAge = ageMap.get(test).toString();
                    fc_age = ageSpinner.getSelectedItem().toString();
                    orgPrice.setText(selectedOrgPrice);


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        conditionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                View view = upload1.this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                if (!GetSharedValues.getUsertype(upload1.this).equals("store_front")) {
                    String test = conditionSpinner.getSelectedItem().toString();
                    selectedCondition = conditionMap.get(test).toString();
                    fc_condition = conditionSpinner.getSelectedItem().toString();
                    orgPrice.setText(selectedOrgPrice);
//                    fashionCalculator();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                lp_status = true;
                ListPrice.setText(String.valueOf(progress));
                ListPrice.setSelection(ListPrice.getText().length());
                if (progress > 0) {
                    earning.setText(getResources().getString(R.string.Rs) + " " + String.valueOf(Integer.parseInt(String.valueOf(progress)) * ((100 - EarningPercentage) / 100)));
                }
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
        Runtime.getRuntime().gc();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ACTION_REQUEST_CONDITION_GUIDE:
                    break;
            }
        }
    }


    //    Display functions
//    ----------------------------------------------------------
    public void setSpinners() {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.simple_spinner_item, age);
        ageSpinner.setAdapter(adapter);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
                R.layout.simple_spinner_item, condition);
        conditionSpinner.setAdapter(adapter1);

    }


//    Other functions
//    ------------------------------------------------------------


    public Boolean Fcvalidate() {
        if (String.valueOf(OPrice).length() > 2) {
            if (ageposition != 0) {
                return conditionId != 0;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


//    Get functions from server
//----------------------------------------------------------------

    private void GetData() {
        if (EditStatus) {
            if (PTA) {
                ApiService.getInstance(upload1.this, 1).getData(upload1.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/catalogue/zapfashioncalculator/?p_id=" + ProductId + "&action=p_t_a", "getdata");
            } else {
                ApiService.getInstance(upload1.this, 1).getData(upload1.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/catalogue/zapfashioncalculator/?p_id=" + ProductId, "getdata");

            }
        } else {
            ApiService.getInstance(upload1.this, 1).getData(upload1.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/catalogue/zapfashioncalculator/?p_id=" + ProductId + "&action=p_t_a", "getdata");
        }

    }

    public void getEditData(int ProductId) {
        progressBar.setVisibility(View.VISIBLE);
        if (PTA) {
            ApiService.getInstance(upload1.this, 1).getData(upload1.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/upload/album/" + ProductId + "/2/?action=p_t_a", "getEditdata");
        } else {
            ApiService.getInstance(upload1.this, 1).getData(upload1.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/upload/album/" + ProductId + "/2/", "getEditdata");
        }
    }


//    Onclick functions
//    -------------------------------------------------------------

    public void uploadNext2(View v) {

        if (GetSharedValues.getUsertype(upload1.this).equals("store_front")) {
            selectedOrgPrice = orgPrice.getText().toString();
            selectedListPrice = ListPrice.getText().toString();
            if (selectedOrgPrice.length() > 0 && !selectedOrgPrice.isEmpty() && selectedListPrice.length() > 0 && !selectedListPrice.isEmpty()) {
                if (Integer.parseInt(selectedOrgPrice) > 0) {
                    JSONObject data = new JSONObject();
                    try {
                        data.put("original_price", selectedOrgPrice);
                        data.put("listing_price", selectedListPrice);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (!EditStatus) {
                        postData(data);
                    } else {
                        editData(data);
                    }
                } else {
                    CustomMessage.getInstance().CustomMessage(upload1.this, "Invalid amount");
                }
            } else {
                CustomMessage.getInstance().CustomMessage(upload1.this, "Fill all fields");
            }
        } else {
            selectedOrgPrice = orgPrice.getText().toString();
            selectedListPrice = ListPrice.getText().toString();
            if (selectedOrgPrice.length() > 0 && !selectedOrgPrice.isEmpty() && selectedListPrice.length() > 0 && !selectedListPrice.isEmpty() && selectedAge.length() > 0 && selectedCondition.length() > 0) {
                if (Integer.parseInt(selectedOrgPrice) > 0) {
                    JSONObject data = new JSONObject();
                    try {
                        data.put("original_price", selectedOrgPrice);
                        data.put("listing_price", selectedListPrice);
                        data.put("age", selectedAge);
                        data.put("condition", selectedCondition);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (!EditStatus) {
                        postData(data);
                    } else {
                        editData(data);
                    }
                } else {
                    CustomMessage.getInstance().CustomMessage(upload1.this, "Invalid amount");
                }
            } else {
                CustomMessage.getInstance().CustomMessage(upload1.this, "Fill all fields");
            }
        }
    }


//    Server requests
//    --------------------------------------------------------------

    private void postData(JSONObject data) {
        //////System.out.println("post data__" + data);
        ApiService.getInstance(upload1.this, 1).postData(upload1.this, EnvConstants.APP_BASE_URL + "/upload/album/" + ProductId + "/", data, SCREEN_NAME, "postdata");
    }

    private void editData(JSONObject data) {
        ////System.out.println("data:" + data);
        if (PTA) {
            //////System.out.println("inside pta check");
            ApiService.getInstance(upload1.this, 1).putData(upload1.this, EnvConstants.APP_BASE_URL + "/upload/album/" + ProductId + "/2/?action=p_t_a", data, SCREEN_NAME, "editdata");
        } else {
            ApiService.getInstance(upload1.this, 1).putData(upload1.this, EnvConstants.APP_BASE_URL + "/upload/album/" + ProductId + "/2/", data, SCREEN_NAME, "editdata");
        }
    }

//    Server responses
//    ---------------------------------------------------------------

    @Override
    public void onResponseCallback(JSONObject response, String flag) {
        if (flag.equals("getdata")) {
            //////System.out.println(response);
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        JSONObject data = resp.getJSONObject("data");

                        JSONObject calculator = data.getJSONObject("calculator");
                        FashionCalculator.New_with_tags.clear();
                        FashionCalculator.Pre_Loved.clear();
                        FashionCalculator.Pre_Pre_Loved.clear();
                        FashionCalculator.Further.clear();
                        for (int i = 0; i < 4; i++) {
                            FashionCalculator.New_with_tags.add(calculator.getJSONObject(String.valueOf(i)).getDouble("0"));
                            FashionCalculator.Pre_Loved.add(calculator.getJSONObject(String.valueOf(i)).getDouble("1"));
                            FashionCalculator.Pre_Pre_Loved.add(calculator.getJSONObject(String.valueOf(i)).getDouble("2"));
                            FashionCalculator.Further.add(calculator.getJSONObject(String.valueOf(i)).getDouble("3"));


                            ////System.out.println("New_with_tags:" + calculator.getJSONObject(String.valueOf(i)).getDouble("0"));
                            ////System.out.println("Pre_Loved:" + calculator.getJSONObject(String.valueOf(i)).getDouble("1"));
                            ////System.out.println("Pre_Pre_Loved:" + calculator.getJSONObject(String.valueOf(i)).getDouble("2"));
                            ////System.out.println("Further:" + calculator.getJSONObject(String.valueOf(i)).getDouble("3"));
                        }

                        EarningPercentage = data.getDouble("percentage_commission");

                        if (!GetSharedValues.getUsertype(upload1.this).equals("store_front")) {
                            JSONObject metrics_data = data.getJSONObject("metrics_data");
                            JSONArray AGE = metrics_data.getJSONArray("ages");
                            ageMap.put("Select age", 0);
                            age.add("Select age");
                            for (int i = 0; i < AGE.length(); i++) {
                                ageMap.put(AGE.getJSONObject(i).getString("name"), AGE.getJSONObject(i).getInt("id"));
                                age.add(AGE.getJSONObject(i).getString("name"));
                            }

                            JSONArray CONDITION = metrics_data.getJSONArray("conditions");
                            conditionMap.put("Select condition", 0);
                            condition.add("Select condition");
                            for (int i = 0; i < CONDITION.length(); i++) {
                                conditionMap.put(CONDITION.getJSONObject(i).getString("name"), CONDITION.getJSONObject(i).getInt("id"));
                                condition.add(CONDITION.getJSONObject(i).getString("name"));
                                FashionCalculator.conditionTags.add(CONDITION.getJSONObject(i).getString("name"));
                            }

                            setSpinners();
                        }
                        if (EditStatus) {
                            //////System.out.println("insideeeeeeee__" + ProductId);
                            getEditData(ProductId);
                        }
                    } else {
                        CustomMessage.getInstance().CustomMessage(upload1.this, "Oops. Something went wrong!");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    CustomMessage.getInstance().CustomMessage(upload1.this, "Oops. Something went wrong!");
                }
            } else {
                CustomMessage.getInstance().CustomMessage(upload1.this, "Oops. Something went wrong!");
            }

        } else if (flag.equals("postdata")) {
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                //////System.out.println(resp);
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {

                        today = new Date();
                        DateFormat dfclosetime = new SimpleDateFormat("hh:mm:ss aa", Locale.ENGLISH);
                        dfclosetime.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
                        String strclosetime = dfclosetime.format(today);

                        Intent upload3 = new Intent(upload1.this, upload2.class);
                        upload3.putExtra("ProductId", ProductId);
                        upload3.putExtra("EditStatus", false);
                        upload3.putExtra("ListingPrice", selectedListPrice);
                        upload3.putExtra("condition", selectedCondition);
                        upload3.putExtra("PTA", false);
                        startActivity(upload3);
                        finish();
                    } else {
                        CustomMessage.getInstance().CustomMessage(upload1.this, "Oops. Something went wrong!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    CustomMessage.getInstance().CustomMessage(upload1.this, "Oops. Something went wrong!");

                }
            } else {
                CustomMessage.getInstance().CustomMessage(upload1.this, "Oops. Something went wrong!");

            }
        } else if (flag.equals("getEditdata")) {
            //////System.out.println("Editstatus " + response);
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                //////System.out.println(resp);
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        JSONObject data = resp.getJSONObject("data");

                        if (GetSharedValues.getUsertype(upload1.this).equals("store_front")) {

                            if (!data.isNull("original_price")) {
                                String orgP = data.getString("original_price");
                                int op = (int) Double.parseDouble(orgP);
                                int spaceIndex = orgP.indexOf(".");
                                if (spaceIndex != -1) {
                                    orgPrice.setText(String.valueOf(op));
                                    seekBar.setMax(data.getInt("listing_price"));
                                    seekBar.setProgress(data.getInt("listing_price"));
                                    endprice.setVisibility(View.VISIBLE);
                                    endprice.setText(String.valueOf(data.getInt("listing_price")));
                                    earning.setText(getResources().getString(R.string.Rs) + " " + String.valueOf(data.getInt("listing_price") * ((100 - EarningPercentage) / 100)));

                                } else {
                                    orgPrice.setText(orgP);
                                    ListPrice.setText(data.getInt("listing_price"));
                                    seekBar.setMax(data.getInt("listing_price"));
                                    seekBar.setProgress(data.getInt("listing_price"));
                                    endprice.setVisibility(View.VISIBLE);
                                    endprice.setText(String.valueOf(data.getInt("listing_price")));
                                    earning.setText(getResources().getString(R.string.Rs) + " " + String.valueOf(data.getInt("listing_price") * ((100 - EarningPercentage) / 100)));
                                }
                            } else {
                                orgPrice.setText("0");
                                ListPrice.setText("0");
                                seekBar.setMax(0);
                                seekBar.setProgress(0);
                                endprice.setVisibility(View.GONE);
//                                endprice.setText(String.valueOf(data.getInt("listing_price")));
                                earning.setText(getResources().getString(R.string.Rs) + " " + String.valueOf(0));

                            }


                        } else {
                            if (!data.isNull("original_price")) {
                                String orgP = data.getString("original_price");
                                int op = (int) Double.parseDouble(orgP);
                                ageposition = age.indexOf(data.getString("age"));
                                fc_condition = data.getString("condition");
                                int spaceIndex = orgP.indexOf(".");
                                if (spaceIndex != -1) {
                                    orgPrice.setText(String.valueOf(op));
                                    ListPrice.setText(String.valueOf(data.getInt("listing_price")));
                                    seekBar.setProgress(data.getInt("listing_price"));
                                    ageSpinner.setSelection(age.indexOf(data.getString("age")));
                                    conditionSpinner.setSelection(condition.indexOf(data.getString("condition")));
                                    seekBar.setMax(data.getInt("listing_price"));
                                    endprice.setText(String.valueOf(data.getString("listing_price")));
                                    earning.setText(getResources().getString(R.string.Rs) + " " + String.valueOf(data.getInt("listing_price") * ((100 - EarningPercentage) / 100)));
                                    selectedAge = ageMap.get(data.getString("age")).toString();
                                    selectedCondition = conditionMap.get(data.getString("condition")).toString();
//
                                } else {
                                    orgPrice.setText(orgP);
                                    ListPrice.setText(String.valueOf(data.getString("listing_price")));
                                    seekBar.setProgress(data.getInt("listing_price"));
                                    ageSpinner.setSelection(age.indexOf(data.getString("age")));
                                    conditionSpinner.setSelection(condition.indexOf(data.getString("condition")));
                                    seekBar.setMax(data.getInt("listing_price"));
                                    endprice.setText(data.getInt("listing_price"));
                                    earning.setText(getResources().getString(R.string.Rs) + " " + String.valueOf(data.getInt("listing_price") * ((100 - EarningPercentage) / 100)));
                                    selectedAge = ageMap.get(data.getString("age")).toString();
                                    selectedCondition = conditionMap.get(data.getString("condition")).toString();
                                }
                            } else {
                                orgPrice.setText("0");
                                ListPrice.setText("0");
                                seekBar.setProgress(0);
                                ageSpinner.setSelection(0);
                                conditionSpinner.setSelection(0);
                                seekBar.setMax(0);
                                endprice.setVisibility(View.GONE);
                                earning.setText(getResources().getString(R.string.Rs) + " " + String.valueOf(0));

                            }

                        }


                        progressBar.setVisibility(View.GONE);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        CustomMessage.getInstance().CustomMessage(upload1.this, "Oops. Something went wrong!");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                    CustomMessage.getInstance().CustomMessage(upload1.this, "Oops. Something went wrong!");

                }
            } else {
                progressBar.setVisibility(View.GONE);
                CustomMessage.getInstance().CustomMessage(upload1.this, "Oops. Something went wrong!");

            }
        } else if (flag.equals("editdata")) {
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            //////System.out.println(resp);

            if (resp != null) {
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        Intent upload3 = new Intent(upload1.this, upload2.class);
                        upload3.putExtra("EditStatus", true);
                        upload3.putExtra("PTA", PTA);
                        upload3.putExtra("ProductId", ProductId);
                        upload3.putExtra("ListingPrice", selectedListPrice);
                        upload3.putExtra("condition", selectedCondition);
                        startActivity(upload3);
                        finish();
                    } else {
                        CustomMessage.getInstance().CustomMessage(upload1.this, "Oops. Something went wrong!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    CustomMessage.getInstance().CustomMessage(upload1.this, "Oops. Something went wrong!");
                }
            } else {
                CustomMessage.getInstance().CustomMessage(upload1.this, "Oops. Something went wrong!");
            }
        }

    }

    @Override
    public void onErrorCallback(VolleyError error, String flag) {
        CustomMessage.getInstance().CustomMessage(upload1.this, "Oops. Something went wrong!");

    }


    @Override
    public void onBackPressed() {
        if (!EditStatus) {
            Intent upload2 = new Intent(upload1.this, upload.class);
            upload2.putExtra("EditStatus", true);
            upload2.putExtra("ProductId", ProductId);
            upload2.putExtra("PTA", true);
            startActivity(upload2);
            finish();
        } else {
            Intent upload1 = new Intent(activity.upload1.this, upload.class);
            upload1.putExtra("EditStatus", true);
            upload1.putExtra("ProductId", ProductId);
            upload1.putExtra("PTA", PTA);
            startActivity(upload1);
            finish();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!GetSharedValues.GetgcmId(upload1.this).equals("")) {
            ApiService.getInstance(upload1.this, 1).getData(upload1.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/?gcm_reg_id=" + GetSharedValues.GetgcmId(upload1.this), "session");
        } else {
            ApiService.getInstance(upload1.this, 1).getData(upload1.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/", "session");
        }
    }

    public class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
//            MyApplicationClass.sFirstRun = false;
            Intent intent = new Intent("MainFeed");
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
}

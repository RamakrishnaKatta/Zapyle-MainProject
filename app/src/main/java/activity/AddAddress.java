package activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.exceptions.CleverTapMetaDataNotFoundException;
import com.clevertap.android.sdk.exceptions.CleverTapPermissionsNotSatisfied;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
//import com.uxcam.UXCam;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import application.MyApplicationClass;
import network.ApiCommunication;
import network.ApiService;
import network.JsonParser;
import utils.CustomMessage;
import utils.GetSharedValues;

//import com.//Appsee.////Appsee;


/**
 * Created by haseeb on 28/1/16.
 */
public class AddAddress extends ActionBarActivity implements ApiCommunication{

    public String SCREEN_NAME = "ADD_ADDRESS";
    ProgressBar progressbar;

    HashMap<String, Integer> states = new HashMap<String, Integer>();
    ArrayList<String> stateName = new ArrayList<String>();

    EditText a_name, a_address, a_city, a_postcode, a_phonenumber;
    String name, address, city, postcode, phonenumber, state;

    Spinner a_stateSpinner;

    Boolean EditStatus = false;
    Button AddButton;
    int EditId = 0;
    Tracker mTracker;
    CleverTapAPI cleverTap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addaddress);

        MyApplicationClass application = (MyApplicationClass) getApplication();
        mTracker = application.getDefaultTracker();
        try {
            cleverTap = CleverTapAPI.getInstance(getApplicationContext());
        } catch (CleverTapMetaDataNotFoundException e) {
            // thrown if you haven't specified your CleverTap Account ID or Token in your AndroidManifest.xml
        } catch (CleverTapPermissionsNotSatisfied e) {
            // thrown if you haven’t requested the required permissions in your AndroidManifest.xml
        }



        //UXCam.startWithKey("1dfb25141864376");
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        ////Appsee.start("4fb55809ab45411c909521bda720f8e6");
        ////////Thread.currentThread().setDefaultUncaughtExceptionHandler(new SplashScreen.MyUncaughtExceptionHandler());
        progressbar = (ProgressBar) findViewById(R.id.addaddress_progressBar);

        EditStatus = getIntent().getBooleanExtra("EditStatus", false);
        AddButton = (Button) findViewById(R.id.AddButton);
        GetStates();


        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final View customView = getLayoutInflater().inflate(R.layout.product_bar, null);
        getSupportActionBar().setCustomView(customView);
        Toolbar parent = (Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0, 0);
        TextView tv_headTitle = (TextView) findViewById(R.id.product_title_text);
        if (EditStatus) {
            tv_headTitle.setText("EDIT ADDRESS");
        } else {
            tv_headTitle.setText("ADD ADDRESS");

        }


        ImageView Skip = (ImageView) findViewById(R.id.productfeedButton);
        Skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        a_name = (EditText) findViewById(R.id.edittext_name);
        a_address = (EditText) findViewById(R.id.edittext_Address);
        a_city = (EditText) findViewById(R.id.edittext_city);
        a_stateSpinner = (Spinner) findViewById(R.id.spinner_state);
        a_postcode = (EditText) findViewById(R.id.edittext_postcode);
        a_phonenumber = (EditText) findViewById(R.id.edittext_phone);


        if (EditStatus) {
            name = getIntent().getStringExtra("EditName");
            address = getIntent().getStringExtra("EditAddress");

            state = getIntent().getStringExtra("EditState");
            city = getIntent().getStringExtra("EditCity");
            phonenumber = getIntent().getStringExtra("EditPhone");
            postcode = getIntent().getStringExtra("EditPincode");
            EditId = getIntent().getIntExtra("EditId", 0);

        }

        a_postcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 6){
                    new PincodeAutofill().execute(Integer.parseInt(s.toString()));
                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.set("UserName", GetSharedValues.getUsername(getApplicationContext()));mTracker.set("ZapUsername", GetSharedValues.getZapname(getApplicationContext()));   mTracker.setScreenName("Add address");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    //    Onclick functions
//    -----------------------------------------------------------------

    public void onSaveAddress(View v) {
        AddButton.setEnabled(false);
        if (!EditStatus) {
            String name = a_name.getText().toString();
            String address = a_address.getText().toString();
            String city = a_city.getText().toString();
            String phone = a_phonenumber.getText().toString();
            String pincode = a_postcode.getText().toString();
            String state = a_stateSpinner.getSelectedItem().toString();
            int s_state = states.get(state);

            ////////System.out.println(name + "__" + address + "__" + city + "__" + phone + "__" + pincode + "__" + state);

            if (name.trim().length() != 0 && address.trim().length() != 0 && city.trim().length() != 0 && phone.trim().length() != 0 && pincode.trim().length() != 0) {
                if (s_state > 0) {
                    if (pincode.trim().length() == 6) {

                        HashMap<String, Object> pincode_check = new HashMap<String, Object>();
                        pincode_check.put("pincode_checked", pincode);
                        cleverTap.event.push("pincode_check", pincode_check);
                        if (phone.trim().length() >= 10) {
                            postAddress();


                        } else {
                            AddButton.setEnabled(true);
                            CustomMessage.getInstance().CustomMessage(AddAddress.this, "Incorrect phone number");

                        }


                    } else {
                        AddButton.setEnabled(true);
                        CustomMessage.getInstance().CustomMessage(AddAddress.this, "Incorrect pincode");
                    }
                } else {
                    AddButton.setEnabled(true);
                    CustomMessage.getInstance().CustomMessage(AddAddress.this, "Please select state");
                }

            } else {
                AddButton.setEnabled(true);
                CustomMessage.getInstance().CustomMessage(AddAddress.this, "Please fill all the required fields.");
            }
        } else {
            updateToServer();
        }
    }


//    Other functions
//    ------------------------------------------------------------------

    private void setDataToSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.simple_spinner_item, stateName);
        a_stateSpinner.setAdapter(adapter);
        if (EditStatus) {
            a_name.setText(name);
            a_address.setText(address);
            a_stateSpinner.setSelection(stateName.indexOf(state));
            a_city.setText(city);
            a_phonenumber.setText(phonenumber);
            a_postcode.setText(postcode);
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

        ////////System.out.println("data for sending__" + data);
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
        ApiService.getInstance(AddAddress.this, 1).postData(AddAddress.this, EnvConstants.APP_BASE_URL + "/address/crud/", addressObject, SCREEN_NAME, "Add");

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
            addressObject.put("address_id", EditId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ////////System.out.println(addressObject + "____");
        ApiService.getInstance(AddAddress.this, 1).putData(AddAddress.this, EnvConstants.APP_BASE_URL + "/address/crud/", addressObject, SCREEN_NAME, "Put");

    }


//    server resquests
//    ------------------------------------------------------------------

    private void GetStates() {
        ApiService.getInstance(AddAddress.this, 1).getData(AddAddress.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/address/get_states/", "getstates");
    }



    @Override
    protected void onRestart() {
        super.onRestart();
        if(!GetSharedValues.GetgcmId(AddAddress.this).equals("")) {
            ApiService.getInstance(AddAddress.this, 1).getData(AddAddress.this, true, "ADDADDRESS", EnvConstants.APP_BASE_URL + "/user/session/?gcm_reg_id="+GetSharedValues.GetgcmId(AddAddress.this), "session");
        }
        else {
            ApiService.getInstance(AddAddress.this, 1).getData(AddAddress.this, true, "ADDADDRESS", EnvConstants.APP_BASE_URL + "/user/session/", "session");
        }
    }


    @Override
    public void onResponseCallback(JSONObject response, String flag) {
        if (flag.equals("getstates")) {
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
                        CustomMessage.getInstance().CustomMessage(AddAddress.this, "Oops. Something went wrong!");
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    CustomMessage.getInstance().CustomMessage(AddAddress.this, "Oops. Something went wrong!");
                }

            }
        } else if (flag.equals("Add")) {
            ////////System.out.println(response);
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        JSONObject data = resp.getJSONObject("data");
                        Intent Checkout = new Intent(AddAddress.this, shoppingcartnew.class);
                        Checkout.putExtra("data", data.toString());
                        setResult(RESULT_OK, Checkout);
                        finish();
                        AddButton.setEnabled(true);
                    } else {
                        AddButton.setEnabled(true);
                        JSONObject detail = resp.getJSONObject("detail");
                        CustomMessage.getInstance().CustomMessage(AddAddress.this, detail.getJSONArray("pincode").getString(0));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    AddButton.setEnabled(true);
                    CustomMessage.getInstance().CustomMessage(AddAddress.this, "Oops. Something went wrong!");
                }
            } else {
                AddButton.setEnabled(true);
                CustomMessage.getInstance().CustomMessage(AddAddress.this, "Oops. Something went wrong!");
            }
        } else if (flag.equals("Put")) {
            ////////System.out.println(response);
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        JSONObject data = resp.getJSONObject("data");
                        Intent Checkout = new Intent(AddAddress.this, shoppingcartnew.class);
                        Checkout.putExtra("edited_data", data.toString());
                        setResult(RESULT_OK, Checkout);
                        finish();
                        AddButton.setEnabled(true);
                    } else {
                        AddButton.setEnabled(true);
                        JSONObject detail = resp.getJSONObject("detail");
                        CustomMessage.getInstance().CustomMessage(AddAddress.this, detail.getJSONArray("pincode").getString(0));                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    AddButton.setEnabled(true);
                    CustomMessage.getInstance().CustomMessage(AddAddress.this, "Oops. Something went wrong!");
                }
            } else {
                AddButton.setEnabled(true);
                CustomMessage.getInstance().CustomMessage(AddAddress.this, "Oops. Something went wrong!");
            }
        }   else if (flag.equals("pincodeAutofill")){
            JSONObject PincodeObject = JsonParser.getInstance().parserJsonObject(response);
            System.out.println("PINCODE : 2"+PincodeObject);
            try {
                String status = PincodeObject.getString("status");
//                if (addaddressform.getVisibility() == View.VISIBLE){
                if (status.equals("success")){
                    JSONObject data = PincodeObject.getJSONObject("data");
                    String city = data.getString("city");
                    String state = data.getString("state");
                    a_city.setText(city);
                    a_stateSpinner.setSelection(stateName.indexOf(state));
                }
                else {
                    CustomMessage.getInstance().CustomMessage(AddAddress.this, PincodeObject.getString("detail"));
                    a_city.setText("");
                    a_stateSpinner.setSelection(0);
                }
//                }



            } catch (JSONException e) {
                e.printStackTrace();
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

    }


    private class PincodeAutofill extends AsyncTask<Integer,View,JSONObject> {

        @Override
        protected JSONObject doInBackground(Integer... params) {
            ApiService.getInstance(AddAddress.this, 1).getData(AddAddress.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/address/pincode/?pincode="+params[0], "pincodeAutofill");
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            System.out.println("PINCODE : "+jsonObject);
        }
    }
}

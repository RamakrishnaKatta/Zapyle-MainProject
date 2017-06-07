package activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
//import com.//Appsee.//Appsee;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
//import com.uxcam.UXCam;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import application.MyApplicationClass;
import network.ApiCommunication;
import network.ApiService;
import utils.CustomMessage;
import utils.FontUtils;
import utils.GetSharedValues;

public class Mybankcash extends AppCompatActivity implements ApiCommunication {
    String SCREEN_NAME = "MYBANKACCOUNT";
    TextView tvaccount,tvifsc,tvnext,tvcancel,tvaccountname;
    EditText edtaccount,edtifsc,edtaccountname;
    int intedit=0;
    RelativeLayout rlbottom;
    int intwidth;
    static  int intcheck=1;
    Tracker mTracker;

//    String callingActivity = "activity.HomePage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mybankcash);
        MyApplicationClass application = (MyApplicationClass) getApplication();
        mTracker = application.getDefaultTracker();
        //Appsee.start("4fb55809ab45411c909521bda720f8e6");
//        try {
//            callingActivity = "activity." + getIntent().getStringExtra("activity");
//        }
//        catch (Exception e){
//            callingActivity = "activity.HomePage";
//        }


        //UXCam.startWithKey("1dfb25141864376");         overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        //////Thread.currentThread().setDefaultUncaughtExceptionHandler(new SplashScreen.MyUncaughtExceptionHandler());
        tvaccount=(TextView)findViewById(R.id.tvaccount);
        tvaccountname=(TextView)findViewById(R.id.tvaccountname);
        tvifsc=(TextView)findViewById(R.id.tvifsc);
        edtaccount=(EditText)findViewById(R.id.edtaccount);
        edtaccountname=(EditText)findViewById(R.id.edtaccountname);
        edtifsc=(EditText)findViewById(R.id.edtifsc);
        edtifsc.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        View customView = getLayoutInflater().inflate(R.layout.profile_actionbar, null);
        getSupportActionBar().setCustomView(customView);
        Toolbar parent = (Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0, 0);
        TextView tvbartitle=(TextView)findViewById(R.id.product_title_text);
        tvbartitle.setText("MY BANK ACCOUNT");
//        rlbottom=(RelativeLayout)findViewById(R.id.bottomLayout3);
//        rlbottom.setVisibility(View.GONE);
        ImageView imgback=(ImageView)findViewById(R.id.profilebackButton);
        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        intwidth = metrics.widthPixels;
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(intwidth/2+10, LinearLayout.LayoutParams.WRAP_CONTENT);
        params1.setMargins(5,0,0,0);
        params1.gravity=Gravity.CENTER_VERTICAL;
        edtaccount.setLayoutParams(params1);
        edtaccountname.setLayoutParams(params1);
        edtifsc.setLayoutParams(params1);
        tvnext=(TextView)findViewById(R.id.next);
        tvcancel=(TextView)findViewById(R.id.cancel);
        View view = findViewById(R.id.rl);
        FontUtils.setCustomFont(view, getAssets());
        GetMybankaccount();
      
        intedit = 1;
        tvaccount.setVisibility(View.GONE);
        tvaccountname.setVisibility(View.GONE);
        edtaccount.setVisibility(View.VISIBLE);
        edtaccountname.setVisibility(View.VISIBLE);
        tvifsc.setVisibility(View.GONE);
        edtifsc.setVisibility(View.VISIBLE);
        edtaccountname.requestFocus();
        edtaccount.setSelection(0, edtaccount.getText().length());

        tvnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtaccountname.getText().length() < 2) {
                   CustomMessage.getInstance().CustomMessage(Mybankcash.this, "Please add account name");
                    edtaccountname.requestFocus();
                }
                else if (edtaccount.getText().length() < 2) {
                   CustomMessage.getInstance().CustomMessage(Mybankcash.this, "Please add your account number");
                    edtaccount.requestFocus();
                } else if (edtifsc.getText().length() < 2) {
                   CustomMessage.getInstance().CustomMessage(Mybankcash.this, "Please add IFSC CODE ");
                    edtifsc.requestFocus();
                }
                else {
                    tvnext.setEnabled(false);
                    InfToServer();
                }


            }


        });

        tvcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvnext.setEnabled(true);
                edtaccountname.setText(tvaccountname.getText().toString());
                edtaccount.setText(tvaccount.getText().toString());
                edtifsc.setText(tvifsc.getText().toString());

            }
        });




    }
    private void GetMybankaccount() {
        // //////System.out.println("xxxx" + EnvConstants.APP_BASE_URL + "/user/myzap/");

        ApiService.getInstance(Mybankcash.this, 1).getData(Mybankcash.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/accountnumber/", "getbank");


    }

    private void InfToServer() {
        JSONObject data=new JSONObject();

        //////System.out.println("data for sending__" + data);
        try {
            data.put("account_number",edtaccount.getText().toString());
            data.put("ifsc_code",edtifsc.getText().toString());
            data.put("account_holder",edtaccountname.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //////System.out.println( EnvConstants.APP_BASE_URL + "/user/myinfo/"+ data);
        ApiService.getInstance(Mybankcash.this, 1).postData(Mybankcash.this, EnvConstants.APP_BASE_URL + "/user/accountnumber/", data, SCREEN_NAME, "datatoserver");

    }
    @Override
    public void onResponseCallback(JSONObject response, String flag) {
        //////System.out.println("responsebank___" + response);

        if (flag.equals("datatoserver")) {
            //////System.out.println("response_____" + response);
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {

                        //CustomMessage  CustomMessage.getInstance().CustomMessage(Mybankcash.this, "Updated successfully");
                        Intent mypref = new Intent(Mybankcash.this, Myaccountpage.class);
                        mypref.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mypref);
                        finish();
                        tvnext.setEnabled(true);
                    }
                    else{
                        tvnext.setEnabled(true);
                        String [] errorlist={"fb_friends_list","account_number","ifsc_code"};
                        int intcheck=0;
                        JSONObject detail = resp.getJSONObject("detail");
                        for(int i=0;i<errorlist.length;i++) {
                            try{
                                intcheck=0;

                                for (int j = 0; j < detail.getJSONArray(errorlist[i]).length(); j++) {

                                  CustomMessage.getInstance().CustomMessage(this, detail.getJSONArray(errorlist[i]).getString(0));
                                    intcheck=1;

                                }
                            }   catch (Exception e)   {

                            }

                            if (intcheck==1) {
                                i=errorlist.length;
                            }

                        }
                    }
                } catch (JSONException e) {
                    tvnext.setEnabled(true);
                    e.printStackTrace();
                    Intent mydialog = new Intent(Mybankcash.this, AlertActivity.class);
                    //int imgid=R.drawable.alertoop;
                    String strmessage="OOPS!";

                    mydialog.putExtra("Message",strmessage);
                    mydialog.putExtra("Buttontext"," RETRY ");
                    mydialog.putExtra("tip","SOMETHING WENT WRONG");
                    mydialog.putExtra("activity","Myaccountpage");
                    mydialog.putExtra("calling", "Myaccountpage");
                    startActivity(mydialog);
                    finish();
                }
            }
        }
       else if (flag.equals("getbank")) {
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {

                try {

                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        tvaccountname.setText(resp.getString("account_holder"));
                        tvaccount.setText(resp.getString("user_acc"));
                        tvifsc.setText(resp.getString("ifsc_code"));
                        edtaccountname.setText(resp.getString("account_holder"));
                        edtaccount.setText(resp.getString("user_acc"));
                        edtifsc.setText(resp.getString("ifsc_code"));
                    }
                    else {

                        tvnext.setEnabled(true);
                        if (intcheck == 1) {
                            intcheck = 2;
                            Intent mydialog = new Intent(Mybankcash.this, AlertActivity.class);
                            //int imgid = R.drawable.alertbank;
                            String strmessage = "YOU HAVE NO BANK DETAILS SAVED";
                           // mydialog.putExtra("imgid", imgid);
                            mydialog.putExtra("Message", strmessage);
                            mydialog.putExtra("Buttontext", " ADD BANK DETAILS ");
                            mydialog.putExtra("tip", "");
                            mydialog.putExtra("activity", "Mybankcash");
                            mydialog.putExtra("header", "MY BANK ACCOUNT");
                            startActivity(mydialog);
                            finish();
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Intent mydialog = new Intent(Mybankcash.this, AlertActivity.class);
                    //int imgid=R.drawable.alertoop;
                    String strmessage="OOPS!";

                    mydialog.putExtra("Message",strmessage);
                    mydialog.putExtra("Buttontext","RETRY");
                    mydialog.putExtra("tip","SOMETHING WENT WRONG");
                    mydialog.putExtra("activity","Myaccountpage");
                    mydialog.putExtra("calling", "Myaccountpage");
                    startActivity(mydialog);
                    finish();
                }

            }

        }

    }
    @Override
    public void onBackPressed() {
        Intent dintent = new Intent(Mybankcash.this, Myaccountpage.class);
        startActivity(dintent);
        finish();
    }
    @Override
    public void onErrorCallback(VolleyError error, String flag) {

    }
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(!GetSharedValues.GetgcmId(Mybankcash.this).equals("")) {
            ApiService.getInstance(Mybankcash.this, 1).getData(Mybankcash.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/?gcm_reg_id="+GetSharedValues.GetgcmId(Mybankcash.this), "session");
        }
        else {
            ApiService.getInstance(Mybankcash.this, 1).getData(Mybankcash.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/", "session");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mTracker.set("UserName", GetSharedValues.getUsername(getApplicationContext()));mTracker.set("ZapUsername", GetSharedValues.getZapname(getApplicationContext()));   mTracker.setScreenName("My bankcash");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}

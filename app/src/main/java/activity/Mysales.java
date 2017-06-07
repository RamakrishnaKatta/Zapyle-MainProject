package activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
//import com.//Appsee.//Appsee;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.exceptions.CleverTapMetaDataNotFoundException;
import com.clevertap.android.sdk.exceptions.CleverTapPermissionsNotSatisfied;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
//import com.uxcam.UXCam;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import application.MyApplicationClass;
import models.MysaleListmodel;
import network.ApiCommunication;
import network.ApiService;
import utils.CustomMessage;
import utils.ExternalFunctions;
import utils.FontUtils;
import utils.GetSharedValues;

public class Mysales extends AppCompatActivity implements ApiCommunication {
    String SCREEN_NAME = "MY_SALES";
    ArrayList<MysaleListmodel> dataToAdapter = new ArrayList<MysaleListmodel>();
    ListView lsv;
    Context ctx;
    ProgressBar progressBar;
    RelativeLayout rl;
    int intemtysale;
    Tracker mTracker;

    String stime = "";
    String endtime = "";
    CleverTapAPI cleverTap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mysalespage);
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
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        View customView = getLayoutInflater().inflate(R.layout.profile_actionbar, null);
        getSupportActionBar().setCustomView(customView);
        rl = (RelativeLayout) findViewById(R.id.rl);
        Toolbar parent = (Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0, 0);
        TextView tvbartitle = (TextView) findViewById(R.id.product_title_text);
        tvbartitle.setText("MY SALES");
        ImageView imgback = (ImageView) findViewById(R.id.profilebackButton);
        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ctx = this;
        lsv = (ListView) findViewById(R.id.listView);
        progressBar = (ProgressBar) findViewById(R.id.ob3progressBar);
        progressBar.setVisibility(View.VISIBLE);
        //intemtysale=R.drawable.alertsales;
        GetSales();


    }

    @Override
    public void onBackPressed() {

        Intent dintent = new Intent(Mysales.this, Myaccountpage.class);
        startActivity(dintent);
        finish();
    }

    private void GetSales() {

        ApiService.getInstance(Mysales.this, 1).getData(Mysales.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/mysales/", "getsales");


    }

    @Override
    public void onResponseCallback(JSONObject response, String flag) {


        //////System.out.println("response getsales___" + response);
        if (flag.equals("getsales")) {
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                try {

                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        if (resp.getJSONArray("data").length() > 0) {

                            for (int i = 0; i < resp.getJSONArray("data").length(); i++) {

                                JSONObject data = resp.getJSONArray("data").getJSONObject(i);
                                //////System.out.println("IIIIIIIII" + data.getString("title"));
//                                JSONObject size = data.getJSONObject("size");
                                MysaleListmodel mylist = new MysaleListmodel();
                                mylist.setimagepath(data.getString("image"));
                                mylist.setproductname(data.getString("title"));
                                mylist.setamount(data.getInt("amount"));
                                mylist.setreturn(data.getBoolean("payout"));
                                mylist.setsize(data.getString("size"));
                                //////System.out.println("IIIIIIIII" + size);
                                dataToAdapter.add(mylist);

                            }
                            ListAdapter lst = new ListAdapter(ctx, dataToAdapter);
                            lsv.setAdapter(lst);

                        } else {
                            //CustomMessage.getInstance().CustomMessage(Mysales.this, "You didnt make any order yet");
                            progressBar.setVisibility(View.GONE);
                            Intent mydialog = new Intent(Mysales.this, AlertActivity.class);
                            //int imgid=R.drawable.alertsales;
                            String strmessage = "YOU HAVE'NT SOLD ANYTHING YET?";
                            // mydialog.putExtra("imgid", imgid);
                            mydialog.putExtra("Message", strmessage);
                            mydialog.putExtra("Buttontext", " LET'S SEE MY CLOSET ");
                            mydialog.putExtra("tip", "TIPS:EDIT YOUR ITEMS\n   OR SHARE THEM");
                            mydialog.putExtra("activity", "profile");
                            mydialog.putExtra("header", "MY SALES");
                            mydialog.putExtra("calling", "Myaccountpage");
                            startActivity(mydialog);
                            finish();


                        }
                    } else {

                        progressBar.setVisibility(View.GONE);
                        Intent mydialog = new Intent(Mysales.this, AlertActivity.class);
                        //int imgid=R.drawable.alertoop;
                        String strmessage = "OOPS!";
                        // mydialog.putExtra("imgid", imgid);
                        mydialog.putExtra("Message", strmessage);
                        mydialog.putExtra("Buttontext", " RETRY ");
                        mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                        mydialog.putExtra("activity", "MyProfile");
                        mydialog.putExtra("calling", "Myaccountpage");
                        startActivity(mydialog);
                        finish();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                    Intent mydialog = new Intent(Mysales.this, AlertActivity.class);
                    //int imgid=R.drawable.alertoop;
                    String strmessage = "OOPS!";
                    // mydialog.putExtra("imgid", imgid);
                    mydialog.putExtra("Message", strmessage);
                    mydialog.putExtra("Buttontext", " RETRY ");
                    mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                    mydialog.putExtra("activity", "MyProfile");
                    mydialog.putExtra("calling", "Myaccountpage");
                    startActivity(mydialog);
                    finish();

                }

            } else {
                CustomMessage.getInstance().CustomMessage(Mysales.this, "You didnt make any order yet");
                progressBar.setVisibility(View.GONE);
                Intent mydialog = new Intent(Mysales.this, AlertActivity.class);
                //int imgid=R.drawable.alertoop;
                String strmessage = "OOPS!";
                // mydialog.putExtra("imgid", imgid);
                mydialog.putExtra("Message", strmessage);
                mydialog.putExtra("Buttontext", " RETRY ");
                mydialog.putExtra("tip", "SOMETHING WENT WRONG");
                mydialog.putExtra("activity", "MyProfile");
                mydialog.putExtra("calling", "Myaccountpage");
                startActivity(mydialog);
                finish();
            }


        }

    }

    @Override
    public void onErrorCallback(VolleyError error, String flag) {

    }

    public class ListAdapter extends BaseAdapter {
        ArrayList<MysaleListmodel> strBranList = new ArrayList<MysaleListmodel>();
        Context context;
        // String [] strUnselected;
        private LayoutInflater inflater = null;
        private int intshowsel, intselcount, intunselcount;

        public ListAdapter(Context mainactivity, ArrayList<MysaleListmodel> strBrandList) {
            // TODO Auto-generated constructor stub
            this.strBranList = strBrandList;
            this.context = mainactivity;

            inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return strBranList.size();
        }

        @Override
        public MysaleListmodel getItem(int position) {
            // TODO Auto-generated method stub
            return strBranList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            // TODO Auto-generated method stub
            final MyViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.mysales_list, null);
                holder = new MyViewHolder(convertView);

                convertView.setTag(holder);
            } else {
                holder = (MyViewHolder) convertView.getTag();
            }

            final MysaleListmodel data = getItem(position);

            Glide.with(context)
                    .load(EnvConstants.APP_MEDIA_URL + data.getimagepath())
                    .fitCenter()
                    .placeholder(R.drawable.playholderscreen)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(holder.img);


            //holder.img.setBackgroundResource(R.drawable.insta);
            holder.tvproduct.setText(data.getproductname().toUpperCase());
            holder.tvsize.setText(data.getsize().toUpperCase());
            holder.tvamount.setText(String.valueOf(data.getamount()).toUpperCase());
            if (data.getreturn() == true) {
                holder.tvpaid.setText("Payment recieved");

            } else {
                holder.tvpaid.setVisibility(View.GONE);
            }


            FontUtils.setCustomFont(convertView, context.getAssets());

            progressBar.setVisibility(View.GONE);

            return convertView;
        }


        public class MyViewHolder {
            public TextView tvproduct, tvsize, tvamount, tvpaid;
            public ImageView img;


            public MyViewHolder(View item) {
                tvproduct = (TextView) item.findViewById(R.id.tvpname);
                tvsize = (TextView) item.findViewById(R.id.tvsize);
                tvamount = (TextView) item.findViewById(R.id.tvamount);
                tvpaid = (TextView) item.findViewById(R.id.tvpaidstatus);
                img = (ImageView) item.findViewById(R.id.imgproduct);


            }
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
        if (!GetSharedValues.GetgcmId(Mysales.this).equals("")) {
            ApiService.getInstance(Mysales.this, 1).getData(Mysales.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/?gcm_reg_id=" + GetSharedValues.GetgcmId(Mysales.this), "session");
        } else {
            ApiService.getInstance(Mysales.this, 1).getData(Mysales.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/", "session");
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
    protected void onResume() {
        super.onResume();
        mTracker.set("UserName", GetSharedValues.getUsername(getApplicationContext()));
        mTracker.set("ZapUsername", GetSharedValues.getZapname(getApplicationContext()));
        mTracker.setScreenName("My sales");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}

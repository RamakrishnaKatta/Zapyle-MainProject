package activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import models.TrackerModel;
import network.ApiCommunication;
import network.ApiService;
import network.JsonParser;
import utils.CustomMessage;
import utils.ExternalFunctions;

/**
 * Created by haseeb on 7/9/16.
 */
public class OrderTrack extends ActionBarActivity implements ApiCommunication {


    int orderID = 0;
    TextView tv_title,
            tv_qty,
            tv_size,
            tv_orderID,
            tv_seller,
            tv_date,
            tv_amount,
            tv_paymentmode,
            tv_zapcash,
            tv_buyer,
            tv_address,
            tv_headerTitle,
            tv_toggleDetails,
            tv_headerStatus,
            tv_description,
            tv_cta,
            tv_returnPolicy,
            tv_gotit,
            tv_submit_rating;


   // DotsTextView dots;

    LinearLayout lv_11, lv_12, lv_21, lv_22, lv_31, lv_32, lv_tracker, lv_detailLayout, lv_dotLayout;
    ProgressBar progressBar;
    ScrollView orderScrollView;
    RecyclerView recyclerview;
    JSONObject data;
    ArrayList<TrackerModel> trackerData = new ArrayList<TrackerModel>();
    int currentStep = 0, productId = 0;
    String description, current_title;
    RatingBar rating;
    SharedPreferences sharedPref;
    String reaseonStep = null;
    public int DIALOGE_SHOW = 0;
    public int DIALOGE_HIDE = 1;
    public int Rating = 0;
    Boolean ratingStatus = false, ctaStatus = false;
    String stime = "";
    String endtime = "";
    CleverTapAPI cleverTap;
    String SCREEN_NAME = "ORDER_TRACK";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myordertrack);

        ConfigureHeader();

        progressBar = (ProgressBar) findViewById(R.id.progress);
        orderScrollView = (ScrollView) findViewById(R.id.orderScrollView);
        progressBar.setVisibility(View.VISIBLE);
        orderScrollView.setVisibility(View.INVISIBLE);

        DefineWidgets();

        orderID = Integer.parseInt(getIntent().getStringExtra("orderID"));
        GetData();
        stime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        try {
            cleverTap = CleverTapAPI.getInstance(getApplicationContext());
        } catch (CleverTapMetaDataNotFoundException e) {
            // thrown if you haven't specified your CleverTap Account ID or Token in your AndroidManifest.xml
        } catch (CleverTapPermissionsNotSatisfied e) {
            // thrown if you haven’t requested the required permissions in your AndroidManifest.xml
        }
        tv_toggleDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = tv_toggleDetails.getTag().toString();
                switch (tag) {
                    case "HIDDEN":
                        System.out.println("inside fir");
                        tv_toggleDetails.setTag("SHOWING");
                        handleDialogueUiThread(1);
                        break;

                    case "SHOWING":
                        System.out.println("inside sec");
                        handleDialogueUiThread(0);
                        tv_toggleDetails.setTag("HIDDEN");
                        break;

                    default:
                        System.out.println("inside def");
                        handleDialogueUiThread(1);
                        break;
                }
            }
        });

        tv_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent product = new Intent(OrderTrack.this, activity.product.class);
                product.putExtra("album_id", productId);
                startActivity(product);
                finish();
            }
        });
    }

    private void ConfigureHeader() {
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        View customView = getLayoutInflater().inflate(R.layout.ordertrack_actionbar, null);
        getSupportActionBar().setCustomView(customView);
        Toolbar parent = (Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0, 0);
        tv_headerTitle = (TextView) customView.findViewById(R.id.product_title_text);
        ImageView back = (ImageView) customView.findViewById(R.id.tracker_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void DefineWidgets() {
//        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        lv_tracker = (LinearLayout) findViewById(R.id.tracker_pad);

        rating = (RatingBar) findViewById(R.id.rating);
        rating.setVisibility(View.GONE);

        JSONObject screenSize = ExternalFunctions.displaymetrics(this);
        int screenWidth = screenSize.optInt("width");
        lv_11 = (LinearLayout) findViewById(R.id.o_l1_1);
        lv_12 = (LinearLayout) findViewById(R.id.o_l1_2);
        lv_21 = (LinearLayout) findViewById(R.id.o_l2_1);
        lv_22 = (LinearLayout) findViewById(R.id.o_l2_2);
        lv_31 = (LinearLayout) findViewById(R.id.o_l3_1);
        lv_32 = (LinearLayout) findViewById(R.id.o_l3_2);
        lv_dotLayout = (LinearLayout) findViewById(R.id.dotLayout);
        lv_dotLayout.setVisibility(View.GONE);
        lv_detailLayout = (LinearLayout) findViewById(R.id.orderDetailsLayout);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((screenWidth / 2) - 10, ViewGroup.LayoutParams.WRAP_CONTENT);
        lv_11.setLayoutParams(lp);
        lv_12.setLayoutParams(lp);
        lv_21.setLayoutParams(lp);
        lv_22.setLayoutParams(lp);
        lv_31.setLayoutParams(lp);
        lv_32.setLayoutParams(lp);


//        dots = (DotsTextView) findViewById(R.id.dots);
//        dots.setVisibility(View.GONE);
        tv_title = (TextView) findViewById(R.id.o_Title);
        tv_submit_rating = (TextView) findViewById(R.id.submitRating);
        tv_submit_rating.setVisibility(View.GONE);
        tv_gotit = (TextView) findViewById(R.id.o_gotit);
        tv_gotit.setVisibility(View.GONE);
        tv_title.setPaintFlags(tv_title.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tv_qty = (TextView) findViewById(R.id.o_quantity);
        tv_size = (TextView) findViewById(R.id.o_size);
        tv_orderID = (TextView) findViewById(R.id.o_orderno);
        tv_seller = (TextView) findViewById(R.id.o_seller);
        tv_date = (TextView) findViewById(R.id.o_orderDate);
        tv_amount = (TextView) findViewById(R.id.o_pricePaid);
        tv_paymentmode = (TextView) findViewById(R.id.o_paymentmode);
        tv_zapcash = (TextView) findViewById(R.id.o_zapcash);
        tv_buyer = (TextView) findViewById(R.id.o_buyer);
        tv_address = (TextView) findViewById(R.id.o_buyerAddress);
        tv_headerStatus = (TextView) findViewById(R.id.o_statusHeader);
        tv_cta = (TextView) findViewById(R.id.o_orderCta);
        tv_returnPolicy = (TextView) findViewById(R.id.returnpolicy);
        tv_cta.setPaintFlags(tv_cta.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tv_description = (TextView) findViewById(R.id.o_description);
        tv_toggleDetails = (TextView) findViewById(R.id.toggleDetails);
        tv_toggleDetails.setPaintFlags(tv_toggleDetails.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        tv_orderID.setLayoutParams(lp);
        tv_date.setLayoutParams(lp);
        tv_paymentmode.setLayoutParams(lp);
        tv_seller.setLayoutParams(lp);
        tv_amount.setLayoutParams(lp);
        tv_zapcash.setLayoutParams(lp);

        rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float ratingValue, boolean fromUser) {
                Rating = (int) ratingValue;

            }
        });

        tv_submit_rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Rating != 0) {
                    rating.setVisibility(View.GONE);
                    tv_submit_rating.setVisibility(View.GONE);
                    tv_gotit.setVisibility(View.VISIBLE);
                    tv_gotit.setAnimation(AnimationUtils.loadAnimation(OrderTrack.this, R.anim.zoomin));
                    final MediaPlayer mp = MediaPlayer.create(OrderTrack.this, R.raw.ratingclick);
                    mp.start();
                    RateOrder(Rating);
                    tv_gotit.postDelayed(new Runnable() {
                        public void run() {
                            tv_gotit.setAnimation(null);
                            tv_gotit.setVisibility(View.GONE);

                        }
                    }, 2000);
                }
            }
        });

        tv_returnPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnpolicy = new Intent(OrderTrack.this, ShippingReturns.class);
                returnpolicy.putExtra("orderID", orderID);
                startActivity(returnpolicy);
                finish();
            }
        });
    }


//  Main getdata function
//    =========================

    private void GetData() {
        ApiService.getInstance(OrderTrack.this, 1).getData(OrderTrack.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/order/details/" + orderID, "orderDetails");
    }


    private void RateOrder(int rating) {

        JSONObject rateObject = new JSONObject();
        try {
            rateObject.put("order_id", orderID);
            rateObject.put("rating", rating);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("RATING OBJECT : " + rateObject);

        ApiService.getInstance(OrderTrack.this, 1).postData(OrderTrack.this, EnvConstants.APP_BASE_URL + "/order/rate_order", rateObject, SCREEN_NAME, "Rating");
    }


//    Display function
//    =========================

    private void DisplayData() {
        orderScrollView.setVisibility(View.VISIBLE);
        JSONObject product = data.optJSONObject("product");
        tv_headerTitle.setText(product.optString("title"));
        tv_title.setText(product.optString("title"));

        productId = product.optInt("id");

        tv_qty.setText(String.valueOf(data.optInt("quantity")));
        tv_size.setText(data.optString("size"));
        tv_orderID.setText(data.optString("order_number"));
        tv_seller.setText(data.optString("seller"));
        tv_date.setText(data.optString("placed_at"));
        tv_amount.setText(getResources().getString(R.string.Rs) + String.valueOf(data.optInt("amount_paid")));
        tv_paymentmode.setText(data.optString("payout_mode"));
        tv_zapcash.setText(getResources().getString(R.string.Rs) + String.valueOf(data.optInt("zapwallet_used")));
        tv_buyer.setText(data.optJSONObject("delivery_address").optString("name") + " - " + data.optJSONObject("delivery_address").optString("phone"));
        String add = data.optJSONObject("delivery_address").optString("address") + "," +
                data.optJSONObject("delivery_address").optString("city") + "," +
                data.optJSONObject("delivery_address").optString("state") + "," +
                data.optJSONObject("delivery_address").optString("pincode");

        tv_address.setText(add);
        JSONObject trackerObject = data.optJSONObject("tracker");
        description = trackerObject.optString("description");
        current_title = trackerObject.optString("current_title");
        ratingStatus = data.optBoolean("rating");
        ctaStatus = trackerObject.optBoolean("cta");


        tv_cta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowReasonDialog();
            }

        });


        currentStep = trackerObject.optInt("current_step");
        JSONArray dataArray = trackerObject.optJSONArray("steps");
        for (int i = 0; i < dataArray.length(); i++) {
            TrackerModel trackerdata = new TrackerModel();
            trackerdata.setTitle(dataArray.optJSONObject(i).optString("title"));
            if (dataArray.optJSONObject(i).has("time")) {
                trackerdata.setDate(dataArray.optJSONObject(i).optString("time"));
            } else {
                trackerdata.setDate(null);
            }
            trackerData.add(trackerdata);
        }

        lv_detailLayout.setVisibility(View.GONE);
        tv_toggleDetails.setTag("HIDDEN");
        tv_toggleDetails.setText("SHOW ORDER DETAILS");
        DisplayTracker(trackerData);


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

    private void DisplayTracker(ArrayList<TrackerModel> trackerData) {
        tv_headerStatus.setText(current_title);
        if (ratingStatus) {
            rating.setVisibility(View.VISIBLE);
            tv_submit_rating.setVisibility(View.VISIBLE);
        } else {
            rating.setVisibility(View.GONE);
            tv_submit_rating.setVisibility(View.GONE);
        }

        tv_description.setText(description);
        if (ctaStatus) {
            tv_cta.setVisibility(View.VISIBLE);
            tv_returnPolicy.setVisibility(View.VISIBLE);
        } else {
            tv_cta.setVisibility(View.GONE);
            tv_returnPolicy.setVisibility(View.GONE);
        }
        for (int i = 0; i < trackerData.size(); i++) {
            LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = vi.inflate(R.layout.tracker_holder, null);
            View stem1 = v.findViewById(R.id.o_stem1);
            View stem2 = v.findViewById(R.id.o_stem2);
            TextView ball = (TextView) v.findViewById(R.id.o_ball);
            TextView title = (TextView) v.findViewById(R.id.o_tracker_title);
            TextView order_description = (TextView) v.findViewById(R.id.o_description);
            TextView date = (TextView) v.findViewById(R.id.o_tracker_ordred_date);
            LinearLayout dataLayout = (LinearLayout) v.findViewById(R.id.data_layout);

            order_description.setVisibility(View.GONE);
            if (i == 0) {
                stem1.setVisibility(View.GONE);
            }
            if (i == (trackerData.size() - 1)) {
                stem2.setVisibility(View.GONE);
            }
            if (i <= currentStep) {
                if (stem1.getVisibility() == View.VISIBLE) {
                    stem1.setBackgroundColor(Color.parseColor("#FF7373"));
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ball.setBackground(ContextCompat.getDrawable(OrderTrack.this, R.drawable.tracker_bg));
                }
                if (stem2.getVisibility() == View.VISIBLE) {
                    stem2.setBackgroundColor(Color.parseColor("#FF7373"));
                }
            }

            if (i == currentStep) {
                title.setTextColor(Color.parseColor("#FF7373"));
                order_description.setVisibility(View.VISIBLE);
                order_description.setText(description);
            }
            title.setText(trackerData.get(i).getTitle());
            if (trackerData.get(i).getDate() != null) {
                date.setText(trackerData.get(i).getDate());
            } else {
                date.setVisibility(View.GONE);
            }
            System.out.println("DATALAYOUT HEIGHT : " + dataLayout.getHeight() + "____" + dataLayout.getMeasuredHeight());
//            stem2.getLayoutParams().height = dataLayout.getMeasuredHeight();

            lv_tracker.addView(v);
        }

        progressBar.setVisibility(View.GONE);
    }


    private void ShowReasonDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(OrderTrack.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog_return_alert, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OrderTrack.this);
        alertDialogBuilder.setView(promptView);

        ImageView close = (ImageView) promptView.findViewById(R.id.close_reason_popup);
        TextView cancel = (TextView) promptView.findViewById(R.id.reason_cancel);
        TextView submit = (TextView) promptView.findViewById(R.id.reason_submit);
        LinearLayout reasonLayout = (LinearLayout) promptView.findViewById(R.id.reasonLayout);

        DisplayReasons(reasonLayout);

        // setup a dialog window
        alertDialogBuilder.setCancelable(false);
        // create an alert dialog

        final AlertDialog alert = alertDialogBuilder.create();
        alert.show();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
                tv_cta.setVisibility(View.GONE);
                tv_returnPolicy.setVisibility(View.GONE);
                lv_dotLayout.setVisibility(View.VISIBLE);
//                if (!dots.isPlaying()) {
//                    dots.setVisibility(View.VISIBLE);
//                    dots.start();
//                }
//                if (!dots.isPlaying()) {
//                    dots.setVisibility(View.VISIBLE);
//                    dots.start();
//                }

                if (reaseonStep != null) {
                    final JSONObject returnObject = new JSONObject();
                    try {
                        returnObject.put("order_id", orderID);
                        returnObject.put("reason", reaseonStep);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApiService.getInstance(OrderTrack.this, 1).postData(OrderTrack.this, EnvConstants.APP_BASE_URL + "/user/myorders/", returnObject, SCREEN_NAME, "Return");
                } else {
                     CustomMessage.getInstance().CustomMessage(OrderTrack.this, "Select a reason");
                }
            }
        });


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
    }

    private void DisplayReasons(final LinearLayout reasonLayout) {
        SharedPreferences reasons = getSharedPreferences("ReturnReason",
                Context.MODE_PRIVATE);
        final String dataString = reasons.getString("Reasons", "");
        try {
            JSONObject data = new JSONObject(dataString);
            for (int i = 0; i < 5; i++) {
                LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v = vi.inflate(R.layout.reason_holder, null);
                TextView reason = (TextView) v.findViewById(R.id.reason);
                CheckBox checkBox = (CheckBox) v.findViewById(R.id.reason_checkbox);
                reason.setText(data.optString(String.valueOf(i)));
                reason.setTextColor(Color.parseColor("#4a4a4a"));

                if (reaseonStep == String.valueOf(i)) {
                    checkBox.setChecked(true);
                }

                final int finalI = i;
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        reaseonStep = String.valueOf(finalI);
                        reasonLayout.removeAllViews();
                        DisplayReasons(reasonLayout);
                    }
                });
                reasonLayout.addView(v);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void handleDialogueUiThread(final int event) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (event == DIALOGE_HIDE) {
                    System.out.println("SIZE : inside dialog down");
                    lv_detailLayout.animate().alpha(0.0f).setDuration(500);
                    lv_detailLayout.setVisibility(View.GONE);
                    tv_toggleDetails.setText("SHOW ORDER DETAILS");

                } else {
                    System.out.println("SIZE : inside dialog up");
                    lv_detailLayout.animate().alpha(1.0f).setDuration(500);
//                    expand(lv_detailLayout);
                    lv_detailLayout.setVisibility(View.VISIBLE);
                    tv_toggleDetails.setText("HIDE ORDER DETAILS");
                }

            }
        });

    }


//    Server responses
//    ======================

    @Override
    public void onResponseCallback(JSONObject response, String flag) {
        System.out.println("ORDER RESP : " + response);

        if (flag.equals("orderDetails")) {
            JSONObject resp = JsonParser.getInstance().parserJsonObject(response);
            try {
                String status = resp.getString("status");
                if (status.equals("success")) {
                    data = resp.getJSONObject("data");

                    DisplayData();
                } else {
                     CustomMessage.getInstance().CustomMessage(this, "Oops. Something went wrong!");
                    progressBar.setVisibility(View.GONE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (flag.equals("Return")) {
            JSONObject resp = JsonParser.getInstance().parserJsonObject(response);
            try {
                System.out.println("RETURN : " + resp);
                String status = resp.getString("status");
                if (status.equals("success")) {
                    System.out.println(resp);
                    JSONObject dataObject = resp.optJSONObject("data");

                    JSONObject trackerObject = dataObject.optJSONObject("tracker");
                    description = trackerObject.optString("description");
                    current_title = trackerObject.optString("current_title");
                    currentStep = trackerObject.optInt("current_step");
                    ratingStatus = data.optBoolean("rating");
                    ctaStatus = trackerObject.optBoolean("cta");


                    tv_cta.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ShowReasonDialog();
                        }

                    });

                    JSONArray dataArray = trackerObject.optJSONArray("steps");
                    trackerData.clear();
                    for (int i = 0; i < dataArray.length(); i++) {
                        TrackerModel trackerdata = new TrackerModel();
                        trackerdata.setTitle(dataArray.optJSONObject(i).optString("title"));
                        if (dataArray.optJSONObject(i).has("time")) {
                            trackerdata.setDate(dataArray.optJSONObject(i).optString("time"));
                        } else {
                            trackerdata.setDate(null);
                        }
                        trackerData.add(trackerdata);
                    }
                    lv_tracker.removeAllViews();
                    DisplayTracker(trackerData);
                    lv_dotLayout.setVisibility(View.GONE);

//                    if (!dots.isPlaying()) {
//                        dots.setVisibility(View.GONE);
//                        dots.stop();
//                    }


                } else {
                    System.out.println("RETURN ERROR : ");
                    String detail = resp.optString("detail");
                    if (detail != null || detail.length() > 1) {
                         CustomMessage.getInstance().CustomMessage(this, detail);
                    } else {
                         CustomMessage.getInstance().CustomMessage(this, "Oops. Something went wrong!");
                    }
                    tv_cta.setVisibility(View.VISIBLE);
                    tv_returnPolicy.setVisibility(View.VISIBLE);

//                    if (!dots.isPlaying()) {
//                        dots.setVisibility(View.GONE);
//                        dots.stop();
//                    }
                    lv_dotLayout.setVisibility(View.GONE);
                    reaseonStep = null;

                }

            } catch (JSONException e) {
                reaseonStep = null;
                e.printStackTrace();
                 CustomMessage.getInstance().CustomMessage(this, "Oops. Something went wrong!");
                tv_cta.setVisibility(View.VISIBLE);
                tv_returnPolicy.setVisibility(View.VISIBLE);

//                if (!dots.isPlaying()) {
//                    dots.setVisibility(View.GONE);
//                    dots.stop();
//                }
                lv_dotLayout.setVisibility(View.GONE);

            }
        } else if (flag.equals("Rating")) {
            System.out.println("RATING : " + response);
        }

    }


    @Override
    public void onErrorCallback(VolleyError error, String flag) {
        if (flag.equals("Rating")) {
            System.out.println("RATING : ERROR : " + error);
        }
    }
}

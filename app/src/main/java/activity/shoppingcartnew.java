package activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.exceptions.CleverTapMetaDataNotFoundException;
import com.clevertap.android.sdk.exceptions.CleverTapPermissionsNotSatisfied;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
//import com.uxcam.UXCam;
import com.google.android.gms.nearby.internal.connection.dev.ParcelablePayload;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import application.MyApplicationClass;
import in.juspay.godel.analytics.GodelTracker;
import in.juspay.godel.ui.JuspayBrowserFragment;
import in.juspay.juspaysafe.BrowserCallback;
import in.juspay.juspaysafe.BrowserParams;
import in.juspay.juspaysafe.JuspaySafeBrowser;
import network.ApiCommunication;
import network.ApiService;

import utils.CustomMessage;
import utils.ExternalFunctions;
import utils.FontUtils;
import utils.GetSharedValues;

public class shoppingcartnew extends AppCompatActivity implements ApiCommunication {
    JSONArray addressList = new JSONArray();
    String SCREEN_NAME = "CHECKOUT";
    LinearLayout AddressHolder;
    int selectedAddressId = 0;
    int EditIndex = 0;
    Boolean zapCashStatus = false;
    int ZapcashAvailable = 0;
    Boolean CouponApplied = false;
    Boolean PriceCheck = false;
    int CouponPrice = 0;
    int ORGPrice, LISTPrice, FINALPrice, Shipping;
    int AlbumId;
    int ZAPCashUsed = 0;
    RelativeLayout rlzapcash;
    CheckBox zapcash_checkbox;
    boolean blpayment = false;
    boolean bladd = false;
    boolean blamount = false;
    RelativeLayout rlamount, rladd, rlpay, rlpaymentgateway, rlcod, checkout_main_layout;
    LinearLayout lnreview, lnpayment;

    LinearLayout lnselectpayment, lnselectcod;
    String selectedPaymentMethod = "";

    ProgressDialog mProgress;
    ImageView card_image_click, cod_image_click, imgindicator, imageView16, imageView17, img1;
    String Pincode_selected;

    ImageView imgback;
    TextView zapcash_text;
    Boolean ZapCehck = false;
    Boolean FailedSummaryCheck = false, paymetSelect = false;
    TextView tvlabeladdress, tvlabelpayment;
    String callingActivity = "activity.HomePage";
    int amount_pay = 0;
    ProgressBar checkout_progressBar;
    TextView confirm_checkout, addaddress, tvlabelamount, tvtotal, applied_zapcash, priceOrgprice, priceDiscount, priceListprice, priceShipping, finalPrice, empty_address_message;
    Tracker mTracker;

    String stime = "";
    String endtime = "";
    CleverTapAPI cleverTap;

    SharedPreferences settings;
    int cart_id = 0;
    String txnId = "";
    private int TimeCounter = 0;
    private Timer t;
    TextView tvpayment,tvonlinemessage,tvcodmessage,tvcodlabel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_cart_new);
        MyApplicationClass application = (MyApplicationClass) getApplication();
        mTracker = application.getDefaultTracker();
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.main_layout);
        FontUtils.setCustomFont(rl, getAssets());
        stime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        try {
            cleverTap = CleverTapAPI.getInstance(getApplicationContext());
        } catch (CleverTapMetaDataNotFoundException e) {
            // thrown if you haven't specified your CleverTap Account ID or Token in your AndroidManifest.xml
        } catch (CleverTapPermissionsNotSatisfied e) {
            // thrown if you haven’t requested the required permissions in your AndroidManifest.xml
        }
        checkout_main_layout = (RelativeLayout) findViewById(R.id.checkout_main_layout);
        checkout_main_layout.setVisibility(View.INVISIBLE);

        mProgress = new ProgressDialog(shoppingcartnew.this);
        mProgress.setCancelable(false);
        try {
            AlbumId = getIntent().getIntExtra("AlbumId", 0);
            cart_id = getIntent().getIntExtra("cart_id", 0);
        } catch (Exception e) {

        }

        settings = getSharedPreferences("PAYMENT_MODE",
                Context.MODE_PRIVATE);
        tvlabelpayment = (TextView) findViewById(R.id.textView55);
        zapcash_checkbox = (CheckBox) findViewById(R.id.zapcash_checkbox);


        try {
            callingActivity = "activity." + getIntent().getStringExtra("activity");
        } catch (Exception e) {
            callingActivity = "activity.Cart";
        }
        checkout_progressBar = (ProgressBar) findViewById(R.id.checkout_progressBar);
        checkout_progressBar.setVisibility(View.VISIBLE);


        confirm_checkout = (TextView) findViewById(R.id.confirm_checkout);
        confirm_checkout.setText("SELECT DELIVERY ADDRESS");
        AddressHolder = (LinearLayout) findViewById(R.id.AddressHolder);
        rlzapcash = (RelativeLayout) findViewById(R.id.rlzapcash);
        applied_zapcash = (TextView) findViewById(R.id.applied_zapcash);
        tvtotal = (TextView) findViewById(R.id.tvtotal);
        tvlabelamount = (TextView) findViewById(R.id.tvcnt);
        tvlabeladdress = (TextView) findViewById(R.id.textView53);
        addaddress = (TextView) findViewById(R.id.addaddress);

        tvonlinemessage=(TextView) findViewById(R.id.tvexplanationonline);
        tvcodmessage=(TextView) findViewById(R.id.tvexplanationcod);
        tvcodlabel=(TextView) findViewById(R.id.tvcod);
        applied_zapcash.setVisibility(View.INVISIBLE);
        rlamount = (RelativeLayout) findViewById(R.id.relativeLayout7);
        rladd = (RelativeLayout) findViewById(R.id.relativeLayout6);
        rlpay = (RelativeLayout) findViewById(R.id.relativeLayout9);
        lnreview = (LinearLayout) findViewById(R.id.Revieworder);
        lnpayment = (LinearLayout) findViewById(R.id.payment);
        imgback = (ImageView) findViewById(R.id.imgclose);
        tvpayment=(TextView) findViewById(R.id.tvpayment);
        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        imgindicator = (ImageView) findViewById(R.id.imgindicator);
        imageView16 = (ImageView) findViewById(R.id.imageView16);
        imageView17 = (ImageView) findViewById(R.id.imageView17);
        img1 = (ImageView) findViewById(R.id.img1);


        rlpaymentgateway = (RelativeLayout) findViewById(R.id.rlpaymentgateway);
        rlcod = (RelativeLayout) findViewById(R.id.rlcod);

        zapcash_text = (TextView) findViewById(R.id.zapcash_text);

//        new addition
//        -----------------------------
        tvlabelamount.setText("1.Review Order");
        imgindicator.setImageResource(R.drawable.grey_up);
        imageView16.setImageResource(R.drawable.grey_down);
        imageView17.setImageResource(R.drawable.grey_down);
        SpannableString spanString = new SpannableString("1.Review Order");
        spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
        tvlabelamount.setText(spanString);

        SpannableString spanString1 = new SpannableString("2. Delivery Address");
        spanString1.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString1.length(), 0);
        tvlabeladdress.setText(spanString1);

        SpannableString spanString3 = new SpannableString("3. Payment Method");
        spanString3.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString3.length(), 0);
        tvlabelpayment.setText(spanString3);

        tvtotal.setText("");
        lnpayment.setVisibility(View.GONE);
        AddressHolder.setVisibility(View.GONE);
        lnreview.setVisibility(View.VISIBLE);
        blamount = true;
        blpayment = false;
        bladd = false;


        lnpayment.setVisibility(View.GONE);
        AddressHolder.setVisibility(View.GONE);
        addaddress.setVisibility(View.GONE);
        Getdata();

        lnselectpayment= (LinearLayout) findViewById(R.id.selectcredit);
        lnselectcod = (LinearLayout) findViewById(R.id.selectcod);

        card_image_click = (ImageView) findViewById(R.id.card_image_click);
        cod_image_click = (ImageView) findViewById(R.id.cod_image_click);
        empty_address_message = (TextView) findViewById(R.id.empty_address_message);
        empty_address_message.setVisibility(View.GONE);


        try {
            selectedAddressId = getIntent().getIntExtra("selectedAddress", 0);
            zapCashStatus = getIntent().getBooleanExtra("ZapCashUsed", false);
            //System.out.println("DDDDD:" + selectedAddressId + "__" + zapCashStatus);
            if (selectedAddressId != 0) {
                FailedSummaryCheck = true;
            }
//            DisplayUpatedValues();

        } catch (Exception e) {

        }

        rlpaymentgateway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!selectedPaymentMethod.equals("CARD")) {
                    if (selectedAddressId > 0) {
                        confirm_checkout.setText("CONFIRM ORDER");
                    } else {
                        confirm_checkout.setText("SELECT DELIVERY ADDRESS");
                    }
                    selectedPaymentMethod = "CARD";
                    card_image_click.setImageResource(R.drawable.roundshape_bg_blue);
                    cod_image_click.setImageResource(R.drawable.roundshape_bg_nothing);
                    rlpaymentgateway.setBackgroundColor(Color.rgb(213, 213, 213));
                    if (rlcod.getVisibility() == View.VISIBLE) {
                        rlcod.setBackgroundResource(R.drawable.background_checkout_holders);
                    }
                } else {
                    if (selectedAddressId > 0) {
                        confirm_checkout.setText("SELECT PAYMENT METHOD");
                    } else {
                        confirm_checkout.setText("SELECT DELIVERY ADDRESS");
                    }

                    selectedPaymentMethod = "CARD";
                    card_image_click.setImageResource(R.drawable.roundshape_bg_nothing);
                    cod_image_click.setImageResource(R.drawable.roundshape_bg_nothing);
                    rlpaymentgateway.setBackgroundResource(R.drawable.background_checkout_holders);
                }
            }
        });

        lnselectpayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!selectedPaymentMethod.equals("CARD")) {

                    if (selectedAddressId > 0) {
                        confirm_checkout.setText("PROCEED TO PAYMENT");
                    } else {
                        confirm_checkout.setText("SELECT DELIVERY ADDRESS");
                    }
                    selectedPaymentMethod = "CARD";
                    card_image_click.setImageResource(R.drawable.roundshape_bg_blue);
                    cod_image_click.setImageResource(R.drawable.roundshape_bg_nothing);
                    rlpaymentgateway.setBackgroundColor(Color.rgb(213, 213, 213));
                    if (rlcod.getVisibility() == View.VISIBLE) {
                        rlcod.setBackgroundResource(R.drawable.background_checkout_holders);


                    }
                } else {
                    if (selectedAddressId > 0) {
                        confirm_checkout.setText("SELECT PAYMENT METHOD");
                    } else {
                        confirm_checkout.setText("SELECT DELIVERY ADDRESS");
                    }
                    selectedPaymentMethod = "";
                    card_image_click.setImageResource(R.drawable.roundshape_bg_nothing);
                    cod_image_click.setImageResource(R.drawable.roundshape_bg_nothing);
                    rlpaymentgateway.setBackgroundResource(R.drawable.background_checkout_holders);
                }
            }
        });

        lnselectcod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!selectedPaymentMethod.equals("COD")) {
                    if (selectedAddressId > 0) {
                        confirm_checkout.setText("CONFIRM ORDER");
                    } else {
                        confirm_checkout.setText("SELECT DELIVERY ADDRESS");
                    }
                    selectedPaymentMethod = "COD";
                    cod_image_click.setImageResource(R.drawable.roundshape_bg_blue);
                    card_image_click.setImageResource(R.drawable.roundshape_bg_nothing);
                    if (rlcod.getVisibility() == View.VISIBLE) {
                        rlcod.setBackgroundColor(Color.rgb(213, 213, 213));
                    }
                    rlpaymentgateway.setBackgroundResource(R.drawable.background_checkout_holders);
                } else {
                    if (selectedAddressId > 0) {
                        confirm_checkout.setText("SELECT PAYMENT METHOD");
                    } else {
                        confirm_checkout.setText("SELECT DELIVERY ADDRESS");
                    }
                    selectedPaymentMethod = "";
                    cod_image_click.setImageResource(R.drawable.roundshape_bg_nothing);
                    card_image_click.setImageResource(R.drawable.roundshape_bg_nothing);
                    if (rlcod.getVisibility() == View.VISIBLE) {
                        rlcod.setBackgroundResource(R.drawable.background_checkout_holders);
                    }
                }
            }
        });


        rlamount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!blamount) {
                    if (selectedPaymentMethod.length() > 0) {
                        if (selectedAddressId > 0) {
                            confirm_checkout.setText("PROCEED TO PAYMENT");
                        } else {
                            confirm_checkout.setText("SELECT DELIVERY ADDRESS");
                        }
                    } else {
                        if (selectedAddressId > 0) {
                            confirm_checkout.setText("SELECT PAYMENT METHOD");
                        } else {
                            confirm_checkout.setText("SELECT DELIVERY ADDRESS");
                        }
                    }
                    tvlabelamount.setText("1.Review Order");
                    imgindicator.setImageResource(R.drawable.grey_up);
                    imageView16.setImageResource(R.drawable.grey_down);
                    imageView17.setImageResource(R.drawable.grey_down);
                    SpannableString spanString = new SpannableString("1.Review Order");
                    spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
                    tvlabelamount.setText(spanString);

                    SpannableString spanString1 = new SpannableString("2. Delivery Address");
                    spanString1.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString1.length(), 0);
                    tvlabeladdress.setText(spanString1);

                    SpannableString spanString3 = new SpannableString("3. Payment Method");
                    spanString3.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString3.length(), 0);
                    tvlabelpayment.setText(spanString3);

                    tvtotal.setText("");
                    lnpayment.setVisibility(View.GONE);
                    AddressHolder.setVisibility(View.GONE);
                    addaddress.setVisibility(View.GONE);
                    lnreview.setVisibility(View.VISIBLE);
                   // YoYo.with(Techniques.FadeIn).duration(600).playOn(lnreview);
                    blamount = true;
                    blpayment = false;
                    bladd = false;
                } else {
                    if (selectedPaymentMethod.length() > 0) {
                        confirm_checkout.setText("PROCEED TO PAYMENT");
                    } else {
                        confirm_checkout.setText("SELECT PAYMENT METHOD");
                    }
                    tvlabelamount.setText("1. Total Payment");
                    imgindicator.setImageResource(R.drawable.grey_down);
                    SpannableString spanString = new SpannableString("1.Total Payment");
                    spanString.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString.length(), 0);
                    tvlabelamount.setText(spanString);
                    DisplayUpatedValues();
                    lnreview.setVisibility(View.GONE);
                    addaddress.setVisibility(View.GONE);
                    blamount = false;
                }
            }
        });

        addaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPaymentMethod.length() > 0) {
                    confirm_checkout.setText("PROCEED TO PAYMENT");
                } else {
                    confirm_checkout.setText("SELECT PAYMENT METHOD");
                }
                Intent add = new Intent(shoppingcartnew.this, AddAddress.class);
                add.putExtra("EditStatus", false);
                startActivityForResult(add, 1);
            }
        });

        rladd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bladd) {
                    if (selectedPaymentMethod.length() > 0) {
                        confirm_checkout.setText("PROCEED TO PAYMENT");
                    } else {
                        confirm_checkout.setText("SELECT PAYMENT METHOD");
                    }
                    addaddress.setVisibility(View.VISIBLE);
                   // YoYo.with(Techniques.FadeIn).duration(600).playOn(addaddress);
                    tvlabelamount.setText("1. Total Payment");
                    imageView16.setImageResource(R.drawable.grey_up);
                    imageView17.setImageResource(R.drawable.grey_down);
                    imgindicator.setImageResource(R.drawable.grey_down);
                    SpannableString spanString = new SpannableString("2. Delivery Address");
                    spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
                    tvlabeladdress.setText(spanString);

                    SpannableString spanString2 = new SpannableString("1.Total Payment");
                    spanString2.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString2.length(), 0);
                    tvlabelamount.setText(spanString2);

                    SpannableString spanString3 = new SpannableString("3. Payment Method");
                    spanString3.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString3.length(), 0);
                    tvlabelpayment.setText(spanString3);

                    DisplayUpatedValues();
                    lnreview.setVisibility(View.GONE);
                    lnpayment.setVisibility(View.GONE);
                    if (addressList.length() > 0) {
                        AddressHolder.setVisibility(View.VISIBLE);
                       // YoYo.with(Techniques.FadeIn).duration(600).playOn(AddressHolder);
                    } else {
                        empty_address_message.setVisibility(View.VISIBLE);
                        String spanString_empty_address = "Hey! You haven't added any address yet.";
                        empty_address_message.setText(spanString_empty_address);
                    }
                    blamount = false;
                    blpayment = false;
                    bladd = true;
                } else {
                    if (selectedPaymentMethod.length() > 0) {
                        confirm_checkout.setText("PROCEED TO PAYMENT");
                    } else {
                        confirm_checkout.setText("SELECT PAYMENT METHOD");
                    }
                    addaddress.setVisibility(View.GONE);
                    imageView16.setImageResource(R.drawable.grey_down);
                    AddressHolder.setVisibility(View.GONE);
                    empty_address_message.setVisibility(View.GONE);
                    bladd = false;
                    SpannableString spanString = new SpannableString("2. Delivery Address");
                    spanString.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString.length(), 0);
                    tvlabeladdress.setText(spanString);
                }
            }
        });

        rlpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!blpayment) {
                    if (selectedPaymentMethod.length() > 0) {
                        if (selectedPaymentMethod.equals("CARD")) {
                            confirm_checkout.setText("CONFIRM ORDER");
                        } else if (selectedPaymentMethod.equals("COD")) {
                            confirm_checkout.setText("CONFIRM ORDER");
                        }
                    } else {
                        confirm_checkout.setText("SELECT PAYMENT METHOD");
                    }


                    addaddress.setVisibility(View.GONE);
                    tvlabelamount.setText("1. Total Payment");
                    imageView17.setImageResource(R.drawable.grey_up);
                    imageView16.setImageResource(R.drawable.grey_down);
                    imgindicator.setImageResource(R.drawable.grey_down);
                    SpannableString spanString = new SpannableString("3. Payment Method");
                    spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
                    tvlabelpayment.setText(spanString);

                    SpannableString spanString2 = new SpannableString("1.Total Payment");
                    spanString2.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString2.length(), 0);
                    tvlabelamount.setText(spanString2);

                    SpannableString spanString3 = new SpannableString("2. Delivery Address");
                    spanString3.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString3.length(), 0);
                    tvlabeladdress.setText(spanString3);


                    DisplayUpatedValues();
                    lnreview.setVisibility(View.GONE);
                    lnpayment.setVisibility(View.VISIBLE);
                   // YoYo.with(Techniques.FadeIn).duration(600).playOn(lnpayment);
                    AddressHolder.setVisibility(View.GONE);
                    blamount = false;
                    blpayment = true;
                    bladd = false;
                } else {
                    confirm_checkout.setText("SELECT PAYMENT METHOD");
                    addaddress.setVisibility(View.GONE);
                    imageView17.setImageResource(R.drawable.grey_down);
                    lnpayment.setVisibility(View.GONE);
                    blpayment = false;
                    SpannableString spanString = new SpannableString("3. Payment Method");
                    spanString.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString.length(), 0);
                    tvlabelpayment.setText(spanString);
                }
            }
        });

        zapcash_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (FINALPrice > ZapcashAvailable) {
                        ZAPCashUsed = ZapcashAvailable;
                        SharedPreferences settings = getApplicationContext().getSharedPreferences("BuySession",
                                Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("ZAPCASH", String.valueOf(ZAPCashUsed));
                        editor.apply();

                        FINALPrice = (LISTPrice - ZAPCashUsed) + Shipping;
                        applied_zapcash.setVisibility(View.VISIBLE);
                        applied_zapcash.setText("- " + getResources().getString(R.string.Rs) + String.valueOf(ZAPCashUsed));
                        zapCashStatus = true;
                        PriceCheck = true;
                        DisplayUpatedValues();
                        rlpay.setVisibility(View.VISIBLE);
                        ZapCehck = false;

                    } else {
                        zapCashStatus = true;
                        ZAPCashUsed = LISTPrice;
                        PriceCheck = false;
                        SharedPreferences settings = getApplicationContext().getSharedPreferences("BuySession",
                                Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("ZAPCASH", String.valueOf(ZAPCashUsed));
                        editor.apply();
                        FINALPrice = (LISTPrice - ZAPCashUsed) + Shipping;
                        applied_zapcash.setVisibility(View.VISIBLE);
                        applied_zapcash.setText("- " + getResources().getString(R.string.Rs) + String.valueOf(ZAPCashUsed));
                        DisplayUpatedValues();
                        if (Shipping == 0) {
                            rlpay.setVisibility(View.GONE);
                            ZapCehck = true;
                        } else {
                            rlpay.setVisibility(View.VISIBLE);
                            ZapCehck = false;
                        }
                    }
                } else {
                    ZapCehck = false;
                    if (zapCashStatus) {
                        PriceCheck = false;
                        //////System.out.println("inside checked1:"+FINALPrice);
                        SharedPreferences settings = getApplicationContext().getSharedPreferences("BuySession",
                                Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("ZAPCASH", String.valueOf(0));
                        editor.apply();
                        zapCashStatus = false;
                        FINALPrice = FINALPrice + ZAPCashUsed;
                        applied_zapcash.setVisibility(View.INVISIBLE);
                        ZAPCashUsed = 0;
                        DisplayUpatedValues();
                        rlpay.setVisibility(View.VISIBLE);
                        ZapCehck = false;
                    }
                }
            }
        });
    }


//    GetZapCash function
//    -----------------------------------------------------------

    private void GetzapCash() {
        ApiService.getInstance(shoppingcartnew.this, 1).getData(shoppingcartnew.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/my_zapcash/", "getzapcash");

    }


//  Confirm click event
//    -------------------------------------------------------------

    public void confirmCheckout(View v) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("mode", selectedPaymentMethod);
        editor.apply();
        if (selectedAddressId != 0) {
            if (selectedPaymentMethod.equals("COD")) {

                //   if (FINALPrice <= 40000) {
                PincodeCheck();
//                } else {
//                    CustomMessage.getInstance().CustomMessage(shoppingcartnew.this, "Cash on delivery is available only for products less than Rs10,000.");
//                }
            } else if (selectedPaymentMethod.equals("CARD")) {
                Confirm();
            } else if (ZapCehck) {
                //System.out.println("inside zapcash applied and confirm");
                Confirm();
            } else {
//                CustomMessage.getInstance().CustomMessage(shoppingcartnew.this, "Please select a payment option.");
                if (!blpayment) {
                    if (selectedPaymentMethod.length() > 0) {
                        if (selectedPaymentMethod.equals("CARD")) {
                           // confirm_checkout.setText("ENTER CARD DETAILS");
                        } else if (selectedPaymentMethod.equals("COD")) {
                            confirm_checkout.setText("CONFIRM ORDER");
                        }
                    } else {
                        confirm_checkout.setText("SELECT PAYMENT METHOD");
                    }
                    addaddress.setVisibility(View.GONE);
                    tvlabelamount.setText("1. Total Payment");
                    imageView17.setImageResource(R.drawable.grey_up);
                    imageView16.setImageResource(R.drawable.grey_down);
                    imgindicator.setImageResource(R.drawable.grey_down);
                    SpannableString spanString = new SpannableString("3. Payment Method");
                    spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
                    tvlabelpayment.setText(spanString);

                    SpannableString spanString2 = new SpannableString("1.Total Payment");
                    spanString2.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString2.length(), 0);
                    tvlabelamount.setText(spanString2);

                    SpannableString spanString3 = new SpannableString("2. Delivery Address");
                    spanString3.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString3.length(), 0);
                    tvlabeladdress.setText(spanString3);


                    DisplayUpatedValues();
                    lnreview.setVisibility(View.GONE);
                    lnpayment.setVisibility(View.VISIBLE);
                    AddressHolder.setVisibility(View.GONE);
                    blamount = false;
                    blpayment = true;
                    bladd = false;
                } else {
                    confirm_checkout.setText("SELECT PAYMENT METHOD");
                    addaddress.setVisibility(View.GONE);
                    imageView17.setImageResource(R.drawable.grey_down);
                    lnpayment.setVisibility(View.VISIBLE    );
                    blpayment = false;
                    SpannableString spanString = new SpannableString("3. Payment Method");
                    spanString.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString.length(), 0);
                    tvlabelpayment.setText(spanString);
                }
            }

        } else {
//            CustomMessage.getInstance().CustomMessage(shoppingcartnew.this, "Select address");
            if (!bladd) {
                if (selectedPaymentMethod.length() > 0) {
                    confirm_checkout.setText("PROCEED TO PAYMENT");
                } else {
                    confirm_checkout.setText("SELECT PAYMENT METHOD");
                }
                addaddress.setVisibility(View.VISIBLE);
                tvlabelamount.setText("1. Total Payment");
                imageView16.setImageResource(R.drawable.grey_up);
                imageView17.setImageResource(R.drawable.grey_down);
                imgindicator.setImageResource(R.drawable.grey_down);
                SpannableString spanString = new SpannableString("2. Delivery Address");
                spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
                tvlabeladdress.setText(spanString);

                SpannableString spanString2 = new SpannableString("1.Total Payment");
                spanString2.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString2.length(), 0);
                tvlabelamount.setText(spanString2);

                SpannableString spanString3 = new SpannableString("3. Payment Method");
                spanString3.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString3.length(), 0);
                tvlabelpayment.setText(spanString3);

                DisplayUpatedValues();
                lnreview.setVisibility(View.GONE);
                lnpayment.setVisibility(View.GONE);
                if (addressList.length() > 0) {
                    AddressHolder.setVisibility(View.VISIBLE);
                } else {
                    empty_address_message.setVisibility(View.VISIBLE);
                    String spanString_empty_address = "Hey! You haven't added any address yet.";
                    empty_address_message.setText(spanString_empty_address);
                }
                blamount = false;
                blpayment = false;
                bladd = true;
            } else {
                if (selectedPaymentMethod.length() > 0) {
                    confirm_checkout.setText("PROCEED TO PAYMENT");
                } else {
                    confirm_checkout.setText("SELECT PAYMENT METHOD");
                }
                addaddress.setVisibility(View.GONE);
                imageView16.setImageResource(R.drawable.grey_down);
                AddressHolder.setVisibility(View.GONE);
                empty_address_message.setVisibility(View.GONE);
                bladd = false;
                SpannableString spanString = new SpannableString("2. Delivery Address");
                spanString.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString.length(), 0);
                tvlabeladdress.setText(spanString);
            }

        }
    }


//    Confirm function
//    --------------------------------------------------------------

    public void Confirm() {

        if (selectedPaymentMethod.equals("CARD")) {
            confirm_checkout.setEnabled(false);
            JSONObject confirmObject = new JSONObject();
            try {
                confirmObject.put("address_id", selectedAddressId);
                confirmObject.put("apply_zapcash", zapCashStatus);
                confirmObject.put("amount", FINALPrice);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ////////System.out.println(confirmObject + "____");
            mProgress.setMessage("Proceeding to Transaction ...");
            mProgress.show();
            HashMap<String, Object> checkout_step = new HashMap<String, Object>();
            checkout_step.put("cart_id", cart_id);
            checkout_step.put("step", "3");
            checkout_step.put("value_of_cart", FINALPrice);
            cleverTap.event.push("checkout_step", checkout_step);
            ApiService.getInstance(shoppingcartnew.this, 1).postData(shoppingcartnew.this, EnvConstants.APP_BASE_URL + "/order/juspay_order", confirmObject, SCREEN_NAME, "confirmPost");

        } else if (selectedPaymentMethod.equals("COD")) {
            PincodeCheck();
        } else {
            confirm_checkout.setEnabled(false);
            JSONObject confirmObject = new JSONObject();
            try {
                confirmObject.put("address_id", selectedAddressId);
                confirmObject.put("apply_zapcash", zapCashStatus);
                confirmObject.put("amount", FINALPrice);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            ////////System.out.println(confirmObject + "____");
            mProgress.setMessage("Proceeding to Transaction ...");
            mProgress.show();
            HashMap<String, Object> checkout_step = new HashMap<String, Object>();
            checkout_step.put("cart_id", cart_id);
            checkout_step.put("step", "3");
            checkout_step.put("value_of_cart", FINALPrice);
            cleverTap.event.push("checkout_step", checkout_step);
            ApiService.getInstance(shoppingcartnew.this, 1).postData(shoppingcartnew.this, EnvConstants.APP_BASE_URL + "/order/juspay_order", confirmObject, SCREEN_NAME, "confirmPost");

        }

    }


    //    Pincode check url
//    ------------------------------------------------------------------

    public void PincodeCheck() {
        mProgress.setMessage("Checking Pincode ...");
        mProgress.show();
        ApiService.getInstance(shoppingcartnew.this, 1).getData(shoppingcartnew.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/order/pincode/?pincode=" + Pincode_selected + "&?action=cod", "pincodecheck");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case 1:
                    confirm_checkout.setText("SELECT PAYMENT METHOD");
                    String newAddress = data.getStringExtra("data");
                    try {
                        JSONObject obj1 = new JSONObject(newAddress);
                        empty_address_message.setVisibility(View.GONE);
                        AddressHolder.setVisibility(View.VISIBLE);
                        addressList.put(obj1);
                        selectedAddressId = obj1.getInt("id");
                        AddressHolder.removeAllViews();
                        displayAdddresses();
//                        progressbar.setVisibility(View.VISIBLE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ////////System.out.println("jjjjjjjjjjjjjjjj" + newAddress);
                    break;


                case 2:
                    confirm_checkout.setText("SELECT PAYMENT METHOD");
                    String editAddress = data.getStringExtra("edited_data");
//                    addressList.remove(EditIndex);
                    try {
                        JSONObject obj1 = new JSONObject(editAddress);
                        addressList.put(EditIndex, obj1);
                        selectedAddressId = obj1.getInt("id");
                        AddressHolder.removeAllViews();
                        displayAdddresses();
//                        progressbar.setVisibility(View.VISIBLE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ////////System.out.println("jjjjjjjjjjjjjjjj" + editAddress);
                    break;


            }
        }

    }


    public void DisplayUpatedValues() {
        String listP = String.valueOf(FINALPrice);
//        cartListingPrice.setText(listP);
//        priceListprice.setText(listP);
        NumberFormat myFormat = NumberFormat.getInstance();
        myFormat.setGroupingUsed(true);
        finalPrice.setText(getResources().getString(R.string.Rs) + myFormat.format(Double.parseDouble(String.valueOf(listP))));
        tvtotal.setText(getResources().getString(R.string.Rs) + myFormat.format(Double.parseDouble(String.valueOf(listP))));
        zapcash_text.setText(getResources().getString(R.string.Rs) + myFormat.format(Double.parseDouble(String.valueOf(ZapcashAvailable - ZAPCashUsed))));
        tvpayment.setText("Pay "+getResources().getString(R.string.Rs) + myFormat.format(Double.parseDouble(String.valueOf(listP))));
    }

    public void displayValues(JSONObject item) {

        priceOrgprice = (TextView) findViewById(R.id.price_originalPrice);
        priceDiscount = (TextView) findViewById(R.id.price_discount);
        priceListprice = (TextView) findViewById(R.id.price_listingprice);
        priceShipping = (TextView) findViewById(R.id.price_shippingcharge);
        finalPrice = (TextView) findViewById(R.id.finalPrice);



        try {

            if (!item.isNull("total_original_price")) {
                ORGPrice = item.getInt("total_original_price");// * item.getInt("product_quantity");
            } else {
                ORGPrice = 0;
            }
            LISTPrice = item.getInt("total_listing_price");// * item.getInt("product_quantity");
            FINALPrice = item.getInt("total_price_with_shipping_charge");


            int discount = 0;
            if (!item.isNull("total_discount")) {
                discount = item.getInt("total_discount");
            } else {
                discount = 0;
            }

            if (ZapcashAvailable > 0) {
                rlzapcash.setVisibility(View.VISIBLE);
                zapcash_text.setText(getResources().getString(R.string.Rs) + String.valueOf(ZapcashAvailable));
            } else {
                rlzapcash.setVisibility(View.GONE);
            }




            NumberFormat myFormat = NumberFormat.getInstance();
            myFormat.setGroupingUsed(true);

            String listP = getResources().getString(R.string.Rs) + myFormat.format(Double.parseDouble(String.valueOf(LISTPrice)));
            String finalP = getResources().getString(R.string.Rs) + myFormat.format(Double.parseDouble(String.valueOf(FINALPrice)));
            if (ORGPrice != 0) {
                String orgP = getResources().getString(R.string.Rs) + myFormat.format(Double.parseDouble(String.valueOf(ORGPrice)));

                priceOrgprice.setText(orgP);
            } else {
                priceOrgprice.setVisibility(View.GONE);
            }
            priceListprice.setText(listP);

            if (discount != 0) {
                priceDiscount.setText(getResources().getString(R.string.Rs) + myFormat.format(Double.parseDouble(String.valueOf(discount))));
            } else {
                priceDiscount.setVisibility(View.GONE);
            }
            Shipping = item.getInt("total_shipping_charge");
            tvonlinemessage.setText(item.getString("online_message"));
            tvcodmessage.setText(item.getString("cod_message"));
            String sp = getResources().getString(R.string.Rs) + String.valueOf(item.getInt("total_shipping_charge"));
            priceShipping.setText(sp);
            finalPrice.setText(finalP);
            tvpayment.setText(finalP);
            tvtotal.setText(finalP);
            tvpayment.setText("Pay "+finalP);

            if (item.getInt("zap_cash") > 0) {
                ZapcashAvailable = item.getInt("zap_cash");
                rlzapcash.setVisibility(View.VISIBLE);
                TextView zapcash_text = (TextView) findViewById(R.id.zapcash_text);
                zapcash_text.setText(String.valueOf(ZapcashAvailable));
            } else {
                rlzapcash.setVisibility(View.GONE);
                ZapcashAvailable = 0;

            }

            if (item.has("cod")) {
                if (!item.getBoolean("cod")) {

                    for (int i = 0; i < rlcod.getChildCount(); i++) {
                        View child = rlcod.getChildAt(i);
                        child.setEnabled(false);
                    }
                    cod_image_click.setClickable(false);
                    tvcodlabel.setTextColor(Color.parseColor("#d5d5d5"));
                    tvcodmessage.setTextColor(Color.parseColor("#EEAAAA"));
                }
                  else{
                    rlcod.setEnabled(true);
                    cod_image_click.setEnabled(true);
                    tvcodmessage.setTextColor(Color.parseColor("#9b9b9b"));
                    tvcodlabel.setTextColor(Color.parseColor("#000000"));

                }
            }
            HashMap<String, Object> checkout_step = new HashMap<String, Object>();
            checkout_step.put("cart_id", cart_id);
            checkout_step.put("step", "1");
            checkout_step.put("value_of_cart", FINALPrice);
            cleverTap.event.push("checkout_step", checkout_step);
            if (FailedSummaryCheck) {
                confirm_checkout.setText("PROCEED TO PAYMENT");
                lnreview.setVisibility(View.GONE);
                lnpayment.setVisibility(View.VISIBLE);
                AddressHolder.setVisibility(View.GONE);
                blamount = false;
                blpayment = true;
                bladd = false;
                SpannableString spanString = new SpannableString("3. Payment Method");
                spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
                tvlabelpayment.setText(spanString);
                if (zapCashStatus) {
                    zapcash_checkbox.setChecked(true);
                    if (FINALPrice > ZapcashAvailable) {
                        ZAPCashUsed = ZapcashAvailable;
                        SharedPreferences settings = getApplicationContext().getSharedPreferences("BuySession",
                                Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("ZAPCASH", String.valueOf(ZAPCashUsed));
                        editor.apply();

                        FINALPrice = (LISTPrice - ZAPCashUsed) + Shipping;
                        applied_zapcash.setVisibility(View.VISIBLE);
                        applied_zapcash.setText("- " + getResources().getString(R.string.Rs) + String.valueOf(ZAPCashUsed));
                        zapCashStatus = true;
                        PriceCheck = true;
                        DisplayUpatedValues();
                        rlpay.setVisibility(View.VISIBLE);
                        ZapCehck = false;

                    } else {
                        zapCashStatus = true;
                        ZAPCashUsed = LISTPrice;
                        PriceCheck = false;
                        SharedPreferences settings = getApplicationContext().getSharedPreferences("BuySession",
                                Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("ZAPCASH", String.valueOf(ZAPCashUsed));
                        editor.apply();
                        FINALPrice = (LISTPrice - ZAPCashUsed) + Shipping;
                        applied_zapcash.setVisibility(View.VISIBLE);
                        applied_zapcash.setText("- " + getResources().getString(R.string.Rs) + String.valueOf(ZAPCashUsed));
                        DisplayUpatedValues();
                        if (Shipping == 0) {
                            rlpay.setVisibility(View.GONE);
                            ZapCehck = true;
                        } else {
                            rlpay.setVisibility(View.VISIBLE);
                            ZapCehck = false;
                        }
                    }


                } else {
                    zapcash_checkbox.setChecked(false);
                }
            }


        } catch (JSONException e) {
            //System.out.println("bbbbbb" + e.getMessage());
            e.printStackTrace();
        }

        displayAdddresses();

    }


    //    Custom alert dialog display function
//    -------------------------------------------------------------------

    protected void showInputDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(shoppingcartnew.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog_cod, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(shoppingcartnew.this);
        alertDialogBuilder.setView(promptView);
        // create an alert dialog
        final AlertDialog alert = alertDialogBuilder.create();
        TextView message_to_confirm_cod = (TextView) promptView.findViewById(R.id.message_to_confirm_cod);
        TextView proceed_cod = (TextView) promptView.findViewById(R.id.proceed_cod);
        ImageView imgclose = (ImageView) promptView.findViewById(R.id.imgclose);
        TextView cancel_dialog = (TextView) promptView.findViewById(R.id.cancel_dialog);
        String msg = "You will have to pay an amount of ₹" + FINALPrice + " in cash at the time of delivery.";
        message_to_confirm_cod.setText(msg);

        imgclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });

        proceed_cod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
                JSONObject confirmObject = new JSONObject();
                try {
                    confirmObject.put("address_id", selectedAddressId);
                    confirmObject.put("apply_zapcash", zapCashStatus);
                    confirmObject.put("amount", FINALPrice);
                    confirmObject.put("cod", true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ////////System.out.println(confirmObject + "____");
                mProgress.setMessage("Proceeding to Transaction ...");
                mProgress.show();


                ApiService.getInstance(shoppingcartnew.this, 1).postData(shoppingcartnew.this, EnvConstants.APP_BASE_URL + "/order/juspay_order", confirmObject, SCREEN_NAME, "confirmPost");

            }
        });

        cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
        alert.show();


    }


    //    Custom alert dialog display function
//    -------------------------------------------------------------------

    protected void showDialogForPincode() {
        LayoutInflater layoutInflater = LayoutInflater.from(shoppingcartnew.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog_pincode_error, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(shoppingcartnew.this);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setCancelable(true);
        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }


    public void displayAdddresses() {
        final RadioGroup radioGroup = new RadioGroup(this);
        JSONObject jobj = new JSONObject();
//        LinearLayout params = new LinearLayout.LayoutParams()
        for (int i = 0; i < addressList.length(); i++) {
            LayoutInflater address_inflater = LayoutInflater.from(shoppingcartnew.this);
            final View view;
            view = address_inflater.inflate(R.layout.addressholder_checkout, null, false);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            view.setLayoutParams(params);
            //params.setMargins(0, 20, 0, 20);
            TextView addressView = (TextView) view.findViewById(R.id.addressText);
            TextView txtname = (TextView) view.findViewById(R.id.tv);
            txtname.setText(GetSharedValues.getZapname(this).toUpperCase());
            final TextView addressEdit = (TextView) view.findViewById(R.id.addressEdit);
            final RelativeLayout selectAddress = (RelativeLayout) view.findViewById(R.id.selectAddress);
            img1 = (ImageView) view.findViewById(R.id.img);

            String address = null;

            try {
                jobj = addressList.getJSONObject(i);
                if (addressList.length() == 1) {
                    selectedAddressId = jobj.getInt("id");
                    if (selectedAddressId > 0) {
                        confirm_checkout.setText("CONFIRM ORDER");
                    } else {
                        confirm_checkout.setText("SELECT PAYMENT METHOD");
                    } 
                }
                if (jobj.getInt("id") == selectedAddressId) {
                    SharedPreferences settings = getApplicationContext().getSharedPreferences("BuySession",
                            Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("PINCODE", jobj.getString("pincode"));
                    editor.apply();
                    Pincode_selected = jobj.getString("pincode");
                    img1.setImageResource(R.drawable.roundshape_bg_blue);
                    selectAddress.setTag("selected");
                } else {

                }
            } catch (Exception e) {
                img1.setImageResource(R.drawable.roundshape_bg_nothing);
                selectAddress.setTag("unselected");
            }


            try {
                address = jobj.getString("name") + " - " + jobj.getString("phone") + ",\n" +
                        jobj.getString("address") + ",\n" +
                        jobj.getString("city") + ", " +
                        jobj.getString("state") + ", \n" +
                        jobj.getString("pincode");

                selectAddress.setId(jobj.getInt("id"));
                addressView.setText(address);
                // addressView.setTextColor(Color.GRAY);
                selectAddress.setTag("unselected");

            } catch (JSONException e) {
                e.printStackTrace();
            }


            final int finalI = i;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedAddressId > 0) {
                        confirm_checkout.setText("CONFIRM ORDER");
                    } else {
                        confirm_checkout.setText("SELECT PAYMENT METHOD");
                    }

                    if (selectAddress.getTag().equals("selected")) {
                        selectedAddressId = selectAddress.getId();
                        img1.setImageResource(R.drawable.roundshape_bg_nothing);
                        // selectAddress.setBackgroundColor(Color.WHITE);
                        AddressHolder.removeAllViews();
                        displayAdddresses();
                        selectAddress.setTag("unselected");
                    } else {
                        selectedAddressId = selectAddress.getId();
                        img1.setImageResource(R.drawable.roundshape_bg_blue);
                        //  selectAddress.setBackgroundColor(Color.BLACK);
                        AddressHolder.removeAllViews();

                        displayAdddresses();
                        selectAddress.setTag("selected");
                        HashMap<String, Object> checkout_step = new HashMap<String, Object>();
                        checkout_step.put("cart_id", cart_id);
                        checkout_step.put("step", "2");
                        checkout_step.put("value_of_cart", FINALPrice);
                        cleverTap.event.push("checkout_step", checkout_step);
                    }

                }
            });
            final JSONObject finalJobj = jobj;
            addressEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirm_checkout.setText("SELECT PAYMENT METHOD");
                    EditIndex = finalI;
                    EditAddress(finalJobj);
                }
            });

            radioGroup.addView(view);

        }
        AddressHolder.addView(radioGroup);
        //  progressbar.setVisibility(View.GONE);
        checkout_main_layout.setVisibility(View.VISIBLE);
        checkout_progressBar.setVisibility(View.GONE);

    }

    private void EditAddress(JSONObject s) {

        try {
            Intent add = new Intent(shoppingcartnew.this, AddAddress.class);
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


    public void Getdata() {
        ApiService.getInstance(shoppingcartnew.this, 1).getData(shoppingcartnew.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/zapcart/checkout/", "checkout");
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
    public void onResponseCallback(JSONObject response, String flag) {
        //System.out.println("shopcart new" + response);
        if (flag.equals("getzapcash")) {
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        JSONObject data = resp.getJSONObject("data");
                        String secondary_status = data.getString("status");
                        if (secondary_status.equals("success")) {
                            if (data.getInt("amount") > 0) {
                                ZapcashAvailable = data.getInt("amount");
                                rlzapcash.setVisibility(View.VISIBLE);
                                TextView zapcash_text = (TextView) findViewById(R.id.zapcash_text);
                                zapcash_text.setText(String.valueOf(ZapcashAvailable));
                                Getdata();
                            } else {
                                rlzapcash.setVisibility(View.GONE);
                                ZapcashAvailable = 0;
                                Getdata();
                            }
                            if (!data.getBoolean("cod")) {
                                rlcod.setVisibility(View.GONE);
                            }

                        } else {
                            rlzapcash.setVisibility(View.GONE);
                            ZapcashAvailable = 0;
                            Getdata();
                        }
                    } else {
                        rlzapcash.setVisibility(View.GONE);
                        ZapcashAvailable = 0;
                        Getdata();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Getdata();
                }
            } else {
                Getdata();
            }

        } else if (flag.equals("checkout")) {
            System.out.println("RESPONSE: " + response);
            try {
                JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
                if (resp != null) {
                    String status = resp.getString("status");
                    if (!status.equals("error")) {
                        JSONObject data = resp.getJSONObject("data");
                        addressList = data.getJSONArray("addresses");
                        //  AddressHolder.setVisibility(View.VISIBLE);
                        AddressHolder.removeAllViews();
                        displayValues(data);
                    }

                }

            } catch (Exception e) {

            }

        } else if (flag.equals("webhook")) {
            try {
                JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
                System.out.println("webhhok" + resp);
                mProgress.dismiss();
                if (resp != null) {
                    String status = resp.getString("status");
                    if (!status.equals("error")) {
                        GodelTracker.getInstance().trackPaymentStatus(txnId, GodelTracker.PaymentStatus.SUCCESS);
                        Intent success = new Intent(shoppingcartnew.this, SummaryPage.class);

                        success.putExtra("TxnId", txnId);
                        startActivity(success);
                        finish();
                    }else{
                        confirm_checkout.setText("RETRY");
                        GodelTracker.getInstance().trackPaymentStatus(txnId, GodelTracker.PaymentStatus.CANCELLED);
                        CustomMessage.getInstance().CustomMessage(shoppingcartnew.this, "Transaction hasbeen cancelled.");

                    }

                }

            } catch (Exception e) {

            }

        } else if (flag.equals("confirmPost")) {
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                System.out.println("azzzzzzzzzzzz" + resp);
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        confirm_checkout.setEnabled(true);

                        JSONObject data = resp.getJSONObject("data");
                        String txnStatus = data.getString("message");

                        if (txnStatus.equals("TXSUCCESS")) {
                            txnId = data.getString("order_id");
                            Boolean Cod_status = false;
                            Cod_status = selectedPaymentMethod.equals("COD");
                            Intent summary = new Intent(shoppingcartnew.this, SummaryPage.class);
                            summary.putExtra("Cod_Status", Cod_status);
                            summary.putExtra("Cod_Amount", FINALPrice);
                            summary.putExtra("TxnId", txnId);
                            startActivity(summary);
                            finish();
                        } else {
                            amount_pay = data.getInt("amount_pay");
                            txnId = data.getString("order_id");
                            SharedPreferences settings = getApplicationContext().getSharedPreferences("BuySession",
                                    Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("mobile_url", data.getString("mobile_url"));
                            editor.putString("TxnId", txnId);

                            editor.putInt("AMOUNT", amount_pay);
                            editor.putInt("AlbumId", AlbumId);
                            editor.putBoolean("CouponStatus", CouponApplied);
                            editor.putBoolean("ZapCashStatus", zapCashStatus);
                            editor.putInt("SelectedAddress", selectedAddressId);
                            editor.putInt("CouponPrice", CouponPrice);
                            editor.apply();

                            BrowserParams browserParams = new BrowserParams();

                            browserParams.setUrl(data.getString("mobile_url"));

                            JuspayBrowserFragment.openJuspayConnection(this);
                            JuspaySafeBrowser.setEndUrls(new String[]{data.getString("endURL") + ".*"});
                            JuspaySafeBrowser.start(this, browserParams, callBack);

                            final int time_out = data.getInt("timeout_min") * 60;

                            t = new Timer();
                            t.scheduleAtFixedRate(new TimerTask() {

                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub
                                    runOnUiThread(new Runnable() {
                                        public void run() {

                                            TimeCounter++;

                                            if (TimeCounter == time_out) {

                                                t.cancel();
                                                JuspaySafeBrowser.exit();
                                                showTimeout();
                                            }


                                        }
                                    });

                                }
                            }, 1000, 1000);




                        }
                    } else {
                        confirm_checkout.setEnabled(true);
                        mProgress.dismiss();
                        System.out.println("CHECKOUT 1");
                        CustomMessage.getInstance().CustomMessage(shoppingcartnew.this, "Oops. Something went wrong!");
                    }
                } catch (JSONException e) {
                    confirm_checkout.setEnabled(true);
                    e.printStackTrace();
                    mProgress.dismiss();
                    System.out.println("CHECKOUT 2 : " + e);
                    CustomMessage.getInstance().CustomMessage(shoppingcartnew.this, "Oops. Something went wrong!");
                }
            } else {
                mProgress.dismiss();
                System.out.println("CHECKOUT 3");
                CustomMessage.getInstance().CustomMessage(shoppingcartnew.this, "Oops. Something went wrong!");
            }
        } else if (flag.equals("pincodecheck")) {
            //System.out.println("pincodecheck:" + response);
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        Boolean PincodeCheck = resp.getBoolean("data");
                        if (PincodeCheck) {
                            mProgress.dismiss();
                            showInputDialog();
                        } else {
                            confirm_checkout.setEnabled(true);
                            mProgress.dismiss();
                            showDialogForPincode();

                        }
                    } else {
                        confirm_checkout.setEnabled(true);
                        mProgress.dismiss();
                        CustomMessage.getInstance().CustomMessage(shoppingcartnew.this, "Oops. Something went wrong!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    confirm_checkout.setEnabled(true);
                    mProgress.dismiss();
                    CustomMessage.getInstance().CustomMessage(shoppingcartnew.this, "Oops. Something went wrong!");
                }
            } else {
                confirm_checkout.setEnabled(true);
                mProgress.dismiss();
                CustomMessage.getInstance().CustomMessage(shoppingcartnew.this, "Oops. Something went wrong!");
            }

        }

    }

    @Override
    public void onErrorCallback(VolleyError error, String flag) {
        confirm_checkout.setEnabled(true);
        mProgress.dismiss();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

//    onbackpressed function
//    -----------------------------------------------------------------

    public void onBackPressed() {
        try {
            if (!callingActivity.equals("activity.Cart")) {
                Intent fwd = new Intent(shoppingcartnew.this, Class.forName(callingActivity));
                fwd.putExtra("AlbumId", AlbumId);
                fwd.putExtra("activity", "shoppingcartnew");
                startActivity(fwd);
                finish();
            } else {
                Intent fwd = new Intent(shoppingcartnew.this, Class.forName(callingActivity));
                fwd.putExtra("AlbumId", AlbumId);
                startActivity(fwd);
                finish();
            }
        } catch (ClassNotFoundException e) {
            Intent fwd = new Intent(shoppingcartnew.this, Cart.class);
            fwd.putExtra("activity", "shoppingcartnew");
            fwd.putExtra("AlbumId", AlbumId);
            startActivity(fwd);
            finish();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mTracker.set("UserName", GetSharedValues.getUsername(getApplicationContext()));
        mTracker.set("ZapUsername", GetSharedValues.getZapname(getApplicationContext()));
        mTracker.setScreenName("Checkout page");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public BrowserCallback callBack = new BrowserCallback() {

        @Override
        public void ontransactionAborted() {
            // ...

            confirm_checkout.setText("RETRY");
            JSONObject confirmObject = new JSONObject();
            try {
                confirmObject.put("order_id", txnId);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            mProgress.dismiss();
            ApiService.getInstance(shoppingcartnew.this, 1).putData(shoppingcartnew.this, EnvConstants.APP_BASE_URL + "/order/juspay_order", confirmObject, SCREEN_NAME, "cancel_order");

            CustomMessage.getInstance().CustomMessage(shoppingcartnew.this, "Transaction hasbeen cancelled.");
        }


        @Override
        public void endUrlReached(String url) {

            System.out.println("return url" + url);
            ApiService.getInstance(shoppingcartnew.this, 1).getData(shoppingcartnew.this, true, SCREEN_NAME, url, "webhook");
            //  GodelTracker.getInstance().trackPaymentStatus(transactionId, GodelTracker.PaymentStatus.SUCCESS);
           // Toast.makeText(getApplicationContext(), "Transaction successful!", Toast.LENGTH_LONG).show();
            CustomMessage.getInstance().CustomMessage(shoppingcartnew.this, "Transaction successful!");
        }

    };

    public void showTimeout() {
        AlertDialog alertDialog = new AlertDialog.Builder(
                this).create();
        System.out.println("asasatimeout");
        // Setting Dialog Title
        alertDialog.setTitle("Transaction timed-out");

        // Setting Dialog Message
        alertDialog.setMessage("Your Payment session has timed out.Please try again");

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.tick);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed

            }
        });

        // Showing Alert Message
        alertDialog.show();
    }


}

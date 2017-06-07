package activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
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

import com.android.volley.VolleyError;
import com.citrus.sdk.Callback;
import com.citrus.sdk.CitrusClient;
import com.citrus.sdk.CitrusUser;
import com.citrus.sdk.TransactionResponse;
import com.citrus.sdk.classes.Amount;
import com.citrus.sdk.classes.CitrusException;
import com.citrus.sdk.payment.CardOption;
import com.citrus.sdk.payment.MerchantPaymentOption;
import com.citrus.sdk.payment.NetbankingOption;
import com.citrus.sdk.payment.PaymentOption;
import com.citrus.sdk.payment.PaymentType;
import com.citrus.sdk.response.BindUserResponse;
import com.citrus.sdk.response.CitrusError;
import com.citrus.sdk.response.CitrusResponse;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
//import com.uxcam.UXCam;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import application.MyApplicationClass;
import network.ApiCommunication;
import network.ApiService;
import payment.CitrusUtils;
import payment.CreditDebitCardDetails;
import payment.NetbankingAdapter;
import utils.CustomMessage;
import utils.FontUtils;
import utils.GetSharedValues;

public class shoppingcartnew extends AppCompatActivity implements ApiCommunication {
    JSONArray addressList = new JSONArray();
    String SCREEN_NAME = "CHECKOUT";
    LinearLayout AddressHolder, EmptyAddressHolder;
    int selectedAddressId = 0;
    int EditIndex = 0;
    Boolean zapCashStatus = false;
    int ZapcashAvailable = 0;
    int ZapcashBalance = 0;
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
    boolean blnet = false;
    public static RelativeLayout rlbank1, rlbank2, rlbank3;
    RelativeLayout rlamount, rladd, rlpay, rlotherbank, rlnet, rlcredit, rlcod, rlsaved, checkout_main_layout;
    public static TextView tvbank1;
    TextView tvbank2, tvbank3;
    LinearLayout lnreview, lnpayment;

    private ArrayList<NetbankingOption> mNetbankingOptionsList = new ArrayList<>();
    CitrusClient citrusClient;
    public static NetbankingOption selectedBank = null;
    PaymentOption selectedSavedCard = null;
    MerchantPaymentOption Options;
    LinearLayout lnselectcredit, lnselectcod, lnselectsaved;
    String selectedPaymentMethod = "";

    ProgressDialog mProgress;
    ProgressBar savedProgressBar;
    ImageView card_image_click, cod_image_click, imgindicator, imageView16, imageView17, img0, img1;
    String Pincode_selected;
    LinearLayout savedcardHolder;
    LinearLayout lnbank1click, lnbank2click, lnbank3click, saved_cards_list_holder;
    public static ImageView imbank1click;
    ImageView imbank2click, imbank3click, imgback;
    String selected_saved_card_position = "";
    TextView zapcash_text;
    Boolean ZapCehck = false;
    Boolean FailedSummaryCheck = false;
    //    Boolean lesser_Check = false;
    TextView tvlabeladdress, tvlabelpayment;
    String callingActivity = "activity.HomePage";
    int amount_pay = 0;
    ProgressBar checkout_progressBar;
    TextView confirm_checkout, addaddress, tvlabelamount, tvtotal, applied_zapcash, priceOrgprice, priceDiscount, priceListprice, priceShipping, finalPrice, empty_saved_message, empty_address_message;
    Tracker mTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_cart_new);
        MyApplicationClass application = (MyApplicationClass) getApplication();
        mTracker = application.getDefaultTracker();
        //UXCam.startWithKey("1dfb25141864376");
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.main_layout);
        FontUtils.setCustomFont(rl, getAssets());

        checkout_main_layout = (RelativeLayout) findViewById(R.id.checkout_main_layout);
        checkout_main_layout.setVisibility(View.INVISIBLE);

        mProgress = new ProgressDialog(shoppingcartnew.this);
        mProgress.setCancelable(false);
        try {
            AlbumId = getIntent().getIntExtra("AlbumId", 0);
        } catch (Exception e) {

        }


        tvlabelpayment = (TextView) findViewById(R.id.textView55);
        zapcash_checkbox = (CheckBox) findViewById(R.id.zapcash_checkbox);


        //System.out.println("ALBUMID:" + AlbumId);
        try {
            callingActivity = "activity." + getIntent().getStringExtra("activity");
        } catch (Exception e) {
            callingActivity = "activity.ShopCart";
        }
        checkout_progressBar = (ProgressBar) findViewById(R.id.checkout_progressBar);
        checkout_progressBar.setVisibility(View.VISIBLE);
        //  final NetbankingAdapter netbankingAdapter = new NetbankingAdapter(this, mNetbankingOptionsList);
        citrusClient = CitrusClient.getInstance(shoppingcartnew.this);
        citrusClient.enableLog(true);
        citrusClient.enableAutoOtpReading(true);
        citrusClient.init(EnvConstants.SIGNUP_ID, EnvConstants.SIGNUP_SECRET, EnvConstants.SIGNIN_ID, EnvConstants.SIGNIN_SECRET, EnvConstants.VANITY, EnvConstants.environment);


        confirm_checkout = (TextView) findViewById(R.id.confirm_checkout);
        confirm_checkout.setText("SELECT DELIVERY ADDRESS");
        AddressHolder = (LinearLayout) findViewById(R.id.AddressHolder);
        rlzapcash = (RelativeLayout) findViewById(R.id.rlzapcash);
        applied_zapcash = (TextView) findViewById(R.id.applied_zapcash);
        tvtotal = (TextView) findViewById(R.id.tvtotal);
        tvlabelamount = (TextView) findViewById(R.id.tvcnt);
        tvlabeladdress = (TextView) findViewById(R.id.textView53);
        addaddress = (TextView) findViewById(R.id.addaddress);

        applied_zapcash.setVisibility(View.INVISIBLE);
        rlamount = (RelativeLayout) findViewById(R.id.relativeLayout7);
        rladd = (RelativeLayout) findViewById(R.id.relativeLayout6);
        rlpay = (RelativeLayout) findViewById(R.id.relativeLayout9);
        lnreview = (LinearLayout) findViewById(R.id.Revieworder);
        lnpayment = (LinearLayout) findViewById(R.id.payment);
        imgback = (ImageView) findViewById(R.id.imgclose);
        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        lnbank1click = (LinearLayout) findViewById(R.id.linearLayoutb1);
        lnbank2click = (LinearLayout) findViewById(R.id.linearLayoutb2);
        lnbank3click = (LinearLayout) findViewById(R.id.linearLayoutb3);

        imbank1click = (ImageView) findViewById(R.id.imgb1);
        imbank2click = (ImageView) findViewById(R.id.imgb2);
        imbank3click = (ImageView) findViewById(R.id.imgb3);


        imgindicator = (ImageView) findViewById(R.id.imgindicator);
        imageView16 = (ImageView) findViewById(R.id.imageView16);
        imageView17 = (ImageView) findViewById(R.id.imageView17);
        img0 = (ImageView) findViewById(R.id.img0);
        img1 = (ImageView) findViewById(R.id.img1);

        rlbank1 = (RelativeLayout) findViewById(R.id.rlbank1);
        rlbank2 = (RelativeLayout) findViewById(R.id.rlbank2);
        rlbank3 = (RelativeLayout) findViewById(R.id.rlbank3);
        rlotherbank = (RelativeLayout) findViewById(R.id.rlother);

        rlnet = (RelativeLayout) findViewById(R.id.rlnet);
        rlcredit = (RelativeLayout) findViewById(R.id.rlcredit);
        rlcod = (RelativeLayout) findViewById(R.id.rlcod);
        rlsaved = (RelativeLayout) findViewById(R.id.rlsaved);

        tvbank1 = (TextView) findViewById(R.id.textViewb62);
        tvbank2 = (TextView) findViewById(R.id.textViewb262);
        tvbank3 = (TextView) findViewById(R.id.textViewb362);

        zapcash_text = (TextView) findViewById(R.id.zapcash_text);

        rlbank1.setVisibility(View.GONE);
        rlbank2.setVisibility(View.GONE);
        rlbank3.setVisibility(View.GONE);
        rlotherbank.setVisibility(View.GONE);


//        lnreview.setVisibility(View.GONE);
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
        GetzapCash();
        BindUser();

        lnselectcredit = (LinearLayout) findViewById(R.id.selectcredit);
        lnselectcod = (LinearLayout) findViewById(R.id.selectcod);
        lnselectsaved = (LinearLayout) findViewById(R.id.selectsaved);

        saved_cards_list_holder = (LinearLayout) findViewById(R.id.saved_cards_list_holder);

        card_image_click = (ImageView) findViewById(R.id.card_image_click);
        cod_image_click = (ImageView) findViewById(R.id.cod_image_click);
        empty_saved_message = (TextView) findViewById(R.id.empty_saved_message);
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

        lnselectcredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (saved_cards_list_holder.getVisibility() == View.VISIBLE) {
                    saved_cards_list_holder.setVisibility(View.GONE);
                }
                if (empty_saved_message.getVisibility() == View.VISIBLE) {
                    empty_saved_message.setVisibility(View.GONE);
                }
                rlbank1.setVisibility(View.GONE);
                rlbank2.setVisibility(View.GONE);
                rlbank3.setVisibility(View.GONE);
                rlotherbank.setVisibility(View.GONE);
                if (!selectedPaymentMethod.equals("CARD")) {
                    confirm_checkout.setText("ENTER CARD DETAILS");
                    selectedPaymentMethod = "CARD";
                    card_image_click.setImageResource(R.drawable.roundshape_bg_blue);
                    cod_image_click.setImageResource(R.drawable.roundshape_bg_nothing);
                    imbank1click.setImageResource(R.drawable.roundshape_bg_nothing);
                    imbank2click.setImageResource(R.drawable.roundshape_bg_nothing);
                    imbank3click.setImageResource(R.drawable.roundshape_bg_nothing);
                    rlcredit.setBackgroundColor(Color.rgb(213, 213, 213));
                    if (rlcod.getVisibility() == View.VISIBLE) {
                        rlcod.setBackgroundResource(R.drawable.background_checkout_holders);
                    }
                    rlnet.setBackgroundResource(R.drawable.background_checkout_holders);
                    rlsaved.setBackgroundResource(R.drawable.background_checkout_holders);
                } else {
                    confirm_checkout.setText("SELECT PAYMENT METHOD");
                    selectedPaymentMethod = "CARD";
                    card_image_click.setImageResource(R.drawable.roundshape_bg_nothing);
                    cod_image_click.setImageResource(R.drawable.roundshape_bg_nothing);
                    imbank1click.setImageResource(R.drawable.roundshape_bg_nothing);
                    imbank2click.setImageResource(R.drawable.roundshape_bg_nothing);
                    imbank3click.setImageResource(R.drawable.roundshape_bg_nothing);
                    rlcredit.setBackgroundResource(R.drawable.background_checkout_holders);
                }
            }
        });

        lnselectcod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (saved_cards_list_holder.getVisibility() == View.VISIBLE) {
                    saved_cards_list_holder.setVisibility(View.GONE);
                }
                if (empty_saved_message.getVisibility() == View.VISIBLE) {
                    empty_saved_message.setVisibility(View.GONE);
                }
                rlbank1.setVisibility(View.GONE);
                rlbank2.setVisibility(View.GONE);
                rlbank3.setVisibility(View.GONE);
                rlotherbank.setVisibility(View.GONE);
                if (!selectedPaymentMethod.equals("COD")) {
                    confirm_checkout.setText("CONFIRM ORDER");
                    selectedPaymentMethod = "COD";
                    cod_image_click.setImageResource(R.drawable.roundshape_bg_blue);
                    card_image_click.setImageResource(R.drawable.roundshape_bg_nothing);
                    imbank1click.setImageResource(R.drawable.roundshape_bg_nothing);
                    imbank2click.setImageResource(R.drawable.roundshape_bg_nothing);
                    imbank3click.setImageResource(R.drawable.roundshape_bg_nothing);
                    if (rlcod.getVisibility() == View.VISIBLE) {
                        rlcod.setBackgroundColor(Color.rgb(213, 213, 213));
                    }
                    rlcredit.setBackgroundResource(R.drawable.background_checkout_holders);
                    rlnet.setBackgroundResource(R.drawable.background_checkout_holders);
                    rlsaved.setBackgroundResource(R.drawable.background_checkout_holders);
                } else {
                    confirm_checkout.setText("SELECT PAYMENT METHOD");
                    selectedPaymentMethod = "COD";
                    cod_image_click.setImageResource(R.drawable.roundshape_bg_blue);
                    card_image_click.setImageResource(R.drawable.roundshape_bg_nothing);
                    imbank1click.setImageResource(R.drawable.roundshape_bg_nothing);
                    imbank2click.setImageResource(R.drawable.roundshape_bg_nothing);
                    imbank3click.setImageResource(R.drawable.roundshape_bg_nothing);
                    if (rlcod.getVisibility() == View.VISIBLE) {
                        rlcod.setBackgroundResource(R.drawable.background_checkout_holders);
                    }
                }
            }
        });

        lnselectsaved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //System.out.println("SAVED:");
                saved_cards_list_holder.removeAllViews();
                if (saved_cards_list_holder.getVisibility() == View.GONE) {
                    saved_cards_list_holder.setVisibility(View.VISIBLE);
                }
                if (empty_saved_message.getVisibility() == View.VISIBLE) {
                    empty_saved_message.setVisibility(View.GONE);
                }
                rlbank1.setVisibility(View.GONE);
                rlbank2.setVisibility(View.GONE);
                rlbank3.setVisibility(View.GONE);
                rlotherbank.setVisibility(View.GONE);
                cod_image_click.setImageResource(R.drawable.roundshape_bg_nothing);
                card_image_click.setImageResource(R.drawable.roundshape_bg_nothing);
                if (!selectedPaymentMethod.equals("SAVEDCARD")) {
                    confirm_checkout.setText("PROCEED TO PAYMENT");
                    img0.setImageResource(R.drawable.grey_up);
                    //System.out.println("SAVED:inside if");
                    selectedPaymentMethod = "SAVEDCARD";
                    rlsaved.setBackgroundColor(Color.rgb(213, 213, 213));
                    if (rlcod.getVisibility() == View.VISIBLE) {
                        rlcod.setBackgroundResource(R.drawable.background_checkout_holders);
                    }
                    rlcredit.setBackgroundResource(R.drawable.background_checkout_holders);
                    rlnet.setBackgroundResource(R.drawable.background_checkout_holders);
                    savedcardHolder = (LinearLayout) findViewById(R.id.savedcardHolder);
                    savedProgressBar = (ProgressBar) findViewById(R.id.saved_card_progressBar);
                    savedProgressBar.setVisibility(View.VISIBLE);
                    fetchWallet();
                } else {
                    confirm_checkout.setText("PROCEED TO PAYMENT");
                    img0.setImageResource(R.drawable.grey_down);
                    //System.out.println("SAVED:inside else");
                    selectedPaymentMethod = "";
                    rlsaved.setBackgroundResource(R.drawable.background_checkout_holders);
                    if (rlcod.getVisibility() == View.VISIBLE) {
                        rlcod.setBackgroundResource(R.drawable.background_checkout_holders);
                    }
                    rlcredit.setBackgroundResource(R.drawable.background_checkout_holders);
                    rlnet.setBackgroundResource(R.drawable.background_checkout_holders);
                }
            }
        });

        CitrusClient.getInstance(this).getMerchantPaymentOptions(new Callback<MerchantPaymentOption>() {
            @Override

            public void success(MerchantPaymentOption merchantPaymentOption) {
                Options = merchantPaymentOption;
                mNetbankingOptionsList.clear();
                mNetbankingOptionsList.addAll(merchantPaymentOption.getNetbankingOptionList());
                tvbank1.setText(mNetbankingOptionsList.get(0).getBankName());
                tvbank2.setText(mNetbankingOptionsList.get(1).getBankName());
                tvbank3.setText(mNetbankingOptionsList.get(2).getBankName());
                //netbankingAdapter.notifyDataSetChanged();
            }

            @Override
            public void error(CitrusError error) {
                ////Log.e("CITRUS ERROR", "Failed to get netbanking options");
            }
        });

        lnbank1click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm_checkout.setText("PROCEED TO PAYMENT");
                selectedPaymentMethod = "NETBANKING";
                imbank1click.setImageResource(R.drawable.roundshape_bg_blue);
                imbank2click.setImageResource(R.drawable.roundshape_bg_nothing);
                imbank3click.setImageResource(R.drawable.roundshape_bg_nothing);
                card_image_click.setImageResource(R.drawable.roundshape_bg_nothing);
                cod_image_click.setImageResource(R.drawable.roundshape_bg_nothing);
                selectedBank = mNetbankingOptionsList.get(0);

            }
        });

        lnbank2click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm_checkout.setText("PROCEED TO PAYMENT");
                selectedPaymentMethod = "NETBANKING";
                imbank1click.setImageResource(R.drawable.roundshape_bg_nothing);
                imbank2click.setImageResource(R.drawable.roundshape_bg_blue);
                imbank3click.setImageResource(R.drawable.roundshape_bg_nothing);
                card_image_click.setImageResource(R.drawable.roundshape_bg_nothing);
                cod_image_click.setImageResource(R.drawable.roundshape_bg_nothing);
                selectedBank = mNetbankingOptionsList.get(1);
            }
        });

        lnbank3click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm_checkout.setText("PROCEED TO PAYMENT");
                selectedPaymentMethod = "NETBANKING";
                imbank1click.setImageResource(R.drawable.roundshape_bg_nothing);
                imbank2click.setImageResource(R.drawable.roundshape_bg_nothing);
                imbank3click.setImageResource(R.drawable.roundshape_bg_blue);
                card_image_click.setImageResource(R.drawable.roundshape_bg_nothing);
                cod_image_click.setImageResource(R.drawable.roundshape_bg_nothing);
                selectedBank = mNetbankingOptionsList.get(2);
            }
        });

        rlnet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (saved_cards_list_holder.getVisibility() == View.VISIBLE) {
                    saved_cards_list_holder.setVisibility(View.GONE);
                }
                card_image_click.setImageResource(R.drawable.roundshape_bg_nothing);
                cod_image_click.setImageResource(R.drawable.roundshape_bg_nothing);
                selectedPaymentMethod = "NETBANKING";
                if (!blnet) {
                    confirm_checkout.setText("PROCEED TO PAYMENT");
                    blnet = true;
                    img1.setImageResource(R.drawable.grey_up);
                    rlbank1.setVisibility(View.VISIBLE);
                    rlbank2.setVisibility(View.VISIBLE);
                    rlbank3.setVisibility(View.VISIBLE);
                    rlotherbank.setVisibility(View.VISIBLE);
                    rlnet.setBackgroundColor(Color.rgb(213, 213, 213));
                    rlcredit.setBackgroundResource(R.drawable.background_checkout_holders);
                    if (rlcod.getVisibility() == View.VISIBLE) {
                        rlcod.setBackgroundResource(R.drawable.background_checkout_holders);
                    }
                    rlsaved.setBackgroundResource(R.drawable.background_checkout_holders);
                } else {
                    confirm_checkout.setText("PROCEED TO PAYMENT");
                    blnet = false;
                    img1.setImageResource(R.drawable.grey_down);
                    rlbank1.setVisibility(View.GONE);
                    rlbank2.setVisibility(View.GONE);
                    rlbank3.setVisibility(View.GONE);
                    rlotherbank.setVisibility(View.GONE);
                    rlnet.setBackgroundResource(R.drawable.background_checkout_holders);
                    rlcredit.setBackgroundResource(R.drawable.background_checkout_holders);
                    if (rlcod.getVisibility() == View.VISIBLE) {
                        rlcod.setBackgroundResource(R.drawable.background_checkout_holders);
                    }
                    rlsaved.setBackgroundResource(R.drawable.background_checkout_holders);
                }
            }
        });

        rlotherbank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNetBankList();
                confirm_checkout.setText("PROCEED TO PAYMENT");
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
        });

        rlpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!blpayment) {
                    if (selectedPaymentMethod.length() > 0) {
                        if (selectedPaymentMethod.equals("CARD")) {
                            confirm_checkout.setText("ENTER CARD DETAILS");
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
//        progressbar.setVisibility(View.VISIBLE);
        ApiService.getInstance(shoppingcartnew.this, 1).getData(shoppingcartnew.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/my_zapcash/", "getzapcash");

    }


    //    Bind user
//    ---------------------------------------------------------------------------

    private void BindUser() {
        String emailId = GetSharedValues.getUseremail(shoppingcartnew.this);
        String mobileNo = GetSharedValues.getUserephonenumber(shoppingcartnew.this);

        citrusClient.bindUserByMobile(emailId, mobileNo, new Callback<BindUserResponse>() {
            @Override
            public void success(BindUserResponse bindUserResponse) {
                //System.out.println("BINDUSER:" + bindUserResponse.getMessage() + "___" + bindUserResponse.getResponseCode());

            }

            @Override
            public void error(CitrusError citrusError) {
                //System.out.println("BINDUSER:" + citrusError.getMessage());
//               CustomMessage.getInstance().CustomMessage(SavedCardDetails.this, citrusError.getMessage());
//                progressBar.setVisibility(View.GONE);
            }
        });
    }


    //    Fetch saved cards
//    -------------------------------------------------------------

    private void fetchWallet() {
        //System.out.println("SAVED:inside fetch wallet");
        citrusClient.getWallet(new Callback<List<PaymentOption>>() {
            @Override
            public void success(List<PaymentOption> paymentOptionList) {
                //System.out.println("SAVED:inside success:" + paymentOptionList.size());
                savedProgressBar.setVisibility(View.GONE);
                if (paymentOptionList.size() > 0) {
                    empty_saved_message.setVisibility(View.GONE);
                    DisplaySavedcards(paymentOptionList);
                } else {
                    empty_saved_message.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void error(CitrusError error) {
                //System.out.println("SAVED:inside error:");
                savedProgressBar.setVisibility(View.GONE);
                empty_saved_message.setVisibility(View.GONE);
                //////System.out.println("fetch wallet error__" + error.getMessage());
            }
        });
    }

//    Display saved cards
//    --------------------------------------------------------------

    private void DisplaySavedcards(final List<PaymentOption> paymentOptionList) {
        selectedPaymentMethod = "SAVEDCARD";

        for (int i = 0; i < paymentOptionList.size(); i++) {
            LayoutInflater address_inflater = LayoutInflater.from(shoppingcartnew.this);
            final View view;
            view = address_inflater.inflate(R.layout.savedcard_list, null, false);
            ImageView card_image = (ImageView) view.findViewById(R.id.saved_card_image);
            TextView card_text = (TextView) view.findViewById(R.id.saved_card_bank_name);
            final LinearLayout click = (LinearLayout) view.findViewById(R.id.linearLayout7);
            final ImageView selectCard = (ImageView) view.findViewById(R.id.img);
            final PaymentOption paymentOption = paymentOptionList.get(i);
            if (paymentOption != null) {
                if (paymentOption instanceof CardOption) {
                    card_text.setText(((CardOption) paymentOption).getCardNumber());
                    Drawable mCardIconDrawable = paymentOption.getOptionIcon(shoppingcartnew.this);
                    card_image.setImageDrawable(mCardIconDrawable);
                }
            }

            try {
                if (selected_saved_card_position.equals(String.valueOf(i))) {
                    selectCard.setImageResource(R.drawable.roundshape_bg_blue);
                    selectedSavedCard = paymentOption;
                } else {
                    selectCard.setImageResource(R.drawable.roundshape_bg_nothing);
                }
            } catch (Exception e) {
                //System.out.println(e);
                selectCard.setImageResource(R.drawable.roundshape_bg_nothing);
            }


            final int finalI = i;
            click.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (!selected_saved_card_position.equals(String.valueOf(finalI))) {
                            selectCard.setImageResource(R.drawable.roundshape_bg_blue);
                            imbank1click.setImageResource(R.drawable.roundshape_bg_nothing);
                            imbank2click.setImageResource(R.drawable.roundshape_bg_nothing);
                            imbank3click.setImageResource(R.drawable.roundshape_bg_nothing);
                            card_image_click.setImageResource(R.drawable.roundshape_bg_nothing);
                            cod_image_click.setImageResource(R.drawable.roundshape_bg_nothing);
                            selected_saved_card_position = String.valueOf(finalI);
                            selectedPaymentMethod = "SAVEDCARD";
                            selectedSavedCard = paymentOption;
                            saved_cards_list_holder.removeAllViews();
                            DisplaySavedcards(paymentOptionList);
                        } else {
                            selectedPaymentMethod = "";
                            selected_saved_card_position = "";
                            selectCard.setImageResource(R.drawable.roundshape_bg_nothing);
                            imbank1click.setImageResource(R.drawable.roundshape_bg_nothing);
                            imbank2click.setImageResource(R.drawable.roundshape_bg_nothing);
                            imbank3click.setImageResource(R.drawable.roundshape_bg_nothing);
                            card_image_click.setImageResource(R.drawable.roundshape_bg_nothing);
                            cod_image_click.setImageResource(R.drawable.roundshape_bg_nothing);
                            saved_cards_list_holder.removeAllViews();
                            DisplaySavedcards(paymentOptionList);
                        }
                    } catch (Exception e) {
                        selectedPaymentMethod = "SAVEDCARD";
                        selectCard.setImageResource(R.drawable.roundshape_bg_blue);
                        selected_saved_card_position = String.valueOf(finalI);
                        selectedSavedCard = paymentOption;
                        imbank1click.setImageResource(R.drawable.roundshape_bg_nothing);
                        imbank2click.setImageResource(R.drawable.roundshape_bg_nothing);
                        imbank3click.setImageResource(R.drawable.roundshape_bg_nothing);
                        card_image_click.setImageResource(R.drawable.roundshape_bg_nothing);
                        cod_image_click.setImageResource(R.drawable.roundshape_bg_nothing);
                        saved_cards_list_holder.removeAllViews();
                        DisplaySavedcards(paymentOptionList);
                    }
                }
            });
            saved_cards_list_holder.addView(view);
        }

    }


//  Confirm click event
//    -------------------------------------------------------------

    public void confirmCheckout(View v) {
        if (selectedAddressId != 0) {
            if (selectedPaymentMethod.equals("COD")) {
                if (FINALPrice <= 40000) {
                    PincodeCheck();
                } else {
                    CustomMessage.getInstance().CustomMessage(shoppingcartnew.this, "Cash on delivery is available only for products less than Rs10,000.");
                }
            } else if (selectedPaymentMethod.equals("CARD")) {
                Confirm();
            } else if (selectedPaymentMethod.equals("NETBANKING")) {
                Confirm();
            } else if (selectedPaymentMethod.equals("SAVEDCARD")) {
                Confirm();
            } else if (ZapCehck) {
                //System.out.println("inside zapcash applied and confirm");
                Confirm();
            } else {
//                CustomMessage.getInstance().CustomMessage(shoppingcartnew.this, "Please select a payment option.");
                if (!blpayment) {
                    if (selectedPaymentMethod.length() > 0) {
                        if (selectedPaymentMethod.equals("CARD")) {
                            confirm_checkout.setText("ENTER CARD DETAILS");
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
                    lnpayment.setVisibility(View.GONE);
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

//                if (!bladd) {
//                    tvlabelamount.setText("1. Total Payment");
//                    imageView16.setImageResource(R.drawable.grey_up);
//                    SpannableString spanString = new SpannableString("2. Delivery Address");
//                    spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
//                    tvlabeladdress.setText(spanString);
//
//                    SpannableString spanString2 = new SpannableString("1.Total Payment");
//                    spanString2.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString2.length(), 0);
//                    tvlabelamount.setText(spanString2);
//
//                    SpannableString spanString3 = new SpannableString("3. Payment Method");
//                    spanString3.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString3.length(), 0);
//                    tvlabelpayment.setText(spanString3);
//
//                    DisplayUpatedValues();
//                    lnreview.setVisibility(View.GONE);
//                    lnpayment.setVisibility(View.GONE);
//                    if (addressList.length() > 0) {
//                        AddressHolder.setVisibility(View.VISIBLE);
//                    } else {
//                        empty_address_message.setVisibility(View.VISIBLE);
//                        SpannableString spanString_empty_address = new SpannableString("Hey! You haven't added any address yet. Add Address");
//                        spanString_empty_address.setSpan(new UnderlineSpan(), 40, spanString_empty_address.length(), 0);
//                        empty_address_message.setText(spanString_empty_address);
//                    }
//                    blamount = false;
//                    blpayment = false;
//                    bladd = true;
//                } else {
//                    imageView16.setImageResource(R.drawable.grey_down);
//                    AddressHolder.setVisibility(View.GONE);
//                    empty_address_message.setVisibility(View.GONE);
//                    bladd = false;
//                    SpannableString spanString = new SpannableString("2. Delivery Address");
//                    spanString.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString.length(), 0);
//                    tvlabeladdress.setText(spanString);
//                }

        }
    }


    //    Saved card payment function
//    -------------------------------------------------------------
    private void proceedToPayment(CitrusUtils.PaymentType paymentType, final PaymentOption paymentOption, String txnRef) {
        PaymentType paymentType1;
        Callback<TransactionResponse> callback = new Callback<TransactionResponse>() {
            @Override
            public void success(TransactionResponse transactionResponse) {
                ////System.out.println("success inside payment ___" + transactionResponse.getMessage());
                String TxnId = transactionResponse.getTransactionId();
                if (transactionResponse.getTransactionStatus().toString().contains("SUCCESS")) {
                    ToOrderSummary(true, TxnId, null, paymentOption);
                } else {
                    ToOrderSummary(false, TxnId, null, paymentOption);
                }
            }

            @Override
            public void error(CitrusError error) {
                //////System.out.println("Confirm pay: inside error: " + error.getMessage() + "__" + error.getTransactionResponse().getTransactionStatus().toString());
                mProgress.dismiss();
                ToOrderSummary(false, error.getTransactionResponse().getTransactionId(), null, paymentOption);


            }
        };

        try {

            Amount amountObj = new Amount(String.valueOf(FINALPrice));
            paymentType1 = new PaymentType.PGPayment(amountObj, EnvConstants.BILL_URL + txnRef + "/", paymentOption, new CitrusUser(citrusClient.getUserEmailId(), citrusClient.getUserMobileNumber()));
            citrusClient.pgPayment((PaymentType.PGPayment) paymentType1, callback);

        } catch (CitrusException e) {
            e.printStackTrace();
            //////System.out.println(e.getMessage() + "__error in payment");
            mProgress.dismiss();

        }
    }


//    NetBanking Payment process function
//    ----------------------------------------------------------

    private void PaymentProcess(final NetbankingOption netbankingOption, String TxnRef) {
        Callback<TransactionResponse> callback = new Callback<TransactionResponse>() {
            @Override
            public void success(TransactionResponse transactionResponse) {
                ////Log.e("CITRUS SUCCESS", transactionResponse.getMessage() + "");
                String TxnId = transactionResponse.getTransactionId();
                if (transactionResponse.getTransactionStatus().toString().contains("SUCCESS")) {
                    savePaymentOption(netbankingOption, TxnId);

                } else {
                    confirm_checkout.setEnabled(true);
                    ToOrderSummary(false, TxnId, netbankingOption, null);
                }


            }

            @Override
            public void error(CitrusError error) {
                ////Log.e("CITRUS ERROR", error.getMessage());
                confirm_checkout.setEnabled(true);
                ToOrderSummary(false, error.getTransactionResponse().getTransactionId(), netbankingOption, null);
                mProgress.dismiss();
            }
        };
        try {
            Amount amountObj = new Amount(String.valueOf(FINALPrice));
            PaymentType paymentType1 = new PaymentType.PGPayment(amountObj, EnvConstants.BILL_URL + TxnRef + "/", netbankingOption, new CitrusUser(citrusClient.getUserEmailId(), citrusClient.getUserMobileNumber()));
            citrusClient.pgPayment((PaymentType.PGPayment) paymentType1, callback);
        } catch (CitrusException e) {
            e.printStackTrace();
        }
    }


    //    Save payment option
//    ----------------------------------------------------------

    private void savePaymentOption(final NetbankingOption netbankingOption, final String TxnId) {
        citrusClient.savePaymentOption(netbankingOption, new Callback<CitrusResponse>() {
            @Override
            public void success(CitrusResponse citrusResponse) {
                if (citrusResponse.getStatus().toString().contains("SUCCESS")) {
                    confirm_checkout.setEnabled(true);
                    ToOrderSummary(true, TxnId, netbankingOption, null);
                } else {
                    confirm_checkout.setEnabled(true);
                    ToOrderSummary(true, TxnId, netbankingOption, null);
                }
            }

            @Override
            public void error(CitrusError error) {
                confirm_checkout.setEnabled(true);
                ToOrderSummary(true, TxnId, netbankingOption, null);
            }
        });
    }


    //  To order summary
//    -------------------------------------------------------------------------------

    public void ToOrderSummary(Boolean status, String TxnId, NetbankingOption netbankingOption, PaymentOption paymentOption) {
        if (status) {
            mProgress.dismiss();
            Intent success = new Intent(shoppingcartnew.this, SummaryPage.class);
            success.putExtra("TxnId", TxnId);
            startActivity(success);
            finish();
        } else {
            mProgress.dismiss();
            confirm_checkout.setEnabled(true);
            //System.out.println("Zapcash:" + zapCashStatus);
            Intent failed = new Intent(shoppingcartnew.this, FailedSummaryPage.class);
            failed.putExtra("Payment", selectedPaymentMethod);
            failed.putExtra("Netbank", netbankingOption);
            failed.putExtra("SavedCard", paymentOption);
            failed.putExtra("ZapCashUsed", zapCashStatus);
            failed.putExtra("selectedAddress", selectedAddressId);
            failed.putExtra("TxnId", TxnId);
            startActivity(failed);
            finish();
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
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ////////System.out.println(confirmObject + "____");
            mProgress.setMessage("Proceeding to Transaction ...");
            mProgress.show();
            ApiService.getInstance(shoppingcartnew.this, 1).postData(shoppingcartnew.this, EnvConstants.APP_BASE_URL + "/payment/confirmorder/", confirmObject, SCREEN_NAME, "confirmPost");

        } else if (selectedPaymentMethod.equals("COD")) {
            PincodeCheck();
        } else if (selectedPaymentMethod.equals("NETBANKING")) {
            confirm_checkout.setEnabled(false);
            JSONObject confirmObject = new JSONObject();
            try {
                confirmObject.put("address_id", selectedAddressId);
                confirmObject.put("apply_zapcash", zapCashStatus);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ////////System.out.println(confirmObject + "____");
            mProgress.setMessage("Proceeding to Transaction ...");
            mProgress.show();
            ApiService.getInstance(shoppingcartnew.this, 1).postData(shoppingcartnew.this, EnvConstants.APP_BASE_URL + "/payment/confirmorder/", confirmObject, SCREEN_NAME, "confirmPost");

        } else if (selectedPaymentMethod.equals("SAVEDCARD")) {
            confirm_checkout.setEnabled(false);
            JSONObject confirmObject = new JSONObject();
            try {
                confirmObject.put("address_id", selectedAddressId);
                confirmObject.put("apply_zapcash", zapCashStatus);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ////////System.out.println(confirmObject + "____");
            mProgress.setMessage("Proceeding to Transaction ...");
            mProgress.show();
            ApiService.getInstance(shoppingcartnew.this, 1).postData(shoppingcartnew.this, EnvConstants.APP_BASE_URL + "/payment/confirmorder/", confirmObject, SCREEN_NAME, "confirmPost");

        } else {
            confirm_checkout.setEnabled(false);
            JSONObject confirmObject = new JSONObject();
            try {
                confirmObject.put("address_id", selectedAddressId);
                confirmObject.put("apply_zapcash", zapCashStatus);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ////////System.out.println(confirmObject + "____");
            mProgress.setMessage("Proceeding to Transaction ...");
            mProgress.show();
            ApiService.getInstance(shoppingcartnew.this, 1).postData(shoppingcartnew.this, EnvConstants.APP_BASE_URL + "/payment/confirmorder/", confirmObject, SCREEN_NAME, "confirmPost");

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

    public void showNetBankList() {
        try {

            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.netbanking_list, null);
            dialogBuilder.setView(dialogView);
            ListView lvfontlist = (ListView) dialogView.findViewById(R.id.listfont);
            ImageView imgclose = (ImageView) dialogView.findViewById(R.id.imgclose);
            final AlertDialog b = dialogBuilder.create();
            final NetbankingAdapter netbankingAdapter = new NetbankingAdapter(this, mNetbankingOptionsList, b);
            lvfontlist.setAdapter(netbankingAdapter);
            dialogBuilder.setCancelable(false);
            imgclose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    b.cancel();
                }
            });


            b.show();
        } catch (Exception e) {


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


            if (item.has("cod")){
                if (!item.getBoolean("cod")){
                    rlcod.setVisibility(View.GONE);
                }
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
            String sp = getResources().getString(R.string.Rs) + String.valueOf(item.getInt("total_shipping_charge"));
            priceShipping.setText(sp);
            finalPrice.setText(finalP);
            tvtotal.setText(finalP);

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


// Prompt for enter cvv function
//    --------------------------------------------------------------

    private void showPrompt(final CitrusUtils.PaymentType paymentType, final PaymentOption paymentOption, final String txnRef) {
        LayoutInflater layoutInflater = LayoutInflater.from(shoppingcartnew.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog_cvv, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(shoppingcartnew.this);
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setView(promptView);
        final AlertDialog alert = alertDialogBuilder.create();

        final EditText editText = (EditText) promptView.findViewById(R.id.cvv_number);
        TextView proceed_cvv = (TextView) promptView.findViewById(R.id.proceed_cvv);
        ImageView imgclose = (ImageView) promptView.findViewById(R.id.imgclose);

        imgclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });

        proceed_cvv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress.setMessage("Processing Payment ...Please Wait...");
                mProgress.show();
                String cvv = editText.getText().toString();
                editText.clearFocus();
                // Hide the keyboard.
                InputMethodManager imm = (InputMethodManager) shoppingcartnew.this.getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                // Set the OTP
                ((CardOption) paymentOption).setCardCVV(cvv);
                proceedToPayment(paymentType, paymentOption, txnRef);
            }
        });

        // create an alert dialog
        editText.requestFocus();
        alert.show();


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
                    confirmObject.put("cod", true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ////////System.out.println(confirmObject + "____");
                mProgress.setMessage("Proceeding to Transaction ...");
                mProgress.show();
                ApiService.getInstance(shoppingcartnew.this, 1).postData(shoppingcartnew.this, EnvConstants.APP_BASE_URL + "/payment/confirmorder/", confirmObject, SCREEN_NAME, "confirmPost");

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
                    confirm_checkout.setText("SELECT PAYMENT METHOD");
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
                    confirm_checkout.setText("SELECT PAYMENT METHOD");
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
            System.out.println("RESPONSE: "+response);
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
        } else if (flag.equals("confirmPost")) {
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                //System.out.println(resp);
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        confirm_checkout.setEnabled(true);
                        mProgress.dismiss();
                        JSONObject data = resp.getJSONObject("data");
                        String txnStatus = data.getString("message");

                        if (txnStatus.equals("TXSUCCESS")) {
                            String txnId = data.getString("transaction_id");
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
                            String txnRef = data.getString("transaction_ref");
                            SharedPreferences settings = getApplicationContext().getSharedPreferences("BuySession",
                                    Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putInt("AMOUNT", amount_pay);
                            editor.putInt("AlbumId", AlbumId);
                            editor.putBoolean("CouponStatus", CouponApplied);
                            editor.putBoolean("ZapCashStatus", zapCashStatus);
                            editor.putInt("SelectedAddress", selectedAddressId);
                            editor.putInt("CouponPrice", CouponPrice);
                            editor.apply();


                            if (selectedPaymentMethod.equals("CARD")) {
                                Intent paymentMode = new Intent(shoppingcartnew.this, CreditDebitCardDetails.class);
                                paymentMode.putExtra("AlbumId", AlbumId);
                                paymentMode.putExtra("CouponStatus", CouponApplied);
                                paymentMode.putExtra("ZapCashStatus", zapCashStatus);
                                paymentMode.putExtra("CouponPrice", CouponPrice);
                                paymentMode.putExtra("selectedAddress", selectedAddressId);
                                paymentMode.putExtra("TxnRef", txnRef);
                                paymentMode.putExtra("AMOUNT", amount_pay);
                                startActivity(paymentMode);
                                finish();
                            } else if (selectedPaymentMethod.equals("NETBANKING")) {
                                PaymentProcess(selectedBank, txnRef);
                            } else if (selectedPaymentMethod.equals("SAVEDCARD")) {
                                if (selectedSavedCard instanceof CardOption) {
                                    showPrompt(CitrusUtils.PaymentType.PG_PAYMENT, selectedSavedCard, txnRef);
                                    //////System.out.println(paymentOption + "__inside onitem click " + paymentOption.getAnalyticsPaymentType());
                                } else {
                                    proceedToPayment(CitrusUtils.PaymentType.PG_PAYMENT, selectedSavedCard, txnRef);
                                }

                            } else {
                                Intent summary = new Intent(shoppingcartnew.this, FailedSummaryPage.class);
                                startActivity(summary);
                                finish();
                            }


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
            if (!callingActivity.equals("activity.ShopCart")) {
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
            Intent fwd = new Intent(shoppingcartnew.this, ShopCart.class);
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
}

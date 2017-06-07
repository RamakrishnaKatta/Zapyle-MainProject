package activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.android.volley.VolleyError;
import com.citrus.sdk.Callback;
import com.citrus.sdk.CitrusClient;
import com.citrus.sdk.CitrusUser;
import com.citrus.sdk.TransactionResponse;
import com.citrus.sdk.classes.Amount;
import com.citrus.sdk.classes.CitrusException;
import com.citrus.sdk.payment.CardOption;
import com.citrus.sdk.payment.NetbankingOption;
import com.citrus.sdk.payment.PaymentOption;
import com.citrus.sdk.payment.PaymentType;
import com.citrus.sdk.response.CitrusError;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
//import com.uxcam.UXCam;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;


import org.json.JSONException;
import org.json.JSONObject;

import application.MyApplicationClass;
import network.ApiCommunication;
import network.ApiService;
import payment.CitrusUtils;
import utils.CommonFinish;
import utils.CustomMessage;
import utils.ExternalFunctions;
import utils.GetSharedValues;

//import com.//Appsee.//Appsee;

/**
 * Created by haseeb on 19/11/15.
 */
public class FailedSummaryPage extends ActionBarActivity implements ApiCommunication {
    public String SCREEN_NAME = "FAILEDTRANSACTION";

    //    selected items
    ProgressBar progressbar;
    Boolean zapCashStatus = false;
    TextView tryAgain;

    Double FINALPrice;
    int ZAPCashUsed = 0;
    NetbankingOption netbankingOption = null;
    PaymentOption paymentOption = null;
    CardOption cardOption = null;
    String selectedPaymentMethod;

    ProgressDialog mProgress;

    String TxnId;
    MixpanelAPI mixpanel;
    Boolean CouponApplied = false;
    Boolean Card_Status = false;
    int AlbumId = 0;
    int CouponPrice = 0;
    CitrusClient citrusClient;
    Boolean zapcashUsed = false;
    int selectedAddressId = 0;
    Boolean Cod_status = false;
    TextView payment_options;
    Tracker mTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.failed_transaction);
        MyApplicationClass application = (MyApplicationClass) getApplication();
        mTracker = application.getDefaultTracker();
        //UXCam.startWithKey("1dfb25141864376");
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        //////Thread.currentThread().setDefaultUncaughtExceptionHandler(new SplashScreen.MyUncaughtExceptionHandler());

        mProgress = new ProgressDialog(FailedSummaryPage.this);
        mProgress.setCancelable(false);

        citrusClient = CitrusClient.getInstance(FailedSummaryPage.this);
        citrusClient.enableLog(true);
        citrusClient.enableAutoOtpReading(true);
        citrusClient.init(EnvConstants.SIGNUP_ID, EnvConstants.SIGNUP_SECRET, EnvConstants.SIGNIN_ID, EnvConstants.SIGNIN_SECRET, EnvConstants.VANITY, EnvConstants.environment);


        LocalBroadcastManager.getInstance(this).registerReceiver(CommonFinish.broadcastReceiver, new IntentFilter("FailedSummaryPage"));
        ExternalFunctions.cContextArray[17] = FailedSummaryPage.this;


        SharedPreferences settings = getSharedPreferences("BuySession",
                Context.MODE_PRIVATE);

        try {
            ZAPCashUsed = Integer.parseInt(settings.getString("ZAPCASH", ""));
        } catch (Exception e) {

        }

        AlbumId = settings.getInt("AlbumId", 0);
        CouponApplied = settings.getBoolean("CouponStatus", false);
        CouponPrice = settings.getInt("CouponPrice", 0);
        zapCashStatus = settings.getBoolean("ZapCashUsed", false);
        tryAgain = (TextView) findViewById(R.id.tryAgain);
        payment_options = (TextView) findViewById(R.id.payment_options);


        finishFeed(FailedSummaryPage.this);

        try {
            TxnId = getIntent().getStringExtra("TxnId");
        } catch (Exception e) {

        }

        try {
            zapcashUsed = getIntent().getBooleanExtra("ZapCashUsed", false);
        } catch (Exception e) {

        }

        //System.out.println("zapCashStatus:" + zapcashUsed);

        try {
            selectedAddressId = getIntent().getIntExtra("selectedAddress", 0);
        } catch (Exception e) {

        }

        //System.out.println("PAYMENT:" + selectedAddressId);

        try {
            selectedPaymentMethod = getIntent().getStringExtra("Payment");
        } catch (Exception e) {

        }

        try {
            cardOption = getIntent().getParcelableExtra("CardDetail");
        } catch (Exception e) {

        }

        try {
            netbankingOption = getIntent().getParcelableExtra("Netbank");
        } catch (Exception e) {

        }

        try {
            paymentOption = getIntent().getParcelableExtra("SavedCard");
        } catch (Exception e) {

        }


        try {
            TxnId = getIntent().getStringExtra("TxnId");
        } catch (Exception e) {

        }


        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final View customView = getLayoutInflater().inflate(R.layout.summary_action_bar, null);
        getSupportActionBar().setCustomView(customView);
        Toolbar parent = (Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0, 0);
        TextView heading = (TextView) customView.findViewById(R.id.heading);
        heading.setText("Payment Failed");

        mixpanel = MixpanelAPI.getInstance(FailedSummaryPage.this, getResources().getString(R.string.mixpanelToken));

        //System.out.println("PAYMENTETHOD:" + selectedPaymentMethod + "__" + netbankingOption + "__" + paymentOption + "__" + cardOption);

        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (selectedPaymentMethod) {
                    case "NETBANKING":
                        Confirm();
                        break;

                    case "SAVEDCARD":
                        Confirm();
                        break;

                    case "COD":
                        Confirm();
                        break;

                    case "CARD":
                        Confirm();
                        break;
                }
            }
        });


        payment_options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FailedSummaryPage.this, shoppingcartnew.class);
                intent.putExtra("ZapCashUsed", zapcashUsed);
                intent.putExtra("selectedAddress", selectedAddressId);
                intent.putExtra("AlbumId", AlbumId);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("activity", "ShopCart");
                startActivity(intent);
                finish();
            }
        });
    }


    //    Saved card payment function
//    -------------------------------------------------------------
    private void proceedToPayment(CitrusUtils.PaymentType paymentType, final PaymentOption paymentOption, String txnRef, int amount) {
        //System.out.println("PAYMENT:" + "inside saved card payment:" + paymentOption + "__" + txnRef);
        PaymentType paymentType1;
        Callback<TransactionResponse> callback = new Callback<TransactionResponse>() {
            @Override
            public void success(TransactionResponse transactionResponse) {
                //System.out.println("PAYMENT success inside payment ___" + transactionResponse.getMessage());
                String TxnId = transactionResponse.getTransactionId();
                if (transactionResponse.getTransactionStatus().toString().contains("SUCCESS")) {
                    ToOrderSummary(true, TxnId, null, paymentOption, null);
                } else {
//                    ToOrderSummary(false, TxnId, null, paymentOption, null);
                }
            }

            @Override
            public void error(CitrusError error) {
                //System.out.println("PAYMENTConfirm pay: inside error: " + error.getMessage() + "__" + error.getTransactionResponse().getTransactionStatus().toString());
                mProgress.dismiss();
//                ToOrderSummary(false, error.getTransactionResponse().getTransactionId(), null, paymentOption, null);


            }
        };

        try {

            Amount amountObj = new Amount(String.valueOf(amount));
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

    private void PaymentProcess(final NetbankingOption netbankingOption, String TxnRef, int amount) {
        Callback<TransactionResponse> callback = new Callback<TransactionResponse>() {
            @Override
            public void success(TransactionResponse transactionResponse) {
                ////Log.e("CITRUS SUCCESS", transactionResponse.getMessage() + "");
                String TxnId = transactionResponse.getTransactionId();
                if (transactionResponse.getTransactionStatus().toString().contains("SUCCESS")) {
                    ToOrderSummary(false, TxnId, netbankingOption, null, null);
                } else {
//                    tryAgain.setEnabled(true);
//                    ToOrderSummary(false, TxnId, netbankingOption, null, null);
                }


            }

            @Override
            public void error(CitrusError error) {
                ////Log.e("CITRUS ERROR", error.getMessage());
                tryAgain.setEnabled(true);
//                ToOrderSummary(false, error.getTransactionResponse().getTransactionId(), netbankingOption, null, null);
                mProgress.dismiss();
            }
        };
        try {
            Amount amountObj = new Amount(String.valueOf(amount));
            PaymentType paymentType1 = new PaymentType.PGPayment(amountObj, EnvConstants.BILL_URL + TxnRef + "/", netbankingOption, new CitrusUser(citrusClient.getUserEmailId(), citrusClient.getUserMobileNumber()));
            citrusClient.pgPayment((PaymentType.PGPayment) paymentType1, callback);
        } catch (CitrusException e) {
            e.printStackTrace();
        }
    }


//    Card pay function
//    --------------------------------------------------------------

    private void ConfirmPay(String TxnRef, int AMOUNT) {
        PaymentType paymentType;
        Callback<TransactionResponse> callback = new Callback<TransactionResponse>() {
            @Override
            public void success(TransactionResponse transactionResponse) {
                //System.out.println("payment response check :" + transactionResponse.getTransactionStatus().toString() + "__" + transactionResponse.getTransactionId());
                String TxnId = transactionResponse.getTransactionId();
                if (transactionResponse.getTransactionStatus().toString().contains("SUCCESS")) {
                    ToOrderSummary(true, TxnId, null, null, cardOption);

                } else {
//                    ToOrderSummary(false, TxnId, null, null, cardOption);
                }

            }

            @Override
            public void error(CitrusError error) {
                //System.out.println("Confirm pay: inside error: " + error.getMessage() + "__" + error.getTransactionResponse() + "___" + error.getStatus());
                if (error.getMessage().contains("Invalid Card Number") || error.getTransactionResponse().getTransactionStatus().toString().contains("Invalid CVV") || error.getTransactionResponse().getTransactionStatus().toString().contains("Invalid Card")) {
                     CustomMessage.getInstance().CustomMessage(FailedSummaryPage.this, error.getMessage());
                } else {
//                    ToOrderSummary(false, error.getTransactionResponse().getTransactionId(), null, null, cardOption);
                }
            }
        };

        try {
            Amount amountObj = new Amount(String.valueOf(AMOUNT));
            paymentType = new PaymentType.PGPayment(amountObj, EnvConstants.BILL_URL + TxnRef + "/", cardOption, new CitrusUser(citrusClient.getUserEmailId(), citrusClient.getUserMobileNumber()));
            citrusClient.pgPayment((PaymentType.PGPayment) paymentType, callback);
        } catch (CitrusException e) {
            e.printStackTrace();
        }
    }


    //  To order summary
//    -------------------------------------------------------------------------------

    public void ToOrderSummary(Boolean status, String TxnId, NetbankingOption netbankingOption, PaymentOption paymentOption, CardOption cardOption) {
        if (status) {
            mProgress.dismiss();
            Intent success = new Intent(this, SummaryPage.class);
            success.putExtra("TxnId", TxnId);
            startActivity(success);
            finish();
        } else {
            mProgress.dismiss();
            tryAgain.setEnabled(true);
            Intent failed = new Intent(this, FailedSummaryPage.class);
            failed.putExtra("Payment", selectedPaymentMethod);
            failed.putExtra("Netbank", netbankingOption);
            failed.putExtra("SavedCard", paymentOption);
            failed.putExtra("CardDetail", cardOption);
            failed.putExtra("TxnId", TxnId);
            startActivity(failed);
            finish();
        }
    }


    public static void finishFeed(Context context) {
        Intent intent = new Intent("FeedPage");
        intent.putExtra("action", "close");
        intent.putExtra("activityIndex", "7");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }


//    Confirm function
//    --------------------------------------------------------------

    public void Confirm() {
        //System.out.println("PAYMENT:" + "inside confirm");
        if (selectedPaymentMethod.equals("CARD")) {
            tryAgain.setEnabled(false);
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
            ApiService.getInstance(FailedSummaryPage.this, 1).postData(FailedSummaryPage.this, EnvConstants.APP_BASE_URL + "/payment/confirmorder/", confirmObject, SCREEN_NAME, "confirmPost");

        } else if (selectedPaymentMethod.equals("COD")) {
            tryAgain.setEnabled(false);
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
            ApiService.getInstance(FailedSummaryPage.this, 1).postData(FailedSummaryPage.this, EnvConstants.APP_BASE_URL + "/payment/confirmorder/", confirmObject, SCREEN_NAME, "confirmPost");

        } else if (selectedPaymentMethod.equals("NETBANKING")) {
            tryAgain.setEnabled(false);
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
            ApiService.getInstance(FailedSummaryPage.this, 1).postData(FailedSummaryPage.this, EnvConstants.APP_BASE_URL + "/payment/confirmorder/", confirmObject, SCREEN_NAME, "confirmPost");

        } else if (selectedPaymentMethod.equals("SAVEDCARD")) {
            //System.out.println("PAYMENT:" + "inside confirm");
            tryAgain.setEnabled(false);
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
            ApiService.getInstance(FailedSummaryPage.this, 1).postData(FailedSummaryPage.this, EnvConstants.APP_BASE_URL + "/payment/confirmorder/", confirmObject, SCREEN_NAME, "confirmPost");

        } else {
            tryAgain.setEnabled(false);
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
            ApiService.getInstance(FailedSummaryPage.this, 1).postData(FailedSummaryPage.this, EnvConstants.APP_BASE_URL + "/payment/confirmorder/", confirmObject, SCREEN_NAME, "confirmPost");

        }

    }


//    Server responses
//    ----------------------------------------------------------------

    @Override
    public void onResponseCallback(JSONObject response, String flag) {
        if (flag.equals("confirmPost")) {
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                //System.out.println("PAYMENT:" + resp);
                try {
                    String status = resp.getString("status");


                    if (status.equals("success")) {
                        tryAgain.setEnabled(true);
                        mProgress.dismiss();
                        JSONObject data = resp.getJSONObject("data");
                        String txnStatus = data.getString("message");
                        if (txnStatus.equals("TXSUCCESS")) {
                            String txnId = data.getString("transaction_id");
                            Cod_status = selectedPaymentMethod.equals("COD");
                            Intent summary = new Intent(this, SummaryPage.class);
                            summary.putExtra("Cod_Status", Cod_status);
                            summary.putExtra("TxnId", txnId);
                            startActivity(summary);
                            finish();
                        } else {
                            int amount_pay = data.getInt("amount_pay");
                            String txnRef = data.getString("transaction_ref");

                            if (selectedPaymentMethod.equals("CARD")) {
                                ConfirmPay(txnRef, amount_pay);
                            } else if (selectedPaymentMethod.equals("NETBANKING")) {
                                PaymentProcess(netbankingOption, txnRef, amount_pay);
                            } else if (selectedPaymentMethod.equals("SAVEDCARD")) {
                                proceedToPayment(CitrusUtils.PaymentType.PG_PAYMENT, paymentOption, txnRef, amount_pay);
                                //System.out.println("PAYMENT:" + "CHECKINGGGGGGG");
                            }
//                            else {
//                                Intent failed = new Intent(this, FailedSummaryPage.class);
//                                failed.putExtra("Payment", selectedPaymentMethod);
//                                failed.putExtra("Netbank", String.valueOf(netbankingOption));
//                                failed.putExtra("SavedCard", String.valueOf(paymentOption));
//                                failed.putExtra("ZapCashUsed", zapCashStatus);
//                                failed.putExtra("selectedAddress", selectedAddressId);
//                                failed.putExtra("TxnId", TxnId);
//                                startActivity(failed);
//                                finish();
//                            }

                        }
                    } else {
                        tryAgain.setEnabled(true);
                        mProgress.dismiss();
                         CustomMessage.getInstance().CustomMessage(FailedSummaryPage.this, "Oops. Something went wrong!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    tryAgain.setEnabled(true);
                    mProgress.dismiss();
                     CustomMessage.getInstance().CustomMessage(FailedSummaryPage.this, "Oops. Something went wrong!");
                }
            } else {
                tryAgain.setEnabled(true);
                mProgress.dismiss();
                 CustomMessage.getInstance().CustomMessage(FailedSummaryPage.this, "Oops. Something went wrong!");
            }
        }
    }

    @Override
    public void onErrorCallback(VolleyError error, String flag) {

    }


    public void unbindDrawables(View view) {
//        if (view.getBackground() != null) {
//            view.getBackground().setCallback(null);
//        }
//        if (view instanceof ViewGroup) {
//            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
//
//                unbindDrawables(((ViewGroup) view).getChildAt(i));
//            }
//            ((ViewGroup) view).removeAllViews();
//
//        }

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        finish();
        startActivity(getIntent());
    }


    public void onLowMemory() {
        unbindDrawables(findViewById(R.id.summarylayout));
        ExternalFunctions.deleteCache(FailedSummaryPage.this);
    }


    @Override
    protected void onDestroy() {

        super.onDestroy();
        View view = findViewById(R.id.summarylayout);
        unbindDrawables(findViewById(R.id.summarylayout));
//        if (posts.size() > 0) {
//            posts.clear();
//            adapter.notifyDataSetChanged();
//        }
        Runtime.getRuntime().gc();
        ////////System.out.println(Runtime.getRuntime().freeMemory() + "__freememory");
        ////////System.out.println(Runtime.getRuntime().maxMemory() + "__maxmemory");
        ////////System.out.println(Runtime.getRuntime().totalMemory() + "__totalmemory");
        //System.gc();

    }

    @Override
    public void onBackPressed() {

        Intent feed = new Intent(getApplicationContext(), BuySecondPage.class);
        feed.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        feed.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);//        feed.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        feed.putExtra("activity", "FeedPage");
        startActivity(feed);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!GetSharedValues.GetgcmId(FailedSummaryPage.this).equals("")) {
            ApiService.getInstance(FailedSummaryPage.this, 1).getData(FailedSummaryPage.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/?gcm_reg_id=" + GetSharedValues.GetgcmId(FailedSummaryPage.this), "session");
        } else {
            ApiService.getInstance(FailedSummaryPage.this, 1).getData(FailedSummaryPage.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/", "session");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.set("UserName", GetSharedValues.getUsername(getApplicationContext()));mTracker.set("ZapUsername", GetSharedValues.getZapname(getApplicationContext()));
        mTracker.setScreenName(GetSharedValues.getZapname(getApplicationContext()) + "~" + "Failed summary page");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
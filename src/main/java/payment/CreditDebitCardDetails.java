package payment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.citrus.mobile.CType;
import com.citrus.sdk.Callback;
import com.citrus.sdk.CitrusClient;
import com.citrus.sdk.CitrusUser;
import com.citrus.sdk.TransactionResponse;
import com.citrus.sdk.classes.Amount;
import com.citrus.sdk.classes.CitrusException;

import com.citrus.sdk.classes.Month;
import com.citrus.sdk.classes.Year;
import com.citrus.sdk.payment.CardOption;
import com.citrus.sdk.payment.CreditCardOption;
import com.citrus.sdk.payment.DebitCardOption;
import com.citrus.sdk.payment.NetbankingOption;
import com.citrus.sdk.payment.PaymentOption;
import com.citrus.sdk.payment.PaymentType;
import com.citrus.sdk.response.BindUserResponse;
import com.citrus.sdk.response.CitrusError;
import com.citrus.sdk.response.CitrusResponse;
import com.citrus.widgets.CardNumberEditText;
import com.citrus.widgets.ExpiryDate;
//import com.uxcam.UXCam;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import activity.FailedSummaryPage;
import activity.SummaryPage;
import activity.shoppingcartnew;
import application.MyApplicationClass;
import network.ApiCommunication;
import network.ApiService;
import utils.CommonFinish;
import utils.CustomMessage;
import utils.ExternalFunctions;
import utils.GetSharedValues;

/**
 * Created by haseeb on 19/10/15.
 */
public class CreditDebitCardDetails extends ActionBarActivity implements ApiCommunication {

    public String SCREEN_NAME = "CREDITDEBITPAGE";

    private CardNumberEditText editCardNumber = null;
    private ExpiryDate editExpiryDate = null;
    private EditText editCVV = null, editCardHolderName = null, cardHolderNickName = null, cardHolderNumber = null;
    private CType cType = CType.DEBIT;
    ImageView saveOption;
    PaymentType paymentType;
    CitrusClient client;
    CardOption cardOption;
    Boolean SaveCheck = true;
    ProgressDialog progress;
    String[] MTRO = new String[]{"502260", "504433",
            "504434", "504435", "504437", "504645", "504681",
            "504753", "504775", "504809", "504817", "504834",
            "504848", "504884", "504973", "504993", "508125",
            "508126", "508159", "508192", "508227"};

    String[] MTRO1 = new String[]{"600206",
            "603123", "603741", "603845", "622018"};




    //    New params
    int AMOUNT = 0;
    String ZAPCASH;
    String PHONENUMBER, EMAIL, Temp_phone, OTP;
    Boolean PHONENUMBER_STATUS;
    TextView load, SavedCardText;

    Boolean CouponStatus = false;
    Boolean ZapCashStatus = false;
    NetbankingOption netbankingOption = null;
    PaymentOption paymentOption = null;
    int CouponPrice = 0;
    int AlbumId;
    int selectedAddress = 0;

    RadioGroup rg_card_option;
    RadioButton debit_card_rb;

    String TxnRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_credit_debit);
        //////Thread.currentThread().setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
        load = (TextView) findViewById(R.id.load);
        LocalBroadcastManager.getInstance(this).registerReceiver(CommonFinish.broadcastReceiver, new IntentFilter("CreditDebitCardDetails"));
        ExternalFunctions.cContextArray[15] = CreditDebitCardDetails.this;
        //UXCam.startWithKey("1dfb25141864376");         overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final View customView = getLayoutInflater().inflate(R.layout.card_details_actionbar, null);
        getSupportActionBar().setCustomView(customView);
        Toolbar parent = (Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0, 0);


        progress = new ProgressDialog(CreditDebitCardDetails.this);
        progress.setCancelable(false);


        ImageView Skip = (ImageView) findViewById(R.id.card_back_button);
        Skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

//  Initialising Citrus
//        --------------------------------------------


        client = CitrusClient.getInstance(CreditDebitCardDetails.this);
//  Layout params
//        ---------------------------------------------

        editCardNumber = (CardNumberEditText) findViewById(R.id.cardHolderNumber);
        editExpiryDate = (ExpiryDate) findViewById(R.id.cardExpiry);
        editCardHolderName = (EditText) findViewById(R.id.cardHolderName);
        editCVV = (EditText) findViewById(R.id.cardCvv);
        saveOption = (ImageView) findViewById(R.id.checkboxSaveCard);
        SavedCardText = (TextView) findViewById(R.id.savedCardText);
        saveOption.setTag("Checked");
        SavedCardText.setText("Saved this card securely for a faster checkout next time");

        saveOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (saveOption.getTag().toString().equals("Checked")) {
                    saveOption.setImageResource(android.R.color.transparent);
                    saveOption.setTag("UnChecked");
                    SavedCardText.setText("Save this card securely for a faster checkout next time");
                } else {
                    saveOption.setImageResource(R.drawable.select_savedcard_checked);
                    saveOption.setTag("Checked");
                    SavedCardText.setText("Saved this card securely for a faster checkout next time");
                }
            }
        });


        rg_card_option = (RadioGroup) findViewById(R.id.card_options);
        debit_card_rb = (RadioButton) findViewById(R.id.debit_card_option);
        debit_card_rb.setChecked(true);
        cType = CType.DEBIT;
        rg_card_option.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.credit_card_option){
                    cType = CType.CREDIT;
                }
                else {
                    cType = CType.DEBIT;
                }
            }
        });




// Checking phone status
//        ---------------------------------------------

        SharedPreferences settings = getSharedPreferences("BuySession",
                Context.MODE_PRIVATE);

        PHONENUMBER_STATUS = settings.getBoolean("PHONENUMBER_STATUS", false);
        PHONENUMBER = settings.getString("PHONENUMBER", "");
        EMAIL = settings.getString("EMAIL", "");
        AMOUNT = settings.getInt("AMOUNT", 0);
        ZAPCASH = settings.getString("ZAPCASH", "0");

        AlbumId = getIntent().getIntExtra("AlbumId", 0);
        TxnRef = getIntent().getStringExtra("TxnRef");
        CouponStatus = settings.getBoolean("CouponStatus", false);
        ZapCashStatus = settings.getBoolean("ZapCashStatus", false);
        CouponPrice = settings.getInt("CouponPrice", 0);
        selectedAddress = settings.getInt("selectedAddress", 0);
        selectedAddress = getIntent().getIntExtra("selectedAddress", 0);
        //System.out.println("PAYMENT:"+selectedAddress);
    }


//    Add transaction function
//    -----------------------------------------------------------

    public void AddTransaction(final View v) {
        String cardHolderName = editCardHolderName.getText().toString().trim();
        String cardNumber = editCardNumber.getText().toString().trim();
        String cardCVV = editCVV.getText().toString().trim();
        Month month = editExpiryDate.getMonth();
        Year year = editExpiryDate.getYear();
        if(cardHolderName.length() > 0 && cardNumber.length() > 0 && cardCVV.length() > 0) {
            if (String.valueOf(editCardNumber.getText().charAt(0)).contains("4")) {
                //////System.out.println("inside visa");
                onPay(v);
            } else if (String.valueOf(editCardNumber.getText().charAt(0)).contains("5")) {
                //////System.out.println("inside mastercard main loop");

                if (editCardNumber.getText().toString().substring(0, 1).contains("56")) {
                    //////System.out.println("inside mastercard first check");

                     CustomMessage.getInstance().CustomMessage(CreditDebitCardDetails.this, "Sorry! We accept only Visa and MasterCard as of now.");
                } else if (Arrays.asList(MTRO).contains(editCardNumber.getText().toString().substring(0, 5))) {
                    //////System.out.println("inside master card second check");

                     CustomMessage.getInstance().CustomMessage(CreditDebitCardDetails.this, "Sorry! We accept only Visa and MasterCard as of now.");
                } else {
                    //////System.out.println("inside master card elese");

                    onPay(v);

                }
            } else {
                //////System.out.println("inside master card main elese");

                 CustomMessage.getInstance().CustomMessage(CreditDebitCardDetails.this, "Sorry! We accept only Visa and MasterCard as of now.");
            }
        }
        else {
             CustomMessage.getInstance().CustomMessage(CreditDebitCardDetails.this, "Fill all the fields.");
        }

    }


//    On pay function
//    -------------------------------------------------------------

    public void onPay(View v) {
        load.setEnabled(false);
        String cardHolderName = editCardHolderName.getText().toString();
        String cardNumber = editCardNumber.getText().toString();
        String cardCVV = editCVV.getText().toString();
        Month month = editExpiryDate.getMonth();
        Year year = editExpiryDate.getYear();

        if (cardHolderName.trim().length() == 0) {
            editCardHolderName.setError("Please enter the name");
        } else if (cardNumber.trim().length() == 0) {
            editCardNumber.setError("Please enter the card number");
        } else if (cardCVV.trim().length() == 0) {
            editCVV.setError("Please enter the CVV");
        }


        if (cType == CType.DEBIT) {
            cardOption = new DebitCardOption(cardHolderName, cardNumber, cardCVV, month, year);
        } else {
            cardOption = new CreditCardOption(cardHolderName, cardNumber, cardCVV, month, year);
        }

        if (saveOption.getTag().toString().equals("Checked")) {
            if (PHONENUMBER_STATUS) {
                SaveCheck = true;
                //////System.out.println("inside first if");
                BindUser();

            } else {
                PromptPhoneNumber();
            }
        } else {
            if (PHONENUMBER_STATUS) {
                //////System.out.println("inside first else");
                BindUser();
            } else {
                PromptPhoneNumber();
            }
        }

    }


//    Confirm pay function
//    --------------------------------------------------------------

    private void ConfirmPay() {
        Callback<TransactionResponse> callback = new Callback<TransactionResponse>() {
            @Override
            public void success(TransactionResponse transactionResponse) {
                //System.out.println("payment response check :" + transactionResponse.getTransactionStatus().toString() + "__" + transactionResponse.getTransactionId());
                String TxnId = transactionResponse.getTransactionId();
                if (transactionResponse.getTransactionStatus().toString().contains("SUCCESS")) {

                    if (SaveCheck) {
                        savePaymentOption(cardOption, TxnId);
                    } else {
                        load.setEnabled(true);
                        ToOrderSummary(true, TxnId, cardOption);
                    }
                } else {

                    load.setEnabled(true);
                    ToOrderSummary(false, TxnId, cardOption);
                }

            }

            @Override
            public void error(CitrusError error) {
                //System.out.println("Confirm pay: inside error: " + error.getMessage() + "__" + error.getTransactionResponse() + "___" + error.getStatus());
                progress.dismiss();
                load.setEnabled(true);

                if(error.getMessage().contains("Invalid Card Number") || error.getMessage().contains("Invalid CVV") || error.getTransactionResponse().getTransactionStatus().toString().contains("Invalid CVV") || error.getTransactionResponse().getTransactionStatus().toString().contains("Invalid Card")){
                     CustomMessage.getInstance().CustomMessage(CreditDebitCardDetails.this, error.getMessage());
                }
                else{
                    ToOrderSummary(false, error.getTransactionResponse().getTransactionId(), cardOption);
                }
            }
        };

        try {
            Amount amountObj = new Amount(String.valueOf(AMOUNT));
            paymentType = new PaymentType.PGPayment(amountObj, EnvConstants.BILL_URL + TxnRef + "/", cardOption, new CitrusUser(client.getUserEmailId(), client.getUserMobileNumber()));
            client.pgPayment((PaymentType.PGPayment) paymentType, callback);
        } catch (CitrusException e) {
            e.printStackTrace();
        }
    }


//    Save payment option
//    ----------------------------------------------------------

    private void savePaymentOption(final PaymentOption paymentOption, final String TxnId) {
        //System.out.println("savepayment option");
        client.savePaymentOption(paymentOption, new Callback<CitrusResponse>() {
            @Override
            public void success(CitrusResponse citrusResponse) {
                //System.out.println("savepayment success: " + citrusResponse.getMessage() + "___" + citrusResponse.getRawResponse() + "__" + citrusResponse.getStatus());
                if (citrusResponse.getStatus().toString().contains("SUCCESS")) {
                    load.setEnabled(true);
                    ToOrderSummary(true, TxnId, (CardOption) paymentOption);
                } else {
                    load.setEnabled(true);
                    ToOrderSummary(true, TxnId, (CardOption) paymentOption);
                }
            }

            @Override
            public void error(CitrusError error) {
                load.setEnabled(true);
                //////System.out.println("savepayment error: " + error.getMessage());
                ToOrderSummary(true, TxnId, (CardOption) paymentOption);
            }
        });
    }


//    PromptPhoneNumber
//    --------------------------------------------------------------

    private void PromptPhoneNumber() {
        LayoutInflater layoutInflater = LayoutInflater.from(CreditDebitCardDetails.this);

        View promptView = layoutInflater.inflate(R.layout.input_dialog_phonenumber, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CreditDebitCardDetails.this);
        alertDialogBuilder.setView(promptView);
        final EditText editText = (EditText) promptView.findViewById(R.id.phonenumber);
        editText.setText(PHONENUMBER);
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("VERIFY", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String phonenumber = editText.getText().toString();
                        //////System.out.println(phonenumber);
                        if (phonenumber.length() >= 10) {
                            Temp_phone = phonenumber;
                            postPhoneNumber(phonenumber);

                        } else {
                            load.setEnabled(true);
                             CustomMessage.getInstance().CustomMessage(CreditDebitCardDetails.this, "Invalid phone number!");
                        }
                    }
                })
                .setNegativeButton("CANCEL",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                load.setEnabled(true);
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();


    }


//    Post phone number function
//    --------------------------------------------------------

    private void postPhoneNumber(final String phone) {
        progress.setMessage("Processing request ...Please Wait...");
        progress.show();
        JSONObject phonenumber = new JSONObject();
        try {
            phonenumber.put("phone_number", phone);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ApiService.getInstance(CreditDebitCardDetails.this, 1).postData(CreditDebitCardDetails.this, EnvConstants.APP_BASE_URL + "/user/add_phone/", phonenumber, SCREEN_NAME, "phonePost");
    }



//    Bind user
//    ---------------------------------------------------------------------------

    private void BindUser(){
        progress.setMessage("Processing Payment ...Please Wait...");
        progress.show();
        String emailId = EMAIL;
        String mobileNo = PHONENUMBER;

        client.bindUserByMobile(emailId, mobileNo, new Callback<BindUserResponse>() {
            @Override
            public void success(BindUserResponse bindUserResponse) {
                //System.out.println("CTERROR:busuccess:"+bindUserResponse.getMessage() + "___" + bindUserResponse.getResponseCode());
                ConfirmPay();
            }

            @Override
            public void error(CitrusError citrusError) {
                //System.out.println("CTERROR:buerror:"+citrusError.getMessage());
                progress.dismiss();
                 CustomMessage.getInstance().CustomMessage(CreditDebitCardDetails.this, "Oops. Something went wrong!");

                load.setEnabled(true);
            }
        });
    }


    //  To order summary
//    -------------------------------------------------------------------------------

    public void ToOrderSummary(Boolean status, String TxnId, CardOption cardOption) {
        if (status) {
            progress.dismiss();
            Intent success = new Intent(CreditDebitCardDetails.this, SummaryPage.class);
            success.putExtra("Cod_Status",false);
            success.putExtra("TxnId", TxnId);
            startActivity(success);
            finish();
        } else {
            progress.dismiss();
            Intent failed = new Intent(CreditDebitCardDetails.this, FailedSummaryPage.class);
            failed.putExtra("Payment", "CARD");
            failed.putExtra("Netbank", netbankingOption);
            failed.putExtra("SavedCard", paymentOption);
            failed.putExtra("CardDetail", cardOption);
            failed.putExtra("ZapCashUsed", ZapCashStatus);
            failed.putExtra("selectedAddress", selectedAddress);
            failed.putExtra("TxnId", TxnId);
            startActivity(failed);
            finish();
        }
    }



//    Custom alert dialog display function
//    -------------------------------------------------------------------

    protected void showInputDialog() {

        LayoutInflater layoutInflater = LayoutInflater.from(CreditDebitCardDetails.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CreditDebitCardDetails.this);
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //////System.out.println();
                        String entered_otp = editText.getText().toString();
                        OTP = entered_otp;
                        VerifyOtp(entered_otp);

                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                load.setEnabled(true);
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void VerifyOtp(String entered_otp) {
        progress.setMessage("Verifying otp ...Please Wait...");
        progress.show();
        final JSONObject otpObj = new JSONObject();
        try {
            otpObj.put("otp", entered_otp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //////System.out.println(otpObj + "__resetDetails");
        ApiService.getInstance(CreditDebitCardDetails.this, 1).postData(CreditDebitCardDetails.this, EnvConstants.APP_BASE_URL + "/user/otpverify2/", otpObj, SCREEN_NAME, "verifysignupotp");
    }


//    Server responses
//    ---------------------------------------------------------------------

    @Override
    public void onResponseCallback(JSONObject response, String flag) {

        if (flag.equals("phonePost")) {
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                //////System.out.println(resp);
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        progress.dismiss();
                        showInputDialog();

                    } else {
                        load.setEnabled(true);
                        progress.dismiss();
                         CustomMessage.getInstance().CustomMessage(CreditDebitCardDetails.this, "Oops. Something went wrong!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progress.dismiss();
                    load.setEnabled(true);
                     CustomMessage.getInstance().CustomMessage(CreditDebitCardDetails.this, "Oops. Something went wrong!");
                }
            } else {
                load.setEnabled(true);
                progress.dismiss();
                 CustomMessage.getInstance().CustomMessage(CreditDebitCardDetails.this, "Oops. Something went wrong!");
            }
        }
        else if(flag.equals("verifysignupotp")){
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if(resp != null){
                //////System.out.println(resp);
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        SharedPreferences settings = getApplicationContext().getSharedPreferences("BuySession",
                                Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("PHONENUMBER_STATUS", true);
                        editor.putString("PHONE_NUMBER", Temp_phone);
                        editor.apply();
                        PHONENUMBER = Temp_phone;
                        progress.dismiss();
                        BindUser();

                    } else {
                        progress.dismiss();
                        load.setEnabled(true);
                        String detail = resp.getString("detail");
                         CustomMessage.getInstance().CustomMessage(CreditDebitCardDetails.this, detail);
                    }
                } catch (JSONException e) {
                    progress.dismiss();
                    load.setEnabled(true);
                    e.printStackTrace();
                }
            }
            else {
                progress.dismiss();
                load.setEnabled(true);
                 CustomMessage.getInstance().CustomMessage(CreditDebitCardDetails.this, "Oops. Something went wrong!");
            }
        }
    }

    @Override
    public void onErrorCallback(VolleyError error, String flag) {
        if(progress.isShowing()){
            progress.dismiss();
        }
    }
    @Override
    public void onBackPressed(){
        Intent paymentMode = new Intent(CreditDebitCardDetails.this, shoppingcartnew.class);
        paymentMode.putExtra("AMOUNT",AMOUNT);
        paymentMode.putExtra("AlbumId", AlbumId);
        paymentMode.putExtra("CouponPrice", CouponPrice);
        paymentMode.putExtra("CouponStatus", CouponStatus);
        paymentMode.putExtra("ZapCashStatus",ZapCashStatus);
        paymentMode.putExtra("ZapCashUsed",Integer.parseInt(ZAPCASH));
        startActivity(paymentMode);
        finish();
    }



    public class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            //MyApplicationClass.sFirstRun= false;
            Intent intent = new Intent("FeedPage");
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

//
////    Linkuser extended function
////    -----------------------------------------------------------------------------
//
//    private void LinkUserExtended() {
//        String emailId = EMAIL;
//        String mobileNo = PHONENUMBER;
//        //////System.out.println(mobileNo);
//        //////System.out.println("inside linkuser extended");
//        client.linkUserExtended(emailId, mobileNo, new Callback<LinkUserExtendedResponse>() {
//
//            @Override
//            public void success(LinkUserExtendedResponse linkUserExtendedResponse) {
//                // User Linked!
//                //////System.out.println("linkuser type: "+linkUserExtendedResponse.getLinkUserSignInType());
//                LinkUserNext(linkUserExtendedResponse);
//
//            }
//
//            @Override
//            public void error(CitrusError error) {
//                // Error case
//                String errorMessasge = error.getMessage();
//                //////System.out.println("inside linkuser extended error:"+error.getMessage()+"__"+error.getStatus()+"__"+error.getTransactionResponse() );
//                 CustomMessage.getInstance().CustomMessage(CreditDebitCardDetails.this, error.getMessage());
//            }
//        });
//    }
//
//
//
////    LinkUserNext
////    ----------------------------------------------------------------------------
//
//
//    private void LinkUserNext(LinkUserExtendedResponse linkUserExtendedResponse){
//        LinkUserSignInType linkUserSignInType = linkUserExtendedResponse.getLinkUserSignInType();
//        if (linkUserSignInType != LinkUserSignInType.None) {
//            String linkUserMessage = linkUserExtendedResponse.getLinkUserMessage();
//             CustomMessage.getInstance().CustomMessage(CreditDebitCardDetails.this, linkUserMessage);
//            switch (linkUserSignInType) {
//
//                case SignInTypeMOtpOrPassword:
//                    // Show Mobile otp and password sign in screen
//                    showOTPorPasswordPrompt(linkUserExtendedResponse);
//                    break;
//                case SignInTypeMOtp:
//                    // Show Mobile otp sign in screen
//                    showOTPPrompt(linkUserExtendedResponse);
//                    break;
//                case SignInTypeEOtpOrPassword:
//                    // Show Email otp and password sign in screen
//                    showOTPorPasswordPrompt(linkUserExtendedResponse);
//                    break;
//                case SignInTypeEOtp:
//                    // Show Email otp sign in screen
//                    showOTPPrompt(linkUserExtendedResponse);
//                    break;
//                default:
//                    break;
//            }
//        }
//    }
//
//
//
//    private void linkUserSignIn(LinkUserExtendedResponse linkUserExtendedResponse, LinkUserPasswordType type, String password){
//        client.linkUserExtendedSignIn(linkUserExtendedResponse,type,password, new Callback<CitrusResponse>(){
//            @Override
//            public void success(CitrusResponse citrusResponse) {
//                // User Signed In!
//
//                //////System.out.println("sign in response :"+ citrusResponse.getStatus());
//                ConfirmPay();
//
//            }
//            @Override
//            public void error(CitrusError error) {
//                // Error case
//                String errorMessasge = error.getMessage();
//                //////System.out.println("sign in error response :"+ error.getMessage());
//
//            }
//        });
//    }






////    Alert dialogs
////    -------------------------------------------------------------------------------
//
//
//    private void showOTPPrompt(final LinkUserExtendedResponse linkUserExtendedResponse) {
//        LayoutInflater layoutInflater = LayoutInflater.from(CreditDebitCardDetails.this);
//        View promptView = layoutInflater.inflate(R.layout.input_dialog_otp, null);
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CreditDebitCardDetails.this);
//        alertDialogBuilder.setView(promptView);
//
//        final EditText editText = (EditText) promptView.findViewById(R.id.otp_text);
//        // setup a dialog window
//        alertDialogBuilder.setCancelable(false)
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        String otp = editText.getText().toString();
//                        LinkUserPasswordType linkUserPasswordType = LinkUserPasswordType.Otp;
//                        linkUserSignIn(linkUserExtendedResponse,linkUserPasswordType,otp);
//
//                    }
//                })
//                .setNegativeButton("Cancel",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                dialog.cancel();
//                            }
//                        });
//
//        // create an alert dialog
//        editText.requestFocus();
//        AlertDialog alert = alertDialogBuilder.create();
//        alert.show();
//
//
//    }
//
//
//    private void showOTPorPasswordPrompt(final LinkUserExtendedResponse linkUserExtendedResponse) {
//        LayoutInflater layoutInflater = LayoutInflater.from(CreditDebitCardDetails.this);
//        View promptView = layoutInflater.inflate(R.layout.input_dialog_otp_password, null);
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CreditDebitCardDetails.this);
//        alertDialogBuilder.setView(promptView);
//
//        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
//        final TextView otpselect = (TextView) promptView.findViewById(R.id.otpselect);
//        final TextView passwordselect = (TextView) promptView.findViewById(R.id.passwordselect);
//        otpselect.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                otpselect.setBackgroundColor(Color.GRAY);
//                passwordselect.setBackgroundColor(Color.TRANSPARENT);
//                editText.setHint("Enter OTP");
//            }
//        });
//        passwordselect.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                otpselect.setBackgroundColor(Color.TRANSPARENT);
//                passwordselect.setBackgroundColor(Color.GRAY);
//                editText.setHint("Enter Password");
//            }
//        });
//
//        // setup a dialog window
//        alertDialogBuilder.setCancelable(false)
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        String otp = editText.getText().toString();
//                        if (editText.getHint().toString().equals("Enter OTP")) {
//                            LinkUserPasswordType linkUserPasswordType = LinkUserPasswordType.Otp;
//                            linkUserSignIn(linkUserExtendedResponse,linkUserPasswordType,otp);
//
//                        } else {
//                            LinkUserPasswordType linkUserPasswordType = LinkUserPasswordType.Password;
//                            linkUserSignIn(linkUserExtendedResponse,linkUserPasswordType,otp);
//
//                        }
//
//                    }
//                })
//                .setNegativeButton("Cancel",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                dialog.cancel();
//                            }
//                        });
//
//        // create an alert dialog
//        editText.requestFocus();
//        AlertDialog alert = alertDialogBuilder.create();
//        alert.show();
//
//
//    }



//
//
////    SigninCheck
////    ------------------------------------------------------------
//
//    private void IsUserSignedIn(){
//        client.isUserSignedIn(new com.citrus.sdk.Callback<Boolean>() {
//            @Override
//            public void success(Boolean loggedIn) {
//                if(loggedIn){
//                    ConfirmPay();
//                }
//                else {
//                    linkUser();
//                }
//            }
//
//            @Override
//            public void error(CitrusError error) {
//
//            }
//        });
//    }
//
//
////    Link User
////    -----------------------------------------------------------
//    private void linkUser() {
//        progress.setMessage("Processing Payment ...Please Wait...");
//        progress.show();
//        String emailId = EMAIL;
//        String mobileNo = PHONENUMBER;
//
//        client.isCitrusMember(emailId, mobileNo, new Callback<Boolean>() {
//            @Override
//            public void success(Boolean isMember) {
//
//                //////System.out.println("linkuser response : " + isMember);
//                if (isMember) {
//                    signIn();
//                } else {
//                    signUp();
//                }
//
//            }
//
//            @Override
//            public void error(CitrusError error) {
//                 CustomMessage.getInstance().CustomMessage(CreditDebitCardDetails.this, error.getMessage());
//                progress.dismiss();
//            }
//        });
//    }
//
//
//
////    SignIn
////    -----------------------------------------------------------------
//
//    private void signIn() {
//        String emailId = EMAIL;
//        String password = PHONENUMBER;
//
//        client.signIn(emailId, password, new Callback<CitrusResponse>() {
//            @Override
//            public void success(CitrusResponse citrusResponse) {
//                //////System.out.println("login response: " + citrusResponse);
//                ConfirmPay();
//            }
//
//            @Override
//            public void error(CitrusError error) {
//                //////System.out.println("login response error: " + error.getMessage());
//                progress.dismiss();
//
//            }
//        });
//    }
//
//
////    Signup
////    ----------------------------------------------------------------------
//
//    private void signUp() {
//        String emailId = EMAIL;
//        String mobileNo = PHONENUMBER;
//        String password = PHONENUMBER;
//
//        client.signUp(emailId, mobileNo, password, new Callback<CitrusResponse>() {
//            @Override
//            public void success(CitrusResponse citrusResponse) {
//                //////System.out.println("signup response: " + citrusResponse);
//                signIn();
//
//            }
//
//            @Override
//            public void error(CitrusError error) {
//                //////System.out.println("signup response error: " + error.getMessage());
//                progress.dismiss();
//            }
//        });
//    }


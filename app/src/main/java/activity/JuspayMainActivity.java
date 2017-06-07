package activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import in.juspay.godel.PaymentActivity;
import in.juspay.godel.analytics.GodelTracker;
import in.juspay.godel.ui.JuspayBrowserFragment;
import in.juspay.juspaysafe.BrowserCallback;
import in.juspay.juspaysafe.BrowserParams;
import in.juspay.juspaysafe.JuspaySafeBrowser;
import network.ApiCommunication;
import network.ApiService;
import utils.GetSharedValues;

import static in.juspay.juspaysafe.JuspaySafeBrowser.callBack;

public class JuspayMainActivity extends Activity implements ApiCommunication {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.juspay_main);
        postOrder();

    }

    private void postOrder() {

        JSONObject toserverData = new JSONObject();
        try {
            toserverData.put("address_id", 551);
            toserverData.put("apply_zapcash", 0);
            toserverData.put("amount", 25000);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("server_data" + toserverData.toString());
        ApiService.getInstance(JuspayMainActivity.this, 1).postData(JuspayMainActivity.this, EnvConstants.APP_BASE_URL + "/order/juspay_order", toserverData, "PAYMENT", "postfirstdata");

    }

    @Override
    public void onResponseCallback(JSONObject response, String flag) {
        if (flag.equals("postfirstdata")) {
            System.out.println(response);
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        JSONObject data = resp.getJSONObject("data");
                        BrowserParams browserParams = new BrowserParams();
//                        browserParams.setMerchantId("shopa");
//                        browserParams.setClientId("shopa_android");
//                        browserParams.setOrderId("orda_111222");
//                        browserParams.setTransactionId("txna_111222");
//                        browserParams.setAmount("12019.50");
//
//                        browserParams.setCustomerId("9496024288");
//                        browserParams.setCustomerEmail("rajeesh@zapyle.com");
//                        browserParams.setCustomerPhoneNumber("9496024288");
//
//                        browserParams.setRemarks("Zapyle shopping");
//                        // browserParams.setPostData();
                          browserParams.setUrl(data.getString("mobile_url"));
//                        Map<String, String> customParameters = new HashMap<String, String>();
//                        customParameters.put("udf_category", "ZAPYLE");
//                        customParameters.put("udf_city", "BANGALORE");
//                        customParameters.put("udf_cardbrand", "VISA");
//                        browserParams.setCustomParameters(customParameters);
//                        Map<String, String> customHeaders = new HashMap<String, String>();
//                        customHeaders.put("Accept-Encoding", "gzip, deflate, sdch");
//                        customHeaders.put("Accept-Language", "en-US,en;q=0.8");
//                        browserParams.setCustomHeaders(customHeaders);
                        JuspayBrowserFragment.openJuspayConnection(this);
                        JuspaySafeBrowser.start(this, browserParams, callBack);


                    }
                } catch (Exception e) {

                }


            }
        }
    }

    @Override
    public void onErrorCallback(VolleyError error, String flag) {

    }

    public BrowserCallback callBack = new BrowserCallback() {

        @Override
        public void ontransactionAborted() {
            // ...

            Toast.makeText(getApplicationContext(),  "Transaction cancelled.", Toast.LENGTH_LONG).show();
        }

        @Override
        public void endUrlReached(String url) {
          //  GodelTracker.getInstance().trackPaymentStatus(transactionId, GodelTracker.PaymentStatus.SUCCESS);
            Toast.makeText(getApplicationContext(), "Transaction successful!", Toast.LENGTH_LONG).show();
        }

    };





}


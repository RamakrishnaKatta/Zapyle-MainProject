
package com.zapyle.zapyle;

import com.citrus.sdk.Environment;

public class EnvConstants {

//
    public static final String APP_BASE_URL = "https://prod.zapyle.com";
    public static final String APP_MEDIA_URL = "https://prod.zapyle.com";
//

//    public static final String APP_BASE_URL = "http://dev.zapyle.com";
//    public static final String APP_MEDIA_URL = "http://dev.zapyle.com";
//    public static final String APP_BASE_URL = "http://prod.zapyle.com";
//    public static final String APP_MEDIA_URL = "http://prod.zapyle.com";

//    public static final String APP_BASE_URL = "http://192.168.151.52:9000";
//    public static final String APP_MEDIA_URL = "http://192.168.151.52:9000";
    public static final String URL_FEED = APP_BASE_URL + "/catalogue";
    public static final String URL_SELLERS = APP_BASE_URL + "/catalogue/seller_closet/";
    public static final String URL_BUY = APP_BASE_URL + "/filters/getProducts";
    public static final String URL_HOME = APP_BASE_URL + "/discover/";


//    Test credentials
//    ---------------------------------------------------------
//    public static final String APP_BASE_URL = "http://zapmob1.cloudapp.net";
//    public static final String APP_BASE_URL = "http://192.168.46.110:9000";
//    public static final String APP_BASE_URL = "http://test.zapyle.com:8080";
//    public static final String APP_MEDIA_URL = "http://test.zapyle.com:8080";
//    //    public static final String APP_MEDIA_URL = "http://192.168.46.110:9000";
//    public static final String DummyURL_FEED = APP_BASE_URL + "/dummyfeed";
//    public static final String URL_FEED = APP_BASE_URL + "/catalogue";

//    public static final String SIGNUP_ID = "za61q0w33q-signup";
//    public static final String SIGNUP_SECRET = "bb77a0bc677bc69233ccbd3f9610417a";
//    public static final String SIGNIN_ID = "za61q0w33q-signin";
//    public static final String SIGNIN_SECRET = "fa772fe4dca0e25f0161d98e240c5759";
//    public static final String VANITY = "za61q0w33q";

//  Payment gateway parameters

    public static final String BILL_URL = APP_BASE_URL + "/payment/confirmorder/";
//
    public static final String SIGNUP_ID = "cqoos6l2u2-signup";
    public static final String SIGNUP_SECRET = "2268ae0137ab1fadb1f83235cf23847a";
    public static final String SIGNIN_ID = "cqoos6l2u2-signin";
    public static final String SIGNIN_SECRET = "afee8d44e739705c7d7c347d0c3d89d4";
    public static final String VANITY = "cqoos6l2u2";


    public static final String PartnerID = "cqoos6l2u2";
    public static final String Password = "R05H4Q7GB1XX3SWQ98IF";
    public static final String MerchantID = "cqoos6l2u2";
    public static final String CampaignCode = "ZAPCASH";

    public static final String FetchZapCash = "https://coupons.citruspay.com/cms_api.asmx/FetchCampaignBalance";
    public static final String RedeemZapCash = "https://coupons.citruspay.com/cms_api.asmx/RedeemCampaign";


    public static final Environment environment = Environment.PRODUCTION;
    public static final boolean enableLogging = true;


}

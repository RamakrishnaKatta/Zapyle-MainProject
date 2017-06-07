package activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.freshdesk.hotline.Hotline;
import com.freshdesk.hotline.HotlineCallbackStatus;
import com.freshdesk.hotline.UnreadCountCallback;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.makeramen.roundedimageview.RoundedImageView;
//import com.uxcam.UXCam;
import com.zapyle.zapyle.Alerts;
import com.zapyle.zapyle.CheckConnectivity;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import DataBase.DatabaseDB;
import application.MyApplicationClass;
import network.ApiCommunication;
import network.ApiService;
import utils.CustomMessage;
import utils.ExternalFunctions;
import utils.GetSharedValues;


public class ShopCart extends AppCompatActivity implements ApiCommunication {
    String SCREEN_NAME = "SHOPCART";
    ArrayList<Integer> product_id = new ArrayList<Integer>();
    ArrayList<Integer> cart_id = new ArrayList<Integer>();
    ArrayList<Integer> pavilable = new ArrayList<Integer>();
    ArrayList<String> product_pic = new ArrayList<String>();
    ArrayList<String> pname = new ArrayList<String>();
    ArrayList<String> pstyle = new ArrayList<String>();
    ArrayList<String> tsize = new ArrayList<String>();
    ArrayList<String> tqty = new ArrayList<String>();
    ArrayList<String> product_listing_price = new ArrayList<String>();
    ArrayList<String> product_original_price = new ArrayList<String>();
    ArrayList<String> product_size_id = new ArrayList<String>();
    int cartCount = 0;

    private static final String PAGE = "albumId";
    private static final String BODY = "json";

    RelativeLayout relativeLayout7;


    private int totalprice, shipcharge, totwithship;
    ListView lv;
    int AlbumId = 0;
    TextView tvcnt, tvtotal, tvnext, tvcount, emptyCart;
    ProgressBar shop_progressBar;
    ImageView imgback;

    RelativeLayout rlmain;
    public static CustomAdapter adapt;
    public static ProgressDialog pd1;
    String callingActivity = "activity.HomePage";
    DatabaseDB db;
    Tracker mTracker;
    ImageView imgchat;
    TextView chatcount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_cart);
        MyApplicationClass application = (MyApplicationClass) getApplication();
        mTracker = application.getDefaultTracker();
        //////Thread.currentThread().setDefaultUncaughtExceptionHandler(new SplashScreen.MyUncaughtExceptionHandler());
        //UXCam.startWithKey("1dfb25141864376");
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        try {
            callingActivity = "activity." + getIntent().getStringExtra("activity");
        } catch (Exception e) {
            callingActivity = "activity.HomePage";
        }

        try {
            AlbumId = getIntent().getIntExtra("AlbumId", 0);
        } catch (Exception e) {

        }

        //        Database function
//        ----------------------------------------------------

        db = new DatabaseDB(getApplicationContext());
        db.openDB();

        lv = (ListView) findViewById(R.id.lv);
        tvcnt = (TextView) findViewById(R.id.tvcnt);
        tvcount = (TextView) findViewById(R.id.tvcount);
        tvtotal = (TextView) findViewById(R.id.tvtotal);
        tvnext = (TextView) findViewById(R.id.next);
        emptyCart = (TextView) findViewById(R.id.emptyCart);
        rlmain=(RelativeLayout)findViewById(R.id.main_layout);
        imgchat=(ImageView)findViewById(R.id.imgiconnew);
        chatcount=(TextView) findViewById(R.id.chatcount);

        tvcount.setVisibility(View.INVISIBLE);
        chatcount.setVisibility(View.GONE);
        SpannableString spanString = new SpannableString("Hey! your tote in empty. Discover");
        spanString.setSpan(new UnderlineSpan(), 25, spanString.length(), 0);
        emptyCart.setText(spanString);
        emptyCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlelayoutOutofMemmory();
                Intent feed = new Intent(ShopCart.this, HomePageNew.class);
                startActivity(feed);
                finish();
            }
        });
        emptyCart.setVisibility(View.GONE);
        relativeLayout7 = (RelativeLayout) findViewById(R.id.relativeLayout7);
        relativeLayout7.setVisibility(View.GONE);
        lv.setVisibility(View.GONE);
        shop_progressBar = (ProgressBar) findViewById(R.id.shop_progressBar);
        shop_progressBar.setVisibility(View.VISIBLE);

        //System.out.println("shopcartxadsds");
        if (GetSharedValues.LoginStatus(this)) {
            GetEMailAndPhone();
        } else {
            if (db.getTableRecordsCount("CART") > 0) {
                tvnext.setText("PROCEED TO CHECKOUT");
                String query = "SELECT * FROM CART";
                Cursor cursordata = db.getData(query);
                JSONArray ObjectList = new JSONArray();
                System.out.println("inside not loggedin:" + cursordata.getString(1));
                if (db.getTableRecordsCount("CART") == 1) {
                    System.out.println("CART: inside 1");
                    String data = cursordata.getString(1).replace("z*p*", "'");
                    JSONObject dataObj = null;
                    try {
                        dataObj = new JSONObject(data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ObjectList.put(dataObj);
                } else {
                    System.out.println("CART: inside gretaet");

                    while (!cursordata.isAfterLast()) {
                        //System.out.println("DATABASE: cursor data2:" + cursordata.getString(1));
                        String data1 = cursordata.getString(1).replace("z*p*", "'");
                        JSONObject dataObj1 = null;
                        try {
                            dataObj1 = new JSONObject(data1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ObjectList.put(dataObj1);
                        cursordata.moveToNext();
                    }

                }

                DisplayOfflineItems(ObjectList);

            } else {
                shop_progressBar.setVisibility(View.GONE);
                relativeLayout7.setVisibility(View.GONE);
                lv.setVisibility(View.GONE);
                emptyCart.setVisibility(View.VISIBLE);
                tvnext.setText("CONTINUE SHOPPING");
            }

        }
        imgchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Hotline.showConversations(getApplicationContext());
            }
        });

        tvnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean bl = false;
                for (int i = 0; i < pavilable.size(); i++) {
                    if (pavilable.get(i) == 0) {
                        bl = true;
                        showsoldDialog();
                    }
                }

                if (!bl) {
                    if (GetSharedValues.LoginStatus(ShopCart.this)) {
                        if (tvnext.getText().toString().contains("PROCEED TO CHECKOUT")) {
                            if (cartCount > 0) {
                                Intent intent = new Intent(ShopCart.this, shoppingcartnew.class);
                                intent.putExtra("AlbumId", AlbumId);
                                startActivity(intent);
                                finish();
                            } else {
                               CustomMessage.getInstance().CustomMessage(ShopCart.this, "Your Cart is empty.");
                            }
                        } else {
                            Intent home = new Intent(ShopCart.this, HomePageNew.class);
                            startActivity(home);
                        }
                    } else {
                        if (tvnext.getText().toString().contains("PROCEED TO CHECKOUT")) {
                            Alerts.loginAlert(ShopCart.this);
                        } else {
                            Intent home = new Intent(ShopCart.this, HomePageNew.class);
                            startActivity(home);
                        }
                    }
                }
            }
        });

        imgback = (ImageView) findViewById(R.id.imgback);
        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


//    onbackpresses
//    ----------------------------------------------------------------


    @Override
    public void onBackPressed() {

        ExternalFunctions.strOverlayurl = "";
        if (callingActivity.contains("HomePageNew")) {
            Intent fwd = new Intent(this, HomePageNew.class);
            fwd.putExtra("activity", "SplashScreen");
            startActivity(fwd);
            finish();
        } else if (callingActivity.contains("FeedPage")) {
            Intent fwd = new Intent(this, BuySecondPage.class);
            fwd.putExtra("activity", "SplashScreen");
            startActivity(fwd);
            finish();
        }
        else if (callingActivity.contains("BuyPage")){
            Intent fwd = new Intent(this, BuyPage.class);
            fwd.putExtra("activity", "SplashScreen");
            startActivity(fwd);
            finish();
        }
        else if (AlbumId != 0) {
            Intent dintent = new Intent(this, ProductPage.class);
            dintent.putExtra("album_id", AlbumId);
            dintent.putExtra("pta", false);
            dintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            dintent.putExtra("activity", "FeedPage");
            startActivity(dintent);
            finish();
        } else {
            Intent fwd = new Intent(this, HomePageNew.class);
            fwd.putExtra("activity", "SplashScreen");
            startActivity(fwd);
            finish();
        }

    }

    public void showsoldDialog() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.check_soldout, null);
        dialogBuilder.setView(dialogView);
        TextView tvsoldcount = (TextView) dialogView.findViewById(R.id.tvsoldcount);
        RelativeLayout rlremove = (RelativeLayout) dialogView.findViewById(R.id.rlremove);
        ImageView imgclose = (ImageView) dialogView.findViewById(R.id.imgclose);
        final AlertDialog b = dialogBuilder.create();
        imgclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                b.dismiss();
            }
        });


        rlremove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ApiService.getInstance(ShopCart.this, 1).deleteData(ShopCart.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/zapcart/", "cart");
//                            for (int i = 0; i < pavilable.size(); i++) {
//                                if (pavilable.get(i) == 0) {
//                                    pavilable.remove(i);
//                                    product_id.remove(i);
//                                    product_pic.remove(i);
//                                    pname.remove(i);
//                                    pstyle.remove(i);
//                                    tsize.remove(i);
//                                    tqty.remove(i);
//                                    product_listing_price.remove(i);
//                                }
//                                adapt = new CustomAdapter(pname);
//                                lv.setAdapter(adapt);
//
//                                adapt.notifyDataSetChanged();
                b.dismiss();
                //  }
            }
        });


        b.show();
    }


    private void showRemoveDialog(final int position, final CustomAdapter adapter, final int size) {
        System.out.println("CARTTT : "+product_id.get(position).toString()+"-"+ product_size_id.get(position));
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.check_remove, null);
        dialogBuilder.setView(dialogView);
        TextView cancel = (TextView) dialogView.findViewById(R.id.cancel);
        TextView remove = (TextView) dialogView.findViewById(R.id.remove);

        // setup a dialog window
        dialogBuilder.setCancelable(true);
        // create an alert dialog
        final AlertDialog alert = dialogBuilder.create();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               alert.dismiss();
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
                if (GetSharedValues.LoginStatus(ShopCart.this)) {
                    relativeLayout7.setVisibility(View.GONE);
                    lv.setVisibility(View.GONE);
                    shop_progressBar.setVisibility(View.VISIBLE);
                    ApiService.getInstance(ShopCart.this, 1).deleteData(ShopCart.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/zapcart/?item_id=" + cart_id.get(position), "cart");
                    if (size > 1) {
                        String deleteQuery = "delete from CART where albumId = '" + product_id.get(position).toString()+"-"+ product_size_id.get(position) +"'";
                        db.processData(deleteQuery);
                    } else {
                        String deleteQuery = "delete from CART";
                        db.processData(deleteQuery);
                    }
                } else {
                    if (size > 1) {
                        String deleteQuery = "delete from CART where albumId = '" + product_id.get(position).toString()+"-"+ product_size_id.get(position) +"'";
                        db.processData(deleteQuery);
                    } else {
                        String deleteQuery = "delete from CART";
                        db.processData(deleteQuery);
                    }
                    String query = "SELECT * FROM CART";
                    Cursor cursordata = db.getData(query);
                    JSONArray ObjectList = new JSONArray();
                    if (db.getTableRecordsCount("CART") == 1) {
                        //System.out.println("CART: inside 1");
                        String data = cursordata.getString(1).replace("z*p*", "'");
                        JSONObject dataObj = null;
                        try {
                            dataObj = new JSONObject(data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ObjectList.put(dataObj);
                    } else if (db.getTableRecordsCount("CART") > 1) {
                        //System.out.println("CART: inside gretaet");

                        while (!cursordata.isAfterLast()) {
                            //System.out.println("DATABASE: cursor data2:" + cursordata.getString(1));
                            String data1 = cursordata.getString(1).replace("z*p*", "'");
                            JSONObject dataObj1 = null;
                            try {
                                dataObj1 = new JSONObject(data1);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ObjectList.put(dataObj1);
                            cursordata.moveToNext();

                        }


                    }

                    DisplayOfflineItems(ObjectList);
                    adapter.notifyDataSetChanged();
                }
            }
        });


        alert.show();

    }



    public void Getdata() {
        ApiService.getInstance(ShopCart.this, 1).getData(ShopCart.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/zapcart/", "cart");
    }


    //    get function
//    -------------------------------------------------------------------

    public void GetEMailAndPhone() {
        ApiService.getInstance(ShopCart.this, 1).getData(ShopCart.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/get_email_and_num/", "getemail");

    }

    @Override
    public void onResponseCallback(JSONObject response, String flag) {

        //System.out.println("shopcart" + response);

        if (flag.equals("cart")) {
            //System.out.println("cart:" + response);
            try {
                JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
                if (resp != null) {
                    String status = resp.getString("status");
                    if (!status.equals("error")) {
                        JSONObject data = resp.getJSONObject("data");
                        DisplayItems(data);

                    } else {
                        shop_progressBar.setVisibility(View.GONE);
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
                shop_progressBar.setVisibility(View.GONE);
            }
        } else if (flag.equals("getemail")) {
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                //////System.out.println("Get Email Response:"+resp);
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        JSONObject data = resp.getJSONObject("data");
                        JSONObject detail = data.getJSONObject("user_detail");
                        String PHONE_NUMBER = detail.getString("phone_number");
                        String email = detail.getString("email");
                        Boolean Verified = detail.getBoolean("phone_number_verified");

                        if (Verified) {
                            if (PHONE_NUMBER.length() >= 10) {

                                SharedPreferences settings = getApplicationContext().getSharedPreferences("BuySession",
                                        Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString("PHONENUMBER", PHONE_NUMBER);
                                editor.putString("EMAIL", email);
                                editor.putBoolean("PHONENUMBER_STATUS", true);
                                editor.apply();
                            } else {
                                SharedPreferences settings = getApplicationContext().getSharedPreferences("BuySession",
                                        Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString("PHONENUMBER", "0");
                                editor.putString("EMAIL", "0");
                                editor.putBoolean("PHONENUMBER_STATUS", false);
                                editor.apply();
                            }
                        } else {
                            SharedPreferences settings = getApplicationContext().getSharedPreferences("BuySession",
                                    Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("PHONENUMBER", PHONE_NUMBER);
                            editor.putString("EMAIL", email);
                            editor.putBoolean("PHONENUMBER_STATUS", false);
                            editor.apply();
                        }
                        Getdata();
                    } else {
                        SharedPreferences settings = getApplicationContext().getSharedPreferences("BuySession",
                                Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("PHONENUMBER", "0");
                        editor.putString("EMAIL", "0");
                        editor.putBoolean("PHONENUMBER_STATUS", false);
                        editor.apply();
                        Getdata();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Getdata();
                }
            } else {
                SharedPreferences settings = getApplicationContext().getSharedPreferences("BuySession",
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("PHONENUMBER", "0");
                editor.putString("EMAIL", "0");
                editor.putBoolean("PHONENUMBER_STATUS", false);
                editor.apply();
                Getdata();
            }
        }

    }


    @Override
    public void onErrorCallback(VolleyError error, String flag) {

    }


//    Display data
//    --------------------------------------------------------------

    private void DisplayItems(JSONObject data) {
        final JSONArray product;

        try {

            product = data.getJSONArray("items");

            SharedPreferences CartSession = getSharedPreferences("CartSession",
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = CartSession.edit();
            editor.putInt("cartCount", product.length());
            editor.apply();
            //System.out.println("shopcart" + product);
            if (product.length() > 0) {
                tvcount.setVisibility(View.VISIBLE);
                tvcount.setText(String.valueOf(product.length()));
                ExternalFunctions.cartcount = product.length();
            } else {
                tvcount.setVisibility(View.INVISIBLE);
                ExternalFunctions.cartcount = 0;
            }
            cartCount = product.length();
            if (product.length() > 0) {
                tvnext.setText("PROCEED TO CHECKOUT");
                shop_progressBar.setVisibility(View.GONE);
                lv.setVisibility(View.VISIBLE);
                relativeLayout7.setVisibility(View.VISIBLE);
                emptyCart.setVisibility(View.GONE);
                shipcharge = data.getInt("total_shipping_charge");
                totwithship = data.getInt("total_price_with_shipping_charge");
                totalprice = data.getInt("total_original_price");
                //System.out.println("zxzxzxzxz" + totwithship);
                product_id = new ArrayList<Integer>();
                cart_id = new ArrayList<Integer>();
                pavilable = new ArrayList<Integer>();
                product_pic = new ArrayList<String>();
                pname = new ArrayList<String>();
                pstyle = new ArrayList<String>();
                tsize = new ArrayList<String>();
                tqty = new ArrayList<String>();
                product_listing_price = new ArrayList<String>();
                product_original_price = new ArrayList<String>();
                String deleteQuery = "delete from " + "CART";
                db.processData(deleteQuery);

                for (int i = 0; i < product.length(); i++) {
                    JSONObject cart_obj = product.getJSONObject(i);
                    product_pic.add(cart_obj.getString("product_image"));
                    pname.add(cart_obj.getString("title"));
                    pstyle.add(cart_obj.getString("product_brand"));
                    tsize.add(cart_obj.getString("product_size"));
                    tqty.add(cart_obj.getString("product_quantity"));
                    product_listing_price.add(cart_obj.getString("listing_price"));
                    if (!cart_obj.isNull("original_price")) {
                        product_original_price.add(cart_obj.getString("original_price"));
                    }
                    else {
                        product_original_price.add(null);
                    }
                    product_id.add(cart_obj.getInt("product_id"));
                    product_size_id.add(String.valueOf(cart_obj.getInt("product_size_id")));
                    cart_id.add(cart_obj.getInt("id"));
                    pavilable.add(cart_obj.getInt("quantity_available"));
                    String primaryKey = String.valueOf(cart_obj.getInt("product_id")) + "-" + String.valueOf(cart_obj.getInt("product_size_id"));
                    String dataToDb = cart_obj.toString().replace("'", "z*p*");
                    String query = "insert into " + "CART" + " (" + PAGE + ", " + BODY + ") values('" + primaryKey + "', '" + dataToDb + "');";
                    db.processData(query);
//quantity_available//
                    DecimalFormat formatter = new DecimalFormat("#,###,###");
                    String number_string = formatter.format(totwithship);
                    // //System.out.println(number_string);
                    tvtotal.setText(getResources().getString(R.string.Rs) + String.valueOf(number_string));
                    if (pname.size() > 1)
                        tvcnt.setText(pname.size() + " ITEMS");
                    else
                        tvcnt.setText(pname.size() + " ITEM");
                    adapt = new CustomAdapter(pname);
                    lv.setAdapter(adapt);

                    adapt.notifyDataSetChanged();
                }

                //System.out.println("DBCOUNT:__" + db.getTableRecordsCount("CART") + "___" + product.length());


            } else {
                tvnext.setText("CONTINUE SHOPPING");
                shop_progressBar.setVisibility(View.GONE);
                relativeLayout7.setVisibility(View.GONE);
                lv.setVisibility(View.GONE);
                emptyCart.setVisibility(View.VISIBLE);
                ExternalFunctions.cartcount = 0;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void DisplayOfflineItems(JSONArray product) {
        shipcharge = 0;
        totalprice = 0;
        totwithship = 0;
        try {
            if (product.length() > 0) {
                tvcount.setVisibility(View.VISIBLE);
                tvcount.setText(String.valueOf(product.length()));
                ExternalFunctions.cartcount = product.length();
            } else {
                tvcount.setVisibility(View.INVISIBLE);
                ExternalFunctions.cartcount = 0;
            }
            shop_progressBar.setVisibility(View.GONE);
            if (product.length() > 0) {
                tvnext.setText("PROCEED TO CHECKOUT");

                lv.setVisibility(View.VISIBLE);
                relativeLayout7.setVisibility(View.VISIBLE);
                product_id = new ArrayList<Integer>();
                cart_id = new ArrayList<Integer>();
                pavilable = new ArrayList<Integer>();
                product_pic = new ArrayList<String>();
                pname = new ArrayList<String>();
                pstyle = new ArrayList<String>();
                tsize = new ArrayList<String>();
                tqty = new ArrayList<String>();
                product_listing_price = new ArrayList<String>();
                product_original_price = new ArrayList<String>();

                for (int i = 0; i < product.length(); i++) {
                    JSONObject cart_obj = product.getJSONObject(i);
                    //System.out.println("PRODUCT OBJECT:" + product.getJSONObject(i));
                    shipcharge = shipcharge + cart_obj.getInt("shipping_charge");
                    totalprice = totalprice + Integer.parseInt(cart_obj.getString("total_price"));
                    totwithship = totwithship + (Integer.parseInt(cart_obj.getString("total_price")) + cart_obj.getInt("shipping_charge"));
                    product_pic.add(cart_obj.getString("product_image"));
                    pname.add(cart_obj.getString("title"));
                    pstyle.add(cart_obj.getString("product_brand"));
                    tsize.add(cart_obj.getString("product_size"));
                    tqty.add(cart_obj.getString("product_quantity"));
                    int lprice = Integer.parseInt(cart_obj.getString("listing_price")) * Integer.parseInt(cart_obj.getString("product_quantity"));
                    int oprice = Integer.parseInt(cart_obj.getString("original_price")) * Integer.parseInt(cart_obj.getString("product_quantity"));

                    product_listing_price.add(String.valueOf(lprice));
                    product_original_price.add(String.valueOf(oprice));
                    product_id.add(cart_obj.getInt("product_id"));
                    product_size_id.add(String.valueOf(cart_obj.getInt("product_size_id")));
//                cart_id.add(cart_obj.getInt("id"));
                    pavilable.add(cart_obj.getInt("quantity_available"));
                    //System.out.println("zxzxzxzxz" + totwithship);
                    DecimalFormat formatter = new DecimalFormat("#,###,###");
                    String number_string = formatter.format(totwithship);
                    // //System.out.println(number_string);
                    tvtotal.setText(getResources().getString(R.string.Rs) + String.valueOf(number_string));
                    tvcnt.setText(pname.size() + " ITEMS");
                    adapt = new CustomAdapter(pname);
                    lv.setAdapter(adapt);

                    adapt.notifyDataSetChanged();

//quantity_available
                }
            } else {
                shop_progressBar.setVisibility(View.GONE);
                relativeLayout7.setVisibility(View.GONE);
                lv.setVisibility(View.GONE);
                emptyCart.setVisibility(View.VISIBLE);
                tvnext.setText("CONTINUE SHOPPING");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

//    Custom adaptor
//    ------------------------------------------------------------


    public class CustomAdapter extends BaseAdapter {
        ArrayList<String> result;
        Context context;

        private LayoutInflater inflater = null;

        public CustomAdapter(ArrayList<String> prgmNameList) {

//            //System.out.println("hsd" + prgmNameList.get(0));
            // TODO Auto-generated constructor stub
            result = prgmNameList;
            //System.out.println("hsd11" + result.size());
            context = ShopCart.this;

            inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return result.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public class Holder {
            ImageView imgproduct;
            TextView tvpname, tvdescription, tvsize, tvqty, tvofferprice, tvsold, tvremove, tvprice;
            LinearLayout lp;


        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            //System.out.println("hsd11");
            final Holder holder = new Holder();
            View rowView;

            rowView = inflater.inflate(R.layout.shopingcart_list, null);
            holder.imgproduct = (ImageView) rowView.findViewById(R.id.imgproduct);
            holder.tvpname = (TextView) rowView.findViewById(R.id.tvpname);

            holder.tvdescription = (TextView) rowView.findViewById(R.id.tvpdescription);
            holder.tvsize = (TextView) rowView.findViewById(R.id.tvsize);
            holder.tvqty = (TextView) rowView.findViewById(R.id.tvqty);
            holder.tvprice = (TextView) rowView.findViewById(R.id.tvamount);
            holder.tvofferprice = (TextView) rowView.findViewById(R.id.tvofferprice);
            holder.tvsold = (TextView) rowView.findViewById(R.id.tvsold);
            holder.tvremove = (TextView) rowView.findViewById(R.id.tvremove);
            holder.lp = (LinearLayout) rowView.findViewById(R.id.lp);
            holder.tvpname.setText(pname.get(position));
            holder.tvdescription.setText(pstyle.get(position));


            NumberFormat myFormat = NumberFormat.getInstance();
            myFormat.setGroupingUsed(true);




            holder.tvprice.setText(getResources().getString(R.string.Rs) + product_listing_price.get(position));
            if (Double.parseDouble(product_original_price.get(position).replace(",","")) > Double.parseDouble(product_listing_price.get(position).replace(",",""))) {
                holder.tvofferprice.setVisibility(View.VISIBLE);
                holder.tvofferprice.setText(getResources().getString(R.string.Rs) + product_original_price.get(position));
                holder.tvofferprice.setPaintFlags(holder.tvprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
            else {
                holder.tvofferprice.setVisibility(View.GONE);
            }
            String size = "";
            if (tsize.get(position).contains("_")) {
                size = tsize.get(position).split("_")[0] + "-" + tsize.get(position).split("_")[1];
            } else {
                size = tsize.get(position);
            }
            holder.tvsize.setText(size);
            holder.tvqty.setText(tqty.get(position));
            if (pavilable.get(position) == 0) {
                holder.tvsold.setVisibility(View.VISIBLE);
            } else {
                holder.tvsold.setVisibility(View.GONE);
            }

            holder.tvremove.setPaintFlags(holder.tvremove.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
//            if (result.size() > 1) {
//                holder.tvremove.setEnabled(true);
//            } else {
//                holder.tvremove.setEnabled(false);
//            }


            Glide.with(ShopCart.this)
                    .load(EnvConstants.APP_MEDIA_URL + product_pic.get(position))
                    .fitCenter()
                    .placeholder(R.drawable.playholderscreen)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(holder.imgproduct);

            if (position % 2 == 0) {
                holder.lp.setBackgroundColor(Color.rgb(250, 250, 250));
            } else {
                holder.lp.setBackgroundColor(Color.rgb(255, 240, 240));
            }


            holder.imgproduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent productPage = new Intent(context, ProductPage.class);
                    productPage.putExtra("album_id", product_id.get(position));
                    productPage.putExtra("activity", "ShopCart");
                    productPage.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    Runtime.getRuntime().gc();
                    context.startActivity(productPage);
                }
            });


            holder.tvremove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showRemoveDialog(position, adapt , result.size());

                }
            });


            return rowView;
        }



    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handlelayoutOutofMemmory();
        Runtime.getRuntime().gc();

        //System.gc();
        if (product_id.size() > 0) {
            product_id.clear();
            pavilable.clear();
            product_pic.clear();
            pname.clear();
            pstyle.clear();
            tsize.clear();
            tqty.clear();
            product_listing_price.clear();
            product_original_price.clear();
        }
        int count1 = lv.getChildCount();
        for (int i = 0; i < count1; i++) {
            ImageView rv = (ImageView) lv.getChildAt(i).findViewById(R.id.imgproduct);
            if (rv != null) {
                if (rv.getDrawable() != null)
                    rv.getDrawable().setCallback(null);
            }
        }

        Runtime runtime = Runtime.getRuntime();
    }

    public void handlelayoutOutofMemmory() {
        ExternalFunctions.nullViewDrawablesRecursive(rlmain);
        rlmain = null;
        System.gc();


    }

    @Override
    protected void onResume() {
        super.onResume();
        Hotline.getInstance(getApplicationContext()).getUnreadCountAsync(new UnreadCountCallback() {
            @Override
            public void onResult(HotlineCallbackStatus hotlineCallbackStatus, int unreadCount) {
                //Assuming "badgeTextView" is a text view to show the count on

                System.out.println("Hotline count:"+unreadCount);
                if(unreadCount>0){
                    chatcount.setText(String.valueOf(unreadCount));
                    chatcount.setVisibility(View.VISIBLE);
                }else{
                    chatcount.setVisibility(View.GONE);
                }



            }
        });
        mTracker.set("UserName", GetSharedValues.getUsername(getApplicationContext()));mTracker.set("ZapUsername", GetSharedValues.getZapname(getApplicationContext()));   mTracker.setScreenName("Cart page");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}

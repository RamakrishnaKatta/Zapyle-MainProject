package services;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.VolleyError;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import DataBase.DatabaseDB;
import models.ClosetData;
import models.HomeFeedItem;
import models.ProfileData;
import models.SingleItemData;
import network.ApiCommunication;
import network.ApiService;
import network.JsonParser;

/**
 * Created by haseeb on 29/7/16.
 */
public class FeedService extends IntentService implements ApiCommunication {

    private static final String PAGE = "pageno";
    private static final String BODY = "json";
    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;
    public String Url, Method, SCREEN_NAME;
    public int pageNo = 1;
    Boolean dbStatus = false;
    public ResultReceiver receiver;
    DatabaseDB db;
    ArrayList<ProfileData> posts = new ArrayList<ProfileData>();
    String dBstatus;

    public FeedService() {
        super(FeedService.class.getName());
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        System.out.println("FEEDSSERVICE inside reciver :");
        db = new DatabaseDB(getApplicationContext());
        db.openDB();


        receiver = intent.getParcelableExtra("receiver");
        Url = intent.getStringExtra("url");
        System.out.println("url"+Url);
        Method = intent.getStringExtra("Method");
        SCREEN_NAME = intent.getStringExtra("ScreenName");
        try {
            pageNo = intent.getIntExtra("PageNo", 1);
        } catch (Exception e) {
            pageNo = 1;
        }


        Bundle bundle = new Bundle();

        if (!TextUtils.isEmpty(Url)) {
            /* Update UI: Download Service is Running */
            receiver.send(STATUS_RUNNING, Bundle.EMPTY);
            Log.d("FEEDSERVICE", "Service SRunning!");

            try {
                GetData(Url);
            } catch (Exception e) {

                /* Sending error message back to activity */
                bundle.putString(Intent.EXTRA_TEXT, e.toString());
                receiver.send(STATUS_ERROR, bundle);
            }
        }
        Log.d("FEEDSERVICE", "Service Stopping!");
        this.stopSelf();


    }


//    GetData function
//    =====================================

    private void GetData(String url) {
        ApiService.getInstance(this, 1).getData(this, true, SCREEN_NAME, url, "feed");
        System.out.println("FEEDSSERVICE inside reciver : "+url);
    }


//    Handling responses
//    ======================================

    @Override
    public void onResponseCallback(JSONObject response, String flag) {
        System.out.println("FEEDSSERVICE inside reciver : " + response);
        if (flag.equals("feed")) {
            if (Method.equals("Overlay")) {

                JSONObject resp = JsonParser.getInstance().parserJsonObject(response);
                Bundle bundle = new Bundle();
                bundle.putString("DATALIST", resp.toString());
                bundle.putString("Method", "Overlay");
                System.out.println("STATUS FINISHED");
                receiver.send(STATUS_FINISHED, bundle);
            } else if (Method.equals("Closet")) {
                System.out.println("FEEDSSERVICE inside reciver closet: ");
                try {
                    String status = response.getString("status");
                    if (status.equals("success")) {

                        JSONObject Maindata = JsonParser.getInstance().parserJsonObject(response);
                        JSONObject data = Maindata.getJSONObject("data");
                        JSONArray dataArray = data.getJSONArray("data");
                        ArrayList<ClosetData> dataList = new ArrayList<ClosetData>();
                        for (int i = 0; i < dataArray.length(); i++) {
                            ClosetData closetdata = new ClosetData();
                            closetdata.setUserName(dataArray.getJSONObject(i).getString("zap_username"));
                            closetdata.setAdmire(dataArray.getJSONObject(i).getBoolean("admiring"));
//                            closetdata.setProducts(dataArray.getJSONObject(i).getJSONArray("product"));
                            ArrayList<SingleItemData> singleItemDatas = new ArrayList<SingleItemData>();
                            for (int j=0;j<dataArray.getJSONObject(i).getJSONArray("product").length();j++){
                                SingleItemData itemData = new SingleItemData();
                                try {
                                    itemData.setImageUrl(dataArray.getJSONObject(i).getJSONArray("product").getJSONObject(j).getString("image"));
                                    itemData.setProductId(dataArray.getJSONObject(i).getJSONArray("product").getJSONObject(j).getInt("id"));
                                    itemData.setTitle(dataArray.getJSONObject(i).getJSONArray("product").getJSONObject(j).getString("title"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                singleItemDatas.add(itemData);
                            }
                            closetdata.setProducts(singleItemDatas);
                            closetdata.setUserId(dataArray.getJSONObject(i).getInt("id"));
                            closetdata.setUserImage(dataArray.getJSONObject(i).getString("profile_pic"));
                            dataList.add(closetdata);
                        }
                        Bundle bundle = new Bundle();
                        if (pageNo > 1) {
                            bundle.putSerializable("DATALIST", dataList);
                            bundle.putString("Method", "ClosetMore");
                            System.out.println("STATUS FINISHED");
                            receiver.send(STATUS_FINISHED, bundle);
                        } else {
                            bundle.putSerializable("DATALIST", dataList);
                            bundle.putString("Method", "Closet");
                            System.out.println("STATUS FINISHED");
                            receiver.send(STATUS_FINISHED, bundle);
                        }
                        dBstatus = "STORE";
//                        new DbOpertaions(response).doInBackground();
                        System.out.println("FEEDSSERVICE inside reciver after closet: ");
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putString("Method", "Closet");
                        receiver.send(STATUS_ERROR, bundle);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (Method.equals("product")) {

                ArrayList<HomeFeedItem> temp = JsonParser.getInstance().parserFeed(response);
                JSONObject dataInside = JsonParser.getInstance().parserJsonObject(response);
                try {
                    JSONObject CheckData = dataInside.getJSONObject("data");

//                    System.out.println("COLLLLLLLL : "+CheckData.getJSONObject("collection_data"));
                     if (temp != null) {
                        Bundle bundle = new Bundle();
                        if (pageNo > 1) {
                                bundle.putSerializable("DATALIST", temp);
                                bundle.putString("Method", "ProductMore");
                                System.out.println("STATUS FINISHED");
                                receiver.send(STATUS_FINISHED, bundle);
                        } else {
                            if (CheckData.has("collection_data")) {
                                if (CheckData.getJSONObject("collection_data") != null) {
                                    bundle.putSerializable("DATALIST", temp);
                                    bundle.putString("Collection", CheckData.getJSONObject("collection_data").toString());
                                    bundle.putString("Method", "product");
                                    System.out.println("STATUS FINISHED");
                                    receiver.send(STATUS_FINISHED, bundle);
                                } else {
                                    bundle.putSerializable("DATALIST", temp);
                                    bundle.putString("Collection", null);
                                    bundle.putString("Method", "product");
                                    System.out.println("STATUS FINISHED");
                                    receiver.send(STATUS_FINISHED, bundle);
                                }
                            }else {
                                bundle.putSerializable("DATALIST", temp);
                                bundle.putString("Method", "product");
                                System.out.println("STATUS FINISHED");
                                receiver.send(STATUS_FINISHED, bundle);
                            }
                        }
                        dBstatus = "PRODUCT";
                        System.out.println("DBJSONOBJECT : "+response.toString());
//                    new DbOpertaions(response).doInBackground();

                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putString("Method", "product");
                        receiver.send(STATUS_ERROR, bundle);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } else if (Method.equals("profile")) {
                System.out.println("CHECK data inside reciever : "+response);
                JSONObject resp = JsonParser.getInstance().parserJsonObject(response);
                if (resp != null) {
                    posts.clear();
                    try {
                        String status = resp.getString("status");
                        if (status.equals("success")) {
                            JSONObject data = resp.getJSONObject("data");
                            JSONArray albumData = data.getJSONArray("posts");
                            posts.add(null);
                            for (int i = 0; i < albumData.length(); i++) {
                                ProfileData profileData = new ProfileData();
                                profileData.setAlbumid(String.valueOf(albumData.getJSONObject(i).getInt("id")));
                                profileData.setImage(albumData.getJSONObject(i).getString("image_url"));
                                profileData.setSale(albumData.getJSONObject(i).getBoolean("sale"));
                                profileData.setTitle(albumData.getJSONObject(i).getString("title"));
                                profileData.setBrand(albumData.getJSONObject(i).getString("brand"));
                                if (!albumData.getJSONObject(i).isNull("listing_price")) {
                                    profileData.setL_price(albumData.getJSONObject(i).getInt("listing_price"));
                                }
                                else {
                                    profileData.setL_price(0);
                                }
                                if (!albumData.getJSONObject(i).isNull("original_price")) {
                                    profileData.setO_price(albumData.getJSONObject(i).getInt("original_price"));
                                }
                                else {
                                    profileData.setO_price(0);
                                }
                                profileData.setLikecount(albumData.getJSONObject(i).getInt("love_count"));
                                profileData.setLoved(albumData.getJSONObject(i).getBoolean("loved_by_user"));
                                if (!albumData.getJSONObject(i).isNull("discount")) {
                                    profileData.setDiscount(albumData.getJSONObject(i).getString("discount"));
                                }else {
                                    profileData.setDiscount(null);
                                }
                                posts.add(profileData);
                            }
                            if (albumData.length() == 0){
                                ProfileData profileData = new ProfileData();
                                profileData.setAlbumid("0");
                                profileData.setImage("");
                                profileData.setSale(false);
                                profileData.setL_price(0);
                                profileData.setO_price(0);
                                profileData.setLikecount(0);
                                profileData.setTitle("");
                                profileData.setLoved(false);
                                profileData.setDiscount("");
                                posts.add(profileData);
                            }

                            Bundle bundle = new Bundle();
                            bundle.putSerializable("DATALIST", posts);
                            bundle.putString("Usertype",data.getString("user_type"));
                            bundle.putInt("id",data.getInt("id"));
                            bundle.putString("Username",data.getString("zap_username"));
                            bundle.putString("Description",data.getString("description"));
                            bundle.putString("Profilepic",data.getString("profile_pic"));
                            bundle.putInt("admiringCount",data.getInt("admiring"));
                            bundle.putInt("admireCount",data.getInt("admirers"));
                            bundle.putInt("listingCount",data.getInt("no_of_posts"));
                            bundle.putBoolean("admiredBy",data.getBoolean("admired_by_user"));
                            bundle.putString("seller_type", data.getString("seller_type"));
                            if (data.has("designer_details")) {
                                if (!data.isNull("designer_details")) {
                                    bundle.putString("Designer", "");
                                    bundle.putString("ShortDescription", data.getJSONObject("designer_details").getString("description_short"));
                                    bundle.putString("Description", data.getJSONObject("designer_details").getString("description"));
                                    bundle.putString("Coverpic", data.getJSONObject("designer_details").getString("cover_pic"));
                                } else {
                                    bundle.putString("Designer", null);
                                    bundle.putString("ShortDescription", null);
                                    bundle.putString("Coverpic", null);
                                }
                            }
                            else {
                                bundle.putString("Designer", null);
                            }
                            bundle.putString("Method", "profile");
                            System.out.println("STATUS FINISHED");
                            receiver.send(STATUS_FINISHED, bundle);


                        } else {
                            Bundle bundle = new Bundle();
                            bundle.putString("Method", "profile");
                            receiver.send(STATUS_ERROR, bundle);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        System.out.println("CHECK data inside reciever error : "+e);
                        Bundle bundle = new Bundle();
                        bundle.putString("Method", "profile");
                        receiver.send(STATUS_ERROR, bundle);

                    }
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("Method", "profile");
                    receiver.send(STATUS_ERROR, bundle);

                }
            }
        }
    }

    @Override
    public void onErrorCallback(VolleyError error, String flag) {
        System.out.println("zzzzerror");
        if (Method.equals("product")) {
            Bundle bundle = new Bundle();
            bundle.putString("Method", "ProductPageFinish");
            receiver.send(STATUS_ERROR, bundle);
        }else if (Method.equals("Closet")) {
            Bundle bundle = new Bundle();
            bundle.putString("Method", "ClosetPageFinish");
            receiver.send(STATUS_ERROR, bundle);
        }

    }



//    AsyncTask
//    ================================================

    public class DbOpertaions extends AsyncTask<String,String,String>   {
        JSONObject param;
        
     public DbOpertaions(JSONObject param){
         this.param=param;
         
     }
        @Override
        protected String doInBackground(String... params) {
            System.out.println("DBOPERATION FS");
            if (dBstatus.equals("STORE")) {
                System.out.println("DBOPERATION FS STORE");
                if (db.getRecordsCount() == 0) {
                    try {
                        String data = param.toString().replace("'", "z*p*");
                        String query = "insert into " + "CLOSET" + " (" + PAGE + ", " + BODY + ") values('" + 1 + "', '" + data + "');";
                        db.processData(query);
                    } catch (Exception e) {
                    }
                } else {
                    try {
                        String deleteQuery = "delete from " + "CLOSET";
                        db.processData(deleteQuery);
                        String data = param.toString().replace("'", "z*p*");
                        String query = "insert into " + "CLOSET" + " (" + PAGE + ", " + BODY + ") values('" + 1 + "', '" + data + "');";
                        db.processData(query);
                        dbStatus = true;
                    } catch (Exception e) {
                    }
                }
            }
            else if (dBstatus.equals("PRODUCT")){
                System.out.println("DBOPERATION FS PRODUCT"+param);
                if (db.getRecordsCount() == 0) {
                    try {
                        String data = param.toString().replace("'", "z*p*");
                        System.out.println("DBOPERATION FS PRODUCT"+data);
                        String query = "insert into " + "FEED" + " (" + PAGE + ", " + BODY + ") values('" + 1 + "', '" + data + "');";
                        db.processData(query);
                    } catch (Exception e) {
                    }
                } else {
                    try {
                        String deleteQuery = "delete from " + "FEED";
                        db.processData(deleteQuery);
                        String data = param.toString().replace("'", "z*p*");
                        System.out.println("DBOPERATION FS PRODUCT DELETE"+data);
                        String query = "insert into " + "FEED" + " (" + PAGE + ", " + BODY + ") values('" + 1 + "', '" + data + "');";
                        db.processData(query);
                        dbStatus = true;
                    } catch (Exception e) {
                    }
                }
            }
            return dBstatus;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}

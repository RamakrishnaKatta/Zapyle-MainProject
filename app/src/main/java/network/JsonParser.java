package network;

import com.zapyle.zapyle.EnvConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;

import models.HomeFeedItem;
import utils.ExternalFunctions;

/**
 * Created by haseeb on 25/11/15.
 */
public class JsonParser {
    private static JsonParser instance;

    private JsonParser() {

    }

    public static synchronized JsonParser getInstance() {
        if (instance == null)
            instance = new JsonParser();
        return instance;
    }

    // Parsers
    public ArrayList<HomeFeedItem> parserFeed(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        } else {
            ArrayList<HomeFeedItem> temp = new ArrayList<HomeFeedItem>();
            JSONObject feedsData = null;
            JSONArray jsonArray = null;
            try {
                feedsData = jsonObject.getJSONObject("data");
                jsonArray = feedsData.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    HomeFeedItem homeFeedItem = new HomeFeedItem();
                    homeFeedItem.setId(jsonArray.getJSONObject(i).getInt("id"));
                    homeFeedItem.setUserId(jsonArray.getJSONObject(i).getJSONObject("uploaded_by").getInt("id"));
                    homeFeedItem.setSale(jsonArray.getJSONObject(i).getString("sale"));
                    homeFeedItem.setProductImage(jsonArray.getJSONObject(i).getJSONArray("images"));
                    homeFeedItem.setProductName(jsonArray.getJSONObject(i).getString("title"));
                    homeFeedItem.setComments(jsonArray.getJSONObject(i).getInt("comment_count"));
                    if (jsonArray.getJSONObject(i).isNull("discount")) {
                        homeFeedItem.setDiscount("0");
                    } else {
                        homeFeedItem.setDiscount(jsonArray.getJSONObject(i).getString("discount"));
                    }
                    homeFeedItem.setSold_out(jsonArray.getJSONObject(i).getBoolean("sold_out"));
                    homeFeedItem.setIsLiked(jsonArray.getJSONObject(i).getBoolean("liked_by_user"));
                    homeFeedItem.setLikes(jsonArray.getJSONObject(i).getInt("likes_count"));

                    NumberFormat myFormat = NumberFormat.getInstance();
                    myFormat.setGroupingUsed(true);

                    if (!jsonArray.getJSONObject(i).isNull("listing_price")) {
                        Double price = Double.parseDouble(jsonArray.getJSONObject(i).getString("listing_price"));
                        Double lastprice = ExternalFunctions.round(price, 2);
                        homeFeedItem.setNewPrice(myFormat.format(lastprice));
                    }
                    else {
                        homeFeedItem.setNewPrice("0");
                    }
                    if (!jsonArray.getJSONObject(i).isNull("style")) {
                        homeFeedItem.setStyle(jsonArray.getJSONObject(i).getString("style"));
                    } else {
                        homeFeedItem.setStyle("");
                    }

                    if (!jsonArray.getJSONObject(i).isNull("original_price")) {
                        Double Oprice = Double.parseDouble(jsonArray.getJSONObject(i).getString("original_price"));
                        Double Orgprice = ExternalFunctions.round(Oprice, 2);
                        homeFeedItem.setOldPrice(myFormat.format(Orgprice));
                    } else {
                        homeFeedItem.setOldPrice("0");
                    }

                    homeFeedItem.setUserImage(jsonArray.getJSONObject(i).getJSONObject("uploaded_by").getString("profile_pic"));
                    homeFeedItem.setUserName(jsonArray.getJSONObject(i).getJSONObject("uploaded_by").getString("zap_username"));
                    homeFeedItem.setUsertype(jsonArray.getJSONObject(i).getJSONObject("uploaded_by").getString("user_type"));
                    homeFeedItem.setBrand(jsonArray.getJSONObject(i).getString("brand"));
                    homeFeedItem.setService(jsonArray.getJSONObject(i).getJSONObject("uploaded_by").getString("user_type"));
//                    homeFeedItem.setFullname(jsonArray.getJSONObject(i).getJSONObject("user_details_dict").getString("full_name"));
                    assert temp != null;
                    temp.add(homeFeedItem);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            ////System.out.println(temp + "________temp inside parser");
            return temp;


        }

    }

    public JSONObject parserLike(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        } else {
            return jsonObject;
        }
    }


    public JSONObject parserProfile(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        } else {
            return jsonObject;
        }
    }

    public JSONObject parserJsonObject(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        } else {
            return jsonObject;
        }
    }


    public HomeFeedItem parserHomefeedObject(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        } else {
            HomeFeedItem homeFeedItem = new HomeFeedItem();
            try {

                homeFeedItem.setId(jsonObject.getInt("id"));

                homeFeedItem.setUserId(jsonObject.getJSONObject("uploaded_by").getInt("id"));

                homeFeedItem.setSale(jsonObject.getString("sale"));
                homeFeedItem.setProductImage(jsonObject.getJSONArray("images"));
                homeFeedItem.setProductName(jsonObject.getString("title"));
                homeFeedItem.setComments(jsonObject.getInt("comment_count"));
                if (jsonObject.isNull("discount")) {
                    homeFeedItem.setDiscount("0");
                } else {
                    homeFeedItem.setDiscount(jsonObject.getString("discount"));
                }
                homeFeedItem.setSold_out(jsonObject.getBoolean("sold_out"));
                homeFeedItem.setIsLiked(jsonObject.getBoolean("liked_by_user"));
                homeFeedItem.setLikes(jsonObject.getInt("likes_count"));
                homeFeedItem.setNewPrice(jsonObject.getString("listing_price"));
                if (!jsonObject.isNull("style")) {
                    homeFeedItem.setStyle(jsonObject.getString("style"));
                } else {
                    homeFeedItem.setStyle("");
                }
                homeFeedItem.setOldPrice(jsonObject.getString("original_price"));
                homeFeedItem.setUserImage(jsonObject.getJSONObject("uploaded_by").getString("profile_pic"));
                homeFeedItem.setUserName(jsonObject.getJSONObject("uploaded_by").getString("zap_username"));
                homeFeedItem.setUsertype(jsonObject.getJSONObject("uploaded_by").getString("user_type"));
                homeFeedItem.setBrand(jsonObject.getString("brand"));
                homeFeedItem.setService(jsonObject.getJSONObject("uploaded_by").getString("user_type"));
//                    homeFeedItem.setFullname(jsonArray.getJSONObject(i).getJSONObject("user_details_dict").getString("full_name"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return homeFeedItem;
        }
    }


}
package models;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by haseeb on 9/8/16.
 */
public class ClosetData {

    String UserName, UserImage;
    Boolean Admire;
    ArrayList<SingleItemData> Products = new ArrayList<SingleItemData>();
    int UserId;

    public String getUserName() {
        return UserName;
    }

    public String getUserImage() {
        return UserImage;
    }

    public int getUserId() {
        return UserId;
    }

    public Boolean getAdmire() {
        return Admire;
    }

    public ArrayList<SingleItemData> getProducts() {
        return Products;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public void setUserImage(String userImage) {
        UserImage = userImage;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    public void setProducts(ArrayList<SingleItemData> products) {
        Products = products;
    }

    public void setAdmire(Boolean admire) {
        Admire = admire;
    }
}

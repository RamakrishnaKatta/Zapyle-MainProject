package notifications;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by haseeb on 15/8/15.
 */
public class NotificationData {

    String action;
    String notif_time;
    int productid;
    String productname;
    String productImage;
    String message;
    String user_id;
    String username;
    String user_profile_pic;
    String user_type;



    public String getAction(){
        return action;
    }

    public String getUsername(){
        return username;
    }

    public int getProductid(){
        return productid;
    }

    public String getProductname(){
        return productname;
    }

    public String getProductImage(){
        return productImage;
    }

    public String getNotif_time(){
        return notif_time;
    }

    public String getMessage(){
        return message;
    }

    public String getProfile_pic(){
        return user_profile_pic;
    }

    public String getUser_id(){
        return user_id;
    }

    public String getUser_type(){
        return user_type;
    }








    public void setAction(String action){
        this.action = action;
    }

    public void setProductid(int productid){
        this.productid = productid;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void setNotif_time(String notif_time){
        this.notif_time = notif_time;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public void setUser_id(String user_id){
        this.user_id = user_id;
    }

    public void setProfile_pic(String user_profile_pic){
        this.user_profile_pic = user_profile_pic;
    }

    public void setProductname(String productname){
        this.productname = productname;
    }
    public void setProductImage(String productImage){
        this.productImage = productImage;
    }

    public void setUser_type(String user_type){
        this.user_type = user_type;
    }
}

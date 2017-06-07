package models;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by haseeb on 25/11/15.
 */
public class HomeFeedItem {

    String brand;
    String sale;
    String fullname;
    Boolean sold_out;

    public Boolean getSold_out(){
        return sold_out;
    }
    public void setSold_out(Boolean sold_out){
        this.sold_out = sold_out;
    }

    public String getFullname(){
        return fullname;
    }
    public void setFullname(String fullname){
        this.fullname = fullname;
    }


    public String getSale(){
        return sale;
    }

    public void setSale(String sale){
        this.sale = sale;
    }

    public String getBrand(){
        return brand;
    }
    public void setBrand(String brand){
        this.brand = brand;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setIsVerified(boolean isVerified) {
        this.isVerified = isVerified;
    }

    public JSONArray  getProductImage() {
        return productImage;
    }

    public void setProductImage(JSONArray productImage) {
        this.productImage = productImage;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setIsLiked(boolean isLiked) {
        this.isLiked = isLiked;
    }

    String userImage;
    String userName;
    boolean isVerified;
    JSONArray productImage = null;
    String productName;
    int likes;
    int comments;
    boolean isLiked;

    String discount;
    String oldPrice;
    int userId;

    public String getNewPrice() {
        return newPrice;
    }
    public int getUserId() {
        return userId;
    }

    public void setNewPrice(String newPrice) {
        this.newPrice = newPrice;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(String oldPrice) {
        this.oldPrice = oldPrice;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    String newPrice;
    String style;
    String time;
    String service;
    String Usertype;

    public String getUsertype(){
        return Usertype;
    }
    public void setUsertype(String Usertype){
        this.Usertype = Usertype;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    Boolean p_t_a;
    int id; public void setPTA(Boolean p_t_a){
        this.p_t_a = p_t_a;
    }
    public Boolean getPTA(){
        return p_t_a;
    }

}


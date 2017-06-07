package models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import models.UserDetailsDict;

/**
 * Created by hitech on 9/7/15.
 */
public class Post {
    @SerializedName("upload_time")
    @Expose
    private String uploadTime;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @Expose
    private String description;
//    @Expose
//    private String fashiontype;
    @SerializedName("user_details_dict")
    @Expose
    private UserDetailsDict userDetailsDict;
    @Expose
    private String brand;
    @SerializedName("product_size")
    @Expose
    private String productSize;
    @SerializedName("comment_count")
    @Expose
    private Integer commentCount;
    @SerializedName("fashion_type")
    @Expose
    private String fashionType;
    @Expose
    private List<String> images = new ArrayList<String>();
    @Expose
    private List<String> tags = new ArrayList<String>();
    @SerializedName("album_title")
    @Expose
    private String albumTitle;
    @SerializedName("liked_by_user")
    @Expose
    private Boolean likedByUser;
    @Expose
    private Integer id;
    @SerializedName("likes_count")
    @Expose
    private Integer likesCount;

    @SerializedName("original_price")
    @Expose
    private Double originalPrice;

    @SerializedName("sale")
    @Expose
    private String sale;

    @SerializedName("listing_price")
    @Expose
    private Double listingPrice;





    public Double getListingPrice() {
        return listingPrice;
    }

    /**
     *
     * @param listingPrice
     * The listing_price
     */
    public void setListingPrice(Double listingPrice) {
        this.listingPrice = listingPrice;
    }



    public String getSale() {
        return sale;
    }

    /**
     *
     * @param sale
     * The sale
     */
    public void setSale(String sale) {
        this.sale = sale;
    }


    public Double getOriginalPrice() {
        return originalPrice;
    }

    /**
     *
     * @param originalPrice
     * The original_price
     */
    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
    }

    /**
     *
     * @return
     * The uploadTime
     */
    public String getUploadTime() {
        return uploadTime;
    }

    /**
     *
     * @param uploadTime
     * The upload_time
     */
    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    /**
     *
     * @return
     * The userId
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     *
     * @param userId
     * The user_id
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     *
     * @return
     * The description
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     * The description
     */
    public void setDescription(String description) {
        this.description = description;
    }


    public List<String> getTags() {
        return tags;
    }

    /**
     *
     * @param tags
     * The tags
     */
    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    /**
     *
     * @return
     * The fashiontype
     */
//    public String getTags() {
//        return fashiontype;
//    }

    /**
     *
     * @param fashiontype
     * The fashiontype
     */
//    public void setTags(String fashiontype) {
//        this.fashiontype = fashiontype;
//    }

    /**
     *
     * @return
     * The userDetailsDict
     */
    public UserDetailsDict getUserDetailsDict() {
        return userDetailsDict;
    }

    /**
     *
     * @param userDetailsDict
     * The user_details_dict
     */
    public void setUserDetailsDict(UserDetailsDict userDetailsDict) {
        this.userDetailsDict = userDetailsDict;
    }

    /**
     *
     * @return
     * The brand
     */
    public String getBrand() {
        return brand;
    }

    /**
     *
     * @param brand
     * The brand
     */
    public void setBrand(String brand) {
        this.brand = brand;
    }

    /**
     *
     * @return
     * The productSize
     */
    public String getProductSize() {
        return productSize;
    }

    /**
     *
     * @param productSize
     * The product_size
     */
    public void setProductSize(String productSize) {
        this.productSize = productSize;
    }

    /**
     *
     * @return
     * The commentCount
     */
    public Integer getCommentCount() {
        return commentCount;
    }

    /**
     *
     * @param commentCount
     * The comment_count
     */
    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    /**
     *
     * @return
     * The fashionType
     */
    public String getFashionType() {
        return fashionType;
    }

    /**
     *
     * @param fashionType
     * The fashion_type
     */
    public void setFashionType(String fashionType) {
        this.fashionType = fashionType;
    }

    /**
     *
     * @return
     * The images
     */
    public List<String> getImages() {
        return images;
    }

    /**
     *
     * @param images
     * The images
     */
    public void setImages(List<String> images) {
        this.images = images;
    }

    /**
     *
     * @return
     * The albumTitle
     */
    public String getAlbumTitle() {
        return albumTitle;
    }

    /**
     *
     * @param albumTitle
     * The album_title
     */
    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    /**
     *
     * @return
     * The likedByUser
     */
    public Boolean getLikedByUser() {
        return likedByUser;
    }

    /**
     *
     * @param likedByUser
     * The liked_by_user
     */
    public void setLikedByUser(Boolean likedByUser) {
        this.likedByUser = likedByUser;
    }

    /**
     *
     * @return
     * The id
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The likesCount
     */
    public Integer getLikesCount() {
        return likesCount;
    }

    /**
     *
     * @param likesCount
     * The likes_count
     */
    public void setLikesCount(Integer likesCount) {
        this.likesCount = likesCount;
    }

}

package models;

/**
 * Created by haseeb on 28/11/15.
 */
public class ProfileData {

    String image, title;
    String albumid, discount, brand;
    Boolean sale, loved;
    Boolean p_t_a;
    int o_price, l_price, likecount, id;


    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getLoved() {
        return loved;
    }

    public void setLoved(Boolean loved) {
        this.loved = loved;
    }

    public void setO_price(int o_price) {
        this.o_price = o_price;
    }

    public int getO_price() {
        return o_price;
    }


    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getDiscount() {
        return discount;
    }

    public void setL_price(int l_price) {
        this.l_price = l_price;
    }

    public int getL_price() {
        return l_price;
    }

    public void setLikecount(int likecount) {
        this.likecount = likecount;
    }

    public int getLikecount() {
        return likecount;
    }

    public void setSale(Boolean sale){
        this.sale = sale;
    }

    public Boolean getSale(){
        return sale;
    }

    public void setImage(String image){
        this.image = image;
    }

    public String getImage(){
        return image;
    }

    public void setAlbumid(String albumid){
        this.albumid = albumid;
    }
    public void setPTA(Boolean p_t_a){
        this.p_t_a = p_t_a;
    }
    public Boolean getPTA(){
        return p_t_a;
    }
    public String getAlbumid(){
        return albumid;
    }
}

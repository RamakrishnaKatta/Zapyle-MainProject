package models;

/**
 * Created by haseeb on 5/10/15.
 */
public class Transactions {
    String total_price;
    String final_price;
    String delivery_address;
    String coupon;


    public Transactions() {}
    public Transactions(String total_price,String final_price, String delivery_address, String coupon) {
        this.total_price = total_price;
        this.final_price = final_price;
        this.delivery_address = delivery_address;
        this.coupon = coupon;

    }

}

package models;

import java.util.ArrayList;

/**
 * Created by zapyle on 27/1/16.
 */
public class MyorderListmodel {


    String strimagepath;
    String strprodname;
    String strsize;
    float stramount;
    String strorderid;
    Boolean boolreturn;
    String requestreason;
    String strsetstatus;
    String placed_at;
    String originalPrice;

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public String getPlaced_at() {
        return placed_at;
    }

    public void setPlaced_at(String placed_at) {
        this.placed_at = placed_at;
    }

    public String getimagepath(){
        return  strimagepath;
    }
    public String getproductname(){
        return  strprodname;
    }

    public String getstatus(){
        return strsetstatus;
    }
    public String getsize(){
        return  strsize;
    }
    public float getamount(){
        return  stramount;
    }
    public boolean getreturn(){
        return  boolreturn;
    }
    public String getorderid(){
        return strorderid;
    }
    public String getReturn(){
        return requestreason;
    }

    public void setimagepath(String imagepath){
        this.strimagepath = imagepath;
    }
    public void setproductname(String prodname){
        this.strprodname = prodname;
    }
    public void setstatus(String strstaus){
        this.strsetstatus=strstaus;
    }
    public void setsize(String size){
        this.strsize = size;
    }
    public void setamount(float amount){
        this.stramount = amount;
    }
    public void setreturn(boolean boolreturn){
        this.boolreturn =boolreturn;
    }

    public void setorderid(String orderid){
        this.strorderid=orderid;
    }
    public void setreq(String requestreason){
        this.requestreason=requestreason;
    }
}
